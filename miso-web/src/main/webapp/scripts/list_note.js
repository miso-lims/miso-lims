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

      return Urls.rest.notes.datatable(entityType, id) + "?includeRelated=true";
    },
    createBulkActions: function (config, entityId) {
      return [
        {
          name: "Delete",
          action: function (items) {
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
            if (currentEntityNotes.length !== items.length || currentEntityNotes.length === 0) {
              Utils.showOkDialog("Error", ["Cannot delete notes from parent entities."]);
              return;
            }
            var lines = ["Are you sure you want to delete these notes? This cannot be undone."];
            var ids = [];
            jQuery.each(currentEntityNotes, function (index, note) {
              lines.push("* " + note.text);
              ids.push(note.id);
            });
            Utils.showConfirmDialog("Delete Notes", "Delete", lines, function () {
              Utils.ajaxWithDialog(
                "Deleting Notes",
                "POST",
                Urls.rest.notes.bulkDelete,
                {
                  entityType: entityType,
                  entityId: id,
                  ids: ids,
                },
                function () {
                  Utils.page.pageReload();
                }
              );
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
            Utils.notes.showNoteDialog(entityType, id);
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
      return data;
    },
    bServerSide: false,
    bStateSave: false,
  };
})();
