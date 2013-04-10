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

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siberhus.web.ckeditor.utils.PathUtils;
import com.siberhus.web.ckeditor.utils.WebVarLookup;

/**
 * 
 * @author Hussachai Puripunpinyo (http://www.siberhus.com)
 * 
 */
@WebListener
public class CkeditorConfigurationHolder implements ServletContextListener{
	
	private final Logger log = LoggerFactory.getLogger(CkeditorConfigurationHolder.class);
	
	private static CkeditorConfig ckeditor = null;

	private Properties properties = new Properties();
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			init(sce.getServletContext());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {}
	
	
	public void init(ServletContext servletContext) throws Exception {
		InputStream inStream = this.getClass().getResourceAsStream("/ckeditor.properties");
		if (inStream == null) {
			log.info("Could not locate ckeditor.properties in classpath.");
			inStream = CkeditorConfigurationHolder.class
				.getResourceAsStream("/com/siberhus/web/ckeditor/ckeditor.properties");
		}
		properties.load(inStream);
		ckeditor = new CkeditorConfig();
		String prop = null;
		prop = getProperty("ckeditor.config");
		if (prop != null)
			ckeditor.config = prop;
		
		prop = getProperty("ckeditor.skipAllowedItemsCheck");
		if (prop != null)
			ckeditor.skipAllowedItemsCheck = Boolean.valueOf(prop);

		prop = getProperty("ckeditor.defaulFileBrowser");
		if (prop != null)
			ckeditor.defaultFileBrowser = prop;

		prop = getProperty("ckeditor.connectors.prefix");
		if (prop != null)
			ckeditor.connectors.prefix = PathUtils.checkSlashes(prop, "L- R-",true);

		prop = getProperty("ckeditor.upload.basedir");
		if (prop != null)
			ckeditor.upload.basedir = prop;

		prop = getProperty("ckeditor.upload.baseurl");
		if (prop != null){
			ckeditor.upload.baseurl = prop;
			if(!prop.toUpperCase().startsWith("HTTP")){
				ckeditor.upload.isBaseurlVar = true;
			}
		}
		prop = getProperty("ckeditor.upload.enableContentController");
		if (prop != null)
			ckeditor.upload.enableContentController = Boolean.valueOf(prop);

		prop = getProperty("ckeditor.upload.overwrite");
		if (prop != null)
			ckeditor.upload.overwrite = Boolean.valueOf(prop);

		// LINK
		prop = getProperty("ckeditor.upload.link.browser");
		if (prop != null)
			ckeditor.upload.link.browser = Boolean.valueOf(prop);

		prop = getProperty("ckeditor.upload.link.upload");
		if (prop != null)
			ckeditor.upload.link.upload = Boolean.valueOf(prop);

		prop = getProperty("ckeditor.upload.link.allowed");
		if (prop != null){
			for (String p : prop.split(","))
				ckeditor.upload.link.allowed.add(p);
			ckeditor.upload.link.allowed = Collections.unmodifiableList(ckeditor.upload.link.allowed);
		}
		prop = getProperty("ckeditor.upload.link.denied");
		if (prop != null){
			for (String p : prop.split(","))
				ckeditor.upload.link.denied.add(p);
			ckeditor.upload.link.denied = Collections.unmodifiableList(ckeditor.upload.link.denied);
		}
		// IMAGE
		prop = getProperty("ckeditor.upload.image.browser");
		if (prop != null)
			ckeditor.upload.image.browser = Boolean.valueOf(prop);
		
		prop = getProperty("ckeditor.upload.image.upload");
		if (prop != null)
			ckeditor.upload.image.upload = Boolean.valueOf(prop);

		prop = getProperty("ckeditor.upload.image.allowed");
		if (prop != null){
			for (String p : prop.split(","))
				ckeditor.upload.image.allowed.add(p);
			ckeditor.upload.image.allowed = Collections.unmodifiableList(ckeditor.upload.image.allowed);
		}
		prop = getProperty("ckeditor.upload.image.denied");
		if (prop != null){
			for (String p : prop.split(","))
				ckeditor.upload.image.denied.add(p);
			ckeditor.upload.image.denied = Collections.unmodifiableList(ckeditor.upload.image.denied);
		}
		// FLASH
		prop = getProperty("ckeditor.upload.flash.browser");
		if (prop != null)
			ckeditor.upload.flash.browser = Boolean.valueOf(prop);

		prop = getProperty("ckeditor.upload.flash.upload");
		if (prop != null)
			ckeditor.upload.flash.upload = Boolean.valueOf(prop);

		prop = getProperty("ckeditor.upload.flash.allowed");
		if (prop != null){
			for (String p : prop.split(","))
				ckeditor.upload.flash.allowed.add(p);
			ckeditor.upload.flash.allowed = Collections.unmodifiableList(ckeditor.upload.flash.allowed);
		}
		prop = getProperty("ckeditor.upload.flash.denied");
		if (prop != null){
			for (String p : prop.split(","))
				ckeditor.upload.flash.denied.add(p);
			ckeditor.upload.flash.denied = Collections.unmodifiableList(ckeditor.upload.flash.denied);
		}
		prop = getProperty("ckeditor.fileupload.sizeThreshold");
		if (prop != null)
			ckeditor.fileupload.sizeThreshold = Integer.valueOf(prop);
		prop = getProperty("ckeditor.fileupload.repository");
		if (prop != null){
			if(prop.startsWith("ctx:")){
				prop = servletContext.getRealPath(prop.substring(4));
			}
			File repository = new File(prop);
			if(!repository.exists()){
				throw new IllegalArgumentException("ckeditor.fileupload.repository=file not found");
			}
			ckeditor.fileupload.repository = repository;
		}
		prop = getProperty("ckeditor.fileupload.sizeMax");
		if (prop != null)
			ckeditor.fileupload.sizeMax = Long.valueOf(prop);
		prop = getProperty("ckeditor.fileupload.fileSizeMax");
		if (prop != null)
			ckeditor.fileupload.fileSizeMax = Long.valueOf(prop);
		
	}

	private String getProperty(String name) {
		return StringUtils.trimToNull(properties.getProperty(name));
	}
	
	public static CkeditorConfig config() {
		if (ckeditor == null) {
			throw new RuntimeException(
					"CkeditorConfiguration has not been initialized yet."
							+ "Add "
							+ CkeditorConfigurationHolder.class.getName()
							+ " to Bootstrap.Classes fitlter param");
		}
		return ckeditor;
	}
	
	public static class CkeditorConfig {
		/* File name, relative to the webapp root, for the custom config file */
		private String config = null;
		public String config(){ return config; }
		/* Skip the check on valid options names */
		private boolean skipAllowedItemsCheck = false;
		public boolean skipAllowedItemsCheck(){ return skipAllowedItemsCheck; }
		/*
		 * Define the default file browser if not defined in tags. Possible
		 * values are: standard, ofm
		 */
		private String defaultFileBrowser = CkeditorTagConfig.DEFAULT_FILEBROWSER;
		public String defaultFileBrowser(){ return defaultFileBrowser; }
		
		private Connectors connectors = new Connectors();
		public Connectors connectors(){ return connectors; }
		
		public static class Connectors {
			private String prefix = CkeditorTagConfig.DEFAULT_CONNECTORS_PREFIX;
			public String prefix(){ return prefix; }
		}
		
		private Upload upload = new Upload();
		public Upload upload(){ return upload; }
		
		public static class Upload {
			/*
			 * Base directory relative to webapp root if baseurl not defined,
			 * otherwise the absolute path where to store the uploaded files
			 */
			private String basedir = "/uploads/";
//			public String basedir(){ return basedir; }
			public String basedir(HttpServletRequest request){ 
				StrSubstitutor substitutor = new StrSubstitutor(new WebVarLookup(request), "${", "}",'$');
				return substitutor.replace(basedir);
			}
			/* The base URL to access the uploaded files */
			private String baseurl = null;
			private boolean isBaseurlVar = false;
			public String baseurl(HttpServletRequest request){
				if(isBaseurlVar){
					StrSubstitutor substitutor = new StrSubstitutor(new WebVarLookup(request), "${", "}",'$');
					return substitutor.replace(baseurl);
//					Object val = request.getAttribute(baseurl);
//					if(val==null){
//						val = request.getSession().getAttribute(baseurl);
//						if(val==null){
//							val = request.getServletContext().getAttribute(baseurl);
//						}
//					}
//					if(val!=null){
//						return val.toString();
//					}
				}
				return baseurl; 
			}
			/*
			 * Automatically create a mapping to view files when using baseurl
			 * setting
			 */
			private boolean enableContentController = false;
			public boolean enableContentController(){ return enableContentController; }
			
			/* Overwite files on upload */
			private boolean overwrite = false;
			public boolean overwrite(){ return overwrite; }
			
			private XObject link = new XObject();
			public XObject link(){ return link; }
			
			private XObject image = new XObject();
			public XObject image(){ return image; }
			
			private XObject flash = new XObject();
			public XObject flash(){ return flash; }
			
			public static class XObject {
				/* Enable file browser for x objects */
				private boolean browser = false;
				public boolean browser(){ return browser; }
				
				/* Enable upload tab for x objects */
				private boolean upload = false;
				public boolean upload(){ return upload; }
				
				/* Extensions allowed for x objects */
				private List<String> allowed = new ArrayList<String>();
				public List<String> allowed(){ return allowed; }
				
				/* Extensions denied for x objects */
				private List<String> denied = new ArrayList<String>();
				public List<String> denied(){ return denied; }
			}
		}
		
		private FileUpload fileupload = new FileUpload();
		public FileUpload fileupload(){ return fileupload; }
		
		public static class FileUpload {
			private Integer sizeThreshold;
			public Integer sizeThreshold(){ return sizeThreshold; }
			
			private File repository = new File(System.getProperty("java.io.tmpdir"));
			public File repository(){ return repository; }
			
			private Long sizeMax;
			public Long sizeMax(){ return sizeMax; }
			
			private Long fileSizeMax;
			public Long fileSizeMax(){ return fileSizeMax; }
		}
	}
}
