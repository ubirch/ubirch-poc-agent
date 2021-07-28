#Interface Description


3. [Example](#example)
3. [Swagger](#swagger)
4. [Prometheus](#prometheus-metrics)

## Example

```shell script
#!/bin/bash

host="http://localhost:8081"
accept="application/cbor"
deviceId="e1aead08-1fcb-47b3-bf2c-d3343cb979da"
devicePwd="ab561dfd-f414-484e-9ac1-42d018f73b3d"

curl -v --location --request POST ${host}/${deviceId} \
--header "X-Auth-Token: $devicePwd" \
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
```



## Swagger

Visit `http://localhost:8081/docs` on your browser to see the swagger docs.

_Note_: The swagger endpoint can be disabled if required. There is a configuration boolean key `system.server.swaggerActivated` that can be used to de/activate the swagger endpoint. By default it is activated.

## Prometheus Metrics

**Note**: If you're starting the multiple services on the same machine/jvm, the port might change; and
you will have to change it accordingly. The port that is assigned to Prometheus is show on the console of
every service at boot.

```
  (1) http://localhost:4321/
```

or

```  
   (2) watch -d "curl --silent http://localhost:4321 | grep SERVICE-NAME"
```
