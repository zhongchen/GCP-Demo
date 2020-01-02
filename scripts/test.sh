#!/usr/bin/env bash

set -e

# enable this for debugging purpose
set -x

echo "start"

if [[ $# -gt 0 ]]; then
  echo "goog"
else
  echo "what"
fi

echo "end"
