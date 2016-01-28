package uk.ac.bbsrc.tgac.miso.dto;

import java.util.Set;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import uk.ac.bbsrc.tgac.miso.core.data.Identity;
import uk.ac.bbsrc.tgac.miso.core.data.Institute;
import uk.ac.bbsrc.tgac.miso.core.data.QcPassedDetail;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAdditionalInfo;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAnalyte;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleGroupId;
import uk.ac.bbsrc.tgac.miso.core.data.SampleNumberPerProject;
import uk.ac.bbsrc.tgac.miso.core.data.SamplePurpose;
import uk.ac.bbsrc.tgac.miso.core.data.SampleValidRelationship;
import uk.ac.bbsrc.tgac.miso.core.data.Subproject;
import uk.ac.bbsrc.tgac.miso.core.data.TissueMaterial;
import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;
import uk.ac.bbsrc.tgac.miso.core.data.TissueType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.IdentityImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.InstituteImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.QcPassedDetailImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleAdditionalInfoImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleAnalyteImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleClassImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleGroupImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleNumberPerProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SamplePurposeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleValidRelationshipImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SubprojectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueMaterialImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueOriginImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueTypeImpl;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

import com.google.common.collect.Sets;

public class Dtos {

  private static DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis();

  public static TissueOriginDto asDto(TissueOrigin from) {
    TissueOriginDto dto = new TissueOriginDto();
    dto.setId(from.getTissueOriginId());
    dto.setAlias(from.getAlias());
    dto.setDescription(from.getDescription());
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
    to.setAlias(from.getAlias());
    to.setDescription(from.getDescription());
    return to;
  }

  public static TissueTypeDto asDto(TissueType from) {
    TissueTypeDto dto = new TissueTypeDto();
    dto.setId(from.getTissueTypeId());
    dto.setAlias(from.getAlias());
    dto.setDescription(from.getDescription());
    dto.setCreatedById(from.getCreatedBy().getUserId());
    dto.setCreationDate(dateTimeFormatter.print(from.getCreationDate().getTime()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(dateTimeFormatter.print(from.getLastUpdated().getTime()));
    return dto;
  }

  public static Set<TissueTypeDto> asTissueTypeDtos(Set<TissueType> from) {
    Set<TissueTypeDto> dtoSet = Sets.newHashSet();
    for (TissueType tissueOrigin : from) {
      dtoSet.add(asDto(tissueOrigin));
    }
    return dtoSet;
  }

  public static TissueType to(TissueTypeDto from) {
    TissueType to = new TissueTypeImpl();
    to.setAlias(from.getAlias());
    to.setDescription(from.getDescription());
    return to;
  }

  public static SubprojectDto asDto(Subproject from) {
    SubprojectDto dto = new SubprojectDto();
    dto.setId(from.getSubprojectId());
    dto.setAlias(from.getAlias());
    dto.setDescription(from.getDescription());
    dto.setPriority(from.getPriority());
    dto.setParentProjectId(from.getParentProject().getProjectId());
    dto.setCreatedById(from.getCreatedBy().getUserId());
    dto.setCreationDate(dateTimeFormatter.print(from.getCreationDate().getTime()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(dateTimeFormatter.print(from.getLastUpdated().getTime()));
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
    return to;
  }

  public static SampleClassDto asDto(SampleClass from) {
    SampleClassDto dto = new SampleClassDto();
    dto.setId(from.getSampleClassId());
    dto.setAlias(from.getAlias());
    dto.setSampleCategory(from.getSampleCategory());
    dto.setCreatedById(from.getCreatedBy().getUserId());
    dto.setCreationDate(dateTimeFormatter.print(from.getCreationDate().getTime()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(dateTimeFormatter.print(from.getLastUpdated().getTime()));
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
    return to;
  }

  public static QcPassedDetailDto asDto(QcPassedDetail from) {
    QcPassedDetailDto dto = new QcPassedDetailDto();
    dto.setId(from.getQcPassedDetailId());
    dto.setStatus(from.getStatus());
    dto.setDescription(from.getDescription());
    dto.setNoteRequired(from.getNoteRequired());
    dto.setCreatedById(from.getCreatedBy().getUserId());
    dto.setCreationDate(dateTimeFormatter.print(from.getCreationDate().getTime()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(dateTimeFormatter.print(from.getLastUpdated().getTime()));
    return dto;
  }

  public static Set<QcPassedDetailDto> asQcPassedDetailDtos(Set<QcPassedDetail> from) {
    Set<QcPassedDetailDto> dtoSet = Sets.newHashSet();
    for (QcPassedDetail qcPassedDetail : from) {
      dtoSet.add(asDto(qcPassedDetail));
    }
    return dtoSet;
  }

  public static QcPassedDetail to(QcPassedDetailDto from) {
    QcPassedDetail to = new QcPassedDetailImpl();
    to.setStatus(from.getStatus());
    to.setDescription(from.getDescription());
    to.setNoteRequired(from.isNoteRequired());
    return to;
  }

  public static SampleAdditionalInfoDto asDto(SampleAdditionalInfo from) {
    SampleAdditionalInfoDto dto = new SampleAdditionalInfoDto();
    dto.setId(from.getSampleAdditionalInfoId());
    dto.setSampleId(from.getSample().getId());
    dto.setSampleClassId(from.getSampleClass().getSampleClassId());
    if (from.getTissueOrigin() != null) {
      dto.setTissueOriginId(from.getTissueOrigin().getTissueOriginId());
    }
    if (from.getTissueType() != null) {
      dto.setTissueTypeId(from.getTissueType().getTissueTypeId());
    }
    if (from.getQcPassedDetail() != null) {
      dto.setQcPassedDetailId(from.getQcPassedDetail().getQcPassedDetailId());
    }
    if (from.getSubproject() != null) {
      dto.setSubprojectId(from.getSubproject().getSubprojectId());
    }
    if (from.getPrepKit() != null) {
      dto.setPrepKitId(from.getPrepKit().getKitDescriptorId());
    }
    dto.setPassageNumber(from.getPassageNumber());
    dto.setTimesReceived(from.getTimesReceived());
    dto.setVolume(from.getVolume());
    dto.setConcentration(from.getConcentration());
    dto.setCreatedById(from.getCreatedBy().getUserId());
    dto.setCreationDate(dateTimeFormatter.print(from.getCreationDate().getTime()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(dateTimeFormatter.print(from.getLastUpdated().getTime()));
    return dto;
  }

  public static Set<SampleAdditionalInfoDto> asSampleAdditionalInfoDtos(Set<SampleAdditionalInfo> from) {
    Set<SampleAdditionalInfoDto> dtoSet = Sets.newHashSet();
    for (SampleAdditionalInfo sampleAdditionalInfo : from) {
      dtoSet.add(asDto(sampleAdditionalInfo));
    }
    return dtoSet;
  }

  public static SampleAdditionalInfo to(SampleAdditionalInfoDto from) {
    SampleAdditionalInfo to = new SampleAdditionalInfoImpl();
    to.setPassageNumber(from.getPassageNumber());
    to.setTimesReceived(from.getTimesReceived());
    to.setTubeNumber(from.getTubeNumber());
    to.setVolume(from.getVolume());
    to.setConcentration(from.getConcentration());
    return to;
  }

  public static TissueMaterialDto asDto(TissueMaterial from) {
    TissueMaterialDto dto = new TissueMaterialDto();
    dto.setId(from.getTissueMaterialId());
    dto.setAlias(from.getAlias());
    dto.setDescription(from.getDescription());
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
    to.setDescription(from.getDescription());
    return to;
  }

  public static SamplePurposeDto asDto(SamplePurpose from) {
    SamplePurposeDto dto = new SamplePurposeDto();
    dto.setId(from.getSamplePurposeId());
    dto.setAlias(from.getAlias());
    dto.setDescription(from.getDescription());
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
    to.setDescription(from.getDescription());
    return to;
  }

  public static SampleGroupDto asDto(SampleGroupId from) {
    SampleGroupDto dto = new SampleGroupDto();
    dto.setId(from.getSampleGroupId());
    dto.setGroupId(from.getGroupId());
    dto.setDescription(from.getDescription());
    dto.setCreatedById(from.getCreatedBy().getUserId());
    dto.setCreationDate(dateTimeFormatter.print(from.getCreationDate().getTime()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(dateTimeFormatter.print(from.getLastUpdated().getTime()));
    return dto;
  }

  public static Set<SampleGroupDto> asSampleGroupDtos(Set<SampleGroupId> from) {
    Set<SampleGroupDto> dtoSet = Sets.newHashSet();
    for (SampleGroupId samplePurpose : from) {
      dtoSet.add(asDto(samplePurpose));
    }
    return dtoSet;
  }

  public static SampleGroupId to(SampleGroupDto from) {
    SampleGroupId to = new SampleGroupImpl();
    to.setGroupId(from.getGroupId());
    to.setDescription(from.getDescription());
    return to;
  }

  public static SampleAnalyteDto asDto(SampleAnalyte from) {
    SampleAnalyteDto dto = new SampleAnalyteDto();
    dto.setId(from.getSampleAnalyteId());
    dto.setSampleId(from.getSample().getSampleId());
    if (from.getSamplePurpose() != null) {
      dto.setSamplePurposeId(from.getSamplePurpose().getSamplePurposeId());
    }
    if (from.getSampleGroup() != null) {
      dto.setSampleGroupId(from.getSampleGroup().getSampleGroupId());
    }
    if (from.getTissueMaterial() != null) {
      dto.setTissueMaterialId(from.getTissueMaterial().getTissueMaterialId());
    }
    if (!LimsUtils.isStringEmptyOrNull(from.getRegion())) {
      dto.setRegion(from.getRegion());
    }
    if (!LimsUtils.isStringEmptyOrNull(from.getTubeId())) {
      dto.setTubeId(from.getTubeId());
    }
    if (from.getStockNumber() != null) {
      dto.setStockNumber(from.getStockNumber());
    }
    if (from.getAliquotNumber() != null) {
      dto.setAliquotNumber(from.getAliquotNumber());
    }
    dto.setCreatedById(from.getCreatedBy().getUserId());
    dto.setCreationDate(dateTimeFormatter.print(from.getCreationDate().getTime()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(dateTimeFormatter.print(from.getLastUpdated().getTime()));
    return dto;
  }

  public static Set<SampleAnalyteDto> asSampleAnalyteDtos(Set<SampleAnalyte> from) {
    Set<SampleAnalyteDto> dtoSet = Sets.newHashSet();
    for (SampleAnalyte sampleAnalyte : from) {
      dtoSet.add(asDto(sampleAnalyte));
    }
    return dtoSet;
  }

  public static SampleAnalyte to(SampleAnalyteDto from) {
    SampleAnalyte to = new SampleAnalyteImpl();
    if (from.getRegion() != null) {
      to.setRegion(from.getRegion());
    }
    if (from.getTubeId() != null) {
      to.setTubeId(from.getTubeId());
    }
    if (from.getStockNumber() != null) {
      to.setStockNumber(from.getStockNumber());
    }
    if (from.getAliquotNumber() != null) {
      to.setAliquotNumber(from.getAliquotNumber());
    }
    return to;
  }

  public static SampleDto asDto(Sample from) {
    SampleDto dto = new SampleDto();
    dto.setId(from.getSampleId());
    if (!LimsUtils.isStringEmptyOrNull(from.getAccession())) {
      dto.setAccession(from.getAccession());
    }
    dto.setName(from.getName());
    dto.setDescription(from.getDescription());
    if (!LimsUtils.isStringEmptyOrNull(from.getIdentificationBarcode())) {
      dto.setIdentificationBarcode(from.getIdentificationBarcode());
    }
    if (!LimsUtils.isStringEmptyOrNull(from.getLocationBarcode())) {
      dto.setLocationBarcode(from.getLocationBarcode());
    }
    dto.setSampleType(from.getSampleType());
    if (from.getReceivedDate() != null) {
      dto.setReceivedDate(dateTimeFormatter.print(from.getReceivedDate().getTime()));
    }
    if (from.getQcPassed() != null) {
      dto.setQcPassed(from.getQcPassed());
    }
    if (!LimsUtils.isStringEmptyOrNull(from.getAlias())) {
      dto.setAlias(from.getAlias());
    }
    dto.setProjectId(from.getProject().getProjectId());
    dto.setScientificName(from.getScientificName());
    if (!LimsUtils.isStringEmptyOrNull(from.getTaxonIdentifier())) {
      dto.setTaxonIdentifier(from.getTaxonIdentifier());
    }
    if (from.getIdentity() != null) {
      dto.setSampleIdentityDto(asDto(from.getIdentity()));
    }
    if (from.getSampleAnalyte() != null) {
      dto.setSampleAnalyte(asDto(from.getSampleAnalyte()));
    }
    if (from.getSampleAdditionalInfo() != null) {
      dto.setSampleAdditionalInfo(asDto(from.getSampleAdditionalInfo()));
    }
    if (from.getParent() != null) {
      dto.setParentId(from.getParent().getSampleId());
    }

    return dto;
  }

  public static Set<SampleDto> asSampleDtos(Set<Sample> from) {
    Set<SampleDto> dtoSet = Sets.newHashSet();
    for (Sample sample : from) {
      dtoSet.add(asDto(sample));
    }
    return dtoSet;
  }

  public static Sample to(SampleDto from) {
    Sample to = new SampleImpl();
    if (!LimsUtils.isStringEmptyOrNull(from.getAccession())) {
      to.setAccession(from.getAccession());
    }
    to.setName(from.getName());
    to.setDescription(from.getDescription());
    if (!LimsUtils.isStringEmptyOrNull(from.getIdentificationBarcode())) {
      to.setIdentificationBarcode(from.getIdentificationBarcode());
    }
    if (!LimsUtils.isStringEmptyOrNull(from.getLocationBarcode())) {
      to.setLocationBarcode(from.getLocationBarcode());
    }
    to.setSampleType(from.getSampleType());
    if (from.getReceivedDate() != null) {
      to.setReceivedDate(dateTimeFormatter.parseDateTime(from.getReceivedDate()).toDate());
    }
    if (from.getQcPassed() != null) {
      to.setQcPassed(from.getQcPassed());
    }
    if (!LimsUtils.isStringEmptyOrNull(from.getAlias())) {
      to.setAlias(from.getAlias());
    }
    // Project
    to.setScientificName(from.getScientificName());
    if (!LimsUtils.isStringEmptyOrNull(from.getTaxonIdentifier())) {
      to.setTaxonIdentifier(from.getTaxonIdentifier());
    }

    to.setAlias(from.getAlias());
    to.setDescription(from.getDescription());

    return to;
  }

  public static SampleIdentityDto asDto(Identity from) {
    SampleIdentityDto dto = new SampleIdentityDto();
    dto.setId(from.getIdentityId());
    dto.setSampleId(from.getSample().getSampleId());
    dto.setInternalName(from.getInternalName());
    dto.setExternalName(from.getExternalName());
    dto.setCreatedById(from.getCreatedBy().getUserId());
    dto.setCreationDate(dateTimeFormatter.print(from.getCreationDate().getTime()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(dateTimeFormatter.print(from.getLastUpdated().getTime()));
    return dto;
  }

  public static Set<SampleIdentityDto> asSampleIdentityDtos(Set<Identity> from) {
    Set<SampleIdentityDto> dtoSet = Sets.newHashSet();
    for (Identity tissueOrigin : from) {
      dtoSet.add(asDto(tissueOrigin));
    }
    return dtoSet;
  }

  public static Identity to(SampleIdentityDto from) {
    Identity to = new IdentityImpl();
    to.setInternalName(from.getInternalName());
    to.setExternalName(from.getExternalName());
    return to;
  }

  public static SampleNumberPerProjectDto asDto(SampleNumberPerProject from) {
    SampleNumberPerProjectDto dto = new SampleNumberPerProjectDto();
    dto.setId(from.getSampleNumberPerProjectId());
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
    dto.setId(from.getSampleValidRelationshipId());
    dto.setParentId(from.getParent().getSampleClassId());
    dto.setChildId(from.getChild().getSampleClassId());
    dto.setCreatedById(from.getCreatedBy().getUserId());
    dto.setCreationDate(dateTimeFormatter.print(from.getCreationDate().getTime()));
    dto.setUpdatedById(from.getUpdatedBy().getUserId());
    dto.setLastUpdated(dateTimeFormatter.print(from.getLastUpdated().getTime()));
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

}
