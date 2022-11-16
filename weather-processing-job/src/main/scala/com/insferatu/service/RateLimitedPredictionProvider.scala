package com.insferatu.service

import cats.effect.Async
import cats.effect.Temporal
import com.insferatu.model.EnrichedWeatherMeasurement
import com.insferatu.model.WeatherMeasurement
import org.apache.logging.log4j.LogManager
import org.http4s.Uri
import org.http4s.client.Client
import scala.concurrent.duration._

case class RateLimitedPredictionProvider[F[_]: Async: Temporal](uri: Uri, totalParallelism: Int, millisPerRequest: Int)
    extends PredictionProvider[F] {
  @transient private val logger = LogManager.getLogger(this.getClass)

  override def processRows(
    httpClient: Client[F],
    shareCount: Int,
    measurements: List[WeatherMeasurement]
  ): F[List[EnrichedWeatherMeasurement]] =
    fs2.Stream
      .emits[F, WeatherMeasurement](measurements)
      .meteredStartImmediately((millisPerRequest * shareCount).milliseconds)
      .parEvalMapUnordered(totalParallelism / shareCount)(enrichMeasurement(httpClient, uri, logger))
      .compile
      .toList
}
