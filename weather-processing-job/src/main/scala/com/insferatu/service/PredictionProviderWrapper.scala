package com.insferatu.service

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.insferatu.config.PredictionProviderConfig
import com.insferatu.model.EnrichedWeatherMeasurement
import com.insferatu.model.WeatherMeasurement
import org.http4s.blaze.client.BlazeClientBuilder

object PredictionProviderWrapper extends Serializable {
  def processRows(predictionProviderConfig: PredictionProviderConfig, shareCount: Int)(
    rows: Iterator[WeatherMeasurement]
  ): Iterator[EnrichedWeatherMeasurement] = {
    BlazeClientBuilder[IO]
      .withMaxTotalConnections(predictionProviderConfig.maxTotalConnections)
      .resource
      .use { httpClient =>
        for {
          predictionProvider <- PredictionProvider.instance[IO](predictionProviderConfig)
          enrichedMeasurements <- predictionProvider.processRows(httpClient, shareCount, rows.toList)
        } yield enrichedMeasurements.iterator
      }.unsafeRunSync()
  }
}
