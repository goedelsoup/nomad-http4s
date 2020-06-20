package compstak.nomad
package endpoints

import cats.effect._
import cats.implicits._

class AllocationsSuite extends NomadSuite {

  "Allocations" - {
    "get" in {
      allocations
        .alloc[IO](auth, "1f95810d-0ddc-fc8d-12a8-b70763b81cf9")
        .run(client)
        .map(_ => assert(true))
    }

    "list" in {
      allocations
        .list[IO](auth)
        .run(client)
        .map(_ => assert(true))
    }

    "restart" in {
      allocations
        .restart[IO](auth, "1f95810d-0ddc-fc8d-12a8-b70763b81cf9", "traefik")
        .run(client)
        .map(_ => assert(true))
    }

    "signal" in {
      allocations
        .signal[IO](auth, "1f95810d-0ddc-fc8d-12a8-b70763b81cf9", "traefik", "SIGCONT")
        .run(client)
        .map(_ => assert(true))
    }

    "stop" in {
      allocations
        .stop[IO](auth, "")
        .run(client)
        .map(_ => assert(true))
    }
  }
}
