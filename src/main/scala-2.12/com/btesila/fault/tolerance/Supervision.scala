package com.btesila.fault.tolerance

import akka.actor.SupervisorStrategy.{Decider, Restart, Resume}
import akka.actor.{Actor, ActorKilledException, ActorLogging, ActorSystem, Kill, OneForOneStrategy, Props, SupervisorStrategy, Terminated}

/**
  * @author Adobe Systems Inc
  */
object Supervision extends App {

  class Barista extends Actor with ActorLogging {

    import Barista._
    import OrderRegister._

    val defaultDecider: Decider = {
      case _: ActorKilledException ⇒ Restart
      case _: Exception            ⇒ Restart
    }
    override val supervisorStrategy: SupervisorStrategy = {
      OneForOneStrategy()(defaultDecider)
    }

    val child = context.actorOf(OrderRegister.props(), "hakky-order-register")

    override def receive: Receive = {
      case CoffeeRequest =>
        log.debug("Preparing a coffee right away")
        child ! Order(Coffee)
        child ! Kill
      case JuiceRequest =>
        log.debug("Preparing a juice right away")
        child ! Order(Juice)
    }
  }

  object Barista {
    def props(): Props = Props(classOf[Barista])

    case object CoffeeRequest

    case object JuiceRequest

  }

  class OrderRegister extends Actor with ActorLogging {

    import OrderRegister._

    var revenue = 0

    override def receive: Receive = {
      case Order(Juice) =>
        throw new RuntimeException("Surpriseee")
      case Order(article) =>
        val price = prices(article)
        revenue += price
        log.info("Increasing my revenue by {}", price)
    }
  }

  object OrderRegister {
    def props(): Props = Props(classOf[OrderRegister])

    sealed trait Article

    case object Coffee extends Article

    case object Juice extends Article

    case class Receipt(price: Int)

    case class Order(article: Article)

    val prices: Map[Article, Int] = Map(Coffee -> 10, Juice -> 5)
  }


  val system = ActorSystem("hakky-bar")
  val barista = system.actorOf(Barista.props(), "hakky-barista")

  barista ! Barista.CoffeeRequest
  barista ! Barista.JuiceRequest
  barista ! Barista.CoffeeRequest
}
