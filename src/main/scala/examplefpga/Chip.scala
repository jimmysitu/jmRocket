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
import sifive.blocks.devices.uart._

import sifive.fpgashells.clocks._
import sifive.fpgashells.shell._
import sifive.fpgashells.shell.xilinx._
import sifive.fpgashells.ip.xilinx.{IBUFG, IOBUF, PULLUP, PowerOnResetFPGAOnly}

// Example FPGA Chip
class ExampleFPGAChip(implicit p: Parameters) extends LazyModule {
  // Clock & Reset from FPGA shell
  val chipClock = p(ClockInputOverlayKey).head(ClockInputOverlayParams())
  val corePLL   = p(PLLFactoryKey)()
  val coreGroup = ClockGroup()
  val wrangler  = LazyModule(new ResetWrangler)
  //val coreClock = ClockSinkNode(freqMHz = p(DevKitFPGAFrequencyKey))
  val coreClock = ClockSinkNode(freqMHz = 50)
  coreClock := wrangler.node := coreGroup := corePLL := chipClock

  // SoC design
  val platform = LazyModule(new ExampleFPGAPlatform()(p))

  // Interface from FPGA shell
  val jtag = p(JTAGDebugOverlayKey).headOption.map(_(JTAGDebugOverlayParams()))
  val leds = p(LEDOverlayKey).headOption.map(_(LEDOverlayParams()))
  val btns = p(SwitchOverlayKey).headOption.map(_(SwitchOverlayParams()))
 
  // One or more UARTs
  val uarts = p(UARTPortOverlayKey).map(_(UARTPortOverlayParams()))


  override lazy val module = new LazyRawModuleImp(this) {
    val (core, _) = coreClock.in(0)
    childClock := core.clock
    childReset := core.reset

    jtag.get <> platform.module.io.jtag.get
    leds.get <> platform.module.io.leds
    btns.get <> platform.module.io.btns
    (uarts zip platform.module.io.uarts).foreach {
      case (u, p) => u <> p
    }
  }
}

