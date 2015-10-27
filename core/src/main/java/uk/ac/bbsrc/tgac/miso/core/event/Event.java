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

package uk.ac.bbsrc.tgac.miso.core.event;

import net.sf.json.JSONObject;
import uk.ac.bbsrc.tgac.miso.core.event.type.MisoEventType;

/**
 * Interface describing any propagatable event, comprising a message, a type, the object to which the event is related and a freeform
 * key-value context that represents any extra information about the event-space
 * 
 * @author Rob Davey
 * @date 23-Sep-2011
 * @since 0.1.2
 */
public interface Event<T> {
  String getEventMessage();

  MisoEventType getEventType();

  T getEventObject();

  JSONObject getEventContext();
}
