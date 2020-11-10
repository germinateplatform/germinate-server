---
title: Germinate FAQ
---

# Germinate Frequently Asked Questions

## How do I install Germinate?

Germinate can either be run using a <a href="setup.html#docker">Docker container</a> or <a href="setup.html#manual-setup">built manually</a>.

## How do I import my data?

Germinate offers a user validation and upload function through its web interface. To import your data, follow these steps:

1. Run Germinate as well as [Gatekeeper](https://germinateplatform.github.io/gatekeeper-server).
    - Make sure to define a user account inside Gatekeeper for Germinate that has `Data Curator` permissions.
2. Download the Excel data template from the [dedicated GitHub repository](https://github.com/germinateplatform/germinate-data-templates).
3. Fill the data templates with your data.
    - Start with the germplasm MCPD (Multi-Crop Passport Data) sheet to define your germplasm. Every germplasm referenced in any of the other data templates **has to be** imported via a germplasm MCPD template first.
    - Continue with the other data types.
4. Once your data templates are ready, navigate to your Germinate web interface. Log in using the Germinate user account defined in Gatekeeper and then go to the data import page.
    - Select the data template type you want to import (remember, germplasm first). Then select the template file and select "Upload and check".
    - Your file will be checked for validity by Germinate.
    - When the check finishes, you can either import your data or you will see a detailed list of issues with your data that need to be addressed before the data can be imported. 

Please also check the <a href="dataimport.html">data import</a> documentation page for more information.

## How do I add my logo instead of the default logo?

Germinate by default will include the James Hutton Institute logo in the left navigation menu. You can replace that logo with your institution logo by modifiying the `logo.svg` and `logo-horizontal.svg` files in the <a href="config.html">config folder</a>. 

## How do I change the main color of the web interface?

Simply change the property `color.primary` in the `config.properties` file to a valid [HEX color](https://en.wikipedia.org/wiki/Web_colors#Hex_triplet) like `#00acef` (bright blue), `#ff0000` (red), etc.

## How do I change the text on the web interface?

Germinate comes with a default set of text for the interface in English. If you wish to change any of the text, please download [the template English text file](https://raw.githubusercontent.com/germinateplatform/germinate-i18n/master/en_GB.json) as a basis.

Identify the row holding the text you wish to change, e.g.:

```json
"pageDashboardText": "<b>Welcome to Germinate.</b> This is the template text for the Germinate home page. Replace me with text about this project."
```  
Change the text according to your requirements making sure it contains valid HTML code and double quotes are properly escaped using a backslash (``\``).

Create a new `en_GB.json` file in your <a href="config.html">config folder</a> and place the changed property in it like this:

```json
{
  "pageDashboardText": "<b>Welcome to Germinate.</b> This website supports the project \"BestProjectEver\". Read more about this project at <a href='https://example.com'>example.com</a>."
}
```

Save the file and reload Germinate in your browser.

## How do I add a language to Germinate?

We've seen how we can change the default English text, but how do we add a completely new language to the Germinate user interface?

There is a small selection of translated Germinate texts available at https://github.com/germinateplatform/germinate-i18n.
If one of them is what you are looking for, great! Save it into your <a href="config.html">config folder</a> and make adjustments to the text as seen before.

If the language you are looking for is not available, then you can contribute to the Germinate Platform by providing a translation! Your translation will be made available on the GitHub repository and you will be given credit for your contribution!
This way, others looking for the language that you provided will be able to get started more quickly.

To translate Germinate into a new language, start with [the template English text file](https://raw.githubusercontent.com/germinateplatform/germinate-i18n/master/en_GB.json).
Translate each property into your language and save the final file according to the [ISO 3166-1 alpha-2](https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2) code for the country best representing this locale.

To make the process of translating easier, get in touch with us and we can give you access to our online translation software where you can work on the translation of Germinate. Just send an email to germinate@hutton.ac.uk.

## My question is not covered here...

Please get in touch with us either via email (germinate@hutton.ac.uk) or via the GitHub issue trackers available at https://github.com/germinateplatform.
We're more than happy to help you get up and running with Germinate and any questions we receive may be added to this FAQ to help others!