name := "checkedshell"
version := "1.0"
scalaVersion := "2.13.0"

resolvers += Resolver.typesafeIvyRepo("releases")
resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= 
  Seq("org.slf4j" % "slf4j-parent" % "1.7.28",
      "ch.qos.logback"  %  "logback-classic"    % "1.2.3",

      "org.scalatest" %% "scalatest" % "3.0.8" % "test",
      "org.scalactic" %% "scalactic" % "3.0.8" % "test")


mainClass in assembly := Some("com.jakway.Main")

//ignore anything named snippets.scala
excludeFilter in unmanagedSources := HiddenFileFilter || "snippets.scala"

//enable more warnings
scalacOptions in compile ++= Seq("-unchecked", "-deprecation", "-feature")
