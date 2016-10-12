/*
 * Copyright (c) 2016, EMC Corporation.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * + Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * + Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 * + The name of EMC Corporation may not be used to endorse or promote
 *   products derived from this software without specific prior written
 *   permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
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

    private final EcsS3Adapter _adapter;

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
        log.error("New ecs url: " + url);
        return url;
    }

}
