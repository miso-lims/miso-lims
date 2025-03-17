ListTarget.note = (function () {
  return {
    name: "Notes",
    createUrl: function (config, entityId) {
      var id = config.sampleId || config.libraryId || entityId;
      var entityType = config.entityType;

      if (!id || !entityType) {
        console.error("Error: Missing entity ID or type for notes");
        return null;
      }

      return "/rest/notes/" + entityType + "/" + id + "/dt?includeRelated=true";
    },
    getQueryUrl: function () {
      return "/rest/notes/search";
    },
    createBulkActions: function (config, entityId) {
      return [
        {
          name: "Delete",
          action: function (items) {
            if (items.length === 0) return;
            var id = config.sampleId || config.libraryId || entityId;
            var entityType = config.entityType;
            if (!id || !entityType) {
              Utils.showOkDialog("Error", ["Cannot determine which entity these notes belong to."]);
              return;
            }
            var currentEntityNotes = items.filter(function (note) {
              var typeMatches =
                note.entityType.toLowerCase().indexOf(entityType.toLowerCase()) !== -1;
              var idMatches = String(note.entityId) === String(id);

              return typeMatches && idMatches;
            });

            if (currentEntityNotes.length === 0) {
              Utils.showOkDialog("Error", ["Cannot delete notes from parent entities."]);
              return;
            }
            var lines = ["Are you sure you want to delete these notes? This cannot be undone."];
            var ids = [];
            jQuery.each(currentEntityNotes, function (index, note) {
              lines.push("* " + note.text);
              ids.push(note.id);
            });
            var url = "/rest/notes/" + entityType + "/" + id + "/" + ids.join(",");
            Utils.showConfirmDialog("Delete Notes", "Delete", lines, function () {
              Utils.ajaxWithDialog("Deleting Notes", "DELETE", url, null, function () {
                Utils.page.pageReload();
              });
            });
          },
        },
      ];
    },
    createStaticActions: function (config, entityId) {
      return [
        {
          name: "Add Note",
          handler: function () {
            var id = config.sampleId || config.libraryId || entityId;
            var entityType = config.entityType;
            if (!id || !entityType) {
              Utils.showOkDialog("Error", ["Cannot determine which entity to add a note to."]);
              return;
            }
            Utils.showDialog(
              "Create New Note",
              "Add Note",
              [
                {
                  label: "Internal Only?",
                  property: "internalOnly",
                  type: "checkbox",
                  value: true,
                },
                {
                  label: "Text",
                  property: "text",
                  type: "textarea",
                  rows: 3,
                  required: true,
                },
              ],
              function (results) {
                var url = "/rest/notes/" + entityType + "/" + id;

                Utils.ajaxWithDialog(
                  "Adding Note",
                  "POST",
                  url,
                  {
                    internalOnly: results.internalOnly,
                    text: results.text,
                  },
                  Utils.page.pageReload
                );
              }
            );
          },
        },
      ];
    },
    createColumns: function (config, entityId) {
      return [
        {
          sTitle: "Name",
          mData: "entityName",
          include: true,
        },
        {
          sTitle: "Alias",
          mData: "entityAlias",
          include: true,
        },
        {
          sTitle: "Note",
          mData: "text",
          include: true,
        },
        {
          sTitle: "Owner",
          mData: "ownerName",
          include: true,
        },
        {
          sTitle: "Created",
          mData: "creationDate",
          include: true,
        },
        {
          sTitle: "Source",
          mData: "source",
          include: true,
          mRender: function (data, type, full) {
            if (!data) {
              return "Current";
            }
            return data.charAt(0).toUpperCase() + data.slice(1);
          },
        },
      ];
    },
    aoColumnDefs: [
      {
        sDefaultContent: "",
        aTargets: ["_all"],
      },
    ],
    processData: function (data) {
      return Array.isArray(data) ? data : data.data || [];
    },
    bServerSide: true,
    bStateSave: false,
  };
})();

Utils.notes = {
  showNoteDialog: function (entityType, entityId) {
    Utils.showDialog(
      "Create New Note",
      "Add Note",
      [
        {
          label: "Internal Only?",
          property: "internalOnly",
          type: "checkbox",
          value: true,
        },
        {
          label: "Text",
          property: "text",
          type: "textarea",
          rows: 3,
          required: true,
        },
      ],
      function (results) {
        var url = "/rest/notes/" + entityType + "/" + entityId;

        Utils.ajaxWithDialog(
          "Adding Note",
          "POST",
          url,
          {
            internalOnly: results.internalOnly,
            text: results.text,
          },
          Utils.page.pageReload
        );
      }
    );
  },

  deleteNote: function (entityType, entityId, noteId) {
    Utils.showConfirmDialog(
      "Delete Note",
      "Delete",
      ["Are you sure you wish to delete this note?"],
      function () {
        var url = "/rest/notes/" + entityType + "/" + entityId + "/" + noteId;
        Utils.ajaxWithDialog("Deleting Note", "DELETE", url, null, Utils.page.pageReload);
      }
    );
  },
};
