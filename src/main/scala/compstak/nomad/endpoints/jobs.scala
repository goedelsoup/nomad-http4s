package compstak.nomad
package endpoints

import cats.data.Kleisli
import cats.effect.Sync

import compstak.nomad.Auth
import compstak.nomad.data.Allocations.Alloc
import compstak.nomad.data.Deployments.Deployment
import compstak.nomad.data.Evaluations.Evaluation
import compstak.nomad.data.Jobs._
import compstak.nomad.internals._
import compstak.nomad.internals.NomadMedia._

import org.http4s._
import org.http4s.implicits._
import org.http4s.client.Client

object jobs {

  def allocations[F[_]: Sync](
    auth: Auth,
    id: String
  ): Kleisli[F, Client[F], List[Alloc]] =
    RequestConstructor.runRequestWithNoBody[F, List[Alloc]](
      auth,
      Method.GET,
      uri"/v1/job" / id / "allocations"
    )

  def create[F[_]: Sync](
    auth: Auth,
    createJob: CreateJob
  ): Kleisli[F, Client[F], CreatedJob] =
    RequestConstructor.runRequestWithBody[F, CreateJob, CreatedJob](
      auth,
      Method.POST,
      uri"/v1/jobs",
      createJob
    )

  def deployments[F[_]: Sync](
    auth: Auth,
    id: String
  ): Kleisli[F, Client[F], List[Deployment]] =
    RequestConstructor.runRequestWithNoBody[F, List[Deployment]](
      auth,
      Method.GET,
      uri"/v1/jobs" / id / "deployments"
    )

  def dispatch[F[_]: Sync](
    auth: Auth,
    job: String,
    dispatchJob: DispatchJob
  ): Kleisli[F, Client[F], Job] =
    RequestConstructor.runRequestWithBody[F, DispatchJob, Job](
      auth,
      Method.POST,
      uri"/v1/jobs" / job / "dispatch",
      dispatchJob
    )

  def forcePeriodic[F[_]: Sync](
    auth: Auth,
    job: String
  ): Kleisli[F, Client[F], Evaluation] =
    RequestConstructor.runRequestWithNoBody(
      auth,
      Method.POST,
      uri"/v1/jobs" / job / "periodic" / "force"
    )

  def evaluations[F[_]: Sync](
    auth: Auth,
    id: String
  ): Kleisli[F, Client[F], List[Evaluation]] =
    RequestConstructor.runRequestWithNoBody[F, List[Evaluation]](
      auth,
      Method.GET,
      uri"/v1/jobs" / id / "evaluations"
    )

  def job[F[_]: Sync](
    auth: Auth,
    id: String
  ): Kleisli[F, Client[F], Job] =
    RequestConstructor.runRequestWithNoBody[F, Job](
      auth,
      Method.GET,
      uri"/v1/jobs" / id
    )

  def latestDeployment[F[_]: Sync](
    auth: Auth,
    id: String
  ): Kleisli[F, Client[F], Deployment] =
    RequestConstructor.runRequestWithNoBody[F, Deployment](
      auth,
      Method.GET,
      uri"/v1/jobs" / id / "deployment"
    )

  def list[F[_]: Sync](
    auth: Auth,
    prefix: Option[String]
  ): Kleisli[F, Client[F], List[Job]] =
    RequestConstructor.runRequestWithNoBody(
      auth,
      Method.GET,
      prefix.fold(uri"/v1/jobs") { p =>
        uri"/v1/jobs" +? p
      }
    )

  def parse[F[_]: Sync](
    auth: Auth,
    parseJob: ParseJob
  ): Kleisli[F, Client[F], Job] =
    RequestConstructor.runRequestWithBody[F, ParseJob, Job](
      auth,
      Method.POST,
      uri"/v1/jobs/parse",
      parseJob
    )

  /*
  def parse[F[_]: Sync](
    auth: Auth,
    planJob: PlanJob
  ): Kleisli[F, Client[F], Job] =
    RequestConstructor.runRequestWithBody[F, PlanJob, Job](
      auth,
      Method.POST,
      uri"/v1/jobs" / planJob.job.id / "plan",
      planJob
    )
   */

  def revert[F[_]: Sync](
    auth: Auth,
    revertJob: RevertJob
  ): Kleisli[F, Client[F], Job] =
    RequestConstructor.runRequestWithBody[F, RevertJob, Job](
      auth,
      Method.POST,
      uri"/v1/jobs" / revertJob.id / "revert",
      revertJob
    )

  def stop[F[_]: Sync](
    auth: Auth,
    job: String
  ): Kleisli[F, Client[F], Evaluation] =
    RequestConstructor.runRequestWithNoBody(
      auth,
      Method.DELETE,
      uri"/v1/jobs" / job
    )

  def summary[F[_]: Sync](
    auth: Auth,
    id: String
  ): Kleisli[F, Client[F], Summary] =
    RequestConstructor.runRequestWithNoBody[F, Summary](
      auth,
      Method.GET,
      uri"/v1/jobs" / id / "summary"
    )

  def validate[F[_]: Sync](
    auth: Auth,
    job: Job
  ): Kleisli[F, Client[F], Validation] =
    RequestConstructor.runRequestWithBody[F, Job, Validation](
      auth,
      Method.POST,
      uri"/v1/validate/job",
      job
    )

  def versions[F[_]: Sync](
    auth: Auth,
    id: String
  ): Kleisli[F, Client[F], List[Job]] =
    RequestConstructor.runRequestWithNoBody[F, List[Job]](
      auth,
      Method.GET,
      uri"/v1/jobs" / id / "versions"
    )
}
