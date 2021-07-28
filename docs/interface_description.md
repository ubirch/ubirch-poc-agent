# Interface Description

The PoC Agent offers a certification endpoint, along with its swagger documentation (which can be turned on/off); and an interfaces to collect metrics with Prometheus.

1. [Certification](#certification-endpoint)
2. [Swagger](#swagger)
3. [Prometheus](#prometheus-metrics)

## Certification Endpoint

This endpoint is meant to create a UPP-DCC Certificate.

### Path

| Method | Path |  Description |
|--------|------|-------------|
| POST | `/<UUID>` | DCC JSON test data |

Where `<UUID>` is the identifier of an Ubirch identity. 

> [Identity Procurement](./identity_procurement.md)

### Headers

- `X-Auth-Token`: UBIRCH token related to `<UUID>`
- `Accept`:
    * `application/cbor`: By default used when not provided. Gets a base64 encoded cbor object for the dcc certificate.
    * `application/cbor+base45`: Gets zipped base45 version of the cbor object for the dcc certificate.
    * `application/pdf`: Gets a base64 encoded pdf for the dcc certificate.
    
- `Content-Type`: Optional as application/json.

### Body

```json
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
      "se": "XXXXYYYYZZZZ"
    }
  ]
}
```

### 200 Response

```json
{
  "hash":"PYJCVPY2C9+Wx9AVHgMl9f3oeM5AWbqR7H6q8kVYlyA=",
  "upp":"liPEEI5qVnlbDEoemqVPbkL/9bPEQEvRcN6FSHNGh6vbvS1clcyPEmRMxcxdenvTSZBs8wF7qH1EEnt1vPIvQ930p4DtGOwzRTXa1klPqtqEeiUiWhgAxCA9gkJU9jYL35bH0BUeAyX1/eh4zkBZupHsfqryRViXIMRAAxJ89aasfPXfdlt7qvpVP4MaYK0cYzhoFrJwzXynajozt3JeC4kjbpRUmTbtgRlE+WA43KIweODR6n+JTgz5WA==",
  "dcc":"0oRDoQEmoQRI7JQ83GRvK3BZASqkAWJERQQaYuFasAYaYQAnMDkBA6EBpGF0galiY2l4MFVSTjpVVkNJOjAxREUvMTAzNjUwMjAzLzJMMVdQWUcyNTROUVVPWUZNTzBYVk4jOGJjb2JERWJpc3RSb2JlcnQgS29jaC1JbnN0aXR1dGJubXZSb2NoZSBMaWdodEN5Y2xlciBxUENSYnNjdDIwMjEtMDQtMTNUMTQ6MjA6MDBaYnRjckhhdXB0YmFobmhvZiBLw7ZsbmJ0Z2k4NDA1MzkwMDZidHJpMjYwNDE1MDAwYnR0aExQNjQ2NC00Y2RvYmoxOTc5LTA0LTE0Y25hbaRiZm5qTXVzdGVyZnJhdWJnbmVFcmlrYWNmbnRqTVVTVEVSRlJBVWNnbnRlRVJJS0FjdmVyZTEuMy4wWEByBP0xbS6OjgNjsDr4kJhFPfa1uiluqSLVBD2QVu1ETb04oGCN3ViyH/a4EjTTtpC5aEwi5oYH5n+xTvxeZvK3",
  "response":{
    "statusCode":200,
    "header":{

    },
    "content":"liPEEJ08eP8i80RBpdGFxjbUhv/EQAMSfPWmrHz133Zbe6r6VT+DGmCtHGM4aBaycM18p2o6M7dyXguJI26UVJk27YEZRPlgONyiMHjg0ep/iU4M+VgAxCDX/YVHO1VL+77bn2K+/wLwAAAAAAAAAAAAAAAAAAAAAMRAFihVhd4dDLwRj5haD+Xsis2U8OKYoXPIHE2cZ6c6JMW4lqY0ZdKnM6tr8+5rfrIXgrQlGv6HzfxIxMPewJL2CA=="
  },
  "requestID":"d7fd8547-3b55-4bfb-bedb-9f62beff02f0",
  "dccID":"URN:UVCI:01DE/103650203/2L1WPYG254NQUOYFMO0XVN#8"
}
```

> Note that depending on the "Accept Header", the field dcc could also be base45 encoded or in case a pdf is requested, the field pdf would be part of the object instead.

#### Response Headers

`X-DGC-ID`: Unique identifier of the DCC Certificate

`requestID`: Unique request Id of the UPP Trust Registration


### Example

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

You can also check out this example: [Curl Example](../deploymentComponents/create_test.sh).

## Swagger

Visit `http://localhost:8081/docs` on your browser to see the swagger docs.

_Note_: The swagger endpoint can be disabled if required. There is a configuration boolean key `system.server.swaggerActivated` that can be used to de/activate the swagger endpoint. By default, it is activated.

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
