package com.btesila.trainining.hakky.cupcake

import spray.json._

/**
  * @author Adobe Systems Inc
  */
object MagnetPattern extends App {
  def fetchDescription(magnet: Magnet) = magnet()

  println(fetchDescription(1))
  println(fetchDescription("lala"))
  println(fetchDescription(Msg("hi")))
  println(fetchDescription(AnotherMsg("hello, again")))

  case class Msg(description: String)
  object Msg extends DefaultJsonProtocol {
    implicit val jsonFormat: JsonFormat[Msg] = jsonFormat1(Msg.apply)
  }

  case class AnotherMsg(anotherDescription: String)
  object AnotherMsg extends DefaultJsonProtocol {
    implicit val jsonFormat: JsonFormat[AnotherMsg] = jsonFormat1(AnotherMsg.apply)
  }
}

sealed trait Magnet {
  type Result
  def apply(): Result
}

object Magnet {
  implicit def fromInt(a: Int): Magnet = new Magnet {
    override def apply() = s"Int description: $a"
    override type Result = String
  }

  implicit def fromString(a: String): Magnet = new Magnet {
    override def apply() = a.toList
    override type Result = List[Char]
  }

  implicit def fromObject[T: JsonFormat](a: T): Magnet = new Magnet {
    override def apply() = s"Object description ${a.toJson}"
    override type Result = String
  }
}


