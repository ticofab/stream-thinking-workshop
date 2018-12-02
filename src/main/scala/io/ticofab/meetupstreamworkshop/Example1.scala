package io.ticofab.meetupstreamworkshop

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.concurrent.Future
import scala.concurrent.duration._

object Example2 extends App {
  implicit val as = ActorSystem()
  implicit val am = ActorMaterializer()
  implicit val ec = as.dispatcher

  val mySource: Source[Int, NotUsed] =
    Source(1 to 10)

  val myFlow: Flow[Int, Int, NotUsed] =
    Flow[Int]
      .map(n => n * 2)

  val mySink: Sink[Any, Future[Done]] =
    Sink.foreach(println)

  val blueprint =
    mySource
      .via(myFlow)
      .throttle(1, 1.second)
      .to(mySink)

  blueprint.run()

}
