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
