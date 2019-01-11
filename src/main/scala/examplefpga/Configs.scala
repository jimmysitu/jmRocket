package examplefpga

import chisel3._
import freechips.rocketchip.config._
import freechips.rocketchip.devices.debug._
import freechips.rocketchip.devices.tilelink._
import freechips.rocketchip.diplomacy._
import freechips.rocketchip.rocket._
import freechips.rocketchip.tile._
import freechips.rocketchip.tilelink._
import freechips.rocketchip.subsystem._
import freechips.rocketchip.util._
import testchipip._

import sifive.blocks.devices.gpio._
import sifive.fpgashells.shell.xilinx._
import sifive.fpgashells.shell._


class WithBootROM extends Config((site, here, up) => {
  case BootROMParams => BootROMParams(
    contentFileName = s"./testchipip/bootrom/bootrom.rv${site(XLen)}.img")
})

object ConfigValName {
  implicit val valName = ValName("ExampleDesign")
}
import ConfigValName._


/**
 * System level Config
 */
class WithExampleFPGASystem extends Config((site, here, up) => {
  case BuildTop => (clock: Clock, reset: Bool, p: Parameters) => {
    Module(LazyModule(new ExampleFPGASystem()(p)).module)
  }
  case JtagDTMKey => new JtagDTMConfig (
    idcodeVersion = 2,
    idcodePartNum = 0x000,
    idcodeManufId = 0x489,
    debugIdleCycles = 5)
  case PeripheryGPIOKey => List(
    GPIOParams(address = 0x10012000, width = 2, includeIOF = false),
    GPIOParams(address = 0x10013000, width = 2, includeIOF = false))
})

class With1Tiny64Core extends Config((site, here, up) => {
  case XLen => 64
  case RocketTilesKey => List(RocketTileParams(
      core = RocketCoreParams(
        useVM = false,
        fpu = None,
        mulDiv = Some(MulDivParams(mulUnroll = 8))),
      btb = None,
      dcache = Some(DCacheParams(
        rowBits = site(SystemBusKey).beatBits,
        nSets = 256, // 16Kb scratchpad
        nWays = 1,
        nTLBEntries = 4,
        nMSHRs = 0,
        blockBytes = site(CacheBlockBytes),
        scratch = Some(0x80000000L))),
      icache = Some(ICacheParams(
        rowBits = site(SystemBusKey).beatBits,
        nSets = 64,
        nWays = 1,
        nTLBEntries = 4,
        blockBytes = site(CacheBlockBytes)))))
  case RocketCrossingKey => List(RocketCrossingParams(
    crossingType = SynchronousCrossing(),
    master = TileMasterPortParams()
  ))
})

class BaseExampleConfig extends Config(
  new WithBootROM ++
  new freechips.rocketchip.system.DefaultConfig)

class DefaultExampleConfig extends Config(
  new WithJtagDTM ++
  new WithExampleFPGASystem ++ new BaseExampleConfig)

class BaseTiny64Config extends Config(
  new WithBootROM ++
  new WithNoMemPort ++
  new WithNoMMIOPort ++
  new WithNMemoryChannels(0) ++
  new WithNBanks(0) ++
  new With1Tiny64Core ++
  new freechips.rocketchip.system.BaseConfig)

class Tiny64Config extends Config(
  new WithExampleFPGASystem ++ new BaseTiny64Config)

/**
 * Platform Level Config
 */
class WithExampleFPGAPlatform extends Config((site, here, up) => {
  case BuildPlatform => {
    (p: Parameters) => new ExampleFPGAPlatform()(p)
  }
  case BuildTop => (clock: Clock, reset: Bool, p: Parameters) => {
    Module(LazyModule(new ExampleFPGASystem()(p)).module)
  }
  case JtagDTMKey => new JtagDTMConfig (
    idcodeVersion = 2,
    idcodePartNum = 0x000,
    idcodeManufId = 0x489,
    debugIdleCycles = 5)
  case PeripheryGPIOKey => List(
    GPIOParams(address = 0x10012000, width = 2, includeIOF = false),
    GPIOParams(address = 0x10013000, width = 2, includeIOF = false))
})

class Tiny64Platform extends Config(
  new WithJtagDTM ++
  new WithExampleFPGAPlatform ++ new BaseTiny64Config)

/**
 * Chip Level Config
 */
class WithExampleFPGAChip extends Config((site, here, up) => {
  case DesignKey => {
    (p: Parameters) => new ExampleFPGAChip()(p)
  }
  case BuildTop => (clock: Clock, reset: Bool, p: Parameters) => {
    Module(LazyModule(new ExampleFPGASystem()(p)).module)
  }
  case JtagDTMKey => new JtagDTMConfig (
    idcodeVersion = 2,
    idcodePartNum = 0x000,
    idcodeManufId = 0x489,
    debugIdleCycles = 5)
  case PeripheryGPIOKey => List(
    GPIOParams(address = 0x10012000, width = 2, includeIOF = false),
    GPIOParams(address = 0x10013000, width = 2, includeIOF = false))
})

class Tiny64Chip extends Config(
  new WithJtagDTM ++
  new WithExampleFPGAChip ++ new BaseTiny64Config)
