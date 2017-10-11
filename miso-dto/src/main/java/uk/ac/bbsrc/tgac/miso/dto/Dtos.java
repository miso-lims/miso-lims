package uk.ac.bbsrc.tgac.miso.dto;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.NotImplementedException;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment.RunPartition;
import uk.ac.bbsrc.tgac.miso.core.data.IlluminaRun;
import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.IndexFamily;
import uk.ac.bbsrc.tgac.miso.core.data.Institute;
import uk.ac.bbsrc.tgac.miso.core.data.Kit;
import uk.ac.bbsrc.tgac.miso.core.data.KitImpl;
import uk.ac.bbsrc.tgac.miso.core.data.LS454Run;
import uk.ac.bbsrc.tgac.miso.core.data.Lab;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.PartitionQCType;
import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.PoolOrder;
import uk.ac.bbsrc.tgac.miso.core.data.PoolOrderCompletion;
import uk.ac.bbsrc.tgac.miso.core.data.PoolQC;
import uk.ac.bbsrc.tgac.miso.core.data.Printer;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.QC;
import uk.ac.bbsrc.tgac.miso.core.data.ReferenceGenome;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleGroupId;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleLCMTube;
import uk.ac.bbsrc.tgac.miso.core.data.SampleNumberPerProject;
import uk.ac.bbsrc.tgac.miso.core.data.SamplePurpose;
import uk.ac.bbsrc.tgac.miso.core.data.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.SampleSlide;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;
import uk.ac.bbsrc.tgac.miso.core.data.SampleValidRelationship;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.Stain;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.StudyType;
import uk.ac.bbsrc.tgac.miso.core.data.Submission;
import uk.ac.bbsrc.tgac.miso.core.data.Subproject;
import uk.ac.bbsrc.tgac.miso.core.data.TissueMaterial;
import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;
import uk.ac.bbsrc.tgac.miso.core.data.TissueType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedLibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedQcStatusImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedSampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.InstituteImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LabImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolOrderImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleAliquotImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleClassImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleGroupImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIdentityImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleLCMTubeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleNumberPerProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SamplePurposeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleSlideImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleStockImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissueImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissueProcessingImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleValidRelationshipImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerReferenceImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StudyImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SubprojectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueMaterialImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueOriginImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueTypeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolableElementView;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.service.printing.Backend;
import uk.ac.bbsrc.tgac.miso.core.service.printing.Driver;
import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

public class Dtos {

  public static TissueOriginDto asDto(TissueOrigin from) {
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

  public static Set<TissueOriginDto> asTissueOriginDtos(Set<TissueOrigin> from) {
    Set<TissueOriginDto> dtoSet = Sets.newHashSet();
    for (TissueOrigin tissueOrigin : from) {
      dtoSet.add(asDto(tissueOrigin));
    }
    return dtoSet;
  }

  public static TissueOrigin to(TissueOriginDto from) {
    TissueOrigin to = new TissueOriginImpl();
    if (from.getId() != null) to.setId(from.getId());
    to.setAlias(from.getAlias());
    to.setDescription(from.getDescription());
    return to;
  }

  public static TissueTypeDto asDto(TissueType from) {
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

  public static Set<TissueTypeDto> asTissueTypeDtos(Set<TissueType> from) {
    Set<TissueTypeDto> dtoSet = Sets.newHashSet();
    for (TissueType tissueType : from) {
      dtoSet.add(asDto(tissueType));
    }
    return dtoSet;
  }

  public static TissueType to(TissueTypeDto from) {
    TissueType to = new TissueTypeImpl();
    if (from.getId() != null) to.setId(from.getId());
    to.setAlias(from.getAlias());
    to.setDescription(from.getDescription());
    return to;
  }

  public static SubprojectDto asDto(Subproject from) {
    SubprojectDto dto = new SubprojectDto();
    dto.setId(from.getId());
    dto.setAlias(from.getAlias());
    dto.setDescription(from.getDescription());
    dto.setPriority(from.getPriority());
    dto.setParentProjectId(from.getParentProject().getProjectId());
    dto.setCreatedById(from.getCreatedBy().getUserId());
    dto.setCreationDate(formatDateTime(from.getCreationDate()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(formatDateTime(from.getLastUpdated()));
    dto.setReferenceGenomeId(from.getReferenceGenomeId());
    return dto;
  }

  public static Set<SubprojectDto> asSubprojectDtos(Set<Subproject> from) {
    Set<SubprojectDto> dtoSet = Sets.newHashSet();
    for (Subproject subproject : from) {
      dtoSet.add(asDto(subproject));
    }
    return dtoSet;
  }

  public static Subproject to(SubprojectDto from) {
    Subproject to = new SubprojectImpl();
    to.setAlias(from.getAlias());
    to.setDescription(from.getDescription());
    to.setPriority(from.getPriority());
    to.setReferenceGenomeId(from.getReferenceGenomeId());
    return to;
  }

  public static SampleClassDto asDto(SampleClass from) {
    SampleClassDto dto = new SampleClassDto();
    dto.setId(from.getId());
    dto.setAlias(from.getAlias());
    dto.setSampleCategory(from.getSampleCategory());
    dto.setSuffix(from.getSuffix());
    dto.setCreatedById(from.getCreatedBy().getUserId());
    dto.setCreationDate(formatDateTime(from.getCreationDate()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(formatDateTime(from.getLastUpdated()));
    dto.setDNAseTreatable(from.getDNAseTreatable());
    return dto;
  }

  public static Set<SampleClassDto> asSampleClassDtos(Set<SampleClass> from) {
    Set<SampleClassDto> dtoSet = Sets.newHashSet();
    for (SampleClass sampleClass : from) {
      dtoSet.add(asDto(sampleClass));
    }
    return dtoSet;
  }

  public static SampleClass to(SampleClassDto from) {
    SampleClass to = new SampleClassImpl();
    to.setAlias(from.getAlias());
    to.setSampleCategory(from.getSampleCategory());
    to.setSuffix(from.getSuffix());
    to.setDNAseTreatable(from.getDNAseTreatable());
    return to;
  }

  public static DetailedQcStatusDto asDto(DetailedQcStatus from) {
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

  public static Set<DetailedQcStatusDto> asDetailedQcStatusDtos(Set<DetailedQcStatus> from) {
    Set<DetailedQcStatusDto> dtoSet = Sets.newHashSet();
    for (DetailedQcStatus detailedQcStatus : from) {
      dtoSet.add(asDto(detailedQcStatus));
    }
    return dtoSet;
  }

  public static DetailedQcStatus to(DetailedQcStatusDto from) {
    DetailedQcStatus to = new DetailedQcStatusImpl();
    to.setStatus(from.getStatus());
    to.setDescription(from.getDescription());
    to.setNoteRequired(from.isNoteRequired());
    return to;
  }

  public static SampleDto asMinimalDto(Sample from) {
    DetailedSampleDto dto = new DetailedSampleDto();
    copySampleFields(from, dto);

    if (isDetailedSample(from)) {
      dto.setSampleClassId(((DetailedSample) from).getSampleClass().getId());
    }
    return dto;
  }

  private static SampleDto copySampleFields(Sample from, SampleDto dto) {
    dto.setId(from.getId());
    dto.setName(from.getName());
    dto.setDescription(from.getDescription());
    dto.setUpdatedById(from.getLastModifier().getUserId());
    dto.setIdentificationBarcode(from.getIdentificationBarcode());
    dto.setLocationBarcode(from.getLocationBarcode());
    dto.setLocationLabel(BoxUtils.makeLocationLabel(from));
    dto.setBoxId(from.getBox() == null ? null : from.getBox().getId());
    dto.setSampleType(from.getSampleType());
    dto.setReceivedDate(from.getReceivedDate() == null ? null : formatDate(from.getReceivedDate()));
    if (from.getQcPassed() != null) {
      dto.setQcPassed(from.getQcPassed());
    }
    dto.setAlias(from.getAlias());
    dto.setProjectId(from.getProject().getProjectId());
    dto.setScientificName(from.getScientificName());
    dto.setTaxonIdentifier(from.getTaxonIdentifier());
    if (from.getVolume() != null) {
      dto.setVolume(from.getVolume().toString());
    }
    dto.setDiscarded(from.isDiscarded());
    dto.setLastModified(formatDateTime(from.getLastModified()));

    return dto;

  }

  private static DetailedSampleDto asDetailedSampleDto(DetailedSample from) {
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
    }
    if (from.getParent() != null) {
      dto.setParentId(from.getParent().getId());
      dto.setParentAlias(from.getParent().getAlias());
      dto.setParentTissueSampleClassId(from.getParent().getSampleClass().getId());
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
    dto.setConcentration(from.getConcentration() == null ? null : from.getConcentration().toString());
    dto.setNonStandardAlias(from.hasNonStandardAlias());
    if (from.getDetailedQcStatus() != null) {
      dto.setDetailedQcStatusId(from.getDetailedQcStatus().getId());
    }
    dto.setDetailedQcStatusNote(from.getDetailedQcStatusNote());
    return dto;
  }

  private static DetailedSample toDetailedSample(DetailedSampleDto from) {
    DetailedSample to = null;
    if (from.getClass() == SampleIdentityDto.class) {
      to = toIdentitySample((SampleIdentityDto) from);
    } else if (from.getClass() == SampleTissueDto.class) {
      to = toTissueSample((SampleTissueDto) from);
    } else if (from instanceof SampleTissueProcessingDto) {
      to = toTissueProcessingSample((SampleTissueProcessingDto) from);
    } else if (from.getClass() == SampleStockDto.class) {
      to = toStockSample((SampleStockDto) from);
    } else if (from.getClass() == SampleAliquotDto.class) {
      to = toAliquotSample((SampleAliquotDto) from);
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
    to.setConcentration(from.getConcentration() == null ? null : Double.valueOf(from.getConcentration()));
    if (from.getIdentityId() != null) {
      to.setIdentityId(from.getIdentityId());
    }
    to.setNonStandardAlias(from.getNonStandardAlias());
    to.setParent(getParent(from));
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
  private static DetailedSample getParent(DetailedSampleDto childDto) {
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
      }
      if (childDto instanceof SampleStockDto && childDto.getClass() != SampleStockDto.class) {
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

  public static TissueMaterialDto asDto(TissueMaterial from) {
    TissueMaterialDto dto = new TissueMaterialDto();
    dto.setId(from.getId());
    dto.setAlias(from.getAlias());
    dto.setCreatedById(from.getCreatedBy().getUserId());
    dto.setCreationDate(formatDateTime(from.getCreationDate()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(formatDateTime(from.getLastUpdated()));
    return dto;
  }

  public static Set<TissueMaterialDto> asTissueMaterialDtos(Set<TissueMaterial> from) {
    Set<TissueMaterialDto> dtoSet = Sets.newHashSet();
    for (TissueMaterial tissueMaterial : from) {
      dtoSet.add(asDto(tissueMaterial));
    }
    return dtoSet;
  }

  public static TissueMaterial to(TissueMaterialDto from) {
    TissueMaterial to = new TissueMaterialImpl();
    to.setAlias(from.getAlias());
    return to;
  }

  public static SamplePurposeDto asDto(SamplePurpose from) {
    SamplePurposeDto dto = new SamplePurposeDto();
    dto.setId(from.getId());
    dto.setAlias(from.getAlias());
    dto.setCreatedById(from.getCreatedBy().getUserId());
    dto.setCreationDate(formatDateTime(from.getCreationDate()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(formatDateTime(from.getLastUpdated()));
    return dto;
  }

  public static Set<SamplePurposeDto> asSamplePurposeDtos(Set<SamplePurpose> from) {
    Set<SamplePurposeDto> dtoSet = Sets.newHashSet();
    for (SamplePurpose samplePurpose : from) {
      dtoSet.add(asDto(samplePurpose));
    }
    return dtoSet;
  }

  public static SamplePurpose to(SamplePurposeDto from) {
    SamplePurpose to = new SamplePurposeImpl();
    to.setAlias(from.getAlias());
    return to;
  }

  public static SampleGroupDto asDto(SampleGroupId from) {
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

  public static Set<SampleGroupDto> asSampleGroupDtos(Set<SampleGroupId> from) {
    Set<SampleGroupDto> dtoSet = Sets.newHashSet();
    for (SampleGroupId sampleGroup : from) {
      dtoSet.add(asDto(sampleGroup));
    }
    return dtoSet;
  }

  public static SampleGroupId to(SampleGroupDto from) {
    SampleGroupId to = new SampleGroupImpl();
    to.setGroupId(from.getGroupId());
    to.setDescription(from.getDescription());
    return to;
  }

  private static SampleAliquotDto asAliquotSampleDto(SampleAliquot from) {
    SampleAliquotDto dto = new SampleAliquotDto();
    if (from.getSamplePurpose() != null) {
      dto.setSamplePurposeId(from.getSamplePurpose().getId());
    }
    return dto;
  }

  private static SampleStockDto asStockSampleDto(SampleStock from) {
    SampleStockDto dto = new SampleStockDto();
    dto.setStrStatus(from.getStrStatus().getLabel());
    dto.setDnaseTreated(from.getDNAseTreated());
    return dto;
  }

  private static SampleStock toStockSample(SampleStockDto from) {
    SampleStock to = new SampleStockImpl();
    if (from.getStrStatus() != null) {
      to.setStrStatus(from.getStrStatus());
    }
    to.setDNAseTreated(from.getDnaseTreated());
    return to;
  }

  private static SampleAliquot toAliquotSample(SampleAliquotDto from) {
    SampleAliquot to = new SampleAliquotImpl();
    if (from.getSamplePurposeId() != null) {
      to.setSamplePurpose(new SamplePurposeImpl());
      to.getSamplePurpose().setId(from.getSamplePurposeId());
    }
    return to;
  }

  public static SampleDto asDto(Sample from) {
    SampleDto dto = null;

    if (isDetailedSample(from)) {
      dto = asDetailedSampleDto((DetailedSample) from);
    } else {
      dto = new SampleDto();
    }
    copySampleFields(from, dto);
    dto.setAccession(from.getAccession());

    if (from.getQCs() != null && !from.getQCs().isEmpty()) {
      dto.setQcs(asQcDtos(from.getQCs()));
    }
    return dto;
  }

  public static List<SampleDto> asSampleDtos(Collection<Sample> from, boolean full) {
    List<SampleDto> dtoSet = new ArrayList<>();
    for (Sample sample : from) {
      dtoSet.add(full ? asDto(sample) : asMinimalDto(sample));
    }
    return dtoSet;
  }

  public static Sample to(SampleDto from) {
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
    if (from.getDiscarded() != null) to.setDiscarded(from.getDiscarded());
    if (from.getProjectId() != null) {
      to.setProject(new ProjectImpl());
      to.getProject().setProjectId(from.getProjectId());
    }
    return to;
  }

  private static SampleIdentityDto asIdentitySampleDto(SampleIdentity from) {
    SampleIdentityDto dto = new SampleIdentityDto();
    dto.setExternalName(from.getExternalName());
    dto.setDonorSex(from.getDonorSex().getLabel());
    return dto;
  }

  private static SampleIdentity toIdentitySample(SampleIdentityDto from) {
    SampleIdentity to = new SampleIdentityImpl();
    to.setExternalName(from.getExternalName());
    if (from.getDonorSex() != null) {
      to.setDonorSex(from.getDonorSex());
    }
    return to;
  }

  public static SampleNumberPerProjectDto asDto(SampleNumberPerProject from) {
    SampleNumberPerProjectDto dto = new SampleNumberPerProjectDto();
    dto.setId(from.getId());
    dto.setProjectId(from.getProject().getProjectId());
    dto.setHighestSampleNumber(from.getHighestSampleNumber());
    dto.setPadding(from.getPadding());
    dto.setCreatedById(from.getCreatedBy().getUserId());
    dto.setCreationDate(formatDateTime(from.getCreationDate()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(formatDateTime(from.getLastUpdated()));
    return dto;
  }

  public static Set<SampleNumberPerProjectDto> asSampleNumberPerProjectDtos(Set<SampleNumberPerProject> from) {
    Set<SampleNumberPerProjectDto> dtoSet = Sets.newHashSet();
    for (SampleNumberPerProject sampleNumberPerProject : from) {
      dtoSet.add(asDto(sampleNumberPerProject));
    }
    return dtoSet;
  }

  public static SampleNumberPerProject to(SampleNumberPerProjectDto from) {
    SampleNumberPerProject to = new SampleNumberPerProjectImpl();
    to.setHighestSampleNumber(from.getHighestSampleNumber());
    to.setPadding(from.getPadding());
    return to;
  }

  public static SampleValidRelationshipDto asDto(SampleValidRelationship from) {
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

  public static Set<SampleValidRelationshipDto> asSampleValidRelationshipDtos(Set<SampleValidRelationship> from) {
    Set<SampleValidRelationshipDto> dtoSet = Sets.newHashSet();
    for (SampleValidRelationship sampleValidRelationship : from) {
      dtoSet.add(asDto(sampleValidRelationship));
    }
    return dtoSet;
  }

  public static SampleValidRelationship to(SampleValidRelationshipDto from) {
    SampleValidRelationship to = new SampleValidRelationshipImpl();
    return to;
  }

  public static InstituteDto asDto(Institute from) {
    InstituteDto dto = new InstituteDto();
    dto.setId(from.getId());
    dto.setAlias(from.getAlias());
    dto.setCreatedById(from.getCreatedBy().getUserId());
    dto.setCreationDate(formatDateTime(from.getCreationDate()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(formatDateTime(from.getLastUpdated()));
    return dto;
  }

  public static Set<InstituteDto> asInstituteDtos(Set<Institute> from) {
    Set<InstituteDto> dtoSet = Sets.newHashSet();
    for (Institute institute : from) {
      dtoSet.add(asDto(institute));
    }
    return dtoSet;
  }

  public static Institute to(InstituteDto from) {
    Institute to = new InstituteImpl();
    to.setAlias(from.getAlias());
    return to;
  }

  public static LabDto asDto(Lab from) {
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

  public static Set<LabDto> asLabDtos(Collection<Lab> from) {
    Set<LabDto> dtoSet = Sets.newHashSet();
    for (Lab lab : from) {
      dtoSet.add(asDto(lab));
    }
    return dtoSet;
  }

  public static Lab to(LabDto from) {
    Lab to = new LabImpl();
    to.setAlias(from.getAlias());
    return to;
  }

  private static SampleTissueDto asTissueSampleDto(SampleTissue from) {
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

  private static SampleTissue toTissueSample(SampleTissueDto from) {
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

  private static SampleTissueProcessingDto asTissueProcessingSampleDto(SampleTissueProcessing from) {
    SampleTissueProcessingDto dto = null;
    from = deproxify(from);

    if (from instanceof SampleSlideImpl) {
      dto = asSlideSampleDto((SampleSlide) from);
    } else if (from.getClass() == SampleLCMTubeImpl.class) {
      dto = asLCMTubeSampleDto((SampleLCMTube) from);
    } else {
      dto = new SampleTissueProcessingDto();
    }
    return dto;
  }

  private static SampleTissueProcessing toTissueProcessingSample(SampleTissueProcessingDto from) {
    SampleTissueProcessing to = null;
    if (from.getClass() == SampleSlideDto.class) {
      to = toSlideSample((SampleSlideDto) from);
    } else if (from.getClass() == SampleLCMTubeDto.class) {
      to = toLCMTubeSample((SampleLCMTubeDto) from);
    } else {
      to = new SampleTissueProcessingImpl();
    }
    return to;
  }

  private static SampleSlideDto asSlideSampleDto(SampleSlide from) {
    SampleSlideDto dto = new SampleSlideDto();
    dto.setSlides(from.getSlides());
    dto.setDiscards(from.getDiscards());
    dto.setSlidesRemaining(from.getSlidesRemaining());
    dto.setThickness(from.getThickness());
    dto.setStain(from.getStain() == null ? null : asDto(from.getStain()));
    return dto;
  }

  private static SampleSlide toSlideSample(SampleSlideDto from) {
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

  private static SampleLCMTubeDto asLCMTubeSampleDto(SampleLCMTube from) {
    SampleLCMTubeDto dto = new SampleLCMTubeDto();
    dto.setSlidesConsumed(from.getSlidesConsumed());
    return dto;
  }

  private static SampleLCMTube toLCMTubeSample(SampleLCMTubeDto from) {
    SampleLCMTube to = new SampleLCMTubeImpl();
    to.setSlidesConsumed(from.getSlidesConsumed());
    return to;
  }

  public static KitDescriptorDto asDto(KitDescriptor from) {
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

  public static Set<KitDescriptorDto> asKitDescriptorDtos(Collection<KitDescriptor> from) {
    Set<KitDescriptorDto> dtoSet = Sets.newHashSet();
    for (KitDescriptor kd : from) {
      dtoSet.add(asDto(kd));
    }
    return dtoSet;
  }

  public static KitDescriptor to(KitDescriptorDto from) {
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

  public static LibraryDesignCodeDto asDto(LibraryDesignCode from) {
    LibraryDesignCodeDto dto = new LibraryDesignCodeDto();
    dto.setId(from.getId());
    dto.setCode(from.getCode());
    dto.setDescription(from.getDescription());
    return dto;
  }

  public static LibraryDesignCode to(LibraryDesignCodeDto from) {
    LibraryDesignCode to = new LibraryDesignCode();
    if (from.getId() != null) to.setId(from.getId());
    to.setCode(from.getCode());
    to.setDescription(from.getDescription());
    return to;
  }

  private static DetailedLibraryDto asDetailedLibraryDto(DetailedLibrary from) {
    DetailedLibraryDto dto = new DetailedLibraryDto();
    if (from.getLibraryDesign() != null) {
      dto.setLibraryDesignId(from.getLibraryDesign().getId());
    }
    dto.setLibraryDesignCodeId(from.getLibraryDesignCode().getId());
    dto.setPreMigrationId(from.getPreMigrationId());
    dto.setArchived(from.getArchived());
    dto.setNonStandardAlias(from.hasNonStandardAlias());
    if (from.getSample().getBox() != null) {
      dto.setSampleBoxPositionLabel(BoxUtils.makeBoxPositionLabel(from.getSample().getBox().getAlias(), from.getSample().getBoxPosition()));
    }
    if (from.getGroupId() != null) {
      dto.setGroupId(from.getGroupId());
    }
    if (from.getGroupDescription() != null) {
      dto.setGroupDescription(from.getGroupDescription());
    }
    return dto;
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

  public static PoolOrderDto asDto(PoolOrder from) {
    PoolOrderDto dto = new PoolOrderDto();
    dto.setId(from.getId());
    dto.setParameters(asDto(from.getSequencingParameter()));
    dto.setPartitions(from.getPartitions());
    dto.setCreatedById(from.getCreatedBy().getUserId());
    dto.setCreationDate(formatDateTime(from.getCreationDate()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(formatDateTime(from.getLastUpdated()));
    return dto;
  }

  public static Set<PoolOrderDto> asPoolOrderDtos(Collection<PoolOrder> from) {
    Set<PoolOrderDto> dtoSet = Sets.newHashSet();
    for (PoolOrder po : from) {
      dtoSet.add(asDto(po));
    }
    return dtoSet;
  }

  public static PoolOrder to(PoolOrderDto from) {
    PoolOrder to = new PoolOrderImpl();
    if (from.getId() != null) to.setId(from.getId());
    to.setPoolId(from.getPoolId());
    to.setSequencingParameter(to(from.getParameters()));
    to.setPartitions(from.getPartitions());
    return to;
  }

  public static SequencingParametersDto asDto(SequencingParameters from) {
    SequencingParametersDto dto = new SequencingParametersDto();
    dto.setId(from.getId());
    dto.setName(from.getName());
    dto.setPlatform(asDto(from.getPlatform()));
    return dto;
  }

  public static SequencingParameters to(SequencingParametersDto from) {
    SequencingParameters to = new SequencingParameters();
    to.setId(from.getId());
    to.setName(from.getName());
    if (from.getPlatform() != null) {
      to.setPlatform(to(from.getPlatform()));
    }
    return to;
  }

  public static List<SequencingParametersDto> asSequencingParametersDtos(Collection<SequencingParameters> from) {
    List<SequencingParametersDto> dtoList = new ArrayList<>();
    for (SequencingParameters sp : from) {
      dtoList.add(asDto(sp));
    }
    Collections.sort(dtoList, new Comparator<SequencingParametersDto>() {

      @Override
      public int compare(SequencingParametersDto o1, SequencingParametersDto o2) {
        return o1.getName().compareTo(o2.getName());
      }
    });
    return dtoList;
  }

  public static LibraryDto asDto(Library from) {
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
    if (from.getSample() instanceof DetailedSample) {
      dto.setParentSampleClassId(((DetailedSample) from.getSample()).getSampleClass().getId());
    }
    dto.setCreationDate(formatDate(from.getCreationDate()));
    dto.setDescription(from.getDescription());
    dto.setId(from.getId());
    if (from.getInitialConcentration() != null) {
      dto.setConcentration(from.getInitialConcentration().toString());
    }
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
    dto.setDnaSize(from.getDnaSize());
    dto.setIdentificationBarcode(from.getIdentificationBarcode());
    if (from.getQCs() != null && !from.getQCs().isEmpty()) {
      dto.setQcs(asQcDtos(from.getQCs()));
    }
    dto.setLocationBarcode(from.getLocationBarcode());
    dto.setLocationLabel(BoxUtils.makeLocationLabel(from));
    dto.setBoxId(from.getBox() == null ? null : from.getBox().getId());
    if (from.getReceivedDate() != null) {
      dto.setReceivedDate(formatDate(from.getReceivedDate()));
    }
    return dto;
  }

  public static List<LibraryDto> asLibraryDtos(Collection<Library> from) {
    List<LibraryDto> dtoSet = new ArrayList<>();
    for (Library lib : from) {
      dtoSet.add(asDto(lib));
    }
    return dtoSet;
  }

  public static Library to(LibraryDto from) {
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
    to.setInitialConcentration(from.getConcentration() == null ? null : Double.valueOf(from.getConcentration()));
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
    return to;
  }

  public static BoxDto asDto(Box from, boolean includeBoxables) {
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
    if (includeBoxables && from.getBoxables() != null) {
      dto.setItems(asBoxablesDtos(from.getBoxables()));
    }
    dto.setTubeCount(from.getTubeCount());
    return dto;
  }

  private static List<BoxableDto> asBoxablesDtos(Map<String, BoxableView> boxables) {
    List<BoxableDto> items = new ArrayList<>();
    for (Entry<String, BoxableView> entry : boxables.entrySet()) {
      items.add(asDto(entry.getValue()));
    }
    return items;
  }

  public static BoxableDto asDto(BoxableView from) {
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

  public static Box to(BoxDto from) {
    Box to = new BoxImpl();
    if (from.getId() != null) to.setId(from.getId());
    to.setAlias(from.getAlias());
    to.setDescription(from.getDescription());
    to.setIdentificationBarcode(from.getIdentificationBarcode());
    return to;
  }

  private static DilutionDto asDto(LibraryDilution from, LibraryDto libraryDto) {
    DilutionDto dto = new DilutionDto();
    dto.setId(from.getId());
    dto.setName(from.getName());
    dto.setDilutionUserName(from.getDilutionCreator());
    dto.setConcentration(from.getConcentration() == null ? null : from.getConcentration().toString());
    dto.setVolume(from.getVolume() == null ? null : from.getVolume().toString());
    if (from.getCreationDate() != null) {
      dto.setCreationDate(formatDate(from.getCreationDate()));
    }
    dto.setIdentificationBarcode(from.getIdentificationBarcode());
    dto.setLocationLabel(BoxUtils.makeLocationLabel(from));
    if (from.getTargetedSequencing() != null) {
      dto.setTargetedSequencingId(from.getTargetedSequencing().getId());
    }
    dto.setLibrary(libraryDto);
    return dto;
  }

  public static DilutionDto asMinimalDto(LibraryDilution from) {
    return asDto(from, asMinimalDto(from.getLibrary()));
  }

  public static DilutionDto asDto(LibraryDilution from) {
    return asDto(from, asDto(from.getLibrary()));

  }

  public static DilutionDto asDto(PoolableElementView from) {
    DilutionDto dto = new DilutionDto();
    dto.setId(from.getDilutionId());
    dto.setName(from.getDilutionName());
    dto.setDilutionUserName(from.getCreatorName());
    dto.setConcentration(from.getDilutionConcentration() == null ? null : from.getDilutionConcentration().toString());
    dto.setLastModified(formatDateTime(from.getLastModified()));
    dto.setCreationDate(formatDate(from.getCreated()));
    dto.setIdentificationBarcode(from.getDilutionBarcode());
    dto.setIndexIds(from.getIndices().stream().map(Index::getId).collect(Collectors.toList()));
    dto.setTargetedSequencingId(from.getTargetedSequencingId());
    dto.setVolume(from.getDilutionVolume() == null ? null : from.getDilutionVolume().toString());

    LibraryDto ldto = new LibraryDto();
    ldto.setId(from.getLibraryId());
    ldto.setName(from.getLibraryName());
    ldto.setAlias(from.getLibraryAlias());
    ldto.setIdentificationBarcode(from.getLibraryBarcode());
    ldto.setLowQuality(from.isLowQualityLibrary());
    ldto.setParentSampleId(from.getSampleId());
    ldto.setParentSampleAlias(from.getSampleAlias());
    if (from.getPlatformType() != null) {
      ldto.setPlatformType(from.getPlatformType().getKey());
    }
    dto.setLibrary(ldto);
    return dto;
  }

  public static LibraryDto asMinimalDto(Library from) {
    LibraryDto dto = new LibraryDto();
    dto.setId(from.getId());
    dto.setName(from.getName());
    dto.setAlias(from.getAlias());
    dto.setIdentificationBarcode(from.getIdentificationBarcode());
    if (from.getPlatformType() != null) {
      dto.setPlatformType(from.getPlatformType().getKey());
    }
    return dto;
  }

  public static LibraryDilution to(DilutionDto from) {
    LibraryDilution to = new LibraryDilution();
    if (from.getId() != null) to.setId(from.getId());
    if (!isStringEmptyOrNull(from.getName())) {
      to.setName(from.getName());
    }
    to.setIdentificationBarcode(from.getIdentificationBarcode());
    to.setConcentration(from.getConcentration() == null ? null : Double.valueOf(from.getConcentration()));
    to.setVolume(from.getVolume() == null ? null : Double.valueOf(from.getVolume()));
    to.setLibrary(to(from.getLibrary()));
    to.setDilutionCreator(from.getDilutionUserName());
    to.setCreationDate(parseDate(from.getCreationDate()));
    if (from.getTargetedSequencingId() != null) {
      to.setTargetedSequencing(new TargetedSequencing());
      to.getTargetedSequencing().setId(from.getTargetedSequencingId());
    }
    return to;
  }

  public static PoolDto asDto(Pool from, boolean includeContents) {
    PoolDto dto = new PoolDto();
    dto.setId(from.getId());
    dto.setName(from.getName());
    dto.setAlias(from.getAlias());
    dto.setDescription(from.getDescription());
    dto.setConcentration(from.getConcentration() == null ? null : from.getConcentration().toString());
    dto.setReadyToRun(from.getReadyToRun());
    dto.setQcPassed(from.getQcPassed());
    dto.setCreationDate(formatDate(from.getCreationDate()));
    dto.setDiscarded(from.isDiscarded());
    if (from.getVolume() != null) {
      dto.setVolume(from.getVolume().toString());
    }
    dto.setPlatformType(from.getPlatformType().name());
    dto.setLongestIndex(from.getLongestIndex());
    dto.setLastModified(formatDateTime(from.getLastModified()));
    dto.setDilutionCount(from.getPoolableElementViews().size());
    if (includeContents) {
      Set<DilutionDto> pooledElements = new HashSet<>();
      for (PoolableElementView ld : from.getPoolableElementViews()) {
        if (ld != null) {
          pooledElements.add(asDto(ld));
        }
      }
      dto.setPooledElements(pooledElements);
      dto.setDuplicateIndicesSequences(from.getDuplicateIndicesSequences());
    } else {
      dto.setPooledElements(Collections.emptySet());
    }
    dto.setDuplicateIndices(from.hasDuplicateIndices());
    dto.setIdentificationBarcode(from.getIdentificationBarcode());
    dto.setLocationLabel(BoxUtils.makeLocationLabel(from));
    dto.setBoxId(from.getBox() == null ? null : from.getBox().getId());
    dto.setHasLowQualityLibraries(from.getHasLowQualityMembers());
    return dto;
  }

  public static List<PoolDto> asPoolDtos(Collection<Pool> poolSubset, boolean includeContents) {
    List<PoolDto> dtoList = new ArrayList<>();
    for (Pool pool : poolSubset) {
      dtoList.add(asDto(pool, includeContents));
    }
    return dtoList;
  }

  public static RunDto asDto(Run from) {
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
    if (from.getSequencerReference() != null) {
      dto.setPlatformType(from.getSequencerReference().getPlatform().getPlatformType().getKey());
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
    return dto;
  }

  public static List<RunDto> asRunDtos(Collection<Run> runSubset) {
    List<RunDto> dtoList = new ArrayList<>();
    for (Run run : runSubset) {
      dtoList.add(asDto(run));
    }
    return dtoList;
  }

  public static ContainerDto asDto(SequencerPartitionContainer from) {
    ContainerDto dto = new ContainerDto();
    dto.setId(from.getId());
    dto.setIdentificationBarcode(from.getIdentificationBarcode());
    dto.setPlatform(from.getPlatform().getPlatformType().getKey());
    Run lastRun = from.getLastRun();
    if (lastRun != null) {
      dto.setLastRunAlias(lastRun.getAlias());
      dto.setLastRunId(lastRun.getId());
      dto.setLastSequencerId(lastRun.getSequencerReference().getId());
      dto.setLastSequencerName(lastRun.getSequencerReference().getName());
    }
    if (from.getLastModified() != null) {
      dto.setLastModified(formatDateTime(from.getLastModified()));
    }
    if (from.getClusteringKit() != null) {
      dto.setClusteringKit(asDto(from.getClusteringKit()));
    }
    if (from.getMultiplexingKit() != null) {
      dto.setMultiplexingKit(asDto(from.getMultiplexingKit()));
    }
    return dto;
  }

  public static List<ContainerDto> asContainerDtos(Collection<SequencerPartitionContainer> containerSubset) {
    List<ContainerDto> dtoList = new ArrayList<>();
    for (SequencerPartitionContainer container : containerSubset) {
      dtoList.add(asDto(container));
    }
    return dtoList;
  }

  public static QcTypeDto asDto(QcType from) {
    QcTypeDto dto = new QcTypeDto();
    dto.setId(from.getQcTypeId());
    dto.setName(from.getName());
    dto.setDescription(from.getDescription());
    dto.setQcTarget(from.getQcTarget());
    dto.setUnits(from.getUnits());
    dto.setPrecisionAfterDecimal(from.getPrecisionAfterDecimal());
    dto.setArchived(from.isArchived());
    return dto;
  }

  public static QcType to(QcTypeDto from) {
    QcType to = new QcType();
    if (from.getId() != null) to.setQcTypeId(from.getId());
    to.setName(from.getName());
    to.setDescription(from.getDescription());
    to.setQcTarget(from.getQcTarget());
    to.setUnits(from.getUnits());
    to.setPrecisionAfterDecimal(from.getPrecisionAfterDecimal());
    to.setArchived(from.isArchived());
    return to;
  }

  public static QcDto asDto(QC from) {
    QcDto dto = new QcDto();
    dto.setId(from.getId());
    dto.setDate(formatDate(from.getDate()));
    dto.setCreator(from.getCreator().getFullName());
    dto.setType(asDto(from.getType()));
    dto.setResults(from.getResults());
    dto.setEntityId(from.getEntity().getId());
    dto.setEntityAlias(from.getEntity().getAlias());
    return dto;
  }

  public static List<QcDto> asQcDtos(Collection<? extends QC> qcSubset) {
    List<QcDto> dtoList = new ArrayList<>();
    for (QC qc : qcSubset) {
      dtoList.add(asDto(qc));
    }
    return dtoList;
  }

  public static List<QcTypeDto> asQcTypeDtos(Collection<QcType> qcTypeSubset) {
    List<QcTypeDto> dtoList = new ArrayList<>();
    for (QcType qcType : qcTypeSubset) {
      dtoList.add(asDto(qcType));
    }
    return dtoList;
  }

  public static PoolOrderCompletionDto asDto(PoolOrderCompletion from) {
    PoolOrderCompletionDto dto = new PoolOrderCompletionDto();
    dto.setPool(asDto(from.getPool(), false));
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
    return dto;
  }

  public static PlatformDto asDto(Platform from) {
    PlatformDto dto = new PlatformDto();
    dto.setId(from.getId());
    dto.setPlatformType(from.getPlatformType().name());
    dto.setDescription(from.getDescription());
    dto.setInstrumentModel(from.getInstrumentModel());
    dto.setNumContainers(from.getNumContainers());
    dto.setPartitionSizes(from.getPartitionSizes());
    return dto;
  }

  public static Platform to(PlatformDto from) {
    Platform to = new Platform();
    to.setId(from.getId());
    to.setPlatformType(PlatformType.get(from.getPlatformType()));
    to.setDescription(from.getDescription());
    to.setInstrumentModel(from.getInstrumentModel());
    to.setNumContainers(from.getNumContainers());
    return to;
  }

  public static ProjectDto asDto(Project from) {
    ProjectDto dto = new ProjectDto();
    dto.setId(from.getId());
    dto.setName(from.getName());
    dto.setAlias(from.getAlias());
    dto.setShortName(from.getShortName());
    dto.setDescription(from.getDescription());
    dto.setProgress(from.getProgress().getKey());
    return dto;
  }

  public static List<ProjectDto> asProjectDtos(Collection<Project> projects) {
    List<ProjectDto> dtoList = new ArrayList<>();
    for (Project project : projects) {
      dtoList.add(asDto(project));
    }
    return dtoList;
  }

  public static LibraryDesignDto asDto(LibraryDesign from) {
    LibraryDesignDto dto = new LibraryDesignDto();
    dto.setId(from.getId());
    dto.setName(from.getName());
    dto.setDesignCodeId(from.getLibraryDesignCode().getId());
    dto.setSampleClassId(from.getSampleClass().getId());
    dto.setSelectionId(from.getLibrarySelectionType().getId());
    dto.setStrategyId(from.getLibraryStrategyType().getId());
    return dto;
  }

  public static LibraryTypeDto asDto(LibraryType from) {
    LibraryTypeDto dto = new LibraryTypeDto();
    dto.setId(from.getId());
    dto.setAlias(from.getDescription());
    dto.setArchived(from.getArchived());
    dto.setPlatform(from.getPlatformType().name());
    return dto;
  }

  public static LibrarySelectionTypeDto asDto(LibrarySelectionType from) {
    LibrarySelectionTypeDto dto = new LibrarySelectionTypeDto();
    dto.setId(from.getId());
    dto.setAlias(from.getDescription());
    dto.setName(from.getName());
    return dto;
  }

  public static LibraryStrategyTypeDto asDto(LibraryStrategyType from) {
    LibraryStrategyTypeDto dto = new LibraryStrategyTypeDto();
    dto.setId(from.getId());
    dto.setDescription(from.getDescription());
    dto.setName(from.getName());
    return dto;
  }

  public static IndexDto asDto(Index from) {
    return asDto(from, true);
  }

  public static IndexDto asDto(Index from, boolean includeFamily) {
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

  public static IndexFamilyDto asDto(IndexFamily from) {
    return asDto(from, true);
  }

  private static IndexFamilyDto asDto(IndexFamily from, boolean includeChildren) {
    IndexFamilyDto dto = new IndexFamilyDto();
    dto.setId(from.getId());
    dto.setName(from.getName());
    dto.setArchived(from.getArchived());
    if (includeChildren) {
      dto.setIndices(from.getIndices().stream().map(x -> asDto(x, false)).collect(Collectors.toList()));
    }
    dto.setMaximumNumber(from.getMaximumNumber());
    dto.setPlatformType(from.getPlatformType() == null ? null : from.getPlatformType().name());
    return dto;
  }

  public static StainDto asDto(Stain from) {
    StainDto dto = new StainDto();
    dto.setId(from.getId());
    dto.setCategory(from.getCategory() == null ? null : from.getCategory().getName());
    dto.setName(from.getName());
    return dto;
  }

  public static Run to(NotificationDto from, User user) {
    final Run to = from.getPlatformType().createRun(user);
    setCommonRunValues(from, to);

    switch (to.getPlatformType()) {
    case PACBIO:
      break;
    case ILLUMINA:
      setIlluminaRunValues((IlluminaNotificationDto) from, (IlluminaRun) to);
      break;
    case LS454:
      to.setPairedEnd(((LS454NotificationDto) from).isPairedEndRun());
      ((LS454Run) to).setCycles(((LS454NotificationDto) from).getCycles());
      break;
    case SOLID:
      to.setPairedEnd(((SolidNotificationDto) from).isPairedEndRun());
      break;
    default:
      throw new NotImplementedException();
    }
    return to;
  }

  private static void setIlluminaRunValues(IlluminaNotificationDto from, IlluminaRun to) {
    to.setPairedEnd(from.isPairedEndRun());
    to.setNumCycles(from.getNumCycles());
    to.setImgCycle(from.getImgCycle());
    to.setCallCycle(from.getCallCycle());
    to.setScoreCycle(from.getScoreCycle());
  }

  private static void setCommonRunValues(NotificationDto from, Run to) {
    to.setAlias(from.getRunAlias());
    to.setFilePath(from.getSequencerFolderPath());
    to.setHealth(from.getHealthType());
    to.setStartDate(LimsUtils.toBadDate(from.getStartDate()));
    to.setCompletionDate(LimsUtils.toBadDate(from.getCompletionDate()));
    to.setMetrics(from.getMetrics());
  }

  public static TargetedSequencingDto asDto(TargetedSequencing from) {
    TargetedSequencingDto dto = new TargetedSequencingDto();
    dto.setId(from.getId());
    dto.setAlias(from.getAlias());
    dto.setKitDescriptorIds(from.getKitDescriptors().stream().map(KitDescriptor::getId).collect(Collectors.toList()));
    return dto;
  }

  public static Pool to(PoolDto dto) {
    PoolImpl to = new PoolImpl();
    to.setId(dto.getId() == null ? PoolImpl.UNSAVED_ID : dto.getId());
    to.setAlias(dto.getAlias());
    to.setConcentration(dto.getConcentration() == null ? null : Double.valueOf(dto.getConcentration()));
    to.setCreationDate(parseDate(dto.getCreationDate()));
    to.setDescription(dto.getDescription());
    to.setIdentificationBarcode(dto.getIdentificationBarcode());
    to.setDiscarded(dto.isDiscarded());
    if (dto.getVolume() != null) {
      to.setVolume(Double.valueOf(dto.getVolume()));
    }
    to.setPlatformType(PlatformType.valueOf(dto.getPlatformType()));
    to.setPoolableElementViews(dto.getPooledElements().stream().map(dilution -> {
      PoolableElementView view = new PoolableElementView();
      view.setDilutionId(dilution.getId());
      view.setDilutionName(dilution.getName());
      return view;
    }).collect(Collectors.toSet()));
    to.setQcPassed(dto.getQcPassed());
    to.setReadyToRun(dto.getReadyToRun());
    return to;
  }

  public static PrinterBackendDto asDto(Backend from) {
    PrinterBackendDto dto = new PrinterBackendDto();
    dto.setId(from.ordinal());
    dto.setName(from.name());
    dto.setConfigurationKeys(from.getConfigurationKeys());
    return dto;
  }

  public static PrinterDriverDto asDto(Driver from) {
    PrinterDriverDto dto = new PrinterDriverDto();
    dto.setId(from.ordinal());
    dto.setName(from.name());
    return dto;
  }

  public static PrinterDto asDto(Printer from) {
    PrinterDto dto = new PrinterDto();
    dto.setId(from.getId());
    dto.setAvailable(from.isEnabled());
    dto.setBackend(from.getBackend().name());
    // We intentionally do not pass configuration to the front end since it has passwords in it.
    dto.setDriver(from.getDriver().name());
    dto.setName(from.getName());
    return dto;
  }

  public static Printer to(PrinterDto dto) throws JsonProcessingException {
    Printer to = new Printer();
    to.setId(dto.getId());
    to.setBackend(Backend.valueOf(dto.getBackend()));
    to.setConfiguration(new ObjectMapper().writeValueAsString(dto.getConfiguration()));
    to.setDriver(Driver.valueOf(dto.getDriver()));
    to.setEnabled(dto.isAvailable());
    to.setName(dto.getName());

    return to;
  }

  public static StudyDto asDto(Study from) {
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

  public static StudyTypeDto asDto(StudyType from) {
    StudyTypeDto dto = new StudyTypeDto();
    dto.setId(from.getId());
    dto.setName(from.getName());
    return dto;
  }

  public static ChangeLogDto asDto(ChangeLog from) {
    ChangeLogDto dto = new ChangeLogDto();
    dto.setSummary(from.getSummary());
    dto.setTime(formatDateTime(from.getTime()));
    dto.setUserName(from.getUser().getFullName());
    return dto;
  }

  public static UserDto asDto(User from) {
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

  public static GroupDto asDto(Group from) {
    GroupDto dto = new GroupDto();
    dto.setId(from.getGroupId());
    dto.setDescription(from.getDescription());
    dto.setName(from.getName());
    return dto;
  }

  public static SequencerDto asDto(SequencerReference from) {
    SequencerDto dto = new SequencerDto();
    dto.setId(from.getId());
    dto.setDateCommissioned(formatDate(from.getDateCommissioned()));
    dto.setDateDecommissioned(formatDate(from.getDateDecommissioned()));
    dto.setIp(from.getIpAddress());
    dto.setName(from.getName());
    dto.setPlatform(asDto(from.getPlatform()));
    dto.setSerialNumber(from.getSerialNumber());
    return dto;
  }

  public static SequencerReference to(SequencerDto dto) {
    SequencerReference to = new SequencerReferenceImpl();
    to.setId(dto.getId());
    to.setDateCommissioned(parseDate(dto.getDateCommissioned()));
    to.setDateDecommissioned(parseDate(dto.getDateDecommissioned()));
    to.setIpAddress(dto.getIp());
    to.setName(dto.getName());
    to.setPlatform(to(dto.getPlatform()));
    to.setSerialNumber(dto.getSerialNumber());
    return to;
  }

  public static QC to(QcDto dto) {
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
    default:
      throw new IllegalArgumentException("No such QC target: " + dto.getType().getQcTarget());
    }
    if (dto.getId() != null) {
      to.setId(dto.getId());
    }
    to.setDate(parseDate(dto.getDate()));
    to.setResults(dto.getResults());
    to.setType(to(dto.getType()));
    return to;
  }

  public static ReferenceGenomeDto asDto(ReferenceGenome from) {
    ReferenceGenomeDto dto = new ReferenceGenomeDto();
    dto.setId(from.getId());
    dto.setAlias(from.getAlias());
    return dto;
  }

  public static PartitionDto asDto(Partition from) {
    PartitionDto dto = new PartitionDto();
    dto.setId(from.getId());
    dto.setContainerId(from.getSequencerPartitionContainer().getId());
    dto.setContainerName(from.getSequencerPartitionContainer().getIdentificationBarcode());
    dto.setPartitionNumber(from.getPartitionNumber());
    dto.setPool(from.getPool() == null ? null : asDto(from.getPool(), false));
    return dto;
  }

  public static PartitionQCTypeDto asDto(PartitionQCType from) {
    PartitionQCTypeDto dto = new PartitionQCTypeDto();
    dto.setId(from.getId());
    dto.setDescription(from.getDescription());
    dto.setNoteRequired(from.isNoteRequired());
    return dto;
  }

  public static ExperimentDto asDto(Experiment from) {
    ExperimentDto dto = new ExperimentDto();
    dto.setId(from.getId());
    dto.setAccession(from.getAccession());
    dto.setAlias(from.getAlias());
    dto.setDescription(from.getDescription());
    dto.setName(from.getName());
    dto.setPlatform(asDto(from.getPlatform()));
    dto.setLibrary(asDto(from.getLibrary()));
    dto.setPartitions(from.getRunPartitions().stream()
        .map(entry -> new ExperimentDto.RunPartitionDto(asDto(entry.getRun()), asDto(entry.getPartition()))).collect(Collectors.toList()));
    dto.setStudy(asDto(from.getStudy()));
    dto.setTitle(from.getTitle());
    return dto;
  }

  public static KitConsumableDto asDto(Kit from) {
    KitConsumableDto dto = new KitConsumableDto();
    dto.setId(from.getId());
    dto.setDate(formatDate(from.getKitDate()));
    dto.setDescriptor(asDto(from.getKitDescriptor()));
    dto.setLotNumber(from.getLotNumber());
    return dto;
  }

  public static Kit to(KitConsumableDto dto) {
    Kit to = new KitImpl();
    if (dto.getId() != null) {
      to.setId(dto.getId());
    }
    to.setKitDate(parseDate(dto.getDate()));
    to.setKitDescriptor(to(dto.getDescriptor()));
    to.setLotNumber(dto.getLotNumber());
    return to;
  }

  public static Experiment to(ExperimentDto dto) {
    Experiment to = new Experiment();
    to.setId(dto.getId());
    to.setAlias(dto.getAlias());
    to.setDescription(dto.getDescription());
    to.setLibrary(to(dto.getLibrary()));
    to.setPlatform(to(dto.getPlatform()));
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

  public static Study to(StudyDto dto) {
    Study to = new StudyImpl();
    to.setId(dto.getId());
    return to;
  }

  public static Run to(RunDto dto) {
    Run to = PlatformType.get(dto.getPlatformType()).createRun(null);
    to.setId(dto.getId());
    return to;
  }

  public static Partition to(PartitionDto dto) {
    Partition to = new PartitionImpl();
    to.setId(dto.getId());
    return to;
  }

  public static SubmissionDto asDto(Submission from) {
    SubmissionDto dto = new SubmissionDto();
    dto.setId(from.getId());
    dto.setAccession(from.getAccession());
    dto.setAlias(from.getAlias());
    dto.setCompleted(from.isCompleted());
    dto.setCreationDate(formatDate(from.getCreationDate()));
    dto.setDescription(from.getDescription());
    dto.setName(from.getName());
    dto.setSubmittedDate(formatDate(from.getSubmissionDate()));
    dto.setTitle(from.getTitle());
    dto.setVerified(from.isVerified());
    return dto;
  }
}
