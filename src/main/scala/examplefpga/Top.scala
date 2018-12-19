package examplefpga

import chisel3._
import freechips.rocketchip.subsystem._
import freechips.rocketchip.config.Parameters
import freechips.rocketchip.devices.debug._
import freechips.rocketchip.devices.tilelink._
import freechips.rocketchip.util.DontTouch
import testchipip._

import sifive.blocks.devices.gpio._

class ExampleFPGATop(implicit p: Parameters) extends RocketSubsystem
    with CanHaveMasterAXI4MemPort
    with CanHaveMasterAXI4MMIOPort
    with HasPeripheryBootROM
    with HasSyncExtInterrupts
    with HasPeripheryGPIO
    with HasPeripheryDebug {
  override lazy val module = new ExampleFPGATopModule(this)
}


class ExampleFPGATopModule[+L <: ExampleFPGATop](l: L) extends RocketSubsystemModuleImp(l)
    with HasRTCModuleImp
    with CanHaveMasterAXI4MemPortModuleImp
    with CanHaveMasterAXI4MMIOPortModuleImp
    with HasPeripheryBootROMModuleImp
    with HasExtInterruptsModuleImp
    with HasPeripheryGPIOModuleImp
    with HasPeripheryDebugModuleImp
    with DontTouch {
}

