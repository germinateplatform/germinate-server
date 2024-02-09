SET FOREIGN_KEY_CHECKS=0;

ALTER TABLE `attributedata`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `attributes`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `biologicalstatus`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `climatedata`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `climates`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `collaborators`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `collectingsources`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `comments`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `commenttypes`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `countries`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `data_export_jobs`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `data_import_jobs`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `datasetaccesslogs`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `datasetcollaborators`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `datasetfileresources`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `datasetlocations`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `datasetmembers`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `datasetmembertypes`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `datasetmeta`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `datasetpermissions`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `datasets`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `datasetstates`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `datasettypes`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `entitytypes`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `experiments`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `fileresources`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `fileresourcetypes`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `germinatebase`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `germplasminstitutions`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `groupmembers`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `groups`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `grouptypes`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `image_to_tags`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `images`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `imagetags`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `imagetypes`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `institutions`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `licensedata`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `licenselogs`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `licenses`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `links`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `linktypes`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `locales`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `locations`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `locationtypes`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `mapdefinitions`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `mapfeaturetypes`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `mapoverlays`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `maps`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `markers`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `markertypes`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `mcpd`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `mlsstatus`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `news`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `newstypes`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `pedigreedefinitions`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `pedigreedescriptions`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `pedigreenotations`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `pedigrees`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `phenotypedata`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `phenotypes`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `publicationdata`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `publications`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `schema_version`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `stories`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `storysteps`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `synonyms`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `synonymtypes`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `taxonomies`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `treatments`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `trialseries`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `units`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `userfeedback`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `usergroupmembers`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `usergroups`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

/* Create new table */
CREATE TABLE `trialsetup`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `germinatebase_id` int(11) NOT NULL,
  `dataset_id` int(11) NOT NULL,
  `location_id` int(11) NULL,
  `treatment_id` int(11) NULL,
  `trialseries_id` int(11) NULL,
  `block` varchar(10) NOT NULL DEFAULT '1',
  `rep` varchar(10) NOT NULL DEFAULT '1',
  `trial_row` smallint(6) NULL,
  `trial_column` smallint(6) NULL,
  `latitude` decimal(64, 10) NULL,
  `longitude` decimal(64, 10) NULL,
  `elevation` decimal(64, 10) NULL,
  `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`germinatebase_id`) REFERENCES `germinatebase` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`dataset_id`) REFERENCES `datasets` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`location_id`) REFERENCES `locations` (`id`) ON DELETE SET NULL ON UPDATE SET NULL,
  FOREIGN KEY (`treatment_id`) REFERENCES `treatments` (`id`) ON DELETE SET NULL ON UPDATE SET NULL,
  FOREIGN KEY (`trialseries_id`) REFERENCES `trialseries` (`id`) ON DELETE SET NULL ON UPDATE SET NULL
);

/* Insert all distinct combinations */
INSERT INTO `trialsetup` (`germinatebase_id`,`dataset_id`,`location_id`,`treatment_id`,`trialseries_id`,`block`,`rep`,`trial_row`,`trial_column`,`latitude`,`longitude`,`elevation`)
    SELECT DISTINCT `germinatebase_id`,`dataset_id`,`location_id`,`treatment_id`,`trialseries_id`,`block`,`rep`,`trial_row`,`trial_column`,`latitude`,`longitude`,`elevation` FROM `phenotypedata`;

/* Add the new column */
ALTER TABLE `phenotypedata`
    ADD COLUMN `trialsetup_id` int(11) NOT NULL AFTER `id`;

/* Add the foreign key */
ALTER TABLE `phenotypedata` ADD FOREIGN KEY (`trialsetup_id`) REFERENCES `trialsetup` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

/* Populate the new key */
UPDATE `phenotypedata` SET `trialsetup_id` = (SELECT `id` FROM `trialsetup` WHERE
    `phenotypedata`.`germinatebase_id` <=> `trialsetup`.`germinatebase_id` AND
    `phenotypedata`.`dataset_id` <=> `trialsetup`.`dataset_id` AND
    `phenotypedata`.`location_id` <=> `trialsetup`.`location_id` AND
    `phenotypedata`.`treatment_id` <=> `trialsetup`.`treatment_id` AND
    `phenotypedata`.`trialseries_id` <=> `trialsetup`.`trialseries_id` AND
    `phenotypedata`.`block` <=> `trialsetup`.`block` AND
    `phenotypedata`.`rep` <=> `trialsetup`.`rep` AND
    `phenotypedata`.`trial_row` <=> `trialsetup`.`trial_row` AND
    `phenotypedata`.`trial_column` <=> `trialsetup`.`trial_column` AND
    `phenotypedata`.`latitude` <=> `trialsetup`.`latitude` AND
    `phenotypedata`.`longitude` <=> `trialsetup`.`longitude` AND
    `phenotypedata`.`elevation` <=> `trialsetup`.`elevation` LIMIT 1);

ALTER TABLE `phenotypedata` DROP FOREIGN KEY `phenotypedata_ibfk_1`;
ALTER TABLE `phenotypedata` DROP FOREIGN KEY `phenotypedata_ibfk_3`;
ALTER TABLE `phenotypedata` DROP FOREIGN KEY `phenotypedata_ibfk_4`;
ALTER TABLE `phenotypedata` DROP FOREIGN KEY `phenotypedata_ibfk_5`;
ALTER TABLE `phenotypedata` DROP FOREIGN KEY `phenotypedata_ibfk_6`;

/* Remove no longer required columns */
ALTER TABLE `phenotypedata`
    DROP COLUMN `germinatebase_id`,
    DROP COLUMN `dataset_id`,
    DROP COLUMN `location_id`,
    DROP COLUMN `treatment_id`,
    DROP COLUMN `trialseries_id`,
    DROP COLUMN `block`,
    DROP COLUMN `rep`,
    DROP COLUMN `trial_row`,
    DROP COLUMN `trial_column`,
    DROP COLUMN `latitude`,
    DROP COLUMN `longitude`,
    DROP COLUMN `elevation`;

SET FOREIGN_KEY_CHECKS=1;