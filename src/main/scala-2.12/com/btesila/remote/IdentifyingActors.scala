package com.btesila.remote

import akka.actor.{Actor, ActorIdentity, ActorLogging, ActorRef, ActorSelection, ActorSystem, Identify, Kill, Props, Stash}

object IdentifyingActors extends App {

  class Barista extends Actor with ActorLogging with Stash {

    import Barista._
    import OrderRegister._

    val Id = 1
    val orderRegister: ActorSelection = context.actorSelection("../hakky-order-register")
    orderRegister ! Identify(Id)

    override def receive: Receive = {
      case ActorIdentity(Id, Some(actorRef)) =>
        log.info("Found an order register!")
        unstashAll()
        context.become(receivingRequests(actorRef))
      case _: ActorIdentity =>
        log.info("No order register available...")
      case _ => stash()
    }

    def receivingRequests(orderRegister: ActorRef): Receive = {
      case CoffeeRequest =>
        log.debug("Preparing a coffee right away")
        orderRegister ! Order(Coffee)
      case JuiceRequest =>
        log.debug("Preparing a juice right away")
        orderRegister ! Order(Juice)
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
      case Order(article: Juice.type) =>
        throw new RuntimeException("Surprise!!!")
      case Order(_) if revenue >= 10 =>
        self ! Kill
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
  val orderRegister = system.actorOf(OrderRegister.props(), "hakky-order-register")

  barista ! Barista.CoffeeRequest
  barista ! Barista.JuiceRequest
  barista ! Barista.CoffeeRequest
  barista ! Barista.CoffeeRequest
  barista ! Barista.CoffeeRequest
}
