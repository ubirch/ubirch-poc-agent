# Ubirch PoC Agent


## How to run the UBIRCH client

1. Set up your account at `console.dev.ubirch.com`
2. Go to Things -> Add New Device and create a new Device..
3. In the `go_client/config.json` replace the `devices` map with the format `<device_id>:<device_password>`.
4. From the `go_client` location run the `docker run -v $(pwd):/data -p 8080:8080 ubirch/ubirch-client:v1.2.2`.
5. UBIRCH client should be successfully running.