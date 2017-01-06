package com.btesila.cluster

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

class Barista extends Actor with ActorLogging {

  import Barista._
  import OrderRegister._
  import context.dispatcher

  implicit val timeout = Timeout(10 seconds)

  val child: ActorRef = context.actorOf(OrderRegister.props(), "hakky-order-register")

  log.info("Started Baristaaa")

  override def receive: Receive = {
    case CoffeeRequest =>
      val coffeeHouse = sender()
      log.info("Placing order...")
      (child ? Order(Coffee)).mapTo[Receipt].pipeTo(coffeeHouse)
    case JuiceRequest =>
      val coffeeHouse = sender()
      log.info("Placing order...")
      (child ? Order(Juice)).mapTo[Receipt].pipeTo(coffeeHouse)
    case msg =>
      log.info("Receiving unhandled msg {}", msg)
  }
}


object Barista {
  def props(): Props = Props(classOf[Barista])

  def start(port: Int): Unit = {
    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")
      .withFallback(ConfigFactory.parseString("akka.cluster.roles = [barista]"))
      .withFallback(ConfigFactory.load("cluster.conf"))

    val system = ActorSystem("hakky-cluster", config)

    system.actorOf(props(), "hakky-barista")
  }

  sealed trait BaristaRequest

  case object CoffeeRequest extends BaristaRequest

  case object JuiceRequest extends BaristaRequest

  case object Ok

}

class OrderRegister extends Actor with ActorLogging {

  import OrderRegister._

  var revenue = 0

  override def receive: Receive = {
    case Order(article) =>
      val price = prices(article)
      revenue += price
      sender() ! Receipt(price)
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