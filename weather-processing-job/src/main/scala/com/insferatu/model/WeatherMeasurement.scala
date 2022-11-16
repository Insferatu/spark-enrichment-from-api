package com.insferatu.model

import java.sql.Timestamp

case class WeatherMeasurement(
  formattedDate: Timestamp,
  summary: String,
  precipType: String,
  temperature: Double,
  apparentTemperature: Double,
  humidity: Double,
  windSpeed: Double,
  windBearing: Double,
  visibility: Double,
  loudCover: Double,
  pressure: Double,
  dailySummary: String
) {
  def getPredictionInput: PredictionInput = PredictionInput(temperature, humidity, pressure)
}
