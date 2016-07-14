/**
 * Copyright 2016 EMC Corporation. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.emc.ecs.alfresco;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import com.emc.object.Protocol;
import com.emc.object.s3.LargeFileUploader;
import com.emc.object.s3.S3Config;
import com.emc.object.s3.S3ObjectMetadata;
import com.emc.object.s3.jersey.S3JerseyClient;
import com.emc.rest.smart.ecs.Vdc;

/**
 * @author seibed
 *
 */
public class EcsS3Adapter {

    /**
     * Name of the properties file holding parameters. This is searched for in
     * the classpath and the home directory, e.g., => $HOME/ecsS3.properties.
     */
    private static final String PROPERTIES_FILE_NAME = "ecsS3";

    /**
     * Property key for the bucket name used to store alfresco content.
     */
    private static final String BUCKET_NAME = "alfresco.bucketName";

    /**
     * Property key for the boolean to decide whether to enable VHOST.
     */
    private static final String ENABLE_VHOST = null;

    /**
     * Property key for the ECS S3 endpoint URL string.
     */
    private static final String ENDPOINT = null;

    /**
     * Property key for the ECS S3 access key.
     */
    private static final String ACCESS_KEY = null;

    /**
     * Property key for the ECS S3 secret key.
     */
    private static final String SECRET_KEY = null;

    /**
     * The client used to connect with the ECS S3 instance.
     */
    private final S3JerseyClient _client;

    /**
     * The bucket name used to store alfresco content.
     */
    private final String _bucketName;

    /**
     * Properties for the adapter.
     */
    private final Properties _properties;

    public EcsS3Adapter() throws Exception {
        _properties = loadProperties();
        _client = new S3JerseyClient(getS3Config());
        _bucketName = getProperty(BUCKET_NAME);
    }

    /**
     * @param contentUrl
     */
    public void delete(String contentUrl) {
        String bucketName = getBucketName(contentUrl);
        _client.deleteObject(bucketName, getKey(contentUrl));
        if (_client.listObjects(bucketName).getObjects().isEmpty()) {
            _client.deleteBucket(bucketName);
        }
    }

    /**
     * @param contentUrl
     * @return
     */
    public boolean exists(String contentUrl) {
        return (getObjectMetadata(contentUrl) != null);
    }

    /**
     * @param contentUrl
     * @return
     */
    public InputStream getInputStream(String contentUrl) {
        return _client.getObject(getBucketName(contentUrl), getKey(contentUrl)).getObject();
    }

    /**
     * @param contentUrl
     * @return
     */
    public long getLastModified(String contentUrl) {
        S3ObjectMetadata objectMetadata = getObjectMetadata(contentUrl);
        return ((objectMetadata == null) || (objectMetadata.getLastModified() == null)) ? 0
                : objectMetadata.getLastModified().getTime();
    }

    /**
     * @param contentUrl
     * @return
     */
    public long getSize(String contentUrl) {
        S3ObjectMetadata objectMetadata = getObjectMetadata(contentUrl);
        return (objectMetadata == null) ? 0 : objectMetadata.getContentLength();
    }

    /**
     * @param writer
     * @throws Exception
     */
    public void closeStream(EcsS3ContentWriter writer) throws Exception {
        String bucketName = getBucketName(writer.getContentUrl());
        if (!_client.bucketExists(bucketName)) {
            // create a new bucket
            _client.createBucket(bucketName);
        }
        String key = getKey(writer.getContentUrl());
        LargeFileUploader largeFileUploader = new LargeFileUploader(_client, bucketName, key, writer.getTempFile());
        largeFileUploader.run();
    }

    /**
     * @param contentUrl
     * @return The metadata.
     */
    private S3ObjectMetadata getObjectMetadata(String contentUrl) {
        String bucketName = getBucketName(contentUrl);
        String key = getKey(contentUrl);
        return _client.getObjectMetadata(bucketName, key);
    }

    /**
     * @param contentUrl
     * @return
     */
    private String getBucketName(String contentUrl) {
        return _bucketName;
    }

    /**
     * @param contentUrl
     * @return
     */
    private String getKey(String contentUrl) {
        return contentUrl.substring(EcsS3ContentStore.PROTOCOL_AND_DELIMITER_LENGTH);
    }

    /**
     * @return
     * @throws URISyntaxException 
     */
    private S3Config getS3Config() throws URISyntaxException {
        String accessKey = getProperty(ACCESS_KEY);
        String secretKey = getProperty(SECRET_KEY);
        URI endpoint = new URI(getProperty(ENDPOINT));
        boolean enableVhost = Boolean.parseBoolean(getProperty(ENABLE_VHOST, Boolean.FALSE.toString()));

        S3Config s3Config;
        if (enableVhost) {
            s3Config = new S3Config(endpoint).withUseVHost(true);
        } else if (endpoint.getPort() > 0) {
            s3Config = new S3Config(Protocol.valueOf(endpoint.getScheme().toUpperCase()), new Vdc(endpoint.getHost()));
            s3Config.setPort(endpoint.getPort());
        } else {
            s3Config = new S3Config(Protocol.valueOf(endpoint.getScheme().toUpperCase()), endpoint.getHost());
        }
        return s3Config.withIdentity(accessKey).withSecretKey(secretKey);
    }

    /**
     * @param propertyKey
     *            The key for the property.
     * @return The property value.
     */
    protected final String getProperty(String propertyKey) {
        return _properties.getProperty(propertyKey);
    }

    /**
     * @param propertyKey
     *            The key for the property.
     * @param defaultValue
     * @return The property value, or the default if the property value is
     *         missing.
     */
    protected final String getProperty(String propertyKey, String defaultValue) {
        return _properties.getProperty(propertyKey, defaultValue);
    }

    /**
     * Loads the default properties, throws an exception if they can't be
     * loaded.
     * 
     * @return The properties.
     * @throws IOException
     */
    protected static Properties loadProperties() throws IOException {
        return loadProperties(PROPERTIES_FILE_NAME);
    }

    /**
     * Locates and loads the properties file for the configuration. This file
     * can reside in one of two places: somewhere in the CLASSPATH or in the
     * user's home directory.
     *
     * @param fileName
     *            The file name.
     * @return the contents of the properties file as a
     *         {@link java.util.Properties} object.
     * @throws IOException
     */
    protected static Properties loadProperties(String fileName) throws IOException {
        String fullFileName = fileName + ".properties";
        InputStream inputStream = EcsS3Adapter.class.getClassLoader().getResourceAsStream(fullFileName);
        if (inputStream == null) {
            // Check in home directory
            File homeProperties = new File(System.getProperty("user.home") + File.separator + fullFileName);
            if (homeProperties.exists()) {
                inputStream = new FileInputStream(homeProperties);
            }
        }

        if (inputStream == null) {
            throw new FileNotFoundException("Properties file cannot be located: " + fullFileName);
        }

        Properties properties = new Properties();
        properties.load(inputStream);
        inputStream.close();
        return properties;
    }

}
