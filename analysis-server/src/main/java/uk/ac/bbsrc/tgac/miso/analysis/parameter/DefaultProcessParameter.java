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

package uk.ac.bbsrc.tgac.miso.analysis.parameter;

import uk.ac.ebi.fgpt.conan.model.AbstractConanParameter;

/**
 * uk.ac.bbsrc.tgac.miso.task.util
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 27-Jun-2011
 * @since 0.0.3
 */
public class DefaultProcessParameter extends AbstractConanParameter implements Optionable, Transientable {
  private boolean optional = false;
  private boolean t = false;

  public DefaultProcessParameter(String name) {
    super(name);
  }

  @Override
  public boolean validateParameterValue(String value) {
    return true;
  }

  @Override
  public boolean isOptional() {
    return optional;
  }

  @Override
  public void setOptional(boolean optional) {
    this.optional = optional;
  }

  @Override
  public boolean isTransient() {
    return t;
  }

  @Override
  public void setTransient(boolean t) {
    this.t = t;
  }
}
