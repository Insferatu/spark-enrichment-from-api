package com.insferatu

import com.insferatu.config.Config
import com.insferatu.model.WeatherMeasurement
import com.insferatu.service.PredictionProviderWrapper
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.SparkSession

object Main {

  implicit class DatasetExt[U](ds: Dataset[U]) {
    def repartitionWithThreshold(minNumberOfPartitions: Int): Dataset[U] = {
      if (ds.rdd.getNumPartitions < minNumberOfPartitions) ds.repartition(minNumberOfPartitions)
      else ds
    }
  }

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .config("spark.app.name", "WeatherForecaster")
      .getOrCreate

    import spark.implicits._

    val schema = StructType(
      Array(
        StructField("formattedDate", TimestampType, nullable = true),
        StructField("summary", StringType, nullable = true),
        StructField("precipType", StringType, nullable = true),
        StructField("temperature", DoubleType, nullable = true),
        StructField("apparentTemperature", DoubleType, nullable = true),
        StructField("humidity", DoubleType, nullable = true),
        StructField("windSpeed", DoubleType, nullable = true),
        StructField("windBearing", DoubleType, nullable = true),
        StructField("visibility", DoubleType, nullable = true),
        StructField("loudCover", DoubleType, nullable = true),
        StructField("pressure", DoubleType, nullable = true),
        StructField("dailySummary", StringType, nullable = true)
      )
    )

    val config = Config.loadConfig

    val executorNum = spark.conf.get("spark.executor.instances").toInt
    val executorCores = spark.conf.get("spark.executor.cores").toInt
    val taskCores = spark.conf.get("spark.task.cpus").toInt

    val minNumberOfPartitions = (executorCores / taskCores) * executorNum
    val shareCount = minNumberOfPartitions

    spark.read
      .option("header", true)
      .schema(schema)
      .csv("file:///opt/data/WeatherHistory.csv")
      .limit(config.sampleCount)
      .as[WeatherMeasurement]
      .repartitionWithThreshold(minNumberOfPartitions)
      .mapPartitions(PredictionProviderWrapper.processRows(config.predictionProvider, shareCount)(_))
      .agg(
        count("probabilityOfRainOpt").as("predictionCount"),
        avg("probabilityOfRainOpt").as("averageProbabilityOfRain")
      )
      .show(1, false)
  }
}
