package com.insferatu

import cats.Applicative
import cats.effect.Async
import cats.effect.kernel.Sync
import cats.effect.kernel.Temporal
import cats.syntax.flatMap._
import cats.syntax.functor._
import com.insferatu.config.Config
import com.insferatu.monitoring.MetricsRepository
import com.insferatu.service.Predictor
import io.chrisdavenport.epimetheus.CollectorRegistry
import org.http4s.HttpApp
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.middleware.Throttle
import scala.concurrent.duration._

object Server {
  def start[F[_]: Async]: F[Unit] = {
    for {
      config <- Config.load
      registry <- CollectorRegistry.build
      metrics <- MetricsRepository.instance(registry)
      routes <- Routes.instance(metrics)
      predictor <- Predictor.instance(config.prediction)
      mainHttpApp <- wrapMainHttpApp(config.rateLimitRequestsPerSecond) {
        Router("/prediction" -> routes.predictionRoutes(predictor)).orNotFound
      }
      metricsHttpApp = Router("/metrics" -> routes.metricsRoutes).orNotFound
      mainStream = BlazeServerBuilder[F]
        .bindHttp(config.server.mainPort, config.server.host)
        .withHttpApp(mainHttpApp)
        .serve
      metricsStream = BlazeServerBuilder[F]
        .bindHttp(config.server.metricsPort, config.server.host)
        .withHttpApp(metricsHttpApp)
        .serve
      _ <- mainStream.merge(metricsStream).compile.drain
    } yield ()
  }

  def wrapMainHttpApp[F[_]: Applicative: Temporal](
    rateLimitRequestsPerSecondOpt: Option[Int]
  )(httpApp: HttpApp[F]): F[HttpApp[F]] = {
    import cats.syntax.applicative._

    rateLimitRequestsPerSecondOpt.map { rateLimitRequestsPerSecond =>
      Throttle(rateLimitRequestsPerSecond, 1.second)(httpApp)
    }
      .getOrElse(httpApp.pure)
  }
}
