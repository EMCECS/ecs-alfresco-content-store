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
# or in the "license" file accompanying this file. This file is distributed
# on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
# express or implied. See the License for the specific language governing
# permissions and limitations under the License.
#
if [[ -z ${MAVEN_OPTS} ]]; then
    echo "The environment variable 'MAVEN_OPTS' is not set, setting it for you";
    MAVEN_OPTS="-Xms256m -Xmx1524m -XX:PermSize=300m"
fi
echo "MAVEN_OPTS is set to '$MAVEN_OPTS'";
mvn clean -Pamp-to-war
