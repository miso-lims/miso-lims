package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.math.BigDecimal;

import uk.ac.bbsrc.tgac.miso.AbstractHibernateQcDaoTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateLibraryAliquotQcDao;
import uk.ac.bbsrc.tgac.miso.core.data.qc.LibraryAliquotQC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.LibraryAliquotQcControlRun;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcControlRun;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcTarget;

public class HibernateLibraryAliquotQcDaoIT extends AbstractHibernateQcDaoTest<LibraryAliquotQC, HibernateLibraryAliquotQcDao, LibraryAliquot, LibraryAliquotQcControlRun> {

    public HibernateLibraryAliquotQcDaoIT() {
        super(LibraryAliquotQC.class, LibraryAliquot.class, LibraryAliquotQcControlRun.class, QcTarget.LibraryAliquot, 1L, 2L, 2L, 5L, 14L, 1L);
    }

    @Override
    public HibernateLibraryAliquotQcDao constructTestSubject() {
        return new HibernateLibraryAliquotQcDao();
    }

    @Override
    protected LibraryAliquotQC makeQc(LibraryAliquot entity) {
        LibraryAliquotQC qc = new LibraryAliquotQC();
        qc.setLibraryAliquot(entity);
        return qc;
    }

    @Override
    protected QcControlRun makeControlRun(LibraryAliquotQC qc) {
        LibraryAliquotQcControlRun controlRun = new LibraryAliquotQcControlRun();
        controlRun.setQc(qc);
        return controlRun;
    }

    @Override
    protected BigDecimal getConcentration(LibraryAliquot entity) {
        return entity.getConcentration();
    }

    @Override
    protected void setConcentration(LibraryAliquot entity, BigDecimal concentration) {
        entity.setConcentration(concentration);
    }

}