package compstak.nomad
package endpoints

import cats.effect._
import cats.implicits._

class AgentSuite extends NomadSuite {

  "Agent" - {
    "members" in {
      agent
        .members[IO](auth)
        .run(client)
        .map(_ => assert(true))
    }

    "servers" in {
      agent
        .servers[IO](auth)
        .run(client)
        .map(_ => assert(true))
    }

    "self" in {
      agent
        .self[IO](auth)
        .run(client)
        .map(_ => assert(true))
    }

    "health" in {
      agent
        .health[IO](auth)
        .run(client)
        .map(_ => assert(true))
    }
  }
}
