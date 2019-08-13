# Sequencing Orders

Sequencing orders are used to track which pools are ready to be sequenced, how much
sequencing needs to be done for a given pool, and whether that required
sequencing has been completed. To facilitate this, MISO has the idea of a
sequencing order that indicates how much sequencing needs to be performed and whether the
required sequencing has been completed.

Sequencing orders are based on a pool, sequencing platform, and the sequencing parameters.

Since sequencing orders are tied to a particular sequencing platform, the
technology-specific terminology (lanes, chips, flow cells) may be used instead
of the generic term "partitions".

## Outstanding Sequencing Orders List

The Outstanding Sequencing Orders page shows the orders that still require sequencing to be
performed. From the "Instrument Runs" list, click "Sequencing Orders - Outstanding". There are tabs for
each of the sequencing instrument platforms active in MISO.

There are columns for the number of matching partitions in different states. The
"Remaining" column indicates the number of "Requested" partitions minus all the
partitions from runs that have completed successfully or are currently running.
If the remaining number is marked with an asterisk, then in-progress partitions
have been subtracted from this number. See the _In-Progress Sequencing Orders List_ below.

Sequencing orders are only shown on this page if the "Remaining" column is greater than
zero.

## All Sequencing Orders List

This page shows all sequencing orders, including ones that are complete. The format is
similar to the "Outstanding Sequencing Orders" list. From the "Instrument Runs" list, click
"Sequencing Orders - All".

## In-Progress Sequencing Orders List

The In-Progress Sequencing Orders page is similar to the "Outstanding Sequencing Orders" list. From the
"Instrument Runs" list, click "Sequencing Orders - In-Progress". If a pool has been added to a
sequencing container, but that container has not been run, it is in a limbo
between being sequenced and not. This state is called _in-progress_ since the pool
has been loaded on a flow cell but not yet associated with the status of a
sequencing run. This page shows all of the sequencing orders in this intermediate state.

## Creating Sequencing Orders

Sequencing orders can be created on pools either for a single pool or for multiple pools at once.

{% assign subsub = 1 %}
{% include userman-heading.md section=page.section sub=sub subsub=subsub section-title=section-title title="Single

To create a sequencing order for a single pool, first navigate to the 
[Edit Pool Page](../pools/#editing-a-single-pool).

On the Edit Pool Page, scroll down to the "Requested Sequencing Orders" table and click
"Create" from the toolbar at the top of the table.

Select the platform, sequencing parameters, and partitions and click "Save".

### Bulk

On the [List Pools Page](../pools/#pool-list), check the boxes
beside the pools needing orders and click "Create Orders".

Select the platform, sequencing parameters, and partitions and click "Save".

## Extending a Sequencing Order

From the Edit Pool Page, scroll down to the "Requested Sequencing Orders" table. Check the
orders to be extended. From the toolbar at the top of the table, click "Order
More". In the dialog, add the number of additional partitions to be sequenced.
Click "Save".

## Deleting Sequencing Orders

From the Edit Pool Page, scroll down to the "Requested Sequencing Orders" table. Check the
orders to be deleted. From the toolbar at the top of the table, click "Delete".
