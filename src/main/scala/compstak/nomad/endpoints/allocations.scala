package compstak.nomad.endpoints

import cats.data.Kleisli
import cats.effect.Sync

import io.circe.Json
import io.circe.syntax._

import compstak.nomad.Auth
import compstak.nomad.data.Allocations._
import compstak.nomad.internals._
import compstak.nomad.internals.NomadMedia._

import org.http4s._
import org.http4s.implicits._
import org.http4s.client.Client

object allocations {

  def alloc[F[_]: Sync](
    auth: Auth,
    id: String
  ): Kleisli[F, Client[F], List[Alloc]] =
    RequestConstructor.runRequestWithNoBody(
      auth,
      Method.GET,
      uri"/v1/allocation" / id
    )

  def list[F[_]: Sync](
    auth: Auth
  ): Kleisli[F, Client[F], List[Alloc]] =
    RequestConstructor.runRequestWithNoBody(
      auth,
      Method.GET,
      uri"/v1/allocations"
    )

  def restart[F[_]: Sync](
    auth: Auth,
    id: String
  ): Kleisli[F, Client[F], Unit] =
    RequestConstructor.runRequestWithNoBody(
      auth,
      Method.POST,
      uri"/v1/allocation" / id / "restart"
    )

  def signal[F[_]: Sync](
    auth: Auth,
    id: String,
    signal: String
  ): Kleisli[F, Client[F], Unit] =
    RequestConstructor.runRequestWithBody(
      auth,
      Method.POST,
      uri"/v1/allocation" / id / "signal",
      Json.obj("Signal" -> signal.asJson)
    )

  def stop[F[_]: Sync](
    auth: Auth,
    id: String
  ): Kleisli[F, Client[F], Unit] =
    RequestConstructor.runRequestWithNoBody(
      auth,
      Method.POST,
      uri"/v1/allocation" / id / "stop"
    )
}
