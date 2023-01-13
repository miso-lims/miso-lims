if (typeof FormTarget === "undefined") {
  FormTarget = {};
}
FormTarget.experiment = (function ($) {
  return {
    getUserManualUrl: function () {
      return Urls.external.userManual("european_nucleotide_archive_support", "experiments");
    },
    getSaveUrl: function (experiment) {
      if (experiment.id) {
        return Urls.rest.experiments.update(experiment.id);
      } else {
        throw new Error("Page not intended for new experiment creation");
      }
    },
    getSaveMethod: function (experiment) {
      return "PUT";
    },
    getEditUrl: function (experiment) {
      return Urls.ui.experiments.edit(experiment.id);
    },
    getSections: function (config, object) {
      return [
        {
          title: "Experiment Information",
          fields: [
            {
              title: "Experiment ID",
              data: "id",
              type: "read-only",
            },
            {
              title: "Name",
              data: "id",
              type: "read-only",
            },
            {
              title: "Title",
              data: "title",
              type: "text",
              required: true,
              maxLength: 255,
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
              title: "Accession",
              data: "accession",
              type: "read-only",
              getLink: function (experiment) {
                return Urls.external.enaAccession(experiment.accession);
              },
              include: object.accession,
            },
            {
              title: "Study",
              data: "study.id",
              type: "read-only",
              getDisplayValue: function (experiment) {
                return experiment.study.name + " (" + experiment.study.alias + ")";
              },
              getLink: function (experiment) {
                return Urls.ui.studies.edit(experiment.study.id);
              },
            },
            {
              title: "Platform",
              data: "instrumentModel.id",
              type: "read-only",
              getDisplayValue: function (experiment) {
                return (
                  experiment.instrumentModel.platformType + " - " + experiment.instrumentModel.alias
                );
              },
            },
            {
              title: "Library",
              data: "library.id",
              type: "read-only",
              getDisplayValue: function (experiment) {
                return experiment.library.name + " (" + experiment.library.alias + ")";
              },
              getLink: function (experiment) {
                return Urls.ui.libraries.edit(experiment.libraryId);
              },
            },
          ],
        },
      ];
    },
  };
})(jQuery);
