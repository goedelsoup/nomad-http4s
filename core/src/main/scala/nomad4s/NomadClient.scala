package nomad4s

import cats.Show
import cats.effect._
import cats.implicits._
import io.circe._
import io.circe.syntax._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import nomad4s.jobs._
import org.http4s._
import org.http4s.Uri.uri
import org.http4s.circe._
import org.http4s.client.Client
import org.http4s.dsl.Http4sDsl

final class NomadClient[F[_]: Sync](val http: Client[F], host: Uri) extends NomadAlg[F] with Http4sDsl[F] {

  val logger = Slf4jLogger.unsafeCreate[F]

  def dispatch[P: Encoder, M: Encoder](job: JobId, payload: P, meta: M): F[DispatchResult] =
    http.fetch[DispatchResult](
      Request[F](
        method = POST,
        uri = host / "v1" / "job" / job / "dispatch"
      ).withEntity(
        Json.obj(
          "Payload" -> payload.asJson,
          "Meta" -> meta.asJson
        )
      )
    ) {
      case resp if resp.status === Ok => decodeAndLog(resp)
    }

  def read[P: Decoder: Show, M: Decoder: Show](job: JobId): F[ReadResult[P, M]] =
    http.fetch[ReadResult[P, M]](
      Request[F](
        method = GET,
        uri = host / "v1" / "job" / job
      )
    ) {
      case resp if resp.status === Ok => decodeAndLog(resp)
    }

  def stop(job: JobId, purge: Boolean = false): F[StopResult] =
    http.fetch[StopResult](
      Request[F](
        method = DELETE,
        uri = host / "v1" / "job" / job +? ("purge", purge)
      )
    ) {
      case resp if resp.status === Ok => decodeAndLog(resp)
    }

  private[this] def decodeAndLog[A: Show](response: Response[F])(implicit ED: EntityDecoder[F, A]) =
    response.as[A].flatTap(a => logger.info(a.show))
}
