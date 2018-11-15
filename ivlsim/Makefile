base_dir=$(abspath ..)
sim_dir=$(abspath .)

PROJECT ?= example
MODEL ?= TestHarness
CONFIG ?= DefaultExampleConfig
CFG_PROJECT ?= $(PROJECT)
TB ?= TestDriver

simv = $(sim_dir)/simv-$(PROJECT)-$(CONFIG)
simv_debug = $(sim_dir)/simv-$(PROJECT)-$(CONFIG)-debug

default: $(simv)

debug: $(simv_debug)

include $(base_dir)/Makefrag
include $(sim_dir)/Makefrag-iverilog

rocketchip_vsrc_dir = $(ROCKETCHIP_DIR)/src/main/resources/vsrc

sim_vsrcs = \
	$(build_dir)/$(PROJECT).$(MODEL).$(CONFIG).v \
	$(rocketchip_vsrc_dir)/TestDriver.v \
	$(rocketchip_vsrc_dir)/AsyncResetReg.v \
	$(rocketchip_vsrc_dir)/plusarg_reader.v \
	$(testchip_vsrcs)

sim_csrcs = \
	$(testchip_csrcs)

CXX = g++

CXX_OPTS = \
    -D__ICARUS__ \
	-I$(abspath iverilog/install/include/iverilog) \
	-I$(RISCV)/include \
	-std=c++11 \
	-Wl,-rpath,$(RISCV)/lib \
	-Wl,--no-as-needed \
	-L$(abspath iverilog/install/lib) \
	-L$(RISCV)/lib \
	-lvpi -lveriuser -lfesvr -lpthread \
	-fPIC -shared \

IVL = $(IVL_TARGET)
VVP = $(VVP_TARGET)

IVL_OPTS = \
	-g2005-sv \
    -c timescale \
    -s $(TB) \
    -y $(generated_dir) -Y .v \
    -m$(abspath $(simv).vpi) \
	-DCLOCK_PERIOD=1.0 \
	-DPRINTF_COND=$(TB).printf_cond \
	-DSTOP_COND=\!$(TB).reset \
	-DRANDOMIZE_MEM_INIT \
	-DRANDOMIZE_REG_INIT \
	-DRANDOMIZE_GARBAGE_ASSIGN \
	-DRANDOMIZE_INVALID_ASSIGN \


verilog: $(sim_vsrcs)

$(simv) : $(sim_vsrcs) $(sim_csrcs) $(INSTALLED_IVL)
	rm -rf csrc && \
    $(CXX) $(CXX_OPTS) $(sim_csrcs) -o $(simv).vpi && \
    $(IVL) $(IVL_OPTS) -o $(simv) $(sim_vsrcs) \

$(simv_debug) : $(sim_vsrcs) $(sim_csrcs) $(INSTALLED_IVL)
	rm -rf csrc && \
    $(CXX) $(CXX_OPTS) $(sim_csrcs) -o $(simv).vpi && \
	$(IVL) $(IVL_OPTS) -o $(simv_debug) $(sim_vsrcs) -DDEBUG \

exec_simv = $(VVP) -M. -msimv-$(PROJECT)-$(CONFIG) $(simv) 
exec_simv_debug = $(VVP) -v -M. -msimv-$(PROJECT)-$(CONFIG) $(simv_debug)

$(output_dir)/%.out: $(output_dir)/% $(simv)
	$(exec_simv) +verbose +max-cycles=1000000 $< 3>&1 1>&2 2>&3 | spike-dasm > $@

$(output_dir)/%.run: $(output_dir)/% $(simv)
	$(exec_simv) +max-cycles=1000000 $< && touch $@

$(output_dir)/%.fst: $(output_dir)/% $(simv_debug)
	$(exec_simv_debug) -fst +vcdfile=$@ +max-cycles=1000000 $<

run-regression-tests: $(addprefix $(output_dir)/,$(addsuffix .out,$(regression-tests)))

run-regression-tests-fast: $(addprefix $(output_dir)/,$(addsuffix .run,$(regression-tests)))

run-regression-tests-debug: $(addprefix $(output_dir)/,$(addsuffix .fst,$(regression-tests)))

clean:
	rm -rf generated-src csrc simv-*

.PHONY: clean
