package com.insferatu

import cats.effect.Concurrent
import cats.syntax.applicative._
import cats.syntax.flatMap._
import cats.syntax.functor._
import com.insferatu.model.PredictionInput
import com.insferatu.monitoring.MetricsRepository
import com.insferatu.service.Predictor
import io.circe.syntax._
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

class Routes[F[_]: Concurrent](metrics: MetricsRepository[F]) {
  val dsl: Http4sDsl[F] = new Http4sDsl[F] {}

  import dsl._

  def metricsRoutes: HttpRoutes[F] = {
    HttpRoutes
      .of[F] { case _ =>
        for {
          metrics <- metrics.getMetrics
          response <- Ok(metrics)
        } yield response
      }
  }

  def predictionRoutes(predictor: Predictor[F]): HttpRoutes[F] = {
    import org.http4s.circe.CirceEntityDecoder._
    import org.http4s.circe.CirceEntityEncoder._
    import com.insferatu.instances.model.PredictionInputJsonInstances._
    import com.insferatu.instances.model.PredictionResultJsonInstances._

    HttpRoutes
      .of[F] { case rq @ GET -> Root / "predict" =>
        for {
          _ <- metrics.receivedRequests.inc
          predictionInput <- rq.as[PredictionInput]
          predictionResult <- predictor.predict(predictionInput)
          response <- Ok(predictionResult.asJson)
          _ <- metrics.successfullyProcessedRequests.inc
        } yield response
      }

  }
}

object Routes {
  def instance[F[_]: Concurrent](metrics: MetricsRepository[F]): F[Routes[F]] = new Routes[F](metrics).pure[F]
}
