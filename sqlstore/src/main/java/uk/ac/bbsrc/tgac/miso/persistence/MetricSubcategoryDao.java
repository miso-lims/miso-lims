package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.impl.MetricSubcategory;
import uk.ac.bbsrc.tgac.miso.core.data.type.MetricCategory;

public interface MetricSubcategoryDao extends BulkSaveDao<MetricSubcategory> {

  MetricSubcategory getByAliasAndCategory(String alias, MetricCategory category) throws IOException;

  long getUsage(MetricSubcategory subcategory) throws IOException;

}
