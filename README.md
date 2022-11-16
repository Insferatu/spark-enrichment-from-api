# Project Title

Example of effective Spark job interaction with external API

### Prerequisites

- docker
- sbt >= 1.6.0
- Make sure that docker has enough of resources to run everything. On my MacPro M1 I provided 10GB of RAM and 8 cores.

## Running the examples

In order to run any example you must choose two scripts from /scripts directory. Here are the names of 
scripts depending on the example you want to run:
1. Sequential processing - setup-infrastructure-1.sh and run-weather-processing-job-1.sh
2. Full parallel processing - setup-infrastructure-1.sh and run-weather-processing-job-2.sh
3. Restricted parallel processing - setup-infrastructure-1.sh and run-weather-processing-job-3.sh
4. Restricted parallel processing with two executors involved - setup-infrastructure-1.sh and run-weather-processing-job-4.sh
5. Rate limited processing - setup-infrastructure-2.sh and run-weather-processing-job-5.sh

After finding proper scripts you need to run setup-infrastructure-X.sh in one shell tab and wait for these lines
```
infrastructure-spark-worker-2-1         | 22/11/16 12:12:00 INFO Worker: Successfully registered with master spark://6a163606cc21:7077
infrastructure-spark-worker-1-1         | 22/11/16 12:12:00 INFO Worker: Successfully registered with master spark://6a163606cc21:7077
```
It means that Spark cluster is started and all its workers are ready. Now you can trigger run-weather-processing-job-X.sh
in other shell tab and see the job output. While job is working you can check additional info from these sources:
1. localhost:9090 - Prometheus UI
2. localhost:8080 - Spark master UI
3. localhost:4040 - WPJ Spark driver UI
4. localhost:8181, localhost:8281 - Spark workers UI, might be useful if you want to check executor logs

After run-weather-processing-job-X.sh script is finished you can also close setup-infrastructure-X.sh by hitting Ctrl+C.