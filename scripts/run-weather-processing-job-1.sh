docker compose --project-directory infrastructure exec spark-submit bash \
  spark-submit  --master spark://spark:7077 --deploy-mode client --executor-memory 512m \
  --driver-memory 512m --conf "spark.cores.max=2" --conf "spark.executor.instances=2" \
  --conf "spark.executor.cores=1" --conf "spark.task.cpus=1" \
  --driver-java-options "-Dprediction-provider.processing-method.type=sequential-processing" \
  --class com.insferatu.Main file:///opt/lib/weather-processing-job-0.1.0.jar