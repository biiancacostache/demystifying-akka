package com.btesila.cluster

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props, ReceiveTimeout}
import akka.cluster.Cluster
import akka.routing.FromConfig
import com.btesila.cluster.Barista.{BaristaRequest, CoffeeRequest}
import com.btesila.cluster.OrderRegister.Receipt
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

class CoffeHouse extends Actor with ActorLogging {
  val router: ActorRef = context.actorOf(FromConfig.props(), "barista-router")

  override def preStart(): Unit = {
    sendJobs()
    context.setReceiveTimeout(10 seconds)
  }

  def receive = {
    case Receipt(amount) =>
      log.info("Have to handle the receipt worth {} to the customer", amount)
    case ReceiveTimeout =>
      log.info("Timeout")
      sendJobs()
  }

  def sendJobs(): Unit = {
    log.info("Starting batch of coffee requests...")
    val requests: List[BaristaRequest] = List.fill(100)(CoffeeRequest)
    requests foreach {
      router ! _
    }
  }
}

object CoffeHouse {
  def props(): Props = Props(classOf[CoffeHouse])

  def start(): Unit = {
    val config = ConfigFactory.parseString("akka.cluster.roles = [coffee-house]")
      .withFallback(ConfigFactory.load("cluster.conf"))
    val system = ActorSystem("hakky-cluster", config)

    Cluster(system) registerOnMemberUp {
      system.actorOf(CoffeHouse.props(), "coffee-house")
    }
  }
}
