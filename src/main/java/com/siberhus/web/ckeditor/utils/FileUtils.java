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

import java.util.List;

import org.apache.commons.io.FilenameUtils;

import com.siberhus.web.ckeditor.CkeditorConfigurationHolder;
import com.siberhus.web.ckeditor.CkeditorConfigurationHolder.CkeditorConfig;

/**
 * 
 * @author Hussachai Puripunpinyo (http://www.siberhus.com)
 * 
 */
public class FileUtils {
	
	public static boolean isFileAllowed(String filename, String type) {
		String fileExt = FilenameUtils.getExtension(filename);
		return isAllowed(fileExt, type);
	}

	public static boolean isAllowed(String ext, String type) {
		String resourceType = type.toLowerCase();
		if ("file".equals(resourceType)) {
			resourceType = "link";
		}
		String fileExt = ext.toLowerCase();
		CkeditorConfig.Upload config = CkeditorConfigurationHolder.config().upload();
		List<String> allowedList = null;// config."${resourceType}".allowed
		List<String> deniedList = null;// config."${resourceType}".denied
		if ("link".equals(resourceType)) {
			allowedList = config.link().allowed();
			deniedList = config.link().denied();
		} else if ("image".equals(resourceType)) {
			allowedList = config.image().allowed();
			deniedList = config.image().denied();
		} else if ("flash".equals(resourceType)) {
			allowedList = config.flash().allowed();
			deniedList = config.flash().denied();
		} else {
			throw new IllegalArgumentException("Unkown resourceType: "
					+ resourceType);
		}
		return ((allowedList.contains(fileExt) || allowedList.isEmpty()) && !(deniedList
				.contains(fileExt)));
	}

}
