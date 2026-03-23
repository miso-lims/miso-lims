package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import com.eaglegenomics.simlims.core.User;

import jakarta.persistence.*;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayRunSample.ArrayRunSampleId;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

@Entity
@Table(name = "ArrayRun_Sample")
@IdClass(ArrayRunSampleId.class)
public class ArrayRunSample implements Serializable {

    public static class ArrayRunSampleId implements Serializable {

        private static final long serialVersionUID = 1L;

        private ArrayRun arrayRun;

        private Array array;

        private String position;

        private Sample sample;

        public ArrayRunSampleId() {

        }

        public ArrayRunSampleId(ArrayRun arrayRun, Array array, String position, Sample sample) {
            this.arrayRun = arrayRun;
            this.array = array;
            this.position = position;
            this.sample = sample;
        }

        public ArrayRun getArrayRun() {
            return arrayRun;
        }

        public void setArrayRun(ArrayRun arrayRun) {
            this.arrayRun = arrayRun;
        }

        public Array getArray() {
            return array;
        }

        public void setArray(Array array) {
            this.array = array;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public Sample getSample() {
            return sample;
        }

        public void setSample(Sample sample) {
            this.sample = sample;
        }

        @Override
        public boolean equals(Object obj) {
            return LimsUtils.equals(this, obj,
                    ArrayRunSampleId::getArrayRun,
                    ArrayRunSampleId::getArray,
                    ArrayRunSampleId::getPosition,
                    ArrayRunSampleId::getSample);
        }

        @Override
        public int hashCode() {
            return Objects.hash(getArrayRun(), getArray(), getPosition(), getSample());
        }
    }

    private static  final long serialVersionUID = 1L;

    @Id
    @ManyToOne
    @JoinColumn(name = "arrayRunId")
    private ArrayRun arrayRun;

    @Id
    @ManyToOne
    @JoinColumn(name = "arrayId")
    private Array array;

    @Id
    @Column(name = "position")
    private String position;

    @Id
    @ManyToOne(targetEntity = SampleImpl.class)
    @JoinColumn(name = "sampleId")
    private Sample sample;

    @ManyToOne
    @JoinColumn(name = "statusId")
    private RunItemQcStatus qcStatus;

    private String qcNote;

    @ManyToOne(targetEntity = UserImpl.class)
    @JoinColumn(name = "qcUser")
    private User qcUser;

    private LocalDate qcDate;

    public ArrayRunSample() {

    }

    public ArrayRunSample(ArrayRun arrayRun, Array array, String position, Sample sample) {
        this.arrayRun = arrayRun;
        this.array = array;
        this.position = position;
        this.sample = sample;
    }

    public ArrayRun getArrayRun() {
        return arrayRun;
    }

    public void setArrayRun(ArrayRun arrayRun) {
        this.arrayRun = arrayRun;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Array getArray() {
        return array;
    }

    public void setArray(Array array) {
        this.array = array;
    }

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    public RunItemQcStatus getQcStatus() {
        return qcStatus;
    }

    public void setQcStatus(RunItemQcStatus qcStatus) {
        this.qcStatus = qcStatus;
    }

    public String getQcNote() {
        return qcNote;
    }

    public void setQcNote(String qcNote) {
        this.qcNote = qcNote;
    }

    public User getQcUser() {
        return qcUser;
    }

    public void setQcUser(User qcUser) {
        this.qcUser = qcUser;
    }

    public LocalDate getQcDate() {
        return qcDate;
    }

    public void setQcDate(LocalDate qcDate) {
        this.qcDate = qcDate;
    }
}
