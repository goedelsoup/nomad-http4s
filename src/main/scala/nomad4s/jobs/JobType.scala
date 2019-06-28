package nomad4s.jobs

import cats.implicits._
import io.circe._

sealed abstract class JobType(val tag: String) extends Product with Serializable

object JobType {

  case object Batch extends JobType("batch")
  case object Service extends JobType("service")
  case object System extends JobType("system")

  implicit val decoderForJobType: Decoder[JobType] =
    Decoder.decodeString.emap(
      s => (Batch :: Service :: System :: Nil).find(_.tag === s).toRight(s"Invalid job type: $s")
    )

  implicit val encoderForJobType: Encoder[JobType] =
    Encoder.encodeString.contramap(_.tag)
}
