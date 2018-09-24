---
layout: page
title: "Advanced Search Syntax"
category: usr
date: 2018-02-16
order: 3
---

Many search boxes in MISO have GMail-style search using the syntax on this page.

Multiple searches can be separated by spaces (not AND). This changes the behaviour of spaces. For instance, before “LIB4 ABCD” would not match anything, but now it will search for a library that matches “LIB4” and “ABCD”.

All syntax is case insensitive.

If a particular filter doesn't apply to a particular object type, it is ignored. Any other search term is taken as a regular query and matched against the current fields for each item.

The more search terms, the longer the search will take.

“has:” can be substituted for “is:”

Legend: 🔷 fully implemented in all versions; 🔹 only available in Detailed Samples; ⚪ some exceptions: see 'Meaning' for details

<table>
<tr>
  <th>Syntax</th>
  <th>Meaning</th>
  <th>Samples</th>
  <th>Libraries</th>
  <th>Dilutions</th>
  <th>Pools</th>
  <th>Runs</th>
  <th>Containers</th>
  <th>Order Completions</th>
</tr>
<tr>
  <td name='syntax'>is:fulfilled</td>
  <td name='meaning'>Check if there are no outstanding orders (remaining = 0).</td>
  <td name='samples'></td>
  <td name='libraries'></td>
  <td name='dilutions'></td>
  <td name='pool'>🔷</td>
  <td name='run'></td>
  <td name='container'></td>
  <td name='ordercomp'>🔷</td>
</tr>
<tr>
  <td name='syntax'>is:active<br/>is:order</td>
  <td name='meaning'>Check if there are outstanding orders (remaining > 0).</td>
  <td name='samples'></td>
  <td name='libraries'></td>
  <td name='dilutions'></td>
  <td name='pool'>🔷</td>
  <td name='run'></td>
  <td name='container'></td>
  <td name='ordercomp'>🔷</td>
</tr>
  <tr>
    <td name='syntax'>is:unknown<br/>is:complete<br/>is:completed<br/>is:failed<br/>is:started<br/>is:stopped<br/>is:running</td>
    <td name='meaning'>Match based on a run's “health”. For order completions, this means that the order includes at least one run with this status.</td>
    <td name='samples'></td>
    <td name='libraries'></td>
    <td name='dilutions'></td>
    <td name='pool'></td>
    <td name='run'>🔷</td>
    <td name='container'></td>
    <td name='ordercomp'>🔷</td>
  </tr>
<tr>
  <td name='syntax'>is:incomplete</td>
  <td name='meaning'>	Matches when a run's health (or an order completion with a run of this health) is any of running, started, or stopped (which all decrease the “remaining” count for order completions).</td>
  <td name='samples'></td>
  <td name='libraries'></td>
  <td name='dilutions'></td>
  <td name='pool'></td>
  <td name='run'>🔷</td>
  <td name='container'></td>
  <td name='ordercomp'>🔷</td>
</tr>

<tr>
  <td name='syntax'>created:<a href="#date">DATE</a><br/>createdon:<a href="#date">DATE</a></td>
<td name='meaning'>Checks when this item was entered into MISO.</td>
<td name='samples'>🔷</td>
<td name='libraries'>🔷</td>
<td name='dilutions'>🔷</td>
<td name='pool'>🔷</td>
<td name='run'>🔷</td>
<td name='container'>🔷</td>
<td name='ordercomp'></td>
</tr>

<tr>
  <td name='syntax'>changed:<a href="#date">DATE</a><br/>modified:<a href="#date">DATE</a><br/>updated:<a href="#date">DATE</a><br/>changedon:<a href="#date">DATE</a><br/>modifiedon:<a href="#date">DATE</a><br/>updatedon:<a href="#date">DATE</a></td>
<td name='meaning'>Checks when any person last edited this item.</td>
<td name='samples'>🔷</td>
<td name='libraries'>🔷</td>
<td name='dilutions'>🔷</td>
<td name='pool'>🔷</td>
<td name='run'>🔷</td>
<td name='container'>🔷</td>
<td name='ordercomp'>🔷</td>
</tr>

<tr>
  <td name='syntax'>received:<a href="#date">DATE</a><br/>recieved:<a href="#date">DATE</a><br/>receivedon:<a href="#date">DATE</a><br/>recievedon:<a href="#date">DATE</a></td>
  <td name='meaning'>Checks whether this item has a received date that matches the provided date.</td>
  <td name='samples'>🔷</td>
  <td name='libraries'></td>
  <td name='dilutions'></td>
  <td name='pool'></td>
  <td name='run'></td>
  <td name='container'></td>
  <td name='ordercomp'></td>
</tr>

<tr>
  <td name='syntax'>createdby:<a href="#user">USER</a><br/>creator:<a href="#user">USER</a><br/>creater:<a href="#user">USER</a></td>
  <td name='meaning'>Check for items entered into MISO by a particular user.</td>
  <td name='samples'>🔷</td>
  <td name='libraries'>🔷</td>
  <td name='dilutions'>🔷</td>
  <td name='pool'>🔷</td>
  <td name='run'>🔷</td>
  <td name='container'>🔷</td>
  <td name='ordercomp'></td>
</tr>
<tr>
  <td name='syntax'>changedby:<a href="#user">USER</a><br/>modifier:<a href="#user">USER</a><br/>updater:<a href="#user">USER</a></td>
  <td name='meaning'>Checks for the last person to edit this item in MISO.</td>
  <td name='samples'>🔷</td>
  <td name='libraries'>🔷</td>
  <td name='dilutions'>🔷</td>
  <td name='pool'>🔷</td>
  <td name='run'>🔷</td>
  <td name='container'>🔷</td>
  <td name='ordercomp'></td>
</tr>
<tr>
  <td name='syntax'>platform:PLATFORM</td>
  <td name='meaning'>Check if this item is meant for a particular platform type: ILLUMINA, LS454, SOLID, IONTORRENT, PACBIO, OXFORDNANOPORE.</td>
  <td name='samples'></td>
  <td name='libraries'>🔷</td>
  <td name='dilutions'>🔷</td>
  <td name='pool'>🔷</td>
  <td name='run'>🔷</td>
  <td name='container'>🔷</td>
  <td name='ordercomp'>🔷</td>
</tr>
<tr>
  <td name='syntax'>index:<a href="#name">NAME</a><br/>index:SEQ</td>
  <td name='meaning'>Checks if this item has the index provided. The index can be a name or a DNA sequence. The sequence must be an exact match.</td>
  <td name='samples'></td>
  <td name='libraries'>🔷</td>
  <td name='dilutions'>🔷</td>
  <td name='pool'>🔷</td>
  <td name='run'>🔷</td>
  <td name='container'>🔷</td>
  <td name='ordercomp'>🔷</td>
</tr>
<tr>
  <td name='syntax'>class:<a href="#name">NAME</a></td>
  <td name='meaning'>Check if the item belongs to the sample class provided.</td>
  <td name='samples'>🔹</td>
  <td name='libraries'></td>
  <td name='dilutions'></td>
  <td name='pool'></td>
  <td name='run'></td>
  <td name='container'></td>
  <td name='ordercomp'></td>
</tr>
<tr>
  <td name='syntax'>institute:<a href="#name">NAME</a><br/>inst:<a href="#name">NAME</a></td>
  <td name='meaning'>Check if the item came from the institute mentioned. Currently, this only matches identities. It will be expanded in future.</td>
  <td name='samples'>🔹⚪</td>
  <td name='libraries'>🔹⚪</td>
  <td name='dilutions'>🔹⚪</td>
  <td name='pool'></td>
  <td name='run'></td>
  <td name='container'></td>
  <td name='ordercomp'></td>
</tr>
<tr>
  <td name='syntax'>external:<a href="#name">NAME</a><br/>ext:<a href="#name">NAME</a><br/>extern:<a href="#name">NAME</a></td>
  <td name='meaning'>Checks if an item came from the external identifier or external name. Currently, this only matches identities and tissues. It will be expanded in future.</td>
  <td name='samples'>🔹⚪</td>
  <td name='libraries'>🔹⚪</td>
  <td name='dilutions'>🔹⚪</td>
  <td name='pool'></td>
  <td name='run'></td>
  <td name='container'></td>
  <td name='ordercomp'></td>
</tr>
<tr>
  <td name='syntax'>box:<a href="#name">NAME</a></td>
  <td name='meaning'>	Checks if an item is located in a particular box. The name can either be the partial name or partial alias of the box.</td>
  <td name='samples'>🔷</td>
  <td name='libraries'>🔷</td>
  <td name='dilutions'>🔷</td>
  <td name='pool'>🔷</td>
  <td name='run'></td>
  <td name='container'></td>
  <td name='ordercomp'></td>
</tr>
</table>

# Formats

Many of the expressions above use <a href="#date">DATE</a>, <a href="#name">NAME</a>, or <a href="#user">USER</a>. The formats are defined below.

<a name="date">
## DATE

When the syntax specifies DATE, you can use one of the following formats.

| Format | Behaviour
|--------|----------
| now<br/>hour<br/>thishour<br/>lasthour | Filter from the 1 hour ago to the current time.
| today | Anything that happened on the current calendar day.
| yesterday | Anything on the last calendar day.
| thisweek | Filter from Monday 00:00:00 of the current week to the present time.
| lastweek | Filter from Monday 00:00:00 of the previous week to Sunday 23:59:59 of the previous week.
| *N*hours | Filter for anything from the current time to N hours ago.
| *N*days | Filter for anything from the current time to N*24 hours ago.
| YYYY-MM-DD | Search from YYYY-MM-DD 00:00:00 to YYYY-MM-DD 23:59:59

<a name="user">
## USER

When the syntax specifies USER, you can use one of the following formats.

| Format | Behaviour
|--------|----------
| me | Searches for the current user.
| Anything else | Assumed to be the user's login name (not their human name). This starts searching from the beginning, so “jrh” will match “jrhacker”, but “hacker” will not match “jrhacker”


<a name="name">
## NAME

<a href="#name">NAME</a> usually searches the 'Name' and 'Alias' fields for a particular entity and can be a partial match. What it actually searches depends on the syntax.
