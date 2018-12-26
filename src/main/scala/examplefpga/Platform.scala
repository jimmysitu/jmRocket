package examplefpga

import chisel3._
import chisel3.util._
import freechips.rocketchip.subsystem._
import freechips.rocketchip.config.{Field, Parameters}
import freechips.rocketchip.diplomacy._
import freechips.rocketchip.devices.debug._
import freechips.rocketchip.devices.tilelink._
import freechips.rocketchip.util._
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
  val leds = Output(UInt(p(PeripheryGPIOKey)(1).width.W))
  //val leds = Output(Vec(p(PeripheryGPIOKey)(1).width, Bool()))
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
  val io_jtag = io.jtag.get
  sys_jtag.jtag.TCK := io_jtag.jtag_TCK
  sys_jtag.jtag.TMS := io_jtag.jtag_TMS
  sys_jtag.jtag.TDI := io_jtag.jtag_TDI
  io_jtag.jtag_TDO := sys_jtag.jtag.TDO.data

  sys_jtag.reset := io.jtag_reset
  sys_jtag.mfr_id := p(JtagDTMKey).idcodeManufId.U(11.W)

  // Buttons Inputs
  (0 until p(PeripheryGPIOKey)(0).width).toList.foreach {
    case (i) => sys.gpio(0).pins(i).i.ival := io.btns(i)
  }

  // LEDs Outputs
  io.leds := Cat(
    Seq.tabulate(p(PeripheryGPIOKey)(1).width) {
      i => sys.gpio(1).pins(i).o.oval
    }
  )
  // Tie off no use inputs
  sys.gpio(1).pins.foreach { _.i.ival := false.B}

  // External Interrupt
  sys.interrupts := io.ints

}
