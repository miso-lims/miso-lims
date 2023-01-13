var Tile = {
  title: function (label) {
    var title = document.createElement("DIV");

    title.setAttribute("class", "name");
    title.setAttribute("style", "font-weight:bold");
    title.innerText = label;

    return title;
  },
  titleAndStatus: function (label, status) {
    var title = this.title(label);
    if (status != null) {
      title.append(status);
    }
    return title;
  },
  lines: function (lines, special) {
    var p = document.createElement("P");
    if (special) {
      p.setAttribute("style", "font-style:italic");
    }

    lines
      .filter(function (line) {
        return !!line;
      })
      .forEach(function (line, index, arr) {
        p.appendChild(document.createTextNode(line));
        if (index < arr.length - 1) {
          p.appendChild(document.createElement("BR"));
        }
      });
    return p;
  },
  statusOk: function (title) {
    var status = document.createElement("IMG");
    status.setAttribute("src", "/styles/images/tile-ok.svg");
    if (title) {
      status.setAttribute("title", title);
    }
    return status;
  },
  statusMaybe: function (title) {
    var status = document.createElement("IMG");
    status.setAttribute("src", "/styles/images/tile-maybe.svg");
    if (title) {
      status.setAttribute("title", title);
    }
    return status;
  },
  statusBad: function (title) {
    var status = document.createElement("IMG");
    status.setAttribute("src", "/styles/images/tile-bad.svg");
    if (title) {
      status.setAttribute("title", title);
    }
    return status;
  },
  statusBusy: function (title) {
    var status = document.createElement("IMG");
    status.setAttribute("src", "/styles/images/tile-busy.svg");
    if (title) {
      status.setAttribute("title", title);
    }
    return status;
  },
  make: function (tileparts, clickHandler) {
    var div = document.createElement("DIV");
    div.setAttribute("class", "tile");
    tileparts.forEach(function (part) {
      div.appendChild(part);
    });
    div.onclick = clickHandler;
    return div;
  },
};
