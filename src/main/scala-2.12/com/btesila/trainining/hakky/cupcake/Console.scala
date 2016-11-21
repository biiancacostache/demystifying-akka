package com.btesila.trainining.hakky.cupcake

import scala.util.parsing.combinator.RegexParsers

trait Console {
  sealed trait Command

  object Command {
    case class Customer(cupcake: Cupcake, count: Int) extends Command
    case object Status extends Command
    case object Quit extends Command
    case class Unknown(command: String) extends Command

    def apply(command: String): Command = CommandParser.parseCommand(command)
  }

  private object CommandParser extends RegexParsers {
    def parseCommand(command: String): Command =
      parseAll(parser, command) match {
        case Success(c, _) => c
        case _             => Command.Unknown(command)
      }

    def cupcake: Parser[Cupcake] =
      "M|m|N|n|O|o".r ^^ Cupcake.apply

    def customer: Parser[Command.Customer] =
      ("G|g".r ~> opt(cupcake) ~ opt(int)) ^^ {
        case (cupcake ~ count) =>
          Command.Customer(
            cupcake getOrElse Cupcake.OreoCupcake,
            count getOrElse 1
          )
      }

    def int: Parser[Int] =
      """\d+""".r ^^ (_.toInt)

    def status: Parser[Command.Status.type] =
      "status|s".r ^^ (_ => Command.Status)

    def quit: Parser[Command.Quit.type ] =
      "quit|q".r ^^ (_ => Command.Quit)

    private val parser: Parser[Command] =
      customer | status | quit
  }
}