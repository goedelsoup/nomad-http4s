package compstak.nomad.endpoints

import cats.data.Kleisli
import cats.effect.Sync

import compstak.nomad.Auth
import compstak.nomad.internals._
import compstak.nomad.internals.NomadMedia._

import org.http4s._
import org.http4s.implicits._
import org.http4s.client.Client

object system {

  def forceGarbageCollection[F[_]: Sync](
    auth: Auth
  ): Kleisli[F, Client[F], Unit] =
    RequestConstructor.runRequestWithNoBody(
      auth,
      Method.PUT,
      uri"/v1/system/gc"
    )

  def reconcileSummaries[F[_]: Sync](
    auth: Auth
  ): Kleisli[F, Client[F], Unit] =
    RequestConstructor.runRequestWithNoBody(
      auth,
      Method.PUT,
      uri"/v1/system/reconcile/summaries"
    )
}
