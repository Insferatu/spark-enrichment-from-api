/*
 * Copyright (C) Flo Health, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Contributors: Henadz Varantsou, Ivan Sharamet, Dmitry Aleshkevich
 */
package com.insferatu.config

import cats.effect.Resource
import cats.effect.Sync
import pureconfig.module.catseffect.syntax._
import pureconfig.generic.auto._

final case class ServerConfig(host: String, mainPort: Int, metricsPort: Int)

final case class PredictionConfig(minimumDelay: Int, maximumDelay: Int)

final case class Config(server: ServerConfig, rateLimitRequestsPerSecond: Option[Int], prediction: PredictionConfig)

object Config {
  import pureconfig._

  def load[F[_]: Sync]: F[Config] = ConfigSource.default.loadF[F, Config]
}
