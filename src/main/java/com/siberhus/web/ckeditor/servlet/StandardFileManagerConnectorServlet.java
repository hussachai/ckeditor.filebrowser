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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siberhus.web.ckeditor.CkeditorConfigurationHolder;
import com.siberhus.web.ckeditor.CkeditorConfigurationHolder.CkeditorConfig;
import com.siberhus.web.ckeditor.CkeditorTagConfig;
import com.siberhus.web.ckeditor.utils.FileUtils;
import com.siberhus.web.ckeditor.utils.PathUtils;

/**
 * 
 * @author Hussachai Puripunpinyo (http://www.siberhus.com)
 * 
 */
@WebServlet(name="ck_sfm", urlPatterns={"/ck/sfm/*"})
public class StandardFileManagerConnectorServlet extends BaseActionServlet {
	
	private static final long serialVersionUID = 1L;
	
	private final Logger log = LoggerFactory.getLogger(StandardFileManagerConnectorServlet.class);
	
	// Generic errors
	public static final int ERROR_NOERROR = 0;
	public static final int ERROR_CUSTOM = 1;

	// Connector errors
	public static final int ERROR_FOLDER_EXISTS = 101;
	public static final int ERROR_INVALID_FOLDER_NAME = 102;
	public static final int ERROR_NO_CREATE_PERMISSIONS = 103;
	public static final int ERROR_INVALID_FILE_NAME = 104;
	public static final int ERROR_CANNOT_DELETE = 105;
	public static final int ERROR_UNKNOWN = 110;

	// Uploader errors
	public static final int ERROR_FILE_RENAMED = 201;
	public static final int ERROR_INVALID_FILE_TYPE = 202;
	public static final int ERROR_NO_UPLOAD_PERMISSIONS = 203;
	
	public StreamingResult connector(HttpServletRequest request, HttpServletResponse response){
    	try{
    		return execute(request, response, request.getParameter("Command"), 
				request.getParameter("CurrentFolder"), request.getParameter("userSpace"), false);
    	}catch(Exception e){
    		e.printStackTrace();
    		throw new RuntimeException(e);
    	}
	}
    
	public StreamingResult uploader(HttpServletRequest request, HttpServletResponse response) {
		return execute(request, response, "FileUpload", "/", request.getParameter("userSpace"), true);
	}
	
    private StreamingResult execute(HttpServletRequest request, HttpServletResponse response, 
    		String command, String currentFolder, String userSpace, boolean uploadOnly) {
//		uploadOnly = false by default
    	if(command==null) command = "GetFolders"; //default command
    	if(currentFolder==null) currentFolder = "";
    	CkeditorConfig config = CkeditorConfigurationHolder.config();
    	String baseDir = StringUtils.isNotBlank(config.upload().basedir(request))?config.upload().basedir(request): CkeditorTagConfig.DEFAULT_BASEDIR;
        baseDir = PathUtils.checkSlashes(baseDir, "L+ R+", true);
        
        String spaceDir = PathUtils.sanitizePath(userSpace);
        spaceDir = PathUtils.checkSlashes(spaceDir, "L- R+", true);
        
		String type = request.getParameter("Type");
		String currentPath = baseDir+spaceDir+type+currentFolder;
		String currentUrl = null;
		String realPath = null;
		
        // Use a directory outside of the application space?
		String userDefinedBaseUrl = config.upload().baseurl(request);
        if (StringUtils.isNotBlank(userDefinedBaseUrl)) {
            String baseUrl = PathUtils.checkSlashes(userDefinedBaseUrl, "R-", true);
            baseUrl += baseDir;
            currentUrl = baseUrl+spaceDir+type+currentFolder;
//            realPath = currentPath;
        }else {
            currentUrl = request.getContextPath()+currentPath;
        }
        realPath = request.getSession().getServletContext()
    		.getRealPath(currentPath);
        
        File finalDir = new File(realPath);
    	if (!finalDir.exists()) {
			finalDir.mkdirs();
    	}
    	
        log.debug("userSpace = {}",userSpace);
    	log.debug("Command = {}",command);
    	log.debug("CurrentFolder = {}",currentFolder);
    	log.debug("Type = {}",type);
    	log.debug("finalDir = {}",finalDir);
    	
    	int errorNo = 0;
    	String errorMsg = null;
    	StreamingResult resolution = null;
    	if("GetFolders".equals(command)){
    		String xml = "<Connector command=\""+command+"\" resourceType=\""+type+"\">\n";
    		xml += "\t<CurrentFolder path=\""+currentFolder+"\" url=\""+currentUrl+"\"/>\n";
    		List<File> dirs = new ArrayList<File>();
    		for(File f: finalDir.listFiles()){
    			if(f.isDirectory()){
    				dirs.add(f);
    			}
    		}
    		Collections.sort(dirs);
    		xml += "\t\t<Folders>\n";
    		for(File f : dirs){
    			xml += "\t\t\t<Folder name=\""+f.getName()+"\"/>\n";
    		}
    		xml += "\t\t</Folders>\n";
    		xml += "</Connector>\n";
    		resolution = new StreamingResult("text/xml", xml);
    	}else if("GetFoldersAndFiles".equals(command)){
    		String xml = "<Connector command=\""+command+"\" resourceType=\""+type+"\">\n";
    		xml += "\t<CurrentFolder path=\""+currentFolder+"\" url=\""+currentUrl+"\"/>\n";
    		List<File> dirs = new ArrayList<File>();
    		List<File> files = new ArrayList<File>();
    		for(File f: finalDir.listFiles()){
    			if(f.isDirectory()){
    				dirs.add(f);
    			}else{
    				files.add(f);
    			}
    		}
    		Collections.sort(dirs);
    		Collections.sort(files);
    		xml += "\t\t<Folders>\n";
    		for(File f : dirs){
    			xml += "\t\t\t<Folder name=\""+f.getName()+"\"/>\n";
    		}
    		xml += "\t\t</Folders>\n";
    		xml += "\t\t<Files>\n";
    		for(File f : files){
    			xml += "\t\t\t<File name=\""+f.getName()
    				+"\" size=\""+(f.length()/1024)+"\"/>\n";
    		}
    		xml += "\t\t</Files>\n";
    		xml += "</Connector>\n";
    		resolution = new StreamingResult("text/xml", xml);
    	}else if("CreateFolder".equals(command)){
    		String newFolderName = request.getParameter("NewFolderName");
			File newFinalDir = new File(finalDir, newFolderName);
			errorNo = ERROR_NOERROR;
			if(newFinalDir.exists()){
				errorNo = ERROR_FOLDER_EXISTS;
			}else{
				try{
					if(newFinalDir.mkdir()){
						errorNo = ERROR_NOERROR;
					}else{
						errorNo = ERROR_INVALID_FOLDER_NAME;
					}
				}catch(SecurityException se) {
					errorNo = ERROR_NO_CREATE_PERMISSIONS;
				}
			}
			resolution = renderXmlResult(command, type, currentFolder, currentUrl, errorNo);
    	}else if("DeleteFile".equals(command)){
    		String fileName = request.getParameter("FileName");
    		File fileFinalName = new File(finalDir, fileName);
    		errorNo = ERROR_NOERROR;
            if (fileFinalName.exists()) {
                try {
					if (fileFinalName.delete()) {
						errorNo = ERROR_NOERROR;
					}else {
						errorNo = ERROR_INVALID_FILE_NAME;
					}
				}catch (SecurityException se) {
					errorNo = ERROR_NO_CREATE_PERMISSIONS;
				}
            }else {
                errorNo = ERROR_INVALID_FILE_NAME;
            }
            resolution = renderXmlResult(command, type, currentFolder, currentUrl, errorNo);
    	}else if("DeleteFolder".equals(command)){
    		String folderName = request.getParameter("FolderName");
            File folderFinalName = new File(finalDir, folderName);
            errorNo = ERROR_NOERROR;
            if (folderFinalName.exists() && folderFinalName.isDirectory()) {
                try {
                	org.apache.commons.io.FileUtils.deleteDirectory(folderFinalName);
                    errorNo = ERROR_NOERROR;
                }catch(IOException se) {
//                    errorNo = ERROR_NO_CREATE_PERMISSIONS;
                	errorNo = ERROR_CANNOT_DELETE;
                }
            }else {
                errorNo = ERROR_INVALID_FOLDER_NAME;
            }
            resolution = renderXmlResult(command, type, currentFolder, currentUrl, errorNo);
    	}else if("RenameFile".equals(command)){
    		String oldName = request.getParameter("FileName");
            String newName = request.getParameter("NewName");
			File oldFinalName = new File(finalDir, oldName);
			File newFinalName = new File(finalDir, newName);
            errorNo = ERROR_NOERROR;
            if (!newFinalName.exists() && FileUtils.isFileAllowed(newName, type)) {
                try {
					if( oldFinalName.renameTo( newFinalName ) ) {
						errorNo = ERROR_NOERROR;
					}else {
						errorNo = ERROR_INVALID_FILE_NAME;
					}
				}catch (SecurityException se) {
					errorNo = ERROR_NO_CREATE_PERMISSIONS;
				}
            }else {
                errorNo = ERROR_INVALID_FILE_NAME;
            }
            resolution = renderXmlResult(command, type, currentFolder, currentUrl, errorNo);
    	}else if("RenameFolder".equals(command)){
    		String oldName = request.getParameter("FolderName");
            String newName = request.getParameter("NewName");
            File oldFinalName = new File(finalDir, oldName);
            File newFinalName = new File(finalDir, newName);
            errorNo = ERROR_NOERROR;
            if (!newFinalName.exists()) {
                try {
                    if (oldFinalName.renameTo(newFinalName)) {
                        errorNo = ERROR_NOERROR;
                    }else {
                        errorNo = ERROR_INVALID_FOLDER_NAME;
                    }
                }catch (SecurityException se) {
                    errorNo = ERROR_NO_CREATE_PERMISSIONS;
                }
            }else {
                errorNo = ERROR_INVALID_FOLDER_NAME;
            }
            resolution = renderXmlResult(command, type, currentFolder, currentUrl, errorNo);
    	}else if("FileUpload".equals(command)){
    		errorNo = ERROR_NOERROR;
			errorMsg = "";
//            String uploadFieldName = uploadOnly ? "upload" : "NewFile";
            String newName = "";
			boolean overwrite = config.upload().overwrite();
			
			if (isUploadEnabled(type)) {
				if (!"POST".equals(request.getMethod())) {
					errorNo = ERROR_CUSTOM;
					errorMsg = "INVALID CALL";
				}else {
					MultipartServletRequest mrequest = (MultipartServletRequest)request;
					FileItem fileItem = mrequest.getFileItem("NewFile");;
//					if("upload".equals(uploadFieldName)){
//						fileItem = mrequest.getFileItem("upload");
//					}else{
//						fileItem = mrequest.getFileItem("newfile");
//					}
					if (fileItem==null) {
						errorNo = ERROR_CUSTOM;
						errorMsg = "INVALID FILE";
					}else {
						errorNo = ERROR_NOERROR;
						newName = fileItem.getName();
						String fileBaseName = FilenameUtils.getBaseName(newName);
						String fileExt = FilenameUtils.getExtension(newName);
						if (FileUtils.isAllowed(fileExt, type)) {
                            File fileToSave = new File(finalDir, newName);
							if ( !overwrite ) {
								int idx = 1;
								while (fileToSave.exists()) {
									errorNo = ERROR_FILE_RENAMED;
									newName = fileBaseName+"("+idx+")."+fileExt;
									fileToSave = new File(finalDir, newName);
									idx++;
								}
							}
							try{
								fileItem.write(fileToSave);
							}catch(Exception e){
								errorNo = ERROR_CUSTOM;
								errorMsg = "UNABLE TO SAVE FILE!";
							}
						}else {
							errorNo = ERROR_INVALID_FILE_TYPE;
							errorMsg = "INVALID FILE TYPE";
						}
					}
				}
			}else {
				errorNo = ERROR_CUSTOM;
				errorMsg = "UPLOADS ARE DISABLED!";
			}
			
			String html = "<script type=\"text/javascript\">\n";
			if(uploadOnly){
				if(StringUtils.isNotBlank(config.upload().baseurl(request))){
					currentUrl = request.getContextPath()+currentUrl;
				}
				String fname = StringUtils.isNotBlank(errorMsg)?"":currentUrl+newName;
				html += "\twindow.parent.CKEDITOR.tools.callFunction("+request.getParameter("CKEditorFuncNum")+", '"+fname+"', '"+errorMsg+"');";
			}else{
				html += "\twindow.parent.frames['frmUpload'].OnUploadCompleted("+errorNo+", '"+newName+"');";
			}
			html += "</script>\n";
			resolution = new StreamingResult("text/html", html);
    	}
    	response.setHeader("Cache-Control", "no-cache");
        log.debug("errorNo = {}",errorNo);
        log.debug("errorMsg = {}",errorMsg);
        if(resolution!=null){
        	return resolution;
        }
        throw new IllegalArgumentException("Unkown command: "+command);
    }
    
    private StreamingResult renderXmlResult(String command, String type, String currentFolder, String currentUrl, int errorNo){
    	String xml = "<Connector command=\""+command+"\" resourceType=\""+type+"\">\n";
		xml += "\t<CurrentFolder path=\""+currentFolder+"\" url=\""+currentUrl+"\"/>\n";
		xml += "\t<Error number=\""+errorNo+"\"/>\n";
		xml += "</Connector>\n";
		return new StreamingResult("text/xml", xml);
    }
    
    // Utility methods
    private boolean isUploadEnabled(String type) {
    	CkeditorConfig config = CkeditorConfigurationHolder.config();
    	String resType = StringUtils.lowerCase(type);
    	if("file".equals(resType)){
    		resType = "link";
    	}
    	if("link".equals(resType)){
    		return config.upload().link().upload();
    	}else if("image".equals(resType)){
    		return config.upload().image().upload();
    	}else if("flash".equals(resType)){
    		return config.upload().flash().upload();
    	}
    	return false;
    }
    
}
