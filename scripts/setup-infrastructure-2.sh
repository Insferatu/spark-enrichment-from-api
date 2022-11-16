sbt clean assembly
docker compose --project-directory infrastructure --env-file infrastructure/rate-limit.env up --remove-orphans --build