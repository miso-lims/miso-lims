package uk.ac.bbsrc.tgac.miso.persistence.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.qc.LibraryAliquotQC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.LibraryAliquotQC_;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcCorrespondingField;
import uk.ac.bbsrc.tgac.miso.persistence.LibraryAliquotQcStore;

import java.io.IOException;
import java.math.BigDecimal;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateLibraryAliquotQcDao extends HibernateQcStore<LibraryAliquotQC> implements LibraryAliquotQcStore {

    public HibernateLibraryAliquotQcDao() {
        super(LibraryAliquot.class, LibraryAliquotQC.class);
    }

    @Override
    public void updateEntity(long id, QcCorrespondingField correspondingField, BigDecimal value, String units)
        throws IOException {
        LibraryAliquot entity = (LibraryAliquot) currentSession().get(LibraryAliquot.class, id);
        correspondingField.updateField(entity, value, units);
        currentSession().merge(entity);
    }

    @Override
    public String getIdProperty(){
        return LibraryAliquotQC_.QC_ID;
    }
}
