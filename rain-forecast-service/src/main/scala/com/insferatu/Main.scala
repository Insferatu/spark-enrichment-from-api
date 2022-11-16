package com.insferatu

import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    Server.start[IO].as(ExitCode.Success)
  }
}
