package com.insferatu.service

import cats.effect.Async
import com.insferatu.model.EnrichedWeatherMeasurement
import com.insferatu.model.WeatherMeasurement
import org.apache.logging.log4j.LogManager
import org.http4s.Uri
import org.http4s.client.Client
import cats.instances.list._
import cats.syntax.traverse._

case class SequentialPredictionProvider[F[_]: Async](uri: Uri) extends PredictionProvider[F] {
  @transient private val logger = LogManager.getLogger(this.getClass)

  override def processRows(
    httpClient: Client[F],
    shareCount: Int,
    measurements: List[WeatherMeasurement]
  ): F[List[EnrichedWeatherMeasurement]] =
    measurements.traverse(enrichMeasurement(httpClient, uri, logger))
}
