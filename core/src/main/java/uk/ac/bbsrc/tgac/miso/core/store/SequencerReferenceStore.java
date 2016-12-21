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
import java.util.Map;

import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

/**
 * Defines a DAO interface for storing SequencerReferences
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public interface SequencerReferenceStore extends Store<SequencerReference>, Remover<SequencerReference> {

  /**
   * Get a SequencerReference by a given name
   * 
   * @param referenceName
   *          of type String
   * @return SequencerReference
   * @throws IOException
   *           when
   */
  SequencerReference getByName(String referenceName) throws IOException;

  /**
   * Get the SequencerReference which was the pre-upgrade SequencerReference for the SequencerReference provided (by its id)
   * Returns null if provided SequencerReference has not been upgraded.
   * 
   * @param upgradedReferenceId
   *          of type long
   * @return SequencerReference
   * @throws IOException if there is more than one pre-upgrade SequencerReference for the provided SequencerReference
   */
  SequencerReference getByUpgradedReference(long upgradedReferenceId) throws IOException;

  /**
   * Get all SequencerReferences of a given PlatformType, e.g. PlatformType.ILLUMINA
   * 
   * @param platformType
   *          of type PlatformType
   * @return Collection<SequencerReference>
   * @throws IOException
   *           when
   */
  Collection<SequencerReference> listByPlatformType(PlatformType platformType) throws IOException;
  
  /**
   * @return a map containing all column names and max lengths from the Sequencer Reference table
   * @throws IOException
   */
  public Map<String, Integer> getSequencerReferenceColumnSizes() throws IOException;
}
