if (typeof FormTarget === "undefined") {
  FormTarget = {};
}
FormTarget.submission = (function ($) {
  return {
    getUserManualUrl: function () {
      return Urls.external.userManual("european_nucleotide_archive_support", "submissions");
    },
    getSaveUrl: function (submission) {
      return submission.id
        ? Urls.rest.submissions.update(submission.id)
        : Urls.rest.submissions.create;
    },
    getSaveMethod: function (submission) {
      return submission.id ? "PUT" : "POST";
    },
    getEditUrl: function (submission) {
      return Urls.ui.submissions.edit(submission.id);
    },
    getSections: function (config, object) {
      return [
        {
          title: "Submission Information",
          fields: [
            {
              title: "Submission ID",
              data: "id",
              type: "read-only",
              getDisplayValue: function (submission) {
                return submission.id || "Unsaved";
              },
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
              getLink: function (sample) {
                return Urls.external.enaAccession(sample.accession);
              },
              include: object.accession,
            },
            {
              title: "Completed?",
              data: "completed",
              type: "checkbox",
            },
            {
              title: "Verified?",
              data: "verified",
              type: "checkbox",
            },
            {
              title: "Submission Date",
              data: "submittedDate",
              type: "date",
            },
          ],
        },
      ];
    },
  };
})(jQuery);
