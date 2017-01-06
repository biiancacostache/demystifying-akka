package com.btesila.first.actor.system

import akka.actor.ActorSystem

/**
  * @author Adobe Systems Inc
  */
object CreatingATopLevelActor extends App {
  val system = ActorSystem("hakky-bar")
  val barista = system.actorOf(Barista.props(), "hakky-barista")
}
