package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;

public interface DetailedQcStatusService {

  DetailedQcStatus get(Long detailedQcStatus) throws IOException;

  Long create(DetailedQcStatus detailedQcStatus) throws IOException;

  void update(DetailedQcStatus detailedQcStatus) throws IOException;

  Set<DetailedQcStatus> getAll() throws IOException;

  void delete(Long detailedQcStatus) throws IOException;

}