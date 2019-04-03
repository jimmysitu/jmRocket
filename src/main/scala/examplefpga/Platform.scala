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
import sifive.blocks.devices.uart._
import sifive.blocks.devices.pinctrl._

import sifive.fpgashells.shell._
import sifive.fpgashells.ip.xilinx._
import sifive.fpgashells.clocks._

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
  val jtag = p(ExportDebugJTAG).option(new FPGAJTAGIO)
  val btns = Input(UInt(p(PeripheryGPIOKey)(0).width.W))
  val leds = Output(UInt(p(PeripheryGPIOKey)(1).width.W))
  val ints = Input(UInt(p(NExtTopInterrupts).W))
  val uarts = Vec(p(PeripheryUARTKey).length, new UARTPortIO)
}

// Example FPGA Platform
// buttons as input, leds as output, 1 jtag, 1 uart(rxd,txd)
class ExampleFPGAPlatform(implicit p: Parameters) extends LazyModule {
  val sys = LazyModule(new ExampleFPGASystem()(p))

  override lazy val module = new LazyModuleImp(this) {
    val io = IO(new ExampleFPGAPlatformIO)

    // JTAG Debug Interface
    val sys_jtag = sys.module.debug.systemjtag.get
    val io_jtag = io.jtag.get
    sys_jtag.jtag.TCK := io_jtag.jtag_TCK
    sys_jtag.jtag.TMS := io_jtag.jtag_TMS
    sys_jtag.jtag.TDI := io_jtag.jtag_TDI
    io_jtag.jtag_TDO := sys_jtag.jtag.TDO.data

    //sys_jtag.reset := io.jtag_reset
    sys_jtag.reset := false.B
    sys_jtag.mfr_id := p(JtagDTMKey).idcodeManufId.U(11.W)

    // System Reset
    sys.module.reset :=  reset.toBool | sys.module.debug.ndreset

    // Buttons Inputs
    (0 until p(PeripheryGPIOKey)(0).width).toList.foreach {
      case (i) => sys.module.gpio(0).pins(i).i.ival := io.btns(i)
    }

    // LEDs Outputs
    io.leds := Cat(
      Seq.tabulate(p(PeripheryGPIOKey)(1).width) {
        i => sys.module.gpio(1).pins(i).o.oval
      }
    )

    // Tie off no use inputs
    sys.module.gpio(1).pins.foreach { _.i.ival := false.B}

    // UART
    (io.uarts zip sys.module.uart).foreach {
      case (io, p) => {
        p.rxd := io.rxd
        io.txd := p.txd
      }
    }

    // External Interrupt
    sys.module.interrupts := io.ints
  }
}
