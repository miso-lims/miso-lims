/**
 * Created by IntelliJ IDEA.
 * User: thankia
 * Date: 6/29/12
 * Time: 1:29 PM
 * To change this template use File | Settings | File Templates.
 */


var datas, datanew;
function readStatsdb(file) {


  var w = 820, h = 800, m = [20, 50, 20, 50];
  var max = 600;

  var scores = ["41","40","39","38","37","36","35","34","33","32","31","30","29","28","27","26","25","24","23","22","21","20","19","18","17","16","15","14","13","12","11","10","9","8","7","6","5","4","3","2","1"];
  var position = [];


  datas = [1];
  var patharray = [];
  var width = max / 41, height = 1, duration = 0, domain = null, value = Number;
  var vis = d3.select("#statschart").selectAll("svg")
          .data(datas)
          .enter().append("svg")
          .attr("class", "box")
          .attr("width", w)
          .attr("height", h);


  var d3line2 = d3.svg.line()
          .x(function(d) {
               return d.x;
             })
          .y(function(d) {
               return d.y;
             })
          .interpolate("linear");


  var json = file;

  datanew = json.stats;
  json.stats.filter(function(d) {
    patharray.push(parseInt(max) * (41 - parseInt(d.mean)) / 41);
    position.push(d.base);
  });
  load();
//  });


  function load() {
    var scorebox = vis.selectAll("rect.day")
            .data(scores)
    scorebox.enter().append("svg:rect")
            .attr("x", function(g) {
                    return 52.5;
                  })
            .attr("class", function(d, i) {
                    if (i >= 30) {
                      return "scoreboxgreen";
                    } else if (i >= 20) {
                      return "scoreboxyellow";
                    }
                    else {
                      return "scoreboxred";
                    }
                  })
            .attr("y", function(d, i) {
                    return (parseInt(max) * (41 - (parseInt(i + 1))) / 41);
                  })
            .attr("width", function() {
                    return position.length * 20;
                  })
            .attr('stroke', function() {
                    return "darkgray";
                  })
            .attr('fill', function() {
                    return "transparent";
                  })
            .attr("height", width);

    var basebox = vis.selectAll("rect.day")
            .data(position)
    basebox.enter().append("svg:rect")
            .attr("x", function(d, i) {
                    return (i + 1) * 20 + 32.5;
                  })
            .attr("class", "basebox")
            .attr("y", 0)
            .attr("width", function() {
                    return 20;
                  })
            .attr('stroke', function() {
                    return "darkgray";
                  })
            .attr('fill', function() {
                    return "transparent";
                  })
            .attr("height", function() {
                    return 41 * width;
                  })
            .on("mouseover", function(d, i) {
                  content(i);
                })
            .on("mouseout", hideText);

    var whiskertop = vis.selectAll("line.topwhisker")
            .data(datanew);

    whiskertop.enter().insert("svg:line")
            .attr("class", "whisker")
            .attr("x1", function(g, i) {
                    return (i + 1) * 20 + 35;
                  })
            .attr("y1", function(g) {
                    return (parseInt(max) * (41 - parseInt(g.ninetiethpercentile)) / 41);
                  })
            .attr("x2", function(g, i) {
                    return (i + 1) * 20 + 35 + width;
                  })
            .attr("y2", function(g) {
                    return (parseInt(max) * (41 - parseInt(g.ninetiethpercentile)) / 41);
                  });

    var whiskerbottom = vis.selectAll("line.bottomwhisker")
            .data(datanew);

    whiskerbottom.enter().insert("svg:line")
            .attr("class", "whisker")
            .attr("x1", function(g, i) {
                    return (i + 1) * 20 + 35;
                  })
            .attr("y1", function(g) {
                    return (parseInt(max) * (41 - parseInt(g.tenthpercentile)) / 41);
                  })
            .attr("x2", function(g, i) {
                    return (i + 1) * 20 + 35 + width;
                  })
            .attr("y2", function(g) {
                    return (parseInt(max) * (41 - parseInt(g.tenthpercentile)) / 41);
                  });

    var whiskerjoint = vis.selectAll("line.jointwhisker")
            .data(datanew);

    whiskerjoint.enter().insert("svg:line")
            .attr("class", "whisker")
            .attr("x1", function(g, i) {
                    return (i + 1) * 20 + 35 + width / 2;
                  })
            .attr("y1", function(g) {
                    return (parseInt(max) * (41 - parseInt(g.ninetiethpercentile)) / 41);
                  })
            .attr("x2", function(g, i) {
                    return (i + 1) * 20 + 35 + width / 2;
                  })
            .attr("y2", function(g) {
                    return (parseInt(max) * (41 - parseInt(g.tenthpercentile)) / 41);
                  });

    var box = vis.selectAll("rect.box")
            .data(datanew);

    box.enter().append("svg:rect")
            .attr("x", function(g, i) {
                    return (i + 1) * 20 + 35;
                  })
            .attr("y", function(g) {
                    return (parseInt(max) * (41 - parseInt(g.upperquartile)) / 41);
                  })
            .attr("width", width)
            .attr('stroke', function() {
                    return "black";
                  })
            .attr('fill', function() {
                    return "yellow";
                  })
            .attr("height", function(g) {
                    return (parseInt(max) * (parseInt(g.upperquartile) - parseInt(g.lowerquartile)) / 41);
                  })
            .on("mouseover", function(d, i) {
                  content(i);
                })
            .on("mouseout", hideText);

    var medianLine = vis.selectAll("line.median")
            .data(datanew);

    medianLine.enter().append("svg:line")
            .attr("class", "median")
            .attr('stroke', function() {
                    return "red";
                  })
            .attr("x1", function(g, i) {
                    return (i + 1) * 20 + 35;
                  })
            .attr("y1", function(g) {
                    return (parseInt(max) * (41 - parseInt(g.median)) / 41);
                  })
            .attr("x2", function(g, i) {
                    return (i + 1) * 20 + 35 + width;
                  })
            .attr("y2", function(g) {
                    return (parseInt(max) * (41 - parseInt(g.median)) / 41);
                  }); // median +1

    Path();

    var scoretext = vis.selectAll("text.day")
            .data(scores);
    scoretext.enter().append("svg:text")
            .attr("class", "label")
            .attr("x", 20)
            .attr("y", function(d, i) {
                    return (parseInt(max) * (parseInt(i + 1)) / 41);
                  })
            .attr("text-anchor", "end")
            .text(function(d) {
                    return d;
                  });

    var positiontext = vis.selectAll("text.day")
            .data(position);

    positiontext.enter().append("svg:text")
            .attr("class", "label")
            .attr("x", function(d, i) {
                    return ([i + 1] * 20 + 35);
                  })
            .attr("y", max + 10)
            .text(function(d) {
                    return d;
                  });


  }

  function Path() {
    var pathinfo = [];

    for (var i = 0; i < patharray.length; i++) {
      var tempx = (i + 1) * 20 + 35 + width / 2;
      var tempy = patharray[i]; //(parseInt(max)*(41-parseInt(patharray[i]))/41);
      pathinfo.push({ x: tempx, y:tempy});
    }

    var path = vis.selectAll("path")
            .data(datas);

    path.enter().append("svg:path")
            .attr("width", 200)
            .attr("height", 200)
            .attr("class", "path")
            .attr('stroke', function() {
                    return "red";
                  })
            .style("fill", "none")
            .attr("d", d3line2(pathinfo));

  }



}

function content(g) {

  jQuery("#statschartmessage").show();
  jQuery(document).mousemove(function(e) {
  jQuery("#statschartmessage").css({
                                       top: (e.pageY + 10) - jQuery(window).scrollTop() + "px",
                                       left: (e.pageX + 10) - jQuery('#statschart').offset().left + "px"
                                     });
  });
//div infm code here
  jQuery("#statschartmessage").html("Base: "+datanew[g].base + "<br>Mean: " + Number(datanew[g].mean).toPrecision(2) + "<br>Median: " + Number(datanew[g].median).toPrecision(2) + "<br>Lower Quartile: " + Number(datanew[g].lowerquartile).toPrecision(2) + "<br>Upper Quartile: " + Number(datanew[g].upperquartile).toPrecision(2) + "<br>Tenth Percentile: " + Number(datanew[g].tenthpercentile).toPrecision(2) + "<br>Ninetieth Percentile: " + Number(datanew[g].ninetiethpercentile).toPrecision(2));
}

function hideText() {
  jQuery("#statschartmessage").hide();
}

