#!/bin/bash

BUILD_NUMBER=$1

if [ -z "$1" ]
  then
    echo "No build_number provided"
    echo "build_image build_number"
    exit 1
fi

echo "Building image with name $BUILD_NUMBER"
mvn clean package -Dbuild.number="$BUILD_NUMBER"

