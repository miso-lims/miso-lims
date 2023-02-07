ListTarget.transfernotification = (function ($) {
  /*
   * Expected config: {
   *   transferId: int
   * }
   */

  return {
    name: "Transfer Notifications",
    createUrl: function (config, projectId) {
      throw new Error("Must be provided statically");
    },
    createBulkActions: function (config, projectId) {
      return [
        {
          name: "Cancel",
          action: function (items) {
            if (
              items.some(function (item) {
                return typeof item.sendStatus === "boolean";
              })
            ) {
              Utils.showOkDialog("Error", [
                "Cannot cancel notifications that have already been sent",
              ]);
              return;
            }
            var ids = items.map(function (item) {
              return item.id;
            });
            Utils.ajaxWithDialog(
              "Cancelling...",
              "POST",
              Urls.rest.transfers.bulkDeleteNotifications(config.transferId),
              ids,
              function () {
                var notifications = Transfer.getNotifications().filter(function (notification) {
                  return ids.indexOf(notification.id) === -1;
                });
                Transfer.setNotifications(notifications);
                Utils.showOkDialog("Cancelled", [
                  "Notification" + (ids.length > 1 ? "s" : "") + " cancelled.",
                ]);
              }
            );
          },
        },
        {
          name: "Resend",
          action: function (items) {
            if (items.length > 1) {
              Utils.showOkDialog("Error", ["Only one notification can be resent at a time."]);
              return;
            }
            Utils.ajaxWithDialog(
              "Submitting...",
              "POST",
              Urls.rest.transfers.resendNotification(config.transferId, items[0].id),
              null,
              function (data) {
                Transfer.addNotification(data);
              }
            );
          },
        },
      ];
    },
    createStaticActions: function (config, projectId) {
      return [
        {
          name: "Add",
          handler: function () {
            Contacts.selectContactDialog(true, true, function (contact) {
              confirmNotification(contact, config);
            });
          },
        },
      ];
    },
    createColumns: function (config, projectId) {
      return [
        {
          sTitle: "Sender",
          mData: "senderName",
        },
        {
          sTitle: "Recipient",
          mData: "recipientName",
        },
        {
          sTitle: "Address",
          mData: "recipientEmail",
        },
        {
          sTitle: "Status",
          mData: "sendSuccess",
          mRender: function (data, type, full) {
            if (data === true) {
              return "Sent";
            } else if (data === false) {
              return "Failed";
            } else {
              return "Pending";
            }
          },
        },
      ];
    },
  };

  function confirmNotification(contact, config) {
    Utils.showConfirmDialog(
      "Send Notification",
      "Accept and send",
      [
        "By choosing to proceed, you acknowledge that " +
          "details of the items included in this transfer will be sent to " +
          Contacts.makeContactLabel(contact.name, contact.email) +
          ", and accept responsibility for the transmission.",
      ],
      function () {
        var notification = {
          recipientName: contact.name,
          recipientEmail: contact.email,
        };
        var url =
          Urls.rest.transfers.addNotification(config.transferId) +
          "?" +
          Utils.page.param({
            saveContact: !!contact.save,
          });
        Utils.ajaxWithDialog("Adding Notification", "POST", url, notification, function (data) {
          Transfer.addNotification(data);
        });
      }
    );
  }
})(jQuery);
