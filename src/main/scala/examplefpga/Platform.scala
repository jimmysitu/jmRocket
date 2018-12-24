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

// PinGen
object PinGen {
  def apply(): BasePin =  {
    val pin = new BasePin()
    pin
  }
}

class ExampleFPGAPlatformIO(implicit val p: Parameters) extends Bundle {
  val pins = new Bundle {
    val jtag = new JTAGPins(() => PinGen(), false)
    val btns = new GPIOPins(() => PinGen(), p(PeripheryGPIOKey)(0))
    val leds = new GPIOPins(() => PinGen(), p(PeripheryGPIOKey)(1))
  }
  val jtag_reset = Input(Bool())
  val ndreset = Input(Bool())
  val extInterrupts = Input(UInt(p(NExtTopInterrupts).W))
}

// Example FPGA Platform
// 2 buttons as input, 2 leds as output, 1 jtag
class ExampleFPGAPlatform(implicit val p: Parameters) extends Module {
  val sys = p(BuildTop)(clock, reset.toBool, p)
  val io = IO(new ExampleFPGAPlatformIO)

  // JTAG Debug Interface
  val sys_jtag = sys.debug.systemjtag.get
  JTAGPinsFromPort(io.pins.jtag, sys_jtag.jtag)
  sys_jtag.reset := io.jtag_reset
  sys_jtag.mfr_id := p(JtagDTMKey).idcodeManufId.U(11.W)

  // Buttons Inputs
  GPIOPinsFromPort(io.pins.btns, sys.gpio(0))

  // LEDs Outputs
  GPIOPinsFromPort(io.pins.leds, sys.gpio(1))

  // External Interrupt
  sys.interrupts := io.extInterrupts

}