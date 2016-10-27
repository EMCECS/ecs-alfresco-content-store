/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 *
 * Licensed under the EMC Software License Agreement for Free Software (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * https://github.com/EMCECS/ecs-alfresco-content-store/blob/master/LICENSE.txt
 */
package com.emc.ecs.alfresco;

import org.alfresco.repo.content.AbstractContentStore;
import org.alfresco.repo.content.ContentStore;
import org.alfresco.repo.content.filestore.FileContentStore;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.util.GUID;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author seibed
 *
 */
public class EcsS3ContentStore extends AbstractContentStore implements ContentStore {

    private static final String PROTOCOL_AND_DELIMITER = "ecsS3" + ContentStore.PROTOCOL_DELIMITER;

    public static final int PROTOCOL_AND_DELIMITER_LENGTH = PROTOCOL_AND_DELIMITER.length();

    private static Log log = LogFactory.getLog(EcsS3ContentStore.class);

    /**
     *  The adapter that does the work.
     */
    private final EcsS3Adapter _adapter;

    /**
     * All parameters are read from properties files.
     * @throws Exception
     */
    public EcsS3ContentStore() throws Exception {
        _adapter = new EcsS3Adapter();
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.content.AbstractContentStore#delete(java.lang.String)
     */
    @Override
    public boolean delete(String contentUrl) {
        boolean deleted = false;
        try {
            _adapter.delete(contentUrl);
            deleted = true;
        } catch (Exception e) {
            log.error("Failed to delete " + contentUrl, e);
        }
        return deleted;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.content.ContentStore#getReader(java.lang.String)
     */
    @Override
    public ContentReader getReader(String contentUrl) {
        ContentReader contentReader = null;
        try {
            contentReader = new EcsS3ContentReader(contentUrl, _adapter);
        } catch (Exception e) {
            log.error("Failed to get ContentReader for " + contentUrl, e);
        }
        return contentReader;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.content.AbstractContentStore#getWriterInternal(org.alfresco.service.cmr.repository.ContentReader, java.lang.String)
     */
    @Override
    protected ContentWriter getWriterInternal(ContentReader existingContentReader, String newContentUrl) {
        ContentWriter contentWriter = null;
        try {
            String contentUrl = StringUtils.isNotBlank(newContentUrl) ? newContentUrl : createNewUrl();
            contentWriter = new EcsS3ContentWriter(contentUrl, existingContentReader, _adapter);
        } catch (Exception e) {
            log.error("Failed to get ContentWriter for " + newContentUrl, e);
        }
        return contentWriter;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.content.AbstractContentStore#isContentUrlSupported(java.lang.String)
     */
    @Override
    public boolean isContentUrlSupported(String contentUrl) {
        if (StringUtils.isBlank(contentUrl)) {
            throw new IllegalArgumentException("The contentUrl may not be blank");
        }

        int index = contentUrl.indexOf(PROTOCOL_AND_DELIMITER);
        if (index < 0) {
            // temp: backwards compat for trashathon only !
            index = contentUrl.indexOf(FileContentStore.STORE_PROTOCOL + ContentStore.PROTOCOL_DELIMITER);
        }

        boolean supported = (index >= 0);

        if (log.isDebugEnabled()) {
            String possibleNot = supported ? "" : "NOT ";
            log.debug("contentUrl " + contentUrl + " IS " + possibleNot + " supported by this content store");
        }

        return supported;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.content.ContentStore#isWriteSupported()
     */
    @Override
    public boolean isWriteSupported() {
        return true;
    }

    /**
     * Creates a new content URL. This must be supported by all
     * stores that are compatible with Alfresco.
     *
     * @return Returns a new and unique content URL
     */
    public static String createNewUrl() {
        StringBuilder sb = new StringBuilder(PROTOCOL_AND_DELIMITER);
        String url = sb.append(GUID.generate()).toString();
        log.debug("New ecs url: " + url);
        return url;
    }

}
