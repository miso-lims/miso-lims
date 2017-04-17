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

package uk.ac.bbsrc.tgac.miso.core.store;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

/**
 * Defines a DAO interface for storing Samples
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public interface SampleStore extends Store<Sample>, Remover<Sample>, PaginatedDataSource<Sample> {

  /**
   * List all Samples that match a search criteria
   * 
   * @param query
   *          of type String
   * @return Collection<Sample>
   * @throws IOException
   *           when
   */
  Collection<Sample> listBySearch(String query) throws IOException;

  /**
   * Retrieve a Sample from an underlying data store given a Sample ID barcode
   * <p/>
   * This method intends to retrieve objects in an 'ignorant' fashion, i.e.
   * 
   * @param barcode
   *          of type String
   * @return Sample
   * @throws IOException
   *           when
   */
  Sample getByBarcode(String barcode) throws IOException;

  /**
   * List all Samples related to a parent Project given a Project ID
   * 
   * @param projectId
   *          of type long
   * @return Collection<Sample>
   * @throws IOException
   *           when
   */
  Collection<Sample> listByProjectId(long projectId) throws IOException;

  /**
   * List all Samples by a given alias
   * 
   * @param alias
   *          of type String
   * @return Collection<Sample>
   * @throws IOException
   *           when
   */
  Collection<Sample> listByAlias(String alias) throws IOException;

  /**
   * List all SampleTypes
   * 
   * @return Collection<String>
   * @throws IOException
   *           when
   */
  Collection<String> listAllSampleTypes() throws IOException;

  /**
   * List all persisted objects
   * 
   * @return Collection<Sample>
   * @throws IOException
   *           when the objects cannot be retrieved
   */
  Collection<Sample> listAllWithLimit(long limit) throws IOException;

  /**
   * List all persisted objects
   * 
   * @return Collection<Sample>
   * @throws IOException
   *           when the objects cannot be retrieved
   */
  Collection<Sample> listAllByReceivedDate(long limit) throws IOException;

  /**
   * List all Samples associated with identificationBarcodes from the given identificationBarcode list
   * 
   * @return Collection<Sample>
   * @throws IOException
   *           when the objects cannot be retrieved
   */
  Collection<Sample> getByBarcodeList(Collection<String> barcodeList) throws IOException;
  
  /**
   * @return a map containing all column names and max lengths from the Sample table
   * @throws IOException
   */
  public Map<String, Integer> getSampleColumnSizes() throws IOException;
  
  /**
   * List all Samples associated with ids from the given id list
   * 
   * @return Collection<Sample>
   * @throws IOException
   *           when the objects cannot be retrieved
   */
  Collection<Sample> getByIdList(List<Long> idList) throws IOException;

}
