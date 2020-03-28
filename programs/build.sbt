import checkedshell.project.CommonSettings
import checkedshell.project.Dependencies

name := CommonSettings.Names.programsProjectName
version := CommonSettings.version
scalaVersion := CommonSettings.scalaVersion
scalacOptions ++= CommonSettings.scalacOptions

libraryDependencies ++= Dependencies.all
