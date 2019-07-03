package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;

public interface SampleClassService extends ProviderService<SampleClass> {

  Long create(SampleClass sampleClass) throws IOException;

  void update(SampleClass sampleClass) throws IOException;

  Set<SampleClass> getAll() throws IOException;

  SampleClass inferParentFromChild(long childClassId, String childCategory, String parentCategory);

  List<SampleClass> listByCategory(String sampleCategory) throws IOException;

  SampleClass getRequiredTissueProcessingClass(Long childClassId) throws IOException;

}