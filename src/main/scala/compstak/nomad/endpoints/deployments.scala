package compstak.nomad.endpoints

import cats.data.Kleisli
import cats.effect.Sync

import compstak.nomad.Auth
import compstak.nomad.data.Allocations.Alloc
import compstak.nomad.data.Deployments._
import compstak.nomad.data.Evaluations._
import compstak.nomad.internals._
import compstak.nomad.internals.NomadMedia._

import org.http4s._
import org.http4s.implicits._
import org.http4s.client.Client

object deployments {

  def allocs[F[_]: Sync](
    auth: Auth,
    id: String
  ): Kleisli[F, Client[F], List[Alloc]] =
    RequestConstructor.runRequestWithNoBody(
      auth,
      Method.GET,
      uri"/v1/deployment" / "allocations" / id
    )

  def fail[F[_]: Sync](
    auth: Auth,
    id: String
  ): Kleisli[F, Client[F], List[Alloc]] =
    RequestConstructor.runRequestWithNoBody(
      auth,
      Method.POST,
      uri"/v1/deployment" / "fail" / id
    )

  def deployment[F[_]: Sync](
    auth: Auth,
    id: String
  ): Kleisli[F, Client[F], Deployment] =
    RequestConstructor.runRequestWithNoBody(
      auth,
      Method.GET,
      uri"/v1/deployment" / id
    )

  def list[F[_]: Sync](
    auth: Auth
  ): Kleisli[F, Client[F], List[Deployment]] =
    RequestConstructor.runRequestWithNoBody(
      auth,
      Method.GET,
      uri"/v1/deployments"
    )

  def pause[F[_]: Sync](
    auth: Auth,
    id: String
  ): Kleisli[F, Client[F], List[Alloc]] =
    RequestConstructor.runRequestWithNoBody(
      auth,
      Method.POST,
      uri"/v1/deployment" / "pause" / id
    )

  def promote[F[_]: Sync](
    auth: Auth,
    id: String
  ): Kleisli[F, Client[F], List[Alloc]] =
    RequestConstructor.runRequestWithNoBody(
      auth,
      Method.POST,
      uri"/v1/deployment" / "promote" / id
    )
}
