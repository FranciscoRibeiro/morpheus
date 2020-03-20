#!/bin/sh

export MAVEN_OPTS="-Xmx6G -Xss128M -XX:MetaspaceSize=512M \
-XX:MaxMetaspaceSize=1024M -XX:+CMSClassUnloadingEnabled"

mvn package
