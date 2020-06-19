package compstak.nomad

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.freespec.AsyncFreeSpec
import org.http4s.client.asynchttpclient.AsyncHttpClient

class NomadSuite extends AsyncFreeSpec with AsyncIOSpec with Matchers {
  val client = AsyncHttpClient
    .allocate[IO]()
    .map(_._1)
    .unsafeRunSync()
}
