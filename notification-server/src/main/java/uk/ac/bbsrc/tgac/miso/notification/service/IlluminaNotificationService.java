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

package uk.ac.bbsrc.tgac.miso.notification.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.core.configuration.DuplicateJobException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.integration.Message;

import uk.ac.bbsrc.tgac.miso.notification.core.batch.JobLaunchRequest;
import uk.ac.bbsrc.tgac.miso.notification.util.NotificationUtils;

/**
 * uk.ac.bbsrc.tgac.miso.notification.handler
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 07-Dec-2010
 * @since version
 */
@Deprecated
public class IlluminaNotificationService {
  protected static final Logger log = LoggerFactory.getLogger(IlluminaNotificationService.class);
  public JobLaunchRequest filesToJobRequest(Set<File> files) {
    Map<String, String> params = new HashMap<String, String>();
    String regex = ".*/([\\d]+_[A-z0-9]+_[\\d]+_[A-z0-9_]*)/.*";
    Pattern p = Pattern.compile(regex);
    for (File f : files) {
      Matcher m = p.matcher(f.getAbsolutePath());
      if (m.matches()) {
        params.put(m.group(1), f.getAbsolutePath());
      }
    }
    return new JobLaunchRequest("job" + files.hashCode(), params);
  }

  public Set<Resource> filesToResources(Set<File> files) throws DuplicateJobException {
    Set<Resource> resources = new HashSet<Resource>();
    for (File f : files) {
      log.info("Converting file " + f.getName() + " to resource...");
      resources.add(new FileSystemResource(f));
    }

    return resources;
  }

  public Message<Map<String, Object>> handleStatusXml(File xmlFile) {
    Map<String, Object> hm = new HashMap<String, Object>();
    try {
      StringBuilder sb = new StringBuilder();
      BufferedReader br = new BufferedReader(new FileReader(xmlFile));
      String line = null;
      while ((line = br.readLine()) != null) {
        sb.append(line);
      }
      br.close();
      hm.put("statusXml", sb.toString());
    } catch (FileNotFoundException e) {
      log.warn("handle status XML", e);
    } catch (IOException e) {
      log.warn("handle status XML", e);
    }
    return NotificationUtils.buildSimplePostMessage(hm);
  }

  public Message<Map<String, Object>> handleCompleted(File runCompleted) {
    Map<String, Object> hm = new HashMap<String, Object>();
    String regex = ".*/([\\d]+_[A-z0-9]+_[\\d]+_[A-z0-9_]*)/.*";
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(runCompleted.getAbsolutePath());
    if (m.matches()) {
      hm.put("runName", m.group(1));
    }
    return NotificationUtils.buildSimplePostMessage(hm);
  }
}
