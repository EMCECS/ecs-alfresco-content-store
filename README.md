ecs-alfresco-content-store
===
This is a Content Store implementation to store data in EMC ECS via S3. This produces an Alfresco Module Package (amp) file, configured to use ECS S3 for primary storage. You could also use S3 for other storage purposes, such as content replication. In that case, you will need to change the configuration files appropriately to support that other use case and then rebuild the amp.

How to develop and build
---
This is basically a maven project to build an Alfresco Module Package (amp) file, with a simple gradle wrapper to create a distribution zip for that amp (with licenses and source). The reason for not moving the basic amp build to gradle is that alfresco provides downloadable maven plugins but not gradle plugins, and we can't support the gradle amp plugins ourselves. An amp file is basically a jarred set of jar libraries and configuration files, with the extension "amp".

To work with the code in a development environment, you should import it as a maven project, using the `pom.xml` file. The `build.gradle` file does not contain the dependencies, so you can't import it as a gradle project.

The easiest way to build is from the command line with `gradlew`. Here are the most useful commands, all of which should be run from the project root folder.
  - `./gradlew clean` - cleans up all produced artifacts
  - `./gradlew buildAmp` - builds just the amp file.
  - `./gradlew distZip` (or simply `./gradlew`) - builds a complete distribution zip file.

How to install the amp
---
You MUST do this before you first run Alfresco, as changing the content storage mechanism once any data has been stored will break your Alfresco deployment. The simplest technique is to use the Module Management Tool, as described below.
  - http://docs.alfresco.com/5.1/tasks/amp-install.html

You must also add the following properties to your alfresco-global.properties file:

    #where to store the data
    ecss3.bucketName=
    #configuration parameters for the ecs client - legacy version
    #access id
    ecss3.access_key=
    #access key
    ecss3.secret_key=
    #endpoint or comma-separated lost of endpoints
    ecss3.endpoint=https://object.ecstestdrive.com:443
    #true/false
    ecss3.enable_vhost=
    #true/false for enabling client-side load balancing
    ecss3.smart_client=
    #configuration parameters for the ecs client - new version
    ecss3.config_uri=
    #threshold for multipart file upload (bytes).
    ecss3.large_file_upload_threshold=10485760
    #part size when using multipart file upload (bytes).
    ecss3.large_file_part_size=3145728

