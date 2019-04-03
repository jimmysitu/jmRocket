# Jimmy's RISC-V Rocket Core Project [![Build Status](https://travis-ci.com/jimmysitu/jmRocket.svg?branch=master)](https://travis-ci.com/jimmysitu/jmRocket)

This is a RISC-V project of Rocket Core

## Configures
* Define design hierarchy, for more design notes, please refer to Notes.md
  * Shell, override shell function if FPAG board is reworked
    - Usually extends a FPGA Shell directly
    - Override some resource, e.g. add daughter board, reworked pin out etc.
  * Chip, top level of an FPGA chip, same board project can be reused
    - Connect FPGA shell resource to platform design here, e.g. reset, clock, pll etc.
    - Chip cannot generate RTL by itself since it depend on FPGA Shell resource
  * Platform, which include all design IPs here, this platform can be reused by other ASIC/FPGA project
    * New module of System
    * AXI4 base IPs can be placed here
    * Tie off unused pins here
  * System, extends RocketSubsystem, which include all SoC design.
    - It should be ready reused by and other project base on rocket core(s)
    - Override design function here
    - TestHarness takes this module as DUT, for verification test

## FPGA Compose Flow
* Default FPGA board
  * Generate FPGA RTL for a common FPGA chip by

    ```bash
    make -f Makefile.examplefpga verilog
    ```

  * Clock, reset, PLL may need to be done by manually FPGA flow

* Miz701N

  * Taobao shop [link here](https://item.taobao.com/item.htm?spm=a1z09.2.0.0.1f4f2e8dxhGxDK&id=534106142428&_u=lco8l24734)

  * Generate FPGA bitstream

    ```bash
    make -f Makefile.miz701n bit
    ```


## ASIC Compose Flow
* Generate RTL for ASIC flow
  ```bash
  make -f Makefile.asic verilog
  ```

  All SRAM IPs is described \*.conf file

* \*.behav_srams.v is SRAM IP behavior model

* Release all files to ASIC flow by using
  ```bash
  make -f Makefile.asic release
  ```

