sbt clean assembly
docker compose --project-directory infrastructure up --remove-orphans --build