package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;

public interface QcTypeService {

  Long create(QcType qcType) throws IOException;

  void update(QcType qcType) throws IOException;

  Collection<QcType> getAll() throws IOException;

  QcType get(long id) throws IOException;

  AuthorizationManager getAuthorizationManager();

}