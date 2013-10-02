import sbt._
import Keys._

object BuildSettings {
  val sv = "2.10.3"
  val buildOrganization = "dire"
  val buildVersion = "0.1.0-SNAPSHOT"

  val buildSettings = Defaults.defaultSettings ++ Seq (
    organization := buildOrganization,
    version := buildVersion,
    scalaVersion := sv,
    exportJars := true,
    fork := true,
    publishTo := Some(Resolver.file("file", 
      new File(Path.userHome.absolutePath+"/.m2/repository"))),
    scalacOptions ++= Seq ("-deprecation", "-feature",
      "-language:postfixOps", "-language:implicitConversions",
      "-language:higherKinds")
  )
} 

object Dependencies {
  val scalaz = "org.scalaz"
  val scalazV = "7.0.3"

  val scala_reflect = "org.scala-lang" % "scala-reflect" % BuildSettings.sv

  val scalaz_core = scalaz %% "scalaz-core" % scalazV
  val scalaz_effect = scalaz %% "scalaz-effect" % scalazV
  val scalaz_concurrent = scalaz %% "scalaz-concurrent" % scalazV
  val scalacheckZ = scalaz %% "scalaz-scalacheck-binding" % scalazV % "test"

  val scalacheck = "org.scalacheck" %% "scalacheck" % "1.10.0" % "test"

  val coolness = Seq(scalaz_core, scalaz_effect, scalaz_concurrent,
                     scalacheckZ, scalacheck)
}

object UtilBuild extends Build {
  import Dependencies._
  import BuildSettings._

  def addDeps (ds: ModuleID*) =
    BuildSettings.buildSettings ++
    Seq(libraryDependencies ++= (coolness ++ ds))

  lazy val dire = Project (
    "dire",
    file("."),
    settings = buildSettings
  ) aggregate(core, example, swing)

  lazy val core = Project (
    "dire-core",
    file("core"),
    settings = addDeps(scala_reflect)
  )

  lazy val example = Project (
    "dire-example",
    file("example"),
    settings = addDeps(scala_reflect) ++
               com.github.retronym.SbtOneJar.oneJarSettings
  ) dependsOn(core, swing)

  lazy val swing = Project (
    "dire-swing",
    file("swing"),
    settings = addDeps(scala_reflect)
  ) dependsOn(core)
}

// vim: set ts=2 sw=2 nowrap et:
