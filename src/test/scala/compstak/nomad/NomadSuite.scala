package compstak.nomad

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import cats.implicits._
import org.scalatest.matchers.should.Matchers
import org.scalatest.freespec.AsyncFreeSpec
import org.http4s.client.asynchttpclient.AsyncHttpClient
import org.http4s.implicits._
import org.scalatest.BeforeAndAfterAll
import cats.data.Kleisli

trait NomadSuite extends AsyncFreeSpec with BeforeAndAfterAll with AsyncIOSpec with Matchers {

  val auth = Anonymous(uri"http://127.0.0.1:4646")
  val client = AsyncHttpClient
    .allocate[IO]()
    .map(_._1)
    .unsafeRunSync()
  var dispatchJob: data.Jobs.Job = null
  var dispatchEval: String = null

  override def beforeAll(): Unit = {
    import endpoints.jobs._
    import data.Jobs

    parse[IO](auth, data.Jobs.ParseJob(JobSpecs.echo, true))
      .flatTap { job =>
        dispatchJob = job
        Kleisli(_ => IO(job))
      }
      .flatMap { job =>
        create[IO](auth, Jobs.CreateJob(job))
      }
      .map { job =>
        dispatchEval = job.evalId
        job
      }
      .run(client)
      .void
      .unsafeRunSync()
  }
}
