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

package uk.ac.bbsrc.tgac.miso.core.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import net.sf.json.JSONObject;

/**
 * Defines an aspect whereby the given JoinPoint advice (an execution event, e.g. a DAO save operation) is logged to a particular logger
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Aspect
public class LogAspect {

  /** Field log */
  protected Logger log;

  /**
   * Constructor LogAspect creates a new LogAspect instance with a given Logger name
   * 
   * @param logName
   *          of type String
   */
  public LogAspect(String logName) {
    this.log = LoggerFactory.getLogger(logName);
  }

  /**
   * For a successful event, given a supplied JoinPoint, log advice to the logger defined in the aspect object
   * 
   * @param join
   *          of type JoinPoint
   */
  public void logEvent(JoinPoint join) {
    String name = "SERVICE";
    if (SecurityContextHolder.getContext().getAuthentication() != null) {
      name = SecurityContextHolder.getContext().getAuthentication().getName();
    }
    StringBuilder sb = new StringBuilder();
    sb.append("OK [").append(name).append("] ").append(join.toString()).append(" [\n");
    for (Object o : join.getArgs()) {
      sb.append("-> ").append(o).append("\n");
    }
    sb.append("]\n");
    log.info(sb.toString());
  }

  /**
   * For a failed event, given a supplied JoinPoint and exception, log advice to the logger defined in the aspect object
   * 
   * @param join
   *          of type JoinPoint
   * @param e
   *          of type Exception
   */
  public void logFailedEvent(JoinPoint join, Exception e) {
    String name = "SERVICE:" + e.getClass().getSimpleName();
    if (SecurityContextHolder.getContext().getAuthentication() != null) {
      name = SecurityContextHolder.getContext().getAuthentication().getName();
    }
    this.log.warn("FAIL [" + name + "] " + join.toString() + " [" + join.getArgs()[0] + "]", e);
  }

  /**
   * For a successful AJAX event, given a supplied JoinPoint and JSON query, log advice to the logger defined in the aspect object
   * 
   * @param join
   *          of type JoinPoint
   * @param json
   *          of type JSONObject
   */
  @AfterReturning(pointcut = "@annotation(LoggedAction)", returning = "json")
  public void logAjaxEvent(JoinPoint join, JSONObject json) {
    System.out.println("logAjaxEvent: " + join.toString());
    this.log.info("AJAX OK [" + SecurityContextHolder.getContext().getAuthentication().getName() + "] " + join.toString() + " ["
        + json.toString() + "]");
  }

  /**
   * For a failed AJAX event, given a supplied JoinPoint, JSON query and exception, log advice to the logger defined in the aspect object
   * 
   * @param join
   *          of type JoinPoint
   * @param json
   *          of type JSONObject
   * @param e
   *          of type Exception
   */
  public void logFailedAjaxEvent(JoinPoint join, JSONObject json, Exception e) {
    this.log.warn("AJAX FAIL [" + SecurityContextHolder.getContext().getAuthentication().getName() + "] " + join.toString() + " ["
        + json.toString() + "]", e);
  }
}
