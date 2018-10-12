---
layout: page
title: "12. Orders"
section: 12
---

Orders are used to track which pools are ready to be sequenced, how much
sequencing needs to be done for a given pool, and whether that required
sequencing has been completed. To facilitate this, MISO has the idea of an
order that indicates how much sequencing needs to be performed and whether the
required sequencing has been completed.

Orders are based on a pool, sequencing platform, and the sequencing parameters.

Since orders are tied to a particular sequencing platform, the
technology-specific terminology (lanes, chips, flow cells) may be used instead
of the generic term "partitions".

{% assign sub = 1 %}
{% include userman-heading.md section=page.section sub=sub section-title=section-title title="Active Orders List" %}

The active orders page shows the orders that still require sequencing to be
performed. From the "Tracking" list, click "Active Orders". There are tabs for
each of the sequencing instrument platforms active in MISO.

There are columns for the number of matching partitions in different states. The
"Remaining" column indicates the number of "Requested" paritions minus all the
partitions from runs that have completed successfully or are currently running.
If the remaining number is marked with an asterisk, then pending partitions
have been subtracted from this number. See the _Pending Orders List_ below.

Orders are only shown on this page if the "Remaining" column is greater than
zero.

{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub section-title=section-title title="All Orders List" %}

This page shows all orders, including ones that are complete. The format is
similar to the "Active Orders" list. From the "Tracking List", click "All
Orders".

{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub section-title=section-title title="Pending Orders List" %}

The pending order page is similar to the "Active Orders" list. From the
"Tracking List", click "Pending Orders". If a pool has been added to a
sequencing container, but that container has not been run, it is in a limbo
between being sequenced and not. This state is called _pending_ since the pool
has been loaded on a flow cell but not yet associated with the status of a
sequencing run. This page shows all of the orders in an intermediate state.

{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub section-title=section-title title="Creating Orders" %}

Orders can be created on pools either for a single pool or for multiple pools at once.

{% assign subsub = 1 %}
{% include userman-heading.md section=page.section sub=sub subsub=subsub section-title=section-title title="Single" %}

To create an order for a single pool, first navigate to the [Edit Pool
Page](pools.html#editing_a_single_pool).

On the Edit Pool Page, scroll down to the "Request Orders" table and click
"Create" from the toolbar at the top of the table. Select the sequencing
platform from the dialog. In the next dialog, select the sequencing parameters
and the number of partitions required. An optional description maybe be provided.
Click "Save".

{% assign subsub = subsub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub subsub=subsub section-title=section-title title="Bulk" %}

On the [List Pools Page](pools.html#pool_list), check the boxes
beside the pools needing orders and click "Create Orders".

Select the platform, sequencing parameters, and partitions and click "Save".

{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub section-title=section-title title="Extending an Order" %}

From the Edit Pool Page, scroll down to the "Request Orders" table. Check the
orders to be extended. From the toolbar at the top of the table, click "Order
More". In the dialog, add the number of additional partitions to be sequenced.
Click "Save".

{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub section-title=section-title title="Deleting Orders" %}

From the Edit Pool Page, scroll down to the "Request Orders" table. Check the
orders to be deleted. From the toolbar at the top of the table, click
"Delete".

{% include userman-toplink.md %}

