var WorkflowDisplay = (function () {
  var display;
  var loadingTag = jQuery("<img src='/styles/images/ajax-loader.gif'>");
  var errorDiv = jQuery("<div id='inputError'></div>");
  var SKIP = "SKIP";

  function ajax(method, url, onSuccess, onError) {
    showLoading();
    jQuery.ajax({
      type: method,
      url: url,
      contentType: "application/json; charset=utf8",
      dataType: "json",
      success: onSuccess,
      error: onError,
    });
  }

  function showSuccess() {
    display
      .empty()
      .append(jQuery("<p class='workflowInstruction'>Workflow was successfully completed.</p>"));
  }

  function executeWorkflow(workflowId) {
    ajax("POST", "/miso/rest/workflows/" + workflowId + "/execute", showSuccess, showError);
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
    ajax(
      "POST",
      encodeURI(
        "/miso/rest/workflows/" +
          workflowId +
          "/step/" +
          stepNumber +
          "/?" +
          Utils.page.param({
            input: input,
          })
      ),
      updateDisplay,
      showError
    );
  }

  function makeInstructionTag(message) {
    return jQuery("<p class='workflowInstruction'>" + message + "</p>");
  }

  function showLoading() {
    // Hide children in case the request fails and we need to show them later
    display.children().hide();
    display.append(loadingTag);
  }

  function makeInputTag(workflowId, stepNumber) {
    var inputTag = jQuery("<input type='text'>");

    var doProcess = function () {
      processInput(workflowId, stepNumber, inputTag.val());
    };
    inputTag
      .keypress(function (e) {
        if (e.which === 13) {
          doProcess();
        }
      })
      .bind("paste", function () {
        setTimeout(doProcess, 100);
      });

    return inputTag;
  }

  function changeStep(workflowId, stepNumber) {
    ajax(
      "GET",
      "/miso/rest/workflows/" + workflowId + "/step/" + stepNumber,
      updateDisplay,
      showError
    );
  }

  function cancelInput(workflowId) {
    ajax("DELETE", "/miso/rest/workflows/" + workflowId + "/step/latest", updateDisplay, showError);
  }

  function makeLogIconCell(file, label) {
    var cell = jQuery("<td class='logIconCell'" + (label ? " title='" + label + "'" : "") + ">");
    if (file) {
      cell.append(jQuery("<img src='/styles/images/" + file + "' class='logIcon'>"));
    } else {
      cell.append(jQuery("<div class='logIcon'>"));
    }
    return cell;
  }

  function makeArrowCell(entryStepNumber, currentStepNumber) {
    return entryStepNumber === currentStepNumber
      ? makeLogIconCell("arrow-right.svg")
      : makeLogIconCell();
  }

  function makeActionCell(entryStepNumber, numEntries, workflowId) {
    if (entryStepNumber === numEntries - 1) {
      return makeLogIconCell("arrow-undo.svg", "Undo").click(function () {
        cancelInput(workflowId);
      });
    } else {
      return makeLogIconCell("arrow-back.svg", "Go Back").click(function () {
        changeStep(workflowId, entryStepNumber);
      });
    }
  }

  function makeRow(rowNum, cells) {
    return jQuery("<tr class=" + (rowNum % 2 === 1 ? "odd" : "even") + ">").append(cells);
  }

  function makeLog(logEntries, workflowId, currentStepNumber, complete) {
    var table = jQuery("<table class='workflowLogTable'>");
    var rowNum = 1;

    if (currentStepNumber != null && currentStepNumber < logEntries.length) {
      var arrowCell = makeArrowCell(-1, currentStepNumber);
      var messageCell = jQuery("<td>").text(complete ? "Complete workflow" : "Resume workflow");
      var actionCell = makeLogIconCell("arrow-play.svg", "Return").click(function () {
        continueWorkflow(workflowId);
      });
      table.append(makeRow(rowNum++, [arrowCell, messageCell, actionCell]));
    }

    for (var i = logEntries.length - 1; i >= 0; i--) {
      var arrowCell = makeArrowCell(i, currentStepNumber);
      var messageCell = jQuery("<td>").text(logEntries[i]);
      var actionCell = makeActionCell(i, logEntries.length, workflowId);
      table.append(makeRow(rowNum++, [arrowCell, messageCell, actionCell]));
    }

    return table;
  }

  function makeExecuteButton(workflowId) {
    return jQuery("<a class='ui-button ui-state-default'>")
      .text("Confirm")
      .click(function () {
        executeWorkflow(workflowId);
      });
  }

  function showConfirm(workflowId, stepNumber, message, log, complete) {
    display
      .empty()
      .append([
        makeInstructionTag(message),
        makeExecuteButton(workflowId),
        errorDiv,
        makeLog(log, workflowId, stepNumber, complete),
      ]);
  }

  function continueWorkflow(workflowId) {
    ajax("GET", "/miso/rest/workflows/" + workflowId + "/step/latest", updateDisplay, showError);
  }

  function makeSkipButton(workflowId, stepNumber) {
    return jQuery("<a class='ui-button ui-state-default'>")
      .text("Skip")
      .click(function () {
        processInput(workflowId, stepNumber, SKIP);
      });
  }

  function showPrompt(workflowId, stepNumber, complete, message, inputTypes, logMessages) {
    var elements = [makeInstructionTag(message), makeInputTag(workflowId, stepNumber)];
    if (inputTypes.indexOf(SKIP) > -1) {
      elements.push(jQuery("<br>"));
      elements.push(makeSkipButton(workflowId, stepNumber));
    }
    elements.push(errorDiv);
    elements.push(makeLog(logMessages, workflowId, stepNumber, complete));

    display.empty().append(elements).children("input").focus();
  }

  function initDisplay(divId) {
    display = jQuery("#" + divId);
  }

  function updateDisplay(state) {
    if (Number.isInteger(state["stepNumber"])) {
      showPrompt(
        state["workflowId"],
        state["stepNumber"],
        state["complete"],
        state["message"],
        state["inputTypes"],
        state["log"]
      );
    } else {
      showConfirm(
        state["workflowId"],
        state["stepNumber"],
        state["message"],
        state["log"],
        state["complete"]
      );
    }
  }

  return {
    init: function (divId, state) {
      initDisplay(divId);
      updateDisplay(state);
    },
  };
})();
