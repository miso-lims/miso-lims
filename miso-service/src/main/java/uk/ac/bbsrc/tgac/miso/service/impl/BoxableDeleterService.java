package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.Deletable;
import uk.ac.bbsrc.tgac.miso.service.BoxService;
import uk.ac.bbsrc.tgac.miso.service.DeleterService;

public interface BoxableDeleterService<T extends Boxable & Deletable> extends DeleterService<T> {
  public BoxService getBoxService();

  @Override
  public default void beforeDelete(T object) throws IOException {
    if (object.getBox() != null) {
      object.getBox().getBoxPositions().remove(object.getBoxPosition());
      getBoxService().save(object.getBox());
    }
  }

}
