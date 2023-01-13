var PaneTarget = {};

var Pane = (function () {
  var removeChildren = function (div) {
    while (div.hasChildNodes()) {
      div.removeChild(div.lastChild);
    }
  };

  var initPaneDiv = function (div) {
    div.setAttribute("class", "dashboard_widget");
    removeChildren(div);

    return div;
  };

  var createTitleDiv = function (title) {
    var titleDiv = document.createElement("DIV");

    titleDiv.setAttribute("class", "widget_title ui-corner-top");
    titleDiv.innerText = title;

    return titleDiv;
  };

  var createInputDiv = function () {
    var input = document.createElement("INPUT");

    input.setAttribute("type", "text");
    input.setAttribute("size", "20");

    return input;
  };

  var createTitleDivWithSearch = function (title) {
    var titleDiv = createTitleDiv(title);
    var inputDiv = createInputDiv();

    titleDiv.append(inputDiv);

    return {
      title: titleDiv,
      input: inputDiv,
    };
  };

  var createContentDiv = function () {
    var content = document.createElement("DIV");

    content.setAttribute("class", "widget ui-corner-bottom");

    return content;
  };

  var assemblePane = function (paneId, titleDiv, contentDiv) {
    var div = initPaneDiv(document.getElementById(paneId));

    div.appendChild(titleDiv);
    div.appendChild(contentDiv);
  };

  var updateDiv = function (div, inner) {
    removeChildren(div);
    div.appendChild(inner);
  };

  var createTilesDiv = function (items, transform, add) {
    var innerContent = document.createElement("DIV");

    items.map(transform).forEach(function (item) {
      if (item) {
        innerContent.appendChild(item);
      }
    });

    add.forEach(function (item) {
      if (item) {
        innerContent.appendChild(item);
      }
    });

    return innerContent;
  };

  var createErrorMessage = function (xhr, textStatus, errorThrown) {
    var errorMessage = document.createElement("P");

    try {
      var responseObj = JSON.parse(xhr.responseText);
      if (responseObj.detail) {
        errorMessage.innerText = responseObj.detail;
      }
    } catch (e) {
      errorMessage.innerText = errorThrown;
    }

    return errorMessage;
  };

  var showLoader = function (div) {
    var loader = document.createElement("IMG");

    loader.src = "/styles/images/ajax-loader.gif";
    updateDiv(div, loader);
  };

  var showResults = function (results, transform, div, add) {
    updateDiv(div, createTilesDiv(results, transform, add));
  };

  var showError = function (xhr, textStatus, errorThrown, div) {
    updateDiv(div, createErrorMessage(xhr, textStatus, errorThrown));
  };

  // Make a GET request to the given url with an optional query
  var ajaxCall = function (onLoad, onSuccess, onError, url, query) {
    var queryUrl = encodeURI(
      query === undefined ? url : url + "/?" + Utils.page.param({ q: query })
    );

    onLoad();
    jQuery.ajax({
      dataType: "json",
      type: "GET",
      url: queryUrl,
      contentType: "application/json; charset=utf8",
      success: function (entities) {
        onSuccess(entities);
      },
      error: function (xhr, textStatus, errorThrown) {
        onError(xhr, textStatus, errorThrown);
      },
    });
  };

  return {
    updateDiv: updateDiv,
    setFocusOnReady: function (div) {
      jQuery(document).ready(function () {
        div.focus();
      });
    },
    createSearchPane: function (paneId, title) {
      var titleDivs = createTitleDivWithSearch(title);
      var contentDiv = createContentDiv();

      assemblePane(paneId, titleDivs.title, contentDiv);

      return {
        title: titleDivs.title,
        input: titleDivs.input,
        content: contentDiv,
      };
    },

    createPane: function (paneId, title) {
      var titleDiv = createTitleDiv(title);
      var contentDiv = createContentDiv();

      assemblePane(paneId, titleDiv, contentDiv);

      return {
        title: titleDiv,
        content: contentDiv,
      };
    },

    // add specifies any special tiles to be added to the bottom of the pane
    updateTiles: function (div, transform, url, query, add, checkWorkflow) {
      var workflow = null;
      if (checkWorkflow) {
        workflow = Constants.workflows.find(function (wkflow) {
          return wkflow.barcode == query;
        });
        if (workflow) {
          window.location = window.location.origin + "/miso/workflow/new/" + workflow.workflowName;
          return;
        }
      }
      ajaxCall(
        function () {
          showLoader(div);
        },
        function (results) {
          showResults(results, transform, div, add);
        },
        function (xhr, textStatus, errorThrown) {
          showError(xhr, textStatus, errorThrown, div);
        },
        url,
        query
      );
    },
    registerSearchHandlers: function (inputTag, transform, url, outputDiv, checkWorkflow) {
      var doSearch = function () {
        Pane.updateTiles(outputDiv, transform, url, inputTag.value, [], checkWorkflow);
      };

      // Wait 100 ms before doing the search to allow the paste buffer to copy into the input field
      inputTag.onpaste = function () {
        setTimeout(doSearch, 100);
      };
      inputTag.onkeyup = function (e) {
        if (e.which == 13) {
          // Enter
          doSearch();
        }
      };
    },
  };
})();
