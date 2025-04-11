package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIndexFamily;

public interface SampleIndexFamilyDao extends SaveDao<SampleIndexFamily> {

  SampleIndexFamily getByName(String name) throws IOException;

}
