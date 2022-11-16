package com.insferatu.service

import cats.effect.Sync
import cats.effect.Temporal
import cats.effect.kernel.Async
import com.insferatu.model.PredictionInput
import com.insferatu.model.PredictionResult
import cats.syntax.flatMap._
import cats.syntax.functor._
import com.insferatu.config.PredictionConfig
import scala.util.Random
import scala.concurrent.duration._

trait Predictor[F[_]] {
  def predict(predictionInput: PredictionInput): F[PredictionResult]
}

case class RandomPredictor[F[_]: Async](minimumDelay: Int, maximumDelay: Int, random: Random) extends Predictor[F] {
  override def predict(predictionInput: PredictionInput): F[PredictionResult] = {
    for {
      randomDuration <- Sync[F].delay(random.between(minimumDelay, maximumDelay))
      _ <- Temporal[F].sleep(randomDuration.milliseconds)
      randomPrediction <- Sync[F].delay(random.between(0d, 1d))
    } yield PredictionResult(randomPrediction)
  }
}

object Predictor {
  private val randomSeed = 3845147L

  def instance[F[_]: Async](prediction: PredictionConfig): F[Predictor[F]] =
    for {
      random <- Sync[F].delay(new Random(randomSeed))
    } yield RandomPredictor(prediction.minimumDelay, prediction.maximumDelay, random)
}
