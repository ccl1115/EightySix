/**
4 * Copyright (c) 2012 The Wiseserc. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 */
package com.aliyun.android.oss.task;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;

import com.aliyun.android.oss.OSSException;
import com.aliyun.android.oss.http.HttpMethod;
import com.aliyun.android.oss.http.OSSHttpTool;
import com.aliyun.android.oss.model.ObjectMetaData;
import com.aliyun.android.util.Helper;
import org.apache.http.entity.InputStreamEntity;

/**
 * 上传Object任务
 * 
 * @author Michael
 */
public class PutObjectTask extends Task {
    /**
     * object key
     */
    private String objectKey;

    /**
     * 所属的bucket名字
     */
    private String bucketName;

    /**
     * Object 元数据
     */
    private ObjectMetaData objectMetaData;

    /**
     * 上传的文件
     */
    private String uploadFilePath;

    /**
     * 上传的文件数据
     */
    private byte[] data;

    /**
     * 上传文件的流
     */
    private InputStream inputStream;

    /**
     *
     */
    private long length;

    public PutObjectTask(String bucketName, String objectKey, String contentType) {
        super(HttpMethod.PUT);
        this.objectKey = objectKey;
        this.bucketName = bucketName;
        this.objectMetaData = new ObjectMetaData();
        this.objectMetaData.setContentType(contentType);
    }

    public PutObjectTask(String bucketName, String objectKey, String contentType, byte[] data) {
        this(bucketName, objectKey, contentType);
        this.data = data;
    }

    public PutObjectTask(String bucketName, String objectKey, String contentType, InputStream inputStream, long length) {
        this(bucketName, objectKey, contentType);
        this.inputStream = inputStream;
        this.length = length;
    }

    public PutObjectTask(String bucketName, String objectKey, String contentType, String path) throws OSSException {
        this(bucketName, objectKey, contentType);
        try {
            this.inputStream = new FileInputStream(path);
            this.length = new File(path).length();
        } catch (IOException e) {
            throw new OSSException(e);
        }

    }

    /**
     * 参数合法性验证
     */
    @Override
    protected void checkArguments() {
        if (Helper.isEmptyString(bucketName) || Helper.isEmptyString(objectKey)) {
            throw new IllegalArgumentException(
                    "bucketName or objectKey not set");
        }
        if (objectMetaData == null
                || Helper.isEmptyString(objectMetaData.getContentType())) {
            throw new IllegalArgumentException(
                    "ObjectMetaData not properly set");
        }
    }

    /**
     * 构造HttpPut
     */
    protected HttpUriRequest generateHttpRequest() {
        // 生成Http请求
        String resource = httpTool.generateCanonicalizedResource("/"
                + bucketName + "/" + objectKey);

        HttpPut httpPut = new HttpPut("http://" + bucketName + "." + OSS_END_POINT + "/" + resource);

        // 构造HttpPut
        String dateStr = Helper.getGMTDate();
        String xossHeader = OSSHttpTool
                .generateCanonicalizedHeader(objectMetaData.getAttrs());
        String authorization = OSSHttpTool.generateAuthorization(accessId,
                accessKey, httpMethod.toString(), "",
                objectMetaData.getContentType(), dateStr, xossHeader, resource);

        httpPut.setHeader(AUTHORIZATION, authorization);
        httpPut.setHeader(DATE, dateStr);
        httpPut.setHeader(HOST, OSS_HOST);

        OSSHttpTool.addHttpRequestHeader(httpPut, CACHE_CONTROL,
                objectMetaData.getCacheControl());
        OSSHttpTool.addHttpRequestHeader(httpPut, CONTENT_DISPOSITION,
                objectMetaData.getContentDisposition());
        OSSHttpTool.addHttpRequestHeader(httpPut, CONTENT_ENCODING,
                objectMetaData.getContentEncoding());
        OSSHttpTool.addHttpRequestHeader(httpPut, CONTENT_TYPE,
                objectMetaData.getContentType());
        OSSHttpTool.addHttpRequestHeader(httpPut, EXPIRES,
                Helper.getGMTDate(objectMetaData.getExpirationTime()));

        // 加入用户自定义header
        for (Entry<String, String> entry: objectMetaData.getAttrs().entrySet()) {
            OSSHttpTool.addHttpRequestHeader(httpPut, entry.getKey(), entry.getValue());
        }

        if (this.data != null && this.data.length > 0) {
            httpPut.setEntity(new ByteArrayEntity(this.data));
        } else if (this.inputStream != null) {
            httpPut.setEntity(new InputStreamEntity(inputStream, length));
        }

        return httpPut;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getUploadFilePath() {
        return uploadFilePath;
    }

    public void setUploadFilePath(String uploadFilePath) {
        this.uploadFilePath = uploadFilePath;
    }

    public ObjectMetaData getObjectMetaData() {
        return objectMetaData;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    /**
     * 获取Put Object的结果：OSS收到文件的MD5值，以便用户检查
     * @return OSS收到文件的MD5值
     * 
     * @throws com.aliyun.android.oss.OSSException
     */
    public String getResult() throws OSSException {
        HttpResponse r = this.execute();
        Header etagHeader = r.getFirstHeader("ETag");
        if (etagHeader == null) {
            throw new OSSException("no ETag header returned from oss.");
        }
        String value = etagHeader.getValue();
        
        //去掉返回值首尾的"
        while (value.startsWith("\"")) {
            value = value.substring(1);
        }
        while (value.endsWith("\"")) {
            value = value.substring(0, value.length() - 1);
        }
        return value;
    }
}
