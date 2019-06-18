var PoolOrder = (function($) {

  var containerSelector = '#listAliquotsContainer';
  var listId = 'listAliquots';
  var listSelector = '#' + listId;

  var aliquotListInitialized = false;
  var form = null;

  return {

    setForm: function(formApi) {
      form = formApi;
    },

    getAliquots: function() {
      return $(listSelector).dataTable().fnGetData();
    },

    setAliquots: function(orderAliquots) {
      if (aliquotListInitialized) {
        form.markOtherChanges();
        $(listSelector).dataTable().fnDestroy();
        $(containerSelector).empty();
        ListState[listId] = null;
      }
      $(containerSelector).append($('<table>').attr('id', listId).addClass('display no-border ui-widget-content'));
      ListUtils.createStaticTable(listId, ListTarget.orderaliquot, {}, orderAliquots);
      aliquotListInitialized = true;
    },

    addAliquots: function(orderAliquots) {
      var aliquots = PoolOrder.getAliquots().concat(orderAliquots);
      PoolOrder.setAliquots(aliquots);
    },

    removeAliquots: function(aliquotIds) {
      var aliquots = PoolOrder.getAliquots().filter(function(orderAli) {
        return aliquotIds.indexOf(orderAli.aliquot.id) === -1;
      });
      PoolOrder.setAliquots(aliquots);
    }

  };

})(jQuery);
