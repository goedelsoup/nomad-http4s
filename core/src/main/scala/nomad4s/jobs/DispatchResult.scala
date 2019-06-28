package nomad4s.jobs

import cats.Show
import cats.effect.Sync
import io.circe.Decoder
import org.http4s.circe._

final case class DispatchResult(
  index: Int,
  jobCreateIndex: Int,
  evaluationCreateIndex: Int,
  evaluationId: EvaluationId,
  dispatchedJobId: JobId
)

object DispatchResult {

  implicit val decoderForDispatchResult: Decoder[DispatchResult] =
    Decoder.forProduct5(
      "Index",
      "JobCreateIndex",
      "EvalCreateIndex",
      "EvalID",
      "DispatchedJobID"
    )(DispatchResult.apply)

  implicit def entityDecoderForDispatchResult[F[_]: Sync] = jsonOf[F, DispatchResult]

  implicit def showForDispatchResult: Show[DispatchResult] = Show.fromToString
}
