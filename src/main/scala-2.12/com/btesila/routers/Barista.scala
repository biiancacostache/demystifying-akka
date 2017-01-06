package com.btesila.routers

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.{ask, pipe}
import akka.util.Timeout

import scala.concurrent.duration._

class Barista extends Actor with ActorLogging {

  import Barista._
  import OrderRegister._
  import context.dispatcher

  implicit val timeout = Timeout(10 seconds)


  val child = context.actorOf(OrderRegister.props(), "hakky-order-register")

  override def receive: Receive = {
    case CoffeeRequest =>
      val coffeeHouse = sender()
      log.info("Placing order...")
      (child ? Order(Coffee)).mapTo[Receipt].pipeTo(coffeeHouse)
    case JuiceRequest =>
      val coffeeHouse = sender()
      log.info("Placing order...")
      (child ? Order(Juice)).mapTo[Receipt].pipeTo(coffeeHouse)
  }
}

object Barista {
  def props(): Props = Props(classOf[Barista])

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