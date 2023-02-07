function printerLabelEditor(layout, width, height, saveCallback) {
  var scale = Math.min(Math.max(width, 200) / width, Math.max(height, 200) / height);
  var margins = Math.max(width, height) * 0.25 * scale;
  var bgWidth = width * scale + margins * 2;
  var bgHeight = height * scale + margins * 2;
  var current = JSON.parse(JSON.stringify(layout));
  var dialogNode = document.createElement("div");
  document.body.appendChild(dialogNode);
  // Okay, create an SVG that we're going to use to render the label
  var svg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
  dialogNode.appendChild(svg);
  dialogNode.appendChild(document.createElement("br"));
  svg.setAttributeNS(null, "width", bgWidth);
  svg.setAttributeNS(null, "height", bgHeight);
  // Create a yellow rectangle as background for the label
  var bg = document.createElementNS("http://www.w3.org/2000/svg", "rect");
  bg.setAttributeNS(null, "x", margins);
  bg.setAttributeNS(null, "y", margins);
  bg.setAttributeNS(null, "width", width * scale);
  bg.setAttributeNS(null, "height", height * scale);
  bg.setAttribute("fill", "#FFF27A");
  svg.appendChild(bg);
  var lineGap = 15;
  for (grid = lineGap; grid < bgWidth; grid += lineGap) {
    var line = document.createElementNS("http://www.w3.org/2000/svg", "line");
    line.setAttributeNS(null, "x1", grid);
    line.setAttributeNS(null, "x2", grid);
    line.setAttributeNS(null, "y1", "0");
    line.setAttributeNS(null, "y2", bgHeight);
    line.setAttributeNS(null, "stroke", "#00000040");
    line.setAttributeNS(null, "stroke-dasharray", "1 1");
    svg.appendChild(line);
  }
  for (grid = lineGap; grid < bgHeight; grid += lineGap) {
    var line = document.createElementNS("http://www.w3.org/2000/svg", "line");
    line.setAttributeNS(null, "x1", "0");
    line.setAttributeNS(null, "x2", bgWidth);
    line.setAttributeNS(null, "y1", grid);
    line.setAttributeNS(null, "y2", grid);
    line.setAttributeNS(null, "stroke", "#00000040");
    line.setAttributeNS(null, "stroke-dasharray", "1 1");
    svg.appendChild(line);
  }
  // The create a group (invisible container) to put all the stuff in
  var labelContents = document.createElementNS("http://www.w3.org/2000/svg", "g");
  labelContents.setAttributeNS(null, "transform", "translate(" + margins + "," + margins + ")");
  svg.appendChild(labelContents);
  jQuery(dialogNode)
    .append(
      jQuery(document.createElement("button"))
        .prop({
          type: "button",
          innerHTML: "Add",
          class: "btn-styled",
        })
        .click(function () {
          Utils.showWizardDialog("Add Element", [
            {
              name: "Single Line of Text",
              handler: function () {
                current.push({
                  element: "text",
                  x: 0,
                  y: 0,
                  direction: "NORMAL",
                  height: 5,
                  justification: "LEFT",
                  lineLimit: 30,
                  style: "REGULAR",
                  contents: [
                    {
                      use: "ALIAS",
                    },
                  ],
                });
                drawLabel();
              },
            },
            {
              name: "Block of Text",
              handler: function () {
                current.push({
                  element: "textblock",
                  x: 0,
                  y: 0,
                  direction: "NORMAL",
                  height: 5,
                  lineLimit: 30,
                  rowLimit: 5,
                  contents: [
                    {
                      use: "ALIAS",
                    },
                  ],
                });
                drawLabel();
              },
            },
            {
              name: "1D Barcode",
              handler: function () {
                current.push({
                  element: "1dbarcode",
                  x: 0,
                  y: 0,
                  height: 5,
                  moduleWidth: 0.3,
                  contents: [
                    {
                      use: "BARCODE",
                    },
                  ],
                });
                drawLabel();
              },
            },
            {
              name: "2D Barcode",
              handler: function () {
                current.push({
                  element: "2dbarcode",
                  x: 0,
                  y: 0,
                  moduleSize: 0.3,
                  contents: [
                    {
                      use: "BARCODE",
                    },
                  ],
                });
                drawLabel();
              },
            },
          ]);
        })
    )
    .append(
      jQuery(document.createElement("button"))
        .prop({
          type: "button",
          innerHTML: "Revert",
          class: "btn-styled",
        })
        .click(function () {
          current = JSON.parse(JSON.stringify(layout));
          drawLabel();
        })
    )
    .append(
      jQuery(document.createElement("button"))
        .prop({
          type: "button",
          innerHTML: "Delete",
          class: "btn-styled",
        })
        .click(function () {
          shouldDelete = true;
        })
    )
    .append(
      jQuery(document.createElement("button"))
        .prop({
          type: "button",
          innerHTML: "Delete All",
          class: "btn-styled",
        })
        .click(function () {
          current = [];
          drawLabel();
        })
    );

  dialogNode.appendChild(document.createElement("br"));
  dialogNode.appendChild(
    document.createTextNode(
      "Click to edit contents. Control-Click to edit position and properties."
    )
  );
  dialogNode.appendChild(document.createElement("br"));

  var editArea = document.createElement("div");
  dialogNode.appendChild(editArea);

  var shouldDelete = false;
  svg.addEventListener("click", function (e) {
    e.stopPropagation();
    shouldDelete = false;
  });

  function drawLabel() {
    // Clear the group
    while (labelContents.hasChildNodes()) {
      labelContents.removeChild(labelContents.lastChild);
    }
    // Set up a rectangle for a thing being drawn, these can overlap, but we're overlapping in the same way as the printer, so fine.
    current.forEach(function (labelElement, index) {
      var rect = document.createElementNS("http://www.w3.org/2000/svg", "rect");
      rect.setAttributeNS(null, "x", labelElement.x * scale);
      rect.setAttributeNS(null, "y", labelElement.y * scale);
      rect.setAttribute("stroke", "#000");
      rect.setAttribute("stroke-width", "1px");
      // Set a colour and dimensions for each element. We can't know the real layout exactly since the printers have all the font metrics, so make some reasonable guesses.
      switch (labelElement.element) {
        case "1dbarcode":
          rect.setAttributeNS(null, "height", labelElement.height * scale);
          rect.setAttributeNS(null, "width", labelElement.moduleWidth * 100 * scale);
          rect.setAttribute("fill", "#ff000080");
          break;
        case "2dbarcode":
          rect.setAttributeNS(null, "height", labelElement.moduleSize * 144 * scale);
          rect.setAttributeNS(null, "width", labelElement.moduleSize * 144 * scale);
          rect.setAttribute("fill", "#0000ff80");
          break;
        case "textblock":
          var blockRotate = labelElement.direction.startsWith("VERTICAL");
          rect.setAttributeNS(
            null,
            blockRotate ? "width" : "height",
            labelElement.height * labelElement.rowLimit * scale
          );
          rect.setAttributeNS(
            null,
            blockRotate ? "height" : "width",
            ((labelElement.lineLimit * labelElement.height) / 2) * scale
          );
          rect.setAttribute("fill", "#d0d0d080");
          break;
        case "text":
          var textRotate = labelElement.direction.startsWith("VERTICAL");
          rect.setAttributeNS(null, textRotate ? "width" : "height", labelElement.height * scale);
          rect.setAttributeNS(
            null,
            textRotate ? "height" : "width",
            ((labelElement.lineLimit * labelElement.height) / 2) * scale
          );
          rect.setAttribute("fill", "#a0a0a080");
          break;
      }
      labelContents.appendChild(rect);
      rect.addEventListener("click", function (e) {
        e.stopPropagation();
        while (editArea.hasChildNodes()) {
          editArea.removeChild(editArea.lastChild);
        }

        if (shouldDelete) {
          current.splice(index, 1);
          drawLabel();
          shouldDelete = false;
        } else if (e.ctrlKey || e.metaKey) {
          var edits = [
            {
              type: "float",
              label: "X Position (mm)",
              property: "x",
              value: labelElement.x,
            },
            {
              type: "float",
              label: "Y Position (mm)",
              property: "y",
              value: labelElement.y,
            },
          ];
          if (labelElement.element.startsWith("text")) {
            // Common text formatting
            edits.push({
              type: "float",
              label: "Height (mm)",
              property: "height",
              value: labelElement.height,
            });
            edits.push({
              type: "int",
              label: "Max Line Length (characters)",
              property: "lineLimit",
              value: labelElement.lineLimit,
            });
            edits.push({
              type: "select",
              label: "Direction",
              property: "direction",
              values: ["NORMAL", "UPSIDEDOWN", "VERTICAL_DOWN", "VERTICAL_UP"],
              value: labelElement.direction,
            });
            edits.push({
              type: "select",
              label: "Style",
              property: "style",
              values: ["REGULAR", "BOLD"],
              value: labelElement.style,
            });
          }

          switch (labelElement.element) {
            case "1dbarcode":
              edits.push({
                type: "float",
                label: "Module Width (mm) [width of each stripe]",
                property: "moduleWidth",
                value: labelElement.moduleWidth,
              });
              edits.push({
                type: "float",
                label: "Barcode Height (mm)",
                property: "height",
                value: labelElement.height,
              });
              break;
            case "2dbarcode":
              edits.push({
                type: "float",
                label: "Module Size (mm) [width/height of each square]",
                property: "moduleSize",
                value: labelElement.moduleSize,
              });
              break;
            case "textblock":
              edits.push({
                type: "int",
                label: "Max Number of Rows",
                property: "rowLimit",
                value: labelElement.rowLimit,
              });
              break;
            case "text":
              edits.push({
                type: "select",
                label: "Justification",
                property: "justification",
                values: ["LEFT", "RIGHT"],
                value: labelElement.justification,
              });
              break;
          }
          Utils.showDialog("Edit Element", "Update", edits, function (updated) {
            Object.assign(labelElement, updated);
            drawLabel();
          });
        } else {
          if (labelElement.element == "textblock") {
            var lines = Array.isArray(labelElement.contents)
              ? labelElement.contents
              : [labelElement.contents];
            labelElement.contents = lines;
            var table = document.createElement("table");
            editArea.appendChild(table);
            var drawTable = function () {
              while (table.hasChildNodes()) {
                table.removeChild(table.lastChild);
              }

              lines.forEach(function (l, i) {
                var row = document.createElement("tr");
                table.appendChild(row);
                var header = document.createElement("th");
                row.appendChild(header);
                jQuery(header).append(
                  jQuery(document.createElement("button"))
                    .prop({
                      type: "button",
                      innerHTML: "+",
                      class: "btn-styled",
                    })
                    .click(function () {
                      lines.splice(i, 0, ["new row"]);
                      drawTable();
                    })
                );
                header.appendChild(document.createTextNode("Row " + (i + 1)));
                jQuery(header).append(
                  jQuery(document.createElement("button"))
                    .prop({
                      type: "button",
                      innerHTML: "-",
                      class: "btn-styled",
                    })
                    .click(function () {
                      lines.splice(i, 1);
                      drawTable();
                    })
                );

                var cell = document.createElement("th");
                row.appendChild(cell);
                createLine(cell, l, true, function (output) {
                  labelElement.contents[i] = output;
                });
              });
              var row = document.createElement("tr");
              table.appendChild(row);
              var header = document.createElement("th");
              row.appendChild(header);
              jQuery(header).append(
                jQuery(document.createElement("button"))
                  .prop({
                    type: "button",
                    innerHTML: "+",
                    class: "btn-styled",
                  })
                  .click(function () {
                    lines.splice(lines.length, 0, ["new row"]);
                    drawTable();
                  })
              );
            };
            drawTable();
          } else {
            var container = document.createElement("div");
            editArea.appendChild(container);
            createLine(container, labelElement.contents, true, function (output) {
              labelElement.contents = output;
            });
          }
        }
      });
    });
  }
  function createLine(container, printable, allowAlternate, callback) {
    var printables = [printable].flat(Number.MAX_VALUE).filter(function (x) {
      return x !== null;
    });
    function display() {
      while (container.hasChildNodes()) {
        container.removeChild(container.lastChild);
      }
      jQuery(container).append(
        jQuery(document.createElement("button"))
          .prop({
            type: "button",
            innerHTML: "+",
            class: "btn-styled",
          })
          .click(function () {
            addElement(0, allowAlternate);
          })
      );

      printables.forEach(function (p, i) {
        container.appendChild(document.createElement("br"));
        if (typeof p == "string") {
          var input = document.createElement("input");
          input.type = "text";
          input.value = p;
          container.appendChild(input);
          input.addEventListener("keyup", function () {
            printables[i] = input.value;
            callback(printables);
          });
        } else if (p.hasOwnProperty("use")) {
          var select = document.createElement("select");
          select.addEventListener("change", function () {
            printables[i] = { use: select.value };
            callback(printables);
          });
          Constants.printableFields.forEach(function (value) {
            var option = document.createElement("option");
            option.text = value;
            option.value = value;
            select.appendChild(option);
            if (p.use == option.text) {
              select.value = value;
            }
          });
          container.appendChild(select);
        } else {
          var alternates = document.createElement("table");
          alternates.classList.add("dataTable");
          Object.entries(p).forEach(function (entry, index) {
            var row = document.createElement("tr");
            if (index % 2 == 1) {
              row.classList.add("odd");
            }
            alternates.appendChild(row);
            var header = document.createElement("th");
            header.innerText = entry[0];
            row.appendChild(header);
            var cell = document.createElement("td");
            cell.style.border = "1px solid black";
            row.appendChild(cell);
            createLine(cell, entry[1], false, function (output) {
              p[entry[0]] = output;
            });
          });
          container.appendChild(alternates);
        }
        jQuery(container).append(
          jQuery(document.createElement("button"))
            .prop({
              type: "button",
              innerHTML: "-",
              class: "btn-styled",
            })
            .click(function () {
              printables.splice(i, 1);
              display();
              callback(printables);
            })
        );
        jQuery(container).append(
          jQuery(document.createElement("button"))
            .prop({
              type: "button",
              innerHTML: "+",
              class: "btn-styled",
            })
            .click(function () {
              addElement(i + 1, allowAlternate);
            })
        );
      });
    }
    function addElement(i, allowAlternate) {
      var options = [
        { name: "Text", value: "abc" },
        { name: "Field", value: { use: "ALIAS" } },
      ];
      if (allowAlternate) {
        options.push({
          name: "Alternate",
          value: {
            LIBRARY_ALIQUOT: "library aliquot",
            POOL: "pool",
            SAMPLE: "sample",
            LIBRARY: "library",
            BOX: "box",
            CONTAINER: "container",
            CONTAINER_MODEL: "container model",
            KIT: "kit",
          },
        });
      }
      Utils.showWizardDialog(
        "Add",
        options.map(function (entry) {
          return {
            name: entry.name,
            handler: function () {
              printables.splice(i, 0, entry.value);
              callback(printables);
              display();
            },
          };
        })
      );
    }
    display();
  }
  drawLabel();
  var dialog = jQuery(dialogNode).dialog({
    autoOpen: true,
    height: 600,
    width: 350,
    title: "Edit Label Layout",
    modal: false,
    resizable: true,
    buttons: {
      Save: function () {
        saveCallback(current);
      },
      "Save and Close": function () {
        saveCallback(current);
        dialog.dialog("close");
      },
    },
    closeOnEscape: false,
  });
}
