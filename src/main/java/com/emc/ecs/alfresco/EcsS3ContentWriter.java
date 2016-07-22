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
import java.io.FileOutputStream;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

import org.alfresco.repo.content.AbstractContentWriter;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.util.TempFileProvider;

/**
 * @author seibed
 *
 */
public class EcsS3ContentWriter extends AbstractContentWriter implements ContentWriter {

    private final EcsS3Adapter _adapter;

    private File tempFile;

    /**
     * @param existingContentReader
     * @param adapter 
     */
    protected EcsS3ContentWriter(String contentUrl, ContentReader existingContentReader, EcsS3Adapter adapter) {
        super(contentUrl, existingContentReader);
        _adapter = adapter;
        addListener(new EcsS3ContentStreamListener(_adapter, this));
    }

    /* (non-Javadoc)
     * @see org.alfresco.service.cmr.repository.ContentAccessor#getSize()
     */
    @Override
    public long getSize() {
        return _adapter.getSize(getContentUrl());
    }

    public File getTempFile() {
        return tempFile;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.content.AbstractContentWriter#createReader()
     */
    @Override
    protected ContentReader createReader() throws ContentIOException {
        return new EcsS3ContentReader(getContentUrl(), _adapter);
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.content.AbstractContentWriter#getDirectWritableChannel()
     */
    @Override
    protected WritableByteChannel getDirectWritableChannel() throws ContentIOException {
        try {
            tempFile = TempFileProvider.createTempFile(getContentUrl(), ".s3");
            return Channels.newChannel(new FileOutputStream(tempFile));
        } catch (Exception e) {
            throw new ContentIOException("Failed to open channel. " + this, e);
        }
    }

}
