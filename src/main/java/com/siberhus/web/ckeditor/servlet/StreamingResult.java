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
package com.siberhus.web.ckeditor.servlet;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Hussachai Puripunpinyo (http://www.siberhus.com)
 * 
 */
public class StreamingResult {

	Logger log = LoggerFactory.getLogger(StreamingResult.class);

	/** Date format string for RFC 822 dates. */
	private static final String RFC_822_DATE_FORMAT = "EEE, d MMM yyyy HH:mm:ss Z";
	private InputStream inputStream;
	private Reader reader;
	private String filename;
	private String contentType;
	private String characterEncoding;
	private long lastModified = -1;
	private long length = -1;
	private boolean attachment;

	public StreamingResult(String contentType) {
		this.contentType = contentType;
	}

	public StreamingResult(String contentType, InputStream inputStream) {
		this.contentType = contentType;
		this.inputStream = inputStream;
	}

	public StreamingResult(String contentType, Reader reader) {
		this.contentType = contentType;
		this.reader = reader;
	}

	public StreamingResult(String contentType, String output) {
		this(contentType, new StringReader(output));
	}

	public StreamingResult setFilename(String filename) {
		this.filename = filename;
		setAttachment(filename != null);
		return this;
	}

	public void setCharacterEncoding(String characterEncoding) {
		this.characterEncoding = characterEncoding;
	}

	public StreamingResult setLastModified(long lastModified) {
		this.lastModified = lastModified;
		return this;
	}

	public StreamingResult setLength(long length) {
		this.length = length;
		return this;
	}

	public StreamingResult setAttachment(boolean attachment) {
		this.attachment = attachment;
		return this;
	}

	final public void execute(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		applyHeaders(response);
		stream(response);
	}

	protected void applyHeaders(HttpServletResponse response) {
		response.setContentType(this.contentType);
		if (this.characterEncoding != null) {
			response.setCharacterEncoding(characterEncoding);
		}

		// Set Content-Length header
		if (length >= 0) {
			// Odd that ServletResponse.setContentLength is limited to int.
			// requires downcast from long to int e.g.
			// response.setContentLength((int)length);
			// Workaround to allow large files:
			response.addHeader("Content-Length", Long.toString(length));
		}

		// Set Last-Modified header
		if (lastModified >= 0) {
			response.setDateHeader("Last-Modified", lastModified);
		}

		// For Content-Disposition spec, see http://www.ietf.org/rfc/rfc2183.txt
		if (attachment || filename != null) {
			// Value of filename should be RFC 2047 encoded here (see RFC 2616)
			// but few browsers
			// support that, so just escape the quote for now
			String escaped = this.filename.replace("\"", "\\\"");
			StringBuilder header = new StringBuilder(attachment ? "attachment"
					: "inline").append(";filename=\"").append(escaped)
					.append("\"");
			if (lastModified >= 0) {
				SimpleDateFormat format = new SimpleDateFormat(
						RFC_822_DATE_FORMAT);
				String value = format.format(new Date(lastModified));
				header.append(";modification-date=\"").append(value)
						.append("\"");
			}
			if (length >= 0) {
				header.append(";size=").append(length);
			}
			response.setHeader("Content-Disposition", header.toString());
		}
	}

	protected void stream(HttpServletResponse response) throws Exception {
		int length = 0;
		if (this.reader != null) {
			char[] buffer = new char[512];
			try {
				PrintWriter out = response.getWriter();

				while ((length = this.reader.read(buffer)) != -1) {
					out.write(buffer, 0, length);
				}
			} finally {
				try {
					this.reader.close();
				} catch (Exception e) {
					log.warn("Error closing reader", e);
				}
			}
		} else if (this.inputStream != null) {
			byte[] buffer = new byte[512];
			try {
				ServletOutputStream out = response.getOutputStream();

				while ((length = this.inputStream.read(buffer)) != -1) {
					out.write(buffer, 0, length);
				}
			} finally {
				try {
					this.inputStream.close();
				} catch (Exception e) {
					log.warn("Error closing input stream", e);
				}
			}
		} else {
			throw new RuntimeException(
					"A StreamingResult was constructed without "
							+ "supplying a Reader or InputStream, but stream() was not overridden. Please "
							+ "either supply a source of streaming data, or override the stream() method.");
		}
	}

}