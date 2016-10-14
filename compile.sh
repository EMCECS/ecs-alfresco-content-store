#!/bin/bash
#
# Copyright (c) 2016 EMC Corporation. All Rights Reserved.
#
# Licensed under the EMC Software License Agreement for Free Software (the "License").
# You may not use this file except in compliance with the License.
# A copy of the License is located at
#
# https://github.com/EMCECS/ecs-alfresco-content-store/blob/master/LICENSE.txt
#
if [[ -z ${MAVEN_OPTS} ]]; then
    echo "The environment variable 'MAVEN_OPTS' is not set, setting it for you";
    MAVEN_OPTS="-Xms256m -Xmx1524m -XX:PermSize=300m"
fi
echo "MAVEN_OPTS is set to '$MAVEN_OPTS'";
mvn package -Pamp-to-war
