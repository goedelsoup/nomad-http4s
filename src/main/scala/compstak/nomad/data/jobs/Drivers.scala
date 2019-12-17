package compstak.nomad.data
package jobs

import cats.implicits._

import io.circe._
import io.circe.syntax._

sealed trait Driver { val key: String }
object Driver {

  final case object Docker extends Driver { val key: String = "docker" }
  final case object Exec extends Driver { val key: String = "exec" }
  final case object Java extends Driver { val key: String = "java" }
  final case object Qemu extends Driver { val key: String = "qemu" }
  final case object RawExec extends Driver { val key: String = "raw_exec" }

  implicit val decoderForDriver = new Decoder[Driver] {
    def apply(c: HCursor): Decoder.Result[Driver] =
      c.as[String].flatMap {
        case "docker"   => Docker.asRight
        case "exec"     => Exec.asRight
        case "java"     => Java.asRight
        case "qemu"     => Qemu.asRight
        case "raw_exec" => RawExec.asRight
        case value      => Left(DecodingFailure(s"Invalid driver $value", c.history))
      }
  }

  implicit val encoderForDriver = new Encoder[Driver] {
    def apply(a: Driver): Json = a.key.asJson
  }
}

sealed trait DriverConfig

final case class DockerAuth(
  username: Option[String],
  password: Option[String],
  email: Option[String],
  serverAddress: Option[String]
)

object DockerAuth {

  implicit val decoderForDockerAuth = new Decoder[DockerAuth] {
    def apply(c: HCursor): Decoder.Result[DockerAuth] =
      (
        c.downField("username").as[Option[String]],
        c.downField("password").as[Option[String]],
        c.downField("email").as[Option[String]],
        c.downField("server_address").as[Option[String]]
      ).mapN(DockerAuth.apply)
  }

  implicit val encoderForDockerAuth = new Encoder[DockerAuth] {
    def apply(a: DockerAuth): Json = Json.obj(
      "username" -> a.username.asJson,
      "password" -> a.username.asJson,
      "email" -> a.username.asJson,
      "server_address" -> a.username.asJson
    )
  }
}

sealed trait DockerLoggingEngine
object DockerLoggingEngine {

  final case object FluentD extends DockerLoggingEngine
  final case object JsonFile extends DockerLoggingEngine

  implicit val decoderForDockerLoggingEngine = new Decoder[DockerLoggingEngine] {
    def apply(c: HCursor): Decoder.Result[DockerLoggingEngine] =
      c.as[String].flatMap {
        case "fluentd"   => FluentD.asRight
        case "json-file" => JsonFile.asRight
        case value       => Left(DecodingFailure(s"Invalid logging enging $value", c.history))
      }
  }

  implicit val encoderForDockerLoggingEngine = new Encoder[DockerLoggingEngine] {
    def apply(a: DockerLoggingEngine): Json = a match {
      case FluentD  => "fluentd".asJson
      case JsonFile => "json-file".asJson
    }
  }
}

sealed trait DockerLoggingConfig

final case class DockerLogging(
  `type`: DockerLoggingEngine,
  config: DockerLoggingConfig
)

object DockerLogging {

  implicit val decoderForDockerLogging = new Decoder[DockerLogging] {
    def apply(c: HCursor): Decoder.Result[DockerLogging] =
      (
        c.downField("type").as[DockerLoggingEngine],
        c.downField("type").as[DockerLoggingEngine].flatMap {
          case DockerLoggingEngine.FluentD  => ???
          case DockerLoggingEngine.JsonFile => ???
        }: Decoder.Result[DockerLoggingConfig]
      ).mapN(DockerLogging.apply)
  }

  implicit val encoderForDockerLogging = new Encoder[DockerLogging] {
    def apply(a: DockerLogging): Json = ???
  }
}

final case class DockerMount()

object DockerMount {

  implicit val decoderForDockerMount = new Decoder[DockerMount] {
    def apply(c: HCursor): Decoder.Result[DockerMount] = ???
  }
  implicit val encoderForDockerMount = new Encoder[DockerMount] {
    def apply(a: DockerMount): Json = ???
  }
}

final case class DockerDevice()

object DockerDevice {

  implicit val decoderForDockerDevice = new Decoder[DockerDevice] {
    def apply(c: HCursor): Decoder.Result[DockerDevice] = ???
  }
  implicit val encoderForDockerDevice = new Encoder[DockerDevice] {
    def apply(a: DockerDevice): Json = ???
  }
}

sealed trait DockerNetworkMode
object DockerNetworkMode {

  final case object Bridge extends DockerNetworkMode
  final case object Host extends DockerNetworkMode
  final case object MACVLAN extends DockerNetworkMode
  final case object NoNetwork extends DockerNetworkMode
  final case object Overlay extends DockerNetworkMode

  final case class ThirdParty(name: String) extends DockerNetworkMode

  implicit val decoderForDockerNetworkMode = new Decoder[DockerNetworkMode] {
    def apply(c: HCursor): Decoder.Result[DockerNetworkMode] =
      c.as[String].flatMap {
        case "bridge"  => Bridge.asRight
        case "host"    => Host.asRight
        case "macvlan" => MACVLAN.asRight
        case "none"    => NoNetwork.asRight
        case "overlay" => Overlay.asRight
        case name      => ThirdParty(name).asRight
      }
  }

  implicit val encoderForDockerNetworkMode = new Encoder[DockerNetworkMode] {
    def apply(a: DockerNetworkMode): Json = a match {
      case MACVLAN          => "macvlan".asJson
      case NoNetwork        => "none".asJson
      case Overlay          => "overlay".asJson
      case Bridge           => "bridge".asJson
      case Host             => "host".asJson
      case ThirdParty(name) => name.asJson
    }
  }
}

final case class DockerConfig(
  image: String,
  entrypoint: Option[List[String]],
  command: Option[String],
  args: Option[List[String]],
  auth: Option[List[DockerAuth]],
  portMap: Option[List[Map[String, Int]]],
  labels: Option[Map[String, String]],
  volumes: Option[List[String]],
  volumeDriver: Option[String], // todo ADT
  mounts: Option[List[DockerMount]],
  devices: Option[List[DockerDevice]],
  dnsSearchDomains: Option[List[String]],
  dnsServers: Option[List[String]],
  extraHosts: Option[List[String]],
  forcePull: Option[Boolean],
  hostname: Option[String],
  interactive: Option[Boolean],
  sysctl: Option[Map[String, String]],
  ulimit: Option[Map[String, String]],
  privileged: Option[Boolean],
  ipv4Address: Option[String],
  ipv6Address: Option[String],
  logging: Option[DockerLogging],
  networkMode: Option[DockerNetworkMode],
  sharedMemorySize: Option[Long]
) extends DriverConfig

object DockerConfig {

  def apply(image: String): DockerConfig =
    DockerConfig(
      image,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None
    )

  def make(image: String): DockerConfig =
    DockerConfig(
      image,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None
    )

  implicit val decoderForDockerConfig = new Decoder[DockerConfig] {
    def apply(c0: HCursor): Decoder.Result[DockerConfig] =
      for {
        a <- c0.downField("image").as[String]
        b <- c0.downField("entrypoint").as[Option[List[String]]].map {
          case Some(value) if value.isEmpty => None
          case Some(value)                  => Some(value)
          case None                         => None
        }
        c <- c0.downField("command").as[Option[String]]
        d <- c0.downField("args").as[Option[List[String]]]
        e <- c0.downField("auth").as[Option[List[DockerAuth]]]
        f <- c0.downField("port_map").as[Option[List[Map[String, Int]]]]
        g <- c0.downField("labels").as[Option[Map[String, String]]]
        h <- c0.downField("volumes").as[Option[List[String]]]
        i <- c0.downField("volume_driver").as[Option[String]]
        j <- c0.downField("mounts").as[Option[List[DockerMount]]]
        k <- c0.downField("devices").as[Option[List[DockerDevice]]]
        l <- c0.downField("dns_search_domains").as[Option[List[String]]]
        m <- c0.downField("dns_servers").as[Option[List[String]]]
        n <- c0.downField("extra_hosts").as[Option[List[String]]]
        o <- c0.downField("force_pull").as[Option[Boolean]]
        p <- c0.downField("hostname").as[Option[String]]
        q <- c0.downField("interactive").as[Option[Boolean]]
        r <- c0.downField("sysctl").as[Option[Map[String, String]]]
        s <- c0.downField("ulimit").as[Option[Map[String, String]]]
        t <- c0.downField("privileged").as[Option[Boolean]]
        u <- c0.downField("ipv4_address").as[Option[String]]
        v <- c0.downField("ipv6_address").as[Option[String]]
        w <- c0.downField("logging").as[Option[DockerLogging]]
        x <- c0.downField("network_mode").as[Option[DockerNetworkMode]]
        y <- c0.downField("ssm_size").as[Option[Long]]
      } yield DockerConfig(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y)
  }

  implicit val encoderForDockerConfig = new Encoder[DockerConfig] {
    def apply(a: DockerConfig): Json =
      Json
        .obj(
          "image" -> a.image.asJson,
          "entrypoint" -> a.entrypoint.asJson,
          "command" -> a.command.asJson,
          "args" -> a.args.asJson,
          "auth" -> a.auth.asJson,
          "port_map" -> a.portMap.asJson,
          "labels" -> a.labels.asJson,
          "volumes" -> a.volumes.asJson,
          "volume_driver" -> a.volumeDriver.asJson,
          "mounts" -> a.mounts.asJson,
          "devices" -> a.devices.asJson,
          "dns_search_domains" -> a.dnsSearchDomains.asJson,
          "dns_servers" -> a.dnsServers.asJson,
          "extra_hosts" -> a.extraHosts.asJson,
          "force_pull" -> a.forcePull.asJson,
          "hostname" -> a.hostname.asJson,
          "interactive" -> a.interactive.asJson,
          "sysctl" -> a.sysctl.asJson,
          "ulimit" -> a.ulimit.asJson,
          "privileged" -> a.privileged.asJson,
          "ipv4_address" -> a.ipv4Address.asJson,
          "ipv6_address" -> a.ipv6Address.asJson,
          "logging" -> a.logging.asJson,
          "network_mode" -> a.networkMode.asJson,
          "ssm_size" -> a.sharedMemorySize.asJson
        )
        .dropNullValues
  }
}

final case class ExecConfig(
  command: String,
  args: Option[List[String]]
) extends DriverConfig

object ExecConfig {

  implicit val encoderForExecConfig = new Encoder[ExecConfig] {
    def apply(a: ExecConfig): Json = Json.obj(
      "command" -> a.command.asJson,
      "args" -> a.args.asJson
    )
  }

  implicit val decoderForExecConfig = new Decoder[ExecConfig] {
    def apply(c: HCursor): Decoder.Result[ExecConfig] =
      (
        c.downField("command").as[String],
        c.downField("args").as[Option[List[String]]]
      ).mapN(ExecConfig.apply)
  }
}

final case class JavaConfig(
  `class`: Option[String],
  classPath: Option[String],
  jarPath: Option[String],
  args: Option[List[String]],
  jvmOptions: Option[List[String]]
) extends DriverConfig

object JavaConfig {

  implicit val decoderForJavaConfig = new Decoder[JavaConfig] {
    def apply(c: HCursor): Decoder.Result[JavaConfig] =
      (
        c.downField("class").as[Option[String]],
        c.downField("class_path").as[Option[String]],
        c.downField("jar_path").as[Option[String]],
        c.downField("args").as[Option[List[String]]],
        c.downField("jvm_options").as[Option[List[String]]]
      ).mapN(JavaConfig.apply)
  }

  implicit val encoderForJavaConfig = new Encoder[JavaConfig] {
    def apply(a: JavaConfig): Json = Json.obj(
      "class" -> a.`class`.asJson,
      "class_path" -> a.classPath.asJson,
      "jar_path" -> a.jarPath.asJson,
      "args" -> a.args.asJson,
      "jvm_options" -> a.jvmOptions.asJson
    )
  }
}

object DriverConfig {

  implicit val encoderForDriverConfig = new Encoder[DriverConfig] {
    def apply(a: DriverConfig): Json = a match {
      case c: DockerConfig => c.asJson
      case c: ExecConfig   => c.asJson
      case c: JavaConfig   => c.asJson
    }
  }
}
