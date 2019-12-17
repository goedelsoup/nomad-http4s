package compstak.nomad.data

import cats.implicits._

import io.circe._

object Evaluations {

  final case class Evaluation(
    id: String,
    priority: Int,
    `type`: String,
    triggeredBy: String,
    jobId: String,
    jobModifyIndex: Int,
    nodeId: String,
    nodeModifyIndex: Int,
    status: String,
    statusDescription: String,
    waitTime: Int,
    nextEval: String,
    previousEval: String,
    blockedEval: String,
    failedTGAllocs: String,
    classEligibility: String,
    escapedComputedClass: Boolean,
    annotatePlan: Boolean,
    queuedAllocations: Map[String, Int],
    snapshotIndex: Int,
    createIndex: Int,
    modifyIndex: Int
  )

  object Evaluation {

    implicit val decoderForEvaluation = new Decoder[Evaluation] {
      def apply(c: HCursor): Decoder.Result[Evaluation] =
        (
          c.downField("ID").as[String],
          c.downField("Priority").as[Int],
          c.downField("Type").as[String],
          c.downField("TriggeredBy").as[String],
          c.downField("JobID").as[String],
          c.downField("JobModifyIndex").as[Int],
          c.downField("NodeID").as[String],
          c.downField("NodeModifyIndex").as[Int],
          c.downField("Status").as[String],
          c.downField("StatusDescription").as[String],
          c.downField("Wait").as[Int],
          c.downField("NextEval").as[String],
          c.downField("PreviousEval").as[String],
          c.downField("BlockedEval").as[String],
          c.downField("FailedTGAllocs").as[String],
          c.downField("ClassEligibility").as[String],
          c.downField("EscapedComputedClass").as[Boolean],
          c.downField("AnnotatePlan").as[Boolean],
          c.downField("QueuedAllocations").as[Map[String, Int]],
          c.downField("SnapshotIndex").as[Int],
          c.downField("CreateIndex").as[Int],
          c.downField("ModifyIndex").as[Int]
        ).mapN(Evaluation.apply)
    }
  }
}
