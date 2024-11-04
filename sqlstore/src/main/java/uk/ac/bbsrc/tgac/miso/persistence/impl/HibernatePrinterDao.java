package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.SingularAttribute;
import uk.ac.bbsrc.tgac.miso.core.data.Printer;
import uk.ac.bbsrc.tgac.miso.core.data.Printer_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.PrinterStore;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernatePrinterDao extends HibernateSaveDao<Printer>
    implements PrinterStore, JpaCriteriaPaginatedDataSource<Printer, Printer> {

  public HibernatePrinterDao() {
    super(Printer.class);
  }

  @Override
  public String getFriendlyName() {
    return "Printer";
  }

  @Override
  public SingularAttribute<Printer, ?> getIdProperty() {
    return Printer_.printerId;
  }

  @Override
  public Class<Printer> getEntityClass() {
    return Printer.class;
  }

  @Override
  public Class<Printer> getResultClass() {
    return Printer.class;
  }

  @Override
  public List<Path<String>> getSearchProperties(Root<Printer> root) {
    return Arrays.asList(root.get(Printer_.name));
  }

  @Override
  public Path<?> propertyForDate(QueryBuilder<?, Printer> builder, DateType type) {
    return null;
  }

  @Override
  public Path<?> propertyForSortColumn(QueryBuilder<?, Printer> builder, String original) {
    switch (original) {
      case "id":
        return builder.getRoot().get(Printer_.printerId);
      case "available":
        return builder.getRoot().get(Printer_.enabled);
      default:
        return builder.getRoot().get(original);
    }
  }

  @Override
  public SingularAttribute<Printer, ? extends UserImpl> propertyForUser(boolean creator) {
    return null;
  }

}
