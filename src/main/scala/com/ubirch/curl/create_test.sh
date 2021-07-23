#!/bin/bash

host="http://localhost:8081"
accept="application/cbor"

curl -v --location --request POST ${host}/poc-agent/v1/certification \
--header "Accept: $accept" \
--header 'Content-Type: application/json' \
 --data-binary @- <<EOF | jq .
{
  "nam": {
    "fn": "Musterfrau",
    "gn": "Erika"
  },
  "dob": "1979-04-14",
  "t": [
    {
      "id": "103650203",
      "tg": "840539006",
      "tt": "LP6464-4",
      "nm": "Roche LightCycler qPCR",
      "tr": "260415000",
      "sc": "2021-04-13T14:20:00+00:00",
      "dr": "2021-04-13T20:00:01+00:00",
      "tc": "Hauptbahnhof Köln",
      "se": "$(openssl rand -hex 8)"
    }
  ]
}
EOF
