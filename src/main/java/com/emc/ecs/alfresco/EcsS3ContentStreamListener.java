/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 *
 * Licensed under the EMC Software License Agreement for Free Software (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * https://github.com/EMCECS/ecs-alfresco-content-store/blob/master/LICENSE.txt
 *
 * or in the "LICENSE.txt" file accompanying this file.
 *
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

    private final EcsS3Adapter _adapter;

    private final EcsS3ContentWriter _writer;

    /**
     * @param adapter
     * @param writer
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
