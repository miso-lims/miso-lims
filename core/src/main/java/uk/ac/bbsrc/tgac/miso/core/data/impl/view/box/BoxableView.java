package uk.ac.bbsrc.tgac.miso.core.data.impl.view.box;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.function.Function;

import javax.persistence.MappedSuperclass;

import uk.ac.bbsrc.tgac.miso.core.data.Aliasable;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;

@MappedSuperclass
public abstract class BoxableView implements Aliasable, Serializable {

  private static final long serialVersionUID = 1L;

  private String name;
  private String alias;
  private String identificationBarcode;
  private BigDecimal volume;
  private boolean discarded;

  public abstract EntityType getEntityType();

  public abstract BoxablePositionView getBoxablePosition();

  public abstract String getLocationBarcode();

  public abstract boolean isDistributed();

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
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

  public BigDecimal getVolume() {
    return volume;
  }

  public void setVolume(BigDecimal volume) {
    this.volume = volume;
  }

  public boolean isDiscarded() {
    return discarded;
  }

  public void setDiscarded(boolean discarded) {
    if (discarded) {
      setVolume(BigDecimal.ZERO);
    }
    this.discarded = discarded;
  }

  public Long getBoxId() {
    return getBoxProperty(BoxView::getId);
  }

  public String getBoxName() {
    return getBoxProperty(BoxView::getName);
  }

  public String getBoxAlias() {
    return getBoxProperty(BoxView::getAlias);
  }

  public String getBoxPosition() {
    return getBoxablePosition() == null ? null : getBoxablePosition().getPosition();
  }

  public String getBoxLocationBarcode() {
    return getBoxProperty(BoxView::getLocationBarcode);
  }

  private <T> T getBoxProperty(Function<BoxView, T> getter) {
    return getBoxablePosition() == null ? null : getter.apply(getBoxablePosition().getBox());
  }

  public abstract ChangeLog makeChangeLog();

}
