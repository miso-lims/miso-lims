package uk.ac.bbsrc.tgac.miso.persistence;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Sample;

public interface SampleDao {

  List<Sample> getSample();

  Sample getSample(Long id);

  Long addSample(Sample sample);

  void deleteSample(Sample sample);

  void update(Sample sample);

}