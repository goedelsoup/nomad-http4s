package compstak.nomad.data

import cats.implicits._

import io.circe._

object Deployments {

  final case class TaskGroup(
    autoRevert: Boolean,
    promoted: Boolean,
    placedCanaries: Option[List[String]],
    desiredCanaries: Int,
    desiredTotal: Int,
    placedAllocs: Int,
    healthyAllocs: Int,
    unhealthyAllocs: Int
  )

  object TaskGroup {

    implicit val decoderForTaskGroup = new Decoder[TaskGroup] {
      def apply(c: HCursor): Decoder.Result[TaskGroup] =
        (
          c.downField("AutoRevert").as[Boolean],
          c.downField("Promoted").as[Boolean],
          c.downField("PlacedCanaries").as[Option[List[String]]],
          c.downField("DesiredCanaries").as[Int],
          c.downField("DesiredTotal").as[Int],
          c.downField("PlacedAllocs").as[Int],
          c.downField("HealthyAllocs").as[Int],
          c.downField("UnhealthyAllocs").as[Int]
        ).mapN(TaskGroup.apply)
    }
  }

  final case class Deployment(
    id: String,
    jobId: String,
    jobVersion: Int,
    jobModifyIndex: Int,
    jobCreateIndex: Int,
    taskGroups: Map[String, TaskGroup],
    status: String,
    statusDescription: String,
    createIndex: Int,
    modifyIndex: Int
  )

  object Deployment {

    implicit val decoderForDeployment = new Decoder[Deployment] {
      def apply(c: HCursor): Decoder.Result[Deployment] =
        (
          c.downField("ID").as[String],
          c.downField("JobID").as[String],
          c.downField("JobVersion").as[Int],
          c.downField("JobModifyIndex").as[Int],
          c.downField("JobCreateIndex").as[Int],
          c.downField("TaskGroups").as[Map[String, TaskGroup]],
          c.downField("Status").as[String],
          c.downField("StatusDescription").as[String],
          c.downField("CreateIndex").as[Int],
          c.downField("ModifyIndex").as[Int]
        ).mapN(Deployment.apply)
    }
  }
}
