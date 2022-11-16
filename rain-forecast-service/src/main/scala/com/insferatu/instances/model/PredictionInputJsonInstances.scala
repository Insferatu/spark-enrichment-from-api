package com.insferatu.instances.model

import com.insferatu.model.PredictionInput
import io.circe.Decoder

object PredictionInputJsonInstances {
  implicit val predictionInputDecoder: Decoder[PredictionInput] = {
    import cats.syntax.contravariantSemigroupal._

    (
      Decoder.instance(_.get[Double]("temperature")),
      Decoder.instance(_.get[Double]("humidity")),
      Decoder.instance(_.get[Double]("pressure"))
    ).mapN(PredictionInput.apply)
  }
}
