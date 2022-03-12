import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

Global / onChangedBuildSource := ReloadOnSourceChanges

inThisBuild(
  Seq(
    organization           := "io.github.fun-stack",
    scalaVersion           := "2.13.8",
    licenses               := Seq("MIT License" -> url("https://opensource.org/licenses/MIT")),
    homepage               := Some(url("https://github.com/fun-stack/fun-stack-local")),
    scmInfo                := Some(
      ScmInfo(
        url("https://github.com/fun-stack/fun-stack-local"),
        "scm:git:git@github.com:fun-stack/fun-stack-local.git",
        Some("scm:git:git@github.com:fun-stack/fun-stack-local.git"),
      ),
    ),
    pomExtra               :=
      <developers>
      <developer>
        <id>jkaroff</id>
        <name>Johannes Karoff</name>
        <url>https://github.com/cornerman</url>
      </developer>
    </developers>,
    sonatypeCredentialHost := "s01.oss.sonatype.org",
    sonatypeRepository     := "https://s01.oss.sonatype.org/service/local",
  ),
)

lazy val commonSettings = Seq(
  addCompilerPlugin("org.typelevel" % "kind-projector" % "0.13.2" cross CrossVersion.full),
  libraryDependencies  ++=
    Deps.scalatest.value % Test ::
      Nil,
  scalacOptions --= Seq("-Xfatal-warnings"),
)

lazy val jsSettings = Seq(
  useYarn       := true,
  scalacOptions += {
    val githubRepo    = "fun-stack/fun-stack-local"
    val local         = baseDirectory.value.toURI
    val subProjectDir = baseDirectory.value.getName
    val remote        = s"https://raw.githubusercontent.com/${githubRepo}/${git.gitHeadCommit.value.get}"
    s"-P:scalajs:mapSourceURI:$local->$remote/${subProjectDir}/"
  },
)

lazy val cli = project
  .in(file("cli"))
  .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin, ScalablyTypedConverterPlugin)
  .settings(commonSettings, jsSettings)
  .settings(
    name                            := "fun-stack-local",
    scalaJSUseMainModuleInitializer := true,
    webpackConfigFile               := Some(baseDirectory.value / "webpack.config.js"),
    libraryDependencies            ++=
      Deps.cats.core.value ::
        Deps.awsLambdaJS.value ::
        Nil,
    stIgnore ++= List(
      "oidc-provider",
      "express",
    ),
    Compile / npmDependencies      ++= Seq(
      /* "@types/oidc-provider" -> "^7.8.2", // TODO: crashes scalablytyped */

      "express" -> "^4.17.3",
      "oidc-provider" -> "^7.10.6",
      "@types/node" -> "14.14.31",
      "ws"          -> "8.2.3",
      "@types/ws"   -> "8.2.0",
      "jwt-decode"  -> "3.1.2",
    ),
    Compile / npmDevDependencies ++= Seq(
      "@babel/core" -> "7.17.5",
      "@babel/preset-env" -> "^7.16.11",
      "babel-loader" -> "^8.2.3",
    )
  )
