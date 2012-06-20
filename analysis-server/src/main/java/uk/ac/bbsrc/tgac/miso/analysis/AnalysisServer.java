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

package uk.ac.bbsrc.tgac.miso.analysis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.ac.bbsrc.tgac.miso.analysis.manager.AnalysisRequestManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * uk.ac.bbsrc.tgac.miso.analysis
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @date 28/10/11
 * @since 0.1.2
 */
public class AnalysisServer {
  protected static final Logger log = LoggerFactory.getLogger(AnalysisServer.class);

  public static void main(String[] args) {
    log.info("Starting Analysis Server...");

    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/tgac-analysis.xml");

    AnalysisRequestManager manager = (AnalysisRequestManager) context.getBean("analysisManager");
    log.info("READY: " + manager.getConanTaskService().getTasks().toString());
  }
}
