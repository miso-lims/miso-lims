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

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import uk.ac.ebi.fgpt.conan.model.AbstractConanParameter;

/**
 * uk.ac.bbsrc.tgac.miso.analysis.parameter
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 14/10/11
 * @since 0.1.2
 */
public class PathCreatingPathParameter extends AbstractConanParameter implements Optionable {
  protected static final Logger log = LoggerFactory.getLogger(PathCreatingPathParameter.class);
  private boolean optional = false;

  public PathCreatingPathParameter(String name) {
    super(name);
  }

  public PathCreatingPathParameter(String name, boolean isBoolean) {
    super(name, isBoolean);
  }

  public PathCreatingPathParameter(String name, String description) {
    super(name, description);
  }

  public PathCreatingPathParameter(String name, String description, boolean isBoolean) {
    super(name, description, isBoolean);
  }

  @Override
  public boolean validateParameterValue(String value) {
    File f = new File(value);
    try {
      if (!StringUtils.containsWhitespace(value) && !value.contains("~")) {
        if (!f.exists()) {
          if (f.isDirectory()) {
            return (f.mkdirs());
          } else {
            if (!f.getParentFile().exists()) f.getParentFile().mkdirs();
            return f.createNewFile() && f.delete();
          }
        }
        return true;
      }
    } catch (IOException e) {
      log.error("validate parameter value", e);
    }
    return false;
  }

  @Override
  public boolean isOptional() {
    return optional;
  }

  @Override
  public void setOptional(boolean optional) {
    this.optional = optional;
  }
}