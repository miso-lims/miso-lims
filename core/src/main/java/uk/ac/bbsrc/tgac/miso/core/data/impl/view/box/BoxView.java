package uk.ac.bbsrc.tgac.miso.core.data.impl.view.box;

import java.io.Serializable;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Immutable
@Table(name = "Box")
public class BoxView implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private Long boxId;

  private String name;

  private String alias;

  private String locationBarcode;

  public Long getId() {
    return boxId;
  }

  public void setId(Long boxId) {
    this.boxId = boxId;
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

  public String getLocationBarcode() {
    return locationBarcode;
  }

  public void setLocationBarcode(String locationBarcode) {
    this.locationBarcode = locationBarcode;
  }

}
