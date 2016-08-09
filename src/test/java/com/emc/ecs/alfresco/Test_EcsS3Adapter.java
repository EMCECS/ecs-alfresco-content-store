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

import java.io.OutputStream;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author seibed
 *
 */
public class Test_EcsS3Adapter extends Assert {

    @Test
    public void testGetBucketName() throws Exception {
        EcsS3Adapter adapter = new EcsS3Adapter();
        assertEquals("alfresco-content", adapter.getBucketName(null));
        assertEquals("alfresco-content", adapter.getBucketName("some url"));
    }

    @Test
    public void testGetKey() throws Exception {
        EcsS3Adapter adapter = new EcsS3Adapter();
        assertEquals("", adapter.getKey(null));
        assertEquals("", adapter.getKey(""));
        assertEquals("", adapter.getKey("ecsS3://"));
        assertEquals("myKey", adapter.getKey("ecsS3://myKey"));
        assertEquals("myKey2", adapter.getKey("ecsS3://myKey2"));
    }

    @Test
    public void testExists() throws Exception {
        EcsS3Adapter adapter = new EcsS3Adapter();
        assertEquals(false, adapter.exists(null));
        assertEquals(false, adapter.exists("some-url"));
    }

    @Test
    public void testGetSize() throws Exception {
        EcsS3Adapter adapter = new EcsS3Adapter();
        assertEquals(0, adapter.getSize(null));
        assertEquals(0, adapter.getSize("some-url"));
    }

    @Test
    public void testGetLastModified() throws Exception {
        EcsS3Adapter adapter = new EcsS3Adapter();
        assertEquals(0, adapter.getLastModified(null));
        assertEquals(0, adapter.getLastModified("some-url"));
    }

    @Test
    public void testDelete() throws Exception {
        EcsS3Adapter adapter = new EcsS3Adapter();
        try {
            adapter.delete("some-url");
            fail("This should have thrown an exception.");
        } catch (Exception e) {
            // do nothing, this is expected.
        }
    }

    @Test
    public void testGetInputStream() throws Exception {
        EcsS3Adapter adapter = new EcsS3Adapter();
        try {
            adapter.getInputStream("some-url");
            fail("This should have thrown an exception.");
        } catch (Exception e) {
            // do nothing, this is expected.
        }
    }

    @Test
    public void testCloseStreamAndDelete() throws Exception {
        EcsS3Adapter adapter = new EcsS3Adapter();
        String contentUrl = "ecsS3://myKey";
        try {
            EcsS3ContentWriter writer = new EcsS3ContentWriter(contentUrl, new EcsS3ContentReader(contentUrl, adapter), adapter);
            adapter.closeStream(writer);
            fail("This should have thrown an exception.");
        } catch (Exception e) {
            // do nothing, this is expected.
        }
        assertFalse(adapter.exists(contentUrl));
    }

    @Test
    public void testContentWritingAndDeletion() throws Exception {
        EcsS3Adapter adapter = new EcsS3Adapter();
        String contentUrl = "ecsS3://myKey";
        EcsS3ContentWriter writer = new EcsS3ContentWriter(contentUrl, new EcsS3ContentReader(contentUrl, adapter), adapter);
        OutputStream outputStream = writer.getContentOutputStream();
        byte[] content = "some content".getBytes();
        outputStream.write(content);
        outputStream.close();
//        System.out.println(">>> Checking for object existence...");
        assertTrue(adapter.exists(contentUrl));
        assertEquals(content.length, adapter.getSize(contentUrl));
        System.out.println(">>> Deleting object...");
        adapter.delete(contentUrl);
        assertFalse(adapter.exists(contentUrl));
    }

}
