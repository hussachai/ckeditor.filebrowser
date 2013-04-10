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
import java.io.StringWriter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;

import com.siberhus.web.ckeditor.Ckeditor;

/**
 * 
 * @author Hussachai Puripunpinyo (http://www.siberhus.com)
 * 
 */
public class CkeditorEditorTagHandler extends BaseCkeditorTagHandler {
	
	private String id;
	private String name;
	private String userSpace;
	private String fileBrowser;
	private boolean showThumbs;
	private boolean removeInstance;
	private String options;

	@Override
	public void doTag() throws JspException, IOException {
		JspFragment jspFragment = getJspBody();
		StringWriter body = new StringWriter();
		if(jspFragment!=null){
			jspFragment.invoke(body);
		}
		getTagAttributes().put("id", id);
		getTagAttributes().put("name", name);
		getTagAttributes().put("userSpace", userSpace);
		getTagAttributes().put("fileBrowser", fileBrowser);
		getTagAttributes().put("showThumbs", showThumbs);
		getTagAttributes().put("removeInstance", removeInstance);
		getTagAttributes().put("options", options);
		Ckeditor editor = new Ckeditor(getRequest(), getTagAttributes(),
				body.toString());
		getJspContext().getOut().write(editor.renderEditor());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserSpace() {
		return userSpace;
	}

	public void setUserSpace(String userSpace) {
		this.userSpace = userSpace;
	}

	public String getFileBrowser() {
		return fileBrowser;
	}

	public void setFileBrowser(String fileBrowser) {
		this.fileBrowser = fileBrowser;
	}

	public boolean isShowThumbs() {
		return showThumbs;
	}

	public void setShowThumbs(boolean showThumbs) {
		this.showThumbs = showThumbs;
	}

	public boolean isRemoveInstance() {
		return removeInstance;
	}

	public void setRemoveInstance(boolean removeInstance) {
		this.removeInstance = removeInstance;
	}

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

}
