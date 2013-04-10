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
package com.siberhus.web.ckeditor.tags;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang.StringUtils;

import com.siberhus.web.ckeditor.CkeditorConfigurationHolder;
import com.siberhus.web.ckeditor.CkeditorConfigurationHolder.CkeditorConfig;
import com.siberhus.web.ckeditor.utils.PathUtils;

/**
 * 
 * @author Hussachai Puripunpinyo (http://www.siberhus.com)
 * 
 */
public class OfmBaseUrlTagHandler extends BaseOpenFileManagerTagHandler {
	
	private String space;
	private String type;

	@Override
	public void doTag() throws JspException, IOException {
		HttpServletRequest request = (HttpServletRequest)getPageContext().getRequest();
		String bUrl = PathUtils.getBaseUrl(request, space, type);
		JspWriter writer = getJspContext().getOut();
		CkeditorConfig config = CkeditorConfigurationHolder.config();
		String userDefinedBaseUrl = config.upload().baseurl(request);
		if (StringUtils.isNotBlank(userDefinedBaseUrl)) {
			bUrl = PathUtils.checkSlashes(userDefinedBaseUrl, "L- R+", true)
					+ PathUtils.checkSlashes(bUrl, "R-", true);
			writer.write(bUrl);
		} else {
			bUrl = PathUtils.checkSlashes(bUrl, "R-", false);
			writer.write(getPageContext().getServletContext().getContextPath() + "/" + bUrl);
		}
	}

	public String getSpace() {
		return space;
	}

	public void setSpace(String space) {
		this.space = space;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
