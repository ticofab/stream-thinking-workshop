package io.ticofab.meetupstreamworkshop

import akka.Done
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Source}

import scala.concurrent.Future
import scala.util.{Failure, Success}

object Example2 extends App {
  implicit val as = ActorSystem()
  implicit val am = ActorMaterializer()
  implicit val ec = as.dispatcher

  val failingFlow = Flow[Int].map(n => 100 / (n - 4))

  val futureCompletion: Future[Done] =
    Source(1 to 10)
      .via(failingFlow)
      .runForeach(println)

  futureCompletion
    .onComplete {
      case Success(value) => println("completed with success: " + value)
      case Failure(error) => println("completed with error: " + error.getMessage)
    }
}
