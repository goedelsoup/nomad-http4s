#!/usr/bin/env bash
docker run -d \
  -p 4646:4646 \
  --privileged \
  -e NOMAD_LOCAL_CONFIG='
datacenter = "${REGION}"
region     = "${DATACENTER}"

data_dir = "/nomad/data/"

bind_addr = "0.0.0.0"

advertise {
  http = "{{ GetPrivateIP }}:4646"
  rpc  = "{{ GetPrivateIP }}:4647"
  serf = "{{ GetPrivateIP }}:4648"
}
' \
  -v "/var/run/docker.sock:/var/run/docker.sock" \
  multani/nomad agent -dev