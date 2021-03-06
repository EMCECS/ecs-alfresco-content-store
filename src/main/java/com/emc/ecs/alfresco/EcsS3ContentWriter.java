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

    /**
     * The adapter that does all of the work.
     */
    private final EcsS3Adapter _adapter;

    /**
     * The temporary file to which data will be streamed.
     */
    private File tempFile;

    /**
     * @param contentUrl The Alfresco URL.
     * @param existingContentReader The reader for the content.
     * @param adapter The adapter that does all of the work.
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

    /**
     * @return The temporary file to which data will be streamed.
     */
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
