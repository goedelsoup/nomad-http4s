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
    nodeId: Option[String],
    nodeModifyIndex: Option[Int],
    status: String,
    statusDescription: Option[String],
    waitTime: Option[Int],
    nextEval: Option[String],
    previousEval: Option[String],
    blockedEval: Option[String],
    failedTGAllocs: Option[String],
    classEligibility: Option[Map[String, Boolean]],
    escapedComputedClass: Option[Boolean],
    annotatePlan: Option[Boolean],
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
          c.downField("NodeID").as[Option[String]],
          c.downField("NodeModifyIndex").as[Option[Int]],
          c.downField("Status").as[String],
          c.downField("StatusDescription").as[Option[String]],
          c.downField("Wait").as[Option[Int]],
          c.downField("NextEval").as[Option[String]],
          c.downField("PreviousEval").as[Option[String]],
          c.downField("BlockedEval").as[Option[String]],
          c.downField("FailedTGAllocs").as[Option[String]],
          c.downField("ClassEligibility").as[Option[Map[String, Boolean]]],
          c.downField("EscapedComputedClass").as[Option[Boolean]],
          c.downField("AnnotatePlan").as[Option[Boolean]],
          c.downField("QueuedAllocations").as[Map[String, Int]],
          c.downField("SnapshotIndex").as[Int],
          c.downField("CreateIndex").as[Int],
          c.downField("ModifyIndex").as[Int]
        ).mapN(Evaluation.apply)
    }
  }
}
