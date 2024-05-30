package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Array;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface ArrayStore extends SaveDao<Array>, PaginatedDataSource<Array> {

  public Array getByAlias(String alias) throws IOException;

  public Array getBySerialNumber(String serialNumber) throws IOException;

  public List<Sample> getArrayableSamplesBySearch(String search) throws IOException;

  public List<Array> getArraysBySearch(String search) throws IOException;

  public List<Array> listBySampleId(long sampleId) throws IOException;

}
