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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.siberhus.web.ckeditor.CkeditorConfigurationHolder.CkeditorConfig;
import com.siberhus.web.ckeditor.utils.Literal;
import com.siberhus.web.ckeditor.utils.PathUtils;

/**
 * 
 * @author Hussachai Puripunpinyo (http://www.siberhus.com)
 * 
 */
public class CkeditorTagConfig {
	
	public static final String REQUEST_CONFIG = "ckeditor.plugin.config";

	public static final String PLUGIN_NAME = "ckeditor";

	public static final String DEFAULT_CONNECTORS_PREFIX = "ck";

	public static final String DEFAULT_BASEDIR = "/uploads/";

	public static final String DEFAULT_USERSPACE = "";

	public static final String DEFAULT_INSTANCENAME = "editor";

	public static final String DEFAULT_FILEBROWSER = "standard"; // standard |
																	// ofm

	public static final boolean DEFAULT_SHOWTHUMBS = false;

	public static final List<String> RESOURCE_TYPES = Collections
			.unmodifiableList(Literal.list("link", "image", "flash"));

	public static final List<String> OFM_IMAGE_EXTS = Collections
			.unmodifiableList(Literal.list("jpg", "jpeg", "gif", "png"));

	public static final List<String> OFM_LOCALES = Collections
			.unmodifiableList(Literal.list("ca", "cs", "da", "de", "en", "es",
					"fr", "it", "nl", "pl", "zh-cn"));
	
	private String contextPath;
	String basePath;

	private String connectorsPrefix;

	private boolean skipAllowedItemsCheck = false;
	//
	String instanceId;
	String instanceName = DEFAULT_INSTANCENAME;
	/*
	 * Name of the file user space to use for file browsing and uploads
	 * (default: "")
	 */
	String userSpace = DEFAULT_USERSPACE;
	boolean append = false;
	//
	/* File manager to use. Possible values: ofm, standard (default: "standard") */
	String fileBrowser;
	private String defaultFileBrowser = DEFAULT_FILEBROWSER;
	/*
	 * Show images' thumbnails in Open File Manager. Possible values: true,
	 * false (default: false)
	 */
	boolean showThumbs = false;

	/* Type of resource (default: "") */
	String type = "";
	/* Target attribute for the generated <A> tag (default: "") */
	String target = null;
	//
	boolean removeInstance;
	//
	private Map<String, String> config;

	private Map<String, String> localConfig = null;

	public CkeditorTagConfig(HttpServletRequest request,
			Map<String, Object> attrs) {

		CkeditorConfig cfg = CkeditorConfigurationHolder.config();

		this.contextPath = request.getContextPath();
		// this.basePath =
		// PluginUtils.getPluginResourcePath(this.contextPath,this.PLUGIN_NAME)
		this.basePath = this.contextPath;
		this.connectorsPrefix = cfg.connectors().prefix();
		this.defaultFileBrowser = cfg.defaultFileBrowser();
		this.fileBrowser = this.defaultFileBrowser;
		this.skipAllowedItemsCheck = cfg.skipAllowedItemsCheck();
		this.localConfig = new HashMap<String, String>();
		createOrRetrieveConfig(request);
		if (attrs != null) {
			String val = null;
			if ((val = String.valueOf(attrs.remove("name"))) != null) {
				this.instanceName = val;
			}
			if ((val = String.valueOf(attrs.remove("id"))) != null) {
				this.instanceId = val;
			} else {
				this.instanceId = this.instanceName;
			}
			if ((val = ObjectUtils.toString(attrs.remove("userSpace"),null)) != null) {
				this.userSpace = val;
			}
			if ((val = ObjectUtils.toString(attrs.remove("append"),null)) != null) {
				this.append = Boolean.valueOf(val);
			}
			if ((val = ObjectUtils.toString(attrs.remove("fileBrowser"),null)) != null) {
				this.fileBrowser = val;
			}
			if ((val = ObjectUtils.toString(attrs.remove("showThumbs"),null)) != null) {
				this.showThumbs = Boolean.valueOf(val);
			}
			if ((val = ObjectUtils.toString(attrs.remove("type"),null)) != null) {
				this.type = val;
			}
			if ((val = ObjectUtils.toString(attrs.remove("target"),null)) != null) {
				this.target = val;
			}
			if ((val = ObjectUtils.toString(attrs.remove("removeInstance"),null)) != null) {
				this.removeInstance = Boolean.valueOf(val);
			}
			addConfigItem(attrs, true);
		}
	}

	@SuppressWarnings("unchecked")
	private void createOrRetrieveConfig(HttpServletRequest request) {
		if (request.getAttribute(REQUEST_CONFIG) == null) {
			request.setAttribute(REQUEST_CONFIG, new HashMap<String, String>());
		}
		this.config = (Map<String, String>) request
				.getAttribute(REQUEST_CONFIG);
	}

	public void addConfigItem(Map<String, Object> tagAttributes, boolean local) {
		for (Map.Entry<String, Object> entry : tagAttributes.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (this.skipAllowedItemsCheck
					|| ALLOWED_CONFIG_ITEMS.contains(key)
					|| key.startsWith("filebrowser")) {
				if (value instanceof String) {
					String tmp = StringUtils.trimToNull((String) value);
					if (tmp != null) {
						if (!NumberUtils.isNumber(tmp)
								&& !tmp.equalsIgnoreCase("true")
								&& !tmp.equalsIgnoreCase("false")
								&& !tmp.startsWith("CKEDITOR.")) {
							tmp = "'" + tmp + "'";
						}
					}
					if (local) {
						this.localConfig.put(key, tmp);
					} else {
						this.config.put(key, tmp);
					}
				} else {
					throw new UnknownOptionException(
							"Unknown option: ${key}. Option names are case sensitive! Check the spelling.");
				}
			}
		}
	}

	public void addComplexConfigItem(String var, String value) {
		if (this.skipAllowedItemsCheck || ALLOWED_CONFIG_ITEMS.contains(var)
				|| var.startsWith("toolbar_")) {
			this.config.put(var, value);
		} else {
			throw new UnknownOptionException(
					"Unknown option: ${var}. Option names are case sensitive! Check the spelling.");
		}
	}

	public String getBrowseUrl(String type, String userSpace,
			String fileBrowser, boolean showThumbs) {
		String browserUrl;
		String prefix = connectorsPrefix;
		if ("ofm".equals(fileBrowser)) {
			browserUrl = this.contextPath+ "/ckeditor/ofm/ofm.jsp?fileConnector="
					+ this.contextPath+ "/"+ prefix+ "/ofm/fileManager?treeConnector="
					+ this.contextPath+ "/"+ prefix+ "/ofm/filetree&type="+ type
					+ (StringUtils.isNotBlank(userSpace) ? "&space="+ userSpace : "")
					+ (showThumbs ? "&showThumbs=" + showThumbs : "");
			// browserUrl =
			// "${this.contextPath}/${prefix}/ofm?fileConnector=${this.contextPath}/${prefix}/ofm/filemanager&treeConnector=${this.contextPath}/${prefix}/ofm/filetree&type=${type}${userSpace ? '&space='+ userSpace : ''}${showThumbs ? '&showThumbs='+ showThumbs : ''}";
		} else {
			browserUrl = this.basePath+ "/ckeditor/filebrowser/browser.html?Connector="
					+ this.contextPath+ "/"+ prefix+ "/sfm/connector?Type="
					+ type+ (StringUtils.isNotBlank(userSpace) ? "&userSpace="+ userSpace : "");
			// browserUrl =
			// "${this.basePath}/ckeditor/filebrowser/browser.html?Connector=${this.contextPath}/${prefix}/standard/filemanager?Type=${type}${userSpace ? '&userSpace='+ userSpace : ''}";
		}
		return browserUrl;
	}
	
	public String getUploadUrl(String type, String userSpace) {
		return this.contextPath+ "/"+ connectorsPrefix
				+ "/sfm/uploader?Type="+ type
				+ (StringUtils.isNotBlank(userSpace) ? "&userSpace="+ userSpace : "");
		// return
		// "${this.contextPath}/${getConnectorsPrefix()}/standard/uploader?Type=${type}${userSpace ? '&userSpace='+ userSpace : ''}"
	}

	public StringBuilder getConfiguration() {
		CkeditorConfig ckconfig = CkeditorConfigurationHolder.config();

		String customConfig = ckconfig.config();
		if (customConfig != null && (this.config.get("customConfig") == null)) {
			customConfig = PathUtils.checkSlashes(customConfig, "L- R-", true);
			this.config.put("customConfig", "'" + this.contextPath + "/"
					+ customConfig + "'");
		}
		// Collect browser settings per media type
		for (String t : CkeditorTagConfig.getResourceTypes()) {
			String type = WordUtils.capitalize(t);
			String typeForConnector = "Link".equals(type) ? "File" : type;
			if (("link".equals(t) && ckconfig.upload().link().browser())
					|| ("image".equals(t) && ckconfig.upload().image().browser())
					|| ("flash".equals(t) && ckconfig.upload().flash().browser())) {
				this.config.put("filebrowser" + type + "BrowseUrl",
						"'"+ getBrowseUrl(typeForConnector,this.userSpace, 
								this.fileBrowser,this.showThumbs) + "'");
			}
			if (("link".equals(t) && ckconfig.upload().link().upload())
					|| ("image".equals(t) && ckconfig.upload().image().upload())
					|| ("flash".equals(t) && ckconfig.upload().flash().upload())) {
				this.config.put("filebrowser" + type + "UploadUrl", "'"
						+ getUploadUrl(typeForConnector, this.userSpace) + "'");
			}
		}
		// Config options
		List<String> configs = new ArrayList<String>();
		for (Map.Entry<String, String> entry : this.config.entrySet()) {
			if (localConfig.get(entry.getKey()) == null) {
				configs.add(entry.getKey() + ": " + entry.getValue());
			}
		}
		for (Map.Entry<String, String> entry : this.localConfig.entrySet()) {
			configs.add(entry.getKey() + ": " + entry.getValue());
		}

		StringBuilder configuration = new StringBuilder();
		if (configs.size() > 0) {
			// onfiguration << """, {\n"""
			configuration.append(", {\n");
			configuration.append(StringUtils.join(configs, ",\n"));
			// configuration << """}\n"""
			configuration.append("}\n");
		}

		return configuration;
	}

	public static List<String> getResourceTypes() {
		return RESOURCE_TYPES;
	}

	public static String getUploadPrefix(HttpServletRequest request) {
		CkeditorConfig ckconfig = CkeditorConfigurationHolder.config();
		String prefix = null;
		if (StringUtils.isNotBlank(ckconfig.upload().basedir(request))
				&& ckconfig.upload().enableContentController()) {
			prefix = PathUtils.checkSlashes(ckconfig.upload().basedir(request), "L+ R-",
					true);
		}
		return prefix;
	}
	
	// See: http://docs.cksource.com/ckeditor_api/symbols/CKEDITOR.config.html
	public static final List<String> ALLOWED_CONFIG_ITEMS = Collections
			.unmodifiableList(Literal.list(
					// Items not listed in main config file
					"uiColor",

					// Main config
					"customConfig",
					"autoUpdateElement",
					"baseFloatZIndex",
					"baseHref",
					"contentsCss",
					"contentsLangDirection",
					"contentsLanguage",
					"language",
					"defaultLanguage",
					"enterMode",
					"forceEnterMode",
					"shiftEnterMode",
					"corePlugins",
					"docType",
					"bodyId",
					"bodyClass",
					"fullPage",
					"height",
					"plugins",
					"extraPlugins",
					"removePlugins",
					"protectedSource",
					"tabIndex",
					"theme",
					"skin",
					"width",
					"baseFloatZIndex",

					// plugins/autogrow/plugin.js
					"autoGrow_minHeight",
					"autoGrow_maxHeight",

					// plugins/basicstyles/plugin.js
					"coreStyles_bold",
					"coreStyles_italic",
					"coreStyles_underline",
					"coreStyles_strike",
					"coreStyles_subscript",
					"coreStyles_superscript",

					// plugins/colorbutton/plugin.js
					"colorButton_enableMore",
					"colorButton_colors",
					"colorButton_foreStyle",
					"colorButton_backStyle",

					// plugins/contextmenu/plugin.js
					"browserContextMenuOnCtrl",

					// plugins/dialog/plugin.js
					"dialog_backgroundCoverColor",
					"dialog_backgroundCoverOpacity",
					"dialog_startupFocusTab",
					"dialog_magnetDistance",
					"dialog_buttonsOrder",
					"removeDialogTabs",

					// plugins/editingblock/plugin.js
					"startupMode",
					"startupFocus",
					"editingBlock",

					// plugins/entities/plugin.js
					"entities",
					"entities_latin",
					"entities_greek",
					"entities_processNumerical",
					"entities_additional",

					// plugins/filebrowser/plugin.js
					"filebrowserBrowseUrl",
					"filebrowserUploadUrl",
					"filebrowserImageBrowseUrl",
					"filebrowserFlashBrowseUrl",
					"filebrowserImageUploadUrl",
					"filebrowserFlashUploadUrl",
					"filebrowserImageBrowseLinkUrl",
					"filebrowserWindowFeatures",

					// plugins/find/plugin.js
					"find_highlight",

					// plugins/font/plugin.js
					"font_names",
					"font_defaultLabel",
					"font_style",
					"fontSize_sizes",
					"fontSize_defaultLabel",
					"fontSize_style",

					// plugins/format/plugin.js
					"format_tags",
					"format_p",
					"format_div",
					"format_pre",
					"format_address",
					"format_h1",
					"format_h2",
					"format_h3",
					"format_h4",
					"format_h5",
					"format_h6",

					// plugins/htmldataprocessor/plugin.js
					"forceSimpleAmpersand",
					"fillEmptyBlocks",

					// plugins/image/plugin.js
					"image_removeLinkByEmptyURL",
					"image_previewText",

					// plugins/indent/plugin.js
					"indentOffset",
					"indentUnit",
					"indentClasses",

					// plugins/keystrokes/plugin.js
					"blockedKeystrokes",
					"keystrokes",

					// plugins/menu/plugin.js
					"menu_subMenuDelay",
					"menu_groups",

					// plugins/newpage/plugin.js
					"newpage_html",

					// plugins/pastefromword/plugin.js
					"pasteFromWordPromptCleanup",
					"pasteFromWordCleanupFile",

					// plugins/pastetext/plugin.js
					"forcePasteAsPlainText",

					// plugins/removeformat/plugin.js
					"removeFormatTags",
					"removeFormatAttributes",

					// plugins/resize/plugin.js
					"resize_minWidth",
					"resize_minHeight",
					"resize_maxWidth",
					"resize_maxHeight",
					"resize_enabled",
					"resize_dir",

					// plugins/scayt/plugin.js
					"scayt_autoStartup",
					"scayt_maxSuggestions",
					"scayt_customerid",
					"scayt_moreSuggestions",
					"scayt_contextCommands",
					"scayt_sLang",
					"scayt_uiTabs",
					"scayt_srcUrl",
					"scayt_customDictionaryIds",
					"scayt_userDictionaryName",
					"scayt_contextMenuItemsOrder",

					// plugins/showblocks/plugin.js
					"startupOutlineBlocks",

					// plugins/showborders/plugin.js
					"startupShowBorders",

					// plugins/smiley/plugin.js
					"smiley_path",
					"smiley_images",
					"smiley_descriptions",
					"smiley_columns",

					// plugins/specialchar/plugin.js
					"specialChars",

					// plugins/styles/plugin.js
					"disableReadonlyStyling",
					"stylesSet",

					// plugins/tab/plugin.js
					"tabSpaces",
					"enableTabKeyTools",

					// plugins/templates/plugin.js
					"templates",
					"templates_files",
					"templates_replaceContent",

					// plugins/toolbar/plugin.js
					"toolbarLocation", "toolbar", "toolbar_Basic",
					"toolbar_Full", "toolbarCanCollapse",
					"toolbarStartupExpanded",

					// plugins/undo/plugin.js
					"undoStackSize",

					// plugins/wsc/plugin.js
					"wsc_customerId", "wsc_customLoaderScript",

					// plugins/wysiwygarea/plugin.js
					"disableObjectResizing", "disableNativeTableHandles",
					"disableNativeSpellChecker", "ignoreEmptyParagraph"));
}
