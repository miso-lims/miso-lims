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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.data.decorator.AbstractSubmittableDecorator;
import uk.ac.bbsrc.tgac.miso.core.data.type.SubmissionActionType;
import uk.ac.bbsrc.tgac.miso.core.util.TgacSubmissionConstants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Decorates a Submission so that an ERA Submission submission XML document can be built from it
 *
 * @author Rob Davey
 * @date 12-Oct-2010
 * @since 0.0.2
 */
public class EraSubmissionDecorator extends AbstractSubmittableDecorator<Document> {

  public EraSubmissionDecorator(Submission submittable, Document submission) {
    super(submittable);
    this.submission = submission;
  }

  public void buildSubmission() {
    submittable.buildSubmission();
    Submission sub = (Submission)submittable;

    if (submission != null) {
      Element s = submission.createElementNS(null, "SUBMISSION");
      s.setAttribute("alias", sub.getAlias());
      DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
      s.setAttribute("submission_date", df.format(new Date()));
      //s.setAttribute("submission_comment", submission.getComment());
      s.setAttribute("center_name", TgacSubmissionConstants.CENTRE_ACRONYM.getKey());

      Element title = submission.createElementNS(null, "TITLE");
      title.setTextContent(sub.getTitle());
      s.appendChild(title);

      Element contacts = submission.createElementNS(null, "CONTACTS");
      Element contact = submission.createElementNS(null, "CONTACT");
      contact.setAttribute("name", "Robert Davey"); //TODO - add submitterName to Submission object
      contacts.appendChild(contact);
      s.appendChild(contacts);

      SubmissionActionType sat = sub.getSubmissionActionType();

      Map<String, List<Submittable<Document>>> map = new HashMap<String, List<Submittable<Document>>>();
      map.put("study", new ArrayList<Submittable<Document>>());
      map.put("sample", new ArrayList<Submittable<Document>>());
      map.put("experiment", new ArrayList<Submittable<Document>>());
      map.put("run", new ArrayList<Submittable<Document>>());

      Set<Submittable<Document>> subs = sub.getSubmissionElements();
      for (Submittable<Document> subtype : subs) {
        if (subtype instanceof Study) {
          map.get("study").add(subtype);
        }
        else if (subtype instanceof Sample) {
          map.get("sample").add(subtype);
        }
        else if (subtype instanceof Experiment) {
          map.get("experiment").add(subtype);
        }
        else if (subtype instanceof SequencerPoolPartition) {
          map.get("run").add(subtype);
        }
//        else if (subtype instanceof Lane) {
//          map.get("run").add(subtype);
//        }
      }

      Element actions = submission.createElementNS(null, "ACTIONS");

      for (String key : map.keySet()) {
        List<Submittable<Document>> submittables = map.get(key);
        if (submittables.size() > 0) {
          Element action = submission.createElementNS(null, "ACTION");
          if (sat != null) {
            if (sat.equals(SubmissionActionType.VALIDATE)) {
              Element validate = submission.createElementNS(null, "VALIDATE");
              validate.setAttribute("schema", key);
              validate.setAttribute("source", sub.getName()+"_"+key+".xml");
              action.appendChild(validate);
            }
            else if (sat.equals(SubmissionActionType.ADD)) {
              Element add = submission.createElementNS(null, "ADD");
              add.setAttribute("schema", key);
              add.setAttribute("source", sub.getName()+"_"+key+".xml");
              action.appendChild(add);
            }
          }
          else {

          }
          actions.appendChild(action);
        }
      }
      s.appendChild(actions);

      if (submission.getElementsByTagName("SUBMISSION_SET").item(0) != null) {
        submission.getElementsByTagName("SUBMISSION_SET").item(0).appendChild(s);
      }
      else {
        Element submissionSet=submission.createElementNS(null,"SUBMISSION_SET");
        submission.appendChild(submissionSet);
        submissionSet.appendChild(s);
      }

    }
  }
}
