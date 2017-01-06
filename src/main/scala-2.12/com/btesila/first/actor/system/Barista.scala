package com.btesila.first.actor.system

import akka.actor.{Actor, ActorLogging, Props}

class Barista extends Actor with ActorLogging {
  import Barista._

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
