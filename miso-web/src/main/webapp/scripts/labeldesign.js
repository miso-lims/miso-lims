/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

function printerLabelEditor(layout, width, height, saveCallback) {
  var current = JSON.parse(JSON.stringify(layout));
  var dialogNode = document.createElement("div");
  document.body.appendChild(dialogNode);
  // Okay, create an SVG that we're going to use to render the label
  var svg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
  dialogNode.appendChild(svg);
  dialogNode.appendChild(document.createElement("br"));
  svg.setAttributeNS(null, "width", width + "mm");
  svg.setAttributeNS(null, "height", height + "mm");
  // Create a yellow rectangle as background
  var bg = document.createElementNS("http://www.w3.org/2000/svg", "rect");
  bg.setAttributeNS(null, "x", "0mm");
  bg.setAttributeNS(null, "y", "0mm");
  bg.setAttributeNS(null, "width", width + "mm");
  bg.setAttributeNS(null, "height", height + "mm");
  bg.setAttribute("fill", "#FEFEE3");
  svg.appendChild(bg);
  for (grid = 5; grid < width; grid += 5) {
    var line = document.createElementNS("http://www.w3.org/2000/svg", "line");
    line.setAttributeNS(null, "x1", grid + "mm");
    line.setAttributeNS(null, "x2", grid + "mm");
    line.setAttributeNS(null, "y1", "0mm");
    line.setAttributeNS(null, "y2", height + "mm");
    line.setAttributeNS(null, "stroke", "#00000040");
    line.setAttributeNS(null, "stroke-dasharray", "1 1");
    svg.appendChild(line);
  }
  for (grid = 5; grid < height; grid += 5) {
    var line = document.createElementNS("http://www.w3.org/2000/svg", "line");
    line.setAttributeNS(null, "x1", "0mm");
    line.setAttributeNS(null, "x2", width + "mm");
    line.setAttributeNS(null, "y1", grid + "mm");
    line.setAttributeNS(null, "y2", grid + "mm");
    line.setAttributeNS(null, "stroke", "#00000040");
    line.setAttributeNS(null, "stroke-dasharray", "1 1");
    svg.appendChild(line);
  }
  // The create a group (invisible container) to put all the stuff in
  var labelContents = document.createElementNS(
    "http://www.w3.org/2000/svg",
    "g"
  );
  svg.appendChild(labelContents);
  jQuery(dialogNode)
    .append(
      jQuery(document.createElement("button"))
        .prop({
          type: "button",
          innerHTML: "Add",
          "class": "btn-styled"
        })
        .click(function() {
          Utils.showWizardDialog("Add Element", [
            {
              name: "Single Line of Text",
              handler: function() {
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
                      use: "ALIAS"
                    }
                  ]
                });
                drawLabel();
              }
            },
            {
              name: "Block of Text",
              handler: function() {
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
                      use: "ALIAS"
                    }
                  ]
                });
                drawLabel();
              }
            },
            {
              name: "1D Barcode",
              handler: function() {
                current.push({
                  element: "1dbarcode",
                  x: 0,
                  y: 0,
                  height: 5,
                  moduleWidth: 0.3,
                  contents: [
                    {
                      use: "BARCODE"
                    }
                  ]
                });
                drawLabel();
              }
            },
            {
              name: "2D Barcode",
              handler: function() {
                current.push({
                  element: "2dbarcode",
                  x: 0,
                  y: 0,
                  moduleSize: 0.3,
                  contents: [
                    {
                      use: "BARCODE"
                    }
                  ]
                });
                drawLabel();
              }
            }
          ]);
        })
    )
    .append(
      jQuery(document.createElement("button"))
        .prop({
          type: "button",
          innerHTML: "Revert",
          "class": "btn-styled"
        })
        .click(function() {
          current = JSON.parse(JSON.stringify(layout));
          drawLabel();
        })
    )
    .append(
      jQuery(document.createElement("button"))
        .prop({
          type: "button",
          innerHTML: "Delete",
          "class": "btn-styled"
        })
        .click(function() {
          shouldDelete = true;
        })
    )
    .append(
      jQuery(document.createElement("button"))
        .prop({
          type: "button",
          innerHTML: "Delete All",
          "class": "btn-styled"
        })
        .click(function() {
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
  svg.addEventListener("click", function(e) {
    e.stopPropagation();
    shouldDelete = false;
  });

  function drawLabel() {
    // Clear the group
    while (labelContents.hasChildNodes()) {
      labelContents.removeChild(labelContents.lastChild);
    }
    // Set up a rectangle for a thing being drawn, these can overlap, but we're overlapping in the same way as the printer, so fine.
    current.forEach(function(labelElement, index) {
      var rect = document.createElementNS("http://www.w3.org/2000/svg", "rect");
      rect.setAttributeNS(null, "x", labelElement.x + "mm");
      rect.setAttributeNS(null, "y", labelElement.y + "mm");
      rect.setAttribute("stroke", "#000");
      rect.setAttribute("stroke-width", "1px");
      // Set a colour and dimensions for each element. We can't know the real layout exactly since the printers have all the font metrics, so make some reasonable guesses.
      switch (labelElement.element) {
        case "1dbarcode":
          rect.setAttributeNS(null, "height", labelElement.height + "mm");
          rect.setAttributeNS(
            null,
            "width",
            labelElement.moduleWidth * 100 + "mm"
          );
          rect.setAttribute("fill", "#ff000080");
          break;
        case "2dbarcode":
          rect.setAttributeNS(
            null,
            "height",
            labelElement.moduleSize * 144 + "mm"
          );
          rect.setAttributeNS(
            null,
            "width",
            labelElement.moduleSize * 144 + "mm"
          );
          rect.setAttribute("fill", "#0000ff80");
          break;
        case "textblock":
          var blockRotate = labelElement.direction.startsWith("VERTICAL");
          rect.setAttributeNS(
            null,
            blockRotate ? "width" : "height",
            labelElement.height * labelElement.rowLimit + "mm"
          );
          rect.setAttributeNS(
            null,
            blockRotate ? "height" : "width",
            (labelElement.lineLimit * labelElement.height) / 2 + "mm"
          );
          rect.setAttribute("fill", "#d0d0d080");
          break;
        case "text":
          var textRotate = labelElement.direction.startsWith("VERTICAL");
          rect.setAttributeNS(
            null,
            textRotate ? "width" : "height",
            labelElement.height + "mm"
          );
          rect.setAttributeNS(
            null,
            textRotate ? "height" : "width",
            (labelElement.lineLimit * labelElement.height) / 2 + "mm"
          );
          rect.setAttribute("fill", "#a0a0a080");
          break;
      }
      labelContents.appendChild(rect);
      rect.addEventListener("click", function(e) {
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
              value: labelElement.x
            },
            {
              type: "float",
              label: "Y Position (mm)",
              property: "y",
              value: labelElement.y
            }
          ];
          if (labelElement.element.startsWith("text")) {
            // Common text formatting
            edits.push({
              type: "float",
              label: "Height (mm)",
              property: "height",
              value: labelElement.height
            });
            edits.push({
              type: "int",
              label: "Max Line Length (characters)",
              property: "lineLimit",
              value: labelElement.lineLimit
            });
            edits.push({
              type: "select",
              label: "Direction",
              property: "direction",
              values: ["NORMAL", "UPSIDEDOWN", "VERTICAL_DOWN", "VERTICAL_UP"],
              value: labelElement.direction
            });
            edits.push({
              type: "select",
              label: "Style",
              property: "style",
              values: ["REGULAR", "BOLD"],
              value: labelElement.style
            });
          }

          switch (labelElement.element) {
            case "1dbarcode":
              edits.push({
                type: "float",
                label: "Module Width (mm) [width of each stripe]",
                property: "moduleWidth",
                value: labelElement.moduleWidth
              });
              edits.push({
                type: "float",
                label: "Barcode Height (mm)",
                property: "height",
                value: labelElement.height
              });
              break;
            case "2dbarcode":
              edits.push({
                type: "float",
                label: "Module Size (mm) [width/height of each square]",
                property: "moduleSize",
                value: labelElement.moduleSize
              });
              break;
            case "textblock":
              edits.push({
                type: "int",
                label: "Max Number of Rows",
                property: "rowLimit",
                value: labelElement.rowLimit
              });
              break;
            case "text":
              edits.push({
                type: "select",
                label: "Justification",
                property: "justification",
                values: ["LEFT", "RIGHT"],
                value: labelElement.justification
              });
              break;
          }
          Utils.showDialog("Edit Element", "Update", edits, function(updated) {
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
            function drawTable() {
              while (table.hasChildNodes()) {
                table.removeChild(table.lastChild);
              }

              lines.forEach(function(l, i) {
                var row = document.createElement("tr");
                table.appendChild(row);
                var header = document.createElement("th");
                row.appendChild(header);
                jQuery(header).append(
                  jQuery(document.createElement("button"))
                    .prop({
                      type: "button",
                      innerHTML: "+",
                      "class": "btn-styled"
                    })
                    .click(function() {
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
                      "class": "btn-styled"
                    })
                    .click(function() {
                      lines.splice(i, 1);
                      drawTable();
                    })
                );

                var cell = document.createElement("th");
                row.appendChild(cell);
                createLine(cell, l, true, function(output) {
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
                    "class": "btn-styled"
                  })
                  .click(function() {
                    lines.splice(lines.length, 0, ["new row"]);
                    drawTable();
                  })
              );
            }
            drawTable();
          } else {
            var container = document.createElement("div");
            editArea.appendChild(container);
            createLine(container, labelElement.contents, true, function(
              output
            ) {
              labelElement.contents = output;
            });
          }
        }
      });
    });
  }
  function createLine(container, printable, allowAlternate, callback) {
    var printables = [printable].flat(Number.MAX_VALUE).filter(function(x) {
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
            "class": "btn-styled"
          })
          .click(function() {
            addElement(0, allowAlternate);
          })
      );

      printables.forEach(function(p, i) {
        container.appendChild(document.createElement("br"));
        if (typeof p == "string") {
          var input = document.createElement("input");
          input.type = "text";
          input.value = p;
          container.appendChild(input);
          input.addEventListener("keyup", function() {
            printables[i] = input.value;
            callback(printables);
          });
        } else if (p.hasOwnProperty("use")) {
          var select = document.createElement("select");
          select.addEventListener("change", function() {
            printables[i] = { use: select.value };
            callback(printables);
          });
          Constants.printableFields.forEach(function(value) {
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
          Object.entries(p).forEach(function(entry, index) {
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
            createLine(cell, entry[1], false, function(output) {
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
              "class": "btn-styled"
            })
            .click(function() {
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
              "class": "btn-styled"
            })
            .click(function() {
              addElement(i + 1, allowAlternate);
            })
        );
      });
    }
    function addElement(i, allowAlternate) {
      var options = [
        { name: "Text", value: "abc" },
        { name: "Field", value: { use: "ALIAS" } }
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
            KIT: "kit"
          }
        });
      }
      Utils.showWizardDialog(
        "Add",
        options.map(function(entry) {
          return {
            name: entry.name,
            handler: function() {
              printables.splice(i, 0, entry.value);
              callback(printables);
              display();
            }
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
      Save: function() {
        saveCallback(current);
      },
      "Save and Close": function() {
        saveCallback(current);
        dialog.dialog("close");
      }
    },
    closeOnEscape: false
  });
}
