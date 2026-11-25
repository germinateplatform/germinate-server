---
layout: doc
title: Item Lists & Groups
subtitle: Creating session lists and persistent groups.
---

Item lists and groups are a core concept of Germinate. Both are basically subsets or sub-selections of either germplasm, markers or locations, but they differ in their accessibility to others.

- **Lists**: A list is a user selected subset of the database. This data for these lists is only stored in the browser, which means that it is available to the user who is currently logged in and only on the browser they are currently using. When you move to another browser or a different device altogether, the list won't be available there.
- **Groups**: Groups solve this issue by being persisted into the Germinate database. This feature is only available if Gatekeeper is used and the current user has a valid account. Groups can be configured to only be available to the user who created them or they can be made available to other users as well.


## Working with Lists

Lists are managed by utilising the `marking` feature of Germinate, which is introduced in more detail in the <a href="{{ '/user/germplasm#markingselecting-germplasm' | relative_url }}">germplasm section</a>.

Overall there are three main ways to add and remove items from your lists:

- **Table marking**: Tables that allow item marking (tables showing data for germplasm, markers or locations) have a column at the end that allows marking items. The table column header can be used to bulk mark items currently visible in the table.
- **Selection in charts**: Some of the visualizations allow selection of data points. When items are selected, the overflow menu icon in the top right corner (`⋮`) can be clicked to show options for adding/removing the current selection to/from the marked item list.
- **Providing a list of identifiers**: If you already know which items you would like to add to the marked item list, you can either use the table filtering and select the `In set` comparison method or use the `Mark based on identifier` button in the marked item area of tables (top right).

### Table Marking {#table-marking}

Since tables offer extensive functionality for item filtering, they are the main vehicle for marking of said items. For example, you can use the germplasm table to find any entries that match your search criteria, then tell the table to add all matching entries to the list.

The example below shows the table filtered to only show *`Hordeum vulgare`* germplasm which has trials data. There are 324 matching elements which can now be added to the list.

![Item Marking Table]({{ '/assets/images/groups-lists/table-marking.png' | relative_url }})
<p class="image-caption" markdown="1">Item Marking Table: The table was filtered to find matching entries, then the table marking is used to add them to the current list.</p>

### Selection in Charts {#chart-marking}

Similar to tables, chart visualizations can be powerful mechanisms for the selection of items of interest. For example, you might want to download the genotypic data for germplasm that forms a cluster in a trait data visualization. Or you might want to export the trait data for germplasm that show up as massive outliers in the data. For charts that allow the selection of elements, the overflow menu `⋮` can be used to add/remove these to/from the list.

![Item Marking Chart]({{ '/assets/images/groups-lists/chart-marking.png' | relative_url }})
<p class="image-caption" markdown="1">Item Marking Chart: The selection made in the chart can be used to add or remove the germplasm contained within the selection to/from the list.</p>

### Providing a List of Identifiers

Tables that allow item marking also have a button that can be used to upload a list of identifiers 

1. To upload a list of identifiers, click on the marking button to the left of the marked item section of the table.
2. Enter a list of identifiers, for example the germplasm accession number or display name.
3. The table will be filtered to show any matches based on the provided identifiers.

![Item Marking Table Upload]({{ '/assets/images/groups-lists/table-marking-upload-1.png' | relative_url }})
<p class="image-caption" markdown="1">Item Marking Table Upload: This button opens the identifier upload.</p>

![Item Marking Table Upload]({{ '/assets/images/groups-lists/table-marking-upload-2.png' | relative_url }})
<p class="image-caption" markdown="1">Item Marking Table Upload: The selection made in the chart can be used to add or remove the germplasm contained within the selection to/from the list.</p>

![Item Marking Table Upload]({{ '/assets/images/groups-lists/table-marking-upload-3.png' | relative_url }})
<p class="image-caption" markdown="1">Item Marking Table Upload: The selection made in the chart can be used to add or remove the germplasm contained within the selection to/from the list.</p>

## Working with Groups

### Converting a List to a Group

### Uploading into a Group

## Using Lists or Groups in Data Export