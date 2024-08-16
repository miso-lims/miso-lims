package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import uk.ac.bbsrc.tgac.miso.core.data.BarcodableVisitor;
import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentPosition;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.ServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.data.Workstation;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
@Table(name = "Instrument")
public class InstrumentImpl implements Instrument {

  private static final long serialVersionUID = 1L;

  private static final Long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "instrumentId")
  private long id = InstrumentImpl.UNSAVED_ID;

  @Column(nullable = false)
  private String name;

  @ManyToOne(targetEntity = InstrumentModel.class)
  @JoinColumn(name = "instrumentModelId", nullable = false)
  private InstrumentModel instrumentModel;

  private String serialNumber;
  private String identificationBarcode;

  @ManyToOne
  @JoinColumn(name = "workstationId")
  private Workstation workstation;

  private LocalDate dateCommissioned;
  private LocalDate dateDecommissioned = null;

  @OneToOne(targetEntity = InstrumentImpl.class, optional = true)
  @JoinColumn(name = "upgradedInstrumentId")
  private Instrument upgradedInstrument;

  @ManyToOne
  @JoinColumn(name = "defaultPurposeId")
  private RunPurpose defaultRunPurpose;

  @Transient
  private Date lastServicedDate;

  public InstrumentImpl(String name, InstrumentModel instrumentModel) {
    setName(name);
    setInstrumentModel(instrumentModel);
  }

  /**
   * Exists for Hibernate purposes
   * 
   * @throws IOException
   */
  public InstrumentImpl() {
    setInstrumentModel(null);
    setName(null);
  }

  @Override
  public void setId(long id) {
    this.id = id;
  }

  @Override
  public long getId() {
    return this.id;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public void setInstrumentModel(InstrumentModel instrumentModel) {
    this.instrumentModel = instrumentModel;
  }

  @Override
  public InstrumentModel getInstrumentModel() {
    return this.instrumentModel;
  }

  @Override
  public void setSerialNumber(String serialNumber) {
    this.serialNumber = serialNumber;
  }

  @Override
  public String getSerialNumber() {
    return serialNumber;
  }

  @Override
  public String getIdentificationBarcode() {
    return identificationBarcode;
  }

  @Override
  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = identificationBarcode;
  }

  @Override
  public Workstation getWorkstation() {
    return workstation;
  }

  @Override
  public void setWorkstation(Workstation workstation) {
    this.workstation = workstation;
  }

  @Override
  public void setDateCommissioned(LocalDate date) {
    this.dateCommissioned = date;
  }

  @Override
  public LocalDate getDateCommissioned() {
    return dateCommissioned;
  }

  @Override
  public void setDateDecommissioned(LocalDate date) {
    this.dateDecommissioned = date;
  }

  @Override
  public LocalDate getDateDecommissioned() {
    return dateDecommissioned;
  }

  @Override
  public void setUpgradedInstrument(Instrument instrument) {
    this.upgradedInstrument = instrument;
  }

  @Override
  public Instrument getUpgradedInstrument() {
    return upgradedInstrument;
  }

  @Override
  public RunPurpose getDefaultRunPurpose() {
    return defaultRunPurpose;
  }

  @Override
  public void setDefaultRunPurpose(RunPurpose defaultRunPurpose) {
    this.defaultRunPurpose = defaultRunPurpose;
  }

  @Override
  public String toString() {
    return "AbstractInstrument [id=" + id
        + ", name=" + name
        + ", instrumentModel=" + instrumentModel.getId()
        + ", serialNumber=" + serialNumber
        + ", dateCommissioned=" + dateCommissioned
        + ", dateDecommissioned=" + dateDecommissioned
        + ", upgradedInstrument=" + (upgradedInstrument == null ? null : upgradedInstrument.getId()) + "]";
  }

  @Override
  public boolean isActive() {
    return dateDecommissioned == null;
  }

  @OneToMany(targetEntity = Run.class, mappedBy = "sequencer")
  private Set<Run> runs = new HashSet<>();

  @Override
  public Set<Run> getRuns() {
    return runs;
  }

  @Override
  public void setRuns(Set<Run> runs) {
    this.runs = runs;
  }

  @OneToMany(targetEntity = ServiceRecord.class, cascade = CascadeType.REMOVE)
  @JoinTable(name = "Instrument_ServiceRecord", joinColumns = {@JoinColumn(name = "instrumentId")},
      inverseJoinColumns = {
          @JoinColumn(name = "recordId")
      })
  private Set<ServiceRecord> serviceRecords = new HashSet<>();

  @Override
  public Set<ServiceRecord> getServiceRecords() {
    return serviceRecords;
  }

  @Override
  public void setServiceRecords(Set<ServiceRecord> serviceRecords) {
    this.serviceRecords = serviceRecords;
  }

  @Override
  public boolean isOutOfService() {
    if (getServiceRecords() == null) {
      return false;
    }
    return getServiceRecords().stream()
        .anyMatch(sr -> sr.isOutOfService() && sr.getEndTime() == null && sr.getStartTime() != null
            && sr.getStartTime().before(new Date()) && sr.getPosition() == null);
  }

  @Override
  public Set<InstrumentPosition> getOutOfServicePositions() {
    if (getInstrumentModel() == null) {
      return Collections.emptySet();
    }
    if (isOutOfService()) {
      return getInstrumentModel().getPositions();
    }
    return getInstrumentModel().getPositions().stream()
        .filter(pos -> getServiceRecords().stream().anyMatch(sr -> sr.isOutOfService() && sr.getEndTime() == null
            && sr.getStartTime() != null && sr.getStartTime().before(new Date())
            && sr.getPosition().getAlias().equals(pos.getAlias())))
        .collect(Collectors.toSet());
  }

  @Override
  public String getOutOfServicePositionsLabel() {
    if (isOutOfService()) {
      return "All positions";
    }
    Set<InstrumentPosition> positions = getOutOfServicePositions();
    if (positions.isEmpty()) {
      return null;
    } else {
      return "Position" + (positions.size() == 1 ? "" : "a") + " "
          + positions.stream().map(InstrumentPosition::getAlias).collect(Collectors.joining(", "));
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(getDateCommissioned(),
        getDateDecommissioned(),
        getId(),
        getName(),
        getInstrumentModel(),
        getRuns(),
        getSerialNumber(),
        getServiceRecords(),
        getUpgradedInstrument(),
        getDefaultRunPurpose(),
        getIdentificationBarcode());
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equals(this, obj, InstrumentImpl::getDateCommissioned,
        InstrumentImpl::getDateDecommissioned,
        InstrumentImpl::getId,
        InstrumentImpl::getName,
        InstrumentImpl::getInstrumentModel,
        InstrumentImpl::getRuns,
        InstrumentImpl::getSerialNumber,
        InstrumentImpl::getServiceRecords,
        InstrumentImpl::getUpgradedInstrument,
        InstrumentImpl::getDefaultRunPurpose,
        InstrumentImpl::getIdentificationBarcode);
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public String getDeleteType() {
    return "Instrument";
  }

  @Override
  public String getDeleteDescription() {
    return getName() + " (" + getInstrumentModel().getAlias() + ")";
  }

  @Override
  public String getLabelText() {
    return getName();
  }

  @Override
  public LocalDate getBarcodeDate() {
    return getDateCommissioned();
  }

  @Override
  public <T> T visit(BarcodableVisitor<T> visitor) {
    return visitor.visitInstrument(this);
  }

  @Override
  public InstrumentPosition findPosition(long id) {
    return this.getInstrumentModel().getPositions().stream()
        .filter(p -> p.getId() == id)
        .findFirst().orElse(null);
  }

}
