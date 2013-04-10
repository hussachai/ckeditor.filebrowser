/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package com.siberhus.web.ckeditor.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

/**
 * 
 * @author Hussachai Puripunpinyo (http://www.siberhus.com)
 * 
 */
public class MimeUtils {

	public static final Map<String, String> MIME_TYPES;

	static {
		Map<String, String> mimeTypes = new HashMap<String, String>();
		mimeTypes.put("html", "text/html");
		mimeTypes.put("htm", "text/html");
		mimeTypes.put("xml", "text/xml");
		mimeTypes.put("txt", "text/plain");
		mimeTypes.put("js", "text/javascript");
		mimeTypes.put("rss", "application/rss+xml");
		mimeTypes.put("atom", "application/atom+xml");
		mimeTypes.put("css", "text/css");
		mimeTypes.put("csv", "text/csv");
		mimeTypes.put("json", "application/json");
		mimeTypes.put("pdf", "application/pdf");
		mimeTypes.put("doc", "application/msword");
		mimeTypes.put("png", "image/png");
		mimeTypes.put("gif", "image/gif");
		mimeTypes.put("jpg", "image/jpeg");
		mimeTypes.put("jpeg", "image/jpeg");
		mimeTypes.put("swf", "application/x-shockwave-flash");
		mimeTypes.put("mov", "video/quicktime");
		mimeTypes.put("qt", "video/quicktime");
		mimeTypes.put("avi", "video/x-msvideo");
		mimeTypes.put("asf", "video/x-ms-asf");
		mimeTypes.put("asr", "video/x-ms-asf");
		mimeTypes.put("asx", "video/x-ms-asf");
		mimeTypes.put("mpa", "video/mpeg");
		mimeTypes.put("mpg", "video/mpeg");
		mimeTypes.put("mp2", "video/mpeg");
		mimeTypes.put("rtf", "application/rtf");
		mimeTypes.put("exe", "application/octet-stream");
		mimeTypes.put("xls", "application/vnd.ms-excel");
		mimeTypes.put("xlt", "application/vnd.ms-excel");
		mimeTypes.put("xlc", "application/vnd.ms-excel");
		mimeTypes.put("xlw", "application/vnd.ms-excel");
		mimeTypes.put("xla", "application/vnd.ms-excel");
		mimeTypes.put("xlm", "application/vnd.ms-excel");
		mimeTypes.put("ppt", "application/vnd.ms-powerpoint");
		mimeTypes.put("pps", "application/vnd.ms-powerpoint");
		mimeTypes.put("tgz", "application/x-compressed");
		mimeTypes.put("gz", "application/x-gzip");
		mimeTypes.put("zip", "application/zip");
		mimeTypes.put("mp3", "audio/mpeg");
		mimeTypes.put("mid", "audio/mid");
		mimeTypes.put("ico", "image/x-icon");
		MIME_TYPES = Collections.unmodifiableMap(mimeTypes);
	}

	public static String getDefaultMimeType(String fileName) {
		String fileExt = FilenameUtils.getExtension(fileName);
		if (fileExt != null) {
			String mimeType = MIME_TYPES.get(fileExt.toLowerCase());
			if (mimeType != null) {
				return mimeType;
			}
			return "application/octet-stream";
		} else {
			return "text/plain";
		}
	}

	public static String getMimeTypeByExt(String fileExt) {
		String result = "text/plain";
		if (fileExt != null) {
			String mimeType = MIME_TYPES.get(fileExt.toLowerCase());
			if (mimeType != null) {
				return mimeType;
			}
			return "application/octet-stream";
		}

		return result;
	}

	public static void main(String[] args) {
		// remove: . \ / | : ? * " ' ` ~ < > {space}
		String a = "Hello < * ' | / \\ . ? :  '>";
		System.out.println(a);
		a = a.replaceAll("\\.|\\/|\\\\|\\||:|\\?|\\*|\"|'|~|`|<|>| ", "");
		System.out.println(a);
	}
}
