package com.btesila.trainining.hakky.cupcake

import akka.actor.{Actor, ActorLogging, Kill, Props}

import scala.concurrent.duration._

class HakkyCupcake extends Actor with ActorLogging with Console {
  log.info("The Hakky Cupcake Actor has started")

  import context.dispatcher

//  context.system.scheduler.schedule(1 second, 1 second, self, Kill)

//  override def aroundReceive(receive: Receive, msg: Any): Unit = {
//    log.info("Got a msg: {}, but I will ignore it", msg)
//  }

  def receive: Receive = {
    case c: Command.Customer => log.info("Got command {}", c)
  }
}

object HakkyCupcake {
  def props(): Props = Props[HakkyCupcake]

//  def props(): Props = Props(new HakkyCupcake)

//  def props(): Props = Props(classOf[HakkyCupcake])
}

