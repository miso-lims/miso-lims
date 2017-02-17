package uk.ac.bbsrc.tgac.miso.dto;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Dilution;
import uk.ac.bbsrc.tgac.miso.core.data.Identity;
import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.Institute;
import uk.ac.bbsrc.tgac.miso.core.data.Lab;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.PoolOrder;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleCVSlide;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleGroupId;
import uk.ac.bbsrc.tgac.miso.core.data.SampleLCMTube;
import uk.ac.bbsrc.tgac.miso.core.data.SampleNumberPerProject;
import uk.ac.bbsrc.tgac.miso.core.data.SamplePurpose;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;
import uk.ac.bbsrc.tgac.miso.core.data.SampleValidRelationship;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.Subproject;
import uk.ac.bbsrc.tgac.miso.core.data.TissueMaterial;
import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;
import uk.ac.bbsrc.tgac.miso.core.data.TissueType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedLibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedQcStatusImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedSampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.IdentityImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.InstituteImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LabImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolOrderImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleAliquotImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleCVSlideImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleClassImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleGroupImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleLCMTubeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleNumberPerProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SamplePurposeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleStockImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissueImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissueProcessingImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleValidRelationshipImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SubprojectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueMaterialImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueOriginImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueTypeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;

public class Dtos {

  private static DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis();
  private static DateTimeFormatter dateFormatter = ISODateTimeFormat.date();

  public static TissueOriginDto asDto(TissueOrigin from) {
    TissueOriginDto dto = new TissueOriginDto();
    dto.setId(from.getId());
    dto.setAlias(from.getAlias());
    dto.setDescription(from.getDescription());
    dto.setLabel(from.getItemLabel());
    dto.setCreatedById(from.getCreatedBy().getUserId());
    dto.setCreationDate(dateTimeFormatter.print(from.getCreationDate().getTime()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(dateTimeFormatter.print(from.getLastUpdated().getTime()));
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
    to.setId(from.getId());
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
    dto.setCreationDate(dateTimeFormatter.print(from.getCreationDate().getTime()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(dateTimeFormatter.print(from.getLastUpdated().getTime()));
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
    to.setId(from.getId());
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
    dto.setCreationDate(dateTimeFormatter.print(from.getCreationDate().getTime()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(dateTimeFormatter.print(from.getLastUpdated().getTime()));
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
    dto.setCreationDate(dateTimeFormatter.print(from.getCreationDate().getTime()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(dateTimeFormatter.print(from.getLastUpdated().getTime()));
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
    dto.setCreationDate(dateTimeFormatter.print(from.getCreationDate().getTime()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(dateTimeFormatter.print(from.getLastUpdated().getTime()));
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

  private static DetailedSampleDto asDetailedSampleDto(DetailedSample from) {
    DetailedSampleDto dto = null;
    if (isIdentitySample(from)) {
      dto = asIdentitySampleDto((Identity) from);
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
    dto.setNonStandardAlias(from.hasNonStandardAlias());
    if (from.getDetailedQcStatus() != null) {
      dto.setDetailedQcStatusId(from.getDetailedQcStatus().getId());
    }
    if (!isStringEmptyOrNull(from.getDetailedQcStatusNote())) {
      dto.setDetailedQcStatusNote(from.getDetailedQcStatusNote());
    }
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
    if (!isStringEmptyOrNull(from.getDetailedQcStatusNote())) {
      to.setDetailedQcStatusNote(from.getDetailedQcStatusNote());
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
    if (from.getGroupId() != null) {
      to.setGroupId(from.getGroupId());
      to.setGroupDescription(from.getGroupDescription());
    }
    if (from.getSynthetic() != null) {
      to.setSynthetic(from.getSynthetic());
    }
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
   * identity may or may not yet exist. If the sampleClassId is an aliquot, a stockClassId must be provided.</li>
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
        DetailedSample stock = toStockSample((SampleStockDto) childDto);
        stock.setSampleClass(new SampleClassImpl());
        stock.getSampleClass().setId(((SampleAliquotDto) childDto).getStockClassId());
        stock.setParent(parent);
        parent = stock;
      }
    }
    return parent;
  }

  public static TissueMaterialDto asDto(TissueMaterial from) {
    TissueMaterialDto dto = new TissueMaterialDto();
    dto.setId(from.getId());
    dto.setAlias(from.getAlias());
    dto.setCreatedById(from.getCreatedBy().getUserId());
    dto.setCreationDate(dateTimeFormatter.print(from.getCreationDate().getTime()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(dateTimeFormatter.print(from.getLastUpdated().getTime()));
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
    dto.setCreationDate(dateTimeFormatter.print(from.getCreationDate().getTime()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(dateTimeFormatter.print(from.getLastUpdated().getTime()));
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
    dto.setCreationDate(dateTimeFormatter.print(from.getCreationDate().getTime()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(dateTimeFormatter.print(from.getLastUpdated().getTime()));
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
    dto.setConcentration(from.getConcentration());
    dto.setStrStatus(from.getStrStatus().getLabel());
    dto.setDnaseTreated(from.getDNAseTreated());
    return dto;
  }

  private static SampleStock toStockSample(SampleStockDto from) {
    SampleStock to = new SampleStockImpl();
    to.setConcentration(from.getConcentration());
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
    dto.setId(from.getId());
    if (!isStringEmptyOrNull(from.getAccession())) {
      dto.setAccession(from.getAccession());
    }
    dto.setName(from.getName());
    dto.setDescription(from.getDescription());
    dto.setUpdatedById(from.getLastModifier().getUserId());
    if (!isStringEmptyOrNull(from.getIdentificationBarcode())) {
      dto.setIdentificationBarcode(from.getIdentificationBarcode());
    }
    dto.setLocationLabel(BoxUtils.makeLocationLabel(from));
    dto.setSampleType(from.getSampleType());
    if (from.getReceivedDate() != null) {
      dto.setReceivedDate(dateTimeFormatter.print(from.getReceivedDate().getTime()));
    }
    if (from.getQcPassed() != null) {
      dto.setQcPassed(from.getQcPassed());
    }
    if (!isStringEmptyOrNull(from.getAlias())) {
      dto.setAlias(from.getAlias());
    }
    dto.setProjectId(from.getProject().getProjectId());
    dto.setScientificName(from.getScientificName());
    if (!isStringEmptyOrNull(from.getTaxonIdentifier())) {
      dto.setTaxonIdentifier(from.getTaxonIdentifier());
    }
    dto.setVolume(from.getVolume());
    dto.setDiscarded(from.isDiscarded());
    dto.setLastModified(getDateAsString(from.getLastModified()));

    return dto;
  }

  public static List<SampleDto> asSampleDtos(Collection<Sample> from) {
    List<SampleDto> dtoSet = new ArrayList<>();
    for (Sample sample : from) {
      dtoSet.add(asDto(sample));
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

    if (from.getId() != null) {
      to.setId(from.getId());
    }
    if (!isStringEmptyOrNull(from.getAccession())) {
      to.setAccession(from.getAccession());
    }
    to.setName(from.getName());
    to.setDescription(from.getDescription());
    if (!isStringEmptyOrNull(from.getIdentificationBarcode())) {
      to.setIdentificationBarcode(from.getIdentificationBarcode());
    }
    if (!isStringEmptyOrNull(from.getLocationLabel())) {
      to.setLocationBarcode(from.getLocationLabel());
    }
    to.setSampleType(from.getSampleType());
    if (from.getReceivedDate() != null) {
      to.setReceivedDate(dateFormatter.parseDateTime(from.getReceivedDate()).toDate());
    }
    to.setQcPassed(from.getQcPassed());
    if (!isStringEmptyOrNull(from.getAlias())) {
      to.setAlias(from.getAlias());
    }
    to.setScientificName(from.getScientificName());
    if (!isStringEmptyOrNull(from.getTaxonIdentifier())) {
      to.setTaxonIdentifier(from.getTaxonIdentifier());
    }
    to.setAlias(from.getAlias());
    to.setDescription(from.getDescription());
    to.setVolume(from.getVolume());
    if (from.getDiscarded() != null) to.setDiscarded(from.getDiscarded());
    if (from.getProjectId() != null) {
      to.setProject(new ProjectImpl());
      to.getProject().setProjectId(from.getProjectId());
    }
    return to;
  }

  private static SampleIdentityDto asIdentitySampleDto(Identity from) {
    SampleIdentityDto dto = new SampleIdentityDto();
    dto.setExternalName(from.getExternalName());
    dto.setDonorSex(from.getDonorSex().getLabel());
    return dto;
  }

  private static Identity toIdentitySample(SampleIdentityDto from) {
    Identity to = new IdentityImpl();
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
    dto.setCreationDate(dateTimeFormatter.print(from.getCreationDate().getTime()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(dateTimeFormatter.print(from.getLastUpdated().getTime()));
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
    dto.setCreationDate(dateTimeFormatter.print(from.getCreationDate().getTime()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(dateTimeFormatter.print(from.getLastUpdated().getTime()));
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
    dto.setCreationDate(dateTimeFormatter.print(from.getCreationDate().getTime()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(dateTimeFormatter.print(from.getLastUpdated().getTime()));
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
    dto.setAlias(from.getAlias());
    dto.setCreatedById(from.getCreatedBy().getUserId());
    dto.setCreationDate(dateTimeFormatter.print(from.getCreationDate().getTime()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(dateTimeFormatter.print(from.getLastUpdated().getTime()));
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
    dto.setExternalInstituteIdentifier(from.getExternalInstituteIdentifier());
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
    to.setRegion(from.getRegion());
    to.setExternalInstituteIdentifier(from.getExternalInstituteIdentifier());
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
    return to;
  }

  private static SampleTissueProcessingDto asTissueProcessingSampleDto(SampleTissueProcessing from) {
    SampleTissueProcessingDto dto = null;
    if (from instanceof SampleCVSlideImpl) {
      dto = asCVSlideSampleDto((SampleCVSlide) from);
    } else if (from.getClass() == SampleLCMTubeImpl.class) {
      dto = asLCMTubeSampleDto((SampleLCMTube) from);
    } else {
      dto = new SampleTissueProcessingDto();
    }
    return dto;
  }

  private static SampleTissueProcessing toTissueProcessingSample(SampleTissueProcessingDto from) {
    SampleTissueProcessing to = null;
    if (from.getClass() == SampleCVSlideDto.class) {
      to = toCVSlideSample((SampleCVSlideDto) from);
    } else if (from.getClass() == SampleLCMTubeDto.class) {
      to = toLCMTubeSample((SampleLCMTubeDto) from);
    } else {
      to = new SampleTissueProcessingImpl();
    }
    return to;
  }

  private static SampleCVSlideDto asCVSlideSampleDto(SampleCVSlide from) {
    SampleCVSlideDto dto = new SampleCVSlideDto();
    dto.setSlides(from.getSlides());
    dto.setDiscards(from.getDiscards());
    dto.setSlidesRemaining(from.getSlidesRemaining());
    dto.setThickness(from.getThickness());
    return dto;
  }

  private static SampleCVSlide toCVSlideSample(SampleCVSlideDto from) {
    SampleCVSlide to = new SampleCVSlideImpl();
    to.setSlides(from.getSlides());
    to.setDiscards(from.getDiscards());
    to.setThickness(from.getThickness());
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
    if (from.getKitDescriptor() != null) {
      dto.setKitDescriptorId(from.getKitDescriptor().getId());
    }
    if (from.getLibraryDesign() != null) {
      dto.setLibraryDesignId(from.getLibraryDesign().getId());
    }
    dto.setLibraryDesignCodeId(from.getLibraryDesignCode().getId());
    dto.setPreMigrationId(from.getPreMigrationId());
    dto.setArchived(from.getArchived());
    dto.setNonStandardAlias(from.hasNonStandardAlias());
    return dto;
  }

  public static DetailedLibrary toDetailedLibrary(DetailedLibraryDto from) {
    if (from == null) return null;
    DetailedLibrary to = new DetailedLibraryImpl();
    if (from.getKitDescriptorId() != null) {
      KitDescriptor kitDescriptor = new KitDescriptor();
      kitDescriptor.setId(from.getKitDescriptorId());
      to.setKitDescriptor(kitDescriptor);
    }
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
    return to;
  }

  public static PoolOrderDto asDto(PoolOrder from) {
    PoolOrderDto dto = new PoolOrderDto();
    dto.setId(from.getId());
    dto.setParameters(asDto(from.getSequencingParameter()));
    dto.setPartitions(from.getPartitions());
    dto.setCreatedById(from.getCreatedBy().getUserId());
    dto.setCreationDate(dateTimeFormatter.print(from.getCreationDate().getTime()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(dateTimeFormatter.print(from.getLastUpdated().getTime()));
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
    to.setId(from.getId());
    to.setPartitions(from.getPartitions());
    return to;
  }

  public static SequencingParametersDto asDto(SequencingParameters from) {
    SequencingParametersDto dto = new SequencingParametersDto();
    dto.setId(from.getId());
    dto.setName(from.getName());
    dto.setPlatformId(from.getPlatform().getId());
    return dto;
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
    dto.setCreationDate(dateTimeFormatter.print(from.getCreationDate().getTime()));
    dto.setDescription(from.getDescription());
    dto.setId(from.getId());
    dto.setConcentration(from.getInitialConcentration());
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
    if (from.getLastModified() != null) {
      dto.setLastModified(getDateAsString(from.getLastModified()));
    }
    if (!from.getIndices().isEmpty()) {
      dto.setIndexFamilyName(from.getIndices().get(0).getFamily().getName());
      dto.setIndex1Id(from.getIndices().get(0).getId());
      dto.setIndex1Label(from.getIndices().get(0).getLabel());
      if (from.getIndices().size() > 1) {
        dto.setIndex2Id(from.getIndices().get(1).getId());
        dto.setIndex2Label(from.getIndices().get(1).getLabel());
      }
    }
    dto.setVolume(from.getVolume());
    if (!isStringEmptyOrNull(from.getIdentificationBarcode())) {
      dto.setIdentificationBarcode(from.getIdentificationBarcode());
    }
    dto.setLocationLabel(BoxUtils.makeLocationLabel(from));
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
    to.setAlias(from.getAlias());
    to.setName(from.getName());
    to.setDescription(from.getDescription());
    to.setIdentificationBarcode(from.getIdentificationBarcode());
    to.setInitialConcentration(from.getConcentration());
    to.setLowQuality(from.getLowQuality());
    to.setPaired(from.getPaired());
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
    to.setVolume(from.getVolume());

    return to;
  }

  public static BoxDto asDto(Box from) {
    BoxDto dto = new BoxDto();
    dto.setId(from.getId());
    dto.setAlias(from.getAlias());
    dto.setDescription(from.getDescription());
    dto.setIdentificationBarcode(from.getIdentificationBarcode());
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
    if (from.getBoxables() != null) {
      dto.setItems(asBoxablesDtos(from.getBoxables()));
    }
    return dto;
  }

  public static List<BoxableDto> asBoxablesDtos(Map<String, Boxable> boxables) {
    List<BoxableDto> items = new ArrayList<>();
    for (Entry<String, Boxable> entry : boxables.entrySet()) {
      items.add(asDto(entry.getValue()));
    }
    return items;
  }

  public static BoxableDto asDto(Boxable from) {
    BoxableDto dto = new BoxableDto();
    dto.setId(from.getId());
    dto.setAlias(from.getAlias());
    if (from.getBox() != null) {
      dto.setBoxAlias(from.getBox().getAlias());
      dto.setBoxPosition(BoxUtils.makeLocationLabel(from));
      dto.setCoordinates(from.getBoxPosition());
    }
    dto.setDiscarded(from.isDiscarded());
    dto.setIdentificationBarcode(from.getIdentificationBarcode());
    dto.setName(from.getName());
    dto.setVolume(from.getVolume());
    return dto;
  }

  public static Box to(BoxDto from) {
    Box to = new BoxImpl();
    to.setId(from.getId());
    to.setAlias(from.getAlias());
    to.setDescription(from.getDescription());
    to.setIdentificationBarcode(from.getIdentificationBarcode());
    return to;
  }

  public static Boxable to(BoxableDto item, Boxable to) {
    to.setAlias(item.getAlias());
    to.setVolume(item.getVolume());
    to.setDiscarded(item.getDiscarded());
    return to;
  }

  public static DilutionDto asDto(Dilution from) {
    DilutionDto dto = new DilutionDto();
    dto.setId(from.getId());
    dto.setName(from.getName());
    if (!isStringEmptyOrNull(from.getIdentificationBarcode())) {
      dto.setIdentificationBarcode(from.getIdentificationBarcode());
    }
    LibraryDto ldto = asMinimalDto(from.getLibrary());
    dto.setLibrary(ldto);
    return dto;
  }

  public static LibraryDto asMinimalDto(Library from) {
    LibraryDto dto = new LibraryDto();
    dto.setId(from.getId());
    dto.setName(from.getName());
    dto.setAlias(from.getAlias());
    if (from.getIdentificationBarcode() != null) {
      dto.setIdentificationBarcode(from.getIdentificationBarcode());
    }
    return dto;
  }

  public static LibraryDilution to(DilutionDto from) {
    LibraryDilution to = new LibraryDilution();
    if (from.getId() != null) {
      to.setId(from.getId());
    }
    if (!isStringEmptyOrNull(from.getName())) {
      to.setName(from.getName());
    }
    if (!isStringEmptyOrNull(from.getIdentificationBarcode())) {
      to.setIdentificationBarcode(from.getIdentificationBarcode());
    }
    to.setConcentration(from.getConcentration());
    to.setLibrary(to(from.getLibrary()));
    if (!isStringEmptyOrNull(from.getDilutionUserName())) {
      to.setDilutionCreator(from.getDilutionUserName());
    }
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    try {
      to.setCreationDate(df.parse(from.getCreationDate()));
    } catch (ParseException e) {
      // do nothing because this shouldn't cause it to fail, and the Dtos class does not have a logger
    }
    TargetedSequencing ts = new TargetedSequencing();
    if (from.getTargetedSequencingId() != null) ts.setId(from.getTargetedSequencingId());
    to.setTargetedSequencing(ts);
    return to;
  }

  public static PoolDto asDto(Pool from) {
    PoolDto dto = new PoolDto();
    dto.setId(from.getId());
    dto.setName(from.getName());
    dto.setAlias(from.getAlias());
    if (!isStringEmptyOrNull(from.getDescription())) {
      dto.setDescription(from.getDescription());
    }
    dto.setConcentration(from.getConcentration());
    dto.setReadyToRun(from.getReadyToRun());
    dto.setQcPassed(from.getQcPassed());
    dto.setCreationDate(getDateAsString(from.getCreationDate()));
    if (from.getLastModified() != null) {
      dto.setLastModified(getDateAsString(from.getLastModified()));
    }
    Set<DilutionDto> pooledElements = new HashSet<>();
    for (Dilution ld : from.getPoolableElements()) {
      if (ld != null) {
        pooledElements.add(asDto(ld));
      }
    }
    dto.setPooledElements(pooledElements);
    if (!isStringEmptyOrNull(from.getIdentificationBarcode())) {
      dto.setIdentificationBarcode(from.getIdentificationBarcode());
    }
    dto.setLocationLabel(BoxUtils.makeLocationLabel(from));
    return dto;
  }

  public static List<PoolDto> asPoolDtos(Collection<Pool> poolSubset) {
    List<PoolDto> dtoList = new ArrayList<>();
    for (Pool pool : poolSubset) {
      dtoList.add(asDto(pool));
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
    if (from.getLastUpdated() != null) {
      dto.setLastModified(getDateAsString(from.getLastUpdated()));
    }
    if (from.getSequencerReference() != null) {
      dto.setPlatformType(from.getSequencerReference().getPlatform().getPlatformType().getKey());
    } else {
      dto.setPlatformType("");
    }
    if (from.getStartDate() != null) {
      dto.setStartDate(getDateAsString(from.getStartDate()));
    } else {
      dto.setStartDate("");
    }
    if (from.getCompletionDate() != null) {
      dto.setEndDate(getDateAsString(from.getStartDate()));
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

  public static ContainerDto asDto(SequencerPartitionContainer<SequencerPoolPartition> from) {
    ContainerDto dto = new ContainerDto();
    dto.setId(from.getId());
    dto.setIdentificationBarcode(from.getIdentificationBarcode());
    dto.setPlatform(from.getPlatform().getPlatformType().getKey());
    Run lastRun = from.getLastRun();
    if (lastRun != null) {
      dto.setLastRunAlias(lastRun.getAlias());
      dto.setLastRunId(lastRun.getId());
    }
    if (from.getLastModified() != null) {
      dto.setLastModified(getDateAsString(from.getLastModified()));
    }
    return dto;
  }

  public static List<ContainerDto> asContainerDtos(Collection<SequencerPartitionContainer<SequencerPoolPartition>> containerSubset) {
    List<ContainerDto> dtoList = new ArrayList<>();
    for (SequencerPartitionContainer<SequencerPoolPartition> container : containerSubset) {
      dtoList.add(asDto(container));
    }
    return dtoList;
  }

}
