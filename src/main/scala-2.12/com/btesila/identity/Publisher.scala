package com.btesila.identity

import akka.actor.{Actor, ActorIdentity, ActorLogging, ActorRef, Identify, Props, Terminated}
import com.btesila.identity.Publisher.{CheckForReceiver, HelloBack, Publish}
import com.btesila.identity.Receiver.Hi

/**
  * @author Adobe Systems Inc
  */
class Publisher(receiver: ActorRef) extends Actor with ActorLogging {
  val identifyId = 1

  context.watch(receiver)

  def receive: Receive = withReceiver(receiver)

  def withReceiver(receiver: ActorRef): Receive = {
    case Publish => receiver ! Hi
    case HelloBack => log.info("the receiver is responsive")
    case Terminated(watchedActor) if watchedActor == receiver => {
      log.info("Oh, no! My receiver has died!")
      context.become(withoutReceiver)
    }
  }

  def withoutReceiver: Receive = {
    case CheckForReceiver => {
      log.info("I should check for receiver...")
      val actorSelection = context.actorSelection(receiver.path)
      actorSelection ! Identify(identifyId)
    }
    case ActorIdentity(`identifyId`, Some(actorRef)) => {
      log.info("Got a new receiver")
      context.become(withReceiver(actorRef))
    }
    case ActorIdentity(`identifyId`, None) =>
      log.info("Lookup failed")
  }
}

object Publisher {
  def props(receiver: ActorRef): Props = Props(classOf[Publisher], receiver)

  case object HelloBack
  case object Publish
  case object CheckForReceiver
}
