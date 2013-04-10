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

import com.siberhus.web.ckeditor.CkeditorTagConfig;

/**
 * 
 * @author Hussachai Puripunpinyo (http://www.siberhus.com)
 * 
 */
public class CkeditorConfigTagHandler extends BaseCkeditorTagHandler {

	private String var;

	@Override
	public void doTag() throws JspException, IOException {
		
		CkeditorTagConfig cfg = new CkeditorTagConfig(getRequest(),
				getTagAttributes());
		try {
			if (var != null) {
				JspFragment jspFragment = getJspBody();
				String value = null;
				if(jspFragment!=null){
					StringWriter body = new StringWriter();
					jspFragment.invoke(body);
					value = body.toString();
				}
				cfg.addComplexConfigItem(var, value);
			} else {
				cfg.addConfigItem(getTagAttributes(), false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new JspException(e);
		}
	}

	public String getVar() {
		return var;
	}

	public void setVar(String var) {
		this.var = var;
	}

}