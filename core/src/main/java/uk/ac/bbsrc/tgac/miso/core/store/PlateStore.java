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

package uk.ac.bbsrc.tgac.miso.core.store;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Plate;
import uk.ac.bbsrc.tgac.miso.core.data.Plateable;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingSchemeAware;

/**
 * Defines a DAO interface for storing Plates
 * 
 * @author Rob Davey
 * @date 12-Sep-2011
 * @since 0.1.1
 */
public interface PlateStore extends Store<Plate<? extends List<? extends Plateable>, ? extends Plateable>>, Cascadable,
    Remover<Plate<? extends List<? extends Plateable>, ? extends Plateable>>,
    NamingSchemeAware<Plate<? extends List<? extends Plateable>, ? extends Plateable>> {
  @Override
  public Plate<? extends List<? extends Plateable>, ? extends Plateable> get(long id) throws IOException;

  public List<Plate<? extends List<? extends Plateable>, ? extends Plateable>> listByProjectId(long projectId) throws IOException;

  public List<Plate<? extends List<? extends Plateable>, ? extends Plateable>> listBySearch(String str) throws IOException;

  /**
   * Retrieve a Plate from an underlying data store given an identification barcode
   * 
   * @param barcode
   *          of type String
   * @return Plate
   * @throws java.io.IOException
   *           when
   */
  <T extends List<S>, S extends Plateable> Plate<T, S> getPlateByIdentificationBarcode(String barcode) throws IOException;
}
