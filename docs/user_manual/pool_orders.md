# Pool Orders

Pool orders are used to track sets of library aliquots that are ready to be pooled. It may be useful for people who are
responsible for creating libraries to then create a pool orders to pass requirements on to other people who are
responsible for creating pools. Pool orders are intended to help organize separate teams, and using them is completely
optional.

A pool order specifies which library aliquots should be pooled, and at what proportions. To fulfill a pool order, a
pool containing all of the specified aliquots must be linked to it. If the pool contains an aliquot derived from the
specified aliquot, that is also accepted. A pool can be used to fulfill the pool order even if it contains additional
aliquots, or if the proportions do not match, though a warning will be shown in the latter case.

Optionally, a pool order may specify sequencing requirements. If sequencing requirements are specified, a sequencing
order must also be linked to the pool order in order for the pool order to be fulfilled. See the
[Sequencing Orders](../sequencing_orders/) section for more information on sequencing orders and sequencing
requirements.

A pool order may be marked as draft to indicate that the requirements are not yet confirmed. The draft status must be
removed before a pool order can be fulfilled.


## Pool Orders List

To get to the Pool Orders list page, click "Pool Orders" in the Preparation list in the navigation menu. Pool orders are
divided into tabs depending on their status. A pool order's status may be

* Outstanding: A pool and/or sequencing order (if applicable) is not yet linked to the pool order
* Fulfilled: A pool and sequencing order (if applicable) has been linked to the pool order
* Draft: The pool order is marked as draft

You can click on a pool order ID or alias in the list to get to the Edit Pool Order page. The toolbar at the top of the
list provides other commands for working with pool orders.


## Creating Pool Orders

The easiest way to create a pool order is from the Library Aliquots list. Select the aliquots that you would like to
include in the order and click the "Create Order" button in the toolbar at the top of the list. This will take you to
the Create Pool Order page. Fill in the order information in the top section of the page. Below, you'll see a table
containing the library aliquots you selected. By default, they are all included at a 1:1 ratio. You can click the Edit
Proportions button at the top of the table to modify this. When you are done, click the Save button at the top of the
page.

You can also get to the Create Pool Order page from the Pool Orders list by clicking the Add button in the toolbar at
the top of the list. In this case, you will have to add the library aliquots using the Add button at the top of the
Library Aliquots table at the bottom of the page. This will allow you to search for aliquots by entering multiple names,
aliases, or barcodes.


## Fulfilling Pool Orders

A pool must be linked to the pool order in order to fulfill it. If the pool order specifies sequencing requirements,
then a sequencing order must also be linked. You can create and link pools and sequencing orders from the Edit Pool
Order page.


## Creating a Pool

You can create a pool containing all of the required aliquots at the specified proportions and link it to the pool order
in one step from the Edit Pool Order page. If the pool order has been saved and the order is not marked as a draft, a
Create Pool button will appear near the bottom of the Order Information section. Clicking this button will bring up a
dialog to confirm the aliquots and proportions for the pool. After you've confirmed, another dialog will appear for you
to enter the pool information. After completing the pool information, click Save and the pool will be created and linked
to the order.


## Linking an Existing Pool

If you have already created a pool containing the required aliquots (or aliquots derived from those), you can link it to
the order using the Link Pool button near the bottom of the Order Information section. This will open a dialog where you
can enter the pool name, alias, or barcode to search. Find and select a pool to link it to the order.


## Creating a Sequencing Order

If the pool order specifies sequencing requirements and a pool is already linked to the order, a Create Sequencing Order
button will appear near the bottom of the Order Information section. Clicking this button will open a dialog where you
can confirm the sequencing order details and optionally enter a description. Click Save to create the sequencing order
and link it to the pool order.


## Linking an Existing Sequencing Order

If the pool order specifies sequencing requirements and a pool is already linked to the order, a Link Sequencing Order
button will appear near the bottom of the Order Information section. Click this button to search for a sequencing order
with matching requirements, and link it to the pool order.


## Deleting Pool Orders

To delete pool orders, go to the Pool Orders list. Select the pool orders you wish to delete, then click the Delete
button in the toolbar at the top of the list. Only unfulfilled pool orders can be deleted, and only by the order's
creator or a MISO administrator.
