package examplefpga

import chisel3._
import freechips.rocketchip.diplomacy.LazyModule
import freechips.rocketchip.config.{Field, Parameters}
import freechips.rocketchip.util.GeneratorApp
import freechips.rocketchip.devices.debug._

case object BuildTop extends Field[(Clock, Bool, Parameters) => ExampleFPGATopModule[ExampleFPGATop]]

class TestHarness(implicit val p: Parameters) extends Module {
  val io = IO(new Bundle {
    val success = Output(Bool())
  })

  val dut = p(BuildTop)(clock, reset.toBool, p)
  dut.reset := reset.toBool || dut.debug.ndreset

  Debug.connectDebug(dut.debug, clock, reset.toBool, io.success)
  dut.connectSimAXIMem()
  dut.connectSimAXIMMIO()
  dut.dontTouchPorts()
  dut.tieOffInterrupts()
  dut.gpio(0).pins.map { (pin) => pin.i.ival := false.B}
}

object Generator extends GeneratorApp {
  val longName = names.topModuleProject + "." + names.topModuleClass + "." + names.configs
  generateFirrtl
  generateAnno
}
