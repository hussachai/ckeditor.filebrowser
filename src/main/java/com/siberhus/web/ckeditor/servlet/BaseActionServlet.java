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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Hussachai Puripunpinyo (http://www.siberhus.com)
 * 
 */
public class BaseActionServlet  extends HttpServlet{
	
	private static final long serialVersionUID = 1L;
	
	private Logger log = LoggerFactory.getLogger(BaseActionServlet.class);
	
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String requestURI = request.getRequestURI();
		if(requestURI!=null && requestURI.lastIndexOf("/")!=-1){
			String actionName = requestURI.substring(requestURI.lastIndexOf("/")+1, requestURI.length());
			int paramIdx = actionName.indexOf("?");
			if(paramIdx!=-1){
				actionName = actionName.substring(0, actionName.indexOf("?"));
			}
			Method method = null;
			try {
				method = this.getClass().getMethod(actionName, HttpServletRequest.class, 
						HttpServletResponse.class);
			} catch (Exception e) {
				e.printStackTrace();
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Action="
					+actionName+" not found for servlet="+this.getClass());
				return;
			}
			try {
				boolean isMultipart = ServletFileUpload.isMultipartContent(request);
				if(isMultipart){
					request = new MultipartServletRequest(request);
					log.debug("Files *********************");
					MultipartServletRequest mrequest = (MultipartServletRequest)request;
					for(FileItem fileItem: mrequest.getFileItems()){
						log.debug("File[fieldName={}, fileName={}, fileSize={}]", new Object[]{
							fileItem.getFieldName(),fileItem.getName(),fileItem.getSize()
						});
					}
				}
				if(log.isDebugEnabled()){
					log.debug("Parameters **************************");
					Enumeration<String> paramNames = request.getParameterNames();
					while(paramNames.hasMoreElements()){
						String paramName = paramNames.nextElement();
						log.debug("Param[name={},value(s)={}]",new Object[]{paramName, 
							Arrays.toString(request.getParameterValues(paramName))});
					}
				}
				
				Object result = method.invoke(this, request, response);
				
				if(result instanceof StreamingResult){
					if(!response.isCommitted()){
						((StreamingResult)result).execute(request, response);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				if(e instanceof InvocationTargetException){
					throw new ServletException(((InvocationTargetException)e).getTargetException());
				}
				throw new ServletException(e);
			}
		}
	}
	
}
