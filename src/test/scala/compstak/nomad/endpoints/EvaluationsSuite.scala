package compstak.nomad
package endpoints

import cats.effect._
import cats.implicits._

class EvaluationsSuite extends NomadSuite {

  "Evaluations" - {
    "get" in {
      evaluations
        .evaluation[IO](auth, "74e9f36e-376c-c373-bea8-aa729fc266dd")
        .run(client)
        .map(_ => assert(true))
    }

    "list" in {
      evaluations
        .list[IO](auth)
        .run(client)
        .map(_ => assert(true))
    }

    "allocs" in {
      evaluations
        .allocs[IO](auth, "74e9f36e-376c-c373-bea8-aa729fc266dd")
        .run(client)
        .map(_ => assert(true))
    }
  }
}
