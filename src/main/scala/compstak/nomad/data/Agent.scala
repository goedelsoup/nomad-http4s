package compstak.nomad.data

import java.time.Duration
import java.{util => ju}

import cats.implicits._
import io.circe._

object Agent {

  final case class MemberInfo(
    name: String,
    address: String,
    port: Int,
    tags: Map[String, String],
    status: String,
    protocolMin: Int,
    protocolMax: Int,
    protocol: Int,
    delegateMin: Int,
    delegateMax: Int,
    delegate: Int
  )

  object MemberInfo {

    implicit val decoderForMemberInfo = new Decoder[MemberInfo] {
      def apply(c: HCursor): Decoder.Result[MemberInfo] =
        (
          c.downField("Name").as[String],
          c.downField("Addr").as[String],
          c.downField("Port").as[Int],
          c.downField("Tags").as[Map[String, String]],
          c.downField("Status").as[String],
          c.downField("ProtocolMin").as[Int],
          c.downField("ProtocolMax").as[Int],
          c.downField("ProtocolCur").as[Int],
          c.downField("DelegateMin").as[Int],
          c.downField("DelegateMax").as[Int],
          c.downField("DelegateCur").as[Int]
        ).mapN(MemberInfo.apply)
    }
  }

  final case class Members(
    serverName: String,
    serverRegion: String,
    serverDatacenter: String,
    members: List[MemberInfo]
  )

  object Members {

    implicit val decoderForMembers = new Decoder[Members] {
      def apply(c: HCursor): Decoder.Result[Members] =
        (
          c.downField("ServerName").as[String],
          c.downField("ServerRegion").as[String],
          c.downField("ServerDC").as[String],
          c.downField("Members").as[List[MemberInfo]]
        ).mapN(Members.apply)
    }
  }

  final case class Stats(
    runtime: Stats.Runtime,
    nomad: Stats.Nomad,
    raft: Stats.Raft,
    client: Stats.Client,
    serf: Stats.Serf
  )

  object Stats {

    case class Runtime(
      cpuCount: Int,
      kernelName: String,
      architecture: String,
      goVersion: String,
      maxProcs: Int,
      goRoutines: Int
    )

    object Runtime {

      implicit val decoderForRuntime = new Decoder[Runtime] {
        def apply(c: HCursor): Decoder.Result[Runtime] =
          (
            c.downField("cpu_count").as[Int],
            c.downField("kernel.name").as[String],
            c.downField("arch").as[String],
            c.downField("version").as[String],
            c.downField("max_procs").as[Int],
            c.downField("goroutines").as[Int]
          ).mapN(Runtime.apply)
      }
    }

    case class Nomad(
      server: Boolean,
      leader: Boolean,
      leaderAddress: String,
      bootstrap: Boolean,
      knownRegions: Int
    )

    object Nomad {

      implicit val decoderForNomad = new Decoder[Nomad] {
        def apply(c: HCursor): Decoder.Result[Nomad] =
          (
            boolFromStringCursor(c.downField("server")),
            boolFromStringCursor(c.downField("leader")),
            c.downField("leader_addr").as[String],
            boolFromStringCursor(c.downField("bootstrap")),
            c.downField("known_regions").as[Int]
          ).mapN(Nomad.apply)
      }
    }

    case class Raft(
      numberOfPeers: Int,
      fsmPending: Int,
      lastSnapshotIndex: Int,
      lastLogTerm: Int,
      commitIndex: Int,
      protocolVersionMax: Int,
      snapshotVersionMax: Int,
      latestConfigurationIndex: Int,
      latestConfiguration: String,
      lastContact: String,
      appliedIndex: Int,
      protocolVersion: Int,
      protocolVersionMin: Int,
      snapshotVersionMin: Int,
      state: String,
      lastSnapshotTerm: Int
    )

    object Raft {

      implicit val decoderForRaft = new Decoder[Raft] {
        def apply(c: HCursor): Decoder.Result[Raft] =
          (
            c.downField("num_peers").as[Int],
            c.downField("fsm_pending").as[Int],
            c.downField("last_snapshot_index").as[Int],
            c.downField("last_log_term").as[Int],
            c.downField("commit_index").as[Int],
            c.downField("protocol_version_max").as[Int],
            c.downField("snapshot_version_max").as[Int],
            c.downField("latest_configuration_index").as[Int],
            c.downField("latest_configuration").as[String],
            c.downField("last_contact").as[String],
            c.downField("applied_index").as[Int],
            c.downField("protocol_version").as[Int],
            c.downField("protocol_version_min").as[Int],
            c.downField("snapshot_version_min").as[Int],
            c.downField("state").as[String],
            c.downField("last_snapshot_term").as[Int]
          ).mapN(Raft.apply)
      }
    }

    case class Client(
      heartbeatTtl: Duration,
      nodeId: ju.UUID,
      knownServers: String,
      numberOfAllocations: Int,
      lastHeartbeat: Duration
    )

    object Client {

      implicit val decoderForClient = new Decoder[Client] {
        def apply(c: HCursor): Decoder.Result[Client] =
          (
            durationFromStringCursor(c.downField("heartbeat_ttl")),
            c.downField("node_id").as[ju.UUID],
            c.downField("known_servers").as[String],
            c.downField("num_allocations").as[Int],
            durationFromStringCursor(c.downField("last_heartbeat"))
          ).mapN(Client.apply)
      }
    }

    case class Serf(
      eventTime: Int,
      eventQuery: Int,
      encrypted: Boolean,
      memberTime: Int,
      queryTime: Int,
      intentQueue: Int,
      queryQueue: Int,
      members: Int,
      failed: Int,
      left: Int,
      healthScore: Int
    )

    object Serf {

      implicit val decoderForSerf = new Decoder[Serf] {
        def apply(c: HCursor): Decoder.Result[Serf] =
          (
            c.downField("event_time").as[Int],
            c.downField("event_queue").as[Int],
            boolFromStringCursor(c.downField("encrypted")),
            c.downField("member_time").as[Int],
            c.downField("query_time").as[Int],
            c.downField("intent_queue").as[Int],
            c.downField("query_queue").as[Int],
            c.downField("members").as[Int],
            c.downField("failed").as[Int],
            c.downField("left").as[Int],
            c.downField("health_score").as[Int]
          ).mapN(Serf.apply)
      }
    }

    implicit val decoderForStats = new Decoder[Stats] {
      def apply(c: HCursor): Decoder.Result[Stats] =
        (
          c.downField("runtime").as[Runtime],
          c.downField("nomad").as[Nomad],
          c.downField("raft").as[Raft],
          c.downField("client").as[Client],
          c.downField("serf").as[Serf]
        ).mapN(Stats.apply)
    }
  }

  final case class Info(
    // todo implement config map
    member: MemberInfo,
    stats: Stats
  )

  object Info {

    implicit val decoderForInfo = new Decoder[Info] {
      def apply(c: HCursor): Decoder.Result[Info] =
        (
          c.downField("member").as[MemberInfo],
          c.downField("stats").as[Stats]
        ).mapN(Info.apply)
    }
  }

  final case class Health(
    serverHealthy: Boolean,
    serverMessage: String,
    clientHealthy: Boolean,
    clientMessage: String
  )

  object Health {

    implicit val decoderForHealth = new Decoder[Health] {
      def apply(c: HCursor): Decoder.Result[Health] =
        (
          c.downField("server").downField("ok").as[Boolean],
          c.downField("server").downField("message").as[String],
          c.downField("client").downField("ok").as[Boolean],
          c.downField("client").downField("message").as[String]
        ).mapN(Health.apply)
    }
  }
}
