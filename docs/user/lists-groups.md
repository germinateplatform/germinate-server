---
layout: doc
title: Item Lists & Groups
subtitle: Creating session lists and persistent groups.
---

Item lists and groups are a core concept of Germinate. Both are basically subsets or sub-selections of either germplasm, markers or locations, but they differ in their accessibility to others.

- **Lists**: A list is a user selected subset of the database. This data for these lists is only stored in the browser, which means that it is available to the user who is currently logged in and only on the browser they are currently using. When you move to another browser or a different device altogether, the list won't be available there.
- **Groups**: Groups solve this issue by being persisted into the Germinate database. This feature is only available if Gatekeeper is used and the current user has a valid account. Groups can be configured to only be available to the user who created them or they can be made available to other users as well.

![Item Marking Dropdown]({{ '/assets/images/groups-lists/marking-dropdown.png' | relative_url }})
<p class="image-caption">Item Marking Dropdown: The top navigation menu item shows counts of currently selected items and takes you to the marked items details page.</p>

## Working with Lists

Lists are managed by utilising the `marking` feature of Germinate, which is introduced in more detail in the <a href="{{ '/user/germplasm#markingselecting-germplasm' | relative_url }}">germplasm section</a>. You can always see the total number of marked items across types and the type-specific counts in the dropdown menu in the top bar. Click on any of them to see all items on each list.

Overall there are three main ways to add and remove items from your lists:

- **Table marking**: Tables that allow item marking (tables showing data for germplasm, markers or locations) have a column at the end that allows marking items. The table column header can be used to bulk mark items currently visible in the table.
- **Selection in charts**: Some of the visualizations allow selection of data points. When items are selected, the overflow menu icon in the top right corner (`⋮`) can be clicked to show options for adding/removing the current selection to/from the marked item list.
- **Providing a list of identifiers**: If you already know which items you would like to add to the marked item list, you can either use the table filtering and select the `In set` comparison method or use the `Mark based on identifier` button in the marked item area of tables (top right).

### Table Marking {#table-marking}

Since tables offer extensive functionality for item filtering, they are the main vehicle for marking of said items. For example, you can use the germplasm table to find any entries that match your search criteria, then tell the table to add all matching entries to the list.

The example below shows the table filtered to only show *`Hordeum vulgare`* germplasm which has trials data. There are 324 matching elements which can now be added to the list.

![Item Marking Table]({{ '/assets/images/groups-lists/table-marking.png' | relative_url }})
<p class="image-caption">Item Marking Table: The table was filtered to find matching entries, then the table marking is used to add them to the current list.</p>

### Selection in Charts {#chart-marking}

Similar to tables, chart visualizations can be powerful mechanisms for the selection of items of interest. For example, you might want to download the genotypic data for germplasm that forms a cluster in a trait data visualization. Or you might want to export the trait data for germplasm that show up as massive outliers in the data. For charts that allow the selection of elements, the overflow menu `⋮` can be used to add/remove these to/from the list.

![Item Marking Chart]({{ '/assets/images/groups-lists/chart-marking.png' | relative_url }})
<p class="image-caption">Item Marking Chart: The selection made in the chart can be used to add or remove the germplasm contained within the selection to/from the list.</p>

### Providing a List of Identifiers

Tables that allow item marking also have a button that can be used to upload a list of identifiers 

1. To upload a list of identifiers, click on the marking button to the left of the marked item section of the table.
2. Enter a list of identifiers, for example the germplasm accession number or display name.
3. The table will be filtered to show any matches based on the provided identifiers.

![Item Marking Table Upload]({{ '/assets/images/groups-lists/table-marking-upload-1.png' | relative_url }})
<p class="image-caption">Item Marking Table Upload: This button opens the identifier upload.</p>

![Item Marking Table Upload]({{ '/assets/images/groups-lists/table-marking-upload-2.png' | relative_url }})
<p class="image-caption">Item Marking Table Upload: The selection made in the chart can be used to add or remove the germplasm contained within the selection to/from the list.</p>

![Item Marking Table Upload]({{ '/assets/images/groups-lists/table-marking-upload-3.png' | relative_url }})
<p class="image-caption">Item Marking Table Upload: The selection made in the chart can be used to add or remove the germplasm contained within the selection to/from the list.</p>

## Working with Groups

As mentioned above, groups are kind of like persistent lists that can be shared with other users. You can easily convert a list to a group or add all items from a group to your list.

Generally, groups can be used in all places where the currently active list can be used, e.g. data export and visualization highlighting.

### Converting a List to a Group

Converting a list to a group is easily done using the table marking mechanism. Click on the same button you would to mark all items in the table, then select `Create group`. A window will appear where you can name and describe this new group.

Once created, Germinate will navigate to the group details page so you can confirm the details.

![Item Marking Group Creation]({{ '/assets/images/groups-lists/table-marking-group-1.png' | relative_url }})
<p class="image-caption">Item Marking Group Creation: Use the table marking mechanism to create a new group from the current list.</p>

![Item Marking Group Creation]({{ '/assets/images/groups-lists/table-marking-group-2.png' | relative_url }})
<p class="image-caption">Item Marking Group Creation: Enter a name and a description (the type is pre-determined by the selected item type).</p>

![Item Marking Group Creation]({{ '/assets/images/groups-lists/groups.png' | relative_url }})
<p class="image-caption">Item Marking Group Creation: The groups page shows all groups visible to you. This example is pre-filtered to only show germplasm groups.</p>

![Item Marking Group Creation]({{ '/assets/images/groups-lists/groups-example.png' | relative_url }})
<p class="image-caption">Item Marking Group Creation: The groups details page shows the items contained within this group, who created it and whether it's visible to all other users.</p>


### Uploading into a Group

Uploading new entries into a group can be done directly via file upload. Navigate to the group details page and find the upload button below the table.

When clicked, a window will open where you can paste a list of identifiers (one per row) or drag and drop a `.txt` file with the identifiers. Clicking the upload button lets Germinate search for matches to the list of identifiers and add them to the current group.

![Item Marking Group Upload]({{ '/assets/images/groups-lists/groups-example.png' | relative_url }})
<p class="image-caption">Item Marking Group Upload: The group members table has an upload button at the bottom.</p>

![Item Marking Group Upload]({{ '/assets/images/groups-lists/groups-example.png' | relative_url }})
<p class="image-caption">Item Marking Group Upload: Paste in a list of identifiers (or drag and drop a .txt file) that you wish to add.</p>

![Item Marking Group Upload]({{ '/assets/images/groups-lists/groups-example.png' | relative_url }})
<p class="image-caption">Item Marking Group Upload: Germinate will run a search across the corresponding data type and add any matches to the group.</p>

## Using Lists or Groups in Data Export

The main purpose of building up lists and groups is to use the further down the line for data export or highlighting in visualizations. To this end, every place in Germinate where you can download data will offer you a selection box where you can decide if you want to export the whole dataset or just the data matching entries in a selection of groups or your list.

By default `All items` is selected. Change this to `Group selection` to pick a combination of groups and the marked item list. Note that the 

![Group Data Export]({{ '/assets/images/groups-lists/export-group-1.png' | relative_url }})
<p class="image-caption">Group Data Export: Germinate will run a search across the corresponding data type and add any matches to the group.</p>

![Group Data Export]({{ '/assets/images/groups-lists/export-group-2.png' | relative_url }})
<p class="image-caption">Group Data Export: Germinate will run a search across the corresponding data type and add any matches to the group.</p>