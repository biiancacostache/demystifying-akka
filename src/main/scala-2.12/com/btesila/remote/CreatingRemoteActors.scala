package com.btesila.remote

import akka.actor.{Actor, ActorIdentity, ActorLogging, ActorRef, ActorSelection, ActorSystem, Address, Identify, Props, Stash}
import com.typesafe.config.ConfigFactory

object CreatingRemoteActors extends App {

  class Barista(orderRegisterAddress: String) extends Actor with ActorLogging with Stash {

    import Barista._
    import OrderRegister._

    val Id = 1
    val orderRegister: ActorSelection = context.system.actorSelection(orderRegisterAddress)
    orderRegister ! Identify(Id)


    override def receive: Receive = {
      case ActorIdentity(Id, Some(actorRef)) =>
        log.info("Found an order register!")
        unstashAll()
        context.become(receivingRequests(actorRef))
      case a: ActorIdentity =>
        log.info("No order register available...{}", a)
      case msg =>
        log.info("Stashing message {}", msg)
        stash()
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
    def props(orderRegisterAddress: String): Props = Props(classOf[Barista], orderRegisterAddress)

    case object CoffeeRequest

    case object JuiceRequest

  }

  class OrderRegister extends Actor with ActorLogging {
    log.info("STARTED ORDER REGISTER")

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

  val config = ConfigFactory.load("remote.conf")
  val system = ActorSystem("hakky-bar", config)

  val config1 = ConfigFactory.load("remote-system.conf")
  val system1 = ActorSystem("hakky-bar2", config1)

  val orderRegister = system.actorOf(OrderRegister.props(), "hakky-order-register")
  val barista = system.actorOf(Barista.props(orderRegister.path.toString), "hakky-barista")

  barista ! Barista.CoffeeRequest
  barista ! Barista.JuiceRequest
  barista ! Barista.CoffeeRequest
  barista ! Barista.CoffeeRequest
  barista ! Barista.CoffeeRequest
}
