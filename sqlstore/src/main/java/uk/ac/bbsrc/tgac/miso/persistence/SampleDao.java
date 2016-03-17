package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;

public interface SampleDao {

  List<Sample> getSample() throws IOException;

  Sample getSample(Long id) throws IOException;

  Long addSample(Sample sample) throws IOException, MisoNamingException;

  void deleteSample(Sample sample);

  void update(Sample sample) throws IOException;

}