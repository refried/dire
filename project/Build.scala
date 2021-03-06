import sbt._
import Keys._

object BuildSettings {
  val sv                = "2.11.7"
  val buildOrganization = "dire"
  val buildVersion      = "0.2.0"

  val buildSettings = Seq (
    organization       := buildOrganization,
    version            := buildVersion,
    scalaVersion       := sv,
    exportJars         := true,
    fork               := true,
    publishTo          := Some(Resolver.file("file", 
      new File(Path.userHome.absolutePath+"/.m2/repository"))),

    scalacOptions      ++= Seq(
      "-deprecation",
      "-feature",
      "-language:postfixOps",
      "-language:implicitConversions",
      "-language:higherKinds"
    )
  )
} 

object Dependencies {
  val scalaz            = "org.scalaz"
  val scalazV           = "7.1.4"

  val scalaz_core       = scalaz %% "scalaz-core" % scalazV
  val scalaz_effect     = scalaz %% "scalaz-effect" % scalazV
  val scalaz_concurrent = scalaz %% "scalaz-concurrent" % scalazV
  val scalacheckZ       = scalaz %% "scalaz-scalacheck-binding" % scalazV % "test"

  val scalacheck        = "org.scalacheck" %% "scalacheck" % "1.11.4" % "test"
  val scala_reflect     = "org.scala-lang" % "scala-reflect" % BuildSettings.sv

  val deps              = Seq(scalaz_core, scalaz_effect, scalaz_concurrent,
                              scalacheckZ, scalacheck, scala_reflect)
}

object UtilBuild extends Build {
  import Dependencies._
  import BuildSettings._

  lazy val plusDeps =
    BuildSettings.buildSettings ++ Seq(libraryDependencies ++= deps)

  lazy val dire = Project(
    "dire",
    file("."),
    settings = buildSettings
  ) aggregate(core, swing, example)

  lazy val core = Project(
    "dire-core",
    file("core"),
    settings = plusDeps
  )

  lazy val example = Project(
    "dire-example",
    file("example"),
    settings = plusDeps ++
               com.github.retronym.SbtOneJar.oneJarSettings
  ) dependsOn(core, swing)

  lazy val swing = Project(
    "dire-swing",
    file("swing"),
    settings = plusDeps
  ) dependsOn(core)
}

// vim: set ts=2 sw=2 nowrap et:
