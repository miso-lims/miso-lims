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

package uk.ac.bbsrc.tgac.miso.core.service.integration.ws.solid;

import javax.xml.bind.annotation.XmlRegistry;

/**
 * This object contains factory methods for each Java content interface and Java element interface generated in the
 * uk.ac.bbsrc.tgac.miso.webapp.service.integration.solid.ws package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the Java representation for XML content. The Java
 * representation of XML content can consist of schema derived interfaces and classes representing the binding of schema type definitions,
 * element declarations and model groups. Factory methods for each of these are provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

  /**
   * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package:
   * uk.ac.bbsrc.tgac.miso.webapp.service.integration.solid.ws
   * 
   */
  public ObjectFactory() {
  }

  /**
   * Create an instance of {@link LibraryInfo }
   * 
   */
  public LibraryInfo createLibraryInfo() {
    return new LibraryInfo();
  }

  /**
   * Create an instance of {@link Run }
   * 
   */
  public Run createRun() {
    return new Run();
  }

  /**
   * Create an instance of {@link LogMessageArray }
   * 
   */
  public LogMessageArray createLogMessageArray() {
    return new LogMessageArray();
  }

  /**
   * Create an instance of {@link RunArray }
   * 
   */
  public RunArray createRunArray() {
    return new RunArray();
  }

  /**
   * Create an instance of {@link SampleInfo }
   * 
   */
  public SampleInfo createSampleInfo() {
    return new SampleInfo();
  }

  /**
   * Create an instance of {@link QualityMetrics }
   * 
   */
  public QualityMetrics createQualityMetrics() {
    return new QualityMetrics();
  }

  /**
   * Create an instance of {@link ClusterStatus }
   * 
   */
  public ClusterStatus createClusterStatus() {
    return new ClusterStatus();
  }

  /**
   * Create an instance of {@link QualityMetricsDetails }
   * 
   */
  public QualityMetricsDetails createQualityMetricsDetails() {
    return new QualityMetricsDetails();
  }

  /**
   * Create an instance of {@link JobQueue }
   * 
   */
  public JobQueue createJobQueue() {
    return new JobQueue();
  }

  /**
   * Create an instance of {@link QueueStatistics }
   * 
   */
  public QueueStatistics createQueueStatistics() {
    return new QueueStatistics();
  }

  /**
   * Create an instance of {@link LogMessage }
   * 
   */
  public LogMessage createLogMessage() {
    return new LogMessage();
  }

}
