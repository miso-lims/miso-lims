package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.BoxableId;

@Entity
@Immutable
@Table(name = "BoxableView")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class BoxableView implements Serializable {

  private static final long serialVersionUID = 1L;

  @EmbeddedId
  private BoxableId id;

  private String name;
  private String alias;
  private String identificationBarcode;
  private String locationBarcode;
  private Double volume;
  private boolean discarded;
  private boolean distributed;
  private Long boxId;
  private String boxName;
  private String boxAlias;
  private String boxPosition;
  private String boxLocationBarcode;
  private Long preMigrationId;
  private Long sampleClassId;

  public static BoxableView fromBoxable(Boxable boxable) {
    BoxableView v = new BoxableView();
    v.setId(new BoxableId(boxable.getEntityType(), boxable.getId()));
    v.setName(boxable.getName());
    v.setAlias(boxable.getAlias());
    v.setIdentificationBarcode(boxable.getIdentificationBarcode());
    v.setLocationBarcode(boxable.getLocationBarcode());
    v.setVolume(boxable.getVolume());
    v.setDiscarded(boxable.isDiscarded());
    v.setDistributed(boxable.isDistributed());
    Box box = boxable.getBox();
    if (box != null) {
      v.setBoxId(box.getId());
      v.setBoxName(box.getName());
      v.setBoxAlias(box.getAlias());
      v.setBoxPosition(boxable.getBoxPosition());
      v.setBoxLocationBarcode(box.getLocationBarcode());
    }
    v.setPreMigrationId(boxable.getPreMigrationId());
    return v;
  }

  public BoxableId getId() {
    return id;
  }

  public void setId(BoxableId id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getIdentificationBarcode() {
    return identificationBarcode;
  }

  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = identificationBarcode;
  }

  public String getLocationBarcode() {
    return locationBarcode;
  }

  public void setLocationBarcode(String locationBarcode) {
    this.locationBarcode = locationBarcode;
  }

  public Double getVolume() {
    return volume;
  }

  public void setVolume(Double volume) {
    this.volume = volume;
  }

  public boolean isDiscarded() {
    return discarded;
  }

  public void setDiscarded(boolean discarded) {
    if (discarded) setVolume(0D);
    this.discarded = discarded;
  }

  public boolean isDistributed() {
    return distributed;
  }

  public void setDistributed(boolean distributed) {
    if (distributed) setVolume(0D);
    this.distributed = distributed;
  }

  public Long getBoxId() {
    return boxId;
  }

  public void setBoxId(Long boxId) {
    this.boxId = boxId;
  }

  public String getBoxName() {
    return boxName;
  }

  public void setBoxName(String boxName) {
    this.boxName = boxName;
  }

  public String getBoxAlias() {
    return boxAlias;
  }

  public void setBoxAlias(String boxAlias) {
    this.boxAlias = boxAlias;
  }

  public String getBoxPosition() {
    return boxPosition;
  }

  public void setBoxPosition(String position) {
    this.boxPosition = position;
  }

  public String getBoxLocationBarcode() {
    return boxLocationBarcode;
  }

  public void setBoxLocationBarcode(String boxLocationBarcode) {
    this.boxLocationBarcode = boxLocationBarcode;
  }

  public Long getPreMigrationId() {
    return preMigrationId;
  }

  public void setPreMigrationId(Long preMigrationId) {
    this.preMigrationId = preMigrationId;
  }

  public Long getSampleClassId() {
    return sampleClassId;
  }

  public void setSampleClassId(Long sampleClassId) {
    this.sampleClassId = sampleClassId;
  }

}
