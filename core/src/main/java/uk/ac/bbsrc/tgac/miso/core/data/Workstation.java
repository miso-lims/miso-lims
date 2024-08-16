package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
public class Workstation implements Aliasable, Barcodable, Deletable, Serializable {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long workstationId = UNSAVED_ID;

  private String alias;
  private String description;
  private String identificationBarcode;

  @Override
  public long getId() {
    return workstationId;
  }

  @Override
  public void setId(long id) {
    this.workstationId = id;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public String getDeleteType() {
    return "Workstation";
  }

  @Override
  public String getDeleteDescription() {
    return getAlias();
  }

  @Override
  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
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
  public int hashCode() {
    return Objects.hash(workstationId, alias, description, identificationBarcode);
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equals(this, obj,
        Workstation::getId,
        Workstation::getAlias,
        Workstation::getDescription,
        Workstation::getIdentificationBarcode);
  }

  @Override
  public String getLabelText() {
    return getAlias();
  }

  @Override
  public LocalDate getBarcodeDate() {
    return null;
  }

  @Override
  public <T> T visit(BarcodableVisitor<T> visitor) {
    return visitor.visitWorkstation(this);
  }

}
