package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringBlankOrNull;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tools.jackson.databind.JsonNode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.SampleSheet;
import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.SampleSheetField;
import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.SampleSheetFieldCommonSource;
import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.SampleSheetFieldSource;
import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.SampleSheetParameter;
import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.SampleSheetParameter.ParameterType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.SampleSheetSection;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.SampleSheetService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.SampleSheetGenerator;
import uk.ac.bbsrc.tgac.miso.persistence.SampleSheetDao;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultSampleSheetService extends AbstractSaveService<SampleSheet> implements SampleSheetService {

  private static final String TYPE_LABEL = "sample sheet";
  private static final String FIELD_PARAMETERS = "parameters";
  private static final String FIELD_SECTIONS = "sections";

  @Autowired
  private SampleSheetDao sampleSheetDao;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private DeletionStore deletionStore;

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public List<SampleSheet> list() throws IOException {
    return sampleSheetDao.list();
  }

  @Override
  public SaveDao<SampleSheet> getDao() {
    return sampleSheetDao;
  }

  @Override
  protected void authorizeUpdate(SampleSheet object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  protected void collectValidationErrors(SampleSheet object, SampleSheet beforeChange, List<ValidationError> errors)
      throws IOException {
    if (ValidationUtils.isChanged(SampleSheet::getName, object, beforeChange)
        && sampleSheetDao.getByName(object.getName()) != null) {
      errors.add(ValidationError.forDuplicate(TYPE_LABEL, "name"));
    }
    // Note: parameter and section validation is especially important since it's stored as JSON
    validateParameters(object, errors);
    validateSections(object, errors);
  }

  private void validateParameters(SampleSheet object, List<ValidationError> errors) {
    if (object.getParameters() == null) {
      return;
    }
    Set<String> previousNames = new HashSet<>();
    boolean anyUnnamed = false;
    for (SampleSheetParameter parameter : object.getParameters()) {
      if (isStringBlankOrNull(parameter.getName())) {
        anyUnnamed = true;
        continue;
      }
      if (!previousNames.add(parameter.getName())) {
        errors.add(new ValidationError(FIELD_PARAMETERS, "Duplicate parameter name: " + parameter.getName()));
        continue;
      }
      if (Objects.equals(parameter.getName(), "None") || Arrays.stream(SampleSheetFieldCommonSource.values())
          .anyMatch(source -> Objects.equals(source.name(), parameter.getName()))) {
        errors.add(new ValidationError(FIELD_PARAMETERS, "Invalid parameter name: " + parameter.getName()));
      }
      if (parameter.getType() == null) {
        errors.add(new ValidationError(FIELD_PARAMETERS, "Parameter '%s' requires a type"));
        continue;
      }
      if (parameter.getType() == ParameterType.DROPDOWN) {
        if (parameter.getSource() == null || parameter.getSource().size() < 2) {
          errors.add(new ValidationError(FIELD_PARAMETERS,
              "Dropdown parameter '%s' requires 2 or more options".formatted(parameter.getName())));
        }
        if (parameter.getSource() != null && !parameter.getSource().isEmpty()) {
          JsonNode firstNode = parameter.getSource().get(0);
          Set<String> firstProperties = new HashSet<>(firstNode.propertyNames());
          if (!firstProperties.contains("value")) {
            errors.add(new ValidationError(FIELD_PARAMETERS,
                "Options for dropdown parameter '%s' are missing a 'value' property".formatted(parameter.getName())));
          }
          if (parameter.getSource().valueStream()
              .anyMatch(item -> item.propertyNames().size() != firstProperties.size()
                  || !new HashSet<>(item.propertyNames()).equals(firstProperties))) {
            errors.add(new ValidationError(FIELD_PARAMETERS,
                "Options for dropdown parameter '%s' must all have the same properties"
                    .formatted(parameter.getName())));
          }
        }
      } else {
        if (parameter.getSource() != null) {
          errors.add(new ValidationError(FIELD_PARAMETERS, "Non-dropdown parameters cannot have options"));
        }
      }
      if (parameter.getDefaultValue() != null) {
        switch (parameter.getType()) {
          case TEXT:
            // anything is valid
            break;
          case INT:
            if (!parameter.getDefaultValue().matches("^[+-]?\\d+$")) {
              errors.add(new ValidationError(FIELD_PARAMETERS,
                  "Int parameter '%s' default value is not an integer".formatted(parameter.getName())));
            }
            break;
          case DECIMAL:
            if (!parameter.getDefaultValue().matches("^[+-]?\\d+(\\.\\d+)?$")) {
              errors.add(new ValidationError(FIELD_PARAMETERS,
                  "Decimal parameter '%s' default value is not a decimal number".formatted(parameter.getName())));
            }
            break;
          case DATE:
            try {
              DateTimeFormatter.ISO_LOCAL_DATE.parse(parameter.getDefaultValue());
            } catch (DateTimeParseException e) {
              errors.add(new ValidationError(FIELD_PARAMETERS,
                  "Date parameter '%s' default value should be in format 'yyyy-MM-dd'"
                      .formatted(parameter.getName())));
            }
            break;
          case DROPDOWN:
            if (parameter.getSource().valueStream()
                .noneMatch(x -> Objects.equals(x.get("value").asString(), parameter.getDefaultValue()))) {
              errors.add(new ValidationError(FIELD_PARAMETERS,
                  "Dropdown parameter '%s' default value must match one of its option values"
                      .formatted(parameter.getName())));
            }
            break;
          default:
            errors.add(new ValidationError(FIELD_PARAMETERS,
                "Parameter '%s' has invalid type: %s".formatted(parameter.getName(), parameter.getType())));
            break;
        }
      }
    }
    if (anyUnnamed) {
      errors.add(new ValidationError(FIELD_PARAMETERS, "Each parameter requires a name"));
    }
  }

  private void validateSections(SampleSheet object, List<ValidationError> errors) {
    if (object.getSections() == null || object.getSections().isEmpty()) {
      errors.add(new ValidationError("sections", "A sample sheet must contain at least one section"));
      return;
    }
    Set<String> previousNames = new HashSet<>();
    boolean anyUnnamedSection = false;
    for (SampleSheetSection section : object.getSections()) {
      if (isStringBlankOrNull(section.getName())) {
        anyUnnamedSection = true;
        continue;
      }
      if (!previousNames.add(section.getName())) {
        errors.add(new ValidationError(FIELD_SECTIONS, "Duplicate section name: " + section.getName()));
      }
      if (section.getFormat() == null) {
        errors.add(new ValidationError(FIELD_SECTIONS, "Section '%s' requires a format".formatted(section.getName())));
      }
      if (section.getFields() == null || section.getFields().isEmpty()) {
        errors.add(new ValidationError(FIELD_SECTIONS, "Section '%s' has no fields".formatted(section.getName())));
      } else {
        boolean anyUnnamedField = false;
        for (SampleSheetField field : section.getFields()) {
          if (isStringBlankOrNull(field.getName())) {
            anyUnnamedField = true;
            continue;
          }
          if (field.getSources() == null || field.getSources().isEmpty()) {
            // A field may have no sources to produce an empty column, likely for manual entry
            continue;
          }
          for (SampleSheetFieldSource source : field.getSources()) {
            if (source.getValue() != null) {
              if (source.getSource() != null) {
                errors.add(new ValidationError(FIELD_SECTIONS,
                    "Section '%s' field '%s' source type is invalid when a static value is provided"
                        .formatted(section.getName(), field.getName())));
              }
              if (source.getSourceProperty() != null) {
                errors.add(new ValidationError(FIELD_SECTIONS,
                    "Section '%s' field '%s' source property is invalid when a static value is provided"
                        .formatted(section.getName(), field.getName())));
              }
            } else if (source.getSource() == null) {
              errors.add(new ValidationError(FIELD_SECTIONS, "Section '%s' field '%s' source is missing a source type"
                  .formatted(section.getName(), field.getName())));
            } else {
              SampleSheetFieldCommonSource commonSource = SampleSheetFieldCommonSource.get(source.getSource());
              if (commonSource != null) {
                if (commonSource.getProperties() == null) {
                  if (source.getSourceProperty() != null) {
                    errors.add(new ValidationError(FIELD_SECTIONS,
                        "Section '%s' field '%s' source '%s' should not specify a property".formatted(section.getName(),
                            field.getName(), commonSource.getLabel())));
                  }
                } else {
                  if (!Arrays.stream(commonSource.getProperties()).map(x -> x.name())
                      .anyMatch(x -> Objects.equals(x, source.getSourceProperty()))) {
                    errors.add(new ValidationError(FIELD_SECTIONS,
                        "Section '%s' field '%s' source '%s' specifies invalid property: %s".formatted(
                            section.getName(), field.getName(), commonSource.getLabel(), source.getSourceProperty())));
                  }
                }
              } else {
                // Source should be a parameter
                SampleSheetParameter parameter = object.getParameters().stream()
                    .filter(x -> x.getName().equals(source.getSource())).findFirst().orElse(null);
                if (parameter == null) {
                  errors.add(new ValidationError(FIELD_SECTIONS, "Section '%s' field '%s' includes invalid source: '%s'"
                      .formatted(section.getName(), field.getName(), source.getSource())));
                } else {
                  if (source.getSourceProperty() != null && (parameter.getSource() == null
                      || parameter.getSource().get(0).get(source.getSourceProperty()) == null)) {
                    errors.add(new ValidationError(FIELD_SECTIONS,
                        "Section '%s' field '%s' source '%s' specifies invalid property: %s".formatted(
                            section.getName(),
                            field.getName(), parameter.getName(), source.getSourceProperty())));
                  }
                }
              }
            }
            if (source.getDateFormat() != null
                && !SampleSheetGenerator.VALID_DATE_TIME_FORMATS.contains(source.getDateFormat())) {
              errors.add(new ValidationError(FIELD_SECTIONS, "Section '%s' field '%s' source has an invalid date format"
                  .formatted(section.getName(), field.getName())));
            }
          }
        }
        if (anyUnnamedField) {
          errors.add(new ValidationError(FIELD_SECTIONS,
              "Each field in section '%s' requires a name".formatted(section.getName())));
        }
      }

      if (anyUnnamedSection) {
        errors.add(new ValidationError(FIELD_SECTIONS, "Each section requires a name"));
      }
    }
  }

  @Override
  protected void applyChanges(SampleSheet to, SampleSheet from) throws IOException {
    // There's no changelog or bulk entry, and sample sheets will rarely be modified, so no need to
    // further optimize this
    to.setName(from.getName());
    to.setPlatformType(from.getPlatformType());
    to.setParameters(from.getParameters());
    to.setSections(from.getSections());
  }

  @Override
  public List<SampleSheet> listByPlatform(PlatformType platform) throws IOException {
    return sampleSheetDao.listByPlatform(platform);
  }

}
