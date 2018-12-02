package io.ticofab.meetupstreamworkshop

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink}

import scala.concurrent.Future
import scala.util.Try

object MeetupStreamWorkshopApp extends App {

  case class RsvpId(value: Int) extends AnyVal

  case class Rsvp(rsvp_id: RsvpId)

  val rsvpParserFlow = Flow[String].map(resp => Try {
    val index = resp.indexOf("rsvp_id")
    val colonIndex = resp.indexOf(":", index)
    val commaIndex = resp.indexOf(",", colonIndex)
    Rsvp(RsvpId(resp.substring(colonIndex + 1, commaIndex).toInt))
  })

  implicit val as = ActorSystem()
  implicit val am = ActorMaterializer()
  implicit val ec = as.dispatcher

  val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = "http://stream.meetup.com/2/rsvps"))

  responseFuture.map(resp => {
    resp
      .entity
      .dataBytes
      .map(_.utf8String)
      .via(rsvpParserFlow)
      .divertTo(Sink.foreach(a => println("Failure sink:" + a)), _.isFailure)
      .map(_.get) // at this point we know that only Success tries pass through
      .runWith(Sink.foreach(println))
  })
}