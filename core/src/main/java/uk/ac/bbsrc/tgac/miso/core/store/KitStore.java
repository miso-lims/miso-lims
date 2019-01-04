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
import java.util.List;
import java.util.Map;

import uk.ac.bbsrc.tgac.miso.core.data.Kit;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

/**
 * Defines a DAO interface for storing Kits
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public interface KitStore extends Store<Kit>, PaginatedDataSource<KitDescriptor> {
  Kit getKitByIdentificationBarcode(String barcode) throws IOException;

  Kit getKitByLotNumber(String lotNumber) throws IOException;

  List<Kit> listKitsByType(KitType kitType) throws IOException;

  KitDescriptor getKitDescriptorById(long kitDescriptorId) throws IOException;

  KitDescriptor getKitDescriptorByPartNumber(String partNumber) throws IOException;

  List<KitDescriptor> listAllKitDescriptors() throws IOException;

  List<KitDescriptor> listKitDescriptorsByType(KitType kitType) throws IOException;

  long saveKitDescriptor(KitDescriptor kitDescriptor) throws IOException;

  /**
   * @return a map containing all column names and max lengths from the Kit Descriptor table
   * @throws IOException
   */
  public Map<String, Integer> getKitDescriptorColumnSizes() throws IOException;

  List<LibraryDilution> getDilutionsForKdTsRelationship(KitDescriptor kd, TargetedSequencing ts);

}