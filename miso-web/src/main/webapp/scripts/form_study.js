if (typeof FormTarget === "undefined") {
  FormTarget = {};
}
FormTarget.study = (function ($) {
  /*
   * Expected config {
   *   detailedSample: boolean,
   *   projects: array
   * }
   */

  return {
    getUserManualUrl: function () {
      return Urls.external.userManual("european_nucleotide_archive_support", "studies");
    },
    getSaveUrl: function (study) {
      return study.id ? Urls.rest.studies.update(study.id) : Urls.rest.studies.create;
    },
    getSaveMethod: function (study) {
      return study.id ? "PUT" : "POST";
    },
    getEditUrl: function (study) {
      return Urls.ui.studies.edit(study.id);
    },
    getSections: function (config, object) {
      return [
        {
          title: "Study Information",
          fields: [
            {
              title: "Study ID",
              data: "id",
              type: "read-only",
              getDisplayValue: function (study) {
                return study.id || "Unsaved";
              },
            },
            {
              title: "Project",
              data: "projectId",
              type: "dropdown",
              include: !object.id,
              required: true,
              source: config.projects,
              getItemLabel: function (item) {
                return config.detailedSample ? item.shortName : item.alias;
              },
              getItemValue: Utils.array.getId,
              sortSource: Utils.sorting.standardSort(config.detailedSample ? "shortName" : "alias"),
            },
            {
              title: "Project",
              data: "projectId",
              type: "read-only",
              include: !!object.id,
              getDisplayValue: function (study) {
                var project = Utils.array.findUniqueOrThrow(
                  Utils.array.idPredicate(study.projectId),
                  config.projects
                );
                return config.detailedSample ? project.shortName : project.alias;
              },
              getLink: function (study) {
                return "/miso/project/" + study.projectId;
              },
            },
            {
              title: "Name",
              data: "name",
              type: "read-only",
              getDisplayValue: function (study) {
                return study.name || "Unsaved";
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
              title: "Accession",
              data: "accession",
              type: "read-only",
              getLink: function (study) {
                return Urls.external.enaAccession(study.accession);
              },
              include: !!object.accession,
            },
            {
              title: "Study Type",
              data: "studyTypeId",
              type: "dropdown",
              source: Constants.studyTypes,
              getItemLabel: Utils.array.getName,
              getItemValue: Utils.array.getId,
              sortSource: Utils.sorting.standardSort("name"),
              required: true,
            },
          ],
        },
      ];
    },
  };
})(jQuery);
