#!/bin/sh
# Perform necessary replacements and then run the Scala program

input="${0:-}"
deps=`cat ./deps.toml`

sed -i '' "s/{{.*USER_INPUT.*}}/$USER_INPUT/g" ./src/*
sed -i '' "s/{{.*DEPS.*}}/$deps/g" ./build.toml

bloop server &>/dev/null &
seed bloop > /dev/null
bloop run scala2