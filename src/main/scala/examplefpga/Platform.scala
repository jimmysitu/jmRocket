package examplefpga

import chisel3._
import freechips.rocketchip.subsystem._
import freechips.rocketchip.config.{Field, Parameters}
import freechips.rocketchip.diplomacy._
import freechips.rocketchip.devices.debug._
import freechips.rocketchip.devices.tilelink._
import freechips.rocketchip.util.DontTouch
import testchipip._

import sifive.blocks.devices.jtag._
import sifive.blocks.devices.gpio._
import sifive.blocks.devices.pinctrl._

import sifive.fpgashells.shell._

case object BuildPlatform extends Field[Parameters => ExampleFPGAPlatform]

// PinGen
object PinGen {
  def apply(): BasePin =  {
    val pin = new BasePin()
    pin
  }
}

// Example FPGA Platform IO
class ExampleFPGAPlatformIO(implicit val p: Parameters) extends Bundle {
  val jtag = p(IncludeJtagDTM).option(new FPGAJTAGIO)
  val btns = Input(UInt(p(PeripheryGPIOKey)(0).width.W))
  val leds = Ouptput(UInt(p(PeripheryGPIOKey)(1).width.W))
  val ints = Input(UInt(p(NExtTopInterrupts).W))
  val jtag_reset = Input(Bool())
  val ndreset = Input(Bool())
}

// Example FPGA Platform
// 2 buttons as input, 2 leds as output, 1 jtag
class ExampleFPGAPlatform(implicit val p: Parameters) extends Module {
  val sys = p(BuildTop)(clock, reset.toBool, p)
  val io = IO(new ExampleFPGAPlatformIO)

  // JTAG Debug Interface
  val sys_jtag = sys.debug.systemjtag.get
  sys_jtag.jtag.TCK := io.jtag.jtag_TCK
  sys_jtag.jtag.TMS := io.jtag.jtag_TMS
  sys_jtag.jtag_TDI := io.jtag.jtag_TDI
  io.jtag.jtag_TDO := sys_jtag.jtag_TDO.data

  sys_jtag.reset := io.jtag_reset
  sys_jtag.mfr_id := p(JtagDTMKey).idcodeManufId.U(11.W)

  // Buttons Inputs
  io.btns.zipWithIndex.foreach { (btn, i) => sys.gpio(0).pins(i).i.ival := btn }

  // LEDs Outputs
  io.leds.zipWithIndex.foreach { (led, i) => led := sys.gpio(1).pins(i).o.oval }

  // External Interrupt
  sys.interrupts := io.ints

}
