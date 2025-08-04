---
title: Germinate CLI documentation
---

# Germinate CLI documentation
Starting with version 4.9.0 Germinate includes a proper Command-line Interface (CLI). This means that you can access certain functionalities of Germinate through a command-line terminal.

This CLI offers another way of accessing the data in addition to the web interface, the official Germinate API and the Breeding API (BrAPI).

Currently, the CLI offers options for:

- Data import
  - Import of Germinate data templates for
    - <a href="cli/Germinate-import-mcpd.html">Germplasm MCPD</a>
    - <a href="cli/Germinate-import-trial.html">Trials data</a>
    - Genotypic data
      - <a href="cli/Germinate-import-genotype-excel.html">Excel format</a>
      - <a href="cli/Germinate-import-genotype-excel.html">Excel format transposed</a>
      - <a href="cli/Germinate-import-genotype-hapmap.html">Hapmap format</a>
      - <a href="cli/Germinate-import-genotype-flat.html">Flat text file format</a>
    - <a href="cli/Germinate-import-climate.html">Climate data</a>
    - <a href="cli/Germinate-import-group.html">Groups</a>
    - <a href="cli/Germinate-import-image.html">Images</a>
    - <a href="cli/Germinate-import-pedigree.html">Pedigrees</a>
    - <a href="cli/Germinate-import-geotiff.html">Geotiffs</a>
    - <a href="cli/Germinate-import-shapefile.html">Shapefiles</a>

To use the Germinate CLI, you will need to be able to access the `germinate-<VERSION>.jar` file in the deployed application. If you are using Docker, this will be within the Docker container under `/usr/local/tomcat/webapps/ROOT/WEB-INF/lib`. If you're not using Docker, it will be located under `<TOMCAT_DIRECTORY>/webapps/<YOUR_INSTANCE_NAME>/WEB-INF/lib`.

In either case, you access the CLI using the command

```bash
java -jar germinate-<VERSION>.jar COMMAND
```

where `COMMAND` is one of the available commands through the CLI. There is more detailed information available on the <a href="cli/Germinate.html">Germinate CLI manpage</a> documentation.