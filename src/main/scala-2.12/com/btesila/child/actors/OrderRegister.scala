package com.btesila.child.actors

import akka.actor.{Actor, ActorLogging, Props}

class OrderRegister extends Actor with ActorLogging {
  import OrderRegister._
  var revenue = 0

  override def receive: Receive = {
    case Order(article) =>
      val price = prices(article)
      revenue += price
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
