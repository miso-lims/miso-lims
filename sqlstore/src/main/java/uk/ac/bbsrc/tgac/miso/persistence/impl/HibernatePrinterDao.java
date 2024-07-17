package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Arrays;
import java.util.List;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
  public List<SingularAttribute<? super Printer, String>> getSearchProperties() {
    return Arrays.asList(Printer_.name);
  }

  @Override
  public Path<?> propertyForDate(Root<Printer> root, DateType type) {
    return null;
  }

  @Override
  public Path<?> propertyForSortColumn(Root<Printer> root, String original) {
    switch (original) {
      case "id":
        return root.get(Printer_.printerId);
      case "available":
        return root.get(Printer_.enabled);
      default:
        return root.get(original);
    }
  }

  @Override
  public SingularAttribute<Printer, ? extends UserImpl> propertyForUser(boolean creator) {
    return null;
  }

}
