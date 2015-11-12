package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.SampleAdditionalInfo;

public interface SampleAdditionalInfoService {

  SampleAdditionalInfo get(Long sampleAdditionalInfoId);

  Long create(SampleAdditionalInfo sampleAdditionalInfo) throws IOException;

  void update(SampleAdditionalInfo sampleAdditionalInfo) throws IOException;

  Set<SampleAdditionalInfo> getAll();

  void delete(Long sampleAdditionalInfoId);

}