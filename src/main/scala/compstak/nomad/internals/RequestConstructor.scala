package compstak.nomad
package internals

import cats._
import cats.data.Kleisli
import cats.effect._
import cats.implicits._

import fs2._

import org.http4s._
import org.http4s.implicits._
import org.http4s.client.Client
import org.http4s.headers.{Authorization, Link}

object RequestConstructor {

  def runRequestWithBody[F[_]: Sync, A, B](
    auth: Auth,
    method: Method,
    extendedUri: Uri,
    body: A
  )(implicit AD: EntityEncoder[F, A], BD: EntityDecoder[F, B]): Kleisli[F, Client[F], B] =
    runRequest[F, A, B](auth, method, extendedUri, body.some)

  def runRequestWithNoBody[F[_]: Sync, B](
    auth: Auth,
    method: Method,
    extendedUri: Uri
  )(implicit BD: EntityDecoder[F, B]): Kleisli[F, Client[F], B] =
    runRequest[F, Unit, B](auth, method, extendedUri, None)

  def runRequest[F[_]: Sync, A, B](
    auth: Auth,
    method: Method,
    extendedUri: Uri,
    body: Option[A]
  )(implicit AD: EntityEncoder[F, A], BD: EntityDecoder[F, B]): Kleisli[F, Client[F], B] = Kleisli { c =>
    val uri = Uri.resolve(baseUrl(auth), extendedUri)
    val baseReq = Request[F](method = method, uri = uri)
      .withHeaders(extraHeaders(auth))
    val req = body.fold(baseReq)(a => baseReq.withEntity(a))
    c.expect[B](req)
  }

  def runRequestWithBodyWithoutResponse[F[_]: Sync, A](
    auth: Auth,
    method: Method,
    extendedUri: Uri,
    body: A
  )(implicit AD: EntityEncoder[F, A]): Kleisli[F, Client[F], Unit] =
    runRequestWithoutResponse[F, A](auth, method, extendedUri, body.some)

  def runRequestWithNoBodyWithoutResponse[F[_]: Sync](
    auth: Auth,
    method: Method,
    extendedUri: Uri
  ): Kleisli[F, Client[F], Unit] =
    runRequestWithoutResponse[F, Unit](auth, method, extendedUri, None)

  def runRequestWithoutResponse[F[_]: Sync, A](
    auth: Auth,
    method: Method,
    extendedUri: Uri,
    body: Option[A]
  )(implicit AD: EntityEncoder[F, A]): Kleisli[F, Client[F], Unit] = Kleisli { c =>
    val uri = Uri.resolve(baseUrl(auth), extendedUri)
    val baseReq = Request[F](method = method, uri = uri)
      .withHeaders(extraHeaders(auth))
    val req = body.fold(baseReq)(a => baseReq.withEntity(a))
    c.successful(req).void
  }

  private[this] def baseUrl(auth: Auth): Uri =
    auth match {
      case Anonymous(uri)    => uri
      case TokenInfo(uri, _) => uri
    }

  private[this] def extraHeaders(auth: Auth): Headers =
    auth match {
      case Anonymous(_) => Headers.empty
      case TokenInfo(_, token) =>
        Headers.of(
          Header("X-Nomad-Token", token)
        )
    }
}
