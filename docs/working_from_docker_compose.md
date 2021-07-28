# Working from Docker Compose

1. [Requirements](#requirements)
2. [Docker Compose Flavors](#docker-compose-flavors)
3. [Single PoC Agent](#single-poc-agent)
4. [Load Balanced PoC Agent](#load-balanced-poc-agent)
5. [Verify](#verify-that-it-has-started)
6. [Create a Certificate](#create-a-certificate)

## Requirements

- Docker
- docker-compose

## Docker Compose Flavors

We offer a quick start to start using the system. The repository has two docker compose definitions that can offer a nice and controlled environment.

There are two flavors:

- [Single PoC Agent](#single-poc-agent): One  to one configuration. One PoC Agent and one UPP Signer.
- [Load Balanced PoC Agent](#load-balanced-poc-agent): HA Proxy, two PoC Agents and one UPP Signer.

In order to facilitate the process, a deployment-ready folder is provided. This folder is 'deploymentComponents'

![img_1.png](../assets/docker_compose_structure.png)

## Single PoC Agent

> A key store is expected to be located at: 'deploymentComponent/pocAgent'
> 
> A configured config.json for the UPP Signer is expected at 'deploymentComponent/uppClient'

```shell
docker-compose --f docker-compose.yml up -d
```

The flag (-d) starts the systems in detached mode. If you have run it with this flag, you can use the following command to see the logs.

```shell
docker-compose logs -f
```

## Load Balanced PoC Agent


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




