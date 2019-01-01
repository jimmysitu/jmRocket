package examplefpga

import chisel3._
import freechips.rocketchip.config._
import sifive.fpgashells.shell.xilinx._

class ExampleFPGAShell(implicit p: Parameters) extends Miz701nShell {
  // Override FPGA shell here if FPGA board is reworked
}
