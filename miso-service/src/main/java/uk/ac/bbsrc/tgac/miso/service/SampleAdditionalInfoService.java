package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.SampleAdditionalInfo;

public interface SampleAdditionalInfoService {

  SampleAdditionalInfo get(Long sampleAdditionalInfoId);

  Long create(SampleAdditionalInfo sampleAdditionalInfo, Long sampleId, Long tissueOriginId, Long tissueTypeId, Long qcPassedDetailId,
      Long subprojectId, Long prepKitId, Long sampleClassId) throws IOException;

  void update(SampleAdditionalInfo sampleAdditionalInfo, Long tissueOriginId, Long tissueTypeId, Long qcPassedDetailId, Long prepKitId,
      Long sampleClassId) throws IOException;

  Set<SampleAdditionalInfo> getAll();

  void delete(Long sampleAdditionalInfoId);

}