package uk.ac.bbsrc.tgac.miso.dto;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.commons.lang.NotImplementedException;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractBoxPosition;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractBoxable;
import uk.ac.bbsrc.tgac.miso.core.data.Array;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayModel;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayRun;
import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.BoxPosition;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.ConcentrationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment.RunPartition;
import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.data.IlluminaChemistry;
import uk.ac.bbsrc.tgac.miso.core.data.IlluminaRun;
import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.IndexFamily;
import uk.ac.bbsrc.tgac.miso.core.data.Institute;
import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentPosition;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentStatus;
import uk.ac.bbsrc.tgac.miso.core.data.Issue;
import uk.ac.bbsrc.tgac.miso.core.data.Kit;
import uk.ac.bbsrc.tgac.miso.core.data.KitImpl;
import uk.ac.bbsrc.tgac.miso.core.data.Lab;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.LibrarySpikeIn;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.PartitionQCType;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.PoolOrder;
import uk.ac.bbsrc.tgac.miso.core.data.PoolOrderCompletion;
import uk.ac.bbsrc.tgac.miso.core.data.PoolQC;
import uk.ac.bbsrc.tgac.miso.core.data.Printer;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.QC;
import uk.ac.bbsrc.tgac.miso.core.data.QcTarget;
import uk.ac.bbsrc.tgac.miso.core.data.ReferenceGenome;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquotSingleCell;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleGroupId;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleLCMTube;
import uk.ac.bbsrc.tgac.miso.core.data.SampleNumberPerProject;
import uk.ac.bbsrc.tgac.miso.core.data.SamplePurpose;
import uk.ac.bbsrc.tgac.miso.core.data.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.SampleSingleCell;
import uk.ac.bbsrc.tgac.miso.core.data.SampleSlide;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStockSingleCell;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;
import uk.ac.bbsrc.tgac.miso.core.data.SampleValidRelationship;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.ServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.data.Stain;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.StudyType;
import uk.ac.bbsrc.tgac.miso.core.data.Submission;
import uk.ac.bbsrc.tgac.miso.core.data.Subproject;
import uk.ac.bbsrc.tgac.miso.core.data.TissueMaterial;
import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;
import uk.ac.bbsrc.tgac.miso.core.data.TissueType;
import uk.ac.bbsrc.tgac.miso.core.data.VolumeUnit;
import uk.ac.bbsrc.tgac.miso.core.data.Workset;
import uk.ac.bbsrc.tgac.miso.core.data.impl.AttachmentCategory;
import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ContainerQC;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Deletion;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedLibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedLibraryTemplate;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedQcStatusImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedSampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.FileAttachment;
import uk.ac.bbsrc.tgac.miso.core.data.impl.InstituteImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.InstrumentImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LabImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryTemplate;
import uk.ac.bbsrc.tgac.miso.core.data.impl.OxfordNanoporeContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolOrderImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoreVersion;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleAliquotImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleAliquotSingleCellImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleClassImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleGroupImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIdentityImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleLCMTubeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleNumberPerProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SamplePurposeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleSingleCellImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleSlideImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleStockImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleStockSingleCellImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissueImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissueProcessingImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleValidRelationshipImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation.LocationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StudyImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SubprojectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueMaterialImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueOriginImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueTypeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.boxposition.DilutionBoxPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.boxposition.LibraryBoxPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.boxposition.PoolBoxPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.boxposition.SampleBoxPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BarcodableView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolableElementView;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.SampleSpreadSheets;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.SpreadSheetFormat;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.Spreadsheet;
import uk.ac.bbsrc.tgac.miso.core.data.type.ConsentLevel;
import uk.ac.bbsrc.tgac.miso.core.data.type.DilutionFactor;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.IlluminaWorkflowType;
import uk.ac.bbsrc.tgac.miso.core.data.type.InstrumentType;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow.WorkflowName;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowStepPrompt;
import uk.ac.bbsrc.tgac.miso.core.service.printing.Backend;
import uk.ac.bbsrc.tgac.miso.core.service.printing.Driver;
import uk.ac.bbsrc.tgac.miso.core.service.printing.Layout;
import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

import ca.on.oicr.gsi.runscanner.dto.IlluminaNotificationDto;
import ca.on.oicr.gsi.runscanner.dto.NotificationDto;

@SuppressWarnings("squid:S3776") // make Sonar ignore cognitive complexity warnings for this file
public class Dtos {

  public static TissueOriginDto asDto(@Nonnull TissueOrigin from) {
    TissueOriginDto dto = new TissueOriginDto();
    dto.setId(from.getId());
    dto.setAlias(from.getAlias());
    dto.setDescription(from.getDescription());
    dto.setLabel(from.getItemLabel());
    dto.setCreatedById(from.getCreatedBy().getUserId());
    dto.setCreationDate(formatDateTime(from.getCreationDate()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(formatDateTime(from.getLastUpdated()));
    return dto;
  }

  public static Set<TissueOriginDto> asTissueOriginDtos(@Nonnull Set<TissueOrigin> from) {
    return from.stream().map(Dtos::asDto).collect(Collectors.toSet());
  }

  public static TissueOrigin to(TissueOriginDto from) {
    TissueOrigin to = new TissueOriginImpl();
    if (from.getId() != null) to.setId(from.getId());
    setString(to::setAlias, from.getAlias());
    setString(to::setDescription, from.getDescription());
    return to;
  }

  public static TissueTypeDto asDto(@Nonnull TissueType from) {
    TissueTypeDto dto = new TissueTypeDto();
    dto.setId(from.getId());
    dto.setAlias(from.getAlias());
    dto.setDescription(from.getDescription());
    dto.setLabel(from.getItemLabel());
    dto.setCreatedById(from.getCreatedBy().getUserId());
    dto.setCreationDate(formatDateTime(from.getCreationDate()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(formatDateTime(from.getLastUpdated()));
    return dto;
  }

  public static Set<TissueTypeDto> asTissueTypeDtos(@Nonnull Set<TissueType> from) {
    return from.stream().map(Dtos::asDto).collect(Collectors.toSet());
  }

  public static TissueType to(@Nonnull TissueTypeDto from) {
    TissueType to = new TissueTypeImpl();
    if (from.getId() != null) to.setId(from.getId());
    setString(to::setAlias, from.getAlias());
    setString(to::setDescription, from.getDescription());
    return to;
  }

  public static SubprojectDto asDto(@Nonnull Subproject from) {
    SubprojectDto dto = new SubprojectDto();
    dto.setId(from.getId());
    dto.setAlias(from.getAlias());
    dto.setDescription(from.getDescription());
    dto.setPriority(from.getPriority());
    dto.setParentProjectId(from.getParentProject().getId());
    dto.setCreatedById(from.getCreatedBy().getUserId());
    dto.setCreationDate(formatDateTime(from.getCreationDate()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(formatDateTime(from.getLastUpdated()));
    dto.setReferenceGenomeId(from.getReferenceGenomeId());
    return dto;
  }

  public static Set<SubprojectDto> asSubprojectDtos(@Nonnull Collection<Subproject> from) {
    return from.stream().map(Dtos::asDto).collect(Collectors.toSet());
  }

  public static Subproject to(@Nonnull SubprojectDto from) {
    Subproject to = new SubprojectImpl();
    to.setAlias(from.getAlias());
    to.setDescription(from.getDescription());
    to.setPriority(from.getPriority());
    to.setReferenceGenomeId(from.getReferenceGenomeId());
    return to;
  }

  public static SampleClassDto asDto(@Nonnull SampleClass from) {
    SampleClassDto dto = new SampleClassDto();
    dto.setId(from.getId());
    dto.setAlias(from.getAlias());
    dto.setSampleCategory(from.getSampleCategory());
    dto.setSuffix(from.getSuffix());
    dto.setArchived(from.isArchived());
    dto.setDirectCreationAllowed(from.isDirectCreationAllowed());
    dto.setCreatedById(from.getCreatedBy().getUserId());
    dto.setCreationDate(formatDateTime(from.getCreationDate()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(formatDateTime(from.getLastUpdated()));
    dto.setDNAseTreatable(from.getDNAseTreatable());
    return dto;
  }

  public static Set<SampleClassDto> asSampleClassDtos(@Nonnull Set<SampleClass> from) {
    return from.stream().map(Dtos::asDto).collect(Collectors.toSet());
  }

  public static SampleClass to(@Nonnull SampleClassDto from) {
    SampleClass to = new SampleClassImpl();
    to.setAlias(from.getAlias());
    to.setSampleCategory(from.getSampleCategory());
    to.setSuffix(from.getSuffix());
    to.setArchived(from.isArchived());
    to.setDirectCreationAllowed(from.isDirectCreationAllowed());
    to.setDNAseTreatable(from.getDNAseTreatable());
    return to;
  }

  public static DetailedQcStatusDto asDto(@Nonnull DetailedQcStatus from) {
    DetailedQcStatusDto dto = new DetailedQcStatusDto();
    dto.setId(from.getId());
    dto.setStatus(from.getStatus());
    dto.setDescription(from.getDescription());
    dto.setNoteRequired(from.getNoteRequired());
    dto.setCreatedById(from.getCreatedBy().getUserId());
    dto.setCreationDate(formatDateTime(from.getCreationDate()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(formatDateTime(from.getLastUpdated()));
    return dto;
  }

  public static Set<DetailedQcStatusDto> asDetailedQcStatusDtos(@Nonnull Set<DetailedQcStatus> from) {
    return from.stream().map(Dtos::asDto).collect(Collectors.toSet());
  }

  public static DetailedQcStatus to(@Nonnull DetailedQcStatusDto from) {
    DetailedQcStatus to = new DetailedQcStatusImpl();
    to.setStatus(from.getStatus());
    to.setDescription(from.getDescription());
    to.setNoteRequired(from.isNoteRequired());
    return to;
  }

  public static SampleDto asMinimalDto(@Nonnull Sample from) {
    DetailedSampleDto dto = new DetailedSampleDto();
    copySampleFields(from, dto, false);

    if (isDetailedSample(from)) {
      DetailedSample detailed = (DetailedSample) from;
      dto.setSampleClassId(detailed.getSampleClass().getId());
      dto.setCreationDate(detailed.getCreationDate() == null ? "" : formatDate(detailed.getCreationDate()));
      dto.setIdentityConsentLevel(getIdentityConsentLevelString(detailed));
      if (detailed.getSubproject() != null) {
        dto.setSubprojectAlias(detailed.getSubproject().getAlias());
        dto.setSubprojectPriority(detailed.getSubproject().getPriority());
      }
    }
    return dto;
  }

  public static SampleDto asDto(@Nonnull Sample from, boolean includeBoxPositions) {
    SampleDto dto = null;

    if (isDetailedSample(from)) {
      dto = asDetailedSampleDto((DetailedSample) from);
    } else {
      dto = new SampleDto();
    }
    copySampleFields(from, dto, includeBoxPositions);
    dto.setAccession(from.getAccession());

    if (from.getQCs() != null && !from.getQCs().isEmpty()) {
      dto.setQcs(asQcDtos(from.getQCs()));
    }
    return dto;
  }

  public static List<SampleDto> asSampleDtos(@Nonnull Collection<Sample> from, boolean fullIncludingBoxPositions) {
    return from.stream()
        .map(sample -> (fullIncludingBoxPositions ? asDto(sample, true) : asMinimalDto(sample)))
        .collect(Collectors.toList());
  }

  private static SampleDto copySampleFields(@Nonnull Sample from, @Nonnull SampleDto dto, boolean includeBoxPositions) {
    dto.setId(from.getId());
    dto.setName(from.getName());
    dto.setDescription(from.getDescription());
    dto.setUpdatedById(from.getLastModifier().getUserId());
    dto.setIdentificationBarcode(from.getIdentificationBarcode());
    dto.setLocationBarcode(from.getLocationBarcode());
    dto.setLocationLabel(BoxUtils.makeLocationLabel(from));
    if (from.getBox() != null) {
      dto.setBox(asDto(from.getBox(), includeBoxPositions));
      dto.setBoxPosition(from.getBoxPosition());
    }
    dto.setSampleType(from.getSampleType());
    dto.setReceivedDate(from.getReceivedDate() == null ? null : formatDate(from.getReceivedDate()));
    if (from.getQcPassed() != null) {
      dto.setQcPassed(from.getQcPassed());
    }
    dto.setAlias(from.getAlias());
    dto.setProjectId(from.getProject().getId());
    dto.setScientificName(from.getScientificName());
    dto.setTaxonIdentifier(from.getTaxonIdentifier());
    if (from.getVolume() != null) {
      dto.setVolume(from.getVolume().toString());
    }
    dto.setVolumeUnits(from.getVolumeUnits());
    if (from.getConcentration() != null) {
      dto.setConcentration(from.getConcentration().toString());
    }
    dto.setConcentrationUnits(from.getConcentrationUnits());
    dto.setDiscarded(from.isDiscarded());
    dto.setLastModified(formatDateTime(from.getLastModified()));

    dto.setDistributed(from.isDistributed());
    dto.setDistributionDate(formatDate(from.getDistributionDate()));
    dto.setDistributionRecipient(from.getDistributionRecipient());

    return dto;

  }

  private static DetailedSampleDto asDetailedSampleDto(@Nonnull DetailedSample from) {
    DetailedSampleDto dto = null;
    if (isIdentitySample(from)) {
      dto = asIdentitySampleDto((SampleIdentity) from);
    } else if (isTissueSample(from)) {
      dto = asTissueSampleDto((SampleTissue) from);
    } else if (isTissueProcessingSample(from)) {
      dto = asTissueProcessingSampleDto((SampleTissueProcessing) from);
    } else if (isAliquotSample(from)) {
      dto = asAliquotSampleDto((SampleAliquot) from);
    } else if (isStockSample(from)) {
      dto = asStockSampleDto((SampleStock) from);
    } else {
      throw new IllegalArgumentException();
    }
    dto.setSampleClassId(from.getSampleClass().getId());
    if (from.getDetailedQcStatus() != null) {
      dto.setDetailedQcStatusId(from.getDetailedQcStatus().getId());
    }
    if (from.getSubproject() != null) {
      dto.setSubprojectId(from.getSubproject().getId());
      dto.setSubprojectAlias(from.getSubproject().getAlias());
      dto.setSubprojectPriority(from.getSubproject().getPriority());
    }
    if (from.getParent() != null) {
      dto.setParentId(from.getParent().getId());
      dto.setParentAlias(from.getParent().getAlias());
      dto.setParentTissueSampleClassId(from.getParent().getSampleClass().getId());
      dto.setIdentityConsentLevel(getIdentityConsentLevelString(from));
    }
    Optional<DetailedSample> effective = from.getEffectiveGroupIdSample();
    if (effective.isPresent()) {
      dto.setEffectiveGroupId(effective.get().getGroupId());
      dto.setEffectiveGroupIdSample(effective.get().getAlias());
    }
    if (from.getGroupId() != null) {
      dto.setGroupId(from.getGroupId());
    }
    if (from.getGroupDescription() != null) {
      dto.setGroupDescription(from.getGroupDescription());
    }
    if (from.isSynthetic() != null) {
      dto.setSynthetic(from.isSynthetic());
    }
    dto.setCreationDate(from.getCreationDate() == null ? "" : formatDate(from.getCreationDate()));
    dto.setNonStandardAlias(from.hasNonStandardAlias());
    if (from.getDetailedQcStatus() != null) {
      dto.setDetailedQcStatusId(from.getDetailedQcStatus().getId());
    }
    dto.setDetailedQcStatusNote(from.getDetailedQcStatusNote());
    return dto;
  }

  private static DetailedSample toDetailedSample(@Nonnull DetailedSampleDto from) {
    DetailedSample to = null;
    if (from.getClass() == SampleIdentityDto.class) {
      to = toIdentitySample((SampleIdentityDto) from);
    } else if (from.getClass() == SampleTissueDto.class) {
      to = toTissueSample((SampleTissueDto) from);
    } else if (from instanceof SampleTissueProcessingDto) {
      to = toTissueProcessingSample((SampleTissueProcessingDto) from);
    } else if (from instanceof SampleAliquotDto) {
      to = toAliquotSample((SampleAliquotDto) from);
    } else if (from instanceof SampleStockDto) {
      to = toStockSample((SampleStockDto) from);
    } else {
      to = new DetailedSampleImpl();
    }
    if (from.getDetailedQcStatusId() != null) {
      DetailedQcStatus detailedQcStatus = new DetailedQcStatusImpl();
      detailedQcStatus.setId(from.getDetailedQcStatusId());
      to.setDetailedQcStatus(detailedQcStatus);
    }
    to.setDetailedQcStatusNote(from.getDetailedQcStatusNote());
    if (from.getSubprojectId() != null) {
      Subproject subproject = new SubprojectImpl();
      subproject.setId(from.getSubprojectId());
      to.setSubproject(subproject);
    }
    if (from.getSampleClassId() != null) {
      SampleClass sampleClass = new SampleClassImpl();
      sampleClass.setId(from.getSampleClassId());
      to.setSampleClass(sampleClass);
    }
    to.setGroupId(nullifyStringIfBlank(from.getGroupId()));
    to.setGroupDescription(nullifyStringIfBlank(from.getGroupDescription()));
    to.setSynthetic(from.getSynthetic());
    if (from.getSubprojectId() != null) {
      Subproject subproject = new SubprojectImpl();
      subproject.setId(from.getSubprojectId());
      to.setSubproject(subproject);
    }
    to.setCreationDate(LimsUtils.isStringEmptyOrNull(from.getCreationDate()) ? null : parseDate(from.getCreationDate()));
    if (from.getIdentityId() != null) {
      to.setIdentityId(from.getIdentityId());
    }
    to.setNonStandardAlias(from.getNonStandardAlias());
    to.setParent(getParent(from));
    if (!LimsUtils.isStringEmptyOrNull(from.getExternalNames()) && to.getParent() != null) {
      SampleIdentity identity = LimsUtils.getParent(SampleIdentity.class, to);
      if (identity == null) {
        throw new IllegalStateException("Missing Identity at root of hierarchy");
      }
      identity.setExternalName(from.getExternalNames());
    }
    return to;
  }

  /**
   * Extracts parent details from the DTO, according to these possible cases:
   *
   * <ol>
   * <li>parent ID is provided. This implies that the parent exists, so no other parent information will be required</li>
   * <li>identity information and parentTissueSampleClassId are provided. This implies that a tissue parent should be created, and that the
   * identity may or may not yet exist. If the sampleClassId is an aliquot, a stockClassId must be provided. ParentAliquotClassId may be
   * provided to indicate a second aliquot level in the hierarchy</li>
   * <li>identity information is provided, but no parentTissueSampleClassId. You must be creating a tissue in this case.</li>
   * </ol>
   *
   * @param childDto
   *          the DTO to take parent details from
   * @return the parent details from the DTO, or null if there are none. A returned sample will also include its own parent if applicable.
   */
  private static DetailedSample getParent(@Nonnull DetailedSampleDto childDto) {
    DetailedSample parent = null;
    if (childDto.getParentId() != null) {
      parent = new DetailedSampleImpl();
      parent.setId(childDto.getParentId());
    } else {
      if (childDto instanceof SampleIdentityDto && childDto.getClass() != SampleIdentityDto.class) {
        parent = toIdentitySample((SampleIdentityDto) childDto);
      }

      if (childDto instanceof SampleTissueDto && childDto.getClass() != SampleTissueDto.class) {
        if (childDto.getParentTissueSampleClassId() == null) {
          throw new IllegalArgumentException("No tissue class specified.");
        }
        DetailedSample tissue = toTissueSample((SampleTissueDto) childDto);
        tissue.setSampleClass(new SampleClassImpl());
        tissue.getSampleClass().setId(childDto.getParentTissueSampleClassId());
        tissue.setParent(parent);
        parent = tissue;

        if (childDto instanceof SampleLCMTubeDto) {
          SampleLCMTubeDto lcm = (SampleLCMTubeDto) childDto;
          if (lcm.getParentSlideClassId() != null) {
            SampleSlide slide = new SampleSlideImpl();
            slide.setSampleClass(new SampleClassImpl());
            slide.getSampleClass().setId(lcm.getParentSlideClassId());
            slide.setSlides(0);
            slide.setParent(parent);
            parent = slide;
          }
        }
      }
      if (childDto instanceof SampleSingleCellRelative && childDto.getClass() != SampleSingleCellDto.class) {
        SampleStockDto stockDto = (SampleStockDto) childDto;
        DetailedSample tissueProcessing = toSingleCellSample((SampleSingleCellRelative) childDto);
        tissueProcessing.setSampleClass(new SampleClassImpl());
        tissueProcessing.getSampleClass().setId(stockDto.getTissueProcessingClassId());
        tissueProcessing.setParent(parent);
        parent = tissueProcessing;
      }
      if (childDto instanceof SampleStockDto && childDto.getClass() != SampleStockDto.class
          && childDto.getClass() != SampleStockSingleCellDto.class) {
        SampleAliquotDto aliquotDto = (SampleAliquotDto) childDto;
        DetailedSample stock = toStockSample((SampleStockDto) childDto);
        stock.setSampleClass(new SampleClassImpl());
        stock.getSampleClass().setId(aliquotDto.getStockClassId());
        stock.setParent(parent);
        parent = stock;

        if (aliquotDto.getParentAliquotClassId() != null) {
          DetailedSample parentAliquot = toAliquotSample(aliquotDto);
          parentAliquot.setSampleClass(new SampleClassImpl());
          parentAliquot.getSampleClass().setId(aliquotDto.getParentAliquotClassId());
          parentAliquot.setParent(parent);
          parent = parentAliquot;
        }
      }
    }
    return parent;
  }

  public static TissueMaterialDto asDto(@Nonnull TissueMaterial from) {
    TissueMaterialDto dto = new TissueMaterialDto();
    dto.setId(from.getId());
    dto.setAlias(from.getAlias());
    dto.setCreatedById(from.getCreatedBy().getUserId());
    dto.setCreationDate(formatDateTime(from.getCreationDate()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(formatDateTime(from.getLastUpdated()));
    return dto;
  }

  public static Set<TissueMaterialDto> asTissueMaterialDtos(@Nonnull Set<TissueMaterial> from) {
    return from.stream().map(Dtos::asDto).collect(Collectors.toSet());
  }

  public static TissueMaterial to(@Nonnull TissueMaterialDto from) {
    TissueMaterial to = new TissueMaterialImpl();
    to.setAlias(from.getAlias());
    return to;
  }

  public static SamplePurposeDto asDto(@Nonnull SamplePurpose from) {
    SamplePurposeDto dto = new SamplePurposeDto();
    dto.setId(from.getId());
    dto.setAlias(from.getAlias());
    dto.setCreatedById(from.getCreatedBy().getUserId());
    dto.setCreationDate(formatDateTime(from.getCreationDate()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(formatDateTime(from.getLastUpdated()));
    dto.setArchived(from.isArchived());
    return dto;
  }

  public static Set<SamplePurposeDto> asSamplePurposeDtos(@Nonnull Set<SamplePurpose> from) {
    return from.stream().map(Dtos::asDto).collect(Collectors.toSet());
  }

  public static SamplePurpose to(@Nonnull SamplePurposeDto from) {
    SamplePurpose to = new SamplePurposeImpl();
    to.setAlias(from.getAlias());
    return to;
  }

  public static SampleGroupDto asDto(@Nonnull SampleGroupId from) {
    SampleGroupDto dto = new SampleGroupDto();
    dto.setId(from.getId());
    dto.setGroupId(from.getGroupId());
    dto.setProjectId(from.getProject().getId());
    dto.setSubprojectId(from.getSubproject() == null ? null : from.getSubproject().getId());
    dto.setDescription(from.getDescription());
    dto.setCreatedById(from.getCreatedBy().getUserId());
    dto.setCreationDate(formatDateTime(from.getCreationDate()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(formatDateTime(from.getLastUpdated()));
    return dto;
  }

  public static Set<SampleGroupDto> asSampleGroupDtos(@Nonnull Set<SampleGroupId> from) {
    return from.stream().map(Dtos::asDto).collect(Collectors.toSet());
  }

  public static SampleGroupId to(@Nonnull SampleGroupDto from) {
    SampleGroupId to = new SampleGroupImpl();
    to.setGroupId(from.getGroupId());
    to.setDescription(from.getDescription());
    return to;
  }

  private static SampleAliquotDto asAliquotSampleDto(@Nonnull SampleAliquot from) {
    SampleAliquotDto dto = null;
    if (isAliquotSingleCellSample(from)) {
      SampleAliquotSingleCell scFrom = (SampleAliquotSingleCell) from;
      SampleAliquotSingleCellDto sc = new SampleAliquotSingleCellDto();
      setString(sc::setInputIntoLibrary, scFrom.getInputIntoLibrary());
      dto = sc;
    } else {
      dto = new SampleAliquotDto();
    }
    if (from.getSamplePurpose() != null) {
      dto.setSamplePurposeId(from.getSamplePurpose().getId());
    }
    return dto;
  }

  private static SampleStockDto asStockSampleDto(@Nonnull SampleStock from) {
    SampleStockDto dto = null;
    if (isStockSingleCellSample(from)) {
      SampleStockSingleCell scFrom = (SampleStockSingleCell) from;
      SampleStockSingleCellDto sc = new SampleStockSingleCellDto();
      setString(sc::setTargetCellRecovery, scFrom.getTargetCellRecovery());
      setString(sc::setCellViability, scFrom.getCellViability());
      setString(sc::setLoadingCellConcentration, scFrom.getLoadingCellConcentration());
      dto = sc;
    } else {
      dto = new SampleStockDto();
    }
    dto.setStrStatus(from.getStrStatus().getLabel());
    dto.setDnaseTreated(from.getDNAseTreated());
    return dto;
  }

  private static SampleStock toStockSample(@Nonnull SampleStockDto from) {
    SampleStock to = null;
    if (from instanceof SampleStockSingleCellRelative) {
      SampleStockSingleCellRelative scFrom = (SampleStockSingleCellRelative) from;
      SampleStockSingleCell sc = new SampleStockSingleCellImpl();
      setBigDecimal(sc::setTargetCellRecovery, scFrom.getTargetCellRecovery());
      setBigDecimal(sc::setCellViability, scFrom.getCellViability());
      setBigDecimal(sc::setLoadingCellConcentration, scFrom.getLoadingCellConcentration());
      to = sc;
    } else {
      to = new SampleStockImpl();
    }
    if (from.getStrStatus() != null) {
      to.setStrStatus(from.getStrStatus());
    }
    to.setDNAseTreated(from.getDnaseTreated());
    return to;
  }

  private static SampleAliquot toAliquotSample(@Nonnull SampleAliquotDto from) {
    SampleAliquot to = null;
    if (from.getClass() == SampleAliquotSingleCellDto.class) {
      SampleAliquotSingleCellDto scFrom = (SampleAliquotSingleCellDto) from;
      SampleAliquotSingleCell sc = new SampleAliquotSingleCellImpl();
      setBigDecimal(sc::setInputIntoLibrary, scFrom.getInputIntoLibrary());
      to = sc;
    } else {
      to = new SampleAliquotImpl();
    }
    if (from.getSamplePurposeId() != null) {
      to.setSamplePurpose(new SamplePurposeImpl());
      to.getSamplePurpose().setId(from.getSamplePurposeId());
    }
    return to;
  }

  public static Sample to(@Nonnull SampleDto from) {
    Sample to = null;
    if (from instanceof DetailedSampleDto) {
      to = toDetailedSample((DetailedSampleDto) from);
    } else {
      to = new SampleImpl();
    }

    if (from.getId() != null) to.setId(from.getId());
    to.setAccession(nullifyStringIfBlank(from.getAccession()));
    to.setName(from.getName());
    to.setDescription(nullifyStringIfBlank(from.getDescription()));
    to.setIdentificationBarcode(nullifyStringIfBlank(from.getIdentificationBarcode()));
    to.setLocationBarcode(nullifyStringIfBlank(from.getLocationBarcode()));
    to.setSampleType(from.getSampleType());
    to.setReceivedDate(parseDate(from.getReceivedDate()));
    to.setQcPassed(from.getQcPassed());
    to.setScientificName(from.getScientificName());
    to.setTaxonIdentifier(from.getTaxonIdentifier());
    to.setAlias(from.getAlias());
    to.setDescription(from.getDescription());
    to.setVolume(isStringEmptyOrNull(from.getVolume()) ? null : Double.valueOf(from.getVolume()));
    to.setVolumeUnits(from.getVolumeUnits());
    to.setConcentration(isStringEmptyOrNull(from.getConcentration()) ? null : Double.valueOf(from.getConcentration()));
    to.setConcentrationUnits(from.getConcentrationUnits());
    to.setDiscarded(from.isDiscarded());
    if (from.getProjectId() != null) {
      to.setProject(new ProjectImpl());
      to.getProject().setId(from.getProjectId());
    }
    to.setBoxPosition((SampleBoxPosition) makeBoxablePosition(from, (SampleImpl) to));

    to.setDistributed(from.isDistributed());
    to.setDistributionDate(parseDate(from.getDistributionDate()));
    to.setDistributionRecipient(from.getDistributionRecipient());
    return to;
  }

  private static <T extends AbstractBoxableDto, U extends AbstractBoxable> AbstractBoxPosition makeBoxablePosition(@Nonnull T from,
      @Nonnull U to) {
    if (from.getBox() != null && (from.getBox().getId() != null || !isStringEmptyOrNull(from.getBoxPosition()))) {
      AbstractBoxPosition bp = to.getEntityType().makeBoxPosition();
      bp.setBox(to(from.getBox()));
      bp.setPosition(from.getBoxPosition());
      return bp;
    }
    return null;
  }

  private static SampleIdentityDto asIdentitySampleDto(@Nonnull SampleIdentity from) {
    SampleIdentityDto dto = new SampleIdentityDto();
    dto.setExternalName(from.getExternalName());
    dto.setDonorSex(from.getDonorSex().getLabel());
    if (from.getConsentLevel() != null) {
      dto.setConsentLevel(from.getConsentLevel().getLabel());
      // set here too, so it can be checked consistently for all DetailedSampleDtos
      dto.setIdentityConsentLevel(from.getConsentLevel().getLabel());
    }
    return dto;
  }

  private static SampleIdentity toIdentitySample(@Nonnull SampleIdentityDto from) {
    SampleIdentity to = new SampleIdentityImpl();
    to.setExternalName(from.getExternalName());
    if (from.getDonorSex() != null) {
      to.setDonorSex(from.getDonorSex());
    }
    if (from.getConsentLevel() != null) {
      to.setConsentLevel(ConsentLevel.getByLabel(from.getConsentLevel()));
    }
    return to;
  }

  public static SampleNumberPerProjectDto asDto(@Nonnull SampleNumberPerProject from) {
    SampleNumberPerProjectDto dto = new SampleNumberPerProjectDto();
    dto.setId(from.getId());
    dto.setProjectId(from.getProject().getId());
    dto.setHighestSampleNumber(from.getHighestSampleNumber());
    dto.setPadding(from.getPadding());
    dto.setCreatedById(from.getCreatedBy().getUserId());
    dto.setCreationDate(formatDateTime(from.getCreationDate()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(formatDateTime(from.getLastUpdated()));
    return dto;
  }

  public static Set<SampleNumberPerProjectDto> asSampleNumberPerProjectDtos(@Nonnull Set<SampleNumberPerProject> from) {
    return from.stream().map(Dtos::asDto).collect(Collectors.toSet());
  }

  public static SampleNumberPerProject to(@Nonnull SampleNumberPerProjectDto from) {
    SampleNumberPerProject to = new SampleNumberPerProjectImpl();
    to.setHighestSampleNumber(from.getHighestSampleNumber());
    to.setPadding(from.getPadding());
    return to;
  }

  public static SampleValidRelationshipDto asDto(@Nonnull SampleValidRelationship from) {
    SampleValidRelationshipDto dto = new SampleValidRelationshipDto();
    dto.setId(from.getId());
    dto.setParentId(from.getParent().getId());
    dto.setChildId(from.getChild().getId());
    dto.setCreatedById(from.getCreatedBy().getUserId());
    dto.setCreationDate(formatDateTime(from.getCreationDate()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(formatDateTime(from.getLastUpdated()));
    dto.setArchived(from.getArchived());
    return dto;
  }

  public static Set<SampleValidRelationshipDto> asSampleValidRelationshipDtos(@Nonnull Set<SampleValidRelationship> from) {
    return from.stream().map(Dtos::asDto).collect(Collectors.toSet());
  }

  public static SampleValidRelationship to(@Nonnull SampleValidRelationshipDto from) {
    SampleValidRelationship to = new SampleValidRelationshipImpl();
    return to;
  }

  public static InstituteDto asDto(@Nonnull Institute from) {
    InstituteDto dto = new InstituteDto();
    dto.setId(from.getId());
    dto.setAlias(from.getAlias());
    dto.setCreatedById(from.getCreatedBy().getUserId());
    dto.setCreationDate(formatDateTime(from.getCreationDate()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(formatDateTime(from.getLastUpdated()));
    return dto;
  }

  public static Set<InstituteDto> asInstituteDtos(@Nonnull Set<Institute> from) {
    return from.stream().map(Dtos::asDto).collect(Collectors.toSet());
  }

  public static Institute to(@Nonnull InstituteDto from) {
    Institute to = new InstituteImpl();
    to.setAlias(from.getAlias());
    return to;
  }

  public static LabDto asDto(@Nonnull Lab from) {
    LabDto dto = new LabDto();
    dto.setId(from.getId());
    dto.setInstituteId(from.getInstitute().getId());
    dto.setInstituteAlias(from.getInstitute().getAlias());
    dto.setAlias(from.getAlias());
    dto.setLabel(from.getItemLabel());
    dto.setCreatedById(from.getCreatedBy().getUserId());
    dto.setCreationDate(formatDateTime(from.getCreationDate()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(formatDateTime(from.getLastUpdated()));
    return dto;
  }

  public static Set<LabDto> asLabDtos(@Nonnull Collection<Lab> from) {
    return from.stream().map(Dtos::asDto).collect(Collectors.toSet());
  }

  public static Lab to(@Nonnull LabDto from) {
    Lab to = new LabImpl();
    to.setAlias(from.getAlias());
    return to;
  }

  private static SampleTissueDto asTissueSampleDto(@Nonnull SampleTissue from) {
    SampleTissueDto dto = new SampleTissueDto();
    dto.setPassageNumber(from.getPassageNumber());
    dto.setTimesReceived(from.getTimesReceived());
    dto.setSecondaryIdentifier(from.getSecondaryIdentifier());
    dto.setRegion(from.getRegion());
    dto.setTubeNumber(from.getTubeNumber());
    if (from.getLab() != null) {
      dto.setLabId(from.getLab().getId());
    }
    if (from.getTissueOrigin() != null) {
      dto.setTissueOriginId(from.getTissueOrigin().getId());
    }
    if (from.getTissueType() != null) {
      dto.setTissueTypeId(from.getTissueType().getId());
    }
    if (from.getTissueMaterial() != null) {
      dto.setTissueMaterialId(from.getTissueMaterial().getId());
    }
    return dto;
  }

  private static SampleTissue toTissueSample(@Nonnull SampleTissueDto from) {
    SampleTissue to = new SampleTissueImpl();
    to.setPassageNumber(from.getPassageNumber());
    to.setTimesReceived(from.getTimesReceived());
    to.setTubeNumber(from.getTubeNumber());
    to.setRegion(nullifyStringIfBlank(from.getRegion()));
    to.setSecondaryIdentifier(from.getSecondaryIdentifier());
    if (from.getTissueOriginId() != null) {
      TissueOrigin tissueOrigin = new TissueOriginImpl();
      tissueOrigin.setId(from.getTissueOriginId());
      to.setTissueOrigin(tissueOrigin);
    }
    if (from.getTissueTypeId() != null) {
      TissueType tissueType = new TissueTypeImpl();
      tissueType.setId(from.getTissueTypeId());
      to.setTissueType(tissueType);
    }
    if (from.getTissueMaterialId() != null) {
      TissueMaterial tissueMaterial = new TissueMaterialImpl();
      tissueMaterial.setId(from.getTissueMaterialId());
      to.setTissueMaterial(tissueMaterial);
    }
    if (from.getLabId() != null) {
      Lab lab = new LabImpl();
      lab.setId(from.getLabId());
      to.setLab(lab);
    }
    return to;
  }

  private static SampleTissueProcessingDto asTissueProcessingSampleDto(@Nonnull SampleTissueProcessing from) {
    from = deproxify(from);

    if (isSampleSlide(from)) {
      return asSlideSampleDto((SampleSlide) from);
    } else if (isLcmTubeSample(from)) {
      return asLCMTubeSampleDto((SampleLCMTube) from);
    } else if (isProcessingSingleCellSample(from)) {
      return asSingleCellSampleDto((SampleSingleCell) from);
    } else {
      return new SampleTissueProcessingDto();
    }
  }

  private static SampleTissueProcessing toTissueProcessingSample(@Nonnull SampleTissueProcessingDto from) {
    SampleTissueProcessing to = null;
    if (from.getClass() == SampleSlideDto.class) {
      to = toSlideSample((SampleSlideDto) from);
    } else if (from.getClass() == SampleLCMTubeDto.class) {
      to = toLCMTubeSample((SampleLCMTubeDto) from);
    } else if (from instanceof SampleSingleCellRelative) {
      to = toSingleCellSample((SampleSingleCellRelative) from);
    } else {
      to = new SampleTissueProcessingImpl();
    }
    return to;
  }

  private static SampleSlideDto asSlideSampleDto(@Nonnull SampleSlide from) {
    SampleSlideDto dto = new SampleSlideDto();
    dto.setSlides(from.getSlides());
    dto.setDiscards(from.getDiscards());
    dto.setSlidesRemaining(from.getSlidesRemaining());
    dto.setThickness(from.getThickness());
    dto.setStain(from.getStain() == null ? null : asDto(from.getStain()));
    return dto;
  }

  private static SampleSlide toSlideSample(@Nonnull SampleSlideDto from) {
    SampleSlide to = new SampleSlideImpl();
    to.setSlides(from.getSlides());
    to.setDiscards(from.getDiscards());
    to.setThickness(from.getThickness());
    if (from.getStain() == null) {
      to.setStain(null);
    } else {
      Stain stain = new Stain();
      stain.setId(from.getStain().getId());
      to.setStain(stain);
    }
    return to;
  }

  private static SampleSingleCellDto asSingleCellSampleDto(@Nonnull SampleSingleCell from) {
    SampleSingleCellDto dto = new SampleSingleCellDto();
    setString(dto::setInitialCellConcentration, from.getInitialCellConcentration());
    setString(dto::setDigestion, from.getDigestion());
    return dto;
  }

  private static SampleLCMTubeDto asLCMTubeSampleDto(@Nonnull SampleLCMTube from) {
    SampleLCMTubeDto dto = new SampleLCMTubeDto();
    dto.setSlidesConsumed(from.getSlidesConsumed());
    return dto;
  }

  private static SampleSingleCell toSingleCellSample(@Nonnull SampleSingleCellRelative from) {
    SampleSingleCell to = new SampleSingleCellImpl();
    setBigDecimal(to::setInitialCellConcentration, from.getInitialCellConcentration());
    setString(to::setDigestion, from.getDigestion());
    return to;
  }

  private static SampleLCMTube toLCMTubeSample(@Nonnull SampleLCMTubeDto from) {
    SampleLCMTube to = new SampleLCMTubeImpl();
    to.setSlidesConsumed(from.getSlidesConsumed());
    return to;
  }

  public static KitDescriptorDto asDto(@Nonnull KitDescriptor from) {
    KitDescriptorDto dto = new KitDescriptorDto();
    dto.setId(from.getId());
    dto.setName(from.getName());
    dto.setManufacturer(from.getManufacturer());
    dto.setPartNumber(from.getPartNumber());
    dto.setVersion(from.getVersion());
    dto.setStockLevel(from.getStockLevel());
    dto.setKitType(from.getKitType().getKey());
    dto.setPlatformType(from.getPlatformType().getKey());
    return dto;
  }

  public static Set<KitDescriptorDto> asKitDescriptorDtos(@Nonnull Collection<KitDescriptor> from) {
    return from.stream().map(Dtos::asDto).collect(Collectors.toSet());
  }

  public static KitDescriptor to(@Nonnull KitDescriptorDto from) {
    KitDescriptor to = new KitDescriptor();
    if (from.getId() != null) to.setId(from.getId());
    to.setName(from.getName());
    to.setManufacturer(from.getManufacturer());
    to.setPartNumber(from.getPartNumber());
    to.setVersion(from.getVersion());
    to.setStockLevel(from.getStockLevel());
    to.setKitType(KitType.get(from.getKitType()));
    to.setPlatformType(PlatformType.get(from.getPlatformType()));
    return to;
  }

  public static LibraryDesignCodeDto asDto(@Nonnull LibraryDesignCode from) {
    LibraryDesignCodeDto dto = new LibraryDesignCodeDto();
    dto.setId(from.getId());
    dto.setCode(from.getCode());
    dto.setDescription(from.getDescription());
    dto.setTargetedSequencingRequired(from.isTargetedSequencingRequired());
    return dto;
  }

  public static LibraryDesignCode to(@Nonnull LibraryDesignCodeDto from) {
    LibraryDesignCode to = new LibraryDesignCode();
    if (from.getId() != null) to.setId(from.getId());
    to.setCode(from.getCode());
    to.setDescription(from.getDescription());
    to.setTargetedSequencingRequired(from.isTargetedSequencingRequired());
    return to;
  }

  private static DetailedLibraryDto asDetailedLibraryDto(@Nonnull DetailedLibrary from) {
    DetailedLibraryDto dto = new DetailedLibraryDto();
    if (from.getLibraryDesign() != null) {
      dto.setLibraryDesignId(from.getLibraryDesign().getId());
    }
    dto.setLibraryDesignCodeId(from.getLibraryDesignCode().getId());
    dto.setPreMigrationId(from.getPreMigrationId());
    dto.setArchived(from.getArchived());
    dto.setNonStandardAlias(from.hasNonStandardAlias());
    if (from.getGroupId() != null) {
      dto.setGroupId(from.getGroupId());
      dto.setEffectiveGroupId(from.getGroupId());
      dto.setEffectiveGroupIdSample(from.getAlias());
    } else {
      Optional<DetailedSample> effective = ((DetailedSample) from.getSample()).getEffectiveGroupIdSample();
      effective.ifPresent(upstream -> {
        dto.setEffectiveGroupId(upstream.getGroupId());
        dto.setEffectiveGroupIdSample(upstream.getAlias());
      });
    }
    if (from.getGroupDescription() != null) {
      dto.setGroupDescription(from.getGroupDescription());
    }
    if (from.getSample() != null) {
      dto.setIdentityConsentLevel(getIdentityConsentLevelString((DetailedSample) from.getSample()));
      DetailedSample detailed = (DetailedSample) from.getSample();
      if (detailed.getSubproject() != null) {
        dto.setSubprojectAlias(detailed.getSubproject().getAlias());
        dto.setSubprojectPriority(detailed.getSubproject().getPriority());
      }
    }
    return dto;
  }

  private static String getIdentityConsentLevelString(@Nonnull DetailedSample sample) {
    ConsentLevel level = getIdentityConsentLevel(sample);
    return level == null ? null : level.getLabel();
  }

  public static DetailedLibrary toDetailedLibrary(DetailedLibraryDto from) {
    if (from == null) return null;
    DetailedLibrary to = new DetailedLibraryImpl();
    if (from.getLibraryDesignId() != null) {
      LibraryDesign design = new LibraryDesign();
      design.setId(from.getLibraryDesignId());
      to.setLibraryDesign(design);
    }
    LibraryDesignCode ldCode = new LibraryDesignCode();
    ldCode.setId(from.getLibraryDesignCodeId());
    to.setLibraryDesignCode(ldCode);

    if (from.getArchived() != null) to.setArchived(from.getArchived());
    to.setNonStandardAlias(from.getNonStandardAlias());
    if (from.getGroupId() != null) {
      to.setGroupId(from.getGroupId());
    }
    if (from.getGroupDescription() != null) {
      to.setGroupDescription(from.getGroupDescription());
    }
    return to;
  }

  public static PoolOrderDto asDto(@Nonnull PoolOrder from) {
    PoolOrderDto dto = new PoolOrderDto();
    dto.setId(from.getId());
    dto.setPool(asDto(from.getPool(), false, false));
    dto.setParameters(asDto(from.getSequencingParameter()));
    dto.setPartitions(from.getPartitions());
    dto.setCreatedById(from.getCreatedBy().getUserId());
    dto.setCreationDate(formatDateTime(from.getCreationDate()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(formatDateTime(from.getLastUpdated()));
    dto.setDescription(from.getDescription());
    return dto;
  }

  public static Set<PoolOrderDto> asPoolOrderDtos(@Nonnull Collection<PoolOrder> from) {
    return from.stream().map(Dtos::asDto).collect(Collectors.toSet());
  }

  public static PoolOrder to(@Nonnull PoolOrderDto from) {
    PoolOrder to = new PoolOrderImpl();
    if (from.getId() != null) to.setId(from.getId());
    to.setPool(to(from.getPool()));
    to.setSequencingParameters(to(from.getParameters()));
    to.setPartitions(from.getPartitions());
    to.setDescription(from.getDescription());
    return to;
  }

  public static SequencingParametersDto asDto(@Nonnull SequencingParameters from) {
    SequencingParametersDto dto = new SequencingParametersDto();
    dto.setId(from.getId());
    dto.setName(from.getName());
    dto.setInstrumentModel(asDto(from.getInstrumentModel()));
    return dto;
  }

  public static SequencingParameters to(@Nonnull SequencingParametersDto from) {
    SequencingParameters to = new SequencingParameters();
    to.setId(from.getId());
    to.setName(from.getName());
    if (from.getInstrumentModel() != null) {
      to.setInstrumentModel(to(from.getInstrumentModel()));
    }
    return to;
  }

  public static List<SequencingParametersDto> asSequencingParametersDtos(@Nonnull Collection<SequencingParameters> from) {
    List<SequencingParametersDto> dtoList = from.stream().map(Dtos::asDto).collect(Collectors.toList());
    Collections.sort(dtoList, new Comparator<SequencingParametersDto>() {

      @Override
      public int compare(SequencingParametersDto o1, SequencingParametersDto o2) {
        return o1.getName().compareTo(o2.getName());
      }
    });
    return dtoList;
  }

  public static LibraryDto asDto(@Nonnull Library from, boolean includeBoxPositions) {
    LibraryDto dto = null;
    if (isDetailedLibrary(from)) {
      dto = asDetailedLibraryDto((DetailedLibrary) from);
    } else {
      dto = new LibraryDto();
    }
    dto.setAlias(from.getAlias());
    dto.setName(from.getName());
    dto.setParentSampleId(from.getSample().getId());
    dto.setParentSampleAlias(from.getSample().getAlias());
    dto.setParentSampleProjectId(from.getSample().getProject().getId());
    if (from.getSample() instanceof DetailedSample) {
      dto.setParentSampleClassId(((DetailedSample) from.getSample()).getSampleClass().getId());
    }
    dto.setCreationDate(formatDate(from.getCreationDate()));
    dto.setDescription(from.getDescription());
    dto.setId(from.getId());
    if (from.getConcentration() != null) {
      dto.setConcentration(from.getConcentration().toString());
    }
    dto.setConcentrationUnits(from.getConcentrationUnits());
    if (from.getLibrarySelectionType() != null) {
      dto.setLibrarySelectionTypeId(from.getLibrarySelectionType().getId());
    }
    if (from.getLibraryStrategyType() != null) {
      dto.setLibraryStrategyTypeId(from.getLibraryStrategyType().getId());
    }
    if (from.getLibraryType() != null) {
      dto.setLibraryTypeId(from.getLibraryType().getId());
      dto.setLibraryTypeAlias(from.getLibraryType().getDescription());
    }
    dto.setQcPassed(from.getQcPassed());
    dto.setLowQuality(from.isLowQuality());
    dto.setPaired(from.getPaired());
    if (from.getPlatformType() != null) {
      dto.setPlatformType(from.getPlatformType().getKey());
    }
    dto.setLastModified(formatDateTime(from.getLastModified()));
    if (from.getKitDescriptor() != null) {
      dto.setKitDescriptorId(from.getKitDescriptor().getId());
    }
    if (!from.getIndices().isEmpty()) {
      dto.setIndexFamilyName(from.getIndices().get(0).getFamily().getName());
      for (Index index : from.getIndices()) {
        switch (index.getPosition()) {
        case 1:
          dto.setIndex1Id(index.getId());
          dto.setIndex1Label(index.getLabel());
          break;
        case 2:
          dto.setIndex2Id(index.getId());
          dto.setIndex2Label(index.getLabel());
          break;
        default:
          throw new IllegalArgumentException("Index at position " + index.getPosition());
        }
      }
    }
    if (from.getVolume() != null) {
      dto.setVolume(from.getVolume().toString());
    }
    dto.setVolumeUnits(from.getVolumeUnits());
    dto.setDnaSize(from.getDnaSize());
    dto.setIdentificationBarcode(from.getIdentificationBarcode());
    if (from.getQCs() != null && !from.getQCs().isEmpty()) {
      dto.setQcs(asQcDtos(from.getQCs()));
    }
    dto.setLocationBarcode(from.getLocationBarcode());
    dto.setLocationLabel(BoxUtils.makeLocationLabel(from));
    if (from.getBox() != null) {
      dto.setBox(asDto(from.getBox(), includeBoxPositions));
      dto.setBoxPosition(from.getBoxPosition());
    }
    dto.setDiscarded(from.isDiscarded());
    if (from.getSample().getBox() != null) {
      dto.setSampleBoxPositionLabel(BoxUtils.makeBoxPositionLabel(from.getSample().getBox().getAlias(), from.getSample().getBoxPosition()));
    }
    if (from.getReceivedDate() != null) {
      dto.setReceivedDate(formatDate(from.getReceivedDate()));
    }
    setId(dto::setSpikeInId, from.getSpikeIn());
    setString(dto::setSpikeInVolume, from.getSpikeInVolume());
    setString(dto::setSpikeInDilutionFactor, maybeGetProperty(from.getSpikeInDilutionFactor(), DilutionFactor::getLabel));
    dto.setDistributed(from.isDistributed());
    setString(dto::setDistributionDate, formatDate(from.getDistributionDate()));
    setString(dto::setDistributionRecipient, from.getDistributionRecipient());
    return dto;
  }

  public static Library to(@Nonnull LibraryDto from) {
    Library to = null;
    if (from instanceof DetailedLibraryDto) {
      to = toDetailedLibrary((DetailedLibraryDto) from);
    } else {
      to = new LibraryImpl();
    }
    if (from.getId() != null) to.setId(from.getId());

    to.setAlias(from.getAlias());
    to.setName(from.getName());
    to.setDescription(from.getDescription());
    to.setIdentificationBarcode(from.getIdentificationBarcode());
    to.setConcentration(from.getConcentration() == null ? null : Double.valueOf(from.getConcentration()));
    to.setConcentrationUnits(from.getConcentrationUnits());
    to.setLowQuality(from.getLowQuality());
    if (from.getPaired() != null) {
      to.setPaired(from.getPaired());
    }
    to.setPlatformType(PlatformType.get(from.getPlatformType()));
    if (from.getParentSampleId() != null) {
      to.setSample(new SampleImpl());
      to.getSample().setId(from.getParentSampleId());
    }
    if (from.getLibrarySelectionTypeId() != null) {
      LibrarySelectionType sel = new LibrarySelectionType();
      sel.setId(from.getLibrarySelectionTypeId());
      to.setLibrarySelectionType(sel);
    }
    if (from.getLibraryStrategyTypeId() != null) {
      LibraryStrategyType strat = new LibraryStrategyType();
      strat.setId(from.getLibraryStrategyTypeId());
      to.setLibraryStrategyType(strat);
    }
    if (from.getLibraryTypeId() != null) {
      LibraryType type = new LibraryType();
      type.setId(from.getLibraryTypeId());
      if (from.getLibraryTypeAlias() != null) type.setDescription(from.getLibraryTypeAlias());
      to.setLibraryType(type);
    }
    to.setQcPassed(from.getQcPassed());
    if (from.getIndex1Id() != null) {
      List<Index> indices = new ArrayList<>();
      Index tb1 = new Index();
      tb1.setId(from.getIndex1Id());
      indices.add(tb1);
      if (from.getIndex2Id() != null) {
        Index tb2 = new Index();
        tb2.setId(from.getIndex2Id());
        indices.add(tb2);
      }
      to.setIndices(indices);
    }
    if (from.getVolume() != null) {
      to.setVolume(Double.valueOf(from.getVolume()));
    }
    to.setVolumeUnits(from.getVolumeUnits());
    to.setDnaSize(from.getDnaSize());
    if (from.getKitDescriptorId() != null) {
      KitDescriptor kitDescriptor = new KitDescriptor();
      kitDescriptor.setId(from.getKitDescriptorId());
      to.setKitDescriptor(kitDescriptor);
    }
    to.setLocationBarcode(from.getLocationBarcode());
    to.setCreationDate(parseDate(from.getCreationDate()));
    if (from.getReceivedDate() != null) {
      to.setReceivedDate(parseDate(from.getReceivedDate()));
    }
    to.setBoxPosition((LibraryBoxPosition) makeBoxablePosition(from, (LibraryImpl) to));
    to.setDiscarded(from.isDiscarded());
    setObject(to::setSpikeIn, LibrarySpikeIn::new, from.getSpikeInId());
    setBigDecimal(to::setSpikeInVolume, from.getSpikeInVolume());
    setObject(to::setSpikeInDilutionFactor, from.getSpikeInDilutionFactor(), DilutionFactor::get);
    to.setDistributed(from.isDistributed());
    to.setDistributionDate(parseDate(from.getDistributionDate()));
    to.setDistributionRecipient(from.getDistributionRecipient());
    return to;
  }

  public static List<BoxDto> asBoxDtosWithPositions(@Nonnull Collection<Box> boxes) {
    return boxes.stream()
        .map(box -> asDto(box, true))
        .collect(Collectors.toList());
  }

  public static BoxDto asDto(@Nonnull Box from, boolean includePositions) {
    BoxDto dto = new BoxDto();
    dto.setId(from.getId());
    dto.setName(from.getName());
    dto.setAlias(from.getAlias());
    dto.setDescription(from.getDescription());
    dto.setIdentificationBarcode(from.getIdentificationBarcode());
    dto.setLocationBarcode(from.getLocationBarcode());
    if (from.getUse() != null) {
      dto.setUseId(from.getUse().getId());
      dto.setUseAlias(from.getUse().getAlias());
    }
    if (from.getSize() != null) {
      dto.setSizeId(from.getSize().getId());
      dto.setRows(from.getSize().getRows());
      dto.setCols(from.getSize().getColumns());
      dto.setScannable(from.getSize().getScannable());
    }
    if (includePositions) {
      dto.setItems(from.getBoxPositions().values().stream().map(Dtos::asDto).collect(Collectors.toList()));
    }
    if (from.getStorageLocation() != null) {
      dto.setStorageLocationId(from.getStorageLocation().getId());
      dto.setStorageLocationBarcode(from.getStorageLocation().getIdentificationBarcode());
      dto.setFreezerDisplayLocation(from.getStorageLocation().getFreezerDisplayLocation());
      dto.setStorageDisplayLocation(from.getStorageLocation().getFullDisplayLocation());
    }
    dto.setTubeCount(from.getTubeCount());
    return dto;
  }

  private static BoxableDto asDto(@Nonnull BoxPosition from) {
    BoxableDto dto = new BoxableDto();
    dto.setCoordinates(from.getPosition());
    dto.setEntityType(from.getBoxableId().getTargetType());
    dto.setId(from.getBoxableId().getTargetId());
    return dto;
  }

  public static BoxDto asDtoWithBoxables(@Nonnull Box from, @Nonnull Collection<BoxableView> boxables) {
    BoxDto dto = asDto(from, false);
    dto.setItems(boxables.stream().map(Dtos::asDto).collect(Collectors.toList()));
    return dto;
  }

  public static List<BoxableDto> asBoxableDtos(@Nonnull List<BoxableView> boxables) {
    return boxables.stream()
        .map(Dtos::asDto)
        .collect(Collectors.toList());
  }

  public static BoxableDto asDto(@Nonnull BoxableView from) {
    BoxableDto dto = new BoxableDto();
    dto.setId(from.getId().getTargetId());
    dto.setAlias(from.getAlias());
    dto.setBoxAlias(from.getBoxAlias());
    dto.setBoxPosition(BoxUtils.makeLocationLabel(from));
    dto.setCoordinates(from.getBoxPosition());
    dto.setDiscarded(from.isDiscarded());
    dto.setIdentificationBarcode(from.getIdentificationBarcode());
    dto.setName(from.getName());
    dto.setVolume(from.getVolume());
    dto.setEntityType(from.getId().getTargetType());
    dto.setSampleClassId(from.getSampleClassId());
    return dto;
  }

  public static Box to(@Nonnull BoxDto from) {
    Box to = new BoxImpl();
    if (from.getId() != null) to.setId(from.getId());
    to.setAlias(from.getAlias());
    to.setDescription(from.getDescription());
    to.setIdentificationBarcode(from.getIdentificationBarcode());
    to.setLocationBarcode(from.getLocationBarcode() == null ? "" : from.getLocationBarcode());
    if (from.getUseId() != null) {
      BoxUse use = new BoxUse();
      use.setId(from.getUseId());
      to.setUse(use);
    }
    if (from.getSizeId() != null) {
      BoxSize size = new BoxSize();
      size.setId(from.getSizeId());
      to.setSize(size);
    }
    if (from.getStorageLocationId() != null) {
      to.setStorageLocation(new StorageLocation());
      to.getStorageLocation().setId(from.getStorageLocationId());
    }
    if (!isStringEmptyOrNull(from.getStorageLocationBarcode())) {
      if (to.getStorageLocation() == null) {
        to.setStorageLocation(new StorageLocation());
      }
      to.getStorageLocation().setIdentificationBarcode(from.getStorageLocationBarcode());
    }
    return to;
  }

  private static DilutionDto asDto(@Nonnull LibraryDilution from, @Nonnull LibraryDto libraryDto, boolean includeBoxPositions) {
    DilutionDto dto = new DilutionDto();
    dto.setId(from.getId());
    dto.setName(from.getName());
    dto.setDilutionUserName(from.getCreator().getFullName());
    dto.setConcentration(from.getConcentration() == null ? null : from.getConcentration().toString());
    dto.setConcentrationUnits(from.getConcentrationUnits());
    dto.setVolume(from.getVolume() == null ? null : from.getVolume().toString());
    dto.setVolumeUnits(from.getVolumeUnits());
    dto.setNgUsed(from.getNgUsed() == null ? null : from.getNgUsed().toString());
    dto.setVolumeUsed(from.getVolumeUsed() == null ? null : from.getVolumeUsed().toString());
    if (from.getCreationDate() != null) {
      dto.setCreationDate(formatDate(from.getCreationDate()));
    }
    dto.setIdentificationBarcode(from.getIdentificationBarcode());
    dto.setLocationLabel(BoxUtils.makeLocationLabel(from));
    if (from.getTargetedSequencing() != null) {
      dto.setTargetedSequencingId(from.getTargetedSequencing().getId());
    }
    dto.setLibrary(libraryDto);
    if (from.getBox() != null) {
      dto.setBox(asDto(from.getBox(), includeBoxPositions));
      dto.setBoxPosition(from.getBoxPosition());
    }
    dto.setDiscarded(from.isDiscarded());
    return dto;
  }

  public static DilutionDto asDto(@Nonnull LibraryDilution from, boolean includeFullLibrary, boolean includeBoxPositions) {
    LibraryDto libDto = null;
    if (includeFullLibrary) {
      libDto = asDto(from.getLibrary(), false);
    } else {
      Library lib = from.getLibrary();
      libDto = new LibraryDto();
      libDto.setId(lib.getId());
      libDto.setName(lib.getName());
      libDto.setAlias(lib.getAlias());
      libDto.setIdentificationBarcode(lib.getIdentificationBarcode());
      if (lib.getPlatformType() != null) {
        libDto.setPlatformType(lib.getPlatformType().getKey());
      }
    }
    return asDto(from, libDto, includeBoxPositions);
  }

  public static DilutionDto asDto(@Nonnull PoolableElementView from) {
    DilutionDto dto = new DilutionDto();
    dto.setId(from.getDilutionId());
    dto.setName(from.getDilutionName());
    dto.setDilutionUserName(from.getCreatorName());
    dto.setConcentration(from.getDilutionConcentration() == null ? null : from.getDilutionConcentration().toString());
    dto.setConcentrationUnits(from.getDilutionConcentrationUnits());
    dto.setLastModified(formatDateTime(from.getLastModified()));
    dto.setCreationDate(formatDate(from.getCreated()));
    dto.setIdentificationBarcode(from.getDilutionBarcode());
    dto.setIndexIds(from.getIndices().stream().map(Index::getId).collect(Collectors.toList()));
    dto.setTargetedSequencingId(from.getTargetedSequencingId());
    dto.setVolume(from.getDilutionVolume() == null ? null : from.getDilutionVolume().toString());
    dto.setVolumeUnits(from.getDilutionVolumeUnits());
    dto.setNgUsed(from.getDilutionNgUsed() == null ? null : from.getDilutionNgUsed().toString());
    dto.setVolumeUsed(from.getDilutionVolumeUsed() == null ? null : from.getDilutionVolumeUsed().toString());

    LibraryDto ldto = new LibraryDto();
    ldto.setId(from.getLibraryId());
    ldto.setName(from.getLibraryName());
    ldto.setAlias(from.getLibraryAlias());
    ldto.setIdentificationBarcode(from.getLibraryBarcode());
    ldto.setLowQuality(from.isLibraryLowQuality());
    ldto.setParentSampleId(from.getSampleId());
    ldto.setParentSampleAlias(from.getSampleAlias());
    if (from.getPlatformType() != null) {
      ldto.setPlatformType(from.getPlatformType().getKey());
    }
    ldto.setQcPassed(from.getLibraryQcPassed());
    dto.setLibrary(ldto);

    Sample sample = from.getSample();
    if (isDetailedSample(sample)) {
      DetailedSample detailed = (DetailedSample) sample;
      dto.setIdentityConsentLevel(getIdentityConsentLevelString(detailed));
      if (detailed.getSubproject() != null) {
        dto.setSubprojectAlias(detailed.getSubproject().getAlias());
        dto.setSubprojectPriority(detailed.getSubproject().getPriority());
      }
    }
    return dto;
  }

  public static LibraryDilution to(@Nonnull DilutionDto from) {
    LibraryDilution to = new LibraryDilution();
    if (from.getId() != null) to.setId(from.getId());
    if (!isStringEmptyOrNull(from.getName())) {
      to.setName(from.getName());
    }
    to.setIdentificationBarcode(from.getIdentificationBarcode());
    to.setConcentration(from.getConcentration() == null ? null : Double.valueOf(from.getConcentration()));
    to.setConcentrationUnits(from.getConcentrationUnits());
    to.setNgUsed(from.getNgUsed() == null ? null : Double.valueOf(from.getNgUsed()));
    to.setVolume(from.getVolume() == null ? null : Double.valueOf(from.getVolume()));
    to.setVolumeUnits(from.getVolumeUnits());
    to.setVolumeUsed(from.getVolumeUsed() == null ? null : Double.valueOf(from.getVolumeUsed()));
    to.setLibrary(to(from.getLibrary()));
    to.setCreationDate(parseDate(from.getCreationDate()));
    if (from.getTargetedSequencingId() != null) {
      to.setTargetedSequencing(new TargetedSequencing());
      to.getTargetedSequencing().setId(from.getTargetedSequencingId());
    }
    to.setBoxPosition((DilutionBoxPosition) makeBoxablePosition(from, to));
    to.setDiscarded(from.isDiscarded());
    return to;
  }

  public static PoolDto asDto(@Nonnull Pool from, boolean includeContents, boolean includeBoxPositions) {
    PoolDto dto = new PoolDto();
    dto.setId(from.getId());
    dto.setName(from.getName());
    dto.setAlias(from.getAlias());
    dto.setDescription(from.getDescription());
    dto.setConcentration(from.getConcentration() == null ? null : from.getConcentration().toString());
    dto.setConcentrationUnits(from.getConcentrationUnits());
    dto.setQcPassed(from.getQcPassed());
    dto.setCreationDate(formatDate(from.getCreationDate()));
    if (from.getVolume() != null) {
      dto.setVolume(from.getVolume().toString());
    }
    dto.setVolumeUnits(from.getVolumeUnits());
    if (from.getPlatformType() != null) {
      dto.setPlatformType(from.getPlatformType().name());
    }
    dto.setLongestIndex(from.getLongestIndex());
    dto.setLastModified(formatDateTime(from.getLastModified()));
    dto.setDilutionCount(from.getPoolDilutions().size());
    from.getPoolDilutions().stream()//
        .map(PoolDilution::getPoolableElementView)//
        .map(PoolableElementView::getLibraryDnaSize)//
        .filter(Objects::nonNull)//
        .mapToDouble(Long::doubleValue)//
        .average()//
        .ifPresent(dto::setInsertSize);
    if (includeContents) {
      Set<DilutionDto> pooledElements = new HashSet<>();
      for (PoolDilution pd : from.getPoolDilutions()) {
        DilutionDto ldi = asDto(pd.getPoolableElementView());
        ldi.setProportion(pd.getProportion());
        pooledElements.add(ldi);
      }
      dto.setPooledElements(pooledElements);
      dto.setDuplicateIndicesSequences(from.getDuplicateIndicesSequences());
      dto.setDuplicateIndices(!dto.getDuplicateIndicesSequences().isEmpty());
      dto.setNearDuplicateIndicesSequences(from.getNearDuplicateIndicesSequences());
      dto.setNearDuplicateIndices(!dto.getNearDuplicateIndicesSequences().isEmpty());
    } else {
      dto.setPooledElements(Collections.emptySet());
      dto.setDuplicateIndices(!from.getDuplicateIndicesSequences().isEmpty());
      dto.setNearDuplicateIndices(!from.getNearDuplicateIndicesSequences().isEmpty());
    }
    dto.setHasEmptySequence(from.hasLibrariesWithoutIndex());
    dto.setIdentificationBarcode(from.getIdentificationBarcode());
    dto.setLocationLabel(BoxUtils.makeLocationLabel(from));
    if (from.getBox() != null) {
      dto.setBox(asDto(from.getBox(), includeBoxPositions));
      dto.setBoxPosition(from.getBoxPosition());
    }
    dto.setDiscarded(from.isDiscarded());
    dto.setHasLowQualityLibraries(from.getHasLowQualityMembers());
    dto.setPrioritySubprojectAliases(from.getPrioritySubprojectAliases());

    return dto;
  }

  public static RunDto asDto(@Nonnull Run from) {
    return asDto(from, false, false, false);
  }

  public static RunDto asDto(@Nonnull Run from, boolean includeContainers, boolean includeContainerPartitions,
      boolean includePoolContents) {
    RunDto dto = new RunDto();
    dto.setId(from.getId());
    dto.setName(from.getName());
    dto.setAlias(from.getAlias());
    if (from.getHealth() != null) {
      dto.setStatus(from.getHealth().getKey());
    } else {
      dto.setStatus("");
    }
    dto.setLastModified(formatDateTime(from.getLastModified()));
    if (from.getSequencer() != null) {
      dto.setPlatformType(from.getSequencer().getInstrumentModel().getPlatformType().getKey());
    } else {
      dto.setPlatformType("");
    }
    if (from.getStartDate() != null) {
      dto.setStartDate(formatDate(from.getStartDate()));
    } else {
      dto.setStartDate("");
    }
    if (from.getCompletionDate() != null) {
      dto.setEndDate(formatDate(from.getCompletionDate()));
    }
    if (from.getSequencingParameters() != null) {
      dto.setParameters(asDto(from.getSequencingParameters()));
    } else {
      SequencingParametersDto parametersDto = new SequencingParametersDto();
      parametersDto.setId(-1L);
      parametersDto.setName("(None)");
      dto.setParameters(parametersDto);
    }
    dto.setProgress(from.getProgress());

    if (includeContainers) {
      dto.setContainers(asContainerDtos(from.getSequencerPartitionContainers(), includeContainerPartitions, includePoolContents));
    }

    return dto;
  }

  public static List<RunDto> asRunDtos(Collection<Run> runSubset) {
    return runSubset.stream().map(Dtos::asDto).collect(Collectors.toList());
  }

  public static ContainerDto asDto(@Nonnull SequencerPartitionContainer from) {
    return asDto(from, false, false);
  }

  public static ContainerDto asDto(@Nonnull SequencerPartitionContainer from, boolean includePartitions, boolean includePoolContents) {
    ContainerDto dto = null;
    if (from instanceof OxfordNanoporeContainer) {
      OxfordNanoporeContainer ontFrom = (OxfordNanoporeContainer) from;
      OxfordNanoporeContainerDto ontDto = new OxfordNanoporeContainerDto();
      if (ontFrom.getPoreVersion() != null) {
        ontDto.setPoreVersionId(ontFrom.getPoreVersion().getId());
      }
      ontDto.setReceivedDate(formatDate(ontFrom.getReceivedDate()));
      ontDto.setReturnedDate(formatDate(ontFrom.getReturnedDate()));
      dto = ontDto;
    } else {
      dto = new ContainerDto();
    }
    dto.setId(from.getId());
    dto.setIdentificationBarcode(from.getIdentificationBarcode());
    dto.setModel(asDto(from.getModel()));
    dto.setDescription(from.getDescription());
    Run lastRun = from.getLastRun();
    if (lastRun != null) {
      dto.setLastRunAlias(lastRun.getAlias());
      dto.setLastRunId(lastRun.getId());
      dto.setLastSequencerId(lastRun.getSequencer().getId());
      dto.setLastSequencerName(lastRun.getSequencer().getName());
    }
    if (from.getLastModified() != null) {
      dto.setLastModified(formatDateTime(from.getLastModified()));
    }
    if (from.getClusteringKit() != null) {
      dto.setClusteringKitId(from.getClusteringKit().getId());
    }
    if (from.getMultiplexingKit() != null) {
      dto.setMultiplexingKitId(from.getMultiplexingKit().getId());
    }

    if (includePartitions) {
      dto.setPartitions(asPartitionDtos(from.getPartitions(), includePoolContents));
    }
    return dto;
  }

  public static List<ContainerDto> asContainerDtos(@Nonnull Collection<SequencerPartitionContainer> containerSubset,
      boolean includeContainerPartitions, boolean includePoolContents) {
    return asContainerDtos(containerSubset, includeContainerPartitions, includePoolContents);
  }

  public static SequencerPartitionContainer to(@Nonnull ContainerDto from) {
    SequencerPartitionContainer to = null;
    if (from instanceof OxfordNanoporeContainerDto) {
      OxfordNanoporeContainerDto ontFrom = (OxfordNanoporeContainerDto) from;
      OxfordNanoporeContainer ontTo = new OxfordNanoporeContainer();
      setObject(ontTo::setPoreVersion, PoreVersion::new, ontFrom.getPoreVersionId());
      setDate(ontTo::setReceivedDate, ontFrom.getReceivedDate());
      setDate(ontTo::setReturnedDate, ontFrom.getReturnedDate());
      to = ontTo;
    } else {
      to = new SequencerPartitionContainerImpl();
    }
    setLong(to::setId, from.getId(), false);
    setString(to::setIdentificationBarcode, from.getIdentificationBarcode());
    setObject(to::setModel, SequencingContainerModel::new, maybeGetProperty(from.getModel(), ContainerModelDto::getId));
    setString(to::setDescription, from.getDescription());
    setObject(to::setClusteringKit, KitDescriptor::new, from.getClusteringKitId());
    setObject(to::setMultiplexingKit, KitDescriptor::new, from.getMultiplexingKitId());

    // Note: partitions not included

    return to;
  }

  public static RunPositionDto asDto(@Nonnull RunPosition from) {
    RunPositionDto dto = new RunPositionDto();
    setId(dto::setPositionId, from.getPosition());
    setString(dto::setPositionAlias, maybeGetProperty(from.getPosition(), InstrumentPosition::getAlias));
    setId(dto::setId, from.getContainer());
    setString(dto::setIdentificationBarcode, maybeGetProperty(from.getContainer(), SequencerPartitionContainer::getIdentificationBarcode));
    setObject(dto::setContainerModel, from.getContainer().getModel(), Dtos::asDto);
    setDateTimeString(dto::setLastModified, maybeGetProperty(from.getContainer(), SequencerPartitionContainer::getLastModified));
    return dto;
  }

  public static ContainerModelDto asDto(@Nonnull SequencingContainerModel from) {
    ContainerModelDto dto = new ContainerModelDto();
    dto.setId(from.getId());
    dto.setAlias(from.getAlias());
    dto.setIdentificationBarcode(from.getIdentificationBarcode());
    dto.setPlatformType(from.getPlatformType().name());
    dto.setInstrumentModelIds(from.getInstrumentModels().stream().map(InstrumentModel::getId).collect(Collectors.toList()));
    dto.setPartitionCount(from.getPartitionCount());
    dto.setArchived(from.isArchived());
    return dto;
  }

  public static List<PartitionDto> asPartitionDtos(@Nonnull Collection<Partition> partitionSubset, boolean includePoolContents) {
    List<PartitionDto> dtoList = new ArrayList<>();
    for (Partition partition : partitionSubset) {
      dtoList.add(asDto(partition, includePoolContents));
    }
    return dtoList;
  }

  public static List<ContainerModelDto> asDtos(@Nonnull Collection<SequencingContainerModel> models) {
    return models.stream().map(Dtos::asDto).collect(Collectors.toList());
  }

  public static Set<QcTypeDto> asQcTypeDtos(@Nonnull Set<QcType> from) {
    return from.stream().map(Dtos::asDto).collect(Collectors.toSet());
  }

  public static QcTypeDto asDto(@Nonnull QcType from) {
    QcTypeDto dto = new QcTypeDto();
    dto.setId(from.getId());
    dto.setName(from.getName());
    dto.setDescription(from.getDescription());
    dto.setQcTarget(from.getQcTarget());
    dto.setUnits(from.getUnits());
    dto.setPrecisionAfterDecimal(from.getPrecisionAfterDecimal());
    dto.setCorrespondingField(from.getCorrespondingField());
    dto.setAutoUpdateField(from.isAutoUpdateField());
    dto.setArchived(from.isArchived());
    return dto;
  }

  public static QcType to(@Nonnull QcTypeDto from) {
    QcType to = new QcType();
    if (from.getId() != null) to.setId(from.getId());
    to.setName(from.getName());
    to.setDescription(from.getDescription());
    to.setQcTarget(from.getQcTarget());
    to.setUnits(from.getUnits());
    to.setPrecisionAfterDecimal(from.getPrecisionAfterDecimal());
    to.setArchived(from.isArchived());
    to.setCorrespondingField(from.getCorrespondingField());
    to.setAutoUpdateField(from.isAutoUpdateField());
    return to;
  }

  public static QcDto asDto(@Nonnull QC from) {
    QcDto dto = new QcDto();
    dto.setId(from.getId());
    dto.setDate(formatDate(from.getDate()));
    dto.setCreator(from.getCreator().getFullName());
    dto.setType(asDto(from.getType()));
    dto.setResults(from.getResults());
    dto.setEntityId(from.getEntity().getId());
    dto.setEntityAlias(from.getEntity().getAlias());
    dto.setDescription(from.getDescription());
    return dto;
  }

  public static List<QcDto> asQcDtos(@Nonnull Collection<? extends QC> qcSubset) {
    return qcSubset.stream().map(Dtos::asDto).collect(Collectors.toList());
  }

  public static List<QcTypeDto> asQcTypeDtos(@Nonnull Collection<QcType> qcTypeSubset) {
    return qcTypeSubset.stream().map(Dtos::asDto).collect(Collectors.toList());
  }

  public static PoolOrderCompletionDto asDto(@Nonnull PoolOrderCompletion from) {
    PoolOrderCompletionDto dto = new PoolOrderCompletionDto();
    dto.setId(from.getPool().getId() + "_" + from.getSequencingParameters().getId());
    dto.setPool(asDto(from.getPool(), false, false));
    dto.setParameters(asDto(from.getSequencingParameters()));
    dto.setLastUpdated(formatDateTime(from.getLastUpdated()));
    dto.setRemaining(from.getRemaining());
    dto.setCompleted(from.get(HealthType.Completed));
    dto.setFailed(from.get(HealthType.Failed));
    dto.setRequested(from.get(HealthType.Requested));
    dto.setRunning(from.get(HealthType.Running));
    dto.setStarted(from.get(HealthType.Started));
    dto.setStopped(from.get(HealthType.Stopped));
    dto.setUnknown(from.get(HealthType.Unknown));
    dto.setLoaded(from.getLoaded());
    dto.setDescription(from.getDescription());
    return dto;
  }

  public static InstrumentModelDto asDto(@Nonnull InstrumentModel from) {
    InstrumentModelDto dto = new InstrumentModelDto();
    dto.setId(from.getId());
    dto.setPlatformType(from.getPlatformType().name());
    dto.setDescription(from.getDescription());
    dto.setAlias(from.getAlias());
    dto.setNumContainers(from.getNumContainers());
    dto.setInstrumentType(from.getInstrumentType().name());
    if (from.getPositions() != null) {
      dto.setPositions(from.getPositions().stream().map(InstrumentPosition::getAlias).collect(Collectors.toList()));
    }
    return dto;
  }

  public static InstrumentModel to(@Nonnull InstrumentModelDto from) {
    InstrumentModel to = new InstrumentModel();
    to.setId(from.getId());
    to.setPlatformType(PlatformType.get(from.getPlatformType()));
    to.setDescription(from.getDescription());
    to.setAlias(from.getAlias());
    to.setNumContainers(from.getNumContainers());
    to.setInstrumentType(InstrumentType.valueOf(from.getInstrumentType()));
    return to;
  }

  public static ProjectDto asDto(@Nonnull Project from) {
    ProjectDto dto = new ProjectDto();
    dto.setId(from.getId());
    dto.setName(from.getName());
    dto.setAlias(from.getAlias());
    dto.setShortName(from.getShortName());
    dto.setDescription(from.getDescription());
    dto.setProgress(from.getProgress().getKey());
    if (from.getReferenceGenome() != null) {
      dto.setDefaultSciName(from.getReferenceGenome().getDefaultSciName());
    }
    return dto;
  }

  public static List<ProjectDto> asProjectDtos(@Nonnull Collection<Project> from) {
    return from.stream().map(Dtos::asDto).collect(Collectors.toList());
  }

  public static LibraryDesignDto asDto(@Nonnull LibraryDesign from) {
    LibraryDesignDto dto = new LibraryDesignDto();
    dto.setId(from.getId());
    dto.setName(from.getName());
    dto.setDesignCodeId(from.getLibraryDesignCode().getId());
    dto.setSampleClassId(from.getSampleClass().getId());
    dto.setSelectionId(from.getLibrarySelectionType().getId());
    dto.setStrategyId(from.getLibraryStrategyType().getId());
    return dto;
  }

  public static LibraryTypeDto asDto(@Nonnull LibraryType from) {
    LibraryTypeDto dto = new LibraryTypeDto();
    dto.setId(from.getId());
    dto.setAlias(from.getDescription());
    dto.setArchived(from.getArchived());
    dto.setPlatform(from.getPlatformType().name());
    return dto;
  }

  public static LibrarySelectionTypeDto asDto(@Nonnull LibrarySelectionType from) {
    LibrarySelectionTypeDto dto = new LibrarySelectionTypeDto();
    dto.setId(from.getId());
    dto.setAlias(from.getDescription());
    dto.setName(from.getName());
    return dto;
  }

  public static LibraryStrategyTypeDto asDto(@Nonnull LibraryStrategyType from) {
    LibraryStrategyTypeDto dto = new LibraryStrategyTypeDto();
    dto.setId(from.getId());
    dto.setDescription(from.getDescription());
    dto.setName(from.getName());
    return dto;
  }

  public static IndexDto asDto(@Nonnull Index from) {
    return asDto(from, true);
  }

  public static IndexDto asDto(@Nonnull Index from, boolean includeFamily) {
    IndexDto dto = new IndexDto();
    dto.setId(from.getId());
    dto.setLabel(from.getLabel());
    dto.setName(from.getName());
    dto.setPosition(from.getPosition());
    dto.setSequence(from.getSequence());
    if (includeFamily) {
      dto.setFamily(asDto(from.getFamily(), false));
    }
    return dto;
  }

  public static IndexFamilyDto asDto(@Nonnull IndexFamily from) {
    return asDto(from, true);
  }

  private static IndexFamilyDto asDto(@Nonnull IndexFamily from, boolean includeChildren) {
    IndexFamilyDto dto = new IndexFamilyDto();
    dto.setId(from.getId());
    dto.setName(from.getName());
    dto.setArchived(from.getArchived());
    if (includeChildren) {
      dto.setIndices(from.getIndices().stream().map(x -> asDto(x, true)).collect(Collectors.toList()));
    }
    dto.setMaximumNumber(from.getMaximumNumber());
    dto.setPlatformType(from.getPlatformType() == null ? null : from.getPlatformType().name());
    dto.setFakeSequence(from.hasFakeSequence());
    dto.setUniqueDualIndex(from.isUniqueDualIndex());
    return dto;
  }

  public static StainDto asDto(@Nonnull Stain from) {
    StainDto dto = new StainDto();
    dto.setId(from.getId());
    dto.setCategory(from.getCategory() == null ? null : from.getCategory().getName());
    dto.setName(from.getName());
    return dto;
  }

  public static Run to(@Nonnull NotificationDto from) {
    final Run to = getMisoPlatformTypeFromRunscanner(from.getPlatformType()).createRun();
    setCommonRunValues(from, to);

    switch (to.getPlatformType()) {
    case PACBIO:
    case OXFORDNANOPORE:
      break;
    case ILLUMINA:
      setIlluminaRunValues((IlluminaNotificationDto) from, (IlluminaRun) to);
      break;
    default:
      throw new NotImplementedException();
    }
    return to;
  }

  private static void setIlluminaRunValues(@Nonnull IlluminaNotificationDto from, @Nonnull IlluminaRun to) {
    to.setPairedEnd(from.isPairedEndRun());
    to.setNumCycles(from.getNumCycles());
    to.setImgCycle(from.getImgCycle());
    to.setCallCycle(from.getCallCycle());
    to.setScoreCycle(from.getScoreCycle());
    to.setRunBasesMask(from.getRunBasesMask());
    if (!isStringEmptyOrNull(from.getWorkflowType())) {
      to.setWorkflowType(IlluminaWorkflowType.get(from.getWorkflowType()));
    }
  }

  private static void setCommonRunValues(@Nonnull NotificationDto from, @Nonnull Run to) {
    to.setAlias(from.getRunAlias());
    to.setFilePath(from.getSequencerFolderPath());
    to.setHealth(getMisoHealthTypeFromRunscanner(from.getHealthType()));
    to.setStartDate(LimsUtils.toBadDate(from.getStartDate()));
    to.setCompletionDate(LimsUtils.toBadDate(from.getCompletionDate()));
    to.setMetrics(from.getMetrics());
  }

  public static TargetedSequencingDto asDto(@Nonnull TargetedSequencing from) {
    TargetedSequencingDto dto = new TargetedSequencingDto();
    dto.setId(from.getId());
    dto.setAlias(from.getAlias());
    dto.setArchived(from.isArchived());
    dto.setKitDescriptorIds(from.getKitDescriptors().stream().map(KitDescriptor::getId).collect(Collectors.toList()));
    return dto;
  }

  public static Set<TargetedSequencingDto> asTargetedSequencingDtos(Set<TargetedSequencing> from) {
    return from.stream().map(Dtos::asDto).collect(Collectors.toSet());
  }

  public static TargetedSequencing to(@Nonnull TargetedSequencingDto dto) {
    TargetedSequencing to = new TargetedSequencing();
    to.setId(dto.getId());
    to.setAlias(dto.getAlias());
    return to;
  }

  public static Pool to(@Nonnull PoolDto dto) {
    PoolImpl to = new PoolImpl();
    to.setId(dto.getId() == null ? PoolImpl.UNSAVED_ID : dto.getId());
    to.setAlias(dto.getAlias());
    to.setConcentration(dto.getConcentration() == null ? null : Double.valueOf(dto.getConcentration()));
    to.setConcentrationUnits(dto.getConcentrationUnits());
    to.setCreationDate(parseDate(dto.getCreationDate()));
    to.setDescription(dto.getDescription());
    to.setIdentificationBarcode(dto.getIdentificationBarcode());
    to.setDiscarded(dto.isDiscarded());
    if (dto.getVolume() != null) {
      to.setVolume(Double.valueOf(dto.getVolume()));
    }
    to.setVolumeUnits(dto.getVolumeUnits());
    to.setPlatformType(PlatformType.valueOf(dto.getPlatformType()));
    to.setPoolDilutions(dto.getPooledElements().stream().map(dilution -> {
      PoolableElementView view = new PoolableElementView();
      view.setDilutionId(dilution.getId());
      view.setDilutionName(dilution.getName());
      view.setDilutionVolumeUsed(dilution.getVolumeUsed() == null ? null : Double.valueOf(dilution.getVolumeUsed()));

      PoolDilution link = new PoolDilution(to, view);
      if (dilution.getProportion() != null) {
        link.setProportion(dilution.getProportion());
      }
      return link;
    }).collect(Collectors.toSet()));
    to.setQcPassed(dto.getQcPassed());
    to.setBoxPosition((PoolBoxPosition) makeBoxablePosition(dto, to));
    return to;
  }

  public static PrinterBackendDto asDto(@Nonnull Backend from) {
    PrinterBackendDto dto = new PrinterBackendDto();
    dto.setId(from.ordinal());
    dto.setName(from.name());
    dto.setConfigurationKeys(from.getConfigurationKeys());
    return dto;
  }

  public static PrinterDriverDto asDto(@Nonnull Driver from) {
    PrinterDriverDto dto = new PrinterDriverDto();
    dto.setId(from.ordinal());
    dto.setName(from.name());
    return dto;
  }

  public static PrinterLayoutDto asDto(@Nonnull Layout from) {
    PrinterLayoutDto dto = new PrinterLayoutDto();
    dto.setId(from.ordinal());
    dto.setName(from.name());
    return dto;
  }

  public static PrinterDto asDto(@Nonnull Printer from) {
    PrinterDto dto = new PrinterDto();
    dto.setId(from.getId());
    dto.setAvailable(from.isEnabled());
    dto.setBackend(from.getBackend().name());
    // We intentionally do not pass configuration to the front end since it has passwords in it.
    dto.setDriver(from.getDriver().name());
    dto.setLayout(from.getLayout().name());
    dto.setName(from.getName());
    return dto;
  }

  public static Printer to(@Nonnull PrinterDto dto) throws JsonProcessingException {
    Printer to = new Printer();
    to.setId(dto.getId());
    to.setBackend(Backend.valueOf(dto.getBackend()));
    to.setConfiguration(new ObjectMapper().writeValueAsString(dto.getConfiguration()));
    to.setDriver(Driver.valueOf(dto.getDriver()));
    to.setLayout(Layout.valueOf(dto.getLayout()));
    to.setEnabled(dto.isAvailable());
    to.setName(dto.getName());

    return to;
  }

  public static StudyDto asDto(@Nonnull Study from) {
    StudyDto dto = new StudyDto();
    dto.setId(from.getId());
    dto.setAccession(from.getAccession());
    dto.setAlias(from.getAlias());
    dto.setDescription(from.getDescription());
    dto.setName(from.getName());
    dto.setProjectId(from.getProject().getId());
    dto.setStudyTypeId(from.getStudyType().getId());
    return dto;
  }

  public static StudyTypeDto asDto(@Nonnull StudyType from) {
    StudyTypeDto dto = new StudyTypeDto();
    dto.setId(from.getId());
    dto.setName(from.getName());
    return dto;
  }

  public static ChangeLogDto asDto(@Nonnull ChangeLog from) {
    ChangeLogDto dto = new ChangeLogDto();
    dto.setSummary(from.getSummary());
    dto.setTime(formatDateTime(from.getTime()));
    dto.setUserName(from.getUser().getFullName());
    return dto;
  }

  public static UserDto asDto(@Nonnull User from) {
    UserDto dto = new UserDto();
    dto.setId(from.getUserId());
    dto.setActive(from.isActive());
    dto.setAdmin(from.isAdmin());
    dto.setEmail(from.getEmail());
    dto.setExternal(from.isExternal());
    dto.setFullName(from.getFullName());
    dto.setInternal(from.isInternal());
    dto.setLoginName(from.getLoginName());
    return dto;
  }

  public static GroupDto asDto(@Nonnull Group from) {
    GroupDto dto = new GroupDto();
    dto.setId(from.getGroupId());
    dto.setDescription(from.getDescription());
    dto.setName(from.getName());
    return dto;
  }

  public static InstrumentDto asDto(@Nonnull Instrument from) {
    InstrumentDto dto = new InstrumentDto();
    dto.setId(from.getId());
    dto.setDateCommissioned(formatDate(from.getDateCommissioned()));
    dto.setDateDecommissioned(formatDate(from.getDateDecommissioned()));
    dto.setName(from.getName());
    dto.setInstrumentModel(asDto(from.getInstrumentModel()));
    dto.setSerialNumber(from.getSerialNumber());
    if (from.getDateDecommissioned() == null) {
      if (from.isOutOfService()) {
        dto.setStatus("Out of Service");
      } else {
        dto.setStatus("Production");
      }
    } else if (from.getUpgradedInstrument() != null) {
      dto.setStatus("Upgraded");
    } else {
      dto.setStatus("Retired");
    }
    return dto;
  }

  public static Instrument to(@Nonnull InstrumentDto dto) {
    Instrument to = new InstrumentImpl();
    to.setId(dto.getId());
    to.setDateCommissioned(parseDate(dto.getDateCommissioned()));
    to.setDateDecommissioned(parseDate(dto.getDateDecommissioned()));
    to.setName(dto.getName());
    to.setInstrumentModel(to(dto.getInstrumentModel()));
    to.setSerialNumber(dto.getSerialNumber());
    return to;
  }

  public static QC to(@Nonnull QcDto dto) {
    QC to;
    switch (dto.getType().getQcTarget()) {
    case Library:
      LibraryQC newLibraryQc = new LibraryQC();
      Library ownerLibrary = new LibraryImpl();
      ownerLibrary.setId(dto.getEntityId());
      newLibraryQc.setLibrary(ownerLibrary);
      to = newLibraryQc;
      break;
    case Sample:
      SampleQC newSampleQc = new SampleQC();
      Sample ownerSample = new SampleImpl();
      ownerSample.setId(dto.getEntityId());
      newSampleQc.setSample(ownerSample);
      to = newSampleQc;
      break;
    case Pool:
      PoolQC newPoolQc = new PoolQC();
      Pool ownerPool = new PoolImpl();
      ownerPool.setId(dto.getEntityId());
      newPoolQc.setPool(ownerPool);
      to = newPoolQc;
      break;
    case Container:
      ContainerQC newContainerQc = new ContainerQC();
      SequencerPartitionContainer ownerContainer = new SequencerPartitionContainerImpl();
      ownerContainer.setId(dto.getEntityId());
      newContainerQc.setContainer(ownerContainer);
      to = newContainerQc;
      break;
    default:
      throw new IllegalArgumentException("No such QC target: " + dto.getType().getQcTarget());
    }
    if (dto.getId() != null) {
      to.setId(dto.getId());
    }
    to.setDate(parseDate(dto.getDate()));
    to.setResults(dto.getResults());
    to.setType(to(dto.getType()));
    to.setDescription(dto.getDescription());
    return to;
  }

  public static ReferenceGenomeDto asDto(@Nonnull ReferenceGenome from) {
    ReferenceGenomeDto dto = new ReferenceGenomeDto();
    dto.setId(from.getId());
    dto.setAlias(from.getAlias());
    return dto;
  }

  public static PartitionDto asDto(@Nonnull Partition from) {
    return asDto(from, false);
  }

  public static PartitionDto asDto(@Nonnull Partition from, boolean includePoolContents) {
    PartitionDto dto = new PartitionDto();
    dto.setId(from.getId());
    dto.setContainerId(from.getSequencerPartitionContainer().getId());
    dto.setContainerName(from.getSequencerPartitionContainer().getIdentificationBarcode());
    dto.setPartitionNumber(from.getPartitionNumber());
    dto.setPool(from.getPool() == null ? null : asDto(from.getPool(), includePoolContents, false));
    setString(dto::setLoadingConcentration, from.getLoadingConcentration());
    dto.setLoadingConcentrationUnits(from.getLoadingConcentrationUnits());
    return dto;
  }

  public static PartitionQCTypeDto asDto(@Nonnull PartitionQCType from) {
    PartitionQCTypeDto dto = new PartitionQCTypeDto();
    dto.setId(from.getId());
    dto.setDescription(from.getDescription());
    dto.setNoteRequired(from.isNoteRequired());
    dto.setOrderFulfilled(from.isOrderFulfilled());
    dto.setAnalysisSkipped(from.isAnalysisSkipped());
    return dto;
  }

  public static ExperimentDto asDto(@Nonnull Experiment from) {
    ExperimentDto dto = new ExperimentDto();
    dto.setId(from.getId());
    dto.setAccession(from.getAccession());
    dto.setAlias(from.getAlias());
    dto.setDescription(from.getDescription());
    dto.setName(from.getName());
    dto.setInstrumentModel(asDto(from.getInstrumentModel()));
    dto.setLibrary(asDto(from.getLibrary(), false));
    dto.setPartitions(from.getRunPartitions().stream()
        .map(entry -> new ExperimentDto.RunPartitionDto(asDto(entry.getRun()), asDto(entry.getPartition()))).collect(Collectors.toList()));
    dto.setStudy(asDto(from.getStudy()));
    dto.setTitle(from.getTitle());
    return dto;
  }

  public static KitConsumableDto asDto(@Nonnull Kit from) {
    KitConsumableDto dto = new KitConsumableDto();
    dto.setId(from.getId());
    dto.setDate(formatDate(from.getKitDate()));
    dto.setDescriptor(asDto(from.getKitDescriptor()));
    dto.setLotNumber(from.getLotNumber());
    return dto;
  }

  public static Kit to(@Nonnull KitConsumableDto dto) {
    Kit to = new KitImpl();
    if (dto.getId() != null) {
      to.setId(dto.getId());
    }
    to.setKitDate(parseDate(dto.getDate()));
    to.setKitDescriptor(to(dto.getDescriptor()));
    to.setLotNumber(dto.getLotNumber());
    return to;
  }

  public static Experiment to(@Nonnull ExperimentDto dto) {
    Experiment to = new Experiment();
    to.setId(dto.getId());
    to.setAlias(dto.getAlias());
    to.setDescription(dto.getDescription());
    to.setLibrary(to(dto.getLibrary()));
    to.setInstrumentModel(to(dto.getInstrumentModel()));
    to.setRunPartitions(dto.getPartitions().stream().map(rpDto -> {
      RunPartition rpTo = new RunPartition();
      rpTo.setExperiment(to);
      rpTo.setPartition(to(rpDto.getPartition()));
      rpTo.setRun(to(rpDto.getRun()));
      return rpTo;
    }).collect(Collectors.toList()));
    to.setStudy(to(dto.getStudy()));
    to.setTitle(dto.getTitle());
    return to;
  }

  public static Study to(@Nonnull StudyDto dto) {
    Study to = new StudyImpl();
    to.setId(dto.getId());
    return to;
  }

  public static Run to(@Nonnull RunDto dto) {
    Run to = PlatformType.get(dto.getPlatformType()).createRun();
    to.setId(dto.getId());
    return to;
  }

  public static Partition to(@Nonnull PartitionDto dto) {
    Partition to = new PartitionImpl();
    to.setId(dto.getId());
    return to;
  }

  public static SubmissionDto asDto(@Nonnull Submission from) {
    SubmissionDto dto = new SubmissionDto();
    dto.setId(from.getId());
    dto.setAccession(from.getAccession());
    dto.setAlias(from.getAlias());
    dto.setCompleted(from.isCompleted());
    dto.setCreationDate(formatDate(from.getCreationDate()));
    dto.setDescription(from.getDescription());
    dto.setSubmittedDate(formatDate(from.getSubmissionDate()));
    dto.setTitle(from.getTitle());
    dto.setVerified(from.isVerified());
    return dto;
  }

  public static ArrayDto asDto(@Nonnull Array from) {
    ArrayDto dto = new ArrayDto();
    dto.setId(from.getId());
    dto.setAlias(from.getAlias());
    if (from.getArrayModel() != null) {
      dto.setArrayModelId(from.getArrayModel().getId());
      dto.setArrayModelAlias(from.getArrayModel().getAlias());
      dto.setRows(from.getArrayModel().getRows());
      dto.setColumns(from.getArrayModel().getColumns());
    }
    dto.setSerialNumber(from.getSerialNumber());
    dto.setDescription(from.getDescription());
    if (from.getSamples() != null) {
      dto.setSamples(asArraySampleDtos(from.getSamples()));
    }
    if (from.getLastModified() != null) {
      dto.setLastModified(formatDate(from.getLastModified()));
    }
    return dto;
  }

  private static List<ArraySampleDto> asArraySampleDtos(@Nonnull Map<String, Sample> arraySamples) {
    return arraySamples.entrySet().stream().map(entry -> asArraySampleDto(entry.getKey(), entry.getValue())).collect(Collectors.toList());
  }

  private static ArraySampleDto asArraySampleDto(String position, @Nonnull Sample sample) {
    ArraySampleDto dto = new ArraySampleDto();
    dto.setCoordinates(position);
    dto.setId(sample.getId());
    dto.setAlias(sample.getAlias());
    dto.setName(sample.getName());
    dto.setIdentificationBarcode(sample.getIdentificationBarcode());
    return dto;
  }

  public static Array to(@Nonnull ArrayDto from) {
    Array array = new Array();
    if (from.getId() != null) {
      array.setId(from.getId());
    }
    array.setAlias(from.getAlias());
    array.setArrayModel(new ArrayModel());
    if (from.getArrayModelId() != null) {
      array.getArrayModel().setId(from.getArrayModelId());
    }
    if (from.getArrayModelAlias() != null) {
      array.getArrayModel().setAlias(from.getArrayModelAlias());
    }
    array.setSerialNumber(from.getSerialNumber());
    array.setDescription(nullifyStringIfBlank(from.getDescription()));
    array.setSamples(toArraySamples(from.getSamples()));
    return array;
  }

  private static Map<String, Sample> toArraySamples(List<ArraySampleDto> dtos) {
    Map<String, Sample> samples = new HashMap<>();
    if (dtos != null) {
      for (ArraySampleDto dto : dtos) {
        Sample sample = new SampleImpl();
        sample.setId(dto.getId());
        sample.setAlias(dto.getAlias());
        sample.setName(dto.getName());
        sample.setIdentificationBarcode(dto.getIdentificationBarcode());
        samples.put(dto.getCoordinates(), sample);
      }
    }
    return samples;
  }

  public static ArrayRunDto asDto(@Nonnull ArrayRun from) {
    ArrayRunDto dto = new ArrayRunDto();
    dto.setId(from.getId());
    dto.setAlias(from.getAlias());
    dto.setDescription(from.getDescription());
    dto.setFilePath(from.getFilePath());
    if (from.getInstrument() != null) {
      dto.setInstrumentId(from.getInstrument().getId());
      dto.setInstrumentName(from.getInstrument().getName());
    }
    if (from.getArray() != null) {
      dto.setArray(asDto(from.getArray()));
    }
    dto.setStatus(from.getHealth().getKey());
    if (from.getStartDate() != null) {
      dto.setStartDate(formatDate(from.getStartDate()));
    }
    if (from.getCompletionDate() != null) {
      dto.setCompletionDate(formatDate(from.getCompletionDate()));
    }
    if (from.getLastModified() != null) {
      dto.setLastModified(formatDate(from.getLastModified()));
    }
    return dto;
  }

  public static List<ArrayDto> asArrayDtos(Collection<Array> arrays) {
    return arrays.stream()
        .map(Dtos::asDto)
        .collect(Collectors.toList());
  }

  public static final ArrayRun to(@Nonnull ArrayRunDto from) {
    ArrayRun run = new ArrayRun();
    if (from.getId() != null) {
      run.setId(from.getId());
    }
    run.setAlias(from.getAlias());
    run.setDescription(nullifyStringIfBlank(from.getDescription()));
    run.setFilePath(nullifyStringIfBlank(from.getFilePath()));
    run.setInstrument(new InstrumentImpl());
    if (from.getInstrumentId() != null) {
      run.getInstrument().setId(from.getInstrumentId());
    }
    if (from.getInstrumentName() != null) {
      run.getInstrument().setName(from.getInstrumentName());
    }
    if (from.getArray() != null) {
      run.setArray(to(from.getArray()));
    }
    run.setHealth(HealthType.get(from.getStatus()));
    if (from.getStartDate() != null) {
      run.setStartDate(parseDate(from.getStartDate()));
    }
    if (from.getCompletionDate() != null) {
      run.setCompletionDate(parseDate(from.getCompletionDate()));
    }
    if (from.getLastModified() != null) {
      run.setLastModified(parseDate(from.getLastModified()));
    }
    return run;
  }

  public static InstrumentStatusDto asDto(@Nonnull InstrumentStatus from) {
    InstrumentStatusDto to = new InstrumentStatusDto();
    to.setInstrument(asDto(from.getInstrument()));

    List<InstrumentPositionStatusDto> posDtos = new ArrayList<>();
    from.getPositions().forEach((pos, run) -> {
      InstrumentPositionStatusDto posDto = new InstrumentPositionStatusDto();
      posDto.setPosition(pos.getAlias());
      posDto.setRun(run == null ? null : asDto(run));
      posDto.setPools(run == null ? Collections.emptyList()
          : run.getSequencerPartitionContainers().stream()//
              .flatMap(c -> c.getPartitions().stream())//
              .map(Partition::getPool)//
              .filter(Objects::nonNull)//
              .collect(Collectors.groupingBy(Pool::getId)).values().stream()//
              .map(l -> l.get(0))//
              .sorted((a, b) -> a.getAlias().compareTo(b.getAlias()))//
              .map(p -> asDto(p, false, false))//
              .collect(Collectors.toList()));
      ServiceRecord instrumentOutOfServiceRecord = from.getInstrument().getServiceRecords().stream()
          .filter(sr -> sr.isOutOfService() && sr.getStartTime() != null && sr.getEndTime() == null
              && (sr.getPosition() == null || sr.getPosition().getAlias().equals(pos.getAlias())))
          .min(Comparator.comparing(ServiceRecord::getStartTime))
          .orElse(null);
      if (instrumentOutOfServiceRecord != null) {
        posDto.setOutOfService(true);
        posDto.setOutOfServiceTime(formatDateTime(instrumentOutOfServiceRecord.getStartTime()));
      }
      posDtos.add(posDto);
    });
    to.setPositions(posDtos);
    return to;
  }

  public static SpreadsheetFormatDto asDto(@Nonnull SpreadSheetFormat from) {
    SpreadsheetFormatDto dto = new SpreadsheetFormatDto();
    dto.setName(from.name());
    dto.setDescription(from.description());
    return dto;
  }

  public static SpreadsheetDto asDto(@Nonnull Spreadsheet<?> from) {
    SpreadsheetDto dto = new SpreadsheetDto();
    dto.setDescription(from.description());
    dto.setName(from.name());
    return dto;
  }

  public static SampleSpreadSheetDto asDto(@Nonnull SampleSpreadSheets from) {
    SampleSpreadSheetDto dto = new SampleSpreadSheetDto();
    dto.setDescription(from.description());
    dto.setName(from.name());
    dto.setAllowedClasses(from.allowedClasses());
    return dto;
  }

  public static DeletionDto asDto(@Nonnull Deletion from) {
    DeletionDto dto = new DeletionDto();
    dto.setId(from.getId());
    dto.setTargetType(from.getTargetType());
    dto.setTargetId(from.getTargetId());
    dto.setDescription(from.getDescription());
    dto.setUserName(from.getUser().getFullName());
    dto.setChangeTime(formatDateTime(from.getChangeTime()));
    return dto;
  }

  public static BarcodableDto asDto(@Nonnull BarcodableView from) {
    BarcodableDto dto = new BarcodableDto();
    dto.setId(from.getId().getTargetId());
    dto.setEntityType(from.getId().getTargetType().toString());
    dto.setAlias(from.getAlias());
    dto.setName(from.getName());
    dto.setIdentificationBarcode(from.getIdentificationBarcode());
    return dto;
  }

  public static LibraryTemplateDto asDto(@Nonnull LibraryTemplate from) {
    LibraryTemplateDto dto = null;
    if (from instanceof DetailedLibraryTemplate) {
      dto = asDetailedLibraryTemplateDto((DetailedLibraryTemplate) from);
    } else {
      dto = new LibraryTemplateDto();
    }

    dto.setId(from.getId());
    dto.setAlias(from.getAlias());
    dto.setProjectIds(from.getProjects().stream().map(Project::getId).collect(Collectors.toList()));
    dto.setDefaultVolume(from.getDefaultVolume());
    dto.setPlatformType(from.getPlatformType() != null ? from.getPlatformType().name() : null);
    dto.setLibraryTypeId(from.getLibraryType() != null ? from.getLibraryType().getId() : null);
    dto.setSelectionTypeId(from.getLibrarySelectionType() != null ? from.getLibrarySelectionType().getId() : null);
    dto.setStrategyTypeId(from.getLibraryStrategyType() != null ? from.getLibraryStrategyType().getId() : null);
    dto.setKitDescriptorId(from.getKitDescriptor() != null ? from.getKitDescriptor().getId() : null);
    dto.setIndexFamilyId(from.getIndexFamily() != null ? from.getIndexFamily().getId() : null);
    if (from.getIndexFamily() != null) {
      if (from.getIndexOnes() != null) {
        dto.setIndexOneIds(from.getIndexOnes().entrySet().stream()
            .collect(Collectors.toMap(Entry::getKey, entry -> entry.getValue().getId())));
      }
      if (from.getIndexTwos() != null) {
        dto.setIndexTwoIds(from.getIndexTwos().entrySet().stream()
            .collect(Collectors.toMap(Entry::getKey, entry -> entry.getValue().getId())));
      }
    }
    return dto;
  }

  private static DetailedLibraryTemplateDto asDetailedLibraryTemplateDto(@Nonnull DetailedLibraryTemplate from) {
    DetailedLibraryTemplateDto dto = new DetailedLibraryTemplateDto();
    dto.setDesignId(from.getLibraryDesign() != null ? from.getLibraryDesign().getId() : null);
    dto.setDesignCodeId(from.getLibraryDesignCode() != null ? from.getLibraryDesignCode().getId() : null);
    return dto;
  }

  public static List<LibraryTemplateDto> asLibraryTemplateDtos(@Nonnull Collection<LibraryTemplate> from) {
    return from.stream().map(Dtos::asDto).collect(Collectors.toList());
  }

  public static WorkflowNameDto asDto(@Nonnull WorkflowName from) {
    WorkflowNameDto dto = new WorkflowNameDto();
    dto.setWorkflowName(from);
    dto.setDescription(from.getDescription());
    dto.setBarcode(from.getBarcode());
    return dto;
  }

  public static WorkflowStateDto asDto(@Nonnull Workflow from) {
    WorkflowStateDto dto = new WorkflowStateDto();
    dto.setName(from.getProgress().getWorkflowName().getDescription());
    dto.setWorkflowId(from.getProgress().getId());
    dto.setLog(from.getLog());
    dto.setComplete(from.isComplete());
    dto.setLastModified(formatDateTime(from.getProgress().getLastModified()));
    if (from.isComplete()) {
      dto.setMessage(from.getConfirmMessage());
    } else {
      dto.setStepNumber(from.getNextStepNumber());
      WorkflowStepPrompt prompt = from.getStep(dto.getStepNumber());
      dto.setMessage(prompt.getMessage());
      dto.setInputTypes(prompt.getInputTypes());
    }
    return dto;
  }

  public static WorkflowStateDto asDto(@Nonnull Workflow from, int stepNumber) {
    WorkflowStateDto dto = new WorkflowStateDto();
    dto.setName(from.getProgress().getWorkflowName().getDescription());
    dto.setWorkflowId(from.getProgress().getId());
    dto.setLog(from.getLog());
    dto.setComplete(from.isComplete());
    dto.setLastModified(formatDateTime(from.getProgress().getLastModified()));
    if (stepNumber >= from.getLog().size()) {
      if (from.isComplete()) {
        dto.setMessage(from.getConfirmMessage());
      } else {
        dto.setStepNumber(from.getNextStepNumber());
      }
    } else {
      dto.setStepNumber(stepNumber);
    }
    if (dto.getStepNumber() != null) {
      WorkflowStepPrompt prompt = from.getStep(stepNumber);
      dto.setMessage(prompt.getMessage());
      dto.setInputTypes(prompt.getInputTypes());
    }
    return dto;
  }

  public static StorageLocationDto asDto(@Nonnull StorageLocation from, boolean includeChildLocations, boolean recursive) {
    StorageLocationDto dto = new StorageLocationDto();
    dto.setId(from.getId());
    if (from.getParentLocation() != null) {
      dto.setParentLocationId(from.getParentLocation().getId());
    }
    dto.setLocationUnit(from.getLocationUnit().name());
    switch (from.getLocationUnit().getBoxStorageAmount()) {
    case NONE:
      dto.setAvailableStorage(false);
      break;
    case SINGLE:
      dto.setAvailableStorage(from.getBoxes().isEmpty());
      break;
    case MULTIPLE:
      dto.setAvailableStorage(true);
      break;
    default:
      throw new IllegalStateException("Unexpected BoxStorageAmount");
    }
    dto.setAlias(from.getAlias());
    dto.setIdentificationBarcode(from.getIdentificationBarcode());
    dto.setDisplayLocation(from.getDisplayLocation());
    dto.setFullDisplayLocation(from.getFullDisplayLocation());
    dto.setMapUrl(from.getMapUrl());
    dto.setProbeId(from.getProbeId());
    if (includeChildLocations) {
      dto.setChildLocations(from.getChildLocations().stream()
          .map(child -> Dtos.asDto(child, recursive, recursive))
          .collect(Collectors.toList()));
    }
    dto.setBoxes(from.getBoxes().stream().map(box -> asDto(box, true)).collect(Collectors.toSet()));
    return dto;
  }

  public static StorageLocation to(@Nonnull StorageLocationDto from) {
    StorageLocation location = new StorageLocation();
    location.setId(from.getId());
    location.setAlias(from.getAlias());
    if (!LimsUtils.isStringEmptyOrNull(from.getIdentificationBarcode())) {
      location.setIdentificationBarcode(from.getIdentificationBarcode());
    }
    if (!LimsUtils.isStringEmptyOrNull(from.getMapUrl())) {
      location.setMapUrl(from.getMapUrl());
    }
    if (from.getParentLocationId() != null) {
      location.setParentLocation(new StorageLocation());
      location.getParentLocation().setId(from.getParentLocationId());
    }
    location.setLocationUnit(LocationUnit.valueOf(from.getLocationUnit()));
    location.setProbeId(from.getProbeId());
    return location;
  }

  public static QcTargetDto asDto(@Nonnull QcTarget from) {
    QcTargetDto dto = new QcTargetDto();
    dto.setQcTarget(from);
    dto.setCorrespondingFields(from.getCorrespondingFields());
    return dto;
  }

  public static IssueDto asDto(@Nonnull Issue from) {
    IssueDto dto = new IssueDto();
    dto.setKey(from.getKey());
    dto.setSummary(from.getSummary());
    dto.setUrl(from.getUrl());
    dto.setStatus(from.getStatus());
    dto.setLastUpdated(formatDateTime(from.getLastUpdated()));
    return dto;
  }

  public static AttachmentDto asDto(@Nonnull FileAttachment from) {
    AttachmentDto dto = new AttachmentDto();
    setId(dto::setId, from);
    setString(dto::setFilename, from.getFilename());
    setString(dto::setPath, from.getPath());
    setString(dto::setCategory, maybeGetProperty(from.getCategory(), AttachmentCategory::getAlias));
    setString(dto::setCreator, maybeGetProperty(from.getCreator(), User::getLoginName));
    setDateString(dto::setCreated, from.getCreationTime());
    return dto;
  }

  public static ConcentrationUnitDto asDto(ConcentrationUnit from) {
    ConcentrationUnitDto dto = new ConcentrationUnitDto();
    dto.setName(from);
    dto.setUnits(from == null ? null : from.getUnits());
    return dto;
  }

  public static ServiceRecordDto asDto(@Nonnull ServiceRecord from) {
    ServiceRecordDto dto = new ServiceRecordDto();
    setId(dto::setId, from);
    setDateString(dto::setServiceDate, from.getServiceDate());
    setString(dto::setTitle, from.getTitle());
    setString(dto::setDetails, from.getDetails());
    setString(dto::setReferenceNumber, from.getReferenceNumber());
    setString(dto::setPosition, maybeGetProperty(from.getPosition(), InstrumentPosition::getAlias));
    dto.setAttachments(from.getAttachments().stream().map(Dtos::asDto).collect(Collectors.toList()));
    return dto;
  }

  public static VolumeUnitDto asDto(VolumeUnit from) {
    VolumeUnitDto dto = new VolumeUnitDto();
    dto.setName(from);
    dto.setUnits(from == null ? null : from.getUnits());
    return dto;
  }

  public static WorksetDto asDto(@Nonnull Workset from) {
    WorksetDto dto = new WorksetDto();
    dto.setId(from.getId());
    dto.setAlias(from.getAlias());
    dto.setDescription(from.getDescription());
    if (!from.getSamples().isEmpty()) {
      dto.setSampleIds(from.getSamples().stream().map(Identifiable::getId).collect(Collectors.toList()));
    }
    if (!from.getLibraries().isEmpty()) {
      dto.setLibraryIds(from.getLibraries().stream().map(Identifiable::getId).collect(Collectors.toList()));
    }
    if (!from.getDilutions().isEmpty()) {
      dto.setDilutionIds(from.getDilutions().stream().map(Identifiable::getId).collect(Collectors.toList()));
    }
    dto.setCreator(from.getCreator().getFullName());
    dto.setLastModified(formatDateTime(from.getLastModified()));
    return dto;
  }

  public static Workset to(@Nonnull WorksetDto from) {
    Workset workset = new Workset();
    if (from.getId() != null) {
      workset.setId(from.getId());
    }
    workset.setAlias(from.getAlias());
    workset.setDescription(from.getDescription());
    if (from.getSampleIds() != null && !from.getSampleIds().isEmpty()) {
      workset.setSamples(from.getSampleIds().stream().map(id -> {
        Sample s = new SampleImpl();
        s.setId(id);
        return s;
      }).collect(Collectors.toSet()));
    }
    if (from.getLibraryIds() != null && !from.getLibraryIds().isEmpty()) {
      workset.setLibraries(from.getLibraryIds().stream().map(id -> {
        Library l = new LibraryImpl();
        l.setId(id);
        return l;
      }).collect(Collectors.toSet()));
    }
    if (from.getDilutionIds() != null && !from.getDilutionIds().isEmpty()) {
      workset.setDilutions(from.getDilutionIds().stream().map(id -> {
        LibraryDilution d = new LibraryDilution();
        d.setId(id);
        return d;
      }).collect(Collectors.toSet()));
    }
    return workset;
  }

  public static LibraryTemplate to(@Nonnull LibraryTemplateDto from) {
    LibraryTemplate to = null;
    if (from instanceof DetailedLibraryTemplateDto) {
      to = toDetailedLibraryTemplate((DetailedLibraryTemplateDto) from);
    } else {
      to = new LibraryTemplate();
    }
    if (from.getId() != null) to.setId(from.getId());
    to.setAlias(from.getAlias());
    setObject(to::setPlatformType, from.getPlatformType(), PlatformType::valueOf);
    if (from.getDefaultVolume() != null) {
      to.setDefaultVolume(from.getDefaultVolume());
    }

    if (from.getProjectIds() != null) {
      List<Project> projects = new ArrayList<>();
      from.getProjectIds().stream().forEach(id -> {
        Project project = new ProjectImpl();
        project.setId(id);
        projects.add(project);
      });
      to.setProjects(projects);
    }

    if (from.getLibraryTypeId() != null) {
      LibraryType libraryType = new LibraryType();
      libraryType.setId(from.getLibraryTypeId());
      to.setLibraryType(libraryType);
    }
    if (from.getSelectionTypeId() != null) {
      LibrarySelectionType librarySelectionType = new LibrarySelectionType();
      librarySelectionType.setId(from.getSelectionTypeId());
      to.setLibrarySelectionType(librarySelectionType);
    }
    if (from.getStrategyTypeId() != null) {
      LibraryStrategyType libraryStrategyType = new LibraryStrategyType();
      libraryStrategyType.setId(from.getStrategyTypeId());
      to.setLibraryStrategyType(libraryStrategyType);
    }
    if (from.getKitDescriptorId() != null) {
      KitDescriptor kitDescriptor = new KitDescriptor();
      kitDescriptor.setId(from.getKitDescriptorId());
      to.setKitDescriptor(kitDescriptor);
    }
    if (from.getIndexFamilyId() != null) {
      IndexFamily indexFamily = new IndexFamily();
      indexFamily.setId(from.getIndexFamilyId());
      to.setIndexFamily(indexFamily);
    }
    return to;
  }

  public static DetailedLibraryTemplate toDetailedLibraryTemplate(DetailedLibraryTemplateDto from) {
    if (from == null) return null;
    DetailedLibraryTemplate to = new DetailedLibraryTemplate();
    if (from.getDesignId() != null) {
      LibraryDesign design = new LibraryDesign();
      design.setId(from.getDesignId());
      to.setLibraryDesign(design);
    }
    if (from.getDesignCodeId() != null) {
      LibraryDesignCode ldCode = new LibraryDesignCode();
      ldCode.setId(from.getDesignCodeId());
      to.setLibraryDesignCode(ldCode);
    }
    return to;
  }

  public static LibrarySpikeInDto asDto(@Nonnull LibrarySpikeIn from) {
    LibrarySpikeInDto dto = new LibrarySpikeInDto();
    dto.setId(from.getId());
    setString(dto::setAlias, from.getAlias());
    return dto;
  }

  public static AttachmentCategoryDto asDto(@Nonnull AttachmentCategory from) {
    AttachmentCategoryDto dto = new AttachmentCategoryDto();
    dto.setId(from.getId());
    setString(dto::setAlias, from.getAlias());
    return dto;
  }

  public static AttachmentCategory to(@Nonnull AttachmentCategoryDto from) {
    AttachmentCategory to = new AttachmentCategory();
    setLong(to::setId, from.getId(), false);
    setString(to::setAlias, from.getAlias());
    return to;
  }

  public static PoreVersionDto asDto(@Nonnull PoreVersion from) {
    PoreVersionDto dto = new PoreVersionDto();
    setLong(dto::setId, from.getId(), true);
    setString(dto::setAlias, from.getAlias());
    return dto;
  }

  private static void setBigDecimal(@Nonnull Consumer<BigDecimal> setter, String value) {
    setter.accept(isStringEmptyOrNull(value) ? null : new BigDecimal(value));
  }

  private static void setString(@Nonnull Consumer<String> setter, BigDecimal value) {
    setter.accept(toNiceString(value));
  }

  private static void setString(@Nonnull Consumer<String> setter, String value) {
    setter.accept(isStringBlankOrNull(value) ? null : value.trim());
  }

  private static void setDateString(@Nonnull Consumer<String> setter, Date value) {
    setter.accept(value == null ? null : formatDate(value));
  }

  private static void setDate(@Nonnull Consumer<Date> setter, String value) {
    setter.accept(value == null ? null : parseDate(value));
  }

  private static void setDateTimeString(@Nonnull Consumer<String> setter, Date value) {
    setter.accept(value == null ? null : formatDateTime(value));
  }

  private static void setLong(@Nonnull Consumer<Long> setter, Long value, boolean nullOk) {
    Long effectiveValue = value;
    if (effectiveValue == null) {
      effectiveValue = nullOk ? null : 0L;
    }
    setter.accept(effectiveValue);
  }

  private static void setId(@Nonnull Consumer<Long> setter, Identifiable value) {
    setter.accept(value == null ? null : value.getId());
  }

  private static <T extends Identifiable> void setObject(@Nonnull Consumer<T> setter, @Nonnull Supplier<T> constructor, Long id) {
    if (id == null) {
      setter.accept(null);
    } else {
      T obj = constructor.get();
      obj.setId(id);
      setter.accept(obj);
    }
  }

  private static <T> void setObject(@Nonnull Consumer<T> setter, String value, @Nonnull Function<String, T> lookup) {
    setter.accept(value == null ? null : lookup.apply(value));
  }

  private static <S, R> void setObject(@Nonnull Consumer<R> setter, S value, @Nonnull Function<S, R> transform) {
    setter.accept(value == null ? null : transform.apply(value));
  }

  private static <S, R> R maybeGetProperty(S object, @Nonnull Function<S, R> getter) {
    return object == null ? null : getter.apply(object);
  }

  /**
   * Converts from Runscanner's Platform to MISO's PlatformType.
   * 
   * @param rsType Runscanner Platform
   * @return equivalent MISO PlatformType
   */
  public static PlatformType getMisoPlatformTypeFromRunscanner(@Nonnull ca.on.oicr.gsi.runscanner.dto.type.Platform rsType) {
    return PlatformType.valueOf(rsType.name());
  }

  /**
   * Converts from Runscanner's HealthType to MISO's HealthType
   * 
   * @param rsType Runscanner HealthType
   * @return equivalent MISO HealthType
   */
  public static HealthType getMisoHealthTypeFromRunscanner(@Nonnull ca.on.oicr.gsi.runscanner.dto.type.HealthType rsType) {
    return HealthType.valueOf(toMisoFormat(rsType.name()));
  }

  /**
   * Converts ABCD to Abcd for compatibility with MISO's enum formatting.
   * 
   * @param name Value of a Runscanner enum, all caps
   * @return name made Upper Camel Case
   */
  private static String toMisoFormat(@Nonnull String name) {
    return name.substring(0, 1) + name.substring(1).toLowerCase();
  }

  /**
   * Converts from Runscanner's IlluminaChemistry to MISO's IlluminaChemistry
   * 
   * @param rsType Runscanner IlluminaChemistry
   * @return equivalent MISO IlluminaChemistry
   */
  public static IlluminaChemistry getMisoIlluminaChemistryFromRunscanner(
      @Nonnull ca.on.oicr.gsi.runscanner.dto.type.IlluminaChemistry rsType) {
    return IlluminaChemistry.valueOf(rsType.name());
  }

}
