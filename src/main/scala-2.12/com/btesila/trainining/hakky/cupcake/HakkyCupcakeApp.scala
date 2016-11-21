package com.btesila.trainining.hakky.cupcake

import akka.actor.ActorSystem

import scala.annotation.tailrec
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.io.StdIn

class HakkyCupcakeApp(system: ActorSystem) extends Console {
  private val logger = system.log

  def run(): Unit = {
    logger.info("Opened cupcake shop...")
    logger.info("Enter commands into the console...")
    listenForCommands()
    Await.result(system.whenTerminated, Duration.Inf)
  }

  @tailrec
  private def listenForCommands(): Unit =
    Command(StdIn.readLine())  match {
      case Command.Customer(cupcake, count) =>
        logger.info(s"Got a new order: $count $cupcake's")
        listenForCommands()
      case Command.Status =>
        logger.info(s"A customer wants to know the status of its order")
        listenForCommands()
      case Command.Quit =>
        logger.info("Closing the cupcake shop...")
        system.terminate()
      case Command.Unknown(command) =>
        logger.warning("Unknown command {}", command)
        listenForCommands()
    }
}

object HakkyCupcakeApp extends App {
  val as = ActorSystem("hakky-cupcake")
  val app = HakkyCupcakeApp(as)
  app.run()

  def apply(system: ActorSystem): HakkyCupcakeApp = new HakkyCupcakeApp(system)
}
