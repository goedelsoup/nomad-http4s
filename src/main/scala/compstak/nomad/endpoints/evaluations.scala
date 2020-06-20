package compstak.nomad.endpoints

import cats.data.Kleisli
import cats.effect.Sync

import compstak.nomad.Auth
import compstak.nomad.data.Allocations.Alloc
import compstak.nomad.data.Evaluations._
import compstak.nomad.internals._
import compstak.nomad.internals.NomadMedia._

import org.http4s._
import org.http4s.implicits._
import org.http4s.client.Client

object evaluations {

  def allocs[F[_]: Sync](
    auth: Auth,
    id: String
  ): Kleisli[F, Client[F], List[Alloc]] =
    RequestConstructor.runRequestWithNoBody(
      auth,
      Method.GET,
      uri"/v1/evaluation" / id / "allocations"
    )

  def evaluation[F[_]: Sync](
    auth: Auth,
    id: String
  ): Kleisli[F, Client[F], Evaluation] =
    RequestConstructor.runRequestWithNoBody(
      auth,
      Method.GET,
      uri"/v1/evaluation" / id
    )

  def list[F[_]: Sync](
    auth: Auth
  ): Kleisli[F, Client[F], List[Evaluation]] =
    RequestConstructor.runRequestWithNoBody(
      auth,
      Method.GET,
      uri"/v1/evaluations"
    )
}
