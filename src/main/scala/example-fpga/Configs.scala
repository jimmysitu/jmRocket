package examplefpga

import chisel3._
import freechips.rocketchip.config.{Parameters, Config}
import freechips.rocketchip.subsystem.{WithJtagDTM, WithRoccExample, WithNMemoryChannels, WithNBigCores, WithRV32}
import freechips.rocketchip.diplomacy.{LazyModule, ValName}
import freechips.rocketchip.devices.tilelink.BootROMParams
import freechips.rocketchip.devices.debug._
import freechips.rocketchip.tile.XLen
import testchipip._

class WithBootROM extends Config((site, here, up) => {
  case BootROMParams => BootROMParams(
    contentFileName = s"./testchipip/bootrom/bootrom.rv${site(XLen)}.img")
})

object ConfigValName {
  implicit val valName = ValName("TestHarness")
}
import ConfigValName._

class WithExampleFPGATop extends Config((site, here, up) => {
  case BuildTop => (clock: Clock, reset: Bool, p: Parameters) => {
    Module(LazyModule(new ExampleFPGATop()(p)).module)
  }
})

class BaseExampleConfig extends Config(
  new WithBootROM ++
  new freechips.rocketchip.system.DefaultConfig)

class DefaultExampleConfig extends Config(
  new WithJtagDTM ++
  new WithExampleFPGATop ++ new BaseExampleConfig)

