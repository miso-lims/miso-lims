package uk.ac.bbsrc.tgac.miso.core.data.qc;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "LibraryAliquotQcControl")
public class LibraryAliquotQcControlRun  extends QcControlRun {

    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "qcId")
    private LibraryAliquotQC qc;

    @Override
    public LibraryAliquotQC getQc() {
        return qc;
    }

    public void setQc(LibraryAliquotQC qc) {
        this.qc = qc;
    }

}
