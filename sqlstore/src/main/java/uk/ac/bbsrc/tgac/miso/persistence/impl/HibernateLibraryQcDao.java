package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.math.BigDecimal;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.qc.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcCorrespondingField;
import uk.ac.bbsrc.tgac.miso.persistence.LibraryQcStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateLibraryQcDao extends HibernateQcStore<LibraryQC> implements LibraryQcStore {

  public HibernateLibraryQcDao() {
    super(LibraryImpl.class, LibraryQC.class);
  }

  @Override
  public void updateEntity(long id, QcCorrespondingField correspondingField, BigDecimal value, String units)
      throws IOException {
    Library library = (Library) currentSession().get(LibraryImpl.class, id);
    correspondingField.updateField(library, value, units);
    currentSession().merge(library);
  }

  @Override
  public String getIdProperty() {
    return LibraryImpl_.LIBRARY_ID;
  }

}
