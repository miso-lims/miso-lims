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

package uk.ac.bbsrc.tgac.miso.spring;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.Activity;
import com.eaglegenomics.simlims.core.Protocol;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.manager.ProtocolManager;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.*;
import org.springframework.util.NumberUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.context.request.WebRequest;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.impl.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.*;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Class that binds all the MISO model datatypes to the Spring form path types
 */
public class LimsBindingInitializer extends org.springframework.web.bind.support.ConfigurableWebBindingInitializer implements WebBindingInitializer {
  protected static final Logger log = LoggerFactory.getLogger(LimsBindingInitializer.class);

  @Autowired
  private ProtocolManager protocolManager;

  @Autowired
  private RequestManager requestManager;

  @Autowired
  private SecurityManager securityManager;

  /**
   * Sets the requestManager of this LimsBindingInitializer object.
   *
   * @param requestManager requestManager.
   */
  public void setRequestManager(RequestManager requestManager) {
    assert (requestManager != null);
    this.requestManager = requestManager;
  }

  /**
   * Sets the protocolManager of this LimsBindingInitializer object.
   *
   * @param protocolManager protocolManager.
   */
  public void setProtocolManager(ProtocolManager protocolManager) {
    assert (protocolManager != null);
    this.protocolManager = protocolManager;
  }

  /**
   * Sets the securityManager of this LimsBindingInitializer object.
   *
   * @param securityManager securityManager.
   */
  public void setSecurityManager(SecurityManager securityManager) {
    assert (securityManager != null);
    this.securityManager = securityManager;
  }

  /**
   * Init this binder, registering all the custom editors to class types
   *
   * @param binder of type WebDataBinder
   * @param req of type WebRequest
   */
  public void initBinder(WebDataBinder binder, WebRequest req) {
    binder.registerCustomEditor(Long.class, new CustomNumberEditor(Long.class, false));

    binder.registerCustomEditor(Boolean.class, new CustomBooleanEditor(CustomBooleanEditor.VALUE_TRUE, CustomBooleanEditor.VALUE_FALSE, true));

    binder.registerCustomEditor(Date.class, new CustomDateEditor(
            new SimpleDateFormat("dd/MM/yyyy"), true));

    binder.registerCustomEditor(InetAddress.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String element) throws IllegalArgumentException {
        setValue(resolveInetAddress(element));
      }
    });


    binder.registerCustomEditor(Activity.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String element) throws IllegalArgumentException {
        setValue(resolveActivity(element));
      }
    });

/*
    binder.registerCustomEditor(Request.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String element) throws IllegalArgumentException {
        setValue(resolveRequest(element));
      }
    });
    */
    binder.registerCustomEditor(Protocol.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String element) throws IllegalArgumentException {
        setValue(resolveProtocol(element));
      }
    });

    binder.registerCustomEditor(Group.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String element) throws IllegalArgumentException {
        setValue(resolveGroup(element));
      }
    });

    binder.registerCustomEditor(Set.class, "users", new CustomCollectionEditor(Set.class) {
      @Override
      protected Object convertElement(Object element) {
        return resolveUser(element);
      }
    });

    binder.registerCustomEditor(Set.class, "groups", new CustomCollectionEditor(Set.class) {
      @Override
      protected Object convertElement(Object element) {
        return resolveGroup(element);
      }
    });
    
//TGAC Classes
    binder.registerCustomEditor(com.eaglegenomics.simlims.core.User.class, new PropertyEditorSupport() {
     @Override
      public void setAsText(String element) throws IllegalArgumentException {
        setValue(resolveUser(element));
      }
    });

    binder.registerCustomEditor(com.eaglegenomics.simlims.core.User.class, "securityProfile.owner", new PropertyEditorSupport() {
     @Override
      public void setAsText(String element) throws IllegalArgumentException {
        setValue(resolveUser(element));
      }
    });

    binder.registerCustomEditor(Project.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String element) throws IllegalArgumentException {
        setValue(resolveProject(element));
      }
    });

    /*
    binder.registerCustomEditor(ProjectOverview.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String element) throws IllegalArgumentException {
        log.info("Overviews set found... resolving...");
        setValue(resolveProjectOverview(element));
      }
    });

    binder.registerCustomEditor(Set.class, "overviews", new CustomCollectionEditor(Set.class) {
      @Override
      public void setAsText(String element) throws IllegalArgumentException {
        setValue(resolveProjectOverview(element));
      }
    });
    */

    binder.registerCustomEditor(Set.class, "studies", new CustomCollectionEditor(Set.class) {
      @Override
      protected Object convertElement(Object element) {
        return resolveStudy(element);
      }
    });

    binder.registerCustomEditor(Study.class, "study", new PropertyEditorSupport() {
      @Override
      public void setAsText(String element) throws IllegalArgumentException {
        setValue(resolveStudy(element));
      }
    });

    binder.registerCustomEditor(Set.class, "experiments", new CustomCollectionEditor(Set.class) {
      @Override
      protected Object convertElement(Object element) {
        return resolveExperiment(element);
      }
    });

    binder.registerCustomEditor(Experiment.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String element) throws IllegalArgumentException {
        setValue(resolveExperiment(element));
      }
    });

    binder.registerCustomEditor(Pool.class, "sequencerPartitionContainers.partitions.pool", new PropertyEditorSupport() {
      @Override
      public void setAsText(String element) throws IllegalArgumentException {
        setValue(resolvePool(element));
      }
    });

    /*
    binder.registerCustomEditor(Pool.class, "flowcells.lanes.pool", new PropertyEditorSupport() {
      @Override
      public void setAsText(String element) throws IllegalArgumentException {
        setValue(resolvePool(element));
      }
    });
    */

    binder.registerCustomEditor(Set.class, "samples", new CustomCollectionEditor(Set.class) {
      @Override
      protected Object convertElement(Object element) {
        return resolveSample(element);
      }
    });

    binder.registerCustomEditor(Sample.class, "sample", new PropertyEditorSupport() {
      @Override
      public void setAsText(String element) throws IllegalArgumentException {
        setValue(resolveSample(element));
      }
    });

    binder.registerCustomEditor(Set.class, "runs", new CustomCollectionEditor(Set.class) {
      @Override
      protected Object convertElement(Object element) {
        return resolveRun(element);
      }
    });

    binder.registerCustomEditor(Run.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String element) throws IllegalArgumentException {
        setValue(resolveRun(element));
      }
    });

    binder.registerCustomEditor(Pool.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String element) throws IllegalArgumentException {
        setValue(resolvePool(element));
      }
    });

    binder.registerCustomEditor(PlatformType.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String element) throws IllegalArgumentException {
        setValue(resolvePlatformType(element));
      }
    });

    binder.registerCustomEditor(Set.class, "platformTypes", new CustomCollectionEditor(Set.class) {
      @Override
      protected Object convertElement(Object element) {
        return resolvePlatformType(element);
      }
    });

    binder.registerCustomEditor(List.class, "partitions", new CustomCollectionEditor(List.class) {
      @Override
      protected Object convertElement(Object element) {
        return resolvePartition(element);
      }
    });

    binder.registerCustomEditor(SequencerPartitionContainer.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String element) throws IllegalArgumentException {
        setValue(resolveSequencerPartitionContainer(element));
      }
    });

    binder.registerCustomEditor(List.class, "sequencerPartitionContainers", new CustomCollectionEditor(List.class) {
      @Override
      protected Object convertElement(Object element) {
        return resolveSequencerPartitionContainer(element);
      }
    });

    binder.registerCustomEditor(Library.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String element) throws IllegalArgumentException {
        setValue(resolveLibrary(element));
      }
    });

    binder.registerCustomEditor(Set.class, "libraries", new CustomCollectionEditor(Set.class) {
      @Override
      protected Object convertElement(Object element) {
        return resolveLibrary(element);
      }
    });

    binder.registerCustomEditor(Dilution.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String element) throws IllegalArgumentException {
        log.info("Binding dilution " + element);
        setValue(resolveDilution(element));
      }
    });

    binder.registerCustomEditor(Set.class, "dilutions", new CustomCollectionEditor(Set.class) {
      @Override
      protected Object convertElement(Object element) {
        log.info("Binding dilution set " + element.toString());
        return resolveDilution(element);
      }
    });

    binder.registerCustomEditor(LibraryDilution.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String element) throws IllegalArgumentException {
        log.info("Binding library dilution " + element);
        setValue(resolveLibraryDilution(element));
      }
    });

    binder.registerCustomEditor(Set.class, "libraryDilutions", new CustomCollectionEditor(Set.class) {
      @Override
      protected Object convertElement(Object element) {
        log.info("Binding library dilution set " + element.toString());
        return resolveLibraryDilution(element);
      }
    });

    binder.registerCustomEditor(LibraryType.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String element) throws IllegalArgumentException {
        setValue(resolveLibraryType(element));
      }
    });

    binder.registerCustomEditor(Set.class, "libraryTypes", new CustomCollectionEditor(Set.class) {
      @Override
      protected Object convertElement(Object element) {
        return resolveLibraryType(element);
      }
    });

    binder.registerCustomEditor(LibrarySelectionType.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String element) throws IllegalArgumentException {
        setValue(resolveLibrarySelectionType(element));
      }
    });

    binder.registerCustomEditor(Set.class, "librarySelectionTypes", new CustomCollectionEditor(Set.class) {
      @Override
      protected Object convertElement(Object element) {
        return resolveLibrarySelectionType(element);
      }
    });

    binder.registerCustomEditor(LibraryStrategyType.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String element) throws IllegalArgumentException {
        setValue(resolveLibraryStrategyType(element));
      }
    });

    binder.registerCustomEditor(Set.class, "libraryStrategyTypes", new CustomCollectionEditor(Set.class) {
      @Override
      protected Object convertElement(Object element) {
        return resolveLibraryStrategyType(element);
      }
    });

    binder.registerCustomEditor(TagBarcode.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String element) throws IllegalArgumentException {
        setValue(resolveTagBarcode(element));
      }
    });

    binder.registerCustomEditor(HashMap.class, "tagBarcodes", new CustomMapEditor(HashMap.class) {


      @Override
      protected Object convertValue(Object element) {
        return resolveTagBarcode(element);
      }
    });

    binder.registerCustomEditor(emPCRDilution.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String element) throws IllegalArgumentException {
        setValue(resolveEmPcrDilution(element));
      }
    });

    binder.registerCustomEditor(Set.class, "pcrDilutions", new CustomCollectionEditor(Set.class) {
      @Override
      protected Object convertElement(Object element) {
        return resolveEmPcrDilution(element);
      }
    });

    binder.registerCustomEditor(Platform.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String element) throws IllegalArgumentException {
        setValue(resolvePlatform(element));
      }
    });

    binder.registerCustomEditor(SequencerReference.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String element) throws IllegalArgumentException {
        setValue(resolveSequencerReference(element));
      }
    });

    binder.registerCustomEditor(Status.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String element) throws IllegalArgumentException {
        setValue(resolveStatus(element));
      }
    });

    binder.registerCustomEditor(SecurityProfile.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String element) throws IllegalArgumentException {
        setValue(resolveSecurityProfile(element));
      }
    });

    binder.registerCustomEditor(Submittable.class, "submissionElement", new PropertyEditorSupport() {
      @Override
      public void setAsText(String element) throws IllegalArgumentException {
        setValue(resolveSubmittable(element));
      }
    });

    binder.registerCustomEditor(Set.class, "submissionElements", new CustomCollectionEditor(Set.class) {
      @Override
      protected Object convertElement(Object element) {
        return resolveSubmittable(element);
      }
    });

    binder.registerCustomEditor(Kit.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String element) throws IllegalArgumentException {
        setValue(resolveKit(element));
      }
    });

    binder.registerCustomEditor(Set.class, "kits", new CustomCollectionEditor(Set.class) {
      @Override
      protected Object convertElement(Object element) {
        return resolveKit(element);
      }
    });

    binder.registerCustomEditor(KitDescriptor.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String element) throws IllegalArgumentException {
        setValue(resolveKitDescriptor(element));
      }
    });

    binder.registerCustomEditor(Set.class, "kitDescriptors", new CustomCollectionEditor(Set.class) {
      @Override
      protected Object convertElement(Object element) {
        return resolveKitDescriptor(element);
      }
    });

    binder.registerCustomEditor(Set.class, "poolableElements", new CustomCollectionEditor(Set.class) {
      @Override
      protected Object convertElement(Object element) {
        return resolvePoolable(element);
      }
    });
  }

  /**
   * Resolve an InetAddress object from a String
   *
   * @param element of type Object
   * @return InetAddress
   * @throws IllegalArgumentException when
   */
  private InetAddress resolveInetAddress(Object element) throws IllegalArgumentException {
    InetAddress i = null;
    try {
      if (element instanceof String) i = InetAddress.getByName((String)element);
      return i;
    }
    catch (UnknownHostException e) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to resolve InetAddress " + element, e);
      }
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Resolve a Group object from an ID String
   *
   * @param element of type Object
   * @return Group
   * @throws IllegalArgumentException when
   */
  private Group resolveGroup(Object element) throws IllegalArgumentException {
    Long id = null;
    if (element instanceof String)
      id = NumberUtils.parseNumber((String) element, Long.class).longValue();
    try {
      return id != null ? securityManager.getGroupById(id) : null;
    }
    catch (IOException e) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to resolve group " + element, e);
      }
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Resolve a User object from an ID String
   *
   * @param element of type Object
   * @return User
   * @throws IllegalArgumentException when
   */
  private User resolveUser(Object element) throws IllegalArgumentException {
    Long id = null;
    if (element instanceof String && !"".equals(element))
      id = NumberUtils.parseNumber((String) element, Long.class).longValue();
    try {
      return id != null ? securityManager.getUserById(id) : null;
    }
    catch (IOException e) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to resolve user " + element, e);
      }
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Resolve a SecurityProfile object from an ID String
   *
   * @param element of type Object
   * @return SecurityProfile
   * @throws IllegalArgumentException when
   */
  private SecurityProfile resolveSecurityProfile(Object element) throws IllegalArgumentException {
    Long id = null;
    if (element instanceof String)
      id = NumberUtils.parseNumber((String) element, Long.class).longValue();
    try {
      return id != null ? securityManager.getSecurityProfileById(id) : null;
    }
    catch (IOException e) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to resolve SecurityProfile " + element, e);
      }
      throw new IllegalArgumentException(e);
    }
  }
/*
  private Request resolveRequest(Object element) throws IllegalArgumentException {
    Long id = null;
    if (element instanceof String)
      id = NumberUtils.parseNumber((String) element, Long.class).longValue();
    try {
      return id != null ? requestManager.getRequestById(id) : null;
    }
    catch (IOException e) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to resolve request " + element, e);
      }
      throw new IllegalArgumentException(e);
    }
  }
*/
  /**
   * Resolve a Project object from an ID or {@link uk.ac.bbsrc.tgac.miso.core.data.Project.PREFIX} String
   *
   * @param element of type Object
   * @return Project
   * @throws IllegalArgumentException when
   */
  private Project resolveProject(Object element) throws IllegalArgumentException {
    Long id = null;
    if (element instanceof String) {
      if (((String)element).startsWith(Project.PREFIX)) {
        String ident = ((String)element).substring(Project.PREFIX.length());
        id = NumberUtils.parseNumber(ident, Long.class).longValue();
      }
      else {
        id = NumberUtils.parseNumber((String) element, Long.class).longValue();
      }
    }
    try {
      return id != null ? requestManager.getProjectById(id) : null;
    }
    catch (IOException e) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to resolve project " + element, e);
      }
      throw new IllegalArgumentException(e);
    }
  }

  private ProjectOverview resolveProjectOverview(Object element) throws IllegalArgumentException {
    Long id = null;
    if (element instanceof String)
      id = NumberUtils.parseNumber((String) element, Long.class).longValue();
    try {
      log.info("Resolved project overview");
      return id != null ? requestManager.getProjectOverviewById(id) : null;
    }
    catch (IOException e) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to resolve project overview" + element, e);
      }
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Resolve an Activity object from an ID String
   *
   * @param element of type Object
   * @return Activity
   * @throws IllegalArgumentException when
   */
  private Activity resolveActivity(Object element) throws IllegalArgumentException {
    String id = null;
    if (element instanceof String)
      id = element.toString();
    try {
      return id != null ? protocolManager.getActivity(id) : null;
    }
    catch (IOException e) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to resolve activity " + element, e);
      }
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Resolve a Protocol object from an ID String
   *
   * @param element of type Object
   * @return Protocol
   * @throws IllegalArgumentException when
   */
  private Protocol resolveProtocol(Object element) throws IllegalArgumentException {
    String id = null;
    if (element instanceof String)
      id = element.toString();
    try {
      return id != null ? protocolManager.getProtocol(id) : null;
    }
    catch (IOException e) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to resolve protocol " + element, e);
      }
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Resolve a Study object from an ID or {@link uk.ac.bbsrc.tgac.miso.core.data.Study.PREFIX} String
   *
   * @param element of type Object
   * @return Study
   * @throws IllegalArgumentException when
   */
  private Study resolveStudy(Object element) throws IllegalArgumentException {
    Long id = null;
    if (element instanceof String) {
      if (((String)element).startsWith(Study.PREFIX)) {
        String ident = ((String)element).substring(Study.PREFIX.length());
        id = NumberUtils.parseNumber(ident, Long.class).longValue();
      }
      else {
        id = NumberUtils.parseNumber((String) element, Long.class).longValue();
      }
    }
    try {
      return id != null ? requestManager.getStudyById(id) : null;
    }
    catch (IOException e) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to resolve study " + element, e);
      }
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Resolve an Experiment object from an ID String
   *
   * @param element of type Object
   * @return Experiment
   * @throws IllegalArgumentException when
   */
  private Experiment resolveExperiment(Object element) throws IllegalArgumentException {
    Long id = null;
    log.info("Resolving experiment: " + element.toString());
    if (element instanceof String) {
//      if (((String)element).startsWith(Experiment.PREFIX)) {
//        String ident = ((String)element).substring(Experiment.PREFIX.length());
//        id = NumberUtils.parseNumber(ident, Long.class).longValue();
//      }
//      else {
        id = NumberUtils.parseNumber((String) element, Long.class).longValue();
//      }
    }
    try {
      return id != null ? requestManager.getExperimentById(id) : null;
    }
    catch (IOException e) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to resolve experiment " + element, e);
      }
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Resolve a Sample object from an ID or {@link uk.ac.bbsrc.tgac.miso.core.data.Sample.PREFIX} String
   *
   * @param element of type Object
   * @return Sample
   * @throws IllegalArgumentException when
   */
  private Sample resolveSample(Object element) throws IllegalArgumentException {
    Long id = null;
    if (element instanceof String) {
      if (((String)element).startsWith(Sample.PREFIX)) {
        String ident = ((String)element).substring(Sample.PREFIX.length());
        id = NumberUtils.parseNumber(ident, Long.class).longValue();
      }
      else {
        id = NumberUtils.parseNumber((String) element, Long.class).longValue();
      }
    }
    try {
      return id != null ? requestManager.getSampleById(id) : null;
    }
    catch (IOException e) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to resolve sample " + element, e);
      }
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Resolve a Run object from an ID String
   *
   * @param element of type Object
   * @return Run
   * @throws IllegalArgumentException when
   */
  private Run resolveRun(Object element) throws IllegalArgumentException {
    Long id = null;
    if (element instanceof String)
      id = NumberUtils.parseNumber((String) element, Long.class).longValue();
    try {
      return id != null ? requestManager.getRunById(id) : null;
    }
    catch (IOException e) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to resolve run " + element, e);
      }
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Resolve a PlatformType enum from a given key String
   *
   * @param element of type Object
   * @return PlatformType
   * @throws IllegalArgumentException when
   */
  private PlatformType resolvePlatformType(Object element) throws IllegalArgumentException {
    if (element instanceof String) {
      return PlatformType.get((String)element);
    }
    else {
      if (log.isDebugEnabled()) {
        log.debug("Failed to resolve PlatformType " + element);
      }
      throw new IllegalArgumentException("Cannot resolve PlatformType from key: " + element + ". Accepted keys are: [" + PlatformType.getKeys() + "]");      
    }
  }

  /**
   * Resolve a Pool object from an ID or {@link uk.ac.bbsrc.tgac.miso.core.data.Pool} PREFIX String
   *
   * @param element of type Object
   * @return Pool
   * @throws IllegalArgumentException when
   */
  private Pool resolvePool(Object element) throws IllegalArgumentException {
    Long id = null;
    if (element instanceof String)
      id = NumberUtils.parseNumber((String) element, Long.class).longValue();
    try {
      return id != null ? requestManager.getPoolById(id) : null;
    }
    catch (IOException e) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to resolve pool " + element, e);
      }
      throw new IllegalArgumentException(e);
    }
  }


  /**
   * Resolve a Partition object from an ID String
   *
   * @param element of type Object
   * @return Partition
   * @throws IllegalArgumentException when
   */
  private SequencerPoolPartition resolvePartition(Object element) throws IllegalArgumentException {
    Long id = null;
    if (element instanceof String) {
      String s = (String)element;
      if (!"".equals(s)) {
        id = NumberUtils.parseNumber((String) element, Long.class).longValue();
      }
    }
    try {
      return id != null ? requestManager.getSequencerPoolPartitionById(id) : null;
    }
    catch (IOException e) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to resolve Partition " + element, e);
      }
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Resolve a SequencerPartitionContainer object from an ID String
   *
   * @param element of type Object
   * @return SequencerPartitionContainer
   * @throws IllegalArgumentException when
   */
  private SequencerPartitionContainer resolveSequencerPartitionContainer(Object element) throws IllegalArgumentException {
    Long id = null;
    if (element instanceof String) {
      String s = (String)element;
      if (!"".equals(s)) {
        id = NumberUtils.parseNumber((String) element, Long.class).longValue();
      }
    }
    try {
      return id != null ? requestManager.getSequencerPartitionContainerById(id) : null;
    }
    catch (IOException e) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to resolve SequencerPartitionContainer " + element, e);
      }
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Resolve a Library object from an ID String
   *
   * @param element of type Object
   * @return Library
   * @throws IllegalArgumentException when
   */
  private Library resolveLibrary(Object element) throws IllegalArgumentException {
    Long id = null;
    if (element instanceof String)
      id = NumberUtils.parseNumber((String) element, Long.class).longValue();
    try {
      return id != null ? requestManager.getLibraryById(id) : null;
    }
    catch (IOException e) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to resolve library " + element, e);
      }
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Resolve a Dilution object from an ID String
   *
   * @param element of type Object
   * @return Dilution
   * @throws IllegalArgumentException when
   */
  private Dilution resolveDilution(Object element) throws IllegalArgumentException {
    Long id = null;
    if (element instanceof String) {
      String prefix = ((String)element).substring(0, 3);
      String ident = ((String)element).substring(3);
      id = NumberUtils.parseNumber(ident, Long.class).longValue();

      try {
        if ("LDI".equals(prefix)) {
          log.debug(prefix + ":" + ident + " -> Dilution");
          return id != null ? requestManager.getLibraryDilutionById(id) : null;
        }
        else if ("EDI".equals(prefix)) {
          log.debug(prefix + ":" + ident + " -> Dilution");
          return id != null ? requestManager.getEmPcrDilutionById(id) : null;
        }
        else {
          log.debug("Failed to resolve dilution with identifier: " + prefix+ident);
        }
      }
      catch (IOException e) {
        if (log.isDebugEnabled()) {
          log.debug("Failed to resolve dilution " + element, e);
        }
        throw new IllegalArgumentException(e);
      }
    }
    return null;
  }

  /**
   * Resolve a LibraryDilution object from an ID String
   *
   * @param element of type Object
   * @return LibraryDilution
   * @throws IllegalArgumentException when
   */
  private LibraryDilution resolveLibraryDilution(Object element) throws IllegalArgumentException {
    Long id = null;
    if (element instanceof String)
      id = NumberUtils.parseNumber((String) element, Long.class).longValue();
    try {
      return id != null ? requestManager.getLibraryDilutionById(id) : null;
    }
    catch (IOException e) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to resolve dilution " + element, e);
      }
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Resolve an emPCRDilution object from an ID String
   *
   * @param element of type Object
   * @return emPCRDilution
   * @throws IllegalArgumentException when
   */
  private emPCRDilution resolveEmPcrDilution(Object element) throws IllegalArgumentException {
    Long id = null;
    if (element instanceof String)
      id = NumberUtils.parseNumber((String) element, Long.class).longValue();
    try {
      return id != null ? requestManager.getEmPcrDilutionById(id) : null;
    }
    catch (IOException e) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to resolve dilution " + element, e);
      }
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Resolve a Platform object from an ID String
   *
   * @param element of type Object
   * @return Platform
   * @throws IllegalArgumentException when
   */
  private Platform resolvePlatform(Object element) throws IllegalArgumentException {
    Long id = null;
    if (element instanceof String)
      id = NumberUtils.parseNumber((String) element, Long.class).longValue();
    try {
      return id != null ? requestManager.getPlatformById(id) : null;
    }
    catch (IOException e) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to resolve platform " + element, e);
      }
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Resolve a SequencerReference object from an ID String
   *
   * @param element of type Object
   * @return SequencerReference
   * @throws IllegalArgumentException when
   */
  private SequencerReference resolveSequencerReference(Object element) throws IllegalArgumentException {
    Long id = null;
    if (element instanceof String)
      id = NumberUtils.parseNumber((String) element, Long.class).longValue();
    try {
      return id != null ? requestManager.getSequencerReferenceById(id) : null;
    }
    catch (IOException e) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to resolve SequencerReference " + element, e);
      }
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Resolve a Status object from an ID String
   *
   * @param element of type Object
   * @return Status
   * @throws IllegalArgumentException when
   */
  private Status resolveStatus(Object element) throws IllegalArgumentException {
    Long id = null;
    if (element instanceof String)
      id = NumberUtils.parseNumber((String) element, Long.class).longValue();
    try {
      return id != null ? requestManager.getStatusById(id) : null;
    }
    catch (IOException e) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to resolve status " + element, e);
      }
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Resolve a Submittable object from a PREFIX
   *
   * @param element of type Object
   * @return Submittable
   * @throws IllegalArgumentException when
   */
  private Submittable resolveSubmittable(Object element) throws IllegalArgumentException {
    Long id = null;
    if (element instanceof String) {
      String prefix = ((String)element).substring(0, 3);
      String ident = ((String)element).substring(3);
      id = NumberUtils.parseNumber(ident, Long.class).longValue();

      try {
        if (Study.PREFIX.equals(prefix)) {
          log.info(prefix + ":" + ident + " -> Study");
          return id != null ? requestManager.getStudyById(id) : null;
        }
        else if (Sample.PREFIX.equals(prefix)) {
          log.info(prefix + ":" + ident + " -> Sample");
          return id != null ? requestManager.getSampleById(id) : null;
        }
        else if (Experiment.PREFIX.equals(prefix)) {
          log.info(prefix + ":" + ident + " -> Experiment");
          return id != null ? requestManager.getExperimentById(id) : null;
        }
        else if ("PAR".equals(prefix)) {
          log.info(prefix + ":" + ident + " -> Partition");
          return id != null ? requestManager.getSequencerPoolPartitionById(id) : null;
        }
        else {
          log.debug("Failed to resolve submittable element with identifier: " + prefix+ident);
        }
      }
      catch (IOException e) {
        if (log.isDebugEnabled()) {
          log.debug("Failed to resolve submittable element " + element, e);
        }
        throw new IllegalArgumentException(e);
      }
    }
    return null;
  }

  /**
   * Resolve a Kit object from an ID String
   *
   * @param element of type Object
   * @return Kit
   * @throws IllegalArgumentException when
   */
  private Kit resolveKit(Object element) throws IllegalArgumentException {
    Long id = null;
    if (element instanceof String && !"".equals(element))
      id = NumberUtils.parseNumber((String) element, Long.class).longValue();
    try {
      return id != null ? requestManager.getKitById(id) : null;
    }
    catch (IOException e) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to resolve kit " + element, e);
      }
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Resolve a KitDescriptor object from an ID String
   *
   * @param element of type Object
   * @return KitDescriptor
   * @throws IllegalArgumentException when
   */
  private KitDescriptor resolveKitDescriptor(Object element) throws IllegalArgumentException {
    Long id = null;
    if (element instanceof String)
      id = NumberUtils.parseNumber((String) element, Long.class).longValue();
    try {
      return id != null ? requestManager.getKitDescriptorById(id) : null;
    }
    catch (IOException e) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to resolve kit descriptor " + element, e);
      }
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Resolve a LibraryType object from an ID String
   *
   * @param element of type Object
   * @return LibraryType
   * @throws IllegalArgumentException when
   */
  private LibraryType resolveLibraryType(Object element) throws IllegalArgumentException {
    Long id = null;
    if (element instanceof String)
      id = NumberUtils.parseNumber((String) element, Long.class).longValue();
    try {
      return id != null ? requestManager.getLibraryTypeById(id) : null;
    }
    catch (IOException e) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to resolve LibraryType " + element, e);
      }
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Resolve a LibrarySelectionType object from an ID String
   *
   * @param element of type Object
   * @return LibrarySelectionType
   * @throws IllegalArgumentException when
   */
  private LibrarySelectionType resolveLibrarySelectionType(Object element) throws IllegalArgumentException {
    Long id = null;
    if (element instanceof String)
      id = NumberUtils.parseNumber((String) element, Long.class).longValue();
    try {
      return id != null ? requestManager.getLibrarySelectionTypeById(id) : null;
    }
    catch (IOException e) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to resolve LibrarySelectionType " + element, e);
      }
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Resolve a LibraryStrategyType object from an ID String
   *
   * @param element of type Object
   * @return LibraryStrategyType
   * @throws IllegalArgumentException when
   */
  private LibraryStrategyType resolveLibraryStrategyType(Object element) throws IllegalArgumentException {
    Long id = null;
    if (element instanceof String)
      id = NumberUtils.parseNumber((String) element, Long.class).longValue();
    try {
      return id != null ? requestManager.getLibraryStrategyTypeById(id) : null;
    }
    catch (IOException e) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to resolve LibrarySelectionType " + element, e);
      }
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Resolve a TagBarcode object from an ID String
   *
   * @param element of type Object
   * @return TagBarcode
   * @throws IllegalArgumentException when
   */
  private TagBarcode resolveTagBarcode(Object element) throws IllegalArgumentException {
    Long id = null;
    if (element instanceof String && !"".equals(element))
      id = NumberUtils.parseNumber((String) element, Long.class).longValue();
    try {
      return id != null ? requestManager.getTagBarcodeById(id) : null;
    }
    catch (IOException e) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to resolve TagBarcode " + element, e);
      }
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Resolve a Poolable object from a PREFIX
   *
   * @param element of type Object
   * @return Poolable
   * @throws IllegalArgumentException when
   */
  private Poolable resolvePoolable(Object element) throws IllegalArgumentException {
    Long id = null;
    if (element instanceof String) {
      String prefix = ((String)element).substring(0, 3);
      String ident = ((String)element).substring(3);
      id = NumberUtils.parseNumber(ident, Long.class).longValue();

      try {
        if ("LDI".equals(prefix)) {
          log.info(prefix + ":" + ident + " -> LibraryDilution");
          return id != null ? requestManager.getLibraryDilutionById(id) : null;
        }
        else if ("EDI".equals(prefix)) {
          log.info(prefix + ":" + ident + " -> EmPCRDilution");
          return id != null ? requestManager.getEmPcrDilutionById(id) : null;
        }
        else if ("PLA".equals(prefix)) {
          log.info(prefix + ":" + ident + " -> Plate");
          return id != null ? requestManager.getPlateById(id) : null;
        }
        else {
          log.debug("Failed to resolve poolable element with identifier: " + prefix+ident);
        }
      }
      catch (IOException e) {
        if (log.isDebugEnabled()) {
          log.debug("Failed to resolve poolable element " + element, e);
        }
        throw new IllegalArgumentException(e);
      }
    }
    return null;
  }
}