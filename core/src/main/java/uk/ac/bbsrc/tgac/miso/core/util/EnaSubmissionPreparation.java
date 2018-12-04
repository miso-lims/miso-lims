package uk.ac.bbsrc.tgac.miso.core.util;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment.RunPartition;
import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Pair;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.Submission;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolableElementView;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.SubmissionActionType;

public class EnaSubmissionPreparation {
  private abstract class ChildSubmissionFile {
    public String fileName() {
      return String.format("%s_%s_%s.xml", submission.getAlias(), name(), DF_TIMESTAMP.format(submission.getSubmissionDate()));
    }

    public abstract void generateDocument(ZipOutputStream output)
        throws ParserConfigurationException, IOException, TransformerConfigurationException, TransformerException,
        TransformerFactoryConfigurationError;

    public abstract boolean isEmpty();

    public abstract String name();
  }

  private final class DilutionXmlSubfile extends XmlSubmissionFromSet<Pair<PoolableElementView, Partition>> {

    @Override
    protected Stream<Pair<PoolableElementView, Partition>> items() {
      return submission.getExperiments().stream().flatMap(experiment -> experiment.getRunPartitions().stream()).flatMap(
          rp -> rp.getPartition().getPool().getPoolDilutions().stream()
              .map(pd -> new Pair<>(pd.getPoolableElementView(), rp.getPartition())));
    }

    @Override
    public String name() {
      return "run";
    }

    @Override
    protected void populate(Element xml, Pair<PoolableElementView, Partition> entry) {
      Run r = entry.getValue().getSequencerPartitionContainer().getLastRun();

      xml.setAttribute("alias",
          "L00" + entry.getValue().getPartitionNumber() + ":" + entry.getKey().getDilutionName() + ":" + r.getAlias());
      xml.setAttribute("run_center", centreName);
      if (r.getHealth() == HealthType.Completed) {
        xml.setAttribute("run_date", DF_TIMESTAMP.format(r.getCompletionDate()));
      }
      xml.setAttribute("center_name", centreName);

      for (Experiment e : submission.getExperiments()) {
        Element experimentRef = xml.getOwnerDocument().createElementNS(null, "EXPERIMENT_REF");
        experimentRef.setAttribute("refname", e.getAlias());
        experimentRef.setAttribute("refcenter", centreName);
        xml.appendChild(experimentRef);
      }

      Element dataBlock = xml.getOwnerDocument().createElementNS(null, "DATA_BLOCK");
      dataBlock.setAttribute("sector", Integer.toString(entry.getValue().getPartitionNumber()));
      if (entry.getValue().getPool().getPoolDilutions().size() > 1) {
        // multiplexed
        dataBlock.setAttribute("member_name", entry.getKey().getDilutionName());
      }

    }

    @Override
    protected String xmlCollectionName() {
      return "RUN_SET";
    }

    @Override
    protected String xmlName() {
      return "RUN";
    }
  }

  private final class ExperimentXmlSubfile extends XmlSubmissionFromSet<Experiment> {

    @Override
    protected Stream<Experiment> items() {
      return submission.getExperiments().stream();
    }

    @Override
    public String name() {
      return "experiment";
    }

    @Override
    protected void populate(Element xml, Experiment experiment) {
      xml.setAttribute("alias", experiment.getAlias());
      xml.setAttribute("center_name", centreName);

      Element xmlTitle = xml.getOwnerDocument().createElementNS(null, "TITLE");
      xmlTitle.setTextContent(experiment.getTitle());
      xml.appendChild(xmlTitle);

      Element xmlStudyRef = xml.getOwnerDocument().createElementNS(null, "STUDY_REF");
      xmlStudyRef.setAttribute("refname", experiment.getStudy().getAlias());
      xmlStudyRef.setAttribute("refcenter", centreName);
      if (!isStringEmptyOrNull(experiment.getStudy().getAccession())) {
        xmlStudyRef.setAttribute("accession", experiment.getStudy().getAccession());
      }
      xml.appendChild(xmlStudyRef);

      Element xmlDesign = xml.getOwnerDocument().createElementNS(null, "DESIGN");
      xml.appendChild(xmlDesign);

      Element designDescription = xml.getOwnerDocument().createElementNS(null, "DESIGN_DESCRIPTION");
      designDescription.setTextContent(experiment.getDescription());
      xmlDesign.appendChild(designDescription);

      Element sampleDescriptor = xml.getOwnerDocument().createElementNS(null, "SAMPLE_DESCRIPTOR");
      sampleDescriptor.setAttribute("refcenter", centreName);
      sampleDescriptor.setAttribute("refname", experiment.getLibrary().getSample().getAlias());
      sampleDescriptor.setAttribute("refcenter", centreName);
      if (!isStringEmptyOrNull(experiment.getLibrary().getSample().getAccession())) {
        sampleDescriptor.setAttribute("accession", experiment.getLibrary().getSample().getAccession());
      }

      experiment.getRunPartitions().stream().map(RunPartition::getPartition).map(Partition::getPool).distinct()
          .filter(pool -> pool.getPoolDilutions().size() > 1).forEach(pool -> {
            // multiplexed pool
            Element xmlPool = xml.getOwnerDocument().createElementNS(null, "POOL");
            sampleDescriptor.appendChild(xmlPool);

            pool.getPoolDilutions().stream().map(PoolDilution::getPoolableElementView).forEach(dilution -> {
              Element xmlMember = xml.getOwnerDocument().createElementNS(null, "MEMBER");
              xmlMember.setAttribute("member_name", dilution.getDilutionName());
              xmlMember.setAttribute("refcenter", centreName);
              xmlMember.setAttribute("refname", dilution.getSampleAlias());
              if (!isStringEmptyOrNull(dilution.getSampleAccession())) {
                sampleDescriptor.setAttribute("accession", dilution.getSampleAccession());
              }
              xmlPool.appendChild(xmlMember);

              Element xmlReadLabel = xml.getOwnerDocument().createElementNS(null, "READ_LABEL");
              if (!dilution.getIndices().isEmpty()) {
                StringBuilder tsb = new StringBuilder();
                StringBuilder vsb = new StringBuilder();
                for (Index index : dilution.getIndices()) {
                  tsb.append(index.getSequence());
                  vsb.append(index.getName());
                }
                xmlReadLabel.setAttribute("read_group_tag", tsb.toString());
                xmlReadLabel.setTextContent(vsb.toString());
              }
              xmlMember.appendChild(xmlReadLabel);
            });
          });
      xmlDesign.appendChild(sampleDescriptor);

      Element libraryDescriptor = xml.getOwnerDocument().createElementNS(null, "LIBRARY_DESCRIPTOR");
      Element libraryName = xml.getOwnerDocument().createElementNS(null, "LIBRARY_NAME");
      if (!isStringEmptyOrNull(experiment.getLibrary().getAlias())) {
        libraryName.setTextContent(experiment.getLibrary().getAlias());
      } else {
        libraryName.setTextContent(experiment.getLibrary().getName());
      }
      libraryDescriptor.appendChild(libraryName);

      Element libraryStrategy = xml.getOwnerDocument().createElementNS(null, "LIBRARY_STRATEGY");
      libraryStrategy.setTextContent(experiment.getLibrary().getLibraryStrategyType().getName());
      libraryDescriptor.appendChild(libraryStrategy);

      Element librarySource = xml.getOwnerDocument().createElementNS(null, "LIBRARY_SOURCE");
      librarySource.setTextContent(experiment.getLibrary().getSample().getSampleType());
      libraryDescriptor.appendChild(librarySource);

      Element librarySelection = xml.getOwnerDocument().createElementNS(null, "LIBRARY_SELECTION");
      librarySelection.setTextContent(experiment.getLibrary().getLibrarySelectionType().getName());
      libraryDescriptor.appendChild(librarySelection);

      Element libraryLayout = xml.getOwnerDocument().createElementNS(null, "LIBRARY_LAYOUT");
      Element layout;
      if (experiment.getLibrary().getPaired()) {
        layout = xml.getOwnerDocument().createElementNS(null, "PAIRED");
        if (experiment.getLibrary().getDnaSize() != null) {
          layout.setAttribute("NOMINAL_LENGTH", experiment.getLibrary().getDnaSize().toString());
        } else {
          layout.setAttribute("NOMINAL_LENGTH", "0");
        }
      } else {
        layout = xml.getOwnerDocument().createElementNS(null, "SINGLE");
      }
      libraryLayout.appendChild(layout);
      libraryDescriptor.appendChild(libraryLayout);

      Element poolingStrategy = xml.getOwnerDocument().createElementNS(null, "POOLING_STRATEGY");
      boolean isMultiplexed = experiment.getRunPartitions().stream().map(RunPartition::getPartition).map(Partition::getPool)
          .map(Pool::getPoolDilutions)
          .mapToInt(Set::size).anyMatch(x -> x > 1);
      poolingStrategy.setTextContent(isMultiplexed ? "multiplexed libraries" : "none");
      libraryDescriptor.appendChild(poolingStrategy);

      xmlDesign.appendChild(libraryDescriptor);

      if (experiment.getInstrumentModel() != null && experiment.getInstrumentModel().getPlatformType().getSraName() != null) {
        Element platform = xml.getOwnerDocument().createElementNS(null, "PLATFORM");
        Element type = xml.getOwnerDocument().createElementNS(null, experiment.getInstrumentModel().getPlatformType().getSraName());
        platform.appendChild(type);

        Element model = xml.getOwnerDocument().createElementNS(null, "INSTRUMENT_MODEL");
        model.setTextContent(experiment.getInstrumentModel().getAlias());
        type.appendChild(model);
        xml.appendChild(platform);
      }
      Element processing = xml.getOwnerDocument().createElementNS(null, "PROCESSING");
      xml.appendChild(processing);
    }

    @Override
    protected String xmlCollectionName() {
      return "EXPERIMENT_SET";
    }

    @Override
    protected String xmlName() {
      return "EXPERIMENT";
    }
  }

  private final class SampleXmlSubfile extends XmlSubmissionFromSet<Sample> {
    @Override
    protected Stream<Sample> items() {
      return submission.getExperiments().stream().map(Experiment::getLibrary).map(Library::getSample).distinct();
    }

    @Override
    public String name() {
      return "sample";
    }

    @Override
    protected void populate(Element xml, Sample sample) {
      xml.setAttribute("alias", sample.getAlias());

      xml.setAttribute("center_name", centreName);

      Element sampleTitle = xml.getOwnerDocument().createElementNS(null, "TITLE");
      sampleTitle.setTextContent(sample.getAlias());
      xml.appendChild(sampleTitle);

      Element sampleName = xml.getOwnerDocument().createElementNS(null, "SAMPLE_NAME");
      Element sampleScientificName = xml.getOwnerDocument().createElementNS(null, "SCIENTIFIC_NAME");
      sampleScientificName.setTextContent(sample.getScientificName());
      sampleName.appendChild(sampleScientificName);

      // 2/11/2011 Antony Colles moved IF !=null statement, to help produce valid submission XML.
      Element sampleTaxonIdentifier = xml.getOwnerDocument().createElementNS(null, "TAXON_ID");
      if (!isStringEmptyOrNull(sample.getTaxonIdentifier())) {
        sampleTaxonIdentifier.setTextContent(sample.getTaxonIdentifier());
      } else {
        sampleTaxonIdentifier.setTextContent("000001");
      }
      sampleName.appendChild(sampleTaxonIdentifier);

      xml.appendChild(sampleName);

      Element sampleDescription = xml.getOwnerDocument().createElementNS(null, "DESCRIPTION");
      sampleDescription.setTextContent(sample.getDescription());
      xml.appendChild(sampleDescription);

    }

    @Override
    protected String xmlCollectionName() {
      return "SAMPLE_SET";
    }

    @Override
    protected String xmlName() {
      return "SAMPLE";
    }
  }

  private final class StudyXmlSubfile extends XmlSubmissionFromSet<Study> {
    @Override
    public Stream<Study> items() {
      return submission.getExperiments().stream().map(Experiment::getStudy).distinct();
    }

    @Override
    public String name() {
      return "study";
    }

    @Override
    protected void populate(Element xml, Study s) {
      xml.setAttribute("alias", s.getAlias());

      Element studyDescriptor = xml.getOwnerDocument().createElementNS(null, "DESCRIPTOR");
      xml.appendChild(studyDescriptor);

      Element studyTitle = xml.getOwnerDocument().createElementNS(null, "STUDY_TITLE");
      studyTitle.setTextContent(s.getAlias());
      studyDescriptor.appendChild(studyTitle);

      Element studyType = xml.getOwnerDocument().createElementNS(null, "STUDY_TYPE");
      studyType.setAttribute("existing_study_type", s.getStudyType().getName());
      studyDescriptor.appendChild(studyType);

      Element centerProjectName = xml.getOwnerDocument().createElementNS(null, "CENTER_PROJECT_NAME");
      centerProjectName.setTextContent(s.getProject().getAlias());
      studyDescriptor.appendChild(centerProjectName);

      Element studyAbstract = xml.getOwnerDocument().createElementNS(null, "STUDY_ABSTRACT");
      studyDescriptor.appendChild(studyAbstract);

      Element studyDescription = xml.getOwnerDocument().createElementNS(null, "STUDY_DESCRIPTION");
      studyDescription.setTextContent(s.getDescription());
      studyDescriptor.appendChild(studyDescription);
    }

    @Override
    protected String xmlCollectionName() {
      return "STUDY_SET";
    }

    @Override
    protected String xmlName() {
      return "STUDY";
    }
  }

  private abstract class XmlSubmissionFromSet<T> extends ChildSubmissionFile {
    @Override
    public void generateDocument(ZipOutputStream output)
        throws ParserConfigurationException, IOException, TransformerConfigurationException, TransformerException,
        TransformerFactoryConfigurationError {
      Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

      Element set = document.createElementNS(null, xmlCollectionName());
      document.appendChild(set);
      items().map(item -> {
        Element itemElement = document.createElement(xmlName());
        populate(itemElement, item);
        return itemElement;
      }).forEach(set::appendChild);


      output.putNextEntry(new ZipEntry(fileName()));
      TransformerFactory.newInstance().newTransformer().transform(new DOMSource(document), new StreamResult(output));
      output.closeEntry();
    }

    @Override
    public boolean isEmpty() {
      return items().count() > 0;
    }

    protected abstract Stream<T> items();

    protected abstract void populate(Element itemElement, T item);

    protected abstract String xmlCollectionName();

    protected abstract String xmlName();

  }

  private static final DateFormat DF_TIMESTAMP = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

  private final String centreName;

  private final Submission submission;

  private final ChildSubmissionFile[] FILES = new ChildSubmissionFile[] { new StudyXmlSubfile(), new SampleXmlSubfile(),
      new DilutionXmlSubfile(),
      new ExperimentXmlSubfile() };

  private final SubmissionActionType submissionAction;

  private final User user;

  public EnaSubmissionPreparation(Submission submission, User user, String centreName, SubmissionActionType submissionAction) {
    super();
    this.submission = submission;
    this.user = user;
    this.centreName = centreName;
    this.submissionAction = submissionAction;
  }

  public byte[] toBytes() {
    try (ByteArrayOutputStream outputBytes = new ByteArrayOutputStream()) {

      ZipOutputStream output = new ZipOutputStream(outputBytes);
      submission.setSubmissionDate(new Date());

      Document submissionDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

      Element s = submissionDocument.createElementNS(null, "SUBMISSION");
      s.setAttribute("alias", submission.getAlias());
      s.setAttribute("submission_date", DF_TIMESTAMP.format(submission.getSubmissionDate()));
      s.setAttribute("submission_comment", submission.getDescription());
      s.setAttribute("center_name", centreName);

      Element title = submissionDocument.createElementNS(null, "TITLE");
      title.setTextContent(submission.getTitle());
      s.appendChild(title);

      Element contacts = submissionDocument.createElementNS(null, "CONTACTS");
      Stream
          .concat(Stream.of(user),
              submission.getExperiments().stream().map(experiment -> experiment.getSecurityProfile().getOwner()).filter(Objects::nonNull))
          .map(User::getFullName).distinct()
          .map(contactName -> {
            Element contact = submissionDocument.createElementNS(null, "CONTACT");
            contact.setAttribute("name", contactName);
            return contact;
          }).forEach(contacts::appendChild);

      s.appendChild(contacts);

      Element actions = submissionDocument.createElementNS(null, "ACTIONS");

      for (ChildSubmissionFile subFile : FILES) {

        subFile.generateDocument(output);
        if (!subFile.isEmpty()) {
          if (submissionAction == SubmissionActionType.ADD || submissionAction == SubmissionActionType.VALIDATE) {
            Element action = submissionDocument.createElementNS(null, "ACTION");
            Element validate = submissionDocument.createElementNS(null, submissionAction.name());
            validate.setAttribute("schema", subFile.name());
            validate.setAttribute("source", subFile.fileName());
            action.appendChild(validate);
            actions.appendChild(action);
          }
        }
      }
      s.appendChild(actions);

      if (submissionDocument.getElementsByTagName("SUBMISSION_SET").item(0) != null) {
        submissionDocument.getElementsByTagName("SUBMISSION_SET").item(0).appendChild(s);
      } else {
        Element submissionSet = submissionDocument.createElementNS(null, "SUBMISSION_SET");
        submissionDocument.appendChild(submissionSet);
        submissionSet.appendChild(s);
      }

      ZipEntry mainFile = new ZipEntry("SUBMISSON.xml");
      output.putNextEntry(mainFile);
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");

      StreamResult result = new StreamResult(output);
      DOMSource source = new DOMSource(submissionDocument);
      transformer.transform(source, result);
      output.closeEntry();
      output.close();
      return outputBytes.toByteArray();

    } catch (ParserConfigurationException | TransformerException | IOException e) {
      throw new RuntimeException(
          "Cannot generate data.");
    }
  }

}
