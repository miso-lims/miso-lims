/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.core.service.printing;

import java.io.File;
import java.io.IOException;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.fluxion.spi.ServiceProvider;
import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoPrintException;
import uk.ac.bbsrc.tgac.miso.core.service.printing.context.PrintContext;
import uk.ac.bbsrc.tgac.miso.core.service.printing.schema.BarcodableSchema;

/**
 * uk.ac.bbsrc.tgac.miso.core.service.printing
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 30-Jun-2011
 * @since 0.0.3
 */
@ServiceProvider
public class DefaultPrintService implements MisoPrintService<File, Barcodable, PrintContext<File>> {
  protected static final Logger log = LoggerFactory.getLogger(DefaultPrintService.class);
  private String name;
  private boolean enabled = true;
  private PrintContext<File> pc;
  private Class<? extends Barcodable> printServiceFor;
  private BarcodableSchema<File, Barcodable> barcodableSchema;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long serviceId = -1L;

  @Override
  public long getServiceId() {
    return serviceId;
  }

  @Override
  public void setServiceId(long serviceId) {
    this.serviceId = serviceId;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  @Override
  public PrintContext<File> getPrintContext() {
    return pc;
  }

  @Override
  public void setPrintContext(PrintContext<File> pc) {
    this.pc = pc;
  }

  @Override
  public BarcodableSchema<File, Barcodable> getBarcodableSchema() {
    return barcodableSchema;
  }

  @Override
  public void setBarcodableSchema(BarcodableSchema<File, Barcodable> barcodableSchema) {
    this.barcodableSchema = barcodableSchema;
  }

  @Override
  public boolean print(File content) throws IOException {
    if (pc != null) {
      if (isEnabled()) {
        return pc.print(content);
      } else {
        throw new IOException("Printer " + getName() + " is not enabled.");
      }
    } else {
      throw new IOException("No PrintContext specified");
    }
  }

  @Override
  public void setPrintServiceFor(Class<? extends Barcodable> c) {
    this.printServiceFor = c;
  }

  @Override
  public Class<? extends Barcodable> getPrintServiceFor() {
    return printServiceFor;
  }

  @Override
  public File getLabelFor(Barcodable b) throws MisoPrintException {
    if (getPrintServiceFor().isAssignableFrom(b.getClass())) {
      BarcodableSchema<File, Barcodable> bs = getBarcodableSchema();
      if (bs != null) {
        return bs.getPrintableLabel(b);
      } else {
        throw new MisoPrintException(
            "No barcodable schema set for '" + getName() + "' service. Make sure a schema is set in the " + "printer administration page");
      }
    }
    return null;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.getName()).append(" : ");
    sb.append(this.getPrintServiceFor().getName()).append(" : ");
    sb.append(this.getPrintContext().getName()).append(" : ");
    sb.append(this.isEnabled());
    return sb.toString();
  }
}
