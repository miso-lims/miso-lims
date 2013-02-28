/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.spring.ajax.multipart;

import net.sourceforge.fluxion.ajax.beans.util.FileUploadListener;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * uk.ac.bbsrc.tgac.miso.spring.ajax.multipart
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public class AjaxMultipartResolver extends CommonsMultipartResolver {
  private FileUploadListener fileUploadListener;

  @Override
  public MultipartHttpServletRequest resolveMultipart(HttpServletRequest request) throws MultipartException {
    String encoding = determineEncoding(request);
    FileUpload fileUpload = prepareFileUpload(encoding);
    if (fileUploadListener != null) {
      fileUpload.setProgressListener(fileUploadListener);
      request.getSession(false).setAttribute("upload_listener", fileUploadListener);
    }
    try {
      List fileItems = ((ServletFileUpload) fileUpload).parseRequest(request);
      MultipartParsingResult parsingResult = parseFileItems(fileItems, encoding);
      Map<String, String> multipartContentTypes = new HashMap<String, String>();
      for (List<MultipartFile> files : parsingResult.getMultipartFiles().values()) {
        for (MultipartFile f : files) {
          multipartContentTypes.put(f.getName(), f.getContentType());
        }
      }
      return new DefaultMultipartHttpServletRequest(request, parsingResult.getMultipartFiles(), parsingResult.getMultipartParameters(), multipartContentTypes);
    }
    catch (FileUploadBase.SizeLimitExceededException ex) {
      throw new MaxUploadSizeExceededException(fileUpload.getSizeMax(), ex);
    }
    catch (FileUploadException ex) {
      throw new MultipartException("Could not parse multipart servlet request", ex);
    }
  }

  public FileUploadListener getFileUploadListener() {
    return fileUploadListener;
  }

  public void setFileUploadListener(FileUploadListener fileUploadListener) {
    this.fileUploadListener = fileUploadListener;
  }
}

