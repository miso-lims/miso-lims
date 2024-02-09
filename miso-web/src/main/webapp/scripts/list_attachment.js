ListTarget.attachment = (function () {
  return {
    name: "Attachments",
    createUrl: function (config, projectId) {
      throw new Error("Static data only");
    },
    createBulkActions: function (config, projectId) {
      return [];
    },
    createStaticActions: function (config, projectId) {
      return [
        {
          name: "Upload",
          handler: !config.projectId
            ? function () {
                ListTarget.attachment.showUploadDialog(config.entityType, config.entityId, true);
              }
            : function () {
                Utils.showWizardDialog("Attach Files", [
                  {
                    name: "Upload new files",
                    handler: function () {
                      ListTarget.attachment.showUploadDialog(
                        config.entityType,
                        config.entityId,
                        true
                      );
                    },
                  },
                  {
                    name: "Link project file",
                    handler: function () {
                      ListTarget.attachment.showLinkDialog(
                        config.entityType,
                        config.entityId,
                        config.projectId,
                        true
                      );
                    },
                  },
                ]);
              },
        },
      ];
    },
    createColumns: function (config) {
      return [
        {
          sTitle: "Filename",
          mData: "filename",
          include: true,
          iSortPriority: 1,
          mRender: function (data, type, full) {
            if (type === "display") {
              return (
                '<a href="/miso/attachments/' +
                config.entityType +
                "/" +
                config.entityId +
                "/" +
                full.id +
                '">' +
                data +
                "</a>"
              );
            }
            return data;
          },
        },
        {
          sTitle: "Category",
          mData: "category",
          include: true,
          iSortPriority: 0,
          mRender: function (data, type, full) {
            return data ? data : "Misc.";
          },
        },
        {
          sTitle: "Uploaded By",
          mData: "creator",
          include: true,
          iSortPriority: 0,
        },
        {
          sTitle: "Upload Date",
          mData: "created",
          include: true,
          iSortPriority: 0,
        },
        {
          sTitle: "Delete",
          mData: null,
          include: true,
          iSortPriority: 0,
          mRender: function (data, type, full) {
            if (type === "display") {
              return (
                '<div class="misoicon" onclick="ListTarget.attachment.deleteFile(\'' +
                config.entityType +
                "', " +
                config.entityId +
                ", " +
                full.id +
                ", '" +
                full.filename +
                '\')"><span class="ui-icon ui-icon-trash"></span></div>'
              );
            }
            return "";
          },
        },
      ];
    },
    deleteFile: function (entityType, entityId, attachmentId, filename) {
      Utils.showConfirmDialog(
        "Delete file",
        "Delete",
        ["Are you sure you wish to delete " + filename + "? This cannot be undone."],
        function () {
          var url = "/miso/rest/attachments/" + entityType + "/" + entityId + "/" + attachmentId;
          Utils.ajaxWithDialog("Deleting file", "DELETE", url, null, function () {
            Utils.page.pageReload();
          });
        }
      );
    },
    showUploadDialog: function (entityType, entityId, reloadPage, sharedIds) {
      var dialogArea = jQuery("#dialog");
      dialogArea.empty();

      var form = jQuery('<form id="uploadForm">');
      form.append(
        jQuery('<p><input id="fileInput" type="file" name="files" multiple="multiple"></p>')
      );
      var select = jQuery('<select id="attachmentCategory" type="text" name="attachmentCategory">');
      [
        {
          id: 0,
          alias: "Misc.",
        },
      ]
        .concat(Constants.attachmentCategories)
        .forEach(function (category) {
          select.append(
            jQuery('<option value="' + category.id + '">' + category.alias + "</option>")
          );
        });
      var p = jQuery("<p>")
        .append(jQuery('<label for="attachmentCategory">Category:</label>'))
        .append(select);
      form.append(p);
      dialogArea.append(form);

      var dialog = dialogArea.dialog({
        autoOpen: true,
        width: 500,
        title: "Attach Files",
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
              var params = {};
              if (jQuery("#attachmentCategory").val() > 0) {
                params.categoryId = jQuery("#attachmentCategory").val();
              }
              if (sharedIds) {
                params.entityIds = sharedIds;
              }
              var url =
                "/miso/attachments/" + entityType + "/" + entityId + "?" + Utils.page.param(params);
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
                  jQuery(this).parent().children().children(".ui-dialog-titlebar-close").hide();
                },
              });

              jQuery
                .ajax({
                  url: url,
                  type: "POST",
                  data: formData,
                  cache: false,
                  contentType: false,
                  processData: false,
                })
                .done(function (data) {
                  Utils.showOkDialog(
                    "Attach Files",
                    ["File upload successful"],
                    reloadPage ? Utils.page.pageReload : null
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
    showLinkDialog: function (entityType, entityId, projectId, reloadPage, sharedIds) {
      var url = Urls.rest.projects.attachments(projectId);
      Utils.ajaxWithDialog("Retrieving Project Files", "GET", url, null, function (attachments) {
        if (!attachments.length) {
          Utils.showOkDialog("Link Project File", ["Project has no attachments to link."]);
          return;
        }

        var attachmentIds = attachments.map(Utils.array.getId);
        Utils.showDialog(
          "Link Project File",
          "Link",
          [
            {
              label: "File",
              type: "select",
              required: true,
              values: attachmentIds,
              getLabel: function (value) {
                return Utils.array.findUniqueOrThrow(Utils.array.idPredicate(value), attachments)
                  .filename;
              },
              property: "attachmentId",
            },
          ],
          function (output) {
            var params = {
              fromEntityType: "project",
              fromEntityId: projectId,
              attachmentId: output.attachmentId,
            };
            if (sharedIds) {
              params.entityIds = sharedIds;
            }
            var url =
              "/miso/rest/attachments/" +
              entityType +
              "/" +
              entityId +
              "?" +
              Utils.page.param(params);
            Utils.ajaxWithDialog("Linking File", "POST", url, null, function () {
              Utils.showOkDialog(
                "Link Project File",
                ["File link successful"],
                reloadPage ? Utils.page.pageReload : null
              );
            });
          }
        );
      });
    },
  };
})();
