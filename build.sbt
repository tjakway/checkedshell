name := "checkedshell"
version := "1.0"
scalaVersion := "2.12.9"

resolvers += Resolver.typesafeIvyRepo("releases")
resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= 
  Seq("org.slf4j" % "slf4j-parent" % "1.7.28",
      "ch.qos.logback"  %  "logback-classic"    % "1.2.3",

      "org.scalatest" %% "scalatest" % "3.0.8" % "test",
      "org.scalactic" %% "scalactic" % "3.0.8" % "test",
      "org.scalacheck" %% "scalacheck" % "1.14.0" % "test",
      "org.scalatestplus" % "scalatestplus-scalacheck_2.12" % "1.0.0-SNAP8")

mainClass in assembly := Some("com.jakway.Main")

//ignore anything named snippets.scala
excludeFilter in unmanagedSources := HiddenFileFilter || "snippets.scala"

//enable more warnings
scalacOptions in compile ++= Seq("-unchecked", "-deprecation", "-feature")
