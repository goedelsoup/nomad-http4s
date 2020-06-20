package compstak.nomad
package endpoints

import cats.effect._
import cats.implicits._
import io.circe.syntax._

class JobsSuite extends NomadSuite {

  "Jobs" - {
    "get" in {
      jobs
        .job[IO](auth, dispatchJob.name)
        .run(client)
        .map(_ => assert(true))
    }

    "allocs" in {
      jobs
        .allocations[IO](auth, dispatchJob.name)
        .run(client)
        .as(assert(true))
    }

    "create" in {
      jobs
        .create[IO](auth, data.Jobs.CreateJob(null))
        .run(client)
        .as(assert(true))
    }

    "deployments" in {
      jobs
        .allocations[IO](auth, dispatchJob.name)
        .run(client)
        .as(assert(true))
    }

    "dispatch" in {
      jobs
        .dispatch[IO](auth, "echo", data.Jobs.DispatchJob("", io.circe.Json.obj("word" -> "foo".asJson)))
        .run(client)
        .as(assert(true))
    }

    "force periodic" in {
      jobs
        .allocations[IO](auth, dispatchJob.name)
        .run(client)
        .as(assert(true))
    }

    "evals" in {
      jobs
        .evaluations[IO](auth, dispatchJob.name)
        .run(client)
        .as(assert(true))
    }

    "latest deployment" in {
      jobs
        .latestDeployment[IO](auth, dispatchJob.name)
        .run(client)
        .as(assert(true))
    }

    "list" in {
      jobs
        .list[IO](auth, None)
        .run(client)
        .as(assert(true))
    }

    "parse" in {
      jobs
        .parse[IO](auth, data.Jobs.ParseJob(JobSpecs.echo, true))
        .run(client)
        .as(assert(true))
    }

    "revert" in {
      jobs
        .revert[IO](auth, null)
        .run(client)
        .as(assert(true))
    }

    "stop" in {
      jobs
        .stop[IO](auth, dispatchJob.name)
        .run(client)
        .as(assert(true))
    }

    "summary" in {
      jobs
        .summary[IO](auth, dispatchJob.name)
        .run(client)
        .as(assert(true))
    }

    "validate" in {
      jobs
        .validate[IO](auth, dispatchJob)
        .run(client)
        .as(assert(true))
    }

    "versions" in {
      jobs
        .versions[IO](auth, dispatchJob.name)
        .run(client)
        .as(assert(true))
    }
  }
}
