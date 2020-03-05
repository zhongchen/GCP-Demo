#!/usr/bin/env bash

set -e

# enable this for debugging purpose
#set -x

echo "start"

if [[ $# -gt 0 ]]; then
  echo "goog"
else
  echo "what"
fi

echo "end"

TABLES=(dl.a dl.b dl.c)

echo "${TABLES[@]}"

for t in "${TABLES[@]}";
do
  IFS='.'
  read -ra table <<< "$t"
  echo "${table[1]}"
done
