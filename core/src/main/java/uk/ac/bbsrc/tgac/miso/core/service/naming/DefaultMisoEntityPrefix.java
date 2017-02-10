package uk.ac.bbsrc.tgac.miso.core.service.naming;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.Submission;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.service.integration.ws.solid.Run;

/**
 * This enum represents the set of prefixes for MISO objects, used in naming schemes
 * 
 * @author Rob Davey
 * @date 29/08/12
 * @since 0.1.7
 */
public enum DefaultMisoEntityPrefix {
  BOX(Box.class), //
  EXP(Experiment.class), //
  LIB(Library.class), //
  LDI(LibraryDilution.class), //
  PRO(Project.class), //
  RUN(Run.class), //
  SAM(Sample.class), //
  SPC(SequencerPartitionContainer.class), //
  STU(Study.class), //
  IPO(Pool.class), //
  SUB(Submission.class);

  /**
   * Field key
   */
  private final Class<?> clazz;

  /**
   * Constructs a DefaultMisoEntityPrefix based on a given key
   * 
   * @param key
   *          of type String
   */
  DefaultMisoEntityPrefix(Class<?> clazz) {
    this.clazz = clazz;
  }

  public Class<?> getTargetClass() {
    return clazz;
  }
}
