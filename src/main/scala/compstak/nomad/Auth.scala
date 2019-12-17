package compstak.nomad

import org.http4s.Uri

sealed trait Auth
final case class Anonymous(host: Uri) extends Auth
final case class TokenInfo(host: Uri, token: String) extends Auth
