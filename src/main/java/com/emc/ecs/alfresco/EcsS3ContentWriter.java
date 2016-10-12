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
