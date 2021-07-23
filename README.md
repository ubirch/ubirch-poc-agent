# Ubirch PoC Agent

The PoC Agent stands for Point of Certification Agent. Its task is to enable an on-premise solution that allows creating Ubirch UPP Certificates and DCC Test Certificates.

1. [Certify API](#how-to-set-up-the-certify-api-connection)
2. [Ubirch UPP client](#how-to-run-the-ubirch-client)
3. [Example](#example)   
3. [Swagger](#swagger)
4. [Prometheus](#prometheus-metrics)


## How to set up the Certify API Connection

1. Ask the Ubirch team for a proper pfx file that contains the client certification and its private key for your usage. Note that a password should be available as well.
2. Put this file in a secure place.
3. Run `create_key_store_pck12 pfx_file pfx_pass key_store key_store_pass`. Where _pfx_file_ is the path to the pfx file; `pfx_pass` is the password to the pfx file. `key_store` is the name of the keystore that we are creating for the application. `key_store_pass` is the password for the application keystore.
4. In your application conf file set up the path for the keystore and its password. The keys are
_system.http.client.sslKeyStore_ and _system.http.client.sslKeyStore_.
    

Note: The systems support two kinds of keystores, a jks or pkcs12. We recommend the latter. However you can also configure a traditional jks store.

## How to run the UBIRCH client

1. Set up your account at `console.dev.ubirch.com`
2. Go to Things -> Add New Device and create a new Device..
3. In the `go_client/config.json` replace the `devices` map with the format `<device_id>:<device_password>`.
4. From the `go_client` location run the `docker run -v $(pwd):/data -p 8080:8080 ubirch/ubirch-client:v1.2.2`.
5. UBIRCH client should be successfully running.

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
