import checkedshell.project.CommonSettings

lazy val root = (project in file("."))
    .aggregate(main, programs)

lazy val main = CommonSettings((project in file("main")))

lazy val programs = CommonSettings((project in file("programs")))
                    .dependsOn(main)

//mainClass in assembly := Some("com.foo.bar.Baz")

//ignore anything named snippets.scala
excludeFilter in unmanagedSources := HiddenFileFilter || "snippets.scala"
