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

import org.alfresco.repo.content.AbstractContentStreamListener;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentStreamListener;

/**
 * @author seibed
 *
 */
public class EcsS3ContentStreamListener extends AbstractContentStreamListener implements ContentStreamListener {

    /**
     * The adapter that does the work.
     */
    private final EcsS3Adapter _adapter;

    /**
     * The writer that was streaming data to a temporary file.
     */
    private final EcsS3ContentWriter _writer;

    /**
     * @param adapter The adapter that does the work.
     * @param writer The writer that was streaming data to a temporary file.
     */
    public EcsS3ContentStreamListener(EcsS3Adapter adapter, EcsS3ContentWriter writer) {
        _adapter = adapter;
        _writer = writer;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.content.AbstractContentStreamListener#contentStreamClosedImpl()
     */
    @Override
    public void contentStreamClosedImpl() throws ContentIOException {
        try {
            _adapter.closeStream(_writer);
        } catch (Exception e) {
            throw new ContentIOException("Error saving " + _writer.getContentUrl(), e);
        }
    }

}
