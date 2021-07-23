#!/bin/bash

PFX_P12_FILE_NAME=$1
PFX_P12_FILE=$2
KEYSTORE_FILE=$3
KEYSTORE_PASSWORD=$4
KEY_PASSWORD=$5

if [ -z "$1" ]
  then
    echo "No pfx_file provided"
    echo "create_key_store_jks pfx_file pfx_pass key_store key_store_pass key_pass"
    exit 1
fi

if [ -z "$2" ]
  then
    echo "No pfx_pass provided"
    echo "create_key_store_jks pfx_file pfx_pass key_store key_store_pass key_pass"
    exit 1
fi

if [ -z "$3" ]
  then
    echo "No key_store"
    echo "create_key_store_jks pfx_file pfx_pass key_store key_store_pass key_pass"
    exit 1
fi

if [ -z "$4" ]
  then
    echo "No key_store_pass"
    echo "create_key_store_jks pfx_file pfx_pass key_store key_store_pass key_pass"
    exit 1
fi

if [ -f "$KEYSTORE_FILE" ]; then
  rm "$KEYSTORE_FILE" -r
fi

keytool -importkeystore -srckeystore "$PFX_P12_FILE_NAME" -srcstoretype pkcs12 -srcstorepass "$PFX_P12_FILE" -destkeystore "$KEYSTORE_FILE" -deststoretype jks -deststorepass "$KEYSTORE_PASSWORD" -destkeypass $KEY_PASSWORD -srcalias "" -destalias client_cert

