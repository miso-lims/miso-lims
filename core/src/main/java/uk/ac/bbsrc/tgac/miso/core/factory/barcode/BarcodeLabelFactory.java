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

package uk.ac.bbsrc.tgac.miso.core.factory.barcode;

import net.sourceforge.fluxion.spi.Spi;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.core.service.printing.schema.BarcodableSchema;

/**
 * uk.ac.bbsrc.tgac.miso.core.factory.barcode
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 17/04/12
 * @since 0.1.6
 */
@Spi
public interface BarcodeLabelFactory<T, S, B extends BarcodableSchema<T, S>> {
  T getLabel(B s, S b);

  void setSecurityManager(com.eaglegenomics.simlims.core.manager.SecurityManager securityManager);

  void setFilesManager(MisoFilesManager misoFileManager);
}
