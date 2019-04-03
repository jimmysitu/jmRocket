# Design Notes



## Shell.scala

A **shell** of a project is a container of FPAG board. Usually it extends a FPGA shell from [fpga-shell](https://github.com/jimmysitu/fpga-shells). 

One can override the shell for FPGA board rework or daughter board is used.

Default FPGA shells in  [fpga-shell](https://github.com/jimmysitu/fpga-shells)  contain basic IO overlay  of the FPAG board. IO xdc will be generated automatic if design is connected

IO overlay should only care about the IO design, insert IO buffer, clock buffer etc. It should not control the generation of IO device.

## Chip.scala

A **chip** is the top design of  an FPGA chip. Usually chip can be reused for project using the same shell.

The chip connects FPGA shell resource to platform design here, e.g. reset, clock, pll etc.

- Chip cannot generate RTL by itself since it depend on FPGA Shell resource

## Platform.scala



## System.scala

## TestHarness.scala

