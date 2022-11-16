package com.insferatu.config

final case class Config(sampleCount: Int, predictionProvider: PredictionProviderConfig)

final case class PredictionProviderConfig(
  rainForecastServiceUri: String,
  maxTotalConnections: Int,
  processingMethod: ProcessingMethod
)

sealed trait ProcessingMethod

case object SequentialProcessing extends ProcessingMethod

case object FullParallelProcessing extends ProcessingMethod

case class RestrictedParallelProcessing(totalParallelism: Int) extends ProcessingMethod

case class RateLimitedProcessing(totalParallelism: Int, millisPerRequest: Int) extends ProcessingMethod

object Config {
  import cats.syntax.either._
  import pureconfig._
  import pureconfig.generic.auto._

  def loadConfig: Config =
    ConfigSource.default.load[Config].leftMap(_.prettyPrint()).leftMap(new RuntimeException(_)).valueOr(throw _)
}
