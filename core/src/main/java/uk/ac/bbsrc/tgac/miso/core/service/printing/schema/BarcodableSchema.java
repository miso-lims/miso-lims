package uk.ac.bbsrc.tgac.miso.core.service.printing.schema;

import net.sourceforge.fluxion.spi.Spi;
import uk.ac.bbsrc.tgac.miso.core.factory.barcode.BarcodeLabelFactory;

/**
 * Created with IntelliJ IDEA. User: bianx Date: 09/05/2013 Time: 11:14 To change this template use File | Settings | File Templates.
 */
@Spi
public interface BarcodableSchema<S, T> {
  Class<T> isStateFor();

  public String getRawState(T t);

  public S getPrintableLabel(T t);

  String getName();

  public BarcodeLabelFactory<S, T, BarcodableSchema<S, T>> getBarcodeLabelFactory();
}
