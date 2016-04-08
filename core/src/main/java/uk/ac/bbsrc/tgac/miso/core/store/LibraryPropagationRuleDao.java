package uk.ac.bbsrc.tgac.miso.core.store;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryPropagationRule;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;

public interface LibraryPropagationRuleDao {

  List<LibraryPropagationRule> getLibraryPropagationRulesByClass(SampleClass sampleClass);

  LibraryPropagationRule getLibraryPropagationRule(Long id);

}
