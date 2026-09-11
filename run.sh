#!/usr/bin/env bash
set -e

cd "$(dirname "$0")"

mkdir -p build

javac -d build $(find src -name '*.java')

java -cp build Main
