var Submission = {
  download: function (id) {
    Utils.showDialog(
      "Download XML",
      "Download",
      [
        {
          type: "select",
          required: true,
          label: "Action",
          values: Constants.submissionAction,
          property: "action",
        },
        {
          type: "text",
          required: "true",
          label: "Centre Name",
          property: "centerName",
        },
      ],
      function (results) {
        window.location = Urls.rest.submissions.download(id) + "?" + Utils.page.param(results);
      }
    );
  },
};
