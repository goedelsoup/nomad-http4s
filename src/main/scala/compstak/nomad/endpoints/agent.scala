package compstak.nomad.endpoints

import cats.data.Kleisli
import cats.effect.Sync

import io.circe.Json
import io.circe.syntax._

import compstak.nomad.Auth
import compstak.nomad.data.Agent._
import compstak.nomad.internals._
import compstak.nomad.internals.NomadMedia._

import org.http4s._
import org.http4s.implicits._
import org.http4s.client.Client

object agent {

  def members[F[_]: Sync](
    auth: Auth
  ): Kleisli[F, Client[F], Members] =
    RequestConstructor.runRequestWithNoBody(
      auth,
      Method.GET,
      uri"/v1/agent/members"
    )

  def servers[F[_]: Sync](
    auth: Auth
  ): Kleisli[F, Client[F], List[String]] =
    RequestConstructor.runRequestWithNoBody(
      auth,
      Method.GET,
      uri"/v1/agent/servers"
    )

  def self[F[_]: Sync](
    auth: Auth
  ): Kleisli[F, Client[F], Info] =
    RequestConstructor.runRequestWithNoBody(
      auth,
      Method.GET,
      uri"/v1/agent/self"
    )

  def health[F[_]: Sync](
    auth: Auth
  ): Kleisli[F, Client[F], Health] =
    RequestConstructor.runRequestWithNoBody(
      auth,
      Method.GET,
      uri"/v1/agent/health"
    )
}
