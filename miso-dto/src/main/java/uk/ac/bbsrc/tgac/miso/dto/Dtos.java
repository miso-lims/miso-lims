package uk.ac.bbsrc.tgac.miso.dto;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;

import java.io.IOException;
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
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import ca.on.oicr.gsi.runscanner.dto.IlluminaNotificationDto;
import ca.on.oicr.gsi.runscanner.dto.NotificationDto;
import ca.on.oicr.gsi.runscanner.dto.OxfordNanoporeNotificationDto;
import ca.on.oicr.gsi.runscanner.dto.type.IndexSequencing;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractBoxPosition;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractBoxable;
import uk.ac.bbsrc.tgac.miso.core.data.Aliasable;
import uk.ac.bbsrc.tgac.miso.core.data.Array;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayModel;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayRun;
import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.BoxPosition;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize.BoxType;
import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.ConcentrationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment.RunPartition;
import uk.ac.bbsrc.tgac.miso.core.data.GroupIdentifiable;
import uk.ac.bbsrc.tgac.miso.core.data.HierarchyEntity;
import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.data.IlluminaChemistry;
import uk.ac.bbsrc.tgac.miso.core.data.IlluminaRun;
import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.IndexFamily;
import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentDataManglingPolicy;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentPosition;
import uk.ac.bbsrc.tgac.miso.core.data.IonTorrentRun;
import uk.ac.bbsrc.tgac.miso.core.data.Issue;
import uk.ac.bbsrc.tgac.miso.core.data.Kit;
import uk.ac.bbsrc.tgac.miso.core.data.KitImpl;
import uk.ac.bbsrc.tgac.miso.core.data.LS454Run;
import uk.ac.bbsrc.tgac.miso.core.data.Lab;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.core.data.LibrarySpikeIn;
import uk.ac.bbsrc.tgac.miso.core.data.OxfordNanoporeRun;
import uk.ac.bbsrc.tgac.miso.core.data.PacBioRun;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.PartitionQCType;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Printer;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.ReferenceGenome;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunLibraryQcStatus;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartitionAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquotSingleCell;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleNumberPerProject;
import uk.ac.bbsrc.tgac.miso.core.data.SamplePurpose;
import uk.ac.bbsrc.tgac.miso.core.data.SampleSingleCell;
import uk.ac.bbsrc.tgac.miso.core.data.SampleSlide;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStockRna;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStockSingleCell;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissuePiece;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;
import uk.ac.bbsrc.tgac.miso.core.data.SampleType;
import uk.ac.bbsrc.tgac.miso.core.data.SampleValidRelationship;
import uk.ac.bbsrc.tgac.miso.core.data.ScientificName;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingControlType;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingOrder;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.ServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.data.SolidRun;
import uk.ac.bbsrc.tgac.miso.core.data.Stain;
import uk.ac.bbsrc.tgac.miso.core.data.StainCategory;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.StudyType;
import uk.ac.bbsrc.tgac.miso.core.data.Submission;
import uk.ac.bbsrc.tgac.miso.core.data.Subproject;
import uk.ac.bbsrc.tgac.miso.core.data.TissueMaterial;
import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;
import uk.ac.bbsrc.tgac.miso.core.data.TissueType;
import uk.ac.bbsrc.tgac.miso.core.data.VolumeUnit;
import uk.ac.bbsrc.tgac.miso.core.data.Workstation;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Assay;
import uk.ac.bbsrc.tgac.miso.core.data.impl.AttachmentCategory;
import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Contact;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Deletion;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedLibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedLibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedLibraryTemplate;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedQcStatusImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedSampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.FileAttachment;
import uk.ac.bbsrc.tgac.miso.core.data.impl.InstrumentImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LabImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryBatch;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryTemplate;
import uk.ac.bbsrc.tgac.miso.core.data.impl.OrderLibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.OxfordNanoporeContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Pipeline;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolOrder;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoreVersion;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ReferenceGenomeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPurpose;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleAliquotImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleAliquotRnaImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleAliquotSingleCellImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleClassImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIdentityImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleNumberPerProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SamplePurposeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleSingleCellImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleSlideImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleStockImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleStockRnaImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleStockSingleCellImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissueImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissuePieceImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissueProcessingImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleValidRelationshipImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingOrderImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop.SopCategory;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocationMap;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StudyImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SubprojectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueMaterialImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueOriginImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueTypeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.boxposition.LibraryAliquotBoxPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.boxposition.LibraryBoxPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.boxposition.PoolBoxPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.boxposition.SampleBoxPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.Transfer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferItem;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferLibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferNotification;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferPool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferSample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BarcodableView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.GrandparentSample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.IdentityView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListContainerRunSequencerView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListContainerRunView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListContainerView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListLibraryAliquotView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListPoolView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListWorksetView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentIdentityAttributes;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentSample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentTissueAttributes;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolElement;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.SequencingOrderSummaryView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.box.BoxableView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.box.SampleBoxableView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.instrumentstatus.InstrumentStatus;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.instrumentstatus.InstrumentStatusPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.instrumentstatus.InstrumentStatusPositionRun;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.instrumentstatus.InstrumentStatusPositionRunPool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.QcNode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.QcNodeType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.QcStatusUpdate;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.SampleQcNode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.Workset;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetCategory;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetItem;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetLibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetSample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetStage;
import uk.ac.bbsrc.tgac.miso.core.data.qc.ContainerQC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.ContainerQcControlRun;
import uk.ac.bbsrc.tgac.miso.core.data.qc.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.LibraryQcControlRun;
import uk.ac.bbsrc.tgac.miso.core.data.qc.PoolQC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.PoolQcControlRun;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcControl;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcControlRun;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcTarget;
import uk.ac.bbsrc.tgac.miso.core.data.qc.RequisitionQC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.SampleQcControlRun;
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
import uk.ac.bbsrc.tgac.miso.core.data.type.StatusType;
import uk.ac.bbsrc.tgac.miso.core.data.type.TissuePieceType;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow.WorkflowName;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowStepPrompt;
import uk.ac.bbsrc.tgac.miso.core.service.printing.Backend;
import uk.ac.bbsrc.tgac.miso.core.service.printing.Driver;
import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;
import uk.ac.bbsrc.tgac.miso.core.util.IndexChecker;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.dto.PoolOrderDto.OrderAliquotDto;
import uk.ac.bbsrc.tgac.miso.dto.dashi.QcHierarchyNodeDto;
import uk.ac.bbsrc.tgac.miso.dto.run.IlluminaRunDto;
import uk.ac.bbsrc.tgac.miso.dto.run.IonTorrentRunDto;
import uk.ac.bbsrc.tgac.miso.dto.run.Ls454RunDto;
import uk.ac.bbsrc.tgac.miso.dto.run.OxfordNanoporeRunDto;
import uk.ac.bbsrc.tgac.miso.dto.run.PacBioRunDto;
import uk.ac.bbsrc.tgac.miso.dto.run.RunDto;
import uk.ac.bbsrc.tgac.miso.dto.run.RunPositionDto;
import uk.ac.bbsrc.tgac.miso.dto.run.SolidRunDto;

@SuppressWarnings("squid:S3776") // make Sonar ignore cognitive complexity warnings for this file
public class Dtos {
  private static final Logger log = LoggerFactory.getLogger(Dtos.class);

  public static TissueOriginDto asDto(@Nonnull TissueOrigin from) {
    TissueOriginDto dto = new TissueOriginDto();
    dto.setId(from.getId());
    dto.setAlias(from.getAlias());
    dto.setDescription(from.getDescription());
    dto.setLabel(from.getItemLabel());
    return dto;
  }

  public static Set<TissueOriginDto> asTissueOriginDtos(@Nonnull Collection<TissueOrigin> from) {
    return from.stream().map(Dtos::asDto).collect(Collectors.toSet());
  }

  public static TissueOrigin to(TissueOriginDto from) {
    TissueOrigin to = new TissueOriginImpl();
    if (from.getId() != null)
      to.setId(from.getId());
    setString(to::setAlias, from.getAlias());
    setString(to::setDescription, from.getDescription());
    return to;
  }

  public static TissueTypeDto asDto(@Nonnull TissueType from) {
    TissueTypeDto dto = new TissueTypeDto();
    setLong(dto::setId, from.getId(), true);
    setString(dto::setAlias, from.getAlias());
    setString(dto::setDescription, from.getDescription());
    setString(dto::setLabel, from.getItemLabel());
    return dto;
  }

  public static Set<TissueTypeDto> asTissueTypeDtos(@Nonnull Set<TissueType> from) {
    return from.stream().map(Dtos::asDto).collect(Collectors.toSet());
  }

  public static TissueType to(@Nonnull TissueTypeDto from) {
    TissueType to = new TissueTypeImpl();
    setLong(to::setId, from.getId(), false);
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
    setId(dto::setReferenceGenomeId, from.getReferenceGenome());
    return dto;
  }

  public static Set<SubprojectDto> asSubprojectDtos(@Nonnull Collection<Subproject> from) {
    return from.stream().map(Dtos::asDto).collect(Collectors.toSet());
  }

  public static Subproject to(@Nonnull SubprojectDto from) {
    Subproject to = new SubprojectImpl();
    setLong(to::setId, from.getId(), false);
    setString(to::setAlias, from.getAlias());
    setString(to::setDescription, from.getDescription());
    setBoolean(to::setPriority, from.getPriority(), true);
    setObject(to::setReferenceGenome, ReferenceGenomeImpl::new, from.getReferenceGenomeId());
    setObject(to::setParentProject, ProjectImpl::new, from.getParentProjectId());
    return to;
  }

  public static SampleClassDto asDto(@Nonnull SampleClass from) {
    SampleClassDto dto = new SampleClassDto();
    dto.setId(from.getId());
    dto.setAlias(from.getAlias());
    dto.setSampleCategory(from.getSampleCategory());
    dto.setSampleSubcategory(from.getSampleSubcategory());
    dto.setSuffix(from.getSuffix());
    setString(dto::setV2NamingCode, from.getV2NamingCode());
    dto.setArchived(from.isArchived());
    dto.setDirectCreationAllowed(from.isDirectCreationAllowed());
    dto.setCreationDate(formatDateTime(from.getCreationTime()));
    dto.setLastUpdated(formatDateTime(from.getLastModified()));
    setLong(dto::setCreatedById, maybeGetProperty(from.getCreator(), User::getId), true);
    setLong(dto::setUpdatedById, maybeGetProperty(from.getLastModifier(), User::getId), true);
    setString(dto::setDefaultSampleType, maybeGetProperty(from.getDefaultSampleType(), SampleType::getName));
    dto.setParentRelationships(from.getParentRelationships().stream().map(Dtos::asDto).collect(Collectors.toList()));
    dto.setChildRelationships(from.getChildRelationships().stream().map(Dtos::asDto).collect(Collectors.toList()));
    return dto;
  }

  public static Set<SampleClassDto> asSampleClassDtos(@Nonnull Set<SampleClass> from) {
    return from.stream().map(Dtos::asDto).collect(Collectors.toSet());
  }

  public static SampleClass to(@Nonnull SampleClassDto from) {
    SampleClass to = new SampleClassImpl();
    setLong(to::setId, from.getId(), false);
    to.setAlias(from.getAlias());
    to.setSampleCategory(from.getSampleCategory());
    to.setSampleSubcategory(from.getSampleSubcategory());
    to.setSuffix(from.getSuffix());
    setString(to::setV2NamingCode, from.getV2NamingCode());
    to.setArchived(from.isArchived());
    to.setDirectCreationAllowed(from.isDirectCreationAllowed());
    setObject(to::setDefaultSampleType, from.getDefaultSampleType(), name -> {
      SampleType st = new SampleType();
      st.setName(name);
      return st;
    });
    if (from.getParentRelationships() != null) {
      to.getParentRelationships()
          .addAll(from.getParentRelationships().stream().map(Dtos::to).collect(Collectors.toSet()));
    }
    if (from.getChildRelationships() != null) {
      to.getChildRelationships()
          .addAll(from.getChildRelationships().stream().map(Dtos::to).collect(Collectors.toSet()));
    }
    return to;
  }

  public static DetailedQcStatusDto asDto(@Nonnull DetailedQcStatus from) {
    DetailedQcStatusDto dto = new DetailedQcStatusDto();
    dto.setId(from.getId());
    dto.setStatus(from.getStatus());
    dto.setDescription(from.getDescription());
    dto.setNoteRequired(from.getNoteRequired());
    dto.setCreationDate(formatDateTime(from.getCreationTime()));
    setLong(dto::setCreatedById, maybeGetProperty(from.getCreator(), User::getId), true);
    setLong(dto::setUpdatedById, maybeGetProperty(from.getLastModifier(), User::getId), true);
    dto.setLastUpdated(formatDateTime(from.getLastModified()));
    return dto;
  }

  public static Set<DetailedQcStatusDto> asDetailedQcStatusDtos(@Nonnull Set<DetailedQcStatus> from) {
    return from.stream().map(Dtos::asDto).collect(Collectors.toSet());
  }

  public static DetailedQcStatus to(@Nonnull DetailedQcStatusDto from) {
    DetailedQcStatus to = new DetailedQcStatusImpl();
    setLong(to::setId, from.getId(), false);
    setBoolean(to::setStatus, from.getStatus(), true);
    setString(to::setDescription, from.getDescription());
    setBoolean(to::setNoteRequired, from.isNoteRequired(), false);
    return to;
  }

  public static SampleDto asMinimalDto(@Nonnull Sample from) {
    DetailedSampleDto dto = new DetailedSampleDto();
    copySampleFields(from, dto, false, 0);

    if (isDetailedSample(from)) {
      DetailedSample detailed = (DetailedSample) from;
      dto.setSampleClassId(detailed.getSampleClass().getId());
      setDateString(dto::setCreationDate, detailed.getCreationDate());
      if (detailed.getSubproject() != null) {
        dto.setSubprojectAlias(detailed.getSubproject().getAlias());
        dto.setSubprojectPriority(detailed.getSubproject().getPriority());
      }
      if (detailed.getIdentityAttributes() != null) {
        ParentIdentityAttributes identity = detailed.getIdentityAttributes();
        setString(dto::setIdentityConsentLevel, maybeGetProperty(identity.getConsentLevel(), ConsentLevel::getLabel));
        setString(dto::setEffectiveExternalNames, identity.getExternalName());
      }
      if (detailed.getTissueAttributes() != null) {
        ParentTissueAttributes tissue = detailed.getTissueAttributes();
        setString(dto::setEffectiveTissueOriginAlias, tissue.getTissueOrigin().getAlias());
        setString(dto::setEffectiveTissueOriginDescription, tissue.getTissueOrigin().getDescription());
        setString(dto::setEffectiveTissueTypeAlias, tissue.getTissueType().getAlias());
        setString(dto::setEffectiveTissueTypeDescription, tissue.getTissueType().getDescription());
        setString(dto::setEffectiveTimepoint, tissue.getTimepoint());
      }
      setEffectiveQcFailure(from, dto);
    }
    return dto;
  }

  public static SampleDto asDto(@Nonnull Sample from, boolean includeBoxPositions) {
    return asDto(from, includeBoxPositions, 0);
  }

  public static SampleDto asDto(@Nonnull Sample from, boolean includeBoxPositions, int libraryCount) {
    SampleDto dto = null;

    if (isDetailedSample(from)) {
      dto = asDetailedSampleDto((DetailedSample) from);
    } else {
      dto = new SampleDto();
    }
    copySampleFields(from, dto, includeBoxPositions, libraryCount);
    dto.setAccession(from.getAccession());

    if (from.getQCs() != null && !from.getQCs().isEmpty()) {
      dto.setQcs(asQcDtos(from.getQCs()));
    }

    if (from.getRequisition() != null) {
      Requisition requisition = from.getRequisition();
      setId(dto::setRequisitionId, requisition);
      setString(dto::setRequisitionAlias, requisition.getAlias());
      setId(dto::setRequisitionAssayId, requisition.getAssay());
    }

    return dto;
  }

  public static List<SampleDto> asSampleDtos(@Nonnull Collection<Sample> from, boolean fullIncludingBoxPositions) {
    return from.stream()
        .map(sample -> (fullIncludingBoxPositions ? asDto(sample, true) : asMinimalDto(sample)))
        .collect(Collectors.toList());
  }

  private static SampleDto copySampleFields(@Nonnull Sample from, @Nonnull SampleDto dto, boolean includeBoxPositions,
      int libraryCount) {
    dto.setId(from.getId());
    dto.setName(from.getName());
    dto.setDescription(from.getDescription());
    setLong(dto::setUpdatedById, maybeGetProperty(from.getLastModifier(), User::getId), true);
    dto.setIdentificationBarcode(from.getIdentificationBarcode());
    dto.setLocationBarcode(from.getLocationBarcode());
    dto.setLocationLabel(BoxUtils.makeLocationLabel(from));
    if (from.getBox() != null) {
      dto.setBox(asDto(from.getBox(), includeBoxPositions));
      dto.setBoxPosition(from.getBoxPosition());
    }
    dto.setSampleType(from.getSampleType());
    setId(dto::setDetailedQcStatusId, from.getDetailedQcStatus());
    setString(dto::setDetailedQcStatusNote, from.getDetailedQcStatusNote());
    setString(dto::setQcUserName, maybeGetProperty(from.getQcUser(), User::getFullName));
    setDateString(dto::setQcDate, from.getQcDate());
    dto.setAlias(from.getAlias());
    dto.setProjectId(from.getProject().getId());
    dto.setProjectName(from.getProject().getName());
    dto.setProjectAlias(from.getProject().getAlias());
    dto.setProjectShortName(from.getProject().getShortName());
    setId(dto::setScientificNameId, from.getScientificName());
    dto.setTaxonIdentifier(from.getTaxonIdentifier());
    setString(dto::setInitialVolume, from.getInitialVolume());
    setString(dto::setVolume, from.getVolume());
    dto.setVolumeUnits(from.getVolumeUnits());
    setString(dto::setConcentration, from.getConcentration());
    dto.setConcentrationUnits(from.getConcentrationUnits());
    dto.setDiscarded(from.isDiscarded());
    dto.setLastModified(formatDateTime(from.getLastModified()));
    dto.setLibraryCount(libraryCount);
    setId(dto::setSequencingControlTypeId, from.getSequencingControlType());
    setId(dto::setSopId, from.getSop());

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
    dto.setSampleClassAlias(from.getSampleClass().getAlias());
    dto.setSampleCategory(from.getSampleClass().getSampleCategory());
    dto.setSampleSubcategory(from.getSampleClass().getSampleSubcategory());
    if (from.getSubproject() != null) {
      dto.setSubprojectId(from.getSubproject().getId());
      dto.setSubprojectAlias(from.getSubproject().getAlias());
      dto.setSubprojectPriority(from.getSubproject().getPriority());
    }
    if (from.getParent() != null) {
      DetailedSample parent = from.getParent();
      dto.setParentId(parent.getId());
      setString(dto::setParentName, parent.getName());
      dto.setParentAlias(parent.getAlias());
      dto.setParentSampleClassId(parent.getSampleClass().getId());
      if (parent.getBox() != null) {
        dto.setParentBoxPosition(parent.getBoxPosition());
        dto.setParentBoxPositionLabel(
            BoxUtils.makeBoxPositionLabel(parent.getBox().getAlias(), parent.getBoxPosition()));
      }
    }
    GroupIdentifiable effective = from.getEffectiveGroupIdEntity();
    if (effective != null) {
      dto.setEffectiveGroupId(effective.getGroupId());
      dto.setEffectiveGroupIdSample(effective.getAlias());
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
    setDateString(dto::setCreationDate, from.getCreationDate());
    dto.setNonStandardAlias(from.hasNonStandardAlias());
    setString(dto::setVolumeUsed, from.getVolumeUsed());
    setString(dto::setNgUsed, from.getNgUsed());

    if (from.getIdentityAttributes() != null) {
      ParentIdentityAttributes identity = from.getIdentityAttributes();
      setString(dto::setIdentityConsentLevel, maybeGetProperty(identity.getConsentLevel(), ConsentLevel::getLabel));
      setString(dto::setEffectiveExternalNames, identity.getExternalName());
    }
    if (from.getTissueAttributes() != null) {
      ParentTissueAttributes tissue = from.getTissueAttributes();
      setString(dto::setEffectiveTissueOriginAlias, tissue.getTissueOrigin().getAlias());
      setString(dto::setEffectiveTissueOriginDescription, tissue.getTissueOrigin().getDescription());
      setString(dto::setEffectiveTissueTypeAlias, tissue.getTissueType().getAlias());
      setString(dto::setEffectiveTissueTypeDescription, tissue.getTissueType().getDescription());
    }
    setEffectiveQcFailure(from, dto);

    Requisition requisition = from.getRequisition() == null ? getParentRequisition(from) : from.getRequisition();
    if (requisition != null) {
      setId(dto::setEffectiveRequisitionId, requisition);
      setString(dto::setEffectiveRequisitionAlias, requisition.getAlias());
      setId(dto::setRequisitionAssayId, requisition.getAssay());
    }

    return dto;
  }

  private static void setEffectiveQcFailure(HierarchyEntity from, UpstreamQcFailableDto to) {
    HierarchyEntity failure = from.getFailingParent();
    if (failure != null) {
      to.setEffectiveQcFailureId(failure.getDetailedQcStatus().getId());
      if (failure.getEntityType() == EntityType.SAMPLE && isDetailedSample((Sample) failure)) {
        to.setEffectiveQcFailureLevel(((DetailedSample) failure).getSampleClass().getSampleCategory());
      } else {
        to.setEffectiveQcFailureLevel(failure.getEntityType().getLabel());
      }
    }
  }

  private static void setEffectiveQcFailure(ListLibraryAliquotView from, UpstreamQcFailableDto to) {
    for (ParentAliquot parent = from.getParentAliquot(); parent != null; parent = parent.getParentAliquot()) {
      if (parent.getDetailedQcStatus() != null && Boolean.FALSE.equals(parent.getDetailedQcStatus().getStatus())) {
        to.setEffectiveQcFailureId(parent.getDetailedQcStatus().getId());
        to.setEffectiveQcFailureLevel(EntityType.LIBRARY_ALIQUOT.getLabel());
        return;
      }
    }
    ParentLibrary lib = from.getParentLibrary();
    if (lib != null) {
      if (lib.getDetailedQcStatus() != null && Boolean.FALSE.equals(lib.getDetailedQcStatus().getStatus())) {
        to.setEffectiveQcFailureId(lib.getDetailedQcStatus().getId());
        to.setEffectiveQcFailureLevel(EntityType.LIBRARY.getLabel());
        return;
      }
      ParentSample sam = lib.getParentSample();
      if (sam != null) {
        if (sam.getDetailedQcStatus() != null && Boolean.FALSE.equals(sam.getDetailedQcStatus().getStatus())) {
          to.setEffectiveQcFailureId(sam.getDetailedQcStatus().getId());
          to.setEffectiveQcFailureLevel(
              sam.getParentSampleClass() == null ? EntityType.SAMPLE.getLabel()
                  : sam.getParentSampleClass().getSampleCategory());
          return;
        }
        for (GrandparentSample parent = sam.getParentSample(); parent != null; parent = parent.getParentSample()) {
          if (sam.getDetailedQcStatus() != null && Boolean.FALSE.equals(sam.getDetailedQcStatus().getStatus())) {
            to.setEffectiveQcFailureId(parent.getDetailedQcStatus().getId());
            to.setEffectiveQcFailureLevel(
                parent.getParentSampleClass() == null ? EntityType.SAMPLE.getLabel()
                    : parent.getParentSampleClass().getSampleCategory());
            return;
          }
        }
      }
    }
  }

  public static SampleIdentityDto asDto(IdentityView from) {
    SampleIdentityDto to = new SampleIdentityDto();
    setLong(to::setId, from.getId(), true);
    setLong(to::setProjectId, from.getProjectId(), true);
    setString(to::setAlias, from.getAlias());
    setString(to::setExternalName, from.getExternalName());
    return to;
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
    to.setCreationDate(
        LimsUtils.isStringEmptyOrNull(from.getCreationDate()) ? null : parseDate(from.getCreationDate()));
    if (from.getIdentityId() != null) {
      to.setIdentityId(from.getIdentityId());
    }
    to.setNonStandardAlias(from.getNonStandardAlias());
    to.setParent(getParent(from));
    setBigDecimal(to::setVolumeUsed, from.getVolumeUsed());
    setBigDecimal(to::setNgUsed, from.getNgUsed());
    return to;
  }

  /**
   * Extracts parent details from the DTO, according to these possible cases:
   *
   * <ol>
   * <li>parent ID is provided. This implies that the parent exists, so no other parent information
   * will be required</li>
   * <li>identity information and parentSampleClassId are provided. This implies that a tissue parent
   * should be created, and that the identity may or may not yet exist. If the sampleClassId is an
   * aliquot, a stockClassId must be provided. ParentAliquotClassId may be provided to indicate a
   * second aliquot level in the hierarchy</li>
   * <li>identity information is provided, but no parentSampleClassId. You must be creating a tissue
   * in this case.</li>
   * </ol>
   *
   * @param childDto the DTO to take parent details from
   * @return the parent details from the DTO, or null if there are none. A returned sample will also
   *         include its own parent if applicable.
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
        if (childDto.getParentSampleClassId() == null) {
          throw new IllegalArgumentException("No tissue class specified.");
        }
        DetailedSample tissue = toTissueSample((SampleTissueDto) childDto);
        tissue.setSampleClass(new SampleClassImpl());
        tissue.getSampleClass().setId(childDto.getParentSampleClassId());
        tissue.setParent(parent);
        parent = tissue;

        if (childDto instanceof SampleTissuePieceDto) {
          SampleTissuePieceDto tissuePieceDto = (SampleTissuePieceDto) childDto;
          if (tissuePieceDto.getParentSlideClassId() != null) {
            SampleSlide slide = new SampleSlideImpl();
            slide.setSampleClass(new SampleClassImpl());
            slide.getSampleClass().setId(tissuePieceDto.getParentSlideClassId());
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
          && childDto.getClass() != SampleStockSingleCellDto.class && childDto.getClass() != SampleStockRnaDto.class) {
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
    setLong(dto::setId, from.getId(), true);
    setString(dto::setAlias, from.getAlias());
    return dto;
  }

  public static TissueMaterial to(@Nonnull TissueMaterialDto from) {
    TissueMaterial to = new TissueMaterialImpl();
    setLong(to::setId, from.getId(), false);
    setString(to::setAlias, from.getAlias());
    return to;
  }

  public static SamplePurposeDto asDto(@Nonnull SamplePurpose from) {
    SamplePurposeDto dto = new SamplePurposeDto();
    dto.setId(from.getId());
    dto.setAlias(from.getAlias());
    dto.setArchived(from.isArchived());
    return dto;
  }

  public static SamplePurpose to(@Nonnull SamplePurposeDto from) {
    SamplePurpose to = new SamplePurposeImpl();
    setLong(to::setId, from.getId(), false);
    setString(to::setAlias, from.getAlias());
    setBoolean(to::setArchived, from.isArchived(), false);
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
    } else if (isStockRnaSample(from)) {
      SampleStockRna rnaFrom = (SampleStockRna) from;
      SampleStockRnaDto rna = new SampleStockRnaDto();
      setBoolean(rna::setDnaseTreated, rnaFrom.getDnaseTreated(), true);
      dto = rna;
    } else {
      dto = new SampleStockDto();
    }
    dto.setStrStatus(from.getStrStatus().getLabel());
    setInteger(dto::setSlidesConsumed, from.getSlidesConsumed(), true);
    setId(dto::setReferenceSlideId, from.getReferenceSlide());
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
    } else if (from instanceof SampleStockRnaRelative) {
      SampleStockRnaRelative rnaFrom = (SampleStockRnaRelative) from;
      SampleStockRna rna = new SampleStockRnaImpl();
      setBoolean(rna::setDnaseTreated, rnaFrom.getDnaseTreated(), true);
      to = rna;
    } else {
      to = new SampleStockImpl();
    }
    if (from.getStrStatus() != null) {
      to.setStrStatus(from.getStrStatus());
    }
    setInteger(to::setSlidesConsumed, from.getSlidesConsumed(), true);
    setObject(to::setReferenceSlide, SampleSlideImpl::new, from.getReferenceSlideId());
    return to;
  }

  private static SampleAliquot toAliquotSample(@Nonnull SampleAliquotDto from) {
    SampleAliquot to = null;
    if (from.getClass() == SampleAliquotSingleCellDto.class) {
      SampleAliquotSingleCellDto scFrom = (SampleAliquotSingleCellDto) from;
      SampleAliquotSingleCell sc = new SampleAliquotSingleCellImpl();
      setBigDecimal(sc::setInputIntoLibrary, scFrom.getInputIntoLibrary());
      to = sc;
    } else if (from.getClass() == SampleAliquotRnaDto.class) {
      to = new SampleAliquotRnaImpl();
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

    if (from.getId() != null)
      to.setId(from.getId());
    to.setAccession(nullifyStringIfBlank(from.getAccession()));
    to.setName(from.getName());
    to.setDescription(nullifyStringIfBlank(from.getDescription()));
    to.setIdentificationBarcode(nullifyStringIfBlank(from.getIdentificationBarcode()));
    to.setLocationBarcode(nullifyStringIfBlank(from.getLocationBarcode()));
    to.setSampleType(from.getSampleType());
    setObject(to::setDetailedQcStatus, DetailedQcStatusImpl::new, from.getDetailedQcStatusId());
    setString(to::setDetailedQcStatusNote, from.getDetailedQcStatusNote());
    setObject(to::setScientificName, ScientificName::new, from.getScientificNameId());
    to.setTaxonIdentifier(from.getTaxonIdentifier());
    to.setAlias(from.getAlias());
    to.setDescription(from.getDescription());
    setBigDecimal(to::setInitialVolume, from.getInitialVolume());
    setBigDecimal(to::setVolume, from.getVolume());
    to.setVolumeUnits(from.getVolumeUnits());
    setBigDecimal(to::setConcentration, from.getConcentration());
    to.setConcentrationUnits(from.getConcentrationUnits());
    to.setDiscarded(from.isDiscarded());
    if (from.getProjectId() != null) {
      to.setProject(new ProjectImpl());
      to.getProject().setId(from.getProjectId());
    }
    to.setBoxPosition((SampleBoxPosition) makeBoxablePosition(from, (SampleImpl) to));

    setObject(to::setRequisition, Requisition::new, from.getRequisitionId());
    if (to.getRequisition() != null) {
      Requisition toRequisition = to.getRequisition();
      setString(toRequisition::setAlias, from.getRequisitionAlias());
      if (from.getRequisitionAssayId() != null) {
        setObject(toRequisition::setAssay, Assay::new, from.getRequisitionAssayId());
      }
    }
    setObject(to::setSequencingControlType, SequencingControlType::new, from.getSequencingControlTypeId());
    setObject(to::setSop, Sop::new, from.getSopId());
    to.setCreationReceiptInfo(toReceiptTransfer(from, to));
    return to;
  }

  private static <T extends AbstractBoxableDto, U extends AbstractBoxable> AbstractBoxPosition makeBoxablePosition(
      @Nonnull T from,
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
    dto.setCreationDate(formatDateTime(from.getCreationDate()));
    dto.setLastUpdated(formatDateTime(from.getLastUpdated()));
    setLong(dto::setCreatedById, maybeGetProperty(from.getCreatedBy(), User::getId), true);
    setLong(dto::setUpdatedById, maybeGetProperty(from.getUpdatedBy(), User::getId), true);
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
    setLong(dto::setId, from.getId(), true);
    setLong(dto::setParentId, maybeGetProperty(from.getParent(), SampleClass::getId), true);
    setLong(dto::setChildId, maybeGetProperty(from.getChild(), SampleClass::getId), true);
    setDateTimeString(dto::setCreationDate, from.getCreationTime());
    setDateTimeString(dto::setLastUpdated, from.getLastModified());
    setLong(dto::setCreatedById, maybeGetProperty(from.getCreator(), User::getId), true);
    setLong(dto::setUpdatedById, maybeGetProperty(from.getLastModifier(), User::getId), true);
    setBoolean(dto::setArchived, from.isArchived(), false);
    return dto;
  }

  public static Set<SampleValidRelationshipDto> asSampleValidRelationshipDtos(
      @Nonnull Set<SampleValidRelationship> from) {
    return from.stream().map(Dtos::asDto).collect(Collectors.toSet());
  }

  public static SampleValidRelationship to(@Nonnull SampleValidRelationshipDto from) {
    SampleValidRelationship to = new SampleValidRelationshipImpl();
    setLong(to::setId, from.getId(), false);
    setObject(to::setParent, SampleClassImpl::new, from.getParentId());
    setObject(to::setChild, SampleClassImpl::new, from.getChildId());
    setDateTime(to::setCreationTime, from.getCreationDate());
    setDateTime(to::setLastModified, from.getLastUpdated());
    setObject(to::setCreator, UserImpl::new, from.getCreatedById());
    setObject(to::setLastModifier, UserImpl::new, from.getUpdatedById());
    setBoolean(to::setArchived, from.getArchived(), false);
    return to;
  }

  public static LabDto asDto(@Nonnull Lab from) {
    LabDto dto = new LabDto();
    setLong(dto::setId, from.getId(), true);
    setString(dto::setAlias, from.getAlias());
    setBoolean(dto::setArchived, from.isArchived(), false);
    return dto;
  }

  public static Set<LabDto> asLabDtos(@Nonnull Collection<Lab> from) {
    return from.stream().map(Dtos::asDto).collect(Collectors.toSet());
  }

  public static Lab to(@Nonnull LabDto from) {
    Lab to = new LabImpl();
    setLong(to::setId, from.getId(), false);
    setString(to::setAlias, from.getAlias());
    setBoolean(to::setArchived, from.getArchived(), false);
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
    setString(dto::setTimepoint, from.getTimepoint());
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
    setString(to::setTimepoint, from.getTimepoint());
    return to;
  }

  private static SampleTissueProcessingDto asTissueProcessingSampleDto(@Nonnull SampleTissueProcessing from) {
    from = deproxify(from);

    if (isSampleSlide(from)) {
      return asSlideSampleDto((SampleSlide) from);
    } else if (isTissuePieceSample(from)) {
      return asTissuePieceSampleDto((SampleTissuePiece) from);
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
    } else if (from.getClass() == SampleTissuePieceDto.class) {
      to = toTissuePieceSample((SampleTissuePieceDto) from);
    } else if (from instanceof SampleSingleCellRelative) {
      to = toSingleCellSample((SampleSingleCellRelative) from);
    } else {
      to = new SampleTissueProcessingImpl();
    }
    return to;
  }

  private static SampleSlideDto asSlideSampleDto(@Nonnull SampleSlide from) {
    SampleSlideDto dto = new SampleSlideDto();
    setInteger(dto::setInitialSlides, from.getInitialSlides(), true);
    setInteger(dto::setSlides, from.getSlides(), true);
    setInteger(dto::setThickness, from.getThickness(), true);
    setId(dto::setStainId, from.getStain());
    setString(dto::setPercentTumour, from.getPercentTumour());
    setString(dto::setPercentNecrosis, from.getPercentNecrosis());
    setString(dto::setMarkedArea, from.getMarkedArea());
    setString(dto::setMarkedAreaPercentTumour, from.getMarkedAreaPercentTumour());
    return dto;
  }

  private static SampleSlide toSlideSample(@Nonnull SampleSlideDto from) {
    SampleSlide to = new SampleSlideImpl();
    setInteger(to::setInitialSlides, from.getInitialSlides(), true);
    setInteger(to::setSlides, from.getSlides(), true);
    setInteger(to::setThickness, from.getThickness(), true);
    setObject(to::setStain, Stain::new, from.getStainId());
    setBigDecimal(to::setPercentTumour, from.getPercentTumour());
    setBigDecimal(to::setPercentNecrosis, from.getPercentNecrosis());
    setBigDecimal(to::setMarkedArea, from.getMarkedArea());
    setBigDecimal(to::setMarkedAreaPercentTumour, from.getMarkedAreaPercentTumour());
    return to;
  }

  private static SampleSingleCellDto asSingleCellSampleDto(@Nonnull SampleSingleCell from) {
    SampleSingleCellDto dto = new SampleSingleCellDto();
    setString(dto::setInitialCellConcentration, from.getInitialCellConcentration());
    setString(dto::setDigestion, from.getDigestion());
    return dto;
  }

  private static SampleTissuePieceDto asTissuePieceSampleDto(@Nonnull SampleTissuePiece from) {
    SampleTissuePieceDto dto = new SampleTissuePieceDto();
    dto.setSlidesConsumed(from.getSlidesConsumed());
    dto.setTissuePieceTypeId(from.getTissuePieceType().getId());
    setId(dto::setReferenceSlideId, from.getReferenceSlide());
    return dto;
  }

  private static SampleSingleCell toSingleCellSample(@Nonnull SampleSingleCellRelative from) {
    SampleSingleCell to = new SampleSingleCellImpl();
    setBigDecimal(to::setInitialCellConcentration, from.getInitialCellConcentration());
    setString(to::setDigestion, from.getDigestion());
    return to;
  }

  private static SampleTissuePiece toTissuePieceSample(@Nonnull SampleTissuePieceDto from) {
    SampleTissuePiece to = new SampleTissuePieceImpl();
    TissuePieceType tissuePieceType = new TissuePieceType();
    tissuePieceType.setId(from.getTissuePieceTypeId());
    to.setTissuePieceType(tissuePieceType);
    to.setSlidesConsumed(from.getSlidesConsumed());
    setObject(to::setReferenceSlide, SampleSlideImpl::new, from.getReferenceSlideId());
    return to;
  }

  public static KitDescriptorDto asDto(@Nonnull KitDescriptor from) {
    KitDescriptorDto dto = new KitDescriptorDto();
    dto.setId(from.getId());
    dto.setName(from.getName());
    setString(dto::setDescription, from.getDescription());
    dto.setManufacturer(from.getManufacturer());
    dto.setPartNumber(from.getPartNumber());
    dto.setVersion(from.getVersion());
    dto.setStockLevel(from.getStockLevel());
    setObject(dto::setKitType, from.getKitType(), KitType::getKey);
    setObject(dto::setPlatformType, from.getPlatformType(), PlatformType::getKey);
    setBoolean(dto::setArchived, from.isArchived(), false);
    return dto;
  }

  public static Set<KitDescriptorDto> asKitDescriptorDtos(@Nonnull Collection<KitDescriptor> from) {
    return from.stream().map(Dtos::asDto).collect(Collectors.toSet());
  }

  public static KitDescriptor to(@Nonnull KitDescriptorDto from) {
    KitDescriptor to = new KitDescriptor();
    if (from.getId() != null)
      to.setId(from.getId());
    to.setName(from.getName());
    setString(to::setDescription, from.getDescription());
    to.setManufacturer(from.getManufacturer());
    to.setPartNumber(from.getPartNumber());
    to.setVersion(from.getVersion());
    to.setStockLevel(from.getStockLevel());
    to.setKitType(KitType.get(from.getKitType()));
    to.setPlatformType(PlatformType.get(from.getPlatformType()));
    setBoolean(to::setArchived, from.isArchived(), false);
    return to;
  }

  public static LibraryDesignCodeDto asDto(@Nonnull LibraryDesignCode from) {
    LibraryDesignCodeDto dto = new LibraryDesignCodeDto();
    setLong(dto::setId, from.getId(), true);
    setString(dto::setCode, from.getCode());
    setString(dto::setDescription, from.getDescription());
    setBoolean(dto::setTargetedSequencingRequired, from.isTargetedSequencingRequired(), true);
    return dto;
  }

  public static LibraryDesignCode to(@Nonnull LibraryDesignCodeDto from) {
    LibraryDesignCode to = new LibraryDesignCode();
    setLong(to::setId, from.getId(), false);
    setString(to::setCode, from.getCode());
    setString(to::setDescription, from.getDescription());
    setBoolean(to::setTargetedSequencingRequired, from.isTargetedSequencingRequired(), true);
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
    GroupIdentifiable effective = from.getEffectiveGroupIdEntity();
    if (effective != null) {
      dto.setEffectiveGroupId(effective.getGroupId());
      dto.setEffectiveGroupIdSample(effective.getAlias());
    }
    if (from.getGroupId() != null) {
      dto.setGroupId(from.getGroupId());
    }
    if (from.getGroupDescription() != null) {
      dto.setGroupDescription(from.getGroupDescription());
    }
    if (from.getSample() != null) {
      DetailedSample detailed = (DetailedSample) from.getSample();
      if (detailed.getSubproject() != null) {
        dto.setSubprojectAlias(detailed.getSubproject().getAlias());
        dto.setSubprojectPriority(detailed.getSubproject().getPriority());
      }
      if (detailed.getIdentityAttributes() != null) {
        ParentIdentityAttributes identity = detailed.getIdentityAttributes();
        setString(dto::setIdentityConsentLevel, maybeGetProperty(identity.getConsentLevel(), ConsentLevel::getLabel));
      }
      if (detailed.getTissueAttributes() != null) {
        ParentTissueAttributes tissue = detailed.getTissueAttributes();
        setString(dto::setEffectiveTissueOriginAlias, tissue.getTissueOrigin().getAlias());
        setString(dto::setEffectiveTissueOriginDescription, tissue.getTissueOrigin().getDescription());
        setString(dto::setEffectiveTissueTypeAlias, tissue.getTissueType().getAlias());
        setString(dto::setEffectiveTissueTypeDescription, tissue.getTissueType().getDescription());
      }
    }
    return dto;
  }

  public static DetailedLibrary toDetailedLibrary(DetailedLibraryDto from) {
    if (from == null)
      return null;
    DetailedLibrary to = new DetailedLibraryImpl();
    setObject(to::setLibraryDesign, LibraryDesign::new, from.getLibraryDesignId());
    setObject(to::setLibraryDesignCode, LibraryDesignCode::new, from.getLibraryDesignCodeId());

    if (from.getArchived() != null)
      to.setArchived(from.getArchived());
    to.setNonStandardAlias(from.getNonStandardAlias());
    if (from.getGroupId() != null) {
      to.setGroupId(from.getGroupId());
    }
    if (from.getGroupDescription() != null) {
      to.setGroupDescription(from.getGroupDescription());
    }
    return to;
  }

  public static SequencingOrderDto asDto(@Nonnull SequencingOrder from, IndexChecker indexChecker) {
    SequencingOrderDto dto = new SequencingOrderDto();
    dto.setId(from.getId());
    dto.setPool(asDto(from.getPool(), false, false, indexChecker));
    setId(dto::setContainerModelId, from.getContainerModel());
    dto.setParameters(asDto(from.getSequencingParameter()));
    dto.setPartitions(from.getPartitions());
    dto.setCreationDate(formatDateTime(from.getCreationTime()));
    dto.setLastUpdated(formatDateTime(from.getLastModified()));
    setLong(dto::setCreatedById, maybeGetProperty(from.getCreator(), User::getId), true);
    setLong(dto::setUpdatedById, maybeGetProperty(from.getLastModifier(), User::getId), true);
    dto.setDescription(from.getDescription());
    setLong(dto::setPurposeId, maybeGetProperty(from.getPurpose(), RunPurpose::getId), false);
    setString(dto::setPurposeAlias, maybeGetProperty(from.getPurpose(), RunPurpose::getAlias));
    return dto;
  }

  public static Set<SequencingOrderDto> asSequencingOrderDtos(@Nonnull Collection<SequencingOrder> from,
      IndexChecker indexChecker) {
    return from.stream().map(so -> Dtos.asDto(so, indexChecker)).collect(Collectors.toSet());
  }

  public static SequencingOrder to(@Nonnull SequencingOrderDto from) {
    SequencingOrder to = new SequencingOrderImpl();
    if (from.getId() != null)
      to.setId(from.getId());
    to.setPool(to(from.getPool()));
    setObject(to::setContainerModel, SequencingContainerModel::new, from.getContainerModelId());
    to.setSequencingParameters(to(from.getParameters()));
    to.setPartitions(from.getPartitions());
    to.setDescription(from.getDescription());
    setObject(to::setPurpose, RunPurpose::new, from.getPurposeId());
    return to;
  }

  public static SequencingParametersDto asDto(@Nonnull SequencingParameters from) {
    SequencingParametersDto dto = new SequencingParametersDto();
    setLong(dto::setId, from.getId(), true);
    setString(dto::setName, from.getName());
    setId(dto::setInstrumentModelId, from.getInstrumentModel());
    setString(dto::setInstrumentModelAlias, maybeGetProperty(from.getInstrumentModel(), InstrumentModel::getAlias));
    setInteger(dto::setRead1Length, from.getReadLength(), false);
    setInteger(dto::setRead2Length, from.getReadLength2(), false);
    setString(dto::setChemistry, maybeGetProperty(from.getChemistry(), IlluminaChemistry::name));
    setString(dto::setRunType, from.getRunType());
    return dto;
  }

  public static SequencingParameters to(@Nonnull SequencingParametersDto from) {
    SequencingParameters to = new SequencingParameters();
    setLong(to::setId, from.getId(), false);
    setString(to::setName, from.getName());
    setObject(to::setInstrumentModel, InstrumentModel::new, from.getInstrumentModelId());
    setInteger(to::setReadLength, from.getRead1Length(), false);
    setInteger(to::setReadLength2, from.getRead2Length(), false);
    setObject(to::setChemistry, from.getChemistry(), str -> IlluminaChemistry.valueOf(str));
    setString(to::setRunType, from.getRunType());
    return to;
  }

  public static List<SequencingParametersDto> asSequencingParametersDtos(
      @Nonnull Collection<SequencingParameters> from) {
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
    setString(dto::setParentSampleName, maybeGetProperty(from.getSample(), Sample::getName));
    dto.setParentSampleAlias(from.getSample().getAlias());
    dto.setProjectId(from.getSample().getProject().getId());
    setString(dto::setProjectName, from.getSample().getProject().getName());
    setString(dto::setProjectShortName, from.getSample().getProject().getShortName());
    if (from.getSample() instanceof DetailedSample) {
      dto.setParentSampleClassId(((DetailedSample) from.getSample()).getSampleClass().getId());
    }
    dto.setCreationDate(formatDate(from.getCreationDate()));
    dto.setDescription(from.getDescription());
    dto.setId(from.getId());
    setString(dto::setConcentration, from.getConcentration());
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
    setId(dto::setDetailedQcStatusId, from.getDetailedQcStatus());
    setString(dto::setDetailedQcStatusNote, from.getDetailedQcStatusNote());
    setString(dto::setQcUserName, maybeGetProperty(from.getQcUser(), User::getFullName));
    setDateString(dto::setQcDate, from.getQcDate());
    dto.setLowQuality(from.isLowQuality());
    dto.setPaired(from.getPaired());
    if (from.getPlatformType() != null) {
      dto.setPlatformType(from.getPlatformType().getKey());
    }
    dto.setLastModified(formatDateTime(from.getLastModified()));
    if (from.getKitDescriptor() != null) {
      dto.setKitDescriptorId(from.getKitDescriptor().getId());
    }
    setString(dto::setKitLot, from.getKitLot());
    if (from.getIndex1() != null) {
      IndexFamily family = from.getIndex1().getFamily();
      dto.setIndexFamilyId(family.getId());
      dto.setIndexFamilyName(family.getName());
      dto.setIndex1Id(from.getIndex1().getId());
      dto.setIndex1Label(from.getIndex1().getLabel());
      if (from.getIndex2() != null) {
        dto.setIndex2Id(from.getIndex2().getId());
        dto.setIndex2Label(from.getIndex2().getLabel());
      }
    }
    setString(dto::setInitialVolume, from.getInitialVolume());
    setString(dto::setVolume, from.getVolume());
    dto.setVolumeUnits(from.getVolumeUnits());
    setString(dto::setVolumeUsed, from.getVolumeUsed());
    setString(dto::setNgUsed, from.getNgUsed());
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
      dto.setSampleBoxPosition(from.getSample().getBoxPosition());
      dto.setSampleBoxPositionLabel(
          BoxUtils.makeBoxPositionLabel(from.getSample().getBox().getAlias(), from.getSample().getBoxPosition()));
    }
    setId(dto::setSpikeInId, from.getSpikeIn());
    setString(dto::setSpikeInVolume, from.getSpikeInVolume());
    setString(dto::setSpikeInDilutionFactor,
        maybeGetProperty(from.getSpikeInDilutionFactor(), DilutionFactor::getLabel));
    setBoolean(dto::setUmis, from.getUmis(), true);
    setId(dto::setWorkstationId, from.getWorkstation());
    setId(dto::setThermalCyclerId, from.getThermalCycler());
    setId(dto::setSopId, from.getSop());
    setString(dto::setBatchId, from.getBatchId());
    setEffectiveQcFailure(from, dto);

    Requisition requisition = from.getSample().getRequisition();
    if (requisition == null) {
      requisition = getParentRequisition(from.getSample());
    }
    setId(dto::setRequisitionId, requisition);
    setString(dto::setRequisitionAlias, maybeGetProperty(requisition, Requisition::getAlias));
    setId(dto::setRequisitionAssayId, maybeGetProperty(requisition, Requisition::getAssay));

    return dto;
  }

  public static Library to(@Nonnull LibraryDto from) {
    Library to = null;
    if (from instanceof DetailedLibraryDto) {
      to = toDetailedLibrary((DetailedLibraryDto) from);
    } else {
      to = new LibraryImpl();
    }
    if (from.getId() != null)
      to.setId(from.getId());

    to.setAlias(from.getAlias());
    to.setName(from.getName());
    to.setDescription(from.getDescription());
    to.setIdentificationBarcode(from.getIdentificationBarcode());
    setBigDecimal(to::setConcentration, from.getConcentration());
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
    setObject(to::setLibrarySelectionType, LibrarySelectionType::new, from.getLibrarySelectionTypeId());
    if (from.getLibraryStrategyTypeId() != null) {
      LibraryStrategyType strat = new LibraryStrategyType();
      strat.setId(from.getLibraryStrategyTypeId());
      to.setLibraryStrategyType(strat);
    }
    if (from.getLibraryTypeId() != null) {
      LibraryType type = new LibraryType();
      type.setId(from.getLibraryTypeId());
      if (from.getLibraryTypeAlias() != null)
        type.setDescription(from.getLibraryTypeAlias());
      to.setLibraryType(type);
    }
    setObject(to::setDetailedQcStatus, DetailedQcStatusImpl::new, from.getDetailedQcStatusId());
    setString(to::setDetailedQcStatusNote, from.getDetailedQcStatusNote());
    if (from.getIndex1Id() != null) {
      Index tb1 = new Index();
      tb1.setId(from.getIndex1Id());
      to.setIndex1(tb1);
      if (from.getIndex2Id() != null) {
        Index tb2 = new Index();
        tb2.setId(from.getIndex2Id());
        to.setIndex2(tb2);
      }
    }
    setBigDecimal(to::setInitialVolume, from.getInitialVolume());
    setBigDecimal(to::setVolume, from.getVolume());
    to.setVolumeUnits(from.getVolumeUnits());
    setBigDecimal(to::setVolumeUsed, from.getVolumeUsed());
    setBigDecimal(to::setNgUsed, from.getNgUsed());
    to.setDnaSize(from.getDnaSize());
    if (from.getKitDescriptorId() != null) {
      KitDescriptor kitDescriptor = new KitDescriptor();
      kitDescriptor.setId(from.getKitDescriptorId());
      to.setKitDescriptor(kitDescriptor);
    }
    setString(to::setKitLot, from.getKitLot());
    to.setLocationBarcode(from.getLocationBarcode());
    to.setCreationDate(parseDate(from.getCreationDate()));
    to.setBoxPosition((LibraryBoxPosition) makeBoxablePosition(from, (LibraryImpl) to));
    to.setDiscarded(from.isDiscarded());
    setObject(to::setSpikeIn, LibrarySpikeIn::new, from.getSpikeInId());
    setBigDecimal(to::setSpikeInVolume, from.getSpikeInVolume());
    setObject(to::setSpikeInDilutionFactor, from.getSpikeInDilutionFactor(), DilutionFactor::get);
    setBoolean(to::setUmis, from.getUmis(), false);
    setObject(to::setWorkstation, Workstation::new, from.getWorkstationId());
    setObject(to::setThermalCycler, InstrumentImpl::new, from.getThermalCyclerId());
    setObject(to::setSop, Sop::new, from.getSopId());
    to.setCreationReceiptInfo(toReceiptTransfer(from, to));
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
      dto.setSizeLabel(from.getSize().getLabel());
    }
    if (includePositions) {
      dto.setItems(from.getBoxPositions().values().stream().map(Dtos::asDto).collect(Collectors.toList()));
    }
    if (from.getStorageLocation() != null) {
      dto.setStorageLocationId(from.getStorageLocation().getId());
      dto.setStorageLocationBarcode(from.getStorageLocation().getIdentificationBarcode());
      dto.setFreezerDisplayLocation(from.getStorageLocation().getFreezerDisplayLocation());
      dto.setStorageDisplayLocation(from.getStorageLocation().getFullDisplayLocation());
      setId(dto::setFreezerId, from.getStorageLocation().getFreezerLocation());
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
    dto.setId(from.getId());
    dto.setAlias(from.getAlias());
    dto.setBoxAlias(from.getBoxAlias());
    dto.setBoxPosition(BoxUtils.makeLocationLabel(from));
    dto.setCoordinates(from.getBoxPosition());
    dto.setDiscarded(from.isDiscarded());
    dto.setIdentificationBarcode(from.getIdentificationBarcode());
    dto.setName(from.getName());
    setString(dto::setVolume, from.getVolume());
    dto.setEntityType(from.getEntityType());
    if (from instanceof SampleBoxableView) {
      dto.setSampleClassId(((SampleBoxableView) from).getSampleClassId());
    }
    return dto;
  }

  public static Box to(@Nonnull BoxDto from) {
    Box to = new BoxImpl();
    setLong(to::setId, from.getId(), false);
    setString(to::setAlias, from.getAlias());
    setString(to::setDescription, from.getDescription());
    setString(to::setIdentificationBarcode, from.getIdentificationBarcode());
    setString(to::setLocationBarcode, from.getLocationBarcode());
    setObject(to::setUse, BoxUse::new, from.getUseId());
    setObject(to::setSize, BoxSize::new, from.getSizeId());
    setObject(to::setStorageLocation, StorageLocation::new, from.getStorageLocationId());
    if (!isStringEmptyOrNull(from.getStorageLocationBarcode())) {
      if (to.getStorageLocation() == null) {
        to.setStorageLocation(new StorageLocation());
      }
      StorageLocation storageLocation = to.getStorageLocation();
      setString(storageLocation::setIdentificationBarcode, from.getStorageLocationBarcode());
    }
    return to;
  }

  public static LibraryAliquotDto asDto(@Nonnull LibraryAliquot from, boolean includeBoxPositions) {
    LibraryAliquotDto dto = null;
    Library library = from.getLibrary();
    Sample sample = library == null ? null : library.getSample();
    if (isDetailedLibraryAliquot(from)) {
      DetailedLibraryAliquotDto detailedDto = asDetailedDto((DetailedLibraryAliquot) from);
      DetailedSample detailed = (DetailedSample) sample;
      if (detailed.getSubproject() != null) {
        detailedDto.setSubprojectAlias(detailed.getSubproject().getAlias());
        detailedDto.setSubprojectPriority(detailed.getSubproject().getPriority());
      }
      if (detailed.getIdentityAttributes() != null) {
        ParentIdentityAttributes identity = detailed.getIdentityAttributes();
        setString(detailedDto::setIdentityConsentLevel,
            maybeGetProperty(identity.getConsentLevel(), ConsentLevel::getLabel));
        setString(detailedDto::setEffectiveExternalNames, identity.getExternalName());
      }
      if (detailed.getTissueAttributes() != null) {
        ParentTissueAttributes tissue = detailed.getTissueAttributes();
        setString(detailedDto::setEffectiveTissueOriginAlias, tissue.getTissueOrigin().getAlias());
        setString(detailedDto::setEffectiveTissueOriginDescription, tissue.getTissueOrigin().getDescription());
        setString(detailedDto::setEffectiveTissueTypeAlias, tissue.getTissueType().getAlias());
        setString(detailedDto::setEffectiveTissueTypeDescription, tissue.getTissueType().getDescription());
      }
      dto = detailedDto;
    } else {
      dto = new LibraryAliquotDto();
    }
    if (library != null) {
      setLong(dto::setLibraryId, library.getId(), true);
      setString(dto::setLibraryName, library.getName());
      setString(dto::setLibraryAlias, library.getAlias());
      setBoolean(dto::setLibraryLowQuality, library.isLowQuality(), false);
      setString(dto::setLibraryPlatformType, library.getPlatformType().getKey());
      if (library.getIndex1() != null) {
        List<Index> indices =
            Stream.of(library.getIndex1(), library.getIndex2()).filter(Objects::nonNull).collect(Collectors.toList());
        dto.setIndexIds(indices.stream().sorted(Comparator.comparingInt(Index::getPosition)).map(Index::getId)
            .collect(Collectors.toList()));
        dto.setIndexLabels(indices.stream().sorted(Comparator.comparingInt(Index::getPosition)).map(Index::getLabel)
            .collect(Collectors.toList()));
      }
      if (sample != null) {
        setLong(dto::setSampleId, sample.getId(), true);
        setString(dto::setSampleName, sample.getName());
        setString(dto::setSampleAlias, sample.getAlias());
        Project project = sample.getProject();
        if (sample.getProject() != null) {
          setLong(dto::setProjectId, project.getId(), true);
          setString(dto::setProjectName, project.getName());
          setString(dto::setProjectShortName, project.getShortName());
        }
      }
    }
    if (from.getParentAliquot() != null) {
      setLong(dto::setParentAliquotId, from.getParentAliquot().getId(), true);
      setString(dto::setParentAliquotAlias, from.getParentAliquot().getAlias());
    }
    setString(dto::setParentName, maybeGetProperty(from.getParent(), HierarchyEntity::getName));
    setString(dto::setParentVolume, maybeGetProperty(from.getParent(), HierarchyEntity::getVolume));
    dto.setId(from.getId());
    dto.setName(from.getName());
    setString(dto::setAlias, from.getAlias());
    setString(dto::setDescription, from.getDescription());
    dto.setCreatorName(from.getCreator().getFullName());
    setString(dto::setConcentration, from.getConcentration());
    dto.setConcentrationUnits(from.getConcentrationUnits());
    setString(dto::setVolume, from.getVolume());
    dto.setVolumeUnits(from.getVolumeUnits());
    setString(dto::setNgUsed, from.getNgUsed());
    setString(dto::setVolumeUsed, from.getVolumeUsed());
    setInteger(dto::setDnaSize, from.getDnaSize(), true);
    if (from.getCreationDate() != null) {
      dto.setCreationDate(formatDate(from.getCreationDate()));
    }
    dto.setIdentificationBarcode(from.getIdentificationBarcode());
    dto.setLocationLabel(BoxUtils.makeLocationLabel(from));
    if (from.getTargetedSequencing() != null) {
      dto.setTargetedSequencingId(from.getTargetedSequencing().getId());
    }
    if (from.getBox() != null) {
      dto.setBox(asDto(from.getBox(), includeBoxPositions));
      dto.setBoxPosition(from.getBoxPosition());
    }
    dto.setDiscarded(from.isDiscarded());
    setDateTimeString(dto::setLastModified, from.getLastModified());
    setId(dto::setDetailedQcStatusId, from.getDetailedQcStatus());
    setString(dto::setDetailedQcStatusNote, from.getDetailedQcStatusNote());
    setString(dto::setQcUserName, maybeGetProperty(from.getQcUser(), User::getFullName));
    setDateString(dto::setQcDate, from.getQcDate());
    setEffectiveQcFailure(from, dto);
    setId(dto::setKitDescriptorId, from.getKitDescriptor());
    setString(dto::setKitLot, from.getKitLot());

    Requisition requisition = from.getLibrary().getSample().getRequisition();
    if (requisition == null) {
      requisition = getParentRequisition(from.getLibrary().getSample());
    }
    setId(dto::setRequisitionId, requisition);
    setString(dto::setRequisitionAlias, maybeGetProperty(requisition, Requisition::getAlias));
    setId(dto::setRequisitionAssayId, maybeGetProperty(requisition, Requisition::getAssay));

    return dto;
  }

  private static DetailedLibraryAliquotDto asDetailedDto(DetailedLibraryAliquot from) {
    DetailedLibraryAliquotDto dto = new DetailedLibraryAliquotDto();
    setId(dto::setLibraryDesignCodeId, from.getLibraryDesignCode());
    setBoolean(dto::setNonStandardAlias, from.isNonStandardAlias(), false);
    GroupIdentifiable effective = from.getEffectiveGroupIdEntity();
    if (effective != null) {
      dto.setEffectiveGroupId(effective.getGroupId());
      dto.setEffectiveGroupIdSample(effective.getAlias());
    }
    setString(dto::setGroupId, from.getGroupId());
    setString(dto::setGroupDescription, from.getGroupDescription());
    return dto;
  }

  public static LibraryAliquotDto asDto(@Nonnull ListLibraryAliquotView from) {
    LibraryAliquotDto dto = null;
    if (from.getParentAttributes() != null) { // indicates detailed sample
      DetailedLibraryAliquotDto detailedDto = new DetailedLibraryAliquotDto();
      setId(detailedDto::setLibraryDesignCodeId, from.getDesignCode());
      if (from.getSubprojectId() != null) {
        detailedDto.setSubprojectAlias(from.getSubprojectAlias());
        detailedDto.setSubprojectPriority(from.getSubprojectPriority());
      }
      if (from.getIdentityAttributes() != null) {
        ParentIdentityAttributes identity = from.getIdentityAttributes();
        setString(detailedDto::setIdentityConsentLevel,
            maybeGetProperty(identity.getConsentLevel(), ConsentLevel::getLabel));
        setString(detailedDto::setEffectiveExternalNames, identity.getExternalName());
      }
      if (from.getTissueAttributes() != null) {
        ParentTissueAttributes tissue = from.getTissueAttributes();
        setString(detailedDto::setEffectiveTissueOriginAlias, tissue.getTissueOrigin().getAlias());
        setString(detailedDto::setEffectiveTissueOriginDescription, tissue.getTissueOrigin().getDescription());
        setString(detailedDto::setEffectiveTissueTypeAlias, tissue.getTissueType().getAlias());
        setString(detailedDto::setEffectiveTissueTypeDescription, tissue.getTissueType().getDescription());
      }
      dto = detailedDto;
    } else {
      dto = new LibraryAliquotDto();
    }
    dto.setId(from.getId());
    dto.setName(from.getName());
    setString(dto::setAlias, from.getAlias());
    setString(dto::setCreatorName, maybeGetProperty(from.getCreator(), User::getFullName));
    setString(dto::setConcentration, from.getConcentration());
    dto.setConcentrationUnits(from.getConcentrationUnits());
    dto.setLastModified(formatDateTime(from.getLastModified()));
    dto.setCreationDate(formatDate(from.getCreated()));
    dto.setIdentificationBarcode(from.getAliquotBarcode());
    dto.setLocationLabel(BoxUtils.makeLocationLabel(from));
    dto.setIndexIds(Stream.of(from.getParentLibrary().getIndex1(), from.getParentLibrary().getIndex2())
        .filter(Objects::nonNull)
        .map(Index::getId)
        .collect(Collectors.toList()));
    dto.setIndexLabels(Stream.of(from.getParentLibrary().getIndex1(), from.getParentLibrary().getIndex2())
        .filter(Objects::nonNull)
        .map(Index::getLabel)
        .collect(Collectors.toList()));
    dto.setTargetedSequencingId(from.getTargetedSequencingId());
    setInteger(dto::setDnaSize, from.getDnaSize(), true);
    setString(dto::setVolume, from.getAliquotVolume());
    dto.setVolumeUnits(from.getAliquotVolumeUnits());
    setString(dto::setNgUsed, from.getNgUsed());
    setString(dto::setVolumeUsed, from.getVolumeUsed());

    dto.setLibraryId(from.getLibraryId());
    dto.setLibraryName(from.getLibraryName());
    dto.setLibraryAlias(from.getLibraryAlias());
    dto.setLibraryLowQuality(from.isLibraryLowQuality());
    dto.setLibraryPlatformType(from.getPlatformType().getKey());
    dto.setSampleId(from.getSampleId());
    dto.setSampleName(from.getSampleName());
    dto.setSampleAlias(from.getSampleAlias());
    setLong(dto::setProjectId, from.getProjectId(), true);
    setString(dto::setProjectName, from.getProjectName());
    setString(dto::setProjectShortName, from.getProjectShortName());
    setString(dto::setSequencingControlTypeAlias,
        maybeGetProperty(from.getSampleSequencingControlType(), SequencingControlType::getAlias));
    setId(dto::setDetailedQcStatusId, from.getDetailedQcStatus());
    setString(dto::setDetailedQcStatusNote, from.getDetailedQcStatusNote());
    setString(dto::setQcUserName, maybeGetProperty(from.getQcUser(), User::getFullName));
    setDateString(dto::setQcDate, from.getQcDate());

    List<Long> parentAliquotIds = new ArrayList<>();
    for (ParentAliquot parent = from.getParentAliquot(); parent != null; parent = parent.getParentAliquot()) {
      parentAliquotIds.add(parent.getId());
    }
    dto.setParentAliquotIds(parentAliquotIds);
    setEffectiveQcFailure(from, dto);

    return dto;
  }

  public static LibraryAliquot to(@Nonnull LibraryAliquotDto from) {
    LibraryAliquot to = null;
    if (from instanceof DetailedLibraryAliquotDto) {
      to = toDetailed((DetailedLibraryAliquotDto) from);
    } else {
      to = new LibraryAliquot();
      setObject(to::setLibrary, LibraryImpl::new, from.getLibraryId());
      setObject(to::setParentAliquot, LibraryAliquot::new, from.getParentAliquotId());
    }
    setLong(to::setId, from.getId(), false);
    setString(to::setName, from.getName());
    setString(to::setAlias, from.getAlias());
    setString(to::setDescription, from.getDescription());
    setInteger(to::setDnaSize, from.getDnaSize(), true);
    to.setIdentificationBarcode(from.getIdentificationBarcode());
    setBigDecimal(to::setConcentration, from.getConcentration());
    to.setConcentrationUnits(from.getConcentrationUnits());
    setBigDecimal(to::setNgUsed, from.getNgUsed());
    setBigDecimal(to::setVolume, from.getVolume());
    to.setVolumeUnits(from.getVolumeUnits());
    setBigDecimal(to::setVolumeUsed, from.getVolumeUsed());
    to.setCreationDate(parseDate(from.getCreationDate()));
    if (from.getTargetedSequencingId() != null) {
      to.setTargetedSequencing(new TargetedSequencing());
      to.getTargetedSequencing().setId(from.getTargetedSequencingId());
    }
    to.setBoxPosition((LibraryAliquotBoxPosition) makeBoxablePosition(from, to));
    to.setDiscarded(from.isDiscarded());
    setObject(to::setDetailedQcStatus, DetailedQcStatusImpl::new, from.getDetailedQcStatusId());
    setString(to::setDetailedQcStatusNote, from.getDetailedQcStatusNote());
    setObject(to::setKitDescriptor, KitDescriptor::new, from.getKitDescriptorId());
    setString(to::setKitLot, from.getKitLot());
    return to;
  }

  private static DetailedLibraryAliquot toDetailed(DetailedLibraryAliquotDto from) {
    DetailedLibraryAliquot to = new DetailedLibraryAliquot();
    setBoolean(to::setNonStandardAlias, from.isNonStandardAlias(), false);
    setObject(to::setLibraryDesignCode, LibraryDesignCode::new, from.getLibraryDesignCodeId());
    setString(to::setGroupId, from.getGroupId());
    setString(to::setGroupDescription, from.getGroupDescription());
    setObject(to::setLibrary, DetailedLibraryImpl::new, from.getLibraryId());
    setObject(to::setParentAliquot, DetailedLibraryAliquot::new, from.getParentAliquotId());
    return to;
  }

  public static PoolDto asDto(@Nonnull Pool from, boolean includeContents, boolean includeBoxPositions,
      IndexChecker indexChecker) {
    PoolDto dto = new PoolDto();
    dto.setId(from.getId());
    dto.setName(from.getName());
    dto.setAlias(from.getAlias());
    dto.setDescription(from.getDescription());
    setString(dto::setConcentration, from.getConcentration());
    setInteger(dto::setDnaSize, from.getDnaSize(), true);
    dto.setConcentrationUnits(from.getConcentrationUnits());
    dto.setQcPassed(from.getQcPassed());
    dto.setCreationDate(formatDate(from.getCreationDate()));
    setString(dto::setVolume, from.getVolume());
    dto.setVolumeUnits(from.getVolumeUnits());
    if (from.getPlatformType() != null) {
      dto.setPlatformType(from.getPlatformType().name());
    }
    dto.setLongestIndex(from.getLongestIndex());
    dto.setLastModified(formatDateTime(from.getLastModified()));
    dto.setLibraryAliquotCount(from.getPoolContents().size());

    if (includeContents) {
      Set<LibraryAliquotDto> pooledElements = new HashSet<>();
      for (PoolElement element : from.getPoolContents()) {
        LibraryAliquotDto ldi = asDto(element.getAliquot());
        ldi.setProportion(element.getProportion());
        pooledElements.add(ldi);
      }
      dto.setPooledElements(pooledElements);
      if (indexChecker != null) {
        dto.setDuplicateIndicesSequences(indexChecker.getDuplicateIndicesSequences(from));
        dto.setDuplicateIndices(
            dto.getDuplicateIndicesSequences() != null && !dto.getDuplicateIndicesSequences().isEmpty());
        dto.setNearDuplicateIndicesSequences(indexChecker.getNearDuplicateIndicesSequences(from));
        dto.setNearDuplicateIndices(
            dto.getNearDuplicateIndicesSequences() != null && !dto.getNearDuplicateIndicesSequences().isEmpty());
      }
    } else {
      dto.setPooledElements(Collections.emptySet());
      if (indexChecker != null) {
        dto.setDuplicateIndices(
            indexChecker.getDuplicateIndicesSequences(from) != null
                && !indexChecker.getDuplicateIndicesSequences(from).isEmpty());
        dto.setNearDuplicateIndices(
            indexChecker.getNearDuplicateIndicesSequences(from) != null
                && !indexChecker.getNearDuplicateIndicesSequences(from).isEmpty());
      }
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

  public static PoolDto asDto(@Nonnull ListPoolView from, IndexChecker indexChecker) {
    PoolDto to = new PoolDto();
    setLong(to::setId, from.getId(), true);
    setString(to::setName, from.getName());
    setString(to::setAlias, from.getAlias());
    setString(to::setIdentificationBarcode, from.getIdentificationBarcode());
    setString(to::setDescription, from.getDescription());
    setDateString(to::setCreationDate, from.getCreationTime());
    setString(to::setConcentration, from.getConcentration());
    to.setConcentrationUnits(from.getConcentrationUnits());
    setInteger(to::setDnaSize, from.getDnaSize(), true);
    to.setPlatformType(from.getPlatformType().name());
    if (from.getBoxId() != null) {
      to.setBox(new BoxDto());
      to.getBox().setId(from.getBoxId());
    }
    to.setLocationLabel(BoxUtils.makeLocationLabel(from.isDiscarded(), from.isDistributed(), null, from.getBoxAlias(),
        from.getBoxPosition(), from.getBoxLocationBarcode()));
    to.setLibraryAliquotCount(from.getElements().size());
    setDateTimeString(to::setLastModified, from.getLastModified());
    to.setDuplicateIndices(
        indexChecker.getDuplicateIndicesSequences(from) != null
            && !indexChecker.getDuplicateIndicesSequences(from).isEmpty());
    to.setNearDuplicateIndices(
        indexChecker.getNearDuplicateIndicesSequences(from) != null
            && !indexChecker.getNearDuplicateIndicesSequences(from).isEmpty());
    to.setHasEmptySequence(from.getElements().stream().anyMatch(element -> element.getIndex1() == null));
    to.setPrioritySubprojectAliases(from.getPrioritySubprojectAliases());
    to.setPooledElements(from.getElements().stream()
        .map(element -> {
          DetailedLibraryAliquotDto dto = new DetailedLibraryAliquotDto();
          setString(dto::setName, element.getName());
          setString(dto::setAlias, element.getAlias());
          if (element.getConsentLevel() != null) {
            dto.setIdentityConsentLevel(element.getConsentLevel().getLabel());
          }
          return dto;
        })
        .collect(Collectors.toSet()));
    to.setHasLowQualityLibraries(from.hasLowQualityMembers());
    setString(to::setLongestIndex, from.getLongestIndex());
    return to;
  }

  public static RunDto asDto(@Nonnull Run from) {
    return asDto(from, false, false, false);
  }

  private static RunDto asDto(@Nonnull Run from, boolean includeContainers, boolean includeContainerPartitions,
      boolean includePoolContents) {
    RunDto dto = getPlatformRunDto(from);
    setLong(dto::setId, from.getId(), true);
    setString(dto::setName, from.getName());
    setString(dto::setAlias, from.getAlias());
    setString(dto::setDescription, from.getDescription());
    if (from.getHealth() != null) {
      dto.setStatus(from.getHealth().getKey());
    }
    dto.setLastModified(formatDateTime(from.getLastModified()));
    setString(dto::setAccession, from.getAccession());
    if (from.getSequencer() != null) {
      dto.setPlatformType(from.getSequencer().getInstrumentModel().getPlatformType().getKey());
      dto.setInstrumentId(from.getSequencer().getId());
      dto.setInstrumentName(from.getSequencer().getName());
      if (from.getSequencer().getInstrumentModel() != null) {
        dto.setInstrumentModelId(from.getSequencer().getInstrumentModel().getId());
        dto.setInstrumentModelAlias(from.getSequencer().getInstrumentModel().getAlias());
      }
    }
    if (from.getStartDate() != null) {
      dto.setStartDate(formatDate(from.getStartDate()));
    }
    if (from.getCompletionDate() != null) {
      dto.setEndDate(formatDate(from.getCompletionDate()));
    }
    if (from.getSequencingParameters() != null) {
      dto.setSequencingParametersId(from.getSequencingParameters().getId());
      dto.setSequencingParametersName(from.getSequencingParameters().getName());
    }
    setId(dto::setSequencingKitId, from.getSequencingKit());
    setString(dto::setSequencingKitLot, from.getSequencingKitLot());
    dto.setProgress(from.getProgress());
    dto.setRunPath(from.getFilePath());

    if (includeContainers) {
      dto.setContainers(
          asContainerDtos(from.getSequencerPartitionContainers(), includeContainerPartitions, includePoolContents));
    }

    setBoolean(dto::setQcPassed, from.getQcPassed(), true);
    setString(dto::setQcUserName, maybeGetProperty(from.getQcUser(), User::getFullName));
    setDateString(dto::setQcDate, from.getQcDate());
    setBoolean(dto::setDataReview, from.getDataReview(), true);
    setString(dto::setDataReviewer, maybeGetProperty(from.getDataReviewer(), User::getFullName));
    setDateString(dto::setDataReviewDate, from.getDataReviewDate());
    setId(dto::setSopId, from.getSop());
    setString(dto::setDataManglingPolicy,
        maybeGetProperty(from.getDataManglingPolicy(), InstrumentDataManglingPolicy::name));

    dto.setProjectsLabel(from.getProjectsLabel());

    return dto;
  }

  private static RunDto getPlatformRunDto(@Nonnull Run from) {
    if (from instanceof IlluminaRun) {
      IlluminaRunDto dto = new IlluminaRunDto();
      IlluminaRun illuminaRun = (IlluminaRun) from;
      setString(dto::setWorkflowType,
          maybeGetProperty(illuminaRun.getWorkflowType(), IlluminaWorkflowType::getRawValue));
      dto.setNumCycles(illuminaRun.getNumCycles());
      dto.setCalledCycles(illuminaRun.getCallCycle());
      dto.setImagedCycles(illuminaRun.getImgCycle());
      dto.setScoredCycles(illuminaRun.getScoreCycle());
      dto.setPairedEnd(illuminaRun.getPairedEnd());
      setString(dto::setBasesMask, illuminaRun.getRunBasesMask());
      return dto;
    } else if (from instanceof IonTorrentRun) {
      return new IonTorrentRunDto();
    } else if (from instanceof LS454Run) {
      Ls454RunDto dto = new Ls454RunDto();
      LS454Run ls454Run = (LS454Run) from;
      dto.setCycles(ls454Run.getCycles());
      dto.setPairedEnd(ls454Run.getPairedEnd());
      return dto;
    } else if (from instanceof SolidRun) {
      SolidRunDto dto = new SolidRunDto();
      SolidRun solidRun = (SolidRun) from;
      dto.setPairedEnd(solidRun.getPairedEnd());
      return dto;
    } else if (from instanceof OxfordNanoporeRun) {
      OxfordNanoporeRunDto dto = new OxfordNanoporeRunDto();
      OxfordNanoporeRun ontRun = (OxfordNanoporeRun) from;
      setString(dto::setMinKnowVersion, ontRun.getMinKnowVersion());
      setString(dto::setProtocolVersion, ontRun.getProtocolVersion());
      return dto;
    } else if (from instanceof PacBioRun) {
      return new PacBioRunDto();
    } else {
      throw new IllegalArgumentException("Unknown run type");
    }
  }

  public static List<RunDto> asRunDtos(Collection<Run> runSubset) {
    return runSubset.stream().map(Dtos::asDto).collect(Collectors.toList());
  }

  public static Run to(@Nonnull RunDto dto) {
    Run to = getPlatformRun(dto);
    setLong(to::setId, dto.getId(), false);
    setString(to::setName, dto.getName());
    setString(to::setAlias, dto.getAlias());
    setString(to::setDescription, dto.getDescription());
    setObject(to::setHealth, dto.getStatus(), status -> HealthType.get(status));
    setString(to::setAccession, dto.getAccession());
    setObject(to::setSequencer, InstrumentImpl::new, dto.getInstrumentId());
    setDate(to::setStartDate, dto.getStartDate());
    setDate(to::setCompletionDate, dto.getEndDate());
    setObject(to::setSequencingParameters, SequencingParameters::new, dto.getSequencingParametersId());
    setObject(to::setSequencingKit, KitDescriptor::new, dto.getSequencingKitId());
    setString(to::setSequencingKitLot, dto.getSequencingKitLot());
    setString(to::setFilePath, dto.getRunPath());
    setBoolean(to::setQcPassed, dto.getQcPassed(), true);
    setBoolean(to::setDataReview, dto.getDataReview(), true);
    setObject(to::setSop, Sop::new, dto.getSopId());
    setObject(to::setDataManglingPolicy, dto.getDataManglingPolicy(), InstrumentDataManglingPolicy::valueOf);
    return to;
  }

  private static Run getPlatformRun(RunDto from) {
    if (from instanceof IlluminaRunDto) {
      IlluminaRun run = new IlluminaRun();
      IlluminaRunDto illuminaDto = (IlluminaRunDto) from;
      setObject(run::setWorkflowType, illuminaDto.getWorkflowType(), wf -> IlluminaWorkflowType.get(wf));
      run.setNumCycles(illuminaDto.getNumCycles());
      run.setCallCycle(illuminaDto.getCalledCycles());
      run.setImgCycle(illuminaDto.getImagedCycles());
      run.setScoreCycle(illuminaDto.getScoredCycles());
      run.setPairedEnd(illuminaDto.getPairedEnd());
      setString(run::setRunBasesMask, illuminaDto.getBasesMask());
      return run;
    } else if (from instanceof IonTorrentRunDto) {
      return new IonTorrentRun();
    } else if (from instanceof Ls454RunDto) {
      LS454Run run = new LS454Run();
      Ls454RunDto ls454Dto = (Ls454RunDto) from;
      run.setCycles(ls454Dto.getCycles());
      run.setPairedEnd(ls454Dto.getPairedEnd());
      return run;
    } else if (from instanceof SolidRunDto) {
      SolidRun run = new SolidRun();
      SolidRunDto solidDto = (SolidRunDto) from;
      run.setPairedEnd(solidDto.getPairedEnd());
      return run;
    } else if (from instanceof OxfordNanoporeRunDto) {
      OxfordNanoporeRun run = new OxfordNanoporeRun();
      OxfordNanoporeRunDto ontDto = (OxfordNanoporeRunDto) from;
      setString(run::setMinKnowVersion, ontDto.getMinKnowVersion());
      setString(run::setProtocolVersion, ontDto.getProtocolVersion());
      return run;
    } else if (from instanceof PacBioRunDto) {
      return new PacBioRun();
    } else {
      throw new IllegalArgumentException("Unknown run type");
    }
  }

  public static ContainerDto asDto(@Nonnull SequencerPartitionContainer from, IndexChecker indexChecker) {
    return asDto(from, false, false, indexChecker);
  }

  public static ContainerDto asDto(@Nonnull SequencerPartitionContainer from, boolean includePartitions,
      boolean includePoolContents,
      IndexChecker indexChecker) {
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
      dto.setLastRunInstrumentModelId(lastRun.getSequencer().getInstrumentModel().getId());
      dto.setLastSequencerId(lastRun.getSequencer().getId());
      dto.setLastSequencerName(lastRun.getSequencer().getName());
    }
    if (from.getLastModified() != null) {
      dto.setLastModified(formatDateTime(from.getLastModified()));
    }
    if (from.getClusteringKit() != null) {
      dto.setClusteringKitId(from.getClusteringKit().getId());
    }
    setString(dto::setClusteringKitLot, from.getClusteringKitLot());
    if (from.getMultiplexingKit() != null) {
      dto.setMultiplexingKitId(from.getMultiplexingKit().getId());
    }
    setString(dto::setMultiplexingKitLot, from.getMultiplexingKitLot());

    if (includePartitions) {
      dto.setPartitions(asPartitionDtos(from.getPartitions(), includePoolContents, indexChecker));
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
    setObject(to::setModel, SequencingContainerModel::new,
        maybeGetProperty(from.getModel(), SequencingContainerModelDto::getId));
    setString(to::setDescription, from.getDescription());
    setObject(to::setClusteringKit, KitDescriptor::new, from.getClusteringKitId());
    setString(to::setClusteringKitLot, from.getClusteringKitLot());
    setObject(to::setMultiplexingKit, KitDescriptor::new, from.getMultiplexingKitId());
    setString(to::setMultiplexingKitLot, from.getMultiplexingKitLot());

    // Note: partitions not included

    return to;
  }

  public static RunPositionDto asDto(@Nonnull RunPosition from) {
    RunPositionDto dto = new RunPositionDto();
    setId(dto::setPositionId, from.getPosition());
    setString(dto::setPositionAlias, maybeGetProperty(from.getPosition(), InstrumentPosition::getAlias));
    setId(dto::setId, from.getContainer());
    setString(dto::setIdentificationBarcode,
        maybeGetProperty(from.getContainer(), SequencerPartitionContainer::getIdentificationBarcode));
    setObject(dto::setContainerModel, from.getContainer().getModel(), Dtos::asDto);
    setDateTimeString(dto::setLastModified,
        maybeGetProperty(from.getContainer(), SequencerPartitionContainer::getLastModified));
    return dto;
  }

  public static SequencingContainerModelDto asDto(@Nonnull SequencingContainerModel from) {
    SequencingContainerModelDto dto = new SequencingContainerModelDto();
    dto.setId(from.getId());
    dto.setAlias(from.getAlias());
    dto.setIdentificationBarcode(from.getIdentificationBarcode());
    dto.setPlatformType(from.getPlatformType().name());
    dto.setInstrumentModelIds(
        from.getInstrumentModels().stream().map(InstrumentModel::getId).collect(Collectors.toList()));
    dto.setPartitionCount(from.getPartitionCount());
    dto.setFallback(from.isFallback());
    dto.setArchived(from.isArchived());
    return dto;
  }

  public static SequencingContainerModel to(@Nonnull SequencingContainerModelDto from) {
    SequencingContainerModel to = new SequencingContainerModel();
    setLong(to::setId, from.getId(), false);
    setString(to::setAlias, from.getAlias());
    setString(to::setIdentificationBarcode, from.getIdentificationBarcode());
    setObject(to::setPlatformType, from.getPlatformType(), str -> PlatformType.valueOf(str));
    setObject(to::setInstrumentModels, from.getInstrumentModelIds(), ids -> ids.stream()
        .map(id -> {
          InstrumentModel model = new InstrumentModel();
          model.setId(id);
          return model;
        })
        .collect(Collectors.toList()));
    setInteger(to::setPartitionCount, from.getPartitionCount(), false);
    setBoolean(to::setFallback, from.getFallback(), false);
    setBoolean(to::setArchived, from.getArchived(), false);
    return to;
  }

  public static List<PartitionDto> asPartitionDtos(@Nonnull Collection<Partition> partitionSubset,
      boolean includePoolContents,
      IndexChecker indexChecker) {
    List<PartitionDto> dtoList = new ArrayList<>();
    for (Partition partition : partitionSubset) {
      dtoList.add(asDto(partition, includePoolContents, indexChecker));
    }
    return dtoList;
  }

  public static List<SequencingContainerModelDto> asDtos(@Nonnull Collection<SequencingContainerModel> models) {
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
    setString(dto::setUnits, from.getUnits());
    dto.setPrecisionAfterDecimal(from.getPrecisionAfterDecimal());
    dto.setCorrespondingField(from.getCorrespondingField());
    dto.setAutoUpdateField(from.isAutoUpdateField());
    dto.setArchived(from.isArchived());
    setId(dto::setInstrumentModelId, from.getInstrumentModel());
    dto.setKitDescriptors(from.getKitDescriptors().stream().map(Dtos::asDto).collect(Collectors.toList()));
    dto.setControls(from.getControls().stream().map(Dtos::asDto).collect(Collectors.toList()));
    return dto;
  }

  public static QcType to(@Nonnull QcTypeDto from) {
    QcType to = new QcType();
    if (from.getId() != null)
      to.setId(from.getId());
    to.setName(from.getName());
    to.setDescription(from.getDescription());
    to.setQcTarget(from.getQcTarget());
    to.setUnits(from.getUnits());
    to.setPrecisionAfterDecimal(from.getPrecisionAfterDecimal());
    to.setArchived(from.isArchived());
    to.setCorrespondingField(from.getCorrespondingField());
    to.setAutoUpdateField(from.isAutoUpdateField());
    setObject(to::setInstrumentModel, InstrumentModel::new, from.getInstrumentModelId());
    if (from.getKitDescriptors() != null) {
      to.getKitDescriptors().addAll(from.getKitDescriptors().stream().map(Dtos::to).collect(Collectors.toSet()));
    }
    if (from.getControls() != null) {
      to.getControls().addAll(from.getControls().stream().map(Dtos::to).collect(Collectors.toSet()));
    }
    return to;
  }

  private static QcControlDto asDto(@Nonnull QcControl from) {
    QcControlDto to = new QcControlDto();
    setLong(to::setId, from.getId(), true);
    setString(to::setAlias, from.getAlias());
    return to;
  }

  private static QcControl to(@Nonnull QcControlDto from) {
    QcControl to = new QcControl();
    setLong(to::setId, from.getId(), false);
    setString(to::setAlias, from.getAlias());
    return to;
  }

  public static QcDto asDto(@Nonnull QC from) {
    QcDto dto = new QcDto();
    dto.setId(from.getId());
    dto.setDate(formatDate(from.getDate()));
    dto.setCreator(from.getCreator().getFullName());
    setId(dto::setQcTypeId, from.getType());
    setString(dto::setResults, from.getResults());
    dto.setEntityId(from.getEntity().getId());
    dto.setEntityAlias(from.getEntity().getAlias());
    dto.setDescription(from.getDescription());
    setId(dto::setInstrumentId, from.getInstrument());
    setId(dto::setKitDescriptorId, from.getKit());
    setString(dto::setKitLot, from.getKitLot());
    dto.setControls(from.getControls().stream().map(Dtos::asDto).collect(Collectors.toList()));
    return dto;
  }

  private static List<QcDto> asQcDtos(@Nonnull Collection<? extends QC> qcSubset) {
    return qcSubset.stream().map(Dtos::asDto).collect(Collectors.toList());
  }

  public static QC to(@Nonnull QcDto dto) {
    QC to;
    switch (dto.getQcTarget()) {
      case "Library":
        LibraryQC newLibraryQc = new LibraryQC();
        Library ownerLibrary = new LibraryImpl();
        ownerLibrary.setId(dto.getEntityId());
        newLibraryQc.setLibrary(ownerLibrary);
        to = newLibraryQc;
        break;
      case "Sample":
        SampleQC newSampleQc = new SampleQC();
        Sample ownerSample = new SampleImpl();
        ownerSample.setId(dto.getEntityId());
        newSampleQc.setSample(ownerSample);
        to = newSampleQc;
        break;
      case "Pool":
        PoolQC newPoolQc = new PoolQC();
        Pool ownerPool = new PoolImpl();
        ownerPool.setId(dto.getEntityId());
        newPoolQc.setPool(ownerPool);
        to = newPoolQc;
        break;
      case "Container":
        ContainerQC newContainerQc = new ContainerQC();
        SequencerPartitionContainer ownerContainer = new SequencerPartitionContainerImpl();
        ownerContainer.setId(dto.getEntityId());
        newContainerQc.setContainer(ownerContainer);
        to = newContainerQc;
        break;
      case "Requisition":
        RequisitionQC newRequisitionQc = new RequisitionQC();
        Requisition ownerRequisition = new Requisition();
        ownerRequisition.setId(dto.getEntityId());
        newRequisitionQc.setRequisition(ownerRequisition);
        to = newRequisitionQc;
        break;
      default:
        throw new IllegalArgumentException("No such QC target: " + dto.getQcTarget());
    }
    if (dto.getId() != null) {
      to.setId(dto.getId());
    }
    to.setDate(parseDate(dto.getDate()));
    setBigDecimal(to::setResults, dto.getResults());
    setObject(to::setType, QcType::new, dto.getQcTypeId());
    to.setDescription(dto.getDescription());
    setObject(to::setInstrument, InstrumentImpl::new, dto.getInstrumentId());
    setObject(to::setKit, KitDescriptor::new, dto.getKitDescriptorId());
    setString(to::setKitLot, dto.getKitLot());
    addQcControlRuns(dto.getControls(), to, QcTarget.valueOf(dto.getQcTarget()));
    return to;
  }

  private static QcControlRunDto asDto(@Nonnull QcControlRun from) {
    QcControlRunDto to = new QcControlRunDto();
    setLong(to::setId, from.getId(), true);
    setId(to::setControlId, from.getControl());
    setString(to::setLot, from.getLot());
    setBoolean(to::setQcPassed, from.isQcPassed(), true);
    return to;
  }

  private static void addQcControlRuns(@Nonnull Collection<QcControlRunDto> list, QC qc, QcTarget qcTarget) {
    if (list == null) {
      return;
    }

    for (QcControlRunDto from : list) {
      QcControlRun to = null;

      switch (qcTarget) {
        case Container:
          ContainerQcControlRun containerQcControlRun = new ContainerQcControlRun();
          containerQcControlRun.setQc((ContainerQC) qc);
          ((ContainerQC) qc).getControls().add(containerQcControlRun);
          to = containerQcControlRun;
          break;
        case Library:
          LibraryQcControlRun libraryQcControlRun = new LibraryQcControlRun();
          libraryQcControlRun.setQc((LibraryQC) qc);
          ((LibraryQC) qc).getControls().add(libraryQcControlRun);
          to = libraryQcControlRun;
          break;
        case Pool:
          PoolQcControlRun poolQcControlRun = new PoolQcControlRun();
          poolQcControlRun.setQc((PoolQC) qc);
          ((PoolQC) qc).getControls().add(poolQcControlRun);
          to = poolQcControlRun;
          break;
        case Run:
          throw new IllegalArgumentException("Unhandled QC target: Run");
        case Sample:
          SampleQcControlRun sampleQcControlRun = new SampleQcControlRun();
          sampleQcControlRun.setQc((SampleQC) qc);
          ((SampleQC) qc).getControls().add(sampleQcControlRun);
          to = sampleQcControlRun;
          break;
        default:
          throw new IllegalArgumentException(
              "Unhandled QC target: " + qcTarget == null ? "null" : qcTarget.getLabel());
      }
      setLong(to::setId, from.getId(), false);
      setObject(to::setControl, QcControl::new, from.getControlId());
      setString(to::setLot, from.getLot());
      setBoolean(to::setQcPassed, from.getQcPassed(), false);
    }
  }

  public static List<QcTypeDto> asQcTypeDtos(@Nonnull Collection<QcType> qcTypeSubset) {
    return qcTypeSubset.stream().map(Dtos::asDto).collect(Collectors.toList());
  }

  public static SequencingOrderCompletionDto asDto(@Nonnull SequencingOrderSummaryView from,
      IndexChecker indexChecker) {
    SequencingOrderCompletionDto dto = new SequencingOrderCompletionDto();
    setString(dto::setId, from.getId());
    dto.setPool(asDto(from.getPool(), indexChecker));
    setString(dto::setContainerModelAlias,
        maybeGetProperty(from.getContainerModel(), SequencingContainerModel::getAlias));
    dto.setParameters(asDto(from.getParameters()));
    setDateTimeString(dto::setLastUpdated, from.getLastUpdated());
    dto.setRemaining(from.getRemaining());
    dto.setCompleted(from.get(HealthType.Completed));
    dto.setFailed(from.get(HealthType.Failed));
    dto.setRequested(from.getRequested());
    dto.setRunning(from.get(HealthType.Running));
    dto.setStarted(from.get(HealthType.Started));
    dto.setStopped(from.get(HealthType.Stopped));
    dto.setUnknown(from.get(HealthType.Unknown));
    dto.setLoaded(from.getLoaded());
    dto.setDescription(from.getDescription());
    setString(dto::setPurpose, from.getPurpose());
    return dto;
  }

  public static InstrumentModelDto asDto(@Nonnull InstrumentModel from) {
    InstrumentModelDto dto = new InstrumentModelDto();
    dto.setId(from.getId());
    dto.setAlias(from.getAlias());
    dto.setDescription(from.getDescription());
    setInteger(dto::setNumContainers, from.getNumContainers(), true);
    setString(dto::setPlatformType, maybeGetProperty(from.getPlatformType(), PlatformType::name));
    setString(dto::setInstrumentType, maybeGetProperty(from.getInstrumentType(), InstrumentType::name));
    setObject(dto::setPositions, from.getPositions(), positions -> positions.stream()
        .map(Dtos::asDto)
        .collect(Collectors.toList()));
    setObject(dto::setContainerModels, from.getContainerModels(), containerModels -> containerModels.stream()
        .map(Dtos::asDto)
        .collect(Collectors.toList()));
    setString(dto::setDataManglingPolicy,
        maybeGetProperty(from.getDataManglingPolicy(), InstrumentDataManglingPolicy::name));
    return dto;
  }

  public static InstrumentModel to(@Nonnull InstrumentModelDto from) {
    InstrumentModel to = new InstrumentModel();
    to.setId(from.getId());
    to.setAlias(from.getAlias());
    to.setDescription(from.getDescription());
    setInteger(to::setNumContainers, from.getNumContainers(), false);
    setObject(to::setPlatformType, from.getPlatformType(), str -> PlatformType.valueOf(str));
    setObject(to::setInstrumentType, from.getInstrumentType(), str -> InstrumentType.valueOf(str));
    if (from.getPositions() != null) {
      to.getPositions().addAll(from.getPositions().stream()
          .map(Dtos::to)
          .collect(Collectors.toList()));
    }
    if (from.getContainerModels() != null) {
      to.getContainerModels().addAll(from.getContainerModels().stream()
          .map(Dtos::to)
          .collect(Collectors.toSet()));
    }
    setObject(to::setDataManglingPolicy, from.getDataManglingPolicy(),
        str -> InstrumentDataManglingPolicy.valueOf(str));
    return to;
  }

  public static InstrumentPositionDto asDto(@Nonnull InstrumentPosition from) {
    InstrumentPositionDto to = new InstrumentPositionDto();
    setLong(to::setId, from.getId(), true);
    setString(to::setAlias, from.getAlias());
    return to;
  }

  public static InstrumentPosition to(@Nonnull InstrumentPositionDto from) {
    InstrumentPosition to = new InstrumentPosition();
    setLong(to::setId, from.getId(), false);
    setString(to::setAlias, from.getAlias());
    return to;
  }

  public static ProjectDto asDto(@Nonnull Project from) {
    ProjectDto dto = new ProjectDto();
    dto.setId(from.getId());
    dto.setName(from.getName());
    setDateString(dto::setCreationDate, from.getCreationTime());
    dto.setAlias(from.getAlias());
    dto.setShortName(from.getShortName());
    dto.setDescription(from.getDescription());
    setObject(dto::setStatus, from.getStatus(), (progress) -> progress.getKey());
    if (from.getReferenceGenome() != null) {
      dto.setReferenceGenomeId(from.getReferenceGenome().getId());
      setString(dto::setDefaultSciName, maybeGetProperty(from.getReferenceGenome().getDefaultScientificName(),
          ScientificName::getAlias));
    }
    setId(dto::setDefaultTargetedSequencingId, from.getDefaultTargetedSequencing());
    setId(dto::setPipelineId, from.getPipeline());
    setBoolean(dto::setSecondaryNaming, from.isSecondaryNaming(), false);
    setString(dto::setRebNumber, from.getRebNumber());
    setDateString(dto::setRebExpiry, from.getRebExpiry());
    setInteger(dto::setSamplesExpected, from.getSamplesExpected(), true);
    setId(dto::setContactId, from.getContact());
    setString(dto::setContactName, maybeGetProperty(from.getContact(), Contact::getName));
    setString(dto::setContactEmail, maybeGetProperty(from.getContact(), Contact::getEmail));
    return dto;
  }

  public static List<ProjectDto> asProjectDtos(@Nonnull Collection<Project> from) {
    return from.stream().map(Dtos::asDto).collect(Collectors.toList());
  }

  public static Project to(@Nonnull ProjectDto dto) {
    Project to = new ProjectImpl();
    setLong(to::setId, dto.getId(), false);
    setString(to::setName, dto.getName());
    setDate(to::setCreationTime, dto.getCreationDate());
    setString(to::setAlias, dto.getAlias());
    setString(to::setShortName, dto.getShortName());
    setString(to::setDescription, dto.getDescription());
    setObject(to::setStatus, dto.getStatus(), (key) -> StatusType.get(key));
    setObject(to::setReferenceGenome, ReferenceGenomeImpl::new, dto.getReferenceGenomeId());
    setObject(to::setDefaultTargetedSequencing, TargetedSequencing::new, dto.getDefaultTargetedSequencingId());
    setObject(to::setPipeline, Pipeline::new, dto.getPipelineId());
    setBoolean(to::setSecondaryNaming, dto.isSecondaryNaming(), false);
    setString(to::setRebNumber, dto.getRebNumber());
    setDate(to::setRebExpiry, dto.getRebExpiry());
    setInteger(to::setSamplesExpected, dto.getSamplesExpected(), true);
    if (dto.getContactId() != null || !isStringEmptyOrNull(dto.getContactName())
        || !isStringEmptyOrNull(dto.getContactEmail())) {
      Contact contact = new Contact();
      setLong(contact::setId, dto.getContactId(), false);
      setString(contact::setName, dto.getContactName());
      setString(contact::setEmail, dto.getContactEmail());
      to.setContact(contact);
    }
    return to;
  }

  public static LibraryDesignDto asDto(@Nonnull LibraryDesign from) {
    LibraryDesignDto to = new LibraryDesignDto();
    setLong(to::setId, from.getId(), true);
    setString(to::setName, from.getName());
    setId(to::setDesignCodeId, from.getLibraryDesignCode());
    setString(to::setDesignCodeLabel, maybeGetProperty(from.getLibraryDesignCode(), LibraryDesignCode::getCode));
    setId(to::setSampleClassId, from.getSampleClass());
    setString(to::setSampleClassAlias, maybeGetProperty(from.getSampleClass(), SampleClass::getAlias));
    setId(to::setSelectionId, from.getLibrarySelectionType());
    setString(to::setSelectionName, maybeGetProperty(from.getLibrarySelectionType(), LibrarySelectionType::getName));
    setId(to::setStrategyId, from.getLibraryStrategyType());
    setString(to::setStrategyName, maybeGetProperty(from.getLibraryStrategyType(), LibraryStrategyType::getName));
    return to;
  }

  public static LibraryDesign to(@Nonnull LibraryDesignDto from) {
    LibraryDesign to = new LibraryDesign();
    setLong(to::setId, from.getId(), false);
    setString(to::setName, from.getName());
    setObject(to::setSampleClass, SampleClassImpl::new, from.getSampleClassId());
    setObject(to::setLibraryDesignCode, LibraryDesignCode::new, from.getDesignCodeId());
    setObject(to::setLibrarySelectionType, LibrarySelectionType::new, from.getSelectionId());
    setObject(to::setLibraryStrategyType, LibraryStrategyType::new, from.getStrategyId());
    return to;
  }

  public static LibraryTypeDto asDto(@Nonnull LibraryType from) {
    LibraryTypeDto to = new LibraryTypeDto();
    setLong(to::setId, from.getId(), true);
    setString(to::setDescription, from.getDescription());
    setString(to::setPlatform, maybeGetProperty(from.getPlatformType(), PlatformType::name));
    setString(to::setAbbreviation, from.getAbbreviation());
    setBoolean(to::setArchived, from.getArchived(), false);
    return to;
  }

  public static LibraryType to(@Nonnull LibraryTypeDto from) {
    LibraryType to = new LibraryType();
    setLong(to::setId, from.getId(), false);
    setString(to::setDescription, from.getDescription());
    setObject(to::setPlatformType, from.getPlatform(), str -> PlatformType.valueOf(str));
    setString(to::setAbbreviation, from.getAbbreviation());
    setBoolean(to::setArchived, from.isArchived(), false);
    return to;
  }

  public static LibrarySelectionTypeDto asDto(@Nonnull LibrarySelectionType from) {
    LibrarySelectionTypeDto dto = new LibrarySelectionTypeDto();
    setLong(dto::setId, from.getId(), true);
    setString(dto::setName, from.getName());
    setString(dto::setDescription, from.getDescription());
    return dto;
  }

  public static LibrarySelectionType to(@Nonnull LibrarySelectionTypeDto from) {
    LibrarySelectionType to = new LibrarySelectionType();
    setLong(to::setId, from.getId(), false);
    setString(to::setName, from.getName());
    setString(to::setDescription, from.getDescription());
    return to;
  }

  public static LibraryStrategyTypeDto asDto(@Nonnull LibraryStrategyType from) {
    LibraryStrategyTypeDto dto = new LibraryStrategyTypeDto();
    setLong(dto::setId, from.getId(), true);
    setString(dto::setName, from.getName());
    setString(dto::setDescription, from.getDescription());
    return dto;
  }

  public static LibraryStrategyType to(@Nonnull LibraryStrategyTypeDto from) {
    LibraryStrategyType to = new LibraryStrategyType();
    setLong(to::setId, from.getId(), false);
    setString(to::setName, from.getName());
    setString(to::setDescription, from.getDescription());
    return to;
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
    dto.setRealSequences(from.getRealSequences() == null ? Collections.emptySet() : from.getRealSequences());
    if (includeFamily) {
      dto.setFamily(asDto(from.getFamily(), false));
    }
    return dto;
  }

  public static Index to(@Nonnull IndexDto from) {
    Index to = new Index();
    setObject(to::setFamily, IndexFamily::new, maybeGetProperty(from.getFamily(), IndexFamilyDto::getId));
    setLong(to::setId, from.getId(), false);
    setString(to::setName, from.getName());
    setString(to::setSequence, from.getSequence());
    setInteger(to::setPosition, from.getPosition(), false);
    to.setRealSequences(from.getRealSequences());
    return to;
  }

  public static IndexFamilyDto asDto(@Nonnull IndexFamily from) {
    return asDto(from, true);
  }

  private static IndexFamilyDto asDto(@Nonnull IndexFamily from, boolean includeChildren) {
    IndexFamilyDto dto = new IndexFamilyDto();
    setLong(dto::setId, from.getId(), true);
    setString(dto::setPlatformType, maybeGetProperty(from.getPlatformType(), PlatformType::name));
    setString(dto::setName, from.getName());
    setBoolean(dto::setFakeSequence, from.hasFakeSequence(), false);
    setBoolean(dto::setUniqueDualIndex, from.isUniqueDualIndex(), false);
    setBoolean(dto::setArchived, from.getArchived(), false);
    if (includeChildren) {
      dto.setIndices(from.getIndices().stream().map(x -> asDto(x, true)).collect(Collectors.toList()));
    }
    return dto;
  }

  public static IndexFamily to(@Nonnull IndexFamilyDto from) {
    IndexFamily to = new IndexFamily();
    setLong(to::setId, from.getId(), false);
    setObject(to::setPlatformType, from.getPlatformType(), pt -> PlatformType.valueOf(pt));
    setString(to::setName, from.getName());
    setBoolean(to::setFake, from.getFakeSequence(), false);
    setBoolean(to::setUniqueDualIndex, from.isUniqueDualIndex(), false);
    setBoolean(to::setArchived, from.isArchived(), true);
    if (from.getIndices() != null) {
      to.getIndices().addAll(from.getIndices().stream().map(indexDto -> {
        Index index = Dtos.to(indexDto);
        index.setFamily(to);
        return index;
      }).collect(Collectors.toList()));
    }
    return to;
  }

  public static StainDto asDto(@Nonnull Stain from) {
    StainDto dto = new StainDto();
    setLong(dto::setId, from.getId(), true);
    setString(dto::setName, from.getName());
    setId(dto::setCategoryId, from.getCategory());
    setString(dto::setCategoryName, maybeGetProperty(from.getCategory(), StainCategory::getName));
    return dto;
  }

  public static Stain to(@Nonnull StainDto from) {
    Stain to = new Stain();
    setLong(to::setId, from.getId(), false);
    setString(to::setName, from.getName());
    setObject(to::setCategory, StainCategory::new, from.getCategoryId());
    return to;
  }

  public static Run to(@Nonnull NotificationDto from) {
    final Run to = getMisoPlatformTypeFromRunscanner(from.getPlatformType()).createRun();
    setCommonRunValues(from, to);

    switch (to.getPlatformType()) {
      case PACBIO:
        break;
      case OXFORDNANOPORE:
        setOxfordNanoporeRunValues((OxfordNanoporeNotificationDto) from, (OxfordNanoporeRun) to);
        break;
      case ILLUMINA:
        setIlluminaRunValues((IlluminaNotificationDto) from, (IlluminaRun) to);
        break;
      default:
        throw new NotImplementedException();
    }
    return to;
  }

  private static void setOxfordNanoporeRunValues(@Nonnull OxfordNanoporeNotificationDto from,
      @Nonnull OxfordNanoporeRun to) {
    to.setMinKnowVersion(from.getSoftware());
    to.setProtocolVersion(from.getProtocolVersion());
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
    to.setDataManglingPolicy(getDataManglingPolicy(from.getIndexSequencing()));
  }

  private static InstrumentDataManglingPolicy getDataManglingPolicy(IndexSequencing indexSequencing) {
    if (indexSequencing == null) {
      return null;
    }
    switch (indexSequencing) {
      case NORMAL:
        return InstrumentDataManglingPolicy.NONE;
      case I5_REVERSE_COMPLEMENT:
        return InstrumentDataManglingPolicy.I5_RC;
      default:
        return null;
    }
  }

  private static void setCommonRunValues(@Nonnull NotificationDto from, @Nonnull Run to) {
    to.setAlias(from.getRunAlias());
    to.setFilePath(from.getSequencerFolderPath());
    to.setHealth(getMisoHealthTypeFromRunscanner(from.getHealthType()));
    to.setStartDate(LimsUtils.toBadDate(from.getStartDate()));
    to.setCompletionDate(LimsUtils.toBadDate(from.getCompletionDate()));
    to.setMetrics(from.getMetrics());
    if (from.getSequencingKit() != null) {
      to.setSequencingKit(new KitDescriptor());
      to.getSequencingKit().setName(from.getSequencingKit());
      to.getSequencingKit().setPartNumber(from.getSequencingKit());
    }
  }

  public static TargetedSequencingDto asDto(@Nonnull TargetedSequencing from) {
    TargetedSequencingDto dto = new TargetedSequencingDto();
    dto.setId(from.getId());
    dto.setAlias(from.getAlias());
    setString(dto::setDescription, from.getDescription());
    dto.setArchived(from.isArchived());
    dto.setKitDescriptorIds(from.getKitDescriptors().stream().map(KitDescriptor::getId).collect(Collectors.toList()));
    return dto;
  }

  public static Set<TargetedSequencingDto> asTargetedSequencingDtos(Collection<TargetedSequencing> from) {
    return from.stream().map(Dtos::asDto).collect(Collectors.toSet());
  }

  public static TargetedSequencing to(@Nonnull TargetedSequencingDto dto) {
    TargetedSequencing to = new TargetedSequencing();
    setLong(to::setId, dto.getId(), false);
    setString(to::setAlias, dto.getAlias());
    setString(to::setDescription, dto.getDescription());
    setBoolean(to::setArchived, dto.getArchived(), false);
    if (dto.getKitDescriptorIds() != null) {
      dto.getKitDescriptorIds().forEach(kitId -> {
        KitDescriptor kit = new KitDescriptor();
        kit.setId(kitId);
        to.getKitDescriptors().add(kit);
      });
    }
    return to;
  }

  public static Pool to(@Nonnull PoolDto dto) {
    PoolImpl to = new PoolImpl();
    setLong(to::setId, dto.getId(), false);
    to.setAlias(dto.getAlias());
    setBigDecimal(to::setConcentration, dto.getConcentration());
    to.setConcentrationUnits(dto.getConcentrationUnits());
    setInteger(to::setDnaSize, dto.getDnaSize(), true);
    to.setCreationDate(parseDate(dto.getCreationDate()));
    to.setDescription(dto.getDescription());
    to.setIdentificationBarcode(dto.getIdentificationBarcode());
    to.setDiscarded(dto.isDiscarded());
    setBigDecimal(to::setVolume, dto.getVolume());
    to.setVolumeUnits(dto.getVolumeUnits());
    setObject(to::setPlatformType, dto.getPlatformType(), pt -> PlatformType.valueOf(pt));
    if (dto.getPooledElements() != null) {
      to.setPoolElements(dto.getPooledElements().stream().map(aliquot -> {
        ListLibraryAliquotView view = new ListLibraryAliquotView();
        view.setId(aliquot.getId());
        view.setName(aliquot.getName());
        setBigDecimal(view::setVolumeUsed, aliquot.getVolumeUsed());
        PoolElement link = new PoolElement(to, view);
        if (aliquot.getProportion() != null) {
          link.setProportion(aliquot.getProportion());
        }
        return link;
      }).collect(Collectors.toSet()));
    }
    to.setQcPassed(dto.getQcPassed());
    to.setBoxPosition((PoolBoxPosition) makeBoxablePosition(dto, to));
    if (dto.isMergeChild())
      to.makeMergeChild();
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

  public static PrinterDto asDto(@Nonnull Printer from, @Nonnull ObjectMapper mapper) {
    PrinterDto dto = new PrinterDto();
    dto.setId(from.getId());
    dto.setAvailable(from.isEnabled());
    dto.setBackend(from.getBackend().name());
    // We intentionally do not pass configuration to the front end since it has passwords in it.
    dto.setDriver(from.getDriver().name());
    dto.setHeight(from.getHeight());
    dto.setWidth(from.getWidth());
    try {
      dto.setLayout(mapper.readValue(from.getLayout(), ArrayNode.class));
    } catch (IOException e) {
      log.error("Corrupt printer contents", e);
    }
    dto.setName(from.getName());
    return dto;
  }

  public static Printer to(@Nonnull PrinterDto dto, @Nonnull ObjectMapper mapper) throws JsonProcessingException {
    Printer to = new Printer();
    to.setId(dto.getId());
    to.setBackend(Backend.valueOf(dto.getBackend()));
    to.setConfiguration(mapper.writeValueAsString(dto.getConfiguration()));
    to.setDriver(Driver.valueOf(dto.getDriver()));
    to.setLayout(mapper.writeValueAsString(dto.getLayout()));
    to.setHeight(dto.getHeight());
    to.setWidth(dto.getWidth());
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
    setId(dto::setProjectId, from.getProject());
    setId(dto::setStudyTypeId, from.getStudyType());
    return dto;
  }

  public static Study to(@Nonnull StudyDto dto) {
    Study to = new StudyImpl();
    setLong(to::setId, dto.getId(), false);
    setString(to::setAccession, dto.getAccession());
    setString(to::setAlias, dto.getAlias());
    setString(to::setDescription, dto.getDescription());
    setString(to::setName, dto.getName());
    setObject(to::setProject, ProjectImpl::new, dto.getProjectId());
    setObject(to::setStudyType, StudyType::new, dto.getStudyTypeId());
    return to;
  }

  public static StudyTypeDto asDto(@Nonnull StudyType from) {
    StudyTypeDto dto = new StudyTypeDto();
    setLong(dto::setId, from.getId(), true);
    setString(dto::setName, from.getName());
    return dto;
  }

  public static StudyType to(@Nonnull StudyTypeDto from) {
    StudyType to = new StudyType();
    setLong(to::setId, from.getId(), false);
    setString(to::setName, from.getName());
    return to;
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
    setLong(dto::setId, from.getId(), true);
    dto.setActive(from.isActive());
    dto.setAdmin(from.isAdmin());
    dto.setEmail(from.getEmail());
    dto.setFullName(from.getFullName());
    dto.setInternal(from.isInternal());
    dto.setLoginName(from.getLoginName());
    return dto;
  }

  public static User to(@Nonnull UserDto dto) {
    User user = new UserImpl();
    setLong(user::setId, dto.getId(), false);
    setString(user::setFullName, dto.getFullName());
    setString(user::setLoginName, dto.getLoginName());
    setString(user::setEmail, dto.getEmail());
    setBoolean(user::setAdmin, dto.isAdmin(), false);
    setBoolean(user::setInternal, dto.isInternal(), false);
    setBoolean(user::setActive, dto.isActive(), false);
    return user;
  }

  public static GroupDto asDto(@Nonnull Group from) {
    GroupDto dto = new GroupDto();
    dto.setId(from.getId());
    dto.setDescription(from.getDescription());
    dto.setName(from.getName());
    return dto;
  }

  public static Group to(@Nonnull GroupDto dto) {
    Group to = new Group();
    setLong(to::setId, dto.getId(), false);
    setString(to::setName, dto.getName());
    setString(to::setDescription, dto.getDescription());
    return to;
  }

  public static InstrumentDto asDto(@Nonnull Instrument from) {
    InstrumentDto dto = new InstrumentDto();
    setLong(dto::setId, from.getId(), true);
    setDateString(dto::setDateCommissioned, from.getDateCommissioned());
    setDateString(dto::setDateDecommissioned, from.getDateDecommissioned());
    setString(dto::setName, from.getName());
    setId(dto::setInstrumentModelId, from.getInstrumentModel());
    if (from.getInstrumentModel() != null) {
      setString(dto::setInstrumentModelAlias, from.getInstrumentModel().getAlias());
      setString(dto::setInstrumentType, from.getInstrumentModel().getInstrumentType().toString());
      setString(dto::setPlatformType, from.getInstrumentModel().getPlatformType().getKey());
    }
    setString(dto::setSerialNumber, from.getSerialNumber());
    if (from.getDateDecommissioned() == null) {
      dto.setStatus("Production");
    } else if (from.getUpgradedInstrument() != null) {
      dto.setStatus("Upgraded");
    } else {
      dto.setStatus("Retired");
    }
    setBoolean(dto::setOutOfService, from.isOutOfService(), false);
    setId(dto::setUpgradedInstrumentId, from.getUpgradedInstrument());
    setId(dto::setDefaultRunPurposeId, from.getDefaultRunPurpose());
    setString(dto::setIdentificationBarcode, from.getIdentificationBarcode());
    setId(dto::setWorkstationId, from.getWorkstation());
    setString(dto::setWorkstationAlias, maybeGetProperty(from.getWorkstation(), Workstation::getAlias));
    return dto;
  }

  public static Instrument to(@Nonnull InstrumentDto dto) {
    Instrument to = new InstrumentImpl();
    setLong(to::setId, dto.getId(), false);
    setDate(to::setDateCommissioned, dto.getDateCommissioned());
    setDate(to::setDateDecommissioned, dto.getDateDecommissioned());
    setString(to::setName, dto.getName());
    setObject(to::setInstrumentModel, InstrumentModel::new, dto.getInstrumentModelId());
    setString(to::setSerialNumber, dto.getSerialNumber());
    setObject(to::setUpgradedInstrument, InstrumentImpl::new, dto.getUpgradedInstrumentId());
    setObject(to::setDefaultRunPurpose, RunPurpose::new, dto.getDefaultRunPurposeId());
    setString(to::setIdentificationBarcode, dto.getIdentificationBarcode());
    setObject(to::setWorkstation, Workstation::new, dto.getWorkstationId());
    return to;
  }

  public static ReferenceGenomeDto asDto(@Nonnull ReferenceGenome from) {
    ReferenceGenomeDto dto = new ReferenceGenomeDto();
    setLong(dto::setId, from.getId(), true);
    setString(dto::setAlias, from.getAlias());
    setId(dto::setDefaultScientificNameId, from.getDefaultScientificName());
    return dto;
  }

  public static ReferenceGenome to(@Nonnull ReferenceGenomeDto from) {
    ReferenceGenome to = new ReferenceGenomeImpl();
    setLong(to::setId, from.getId(), false);
    setString(to::setAlias, from.getAlias());
    setObject(to::setDefaultScientificName, ScientificName::new, from.getDefaultScientificNameId());
    return to;
  }

  public static PartitionDto asDto(@Nonnull Partition from, IndexChecker indexChecker) {
    return asDto(from, false, indexChecker);
  }

  public static PartitionDto asDto(@Nonnull Partition from, boolean includePoolContents, IndexChecker indexChecker) {
    PartitionDto dto = new PartitionDto();
    dto.setId(from.getId());
    dto.setContainerId(from.getSequencerPartitionContainer().getId());
    dto.setContainerName(from.getSequencerPartitionContainer().getIdentificationBarcode());
    dto.setPartitionNumber(from.getPartitionNumber());
    dto.setPool(from.getPool() == null ? null : asDto(from.getPool(), includePoolContents, false, indexChecker));
    setString(dto::setLoadingConcentration, from.getLoadingConcentration());
    dto.setLoadingConcentrationUnits(from.getLoadingConcentrationUnits());
    return dto;
  }

  public static PartitionQCTypeDto asDto(@Nonnull PartitionQCType from) {
    PartitionQCTypeDto dto = new PartitionQCTypeDto();
    setLong(dto::setId, from.getId(), true);
    setString(dto::setDescription, from.getDescription());
    setBoolean(dto::setNoteRequired, from.isNoteRequired(), false);
    setBoolean(dto::setOrderFulfilled, from.isOrderFulfilled(), false);
    setBoolean(dto::setAnalysisSkipped, from.isAnalysisSkipped(), false);
    return dto;
  }

  public static PartitionQCType to(@Nonnull PartitionQCTypeDto from) {
    PartitionQCType to = new PartitionQCType();
    setLong(to::setId, from.getId(), false);
    setString(to::setDescription, from.getDescription());
    setBoolean(to::setNoteRequired, from.isNoteRequired(), false);
    setBoolean(to::setOrderFulfilled, from.isOrderFulfilled(), false);
    setBoolean(to::setAnalysisSkipped, from.isAnalysisSkipped(), false);
    return to;
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
        .map(entry -> new ExperimentDto.RunPartitionDto(asDto(entry.getRun()),
            asDto(entry.getPartition(), null)))
        .collect(Collectors.toList()));
    dto.setStudy(asDto(from.getStudy()));
    dto.setTitle(from.getTitle());
    return dto;
  }

  public static Experiment to(@Nonnull ExperimentDto dto) {
    Experiment to = new Experiment();
    setLong(to::setId, dto.getId(), false);
    setString(to::setAccession, dto.getAccession());
    to.setAlias(dto.getAlias());
    to.setDescription(dto.getDescription());
    setString(to::setName, dto.getName());
    to.setLibrary(to(dto.getLibrary()));
    to.setInstrumentModel(to(dto.getInstrumentModel()));
    to.setRunPartitions(dto.getPartitions().stream().map(rpDto -> {
      RunPartition rpTo = new RunPartition();
      rpTo.setExperiment(to);
      rpTo.setPartition(to(rpDto.getPartition()));
      rpTo.setRun(PlatformType.get(rpDto.getRun().getPlatformType()).createRun());
      rpTo.getRun().setId(rpDto.getRun().getId());
      return rpTo;
    }).collect(Collectors.toList()));
    to.setStudy(to(dto.getStudy()));
    to.setTitle(dto.getTitle());
    return to;
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
    if (from.getExperiments() != null && !from.getExperiments().isEmpty()) {
      dto.setExperimentIds(from.getExperiments().stream().map(Experiment::getId).collect(Collectors.toList()));
    }
    return dto;
  }

  public static Submission to(@Nonnull SubmissionDto dto) {
    Submission to = new Submission();
    setLong(to::setId, dto.getId(), false);
    setString(to::setAccession, dto.getAccession());
    setString(to::setAlias, dto.getAlias());
    to.setCompleted(dto.isCompleted());
    setDate(to::setCreationDate, dto.getCreationDate());
    setString(to::setDescription, dto.getDescription());
    setDate(to::setSubmissionDate, dto.getSubmittedDate());
    setString(to::setTitle, dto.getTitle());
    to.setVerified(dto.isVerified());
    if (dto.getExperimentIds() != null && !dto.getExperimentIds().isEmpty()) {
      to.setExperiments(dto.getExperimentIds().stream().map(id -> {
        Experiment exp = new Experiment();
        exp.setId(id);
        return exp;
      }).collect(Collectors.toSet()));
    }
    return to;
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
    return arraySamples.entrySet().stream().map(entry -> asArraySampleDto(entry.getKey(), entry.getValue()))
        .collect(Collectors.toList());
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
      setLong(dto::setArrayId, from.getArray().getId(), true);
      setString(dto::setArrayAlias, from.getArray().getAlias());
    }
    dto.setStatus(from.getHealth().getKey());
    setDateString(dto::setStartDate, from.getStartDate());
    setDateString(dto::setCompletionDate, from.getCompletionDate());
    setDateString(dto::setLastModified, from.getLastModified());
    return dto;
  }

  public static List<ArrayDto> asArrayDtos(Collection<Array> arrays) {
    return arrays.stream()
        .map(Dtos::asDto)
        .collect(Collectors.toList());
  }

  public static ArrayModelDto asDto(@Nonnull ArrayModel from) {
    ArrayModelDto to = new ArrayModelDto();
    setLong(to::setId, from.getId(), true);
    setString(to::setAlias, from.getAlias());
    setInteger(to::setRows, from.getRows(), true);
    setInteger(to::setColumns, from.getColumns(), true);
    return to;
  }

  public static ArrayModel to(@Nonnull ArrayModelDto from) {
    ArrayModel to = new ArrayModel();
    setLong(to::setId, from.getId(), false);
    setString(to::setAlias, from.getAlias());
    setInteger(to::setRows, from.getRows(), false);
    setInteger(to::setColumns, from.getColumns(), false);
    return to;
  }

  public static final ArrayRun to(@Nonnull ArrayRunDto from) {
    ArrayRun run = new ArrayRun();
    if (from.getId() != null) {
      run.setId(from.getId());
    }
    run.setAlias(from.getAlias());
    run.setDescription(nullifyStringIfBlank(from.getDescription()));
    run.setFilePath(nullifyStringIfBlank(from.getFilePath()));
    setObject(run::setInstrument, InstrumentImpl::new, from.getInstrumentId());
    setObject(run::setArray, Array::new, from.getArrayId());
    run.setHealth(HealthType.get(from.getStatus()));
    setDate(run::setStartDate, from.getStartDate());
    setDate(run::setCompletionDate, from.getCompletionDate());
    setDate(run::setLastModified, from.getLastModified());
    return run;
  }

  public static InstrumentStatusDto asDto(@Nonnull InstrumentStatus from) {
    InstrumentStatusDto to = new InstrumentStatusDto();

    InstrumentDto instrumentDto = new InstrumentDto();
    instrumentDto.setId(from.getId());
    instrumentDto.setName(from.getName());
    to.setInstrument(instrumentDto);

    List<InstrumentPositionStatusDto> posDtos = new ArrayList<>();
    for (InstrumentStatusPosition pos : from.getPositions()) {
      InstrumentPositionStatusDto posDto = new InstrumentPositionStatusDto();
      posDto.setPosition(pos.getAlias());
      if (pos.getOutOfServiceTime() != null) {
        posDto.setOutOfService(true);
        setDateTimeString(posDto::setOutOfServiceTime, pos.getOutOfServiceTime());
      }
      if (pos.getRun() != null) {
        RunDto runDto = new RunDto();
        InstrumentStatusPositionRun run = pos.getRun();
        runDto.setId(run.getRunId());
        runDto.setName(run.getName());
        runDto.setAlias(run.getAlias());
        runDto.setStatus(run.getHealth().getKey());
        setDateString(runDto::setStartDate, run.getStartDate());
        setDateString(runDto::setEndDate, run.getCompletionDate());
        setDateTimeString(runDto::setLastModified, run.getLastModified());
        posDto.setRun(runDto);

        List<PoolDto> poolDtos = new ArrayList<>();
        for (InstrumentStatusPositionRunPool pool : run.getPools()) {
          PoolDto poolDto = new PoolDto();
          poolDto.setId(pool.getPoolId());
          poolDto.setName(pool.getName());
          poolDto.setAlias(pool.getAlias());
          poolDtos.add(poolDto);
        }
        posDto.setPools(poolDtos);
      }
      posDtos.add(posDto);
    }
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
    setString(dto::setDefaultVolume, from.getDefaultVolume());
    setString(dto::setVolumeUnits, maybeGetProperty(from.getVolumeUnits(), VolumeUnit::name));
    dto.setPlatformType(from.getPlatformType() != null ? from.getPlatformType().name() : null);
    dto.setLibraryTypeId(from.getLibraryType() != null ? from.getLibraryType().getId() : null);
    dto.setSelectionId(from.getLibrarySelectionType() != null ? from.getLibrarySelectionType().getId() : null);
    dto.setStrategyId(from.getLibraryStrategyType() != null ? from.getLibraryStrategyType().getId() : null);
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

  public static StorageLocationMapDto asDto(@Nonnull StorageLocationMap from) {
    StorageLocationMapDto to = new StorageLocationMapDto();
    setLong(to::setId, from.getId(), true);
    setString(to::setFilename, from.getFilename());
    setString(to::setDescription, from.getDescription());
    return to;
  }

  public static StorageLocationMap to(@Nonnull StorageLocationMapDto from) {
    StorageLocationMap to = new StorageLocationMap();
    setLong(to::setId, from.getId(), false);
    setString(to::setFilename, from.getFilename());
    setString(to::setDescription, from.getDescription());
    return to;
  }

  public static QcTargetDto asDto(@Nonnull QcTarget from) {
    QcTargetDto dto = new QcTargetDto();
    dto.setQcTarget(from);
    dto.setCorrespondingFields(from.getCorrespondingFields());
    return dto;
  }

  public static IssueDto asDto(@Nonnull Issue from) {
    IssueDto dto = new IssueDto();
    dto.setAssignee(from.getAssignee());
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
    setId(dto::setPositionId, from.getPosition());
    setString(dto::setPosition, maybeGetProperty(from.getPosition(), InstrumentPosition::getAlias));
    setString(dto::setServicedBy, from.getServicedByName());
    setBoolean(dto::setOutOfService, from.isOutOfService(), true);
    setDateTimeString(dto::setStartTime, from.getStartTime());
    setDateTimeString(dto::setEndTime, from.getEndTime());
    if (from.getAttachments() != null) {
      dto.setAttachments(from.getAttachments().stream().map(Dtos::asDto).collect(Collectors.toList()));
    }
    return dto;
  }

  public static ServiceRecord to(@Nonnull ServiceRecordDto dto) {
    ServiceRecord to = new ServiceRecord();
    setLong(to::setId, dto.getId(), false);
    setDate(to::setServiceDate, dto.getServiceDate());
    setString(to::setTitle, dto.getTitle());
    setString(to::setDetails, dto.getDetails());
    setString(to::setReferenceNumber, dto.getReferenceNumber());
    setObject(to::setPosition, InstrumentPosition::new, dto.getPositionId());
    setString(to::setServicedByName, dto.getServicedBy());
    setBoolean(to::setOutOfService, dto.getOutOfService(), false);
    setDateTime(to::setStartTime, dto.getStartTime());
    setDateTime(to::setEndTime, dto.getEndTime());
    return to;
  }

  public static VolumeUnitDto asDto(VolumeUnit from) {
    VolumeUnitDto dto = new VolumeUnitDto();
    dto.setName(from);
    dto.setUnits(from == null ? null : from.getUnits());
    return dto;
  }

  public static WorksetDto asDto(@Nonnull Workset from) {
    WorksetDto dto = new WorksetDto();
    setLong(dto::setId, from.getId(), true);
    setString(dto::setAlias, from.getAlias());
    setString(dto::setDescription, from.getDescription());
    setId(dto::setCategoryId, from.getCategory());
    setId(dto::setStageId, from.getStage());
    setWorksetItemIds(from.getWorksetSamples(), dto::setSampleIds);
    setWorksetItemIds(from.getWorksetLibraries(), dto::setLibraryIds);
    setWorksetItemIds(from.getWorksetLibraryAliquots(), dto::setLibraryAliquotIds);
    dto.setCreator(from.getCreator().getFullName());
    dto.setLastModified(formatDateTime(from.getLastModified()));
    return dto;
  }

  private static void setWorksetItemIds(Collection<? extends WorksetItem<?>> worksetItems,
      Consumer<List<Long>> setter) {
    if (!worksetItems.isEmpty()) {
      setter.accept(worksetItems.stream()
          .map(worksetItem -> worksetItem.getItem().getId())
          .collect(Collectors.toList()));
    }
  }

  public static Workset to(@Nonnull WorksetDto from) {
    Workset workset = new Workset();
    setLong(workset::setId, from.getId(), false);
    setString(workset::setAlias, from.getAlias());
    setString(workset::setDescription, from.getDescription());
    setObject(workset::setCategory, WorksetCategory::new, from.getCategoryId());
    setObject(workset::setStage, WorksetStage::new, from.getStageId());
    setWorksetItems(workset::setWorksetSamples, from.getSampleIds(), WorksetSample::new, SampleImpl::new);
    setWorksetItems(workset::setWorksetLibraries, from.getLibraryIds(), WorksetLibrary::new, LibraryImpl::new);
    setWorksetItems(workset::setWorksetLibraryAliquots, from.getLibraryAliquotIds(), WorksetLibraryAliquot::new,
        LibraryAliquot::new);
    return workset;
  }

  private static <T extends Boxable, J extends WorksetItem<T>> void setWorksetItems(Consumer<Set<J>> setter,
      List<Long> ids,
      Supplier<J> worksetItemConstructor, Supplier<T> itemConstructor) {
    if (ids != null && !ids.isEmpty()) {
      setter.accept(ids.stream().map(id -> {
        J worksetItem = worksetItemConstructor.get();
        T item = itemConstructor.get();
        item.setId(id);
        worksetItem.setItem(item);
        return worksetItem;
      }).collect(Collectors.toSet()));
    }
  }

  public static LibraryTemplate to(@Nonnull LibraryTemplateDto from) {
    LibraryTemplate to = null;
    if (from instanceof DetailedLibraryTemplateDto) {
      to = toDetailedLibraryTemplate((DetailedLibraryTemplateDto) from);
    } else {
      to = new LibraryTemplate();
    }
    if (from.getId() != null)
      to.setId(from.getId());
    to.setAlias(from.getAlias());
    setObject(to::setPlatformType, from.getPlatformType(), PlatformType::valueOf);
    setBigDecimal(to::setDefaultVolume, from.getDefaultVolume());
    setObject(to::setVolumeUnits, from.getVolumeUnits(), VolumeUnit::valueOf);

    if (from.getProjectIds() != null) {
      List<Project> projects = new ArrayList<>();
      from.getProjectIds().stream().forEach(id -> {
        Project project = new ProjectImpl();
        project.setId(id);
        projects.add(project);
      });
      to.getProjects().addAll(projects);
    }

    if (from.getLibraryTypeId() != null) {
      LibraryType libraryType = new LibraryType();
      libraryType.setId(from.getLibraryTypeId());
      to.setLibraryType(libraryType);
    }
    setObject(to::setLibrarySelectionType, LibrarySelectionType::new, from.getSelectionId());
    if (from.getStrategyId() != null) {
      LibraryStrategyType libraryStrategyType = new LibraryStrategyType();
      libraryStrategyType.setId(from.getStrategyId());
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
    if (from.getIndexOneIds() != null) {
      to.getIndexOnes().putAll(from.getIndexOneIds().entrySet().stream()
          .collect(Collectors.toMap(entry -> entry.getKey(), entry -> {
            Index index = new Index();
            index.setId(entry.getValue());
            return index;
          })));
    }
    if (from.getIndexTwoIds() != null) {
      to.getIndexTwos().putAll(from.getIndexTwoIds().entrySet().stream()
          .collect(Collectors.toMap(entry -> entry.getKey(), entry -> {
            Index index = new Index();
            index.setId(entry.getValue());
            return index;
          })));
    }
    return to;
  }

  public static DetailedLibraryTemplate toDetailedLibraryTemplate(DetailedLibraryTemplateDto from) {
    if (from == null)
      return null;
    DetailedLibraryTemplate to = new DetailedLibraryTemplate();
    setObject(to::setLibraryDesign, LibraryDesign::new, from.getDesignId());
    setObject(to::setLibraryDesignCode, LibraryDesignCode::new, from.getDesignCodeId());
    return to;
  }

  public static LibrarySpikeInDto asDto(@Nonnull LibrarySpikeIn from) {
    LibrarySpikeInDto dto = new LibrarySpikeInDto();
    setLong(dto::setId, from.getId(), true);
    setString(dto::setAlias, from.getAlias());
    return dto;
  }

  public static LibrarySpikeIn to(@Nonnull LibrarySpikeInDto from) {
    LibrarySpikeIn to = new LibrarySpikeIn();
    setLong(to::setId, from.getId(), false);
    setString(to::setAlias, from.getAlias());
    return to;
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

  public static SampleTypeDto asDto(@Nonnull SampleType from) {
    SampleTypeDto to = new SampleTypeDto();
    setLong(to::setId, from.getId(), true);
    setString(to::setName, from.getName());
    setBoolean(to::setArchived, from.isArchived(), false);
    return to;
  }

  public static SampleType to(@Nonnull SampleTypeDto from) {
    SampleType to = new SampleType();
    setLong(to::setId, from.getId(), false);
    setString(to::setName, from.getName());
    setBoolean(to::setArchived, from.isArchived(), false);
    return to;
  }

  public static StainCategoryDto asDto(@Nonnull StainCategory from) {
    StainCategoryDto to = new StainCategoryDto();
    setLong(to::setId, from.getId(), true);
    setString(to::setName, from.getName());
    return to;
  }

  public static StainCategory to(@Nonnull StainCategoryDto from) {
    StainCategory to = new StainCategory();
    setLong(to::setId, from.getId(), false);
    setString(to::setName, from.getName());
    return to;
  }

  public static BoxUseDto asDto(@Nonnull BoxUse from) {
    BoxUseDto to = new BoxUseDto();
    setLong(to::setId, from.getId(), true);
    setString(to::setAlias, from.getAlias());
    return to;
  }

  public static BoxUse to(@Nonnull BoxUseDto from) {
    BoxUse to = new BoxUse();
    setLong(to::setId, from.getId(), false);
    setString(to::setAlias, from.getAlias());
    return to;
  }

  public static BoxSizeDto asDto(@Nonnull BoxSize from) {
    BoxSizeDto to = new BoxSizeDto();
    setLong(to::setId, from.getId(), true);
    setInteger(to::setRows, from.getRows(), true);
    setInteger(to::setColumns, from.getColumns(), true);
    setBoolean(to::setScannable, from.getScannable(), false);
    setString(to::setBoxType, maybeGetProperty(from.getBoxType(), BoxType::name));
    setString(to::setBoxTypeLabel, maybeGetProperty(from.getBoxType(), BoxType::getLabel));
    if (from.isSaved()) {
      to.setLabel(from.getLabel());
    }
    return to;
  }

  public static BoxSize to(@Nonnull BoxSizeDto from) {
    BoxSize to = new BoxSize();
    setLong(to::setId, from.getId(), false);
    setInteger(to::setRows, from.getRows(), true);
    setInteger(to::setColumns, from.getColumns(), true);
    setBoolean(to::setScannable, from.isScannable(), false);
    setObject(to::setBoxType, from.getBoxType(), BoxType::valueOf);
    return to;
  }

  public static PoolOrderDto asDto(@Nonnull PoolOrder from) {
    PoolOrderDto to = new PoolOrderDto();
    setLong(to::setId, from.getId(), true);
    setString(to::setAlias, from.getAlias());
    setString(to::setDescription, from.getDescription());
    setLong(to::setPurposeId, maybeGetProperty(from.getPurpose(), RunPurpose::getId), true);
    setString(to::setPurposeAlias, maybeGetProperty(from.getPurpose(), RunPurpose::getAlias));
    setInteger(to::setPartitions, from.getPartitions(), true);
    setId(to::setContainerModelId, from.getContainerModel());
    setId(to::setParametersId, from.getParameters());
    setString(to::setParametersName, maybeGetProperty(from.getParameters(), SequencingParameters::getName));
    setBoolean(to::setDraft, from.isDraft(), false);
    if (from.getOrderLibraryAliquots() != null) {
      to.setOrderAliquots(from.getOrderLibraryAliquots().stream().map(Dtos::asDto).collect(Collectors.toList()));
    }
    to.setStatus(from.getStatus().getLabel());
    setLong(to::setPoolId, maybeGetProperty(from.getPool(), Pool::getId), true);
    setString(to::setPoolAlias, maybeGetProperty(from.getPool(), Pool::getAlias));
    setLong(to::setSequencingOrderId, maybeGetProperty(from.getSequencingOrder(), SequencingOrder::getId), true);
    setString(to::setLongestIndex, from.getLongestIndex());
    return to;
  }

  public static PoolOrderDto asDto(@Nonnull PoolOrder from, IndexChecker indexChecker) {
    PoolOrderDto dto = asDto(from);
    if (indexChecker != null) {
      dto.setDuplicateIndicesSequences(indexChecker.getDuplicateIndicesSequences(from));
      dto.setDuplicateIndices(
          dto.getDuplicateIndicesSequences() != null && !dto.getDuplicateIndicesSequences().isEmpty());
      dto.setNearDuplicateIndicesSequences(indexChecker.getNearDuplicateIndicesSequences(from));
      dto.setNearDuplicateIndices(
          dto.getNearDuplicateIndicesSequences() != null && !dto.getNearDuplicateIndicesSequences().isEmpty());
    }
    return dto;
  }

  private static OrderAliquotDto asDto(@Nonnull OrderLibraryAliquot from) {
    OrderAliquotDto to = new OrderAliquotDto();
    setLong(to::setId, from.getAliquot().getId(), true);
    to.setAliquot(Dtos.asDto(from.getAliquot(), false));
    setInteger(to::setProportion, from.getProportion(), true);
    return to;
  }

  public static PoolOrder to(@Nonnull PoolOrderDto from) {
    PoolOrder to = new PoolOrder();
    setLong(to::setId, from.getId(), false);
    setString(to::setAlias, from.getAlias());
    setString(to::setDescription, from.getDescription());
    setObject(to::setPurpose, RunPurpose::new, from.getPurposeId());
    setInteger(to::setPartitions, from.getPartitions(), true);
    setObject(to::setContainerModel, SequencingContainerModel::new, from.getContainerModelId());
    setObject(to::setParameters, SequencingParameters::new, from.getParametersId());
    setBoolean(to::setDraft, from.isDraft(), false);
    if (from.getOrderAliquots() != null) {
      to.setOrderLibraryAliquots(from.getOrderAliquots().stream().map(libDto -> {
        OrderLibraryAliquot lib = Dtos.to(libDto);
        lib.setPoolOrder(to);
        return lib;
      }).collect(Collectors.toSet()));
    }
    setObject(to::setPool, PoolImpl::new, from.getPoolId());
    setObject(to::setSequencingOrder, SequencingOrderImpl::new, from.getSequencingOrderId());
    return to;
  }

  private static OrderLibraryAliquot to(@Nonnull OrderAliquotDto from) {
    OrderLibraryAliquot to = new OrderLibraryAliquot();
    to.setAliquot(Dtos.to(from.getAliquot()));
    setInteger(to::setProportion, from.getProportion(), false);
    return to;
  }

  public static RunPurposeDto asDto(@Nonnull RunPurpose from) {
    RunPurposeDto to = new RunPurposeDto();
    setLong(to::setId, from.getId(), true);
    setString(to::setAlias, from.getAlias());
    return to;
  }

  public static RunPurpose to(@Nonnull RunPurposeDto from) {
    RunPurpose to = new RunPurpose();
    setLong(to::setId, from.getId(), false);
    setString(to::setAlias, from.getAlias());
    return to;
  }

  public static TissuePieceTypeDto asDto(@Nonnull TissuePieceType from) {
    TissuePieceTypeDto to = new TissuePieceTypeDto();
    setLong(to::setId, from.getId(), true);
    setString(to::setName, from.getName());
    setString(to::setAbbreviation, from.getAbbreviation());
    setString(to::setV2NamingCode, from.getV2NamingCode());
    setBoolean(to::setArchived, from.getArchived(), true);
    return to;
  }

  public static TissuePieceType to(@Nonnull TissuePieceTypeDto from) {
    TissuePieceType to = new TissuePieceType();
    if (from.getId() != null)
      to.setId(from.getId());
    setString(to::setName, from.getName());
    setString(to::setAbbreviation, from.getAbbreviation());
    setString(to::setV2NamingCode, from.getV2NamingCode());
    setBoolean(to::setArchived, from.isArchived(), false);
    return to;
  }

  public static TransferDto asDto(@Nonnull Transfer from) {
    TransferDto to = new TransferDto();
    setLong(to::setId, from.getId(), true);
    setString(to::setTransferRequestName, from.getTransferRequestName());
    setDateTimeString(to::setTransferTime, from.getTransferTime());
    setId(to::setSenderLabId, from.getSenderLab());
    setString(to::setSenderLabLabel, maybeGetProperty(from.getSenderLab(), Lab::getAlias));
    setId(to::setSenderGroupId, from.getSenderGroup());
    setString(to::setSenderGroupName, maybeGetProperty(from.getSenderGroup(), Group::getName));
    setString(to::setRecipient, from.getRecipient());
    setId(to::setRecipientGroupId, from.getRecipientGroup());
    setString(to::setRecipientGroupName, maybeGetProperty(from.getRecipientGroup(), Group::getName));
    to.setItems(new ArrayList<>());
    to.getItems().addAll(from.getSampleTransfers().stream().map(item -> {
      TransferItemDto dto = Dtos.asDto(item);
      setId(dto::setProjectId, item.getItem().getProject());
      if (isDetailedSample(item.getItem())) {
        DetailedSample ds = (DetailedSample) item.getItem();
        setId(dto::setSampleClassId, ds.getSampleClass());
      }
      return dto;
    }).collect(Collectors.toList()));
    to.getItems().addAll(from.getLibraryTransfers().stream().map(item -> {
      TransferItemDto dto = Dtos.asDto(item);
      setId(dto::setProjectId, item.getItem().getSample().getProject());
      return dto;
    }).collect(Collectors.toList()));
    to.getItems()
        .addAll(from.getLibraryAliquotTransfers().stream().map(item -> Dtos.asDto(item)).collect(Collectors.toList()));
    to.getItems().addAll(from.getPoolTransfers().stream().map(item -> {
      TransferItemDto dto = Dtos.asDto(item);
      setString(dto::setPlatformType, maybeGetProperty(item.getItem().getPlatformType(), PlatformType::name));
      return dto;
    }).collect(Collectors.toList()));
    return to;
  }

  public static Transfer to(@Nonnull TransferDto from) {
    Transfer to = new Transfer();
    setLong(to::setId, from.getId(), false);
    setString(to::setTransferRequestName, from.getTransferRequestName());
    setDateTime(to::setTransferTime, from.getTransferTime());
    setObject(to::setSenderLab, LabImpl::new, from.getSenderLabId());
    setObject(to::setSenderGroup, Group::new, from.getSenderGroupId());
    setString(to::setRecipient, from.getRecipient());
    setObject(to::setRecipientGroup, Group::new, from.getRecipientGroupId());
    addTransferItems(to::getSampleTransfers, from.getItems(), EntityType.SAMPLE, TransferSample::new, SampleImpl::new,
        SampleBoxPosition::new, Sample::setBoxPosition);
    addTransferItems(to::getLibraryTransfers, from.getItems(), EntityType.LIBRARY, TransferLibrary::new,
        LibraryImpl::new,
        LibraryBoxPosition::new, Library::setBoxPosition);
    addTransferItems(to::getLibraryAliquotTransfers, from.getItems(), EntityType.LIBRARY_ALIQUOT,
        TransferLibraryAliquot::new,
        LibraryAliquot::new, LibraryAliquotBoxPosition::new, LibraryAliquot::setBoxPosition);
    addTransferItems(to::getPoolTransfers, from.getItems(), EntityType.POOL, TransferPool::new, PoolImpl::new,
        PoolBoxPosition::new,
        Pool::setBoxPosition);
    return to;
  }

  private static <T extends Boxable, U extends TransferItem<T>, V extends AbstractBoxPosition> void addTransferItems(
      Supplier<Set<U>> getToSet,
      List<TransferItemDto> transferItemDtos, EntityType type, Supplier<U> constructor, Supplier<T> itemConstructor,
      Supplier<V> boxPositionConstructor, BiConsumer<T, V> boxPositionSetter) {
    if (transferItemDtos != null && !transferItemDtos.isEmpty()) {
      getToSet.get().addAll(transferItemDtos.stream()
          .filter(transferItemDto -> type.getLabel().equals(transferItemDto.getType()))
          .map(transferItemDto -> Dtos.to(transferItemDto, constructor, (xfer, item) -> xfer.setItem(item),
              itemConstructor,
              boxPositionConstructor, boxPositionSetter))
          .collect(Collectors.toList()));
    }
  }

  private static TransferItemDto asDto(@Nonnull TransferItem<?> from) {
    TransferItemDto to = new TransferItemDto();
    setString(to::setType,
        maybeGetProperty(maybeGetProperty(from.getItem(), Boxable::getEntityType), EntityType::getLabel));
    setLong(to::setId, maybeGetProperty(from.getItem(), Boxable::getId), true);
    setString(to::setName, maybeGetProperty(from.getItem(), Boxable::getName));
    setString(to::setAlias, maybeGetProperty(from.getItem(), Boxable::getAlias));
    setBoolean(to::setReceived, from.isReceived(), true);
    setBoolean(to::setQcPassed, from.isQcPassed(), true);
    setString(to::setQcNote, from.getQcNote());
    setLong(to::setBoxId, maybeGetProperty(maybeGetProperty(from.getItem(), Boxable::getBox), Box::getId), true);
    if (from.getItem().getBox() != null) {
      setString(to::setBoxAlias, maybeGetProperty(maybeGetProperty(from.getItem(), Boxable::getBox), Box::getAlias));
      setString(to::setBoxPosition, maybeGetProperty(from.getItem(), Boxable::getBoxPosition));
    } else if (from.getDistributedBoxAlias() != null) {
      setString(to::setBoxAlias, "DISTRIBUTED - " + from.getDistributedBoxAlias());
      setString(to::setBoxPosition, from.getDistributedBoxPosition());
    }
    return to;
  }

  private static <T extends Boxable, U extends TransferItem<T>, V extends AbstractBoxPosition> U to(
      @Nonnull TransferItemDto from,
      Supplier<U> constructor,
      BiConsumer<U, T> itemSetter, Supplier<T> itemConstructor, Supplier<V> boxPositionConstructor,
      BiConsumer<T, V> boxPositionSetter) {
    U to = constructor.get();
    T toItem = itemConstructor.get();
    setLong(toItem::setId, from.getId(), false);
    itemSetter.accept(to, toItem);
    setBoolean(to::setReceived, from.isReceived(), true);
    setBoolean(to::setQcPassed, from.isQcPassed(), true);
    setString(to::setQcNote, from.getQcNote());
    if (from.getBoxId() != null && from.getBoxPosition() != null) {
      V boxPos = boxPositionConstructor.get();
      Box box = new BoxImpl();
      box.setId(from.getBoxId());
      boxPos.setBox(box);
      boxPos.setPosition(from.getBoxPosition());
      boxPositionSetter.accept(toItem, boxPos);
    } else {
      setString(to::setDistributedBoxAlias, from.getBoxAlias());
      setString(to::setDistributedBoxPosition, from.getBoxPosition());
    }
    return to;
  }

  public static ListTransferViewDto asDto(@Nonnull ListTransferView from) {
    ListTransferViewDto to = new ListTransferViewDto();
    setLong(to::setId, from.getId(), false);
    setDateString(to::setTransferTime, from.getTransferTime());
    setId(to::setSenderLabId, from.getSenderLab());
    setString(to::setSenderLabLabel, maybeGetProperty(from.getSenderLab(), Lab::getAlias));
    setId(to::setSenderGroupId, from.getSenderGroup());
    setString(to::setSenderGroupName, maybeGetProperty(from.getSenderGroup(), Group::getName));
    setString(to::setRecipient, from.getRecipient());
    setId(to::setRecipientGroupId, from.getRecipientGroup());
    setString(to::setRecipientGroupName, maybeGetProperty(from.getRecipientGroup(), Group::getName));
    setLong(to::setItems, from.getItems(), false);
    setLong(to::setReceived, from.getReceived(), false);
    setLong(to::setReceiptPending, from.getReceiptPending(), false);
    setLong(to::setQcPassed, from.getQcPassed(), false);
    setLong(to::setQcPending, from.getQcPending(), false);
    setDateTimeString(to::setLastModified, from.getLastModified());
    setString(to::setProjects, from.getProjectLabels().stream().collect(Collectors.joining(", ")));
    return to;
  }

  public static RunPartitionAliquotDto asDto(@Nonnull RunPartitionAliquot from) {
    RunPartitionAliquotDto to = new RunPartitionAliquotDto();
    setId(to::setRunId, from.getRun());
    setId(to::setPartitionId, from.getPartition());
    setId(to::setAliquotId, from.getAliquot());
    setString(to::setRunAlias, maybeGetProperty(from.getRun(), Run::getAlias));
    if (from.getRun() != null && from.getRun().getPlatformType() != null) {
      to.setPlatformType(from.getRun().getPlatformType().name());
    }
    if (from.getPartition() != null && from.getPartition().getSequencerPartitionContainer() != null) {
      to.setContainerId(from.getPartition().getSequencerPartitionContainer().getId());
      to.setContainerIdentificationBarcode(
          from.getPartition().getSequencerPartitionContainer().getIdentificationBarcode());
    }
    setInteger(to::setPartitionNumber, maybeGetProperty(from.getPartition(), Partition::getPartitionNumber), true);
    setString(to::setAliquotName, maybeGetProperty(from.getAliquot(), LibraryAliquot::getName));
    setString(to::setAliquotAlias, maybeGetProperty(from.getAliquot(), LibraryAliquot::getAlias));
    setId(to::setRunPurposeId, from.getPurpose());
    setId(to::setQcStatusId, from.getQcStatus());
    setString(to::setQcNote, from.getQcNote());
    setString(to::setQcUserName, maybeGetProperty(from.getQcUser(), User::getFullName));
    setDateString(to::setQcDate, from.getQcDate());
    setBoolean(to::setDataReview, from.getDataReview(), true);
    setString(to::setDataReviewer, maybeGetProperty(from.getDataReviewer(), User::getFullName));
    setDateString(to::setDataReviewDate, from.getDataReviewDate());
    return to;
  }

  public static RunPartitionAliquot to(@Nonnull RunPartitionAliquotDto from) {
    RunPartitionAliquot to = new RunPartitionAliquot();
    PlatformType platform = PlatformType.valueOf(from.getPlatformType());
    setObject(to::setRun, platform::createRun, from.getRunId());
    setObject(to::setPartition, PartitionImpl::new, from.getPartitionId());
    setObject(to::setAliquot, LibraryAliquot::new, from.getAliquotId());
    setObject(to::setPurpose, RunPurpose::new, from.getRunPurposeId());
    setObject(to::setQcStatus, RunLibraryQcStatus::new, from.getQcStatusId());
    setString(to::setQcNote, from.getQcNote());
    setBoolean(to::setDataReview, from.getDataReview(), true);
    return to;
  }

  public static WorkstationDto asDto(@Nonnull Workstation from) {
    WorkstationDto to = new WorkstationDto();
    setLong(to::setId, from.getId(), true);
    setString(to::setAlias, from.getAlias());
    setString(to::setDescription, from.getDescription());
    setString(to::setIdentificationBarcode, from.getIdentificationBarcode());
    return to;
  }

  public static Workstation to(@Nonnull WorkstationDto from) {
    Workstation to = new Workstation();
    setLong(to::setId, from.getId(), false);
    setString(to::setAlias, from.getAlias());
    setString(to::setDescription, from.getDescription());
    setString(to::setIdentificationBarcode, from.getIdentificationBarcode());
    return to;
  }

  public static ListWorksetViewDto asDto(@Nonnull ListWorksetView from) {
    ListWorksetViewDto to = new ListWorksetViewDto();
    setLong(to::setId, from.getId(), false);
    setString(to::setAlias, from.getAlias());
    setInteger(to::setItemCount, from.getItemCount(), false);
    setString(to::setDescription, from.getDescription());
    setString(to::setStage, from.getStage());
    setString(to::setCreator, maybeGetProperty(from.getCreator(), User::getFullName));
    setDateTimeString(to::setLastModified, from.getLastModified());
    return to;
  }

  private static <T extends Boxable, R extends TransferItem<T>> R toReceiptTransfer(@Nonnull ReceivableDto<T, R> from,
      @Nonnull T item) {
    if (isStringEmptyOrNull(from.getReceivedTime())) {
      return null;
    }
    R transferItem = from.makeTransferItem();
    transferItem.setItem(item);
    setBoolean(transferItem::setReceived, from.isReceived(), true);
    setBoolean(transferItem::setQcPassed, from.isReceiptQcPassed(), true);
    setString(transferItem::setQcNote, from.getReceiptQcNote());
    Transfer transfer = new Transfer();
    transferItem.setTransfer(transfer);
    from.getTransferItemsFunction().apply(transfer).add(transferItem);
    setDateTime(transfer::setTransferTime, from.getReceivedTime());
    setObject(transfer::setSenderLab, LabImpl::new, from.getSenderLabId());
    setObject(transfer::setRecipientGroup, Group::new, from.getRecipientGroupId());
    return transferItem;
  }

  public static SequencingControlTypeDto asDto(@Nonnull SequencingControlType from) {
    SequencingControlTypeDto to = new SequencingControlTypeDto();
    setLong(to::setId, from.getId(), true);
    setString(to::setAlias, from.getAlias());
    return to;
  }

  public static SequencingControlType to(@Nonnull SequencingControlTypeDto from) {
    SequencingControlType to = new SequencingControlType();
    setLong(to::setId, from.getId(), false);
    setString(to::setAlias, from.getAlias());
    return to;
  }

  public static ListContainerViewDto asDto(@Nonnull ListContainerView from) {
    ListContainerViewDto to = new ListContainerViewDto();
    setLong(to::setId, from.getId(), true);
    setString(to::setIdentificationBarcode, from.getIdentificationBarcode());
    setString(to::setPlatform,
        maybeGetProperty(maybeGetProperty(from.getModel(), SequencingContainerModel::getPlatformType),
            PlatformType::getKey));
    setDateTimeString(to::setLastModified, from.getLastModified());

    ListContainerRunView lastRun =
        from.getRuns().stream().max(Comparator.comparing(ListContainerRunView::getStartDate)).orElse(null);
    if (lastRun != null) {
      setLong(to::setLastRunId, lastRun.getId(), true);
      setString(to::setLastRunName, lastRun.getName());
      setString(to::setLastRunAlias, lastRun.getAlias());
      setLong(to::setLastSequencerId, maybeGetProperty(lastRun.getSequencer(), ListContainerRunSequencerView::getId),
          true);
      setString(to::setLastSequencerName,
          maybeGetProperty(lastRun.getSequencer(), ListContainerRunSequencerView::getName));
    }
    return to;
  }

  public static ScientificNameDto asDto(@Nonnull ScientificName from) {
    ScientificNameDto to = new ScientificNameDto();
    setLong(to::setId, from.getId(), true);
    setString(to::setAlias, from.getAlias());
    return to;
  }

  public static ScientificName to(@Nonnull ScientificNameDto from) {
    ScientificName to = new ScientificName();
    setLong(to::setId, from.getId(), false);
    setString(to::setAlias, from.getAlias());
    return to;
  }

  public static SopDto asDto(@Nonnull Sop from) {
    SopDto to = new SopDto();
    setLong(to::setId, from.getId(), false);
    setString(to::setAlias, from.getAlias());
    setString(to::setVersion, from.getVersion());
    setString(to::setCategory, maybeGetProperty(from.getCategory(), SopCategory::name));
    setString(to::setUrl, from.getUrl());
    setBoolean(to::setArchived, from.isArchived(), false);
    return to;
  }

  public static Sop to(@Nonnull SopDto from) {
    Sop to = new Sop();
    setLong(to::setId, from.getId(), false);
    setString(to::setAlias, from.getAlias());
    setString(to::setVersion, from.getVersion());
    setObject(to::setCategory, from.getCategory(), SopCategory::valueOf);
    setString(to::setUrl, from.getUrl());
    setBoolean(to::setArchived, from.isArchived(), false);
    return to;
  }

  public static LibraryBatchDto asDto(@Nonnull LibraryBatch from) {
    LibraryBatchDto to = new LibraryBatchDto();
    setString(to::setBatchId, from.getBatchId());
    setDateString(to::setDate, from.getDate());
    setLong(to::setUserId, from.getUserId(), false);
    setLong(to::setSopId, from.getSopId(), false);
    setLong(to::setKitId, from.getKitId(), false);
    setString(to::setKitLot, from.getKitLot());
    return to;
  }

  public static QcNodeDto asDto(@Nonnull QcNode from) {
    QcNodeDto to = new QcNodeDto();
    convertIntoDto(from, to);
    return to;
  }

  public static QcHierarchyNodeDto asHierarchyDto(@Nonnull SampleQcNode from) {
    QcHierarchyNodeDto dto = new QcHierarchyNodeDto();
    convertIntoDto(from, dto);
    addChildren(from, dto);
    return dto;
  }

  private static <T extends QcNodeDto> void convertIntoDto(@Nonnull QcNode from, @Nonnull T to) {
    setLong(to::setId, from.getId(), true);
    to.setIds(from.getIds());
    setString(to::setEntityType, maybeGetProperty(from.getEntityType(), QcNodeType::getLabel));
    setString(to::setTypeLabel, from.getTypeLabel());
    setString(to::setName, from.getName());
    setString(to::setLabel, from.getLabel());
    setBoolean(to::setQcPassed, from.getQcPassed(), true);
    setLong(to::setQcStatusId, from.getQcStatusId(), true);
    setString(to::setQcNote, from.getQcNote());
    setBoolean(to::setDataReview, from.getDataReview(), true);
  }

  public static QcStatusUpdate to(@Nonnull QcNodeDto from) {
    QcStatusUpdate to = new QcStatusUpdate();
    setLong(to::setId, from.getId(), true);
    to.setIds(from.getIds());
    setObject(to::setEntityType, from.getEntityType(), QcNodeType::lookup);
    setBoolean(to::setQcPassed, from.getQcPassed(), true);
    setLong(to::setQcStatusId, from.getQcStatusId(), true);
    setString(to::setQcNote, from.getQcNote());
    setBoolean(to::setDataReview, from.getDataReview(), true);
    return to;
  }

  private static void addChildren(QcNode from, QcHierarchyNodeDto to) {
    if (from.getChildren() != null && !from.getChildren().isEmpty()) {
      List<QcHierarchyNodeDto> childDtos = new ArrayList<>();
      for (QcNode child : from.getChildren()) {
        QcHierarchyNodeDto childDto = new QcHierarchyNodeDto();
        convertIntoDto(child, childDto);
        childDtos.add(childDto);
        addChildren(child, childDto);
      }
      to.setChildren(childDtos);
    }
  }

  public static TransferNotificationDto asDto(@Nonnull TransferNotification from) {
    TransferNotificationDto to = new TransferNotificationDto();
    setLong(to::setId, from.getId(), true);
    setString(to::setRecipientName, from.getRecipientName());
    setString(to::setRecipientEmail, from.getRecipientEmail());
    setString(to::setSenderName, maybeGetProperty(from.getCreator(), User::getFullName));
    setDateTimeString(to::setSentTime, from.getSentTime());
    setBoolean(to::setSendSuccess, from.getSendSuccess(), true);
    return to;
  }

  public static TransferNotification to(@Nonnull TransferNotificationDto from) {
    // This is only used for creation, so most fields are generated
    TransferNotification to = new TransferNotification();
    setLong(to::setId, from.getId(), false);
    setString(to::setRecipientName, from.getRecipientName());
    setString(to::setRecipientEmail, from.getRecipientEmail());
    return to;
  }

  public static ContactDto asDto(@Nonnull Contact from) {
    ContactDto to = new ContactDto();
    setLong(to::setId, from.getId(), true);
    setString(to::setName, from.getName());
    setString(to::setEmail, from.getEmail());
    return to;
  }

  public static Contact to(@Nonnull ContactDto from) {
    Contact to = new Contact();
    setLong(to::setId, from.getId(), false);
    setString(to::setName, from.getName());
    setString(to::setEmail, from.getEmail());
    return to;
  }

  public static PipelineDto asDto(@Nonnull Pipeline from) {
    PipelineDto to = new PipelineDto();
    setLong(to::setId, from.getId(), true);
    setString(to::setAlias, from.getAlias());
    return to;
  }

  public static Pipeline to(@Nonnull PipelineDto from) {
    Pipeline to = new Pipeline();
    setLong(to::setId, from.getId(), false);
    setString(to::setAlias, from.getAlias());
    return to;
  }

  public static RunLibraryQcStatusDto asDto(RunLibraryQcStatus from) {
    RunLibraryQcStatusDto to = new RunLibraryQcStatusDto();
    setLong(to::setId, from.getId(), true);
    setString(to::setDescription, from.getDescription());
    setBoolean(to::setQcPassed, from.getQcPassed(), true);
    return to;
  }

  public static RunLibraryQcStatus to(RunLibraryQcStatusDto from) {
    RunLibraryQcStatus to = new RunLibraryQcStatus();
    setLong(to::setId, from.getId(), false);
    setString(to::setDescription, from.getDescription());
    setBoolean(to::setQcPassed, from.getQcPassed(), true);
    return to;
  }

  public static SimpleAliasableDto asDto(Aliasable from) {
    SimpleAliasableDto to = new SimpleAliasableDto();
    setLong(to::setId, from.getId(), true);
    setString(to::setAlias, from.getAlias());
    return to;
  }

  public static WorksetCategory toWorksetCategory(SimpleAliasableDto from) {
    WorksetCategory to = new WorksetCategory();
    setLong(to::setId, from.getId(), false);
    setString(to::setAlias, from.getAlias());
    return to;
  }

  public static WorksetStage toWorksetStage(SimpleAliasableDto from) {
    WorksetStage to = new WorksetStage();
    setLong(to::setId, from.getId(), false);
    setString(to::setAlias, from.getAlias());
    return to;
  }

  public static void setBigDecimal(@Nonnull Consumer<BigDecimal> setter, String value) {
    setter.accept(isStringEmptyOrNull(value) ? null : new BigDecimal(value));
  }

  public static void setBoolean(@Nonnull Consumer<Boolean> setter, Boolean value, boolean nullOk) {
    if (value != null || nullOk) {
      setter.accept(value);
    } else {
      setter.accept(false);
    }
  }

  public static void setInteger(@Nonnull Consumer<Integer> setter, Integer value, boolean nullOk) {
    if (value != null || nullOk) {
      setter.accept(value);
    } else {
      setter.accept(0);
    }
  }

  public static void setString(@Nonnull Consumer<String> setter, BigDecimal value) {
    setter.accept(toNiceString(value));
  }

  public static void setString(@Nonnull Consumer<String> setter, Double value) {
    setter.accept(value == null ? null : value.toString());
  }

  public static void setString(@Nonnull Consumer<String> setter, String value) {
    setter.accept(isStringBlankOrNull(value) ? null : value.trim());
  }

  public static void setDateString(@Nonnull Consumer<String> setter, Date value) {
    setter.accept(value == null ? null : formatDate(value));
  }

  public static void setDate(@Nonnull Consumer<Date> setter, String value) {
    setter.accept(value == null ? null : parseDate(value));
  }

  public static void setDateTimeString(@Nonnull Consumer<String> setter, Date value) {
    setter.accept(value == null ? null : formatDateTime(value));
  }

  public static void setDateTime(@Nonnull Consumer<Date> setter, String value) {
    setter.accept(value == null ? null : parseDateTime(value));
  }

  public static void setLong(@Nonnull Consumer<Long> setter, Long value, boolean nullOk) {
    Long effectiveValue = value;
    if (effectiveValue == null) {
      effectiveValue = nullOk ? null : 0L;
    }
    setter.accept(effectiveValue);
  }

  public static void setId(@Nonnull Consumer<Long> setter, Identifiable value) {
    setter.accept(value == null ? null : value.getId());
  }

  public static <T extends Identifiable> void setObject(@Nonnull Consumer<T> setter, @Nonnull Supplier<T> constructor,
      Long id) {
    if (id == null) {
      setter.accept(null);
    } else {
      T obj = constructor.get();
      obj.setId(id);
      setter.accept(obj);
    }
  }

  public static <T> void setObject(@Nonnull Consumer<T> setter, String value, @Nonnull Function<String, T> lookup) {
    setter.accept(value == null ? null : lookup.apply(value));
  }

  public static <S, R> void setObject(@Nonnull Consumer<R> setter, S value, @Nonnull Function<S, R> transform) {
    setter.accept(value == null ? null : transform.apply(value));
  }

  public static <S, R> R maybeGetProperty(S object, @Nonnull Function<S, R> getter) {
    return object == null ? null : getter.apply(object);
  }

  /**
   * Converts from Runscanner's Platform to MISO's PlatformType.
   * 
   * @param rsType Runscanner Platform
   * @return equivalent MISO PlatformType
   */
  public static PlatformType getMisoPlatformTypeFromRunscanner(
      @Nonnull ca.on.oicr.gsi.runscanner.dto.type.Platform rsType) {
    return PlatformType.valueOf(rsType.name());
  }

  /**
   * Converts from Runscanner's HealthType to MISO's HealthType
   * 
   * @param rsType Runscanner HealthType
   * @return equivalent MISO HealthType
   */
  public static HealthType getMisoHealthTypeFromRunscanner(
      @Nonnull ca.on.oicr.gsi.runscanner.dto.type.HealthType rsType) {
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
