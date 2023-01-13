(function (IndexDistance, $, undefined) {
  // NOSONAR (paranoid assurance that undefined is undefined)

  IndexDistance.getBulkIndexActions = function () {
    return [
      {
        name: "Add",
        action: function (items) {
          var indices = getIndicesInput();
          $.each(items, function (index, item) {
            indices.push(item.sequence);
          });
          $("#indices").val(indices.join("\n"));
        },
      },
    ];
  };

  IndexDistance.calculate = function () {
    var indices = getIndicesInput();
    var minDistance = $("#minDistance").val();

    if (!indices.length || indices.length < 2) {
      showError("Enter at least two indices");
    } else if (!/^\d$/.test(minDistance) || minDistance < 1 || minDistance > 4) {
      showError("Enter a minimum distance between 1 and 4");
    } else {
      clearResults();
      var data = {
        indices: indices,
        minimumDistance: minDistance,
      };

      $.ajax({
        url: Urls.rest.indexDistance.check,
        type: "POST",
        dataType: "json",
        contentType: "application/json; charset=utf8",
        data: JSON.stringify(data),
      })
        .done(function (data) {
          var results = "";
          if (Constants.warningEditDistance > Constants.errorEditDistance) {
            results +=
              "Indices with " +
              Constants.warningEditDistance +
              " or fewer mismatches are considered near-matches.\n";
          }
          results +=
            "Indices with " +
            Constants.errorEditDistance +
            " or fewer mismatches are considered DUPLICATES.\n\n";
          results +=
            "Indices were compared at the length of the shortest index, which is " +
            data.shortestIndexLength +
            " bp.\n\n";
          if (!data.collisions || !data.collisions.length) {
            results += "All sequences are at least " + minDistance + " edits apart";
            $("#results").css("color", "green");
            $("#results").val(results);
          } else {
            $.each(data.collisions, function (index, item) {
              results +=
                item.editDistance === 0 ? "Duplicate" : "Near match (" + item.editDistance + ")";
              results += ': "' + item.indices.join('", "') + '"\n';
            });
            showError(results);
          }
        })
        .fail(function (response, textStatus, serverStatus) {
          var message = "Error: ";
          if (response && response.responseText && response.responseText.detail) {
            message += response.responseText.detail;
          } else {
            message += "Calculation failed";
          }
          showError(message);
        });
    }
  };

  IndexDistance.clearForm = function () {
    $("#indices").val("");
    $("#minDistance").val(Math.max(Constants.errorEditDistance, Constants.warningEditDistance) + 1);
    clearResults();
  };

  function clearResults() {
    $("#results").val("");
  }

  function getIndicesInput() {
    return $("#indices")
      .val()
      .split("\n")
      .filter(function (val) {
        return val;
      });
  }

  function showError(message) {
    $("#results").css("color", "red");
    $("#results").val(message);
  }
})((window.IndexDistance = window.IndexDistance || {}), jQuery);
