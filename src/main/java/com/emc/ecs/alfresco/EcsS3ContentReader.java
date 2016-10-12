/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 *
 * Licensed under the EMC Software License Agreement for Free Software (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * https://github.com/EMCECS/ecs-alfresco-content-store/blob/master/LICENSE.txt
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 */
package com.emc.ecs.alfresco;

import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.alfresco.repo.content.AbstractContentReader;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;

/**
 * @author seibed
 *
 */
public class EcsS3ContentReader extends AbstractContentReader implements ContentReader {

    private final EcsS3Adapter _adapter;

    /**
     * @param contentUrl
     * @param adapter 
     */
    protected EcsS3ContentReader(String contentUrl, EcsS3Adapter adapter) {
        super(contentUrl);
        _adapter = adapter;
    }

    /* (non-Javadoc)
     * @see org.alfresco.service.cmr.repository.ContentAccessor#getSize()
     */
    @Override
    public long getSize() {
        return _adapter.getSize(getContentUrl());
    }

    /* (non-Javadoc)
     * @see org.alfresco.service.cmr.repository.ContentReader#exists()
     */
    @Override
    public boolean exists() {
        return _adapter.exists(getContentUrl());
    }

    /* (non-Javadoc)
     * @see org.alfresco.service.cmr.repository.ContentReader#getLastModified()
     */
    @Override
    public long getLastModified() {
        return _adapter.getLastModified(getContentUrl());
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.content.AbstractContentReader#createReader()
     */
    @Override
    protected ContentReader createReader() throws ContentIOException {
        return new EcsS3ContentReader(getContentUrl(), _adapter);
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.content.AbstractContentReader#getDirectReadableChannel()
     */
    @Override
    protected ReadableByteChannel getDirectReadableChannel() throws ContentIOException {
        if (!exists()) {
            throw new ContentIOException("File content does not exist [url=" + getContentUrl() + "]");
        }

        try {
            return Channels.newChannel(_adapter.getInputStream(getContentUrl()));
        } catch (Exception e) {
            throw new ContentIOException("Failed to read content for [url=" + getContentUrl() + "], " + e.getMessage(), e);
        }
    }

}
