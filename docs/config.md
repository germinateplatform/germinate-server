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
# Database properties. Server name, database name and user name are required.
# A password may be optional depending on your configuration and the port only needs to be provided if it's not 3306. 
database.server   = <database server>
database.name     = <database name>
database.username = <database username>
database.password = <database password if required
database.port     = <database port if not 3306>

# This is required so that the server can generate files that link back to the user interface
germinate.client.url = <base url of the client, e.g. https://ics.hutton.ac.uk/germinate-demo/>

# If Gatekeeper is used, these properties are required. Username and password have to be the credentials of an actual Gatekeeper user with admin permissions for Gatekeeper (not Germinate). 
gatekeeper.url                            = <base url of gatekeeper if using>
gatekeeper.username                       = <username of gatekeeper if using>
gatekeeper.password                       = <password of gatekeeper if using and required>
# User registration can be enabled
gatekeeper.registration.enabled           = <should new users be able to register for an account>
gatekeeper.registration.requires.approval = <if registration is enabled, does it require approval from an administrator>
# Passwords are stored hashed and salted, not in plain text. Increasing the salt will make it more robust against brute force attackes, but will also slow down authentication of genuine logins.
bcrypt.salt                               = <the salt value used for password hashing. higher means more secure, but also slower. 10 default>

# Colors for the user interface. These are used in charts and the template. All have to be privided as Hex colors (e.g. #ffffff for white or #000000 for black)      
color.primary   = <the primary color (hex-code) of the user interface>
colors.charts   = <comma separated list of colors (hex-code) used for charts>
colors.template = <comma separated list of colors (hex-code) used for the user interface>
colors.gradient = <comma separated list of colors (hex-code) used for gradients>

# This is the most important property. It points Germinate to the location of all configuration files.
data.directory.external = <location of the directory containing the configuration files (the ones explained in this section). Should be '/data/germinate' if using the Docker image.>

# The authentication mode determines whether users have to log in or not.
# NONE disables authentication (and the use of Gatekeeper).
# FULL requires all users to log in before they can even see any data.
# SELECTIVE only requires users to log in if they want to use any features that alter the database, e.g. creating groups, adding comments, etc.
authentication.mode = <either 'SELECTIVE', 'FULL' or 'NONE'; don't include the quotes>

# The data import mode determines whether Data Curators (in Gatekeeper) can verify or upload data in the Excel templates.
# NONE disables data verification and upload
# VERIFY allows the upload and checking/verification of templates
# IMPORT allows everything VERIFY does, but also allows the actual import of data after the verification step
data.import.mode = <either 'IMPORT', 'VERIFY' OR 'NONE'; don't include the quotes>

# These are used to allow linking to an external resource from the marked germplasm page
external.link.identifier = <the column from the germinatebase to use>
external.link.template   = <the template to put the joined identifiers into. Has to include '{identifiers}' e.g. 'https://www.google.co.uk/search?q={identifiers}'>

# We can't keep files forever. These properties decide when files should be deleted.
files.delete.after.hours.async = <after how many hours should files created from async import/export tasks be deleted>
files.delete.after.hours.temp  = <after how many hours should temporary files be deleted (e.g. chart files, synchronous download files, etc)>

# Google Analytics will be enabled if this property is set.
google.analytics.key      = <google analytics key if using>
# Plausible Analytics information (https://plausible-tracker.netlify.app/globals#plausibleinitoptions)
plausible.domain    = <your site's domain, as declared by you in plausible's settings>
plausible.hash.mode = <enables tracking based on url hash changes>
plausible.api.host  = <plausible's api host to use. Change this if you are self-hosting.>
# The colored boxes at the top of the dashboard/home page can be changed here.
dashboard.categories      = <comma separated list of the dashboard categories to show. any of: 'germplasm', 'markers', 'maps', 'traits', 'climates', 'compounds', 'locations', 'datasets', 'datasetsGenotype', 'datasetsTrials', 'datasetsAllelefreq', 'datasetsClimate', 'datasetsCompound', 'experiments', 'groups', 'images', 'fileresources'; don't include the quotes>
# Pages can be hidden for example if you don't have that kind of data.
hidden.pages              = <names of those pages that should be hidden from the user interface (https://github.com/germinateplatform/germinate-vue/blob/master/src/router/index.js)>
# Columns of each table can be hidden. Note that this only takes effect the first time Germinate is loaded.
# Afterwards we cannot overwrite the client configuration as the user may have chosen to show/hide different columns.
# The values are comma-separated lists of the column keys found in the corresponsing client components found here: https://github.com/germinateplatform/germinate-vue/tree/master/src/components/tables
hidden.columns.germplasm            = <columns to hide in this table >
hidden.columns.germplasm.attributes = <columns to hide in this table >
hidden.columns.images               = <columns to hide in this table >
hidden.columns.climates             = <columns to hide in this table >
hidden.columns.climate.data         = <columns to hide in this table >
hidden.columns.compounds            = <columns to hide in this table >
hidden.columns.compound.data        = <columns to hide in this table >
hidden.columns.comments             = <columns to hide in this table >
hidden.columns.fileresources        = <columns to hide in this table >
hidden.columns.maps                 = <columns to hide in this table >
hidden.columns.markers              = <columns to hide in this table >
hidden.columns.map.definitions      = <columns to hide in this table >
hidden.columns.datasets             = <columns to hide in this table >
hidden.columns.dataset.attributes   = <columns to hide in this table >
hidden.columns.experiments          = <columns to hide in this table >
hidden.columns.entities             = <columns to hide in this table >
hidden.columns.groups               = <columns to hide in this table >
hidden.columns.institutions         = <columns to hide in this table >
hidden.columns.locations            = <columns to hide in this table >
hidden.columns.pedigrees            = <columns to hide in this table >
hidden.columns.pedigreedefinitions  = <columns to hide in this table >
hidden.columns.traits               = <columns to hide in this table >
hidden.columns.trials.data          = <columns to hide in this table >
hidden.columns.collaborators        = <columns to hide in this table >
hidden.columns.publications         = <columns to hide in this table >
# The comments feature can be disabled if you don't want users to add comments.
comments.enabled          = <should the comments feature be enabled>
# The PDCI calculation can be disabled if you don't wish to show this information.
pdci.enabled              = <should the PDCI be calculated and shown on the user interface>
# GDPR compliance is important and this has to be enabled if you're expecting users from the EU. It gives them the option to enable non-essential cookies for convenience. 
gdpr.notification.enabled = <should the GDPR compliance banner be shown. if so, users can deny the usage of cookies which will disable some features>
```

# Gatekeeper Configuration

If you're using Gatekeeper for user authentication or you would like to use the data upload mechanisms offered by Germinate, then Gatekeeper needs to be properly configured.
Please follow the documentation at https://germinateplatform.github.io/gatekeeper-server/ to set up Gatekeeper.

Once running, you will need to create a new user in Gatekeeper that Germinate can use to authenticate itself. To this end, create a new user with `Administrator` permissions for the default Gatekeeper (not Germinate) database.
You can then use the username and password of this new account in the Germinate configuration by providing: `gatekeeper.username` and `gatekeeper.password`.