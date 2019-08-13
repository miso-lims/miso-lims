# Workflows

Workflows are series of pre-defined steps that transition one or more items from a defined starting status to
a defined end status. They represent a series of steps which would be completed in the lab in order to do some
amount of work. They are designed to simplify input by allowing for barcode scanning wherever possible.

## Workflows Widget

The Workflows widget is accessible on the MISO Home Screen. The Workflows (figure {{figure}}) widget displays
your incomplete workflows, favourite workflows, an option to begin a new workflow, and an option to manage
favourite workflows. Once a workflow is complete, it no longer appears in the Workflows widget.

{% include userman-figure.md num=figure cap="Workflows widget" img="workflows-widget.png
{% assign sub = sub | plus: 1 %}
{% assign figure = figure | plus: 1 %}
## Managing Favourite Workflows

If you perform a workflow frequently, you may wish to add it to your favourite workflows. This will allow you
to begin a new workflow of this type by clicking a single item in the Workflows widget.

To add a new workflow to your favourites, go to the Workflows widget and click the "Edit Favourite Workflows"
item at the bottom of the widget. In the dialog box which opens, click the "Add ... to Favourites" link, where
"..." is the name of the workflow you wish to add. The Home page will reload, and a "Begin New ... Workflow"
item will be added to the Workflows widget (figure {{figure}}).

{% include userman-figure.md num=figure cap="Workflows widget with favourite workflow added"
img="new-favourite-workflow.png

To remove a workflow from your favourites, go to the Workflows widget and click the "Edit Favourite Workflows"
item at the bottom of the widget. In the dialog box which opens, click the "Remove ... from Favourites" link,
where "..." is the name of the workflow you wish to remove. The Home page will reload, and the Workflows
widget will no longer contain a "Begin New ... Workflow" item.

## Beginning a New Workflow

To begin a new workflow, go to the Workflows widget. If the workflow you wish to begin is one of your favourite
workflows, click on the "Begin New ... Workflow" item in the widget, where "..." is the name of the workflow.

If the workflow you wish to begin is not one of your favourite workflows, click on the "Begin New Workflow"
item in the Workflows widget. In the dialog box which opens, click the name of the workflow you wish to begin.

{% assign sub = sub | plus: 1 %}
{% assign figure = figure | plus: 1 %}
## Entering Workflow Data

Workflow data can be entered by scanning (using a hand-held scanner) or typing in the information for each
step (see figure {{figure}}).

{% include userman-figure.md num=figure cap="Entering data for a workflow" img="workflow-steps.png

Workflows are designed to have data entered in a given order. If data from the previous step must be
re-entered, click the "Undo" button (near-circle with arrow end, or the icon in the "Selected Flow Cell Model
'PRO-001'" step in figure {{figure}}) on the right side of the screen to re-enter information from that step.
If data from an earlier step in the workflow must be changed, click the "Go Back" button (arrow pointing to
the left, or the icon in the "Selected new Sequencing Container 'CONTAINER'" step in figure {{figure}}) on the
right side of the screen at the target step. If any of these buttons are clicked, you will be brought back to
the target step and will be able to re-enter your data. If you selected "Go Back", you must re-enter all data
from that point in the workflow onwards.

If you exit the workflow at any time before completing it, you will be able to 
[resume the workflow](#resuming-an-incomplete-workflow) at the same step you left off. No data will be created
or saved to MISO until the entire workflow is completed.

## Resuming an Incomplete Workflow

If you navigate away from the workflow page before completing the workflow, it is possible to re-enter the
workflow where you left off.

To resume an incomplete workflow, go to the Workflows widget and click on the incomplete workflow you wish to
resume. You will be brought to the workflow page at the same step as you had left it.
