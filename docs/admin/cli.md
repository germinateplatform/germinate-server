---
layout: doc
title: Germinate Command Line
subtitle: How to use the command line tools bundled with Germinate.
---

Starting with version 4.9.0 Germinate includes a proper Command-line Interface (CLI). This means that you can access certain functionalities of Germinate through a command-line terminal.

This CLI offers another way of accessing the data in addition to the web interface, the official Germinate API and the Breeding API (BrAPI).

Currently, the CLI offers options for:

<div id="auto-toc" class="toc" style="display: block;">
  <p class="toc-title"><strong>Import of Germinate data templates for</strong></p>
  <ul id="auto-toc-list">
    <li><a href="{{ '/cli/Germinate-import-mcpd.html' | relative_url }}">Germplasm MCPD</a></li>
    <li><a href="{{ '/cli/Germinate-import-trial.html' | relative_url }}">Trials data</a></li>
    <li>Genotypic data
      <ul>
        <li><a href="{{ '/cli/Germinate-import-genotype-excel.html' | relative_url }}">Excel format</a></li>
        <li><a href="{{ '/cli/Germinate-import-genotype-excel.html' | relative_url }}">Excel format transposed</a></li>
        <li><a href="{{ '/cli/Germinate-import-genotype-hapmap.html' | relative_url }}">Hapmap format</a></li>
        <li><a href="{{ '/cli/Germinate-import-genotype-flat.html' | relative_url }}">Flat text file format</a></li>
      </ul>
    </li>
    <li><a href="{{ '/cli/Germinate-import-climate.html' | relative_url }}">Climate data</a></li>
    <li><a href="{{ '/cli/Germinate-import-group.html' | relative_url }}">Groups</a></li>
    <li><a href="{{ '/cli/Germinate-import-image.html' | relative_url }}">Images</a></li>
    <li><a href="{{ '/cli/Germinate-import-pedigree.html' | relative_url }}">Pedigrees</a></li>
    <li><a href="{{ '/cli/Germinate-import-geotiff.html' | relative_url }}">Geotiffs</a></li>
    <li><a href="{{ '/cli/Germinate-import-shapefile.html' | relative_url }}">Shapefiles</a></li>
  </ul>
</div>

To use the Germinate CLI, you will need to be able to access the `germinate-<VERSION>.jar` file in the deployed application. If you are using Docker, this will be within the Docker container under `/usr/local/tomcat/webapps/ROOT/WEB-INF/lib`. If you're not using Docker, it will be located under `<TOMCAT_DIRECTORY>/webapps/<YOUR_INSTANCE_NAME>/WEB-INF/lib`.

In either case, you access the CLI using the command

```bash
java -jar germinate-<VERSION>.jar COMMAND
```

where `COMMAND` is one of the available commands through the CLI. There is more detailed information available on the <a href="{{ '/cli/Germinate.html' | relative_url }}">Germinate CLI manpage</a> documentation.