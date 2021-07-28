# Identity Procurement

In order to send a certification request, there is a bill of materials that must be satisfied:

1. An Ubirch-enabled Client Certificate.
2. An Ubirch Thing Identity

## An Ubirch-enabled Client Certificate.

1. Ask the Ubirch team for a proper `pfx file` that contains the client certificates and its private key for your usage. Note that a password should be available as well.
2. Put this file in a secure place.
3. Run `create_key_store_pck12 pfx_file pfx_pass key_store key_store_pass`. 
   
   Where:
      - `pfx_file` is the path to the pfx file;
      - `pfx_pass` is the password to the pfx file. 
      - `key_store` is the name of the keystore that we are creating for the application. 
      `key_store_pass` is the password for the application keystore. 

   Note: The systems support two kinds of keystores, a `jks` or `pkcs12`. We recommend the latter. However, you can also configure a traditional jks store.

4. Store this file in a secure place along with its credentials

## An Ubirch Thing Identity

1. Set up your account at [UBIRCH web UI](https://console.dev.ubirch.com/):
2. Go to `Things` -> click on `Add New Device` and create a new Device.
4. Store the UUID and Password generated in a secure place.

