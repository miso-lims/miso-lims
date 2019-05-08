package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

@Entity
@Table(name = "SequencingParameters")

public class SequencingParameters implements Serializable, Identifiable, Comparable<SequencingParameters>
{

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0;

  @Enumerated(EnumType.STRING)
  private IlluminaChemistry chemistry;

  @ManyToOne(targetEntity = UserImpl.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "createdBy", nullable = false)
  private User createdBy;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date creationDate;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastUpdated;
  @Column(nullable = false)
  private String name;
  @Column(nullable = false)
  private boolean paired;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long parametersId = UNSAVED_ID;

  @ManyToOne(targetEntity = InstrumentModel.class)
  @JoinColumn(name = "instrumentModelId")
  private InstrumentModel instrumentModel;
  @Column(nullable = false)
  private int readLength;

  @ManyToOne(targetEntity = UserImpl.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "updatedBy", nullable = false)
  private User updatedBy;

  public IlluminaChemistry getChemistry() {
    return chemistry;
  }

  public User getCreatedBy() {
    return createdBy;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  @Override
  public long getId() {
    return parametersId;
  }

  public Date getLastUpdated() {
    return lastUpdated;
  }

  public String getName() {
    return name;
  }

  public InstrumentModel getInstrumentModel() {
    return instrumentModel;
  }


  public int getReadLength() {
    return readLength;
  }

  public User getUpdatedBy() {
    return updatedBy;
  }

  public boolean isPaired() {
    return paired;
  }

  public void setChemistry(IlluminaChemistry chemistry) {
    this.chemistry = chemistry;
  }

  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }

  public void setCreationDate(Date creation) {
    this.creationDate = creation;
  }

  @Override
  public void setId(long id) {
    this.parametersId = id;
  }

  public void setLastUpdated(Date lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPaired(boolean paired) {
    this.paired = paired;
  }

  public void setInstrumentModel(InstrumentModel instrumentModel) {
    this.instrumentModel = instrumentModel;
  }

  public void setReadLength(int readLength) {
    this.readLength = readLength;
  }

  public void setUpdatedBy(User updatedBy) {
    this.updatedBy = updatedBy;
  }

  private static final List<String> sortOrder = getSortOrder();
  private static List<String> getSortOrder() {
    List<String> order = new ArrayList<>();
    order.add("v4 2×126");
    order.add("v4");
    order.add("Rapid Run");
    order.add("10X");
    order.add("v3");
    order.add("High"); // NextSeq
    order.add("Mid"); // NextSeq
    order.add("Nano"); // MiSeq
    order.add("Micro"); // MiSeq
    order.add("Custom"); // All
    order.add("Sequencing"); // Oxford Nanopore
    order.add("Configuration"); // Oxford Nanopore
    order.add("Control"); // Oxford Nanopore
    order.add("Platform"); // Oxford Nanopore
    return Collections.unmodifiableList(order);
  }

  @Override
  public int compareTo(SequencingParameters other) {
    Integer thisSortKey = null;
    for (int i = 0; i < sortOrder.size(); i++) {
      if (name.startsWith(sortOrder.get(i))) {
        thisSortKey = i;
        break;
      }
    }
    if (thisSortKey == null) thisSortKey = 100;

    Integer otherSortKey = null;
    for (int i = 0; i < sortOrder.size(); i++) {
      if (other.name.startsWith(sortOrder.get(i))) {
        otherSortKey = i;
        break;
      }
    }
    if (otherSortKey == null) otherSortKey = 100;

    if (thisSortKey == otherSortKey) {
      return name.compareTo(other.name);
    } else {
      return thisSortKey - otherSortKey;
    }
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

}
