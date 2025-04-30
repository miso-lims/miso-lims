if (typeof FormTarget === "undefined") {
  FormTarget = {};
}
FormTarget.sample = (function ($) {
  /*
   * Expected config {
   *   detailedSample: boolean,
   *   projects: array,
   *   sops: array
   * }
   */

  return {
    getUserManualUrl: function () {
      return Urls.external.userManual("samples");
    },
    getSaveUrl: function (sample) {
      if (sample.id) {
        return Urls.rest.samples.update(sample.id);
      } else {
        throw new Error("Page not intended for new sample creation");
      }
    },
    getSaveMethod: function (sample) {
      return sample.id ? "PUT" : "POST";
    },
    getEditUrl: function (sample) {
      return Urls.ui.samples.edit(sample.id);
    },
    getSections: function (config, object) {
      return [
        {
          title: "Sample Information",
          fields: [
            {
              title: "Sample ID",
              data: "id",
              type: "read-only",
              getDisplayValue: function (sample) {
                return sample.id;
              },
            },
            {
              title: "Project",
              data: "projectId",
              type: "dropdown",
              required: true,
              source: config.projects.filter(function (project) {
                return project.status === "Active" || project.id === object.projectId;
              }),
              getItemLabel: function (item) {
                return Constants.isDetailedSample ? item.code : item.name;
              },
              getItemValue: Utils.array.getId,
              sortSource: Utils.sorting.standardSort(Constants.isDetailedSample ? "code" : "id"),
              onChange: function (newValue, form) {
                if (Constants.isDetailedSample) {
                  form.updateField("subprojectId", {
                    source: Constants.subprojects.filter(function (subproject) {
                      return subproject.parentProjectId === parseInt(newValue);
                    }),
                  });
                }
              },
            },
            {
              title: "Name",
              data: "name",
              type: "read-only",
              getDisplayValue: function (sample) {
                return sample.name;
              },
            },
            {
              title: "Alias",
              data: "alias",
              type: "text",
              required: true,
              maxLength: 100,
              note:
                config.detailedSample && object.nonStandardAlias
                  ? "Double-check this alias -- it will be saved even if it is duplicated or does not follow the naming standard!"
                  : null,
            },
            {
              title: "Description",
              data: "description",
              type: "text",
              maxLength: 255,
            },
            {
              title: "Matrix Barcode",
              data: "identificationBarcode",
              type: "text",
              maxLength: 255,
            },
            FormUtils.makeRequisitionField(object),
            FormUtils.makeEffectiveRequisitionField(object),
            {
              title: "Assays",
              data: "requisitionAssayIds",
              type: "special",
              makeControls: function () {
                return makeAssayControls(object);
              },
            },
            {
              title: "Scientific Name",
              data: "scientificNameId",
              type: "dropdown",
              required: true,
              source: Constants.scientificNames,
              sortSource: Utils.sorting.standardSort("alias"),
              getItemLabel: Utils.array.getAlias,
              getItemValue: Utils.array.getId,
            },
            {
              title: "Accession",
              data: "accession",
              type: "read-only",
              getLink: function (sample) {
                return Urls.external.enaAccession(sample.accession);
              },
              include: object.accession,
            },
            {
              title: "Sample Type",
              data: "sampleType",
              type: "dropdown",
              source: Constants.sampleTypes.sort(),
              getItemLabel: function (item) {
                return item;
              },
              getItemValue: function (item) {
                return item;
              },
              required: true,
            },
          ]
            .concat(FormUtils.makeDetailedQcStatusFields(object))
            .concat([
              {
                title: "Discarded",
                data: "discarded",
                type: "checkbox",
                onChange: function (newValue, form) {
                  form.updateField("volume", {
                    disabled: newValue,
                  });
                },
              },
              {
                title: "Initial Volume",
                data: "initialVolume",
                type: "decimal",
                precision: 16,
                scale: 10,
                min: 0,
                onChange: function (newValue, form) {
                  form.updateField("volume", {
                    required: newValue,
                  });
                },
              },
              {
                title: "Volume",
                data: "volume",
                type: "decimal",
                precision: 16,
                scale: 10,
              },
              FormUtils.makeUnitsField(object, "volume"),
              {
                title: "Parent ng Used",
                data: "ngUsed",
                type: "decimal",
                precision: 14,
                scale: 10,
                include:
                  config.detailedSample &&
                  (object.sampleCategory === "Stock" || object.sampleCategory === "Aliquot"),
              },
              {
                title: "Parent Volume Used",
                data: "volumeUsed",
                type: "decimal",
                precision: 16,
                scale: 10,
                min: 0,
                include:
                  config.detailedSample &&
                  (object.sampleCategory === "Stock" || object.sampleCategory === "Aliquot"),
              },
              {
                title: "Concentration",
                data: "concentration",
                type: "decimal",
                precision: 14,
                scale: 10,
              },
              FormUtils.makeUnitsField(object, "concentration"),
              FormUtils.makeBoxLocationField(),
              {
                title: "Location Note",
                data: "locationBarcode",
                type: "text",
                maxLength: 255,
              },
              {
                title: "Sequencing Control Type",
                data: "sequencingControlTypeId",
                type: "dropdown",
                source: Constants.sequencingControlTypes,
                sortSource: Utils.sorting.standardSort("alias"),
                getItemLabel: Utils.array.getAlias,
                getItemValue: Utils.array.getId,
                nullLabel: "n/a",
                include: !Constants.isDetailedSample || object.sampleCategory === "Aliquot",
              },
            ])
            .concat(
              !Constants.isDetailedSample ||
                (object.sampleCategory !== "Identity" && object.sampleCategory !== "Tissue")
                ? FormUtils.makeSopFields(object, config.sops)
                : []
            ),
        },
        {
          title: "Identity",
          include: config.detailedSample && object.sampleCategory === "Identity",
          fields: [
            {
              title: "External Names (comma separated)",
              data: "externalName",
              type: "text",
              required: true,
              maxLength: 255,
              description:
                "Name or other identifier for the donor or organism in an external system",
            },
            {
              title: "Sex",
              data: "donorSex",
              type: "dropdown",
              required: true,
              source: Constants.donorSexes,
              initial: "Unknown",
            },
            {
              title: "Consent",
              data: "consentLevel",
              type: "dropdown",
              required: true,
              source: Constants.consentLevels,
              initial: "This Project",
            },
          ],
        },
        {
          title: "Details",
          include: config.detailedSample,
          fields: [
            {
              title: "Parent",
              data: "parentId",
              type: "read-only",
              getDisplayValue: function (sample) {
                return sample.parentAlias || "n/a";
              },
              getLink: function (sample) {
                return sample.parentId ? Urls.ui.samples.edit(sample.parentId) : null;
              },
            },
            {
              title: "Sample Class",
              data: "sampleClassId",
              type: "read-only",
              getDisplayValue: function (sample) {
                return sample.sampleClassAlias;
              },
            },
            {
              title: "Sub-project",
              data: "subprojectId",
              type: "dropdown",
              source: Constants.subprojects.filter(function (subproject) {
                return subproject.parentProjectId === object.projectId;
              }),
              sortSource: Utils.sorting.standardSort("alias"),
              getItemLabel: function (item) {
                return item.alias;
              },
              getItemValue: function (item) {
                return item.id;
              },
            },
            {
              title: "Effective External Names",
              data: "effectiveExternalNames",
              type: "read-only",
              include: object.sampleCategory !== "Identity",
            },
            {
              title: "Tissue Origin",
              data: "effectiveTissueOriginAlias",
              getDisplayValue: function (item) {
                return (
                  item.effectiveTissueOriginAlias +
                  " (" +
                  item.effectiveTissueOriginDescription +
                  ")"
                );
              },
              type: "read-only",
              include: !["Identity", "Tissue"].includes(object.sampleCategory),
            },
            {
              title: "Tissue Type",
              data: "effectiveTissueTypeAlias",
              getDisplayValue: function (item) {
                return (
                  item.effectiveTissueTypeAlias + " (" + item.effectiveTissueTypeDescription + ")"
                );
              },
              type: "read-only",
              include: !["Identity", "Tissue"].includes(object.sampleCategory),
            },
            {
              title: "Effective Group ID",
              data: "effectiveGroupId",
              type: "read-only",
              getDisplayValue: function (sample) {
                if (sample.hasOwnProperty("effectiveGroupId") && sample.effectiveGroupId !== null) {
                  return sample.effectiveGroupId + " (" + sample.effectiveGroupIdSample + ")";
                } else {
                  return "None";
                }
              },
              include: object.sampleCategory !== "Identity",
            },
            {
              title: "Group ID",
              data: "groupId",
              type: "text",
              maxLength: 100,
              regex: Utils.validation.alphanumRegex,
            },
            {
              title: "Group Description",
              data: "groupDescription",
              type: "text",
              maxLength: 255,
            },
            {
              title: "Date of Creation",
              data: "creationDate",
              type: "date",
            },
          ],
        },
        {
          title: "Tissue",
          include: config.detailedSample && object.sampleCategory === "Tissue",
          fields: [
            {
              title: "Tissue Origin",
              data: "tissueOriginId",
              type: "dropdown",
              source: Constants.tissueOrigins.sort(
                Utils.sorting.standardSortWithException("alias", "nn")
              ),
              getItemLabel: function (item) {
                return item.alias + " (" + item.description + ")";
              },
              getItemValue: function (item) {
                return item.id;
              },
              required: true,
            },
            {
              title: "Tissue Type",
              data: "tissueTypeId",
              type: "dropdown",
              source: Constants.tissueTypes.sort(
                Utils.sorting.standardSortWithException("alias", "n")
              ),
              getItemLabel: function (item) {
                return item.label;
              },
              getItemValue: function (item) {
                return item.id;
              },
              required: true,
            },
            {
              title: "Passage Number",
              data: "passageNumber",
              type: "int",
              min: 1,
            },
            {
              title: "Times Received",
              data: "timesReceived",
              type: "int",
              min: 1,
              max: 1000000000,
            },
            {
              title: "Tube Number",
              data: "tubeNumber",
              type: "int",
              min: 1,
              max: 1000000000,
            },
            {
              title: "Tissue Material",
              data: "tissueMaterialId",
              type: "dropdown",
              nullLabel: "Unknown",
              source: Constants.tissueMaterials,
              getItemLabel: function (item) {
                return item.alias;
              },
              getItemValue: function (item) {
                return item.id;
              },
            },
            {
              title: "Region",
              data: "region",
              type: "text",
              maxLength: 255,
            },
            {
              title: "Secondary Identifier",
              data: "secondaryIdentifier",
              type: "text",
              maxLength: 255,
              description: "Identifier for the tissue sample in an external system",
            },
            {
              title: "Lab",
              data: "labId",
              type: "dropdown",
              nullLabel: "n/a",
              source: Constants.labs.sort(Utils.sorting.standardSort("alias")),
              getItemLabel: Utils.array.getAlias,
              getItemValue: Utils.array.getId,
              include: !!object.lab,
              description:
                "The external lab that a tissue came from. This field is intended for historical data only as the lab should " +
                "normally be recorded in a receipt transfer instead",
            },
            {
              title: "Timepoint",
              data: "timepoint",
              type: "text",
              maxLength: 50,
              description: "When the sample was taken",
            },
          ],
        },
        {
          title: "Tissue Processing",
          include: config.detailedSample && object.sampleCategory === "Tissue Processing",
          fields: makeIndexFields(object),
        },
        {
          title: "Slide",
          include: config.detailedSample && object.sampleSubcategory === "Slide",
          fields: [
            {
              title: "Initial Slides",
              data: "initialSlides",
              type: "int",
              min: 0,
              required: true,
            },
            {
              title: "Slides",
              data: "slides",
              type: "int",
              min: 0,
              required: true,
            },
            {
              title: "Thickness (µm)",
              data: "thickness",
              type: "int",
              min: 1,
            },
            {
              title: "Stain",
              data: "stainId",
              type: "dropdown",
              source: Constants.stains.sort(Utils.sorting.standardSort("name")),
              getItemLabel: function (item) {
                return item.name;
              },
              getItemValue: function (item) {
                return item.id;
              },
            },
            {
              title: "% Tumour",
              data: "percentTumour",
              type: "decimal",
              precision: 11,
              scale: 8,
              min: 0,
              max: 100,
            },
            {
              title: "% Necrosis",
              data: "percentNecrosis",
              type: "decimal",
              precision: 11,
              scale: 8,
              min: 0,
              max: 100,
            },
            {
              title: "Marked Area (mm²)",
              data: "markedArea",
              type: "decimal",
              precision: 11,
              scale: 8,
              min: 0,
            },
            {
              title: "Marked Area % Tumour",
              data: "markedAreaPercentTumour",
              type: "decimal",
              precision: 11,
              scale: 8,
              min: 0,
              max: 100,
            },
          ],
        },
        {
          title: "Tissue Piece",
          include: config.detailedSample && object.sampleSubcategory === "Tissue Piece",
          fields: [
            {
              title: "Piece Type",
              type: "dropdown",
              data: "tissuePieceTypeId",
              required: true,
              source: Constants.tissuePieceTypes,
              sortSource: Utils.sorting.standardSort("name"),
              getItemLabel: Utils.array.getName,
              getItemValue: Utils.array.getId,
            },
            {
              title: "Slides Consumed",
              data: "slidesConsumed",
              type: "int",
              min: 0,
              required: true,
            },
            {
              title: "Reference Slide",
              data: "referenceSlideId",
              type: "dropdown",
              source: object.relatedSlides,
              getItemLabel: function (item) {
                return item.name + " (" + item.alias + ")";
              },
              getItemValue: Utils.array.getId,
              sortSource: Utils.sorting.standardSort("id"),
              description:
                "Indicates a slide whose attributes such as marked area and % tumour are relevant to this sample." +
                " May be used for calculating extraction input per yield, for example.",
            },
          ],
        },
        {
          title: "Single Cell",
          include: config.detailedSample && object.sampleSubcategory === "Single Cell",
          fields: [
            {
              title: "Initial Cell Concentration",
              data: "initialCellConcentration",
              type: "decimal",
              precision: 14,
              scale: 10,
              description: "Initial concentration of cells in the sample at the time of receipt",
            },
            {
              title: "Target Cell Recovery",
              data: "targetCellRecovery",
              type: "int",
              min: 0,
            },
            {
              title: "Loading Cell Concentration",
              data: "loadingCellConcentration",
              type: "decimal",
              precision: 14,
              scale: 10,
              description: "Concentration of cells prepared for loading into the instrument",
            },
            {
              title: "Digestion",
              data: "digestion",
              type: "text",
              maxLength: 255,
              required: true,
            },
          ],
        },
        {
          title: "Stock",
          include: config.detailedSample && object.sampleCategory === "Stock",
          fields: makeIndexFields(object).concat([
            {
              title: "STR Status",
              data: "strStatus",
              type: "dropdown",
              source: Constants.strStatuses,
              required: true,
              description: "Status of short tandem repeat analysis",
            },
            {
              title: "DNAse Treated",
              data: "dnaseTreated",
              type: "checkbox",
              include: object.sampleSubcategory === "RNA (stock)",
              required: true,
            },
            {
              title: "Slides Consumed",
              data: "slidesConsumed",
              type: "int",
              min: 0,
            },
            {
              title: "Reference Slide",
              data: "referenceSlideId",
              type: "dropdown",
              source: object.relatedSlides,
              getItemLabel: function (item) {
                return item.name + " (" + item.alias + ")";
              },
              getItemValue: Utils.array.getId,
              sortSource: Utils.sorting.standardSort("id"),
            },
            {
              title: "Target Cell Recovery",
              data: "targetCellRecovery",
              type: "int",
              min: 0,
              include: object.sampleSubcategory === "Single Cell (stock)",
            },
            {
              title: "Cell Viability",
              data: "cellViability",
              type: "decimal",
              precision: 14,
              scale: 10,
              include: object.sampleSubcategory === "Single Cell (stock)",
            },
            {
              title: "Loading Cell Concentration",
              data: "loadingCellConcentration",
              type: "decimal",
              precision: 14,
              scale: 10,
              include: object.sampleSubcategory === "Single Cell (stock)",
            },
          ]),
        },
        {
          title: "Aliquot",
          include: config.detailedSample && object.sampleCategory === "Aliquot",
          fields: [
            {
              title: "Purpose",
              data: "samplePurposeId",
              type: "dropdown",
              source: Constants.samplePurposes.sort(Utils.sorting.standardSort("alias")),
              getItemLabel: function (item) {
                return item.alias;
              },
              getItemValue: function (item) {
                return item.id;
              },
            },
            {
              title: "Input into Library",
              data: "inputIntoLibrary",
              type: "decimal",
              precision: 14,
              scale: 10,
              include: object.sampleSubcategory === "Single Cell (aliquot)",
            },
          ],
        },
      ];
    },
    confirmSave: function (object, isDialog, form) {
      if (form.isChanged("projectId")) {
        var messages = [];
        if (object.identityConsentLevel && object.identityConsentLevel !== "All Projects") {
          messages.push("• Identity consent level is set to " + object.identityConsentLevel);
        }
        if (object.libraryCount > 0) {
          messages.push(
            "• " +
              object.libraryCount +
              " existing librar" +
              (object.libraryCount > 1 ? "ies" : "y") +
              " will be affected"
          );
        }
        if (messages.length) {
          var deferred = $.Deferred();
          messages.unshift("Are you sure you wish to transfer the sample to a different project?");
          Utils.showConfirmDialog("Confirm", "Save", messages, deferred.resolve, deferred.reject);
          return deferred.promise();
        }
      }
    },
  };

  function makeAssayControls(sample) {
    return FormUtils.makeAssaysFieldWithButtons(sample.requisitionAssayIds, function (assay) {
      if (sample.requisitionId) {
        // all plain samples should be caught here
        Assay.utils.showMetrics(assay, "RECEIPT");
      } else if (sample.sampleCategory === "Stock") {
        Assay.utils.showMetrics(assay, "EXTRACTION");
      } else {
        Utils.showOkDialog("Error", ["No metrics applicable to " + sample.sampleCategory]);
      }
    });
  }

  function makeIndexFields(sample) {
    return [
      {
        title: "Index Family",
        data: "indexFamilyId",
        type: "dropdown",
        include: Constants.sampleIndexFamilies && Constants.sampleIndexFamilies.length,
        nullLabel: "No indices",
        source: Constants.sampleIndexFamilies,
        sortSource: Utils.sorting.standardSort("name"),
        getItemLabel: Utils.array.getName,
        getItemValue: Utils.array.getId,
        onChange: function (newValue, form) {
          var changes = {
            disabled: !newValue,
            required: !!newValue,
          };
          if (newValue) {
            var family = Utils.array.findUniqueOrThrow(
              Utils.array.idPredicate(newValue),
              Constants.sampleIndexFamilies
            );
            changes.source = family.indices;
          } else {
            changes.source = [];
          }
          form.updateField("indexId", changes);
        },
      },
      {
        title: "Index",
        data: "indexId",
        type: "dropdown",
        include: Constants.sampleIndexFamilies && Constants.sampleIndexFamilies.length,
        source: sample.indexFamilyId
          ? Utils.array.findUniqueOrThrow(
              Utils.array.idPredicate(sample.indexFamilyId),
              Constants.sampleIndexFamilies
            ).indices
          : [],
        sortSource: Utils.sorting.standardSort("name"),
        getItemLabel: Utils.array.getName,
        getItemValue: Utils.array.getId,
      },
    ];
  }
})(jQuery);
