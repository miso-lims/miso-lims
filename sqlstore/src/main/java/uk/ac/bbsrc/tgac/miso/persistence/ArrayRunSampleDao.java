package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.ArrayRun;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayRunSample;

public interface ArrayRunSampleDao {

  ArrayRunSample get(ArrayRun run, String position) throws IOException;

  List<ArrayRunSample> listByRunId(long arrayRunId) throws IOException;

  void save(ArrayRunSample arrayRunSample) throws IOException;

  void delete(ArrayRunSample arrayRunSample) throws IOException;

  void deleteByRunId(long arrayRunId) throws IOException;
}
