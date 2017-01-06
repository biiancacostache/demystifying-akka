package com.btesila.remote

import akka.actor.ActorSystem
import com.btesila.hakky.presentation.basics.HakkyActor
import com.btesila.hakky.presentation.basics.HakkyActor.messages.Hi
import com.typesafe.config.ConfigFactory

/**
  * @author Adobe Systems Inc
  */
object Bootstrap extends App {
  val config = ConfigFactory.load("remote.conf")
  val as1 = ActorSystem.create("hakky-remote-1", config)
  val config1 = ConfigFactory.load("remote2.conf")
  val as2 = ActorSystem.create("hakky-remote-2", config1)

  val actor1 = as1.actorOf(HakkyActor.props(), "actor1")
  val actor2 = as2.actorOf(HakkyActor.props(), "actor2")

  val actorSel = as2.actorSelection("akka.tcp://hakky-remote-1@localhost:9100/user/actor1")
  actorSel.tell(Hi, actor2)
}
