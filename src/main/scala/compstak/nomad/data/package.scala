package compstak.nomad

import java.time.Duration

import cats.implicits._
import io.circe.ACursor
import io.circe.DecodingFailure

import scala.util.Try

package object data {

  def boolFromStringCursor(a: ACursor) =
    a.as[String].flatMap {
      case "true"  => true.asRight
      case "false" => false.asRight
      case _       => DecodingFailure("Invalid boolean", a.history).asLeft
    }

  def durationFromStringCursor(a: ACursor) =
    a.as[String].flatMap { str =>
      val maybeNumeric = Try(str.dropRight(1).toDouble).toOption
      maybeNumeric
        .toRight(DecodingFailure("Invalid numeric in intial position", a.history))
        .flatMap { numeric =>
          str.last match {
            case 's' => Duration.ofNanos((numeric * 1000000000).toLong).asRight
            case _   => DecodingFailure("Failed to parse duration", a.history).asLeft
          }
        }
    }
}
