package com.btesila.cluster

object Clustering extends App {
  Barista.start(2551)
  Barista.start(2552)
  Barista.start(2553)
  CoffeHouse.start()
}
