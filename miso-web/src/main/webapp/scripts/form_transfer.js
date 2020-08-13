if (typeof FormTarget === 'undefined') {
  FormTarget = {};
}
FormTarget.transfer = (function($) {

  /*
   * Expected config {
   *   pageMode: string ('create' or 'edit')
   *   editSend: boolean,
   *   editReceipt: boolean,
   *   senderGroups: array,
   *   recipientGroups: array
   * }
   */

  return {
    getUserManualUrl: function() {
      return Urls.external.userManual('transfers');
    },
    getSaveUrl: function(model) {
      return model.id ? Urls.rest.transfers.update(model.id) : Urls.rest.transfers.create;
    },
    getSaveMethod: function(transfer) {
      return transfer.id ? 'PUT' : 'POST';
    },
    getEditUrl: function(transfer) {
      return Urls.ui.transfers.edit(transfer.id);
    },
    getSections: function(config, object) {
      return [{
        title: 'Transfer Information',
        fields: [{
          title: 'Transfer ID',
          data: 'id',
          type: 'read-only',
          getDisplayValue: function(transfer) {
            return transfer.id || 'Unsaved';
          }
        }, {
          title: 'Transfer Request Name',
          data: 'transferRequestName',
          type: 'text',
          maxLength: 100,
          description: 'Name or ID of a transfer request stored in a separate system'
        }, {
          title: 'Transfer Time',
          data: 'transferTime',
          type: 'datetime',
          required: true,
          include: config.editSend || config.editReceipt,
          initial: Utils.getCurrentDatetime()
        }, {
          title: 'Transfer Time',
          data: 'transferTime',
          type: 'read-only',
          include: !config.editSend && !config.editReceipt
        }, {
          title: 'Sender Lab (External)',
          data: 'senderLabId',
          type: 'dropdown',
          source: Constants.labs.filter(function(lab) {
            return lab.id === object.senderLabId || (!lab.archived && !lab.instituteArchived)
          }),
          getItemLabel: function(item) {
            return item.label;
          },
          getItemValue: Utils.array.getId,
          sortSource: Utils.sorting.standardSort('label'),
          include: config.editSend && object.senderLabId,
          required: true
        }, {
          title: 'Sender Lab (External)',
          data: 'senderLabLabel',
          type: 'read-only',
          include: !config.editSend && object.senderLabLabel
        }, {
          title: 'Sender Group (Internal)',
          data: 'senderGroupId',
          type: 'dropdown',
          source: config.senderGroups,
          getItemLabel: Utils.array.getName,
          getItemValue: Utils.array.getId,
          sortSource: Utils.sorting.standardSort('name'),
          include: config.editSend && (config.pageMode === 'create' || object.senderGroupId),
          required: true
        }, {
          title: 'Sender Group (Internal)',
          data: 'senderGroupName',
          type: 'read-only',
          include: !config.editSend && object.senderGroupName
        }, {
          title: 'Recipient (External)',
          data: 'recipient',
          type: 'text',
          maxLength: 255,
          include: config.editSend && (config.pageMode === 'create' || object.recipient),
          required: !!object.recipient,
          onChange: function(newValue, form) {
            if (config.pageMode == 'create') {
              form.updateField('recipientGroupId', {
                disabled: !!newValue
              });
            }
          }
        }, {
          title: 'Recipient (External)',
          data: 'recipient',
          type: 'read-only',
          include: !config.editSend && object.recipient
        }, {
          title: 'Recipient Group (Internal)',
          data: 'recipientGroupId',
          type: 'dropdown',
          source: config.recipientGroups,
          getItemLabel: Utils.array.getName,
          getItemValue: Utils.array.getId,
          sortSource: Utils.sorting.standardSort('name'),
          include: config.editSend && (config.pageMode === 'create' || object.recipientGroupId),
          required: object.recipientGroupId,
          onChange: function(newValue, form) {
            if (config.pageMode == 'create') {
              form.updateField('recipient', {
                disabled: !!newValue
              });
            }
          }
        }, {
          title: 'Recipient Group (Internal)',
          data: 'recipientGroupName',
          type: 'read-only',
          include: !config.editSend && object.recipientGroupName
        }]
      }];
    },
    confirmSave: function(object, saveCallback) {
      object.items = Transfer.getItems();
      saveCallback();
    }
  }

})(jQuery);
