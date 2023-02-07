var Pool = (function ($) {
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
        Pool.setAliquots(aliquots, response.duplicateIndices, response.nearDuplicateIndices);
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

    setAliquots: function (poolAliquots, duplicateIndicesSequences, nearDuplicateIndicesSequences) {
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
        ListTarget.poolelement,
        {
          duplicateIndicesSequences: duplicateIndicesSequences || [],
          nearDuplicateIndicesSequences: nearDuplicateIndicesSequences || [],
        },
        poolAliquots || []
      );
      aliquotListInitialized = true;
    },

    addAliquots: function (poolAliquots) {
      var aliquots = Pool.getAliquots().concat(poolAliquots);
      computeDuplicates(aliquots);
    },

    removeAliquots: function (aliquotIds) {
      var aliquots = Pool.getAliquots().filter(function (poolAli) {
        return aliquotIds.indexOf(poolAli.id) === -1;
      });
      computeDuplicates(aliquots);
    },
  };
})(jQuery);
