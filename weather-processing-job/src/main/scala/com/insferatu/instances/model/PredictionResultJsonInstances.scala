package com.insferatu.instances.model

import com.insferatu.model.PredictionResult
import io.circe.Decoder

object PredictionResultJsonInstances {
  implicit val predictionResultDecoder: Decoder[PredictionResult] =
    Decoder.instance(_.get[Double]("probabilityOfRain")).map(PredictionResult.apply)
}
