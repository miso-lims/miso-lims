var PoolOrder = (function ($) {
  var containerSelector = "#listAliquotsContainer";
  var listId = "listAliquots";
  var listSelector = "#" + listId;

  var aliquotListInitialized = false;
  var form = null;

  function computeDuplicates(aliquots) {
    Utils.ajaxWithDialog(
      "Checking duplicates",
      "POST",
      Urls.rest.poolOrders.indexChecker,
      aliquots.map(Utils.array.getId),
      function (response) {
        PoolOrder.setAliquots(aliquots, response.duplicateIndices, response.nearDuplicateIndices);
      }
    );
  }

  return {
    setForm: function (formApi) {
      form = formApi;
    },

    getAliquots: function () {
      return $(listSelector).dataTable().fnGetData();
    },

    setAliquots: function (orderAliquots, duplicateSequences, nearDuplicateIndices) {
      if (aliquotListInitialized) {
        form.markOtherChanges();
        $(listSelector).dataTable().fnDestroy();
        $(containerSelector).empty();
        ListState[listId] = null;
      }
      $(containerSelector).append(
        $("<table>").attr("id", listId).addClass("display no-border ui-widget-content")
      );
      ListUtils.createStaticTable(
        listId,
        ListTarget.orderaliquot,
        {
          duplicateSequences: duplicateSequences,
          nearDuplicateIndices: nearDuplicateIndices,
        },
        orderAliquots
      );
      aliquotListInitialized = true;
    },

    addAliquots: function (orderAliquots) {
      var aliquots = PoolOrder.getAliquots().concat(orderAliquots);
      computeDuplicates(aliquots);
    },

    removeAliquots: function (aliquotIds) {
      var aliquots = PoolOrder.getAliquots().filter(function (orderAli) {
        return aliquotIds.indexOf(orderAli.aliquot.id) === -1;
      });
      computeDuplicates(aliquots);
    },
  };
})(jQuery);
