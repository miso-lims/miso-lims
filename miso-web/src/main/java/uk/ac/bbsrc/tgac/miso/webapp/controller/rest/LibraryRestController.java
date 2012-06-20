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

package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.TagBarcode;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A controller to handle all REST requests for Libraries
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @date 16-Aug-2011
 * @since 0.1.0
 */
@Controller
@RequestMapping("/rest/library")
@SessionAttributes("library")
public class LibraryRestController {
  protected static final Logger log = LoggerFactory.getLogger(LibraryRestController.class);

  @Autowired
  private RequestManager requestManager;

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public Collection<LibraryType> populateLibraryTypes() throws IOException {
    List<LibraryType> types = new ArrayList<LibraryType>(requestManager.listAllLibraryTypes());
    Collections.sort(types);
    return types;
  }

  public Collection<LibraryType> populateLibraryTypes(String p) throws IOException {
    List<LibraryType> types = new ArrayList<LibraryType>(requestManager.listLibraryTypesByPlatform(p));
    Collections.sort(types);
    return types;
  }

  @RequestMapping(value = "librarytypes", method = RequestMethod.GET)
  public
  @ResponseBody
  String jsonRestLibraryTypes(@RequestParam("platform") String platform) throws IOException {
    if (platform != null && !"".equals(platform)) {
      List<String> types = new ArrayList<String>();
      for (LibraryType t : populateLibraryTypes(platform)) {
        types.add("\"" + t.getDescription() + "\"" + ":" + "\"" + t.getDescription() + "\"");
      }
      return "{"+ LimsUtils.join(types, ",")+"}";
    }
    else {
      return "{}";
    }
  }

  @RequestMapping(value = "tagbarcodes", method = RequestMethod.GET)
  public
  @ResponseBody
  String jsonRestTagBarcodes(@RequestParam("platform") String platform) throws IOException {
    if (platform != null && !"".equals(platform)) {
      List<TagBarcode> tagBarcodes = new ArrayList<TagBarcode>(requestManager.listAllTagBarcodesByPlatform(platform));
      Collections.sort(tagBarcodes);
      List<String> names = new ArrayList<String>();
      names.add("\"\"" + ":" + "\"No Barcode\"");
      for (TagBarcode tb : tagBarcodes) {
        names.add("\"" + tb.getTagBarcodeId() + "\"" + ":" + "\"" + tb.getName() + " ("+tb.getSequence()+")\"");
      }
      return "{"+LimsUtils.join(names, ",")+"}";
    }
    else {
      return "{}";
    }
  }

  @RequestMapping(value = "{libraryId}", method = RequestMethod.GET)
  public @ResponseBody Library jsonRest(@PathVariable Long libraryId) throws IOException {
    return requestManager.getLibraryById(libraryId);
  }
}
