package compstak.nomad
package endpoints

import cats.effect._
import cats.implicits._

class EvaluationsSuite extends NomadSuite {

  "Evaluations" - {
    "get" in {
      evaluations
        .evaluation[IO](auth, dispatchEval)
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
        .allocs[IO](auth, dispatchEval)
        .run(client)
        .map(_ => assert(true))
    }
  }
}
