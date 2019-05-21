package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Array;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayModel;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.service.SaveService;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface ArrayService extends PaginatedDataSource<Array>, SaveService<Array> {

  public ArrayModel getArrayModel(long id) throws IOException;

  public List<ArrayModel> listArrayModels() throws IOException;

  public List<Sample> getArrayableSamplesBySearch(String search) throws IOException;

  public List<Array> getArraysBySearch(String search) throws IOException;

  public List<Array> listBySampleId(long sampleId) throws IOException;

}
