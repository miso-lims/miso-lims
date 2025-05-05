package uk.ac.bbsrc.tgac.miso.core.data.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import uk.ac.bbsrc.tgac.miso.core.data.IlluminaRun;
import uk.ac.bbsrc.tgac.miso.core.data.IonTorrentRun;
import uk.ac.bbsrc.tgac.miso.core.data.LS454Run;
import uk.ac.bbsrc.tgac.miso.core.data.OxfordNanoporeRun;
import uk.ac.bbsrc.tgac.miso.core.data.PacBioRun;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SolidRun;
import uk.ac.bbsrc.tgac.miso.core.data.UltimaRun;
import uk.ac.bbsrc.tgac.miso.core.data.impl.OxfordNanoporeContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;

/**
 * Enum representing the different platform types available
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public enum PlatformType {
  ILLUMINA("Illumina", "Flow Cell", "Lane", "Lanes", "ILLUMINA", false) {
    @Override
    public Run createRun() {
      return new IlluminaRun();
    }
  }, //
  LS454("LS454", "Plate", "Lane", "Lanes", "LS454", false) {
    @Override
    public Run createRun() {
      return new LS454Run();
    }
  }, //
  SOLID("Solid", "Slide", "Lane", "Lanes", "ABI_SOLID", false) {
    @Override
    public Run createRun() {
      return new SolidRun();
    }
  }, //
  IONTORRENT("IonTorrent", "Chip", "Chip", "Chips", null, false) {
    @Override
    public Run createRun() {
      return new IonTorrentRun();
    }
  }, //
  PACBIO("PacBio", "SMRT Cell", "SMRT Cell Contents", "SMRT Cell Contents", null, true) {
    @Override
    public Run createRun() {
      return new PacBioRun();
    }
  }, //
  OXFORDNANOPORE("Oxford Nanopore", "Flow Cell", "Flow Cell", "Flow Cells", null, false) {
    @Override
    public Run createRun() {
      return new OxfordNanoporeRun();
    }

    @Override
    public SequencerPartitionContainer createContainer() {
      return new OxfordNanoporeContainer();
    }
  }, //
  ULTIMA("Ultima", "Wafer", "Wafer Contents", "Wafer Contents", null, false) {
    @Override
    public Run createRun() {
      return new UltimaRun();
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
  private final boolean containerLevelParameters;
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
   * @param key of type String
   */
  PlatformType(String key, String containerName, String partitionName, String pluralPartitionName, String sraName,
      boolean containerLevelParameters) {
    this.key = key;
    this.containerName = containerName;
    this.partitionName = partitionName;
    this.pluralPartitionName = pluralPartitionName;
    this.sraName = sraName;
    this.containerLevelParameters = containerLevelParameters;
  }

  /**
   * Returns a PlatformType given an enum key
   * 
   * @param key of type String
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

  public abstract Run createRun();

  public SequencerPartitionContainer createContainer() {
    return new SequencerPartitionContainerImpl();
  }

  public String getPluralPartitionName() {
    return pluralPartitionName;
  }

  public boolean hasContainerLevelParameters() {
    return containerLevelParameters;
  }
}
