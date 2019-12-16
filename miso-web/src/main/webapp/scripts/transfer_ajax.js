var Transfer = (function($) {

  var itemsListId = 'listItems';

  var form = null;
  var itemsListConfig = {};

  return {

    setForm: function(formApi) {
      form = formApi;
    },

    setItemsListConfig: function(config) {
      itemsListConfig = config;
    },

    setItems: function(items) {
      FormUtils.setTableData(ListTarget.transferitem, itemsListConfig, itemsListId, items, form);
    },

    getItems: function() {
      return FormUtils.getTableData(itemsListId);
    },

    addItems: function(addItems) {
      var items = Transfer.getItems().concat(addItems);
      Transfer.setItems(items);
    },

    removeItems: function(removeItems) {
      var items = removeByTypeAndId(Transfer.getItems(), removeItems);
      Transfer.setItems(items);
    },

    updateItems: function(updatedItems) {
      var items = removeByTypeAndId(Transfer.getItems(), updatedItems).concat(updatedItems);
      Transfer.setItems(items);
    }

  };

  function removeByTypeAndId(allItems, removeItems) {
    return allItems.filter(function(transferItem) {
      return !removeItems.some(function(removeItem) {
        return removeItem.type === transferItem.type && removeItem.id === transferItem.id;
      });
    });
  }

})(jQuery);