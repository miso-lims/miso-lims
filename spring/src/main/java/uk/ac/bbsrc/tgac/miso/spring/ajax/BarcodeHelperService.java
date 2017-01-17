/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
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

package uk.ac.bbsrc.tgac.miso.spring.ajax;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;

@Ajaxified
public class BarcodeHelperService {
  protected static final Logger log = LoggerFactory.getLogger(BarcodeHelperService.class);

  @Autowired
  private RequestManager requestManager;

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public JSONObject regenerateAllBarcodes(HttpSession session, JSONObject json) {
    try {
      for (Sample s : requestManager.listAllSamples()) {
        if (isStringEmptyOrNull(s.getIdentificationBarcode())) {
          requestManager.saveSample(s);
        }
      }

      for (LibraryDilution ld : requestManager.listAllLibraryDilutions()) {
        if (isStringEmptyOrNull(ld.getIdentificationBarcode())) {
          requestManager.saveLibraryDilution(ld);
        }
      }

      for (Library l : requestManager.listAllLibraries()) {
        if (isStringEmptyOrNull(l.getIdentificationBarcode())) {
          requestManager.saveLibrary(l);
        }
      }

      for (Pool p : requestManager.listAllPools()) {
        if (isStringEmptyOrNull(p.getIdentificationBarcode())) {
          requestManager.savePool(p);
        }
      }
    } catch (IOException e) {
      log.error("barcode regeneration failed", e);
      return JSONUtils.JSONObjectResponse("html",
          jQueryDialogFactory.errorDialog("Barcode Administration", "Barcode regeneration failed!:\n\n" + e.getMessage()));
    }

    log.info("Barcodes regenerated!");
    return JSONUtils.JSONObjectResponse("html",
        jQueryDialogFactory.okDialog("Barcode Administration", "Barcodes regenerated successfully!"));
  }
}
