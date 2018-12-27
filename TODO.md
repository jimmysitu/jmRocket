# jmRocket TODO
* FPGA compose flow
  * Take freedom fpga flow as reference
  * FPGA default configure for rocket chip
  * Define design hierarchy
    * System, extends RocketSubsystem, which include all SoC design.
      * It should be ready reused by and other project for both FPGA and ASIC
      * Overrride design function here
      * TestHarness takes this module as DUT, for verification test
    * Platform, which include all FPGA IPs here, same FPGA Chip project can be reused
      * New module of system
      * Tie off unused pins here
      * PLL, DCM, MIG added here
    * Chip, top level of an FPGA Chip, extends FPGA board shell, same board project can be reused
      * Tie pins to FPGA physical pins
      * Override package pins if FPGA board is rework
  * RTL simulation environment

* ASIC compose flow
  * ASIC default configure

* common.mk and Makefrag should be merged to one file
* README.md update
