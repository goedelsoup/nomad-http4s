package nomad4s.jobs

import cats.Show
import cats.effect.Sync
import io.circe.Decoder
import org.http4s.circe._
import cats.data.NonEmptyList

final case class ReadResult[P, M](
  id: JobId,
  region: String,
  parent: JobId,
  name: String,
  jobType: JobType,
  priority: Int,
  allAtOnce: Boolean,
  datacenters: NonEmptyList[String],
  status: JobStatus,
  statusDescription: String,
  payload: P,
  metadata: M,
  createIndex: Int,
  modifyIndex: Int,
  jobModifyIndex: Int,
)

object ReadResult {

  implicit def decoderForReadResult[P: Decoder, M: Decoder]: Decoder[ReadResult[P, M]] =
    Decoder.forProduct15(
      "ID",
      "Region",
      "ParentID",
      "Name",
      "JobType",
      "Priority",
      "AllAtOnce",
      "Datacenters",
      "Status",
      "StatusDescription",
      "Payload",
      "Metadata",
      "CreateIndex",
      "ModifyIndex",
      "JobModifyIndex"
    )(ReadResult.apply)

  implicit def entityDecoderForReadResult[F[_]: Sync, P: Decoder, M: Decoder] = jsonOf[F, ReadResult[P, M]]

  implicit def showForReadResult[P: Show, M: Show]: Show[ReadResult[P, M]] = Show.fromToString
}
