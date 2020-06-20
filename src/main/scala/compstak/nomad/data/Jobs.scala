package compstak.nomad.data

import cats.implicits._

import compstak.nomad.data.jobs._

import io.circe._
import io.circe.syntax._
import compstak.nomad.data.jobs.Driver.RawExec

object Jobs {

  final case class PortDef(
    label: String,
    value: Int
  )
  object PortDef {

    implicit val decoderForPortDef = new Decoder[PortDef] {
      def apply(c: HCursor): Decoder.Result[PortDef] =
        (
          c.downField("Label").as[String],
          c.downField("Value").as[Int]
        ).mapN(PortDef.apply)
    }

    implicit val encoderForPortDef = new Encoder[PortDef] {
      def apply(a: PortDef): Json =
        Json
          .obj(
            "Label" -> a.label.asJson,
            "Value" -> a.value.asJson
          )
          .dropNullValues
    }
  }

  sealed trait JobType
  object JobType {

    final case object Batch extends JobType
    final case object Service extends JobType
    final case object System extends JobType

    implicit val decoderForJobType = new Decoder[JobType] {
      def apply(c: HCursor): Decoder.Result[JobType] =
        c.as[String].flatMap {
          case "batch"   => Batch.asRight
          case "service" => Service.asRight
          case "system"  => System.asRight
          case value     => Left(DecodingFailure(s"Invalid job type $value", c.history))
        }
    }

    implicit val encoderForJobType = new Encoder[JobType] {
      def apply(a: JobType): Json = a match {
        case Batch   => "batch".asJson
        case Service => "service".asJson
        case System  => "system".asJson
      }
    }
  }

  sealed trait CheckType
  object CheckType {

    final case object HTTP extends CheckType
    final case object Script extends CheckType
    final case object TCP extends CheckType

    implicit val decoderForCheckType = new Decoder[CheckType] {
      def apply(c: HCursor): Decoder.Result[CheckType] =
        c.as[String].flatMap {
          case "http"   => HTTP.asRight
          case "script" => Script.asRight
          case "tcp"    => TCP.asRight
          case value    => Left(DecodingFailure(s"Invalid check type $value", c.history))
        }
    }

    implicit val encoderForCheckType = new Encoder[CheckType] {
      def apply(a: CheckType): Json = a match {
        case HTTP   => "http".asJson
        case Script => "script".asJson
        case TCP    => "tcp".asJson
      }
    }
  }

  final case class Check(
    id: Option[String],
    name: String,
    `type`: CheckType,
    command: Option[String],
    args: Option[String],
    path: String,
    protocol: String,
    portLabel: String,
    interval: Long,
    timeout: Long,
    initialStatus: Option[String],
    tlsSkipVerify: Boolean,
    addressMode: Option[String]
  )

  object Check {

    implicit val decoderForCheck = new Decoder[Check] {
      def apply(c: HCursor): Decoder.Result[Check] =
        (
          c.downField("Id").as[Option[String]].map {
            case Some(value) if value.isEmpty => None
            case Some(value)                  => Some(value)
            case None                         => None
          },
          c.downField("Name").as[String],
          c.downField("Type").as[CheckType],
          c.downField("Command").as[Option[String]].map {
            case Some(value) if value.isEmpty => None
            case Some(value)                  => Some(value)
            case None                         => None
          },
          c.downField("Args").as[Option[String]],
          c.downField("Path").as[String],
          c.downField("Protocol").as[String],
          c.downField("PortLabel").as[String],
          c.downField("Interval").as[Long],
          c.downField("Timeout").as[Long],
          c.downField("InitialStatus").as[Option[String]].map {
            case Some(value) if value.isEmpty => None
            case Some(value)                  => Some(value)
            case None                         => None
          },
          c.downField("TLSSkipVerify").as[Boolean],
          c.downField("AddressMode").as[Option[String]].map {
            case Some(value) if value.isEmpty => None
            case Some(value)                  => Some(value)
            case None                         => None
          }
        ).mapN(Check.apply)
    }

    implicit val encoderForCheck = new Encoder[Check] {
      def apply(a: Check): Json =
        Json
          .obj(
            "Id" -> a.id.asJson,
            "Name" -> a.name.asJson,
            "Type" -> a.`type`.asJson,
            "Command" -> a.command.asJson,
            "Args" -> a.args.asJson,
            "Path" -> a.path.asJson,
            "Protocol" -> a.protocol.asJson,
            "PortLabel" -> a.portLabel.asJson,
            "Interval" -> a.interval.asJson,
            "Timeout" -> a.timeout.asJson,
            "InitialStatus" -> a.initialStatus.asJson,
            "TLSSkipVerify" -> a.tlsSkipVerify.asJson,
            "AddressMode" -> a.addressMode.asJson
          )
          .dropNullValues
    }
  }

  final case class Service(
    id: Option[String],
    name: String,
    tags: List[String],
    meta: Option[Json],
    portLabel: String,
    addressMode: String,
    checks: List[Check]
  )
  object Service {

    implicit val decoderForService = new Decoder[Service] {
      def apply(c: HCursor): Decoder.Result[Service] =
        (
          c.downField("Id").as[Option[String]].map {
            case Some(value) if value.isEmpty => None
            case Some(value)                  => Some(value)
            case None                         => None
          },
          c.downField("Name").as[String],
          c.downField("Tags").as[Option[List[String]]].map(_.getOrElse(List.empty)),
          c.downField("Meta").as[Option[Json]],
          c.downField("PortLabel").as[String],
          c.downField("AddressMode").as[String],
          c.downField("Checks").as[Option[List[Check]]].map(_.getOrElse(List.empty))
        ).mapN(Service.apply)
    }

    implicit val encoderForService = new Encoder[Service] {
      def apply(a: Service): Json =
        Json
          .obj(
            "Id" -> a.id.asJson,
            "Name" -> a.name.asJson,
            "Tags" -> a.tags.asJson,
            "Meta" -> a.meta.asJson,
            "PortLabel" -> a.portLabel.asJson,
            "AddressMode" -> a.addressMode.asJson,
            "Checks" -> a.checks.asJson
          )
          .dropNullValues
    }
  }

  final case class Network(
    device: Option[String],
    cidr: Option[String],
    ip: Option[String],
    mBits: Int,
    dynamicPorts: List[PortDef],
    reservedPorts: List[PortDef]
  ) {
    def ports: List[PortDef] = dynamicPorts |+| reservedPorts
  }

  object Network {

    implicit val decoderForNetwork = new Decoder[Network] {
      def apply(c: HCursor): Decoder.Result[Network] =
        (
          c.downField("Device").as[Option[String]].map {
            case Some(value) if value.isEmpty => None
            case Some(value)                  => Some(value)
            case None                         => None
          },
          c.downField("CIDR").as[Option[String]].map {
            case Some(value) if value.isEmpty => None
            case Some(value)                  => Some(value)
            case None                         => None
          },
          c.downField("IP").as[Option[String]].map {
            case Some(value) if value.isEmpty => None
            case Some(value)                  => Some(value)
            case None                         => None
          },
          c.downField("MBits").as[Int],
          c.downField("DynamicPorts").as[Option[List[PortDef]]].map(_.getOrElse(List.empty)),
          c.downField("ReservedPorts").as[Option[List[PortDef]]].map(_.getOrElse(List.empty))
        ).mapN(Network.apply)
    }

    implicit val encoderForNetwork = new Encoder[Network] {
      def apply(a: Network): Json =
        Json
          .obj(
            "Device" -> a.device.asJson,
            "CIDR" -> a.cidr.asJson,
            "IP" -> a.ip.asJson,
            "MBits" -> a.mBits.asJson,
            "DynamicPorts" -> a.dynamicPorts.asJson,
            "ReservedPorts" -> a.reservedPorts.asJson
          )
          .dropNullValues
    }
  }

  final case class Resources(
    cpu: Int,
    memoryMb: Int,
    networks: Option[List[Network]]
  )
  object Resources {

    implicit val decoderForResources = new Decoder[Resources] {
      def apply(c: HCursor): Decoder.Result[Resources] =
        (
          c.downField("CPU").as[Int],
          c.downField("MemoryMB").as[Int],
          c.downField("Networks").as[Option[List[Network]]]
        ).mapN(Resources.apply)
    }

    implicit val encoderForResources = new Encoder[Resources] {
      def apply(a: Resources): Json =
        Json
          .obj(
            "CPU" -> a.cpu.asJson,
            "MemoryMB" -> a.memoryMb.asJson,
            "Networks" -> a.networks.asJson
          )
          .dropNullValues
    }
  }

  final case class Template(
    changeMode: String,
    changeSignal: Option[String],
    destination: String,
    embedded: Option[String],
    envvars: Option[Boolean],
    leftDelimiter: String,
    rightDelimiter: String,
    permissions: String,
    sourcePath: Option[String],
    splay: Long,
    vaultGrace: Long
  )

  object Template {

    implicit val decoderForTemplate = new Decoder[Template] {
      def apply(c: HCursor): Decoder.Result[Template] =
        (
          c.downField("ChangeMode").as[String],
          c.downField("ChangeSignal").as[Option[String]].map {
            case Some(value) if value.isEmpty => None
            case Some(value)                  => Some(value)
            case None                         => None
          },
          c.downField("DestPath").as[String],
          c.downField("EmbeddedTmpl").as[Option[String]].map {
            case Some(value) if value.isEmpty => None
            case Some(value)                  => Some(value)
            case None                         => None
          },
          c.downField("Envvars").as[Option[Boolean]],
          c.downField("LeftDelim").as[String],
          c.downField("RightDelim").as[String],
          c.downField("Perms").as[String],
          c.downField("SourcePath").as[Option[String]].map {
            case Some(value) if value.isEmpty => None
            case Some(value)                  => Some(value)
            case None                         => None
          },
          c.downField("Splay").as[Long],
          c.downField("VaultGrace").as[Long]
        ).mapN(Template.apply)
    }

    implicit val encoderForTemplate = new Encoder[Template] {
      def apply(a: Template): Json =
        Json
          .obj(
            "ChangeMode" -> a.changeMode.asJson,
            "ChangeSignal" -> a.changeSignal.asJson,
            "DestPath" -> a.destination.asJson,
            "EmbeddedTmpl" -> a.embedded.asJson,
            "Envvars" -> a.envvars.asJson,
            "LeftDelim" -> a.leftDelimiter.asJson,
            "RightDelim" -> a.rightDelimiter.asJson,
            "Perms" -> a.permissions.asJson,
            "SourcePath" -> a.sourcePath.asJson,
            "Splay" -> a.splay.asJson,
            "VaultGrace" -> a.vaultGrace.asJson
          )
          .dropNullValues
    }
  }

  final case class VaultConfig(
    changeMode: String,
    changeSignal: String,
    env: Boolean,
    policies: List[String]
  )

  object VaultConfig {

    implicit val decoderForVaultConfig = new Decoder[VaultConfig] {
      def apply(c: HCursor): Decoder.Result[VaultConfig] =
        (
          c.downField("ChangeMode").as[String],
          c.downField("ChangeSignal").as[String],
          c.downField("Env").as[Boolean],
          c.downField("Policies").as[List[String]]
        ).mapN(VaultConfig.apply)
    }

    implicit val encoderForVaultConfig = new Encoder[VaultConfig] {
      def apply(a: VaultConfig): Json =
        Json
          .obj(
            "ChangeMode" -> a.changeMode.asJson,
            "ChangeSignal" -> a.changeSignal.asJson,
            "Env" -> a.env.asJson,
            "Policies" -> a.policies.asJson
          )
          .dropNullValues
    }
  }

  final case class Task(
    name: String,
    driver: Driver,
    user: Option[String],
    config: DriverConfig,
    services: List[Service],
    resources: Resources,
    leader: Option[Boolean],
    templates: Option[List[Template]],
    vault: Option[VaultConfig],
    env: Option[Map[String, String]]
  )

  object Task {

    implicit val decoderForTask = new Decoder[Task] {
      def apply(c: HCursor): Decoder.Result[Task] =
        (
          c.downField("Name").as[String],
          c.downField("Driver").as[Driver],
          c.downField("User").as[Option[String]].map {
            case Some(value) if value.isEmpty => None
            case Some(value)                  => Some(value)
            case None                         => None
          },
          c.downField("Driver").as[Driver].flatMap { driver =>
            val cursor = c.downField("Config")
            driver match {
              case Driver.Docker  => cursor.as[DockerConfig]
              case Driver.Exec    => cursor.as[ExecConfig]
              case Driver.RawExec => cursor.as[ExecConfig]
              case Driver.Java    => cursor.as[JavaConfig]
              case d              => Left(DecodingFailure(s"Driver $d not currently supported", cursor.history))
            }
          }: Decoder.Result[DriverConfig],
          c.downField("Services").as[Option[List[Service]]].map(_.getOrElse(List.empty)),
          c.downField("Resources").as[Resources],
          c.downField("Leader").as[Option[Boolean]],
          c.downField("Templates").as[Option[List[Template]]].map {
            case Some(value) if value.isEmpty => None
            case Some(value)                  => Some(value)
            case None                         => None
          },
          c.downField("Vault").as[Option[VaultConfig]],
          c.downField("Env").as[Option[Map[String, String]]].map {
            case Some(value) if value.isEmpty => None
            case Some(value)                  => Some(value)
            case None                         => None
          }
        ).mapN(Task.apply)
    }

    implicit val encoderForTask = new Encoder[Task] {
      def apply(a: Task): Json =
        Json
          .obj(
            "Name" -> a.name.asJson,
            "Driver" -> a.driver.asJson,
            "User" -> a.user.asJson,
            "Config" -> a.config.asJson,
            "Services" -> a.services.asJson,
            "Resources" -> a.resources.asJson,
            "Leader" -> a.leader.asJson,
            "Templates" -> a.templates.asJson,
            "Vault" -> a.vault.asJson,
            "Env" -> a.env.asJson
          )
          .dropNullValues
    }
  }

  final case class RestartPolicy(
    interval: Long,
    attempts: Int,
    delay: Long,
    mode: String
  )

  object RestartPolicy {

    implicit val decoderForRestartPolicy = new Decoder[RestartPolicy] {
      def apply(c: HCursor): Decoder.Result[RestartPolicy] =
        (
          c.downField("Interval").as[Long],
          c.downField("Attempts").as[Int],
          c.downField("Delay").as[Long],
          c.downField("Mode").as[String]
        ).mapN(RestartPolicy.apply)
    }

    implicit val encoderForRestartPolicy = new Encoder[RestartPolicy] {
      def apply(a: RestartPolicy): Json =
        Json
          .obj(
            "Interval" -> a.interval.asJson,
            "Attempts" -> a.attempts.asJson,
            "Delay" -> a.delay.asJson,
            "Mode" -> a.mode.asJson
          )
          .dropNullValues
    }
  }

  final case class ReschedulePolicy(
    attempts: Int,
    delay: Long,
    delayFunction: String,
    interval: Long,
    maxDelay: Long,
    unlimited: Boolean
  )

  object ReschedulePolicy {

    implicit val decoderForReschedulePolicy = new Decoder[ReschedulePolicy] {
      def apply(c: HCursor): Decoder.Result[ReschedulePolicy] =
        (
          c.downField("Attempts").as[Int],
          c.downField("Delay").as[Long],
          c.downField("DelayFunction").as[String],
          c.downField("Interval").as[Long],
          c.downField("MaxDelay").as[Long],
          c.downField("Unlimited").as[Boolean]
        ).mapN(ReschedulePolicy.apply)
    }

    implicit val encoderForReschedulePolicy = new Encoder[ReschedulePolicy] {
      def apply(a: ReschedulePolicy): Json =
        Json
          .obj(
            "Attempts" -> a.attempts.asJson,
            "Delay" -> a.delay.asJson,
            "DelayFunction" -> a.delayFunction.asJson,
            "MaxDelay" -> a.maxDelay.asJson,
            "Unlimited" -> a.unlimited.asJson
          )
          .dropNullValues
    }
  }

  final case class EphemeralDisk(
    sizeMb: Long
  )

  object EphemeralDisk {

    implicit val decoderForEphemeralDisk = new Decoder[EphemeralDisk] {
      def apply(c: HCursor): Decoder.Result[EphemeralDisk] =
        c.downField("SizeMB").as[Long].map(EphemeralDisk.apply)
    }

    implicit val encoderForEphemeralDisk = new Encoder[EphemeralDisk] {
      def apply(a: EphemeralDisk): Json = Json.obj(
        "SizeMB" -> a.sizeMb.asJson
      )
    }
  }

  final case class TaskGroup(
    name: String,
    count: Int,
    tasks: List[Task],
    restartPolicy: RestartPolicy,
    reschedulePolicy: ReschedulePolicy,
    ephemeralDisk: EphemeralDisk
  )

  object TaskGroup {

    implicit val decoderForTaskGroup = new Decoder[TaskGroup] {
      def apply(c: HCursor): Decoder.Result[TaskGroup] =
        (
          c.downField("Name").as[String],
          c.downField("Count").as[Int],
          c.downField("Tasks").as[List[Task]],
          c.downField("RestartPolicy").as[RestartPolicy],
          c.downField("ReschedulePolicy").as[ReschedulePolicy],
          c.downField("EphemeralDisk").as[EphemeralDisk]
        ).mapN(TaskGroup.apply)
    }

    implicit val encoderForTaskGroup = new Encoder[TaskGroup] {
      def apply(a: TaskGroup): Json =
        Json
          .obj(
            "Name" -> a.name.asJson,
            "Count" -> a.count.asJson,
            "Tasks" -> a.tasks.asJson,
            "RestartPolicy" -> a.restartPolicy.asJson,
            "ReschedulePolicy" -> a.reschedulePolicy.asJson,
            "EphemeralDisk" -> a.ephemeralDisk.asJson
          )
          .dropNullValues
    }
  }

  final case class Update(
    maxParallel: Long,
    minHealthyTime: Long,
    healthyDeadline: Long,
    autoRevert: Boolean,
    canary: Long
  )

  object Update {

    implicit val decoderForUpdate = new Decoder[Update] {
      def apply(c: HCursor): Decoder.Result[Update] =
        (
          c.downField("MaxParallel").as[Long],
          c.downField("MinHealthyTime").as[Long],
          c.downField("HealthyDeadline").as[Long],
          c.downField("AutoRevert").as[Boolean],
          c.downField("Canary").as[Long]
        ).mapN(Update.apply)
    }

    implicit val encoderForUpdate = new Encoder[Update] {
      def apply(a: Update): Json =
        Json
          .obj(
            "MaxParallel" -> a.maxParallel.asJson,
            "MinHealthyTime" -> a.minHealthyTime.asJson,
            "HealthyDeadline" -> a.healthyDeadline.asJson,
            "AutoRevert" -> a.autoRevert.asJson,
            "Canary" -> a.canary.asJson
          )
          .dropNullValues
    }
  }

  final case class Parameterized(
    metaOptional: Option[List[String]],
    metaRequired: Option[List[String]],
    payload: Option[String]
  )

  object Parameterized {

    implicit val decoderForParameterized = new Decoder[Parameterized] {
      def apply(c: HCursor): Decoder.Result[Parameterized] =
        (
          c.downField("MetaOptional").as[Option[List[String]]],
          c.downField("MetaRequired").as[Option[List[String]]],
          c.downField("Payload").as[Option[String]]
        ).mapN(Parameterized.apply)
    }

    implicit val encoderForParameterized = new Encoder[Parameterized] {
      def apply(a: Parameterized): Json =
        Json
          .obj(
            "MetaOptional" -> a.metaOptional.asJson,
            "MetaRequired" -> a.metaRequired.asJson,
            "Payload" -> a.payload.asJson
          )
          .dropNullValues
    }
  }

  final case class Constraint(
    leftTarget: String,
    operand: String,
    rightTarget: String
  )

  object Constraint {
    implicit val decoderForConstraint = new Decoder[Constraint] {
      def apply(c: HCursor): Decoder.Result[Constraint] =
        (
          c.downField("LTarget").as[String],
          c.downField("Operand").as[String],
          c.downField("RTarget").as[String]
        ).mapN(Constraint.apply)
    }
    implicit val encoderForConstraint = new Encoder[Constraint] {
      def apply(a: Constraint): Json =
        Json
          .obj(
            "LTarget" -> a.leftTarget.asJson,
            "Operand" -> a.operand.asJson,
            "RTarget" -> a.rightTarget.asJson
          )
          .dropNullValues
    }
  }

  final case class Job(
    id: String,
    name: String,
    `type`: JobType,
    priority: Int,
    datacenters: List[String],
    taskGroups: List[TaskGroup],
    update: Option[Update],
    parameterized: Option[Parameterized],
    constraints: Option[List[Constraint]]
  )

  object Job {

    implicit val decoderForJob = new Decoder[Job] {
      def apply(c: HCursor): Decoder.Result[Job] =
        (
          c.downField("ID").as[String],
          c.downField("Name").as[String],
          c.downField("Type").as[JobType],
          c.downField("Priority").as[Int],
          c.downField("Datacenters").as[List[String]],
          c.downField("TaskGroups").as[List[TaskGroup]],
          c.downField("Update").as[Option[Update]],
          c.downField("ParameterizedJob").as[Option[Parameterized]],
          c.downField("Constraints").as[Option[List[Constraint]]]
        ).mapN(Job.apply)
    }

    implicit val encoderForJob = new Encoder[Job] {
      def apply(a: Job): Json =
        Json
          .obj(
            "ID" -> a.id.asJson,
            "Name" -> a.name.asJson,
            "Type" -> a.`type`.asJson,
            "Priority" -> a.priority.asJson,
            "Datacenters" -> a.datacenters.asJson,
            "TaskGroups" -> a.taskGroups.asJson,
            "Update" -> a.update.asJson,
            "ParameterizedJob" -> a.parameterized.asJson,
            "Constraints" -> a.constraints.asJson
          )
          .dropNullValues
    }
  }

  case class DispatchedJob(
    id: String,
    evaluation: String,
    evalCreateIndex: Int,
    jobCreateIndex: Int,
    index: Int
  )

  object DispatchedJob {

    implicit val decoderForDispatchedJob = new Decoder[DispatchedJob] {
      def apply(c: HCursor): Decoder.Result[DispatchedJob] =
        (
          c.downField("DispatchedJobID").as[String],
          c.downField("EvalID").as[String],
          c.downField("EvalCreateIndex").as[Int],
          c.downField("JobCreateIndex").as[Int],
          c.downField("Index").as[Int]
        ).mapN(DispatchedJob.apply)
    }

    implicit val encoderForDispatchedJob = new Encoder[DispatchedJob] {
      def apply(a: DispatchedJob): Json =
        Json
          .obj(
            "DispatchedJobID" -> a.id.asJson,
            "EvalID" -> a.evaluation.asJson,
            "EvalCreateIndex" -> a.evalCreateIndex.asJson,
            "JobCreateIndex" -> a.jobCreateIndex.asJson,
            "Index" -> a.index.asJson
          )
          .dropNullValues
    }
  }

  case class TaskSummary(
    queued: Int,
    complete: Int,
    failed: Int,
    running: Int,
    starting: Int,
    lost: Int
  )

  object TaskSummary {

    implicit val decoderForTaskSummary = new Decoder[TaskSummary] {
      def apply(c: HCursor): Decoder.Result[TaskSummary] =
        (
          c.downField("Queued").as[Int],
          c.downField("Complete").as[Int],
          c.downField("Failed").as[Int],
          c.downField("Running").as[Int],
          c.downField("Starting").as[Int],
          c.downField("Lost").as[Int]
        ).mapN(TaskSummary.apply)
    }
  }

  case class Children(
    pending: Int,
    running: Int,
    dead: Int
  )

  object Children {

    implicit val decoderForChildren = new Decoder[Children] {
      def apply(c: HCursor): Decoder.Result[Children] =
        (
          c.downField("Pending").as[Int],
          c.downField("Running").as[Int],
          c.downField("Dead").as[Int]
        ).mapN(Children.apply)
    }
  }

  case class Summary(
    jobId: String,
    summary: Map[String, TaskSummary],
    children: Option[Children],
    createIndex: Int,
    modifyIndex: Int,
    namespace: String
  )

  object Summary {

    implicit val decoderForSummary = new Decoder[Summary] {
      def apply(c: HCursor): Decoder.Result[Summary] =
        (
          c.downField("JobID").as[String],
          c.downField("Summary").as[Map[String, TaskSummary]],
          c.downField("Children").as[Option[Children]],
          c.downField("CreateIndex").as[Int],
          c.downField("ModifyIndex").as[Int],
          c.downField("Namespace").as[String]
        ).mapN(Summary.apply)
    }
  }

  final case class Validation(
    driverConfigValidated: Boolean,
    validationErrors: List[String],
    warnings: String,
    error: String
  )

  object Validation {

    implicit val decoderForValidation = new Decoder[Validation] {
      def apply(c: HCursor): Decoder.Result[Validation] =
        (
          c.downField("DriverConfigValidated").as[Boolean],
          c.downField("ValidationErrors").as[List[String]],
          c.downField("Warnings").as[String],
          c.downField("Error").as[String]
        ).mapN(Validation.apply)
    }
  }

  final case class CreateJob(job: Job)

  object CreateJob {

    implicit val encoderForCreateJob = new Encoder[CreateJob] {
      def apply(a: CreateJob): Json = Json.obj("Job" -> a.job.asJson)
    }
  }

  final case class CreatedJob(
    index: Long,
    evalId: String,
    evalCreateIndex: Long,
    jobModifyIndex: Long,
    warnings: Option[String],
    lastContact: Long,
    knownLeader: Boolean
  )

  object CreatedJob {

    implicit val decoderForCreatedJob = new Decoder[CreatedJob] {
      def apply(c: HCursor): Decoder.Result[CreatedJob] =
        (
          c.downField("Index").as[Long],
          c.downField("EvalID").as[String],
          c.downField("EvalCreateIndex").as[Long],
          c.downField("JobModifyIndex").as[Long],
          c.downField("Warnings").as[Option[String]],
          c.downField("LastContact").as[Long],
          c.downField("KnownLeader").as[Boolean]
        ).mapN(CreatedJob.apply)
    }
  }

  final case class DispatchJob(
    payload: String,
    meta: Json
  )

  object DispatchJob {

    implicit val encoderForDispatchJob = new Encoder[DispatchJob] {
      def apply(a: DispatchJob): Json = Json.obj(
        "Payload" -> a.payload.asJson,
        "Meta" -> a.meta
      )
    }
  }

  final case class ParseJob(
    hcl: String,
    canonicalize: Boolean
  )

  object ParseJob {

    implicit val encoderForParseJob = new Encoder[ParseJob] {
      def apply(a: ParseJob): Json =
        Json
          .obj(
            "JobHCL" -> a.hcl.asJson,
            "Canonicalize" -> a.canonicalize.asJson
          )
          .dropNullValues
    }
  }

  final case class PlanJob(
    job: Job,
    diff: Option[Boolean],
    policyOverride: Option[Boolean]
  )

  object PlanJob {

    implicit val encoderForPlanJob = new Encoder[PlanJob] {
      def apply(a: PlanJob): Json =
        Json
          .obj(
            "Job" -> a.job.asJson,
            "Diff" -> a.diff.asJson,
            "PolicyOverride" -> a.policyOverride.asJson
          )
          .dropNullValues
    }
  }

  final case class RevertJob(
    id: String,
    version: Long,
    enforcePriorVersion: Option[Long],
    vaultToken: Option[String]
  )

  object RevertJob {

    implicit val encoderForRevertJob = new Encoder[RevertJob] {
      def apply(a: RevertJob): Json =
        Json
          .obj(
            "JobID" -> a.id.asJson,
            "JobVersion" -> a.version.asJson,
            "EnforcePriorVersion" -> a.enforcePriorVersion.asJson,
            "VaultToken" -> a.vaultToken.asJson
          )
          .dropNullValues
    }
  }

  final case class Listing(
    id: String,
    parent: Option[String],
    name: String,
    datacenters: List[String],
    `type`: JobType,
    priority: Int,
    parameterized: Boolean,
    stop: Boolean,
    status: String,
    summary: Summary,
    createIndex: Long,
    modifyIndex: Long,
    jobModifyIndex: Long,
    submitTime: Long
  )

  object Listing {

    implicit val decoderForListing = new Decoder[Listing] {
      def apply(c: HCursor): Decoder.Result[Listing] =
        (
          c.downField("ID").as[String],
          c.downField("ParentID").as[Option[String]],
          c.downField("Name").as[String],
          c.downField("Datacenters").as[List[String]],
          c.downField("Type").as[JobType],
          c.downField("Priority").as[Int],
          c.downField("ParameterizedJob").as[Boolean],
          c.downField("Stop").as[Boolean],
          c.downField("Status").as[String],
          c.downField("JobSummary").as[Summary],
          c.downField("CreateIndex").as[Long],
          c.downField("ModifyIndex").as[Long],
          c.downField("JobModifyIndex").as[Long],
          c.downField("SubmitTime").as[Long]
        ).mapN(Listing.apply)
    }
  }

  final case class Stopped(
    evaluation: String,
    evalCreateIndex: Long,
    jobModifyIndex: Long,
    index: Long,
    lastContact: Long,
    knownLeader: Boolean
  )

  object Stopped {

    implicit val decoderForStopped = new Decoder[Stopped] {
      def apply(c: HCursor): Decoder.Result[Stopped] =
        (
          c.downField("EvalID").as[String],
          c.downField("EvalCreateIndex").as[Long],
          c.downField("JobModifyIndex").as[Long],
          c.downField("Index").as[Long],
          c.downField("LastContact").as[Long],
          c.downField("KnownLeader").as[Boolean]
        ).mapN(Stopped.apply)
    }
  }

  final case class VersionSummary(
    diffs: Option[String],
    index: Int,
    knownLeader: Boolean,
    lastContact: Int,
    versions: List[Job]
  )

  object VersionSummary {

    implicit val decoderForVersionSummary = new Decoder[VersionSummary] {
      def apply(c: HCursor): Decoder.Result[VersionSummary] =
        (
          c.downField("Diffs").as[Option[String]],
          c.downField("Index").as[Int],
          c.downField("KnownLeader").as[Boolean],
          c.downField("LastContact").as[Int],
          c.downField("Versions").as[List[Job]]
        ).mapN(VersionSummary.apply)
    }
  }
}
