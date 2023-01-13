var IndexFamily = (function ($) {
  var indicesContainerSelector = "#listIndicesContainer";
  var indicesListId = "listIndices";
  var indicesListSelector = "#" + indicesListId;

  var isAdmin = false;

  return {
    setAdmin: function (admin) {
      isAdmin = admin;
    },

    setIndices: function (indices) {
      $(indicesContainerSelector).append(
        $("<table>").attr("id", indicesListId).addClass("display no-border ui-widget-content")
      );
      ListUtils.createStaticTable(
        indicesListId,
        ListTarget.index,
        {
          isAdmin: isAdmin,
          indexFamilyId: $("#indexFamilyForm_id").val(),
        },
        indices
      );
    },
  };
})(jQuery);
