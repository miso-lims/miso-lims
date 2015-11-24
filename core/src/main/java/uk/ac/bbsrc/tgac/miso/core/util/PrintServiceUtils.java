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

package uk.ac.bbsrc.tgac.miso.core.util;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONObject;
import uk.ac.bbsrc.tgac.miso.core.service.printing.context.PrintContext;

/**
 * uk.ac.bbsrc.tgac.miso.core.util
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 16/04/12
 * @since 0.1.6
 */
public class PrintServiceUtils {
  protected static final Logger log = LoggerFactory.getLogger(PrintServiceUtils.class);

  public static JSONObject mapContextFieldsToJSON(PrintContext context) throws IllegalAccessException {
    JSONObject contextFields = new JSONObject();
    // only get public fields
    for (Field f : context.getClass().getFields()) {
      if (!f.getName().equals("name") && !f.getName().equals("description")) {
        if (f.get(context) == null) {
          contextFields.put(f.getName(), "");
        } else {
          contextFields.put(f.getName(), f.get(context));
        }
      }
    }
    log.info(contextFields.toString());
    return contextFields;
  }

  public static void mapJSONToContextFields(JSONObject contextFields, PrintContext context) throws IllegalAccessException {
    for (Field f : context.getClass().getFields()) {
      if (contextFields.has(f.getName())) {
        f.set(context, contextFields.get(f.getName()));
      }
    }
  }
}
