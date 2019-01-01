# jmRocket TODO
* FPGA compose flow
  * Take freedom fpga flow as reference
  * FPGA default configure for rocket chip
  * Define design hierarchy
    * System, extends RocketSubsystem, which include all SoC design.
      * It should be ready reused by and other project base on rocket core(s)
      * Overrride design function here
      * TestHarness takes this module as DUT, for verification test
    * Platform, which include all design IPs here, this platform can be reused by other ASIC/FPGA project
      * New module of system
      * Tie off unused pins here
    * Chip, top level of an FPGA Chip, same board project can be reused
      * Connect FPGA shell resource to platform design here
    * Shell, override shell function if FPAG board is reworked
  * RTL simulation environment

* ASIC compose flow
  * ASIC default configure

* common.mk and Makefrag should be merged to one file
* README.md update
