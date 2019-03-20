package examplefpga

import chisel3._
import freechips.rocketchip.config.{Field, Parameters}
import freechips.rocketchip.diplomacy._
import freechips.rocketchip.tilelink._
import freechips.rocketchip.subsystem._
import freechips.rocketchip.devices.tilelink._
import freechips.rocketchip.util.DontTouch
import freechips.rocketchip.devices.debug._
import testchipip._

import sifive.blocks.devices.gpio._
import sifive.blocks.devices.uart._

case object BuildTop extends Field[(Clock, Bool, Parameters) => ExampleFPGASystemModuleImp[ExampleFPGASystem]]

// Example FPGA System
class ExampleFPGASystem(implicit p: Parameters) extends RocketSubsystem
    with CanHaveMasterAXI4MemPort
    with CanHaveMasterAXI4MMIOPort
    with HasPeripheryBootROM
    with HasSyncExtInterrupts
    with HasPeripheryGPIO
    with HasPeripheryUART
    with HasPeripheryDebug {

  // Define or override periphery here, e.g.
  //val gpioNodes = p(PeripheryGPIOKey).map { ps => GPIO.attach(GPIOAttachParams(ps, pbus, ibus.fromAsync)).ioNode.makeSink }

  // Override SoC system module here
  override lazy val module = new ExampleFPGASystemModuleImp(this)
  
  // The sbus masters the cbus; here we convert TL-UH -> TL-UL
  sbus.crossToBus(cbus, NoCrossing)

  // The cbus masters the pbus; which might be clocked slower
  cbus.crossToBus(pbus, SynchronousCrossing())

  // The fbus masters the sbus; both are TL-UH or TL-C
  FlipRendering { implicit p =>
    sbus.crossFromBus(fbus, SynchronousCrossing())
  }

  // The sbus masters the mbus; here we convert TL-C -> TL-UH
  private val BankedL2Params(nBanks, coherenceManager) = p(BankedL2Key)
  private val (in, out, halt) = coherenceManager(this)
  if (nBanks != 0) {
    sbus.coupleTo("coherence_manager") { in :*= _ }
    mbus.coupleFrom("coherence_manager") { _ :=* BankBinder(mbus.blockBytes * (nBanks-1)) :*= out }
  }
}

class ExampleFPGASystemModuleImp[+L <: ExampleFPGASystem](_outer: L) extends RocketSubsystemModuleImp(_outer)
    with HasRTCModuleImp
    with CanHaveMasterAXI4MemPortModuleImp
    with CanHaveMasterAXI4MMIOPortModuleImp
    with HasPeripheryBootROMModuleImp
    with HasExtInterruptsModuleImp
    with HasPeripheryGPIOModuleImp
    with HasPeripheryUARTModuleImp
    with HasPeripheryDebugModuleImp
    with DontTouch {

  // Define or override SoC design function here, e.g.
  //val gpio = _outer.gpioNodes.zipWithIndex.map { case(n,i) => n.makeIO()(ValName(s"gpio_$i")) }
}

