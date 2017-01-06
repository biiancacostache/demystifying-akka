package com.btesila.routers

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}
import com.btesila.routers.Barista.{BaristaRequest, CoffeeRequest}

object StandaloneRouter extends App {

  class CoffeeHouseEngine extends Actor with ActorLogging {

    val router = {
      val routees = Vector.fill(5) {
        val r = context.actorOf(Barista.props())
        ActorRefRoutee(r)
      }
      Router(RoundRobinRoutingLogic(), routees)
    }

    def receive = {
      case req: BaristaRequest => router.route(req, sender())
    }
  }

  object CoffeeHouseEngine {
    def props(): Props = Props(classOf[CoffeeHouseEngine])
  }

  val system = ActorSystem("hakky-system")
  val engine = system.actorOf(CoffeeHouseEngine.props(), "coffee-house-engine")

  val requests: List[BaristaRequest] = List.fill(50)(CoffeeRequest)
  requests.foreach(engine.tell(_, ActorRef.noSender))
}
