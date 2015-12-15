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

package uk.ac.bbsrc.tgac.miso.analysis.tgac.test;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.bbsrc.tgac.miso.integration.util.IntegrationException;
import uk.ac.bbsrc.tgac.miso.integration.util.IntegrationUtils;

/**
 * uk.ac.bbsrc.tgac.miso.analysis.tgac.test
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 03/11/11
 * @since 0.1.3
 */
public class AnalysisServerClient {
  protected static final Logger log = LoggerFactory.getLogger(AnalysisServerClient.class);

  public static void main(String[] args) {
    try {
      JSONObject q1 = new JSONObject();
      q1.put("query", "getRunningTasks");
      String q1s = q1.toString();
      log.info("QUERY: " + q1s);
      String q1rs = IntegrationUtils.sendMessage(IntegrationUtils.prepareSocket("norwich", 7898), q1s);
      log.info("RESULT: " + q1rs);

      /*
       * JSONObject q1 = new JSONObject(); q1.put("query", "getPendingTasks"); String q1s = q1.toString(); log.info("QUERY: " + q1s); String
       * q1rs = IntegrationUtils.sendMessage(IntegrationUtils.prepareSocket("norwich", 7898), q1s); log.info("RESULT: " + q1rs);
       * 
       * JSONObject q2 = new JSONObject(); q2.put("query", "getPipelines"); String q2s = q2.toString(); log.info("QUERY: " + q2s); String
       * q2rs = IntegrationUtils.sendMessage(IntegrationUtils.prepareSocket("norwich", 7898), q2s); log.info("RESULT: " + q2rs);
       */
      JSONObject task = new JSONObject();

      JSONObject j = new JSONObject();
      j.put("priority", "MEDIUM");
      j.put("pipeline", "tgac_primary_analysis");

      JSONObject params = new JSONObject();

      String runName = "120313_SN319_0209_AD0HPAACXX";
      String instrument = "SN319";

      String basecalls = "/data/live/ngs-hiseq-1/" + runName + "/Data/Intensities/BaseCalls";
      String fastqs = "/data/live/ngs-hiseq-1/" + runName + "/Data/Intensities/BaseCalls/PriTest";

      // necessary to name the task appropriately
      params.put("RunAccession", runName);

      params.put("basecall-path", basecalls);
      params.put("fastq-path", fastqs);
      params.put("sample-sheet-path", basecalls + "/SampleSheet.csv");
      params.put("sample-sheet-string",
          "FCID,Lane,SampleID,SampleRef,Index,Description,Control,Recipe,Operator,Project\\n"
              + "D0HPAACXX,1,SAM1352,IS_S1_S.cerevisiae,CGATGT,MC1,N,NA,Robert Davey,GEL_IanStansfield_ABDN.IS.20110606.01\\n"
              + "D0HPAACXX,1,SAM1353,IS_S2_S.cerevisiae,TGACCA,MC2,N,NA,Robert Davey,GEL_IanStansfield_ABDN.IS.20110606.01\\n"
              + "D0HPAACXX,1,SAM1354,IS_S3_S.cerevisiae,ACAGTG,Mut1,N,NA,Robert Davey,GEL_IanStansfield_ABDN.IS.20110606.01\\n"
              + "D0HPAACXX,1,SAM1355,IS_S4_S.cerevisiae,GCCAAT,Mut2,N,NA,Robert Davey,GEL_IanStansfield_ABDN.IS.20110606.01\\n"
              + "D0HPAACXX,2,SAM1356,IS_S5_S.cerevisiae,CAGATC,Wt1,N,NA,Robert Davey,GEL_IanStansfield_ABDN.IS.20110606.01\\n"
              + "D0HPAACXX,2,SAM1357,IS_S6_S.cerevisiae,CTTGTA,Wt2,N,NA,Robert Davey,GEL_IanStansfield_ABDN.IS.20110606.01\\n"
              + "D0HPAACXX,2,SAM1358,IS_S7_S.cerevisiae,GCCAAT,CR14,N,NA,Robert Davey,GEL_IanStansfield_ABDN.IS.20110606.01\\n"
              + "D0HPAACXX,3,SAM1218,RR_S1_X.campestris,CGATGT,Delta C1,N,NA,Robert Davey,GEL_RobertRyan_BIOMERIT.RR.20100609.01\\n"
              + "D0HPAACXX,3,SAM1219,RR_S2_X.campestris,TGACCA,Delta G1,N,NA,Robert Davey,GEL_RobertRyan_BIOMERIT.RR.20100609.01\\n"
              + "D0HPAACXX,3,SAM1220,RR_S3_X.campestris,GCCAAT,8004-1,N,NA,Robert Davey,GEL_RobertRyan_BIOMERIT.RR.20100609.01\\n"
              + "D0HPAACXX,4,SAM1221,RR_S4_X.campestris,CAGATC,8004-2,N,NA,Robert Davey,GEL_RobertRyan_BIOMERIT.RR.20100609.01\\n"
              + "D0HPAACXX,4,SAM1222,RR_S5_X.campestris,CTTGTA,Delta H1,N,NA,Robert Davey,GEL_RobertRyan_BIOMERIT.RR.20100609.01\\n"
              + "D0HPAACXX,4,SAM1223,RR_S6_X.campestris,AGTCAA,DeltaF1,N,NA,Robert Davey,GEL_RobertRyan_BIOMERIT.RR.20100609.01\\n"
              + "D0HPAACXX,5,SAM1301,RR_S1_X.campestris(1),CGATGT,Delta C/F 1,N,NA,Robert Davey,GEL_RobertRyan_BIOMERIT.RR.20100719.01\\n"
              + "D0HPAACXX,6,SAM716,NJ_S2_R.gnavus,GCCAAT,RNA Glc E1,N,NA,Robert Davey,CCC_VI_11_NathalieJuge_CommensalRNA\\n"
              + "D0HPAACXX,6,SAM717,NJ_S3_R.gnavus,CAGATC,RNA Glc ATCC29149,N,NA,Robert Davey,CCC_VI_11_NathalieJuge_CommensalRNA\\n"
              + "D0HPAACXX,6,SAM718,NJ_S4_R.gnavus,AGTCAA,RNA Glc ATCC35913,N,NA,Robert Davey,CCC_VI_11_NathalieJuge_CommensalRNA\\n"
              + "D0HPAACXX,7,SAM1303,SS_S11_e.coli,TGACCA,dUTP_mRNA seq,N,NA,Robert Davey,DEVELOPMENT_StrandspecificRNAtest\\n");
      // ls
      params.put("lane-value", "8");
      params.put("makefile-path", fastqs + "/Makefile");
      params.put("nucleic-acid-type", "dna");

      params.put("casava-version", "1.8.2");
      params.put("software-version", "HCS_1.4_RTA_1.12");
      params.put("chemistry-version", "TruSeqSBS_3");

      params.put("instrument-id", instrument);

      j.put("params", params);
      task.put("submit", j);

      String response = IntegrationUtils.sendMessage(IntegrationUtils.prepareSocket("norwich", 7898), task.toString());
      if (!isStringEmptyOrNull(response)) {
        log.info("RESPONSE: " + response);
      }
    } catch (IntegrationException e) {
      log.error(e.getMessage());
      e.printStackTrace();
    }
  }
}
