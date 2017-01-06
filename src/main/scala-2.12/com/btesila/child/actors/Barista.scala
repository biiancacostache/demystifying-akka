package com.btesila.child.actors

import akka.actor.{Actor, ActorLogging, Props}
import com.btesila.hakky.presentation.basics.Barista.{CoffeeRequest, JuiceRequest}

class Barista extends Actor with ActorLogging {
  val child = context.actorOf(OrderRegister.props(), "hakky-order-register")

  override def receive: Receive = {
    case CoffeeRequest => log.info("Preparing a coffee right away")
    case JuiceRequest  => log.info("Preparing a juice right away")
  }
}

object Barista {
  def props(): Props = Props(classOf[Barista])

  case object CoffeeRequest
  case object JuiceRequest
}
