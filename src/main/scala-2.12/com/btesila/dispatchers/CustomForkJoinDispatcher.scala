package com.btesila.dispatchers

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.pattern._
import akka.util.Timeout
import com.btesila.dispatchers.Barista.{BaristaRequest, CoffeeRequest}
import com.btesila.dispatchers.OrderRegister.Receipt
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration.{Duration, _}
import scala.concurrent.{Await, Future}

/**
  * @author Adobe Systems Inc
  */
object CustomForkJoinDispatcher extends App {
  class CoffeeHouseEngine(as: ActorSystem) {
    implicit val executionContext = as.dispatchers.lookup("custom-fork-join")

    def run(requests: List[BaristaRequest]): List[Receipt] = {
      implicit val timeout = Timeout(30, TimeUnit.SECONDS)
      val taskResults = requests map { task =>
        val worker = as.actorOf(Barista.props().withDispatcher("custom-fork-join"))
        (worker ? task).mapTo[Receipt]
      }
      Await.result(Future.sequence(taskResults), Duration.Inf)
    }

    def shutdown(): Unit = {
      scala.sys.addShutdownHook {
        as.terminate()
        Await.result(as.whenTerminated, 10 seconds)
      }
    }
  }

  object CoffeeHouseEngine {
    def apply(as: ActorSystem): CoffeeHouseEngine = new CoffeeHouseEngine(as)
  }

  val config = ConfigFactory.load("custom-dispatcher.conf")
  val system = ActorSystem("hakky-system", config)
  val engine = CoffeeHouseEngine(system)

  val requests: List[BaristaRequest] = List.fill(100)(CoffeeRequest)
  val receipts = engine.run(requests)
  receipts.foreach(println(_))
  engine.shutdown()
}
