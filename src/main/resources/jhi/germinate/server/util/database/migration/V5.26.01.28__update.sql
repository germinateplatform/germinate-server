SET FOREIGN_KEY_CHECKS=0;

RENAME TABLE `phenotypecategories` TO `traitcategories`;

DROP TABLE IF EXISTS `ontologies`;
CREATE TABLE `ontologies`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` text NULL,
  `version` varchar(255) NULL,
  `url` text NOT NULL,
  `created_on` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `scales`;
CREATE TABLE `scales`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` text NULL,
  `unit` varchar(255) NULL,
  `datatype` enum('categorical','numeric','text','date') NOT NULL,
  `restrictions` json NULL,
  `created_on` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `scaleontologies`;
CREATE TABLE `scaleontologies`  (
  `ontology_id` int NOT NULL,
  `scale_id` int NOT NULL,
  `ontology_puid` varchar(255) NOT NULL,
  `created_on` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ontology_id`, `scale_id`),
  FOREIGN KEY (`ontology_id`) REFERENCES `ontologies` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`scale_id`) REFERENCES `scales` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

DROP TABLE IF EXISTS `methods`;
CREATE TABLE `methods`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` text NULL,
  `method_class` enum('measurement','estimation','counting','computation','prediction','description','classification','other') NOT NULL DEFAULT 'other',
  `setsize` int NULL,
  `is_timeseries` tinyint(1) NOT NULL DEFAULT 1,
  `created_on` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `methodontologies`;
CREATE TABLE `methodontologies`  (
  `ontology_id` int NOT NULL,
  `method_id` int NOT NULL,
  `ontology_puid` varchar(255) NOT NULL,
  `created_on` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ontology_id`, `method_id`),
  FOREIGN KEY (`ontology_id`) REFERENCES `ontologies` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`method_id`) REFERENCES `methods` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

DROP TABLE IF EXISTS `traits`;
CREATE TABLE `traits`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` text NULL,
  `abbreviation` varchar(255) NULL,
  `trait_class` enum('abiotic_stress','agronomic','biochemical','biotic_stress','fertility','morphological','phenological','physiological','quality','other') NOT NULL DEFAULT 'other',
  `traitcategory_id` int NULL,
  `synonyms` json NULL,
  `created_on` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`traitcategory_id`) REFERENCES `traitcategories` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

DROP TABLE IF EXISTS `traitontologies`;
CREATE TABLE `traitontologies`  (
  `ontology_id` int NOT NULL,
  `trait_id` int NOT NULL,
  `ontology_puid` varchar(255) NOT NULL,
  `created_on` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ontology_id`, `trait_id`),
  FOREIGN KEY (`ontology_id`) REFERENCES `ontologies` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`trait_id`) REFERENCES `traits` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

DROP TABLE IF EXISTS `variables`;
CREATE TABLE `variables`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` text NULL,
  `trait_id` int NOT NULL,
  `method_id` int NOT NULL,
  `scale_id` int NOT NULL,
  `created_on` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`trait_id`) REFERENCES `traits` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`method_id`) REFERENCES `methods` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`scale_id`) REFERENCES `scales` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

DROP TABLE IF EXISTS `variableontologies`;
CREATE TABLE `variableontologies`  (
  `ontology_id` int NOT NULL,
  `variable_id` int NOT NULL,
  `ontology_puid` varchar(255) NOT NULL,
  `created_on` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ontology_id`, `variable_id`),
  FOREIGN KEY (`ontology_id`) REFERENCES `ontologies` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`variable_id`) REFERENCES `variables` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

DROP TABLE IF EXISTS `temp_traits`;
CREATE TABLE `temp_traits` AS SELECT
`p`.`id` AS `phenotype_id`,
`p`.`name` AS `trait_name`,
`p`.`description` AS `trait_description`,
`p`.`short_name` AS `trait_abbreviation`,
'other' COLLATE utf8mb4_general_ci AS `trait_class`,
`s`.`synonyms` AS `trait_synonyms`,
`p`.`datatype` AS `scale_datatype`,
`p`.`restrictions` AS `scale_restrictions`,
`p`.`setsize` AS `method_setsize`,
`p`.`is_timeseries` AS `method_is_timeseries`,
`p`.`category_id` AS `trait_category_id`,
`u`.`id` AS `unit_id`,
COALESCE(`u`.`unit_name`, `p`.`name`) AS `scale_name`,
COALESCE(`u`.`unit_description`, `p`.`description`) AS `scale_description`,
COALESCE(`u`.`unit_abbreviation`, `p`.`short_name`) AS `scale_unit`,
'measurement' COLLATE utf8mb4_general_ci AS `method_class`,
'Measurement' COLLATE utf8mb4_general_ci AS `method_name`,
cast(null as signed integer) AS `variable_id`,
cast(null as signed integer) AS `trait_id`,
cast(null as signed integer) AS `scale_id`,
cast(null as signed integer) AS `method_id`
FROM
  `phenotypes` `p`
  LEFT JOIN `units` `u` ON `u`.`id` = `p`.`unit_id`
  LEFT JOIN `synonyms` `s` ON `s`.`foreign_id` = `p`.`id`
  AND `s`.`synonymtype_id` = 4;

-- Now write the distinct methods into the new table
INSERT INTO `methods` (`name`, `method_class`, `setsize`, `is_timeseries`) select distinct `method_name`, `method_class`, `method_setsize`, `method_is_timeseries` FROM `temp_traits`;

-- Then reselect the id into the temp table for lookup later
update `temp_traits`
set `method_id` = (
select `id` from `methods` where
  `methods`.`name` <=> `temp_traits`.`method_name` AND
  `methods`.`method_class` <=> `temp_traits`.`method_class` AND
  `methods`.`setsize` <=> `temp_traits`.`method_setsize` AND
  `methods`.`is_timeseries` <=> `temp_traits`.`method_is_timeseries`
);

-- Now write the distinct scales into the new table
INSERT INTO `scales` (`name`, `description`, `unit`, `datatype`, `restrictions`) select distinct `scale_name`, `scale_description`, `scale_unit`, `scale_datatype`, `scale_restrictions` FROM `temp_traits`;

-- Then reselect the id into the temp table for lookup later
update `temp_traits`
set `scale_id` = (
select `id` from `scales` where
  `scales`.`name` <=> `temp_traits`.`scale_name` AND
  `scales`.`description` <=> `temp_traits`.`scale_description` AND
  `scales`.`unit` <=> `temp_traits`.`scale_unit` AND
  `scales`.`datatype` <=> `temp_traits`.`scale_datatype` AND
  `scales`.`restrictions` <=> `temp_traits`.`scale_restrictions`
);

-- Now write the distinct traits into the new table
INSERT INTO `traits` (`name`, `description`, `abbreviation`, `trait_class`, `synonyms`, `traitcategory_id`) select distinct `trait_name`, `trait_description`, `trait_abbreviation`, `trait_class`, `trait_synonyms`, `trait_category_id` FROM `temp_traits`;

-- Then reselect the id into the temp table for lookup later
update `temp_traits`
set `trait_id` = (
select `id` from `traits` where
  `traits`.`name` <=> `temp_traits`.`trait_name` AND
  `traits`.`description` <=> `temp_traits`.`trait_description` AND
  `traits`.`abbreviation` <=> `temp_traits`.`trait_abbreviation` AND
  `traits`.`trait_class` <=> `temp_traits`.`trait_class` AND
  `traits`.`synonyms` <=> `temp_traits`.`trait_synonyms` AND
  `traits`.`traitcategory_id` <=> `temp_traits`.`trait_category_id`
);

-- Now write the distinct variables into the new table
INSERT INTO `variables` (`name`, `description`, `trait_id`, `method_id`, `scale_id`) select `trait_name`, `trait_description`, `trait_id`, `method_id`, `scale_id` from `temp_traits`;

-- Then reselect the id into the temp table for lookup later
update `temp_traits`
set `variable_id` = (
select `id` from `variables` where
  `variables`.`name` <=> `temp_traits`.`trait_name` AND
  `variables`.`description` <=> `temp_traits`.`trait_description` AND
  `variables`.`trait_id` <=> `temp_traits`.`trait_id` AND
  `variables`.`method_id` <=> `temp_traits`.`method_id` AND
  `variables`.`scale_id` <=> `temp_traits`.`scale_id`
);

ALTER TABLE `temp_traits`
ADD INDEX(`phenotype_id`) USING BTREE,
ADD INDEX(`variable_id`) USING BTREE;

ALTER TABLE `phenotypedata` DROP FOREIGN KEY `phenotypedata_ibfk_2`;

ALTER TABLE `phenotypedata`
CHANGE COLUMN `phenotype_id` `variable_id` int NOT NULL DEFAULT 0 COMMENT 'Foreign key phenotypes (phenotype.id).' AFTER `trialsetup_id`,
ADD FOREIGN KEY (`variable_id`) REFERENCES `variables` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

UPDATE `phenotypedata` SET `variable_id` = (SELECT `variable_id` FROM `temp_traits` WHERE `temp_traits`.`phenotype_id` = `phenotypedata`.`variable_id`);

DROP TABLE IF EXISTS `phenotypes`;

DROP TABLE IF EXISTS `temp_traits`;

UPDATE `attributes` SET `target_table` = 'variables' WHERE `target_table` = 'phenotypes';
UPDATE `imagetypes` SET `reference_table` = 'variables' WHERE `reference_table` = 'phenotypes';
UPDATE `linktypes` SET `target_table` = 'variables' WHERE `target_table` = 'phenotypes';

DELETE FROM `synonymtypes` WHERE `target_table` = 'phenotypes';

SET FOREIGN_KEY_CHECKS=1;