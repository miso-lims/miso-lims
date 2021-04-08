package uk.ac.bbsrc.tgac.miso.core.data.impl.view.box;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import uk.ac.bbsrc.tgac.miso.core.data.Boxable.EntityType;

@Entity
@Immutable
@Table(name = "Sample")
public class SampleBoxableView extends BoxableView {

  private static final long serialVersionUID = 1L;

  @Id
  private long sampleId;

  private String locationBarcode;

  private Long sampleClassId;

  @OneToOne
  @JoinColumn(name = "sampleId")
  private SampleBoxablePositionView boxPosition;

  @ManyToMany
  @JoinTable(name = "Transfer_Sample", joinColumns = @JoinColumn(name = "sampleId"), inverseJoinColumns = @JoinColumn(name = "transferId"))
  private Set<BoxableTransferView> transfers;

  @Override
  public EntityType getEntityType() {
    return EntityType.SAMPLE;
  }

  @Override
  public long getId() {
    return sampleId;
  }

  @Override
  public void setId(long id) {
    this.sampleId = id;
  }

  @Override
  public String getLocationBarcode() {
    return locationBarcode;
  }

  public void setLocationBarcode(String locationBarcode) {
    this.locationBarcode = locationBarcode;
  }

  public Long getSampleClassId() {
    return sampleClassId;
  }

  public void setSampleClassId(Long sampleClassId) {
    this.sampleClassId = sampleClassId;
  }

  @Override
  public SampleBoxablePositionView getBoxablePosition() {
    return boxPosition;
  }

  public void setBoxablePosition(SampleBoxablePositionView boxPosition) {
    this.boxPosition = boxPosition;
  }

  public Set<BoxableTransferView> getTransfers() {
    if (transfers == null) {
      transfers = new HashSet<>();
    }
    return transfers;
  }

  @Override
  public boolean isDistributed() {
    return getTransfers().stream().anyMatch(x -> x.getRecipient() != null);
  }

}
