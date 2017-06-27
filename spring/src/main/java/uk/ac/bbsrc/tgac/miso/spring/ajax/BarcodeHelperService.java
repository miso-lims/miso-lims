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
import uk.ac.bbsrc.tgac.miso.service.LibraryDilutionService;
import uk.ac.bbsrc.tgac.miso.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.service.PoolService;
import uk.ac.bbsrc.tgac.miso.service.SampleService;

@Ajaxified
public class BarcodeHelperService {
  protected static final Logger log = LoggerFactory.getLogger(BarcodeHelperService.class);

  @Autowired
  private PoolService poolService;
  @Autowired
  private LibraryService libraryService;
  @Autowired
  private LibraryDilutionService libraryDilutionService;
  @Autowired
  private SampleService sampleService;

  public void setSampleService(SampleService sampleService) {
    this.sampleService = sampleService;
  }

  public void setLibraryService(LibraryService libraryService) {
    this.libraryService = libraryService;
  }

  public void setLibraryDilutionService(LibraryDilutionService libraryDilutionService) {
    this.libraryDilutionService = libraryDilutionService;
  }

  public JSONObject regenerateAllBarcodes(HttpSession session, JSONObject json) {
    try {
      for (Sample s : sampleService.list()) {
        if (isStringEmptyOrNull(s.getIdentificationBarcode())) {
          sampleService.update(s);
        }
      }

      for (LibraryDilution ld : libraryDilutionService.list()) {
        if (isStringEmptyOrNull(ld.getIdentificationBarcode())) {
          libraryDilutionService.update(ld);
        }
      }

      for (Library l : libraryService.list()) {
        if (isStringEmptyOrNull(l.getIdentificationBarcode())) {
          libraryService.update(l);
        }
      }

      for (Pool p : poolService.list()) {
        if (isStringEmptyOrNull(p.getIdentificationBarcode())) {
          poolService.save(p);
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
