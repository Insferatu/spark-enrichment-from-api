docker compose --project-directory infrastructure exec spark-submit bash \
  spark-submit  --master spark://spark:7077 --deploy-mode client --executor-memory 512m \
  --driver-memory 512m --conf "spark.cores.max=2" --conf "spark.executor.instances=2" \
  --conf "spark.executor.cores=1" --conf "spark.task.cpus=1" \
  --driver-java-options "-Dsample-count=10000 -Dprediction-provider.processing-method.type=restricted-parallel-processing -Dprediction-provider.processing-method.total-parallelism=100" \
  --class com.insferatu.Main file:///opt/lib/weather-processing-job-0.1.0.jar