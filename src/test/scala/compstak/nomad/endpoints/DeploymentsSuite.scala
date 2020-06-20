package compstak.nomad
package endpoints

import cats.effect._
import cats.implicits._

class DeploymentsSuite extends NomadSuite {

  "Deployments" - {
    "get" in {
      deployments
        .deployment[IO](auth, "d2158419-6ee5-ce18-3305-43181a2fdde3")
        .run(client)
        .map(_ => assert(true))
    }

    "allocs" in {
      deployments
        .allocs[IO](auth, "d2158419-6ee5-ce18-3305-43181a2fdde3")
        .run(client)
        .map(_ => assert(true))
    }

    "list" in {
      deployments
        .list[IO](auth)
        .run(client)
        .map(_ => assert(true))
    }

    "fail" in {
      deployments
        .fail[IO](auth, "")
        .run(client)
        .map(_ => assert(true))
    }

    "pause" in {
      deployments
        .pause[IO](auth, "")
        .run(client)
        .map(_ => assert(true))
    }

    "promote" in {
      deployments
        .promote[IO](auth, "")
        .run(client)
        .map(_ => assert(true))
    }
  }
}
