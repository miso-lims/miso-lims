ListTarget.locationmap = {
  name: "Location Maps",
  getUserManualUrl: function () {
    return Urls.external.userManual("freezers_and_rooms");
  },
  createUrl: function (config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  createBulkActions: function (config, projectId) {
    return !config.isAdmin
      ? []
      : [
          ListUtils.createBulkDeleteAction("Location Maps", "locationmaps", function (map) {
            return map.filename;
          }),
        ];
  },
  createStaticActions: function (config, projectId) {
    return !config.isAdmin
      ? []
      : [
          {
            name: "Upload",
            handler: function () {
              var dialogArea = jQuery("#dialog");
              dialogArea.empty();

              var form = jQuery('<form id="uploadForm">');
              form.append(jQuery('<p><input id="fileInput" type="file" name="file"></p>'));
              form.append(
                jQuery("<p>")
                  .append(jQuery('<label for="description">Description:</label>'))
                  .append(jQuery('<input id="description" type="text" name="description">'))
              );
              dialogArea.append(form);

              var dialog = dialogArea.dialog({
                autoOpen: true,
                width: 500,
                title: "Upload Location Map",
                modal: true,
                buttons: {
                  upload: {
                    id: "upload",
                    text: "Upload",
                    click: function () {
                      if (!jQuery("#fileInput").val()) {
                        alert("No file selected!");
                        return;
                      }
                      var formData = new FormData(jQuery("#uploadForm")[0]);
                      dialog.dialog("close");
                      dialogArea.empty();
                      dialogArea.append(jQuery("<p>Uploading...</p>"));

                      dialog = jQuery("#dialog").dialog({
                        autoOpen: true,
                        height: 400,
                        width: 350,
                        title: "Uploading File",
                        modal: true,
                        buttons: {},
                        closeOnEscape: false,
                        open: function (event, ui) {
                          jQuery(this)
                            .parent()
                            .children()
                            .children(".ui-dialog-titlebar-close")
                            .hide();
                        },
                      });

                      jQuery
                        .ajax({
                          url: Urls.rest.locationMaps.create,
                          type: "POST",
                          data: formData,
                          cache: false,
                          contentType: false,
                          processData: false,
                        })
                        .done(function (data) {
                          Utils.showOkDialog(
                            "Upload Location Map",
                            ["File upload successful"],
                            Utils.page.pageReload
                          );
                        })
                        .fail(function (xhr, textStatus, errorThrown) {
                          dialog.dialog("close");
                          Utils.showAjaxErrorDialog(xhr, textStatus, errorThrown);
                        });
                    },
                  },
                  cancel: {
                    id: "cancel",
                    text: "Cancel",
                    click: function () {
                      dialog.dialog("close");
                    },
                  },
                },
              });
            },
          },
        ];
  },
  createColumns: function (config, projectId) {
    return [
      {
        sTitle: "Filename",
        mData: "filename",
        include: true,
        iSortPriority: 0,
        mRender: function (data, type, full) {
          return '<a href="' + Urls.ui.freezerMaps.view(data) + '">' + data + "</a>";
        },
      },
      {
        sTitle: "Description",
        mData: "description",
        include: true,
        iSortPriority: 0,
      },
    ];
  },
};
