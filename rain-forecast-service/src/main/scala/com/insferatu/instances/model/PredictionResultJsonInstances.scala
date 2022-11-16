package com.insferatu.instances.model

import com.insferatu.model.PredictionResult
import io.circe.Encoder
import io.circe.Json
import io.circe.syntax._

object PredictionResultJsonInstances {
  implicit val predictionResultEncoder: Encoder[PredictionResult] = Encoder.instance { predictionResult =>
    Json.obj("probabilityOfRain" -> predictionResult.probabilityOfRain.asJson)
  }
}
