---
layout: default
title: "Index Checker"
---

<script src="../scripts/index-checker.js"></script>

Find duplicates and near-matches within a set of indices.

Enter index sequences into the Indices box and click "Calculate." Pairs with fewer different bases than the minimum distance specified will be listed in the results.

<div id="index-checker-container">
  <div class="index-checker-box">
    <label>Indices<br>
      <textarea id="indices" rows="25"></textarea>
    </label>
  </div>
  <div id="index-checker-control-box">
    <label>Min. Mismatches:<br>
      <input id="min-distance" class="index-checker-control" type="text"><br>
    </label>
    <button class="index-checker-control" onclick="IndexChecker.calculate()">Calculate</button><br>
    <button class="index-checker-control" onclick="IndexChecker.resetForm()">Reset</button>
  </div>
  <div class="index-checker-box">
    <label>Results<br>
      <textarea id="results" rows="25" readonly="readonly"></textarea>
    </label>
  </div>
</div>
<script>IndexChecker.resetForm();</script>

**Notes:**

* When comparing indices of different lengths, only the length of the shorter one will be considered. e.g. **AAAAAA** and **AAAAAACC** are considered duplicates.
* For dual barcodes, enter both indices together as if they were one. e.g. **AAAAAA** index 1 and **CCCCCC** index 2 should be entered as **AAAAAACCCCCC**