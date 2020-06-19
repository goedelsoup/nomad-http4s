package compstak.nomad
package endpoints

import cats.effect._
import cats.implicits._
import org.http4s.implicits._

class AgentSuite extends NomadSuite {

  "Agent" - {
    "members" in {
      agent
        .members[IO](Anonymous(uri"http://localhost:4646"))
        .run(client)
        .map(_ => assert(true))
    }

    "servers" in {
      agent
        .servers[IO](Anonymous(uri"http://localhost:4646"))
        .run(client)
        .map(_ => assert(true))
    }

    "self" in {
      agent
        .self[IO](Anonymous(uri"http://localhost:4646"))
        .run(client)
        .map(_ => assert(true))
    }

    "health" in {
      agent
        .health[IO](Anonymous(uri"http://localhost:4646"))
        .run(client)
        .map(_ => assert(true))
    }
  }
}
