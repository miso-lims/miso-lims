package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.boxposition.LibraryAliquotBoxPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.boxposition.LibraryBoxPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.boxposition.PoolBoxPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.boxposition.SampleBoxPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.box.BoxableView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.box.LibraryAliquotBoxableView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.box.LibraryBoxableView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.box.PoolBoxableView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.box.SampleBoxableView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferView;

/**
 * This interface simply describes an object that can be placed into a box. i.e. Sample, Library
 * 
 */
public interface Boxable extends Aliasable, Barcodable, Nameable, Serializable {

  public enum EntityType {
    SAMPLE("Sample", SampleImpl.class, SampleBoxableView.class, SampleBoxPosition::new, SampleImpl_.SAMPLE_ID), //
    LIBRARY("Library", LibraryImpl.class, LibraryBoxableView.class, LibraryBoxPosition::new, LibraryImpl_.LIBRARY_ID), //
    LIBRARY_ALIQUOT("Library Aliquot", LibraryAliquot.class, LibraryAliquotBoxableView.class,
        LibraryAliquotBoxPosition::new, LibraryAliquot_.ALIQUOT_ID), //
    POOL("Pool", PoolImpl.class, PoolBoxableView.class, PoolBoxPosition::new, PoolImpl_.POOL_ID); //

    private static final Map<String, EntityType> lookup;

    static {
      Map<String, EntityType> map = new HashMap<>();
      for (EntityType type : EntityType.values()) {
        map.put(type.getLabel(), type);
      }
      lookup = map;
    }

    private final String label;
    private final Class<? extends Boxable> persistClass;
    private final Class<? extends BoxableView> viewClass;
    private final Supplier<? extends AbstractBoxPosition> positionConstructor;
    private final String idProperty;

    private EntityType(String label, Class<? extends Boxable> persistClass, Class<? extends BoxableView> viewClass,
        Supplier<? extends AbstractBoxPosition> positionConstructor, String idProperty) {
      this.label = label;
      this.persistClass = persistClass;
      this.viewClass = viewClass;
      this.positionConstructor = positionConstructor;
      this.idProperty = idProperty;
    }

    public static EntityType get(String label) {
      return lookup.get(label);
    }

    public String getLabel() {
      return label;
    }

    public Class<? extends Boxable> getPersistClass() {
      return persistClass;
    }

    public Class<? extends BoxableView> getViewClass() {
      return viewClass;
    }

    public AbstractBoxPosition makeBoxPosition() {
      return positionConstructor.get();
    }

    public String getIdProperty() {
      return idProperty;
    }
  }

  public EntityType getEntityType();

  /**
   * Returns the alias of this Sample object.
   *
   * @return String alias.
   */
  @Override
  public String getAlias();

  public Box getBox();

  public String getBoxPosition();

  public Date getLastModified();

  public String getLocationBarcode();

  /**
   * Returns the volume of the Implementor
   * 
   * @return volume
   */
  public BigDecimal getVolume();

  /**
   * Returns whether or not the Implementor has been emptied
   * 
   * @return emptied
   */
  public boolean isDiscarded();

  /**
   * Sets the alias of this Sample object.
   *
   * @param alias alias.
   */
  public void setAlias(String alias);

  /**
   * Sets the 'emptied' attribute for the Implementor
   * 
   * @param boolean emptied
   */
  public void setDiscarded(boolean emptied);

  /**
   * Sets the volume of the Implementor
   * 
   * @param volume
   */
  public void setVolume(BigDecimal volume);

  public Long getPreMigrationId();

  public void setLastModified(Date lastModified);

  public void setLastModifier(User user);

  /**
   * Remove Box and position information from this Boxable
   */
  public void removeFromBox();

  public Set<ListTransferView> getTransferViews();

  public default ListTransferView getReceiptTransfer() {
    return getTransferViews().stream()
        .filter(ListTransferView::isReceipt)
        .findFirst().orElse(null);
  }

  public default ListTransferView getDistributionTransfer() {
    return getTransferViews().stream()
        .filter(ListTransferView::isDistribution)
        .findFirst().orElse(null);
  }

}
