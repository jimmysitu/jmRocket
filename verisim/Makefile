base_dir=$(abspath ..)
sim_dir= .

PROJECT ?= examplefpga
MODEL ?= TestHarness
CONFIG ?= DefaultExampleConfig
CFG_PROJECT ?= $(PROJECT)
TB ?= TestDriver

sim = $(abspath $(sim_dir))/simulator-$(PROJECT)-$(CONFIG)
sim_debug = $(abspath $(sim_dir))/simulator-$(PROJECT)-$(CONFIG)-debug

default: $(sim)

debug: $(sim_debug)

CXXFLAGS := $(CXXFLAGS) -O1 -std=c++11 -I$(RISCV)/include -D__STDC_FORMAT_MACROS
LDFLAGS := $(LDFLAGS) -L$(RISCV)/lib -Wl,-rpath,$(RISCV)/lib -L$(abspath $(sim_dir)) -lfesvr -lpthread

include $(base_dir)/Makefrag
include $(base_dir)/Makefrag-jtag
include $(sim_dir)/Makefrag-verilator

long_name = $(PROJECT).$(MODEL).$(CONFIG)
ifneq ($(filter run% %.run %.out %.vpd %.vcd,$(MAKECMDGOALS)),)
-include $(build_dir)/$(long_name).d
endif

rocketchip_csrc_dir = $(ROCKETCHIP_DIR)/src/main/resources/csrc
rocketchip_vsrc_dir = $(ROCKETCHIP_DIR)/src/main/resources/vsrc

sim_vsrcs = \
	$(build_dir)/$(long_name).v \
	$(rocketchip_vsrc_dir)/AsyncResetReg.v \
	$(rocketchip_vsrc_dir)/plusarg_reader.v \
	$(testchip_vsrcs)

sim_csrcs = \
	$(rocketchip_csrc_dir)/emulator.cc \
	$(rocketchip_csrc_dir)/SimJTAG.cc \
	$(rocketchip_csrc_dir)/SimDTM.cc \
	$(rocketchip_csrc_dir)/remote_bitbang.cc \
	$(testchip_csrcs)
	#$(abspath $(sim_dir))/csrc/verilator-harness.cc \

model_dir = $(build_dir)/$(long_name)
model_dir_debug = $(build_dir)/$(long_name).debug

model_header = $(model_dir)/V$(MODEL).h
model_header_debug = $(model_dir_debug)/V$(MODEL).h

model_mk = $(model_dir)/V$(MODEL).mk
model_mk_debug = $(model_dir_debug)/V$(MODEL).mk

$(model_mk): $(sim_vsrcs) $(INSTALLED_VERILATOR)
	rm -rf $(build_dir)/$(long_name)
	mkdir -p $(build_dir)/$(long_name)
	$(VERILATOR) $(VERILATOR_FLAGS) -Mdir $(build_dir)/$(long_name) \
	-o $(sim) $< $(sim_csrcs) -LDFLAGS "$(LDFLAGS)" \
	-CFLAGS "-I$(build_dir) -I$(rocketchip_csrc_dir) -include $(model_header)" \
	-CFLAGS "-DTEST_HARNESS=V$(MODEL) -include $(build_dir)/$(long_name).plusArgs"
	touch $@

$(sim): $(model_mk) $(sim_csrcs)
	$(MAKE) VM_PARALLEL_BUILDS=1 -C $(build_dir)/$(long_name) -f V$(MODEL).mk


$(model_mk_debug): $(sim_vsrcs) $(INSTALLED_VERILATOR)
	mkdir -p $(build_dir)/$(long_name).debug
	$(VERILATOR) $(VERILATOR_FLAGS) -Mdir $(build_dir)/$(long_name).debug --trace \
	-o $(sim_debug) $< $(sim_csrcs) -LDFLAGS "$(LDFLAGS)" \
	-CFLAGS "-I$(build_dir) -I$(rocketchip_csrc_dir) -include $(model_header_debug)" \
	-CFLAGS "-DTEST_HARNESS=V$(MODEL) -include $(build_dir)/$(long_name).plusArgs"
	touch $@

$(sim_debug): $(model_mk_debug) $(sim_csrcs)
	$(MAKE) VM_PARALLEL_BUILDS=1 -C $(build_dir)/$(long_name).debug -f V$(MODEL).mk

$(output_dir)/%.out: $(output_dir)/% $(sim)
	$(sim) +verbose +max-cycles=1000000 $< 3>&1 1>&2 2>&3 | spike-dasm > $@

$(output_dir)/%.run: $(output_dir)/% $(sim)
	$(sim) +max-cycles=1000000 $< && touch $@

$(output_dir)/%.fst: $(output_dir)/% $(sim_debug)
	rm -f $@.vcd && mkfifo $@.vcd
	vcd2fst -Z $@.vcd $@ &
	$(sim_debug) -v$@.vcd +max-cycles=1000000 $<

$(output_dir)/%.vpd: $(output_dir)/% $(sim_debug)
	rm -f $@.vcd && mkfifo $@.vcd
	vcd2vpd $@.vcd $@ > /dev/null &
	$(sim_debug) -v$@.vcd +max-cycles=1000000 $<

run: run-asm-tests run-bmark-tests
run-debug: run-asm-tests-debug run-bmark-tests-debug
run-fast: run-asm-tests-fast run-bmark-tests-fast

.PHONY: run-asm-tests run-bmark-tests
.PHONY: run-asm-tests-debug run-bmark-tests-debug
.PHONY: run run-debug run-fast

ifeq ($(PROJECT), examplefpga)
run-jtag-dtm-tests-64: $(sim)
	RISCV=$(RISCV) $(GDBSERVER) \
	--sim_cmd="$(sim) $(JTAG_DTM_SIM_ARGS) dummybin" \
	--server_cmd="$(RISCV)/bin/openocd \
	-s $(RISCV)/share/openocd/scripts" \
	--64 \
	$(base_dir)/tests/examplefpga.py \
	$(JTAG_DTM_TEST)

run-jtag-dtm-tests-64-debug: $(sim_debug)
	RISCV=$(RISCV) $(GDBSERVER) \
	--sim_cmd="$(sim_debug) $(JTAG_DTM_SIM_ARGS) $(VERISIM_JTAG_VCDPLUS_64) dummybin" \
	--server_cmd="$(RISCV)/bin/openocd $(OPENOCD_DEBUG) \
	-s $(RISCV)/share/openocd/scripts" \
	--64 \
	$(base_dir)/tests/examplefpga.py \
	$(JTAG_DTM_TEST)

endif

clean:
	rm -rf generated-src ./simulator-*
