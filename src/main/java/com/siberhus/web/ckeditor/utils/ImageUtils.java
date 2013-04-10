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
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

/**
 * 
 * @author Hussachai Puripunpinyo (http://www.siberhus.com)
 * 
 */
public class ImageUtils {
	
	public static class Dimension {
		public int width;
		public int height;
	}
	
	public static Dimension calculateImageDimension(File file, String ext) {
		Dimension result = new Dimension();

		Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(ext);
		if (iter.hasNext()) {
			ImageReader reader = iter.next();
			ImageInputStream stream = null;
			try {
				stream = new FileImageInputStream(file);
				reader.setInput(stream);
				result.width = reader.getWidth(reader.getMinIndex());
				result.height = reader.getHeight(reader.getMinIndex());
			} catch (IOException e) {
				// not supported extension
			} finally {
				reader.dispose();
				try{ if(stream!=null) stream.close(); }catch(Exception e){}
			}
		}

		return result;
	}

}
