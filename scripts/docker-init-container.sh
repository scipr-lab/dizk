#!/bin/bash

docker create \
       --name dizk \
       --tty \
       --interactive \
       --volume `git rev-parse --show-toplevel`:/home/dizk \
       --env "TERM=xterm-256color" \
       dizk-base
