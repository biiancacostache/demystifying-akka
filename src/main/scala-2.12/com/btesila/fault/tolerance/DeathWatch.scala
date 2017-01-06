package com.btesila.fault.tolerance

import akka.actor.{Actor, ActorLogging, ActorSystem, Kill, Props, Terminated}

/**
  * @author Adobe Systems Inc
  */
object DeathWatch extends App {

  class Barista extends Actor with ActorLogging {

    import Barista._
    import OrderRegister._

    val child = context.actorOf(OrderRegister.props(), "hakky-order-register")
    context.watch(child)

    override def receive: Receive = {
      case CoffeeRequest =>
        log.debug("Preparing a coffee right away")
        child ! Order(Coffee)
        child ! Kill
      case JuiceRequest =>
        log.debug("Preparing a juice right away")
        child ! Order(Juice)
      case Terminated(child) =>
        log.info("My child is gone...")
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
}
