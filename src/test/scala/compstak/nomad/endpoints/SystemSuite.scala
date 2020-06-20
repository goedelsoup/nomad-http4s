package compstak.nomad
package endpoints

import cats.effect._
import cats.implicits._

class SystemSuite extends NomadSuite {

  "System" - {
    "force gc" in {
      system
        .forceGarbageCollection[IO](auth)
        .run(client)
        .map(_ => assert(true))
    }

    "reconcile summaries" in {
      system
        .reconcileSummaries[IO](auth)
        .run(client)
        .map(_ => assert(true))
    }
  }
}
