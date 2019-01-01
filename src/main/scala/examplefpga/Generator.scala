package examplefpga

import chisel3._
import freechips.rocketchip.util.GeneratorApp

object Generator extends GeneratorApp {
  val longName = names.topModuleProject + "." + names.topModuleClass + "." + names.configs
  generateFirrtl
  generateAnno
}

object FPGAGenerator extends GeneratorApp {
  val longName = names.topModuleProject + "." + names.topModuleClass + "." + names.configs
  generateFirrtl
  generateAnno
  generateROMs
  generateArtefacts
}
