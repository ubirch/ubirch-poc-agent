# Working From Code

In order to get started with the system directly from its sources, you will need to follow the next steps:

1. [Requirements](#requirements)
2. [Compile PoC Agent](#compile-the-poc-agent)
3. [Set up keystore](#set-up-keystore)
4. [Start the UPP Signer](#start-the-upp-signer)
4. [Start the PoC Agent](#start-the-poc-agent)   
5. [Verify](#verify-that-it-has-started)
6. [Create a Certificate](#create-a-certificate)

## Requirements

- MAVEN 3.8 or greater
- JDK version "11.x.x"
- Docker

## Compile the PoC Agent

```shell
git clone git@github.com:ubirch/ubirch-poc-agent.git
cd ubirch-poc-agent
mvn clean compile test
```

## Set up KeyStore

There are two ways you can configure the keystore:

- In your [Application Configuration File](../src/main/resources/application.conf) set up /override the path for the keystore and its password.
- export `POC_AGENT_SVC_HTTP_CLIENT_SSL_KEYSTORE` and `POC_AGENT_SVC_HTTP_CLIENT_SSL_KEYSTORE_PASSWORD` as environment variables.

> If you don't have a keystore yet. Check out [Identity Procurement](./identity_procurement.md).

## Start the UPP Signer

1. In the `go_client/config.json` replace the `devices` map with the format `<device_id>:<device_password>`.
2. From the `go_client` location run: 

```shell
`docker run -v $(pwd):/data -p 8080:8080 ubirch/ubirch-client:v1.2.2`.
``` 

3. UBIRCH client should be successfully running.

> Note that the go_client folder is a convenient place. You may also have mapped it to another location.

> If you don't have a upp identity and its token. Check out [Identity Procurement](./identity_procurement.md).


## Start the Poc Agent

```shell
mvn compile exec:java -Dexec.mainClass="com.ubirch.Service"
```

## Verify that it has started

If you started the application on port 8081:

```shell
curl -L http://localhost:8081
```

You should get a hello message:

```json
{
  "version": "0.1.0",
  "ok": true,
  "data": "Hallo, Hola, こんにちは, Hello, Salut, Hej, this is the Ubirch Point of Certification agent."
}
```

## Create a Certificate

Open op this file: [Test Example](../deploymentComponents/create_test.sh) and replace deviceId and devicePwd and in the data object the id field with you unique identifier as integrator.



