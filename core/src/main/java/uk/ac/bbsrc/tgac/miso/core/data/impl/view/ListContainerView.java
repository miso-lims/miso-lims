package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.Immutable;

import com.eaglegenomics.simlims.core.User;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;

@Entity
@Table(name = "SequencerPartitionContainer")
@Immutable
public class ListContainerView implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private long containerId;

  @ManyToOne
  @JoinColumn(name = "sequencingContainerModelId")
  private SequencingContainerModel model;

  private String identificationBarcode;

  @ManyToOne
  @JoinColumn(name = "clusteringKit")
  private KitDescriptor clusteringKit;

  @ManyToOne
  @JoinColumn(name = "multiplexingKit")
  private KitDescriptor multiplexingKit;

  @OneToMany(targetEntity = PartitionImpl.class)
  @OrderBy("partitionNumber")
  @JoinColumn(name = "containerId")
  private List<Partition> partitions;

  @ManyToMany
  @JoinTable(name = "Run_SequencerPartitionContainer", joinColumns = {
      @JoinColumn(name = "containers_containerId")}, inverseJoinColumns = {@JoinColumn(name = "Run_runId")})
  private Set<ListContainerRunView> runs;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "creator")
  private User creator;

  @Temporal(TemporalType.TIMESTAMP)
  private Date created;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "lastModifier")
  private User lastModifier;

  @Temporal(TemporalType.TIMESTAMP)
  private Date lastModified;

  public long getId() {
    return containerId;
  }

  public void setId(long id) {
    this.containerId = id;
  }

  public SequencingContainerModel getModel() {
    return model;
  }

  public void setModel(SequencingContainerModel model) {
    this.model = model;
  }

  public String getIdentificationBarcode() {
    return identificationBarcode;
  }

  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = identificationBarcode;
  }

  public KitDescriptor getClusteringKit() {
    return clusteringKit;
  }

  public void setClusteringKit(KitDescriptor clusteringKit) {
    this.clusteringKit = clusteringKit;
  }

  public KitDescriptor getMultiplexingKit() {
    return multiplexingKit;
  }

  public void setMultiplexingKit(KitDescriptor multiplexingKit) {
    this.multiplexingKit = multiplexingKit;
  }

  public List<Partition> getPartitions() {
    if (partitions == null) {
      partitions = new ArrayList<>();
    }
    return partitions;
  }

  public Set<ListContainerRunView> getRuns() {
    if (runs == null) {
      runs = new HashSet<>();
    }
    return runs;
  }

  public User getCreator() {
    return creator;
  }

  public void setCreator(User creator) {
    this.creator = creator;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public User getLastModifier() {
    return lastModifier;
  }

  public void setLastModifier(User lastModifier) {
    this.lastModifier = lastModifier;
  }

  public Date getLastModified() {
    return lastModified;
  }

  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

}
