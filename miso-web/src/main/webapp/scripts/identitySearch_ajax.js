(function (IdentitySearch, $, undefined) {
  // NOSONAR (paranoid assurance that undefined is undefined)

  IdentitySearch.lookup = function (exactMatch) {
    var data = getExternalNamesInput();
    if (!data || data.length == 0) {
      Utils.showOkDialog("Search error", ["Enter at least one external name"]);
    } else {
      IdentitySearch.clearResults();
      jQuery("#searchButton").prop("disabled", true);
      jQuery("#ajaxLoaderDiv").empty();
      jQuery("#ajaxLoaderDiv").html('<img src="/styles/images/ajax-loader.gif"/>');

      $.ajax({
        url:
          Urls.rest.samples.identitiesLookup +
          "?" +
          Utils.page.param({
            exactMatch: exactMatch,
          }),
        type: "POST",
        dataType: "json",
        contentType: "application/json; charset=utf8",
        data: JSON.stringify({
          identitiesSearches: data,
          project: $("#projectAlias").val(),
        }),
      })
        .done(function (results) {
          updateResults(results);
          jQuery("#searchButton").prop("disabled", false);
          jQuery("#ajaxLoaderDiv").empty();
        })
        .fail(function (data) {
          jQuery("#searchButton").prop("disabled", false);
          jQuery("#ajaxLoaderDiv").empty();
          jQuery("#ajaxLoaderDiv").html("Error getting samples: " + data);
        });
    }
  };

  IdentitySearch.clearForm = function () {
    $("#externalNames").val("");
    IdentitySearch.clearResults();
  };

  IdentitySearch.clearResults = function () {
    updateResults([]);
  };

  function updateResults(results) {
    FormUtils.setTableData(
      ListTarget.identitysearch,
      {
        identitySearch: true,
      },
      "listResults",
      results
    );
  }

  function getExternalNamesInput() {
    return $("#externalNames")
      .val()
      .split("\n")
      .filter(function (val) {
        return val;
      });
  }
})((window.IdentitySearch = window.IdentitySearch || {}), jQuery);
