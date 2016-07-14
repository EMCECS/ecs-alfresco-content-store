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
