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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * 
 * @author Hussachai Puripunpinyo (http://www.siberhus.com)
 * 
 */
public abstract class BaseCkeditorTagHandler extends SimpleTagSupport implements
		DynamicAttributes {

	private Map<String, Object> tagAttributes = new HashMap<String, Object>();

	@Override
	public void setDynamicAttribute(String uri, String localName, Object value)
			throws JspException {
		tagAttributes.put(localName, value);
	}

	public Map<String, Object> getTagAttributes() {
		return tagAttributes;
	}

	public PageContext getPageContext() {
		return (PageContext) getJspContext();
	}

	public HttpServletRequest getRequest() {
		return (HttpServletRequest) getPageContext().getRequest();
	}

	public HttpServletResponse getResponse() {
		return (HttpServletResponse) getPageContext().getResponse();
	}

}
