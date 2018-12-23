package examplefpga

import chisel3._
import freechips.rocketchip.subsystem._
import freechips.rocketchip.config.Parameters
import freechips.rocketchip.diplomacy._
import freechips.rocketchip.devices.debug._
import freechips.rocketchip.devices.tilelink._
import freechips.rocketchip.util.DontTouch
import testchipip._

import sifive.blocks.devices.gpio._

//import sifive.fpgashells.shell.xilinx.miz701nshell.{Miz701nShell}
import sifive.fpgashells.ip.xilinx.{IBUFG, IOBUF, PULLUP, PowerOnResetFPGAOnly}


//// Example FPGA Chip
//class ExampleFPGAChip(implicit val p: Parameters) extends Miz701nShell {
//}

