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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.siberhus.web.ckeditor.CkeditorConfigurationHolder;
import com.siberhus.web.ckeditor.CkeditorConfigurationHolder.CkeditorConfig;
import com.siberhus.web.ckeditor.utils.Literal;

/**
 * 
 * @author Hussachai Puripunpinyo (http://www.siberhus.com)
 * 
 */
public class MultipartServletRequest extends HttpServletRequestWrapper{
	
	private List<FileItem> fileItems = new ArrayList<FileItem>();
	
	private Map<String, FileItem> fileItemMap = new HashMap<String, FileItem>();
	
	private Map<String, List<String>> paramMap = new HashMap<String, List<String>>();
	
	public MultipartServletRequest(HttpServletRequest request) throws FileUploadException {
		super(request);
		
//		if(!"POST".equals(request.getMethod())){
//			return;
//		}
		CkeditorConfig config = CkeditorConfigurationHolder.config();
		// Create a factory for disk-based file items
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// Set factory constraints
		if(config.fileupload().sizeThreshold()!=null){
			factory.setSizeThreshold(config.fileupload().sizeThreshold());
		}
		if(config.fileupload().repository()!=null){
			factory.setRepository(config.fileupload().repository());
		}
		
		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);
		
		// Set overall request size constraint
		if(config.fileupload().sizeMax()!=null){
			upload.setSizeMax(config.fileupload().sizeMax());
		}
		if(config.fileupload().fileSizeMax()!=null){
			upload.setFileSizeMax(config.fileupload().fileSizeMax());
		}
		// Copy params from query string
		Enumeration<String> paramNames = request.getParameterNames();
		while(paramNames.hasMoreElements()){
			String paramName = paramNames.nextElement();
			String paramValues[] = request.getParameterValues(paramName);
			if(paramValues!=null){
				this.paramMap.put(paramName, Literal.list(paramValues));
			}
		}
		
		@SuppressWarnings("unchecked")
		List<FileItem> itemList = upload.parseRequest(request);
		
		for(FileItem item : itemList){
			String fieldName = item.getFieldName();
			if(item.isFormField()){
				List<String> values = paramMap.get(fieldName);
				if(values==null){
					paramMap.put(fieldName, Literal.list(item.getString()));
				}else{
					values.add(item.getString());
				}
			}else{
				fileItemMap.put(fieldName, item);
				fileItems.add(item);
			}
		}
	}
	
	public FileItem getFileItem(String fieldName){
		return fileItemMap.get(fieldName);
	}
	
	public List<FileItem> getFileItems(){
		return fileItems;
	}
	
	@Override
	public String getParameter(String name) {
		List<String> values = paramMap.get(name);
		if(values!=null && values.size()>0){
			return values.get(0);
		}else{
			return null;
		}
	}
	
	@Override
	public Enumeration<String> getParameterNames() {
		final Iterator<String> params = paramMap.keySet().iterator();
		return new Enumeration<String>() {
			@Override
			public boolean hasMoreElements() {
				return params.hasNext();
			}
			@Override
			public String nextElement() {
				return params.next();
			}
		};
	}
	
	@Override
	public String[] getParameterValues(String name) {
		List<String> values = paramMap.get(name);
		if(values!=null){
			return values.toArray(new String[0]);
		}
		return null;
	}
	
}
