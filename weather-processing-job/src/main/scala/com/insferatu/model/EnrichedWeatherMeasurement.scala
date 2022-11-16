package com.insferatu.model

import java.sql.Timestamp

case class EnrichedWeatherMeasurement(
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
  dailySummary: String,
  probabilityOfRainOpt: Option[Double]
)

object EnrichedWeatherMeasurement {
  def apply(weatherMeasurement: WeatherMeasurement, probabilityOfRainOpt: Option[Double]): EnrichedWeatherMeasurement =
    EnrichedWeatherMeasurement(
      weatherMeasurement.formattedDate,
      weatherMeasurement.summary,
      weatherMeasurement.precipType,
      weatherMeasurement.temperature,
      weatherMeasurement.apparentTemperature,
      weatherMeasurement.humidity,
      weatherMeasurement.windSpeed,
      weatherMeasurement.windBearing,
      weatherMeasurement.visibility,
      weatherMeasurement.loudCover,
      weatherMeasurement.pressure,
      weatherMeasurement.dailySummary,
      probabilityOfRainOpt
    )
}
