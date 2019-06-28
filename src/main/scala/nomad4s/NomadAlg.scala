package nomad4s

import cats.Show
import io.circe._
import nomad4s.jobs._

trait NomadAlg[F[_]] {

  def dispatch[P: Encoder, M: Encoder](job: JobId, payload: P, meta: M): F[DispatchResult]

  def read[P: Decoder: Show, M: Decoder: Show](job: JobId): F[ReadResult[P, M]]

  def stop(job: JobId, purge: Boolean = false): F[StopResult]
}
