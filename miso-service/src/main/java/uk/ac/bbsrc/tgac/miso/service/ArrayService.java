package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import uk.ac.bbsrc.tgac.miso.core.data.Array;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayModel;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface ArrayService extends PaginatedDataSource<Array> {

  public Array get(long arrayId) throws IOException;

  public long save(Array array) throws IOException;

  public ArrayModel getArrayModel(long id) throws IOException;

  public List<ArrayModel> listArrayModels() throws IOException;

  public Map<String, Integer> getColumnSizes() throws IOException;

  public List<Sample> getArrayableSamplesBySearch(String search) throws IOException;

  public List<Array> getArraysBySearch(String search) throws IOException;

  public List<Array> listBySampleId(long sampleId) throws IOException;

}
