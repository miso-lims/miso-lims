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

package uk.ac.bbsrc.tgac.tagbarcode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.fluxion.spi.ServiceProvider;
import uk.ac.bbsrc.tgac.miso.core.data.TagBarcode;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.service.RequestManagerAware;
import uk.ac.bbsrc.tgac.miso.core.service.tagbarcode.TagBarcodeStrategy;

/**
 * uk.ac.bbsrc.tgac.tagbarcode
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 18/12/12
 * @since 0.1.9
 */
@ServiceProvider
public class MixedBase16sV4TagBarcodeStrategy implements TagBarcodeStrategy, RequestManagerAware {
  protected static final Logger log = LoggerFactory.getLogger(MixedBase16sV4TagBarcodeStrategy.class);

  private Map<Integer, Set<TagBarcode>> tagBarcodeMap = new HashMap<Integer, Set<TagBarcode>>();

  private RequestManager requestManager;

  @Override
  public RequestManager getRequestManager() {
    return requestManager;
  }

  @Override
  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  @Override
  public final String getName() {
    return "16s V4 Indices";
  }

  @Override
  public PlatformType getPlatformType() {
    return PlatformType.ILLUMINA;
  }

  @Override
  public final int getNumApplicableBarcodes() {
    return 1;
  }

  @Override
  public Map<Integer, Set<TagBarcode>> getApplicableBarcodes() {
    if (tagBarcodeMap.isEmpty()) {
      if (requestManager != null) {
        tagBarcodeMap.put(1, new TreeSet<TagBarcode>());

        try {
          List<TagBarcode> barcodes = new ArrayList<TagBarcode>(
              requestManager.listAllTagBarcodesByPlatform(PlatformType.ILLUMINA.getKey()));
          for (TagBarcode t : barcodes) {
            if (getName().equals(t.getStrategyName()) && t.getName() != null && t.getName().startsWith("RP")) {
              log.debug("Registering tag barcode: " + t.getName());
              tagBarcodeMap.get(1).add(t);
            }
          }
        } catch (IOException e) {
          log.error("get barcodes", e);
        }
      }
    }
    return tagBarcodeMap;
  }

  @Override
  public Set<TagBarcode> getApplicableBarcodesForPosition(int position) {
    if (position < 1 || position > getNumApplicableBarcodes()) {
      throw new IndexOutOfBoundsException("This TagBarcodeStrategy only has " + getNumApplicableBarcodes() + " valid positions");
    }
    return tagBarcodeMap.get(position);
  }

  @Override
  public void reload() {
    tagBarcodeMap.clear();
    getApplicableBarcodes();
  }
}
