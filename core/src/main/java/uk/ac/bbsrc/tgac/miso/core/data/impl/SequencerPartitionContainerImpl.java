package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.eaglegenomics.simlims.core.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import uk.ac.bbsrc.tgac.miso.core.data.BarcodableVisitor;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.SequencerPartitionContainerChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.qc.ContainerQC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcTarget;

/**
 * uk.ac.bbsrc.tgac.miso.core.data.impl
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 14-May-2012
 * @since 0.1.6
 */
@Entity
@Table(name = "SequencerPartitionContainer")
@Inheritance(strategy = InheritanceType.JOINED)
public class SequencerPartitionContainerImpl implements SequencerPartitionContainer {

  private static final long serialVersionUID = 1L;
  public static final long UNSAVED_ID = 0L;

  public static final int DEFAULT_PARTITION_LIMIT = 8;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long containerId = SequencerPartitionContainerImpl.UNSAVED_ID;

  /**
   * identificationBarcode is displayed as "serial number" to the user
   */
  private String identificationBarcode;

  private String description;

  @OneToMany(mappedBy = "container")
  private Set<RunPosition> runPositions;

  @OneToMany(targetEntity = SequencerPartitionContainerChangeLog.class, mappedBy = "sequencerPartitionContainer",
      cascade = CascadeType.REMOVE)
  private final Collection<ChangeLog> changeLog = new ArrayList<>();

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "creator", nullable = false, updatable = false)
  private User creator;

  @Column(name = "created", nullable = false, updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date creationTime;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "lastModifier", nullable = false)
  private User lastModifier;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastModified;

  @OneToMany(targetEntity = PartitionImpl.class, mappedBy = "sequencerPartitionContainer", cascade = CascadeType.ALL)
  @OrderBy("partitionNumber")
  private List<Partition> partitions = new ArrayList<>();

  @ManyToOne
  @JoinColumn(name = "clusteringKit")
  private KitDescriptor clusteringKit;

  private String clusteringKitLot;

  @ManyToOne
  @JoinColumn(name = "multiplexingKit")
  private KitDescriptor multiplexingKit;

  private String multiplexingKitLot;

  @Column(name = "movieTime")
  private double movieTime;


  @ManyToOne
  @JoinColumn(name = "sequencingContainerModelId")
  private SequencingContainerModel model;

  @OneToMany(targetEntity = ContainerQC.class, mappedBy = "container")
  private final Collection<ContainerQC> containerQCs = new TreeSet<>();

  /**
   * Construct a new SequencerPartitionContainer with a default empty SecurityProfile
   */
  public SequencerPartitionContainerImpl() {
    setPartitionLimit(DEFAULT_PARTITION_LIMIT);
  }

  @Override
  public User getLastModifier() {
    return lastModifier;
  }

  @Override
  public void setLastModifier(User lastModifier) {
    this.lastModifier = lastModifier;
  }

  @Override
  public Date getLastModified() {
    return lastModified;
  }

  @Override
  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  @Override
  public User getCreator() {
    return creator;
  }

  @Override
  public void setCreator(User creator) {
    this.creator = creator;
  }

  @Override
  public Date getCreationTime() {
    return creationTime;
  }

  @Override
  public void setCreationTime(Date created) {
    this.creationTime = created;
  }

  @Override
  public Collection<ChangeLog> getChangeLog() {
    return changeLog;
  }

  @Override
  public long getId() {
    return containerId;
  }

  @Override
  public void setId(long id) {
    this.containerId = id;
  }

  @Override
  public String getIdentificationBarcode() {
    return identificationBarcode;
  }

  @Override
  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = identificationBarcode;
  }

  @Override
  public double getMovieTime() {
    return movieTime;
  }

  @Override
  public void setMovieTime(double movieTime) {
    this.movieTime = movieTime;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String getLabelText() {
    return getIdentificationBarcode() + " (" + getModel().getAlias() + ")";
  }

  @Override
  public Set<RunPosition> getRunPositions() {
    return runPositions;
  }

  @Override
  public Run getLastRun() {
    if (getRunPositions() == null) {
      return null;
    }
    return getRunPositions().stream().map(RunPosition::getRun).filter(r -> r.getStartDate() != null)
        .max((a, b) -> a.getStartDate().compareTo(b.getStartDate()))
        .orElse(null);
  }

  @Override
  public List<Partition> getPartitions() {
    return partitions;
  }

  @Override
  public void setPartitions(List<Partition> partitions) {
    this.partitions = partitions;
    for (Partition p : partitions) {
      p.setSequencerPartitionContainer(this);
    }
    Collections.sort(partitions, partitionNumberComparator);
  }

  @Override
  public Partition getPartitionAt(int partitionNumber) throws IndexOutOfBoundsException {
    return partitions.get(partitionNumber - 1);
  }

  @Override
  public void setPartitionLimit(int partitionLimit) {
    getPartitions().clear();
    for (int i = 0; i < partitionLimit; i++) {
      PartitionImpl partition = new PartitionImpl();
      partition.setSequencerPartitionContainer(this);
      partition.setPartitionNumber(i + 1);
      getPartitions().add(partition);
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (obj == this)
      return true;
    if (!(obj instanceof SequencerPartitionContainer))
      return false;
    SequencerPartitionContainer them = (SequencerPartitionContainer) obj;
    // If not saved, then compare resolved actual objects. Otherwise just compare IDs.
    if (getId() == SequencerPartitionContainerImpl.UNSAVED_ID
        || them.getId() == SequencerPartitionContainerImpl.UNSAVED_ID) {
      return getIdentificationBarcode().equals(them.getIdentificationBarcode());
    } else {
      return getId() == them.getId();
    }
  }

  @Override
  public int hashCode() {
    if (getId() != SequencerPartitionContainerImpl.UNSAVED_ID) {
      return (int) getId();
    } else {
      int hashcode = -1;
      if (getIdentificationBarcode() != null)
        hashcode = 37 * hashcode + getIdentificationBarcode().hashCode();
      return hashcode;
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getId());
    sb.append(" : ");
    sb.append(getIdentificationBarcode());
    return sb.toString();
  }

  @Override
  public int compareTo(SequencerPartitionContainer t) {
    if (getId() < t.getId())
      return -1;
    if (getId() > t.getId())
      return 1;
    return 0;
  }

  @Override
  public ChangeLog createChangeLog(String summary, String columnsChanged, User user) {
    SequencerPartitionContainerChangeLog changeLog = new SequencerPartitionContainerChangeLog();
    changeLog.setSequencerPartitionContainer(this);
    changeLog.setSummary(summary);
    changeLog.setColumnsChanged(columnsChanged);
    changeLog.setUser(user);
    return changeLog;
  }

  @Override
  public LocalDate getBarcodeDate() {
    return LocalDate.ofInstant(getCreationTime().toInstant(), ZoneId.systemDefault());
  }

  @Override
  public String getAlias() {
    return getIdentificationBarcode();
  }

  @Override
  public KitDescriptor getClusteringKit() {
    return clusteringKit;
  }

  @Override
  public void setClusteringKit(KitDescriptor clusteringKit) {
    this.clusteringKit = clusteringKit;
  }

  @Override
  public String getClusteringKitLot() {
    return clusteringKitLot;
  }

  @Override
  public void setClusteringKitLot(String clusteringKitLot) {
    this.clusteringKitLot = clusteringKitLot;
  }

  @Override
  public KitDescriptor getMultiplexingKit() {
    return multiplexingKit;
  }

  @Override
  public void setMultiplexingKit(KitDescriptor multiplexingKit) {
    this.multiplexingKit = multiplexingKit;
  }

  @Override
  public String getMultiplexingKitLot() {
    return multiplexingKitLot;
  }

  @Override
  public void setMultiplexingKitLot(String multiplexingKitLot) {
    this.multiplexingKitLot = multiplexingKitLot;
  }

  private static final Comparator<Partition> partitionNumberComparator = new Comparator<Partition>() {

    @Override
    public int compare(Partition o1, Partition o2) {
      return o1.getPartitionNumber().compareTo(o2.getPartitionNumber());
    }

  };

  @Override
  public Collection<ContainerQC> getQCs() {
    return containerQCs;
  }

  @Override
  public QcTarget getQcTarget() {
    return QcTarget.Container;
  }

  @Override
  public SequencingContainerModel getModel() {
    return model;
  }

  @Override
  public void setModel(SequencingContainerModel model) {
    this.model = model;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public String getDeleteType() {
    return getModel().getPlatformType().getContainerName();
  }

  @Override
  public String getDeleteDescription() {
    return getIdentificationBarcode() + " (" + getModel().getAlias() + ")";
  }

  @Override
  public <T> T visit(BarcodableVisitor<T> visitor) {
    return visitor.visitContainer(this);
  }
}
