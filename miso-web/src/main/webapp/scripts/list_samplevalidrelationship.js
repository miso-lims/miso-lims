ListTarget.samplevalidrelationship = (function ($) {
  return {
    name: "Sample Class Relationships",
    createUrl: function (config, projectId) {
      throw new Error("Must be provided statically");
    },
    getQueryUrl: null,
    createBulkActions: function (config, projectId) {
      return !config.isAdmin || config.showSide === "child"
        ? []
        : [
            {
              name: "Un/Archive Relationships",
              action: function (items) {
                Utils.showDialog(
                  "Un/Archive Relationships",
                  "OK",
                  [
                    {
                      label: "Archived",
                      property: "archived",
                      type: "checkbox",
                    },
                  ],
                  function (results) {
                    items.forEach(function (item) {
                      item.archived = results.archived;
                    });
                    SampleClass.updateParents(items);
                  }
                );
              },
            },
            {
              name: "Remove",
              action: function (items) {
                SampleClass.removeParents(items);
              },
            },
          ];
    },
    createStaticActions: function (config, projectId) {
      return !config.isAdmin || config.showSide === "child"
        ? []
        : [
            {
              name: "Add",
              handler: function () {
                var sampleCategory = config.getSampleCategory();
                var relationshipOptions = getParentRelationshipOptions(sampleCategory);
                relationshipOptions = removeExistingRelationships(
                  relationshipOptions,
                  config.listId
                );
                if (!relationshipOptions.length) {
                  Utils.showOkDialog("Error", ["No potential classes for new relationship"]);
                  return;
                }
                Utils.showWizardDialog(
                  "Add Parent Relationship",
                  relationshipOptions.map(function (sampleClass) {
                    return {
                      name: sampleClass.alias,
                      handler: function () {
                        var relationship = {
                          archived: false,
                        };
                        relationship.parentId = sampleClass.id;
                        SampleClass.addParent(relationship);
                      },
                    };
                  })
                );
              },
            },
          ];
    },
    createColumns: function (config, projectId) {
      validateShowSide(config.showSide);
      return [
        {
          sTitle: "Sample Class",
          mData: function (full, type, data) {
            return getSampleClass(full, config).alias;
          },
          include: true,
          iSortPriority: 1,
        },
        {
          sTitle: "Category",
          mData: function (full, type, data) {
            return getSampleClass(full, config).sampleCategory;
          },
          include: true,
          bSortable: false,
        },
        {
          sTitle: "Class Archived",
          mData: function (full, type, data) {
            return getSampleClass(full, config).archived;
          },
          include: true,
          bSortable: false,
          mRender: ListUtils.render.archived,
        },
        {
          sTitle: "Relationship Archived",
          mData: "archived",
          include: true,
          iSortPriority: 0,
          mRender: ListUtils.render.archived,
        },
      ];
    },
  };

  function validateShowSide(showSide) {
    if (showSide !== "parent" && showSide !== "child") {
      throw new Error("Unhandled showSide: " + showSide);
    }
  }

  function getDisplayIdField(config) {
    if (config.showSide === "parent") {
      return "parentId";
    } else {
      return "childId";
    }
  }

  function getSampleClass(relationship, config) {
    var id = relationship[getDisplayIdField(config)];
    return Utils.array.findUniqueOrThrow(Utils.array.idPredicate(id), Constants.sampleClasses);
  }

  function getParentRelationshipOptions(sampleCategory) {
    switch (sampleCategory) {
      case "Identity":
        return [];
      case "Tissue":
        return getSampleClassesForCategories(["Identity", "Tissue"]);
      case "Tissue Processing":
        return getSampleClassesForCategories(["Tissue", "Tissue Processing"]);
      case "Stock":
        return getSampleClassesForCategories(["Tissue", "Tissue Processing", "Stock"]);
      case "Aliquot":
        return getSampleClassesForCategories(["Stock", "Aliquot"]);
      default:
        throw new Error("Unhandled sample category: " + sampleCategory);
    }
  }

  function getSampleClassesForCategories(categories) {
    var classes = [];
    categories.forEach(function (category) {
      classes = classes.concat(
        Constants.sampleClasses
          .filter(function (sampleClass) {
            return sampleClass.sampleCategory === category && !sampleClass.archived;
          })
          .sort(Utils.sorting.standardSort("alias"))
      );
    });
    return classes;
  }

  function removeExistingRelationships(relationshipOptions, listId) {
    var existingIds = $("#" + listId)
      .dataTable()
      .fnGetData()
      .map(function (item) {
        return item.parentId;
      });
    return relationshipOptions.filter(function (sampleClass) {
      return existingIds.indexOf(sampleClass.id) === -1;
    });
  }
})(jQuery);
