package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.Workset;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListWorksetView;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface WorksetService extends PaginatedDataSource<ListWorksetView>, DeleterService<Workset>, SaveService<Workset> {

  public List<ListWorksetView> listBySearch(String query) throws IOException;

  public List<Workset> listBySample(long sampleId) throws IOException;

  public List<Workset> listByLibrary(long libraryId) throws IOException;

  public List<Workset> listByLibraryAliquot(long aliquotId) throws IOException;

  public void moveSamples(Workset from, Workset to, Collection<Sample> items) throws IOException;

  public void moveLibraries(Workset from, Workset to, Collection<Library> items) throws IOException;

  public void moveLibraryAliquots(Workset from, Workset to, Collection<LibraryAliquot> items) throws IOException;

}
