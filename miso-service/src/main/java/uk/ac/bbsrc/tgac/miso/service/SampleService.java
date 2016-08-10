package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Sample;

public interface SampleService {

  Sample get(Long sampleId) throws IOException;

  Long create(Sample sample) throws IOException;

  void update(Sample sample) throws IOException;

  List<Sample> getAll() throws IOException;

  List<Sample> getByPageAndSize(int page, int size, String sortCol, String sortDir) throws IOException;

  List<Sample> getByPageAndSizeAndSearch(int page, int size, String querystr, String sortCol, String sortDir) throws IOException;

  List<Sample> getBySearch(String querystr) throws IOException;

  void delete(Long sampleId) throws IOException;

  Long countAll() throws IOException;

  Long countBySearch(String querystr) throws IOException;

}