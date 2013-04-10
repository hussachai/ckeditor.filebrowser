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

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import com.siberhus.web.ckeditor.CkeditorConfigurationHolder;
import com.siberhus.web.ckeditor.CkeditorConfigurationHolder.CkeditorConfig;
import com.siberhus.web.ckeditor.CkeditorTagConfig;

/**
 * 
 * @author Hussachai Puripunpinyo (http://www.siberhus.com)
 * 
 */
public class PathUtils {

	public static String getBaseUrl(HttpServletRequest request, String space, String type) {
		CkeditorConfig config = CkeditorConfigurationHolder.config();
		String baseUrl = null;
		
		baseUrl = StringUtils.isNotBlank(config.upload().basedir(request)) ? config.upload().basedir(request)
					: CkeditorTagConfig.DEFAULT_BASEDIR;
		
		baseUrl = PathUtils.checkSlashes(baseUrl, "L- R-", true);

		String spaceDir = PathUtils.sanitizePath(space);
		if (StringUtils.isNotBlank(spaceDir)) {
			baseUrl += "/" + spaceDir;
		}

		String typeName = PathUtils.sanitizePath(StringUtils.lowerCase(type));
		if (StringUtils.isNotBlank(typeName)) {
			typeName = WordUtils.capitalize(typeName);
			baseUrl += "/" + typeName;
		}
		return baseUrl;
	}

	// Use commons-io FilenameUtils instead.
	// static splitFilename(fileName) {
	// def idx = fileName.lastIndexOf(".")
	// def name = fileName
	// def ext = ""
	// if (idx > 0) {
	// name = fileName[0..idx - 1]
	// ext = fileName[idx + 1..-1]
	// }
	// return [name: name, ext: ext]
	// }

	public static String getFilePath(String fileName) {
		return FilenameUtils.getPath(fileName);
	}

	public static String sanitizePath(String path) {
		String result = "";
		if (path != null) {
			// remove: . \ / | : ? * " ' ` ~ < > {space}
			result = path.replaceAll(
					"\\.|\\/|\\\\|\\||:|\\?|\\*|\"|'|~|`|<|>| ", "");
		}
		return result;
	}

	/**
	 * Remove or add slashes as indicated in rules
	 * 
	 * rules: space separated list of rules R- = remove slash on right R+ = add
	 * slash on right L- = remove slash on left L+ = add slash on left
	 */
	public static String checkSlashes(String path, String rules, boolean isUrl) {
		String result = StringUtils.trim(path);
		if (StringUtils.isNotBlank(result)) {
			String rls[] = rules.split(" ");
			String separator = isUrl ? "/" : File.separator;
			for (String r : rls) {
				boolean isAdd = ("+".equals(String.valueOf(r.charAt(1))));
				if (isAdd) {
					if ("L".equals(StringUtils.upperCase(r.charAt(0) + ""))) {
						// Add separator on left
						if (!result.startsWith("/") && !result.startsWith("\\")) {
							result = separator + result;
						}
					} else {
						// Add separator on right
						if (!result.endsWith("/") && !result.endsWith("\\")) {
							result = result + separator;
						}
					}
				} else {
					if ("L".equals(StringUtils.upperCase(r.charAt(0) + ""))) {
						// Remove separator on left
						if (result.startsWith("/") || result.startsWith("\\")) {
							result = result.substring(1);
						}
					} else {
						// Remove separator on right
						if (result.endsWith("/") || result.endsWith("\\")) {
							result = result.substring(0, result.length() - 1);
						}
					}
				}
			}
		}
		return result;
	}

	public static String normalizePath(String path) {
		String tokens[] = StringUtils.split(path, File.separator);
		List<String> tokenList = Arrays.asList(tokens);
		Stack<String> tokenStack = new Stack<String>();
		for (String token : tokenList) {
			if (".".equals(token)) {
				// skip
			} else if ("..".equals(token)) {
				tokenStack.pop();
			} else {
				tokenStack.push(token);
			}
		}
		String result = "";
		if (path.startsWith(File.separator)) {
			result += File.separator;
		}
		result += StringUtils.join(tokenStack, File.separator);
		if (path.endsWith(File.separator)) {
			result += File.separator;
		}
		return result.toString();
	}

	public static boolean isSafePath(String baseDir, File file) {
		String p = normalizePath(file.getAbsolutePath());
		return p.startsWith(baseDir);
	}

}
