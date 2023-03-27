lazy val commonSettings = Seq(
  organization := "com.example.dentaku",
  scalaVersion := "3.2.2",
  version := "1.0.0"
)

lazy val root = (project in file("."))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    inThisBuild(commonSettings),
    name := "dentaku",
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.4.0",
    libraryDependencies += "com.raquo" %%% "laminar" % "0.14.2",
    fastLinkJS := fastLinkJSWrapper("./public/index.js").value
  )

def fastLinkJSWrapper(newOutputPath: String) = Def.taskDyn {
  Def.task {
    val oldOutputDir = (ThisProject / Compile / fastLinkJSOutput).value
    val report = (ThisProject / Compile / fastLinkJS).value
    val outputFileName = report.data.publicModules.head.jsFileName
    val outputFilePath =
      java.nio.file.Path.of(oldOutputDir.toString, outputFileName).toFile

    IO.copyFile(
      outputFilePath,
      file(newOutputPath)
    )

    val sourceMapName = report.data.publicModules.head.sourceMapName
    sourceMapName.foreach { fname =>
      val sourceMapPath =
        java.nio.file.Path.of(oldOutputDir.toString, fname).toFile
      IO.copyFile(
        sourceMapPath,
        file(s"${newOutputPath}.map")
      )
    }

    report
  }
}
