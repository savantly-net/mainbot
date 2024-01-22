#!/bin/bash

java ${JAVA_OPTS} -cp /app -Dserver.port=${PORT} org.springframework.boot.loader.JarLauncher