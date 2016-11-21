package com.btesila.trainining.hakky.cupcake

sealed trait Cupcake

object Cupcake {
  case object MarshmallowCupcake extends Cupcake
  case object NutellaCupcake extends Cupcake
  case object OreoCupcake extends Cupcake

  def apply(cupcake: String): Cupcake = cupcake.toLowerCase match {
    case "m" => MarshmallowCupcake
    case "n" => NutellaCupcake
    case "o" => OreoCupcake
    case _   => throw new IllegalArgumentException("Sorry, sir, we don't serve this kind of cupcake.")
  }
}
