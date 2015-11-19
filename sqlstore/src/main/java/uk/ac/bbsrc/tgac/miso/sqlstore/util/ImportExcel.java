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

package uk.ac.bbsrc.tgac.miso.sqlstore.util;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.manager.LocalSecurityManager;

import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ExperimentImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryQCImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleQCImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerReferenceImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StudyImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.emPCR;
import uk.ac.bbsrc.tgac.miso.core.data.impl.emPCRDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.illumina.IlluminaRun;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ls454.LS454Run;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.data.type.ProgressType;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoRequestManager;

/**
 * Created by IntelliJ IDEA. User: bian Date: 10-Jun-2010 Time: 10:14:21 read excel 2007+ file
 */
public class ImportExcel {
  protected static final Logger log = LoggerFactory.getLogger(ImportExcel.class);

  static MisoRequestManager misoManager = new MisoRequestManager();
  static LocalSecurityManager securityManager = new LocalSecurityManager();

  static SecurityProfile sp = new SecurityProfile();

  public static void main(String[] args) throws Exception {
    List sheetData = new ArrayList();
    List sheetData1 = new ArrayList();

    List<List<Cell>> ga2Data = new ArrayList<List<Cell>>();
    List<List<Cell>> hiSeqData = new ArrayList<List<Cell>>();

    sp.setAllowAllInternal(true);
    sp.setOwner(securityManager.getUserById(1L));

    InputStream fis2 = null;
    try {
      fis2 = ImportExcel.class.getResourceAsStream(args[0]);
      if (fis2 != null) {
        XSSFWorkbook workbook = new XSSFWorkbook(fis2);

        // GA II sheet
        XSSFSheet gaSheet = workbook.getSheet("Illumina GA");
        for (Row row : gaSheet) {
          Cell[] data = new Cell[9];
          for (int i = 0; i < 9; i++) {
            data[i] = row.getCell(i, Row.CREATE_NULL_AS_BLANK);
          }
          ga2Data.add(Arrays.asList(data));
        }
        processIlluminaData(misoManager.getPlatformById(5), ga2Data);

        // HiSeq sheet
        XSSFSheet hiSheet = workbook.getSheet("HiSeq");
        for (Row row : hiSheet) {
          Cell[] data = new Cell[9];
          for (int i = 0; i < 9; i++) {
            data[i] = row.getCell(i, Row.CREATE_NULL_AS_BLANK);
          }
          hiSeqData.add(Arrays.asList(data));
        }
        processIlluminaData(misoManager.getPlatformById(15), hiSeqData);
      }
    } catch (IOException e) {
      log.error("Excel", e);
    } finally {
      if (fis2 != null) {
        fis2.close();
      }
    }
  }

  @Deprecated
  private static void process454Data(List sheetData) throws Exception {
    // start from the third line of the data.
    for (int l = 2; l < sheetData.size(); l++) {
      List list = (List) sheetData.get(l);
      // different columns for different processing
      // RunName
      XSSFCell cellb = (XSSFCell) list.get(2);
      // Region
      XSSFCell cellh = (XSSFCell) list.get(7);
      int chamber = new Double(cellh.getNumericCellValue()).intValue();
      String chamberStr = String.valueOf(chamber);
      String runD = "";
      if (chamber > 1) {
        List list1 = (List) sheetData.get(l - (chamber - 1));
        XSSFCell cellb1 = (XSSFCell) list1.get(2);
        runD = cellb1.getRichStringCellValue().getString();
      } else {
        runD = cellb.getRichStringCellValue().getString();
      }
      // Date
      XSSFCell cellc = (XSSFCell) list.get(1);
      Date date = null;
      DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
      if (chamber > 1) {
        List list1 = (List) sheetData.get(l - (chamber - 1));
        XSSFCell cellc1 = (XSSFCell) list1.get(1);
        date = df.parse(cellc1.getDateCellValue().toString());
      } else {
        date = df.parse(cellc.getDateCellValue().toString());
      }

      String user = "importer";
      // Sample
      XSSFCell celld = (XSSFCell) list.get(3);
      String cd = null;
      if (celld.getCellType() == Cell.CELL_TYPE_NUMERIC) {
        cd = String.valueOf(celld.getNumericCellValue());
      } else {
        cd = celld.getRichStringCellValue().getString();
      }
      // Project
      XSSFCell celle = (XSSFCell) list.get(4);
      String ce = celle.getRichStringCellValue().getString();

      Project project = null;
      String name = "IMP";
      Experiment experiment = null;

      for (Project p : misoManager.listAllProjects()) {
        String projectDescription = p.getDescription();

        if (projectDescription.equals(ce)) {
          project = p;
          Study s = new StudyImpl();
          s.setProject(p);
          s.setAlias(ce + chamberStr);
          s.setDescription(ce + chamberStr);
          s.setSecurityProfile(sp);
          misoManager.saveStudy(s);
          experiment = new ExperimentImpl();
          experiment.setStudy(s);
          experiment.setTitle(ce + chamberStr);
          experiment.setAlias(ce + chamberStr);
          experiment.setDescription(ce + chamberStr);
          experiment.setSecurityProfile(sp);
          experiment.setPlatform(misoManager.getPlatformById(13));
          experiment.setExperimentId(misoManager.saveExperiment(experiment));
          break;
        }
      }

      if (project == null) {
        Project newp = new ProjectImpl();
        newp.setDescription(ce);
        newp.setProgress(ProgressType.ACTIVE);
        newp.setSecurityProfile(sp);
        misoManager.saveProject(newp);
        Study s = new StudyImpl();
        s.setProject(newp);
        s.setAlias(ce + chamberStr);
        s.setDescription(ce + chamberStr);
        s.setSecurityProfile(sp);
        s.setStudyId(misoManager.saveStudy(s));
        experiment = new ExperimentImpl();
        experiment.setStudy(s);
        experiment.setTitle(ce + chamberStr);
        experiment.setAlias(ce + chamberStr);
        experiment.setDescription(ce + chamberStr);
        experiment.setSecurityProfile(sp);
        experiment.setPlatform(misoManager.getPlatformById(13));
        experiment.setExperimentId(misoManager.saveExperiment(experiment));
        project = newp;
      }

      Sample sample = null;
      Pool pool = null;
      for (Sample sa : misoManager.listAllSamples()) {
        Long sampleId = sa.getSampleId();
        String sampleDescription = sa.getDescription();

        if (sampleDescription.equals(cd)) {
          sample = sa;
          Library li = new LibraryImpl();
          li.setAlias(cd + chamberStr);
          li.setCreationDate(date);
          li.setDescription(cd + chamberStr);
          li.setInitialConcentration(0.0);
          li.setSample(sample);
          li.setSecurityProfile(sp);
          li.setPaired(false);
          misoManager.saveLibrary(li);
          LibraryDilution d = new LibraryDilution();
          d.setLibrary(li);
          d.setIdentificationBarcode("0");
          d.setConcentration(0.0);
          d.setCreationDate(date);
          d.setDilutionCreator(user);
          d.setSecurityProfile(sp);
          misoManager.saveLibraryDilution(d);
          emPCR e = new emPCR();
          e.setLibraryDilution(d);
          e.setCreationDate(date);
          e.setConcentration(0.0);
          e.setPcrCreator(user);
          e.setSecurityProfile(sp);
          misoManager.saveEmPCR(e);
          emPCRDilution ed = new emPCRDilution();
          ed.setEmPCR(e);
          ed.setConcentration(0.0);
          ed.setIdentificationBarcode("0");
          ed.setCreationDate(date);
          ed.setDilutionCreator(user);
          ed.setSecurityProfile(sp);
          misoManager.saveEmPCRDilution(ed);
          Pool l454p = new PoolImpl();
          l454p.setPlatformType(PlatformType.LS454);
          l454p.setConcentration(0.0);
          l454p.setCreationDate(date);
          l454p.setIdentificationBarcode("0");
          l454p.addExperiment(experiment);
          l454p.addPoolableElement(ed);
          l454p.setSecurityProfile(sp);
          misoManager.savePool(l454p);
          pool = l454p;
          break;
        }
      }
      if (sample == null) {
        Sample news = new SampleImpl();
        news.setDescription(cd);
        news.setProject(project);
        news.setAlias(cd);
        news.setSecurityProfile(sp);
        news.setSampleType("GENOMIC");
        news.setQcPassed(true);
        news.setReceivedDate(date);
        news.setScientificName(cd);
        misoManager.saveSample(news);
        sample = news;
        Library li = new LibraryImpl();
        li.setAlias(cd + chamberStr);
        li.setCreationDate(date);
        li.setDescription(cd + chamberStr);
        li.setInitialConcentration(0.0);
        li.setSample(sample);
        li.setSecurityProfile(sp);
        li.setPaired(false);
        misoManager.saveLibrary(li);
        LibraryDilution d = new LibraryDilution();
        d.setLibrary(li);
        d.setIdentificationBarcode("0");
        d.setConcentration(0.0);
        d.setCreationDate(date);
        d.setDilutionCreator(user);
        d.setSecurityProfile(sp);
        misoManager.saveLibraryDilution(d);
        emPCR e = new emPCR();
        e.setLibraryDilution(d);
        e.setCreationDate(date);
        e.setConcentration(0.0);
        e.setPcrCreator(user);
        e.setSecurityProfile(sp);
        misoManager.saveEmPCR(e);
        emPCRDilution ed = new emPCRDilution();
        ed.setEmPCR(e);
        ed.setConcentration(0.0);
        ed.setIdentificationBarcode("0");
        ed.setCreationDate(date);
        ed.setDilutionCreator(user);
        ed.setSecurityProfile(sp);
        misoManager.saveEmPCRDilution(ed);
        Pool l454p = new PoolImpl();
        l454p.setPlatformType(PlatformType.LS454);
        l454p.setConcentration(0.0);
        l454p.setCreationDate(date);
        l454p.setIdentificationBarcode("0");
        l454p.addExperiment(experiment);
        l454p.addPoolableElement(ed);
        l454p.setSecurityProfile(sp);
        misoManager.savePool(l454p);
        pool = l454p;
      }

      Run run = new LS454Run();
      if (chamber > 1) {
        for (Run r : misoManager.listAllLS454Runs()) {
          String runDescription = r.getDescription();

          if (runDescription.equals(runD)) {
            run = r;
            log.info("Run [using existing run]: " + run);
          }
        }
      } else {
        run.setDescription(runD);
        run.setPairedEnd(false);
        run.setPlatformType(PlatformType.LS454);
        run.setSecurityProfile(sp);
        misoManager.saveRun(run);
        log.info("Run [new run created]: " + run);
      }
    }

  }

  private static void processIlluminaData(Platform platform, List<List<Cell>> sheetData) throws Exception {
    for (int l = 0; l < sheetData.size(); l++) {
      List<Cell> list = sheetData.get(l);
      Cell cell0a = list.get(0);
      String cell0astr = getCellContents(cell0a);
      if (cell0astr.equals("ILLUMINA") || cell0astr.contains("ILLUMINA")) {
        String cell0astr2 = cell0astr.substring(cell0astr.indexOf(":") + 1).trim();
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date date = df.parse(cell0astr2);

        String user = getCellContents(sheetData.get(l + 1).get(0));

        boolean paired = false;
        if (sheetData.get(l + 4).get(3) != null && getCellContents(sheetData.get(l + 4).get(3)).equals("P")) {
          paired = true;
        }

        List<Cell> list9 = sheetData.get(l + 9);
        Cell cell9b = list9.get(1);
        String runFilePath = getCellContents(cell9b);
        if (!isStringEmptyOrNull(runFilePath)) {
          String runDirRegex = ".*[\\\\/]*([\\d]{6}_[A-z0-9]+_[\\d]{4,5}[A-z0-9_\\+]*)[\\\\/]*.*";
          Pattern runDirPattern = Pattern.compile(runDirRegex);
          Matcher rm = runDirPattern.matcher(runFilePath);
          if (rm.matches()) {
            Run run = misoManager.getRunByAlias(rm.group(1));
            if (run == null) {
              run = new IlluminaRun();
              run.setAlias(rm.group(1));
              run.setDescription(cell0astr);
              run.setPairedEnd(paired);
              run.setPlatformType(PlatformType.ILLUMINA);
              run.setSecurityProfile(sp);
              run.setFilePath(runFilePath);

              String machine = run.getAlias().split("_")[1];
              SequencerReference sr = misoManager.getSequencerReferenceByName(machine);
              if (sr != null) {
                run.setSequencerReference(sr);
              } else {
                if (machine.equals("SN319")) {
                  machine = "N78135";
                } else if (machine.equals("SN790")) {
                  machine = "N78428";
                }
                sr = new SequencerReferenceImpl(machine, InetAddress.getByName(machine), platform);
                sr.setAvailable(true);
                long srId = misoManager.saveSequencerReference(sr);
                sr.setId(srId);
                run.setSequencerReference(sr);
              }

              run.getStatus().setInstrumentName(machine);
              long runId = misoManager.saveRun(run);
              run.setRunId(runId);
              log.info("Run [new illumina run created]: " + run);
            }
          }
        }
      }
    }
  }

  private static Project processProject(String projectDescr) throws Exception {
    Project project = null;
    for (Project p : misoManager.listAllProjects()) {
      if (p.getDescription().toLowerCase().equals(projectDescr.toLowerCase())) {
        project = p;
        break;
      }
    }

    if (project == null) {
      Project newp = new ProjectImpl();
      newp.setDescription(projectDescr);
      newp.setProgress(ProgressType.ACTIVE);
      newp.setSecurityProfile(sp);
      long projectId = misoManager.saveProject(newp);
      newp.setProjectId(projectId);
      project = newp;
    }
    return project;
  }

  private static Experiment processExperiment(Project project) throws Exception {
    String projectDescr = project.getDescription();

    Study s = new StudyImpl();
    s.setProject(project);
    s.setAlias(projectDescr);
    s.setDescription(projectDescr);
    s.setSecurityProfile(sp);
    misoManager.saveStudy(s);

    Experiment experiment = new ExperimentImpl();
    experiment.setStudy(s);
    experiment.setTitle(projectDescr);
    experiment.setAlias(projectDescr);
    experiment.setDescription(projectDescr);
    experiment.setSecurityProfile(sp);
    experiment.setPlatform(misoManager.getPlatformById(6));
    experiment.setExperimentId(misoManager.saveExperiment(experiment));

    return experiment;
  }

  private static Sample processSample(String sampleDescr, String sampleAlias, Date date, Project project) throws Exception {
    String user = "importer";
    Sample sample = null;
    for (Sample sa : misoManager.listAllSamples()) {
      if (sa.getDescription().equals(sampleDescr)) {
        sample = sa;
        break;
      }
    }
    if (sample == null) {
      Sample news = new SampleImpl();
      news.setDescription(sampleDescr);
      news.setProject(project);
      news.setAlias(sampleAlias);
      news.setSecurityProfile(sp);
      news.setSampleType("GENOMIC");
      news.setQcPassed(true);
      news.setReceivedDate(date);
      news.setScientificName("F. bar");
      long sampleId = misoManager.saveSample(news);
      news.setSampleId(sampleId);
      sample = news;

      SampleQC sampleQC = new SampleQCImpl();
      sampleQC.setSample(news);
      sampleQC.setQcDate(date);
      sampleQC.setResults(0.0);
      sampleQC.setQcType(misoManager.getSampleQcTypeById(3L));
      sampleQC.setQcCreator(sample.getSecurityProfile().getOwner().getLoginName());
      long sampleQCId = misoManager.saveSampleQC(sampleQC);
      sampleQC.setId(sampleQCId);
    }
    return sample;
  }

  private static Library processLibrary(PlatformType pt, Sample sample, String libDesc, String libConc, String libType, String readLength,
      boolean paired, Date date) throws Exception {
    String sNum = sample.getAlias().substring(3, sample.getAlias().lastIndexOf("_"));
    String libAlias = "XX_L" + sNum + "." + (sample.getLibraries().size() + 1) + "_F.bar";

    if (libConc.contains("pmol")) {
      libConc = libConc.replace("pmol", "");
    }

    if (isStringEmptyOrNull(libConc)) {
      libConc = "0";
    }

    Library li = new LibraryImpl();
    li.setAlias(libAlias);
    li.setCreationDate(date);
    li.setDescription(libDesc);
    li.setInitialConcentration(new Double(libConc));
    li.setSample(sample);
    li.setSecurityProfile(sp);
    li.setPaired(paired);

    if (libType != null) {
      li.setPlatformName(pt.getKey());
      if (pt.equals(PlatformType.ILLUMINA)) {
        if (libType.toLowerCase().equals("shotgun")) {
          if (paired) {
            li.setLibraryType(misoManager.getLibraryTypeById(1));
          } else {
            li.setLibraryType(misoManager.getLibraryTypeById(1));
          }
        } else if (libType.toLowerCase().equals("matepair")) {
          li.setLibraryType(misoManager.getLibraryTypeById(2));
        } else if (libType.toLowerCase().equals("rna-seq")) {
          li.setLibraryType(misoManager.getLibraryTypeById(3));
        } else {
          li.setLibraryType(misoManager.getLibraryTypeById(23));
        }
      }
    }

    li.setLibrarySelectionType(misoManager.getLibrarySelectionTypeById(5));
    li.setLibraryStrategyType(misoManager.getLibraryStrategyTypeById(15));

    long libraryId = misoManager.saveLibrary(li);
    li.setLibraryId(libraryId);

    LibraryQC libQC = new LibraryQCImpl();
    libQC.setLibrary(li);

    if (readLength.contains("bp")) {
      readLength = readLength.replace("bp", "");
    }
    if (isStringEmptyOrNull(readLength)) {
      readLength = "0";
    }

    libQC.setInsertSize((int) Double.parseDouble(readLength));
    libQC.setQcDate(date);
    libQC.setResults(0.0);
    libQC.setQcType(misoManager.getLibraryQcTypeById(2L));
    libQC.setQcCreator(sample.getSecurityProfile().getOwner().getLoginName());

    long libraryQcId = misoManager.saveLibraryQC(libQC);
    libQC.setId(libraryQcId);

    return li;
  }

  private static String getCellContents(Cell c) {
    if (c.getCellType() == Cell.CELL_TYPE_NUMERIC) {
      return String.valueOf(c.getNumericCellValue());
    } else if (c.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
      return String.valueOf(c.getBooleanCellValue());
    } else if (c.getCellType() == Cell.CELL_TYPE_STRING) {
      return c.getRichStringCellValue().toString();
    } else if (c.getCellType() == Cell.CELL_TYPE_FORMULA) {
      return c.getCellFormula();
    } else {
      return "";
    }
  }
}
