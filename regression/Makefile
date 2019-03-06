SHELL := /bin/bash
base_dir = $(abspath ..)
generated_dir = $(abspath ./generated-src)

# The default target, which runs all regression targets.
regression: vsim-regression ivlsim-regression verisim-regression 

# Regression targets for the various simulators.
%-regression: %-asm-tests %-bmark-tests

# Some targets can run torture
vsim-regression: vsim-torture
ivlsim-regression: ivlsim-torture
verisim-regression: verisim-torture

# The torture configuration to use
TORTURE_CONFIG ?= default

# The top-level directory that contains rocket-chip
TOP ?= ..

# The directory that tools get checked out into
RISCV_TOOLS = $(abspath $(TOP))/riscv-tools

# The hash of the tools that we're using
TOOLS_HASH ?= $(shell cat $(TOP)/riscv-tools.hash)
$(info Using riscv-tools of $(TOOLS_HASH))

# The directory that the tools get built into.
RISCV ?= install/$(TOOLS_HASH)

# Torture saves the failing tests into a directory, which defaults to just somehing inside the regressions directory.
TORTURE_SAVE_DIR ?= torture-failures

# Include top-level makefrag for options like rocketchip_addons
include $(TOP)/Makefrag

# Removes all the build stamps from the current config
.PHONY: clean
clean:
	rm -rf stamps $(abspath $(RISCV))
	$(MAKE) RISCV=$(RISCV) -C $(abspath $(TOP)/vsim) clean
	$(MAKE) RISCV=$(RISCV) -C $(abspath $(TOP)/ivlsim) clean
	$(MAKE) RISCV=$(RISCV) -C $(abspath $(TOP)/verisim) clean

ifeq ($(SUITE),)
$(error Set SUITE to the regression suite you want to run)
endif

ifeq ($(SUITE),exampleA)
PROJECT=example
CONFIGS=DefaultExampleConfig
endif

ifeq ($(SUITE),examplefpgaA)
PROJECT=examplefpga
CONFIGS=DefaultExampleConfig
endif

ifeq ($(SUITE),examplefpgaB)
PROJECT=examplefpga
CONFIGS=Tiny64Config
endif

ifeq ($(SUITE),RocketSuiteA)
PROJECT=freechips.rocketchip.system
CONFIGS=DefaultConfig
endif

ifeq ($(SUITE),RocketSuiteB)
PROJECT=freechips.rocketchip.system
CONFIGS=DefaultBufferlessConfig
endif

ifeq ($(SUITE),RocketSuiteC)
PROJECT=freechips.rocketchip.system
CONFIGS=TinyConfig
endif

ifeq ($(SUITE),UnittestSuite)
PROJECT=freechips.rocketchip.unittest
CONFIGS=AMBAUnitTestConfig TLSimpleUnitTestConfig TLWidthUnitTestConfig
endif

ifeq ($(SUITE), JtagDtmSuite)
PROJECT=freechips.rocketchip.system

export JTAG_DTM_ENABLE_SBA ?= off
ifeq ($(JTAG_DTM_ENABLE_SBA), off)
CONFIGS_32=WithJtagDTMSystem_DefaultRV32Config
CONFIGS_64=WithJtagDTMSystem_DefaultConfig
endif

ifeq ($(JTAG_DTM_ENABLE_SBA), on)
CONFIGS_32=WithJtagDTMSystem_WithDebugSBASystem_DefaultRV32Config
CONFIGS_64=WithJtagDTMSystem_WithDebugSBASystem_DefaultConfig
endif

CONFIGS += $(CONFIGS_32)
CONFIGS += $(CONFIGS_64)
endif

ifeq ($(SUITE), Miscellaneous)
PROJECT=freechips.rocketchip.system
CONFIGS=RoccExampleConfig
endif

# These are the named regression targets.  While it's expected you run them in
# this order, since there's dependencies for everything it doesn't actually
# matter.  They're here to make running the various targets from the
# commandline a bit cleaner.
submodules: stamps/other-submodules.stamp
tools: $(RISCV)/install.stamp

verilator:
	$(MAKE) RISCV=$(RISCV) -C $(abspath $(TOP)/verisim) verilator
iverilog:
	$(MAKE) RISCV=$(RISCV) -C $(abspath $(TOP)/ivlsim) iverilog

EMU_VERILOG_STAMPS=$(foreach config,$(CONFIGS),stamps/$(config)/verisim-verilog.stamp)
EMU_DEBUG_STAMPS=$(foreach config,$(CONFIGS),stamps/$(config)/verisim-debug.stamp)
EMU_NDEBUG_STAMPS=$(foreach config,$(CONFIGS),stamps/$(config)/verisim-ndebug.stamp)
EMU_ASM_TEST_STAMPS=$(foreach config,$(CONFIGS),stamps/$(config)/verisim-asm-tests.stamp)
EMU_BMARK_TEST_STAMPS=$(foreach config,$(CONFIGS),stamps/$(config)/verisim-bmark-tests.stamp)
EMU_REGRESSION_TEST_STAMPS=$(foreach config,$(CONFIGS),stamps/$(config)/verisim-regression-tests.stamp)
EMU_TORTURE_STAMPS=$(foreach config,$(CONFIGS),stamps/$(config)/verisim-torture-$(TORTURE_CONFIG).stamp)

verisim-verilog: $(EMU_VERILOG_STAMPS)
verisim-debug: $(EMU_DEBUG_STAMPS)
verisim-ndebug: $(EMU_NDEBUG_STAMPS)
verisim-asm-tests: $(EMU_ASM_TEST_STAMPS)
verisim-bmark-tests: $(EMU_BMARK_TEST_STAMPS)
verisim-regression-tests: $(EMU_REGRESSION_TEST_STAMPS)
verisim-torture: $(EMU_TORTURE_STAMPS)

VSIM_VERILOG_STAMPS=$(foreach config,$(CONFIGS),stamps/$(config)/vsim-verilog.stamp)
VSIM_DEBUG_STAMPS=$(foreach config,$(CONFIGS),stamps/$(config)/vsim-debug.stamp)
VSIM_NDEBUG_STAMPS=$(foreach config,$(CONFIGS),stamps/$(config)/vsim-ndebug.stamp)
VSIM_ASM_TEST_STAMPS=$(foreach config,$(CONFIGS),stamps/$(config)/vsim-asm-tests.stamp)
VSIM_BMARK_TEST_STAMPS=$(foreach config,$(CONFIGS),stamps/$(config)/vsim-bmark-tests.stamp)
VSIM_REGRESSION_TEST_STAMPS=$(foreach config,$(CONFIGS),stamps/$(config)/vsim-regression-tests.stamp)
VSIM_TORTURE_STAMPS=$(foreach config,$(CONFIGS),stamps/$(config)/vsim-torture-$(TORTURE_CONFIG).stamp)

vsim-verilog: $(VSIM_VERILOG_STAMPS)
vsim-debug: $(VSIM_DEBUG_STAMPS)
vsim-ndebug: $(VSIM_NDEBUG_STAMPS)
vsim-asm-tests: $(VSIM_ASM_TEST_STAMPS)
vsim-bmark-tests: $(VSIM_BMARK_TEST_STAMPS)
vsim-regression-tests: $(VSIM_REGRESSION_TEST_STAMPS)
vsim-torture: $(VSIM_TORTURE_STAMPS)

IVLSIM_VERILOG_STAMPS=$(foreach config,$(CONFIGS),stamps/$(config)/ivlsim-verilog.stamp)
IVLSIM_DEBUG_STAMPS=$(foreach config,$(CONFIGS),stamps/$(config)/ivlsim-debug.stamp)
IVLSIM_NDEBUG_STAMPS=$(foreach config,$(CONFIGS),stamps/$(config)/ivlsim-ndebug.stamp)
IVLSIM_ASM_TEST_STAMPS=$(foreach config,$(CONFIGS),stamps/$(config)/ivlsim-asm-tests.stamp)
IVLSIM_BMARK_TEST_STAMPS=$(foreach config,$(CONFIGS),stamps/$(config)/ivlsim-bmark-tests.stamp)
IVLSIM_REGRESSION_TEST_STAMPS=$(foreach config,$(CONFIGS),stamps/$(config)/ivlsim-regression-tests.stamp)
IVLSIM_TORTURE_STAMPS=$(foreach config,$(CONFIGS),stamps/$(config)/ivlsim-torture-$(TORTURE_CONFIG).stamp)

ivlsim-verilog: $(IVLSIM_VERILOG_STAMPS)
ivlsim-debug: $(IVLSIM_DEBUG_STAMPS)
ivlsim-ndebug: $(IVLSIM_NDEBUG_STAMPS)
ivlsim-asm-tests: $(IVLSIM_ASM_TEST_STAMPS)
ivlsim-bmark-tests: $(IVLSIM_BMARK_TEST_STAMPS)
ivlsim-regression-tests: $(IVLSIM_REGRESSION_TEST_STAMPS)
ivlsim-torture: $(IVLSIM_TORTURE_STAMPS)

submodule_names = fpga-shells sifive-blocks testchipip rocket-chip $(ROCKETCHIP_ADDONS)

# Checks out all the rocket-chip submodules
stamps/other-submodules.stamp:
	mkdir -p $(dir $@)
	git -C $(abspath $(TOP)) submodule update --init --recursive $(submodule_names)
	date > $@

stamps/riscv-tools_checkout.stamp:
	mkdir -p $(dir $@)
	git -C $(abspath $(TOP)) clone -n https://github.com/riscv/riscv-tools.git
	git -C $(RISCV_TOOLS) checkout $(TOOLS_HASH)
	git -C $(RISCV_TOOLS) submodule update --init --recursive
	mkdir -p $(dir $@)
	date > $@

# riscv-tools_checkout.stamp is an order-only prerequisite:
# https://www.gnu.org/software/make/manual/make.html#Prerequisite-Types
# This means that the rule will be executed, but it will /not/ be
# evaluated as a depenency for install.stamp. This is needed because
# the timestamp on install.stamp will be older than the checkout stamp
# anytime we restore the installation area from cache in travisci.
$(RISCV)/install.stamp: | stamps/riscv-tools_checkout.stamp
	mkdir -p $(dir $@)
	+cd $(RISCV_TOOLS); RISCV=$(abspath $(RISCV)) ./build.sh
	date > $@

openocd: $(RISCV)/install_openocd.stamp
$(RISCV)/install_openocd.stamp: | stamps/riscv-tools_checkout.stamp
	mkdir -p $(dir $@)
	+cd $(RISCV_TOOLS); RISCV=$(abspath $(RISCV));\
		. ./build.common; \
		build_project riscv-openocd --prefix=$$RISCV --enable-remote-bitbang --enable-jtag_vpi --disable-werror
	date > $@

spike: $(RISCV)/install_spike.stamp
$(RISCV)/install_spike.stamp: | stamps/riscv-tools_checkout.stamp
	mkdir -p $(dir $@)
	+cd $(RISCV_TOOLS); RISCV=$(abspath $(RISCV)); \
		. ./build.common; \
		build_project riscv-fesvr --prefix=$$RISCV; \
		build_project riscv-isa-sim --prefix=$$RISCV --with-fesvr=$$RISCV
	date > $@

toolchain: $(RISCV)/install_toolchain.stamp
$(RISCV)/install_toolchain.stamp: | stamps/riscv-tools_checkout.stamp
	mkdir -p $(dir $@)
	+cd $(RISCV_TOOLS); RISCV=$(abspath $(RISCV)); \
		. ./build.common; \
		build_project riscv-gnu-toolchain --prefix=$$RISCV;
	date > $@

pk: $(RISCV)/install_pk.stamp
$(RISCV)/install_pk.stamp: | stamps/riscv-tools_checkout.stamp
	mkdir -p $(dir $@)
	+cd $(RISCV_TOOLS); RISCV=$(abspath $(RISCV)); \
		. ./build.common; \
		CC= CXX= build_project riscv-pk --prefix=$$RISCV --host=riscv64-unknown-elf;
	date > $@

tests: $(RISCV)/install_tests.stamp
$(RISCV)/install_tests.stamp: | stamps/riscv-tools_checkout.stamp
	mkdir -p $(dir $@)
	+cd $(RISCV_TOOLS); RISCV=$(abspath $(RISCV)); \
		. ./build.common; \
		build_project riscv-tests --prefix=$$RISCV/riscv64-unknown-elf;
	date > $@

# Builds the various simulators
stamps/%/verisim-verilog.stamp:
	mkdir -p $(dir $@)
	+flock -x $(dir $@)/chisel-lock $(MAKE) -C $(abspath $(TOP))/verisim PROJECT=$(PROJECT) CONFIG=$* RISCV=$(abspath $(RISCV))
	date > $@

stamps/%/verisim-ndebug.stamp: stamps/other-submodules.stamp $(RISCV)/install.stamp
	mkdir -p $(dir $@)
	+flock -x $(dir $@)/chisel-lock $(MAKE) -C $(abspath $(TOP))/verisim PROJECT=$(PROJECT) CONFIG=$* RISCV=$(abspath $(RISCV))
	date > $@

stamps/%/verisim-debug.stamp: stamps/other-submodules.stamp $(RISCV)/install.stamp
	mkdir -p $(dir $@)
	+flock -x $(dir $@)/chisel-lock $(MAKE) -C $(abspath $(TOP))/verisim PROJECT=$(PROJECT) CONFIG=$* RISCV=$(abspath $(RISCV)) debug
	date > $@

stamps/%/vsim-verilog.stamp:
	mkdir -p $(dir $@)
	+flock -x $(dir $@)/chisel-lock $(MAKE) -C $(abspath $(TOP))/vsim PROJECT=$(PROJECT) CONFIG=$* RISCV=$(abspath $(RISCV))
	date > $@

stamps/%/vsim-ndebug.stamp: stamps/other-submodules.stamp $(RISCV)/install.stamp
	mkdir -p $(dir $@)
	+flock -x $(dir $@)/chisel-lock $(MAKE) -C $(abspath $(TOP))/vsim PROJECT=$(PROJECT) CONFIG=$* RISCV=$(abspath $(RISCV))
	date > $@

stamps/%/vsim-debug.stamp: stamps/other-submodules.stamp $(RISCV)/install.stamp
	mkdir -p $(dir $@)
	+flock -x $(dir $@)/chisel-lock $(MAKE) -C $(abspath $(TOP))/vsim PROJECT=$(PROJECT) CONFIG=$* RISCV=$(abspath $(RISCV)) debug
	date > $@

stamps/%/ivlsim-verilog.stamp:
	mkdir -p $(dir $@)
	+flock -x $(dir $@)/chisel-lock $(MAKE) -C $(abspath $(TOP))/ivlsim PROJECT=$(PROJECT) CONFIG=$* RISCV=$(abspath $(RISCV))
	date > $@

stamps/%/ivlsim-ndebug.stamp: stamps/other-submodules.stamp $(RISCV)/install.stamp
	mkdir -p $(dir $@)
	+flock -x $(dir $@)/chisel-lock $(MAKE) -C $(abspath $(TOP))/ivlsim PROJECT=$(PROJECT) CONFIG=$* RISCV=$(abspath $(RISCV))
	date > $@

stamps/%/ivlsim-debug.stamp: stamps/other-submodules.stamp $(RISCV)/install.stamp
	mkdir -p $(dir $@)
	+flock -x $(dir $@)/chisel-lock $(MAKE) -C $(abspath $(TOP))/ivlsim PROJECT=$(PROJECT) CONFIG=$* RISCV=$(abspath $(RISCV)) debug
	date > $@

# Runs tests on one of the simulators
stamps/%/verisim-asm-tests.stamp: stamps/other-submodules.stamp $(RISCV)/install.stamp
	mkdir -p $(dir $@)
	$(MAKE) -C $(abspath $(TOP))/verisim PROJECT=$(PROJECT) CONFIG=$* RISCV=$(abspath $(RISCV)) run-asm-tests-fast
	date > $@

stamps/%/verisim-bmark-tests.stamp: stamps/other-submodules.stamp $(RISCV)/install.stamp
	mkdir -p $(dir $@)
	$(MAKE) -C $(abspath $(TOP))/verisim PROJECT=$(PROJECT) CONFIG=$* RISCV=$(abspath $(RISCV)) run-bmark-tests-fast
	date > $@

stamps/%/verisim-regression-tests.stamp: stamps/other-submodules.stamp $(RISCV)/install.stamp
	mkdir -p $(dir $@)
	$(MAKE) -C $(abspath $(TOP))/verisim PROJECT=$(PROJECT) CONFIG=$* RISCV=$(abspath $(RISCV)) clean-run-output
	$(MAKE) -C $(abspath $(TOP))/verisim PROJECT=$(PROJECT) CONFIG=$* RISCV=$(abspath $(RISCV)) run-regression-tests-fast
	date > $@

stamps/%/vsim-asm-tests.stamp: stamps/other-submodules.stamp $(RISCV)/install.stamp
	mkdir -p $(dir $@)
	$(MAKE) -C $(abspath $(TOP))/vsim PROJECT=$(PROJECT) CONFIG=$* RISCV=$(abspath $(RISCV)) run-asm-tests-fast
	date > $@

stamps/%/vsim-bmark-tests.stamp: stamps/other-submodules.stamp $(RISCV)/install.stamp
	mkdir -p $(dir $@)
	$(MAKE) -C $(abspath $(TOP))/vsim PROJECT=$(PROJECT) CONFIG=$* RISCV=$(abspath $(RISCV)) run-bmark-tests-fast
	date > $@

stamps/%/vsim-regression-tests.stamp: stamps/other-submodules.stamp $(RISCV)/install.stamp
	mkdir -p $(dir $@)
	$(MAKE) -C $(abspath $(TOP))/vsim PROJECT=$(PROJECT) CONFIG=$* RISCV=$(abspath $(RISCV)) clean-run-output
	$(MAKE) -C $(abspath $(TOP))/vsim PROJECT=$(PROJECT) CONFIG=$* RISCV=$(abspath $(RISCV)) run-regression-tests-fast
	date > $@

stamps/%/ivlsim-asm-tests.stamp: stamps/other-submodules.stamp $(RISCV)/install.stamp
	mkdir -p $(dir $@)
	$(MAKE) -C $(abspath $(TOP))/ivlsim PROJECT=$(PROJECT) CONFIG=$* RISCV=$(abspath $(RISCV)) run-asm-tests-fast
	date > $@

stamps/%/ivlsim-bmark-tests.stamp: stamps/other-submodules.stamp $(RISCV)/install.stamp
	mkdir -p $(dir $@)
	$(MAKE) -C $(abspath $(TOP))/ivlsim PROJECT=$(PROJECT) CONFIG=$* RISCV=$(abspath $(RISCV)) run-bmark-tests-fast
	date > $@

stamps/%/ivlsim-regression-tests.stamp: stamps/other-submodules.stamp $(RISCV)/install.stamp
	mkdir -p $(dir $@)
	$(MAKE) -C $(abspath $(TOP))/ivlsim PROJECT=$(PROJECT) CONFIG=$* RISCV=$(abspath $(RISCV)) clean-run-output
	$(MAKE) -C $(abspath $(TOP))/ivlsim PROJECT=$(PROJECT) CONFIG=$* RISCV=$(abspath $(RISCV)) run-regression-tests-fast
	date > $@

# The torture tests run subtly differently on the different targets, so they
# don't have pattern rules like everything else does.
stamps/%/vsim-torture-$(TORTURE_CONFIG).stamp: stamps/%/vsim-debug.stamp stamps/%/vsim-ndebug.stamp
	mkdir -p $(dir $@)
	$(MAKE) -C $(abspath $(TOP))/torture rnight RTL_CONFIG=$* RISCV=$(abspath $(RISCV)) PATH="$(abspath $(RISCV)/bin:$(PATH))" OPTIONS="-C $(abspath $(TOP)/torture/config/$(TORTURE_CONFIG).config) -p $(abspath $(TORTURE_SAVE_DIR)) -m 30 -t 10"
	date > $@

stamps/%/ivlsim-torture-$(TORTURE_CONFIG).stamp: stamps/%/ivlsim-debug.stamp stamps/%/ivlsim-ndebug.stamp
	mkdir -p $(dir $@)
	$(MAKE) -C $(abspath $(TOP))/torture rnight RTL_CONFIG=$* RISCV=$(abspath $(RISCV)) PATH="$(abspath $(RISCV)/bin:$(PATH))" OPTIONS="-C $(abspath $(TOP)/torture/config/$(TORTURE_CONFIG).config) -p $(abspath $(TORTURE_SAVE_DIR)) -m 30 -t 10"
	date > $@

stamps/%/verisim-torture-$(TORTURE_CONFIG).stamp: stamps/%/verisim-debug.stamp stamps/%/verisim-ndebug.stamp
	mkdir -p $(dir $@)
	$(MAKE) -C $(abspath $(TOP))/torture cnight RTL_CONFIG=$* RISCV=$(abspath $(RISCV)) PATH="$(abspath $(RISCV)/bin:$(PATH))" OPTIONS="-C $(abspath $(TOP)/torture/config/$(TORTURE_CONFIG).config) -p $(abspath $(TORTURE_SAVE_DIR)) -m 30 -t 10"
	date > $@


# Targets for JTAG DTM full-chain simulation

# If this is defined empty, then all tests would run.
JTAG_DTM_TEST ?= MemTest64
GDBSERVER = $(abspath $(TOP))/riscv-tools/riscv-tests/debug/gdbserver.py \
	    --print-failures \
	    --print-log-names

ifdef DEBUG
JTAG_STAMP_SUFFIX=-debug
JTAG_DEBUG_SUFFIX=-debug
VSIM_JTAG_VCDPLUS_32= +vcdplusfile=regression32.vcd
VSIM_JTAG_VCDPLUS_64= +vcdplusfile=regression64.vcd
IVLSIM_JTAG_VCDPLUS_32= +vcdfile=regression32.vcd
IVLSIM_JTAG_VCDPLUS_64= +vcdfile=regression64.vcd
verisim_JTAG_VCDPLUS_32= -v regression32.vcd
verisim_JTAG_VCDPLUS_64= -v regression64.vcd
OPENOCD_DEBUG= -d
else
JTAG_STAMP_SUFFIX=-ndebug
endif

ifdef SEED
SEED_ARG = --seed $(SEED)
endif

JTAG_DTM_SIM_ARGS = +verbose +jtag_rbb_enable=1 $(SEED_ARG)

stamps/riscv-tests.stamp: stamps/riscv-tools_checkout.stamp
	mkdir -p $(dir $@)
	date > $@

stamps/%/vsim-jtag-dtm-32-$(JTAG_DTM_TEST).stamp: stamps/%/vsim$(JTAG_STAMP_SUFFIX).stamp stamps/riscv-tests.stamp
	RISCV=$(RISCV) $(GDBSERVER) \
	--sim_cmd="$(abspath $(TOP))/vsim/simv-$(PROJECT)-$*$(JTAG_DEBUG_SUFFIX) $(JTAG_DTM_SIM_ARGS) $(VSIM_JTAG_VCDPLUS_32)" \
	--server_cmd="$(RISCV)/bin/openocd $(OPENOCD_DEBUG) \
	-s $(RISCV)/share/openocd/scripts" \
	--32 \
	$(abspath $(TOP))/scripts/RocketSim32.py \
	$(JTAG_DTM_TEST)
	date > $@

stamps/%/vsim-jtag-dtm-64-$(JTAG_DTM_TEST).stamp: stamps/%/vsim$(JTAG_STAMP_SUFFIX).stamp stamps/riscv-tests.stamp
	RISCV=$(RISCV) $(GDBSERVER) \
	--sim_cmd="$(abspath $(TOP))/vsim/simv-$(PROJECT)-$*$(JTAG_DEBUG_SUFFIX) $(JTAG_DTM_SIM_ARGS) $(VSIM_JTAG_VCDPLUS_64)" \
	--server_cmd="$(RISCV)/bin/openocd $(OPENOCD_DEBUG) \
	-s $(RISCV)/share/openocd/scripts" \
	--64 \
	$(abspath $(TOP))/scripts/RocketSim64.py \
	$(JTAG_DTM_TEST)
	date > $@

stamps/%/ivlsim-jtag-dtm-32-$(JTAG_DTM_TEST).stamp: stamps/%/ivlsim$(JTAG_STAMP_SUFFIX).stamp stamps/riscv-tests.stamp
	RISCV=$(RISCV) $(GDBSERVER) \
	--sim_cmd="$(abspath $(TOP))/ivlsim/simv-$(PROJECT)-$*$(JTAG_DEBUG_SUFFIX) $(JTAG_DTM_SIM_ARGS) $(ivlsim_JTAG_VCDPLUS_32) dummybin" \
	--server_cmd="$(RISCV)/bin/openocd $(OPENOCD_DEBUG) \
	-s $(RISCV)/share/openocd/scripts" \
	--32 \
	$(abspath $(TOP))/scripts/RocketSim32.py \
	$(JTAG_DTM_TEST)
	date > $@

stamps/%/ivlsim-jtag-dtm-64-$(JTAG_DTM_TEST).stamp: stamps/%/ivlsim$(JTAG_STAMP_SUFFIX).stamp stamps/riscv-tests.stamp
	RISCV=$(RISCV) $(GDBSERVER) \
	--sim_cmd="$(abspath $(TOP))/ivlsim/simv-$(PROJECT)-$*$(JTAG_DEBUG_SUFFIX) $(JTAG_DTM_SIM_ARGS) $(ivlsim_JTAG_VCDPLUS_64) dummybin" \
	--server_cmd="$(RISCV)/bin/openocd $(OPENOCD_DEBUG) \
	-s $(RISCV)/share/openocd/scripts" \
	--64 \
	$(abspath $(TOP))/scripts/RocketSim64.py \
	$(JTAG_DTM_TEST)
	date > $@

stamps/%/verisim-jtag-dtm-32-$(JTAG_DTM_TEST).stamp:  stamps/%/verisim$(JTAG_STAMP_SUFFIX).stamp stamps/riscv-tests.stamp
	RISCV=$(RISCV) $(GDBSERVER) \
	--sim_cmd="$(abspath $(TOP))/verisim/verisim-$(PROJECT)-$*$(JTAG_DEBUG_SUFFIX) $(JTAG_DTM_SIM_ARGS) $(verisim_JTAG_VCDPLUS_32) dummybin" \
	--server_cmd="$(RISCV)/bin/openocd $(OPENOCD_DEBUG) \
	-s $(RISCV)/share/openocd/scripts" \
	--32 \
	$(abspath $(TOP))/scripts/RocketSim32.py \
	$(JTAG_DTM_TEST)
	date > $@

stamps/%/verisim-jtag-dtm-64-$(JTAG_DTM_TEST).stamp:  stamps/%/verisim$(JTAG_STAMP_SUFFIX).stamp stamps/riscv-tests.stamp
	RISCV=$(RISCV) $(GDBSERVER) \
	--sim_cmd="$(abspath $(TOP))/verisim/verisim-$(PROJECT)-$*$(JTAG_DEBUG_SUFFIX) $(JTAG_DTM_SIM_ARGS) $(verisim_JTAG_VCDPLUS_64) dummybin" \
	--server_cmd="$(RISCV)/bin/openocd $(OPENOCD_DEBUG) \
	-s $(RISCV)/share/openocd/scripts" \
	--64 \
	$(abspath $(TOP))/scripts/RocketSim64.py \
	$(JTAG_DTM_TEST)
	date > $@

VSIM_JTAG_DTM_32_TEST_STAMPS=$(foreach config,$(CONFIGS_32),stamps/$(config)/vsim-jtag-dtm-32-$(JTAG_DTM_TEST).stamp)
VSIM_JTAG_DTM_64_TEST_STAMPS=$(foreach config,$(CONFIGS_64),stamps/$(config)/vsim-jtag-dtm-64-$(JTAG_DTM_TEST).stamp)

IVLSIM_JTAG_DTM_32_TEST_STAMPS=$(foreach config,$(CONFIGS_32),stamps/$(config)/ivlsim-jtag-dtm-32-$(JTAG_DTM_TEST).stamp)
IVLSIM_JTAG_DTM_64_TEST_STAMPS=$(foreach config,$(CONFIGS_64),stamps/$(config)/ivlsim-jtag-dtm-64-$(JTAG_DTM_TEST).stamp)

verisim_JTAG_DTM_32_TEST_STAMPS=$(foreach config,$(CONFIGS_32),stamps/$(config)/verisim-jtag-dtm-32-$(JTAG_DTM_TEST).stamp)
verisim_JTAG_DTM_64_TEST_STAMPS=$(foreach config,$(CONFIGS_64),stamps/$(config)/verisim-jtag-dtm-64-$(JTAG_DTM_TEST).stamp)

vsim-jtag-dtm-tests-32 : $(VSIM_JTAG_DTM_32_TEST_STAMPS)
vsim-jtag-dtm-tests-64 : $(VSIM_JTAG_DTM_64_TEST_STAMPS)

ivlsim-jtag-dtm-tests-32 : $(IVLSIM_JTAG_DTM_32_TEST_STAMPS)
ivlsim-jtag-dtm-tests-64 : $(IVLSIM_JTAG_DTM_64_TEST_STAMPS)

verisim-jtag-dtm-tests-32 : $(verisim_JTAG_DTM_32_TEST_STAMPS)
verisim-jtag-dtm-tests-64 : $(verisim_JTAG_DTM_64_TEST_STAMPS)

# Targets for JTAG DTM full-chain simulation
vsim-jtag-dtm-regression: vsim-jtag-dtm-tests-32 vsim-jtag-dtm-tests-64
ivlsim-jtag-dtm-regression: ivlsim-jtag-dtm-tests-32 ivlsim-jtag-dtm-tests-64
verisim-jtag-dtm-regression: verisim-jtag-dtm-tests-32 verisim-jtag-dtm-tests-64
