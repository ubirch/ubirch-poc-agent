# Ubirch PoC Agent

1. [ubirch upp client](#how-to-run-the-ubirch-client)
2. [Swagger](#swagger)
3. [Prometheus](#prometheus-metrics)

## How to run the UBIRCH client

1. Set up your account at `console.dev.ubirch.com`
2. Go to Things -> Add New Device and create a new Device..
3. In the `go_client/config.json` replace the `devices` map with the format `<device_id>:<device_password>`.
4. From the `go_client` location run the `docker run -v $(pwd):/data -p 8080:8080 ubirch/ubirch-client:v1.2.2`.
5. UBIRCH client should be successfully running.

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
