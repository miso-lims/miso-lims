package uk.ac.bbsrc.tgac.miso.persistence;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;

public interface SampleClassDao {

  List<SampleClass> getSampleClass();
  
  List<SampleClass> listByCategory(String sampleCategory);

  SampleClass getSampleClass(Long id);

  Long addSampleClass(SampleClass sampleClass);

  void deleteSampleClass(SampleClass sampleClass);

  void update(SampleClass sampleClass);

}