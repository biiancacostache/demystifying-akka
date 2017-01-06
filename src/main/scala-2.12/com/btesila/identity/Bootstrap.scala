package com.btesila.identity

import akka.actor.ActorSystem
import com.btesila.identity.Publisher.{CheckForReceiver, Publish}

/**
  * @author Adobe Systems Inc
  */
object Bootstrap extends App {
  val as = ActorSystem("hakky-system")
  val receiver = as.actorOf(Receiver.props(), "receiver")
  val publisher = as.actorOf(Publisher.props(receiver), "publisher")
  publisher ! Publish
  as.stop(receiver)
  Thread.sleep(1000)
  val newReceiver = as.actorOf(Receiver.props(), "receiver")
  publisher ! CheckForReceiver
  Thread.sleep(1000)
  publisher ! Publish
}

