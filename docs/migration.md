---
title: Germinate Migration
---

# Migration guide

- [Migrating to 4.0.0](#migrating-to-400)

## Migrating to 4.0.0

Germinate 4.0.0 represents a huge step forward in the Germinate development process. It's a complete rewrite of the server and client code and therefore brings changes to the way it operates and is used. A lot of these changes are quite significant, but we had to make this difficult decision to be able to move forwards without being held back by the massive technology dept that had built up over time.

This section will show you how you can migrate your existing pre-4.0.0 version to this new release. Going forwards, the migration requirements between versions will be kept to a minimum again. The changes introduced in this version will improve the usability and ease of setup and configuration significantly, so we hope that it will all be worth it.

### Server configuration folder

To increase the portability of Germinate and to facilitate the ease of customization on the fly, all configuration files are now located in a folder located outside of the software itself. This allows Germinate itself to not contain any project-specific files and therefore be completely interchangeable between projects.

The  <a href="config.html">configurations page</a> shows you the structure of this folder. Comparing this to the [old structure](https://github.com/germinateplatform/germinate/wiki/Structure) shows certain similarities and hopefully the migration will not be too complex.
Let's go through the old structure item by item and see where it needs to go in the new structure.

`instance-stuff/<your-project>` in the old version is what we now call the external configuration folder of Germinate.

- `apps`: This folder no longer exists, we bundle everything with Germinate and do not allow these apps to be overwritten by users.
- `data`: This folder stays unchanged. It still contains a `genotypes` and an `allelefreq` sub-folder with the corresponding data files.
- `download`: This folder used to contain the images that were available through the web interface. These images (from the `fullsize` sub-directory) will now be placed in the `images/database` folder in the new structure.
- `i18n`: The content of this file represent the largest change between the old and new version. While the old version used Java `.properties` files that were handled by the server and not changeable after Germinate was built, we now moved to `.json` files that can be changed on the fly and will immediately be reflected on the web interface. Unfortunately, there is no easy mapping between the old files and the new. Therefore, these files have to be re-created based on the template files available in their own [GitHub repository](https://github.com/germinateplatform/germinate-i18n). Please follow the instructions available in this repository.
- `res`: This folder was conceptualized as a place where people could deposit their own resources like R scripts and they would then write their own Germinate code to use them. This concept never made it further than the initial idea and has therefore been removed completely.
- `template`: This folder contained all files used to customize the web interface. Its content has been split into several other folders. Any images now need to be placed under the corresponding `images` folder. Any other files like custom CSS, custom HTML or external Javascript are no longer supported as they were only ever used by Germinate instances that we created ourselves and the need for these no longer exists.
- `config.properties`: This file is still the main configuration file and should be kept at the root of the new Germinate configuration directory. Its structure has changed, however, and it should now be configured according to the <a href="config.html">configurations page</a>.   

