package com.btesila.communication

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object PublishSubscribe extends App {

  class Barista extends Actor with ActorLogging {

    import Barista._
    import OrderRegister._

    context.system.eventStream.subscribe(self, classOf[Receipt])

    override def receive: Receive = {
      case CoffeeRequest =>
        log.debug("Preparing a coffee right away")
        context.system.eventStream.publish(Order(Coffee))
      case JuiceRequest =>
        log.debug("Preparing a juice right away")
        context.system.eventStream.publish(Order(Juice))
      case Receipt(price) =>
        log.debug("Gotta handle the receipt worth {} to the customer", price)
    }
  }

  object Barista {
    def props(): Props = Props(classOf[Barista])

    case object CoffeeRequest

    case object JuiceRequest

    case object Ok

  }

  class OrderRegister extends Actor with ActorLogging {

    import OrderRegister._

    context.system.eventStream.subscribe(self, classOf[Order])

    var revenue = 0

    override def receive: Receive = {
      case Order(article) =>
        val price = prices(article)
        revenue += price
        log.info("Increasing my revenue by {}", price)
        context.system.eventStream.publish(Receipt(price))
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

  val config = ConfigFactory.load("withEventStream.conf")
  val system = ActorSystem("hakky-bar", config)
  val barista = system.actorOf(Barista.props(), "hakky-barista")
  val orderRegister = system.actorOf(OrderRegister.props(), "hakky-order-register")

  barista ! Barista.CoffeeRequest
}
