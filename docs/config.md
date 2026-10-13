---
title: Germinate Configuration
---

# Germinate Configuration
This page describes the content of Germinate's configuration folder. This folder does not exist and has to be created somewhere on the system where Germinate can access it.
The structure of this folder is shown below. At a minimum, the `config.properties` file needs to exist. The logos under `images/template` are used on the interface and should be adjusted to meet your requirements.


```
+-- data
|   +-- allelefreq
|   +-- download
|   +-- genotypes
+-- images
|   +-- climate
|   +-- database
|   +-- news
|   +-- template
|       +-- crop.svg
|       +-- logo.svg
|       +-- logo-horizontal.svg
+-- template
|   +-- carousel.json
|   +-- en_GB.json
|   +-- locales.json
+-- config.properties
```

- `data`  
This directory contains all the raw data files that Germinate uses. At the moment those are genotypic data files and allele frequency files. These files are referenced in the database from the `datasets.source_file` column.
  - `allelefreq`  
  Files in this folder are stored in tab-delimited format with the germplasm along the side and markers along the top. Each cell contains the allele frequency value or should be empty.
  - `download`  
  This folder is auto-generated to store file resources. Do not touch it or the files within it.
  - `genotypes`  
  Genotypic data is stored in a custom `.hdf5` format. If you're using the Germinate Data Templates, then a file in this format will automatically be generated for you.
- `images`  
This directory contains all images that the Germinate user interface uses. This includes images referenced from the database as well as images that are part of the user interface template.
  - `climate`  
  Climate images are images referenced from the `climateoverlays` table. They are image overlays shown on top of maps on the user interface.
  - `database`  
  This directory contains image files referenced from the `images` table in Germinate. Germplasm images can be uploaded by data curators and administrators from the passport page of the corresponding germplasm. Images uploaded this way will automatically be placed here.
  - `news`  
  Images referenced from the `news` table are stored in this directory. News items can be created by data curators or administrators from the home page of Germinate. Images uploaded this way will automatically be placed here.
  - `template`  
  This directory contains images referenced from the user interface. This includes the logo (`logo.svg` and `logo-horizontal.svg`) and crop image (`crop.svg`) as well as images used in the carousel on the dashboard.
- `template`  
This directory holds configuration files for the Germinate user interface.
  - `locales.json`  
  This file defines the user locales or languages that should be available for this copy of Germinate. Here is an example that defines *British English* and *German* as the two available languages. Nore that the `flag` property should be the [ISO 3166-1 alpha-2](https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2) code for the that best represents that locale. The `locale` properties is always of the form `{ISO 639-1}_{ISO 3166-1 alpha-2}` ([ISO 639-1](https://en.wikipedia.org/wiki/ISO_639-1), [ISO 3166-1 alpha-2](https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2))
  
      ```json
      [{
        "locale": "en_GB",
        "flag": "gb",
        "name": "British English"
      }, {
        "locale": "de_DE",
        "flag": "de",
        "name": "Deutsch - Deutschland"
      }]
      ```
  - `carousel.json`  
  This file contains the configuration for the image carousel on the Germinate carousel. Here is an example of such a file. Note that the images referenced here would have to be stored in the `images/template` directory. The key of each set of images (e.g. `de_DE`) should always be one of the locales defined in the `locales.json` file.  
      ```json
      {
        "de_DE": [
          {
            "name": "fullimage1.jpg",
            "text": "Germinate ist eine generische Datenbank für pflanzengenetische Ressourcen die Hilfsmittel für die Aufbewahrung von Standardsammlungsinformationen und Pflanzenausweisdaten sowie kompliziertere Datentypen wie phänotypische, genotypische und Feldexperimentdaten bereitstellt."
          },
          {
            "name": "fullimage2.jpg",
            "text": "Wir fokussieren uns auf die Bereitstellung von Lösungen für aktuelle biologische Herausforderungen und entwickeln neuartige Visualisierungen und Benutzeroberflächen die es Benutzern erlauben mehr aus ihren Daten zu machen."
          }
        ],
        "en_GB": [
          {
            "name": "fullimage1.jpg",
            "text": "Germinate is a generic plant genetic resources database and offers facilities to store both standard collection information and passport data along with more advanced data types such as phenotypic, genotypic and field trial data."
          },
          {
            "name": "fullimage2.jpg",
            "text": "We focus on delivering solutions to current biological problems and develop novel visualizations and interfaces to allow users to get more from their data."
          }
        ]
      }
      ```
  - `en_GB.json`  
  This is an example file of one of the possible locale files. Germinate always supports the locale `en_GB` (British English), but also any other locale defined in the `locales.json` file. Files like `en_GB.json` allow you to define custom text within this language. For British English, it would suffice to only specify the text you want to change. For any other language, you'll have to provide your own file (e.g. `de_DE.json`) containing all the text on the interface. A list of all entries can be found in [a dedicated repository](https://github.com/germinateplatform/germinate-i18n).
- `config.properties`  
This is the most important file in your customization. It defines the required properties for Germinate without which it couldn't work. This includes properties like the database details and user credentials as well as various customization options.

```ini
# Database server host
database.server = <string>
# Name of the database
database.name = <string>
# Database user name
database.username = <string>
# Database password (optional, depending on your configuration)
database.password = <string>
# Database port (only needed if not 3306)
database.port = <integer>
# Create automatic database backups every X days (optional); stored under <GERMINATE_FOLDER>/backups
database.backup.every.days = <integer>
# Maximum disk space (in GB) that Germinate backups may take up (optional)
database.backup.max.size.gb = <integer>

# Base URL of the client, so the server can generate files that link back to the user interface, e.g. https://ics.hutton.ac.uk/germinate-demo/
germinate.client.url = <url>

# Base URL of Gatekeeper (if used)
gatekeeper.url = <url>
# Username of a Gatekeeper user with admin permissions for Gatekeeper (not Germinate)
gatekeeper.username = <string>
# Password of that Gatekeeper user
gatekeeper.password = <string>
# Should new users be able to register for an account?
gatekeeper.registration.enabled = <boolean>
# If registration is enabled, does it require approval from an administrator?
gatekeeper.registration.requires.approval = <boolean>
# Salt value (work factor) used for bcrypt password hashing. Higher is more secure but slower. Default is 10
bcrypt.salt = <integer>

# Primary color of the user interface (hex code, e.g. #00acef)
color.primary = <hex color>
# Colors used for charts (comma-separated hex codes)
colors.charts = <comma-separated list of hex colors>
# Colors used for the user interface (comma-separated hex codes)
colors.template = <comma-separated list of hex colors>
# Colors used for gradients (comma-separated hex codes)
colors.gradient = <comma-separated list of hex colors>

# Directory containing the configuration files. Should be /data/germinate when using the Docker image
data.directory.external = <path>

# Authentication mode: NONE disables authentication (and Gatekeeper), FULL requires login to see any data, SELECTIVE only requires login for features that alter the database
authentication.mode = <SELECTIVE | FULL | NONE>

# Data import mode: NONE disables verification and upload, VERIFY allows checking of templates, IMPORT additionally allows importing data after verification
data.import.mode = <IMPORT | VERIFY | NONE>

# Column of the germinatebase table used to link to an external resource from the marked germplasm page
external.link.identifier = <string>
# Template for the external link. Must include {identifiers}, e.g. https://www.google.co.uk/search?q={identifiers}
external.link.template = <string>

# Hours after which files created by async import/export tasks are deleted
files.delete.after.hours.async = <integer>
# Hours after which temporary files (chart files, synchronous downloads, etc.) are deleted
files.delete.after.hours.temp = <integer>

# Google Analytics key (analytics is enabled if set)
google.analytics.key = <string>

# Plausible Analytics: your site's domain as declared in Plausible's settings
plausible.domain = <string>
# Plausible Analytics: should hash mode be used?
plausible.hash.mode = <boolean>
# Plausible Analytics: API host to use (change this if self-hosting)
plausible.api.host = <url>

# Dashboard categories to show (comma-separated): germplasm, markers, maps, traits, climates, compounds, locations, datasets, datasetsGenotype, datasetsTrials, datasetsAllelefreq, datasetsClimate, datasetsCompound, experiments, groups, images, fileresources
dashboard.categories = <comma-separated list of strings>
# Dashboard information sections to show (comma-separated): publications, news, projects, dataupdates, datastories
dashboard.sections = <comma-separated list of strings>
# Pages to hide from the user interface (see https://github.com/germinateplatform/germinate-vue/blob/master/src/mixins/pages.js)
hidden.pages = <comma-separated list of strings>
# Auto-discover which pages to show based on the data available in the database
hidden.pages.autodiscover = <boolean>

# Table column hiding only takes effect the first time Germinate is loaded, as users may later choose to show/hide different columns.
# Values are comma-separated column keys from the client components at https://github.com/germinateplatform/germinate-vue/tree/master/src/components/tables
# Columns to hide in the germplasm table
hidden.columns.germplasm = <comma-separated list of strings>
# Columns to hide in the germplasm attributes table
hidden.columns.germplasm.attributes = <comma-separated list of strings>
# Columns to hide in the images table
hidden.columns.images = <comma-separated list of strings>
# Columns to hide in the climates table
hidden.columns.climates = <comma-separated list of strings>
# Columns to hide in the climate data table
hidden.columns.climate.data = <comma-separated list of strings>
# Columns to hide in the compounds table
hidden.columns.compounds = <comma-separated list of strings>
# Columns to hide in the compound data table
hidden.columns.compound.data = <comma-separated list of strings>
# Columns to hide in the comments table
hidden.columns.comments = <comma-separated list of strings>
# Columns to hide in the file resources table
hidden.columns.fileresources = <comma-separated list of strings>
# Columns to hide in the maps table
hidden.columns.maps = <comma-separated list of strings>
# Columns to hide in the markers table
hidden.columns.markers = <comma-separated list of strings>
# Columns to hide in the map definitions table
hidden.columns.map.definitions = <comma-separated list of strings>
# Columns to hide in the datasets table
hidden.columns.datasets = <comma-separated list of strings>
# Columns to hide in the dataset attributes table
hidden.columns.dataset.attributes = <comma-separated list of strings>
# Columns to hide in the experiments table
hidden.columns.experiments = <comma-separated list of strings>
# Columns to hide in the entities table
hidden.columns.entities = <comma-separated list of strings>
# Columns to hide in the groups table
hidden.columns.groups = <comma-separated list of strings>
# Columns to hide in the institutions table
hidden.columns.institutions = <comma-separated list of strings>
# Columns to hide in the locations table
hidden.columns.locations = <comma-separated list of strings>
# Columns to hide in the pedigrees table
hidden.columns.pedigrees = <comma-separated list of strings>
# Columns to hide in the pedigree definitions table
hidden.columns.pedigreedefinitions = <comma-separated list of strings>
# Columns to hide in the traits table
hidden.columns.traits = <comma-separated list of strings>
# Columns to hide in the trials data table
hidden.columns.trials.data = <comma-separated list of strings>
# Columns to hide in the collaborators table
hidden.columns.collaborators = <comma-separated list of strings>
# Columns to hide in the publications table
hidden.columns.publications = <comma-separated list of strings>

# Should the comments feature be enabled?
comments.enabled = <boolean>
# Should the PDCI be calculated and shown on the user interface?
pdci.enabled = <boolean>
# Enable the BrAPI endpoints
brapi.enabled = <boolean>
# Show the GDPR compliance banner. Users can deny cookies, which disables some features. Enable if expecting users from the EU
gdpr.notification.enabled = <boolean>
# Show the link to Hutton Unearthed on the "About Germinate" page
donations.section.enabled = <boolean>
# URL of the GridScore instance used with Germinate (defaults to the official URL)
gridscore.url = <url>
# URL of the Helium instance used with Germinate (defaults to the official URL)
helium.url = <url>
# URL of the FielDHub instance used with Germinate (defaults to the official URL)
fieldhub.url = <url>
# URL of GenesysPGR for ordering material
genesys.url = <url>
# Client ID provided by Genesys for this instance of Germinate
genesys.client.id = <string>
# Client secret provided by Genesys for this instance of Germinate
genesys.client.secret = <string>
# Custom path to the MySQL Dump executable for database backups. Don't use this with the Germinate Docker image
mysqldump.path = <path>
```

# Gatekeeper Configuration

If you're using Gatekeeper for user authentication or you would like to use the data upload mechanisms offered by Germinate, then Gatekeeper needs to be properly configured.
Please follow the documentation at https://germinateplatform.github.io/gatekeeper-server/ to set up Gatekeeper.

Once running, you will need to create a new user in Gatekeeper that Germinate can use to authenticate itself. To this end, create a new user with `Administrator` permissions for the default Gatekeeper (not Germinate) database.
You can then use the username and password of this new account in the Germinate configuration by providing: `gatekeeper.username` and `gatekeeper.password`.