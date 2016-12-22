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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO. If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.core.store;

import java.io.IOException;
import java.util.List;

import org.joda.time.LocalDate;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import uk.ac.bbsrc.tgac.miso.core.data.KitComponent;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;

/**
 * Defines a DAO interface for storing KitComponents
 *
 * @author Rob Davey, Michal Zak
 * @since 0.0.2
 */
public interface KitComponentStore extends Store<KitComponent> {
  KitComponent getKitComponentByIdentificationBarcode(String barcode) throws IOException;

  List<KitComponent> listKitComponentsByLocationBarcode(String barcode) throws IOException;

  List<KitComponent> listKitComponentsByLotNumber(String lotNumber) throws IOException;

  List<KitComponent> listKitComponentsByReceivedDate(LocalDate receivedDate) throws IOException;

  List<KitComponent> listKitComponentsByExpiryDate(LocalDate expiryDate) throws IOException;

  List<KitComponent> listKitComponentsByExhausted(boolean exhausted) throws IOException;

  List<KitComponent> listKitComponentsByKitComponentDescriptorId(long kitComponentDescriptorId) throws IOException;

  List<KitComponent> listKitComponentsByKitDescriptorId(long kitDescriptorId) throws IOException;

  List<KitComponent> listByLibrary(long libraryId) throws IOException;

  List<KitComponent> listByExperiment(long experimentId) throws IOException;

  List<KitComponent> listByManufacturer(String manufacturerName) throws IOException;

  List<KitComponent> listByType(KitType kitType) throws IOException;

  long saveChangeLog(JSONObject changeLog) throws IOException;

  JSONArray getKitChangeLog() throws IOException;

  JSONArray getKitChangeLogByKitComponentId(long kitComponentId) throws IOException;

  boolean isKitComponentAlreadyLogged(String identificationBarcode) throws IOException;
}