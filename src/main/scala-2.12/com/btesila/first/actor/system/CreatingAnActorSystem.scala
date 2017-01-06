package com.btesila.first.actor.system

import akka.actor.ActorSystem

object CreatingAnActorSystem extends App {
  val system = ActorSystem("hakky-bar")
}