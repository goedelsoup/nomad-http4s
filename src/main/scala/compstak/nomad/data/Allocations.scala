package compstak.nomad.data

import cats.implicits._
import io.circe._

object Allocations {

  final case class RescheduleEvent(
    prevAllocId: String,
    prevNodeId: String,
    rescheduleTime: Double,
    delay: Double
  )

  object RescheduleEvent {

    implicit val decoderForRescheduleEvent = new Decoder[RescheduleEvent] {
      def apply(c: HCursor): Decoder.Result[RescheduleEvent] =
        (
          c.downField("PrevAllocID").as[String],
          c.downField("PrevNodeID").as[String],
          c.downField("RescheduleTime").as[Double],
          c.downField("Delay").as[Double]
        ).mapN(RescheduleEvent.apply)
    }
  }

  final case class Event(
    `type`: String,
    time: Long,
    failsTask: Boolean,
    restartReason: String,
    setupError: String,
    driverError: String,
    exitCode: Int,
    signal: Int,
    message: String,
    killTimeout: Long,
    killError: String,
    killReason: String,
    startDelay: Long,
    downloadError: String,
    validationError: String,
    diskLimit: Int,
    failedSibling: String,
    vaultError: String,
    taskSignalReason: String,
    taskSignal: String,
    driverMessage: String
  )

  object Event {

    implicit val decoderForEvent = new Decoder[Event] {
      def apply(c: HCursor): Decoder.Result[Event] =
        (
          c.downField("Type").as[String],
          c.downField("Time").as[Long],
          c.downField("FailsTask").as[Boolean],
          c.downField("RestartReason").as[String],
          c.downField("SetupError").as[String],
          c.downField("DriverError").as[String],
          c.downField("ExitCode").as[Int],
          c.downField("Signal").as[Int],
          c.downField("Message").as[String],
          c.downField("KillTimeout").as[Long],
          c.downField("KillError").as[String],
          c.downField("KillReason").as[String],
          c.downField("StartDelay").as[Long],
          c.downField("DownloadError").as[String],
          c.downField("ValidationError").as[String],
          c.downField("DiskLimit").as[Int],
          c.downField("FailedSibling").as[String],
          c.downField("VaultError").as[String],
          c.downField("TaskSignalReason").as[String],
          c.downField("TaskSignal").as[String],
          c.downField("DriverMessage").as[String]
        ).mapN(Event.apply)
    }
  }

  final case class TaskState(
    state: String,
    failed: Boolean,
    startedAt: String,
    finishedAt: String,
    events: List[Event]
  )

  object TaskState {

    implicit val decoderForTaskState = new Decoder[TaskState] {
      def apply(c: HCursor): Decoder.Result[TaskState] =
        (
          c.downField("State").as[String],
          c.downField("Failed").as[Boolean],
          c.downField("StartedAt").as[String],
          c.downField("FinishedAt").as[String],
          c.downField("Events").as[List[Event]]
        ).mapN(TaskState.apply)
    }
  }

  final case class RescheduleTracker(
    events: List[RescheduleEvent]
  )

  object RescheduleTracker {

    implicit val decoderForRescheduleTracker = new Decoder[RescheduleTracker] {
      def apply(c: HCursor): Decoder.Result[RescheduleTracker] =
        c.downField("Events").as[List[RescheduleEvent]].map(RescheduleTracker.apply)
    }
  }

  final case class Alloc(
    id: String,
    evalId: String,
    name: String,
    nodeId: String,
    previousAllocation: Option[String],
    nextAllocation: Option[String],
    rescheduleTracker: Option[RescheduleTracker],
    jobId: String,
    taskGroup: String,
    desiredStatus: String,
    desiredDescription: String,
    clientStatus: String,
    clientDescription: String,
    taskStates: Map[String, TaskState],
    createIndex: Int,
    modifyIndex: Int,
    createTime: Long,
    modifyTime: Long
  )

  object Alloc {

    implicit val decoderForAllocation = new Decoder[Alloc] {
      def apply(c: HCursor): Decoder.Result[Alloc] =
        (
          c.downField("ID").as[String],
          c.downField("EvalID").as[String],
          c.downField("Name").as[String],
          c.downField("NodeID").as[String],
          c.downField("PreviousAllocation").as[Option[String]],
          c.downField("NextAllocation").as[Option[String]],
          c.downField("RescheduleTracker").as[Option[RescheduleTracker]],
          c.downField("JobID").as[String],
          c.downField("TaskGroup").as[String],
          c.downField("DesiredStatus").as[String],
          c.downField("DesiredDescription").as[String],
          c.downField("ClientStatus").as[String],
          c.downField("ClientDescription").as[String],
          c.downField("TaskStates").as[Map[String, TaskState]],
          c.downField("CreateIndex").as[Int],
          c.downField("ModifyIndex").as[Int],
          c.downField("CreateTime").as[Long],
          c.downField("ModifyTime").as[Long]
        ).mapN(Alloc.apply)
    }
  }
}
