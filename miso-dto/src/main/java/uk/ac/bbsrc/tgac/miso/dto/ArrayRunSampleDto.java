package uk.ac.bbsrc.tgac.miso.dto;

public class ArrayRunSampleDto {

    // This ID is only for the sake of DataTables selection and doesn't relate to anything in the DB
    private Long id;
    private Long arrayRunId;
    private Long arrayId;
    private String position;
    private Long sampleId;
    private String sampleName;
    private String sampleAlias;
    private Long qcStatusId;
    private String qcNote;
    private String qcUserName;
    private String qcDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getArrayRunId() {
        return arrayRunId;
    }

    public void setArrayRunId(Long arrayRunId) {
        this.arrayRunId = arrayRunId;
    }

    public Long getArrayId() {
        return arrayId;
    }

    public void setArrayId(Long arrayId) {
        this.arrayId = arrayId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Long getSampleId() {
        return sampleId;
    }

    public void setSampleId(Long sampleId) {
        this.sampleId = sampleId;
    }

    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    public String getSampleAlias() {
        return sampleAlias;
    }

    public void setSampleAlias(String sampleAlias) {
        this.sampleAlias = sampleAlias;
    }

    public Long getQcStatusId() {
        return qcStatusId;
    }

    public void setQcStatusId(Long qcStatusId) {
        this.qcStatusId = qcStatusId;
    }

    public String getQcNote() {
        return qcNote;
    }

    public void setQcNote(String qcNote) {
        this.qcNote = qcNote;
    }

    public String getQcUserName() {
        return qcUserName;
    }

    public void setQcUserName(String qcUserName) {
        this.qcUserName = qcUserName;
    }

    public String getQcDate() {
        return qcDate;
    }

    public void setQcDate(String qcDate) {
        this.qcDate = qcDate;
    }

}
