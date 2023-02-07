var Transfer = (function ($) {
  var itemsListId = "listItems";
  var notificationsListId = "listNotifications";

  var form = null;
  var itemsListConfig = {};
  var notificationsListConfig = {};

  return {
    setForm: function (formApi) {
      form = formApi;
    },

    setItemsListConfig: function (config) {
      itemsListConfig = config;
    },

    setItems: function (items) {
      FormUtils.setTableData(ListTarget.transferitem, itemsListConfig, itemsListId, items, form);
      updateBulkActions(items);
    },

    getItems: function () {
      return FormUtils.getTableData(itemsListId);
    },

    addItems: function (addItems) {
      var items = Transfer.getItems().concat(addItems);
      Transfer.setItems(items);
    },

    removeItems: function (removeItems) {
      var items = removeByTypeAndId(Transfer.getItems(), removeItems);
      Transfer.setItems(items);
    },

    updateItems: function (updatedItems) {
      var items = removeByTypeAndId(Transfer.getItems(), updatedItems).concat(updatedItems);
      Transfer.setItems(items);
    },

    setNotificationsListConfig: function (config) {
      notificationsListConfig = config;
    },

    setNotifications: function (notifications) {
      FormUtils.setTableData(
        ListTarget.transfernotification,
        notificationsListConfig,
        notificationsListId,
        notifications,
        null
      );
    },

    getNotifications: function () {
      return FormUtils.getTableData(notificationsListId);
    },

    addNotification: function (notification) {
      var notifications = Transfer.getNotifications();
      notifications.push(notification);
      Transfer.setNotifications(notifications);
    },
  };

  function removeByTypeAndId(allItems, removeItems) {
    return allItems.filter(function (transferItem) {
      return !removeItems.some(function (removeItem) {
        return removeItem.type === transferItem.type && removeItem.id === transferItem.id;
      });
    });
  }

  function updateBulkActions(items) {
    if (!items || !items.length) {
      setNoBulkActionsMessage("Add items to transfer to see bulk actions.");
      return;
    }

    var itemTypes = Utils.array.deduplicateString(
      items.map(function (item) {
        return item.type;
      })
    );

    if (itemTypes.length > 1) {
      setNoBulkActionsMessage("Bulk actions not available for multi-type transfers.");
      return;
    }

    switch (itemTypes[0]) {
      case "Sample":
        setBulkActions(BulkTarget.sample.getBulkActions({}));
        break;
      case "Library":
        setBulkActions(BulkTarget.library.getBulkActions({}));
        break;
      case "Library Aliquot":
        setBulkActions(BulkTarget.libraryaliquot.getBulkActions({}));
        break;
      case "Pool":
        setBulkActions(BulkTarget.pool.getBulkActions({}));
        break;
      default:
        throw new Error("Unexpected item type: " + itemTypes[0]);
    }
  }

  function setNoBulkActionsMessage(message) {
    setToolbar($("<span>").text(message));
  }

  function setBulkActions(actions) {
    var getItems = function () {
      return FormUtils.getSelectedTableData(itemsListId);
    };

    setToolbar(
      actions
        .filter(function (action) {
          return !!action;
        })
        .map(function (action) {
          return Utils.ui.makeBulkActionButton(action, getItems);
        })
    );
  }

  function setToolbar(items) {
    $("#type-toolbar").remove();
    var toolbar = $("<div>").prop("id", "type-toolbar");
    toolbar.append(items);
    $("#listItems > .ui-toolbar").append(toolbar);
  }
})(jQuery);
