package com.insferatu.service

import cats.Parallel
import cats.effect.Async
import com.insferatu.model.EnrichedWeatherMeasurement
import com.insferatu.model.WeatherMeasurement
import org.apache.logging.log4j.LogManager
import org.http4s.Uri
import org.http4s.client.Client
import cats.syntax.parallel._

case class FullParallelPredictionProvider[F[_]: Async: Parallel](uri: Uri) extends PredictionProvider[F] {
  @transient private val logger = LogManager.getLogger(this.getClass)

  override def processRows(
    httpClient: Client[F],
    shareCount: Int,
    measurements: List[WeatherMeasurement]
  ): F[List[EnrichedWeatherMeasurement]] =
    measurements.parTraverse(enrichMeasurement(httpClient, uri, logger))
}
