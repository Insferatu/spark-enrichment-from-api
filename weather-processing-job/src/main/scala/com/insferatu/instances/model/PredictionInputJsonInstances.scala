package com.insferatu.instances.model

import com.insferatu.model.PredictionInput
import io.circe.Encoder
import io.circe.Json
import io.circe.syntax._

object PredictionInputJsonInstances {
  implicit val predictionInputEncoder: Encoder[PredictionInput] = Encoder.instance { predictionInput =>
    Json.obj(
      "temperature" -> predictionInput.temperature.asJson,
      "humidity"    -> predictionInput.humidity.asJson,
      "pressure"    -> predictionInput.pressure.asJson
    )
  }
}
