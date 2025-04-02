ListTarget.note = (function () {
  return {
    name: "Notes",
    createUrl: function (config) {
      throw new Error("Must be provided statically");
    },
    createBulkActions: function (config) {
      return [
        {
          name: "Delete",
          action: function (items) {
            validateConfig(config);
            if (
              items.some(function (note) {
                return note.entityType !== config.entityType || note.entityId !== config.entityId;
              })
            ) {
              Utils.showOkDialog("Error", ["Cannot delete notes from parent entities."]);
              return;
            }
            var lines = [
              "Are you sure you want to delete these notes? This cannot be undone.",
              "Note: a note may only be deleted by its creator or an admin.",
            ];
            var noteIds = [];
            items.forEach(function (note) {
              lines.push("* " + note.text);
              noteIds.push(note.id);
            });
            Utils.showConfirmDialog("Delete Notes", "Delete", lines, function () {
              Utils.ajaxWithDialog(
                "Deleting Notes",
                "POST",
                Urls.rest.notes.bulkDelete,
                {
                  entityType: config.entityType,
                  entityId: config.entityId,
                  noteIds: noteIds,
                },
                Utils.page.pageReload
              );
            });
          },
        },
      ];
    },
    createStaticActions: function (config) {
      return [
        {
          name: "Add Note",
          handler: function () {
            validateConfig(config);
            Utils.notes.showNoteDialog(config.entityType, config.entityId);
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

  function validateConfig(config) {
    if (!config.entityId || !config.entityType) {
      throw new Error("List config missing entity type or ID");
    }
  }
})();
