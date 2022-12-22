package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Metric;
import uk.ac.bbsrc.tgac.miso.core.data.impl.MetricSubcategory;
import uk.ac.bbsrc.tgac.miso.core.data.type.MetricCategory;

public interface MetricDao extends BulkSaveDao<Metric> {

  Metric getByAliasAndCategory(String alias, MetricCategory category, MetricSubcategory subcategory) throws IOException;

  long getUsage(Metric metric) throws IOException;

}
