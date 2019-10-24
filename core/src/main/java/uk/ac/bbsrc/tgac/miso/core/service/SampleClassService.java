package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;

public interface SampleClassService extends DeleterService<SampleClass>, ListService<SampleClass>, SaveService<SampleClass> {

  SampleClass inferParentFromChild(long childClassId, String childCategory, String parentCategory) throws IOException;

  List<SampleClass> listByCategory(String sampleCategory) throws IOException;

  SampleClass getRequiredTissueProcessingClass(Long childClassId) throws IOException;

}