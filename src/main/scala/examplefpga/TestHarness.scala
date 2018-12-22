package examplefpga

import chisel3._
import freechips.rocketchip.diplomacy.LazyModule
import freechips.rocketchip.config.{Field, Parameters}
import freechips.rocketchip.devices.debug._

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
  dut.gpio.map {
    (g) => g.pins.zipWithIndex.map { case(pin, i) => pin.i.ival := (i.U)(0)}
  }
}

