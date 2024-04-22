if (typeof FormTarget === "undefined") {
  FormTarget = {};
}
FormTarget.libraryaliquot = (function ($) {
  /*
   * Expected config {
   *   detailedSample: boolean
   * }
   */

  return {
    getUserManualUrl: function () {
      return Urls.external.userManual("library_aliquots");
    },
    getSaveUrl: function (aliquot) {
      if (aliquot.id) {
        return Urls.rest.libraryAliquots.update(aliquot.id);
      } else {
        throw new Error("Page not intended for new library aliquot creation");
      }
    },
    getSaveMethod: function (aliquot) {
      return "PUT";
    },
    getEditUrl: function (aliquot) {
      return Urls.ui.libraryAliquots.edit(aliquot.id);
    },
    getSections: function (config, object) {
      return [
        {
          title: "Library Aliquot Information",
          fields: [
            {
              title: "Library Aliquot ID",
              data: "id",
              type: "read-only",
            },
            {
              title: "Name",
              data: "name",
              type: "read-only",
            },
            {
              title: "Parent Library Aliquot",
              data: "parentAliquotId",
              include: !!object.parentAliquotId,
              type: "read-only",
              getDisplayValue: function (aliquot) {
                return aliquot.parentAliquotAlias;
              },
              getLink: function (aliquot) {
                return Urls.ui.libraryAliquots.edit(aliquot.parentAliquotId);
              },
            },
            {
              title: "Parent Library",
              data: "libraryId",
              include: !object.parentAliquotId,
              type: "read-only",
              getDisplayValue: function (aliquot) {
                return aliquot.libraryAlias;
              },
              getLink: function (aliquot) {
                return Urls.ui.libraries.edit(aliquot.libraryId);
              },
            },
            {
              title: "Alias",
              data: "alias",
              type: "text",
              required: true,
              maxLength: 100,
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
            {
              title: "Requisition",
              data: "requisitionId",
              type: "read-only",
              getDisplayValue: function (item) {
                return item.requisitionAlias || "n/a";
              },
              getLink: function (item) {
                return item.requisitionId ? Urls.ui.requisitions.edit(item.requisitionId) : null;
              },
            },
            {
              title: "Assays",
              data: "requisitionAssayIds",
              type: "special",
              makeControls: function () {
                return makeAssayControls(object);
              },
            },
            {
              title: "Design Code",
              data: "libraryDesignCodeId",
              type: "dropdown",
              include: config.detailedSample,
              required: true,
              source: Constants.libraryDesignCodes,
              sortSource: Utils.sorting.standardSort("code"),
              getItemLabel: function (item) {
                return item.code + " (" + item.description + ")";
              },
              getItemValue: function (item) {
                return item.id;
              },
              onChange: function (newValue, form) {
                var designCode = Utils.array.findUniqueOrThrow(
                  Utils.array.idPredicate(newValue),
                  Constants.libraryDesignCodes
                );
                var changes = {
                  required: designCode.targetedSequencingRequired,
                };
                form.updateField("kitDescriptorId", changes);
                form.updateField("targetedSequencingId", changes);
              },
            },
          ]
            .concat(FormUtils.makeDetailedQcStatusFields())
            .concat([
              FormUtils.makeDnaSizeField(),
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
                title: "Volume",
                data: "volume",
                type: "decimal",
                precision: 16,
                scale: 10,
              },
              FormUtils.makeUnitsField(object, "volume"),
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
                title: "Creation Date",
                data: "creationDate",
                required: "true",
                type: "date",
              },
              {
                title: "Kit",
                data: "kitDescriptorId",
                type: "dropdown",
                source: Constants.kitDescriptors.filter(function (kit) {
                  return (
                    kit.kitType === "Library" &&
                    kit.platformType === object.libraryPlatformType &&
                    (!kit.archived || kit.id === object.kitDescriptorId)
                  );
                }),
                sortSource: Utils.sorting.standardSort("name"),
                getItemLabel: function (item) {
                  return item.name;
                },
                getItemValue: function (item) {
                  return item.id;
                },
                onChange: function (newValue, form) {
                  var kitDescriptor = !newValue
                    ? null
                    : Utils.array.findUniqueOrThrow(
                        Utils.array.idPredicate(newValue),
                        Constants.kitDescriptors
                      );
                  var tarseqs = !kitDescriptor
                    ? []
                    : Constants.targetedSequencings.filter(function (tarseq) {
                        return (
                          tarseq.kitDescriptorIds.indexOf(kitDescriptor.id) !== -1 &&
                          (!tarseq.archived || object.targetedSequencingId === tarseq.id)
                        );
                      });
                  form.updateField("targetedSequencingId", {
                    source: tarseqs,
                  });
                },
              },
              {
                title: "Kit Lot",
                data: "kitLot",
                type: "text",
                maxLength: 100,
                regex: Utils.validation.uriComponentRegex,
              },
              {
                title: "Targeted Sequencing",
                data: "targetedSequencingId",
                type: "dropdown",
                source: !object.kitDescriptorId
                  ? []
                  : Constants.targetedSequencings.filter(function (tarseq) {
                      return (
                        tarseq.kitDescriptorIds.indexOf(object.kitDescriptorId) !== -1 &&
                        (!tarseq.archived || object.targetedSequencingId === tarseq.id)
                      );
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
                title: "Parent ng Used",
                data: "ngUsed",
                type: "decimal",
                precision: 14,
                scale: 10,
                min: 0,
              },
              {
                title: "Parent Volume Used",
                data: "volumeUsed",
                type: "decimal",
                precision: 16,
                scale: 10,
                min: 0,
              },
            ]),
        },
        {
          title: "Details",
          include: config.detailedSample,
          fields: [
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
            },
            {
              title: "Effective Group ID",
              data: "effectiveGroupId",
              type: "read-only",
              getDisplayValue: function (library) {
                if (
                  library.hasOwnProperty("effectiveGroupId") &&
                  library.effectiveGroupId !== null
                ) {
                  return library.effectiveGroupId + " (" + library.effectiveGroupIdSample + ")";
                } else {
                  return "None";
                }
              },
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
          ],
        },
      ];
    },
  };

  function makeAssayControls(aliquot) {
    return FormUtils.makeAssaysFieldWithButtons(aliquot.requisitionAssayIds, function (assay) {
      Utils.showWizardDialog(
        "View Metrics",
        [
          {
            name: "Library Preparation",
            handler: function () {
              Assay.utils.showMetrics(assay, "LIBRARY_PREP");
            },
          },
          {
            name: "Library Qualification",
            handler: function () {
              Assay.utils.showMetrics(assay, "LIBRARY_QUALIFICATION");
            },
          },
        ],
        "Select category to view metrics. Relevant category depends on the assay test that this library aliquot" +
          " will be used for. Sequencing metrics may be included."
      );
    });
  }
})(jQuery);
