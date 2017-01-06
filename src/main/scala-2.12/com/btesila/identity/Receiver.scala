package com.btesila.identity

import akka.actor.{Actor, ActorLogging, Props}
import com.btesila.identity.Publisher.HelloBack
import com.btesila.identity.Receiver.{Hi, Poison}

/**
  * @author Adobe Systems Inc
  */
class Receiver extends Actor with ActorLogging {
  def receive = {
    case Hi     => sender() ! HelloBack
  }
}

object Receiver {
  def props(): Props = Props(classOf[Receiver])

  case object Hi
  case object Poison
}
