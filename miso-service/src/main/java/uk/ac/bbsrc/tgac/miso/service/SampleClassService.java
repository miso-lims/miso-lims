package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;

public interface SampleClassService {

  SampleClass get(Long sampleClassId) throws IOException;

  Long create(SampleClass sampleClass) throws IOException;

  void update(SampleClass sampleClass) throws IOException;

  Set<SampleClass> getAll() throws IOException;

  void delete(Long sampleClassId) throws IOException;

  public SampleClass inferParentFromChild(long childClassId, String childCategory, String parentCategory);

  List<SampleClass> listByCategory(String sampleCategory) throws IOException;

}