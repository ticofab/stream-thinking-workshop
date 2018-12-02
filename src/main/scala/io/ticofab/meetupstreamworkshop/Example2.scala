package io.ticofab.meetupstreamworkshop

import akka.Done
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object Example1 extends App {
  implicit val as = ActorSystem()
  implicit val am = ActorMaterializer()
  implicit val ec = as.dispatcher

  val futureCompletion: Future[Done] =
    Source(1 to 10)
    .throttle(1, 1.second)
    .runForeach(println)

  futureCompletion
    .onComplete {
      case Success(value) => println("completed with success: " + value)
      case Failure(error) => println("completed with error: " + error.getMessage)
    }
}
