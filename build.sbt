activatorScalaJSSettings

name := "hello-scala-js"

version := "1.0"

scalaVersion := "2.11.0"

libraryDependencies +=
  "org.scala-lang.modules.scalajs" %% "scalajs-jasmine-test-framework" % scalaJSVersion % "test"
