package com.insferatu.service

import cats.Parallel
import cats.effect.Async
import com.insferatu.model.EnrichedWeatherMeasurement
import com.insferatu.model.WeatherMeasurement
import org.apache.logging.log4j.LogManager
import org.http4s.Uri
import org.http4s.client.Client
import cats.effect.syntax.concurrent._
import cats.instances.list._

case class RestrictedParallelPredictionProvider[F[_]: Async: Parallel](uri: Uri, totalParallelism: Int)
    extends PredictionProvider[F] {
  @transient private val logger = LogManager.getLogger(this.getClass)

  override def processRows(
    httpClient: Client[F],
    shareCount: Int,
    measurements: List[WeatherMeasurement]
  ): F[List[EnrichedWeatherMeasurement]] =
    measurements.parTraverseN(totalParallelism / shareCount)(enrichMeasurement(httpClient, uri, logger))
}
