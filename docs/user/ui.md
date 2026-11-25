---
layout: doc
title: User interface & Basics
subtitle: Use the Germinate UI, browse and search data, mark interesting items.
---

## User Interface Overview

![Home Screen]({{ '/assets/images/ui/home.png' | relative_url }})
<p class="image-caption">Home Screen: The landing page of Germinate.</p>

## Main Navigation

Germinate's main navigation sits along the left side of the screen or is hidden away behind a button toggle on smaller screens. It contains a hierarchical structure with links to all the data pages available on Germinate. Thematically, the `Browse` item is broken down into the different data types that Germinate supports with each offering a selection of different sub-pages.

## Top menu

Additionally, there is a menu along the top of the screen. This menu contains useful functionalities like a global search, marked item indicator, language toggle, theme toggle, user menu and import/export job toggle.

## Global Search

While tables offer their own way of searching for things (see below), Germinate offers a database-wide search function that will let you search for keywords and present any matching results broken down per data type.

You can access the global search either from the `Search` menu item in the main navigation or the magnifying glass icon in the top bar - both will take you to the search page.

On the global search page, you can see all results for the current query. The coloured chips show which data types are being searched (all enabled by default). Below that, the coloured tabs let you select which data type you want to look at (germplasm data by default). Finally, for each data type, there is a table displaying data of this selected type matching your search query.

The search results are based on *exact* matches for a pre-defined selection of columns. For example, when searching germplasm data, the following columns are searched:

`Id`, `Germplasm name`, `Display name`, `General identifier`, `Number`, `Persistent Unique ID (PUID)`, `Biological Status`, `Genus`, `Species`, `Subtaxa`, `Synonyms`, `Location`, `Country`

For trials data the following columns are searched:

`Germplasm name`, `Display name`, `General identifier`, `Synonyms`, `Location`, `Country`, `Variable name`, `Trait name`, `Treatment`, `Trait value`

If a match is found in any of these items, a result will show up in the table.

## Tables {#tables}

Tables play a central role on the Germinate user interface - they are one of the main ways in which data is displayed and made explorable. To facilitate this, tables on Germinate have to offer a large number of functionalities including searching, sorting, filtering, selection, and downloading.

![Germplasm Data Table]({{ '/assets/images/ui/table.png' | relative_url }})
<p class="image-caption">Germplasm Data Table: An example of a table showing data on the Germinate user interface.</p>

### Column Selection

Tables often show a large number of columns to show as much information about the entity in question. The columns that are shown by default are pre-defined by the Germinate administrators. However, each user can adjust the visible columns per table by using a drop down menu and toggling elements in that list.

### Display Type

While all tables can display information in a tabular form, some can switch the display to a `Card` form where each item is represented as a card in a grid. Both show the same data, just in different ways. The grid will usually highlight the important information in a larger fashion than the table, e.g. the image table will emphasise the image and display it big at the top while the table display type gives more equal measure to all columns.

Filtering is available on both types, but column sorting and column selection are restricted to the table display type for obvious reasons.

![Germplasm Data Table Display Type Table]({{ '/assets/images/ui/table-display-table.png' | relative_url }})
<p class="image-caption">Germplasm Data Table Display Type Table: The default display type for many tables is a tabular form.</p>

![Germplasm Data Table Display Type Grid]({{ '/assets/images/ui/table-display-grid.png' | relative_url }})
<p class="image-caption">Germplasm Data Table Display Type Grid: Some can be switched to a grid layout.</p>

### Table Filtering

All tables on Germinate allow you to apply filtering conditions to any of the columns you see. These conditions can be combined using the `All` or `Some` toggles - either for the overall condition or even within condition groups - which allows for very powerful combinations.

![Germplasm Data Table Filter]({{ '/assets/images/ui/table-filter.png' | relative_url }})
<p class="image-caption">Germplasm Data Table Filter: Filtering tables can be done with complex combinations of conditions.</p>

The example shown in the figure above combines two different filter groups:

- **Taxonomy filtering**: In this case *both* conditions on `Genus` and `Species` have to match.
- **Germplasm name filtering**: The germplasm name has to be *either* `Sebastian` (exact) or start with the letter `O`.

The overall condition combines these two groups with an `All` condition meaning *both* have to be true. The result is shown below and contains only *`Hordeum vulgare`* germplasm which is either called `Sebastian` or where the name starts with an `O`.

![Germplasm Data Table Filter Result]({{ '/assets/images/ui/table-filter-result.png' | relative_url }})
<p class="image-caption">Germplasm Data Table Filter Result: The table shows results for the filter conditions.</p>

### Quick Search

In addition to the complex filtering, many tables on Germinate offer a quick search box at the top of the table which automatically applies the filter term to a pre-defined set of columns. 

![Germplasm Data Table Quick Search]({{ '/assets/images/ui/table-quick-search.png' | relative_url }})
<p class="image-caption">Germplasm Data Table Quick Search: An example of using the quick search field to search for `yield` traits.</p>

Quick searching a table means filtering multiple columns which may contain the search term. Germinate uses different combinations of columns to achieve this per data type. For this trait example, the actual filter that is automatically assembled for the quick search is shown below.

![Germplasm Data Table Quick Search Filter]({{ '/assets/images/ui/table-quick-search-filter.png' | relative_url }})
<p class="image-caption">Germplasm Data Table Quick Search Filter: The actual filter that's automatically put together.</p>

### Item marking

Germplasm, markers and locations can be added to user-specific lists while exploring Germinate. This mechanism allows you to search for and select only the items you are interested in. The marked item list can later be used to highlight these items in visualizations or to download data for just this subset. To add or remove items from a list, tables that show data for germplasm, markers or locations have a column at the end that has a toggle per entry. The header for this column allows marking of all elements currently visible in the table (after filtering has been applied). So if you are looking for specific elements, search and filter the table down to the items of interest, then select them using the last column (or the header) to add them to the list. The number of items currently on the list is displayed in the top right hand side of the table.

![Germplasm Data Table Item Marking]({{ '/assets/images/ui/table-marking.png' | relative_url }})
<p class="image-caption">Germplasm Data Table Item Marking: Marking items in a table.</p>

## Visualizations

Another big part of Germinate's functionality are various kinds of visualizations. These come in various shapes and forms, but they share some very common principles:

- Interactivity.
- Allow download in SVG and/or PNG format.
- Allow download of underlying data file.
- Allow changing of colour scheme.
- Some of them allow marking/unmarking items.

All of these options can be accessed from the overflow menu icon in the top right corner: `⋮`

![Visualization Drop Down]({{ '/assets/images/ui/visualization-dropdown.png' | relative_url }})
<p class="image-caption">Visualization Drop Down: Options for interacting with charts including downloads, changing colours and marking items.</p>