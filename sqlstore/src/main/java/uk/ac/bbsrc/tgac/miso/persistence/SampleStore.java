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

package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.EntityReference;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

/**
 * Defines a DAO interface for storing Samples
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public interface SampleStore extends Store<Sample>, PaginatedDataSource<Sample> {

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
   * List all Samples associated with identificationBarcodes from the given identificationBarcode list
   * 
   * @return Collection<Sample>
   * @throws IOException
   *           when the objects cannot be retrieved
   */
  Collection<Sample> getByBarcodeList(Collection<String> barcodeList) throws IOException;
  
  /**
   * List all Samples associated with ids from the given id list
   * 
   * @return Collection<Sample>
   * @throws IOException
   *           when the objects cannot be retrieved
   */
  List<Sample> getByIdList(List<Long> idList) throws IOException;

  List<Sample> list() throws IOException;

  Sample getSample(long id) throws IOException;

  Sample getByPreMigrationId(Long id) throws IOException;

  Long addSample(Sample sample) throws IOException;

  void deleteSample(Sample sample);

  void update(Sample sample) throws IOException;

  Long countAll() throws IOException;

  /**
   * List all the identities associated with a given project which have at least one external name which exactly or partially matches the
   * input String or a comma-separated portion of the input String.
   * 
   * @param externalName a single external name String
   * @param projectId Long
   * @param boolean exactMatch
   * @return List<Sample> set of Identities belonging to a given project which have an external name that matches the input string
   * @throws IOException
   */
  Collection<SampleIdentity> getIdentitiesByExternalNameOrAliasAndProject(String externalName, Long projectId, boolean exactMatch)
      throws IOException;

  /**
   * Find a ghost Tissue with Identity, Tissue Origin, Tissue Type, times received, tube number, and passage number matching the provided
   * Tissue
   * 
   * @param tissue partially-formed tissue, minimally containing all of the above-noted attributes. Tissue Origin, Tissue Type, and
   *          parent (Identity) must have their IDs set. Passage number may be null
   * @return the matching ghost tissue, if one exists; null otherwise
   * @throws IOException
   */
  public SampleTissue getMatchingGhostTissue(SampleTissue tissue) throws IOException;

  public long getChildSampleCount(Sample sample);

  /**
   * @param sample the "current" sample
   * @return a reference to the sample in the same project as the "current" sample with the next ID in numerical order
   */
  public EntityReference getNextInProject(Sample sample);

  /**
   * @param sample the "current" sample
   * @return a reference to the sample in the same project as the "current" sample with the previous ID in numerical order
   */
  public EntityReference getPreviousInProject(Sample sample);

}
