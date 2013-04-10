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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siberhus.web.ckeditor.CkeditorConfigurationHolder;
import com.siberhus.web.ckeditor.CkeditorConfigurationHolder.CkeditorConfig;
import com.siberhus.web.ckeditor.CkeditorTagConfig;
import com.siberhus.web.ckeditor.utils.FileUtils;
import com.siberhus.web.ckeditor.utils.ImageUtils;
import com.siberhus.web.ckeditor.utils.ImageUtils.Dimension;
import com.siberhus.web.ckeditor.utils.MimeUtils;
import com.siberhus.web.ckeditor.utils.PathUtils;

/**
 * 
 * @author Hussachai Puripunpinyo (http://www.siberhus.com)
 * 
 */
@WebServlet(name="ck_ofm", urlPatterns={"/ck/ofm/*"})
public class OpenFileManagerConnectorServlet extends BaseActionServlet {

	private static final long serialVersionUID = 1L;
	
	private final Logger log = LoggerFactory
			.getLogger(OpenFileManagerConnectorServlet.class);
	
	/**
	 * Filemanager connector
	 * 
	 */
	public StreamingResult fileManager(HttpServletRequest request, HttpServletResponse response) throws Exception {
		log.debug("Begin fileManager()");
		String mode = request.getParameter("mode");
		String type = request.getParameter("type");
		String space = request.getParameter("space");
		boolean showThumbs = "true".equalsIgnoreCase(request
				.getParameter("showThumbs"));
		ServletContext servletContext = request.getSession().getServletContext();
		String contextPath = servletContext.getContextPath();
		String baseUrl = PathUtils.getBaseUrl(request, space, type);
		String baseDir = getBaseDir(request, baseUrl);
		
		if (log.isDebugEnabled()) {
			log.debug("OFM Parameters ================================");
			log.debug("mode = {}", mode);
			log.debug("baseDir = {}", baseDir);
			log.debug("baseUrl = {}", baseUrl);
			log.debug("type = {}", type);
			log.debug("space = {}", space);
			log.debug("showThumbs = {}", showThumbs);
			log.debug("==============================================");
		}
		String resp = null;
		if ("getinfo".equals(mode)) {
			resp = getInfo(request, contextPath, baseDir, baseUrl);
		} else if ("getfolder".equals(mode)) {
			resp = getFolder(request, contextPath, baseDir, baseUrl, showThumbs);
		} else if ("rename".equals(mode)) {
			resp = rename(request, baseDir, type);
		} else if ("delete".equals(mode)) {
			resp = delete(request, baseDir);
		} else if ("add".equals(mode)) {
			resp = add((MultipartServletRequest)request, baseDir, type);
			return new StreamingResult("text/html", resp);
		} else if ("addfolder".equals(mode)) {
			resp = addFolder(request, baseDir);
		} else if ("download".equals(mode)) {
			resp = download(request, response, baseDir);
		}
		if (log.isDebugEnabled()) {
			log.debug("End fileManager()");
			log.debug("QueryString={}", request.getQueryString());
			log.debug("ResponseString={}", resp);
		}
		if (resp != null) {
			return new StreamingResult("application/json", resp);
		} else {
			return new StreamingResult("application/json", "");
		}
	}

	

	private String getBaseDir(HttpServletRequest request, String baseUrl) {
		CkeditorConfig config = CkeditorConfigurationHolder.config();
		ServletContext servletContext = request.getSession().getServletContext();
		String baseDir = null;
		if (config.upload().baseurl(request) != null) {
			baseDir = PathUtils.checkSlashes(baseUrl, "L+ R+", true);
			baseDir = servletContext.getRealPath(baseDir);
			baseDir = PathUtils.checkSlashes(baseDir, "R+", false);
		} else {
			baseDir = servletContext.getRealPath(baseUrl);
			baseDir = PathUtils.checkSlashes(baseDir, "R+", false);
		}
		
		File f = new File(baseDir);
		if (!f.exists()) {
			f.mkdirs();
		}
		return baseDir;
	}

	private String getInfo(HttpServletRequest request, String contextPath, String baseDir, String baseUrl) {
		String path = request.getParameter("path");
		Map<String, Object> resp = getFileInfo(request, contextPath, baseDir, baseUrl, path, true);
		return new JSONObject(resp).toString();
	}
	
	private String getFolder(HttpServletRequest request, String contextPath, String baseDir, 
			String baseUrl, boolean showThumbs) {
		String path = request.getParameter("path");
		Map<String, Object> resp = new HashMap<String, Object>();
		File currentDir = new File(baseDir
				+ PathUtils.checkSlashes(path, "L- R+", false));
		if (currentDir.exists()) {
			for (File file : currentDir.listFiles()) {
				if (!file.getName().startsWith(".")) {
					String fname = path + file.getName();
					resp.put("\"" + fname + "\"",
							getFileInfo(request, contextPath, baseDir, baseUrl, fname, showThumbs));
				}
			}
		}
		return new JSONObject(resp).toString();
	}

	private Map<String, Object> getFileInfo(HttpServletRequest request, String contextPath, String baseDir, String baseUrl,
			String path, boolean showThumbs) {
		// showThumbs = true by default
		String currentObject = baseDir
				+ PathUtils.checkSlashes(path, "L- R-", false);
		File file = new File(currentObject);
		int width = 0;
		int height = 0;
		long fileSize = 0;
		String preview = null;
		String fileType = null;
		Properties properties = new Properties();
		if (file.isDirectory()) {
			path = PathUtils.checkSlashes(path, "L+ R+", true);
			preview = contextPath + "/ckeditor/ofm/images/fileicons/" + "_Open.png";
			fileType = "dir";
			properties.setProperty("Date Created", "");
			properties.setProperty("Date Modified", "");
			properties.setProperty("Width", "");
			properties.setProperty("Height", "");
			properties.setProperty("Size", "");
		} else {
			fileType = FilenameUtils.getExtension(file.getName());
			fileType = fileType.toLowerCase();
			fileSize = file.length();
			preview = contextPath + "/ckeditor/ofm/images/fileicons/" + fileType
					+ ".png";
			if (CkeditorTagConfig.OFM_IMAGE_EXTS.contains(fileType)) {
				if (showThumbs) {
					CkeditorConfig config = CkeditorConfigurationHolder.config();
					String userDefinedBaseUrl = config.upload().baseurl(request);
					if (userDefinedBaseUrl != null) {
						preview = PathUtils.checkSlashes(userDefinedBaseUrl,
								"L- R+", true) + baseUrl + path;
					} else {
						preview = contextPath + "/" + baseUrl + path;
					}
				}
				Dimension imgDim = ImageUtils.calculateImageDimension(file,fileType);
				if (imgDim != null) {
					width = imgDim.width;
					height = imgDim.height;
				}
			}
			properties.setProperty("Date Created", "");
			properties.setProperty("Date Modified",new SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
				.format(new Date(file.lastModified())));
			properties.setProperty("Width", String.valueOf(width));
			properties.setProperty("Height", String.valueOf(height));
			properties.setProperty("Size", String.valueOf(fileSize));
		}
		Map<String, Object> resp = new HashMap<String, Object>();
		resp.put("Path", path);
		resp.put("Filename", file.getName());
		resp.put("File Type", fileType);
		resp.put("Preview", preview);
		resp.put("Properties", properties);
		resp.put("Error", "");
		resp.put("Code", 0);
		return resp;
	}

	private String rename(HttpServletRequest request, String baseDir, String type) {
		String oldName = request.getParameter("old");
		String newName = request.getParameter("new"); 
		File oldFile = new File(baseDir
				+ PathUtils.checkSlashes(oldName, "L-", false));
		File newFile = new File(oldFile.getParent(), newName);

		boolean isDirectory = oldFile.isDirectory();

		String path = null;
		if (isDirectory) {
			path = PathUtils.getFilePath(PathUtils.checkSlashes(oldName, "R-",false));
		} else {
			path = PathUtils.getFilePath(oldName);
		}

		String resp = null;
		if (PathUtils.isSafePath(baseDir, newFile)) {
			if (!newFile.exists()) {
				if (isDirectory || FileUtils.isFileAllowed(newName, type)) {
					try {
						if (oldFile.renameTo(newFile)) {
							Map<String, Object> tmpJSON = new HashMap<String, Object>();
							tmpJSON.put("Old Path", oldName);
							tmpJSON.put("Old Name", oldFile.getName());
							tmpJSON.put("New Path", path + newFile.getName()
									+ (isDirectory ? "/" : ""));
							tmpJSON.put("New Name", newFile.getName());
							tmpJSON.put("Error", "");
							tmpJSON.put("Code", 0);
							resp = new JSONObject(tmpJSON).toString();
						} else {
							resp = error("ofm.invalidFilename",
									"Invalid file name", false);
						}
					} catch (SecurityException se) {
						resp = error("ofm.noPermissions", "No permissions",false);
					}
				} else {
					resp = error("ofm.invalidFilename", "Invalid file name",false);
				}
			} else {
				resp = error("ofm.fileAlreadyExists", "File exists", false);
			}
		} else {
			resp = error("ofm.noPermissions", "No permissions", false);
		}

		return resp;
	}

	private String delete(HttpServletRequest request, String baseDir) {
		String path = request.getParameter("path");
		File file = new File(baseDir+ PathUtils.checkSlashes(path, "L-", false));
		String resp = null;
		if (PathUtils.isSafePath(baseDir, file)) {
			if (file.exists()) {
				if (file.isDirectory()) {
					try {
						org.apache.commons.io.FileUtils.deleteDirectory(file);
						Map<String, Object> tmpJSON = new HashMap<String, Object>();
						tmpJSON.put("Path", path);
						tmpJSON.put("Error", "");
						tmpJSON.put("Code", 0);
						resp = new JSONObject(tmpJSON).toString();
					} catch (IOException se) {
						resp = error("ofm.noPermissions", "No permissions",false);
					}
				} else {
					try {
						if (file.delete()) {
							resp = "{\"Path\":\"" + path
									+ "\",\"Error\":\"\",\"Code\":0}";
						} else {
							resp = error("ofm.invalidFilename",
									"Invalid file name", false);
						}
					} catch (SecurityException se) {
						resp = error("ofm.noPermissions", "No permissions",false);
					}
				}
			} else {
				resp = error("ofm.fileDoesNotExists", "File does not exists",false);
			}
		} else {
			resp = error("ofm.noPermissions", "No permissions", false);
		}

		return resp;
	}

	private String add(MultipartServletRequest request, String baseDir, String type) throws Exception {
		
		String currentPath = request.getParameter("currentpath");
		boolean overwrite = CkeditorConfigurationHolder.config().upload().overwrite();
		FileItem fileItem = request.getFileItem("newfile");
		String resp = null;
		if (fileItem == null) {
			resp = error("ofm.invalidFilename", "Invalid file", true);
		} else {
			File uploadPath = new File(baseDir
					+ PathUtils.checkSlashes(currentPath, "L- R+", false));
			String newName = fileItem.getName();// original file name
			// def newName = file.originalFilename

			// def f = PathUtils.splitFilename(newName);
			String fileBaseName = FilenameUtils.getBaseName(newName);
			String fileExt = FilenameUtils.getExtension(newName);

			if (FileUtils.isAllowed(fileExt, type)) {
				File fileToSave = new File(uploadPath, newName);
				if (!overwrite) {
					int idx = 1;
					while (fileToSave.exists()) {
						newName = fileBaseName + "(" + idx + ")." + fileExt;
						fileToSave = new File(uploadPath, newName);
						idx++;
					}
				}
				fileItem.write(fileToSave);
				resp = "{\"Path\":\"" + currentPath + "\",\"Name\":\""
						+ newName + "\",\"Error\":\"\",\"Code\":0}";
				resp = "<textarea>" + resp + "</textarea>";
			} else {
				resp = error("ofm.invalidFileType", "Invalid file type", true);
			}
		}

		return resp;
	}

	private String addFolder(HttpServletRequest request, String baseDir) {
		String path = request.getParameter("path");
		String name = request.getParameter("name");
		File newDir = new File(baseDir
				+ PathUtils.checkSlashes(path, "L- R+", false) + name);
		String resp = null;
		if (newDir.exists()) {
			resp = error("ofm.directoryAlreadyExists",
					"Directory already exists!", false);
		} else {
			try {
				if (newDir.mkdir()) {
					Map<String, Object> tmpJSON = new HashMap<String, Object>();
					tmpJSON.put("Parent", path);
					tmpJSON.put("Name", name);
					tmpJSON.put("Error", "");
					tmpJSON.put("Code", 0);
					resp = new JSONObject(tmpJSON).toString();
				} else {
					resp = error("ofm.invalidFolderName",
							"invalid folder name", false);
				}
			} catch (SecurityException se) {
				resp = error("ofm.noPermissions", "No permissions", false);
			}
		}
		return resp;
	}

	private String download(HttpServletRequest request, HttpServletResponse response, String baseDir) throws IOException {
		String path = request.getParameter("path");
		File file = new File(baseDir
				+ PathUtils.checkSlashes(path, "L-", false));
		response.setHeader("Content-Type", "application/force-download");
		response.setHeader("Content-Disposition", "attachment; filename=\""
				+ file.getName() + "\"");
		response.setHeader("Content-Length", String.valueOf(file.length()));
		response.setHeader("Content-Transfer-Encoding", "Binary");

		OutputStream os = response.getOutputStream();
		byte[] buff = null;
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
				file));
		try {
			buff = new byte[2048];
			int bytesRead = 0;
			while ((bytesRead = bis.read(buff, 0, buff.length)) != -1) {
				os.write(buff, 0, bytesRead);
			}
		} finally {
			bis.close();
			os.flush();
			os.close();
		}
		return null;
	}
	
	public StreamingResult show(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		CkeditorConfig config = CkeditorConfigurationHolder.config();
		String filename = PathUtils.checkSlashes(config.upload().basedir(request),
				"L+ R+", false) + request.getParameter("filepath");
		String ext = FilenameUtils.getExtension(request
				.getParameter("filepath"));
		String contentType = MimeUtils.getMimeTypeByExt(ext);
		File file = new File(filename);

		response.setHeader("Content-Type", contentType);
		response.setHeader("Content-Length", String.valueOf(file.length()));
		
		OutputStream os = response.getOutputStream();
		
		byte[] buff = null;
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
				file));
		try {
			buff = new byte[2048];
			int bytesRead = 0;
			while ((bytesRead = bis.read(buff, 0, buff.length)) != -1) {
				os.write(buff, 0, bytesRead);
			}
		} finally {
			bis.close();
			os.flush();
			os.close();
		}
		return null;
	}
	
	private String error(String key, String message, boolean useTextarea) {
		// useTextarea = false by default
		String msg = message;
		// try {
		// msg = messageSource.getMessage(key, null, request.getLocale());
		// }catch (org.springframework.context.NoSuchMessageException nsme) {
		// msg = message;
		// })
		String jsonError = "{\"Error\":\"" + msg + "\",\"Code\":-1}";
		if (useTextarea) {
			jsonError = "<textarea>" + jsonError + "</textarea>";
		}
		return jsonError;
	}
	
}
