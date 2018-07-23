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

package uk.ac.bbsrc.tgac.miso.core.data.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eaglegenomics.simlims.core.User;
import com.google.common.collect.Lists;

import uk.ac.bbsrc.tgac.miso.core.data.IlluminaRun;
import uk.ac.bbsrc.tgac.miso.core.data.IonTorrentRun;
import uk.ac.bbsrc.tgac.miso.core.data.LS454Run;
import uk.ac.bbsrc.tgac.miso.core.data.OxfordNanoporeRun;
import uk.ac.bbsrc.tgac.miso.core.data.PacBioRun;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SolidRun;
import uk.ac.bbsrc.tgac.miso.core.data.impl.OxfordNanoporeContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;

/**
 * Enum representing the different platform types available
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public enum PlatformType {
  ILLUMINA("Illumina", "Flow Cell", "Lane", "Lanes", "ILLUMINA") {
    @Override
    public Run createRun(User user) {
      return new IlluminaRun(user);
    }
  }, //
  LS454("LS454", "Plate", "Lane", "Lanes", "LS454") {
    @Override
    public Run createRun(User user) {
      return new LS454Run(user);
    }
  }, //
  SOLID("Solid", "Slide", "Lane", "Lanes", "ABI_SOLID") {
    @Override
    public Run createRun(User user) {
      return new SolidRun(user);
    }
  }, //
  IONTORRENT("IonTorrent", "Chip", "Chip", "Chips", null) {
    @Override
    public Run createRun(User user) {
      return new IonTorrentRun(user);
    }
  }, //
  PACBIO("PacBio", "8Pac", "SMRT Cell", "SMRT Cells", null) {
    @Override
    public Run createRun(User user) {
      return new PacBioRun(user);
    }
  }, //
  OXFORDNANOPORE("Oxford Nanopore", "Flow Cell", "Flow Cell", "Flow Cells", null) {
    @Override
    public Run createRun(User user) {
      return new OxfordNanoporeRun(user);
    }

    @Override
    public SequencerPartitionContainer createContainer() {
      return new OxfordNanoporeContainer();
    }
  };

  /**
   * Field key
   */
  private final String key;
  private final String containerName;
  private final String partitionName;
  private final String pluralPartitionName;
  private final String sraName;
  /**
   * Field lookup
   */
  private static final Map<String, PlatformType> lookup = new HashMap<>();

  static {
    for (PlatformType s : EnumSet.allOf(PlatformType.class))
      lookup.put(s.getKey(), s);
  }

  /**
   * Constructs a PlatformType based on a given key
   * 
   * @param key
   *          of type String
   */
  PlatformType(String key, String containerName, String partitionName, String pluralPartitionName, String sraName) {
    this.key = key;
    this.containerName = containerName;
    this.partitionName = partitionName;
    this.pluralPartitionName = pluralPartitionName;
    this.sraName = sraName;
  }

  /**
   * Returns a PlatformType given an enum key
   * 
   * @param key
   *          of type String
   * @return PlatformType
   */
  public static PlatformType get(String key) {
    return lookup.get(key);
  }

  /**
   * Returns the key of this PlatformType enum.
   * 
   * @return String key.
   */
  public String getKey() {
    return key;
  }

  /**
   * Returns the keys of this PlatformType enum.
   * 
   * @return ArrayList<String> keys.
   */
  public static ArrayList<String> getKeys() {
    ArrayList<String> keys = new ArrayList<>();
    for (PlatformType r : PlatformType.values()) {
      keys.add(r.getKey());
    }
    return keys;
  }

  public static List<String> platformTypeNames(Collection<PlatformType> platformTypes) {
    List<String> result = Lists.newArrayList();
    for (PlatformType platformType : platformTypes) {
      result.add(platformType.getKey());
    }
    return result;
  }

  public String getContainerName() {
    return containerName;
  }

  public String getPartitionName() {
    return partitionName;
  }

  public String getSraName() {
    return sraName;
  }

  public abstract Run createRun(User user);

  public SequencerPartitionContainer createContainer() {
    return new SequencerPartitionContainerImpl();
  }

  public String getPluralPartitionName() {
    return pluralPartitionName;
  }

}
