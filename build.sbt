val LogbackVersion    = "1.2.11"
val EpimetheusVersion = "0.5.0-M2"
val PureConfigVersion = "0.17.1"
val SparkVersion      = "3.3.0"
val CatsVersion       = "2.7.0"
val CatsEffectVersion = "3.3.12"
val Http4sVersion     = "1.0.0-M30"
val CirceVersion      = "0.14.2"

lazy val `spark-enrichment-from-api` = project
  .in(file("."))
  .settings(clean / skip := true, compile / skip := true, assembly / skip := true)
  .aggregate(`rain-forecast-service`, `weather-processing-job`)

lazy val `rain-forecast-service` = project
  .settings(
    version        := "0.1.0",
    scalaVersion   := "2.13.8",
    scalacOptions ++= Seq("-feature", "UTF-8", "-language:higherKinds", "-language:postfixOps"),
    libraryDependencies ++= Seq(
      "ch.qos.logback"         % "logback-classic"        % LogbackVersion,
      "io.chrisdavenport"     %% "epimetheus"             % EpimetheusVersion,
      "com.github.pureconfig" %% "pureconfig"             % PureConfigVersion,
      "com.github.pureconfig" %% "pureconfig-cats-effect" % PureConfigVersion,
      "org.typelevel"         %% "cats-core"              % CatsVersion,
      "org.typelevel"         %% "cats-effect"            % CatsEffectVersion,
      "org.http4s"            %% "http4s-blaze-server"    % Http4sVersion,
      "org.http4s"            %% "http4s-dsl"             % Http4sVersion,
      "org.http4s"            %% "http4s-circe"           % Http4sVersion,
      "io.circe"              %% "circe-core"             % CirceVersion,
      "io.circe"              %% "circe-generic"          % CirceVersion,
      "io.circe"              %% "circe-parser"           % CirceVersion
    ),
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", _ @_*) => MergeStrategy.discard
      case _ => MergeStrategy.first
    },
    assembly / assemblyOutputPath := file(s"assemblyTarget/${name.value}-${version.value}.jar")
  )

lazy val `weather-processing-job` = project
  .settings(
    version        := "0.1.0",
    scalaVersion   := "2.12.14",
    scalacOptions ++= Seq("-feature", "UTF-8", "-language:higherKinds", "-language:postfixOps"),
    libraryDependencies ++= Seq(
      "org.apache.spark"      %% "spark-core"          % SparkVersion % "provided",
      "org.apache.spark"      %% "spark-sql"           % SparkVersion % "provided",
      "com.github.pureconfig" %% "pureconfig"          % PureConfigVersion,
      "org.typelevel"         %% "cats-core"           % CatsVersion,
      "org.typelevel"         %% "cats-effect"         % CatsEffectVersion,
      "org.http4s"            %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s"            %% "http4s-circe"        % Http4sVersion,
      "io.circe"              %% "circe-core"          % CirceVersion,
      "io.circe"              %% "circe-generic"       % CirceVersion,
      "io.circe"              %% "circe-parser"        % CirceVersion
    ),
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", _ @_*) => MergeStrategy.discard
      case _ => MergeStrategy.first
    },
    assembly / assemblyShadeRules := Seq(
      ShadeRule.rename("shapeless.**" -> s"shadeshapeless.@1").inAll,
      ShadeRule.rename("cats.kernel.**" -> s"shadecats.kernel.@1").inAll,
      ShadeRule.rename("io.circe.**" -> s"shadeio.circe.@1").inAll
    ),
    assembly / assemblyOutputPath := file(s"assemblyTarget/${name.value}-${version.value}.jar")
  )
