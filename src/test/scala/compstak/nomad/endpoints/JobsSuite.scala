package compstak.nomad
package endpoints

import cats.effect._
import cats.implicits._

class JobsSuite extends NomadSuite {

  "Jobs" - {
    "get" in {
      jobs
        .job[IO](auth, "traefik")
        .run(client)
        .map(_ => assert(true))
    }

    "allocs" in {
      jobs
        .allocations[IO](auth, "traefik")
        .run(client)
        .as(assert(true))
    }

    "create" in {
      jobs
        .create[IO](auth, null)
        .run(client)
        .as(assert(true))
    }

    "deployments" in {
      jobs
        .allocations[IO](auth, "traefik")
        .run(client)
        .as(assert(true))
    }

    "dispatch" in {
      jobs
        .dispatch[IO](auth, "traefik", null)
        .run(client)
        .as(assert(true))
    }

    "force periodic" in {
      jobs
        .allocations[IO](auth, "traefik")
        .run(client)
        .as(assert(true))
    }

    "evals" in {
      jobs
        .evaluations[IO](auth, "traefik")
        .run(client)
        .as(assert(true))
    }

    "latest deployment" in {
      jobs
        .latestDeployment[IO](auth, "traefik")
        .run(client)
        .as(assert(true))
    }

    "list" in {
      jobs
        .list[IO](auth, None)
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
        .stop[IO](auth, "foo")
        .run(client)
        .as(assert(true))
    }

    "summary" in {
      jobs
        .summary[IO](auth, "traefik")
        .run(client)
        .as(assert(true))
    }

    "validate" in {
      jobs
        .validate[IO](auth, null)
        .run(client)
        .as(assert(true))
    }

    "versions" in {
      jobs
        .versions[IO](auth, "traefik")
        .run(client)
        .as(assert(true))
    }
  }
}
