var WorkflowDisplay = (function() {
  var display;
  var loadingTag = jQuery("<img src='/styles/images/ajax-loader.gif'>");
  var errorDiv = jQuery("<div id='inputError'></div>");
  var SKIP = "SKIP";

  function ajax(method, url, onSuccess, onError) {
    showLoading();
    jQuery.ajax({
      "type": method,
      "url": url,
      "contentType": "application/json; charset=utf8",
      "dataType": "json",
      "success": onSuccess,
      "error": onError
    })
  }
  
  function showSuccess() {
    display.empty().append(jQuery("<p>Workflow was successfully completed.</p>"));
  }

  function executeWorkflow(workflowId) {
    ajax("POST", "/miso/rest/workflow/" + workflowId + "/execute", showSuccess, showError);
  }

  function showError(xhr) {
    loadingTag.remove();
    display.children().show();
    display.children("input").focus();
    errorDiv.text(parseError(xhr));
  }

  function parseError(xhr) {
    try {
      var responseObj = JSON.parse(xhr.responseText);
      if (responseObj.detail && responseObj.dataFormat === "validation") {
        return responseObj["data"]["GENERAL"];
      }

      return responseObj["message"];
    } catch (err) {
      return "Internal Server Error";
    }
  }

  function processInput(workflowId, stepNumber, input) {
    ajax("POST", encodeURI("/miso/rest/workflow/" + workflowId + "/step/" + stepNumber + "/?" + jQuery.param({
      input: input
    })), updateDisplay, showError);
  }

  function makeMessageTag(message) {
    return jQuery("<p>" + message + "</p>");
  }

  function showLoading() {
    // Hide children in case the request fails and we need to show them later
    display.children().hide();
    display.append(loadingTag);
  }

  function makeInputTag(workflowId, stepNumber) {
    var inputTag = jQuery("<input type='text'>");

    var doProcess = function() {
      processInput(workflowId, stepNumber, inputTag.val())
    };
    inputTag.keypress(function(e) {
      if (e.which === 13) {
        doProcess();
      }
    }).bind("paste", function() {
      setTimeout(doProcess, 100);
    });

    return inputTag;
  }

  function changeStep(workflowId, stepNumber) {
    ajax("GET", "/miso/rest/workflow/" + workflowId + "/step/" + stepNumber, updateDisplay, showError);
  }

  function cancelInput(workflowId) {
    ajax("DELETE", "/miso/rest/workflow/" + workflowId + "/step/latest", updateDisplay, showError);
  }

  function makeArrowCell(entryStepNumber, currentStepNumber) {
    return entryStepNumber === currentStepNumber ? jQuery("<td>").append(jQuery("<img src='/styles/images/arrow.svg' class='logIcon'>"))
        : jQuery("<td>");
  }

  function makeModifyCell(entryStepNumber, numEntries) {
    return entryStepNumber === numEntries - 1 ? jQuery("<td>").append(jQuery("<img src='/styles/images/cancel.png' class='logIcon'>"))
        : jQuery("<td>").append(jQuery("<img src='/styles/images/redo.svg' class='logIcon'>"));
  }

  function makeLogEntryClickHandler(entryStepNumber, numEntries, workflowId) {
    return function() {
      entryStepNumber === numEntries - 1 ? cancelInput(workflowId) : changeStep(workflowId, entryStepNumber)
    };
  }

  function makeLog(logEntries, workflowId, currentStepNumber) {
    var table = jQuery("<table class='workflowLogTable'>");

    for (var i = 0; i < logEntries.length; i++) {
      table.prepend(jQuery("<tr>").append(
          [makeArrowCell(i, currentStepNumber), jQuery("<td>").text(logEntries[i]), jQuery("<td>"), makeModifyCell(i, logEntries.length)])
          .click(makeLogEntryClickHandler(i, logEntries.length, workflowId)));
    }

    return table;
  }

  function makeExecuteButton(workflowId) {
    return jQuery("<a class='ui-button ui-state-default'>").text("Confirm").click(function() {
      executeWorkflow(workflowId);
    });
  }

  function showConfirm(workflowId, stepNumber, message, log) {
    display.empty().append([makeMessageTag(message), makeExecuteButton(workflowId), errorDiv, makeLog(log, workflowId, stepNumber)]);
  }

  function continueWorkflow(workflowId) {
    ajax("GET", "/miso/rest/workflow/" + workflowId + "/step/latest", updateDisplay, showError);
  }

  function makeContinueButton(workflowId, complete) {
    return jQuery("<a class='ui-button ui-state-default'>").text(complete ? "Finish editing" : "Go to current step").click(function() {
      continueWorkflow(workflowId);
    });
  }

  function makeSkipButton(workflowId, stepNumber) {
    return jQuery("<a class='ui-button ui-state-default'>").text("Skip Partition").click(function() {
      processInput(workflowId, stepNumber, SKIP);
    });
  }

  function showPrompt(workflowId, stepNumber, complete, message, inputTypes, logMessages) {
    var elements = [makeMessageTag(message), makeInputTag(workflowId, stepNumber), errorDiv];
    if (inputTypes.indexOf(SKIP) > -1) {
      elements.push(makeSkipButton(workflowId, stepNumber));
    }
    if (stepNumber < logMessages.length) {
      elements.push(makeContinueButton(workflowId, complete));
    }
    elements.push(makeLog(logMessages, workflowId, stepNumber));

    display.empty().append(elements).children("input").focus();
  }

  function initDisplay(divId) {
    display = jQuery("#" + divId);
  }

  function updateDisplay(state) {
    if (Number.isInteger(state["stepNumber"])) {
      showPrompt(state["workflowId"], state["stepNumber"], state["complete"], state["message"], state["inputTypes"], state["log"]);
    } else {
      showConfirm(state["workflowId"], state["stepNumber"], state["message"], state["log"]);
    }
  }

  return {
    init: function(divId, state) {
      initDisplay(divId);
      updateDisplay(state);
    }
  }
})();