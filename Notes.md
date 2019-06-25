# Design Notes



## Shell.scala

A **shell** of a project is a container of FPAG board. Usually it extends a FPGA shell from [fpga-shell](https://github.com/jimmysitu/fpga-shells). 

One can override the shell for FPGA board rework or daughter board is used.

Default FPGA shells in  [fpga-shell](https://github.com/jimmysitu/fpga-shells)  contain basic IO overlay  of the FPAG board. IO xdc will be generated automatic if design is connected

IO overlay should only care about the IO design, insert IO buffer, clock buffer etc. It should not control the generation of IO device.

## Chip.scala

A **chip** is the top design of  an FPGA chip. Usually chip can be reused for project using the same shell.

The chip connects FPGA shell resource to platform design here, e.g. reset, clock, PLL etc.

- Chip cannot generate RTL by itself since it depend on FPGA Shell resource

## Platform.scala

A **platform**, which include all design IPs here. A platform can be reused by other ASIC/FPGA project

- New module of System
- AXI4 base IPs can be placed here, such as co-processor, IO device
- Tie off unused pins here

An ASIC design take **platform** as the top of design

## System.scala

System, extends RocketSubsystem, which include all Rocket SoC design. It could be ready reused by and other project base on rocket core(s).

RocketSubsystem use Tilelink as internal SoC bus. All tilelink base device should be instantiation here by adding nodes in design, override design function to implement special function for custom design.

Attach Tilelink device by using *with* key word such as

```scala
class ExampleSystem() extends RocketSubsystem with HasPeripheryGPIO
```

And parameter should include the PeripheryGPIOKey for GPIO device configuration. Leave this key empty if this device is not used.

TestHarness takes this module as DUT, for verification test

## TestHarness.scala

**TestHarness** is a testbench for **system** only. TestHarness is focus on rocket core verification. There are two kinds of test vector

- RISC-V ISA compatibility test
- Tilelink device test