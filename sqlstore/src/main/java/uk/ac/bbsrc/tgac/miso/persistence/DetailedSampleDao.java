package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;

public interface DetailedSampleDao {

  List<DetailedSample> getDetailedSample() throws IOException;

  DetailedSample getDetailedSample(Long id) throws IOException;

  DetailedSample getDetailedSampleBySampleId(Long id) throws IOException;

  void deleteDetailedSample(DetailedSample detailedSample);

}