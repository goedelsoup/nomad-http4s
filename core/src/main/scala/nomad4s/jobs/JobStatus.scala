package nomad4s.jobs

import cats.implicits._
import io.circe._

sealed abstract class JobStatus(val tag: String) extends Product with Serializable

object JobStatus {

  case object Running extends JobStatus("running")

  implicit val decoderForJobStatus: Decoder[JobStatus] =
    Decoder.decodeString.emap(s => (Running :: Nil).find(_.tag === s).toRight(s"Invalid status: $s"))

}
