package com.insferatu.monitoring

import cats.effect.Sync
import cats.syntax.flatMap._
import cats.syntax.functor._
import io.chrisdavenport.epimetheus.CollectorRegistry
import io.chrisdavenport.epimetheus.Counter
import io.chrisdavenport.epimetheus.Name

trait MetricsRepository[F[_]] {
  def receivedRequests: Counter[F]
  def successfullyProcessedRequests: Counter[F]
  def getMetrics: F[String]
}

case class PrometheusMetricsRepository[F[_]](receivedRequests: Counter[F], successfullyProcessedRequests: Counter[F])(
  private val registry: CollectorRegistry[F]
) extends MetricsRepository[F] {
  def getMetrics: F[String] = registry.write004
}

object MetricsRepository {
  def instance[F[_]: Sync](registry: CollectorRegistry[F]): F[MetricsRepository[F]] =
    for {
      receivedRequests <- Counter.noLabels(registry, Name("received_requests"), "Hint")
      successfullyProcessedRequests <- Counter.noLabels(registry, Name("successfully_processed_requests"), "Hint")
    } yield PrometheusMetricsRepository(receivedRequests, successfullyProcessedRequests)(registry)
}
