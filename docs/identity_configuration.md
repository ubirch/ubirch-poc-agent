# Identity Configuration

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
