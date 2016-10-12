# ecs-alfresco-content-store
Content Store implementation to store data in EMC Elastic Cloud Storage via S3

# How to develop and build
This is basically a maven project to build an alfresco amp file, with a simple gradle wrapper to create a distribution zip for that amp. The reason for not moving the basic amp build to gradle is that alfresco provides downloadable maven plugins but not gradle plugins. An amp file is basically a jarred set of jar libraries and configuration files, with the extension "amp".

To work with the code in a development environment, you should import it as a maven project, using the pom.xml file. The gradle file does not contain the dependencies, so you can't import it as a gradle project.

The easiest way to build is from the command line with gradlew. Here are the most useful commands, all of which should be run from the project root folder.
  - './gradlew clean' - cleans up all produced artifacts
  - './gradlew buildAmp' - builds just the amp file.
  - './gradlew distZip' (or simply './gradlew') - builds a complete distribution zip file.
  