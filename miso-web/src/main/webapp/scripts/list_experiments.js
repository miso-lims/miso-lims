ListTarget.experiment = {
  name: "Experiments",
  getUserManualUrl: function () {
    return Urls.external.userManual("european_nucleotide_archive_support", "experiments");
  },
  createUrl: function (config, projectId) {
    throw new Error("Experiments must be provided statically");
  },
  getQueryUrl: null,
  createBulkActions: function (config, projectId) {
    if (config.inSubmission) {
      return [];
    }
    var actions = [
      {
        name: "Create Submission",
        action: function (experiments) {
          window.location =
            Urls.ui.submissions.create +
            "?" +
            Utils.page.param({
              experimentIds: experiments.map(Utils.array.getId).join(","),
            });
        },
      },
    ];
    if (config.isAdmin) {
      actions.push(
        ListUtils.createBulkDeleteAction("Experiments", "experiments", Utils.array.getAlias)
      );
    }
    return actions;
  },
  createStaticActions: function (config, projectId) {
    var actions = [];
    if (config.runId) {
      actions.push(
        {
          name: "Create New",
          handler: function () {
            var url = Urls.rest.runs.potentialExperiments(config.runId);
            Utils.ajaxWithDialog(
              "Finding potential experiments",
              "GET",
              url,
              null,
              function (potentialExperiments) {
                if (!potentialExperiments || !potentialExperiments.length) {
                  Utils.showOkDialog("Error", [
                    "No potential experiments found. At least one study must exist in the project that the desired library belongs to",
                  ]);
                  return;
                }
                var creationActions = potentialExperiments.map(function (request) {
                  return {
                    name:
                      request.experiment.library.name +
                      " (" +
                      request.experiment.library.alias +
                      ")",
                    handler: function () {
                      Utils.showDialog(
                        "Create Experiment",
                        "Create",
                        [
                          {
                            type: "select",
                            required: true,
                            label: "Study",
                            values: request.studies,
                            getLabel: function (study) {
                              return study.name + " (" + study.alias + ")";
                            },
                            property: "study",
                          },
                          {
                            type: "text",
                            required: "true",
                            label: "Title",
                            property: "title",
                          },
                          {
                            type: "text",
                            required: "true",
                            label: "Alias",
                            property: "alias",
                          },
                        ],
                        function (result) {
                          request.experiment.alias = result.alias;
                          request.experiment.study = result.study;
                          request.experiment.title = result.title;

                          Utils.ajaxWithDialog(
                            "Creating to Experiment",
                            "POST",
                            Urls.rest.experiments.create,
                            request.experiment,
                            Utils.page.pageReload
                          );
                        },
                        showCreate
                      );
                    },
                  };
                });
                var showCreate = function () {
                  Utils.showWizardDialog("Create Experiment", creationActions);
                };
                showCreate();
              }
            );
          },
        },
        {
          name: "Add to Existing",
          handler: function () {
            var url = Urls.rest.runs.potentialExperiments(config.runId);
            Utils.ajaxWithDialog(
              "Finding potential experiments",
              "GET",
              url,
              null,
              function (potentialExperiments) {
                if (!potentialExperiments || !potentialExperiments.length) {
                  Utils.showOkDialog("Error", ["No existing experiments found"]);
                  return;
                }
                Utils.showWizardDialog(
                  "Add to Experiment",
                  potentialExperiments.map(function (request) {
                    return {
                      name:
                        request.partition.containerName +
                        " " +
                        request.partition.partitionNumber +
                        " (" +
                        request.partition.pool.name +
                        ") to " +
                        request.experiment.name +
                        " (" +
                        request.experiment.alias +
                        ")",
                      handler: function () {
                        Utils.ajaxWithDialog(
                          "Adding to Experiment",
                          "POST",
                          Urls.rest.experiments.addRunPartition(request.experiment.id) +
                            "?" +
                            Utils.page.param({
                              runId: config.runId,
                              partitionId: request.partition.id,
                            }),
                          null,
                          Utils.page.pageReload
                        );
                      },
                    };
                  })
                );
              }
            );
          },
        }
      );
    }

    if (config.addToExperiment && config.addToExperiment.length) {
      actions.push({
        name: "Add to Existing",
        handler: function () {
          Utils.showWizardDialog(
            "Add to Experiment",
            config.addToExperiment.map(function (request) {
              return {
                name:
                  request.partition.containerName +
                  " " +
                  request.partition.partitionNumber +
                  " (" +
                  request.partition.pool.name +
                  ") to " +
                  request.experiment.name +
                  " (" +
                  request.experiment.alias +
                  ")",
                handler: function () {
                  Utils.ajaxWithDialog(
                    "Adding to Experiment",
                    "POST",
                    Urls.rest.experiments.addRunPartition(request.experiment.id) +
                      "?" +
                      Utils.page.param({
                        runId: config.runId,
                        partitionId: request.partition.id,
                      }),
                    null,
                    Utils.page.pageReload
                  );
                },
              };
            })
          );
        },
      });
    }
    return actions;
  },
  createColumns: function (config, projectId) {
    return [
      ListUtils.idHyperlinkColumn(
        "Name",
        Urls.ui.experiments.edit,
        "id",
        Utils.array.getName,
        1,
        true
      ),
      ListUtils.labelHyperlinkColumn(
        "Alias",
        Urls.ui.experiments.edit,
        Utils.array.getId,
        "alias",
        0,
        true
      ),
      {
        sTitle: "Platform",
        mData: "instrumentModel.alias",
        include: true,
        iSortPriority: 0,
      },
      ListUtils.labelHyperlinkColumn(
        "Library Name",
        Urls.ui.libraries.edit,
        function (experiment) {
          return experiment.library.id;
        },
        "library.name",
        0,
        !config.libraryId
      ),
      ListUtils.labelHyperlinkColumn(
        "Library Alias",
        Urls.ui.libraries.edit,
        function (experiment) {
          return experiment.library.id;
        },
        "library.alias",
        0,
        !config.libraryId
      ),
      ListUtils.labelHyperlinkColumn(
        "Study Name",
        Urls.ui.studies.edit,
        function (experiment) {
          return experiment.study.id;
        },
        "study.name",
        0,
        !config.studyId
      ),
      ListUtils.labelHyperlinkColumn(
        "Study Alias",
        Urls.ui.studies.edit,
        function (experiment) {
          return experiment.study.id;
        },
        "study.alias",
        0,
        !config.studyId
      ),
    ];
  },
};
