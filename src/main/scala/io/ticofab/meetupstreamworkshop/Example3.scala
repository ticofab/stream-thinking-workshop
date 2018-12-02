package io.ticofab.meetupstreamworkshop

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink}

import scala.util.Try

object Example3 extends App {

  implicit val as = ActorSystem()
  implicit val am = ActorMaterializer()
  implicit val ec = as.dispatcher

  case class RsvpId(value: Int) extends AnyVal

  case class Rsvp(rsvp_id: RsvpId)

  def parse(resp: String): Try[Rsvp] = Try {
    val index = resp.indexOf("rsvp_id")
    val colonIndex = resp.indexOf(":", index)
    val commaIndex = resp.indexOf(",", colonIndex)
    Rsvp(RsvpId(resp.substring(colonIndex + 1, commaIndex).toInt))
  }

  val rsvpParserFlow = Flow.fromFunction(parse)
  val failureSink = Sink.foreach[Try[Rsvp]](f => println("Failure sink:" + f))
  val successSink = Sink.foreach[Rsvp](rsvp => println("Parsed " + rsvp))
  val futureBlueprint = Http()
    .singleRequest(HttpRequest(uri = "http://stream.meetup.com/2/rsvps"))
    .map(resp => {
      resp
        .entity
        .dataBytes
        .map(_.utf8String)
        .via(rsvpParserFlow)
        .divertTo(failureSink, _.isFailure)
        .map(_.get) // at this point we know that only Success tries pass through
    })

  futureBlueprint
    .foreach(_.runWith(successSink))
}