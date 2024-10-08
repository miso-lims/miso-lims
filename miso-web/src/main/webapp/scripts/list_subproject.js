ListTarget.subproject = {
  name: "Subprojects",
  getUserManualUrl: function () {
    return Urls.external.userManual("projects", "subprojects");
  },
  createUrl: function (config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  createBulkActions: function (config, projectId) {
    var actions = BulkTarget.subproject.getBulkActions(config);
    if (config.isAdmin) {
      actions.push(
        ListUtils.createBulkDeleteAction("Subprojects", "subprojects", Utils.array.getAlias)
      );
    }
    return actions;
  },
  createStaticActions: function (config, projectId) {
    return [
      {
        name: "Add",
        handler: function () {
          Utils.showDialog(
            "Create Subprojects",
            "Create",
            [
              {
                property: "quantity",
                type: "int",
                label: "Quantity",
                value: 1,
              },
            ],
            function (result) {
              if (result.quantity < 1) {
                Utils.showOkDialog("Create Subprojects", [
                  "That's a peculiar number of subprojects to create.",
                ]);
                return;
              }
              window.location =
                Urls.ui.subprojects.bulkCreate +
                "?" +
                Utils.page.param({
                  quantity: result.quantity,
                });
            }
          );
        },
      },
    ];
  },
  createColumns: function (config, projectId) {
    return [
      {
        sTitle: "Alias",
        mData: "alias",
        include: true,
        iSortPriority: 1,
      },
      {
        sTitle: "Project",
        mData: "parentProjectId",
        include: true,
        iSortPriority: 0,
        mRender: function (data, type, full) {
          var projectTitle =
            Utils.array.maybeGetProperty(
              Utils.array.findFirstOrNull(Utils.array.idPredicate(data), config.projects),
              "title"
            ) || "Unknown";
          if (type === "display") {
            return '<a href="' + Urls.ui.projects.edit(data) + '">' + projectTitle + "</a>";
          } else {
            return projectTitle;
          }
        },
      },
      {
        sTitle: "Priority",
        mData: "priority",
        include: true,
        iSortPriority: 0,
        mRender: ListUtils.render.booleanChecks,
      },
      {
        sTitle: "Reference Genome",
        mData: "referenceGenomeId",
        include: true,
        iSortPriority: 0,
        mRender: ListUtils.render.textFromId(Constants.referenceGenomes, "alias"),
      },
    ];
  },
};

var Subproject = Subproject || {
  filterSamples: function (samplesArrowClickId, samplesTableId, subprojectAlias) {
    var expandableSection = jQuery("#" + samplesTableId).closest(".expandable_section");
    if (expandableSection.is(":hidden")) {
      // make it visible
      jQuery("#" + samplesArrowClickId)
        .closest(".sectionDivider")
        .click();
    }
    Utils.ui.filterTable(samplesTableId, "subproject", subprojectAlias);
    expandableSection[0].scrollIntoView();
  },
};
