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
