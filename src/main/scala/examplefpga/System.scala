package examplefpga

import chisel3._
import freechips.rocketchip.subsystem._
import freechips.rocketchip.config.{Field, Parameters}
import freechips.rocketchip.diplomacy._
import freechips.rocketchip.devices.debug._
import freechips.rocketchip.devices.tilelink._
import freechips.rocketchip.util.DontTouch
import testchipip._

import sifive.blocks.devices.gpio._

case object BuildTop extends Field[(Clock, Bool, Parameters) => ExampleFPGASystemModule[ExampleFPGASystem]]

// Example FPGA System
class ExampleFPGASystem(implicit p: Parameters) extends RocketSubsystem
    with CanHaveMasterAXI4MemPort
    with CanHaveMasterAXI4MMIOPort
    with HasPeripheryBootROM
    with HasSyncExtInterrupts
    //with HasPeripheryGPIO
    with HasPeripheryDebug {

  // Define or override periphery here
  val gpioNodes = p(PeripheryGPIOKey).map { ps => GPIO.attach(GPIOAttachParams(ps, pbus, ibus.fromAsync)).ioNode.makeSink }

  // Override SoC system module here
  override lazy val module = new ExampleFPGASystemModule(this)
}

class ExampleFPGASystemModule[+L <: ExampleFPGASystem](_outer: L) extends RocketSubsystemModuleImp(_outer)
    with HasRTCModuleImp
    with CanHaveMasterAXI4MemPortModuleImp
    with CanHaveMasterAXI4MMIOPortModuleImp
    with HasPeripheryBootROMModuleImp
    with HasExtInterruptsModuleImp
    //with HasPeripheryGPIOModuleImp
    with HasPeripheryDebugModuleImp
    with DontTouch {

  // Define or override SoC design function here
  val gpio = _outer.gpioNodes.zipWithIndex.map { case(n,i) => n.makeIO()(ValName(s"gpio_$i")) }
}

