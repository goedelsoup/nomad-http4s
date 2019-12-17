package compstak.nomad.internals

import cats._
import io.circe._
import org.http4s._
import org.http4s.circe._

object NomadMedia extends CirceEntityDecoder {
  implicit def jsonEncoder[F[_]: Applicative, A: Encoder] = {
    val json = jsonEncoderOf[F, A]

    new EntityEncoder[F, A] {
      def headers: Headers = json.headers
      def toEntity(a: A): Entity[F] = json.toEntity(a)
    }
  }
}
