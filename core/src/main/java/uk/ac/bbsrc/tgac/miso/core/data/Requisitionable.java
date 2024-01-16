package uk.ac.bbsrc.tgac.miso.core.data;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition;

public interface Requisitionable extends Aliasable, Nameable {

  Requisition getRequisition();

  void setRequisition(Requisition requisition);

}
