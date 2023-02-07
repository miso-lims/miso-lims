if (typeof FormTarget === "undefined") {
  FormTarget = {};
}
FormTarget.transfer = (function ($) {
  /*
   * Expected config {
   *   pageMode: string ('create' or 'edit')
   *   editSend: boolean,
   *   editReceipt: boolean,
   *   senderGroups: array,
   *   recipientGroups: array
   * }
   */

  var descriptions = {
    senderLab: "External lab from which the items were received. Applicable to receipt transfers.",
    senderGroup:
      "Internal group from whom the items are being sent. Applicable to internal and distribution transfers.",
    recipient: "External entity who is receiving the items. Applicable to distribution transfers.",
    recipientGroup:
      "Internal group that is receiving the items. Applicable to receipt and internal transfers.",
  };

  return {
    getUserManualUrl: function () {
      return Urls.external.userManual("transfers");
    },
    getSaveUrl: function (model) {
      return model.id ? Urls.rest.transfers.update(model.id) : Urls.rest.transfers.create;
    },
    getSaveMethod: function (transfer) {
      return transfer.id ? "PUT" : "POST";
    },
    getEditUrl: function (transfer) {
      return Urls.ui.transfers.edit(transfer.id);
    },
    getSections: function (config, object) {
      return [
        {
          title: "Transfer Information",
          fields: [
            {
              title: "Transfer ID",
              data: "id",
              type: "read-only",
              getDisplayValue: function (transfer) {
                return transfer.id || "Unsaved";
              },
            },
            {
              title: "Transfer Request Name",
              data: "transferRequestName",
              type: "text",
              maxLength: 100,
              description: "Name or ID of a transfer request stored in a separate system",
            },
            {
              title: "Transfer Time",
              data: "transferTime",
              type: "datetime",
              required: true,
              include: config.editSend || config.editReceipt,
              initial: Utils.getCurrentDatetime(),
            },
            {
              title: "Transfer Time",
              data: "transferTime",
              type: "read-only",
              include: !config.editSend && !config.editReceipt,
            },
            {
              title: "Sender Lab (External)",
              data: "senderLabId",
              type: "dropdown",
              source: Constants.labs.filter(function (lab) {
                return lab.id === object.senderLabId || !lab.archived;
              }),
              getItemLabel: Utils.array.getAlias,
              getItemValue: Utils.array.getId,
              sortSource: Utils.sorting.standardSort("label"),
              include: config.editSend && (config.pageMode === "create" || object.senderLabId),
              required: !!object.senderLabId,
              onChange: function (newValue, form) {
                if (config.pageMode == "create") {
                  updateFieldRestrictions(form);
                }
              },
              description: descriptions.senderLab,
            },
            {
              title: "Sender Lab (External)",
              data: "senderLabLabel",
              type: "read-only",
              include: !config.editSend && object.senderLabLabel,
              description: descriptions.senderLab,
            },
            {
              title: "Sender Group (Internal)",
              data: "senderGroupId",
              type: "dropdown",
              source: config.senderGroups,
              getItemLabel: Utils.array.getName,
              getItemValue: Utils.array.getId,
              sortSource: Utils.sorting.standardSort("name"),
              include: config.editSend && (config.pageMode === "create" || object.senderGroupId),
              required: !!object.senderGroupId,
              onChange: function (newValue, form) {
                if (config.pageMode == "create") {
                  updateFieldRestrictions(form);
                }
              },
              description: descriptions.senderGroup,
            },
            {
              title: "Sender Group (Internal)",
              data: "senderGroupName",
              type: "read-only",
              include: !config.editSend && object.senderGroupName,
              description: descriptions.senderGroup,
            },
            {
              title: "Recipient (External)",
              data: "recipient",
              type: "text",
              maxLength: 255,
              include: config.editSend && (config.pageMode === "create" || object.recipient),
              required: !!object.recipient,
              onChange: function (newValue, form) {
                if (config.pageMode == "create") {
                  updateFieldRestrictions(form);
                }
              },
              description: descriptions.recipient,
            },
            {
              title: "Recipient (External)",
              data: "recipient",
              type: "read-only",
              include: !config.editSend && object.recipient,
              description: descriptions.recipient,
            },
            {
              title: "Recipient Group (Internal)",
              data: "recipientGroupId",
              type: "dropdown",
              source: config.recipientGroups,
              getItemLabel: Utils.array.getName,
              getItemValue: Utils.array.getId,
              sortSource: Utils.sorting.standardSort("name"),
              include: config.editSend && (config.pageMode === "create" || object.recipientGroupId),
              required: object.recipientGroupId,
              onChange: function (newValue, form) {
                if (config.pageMode == "create") {
                  updateFieldRestrictions(form);
                }
              },
              description: descriptions.recipientGroup,
            },
            {
              title: "Recipient Group (Internal)",
              data: "recipientGroupName",
              type: "read-only",
              include: !config.editSend && object.recipientGroupName,
              description: descriptions.recipientGroup,
            },
          ],
        },
      ];
    },
    confirmSave: function (object) {
      object.items = Transfer.getItems();
    },
  };

  function updateFieldRestrictions(form) {
    var senderLab = form.get("senderLabId");
    var senderGroup = form.get("senderGroupId");
    var recipient = form.get("recipient");
    var recipientGroup = form.get("recipientGroupId");

    if (senderLab) {
      // receipt transfer
      disableField(form, "senderGroupId", true);
      disableField(form, "recipient", true);
      disableField(form, "recipientGroupId", false);
    } else if (recipient) {
      // distribution transfer
      disableField(form, "senderLabId", true);
      disableField(form, "senderGroupId", false);
      disableField(form, "recipientGroupId", true);
    } else if (senderGroup && recipientGroup) {
      // internal transfer
      disableField(form, "senderLabId", true);
      disableField(form, "recipient", true);
    } else if (senderGroup) {
      disableField(form, "senderLabId", true);
      disableField(form, "recipient", false);
      disableField(form, "recipientGroupId", false);
    } else if (recipientGroup) {
      disableField(form, "senderLabId", false);
      disableField(form, "senderGroupId", false);
      disableField(form, "recipient", true);
    } else {
      disableField(form, "senderLabId", false);
      disableField(form, "senderGroupId", false);
      disableField(form, "recipient", false);
      disableField(form, "recipientGroupId", false);
    }
  }

  function disableField(form, field, disable) {
    form.updateField(field, {
      disabled: disable,
    });
  }
})(jQuery);
