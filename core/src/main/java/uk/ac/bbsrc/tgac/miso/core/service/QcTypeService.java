package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;

public interface QcTypeService extends DeleterService<QcType> {

  Long create(QcType qcType) throws IOException;

  void update(QcType qcType) throws IOException;

  Collection<QcType> getAll() throws IOException;

}