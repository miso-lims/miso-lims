<%@ include file="../header.jsp" %>
<%--
  ~ Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
  ~ MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
  ~ **********************************************************************
  ~
  ~ This file is part of MISO.
  ~
  ~ MISO is free software: you can redistribute it and/or modify
  ~ it under the terms of the GNU General Public License as published by
  ~ the Free Software Foundation, either version 3 of the License, or
  ~ (at your option) any later version.
  ~
  ~ MISO is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ GNU General Public License for more details.
  ~
  ~ You should have received a copy of the GNU General Public License
  ~ along with MISO.  If not, see <http://www.gnu.org/licenses/>.
  ~
  ~ **********************************************************************
  --%>

<script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.popup.js'/>"></script>
<script src="<c:url value='/scripts/datatables_utils.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/datatables/js/jquery.dataTables.min.js'/>" type="text/javascript"></script>
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" type="text/css">
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables_themeroller.css'/>">

<h1>
  <div id="totalProjectCount"> Projects
  </div>
</h1>

<div style="clear:both"/>
<div id="traftrigger" onclick="readJson()" class="ui-corner-all">
  Projects Tree
</div>


<div style='display:none'>
  <div id="trafpanel">

    <div id="trafresultgraph">
      <div id="selection">
        <button class="D3Button" onclick="changeD3('round') "> Round Tree</button>
        <button class="D3Button" onclick="changeD3('dendo')"> Dendogram</button>
        <input type=button class="fg-button-tree ui-state-default ui-corner-all"
               id='group' value="Show Project">

        <div id="loading" style=" position: absolute; right: 0; top:0 "><img
            src="<c:url value="/styles/images/loading.gif"/>"/></div>
      </div>

      <div id='chart'>
        <%--chart will go here--%>
      </div>
    </div>
  </div>
</div>

<table cellpadding="0" cellspacing="0" border="0" class="display" id="listingProjectsTable">
</table>
<script type="text/javascript">
var fun, state = "tree";

jQuery(document).ready(function () {
  jQuery("#traftrigger").colorbox({width: "90%", inline: true, href: "#trafpanel"});
  Project.ui.createListingProjectsTable();
});


jQuery(function () {
  var theTable = jQuery("#table");

  jQuery("#filter").keyup(function () {
    jQuery.uiTableFilter(theTable, this.value + ' ' + jQuery('#progressFilter').val());
    writeTotalNo();
    jQuery('table.overviewSummary tr').show();
  });

  jQuery("#progressFilter").change(function () {
    jQuery.uiTableFilter(theTable, this.value + ' ' + jQuery('#filter').val());
    writeTotalNo();
    jQuery('table.overviewSummary tr').show();
  });

  jQuery('#filter-form').submit(
      function () {
        theTable.find("tbody > tr:visible > td:eq(1)").mousedown();
        return false;
      }).focus(); //Give focus to input field
});

function writeTotalNo() {
  jQuery('#totalProjectCount').html(jQuery('#table>tbody>tr:visible').length.toString() + " Projects");
}

//circular D3 Graph
var duration = 500, i = 0, root;
var width = getWindowWidth() * 0.8;
var mainjson, vis, firsttime = "true";

function getWindowWidth() {
  return window.innerWidth || document.body.clientWidth;
}

function readJson() {
  fun = new changetotree();
  fun.init();

  document.getElementById("group").onclick = function () {
    fun.changeView();
  };

  jQuery("#loading").fadeIn();
  if (firsttime == "true") {
    jQuery("#loading").fadeIn();
    Fluxion.doAjax(
      'projectsTreeControllerHelperService',
      'listProjectTree',
      {'url': ajaxurl},
      {'doOnSuccess': function (json_data) {
        mainjson = json_data;
        root = mainjson;
        fun.updatetree(root);
        jQuery("#loading").fadeOut();
        fun.changeView();
        jQuery("#loading").fadeOut();
        firsttime = "false";
      }
      }
    );
  }
  else {
    document.getElementById("chart").innerHTML = "";
    changeD3(state)
  }
}

function changetotree() {
  var changeTreeObj = new Object();
  var r, tree, diagonal;

  function init() {
    r = width / 2;
    tree = d3.layout.tree()
        .size([360, r - 120])
        .separation(function (a, b) {
                      return (a.parent == b.parent ? 1 : 2) / a.depth;
                    });

    diagonal = d3.svg.diagonal.radial()
        .projection(function (d) {
                      return [d.y, d.x / 180 * Math.PI];
                    });
    vis = d3.select("#chart").append("svg:svg")
        .attr("width", r * 2)
        .attr("height", r * 2)
        .append("svg:g")
        .attr("transform", "translate(" + r + "," + r + ")");
  }

  function updatetree(source) {
    // Compute the new tree layout.
    var nodes = tree.nodes(root);

    // Update the links…
    var link = vis.selectAll("path.link")
        .data(tree.links(nodes), function (d) {
                return d.target.id;
              });

    // Enter any new links at the parent's previous position.
    link.enter().append("svg:path")
        .attr("class", "link")
        .attr("d", diagonal)
        .transition()
        .duration(duration)
        .attr("d", diagonal);

    // Transition links to their new position.
    link.transition()
        .duration(duration)
        .attr("d", diagonal);

    // Transition exiting nodes to the parent's new position.
    link.exit().transition()
        .duration(duration)
        .attr("d", diagonal)
        .remove();

    var node = vis.selectAll("circle.node")
        .data(nodes);

    // Enter any new nodes at the parent's previous position.
    node.enter().append("svg:circle")
        .attr("dx", function (d) {
                return source.x0;
              })
        .attr("dy", function (d) {
                return source.y0;
              })
        .attr("r", 4)

        .attr("class", "node")
        .style("fill", function (d) {
                 return d._children ? "lightsteelblue" : "#fff";
               })
        .style("stroke", function (d) {
                 return d.color == 0 ? "red"
                     : d.color == 1 ? "lightgreen"
                            : d.color == 2 ? "gray" :
                              "steelblue";
               })
        .style("stroke-width", "1.5px")
        .on("click", clicktree)
        .append("svg:title")
        .text(function (d) {
                return d.description;
              })
        .transition()
        .duration(duration)
        .attr("dx", function (d) {
                return d.x;
              })
        .attr("dy", function (d) {
                return d.y;
              })
        .attr("transform", function (d) {
                return "rotate (" + (d.x - 90) + ")translate(" + d.y + ")";
              });

    // Transition nodes to their new position.
    node.transition()
        .duration(duration)
        .attr("dx", function (d) {
                return d.x;
              })
        .attr("dy", function (d) {
                return d.y;
              })
        .style("fill", function (d) {
                 return d._children ? "lightsteelblue" : "#fff";
               })
        .style("stroke", function (d) {
                 return d.color == 0 ? "red"
                     : d.color == 1 ? "lightgreen"
                            : d.color == 2 ? "gray" :
                              "steelblue";
               })
        .style("stroke-width", "1.5px")
        .attr("transform", function (d) {
                return "rotate (" + (d.x - 90) + ")translate(" + d.y + ")";
              });

    // Transition exiting nodes to the parent's new position.
    node.exit().transition()
        .duration(duration)

        .attr("dx", function (d) {
                return source.x;
              })
        .attr("dy", function (d) {
                return source.y;
              })
        .remove();

    // Update the texts…
    var text = vis.selectAll("text")
        .data(nodes, function (d) {
                return d.name;
              });

    text.enter().append("svg:text")
        .attr("class", "node1")
        .attr("dx", function (d) {
                return d.x < 180 ? 8 : -8;
              })
        .attr("dy", ".75em")

        .attr("transform", function (d) {
                return d.x < 180 ? "rotate (" + (d.x - 90) + ")translate(" + d.y + ")" : "rotate (" + (d.x + 90) + ")translate(-" + (d.y + 5) + ")";
              })

        .text(function (d) {
                return d.name;
              })
        .attr("text-anchor", function (d) {
                return d.x < 180 ? "start" : "end";
              })
        .transition()
        .duration(duration)
        .attr("text-anchor", function (d) {
                return d.x < 180 ? "start" : "end";
              })
        .attr("direction", "rtl")
        .attr("transform", function (d) {
                return d.x < 180 ? "rotate (" + (d.x - 90) + ")translate(" + d.y + ")" : "rotate (" + (d.x + 90) + ")translate(-" + (d.y + 5) + ")";
              })

        .attr("dx", "8")
        .attr("dy", ".75em");

    text.transition()
        .duration(duration)
        .attr("class", "node")
        .attr("dx", "8")
        .attr("dy", ".75em")
        .attr("text-anchor", function (d) {
                return d.x < 180 ? "start" : "end";
              })

        .attr("transform", function (d) {
                return d.x < 180 ? "rotate (" + (d.x - 90) + ")translate(" + d.y + ")" : "rotate (" + (d.x + 90) + ")translate(-" + (d.y + 5) + ")";
              })
    // Transition exiting nodes to the parent's new position.
    text.exit().transition()
        .duration(duration)
        .attr("dx", "8")
        .attr("dy", ".75em")
        .remove();

    // Stash the old positions for transition.
    nodes.forEach(function (d) {
      d.x0 = d.x;
      d.y0 = d.y;
    });
  }

  // Toggle children on click.
  function clicktree(d) {
    if (d.children) {
      d._children = d.children;
      d.children = null;
      updatetree(d);
    }
    else if (d._children) {
      d.children = d._children;
      d._children = null;
      d.children.forEach(function (child) {
        if (child.children) {
          child._children = child.children;
          child.children = null;
        }
      });
      updatetree(d);
    }
    else if (d.subs > 0) {
      console.log("show" + d.show)
      console.log("subs" + d.subs)
      var id = 0;
      var method = d.show + "subs";
      console.log("id" + d.id)
      console.log("parent" + d.parent.id)
      if (d.id) {
        id = d.id;
      }
      else {
        id = d.parent.id;
        method = d.show + "s";
      }
      Fluxion.doAjax(
        'projectsTreeControllerHelperService',
        method,
        {'id': id, 'url': ajaxurl},
        {'doOnSuccess': function (json_data) {
          console.log(json_data);
          d.children = json_data.children;
          d._children = null;
          d.children.forEach(function (child) {
            if (child.children) {
              child._children = child.children;
              child.children = null;
            }
          });
          updatetree(d);
        }
        }
      );
    }
  }

  function changeView() {
    jQuery("#loading").fadeIn();
    var temp = document.getElementById("group");

    if (temp.value == "Show All") {
      document.getElementById("group").value = "Show Project";

      vis.selectAll("circle.node")
          .each(function (d) {
                  if (d._children) {
                    d.children = d._children;
                    d._children = null;
                  }
                });
      updatetree(mainjson);
    }
    else {
      document.getElementById("group").value = "Show All";
      vis.selectAll("circle.node")
          .filter(function (d) {
                    return d.show == "PROJECT" || d.show == "MISO";
                  })
          .each(function (d) {

                  if (d.children) {

                    d._children = d.children;
                    d.children = null;
                  }
                });
      mainClick();
    }
    jQuery("#loading").fadeOut();
  }

  function mainClick() {
    vis.selectAll("circle.node")
        .filter(function (d) {
                  return d.show == "MISO";
                })
        .each(function (d) {
                if (d.children) {

                  d._children = d.children;
                  d.children = null;
                }
                else {

                  d.children = d._children;
                  d._children = null;
                }
              });
    updatetree(d);
  }

  changeTreeObj.init = init;
  changeTreeObj.changeView = changeView;
  changeTreeObj.updatetree = updatetree;
  return changeTreeObj;
}

function changetodendo() {
  var w, dendo_tree, dendo_diagonal, dendo_vis;
  var changeDendoObj = new Object();

  function init() {
    w = width,
        h = 8000,
        j = 0;

    dendo_tree = d3.layout.tree()
        .size([h, w - 160]);

    dendo_diagonal = d3.svg.diagonal()
        .projection(function (d) {
                      return [d.y, d.x];
                    });

    vis = d3.select("#chart").append("svg:svg")
        .attr("width", w)
        .attr("height", h)
        .append("svg:g")
        .attr("transform", "translate(40,0)");
  }

  function dendo_update(source) {
    // Compute the new tree layout.
    var nodes = dendo_tree.nodes(root).reverse();

    // Update the links…
    var link = vis.selectAll("path.link")
        .data(dendo_tree.links(nodes), function (d) {
                return d.target.id;
              });

    // Enter any new links at the parent's previous position.
    link.enter().append("svg:path", "circles")

        .attr("class", "link")
        .attr("d", function (d) {
                var o = {x: source.x0, y: source.y0};
                return dendo_diagonal({source: o, target: o});
              })
        .transition()
        .duration(duration)
        .attr("d", dendo_diagonal);

    // Transition links to their new position.
    link.transition()
        .duration(duration)
        .attr("d", dendo_diagonal);

    // Transition exiting nodes to the parent's new position.
    link.exit().transition()
        .duration(duration)
        .attr("d", function (d) {
                var o = {x: source.x, y: source.y};
                return dendo_diagonal({source: o, target: o});
              })
        .remove();

    var node = vis.selectAll("circle.node")
        .data(nodes);
    // Enter any new nodes at the parent's previous position.
    node.enter().append("svg:circle")
        .attr("cx", function (d) {
                return source.y0;
              })
        .attr("cy", function (d) {
                return source.x0;
              })
        .attr("r", "4")
        .attr("class", "node")

        .style("fill", function (d) {
                 return d._children ? "lightsteelblue" : "#fff";
               })
        .style("stroke", function (d) {
                 return d.color == 0 ? "red"
                     : d.color == 1 ? "lightgreen"
                            : d.color == 2 ? "gray" :
                              "steelblue";
               })
        .style("stroke-width", "1.5px")
        .on("click", dendo_click)
        .append("svg:title")
        .text(function (d) {
                return d.description;
              })
        .transition()

        .duration(duration)
        .attr("cx", function (d) {
                return d.y;
              })
        .attr("cy", function (d) {
                return d.x;
              })
        .attr("r", "4");


    // Transition nodes to their new position.
    node.transition()
        .duration(duration)
        .attr("cx", function (d) {
                return d.y;
              })
        .attr("cy", function (d) {
                return d.x;
              })
        .attr("r", "4")

        .style("fill", function (d) {
                 return d._children ? "lightsteelblue" : "#fff";
               })
        .style("stroke", function (d) {
                 return d.color == 0 ? "red"
                     : d.color == 1 ? "lightgreen"
                            : d.color == 2 ? "gray" :
                              "steelblue";
               })
        .style("stroke-width", "1.5px");

    // Transition exiting nodes to the parent's new position.
    node.exit().transition()
        .duration(duration)
        .attr("r", "4")
        .attr("cx", function (d) {
                return source.y;
              })
        .attr("cy", function (d) {
                return source.x;
              })
        .remove();

    var text = vis.selectAll("text")
        .data(nodes, function (d) {
                return d.name;
              });

    text.enter().append("svg:text")
        .attr("class", "node1")
        .attr("dx", function (d) {
                return source.y0 + 5;
              })
        .attr("dy", function (d) {
                return source.x0;
              })

        .text(function (d) {
                return d.name;
              })

        .transition()
        .duration(duration)
        .attr("dx", function (d) {
                return d.y + 5;
              })
        .attr("dy", function (d) {
                return d.x + 5;
              });

    text.transition()
        .duration(duration)
        .attr("class", "node")
        .attr("dx", function (d) {
                return d.y + 5;
              })
        .attr("dy", function (d) {
                return d.x;
              })

    // Transition exiting nodes to the parent's new position.
    text.exit().transition()
        .duration(duration)
        .attr("dx", function (d) {
                return source.y + 5;
              })
        .attr("dy", function (d) {
                return source.x;
              })
        .remove();

    nodes.forEach(function (d) {
      d.x0 = d.x;
      d.y0 = d.y;
    });
  }

  // Toggle children on click.
  function dendo_click(d) {
    if (d.children) {
      d._children = d.children;
      d.children = null;
      dendo_update(d);
    }
    else if (d._children) {
      d.children = d._children;
      d._children = null;

      /*
       Addition for the step by step entry
       */
      d.children.forEach(function (child) {
        if (child.children) {
          child._children = child.children;
          child.children = null;
        }
      });
      dendo_update(d);
    }
    else if (d.subs > 0) {
      console.log("show" + d.show)
      console.log("subs" + d.subs)
      var id = 0;
      var method = d.show + "subs";
      console.log("id" + d.id)
      console.log("parent" + d.parent.id)

      if (d.id) {
        id = d.id;
      }
      else {
        id = d.parent.id;
        method = d.show + "s";
      }
      Fluxion.doAjax(
        'projectsTreeControllerHelperService',
        method,
        {'id': id, 'url': ajaxurl},
        {'doOnSuccess': function (json_data) {
          console.log(json_data);
          d.children = json_data.children;
          d._children = null;
          d.children.forEach(function (child) {
            if (child.children) {
              child._children = child.children;
              child.children = null;
            }
          });
          dendo_update(d);
        }
        }
      );
    }
  }

  function changeView() {
    jQuery("#loading").fadeIn();
    var temp = document.getElementById("group");

    if (temp.value == "Show All") {
      document.getElementById("group").value = "Show Project";
      vis.selectAll("circle.node")
          .each(function (d) {
                  if (d._children) {
                    d.children = d._children;
                    d._children = null;
                  }

                });
      dendo_update(mainjson);
    }
    else {
      document.getElementById("group").value = "Show All";
      vis.selectAll("circle.node")
          .filter(function (d) {
                    return d.show == "PROJECT" || d.show == "MISO";
                  })
          .each(function (d) {
                  if (d.children) {

                    d._children = d.children;
                    d.children = null;
                  }
                });
      maindendo();
    }
    jQuery("#loading").fadeOut();
  }

  function maindendo() {
    vis.selectAll("circle.node")
        .filter(function (d) {
                  return d.show == "MISO";
                })
        .each(function (d) {
                if (d.children) {

                  d._children = d.children;
                  d.children = null;
                }
                else {

                  d.children = d._children;
                  d._children = null;
                }
              });
    dendo_update(d);
  }

  changeDendoObj.init = init;
  changeDendoObj.changeView = changeView;
  return changeDendoObj;
}

function changeD3(type) {
  state = type;
  document.getElementById("chart").innerHTML = "";

  if (type == "dendo") {
    document.getElementById("group").value = "Show All";
    fun = new changetodendo();
    fun.init();

    document.getElementById("group").onclick = function () {
      fun.changeView();
    };

    fun.changeView();
  }
  else {
    document.getElementById("group").value = "Show All";
    fun = new changetotree();
    fun.init();

    document.getElementById("group").onclick = function () {
      fun.changeView();
    };

    fun.changeView();
  }
}
d3.select(self.frameElement).style("height", "1000px");
</script>

<%@ include file="../footer.jsp" %>
