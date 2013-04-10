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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.text.StrLookup;

/**
 * 
 * @author Hussachai Puripunpinyo (http://www.siberhus.com)
 * 
 */
public class WebVarLookup extends StrLookup {
	
	private ServletContext servletContext;
	private HttpServletRequest request;
	
	public WebVarLookup(HttpServletRequest request){
		this.request = request;
		this.servletContext = request.getSession().getServletContext();
	}
	
	@Override
	public String lookup(String key) {
		StrLookup lookup = new RequestVarLookup(request);
		String value = null;
		if( (value=lookup.lookup(key))==null){
			lookup = new SessionVarLookup(request.getSession());
			if( (value=lookup.lookup(key))==null){
				lookup = new ApplicationVarLookup(servletContext);
				return lookup.lookup(key);
			}
		}
		return value;
	}
	
	public static class ApplicationVarLookup extends StrLookup {
		private ServletContext context;
		public ApplicationVarLookup(ServletContext context){
			this.context = context;
		}
		@Override
		public String lookup(String key) {
			Object value = context.getAttribute(key);
			if(value!=null){
				return value.toString();
			}
			return null;
		}
	}
	
	public static class RequestVarLookup extends StrLookup {
		private HttpServletRequest request;
		public RequestVarLookup(HttpServletRequest request){
			this.request = request;
		}
		@Override
		public String lookup(String key) {
			Object value = request.getAttribute(key);
			if(value!=null){
				return value.toString();
			}
			return null;
		}
	}
	
	public static class SessionVarLookup extends StrLookup {
		private HttpSession session;
		public SessionVarLookup(HttpSession session){
			this.session = session;
		}
		@Override
		public String lookup(String key) {
			Object value = session.getAttribute(key);
			if(value!=null){
				return value.toString();
			}
			return null;
		}
	}
}
