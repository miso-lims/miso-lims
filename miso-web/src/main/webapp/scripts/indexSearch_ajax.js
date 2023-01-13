(function (IndexSearch, $, undefined) {
  // NOSONAR (paranoid assurance that undefined is undefined)

  IndexSearch.search = function () {
    var searchData = {};
    searchData.position1Indices = getIndicesInput(1);
    searchData.position2Indices = getIndicesInput(2);

    if (!searchData.position1Indices.length && !searchData.position2Indices.length) {
      showError("No index sequences entered");
    } else {
      clearResults();

      Utils.ajaxWithDialog(
        "Index Search",
        "POST",
        "/miso/rest/indices/search",
        searchData,
        function (data) {
          var results = "";
          if (!data || !data.length) {
            results += "No input sequences found in any index families";
          } else {
            var sequenceCount =
              searchData.position1Indices.length + searchData.position2Indices.length;
            data
              .sort(function (a, b) {
                var aMatches = a.position1Matches + a.position2Matches;
                var bMatches = b.position1Matches + b.position2Matches;
                return bMatches - aMatches;
              })
              .forEach(function (result) {
                results +=
                  result.indexFamily +
                  ": " +
                  (result.position1Matches + result.position2Matches) +
                  "/" +
                  sequenceCount +
                  "\n";
              });
          }
          $("#results").css("color", "black");
          $("#results").val(results);
        },
        function (response, textStatus, serverStatus) {
          var message = "Error: ";
          if (response && response.responseText && response.responseText.detail) {
            message += response.responseText.detail;
          } else {
            message += "Search failed";
          }
          showError(message);
        }
      );
    }
  };

  IndexSearch.clearForm = function () {
    $("#indices").val("");
    clearResults();
  };

  function getIndicesInput(pos) {
    return $("#indices" + pos)
      .val()
      .split("\n")
      .filter(function (val) {
        return val;
      });
  }

  function clearResults() {
    $("#results").val("");
  }

  function showError(message) {
    $("#results").css("color", "red");
    $("#results").val(message);
  }
})((window.IndexSearch = window.IndexSearch || {}), jQuery);
