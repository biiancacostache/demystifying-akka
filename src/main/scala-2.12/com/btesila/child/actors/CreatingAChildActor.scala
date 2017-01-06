package com.btesila.child.actors

import akka.actor.ActorSystem

object CreatingAChildActor extends App {
  val system = ActorSystem("hakky-bar")
  val barista = system.actorOf(Barista.props(), "hakky-barista")
}
