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

package uk.ac.bbsrc.tgac.miso.core.event.model;

import net.sf.json.JSONObject;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview;
import uk.ac.bbsrc.tgac.miso.core.event.Event;
import uk.ac.bbsrc.tgac.miso.core.event.type.MisoEventType;

/**
 * uk.ac.bbsrc.tgac.miso.core.event.model
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 16/11/11
 * @since 0.1.3
 */
public class ProjectOverviewEvent implements Event<ProjectOverview> {
  private ProjectOverview projectOverview;
  private String message;
  private MisoEventType eventType;
  private JSONObject eventContext = new JSONObject();

  public ProjectOverviewEvent(ProjectOverview projectOverview, MisoEventType eventType, String message) {
    this.projectOverview = projectOverview;
    this.eventType = eventType;
    this.message = message;
  }

  @Override
  public ProjectOverview getEventObject() {
    return this.projectOverview;
  }

  @Override
  public JSONObject getEventContext() {
    return eventContext;
  }

  public void setEventContext(JSONObject eventContext) {
    this.eventContext = eventContext;
  }

  @Override
  public MisoEventType getEventType() {
    return eventType;
  }

  @Override
  public String getEventMessage() {
    return message;
  }
}
