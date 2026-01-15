package uk.ac.bbsrc.tgac.miso.core.data.qc;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;

@Entity
@Table(name = "LibraryAliquotQc")
public class LibraryAliquotQC extends QC{

    private static final long serialVersionUID = 1L;

    @ManyToOne(targetEntity = LibraryAliquot.class)
    @JoinColumn(name = "aliquotId")
    private LibraryAliquot libraryAliquot;

    @OneToMany(mappedBy = "qc", cascade = CascadeType.REMOVE)
    private List<LibraryAliquotQcControlRun> controls;

    public LibraryAliquot getLibraryAliquot() {
        return libraryAliquot;
    }

    public void setLibraryAliquot(LibraryAliquot libraryAliquot) {
        this.libraryAliquot = libraryAliquot;
    }

    @Override
    public QualityControllable<?> getEntity() {
        return libraryAliquot;
    }

    @Override
    public List<LibraryAliquotQcControlRun> getControls() {
        if (controls == null) {
            controls = new ArrayList<>();
        }
        return controls;
    }

}
