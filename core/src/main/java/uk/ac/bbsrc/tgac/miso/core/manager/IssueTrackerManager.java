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

package uk.ac.bbsrc.tgac.miso.core.manager;

import java.io.IOException;

import net.sf.json.JSONObject;
import net.sourceforge.fluxion.spi.Spi;

/**
 * uk.ac.bbsrc.tgac.miso.core.manager
 * <p/>
 * An interface to define simple access to a specified issue tracker
 * 
 * @author Rob Davey
 * @date 25-Jan-2011
 * @since 0.0.3
 */
@Spi
public interface IssueTrackerManager {
  String OAUTH = "oauth";
  String BASIC = "basic";

  String getType();

  JSONObject getIssue(String issueKey) throws IOException;

  String getBaseTrackerUrl();

  public enum TrackerType {
    JIRA("jira"), RT("RT");

    /** Field key */
    private String key;

    /**
     * Constructs a TrackerType based on a given key
     * 
     * @param key
     *          of type String
     */
    TrackerType(String key) {
      this.key = key;
    }

    /**
     * Returns the key of this TrackerType enum.
     * 
     * @return String key.
     */
    public String getKey() {
      return key;
    }
  }
}