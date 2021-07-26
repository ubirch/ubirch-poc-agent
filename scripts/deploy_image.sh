#!/bin/bash

BUILD_NUMBER=$1

if [ -z "$1" ]
  then
    echo "No build_number provided"
    echo "deploy_image build_number"
    exit 1
fi

echo "Building image with name $BUILD_NUMBER"
cd ..
mvn clean deploy -Dbuild.number="$BUILD_NUMBER"

