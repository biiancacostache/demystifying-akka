package com.btesila.routers

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.routing.FromConfig
import com.btesila.routers.Barista.{BaristaRequest, CoffeeRequest}
import com.typesafe.config.ConfigFactory

/**
  * @author Adobe Systems Inc
  */
object PoolRouterActor extends App {

  class CoffeeHouseEngine extends Actor with ActorLogging {

    val router = context.actorOf(FromConfig.props(Barista.props()), "coffee-house-pool")

    def receive = {
      case req: BaristaRequest => router ! req
    }
  }

  object CoffeeHouseEngine {
    def props(): Props = Props(classOf[CoffeeHouseEngine])
  }

  val config = ConfigFactory.load("router.conf")
  val system = ActorSystem("hakky-system", config)
  val engine = system.actorOf(CoffeeHouseEngine.props(), "coffee-house-engine")

  val requests: List[BaristaRequest] = List.fill(50)(CoffeeRequest)
  requests.foreach(engine.tell(_, ActorRef.noSender))
}
