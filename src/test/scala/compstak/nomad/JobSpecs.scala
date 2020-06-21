package compstak.nomad

object JobSpecs {

  val echo: String =
    s"""
job "echo" {
  type        = "batch"
  region      = "global"
  datacenters = ["dc1"]

  parameterized {
    payload       = "forbidden"
    meta_required = ["word"]
    meta_optional = []
  }

  group "echo" {

    task "echo" {
      driver = "raw_exec"

      config {
        command = "echo"
        args = ["$${NOMAD_META_word}"]
      }
    }
  }
}
    """
}
