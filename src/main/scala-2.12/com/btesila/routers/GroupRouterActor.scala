package com.btesila.routers

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.routing.RoundRobinGroup
import com.btesila.routers.Barista.{BaristaRequest, CoffeeRequest}
import com.typesafe.config.ConfigFactory

/**
  * @author Adobe Systems Inc
  */
object GroupRouterActor extends App {
  class CoffeeHouseEngine(paths: List[String]) extends Actor with ActorLogging {

      val router = context.actorOf(RoundRobinGroup(paths).props(), "coffee-house-group")

      def receive = {
        case req: BaristaRequest => router ! req
      }
    }

    object CoffeeHouseEngine {
      def props(paths: List[String]): Props = Props(classOf[CoffeeHouseEngine], paths)
    }

    val config = ConfigFactory.load("router.conf")
    val system = ActorSystem("hakky-system", config)
    val baristas = List.fill(5) { system.actorOf(Barista.props())}.map(_.path.name)

  val engine = system.actorOf(CoffeeHouseEngine.props(baristas), "coffee-house-engine")

    val requests: List[BaristaRequest] = List.fill(50)(CoffeeRequest)
    requests.foreach(engine.tell(_, ActorRef.noSender))
}
