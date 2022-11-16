package com.insferatu.service

import cats.Parallel
import cats.effect.Async
import cats.effect.Sync
import cats.syntax.applicative._
import cats.syntax.applicativeError._
import cats.syntax.functor._
import com.insferatu.config._
import com.insferatu.model.EnrichedWeatherMeasurement
import com.insferatu.model.PredictionResult
import com.insferatu.model.WeatherMeasurement
import org.apache.logging.log4j.Logger
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.client.Client
import org.http4s.Method
import org.http4s.Request
import org.http4s.Uri

trait PredictionProvider[F[_]] extends Serializable {
  def processRows(
    httpClient: Client[F],
    shareCount: Int,
    measurements: List[WeatherMeasurement]
  ): F[List[EnrichedWeatherMeasurement]]

  protected def enrichMeasurement[F[_]: Async](httpClient: Client[F], uri: Uri, logger: Logger)(
    measurement: WeatherMeasurement
  ): F[EnrichedWeatherMeasurement] = {
    import com.insferatu.instances.model.PredictionInputJsonInstances._
    import com.insferatu.instances.model.PredictionResultJsonInstances._

    val req =
      Request[F](method = Method.GET, uri = uri / "prediction" / "predict")
        .withEntity(measurement.getPredictionInput)
    for {
      predictionResultOpt <- httpClient
        .expectOptionOr[PredictionResult](req) { response =>
          new RuntimeException(response.toString).pure[F].widen[Throwable]
        }
        .handleErrorWith { exc =>
          Sync[F].delay(logger.error(exc)).as(None)
        }
    } yield EnrichedWeatherMeasurement(measurement, predictionResultOpt.map(_.probabilityOfRain))
  }
}

object PredictionProvider {
  def instance[F[_]: Async: Parallel](predictionProviderConfig: PredictionProviderConfig): F[PredictionProvider[F]] = {
    val uri = Uri.unsafeFromString(s"http://${predictionProviderConfig.rainForecastServiceUri}")
    predictionProviderConfig.processingMethod match {
      case SequentialProcessing => SequentialPredictionProvider(uri).pure[F].widen[PredictionProvider[F]]
      case FullParallelProcessing => FullParallelPredictionProvider(uri).pure[F].widen[PredictionProvider[F]]
      case RestrictedParallelProcessing(totalParallelism) =>
        RestrictedParallelPredictionProvider(uri, totalParallelism).pure[F].widen[PredictionProvider[F]]
      case RateLimitedProcessing(totalParallelism, millisPerRequest) =>
        RateLimitedPredictionProvider(uri, totalParallelism, millisPerRequest).pure[F].widen[PredictionProvider[F]]
    }
  }
}
