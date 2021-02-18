package uk.ac.bbsrc.tgac.miso.persistence;

import uk.ac.bbsrc.tgac.miso.core.data.SamplePurpose;

public interface SamplePurposeDao extends SaveDao<SamplePurpose> {

  long getUsage(SamplePurpose samplePurpose);

}