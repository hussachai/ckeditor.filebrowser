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
package com.siberhus.web.ckeditor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * 
 * @author Hussachai Puripunpinyo (http://www.siberhus.com)
 * 
 */
public class Ckeditor {
	
	private CkeditorTagConfig tagConfig;
	private String initialValue = "";
	
	public Ckeditor(HttpServletRequest request, Map<String, Object> attrs,
			String value) {
		this.tagConfig = new CkeditorTagConfig(request, attrs);
		this.initialValue = value;
	}
	
	public String renderResources(boolean minified) {
		return "<script type=\"text/javascript\" src=\"" + this.tagConfig.basePath
				+ "/ckeditor/ckeditor/ckeditor" + (minified ? "" : "_source")
				+ ".js\"></script>";
	}

	public String renderEditor() {
		StringBuilder outb = new StringBuilder();

		if (!this.tagConfig.append) {
			outb.append("<textarea id=\""+tagConfig.instanceId+"\" name=\"");
			outb.append(this.tagConfig.instanceName);
			outb.append("\">");
			outb.append(StringEscapeUtils.escapeHtml(this.initialValue));
			outb.append("</textarea>\n");
		}
		outb.append("<script type=\"text/javascript\">\n");

		if (this.tagConfig.removeInstance) {
			outb.append("if (CKEDITOR.instances['" + tagConfig.instanceId
					+ "']){CKEDITOR.remove(CKEDITOR.instances['"
					+ this.tagConfig.instanceId + "']);}\n");
		}

		outb.append("CKEDITOR.");
		if (this.tagConfig.append) {
			outb.append("appendTo");
		} else {
			outb.append("replace");
		}
		outb.append("('"+tagConfig.instanceId+"'");
		outb.append(this.tagConfig.getConfiguration());
		outb.append(");\n");
		outb.append("</script>\n");
		return outb.toString();
	}

	public String renderFileBrowser() {
		StringBuilder outb = new StringBuilder();
		outb.append("<a href=");
		outb.append("\"");
		outb.append(renderFileBrowserLink());
		outb.append("\" ");
		if (this.tagConfig.target != null) {
			outb.append("target=");
			outb.append("\"");
			outb.append(this.tagConfig.target);
			outb.append("\" ");
		}
		outb.append(">");
		outb.append(StringEscapeUtils.escapeHtml(this.initialValue));
		outb.append("</a>");
		return outb.toString();
	}

	public String renderFileBrowserLink() {
		return this.tagConfig.getBrowseUrl(this.tagConfig.type,
				this.tagConfig.userSpace, this.tagConfig.fileBrowser,
				this.tagConfig.showThumbs);
	}

}
