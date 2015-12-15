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

package uk.ac.bbsrc.tgac.miso.webapp.service.forms;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.eaglegenomics.simlims.core.Note;

import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.util.TaxonomyUtils;

/**
 * uk.ac.bbsrc.tgac.miso.webapp.service.forms
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 08-Sep-2011
 * @since 0.1.1
 */
public class MisoFormsService {
  protected static final Logger log = LoggerFactory.getLogger(MisoFormsService.class);

  @Autowired
  private RequestManager requestManager;

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public void importSampleDeliveryFormSamples(List<Sample> samples, boolean checkTaxon) throws IOException {
    Map<String, String> foundTaxons = new HashMap<String, String>();
    if (importSampleDeliveryFormSamplesValidation(samples)) {
      log.info("Samples valid. Importing...");
      for (Sample s : samples) {
        Sample ms = requestManager.getSampleByBarcode(s.getIdentificationBarcode());
        if (ms != null) {
          // only process if there's a description
          if (!isStringEmptyOrNull(s.getDescription())) {
            ms.setDescription(s.getDescription());
            log.info(ms.getName() + " : Set description -> " + ms.getDescription());

            if (!isStringEmptyOrNull(s.getScientificName())) {
              ms.setScientificName(s.getScientificName());
              log.info(ms.getName() + " : Set scientific name -> " + ms.getScientificName());
              if (checkTaxon) {
                if (foundTaxons.containsKey(s.getScientificName())) {
                  ms.setTaxonIdentifier(foundTaxons.get(s.getScientificName()));
                  log.info(ms.getName() + " : Set previously found taxon -> " + ms.getScientificName());
                } else {
                  String taxon = TaxonomyUtils.checkScientificNameAtNCBI(s.getScientificName());
                  if (taxon != null) {
                    foundTaxons.put(s.getScientificName(), taxon);
                    ms.setTaxonIdentifier(taxon);
                    log.info(ms.getName() + " : Set taxon -> " + ms.getScientificName());
                  }
                }
              }
            }

            if (!s.getNotes().isEmpty()) {
              for (Note n : s.getNotes()) {
                n.setOwner(ms.getSecurityProfile().getOwner());
              }
              ms.setNotes(s.getNotes());
            }

            requestManager.saveSample(ms);
          }
        } else {
          throw new IOException("No such sample " + s.getAlias() + " with barcode: " + s.getIdentificationBarcode());
        }
      }
    } else {
      throw new IOException("Form not valid. Some samples have no description or scientific name");
    }
  }

  public boolean importSampleDeliveryFormSamplesValidation(List<Sample> samples) {
    boolean b = true;
    for (Sample s : samples) {
      if (isStringEmptyOrNull(s.getDescription()) || isStringEmptyOrNull(s.getScientificName())) {
        log.warn(s.getIdentificationBarcode() + ": Sample not valid!");
        b = false;
      }
    }
    return b;
  }
}
