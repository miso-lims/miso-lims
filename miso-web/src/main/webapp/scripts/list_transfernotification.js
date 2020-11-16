ListTarget.transfernotification = (function($) {

  /*
   * Expected config: {
   *   transferId: int
   * }
   */

  return {
    name: "Transfer Notifications",
    createUrl: function(config, projectId) {
      throw new Error("Must be provided statically");
    },
    createBulkActions: function(config, projectId) {
      return [
          {
            name: 'Cancel',
            action: function(items) {
              if (items.some(function(item) {
                return typeof item.sendStatus === 'boolean';
              })) {
                Utils.showOkDialog('Error', ['Cannot cancel notifications that have already been sent']);
                return;
              }
              var ids = items.map(function(item) {
                return item.id;
              })
              Utils.ajaxWithDialog('Cancelling...', 'POST', Urls.rest.transfers.bulkDeleteNotifications(config.transferId), ids,
                  function() {
                    var notifications = Transfer.getNotifications().filter(function(notification) {
                      return ids.indexOf(notification.id) === -1;
                    });
                    Transfer.setNotifications(notifications);
                    Utils.showOkDialog('Cancelled', ['Notification' + (ids.length > 1 ? 's' : '') + ' cancelled.']);
                  });
            }
          },
          {
            name: 'Resend',
            action: function(items) {
              if (items.length > 1) {
                Utils.showOkDialog('Error', ['Only one notification can be resent at a time.']);
                return;
              }
              Utils.ajaxWithDialog('Submitting...', 'POST', Urls.rest.transfers.resendNotification(config.transferId, items[0].id), null,
                  function(data) {
                    Transfer.addNotification(data);
                  });
            }
          }];
    },
    createStaticActions: function(config, projectId) {
      return [{
        name: 'Add',
        handler: function() {
          Utils.showWizardDialog('Add Notification', [{
            name: 'Search internal users',
            handler: function() {
              searchInternal(config);
            }
          }, {
            name: 'Search contacts',
            handler: function() {
              searchContacts(config);
            }
          }, {
            name: 'New contact',
            handler: function() {
              newContact(config);
            }
          }]);
        }
      }];
    },
    createColumns: function(config, projectId) {
      return [{
        sTitle: 'Sender',
        mData: 'senderName'
      }, {
        sTitle: 'Recipient',
        mData: 'recipientName'
      }, {
        sTitle: 'Address',
        mData: 'recipientEmail',
      }, {
        sTitle: 'Status',
        mData: 'sendSuccess',
        mRender: function(data, type, full) {
          if (data === true) {
            return 'Sent';
          } else if (data === false) {
            return 'Failed';
          } else {
            return 'Pending';
          }
        }
      }];
    }
  };

  function searchInternal(config) {
    Utils.showDialog('Search Internal Users', 'Search', [{
      label: 'Name',
      property: 'q',
      type: 'text',
      required: true
    }], function(results) {
      doSearch(Urls.rest.users.search, results.q, config);
    });
  }

  function searchContacts(config) {
    Utils.showDialog('Search Contacts', 'Search', [{
      label: 'Name',
      property: 'q',
      type: 'text',
      required: true
    }], function(results) {
      doSearch(Urls.rest.contacts.search, results.q, config);
    });
  }

  function doSearch(url, query, config) {
    Utils.ajaxWithDialog('Searching', 'GET', url + '?' + $.param({
      q: query
    }), null, function(data) {
      var options = data.map(function(item) {
        return {
          name: item.name + ' <' + item.email + '>',
          handler: function() {
            confirmNotification(item, config);
          }
        };
      });
      options.push({
        name: 'New contact',
        handler: newContact
      });
      Utils.showWizardDialog(options.length > 1 ? 'Select Contact' : 'No Results', options);
    });
  }

  function newContact(config) {
    Utils.showDialog('New Contact', 'Continue', [{
      label: 'Name',
      property: 'name',
      type: 'text',
      required: true
    }, {
      label: 'Email',
      property: 'email',
      type: 'text',
      regex: Utils.validation.emailRegex,
      required: true
    }, {
      label: 'Save contact',
      property: 'save',
      type: 'checkbox'
    }], function(results) {
      confirmNotification(results, config);
    });
  }

  function confirmNotification(contact, config) {
    Utils.showConfirmDialog('Send Notification', 'Accept and send', ['By choosing to proceed, you acknowledge that '
        + 'details of the items included in this transfer will be sent to ' + contact.name + ' <' + contact.email
        + '>, and accept responsibility for the transmission.'], function() {
      var notification = {
        recipientName: contact.name,
        recipientEmail: contact.email
      };
      var url = Urls.rest.transfers.addNotification(config.transferId) + '?' + $.param({
        saveContact: !!contact.save
      });
      Utils.ajaxWithDialog('Adding Notification', 'POST', url, notification, function(data) {
        Transfer.addNotification(data);
      });
    });
  }

})(jQuery);
