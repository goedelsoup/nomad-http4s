package nomad4s.jobs

import cats.Show
import cats.effect.Sync
import io.circe.Decoder
import org.http4s.circe._

final case class StopResult(
  evaluationId: EvaluationId,
  evaluationCreateIndex: Int,
  jobModifyIndex: Int
)

object StopResult {

  implicit val decoderForStopResult: Decoder[StopResult] =
    Decoder.forProduct3(
      "EvalID",
      "EvalCreateIndex",
      "JobModifyIndex"
    )(StopResult.apply)

  implicit def entityDecoderForStopResult[F[_]: Sync] = jsonOf[F, StopResult]

  implicit def showForStopResult: Show[StopResult] = Show.fromToString
}
