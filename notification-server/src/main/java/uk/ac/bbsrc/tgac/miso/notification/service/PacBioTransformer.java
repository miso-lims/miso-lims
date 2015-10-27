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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.Message;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.ac.bbsrc.tgac.miso.core.exception.InterrogationException;
import uk.ac.bbsrc.tgac.miso.core.service.integration.ws.pacbio.PacBioService;
import uk.ac.bbsrc.tgac.miso.core.service.integration.ws.pacbio.PacBioServiceWrapper;
import uk.ac.bbsrc.tgac.miso.integration.util.IntegrationUtils;
import uk.ac.bbsrc.tgac.miso.core.util.SubmissionUtils;
import uk.ac.bbsrc.tgac.miso.integration.context.ApplicationContextProvider;
import uk.ac.bbsrc.tgac.miso.tools.run.util.FileSetTransformer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * uk.ac.bbsrc.tgac.miso.notification.util
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 10/04/12
 * @since 0.1.6
 */
public class PacBioTransformer implements FileSetTransformer<String, String, File> {
  protected static final Logger log = LoggerFactory.getLogger(PacBioTransformer.class);

  private final Pattern cellDirPattern = Pattern.compile("[A-Z]{1}[0-9]{2}_[0-9]{1}");

  private final DateFormat startDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
  private final Calendar cal = Calendar.getInstance();

  public Map<String, String> transform(Message<Set<File>> message) {
    return transform(message.getPayload());
  }

  /*
   * 
   * <?xml version="1.0" encoding="utf-8"?> <Metadata xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
   * xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns="http://pacificbiosciences.com/PAP/Metadata.xsd">
   * <InstCtrlVer>1.3.0.0.103161</InstCtrlVer> <SigProcVer>NRT@172.31.128.10:8082, SwVer=1300.103161, HwVer=1.0</SigProcVer> <Run>
   * <RunId>r000056_42165_120321</RunId> <Name>2012_03_21_Streptomyces_10kb_C2</Name> <WhenCreated>2012-03-21T13:09:38</WhenCreated>
   * <WhenStarted>2012-03-21T06:46:09</WhenStarted> </Run> <Movie> <WhenStarted>2012-03-21T10:54:25.802952+00:00</WhenStarted>
   * <DurationInSec>5400</DurationInSec> <Number>0</Number> </Movie> <Sample> <Name>S.bottropensis_10kb_C2</Name>
   * <PlateId>2012_03_21_Streptomyces_10kb_C2</PlateId> <WellName>A04</WellName> <Concentration>0</Concentration>
   * <SampleReuseEnabled>false</SampleReuseEnabled> <UseCount>1</UseCount> </Sample> <InstrumentId>1</InstrumentId>
   * <InstrumentName>42165</InstrumentName> <CollectionProtocol>Standard Seq v2</CollectionProtocol> <CollectionNumber>2</CollectionNumber>
   * <CellIndex>1</CellIndex> <SetNumber>1</SetNumber> <EightPac> <PartNumber>0015</PartNumber> <LotNumber>230088</LotNumber>
   * <Barcode>10030156255000000152300880723124</Barcode> <ExpirationDate>2012-07-23</ExpirationDate> </EightPac> <TemplatePrep> <Name>DNA
   * Template Prep Kit 2.0 (3Kb - 10Kb)</Name> <PartNumber>001540835</PartNumber> <LotNumber>110170</LotNumber>
   * <Barcode>110170001540835072512</Barcode> <ExpirationDate>2012-07-25</ExpirationDate>
   * <AdapterSequence>ATCTCTCTCttttcctcctcctccgttgttgttgttGAGAGAGAT</AdapterSequence> <InsertSize>10000</InsertSize> </TemplatePrep>
   * <BindingKit> <Name>DNA/Polymerase Binding Kit 2.0 (24 Rxn)</Name> <PartNumber>001672551</PartNumber> <LotNumber>120126</LotNumber>
   * <Barcode>120126001672551071712</Barcode> <ExpirationDate>2012-07-17</ExpirationDate> <Control>Strobe_v1</Control>
   * <IsControlUsed>false</IsControlUsed> </BindingKit> <SequencingKit> <Name>ReagentPlate0</Name> <PartNumber>001558034</PartNumber>
   * <LotNumber>111220</LotNumber> <Barcode>111220574001558034072012</Barcode> <ExpirationDate>2012-07-20</ExpirationDate>
   * <Protocol>C2ReagentMixingProtocol_DWP</Protocol> </SequencingKit> <ReagentTube0> <Name>ReagentTube0-0</Name>
   * <PartNumber>001028310</PartNumber> <LotNumber>111219</LotNumber> <Barcode>111219871001028310120112</Barcode>
   * <ExpirationDate>2012-12-01</ExpirationDate> </ReagentTube0> <Primary> <Protocol>BasecallerV1</Protocol>
   * <ConfigFileName>1-3-0_Standard_C2.xml</ConfigFileName> <ResultsFolder>Analysis_Results</ResultsFolder>
   * <CollectionPathUri>rsy://pacbio-1/sequencer_output/2012_03_21_Streptomyces_10kb_C2_56/A04_2/</CollectionPathUri>
   * <CollectionFileCopy>Fasta</CollectionFileCopy> <CollectionFileCopy>Fastq</CollectionFileCopy> </Primary> <Secondary> <ProtocolName />
   * <CellCountInJob>0</CellCountInJob> </Secondary> <Custom> <KeyValue
   * key="svc:/CentralDataSvc/#Display.Sample_Metadata.User_Defined_Field_1" /> <KeyValue
   * key="svc:/CentralDataSvc/#Display.Sample_Metadata.User_Defined_Field_2" /> <KeyValue
   * key="svc:/CentralDataSvc/#Display.Sample_Metadata.User_Defined_Field_3" /> <KeyValue
   * key="svc:/CentralDataSvc/#Display.Sample_Metadata.User_Defined_Field_4" /> <KeyValue
   * key="svc:/CentralDataSvc/#Display.Sample_Metadata.User_Defined_Field_5" /> <KeyValue
   * key="svc:/CentralDataSvc/#Display.Sample_Metadata.User_Defined_Field_6" /> </Custom> </Metadata>
   */

  @Override
  public Map<String, String> transform(Set<File> files) {
    log.info("Processing " + files.size() + " PacBio run directories...");

    HashMap<String, JSONArray> map = new HashMap<String, JSONArray>();
    map.put("Running", new JSONArray());
    map.put("Completed", new JSONArray());
    map.put("Failed", new JSONArray());
    map.put("Unknown", new JSONArray());

    for (File rootFile : files) {
      if (rootFile.isDirectory()) {
        if (rootFile.canRead()) {
          JSONObject run = new JSONObject();
          String runName = rootFile.getName();

          run.put("runName", runName);
          run.put("fullPath", rootFile.getAbsolutePath());

          run.put("cells", new JSONArray());

          // get cell directories
          for (File cell : rootFile.listFiles(new CellDirFilter())) {
            for (File metadata : cell.listFiles(new MetadataFilenameFilter())) {
              try {
                JSONObject cellObj = new JSONObject();
                String cellXml = SubmissionUtils.transform(metadata, true);
                Document statusDoc = SubmissionUtils.emptyDocument();
                SubmissionUtils.transform(metadata, statusDoc);

                if (!run.has("sequencerName")) {
                  run.put("sequencerName", statusDoc.getElementsByTagName("InstrumentName").item(0).getTextContent());
                  log.debug("Got sequencer name");
                }

                if (!run.has("plateId")) {
                  run.put("plateId", statusDoc.getElementsByTagName("PlateId").item(0).getTextContent());
                  log.debug("Got plate id");
                }

                if (!run.has("startDate")) {
                  run.put("startDate", statusDoc.getElementsByTagName("WhenStarted").item(0).getTextContent());
                  log.debug("Got start date");
                }

                if (!run.has("completionDate") && run.has("startDate")) {
                  try {
                    String sec = statusDoc.getElementsByTagName("DurationInSec").item(0).getTextContent();

                    Date start = startDateFormat.parse(run.getString("startDate"));
                    cal.setTime(start);
                    cal.add(Calendar.SECOND, Integer.parseInt(sec));

                    run.put("completionDate", startDateFormat.format(cal.getTime()));
                    log.debug("Got completion date");
                  } catch (ParseException e) {
                    log.warn("Cannot parse detected start date!");
                  }
                }

                cellObj.put("well", statusDoc.getElementsByTagName("WellName").item(0).getTextContent());
                cellObj.put("index", statusDoc.getElementsByTagName("CellIndex").item(0).getTextContent());
                cellObj.put("sample",
                    ((Element) statusDoc.getElementsByTagName("Sample").item(0)).getElementsByTagName("Name").item(0).getTextContent());
                String comp = URLEncoder.encode(new String(IntegrationUtils.compress(cellXml.getBytes())), "UTF-8");
                cellObj.put("cellStatus", comp);

                run.getJSONArray("cells").add(cellObj);
              } catch (ParserConfigurationException e) {
                e.printStackTrace();
              } catch (TransformerException e) {
                e.printStackTrace();
              } catch (IOException e) {
                log.error(runName + ":: Unable to process run: " + e.getMessage());
                e.printStackTrace();
              }
            }
          }

          try {
            PacBioServiceWrapper pacbioServiceWrapper = ApplicationContextProvider.getApplicationContext()
                .getBean(run.getString("sequencerName"), PacBioServiceWrapper.class);
            PacBioService pacbioService = pacbioServiceWrapper.getPacBioService();

            String plateStatus = pacbioService.getPlateStatus(URLEncoder.encode(run.getString("plateId"), "UTF-8"));
            if ("Complete".equals(plateStatus)) {
              log.debug(runName + " :: Completed");
              if (!run.has("completionDate")) {
                run.put("completionDate", "");
              }
              map.get("Completed").add(run);
            } else if ("Running".equals(plateStatus) || "Failed".equals(plateStatus)) {
              log.debug(runName + " :: " + plateStatus);
              map.get(plateStatus).add(run);
            } else {
              if ("Aborted".equals(plateStatus)) {
                log.debug(runName + " :: Aborted");
                map.get("Failed").add(run);
              } else if ("Ready".equals(plateStatus)) {
                log.debug(runName + " :: Unknown");
                map.get("Unknown").add(run);
              }
            }
          } catch (InterrogationException e) {
            log.warn(e.getMessage() + ". Attempting fall-back date resolution...");
          } catch (UnsupportedEncodingException e) {
            log.warn(e.getMessage() + ". Cannot encode plateId to be URL friendly.");
          }
        } else {
          log.error(rootFile.getName() + " :: Permission denied");
        }
      }
    }

    HashMap<String, String> smap = new HashMap<String, String>();
    for (String key : map.keySet()) {
      smap.put(key, map.get(key).toString());
    }

    return smap;
  }

  private class CellDirFilter implements FileFilter {
    @Override
    public boolean accept(File pathname) {
      Matcher m = cellDirPattern.matcher(pathname.getName());
      return (pathname.isDirectory() && m.matches());
    }
  }

  private class MetadataFilenameFilter implements FilenameFilter {
    @Override
    public boolean accept(File dir, String name) {
      return (name.contains("metadata"));
    }
  }
}
