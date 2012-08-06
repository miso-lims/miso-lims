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

package uk.ac.bbsrc.tgac.miso.core.data.decorator.submission.era;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.data.decorator.AbstractSubmittableDecorator;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.service.submission.FilePathGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.submission.TGACIlluminaFilepathGenerator;
import uk.ac.bbsrc.tgac.miso.core.util.TgacSubmissionConstants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;

/**
 * Decorates a SequencerPoolPartition so that an ERA Run submission XML document can be built from it
 *
 * @author Rob Davey
 * @date 12-Oct-2010
 * @since 0.0.2
 */
public class EraRunDecorator extends AbstractSubmittableDecorator<Document> {

  private Run r;
  protected static final Logger log = LoggerFactory.getLogger(EraRunDecorator.class);
  public EraRunDecorator(Submittable submittable, Document submission) {
    super(submittable);
    this.submission = submission;
  }

  public EraRunDecorator(Submittable submittable, Run r, Document submission) {
    super(submittable);
    this.submission = submission;
    this.r = r;
  }

  public void buildSubmission() {
    SequencerPoolPartition p = (SequencerPoolPartition)submittable;

    if (p.getPool() != null) {
      Pool<? extends Poolable> pool = p.getPool();

      log.debug("pool:" + pool.getName());
      //TODO - fix this. not great.
      //Run r = p.getFlowcell().getRun();
      Run r = p.getSequencerPartitionContainer().getRun();

      if (r == null) r = this.r;

      if (r != null) {

        Element run = submission.createElementNS(null, "RUN");
        run.setAttribute("alias", r.getName());
        run.setAttribute("run_center", TgacSubmissionConstants.CENTRE_ACRONYM.getKey());
        if (r.getStatus()!= null && r.getStatus().getHealth().equals(HealthType.Completed)) {
          DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
          run.setAttribute("run_date", df.format(r.getStatus().getCompletionDate()));
        }
        run.setAttribute("center_name", TgacSubmissionConstants.CENTRE_NAME.getKey());
        //doc.appendChild(run);
        Collection<Experiment> es = pool.getExperiments();
        for (Experiment e : es) {
          Element experimentRef = submission.createElementNS(null, "EXPERIMENT_REF");
          experimentRef.setAttribute("refname", e.getName());
          experimentRef.setAttribute("refcenter", TgacSubmissionConstants.CENTRE_NAME.getKey());
          run.appendChild(experimentRef);
        }

        Element dataBlock = submission.createElementNS(null, "DATA_BLOCK");
        //dataBlock.setAttribute("name", l.getFlowcell().getName());
        dataBlock.setAttribute("sector", Integer.toString(p.getPartitionNumber()));
        //dataBlock.setAttribute("region", "0"); // tile number

        Element files = submission.createElementNS(null, "FILES");
        Collection<? extends Dilution> dilutions = pool.getDilutions();
        FilePathGenerator fpg = new TGACIlluminaFilepathGenerator();
        for(Dilution libraryDilution : dilutions) {

            //replace with FPG call?
           // String fileName=libraryDilution.getLibrary().getName()+"_"+
           //         libraryDilution.getLibrary().getTagBarcode().getSequence()+
           //         "_L00"+p.getPartitionNumber()+"*.fastq.gz";

          try{
            String fileName = fpg.generateFilePath(p,libraryDilution).getName();
            Element file = submission.createElementNS(null,"FILE");
            file.setAttribute("filename", fileName);
            file.setAttribute("filetype", "fastq");
            //file.setAttribute("checksum_method", "MD5");
            //file.setAttribute("checksum", "not implemented yet");
            Element readLabel = submission.createElementNS(null, "READ_LABEL");
            file.appendChild(readLabel);
            files.appendChild(file);
          }
          catch (Exception e) {
            e.printStackTrace();
          }
        }
        dataBlock.appendChild(files);

        run.appendChild(dataBlock);
  /*
        for (String s : p.getDataFile()) {
          Element file = doc.createElementNS(null, "FILE");
          file.setAttribute("filename", s);
          file.setAttribute("filetype", s.substring(s.lastIndexOf("."), s.length()));
        }
  */

        if (submission.getElementsByTagName("RUN_SET").item(0) != null) {
          submission.getElementsByTagName("RUN_SET").item(0).appendChild(run);
        }
        else {
          submission.appendChild(run);
        }
      }
      /*
      else {
        Element runSet=submission.createElementNS(null,"RUN_SET");
        submission.appendChild(runSet);
        runSet.appendChild(run);
      }
      */
    }
  }
}
