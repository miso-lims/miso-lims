package uk.ac.bbsrc.tgac.miso.core.service;

import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.Workset;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface WorksetService extends PaginatedDataSource<Workset>, DeleterService<Workset>, SaveService<Workset> {

  public List<Workset> listBySearch(String query);

  public List<Workset> listBySample(long sampleId);

  public List<Workset> listByLibrary(long libraryId);

  public List<Workset> listByLibraryAliquot(long aliquotId);

  public void moveSamples(Workset from, Workset to, Collection<Sample> items);

  public void moveLibraries(Workset from, Workset to, Collection<Library> items);

  public void moveLibraryAliquots(Workset from, Workset to, Collection<LibraryAliquot> items);

}
