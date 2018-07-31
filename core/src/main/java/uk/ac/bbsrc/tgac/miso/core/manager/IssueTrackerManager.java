/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
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
import java.util.List;
import java.util.Properties;

import uk.ac.bbsrc.tgac.miso.core.data.Issue;

/**
 * uk.ac.bbsrc.tgac.miso.core.manager
 * <p/>
 * An interface to define simple access to a specified issue tracker
 * 
 * @author Rob Davey
 * @date 25-Jan-2011
 * @since 0.0.3
 */
public interface IssueTrackerManager {

  public List<Issue> getIssuesByTag(String tag) throws IOException;

  public List<Issue> searchIssues(String query) throws IOException;

  public void setConfiguration(Properties properties);

}