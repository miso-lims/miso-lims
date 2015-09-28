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

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Properties;

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
  public EraRunDecorator(Submittable submittable, Properties submissionProperties, Document submission) {
    super(submittable, submissionProperties);
    this.submission = submission;
  }

  public EraRunDecorator(Submittable submittable, Run r, Properties submissionProperties, Document submission) {
    super(submittable, submissionProperties);
    this.submission = submission;
    this.r = r;
  }

  public void buildSubmission() {
    SequencerPoolPartition p = (SequencerPoolPartition)submittable;

    if (p.getPool() != null) {
      Pool<? extends Poolable> pool = p.getPool();

      //TODO - fix this. not great.
      Run r = p.getSequencerPartitionContainer().getRun();

      if (r == null) r = this.r;

      if (r != null) {
        Collection<? extends Poolable> poolables = pool.getPoolableElements();

        for (Poolable poolable : poolables) {
          Element run = submission.createElementNS(null, "RUN");
          run.setAttribute("alias", "L00"+p.getPartitionNumber()+":"+poolable.getName()+":"+r.getAlias());
          run.setAttribute("run_center", submissionProperties.getProperty("submission.centreName"));
          if (r.getStatus()!= null && r.getStatus().getHealth().equals(HealthType.Completed)) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            run.setAttribute("run_date", df.format(r.getStatus().getCompletionDate()));
          }
          run.setAttribute("center_name", submissionProperties.getProperty("submission.centreName"));

          Collection<Experiment> es = pool.getExperiments();
          for (Experiment e : es) {
            Element experimentRef = submission.createElementNS(null, "EXPERIMENT_REF");
            experimentRef.setAttribute("refname", e.getAlias());
            experimentRef.setAttribute("refcenter", submissionProperties.getProperty("submission.centreName"));
            run.appendChild(experimentRef);
          }

          Element dataBlock = submission.createElementNS(null, "DATA_BLOCK");
          dataBlock.setAttribute("sector", Integer.toString(p.getPartitionNumber()));
          if (poolables.size() > 1) {
            //multiplexed
            dataBlock.setAttribute("member_name", poolable.getName());
          }

          Element files = submission.createElementNS(null, "FILES");

//          FilePathGenerator fpg = new TGACIlluminaFilepathGenerator();
//          String basePath = submissionProperties.getProperty("submission.baseReadPath");
//          if (basePath != null) {
//            fpg = new TGACIlluminaFilepathGenerator(basePath);
//          }

          try {
            Element file = submission.createElementNS(null,"FILE");
            file.setAttribute("filename", r.getAlias()+"/"+"00"+p.getPartitionNumber()+"/"+poolable.getName()+"_R1.fastq.gz");
            file.setAttribute("filetype", "fastq");
            file.setAttribute("quality_scoring_system","phred");
            file.setAttribute("quality_encoding", "ascii");
            file.setAttribute("ascii_offset", "!");
            file.setAttribute("checksum_method", "MD5");
            file.setAttribute("checksum", "");
            Element readLabel = submission.createElementNS(null, "READ_LABEL");
            readLabel.setTextContent("1");
            file.appendChild(readLabel);
            files.appendChild(file);

            if (r.getPairedEnd()) {
              Element file2 = submission.createElementNS(null,"FILE");
              file2.setAttribute("filename", r.getAlias()+"/"+"00"+p.getPartitionNumber()+"/"+poolable.getName()+"_R2.fastq.gz");
              file2.setAttribute("filetype", "fastq");
              file2.setAttribute("quality_scoring_system","phred");
              file2.setAttribute("quality_encoding", "ascii");
              file2.setAttribute("ascii_offset", "!");
              file2.setAttribute("checksum_method", "MD5");
              file2.setAttribute("checksum", "");
              Element readLabel2 = submission.createElementNS(null, "READ_LABEL");
              readLabel2.setTextContent("2");
              file2.appendChild(readLabel2);
              files.appendChild(file2);
            }
          }
          catch (Exception e) {
            e.printStackTrace();
          }

          dataBlock.appendChild(files);
          run.appendChild(dataBlock);

          submission.getElementsByTagName("RUN_SET").item(0).appendChild(run);
        }
      }
    }
  }
}
