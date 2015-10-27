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

package uk.ac.bbsrc.tgac.miso.tools.run.util;

import net.sf.json.JSONObject;
import org.springframework.util.LinkedMultiValueMap;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * uk.ac.bbsrc.tgac.miso.notification.util
 * <p/>
 * Takes a set of files and transforms them into a textual representation ready for passing to a Message receiving service
 * 
 * @author Rob Davey
 * @date 16-Feb-2011
 * @since 0.0.3
 */
public interface FileSetTransformer<K, V, S> {
  Map<K, V> transform(Set<S> files);
}