lazy val commonSettings = Seq(
  organization := "edu.berkeley.cs",
  version := "1.0",
  scalaVersion := "2.12.4",
  traceLevel := 15,
  scalacOptions ++= Seq("-deprecation","-feature","-unchecked","-Xsource:2.11","-language:reflectiveCalls"),
  libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  libraryDependencies += "org.json4s" %% "json4s-native" % "3.5.3",
  libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
  resolvers ++= Seq(
    Resolver.sonatypeRepo("snapshots"),
    Resolver.sonatypeRepo("releases"),
    Resolver.mavenLocal))

lazy val rocketChip = RootProject(file("rocket-chip"))

lazy val testchipip = (project in file("testchipip")).
  settings(commonSettings:_*).
  dependsOn(rocketChip)

lazy val sifiveBlocks = (project in file("sifive-blocks")).
  settings(commonSettings:_*).
  dependsOn(rocketChip)

lazy val default = (project in file(".")).
  settings(commonSettings:_*).
  dependsOn(testchipip, sifiveBlocks)

