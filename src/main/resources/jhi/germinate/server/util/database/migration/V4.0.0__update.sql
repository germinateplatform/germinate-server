/*
 * Copyright $today.year Information and Computational Sciences,
 * The James Hutton Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

SET FOREIGN_KEY_CHECKS=0;

CREATE TABLE `dataset_export_jobs`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` varchar(36) NOT NULL,
  `job_id` text NOT NULL,
  `user_id` int(11) NULL,
  `status` enum('waiting','running','failed','completed','cancelled') NOT NULL DEFAULT 'waiting',
  `visibility` tinyint(1) NOT NULL DEFAULT 1,
  `experiment_type_id` int(11) NULL,
  `dataset_ids` json NULL,
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`),
  FOREIGN KEY (`experiment_type_id`) REFERENCES `experimenttypes` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
);

INSERT INTO `groups` (`name`, `description`, `visibility`, `grouptype_id`) SELECT `name`, `name`, 1, 1 FROM `megaenvironments`;

INSERT INTO `groupmembers` (`foreign_id`, `group_id`) SELECT `location_id`, (SELECT `id` FROM `groups` WHERE `groups`.`name` = `megaenvironments`.`name`) FROM `megaenvironmentdata` LEFT JOIN `megaenvironments` ON `megaenvironmentdata`.`megaenvironment_id` = `megaenvironments`.`id` WHERE `megaenvironmentdata`.`is_final` = 1;

DROP TABLE `megaenvironmentdata`;
DROP TABLE `megaenvironments`;
DROP TABLE `megaenvironmentsource`;

ALTER TABLE `climatedata` CHANGE COLUMN `recording_date` `old_recording_date` varchar(32) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'The month that the data was recorded. This uses an integer to represent the month (1-12).' AFTER `dataset_id`;
ALTER TABLE `climatedata` ADD COLUMN `recording_date` datetime(0) NULL COMMENT 'The date at which this data point was recorded.' AFTER `dataset_id`;
UPDATE `climatedata` SET `recording_date` = STR_TO_DATE(CONCAT(YEAR(CURDATE()), '-', LPAD(`old_recording_date`,2 , '00'), '-01'), '%Y-%m-%d');

ALTER TABLE `climatedata` ADD INDEX `climate_query_index`(`climate_id`, `location_id`, `recording_date`, `dataset_id`, `climate_value`);

CREATE TABLE `datasetlocations`  (
  `dataset_id` int(11) NOT NULL,
  `location_id` int(11) NOT NULL,
  `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`dataset_id`, `location_id`),
  FOREIGN KEY (`dataset_id`) REFERENCES `datasets` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`location_id`) REFERENCES `locations` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO `datasetlocations` (`dataset_id`, `location_id`) SELECT `id`, `location_id` FROM `datasets` WHERE NOT ISNULL(`location_id`);

DROP PROCEDURE IF EXISTS drop_all_indexes;

DELIMITER //

CREATE PROCEDURE drop_all_indexes()

BEGIN

    DECLARE index_name TEXT DEFAULT NULL;
    DECLARE done TINYINT DEFAULT FALSE;

    DECLARE cursor1 CURSOR FOR SELECT constraint_name
                               FROM information_schema.TABLE_CONSTRAINTS
                               WHERE TABLE_SCHEMA = database()
                                 AND TABLE_NAME = "datasets"
                                 AND CONSTRAINT_TYPE = "FOREIGN KEY";

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    OPEN cursor1;

    my_loop:
        LOOP

            FETCH NEXT FROM cursor1 INTO index_name;

            IF done THEN
                LEAVE my_loop;
            ELSE
                SET @query = CONCAT('ALTER TABLE `datasets` DROP FOREIGN KEY ', index_name);
                PREPARE stmt FROM @query;
                EXECUTE stmt;
                DEALLOCATE PREPARE stmt;

            END IF;
        END LOOP;

END;
//

DELIMITER ;

CALL drop_all_indexes();

DROP PROCEDURE IF EXISTS drop_all_indexes;

ALTER TABLE `datasets` DROP `location_id`;

ALTER TABLE `datasets`
ADD CONSTRAINT `datasets_ibfk_experiment` FOREIGN KEY (`experiment_id`) REFERENCES `experiments` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT `datasets_ibfk_dataset_state` FOREIGN KEY (`dataset_state_id`) REFERENCES `datasetstates` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT `datasets_ibfk_license` FOREIGN KEY (`license_id`) REFERENCES `licenses` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

CREATE TABLE `imagetags`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tag_name` varchar(255) NOT NULL,
  `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`, `tag_name`)
);

CREATE TABLE `image_to_tags`  (
  `image_id` int(11) NOT NULL,
  `imagetag_id` int(11) NOT NULL,
  `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`image_id`, `imagetag_id`),
  CONSTRAINT `image_to_tag_ibfk_image` FOREIGN KEY (`image_id`) REFERENCES `germinate_demo_api`.`images` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `image_to_tag_ibfk_imagetag` FOREIGN KEY (`imagetag_id`) REFERENCES `germinate_demo_api`.`imagetags` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

UPDATE `news` SET `image` = REPLACE(`image`, 'css/images/css-images/', '');

ALTER TABLE `images` ADD INDEX `imagetype_foreign_id`(`foreign_id`) USING BTREE;

/* Convert the whole database to UTF-8 */
ALTER TABLE `analysismethods` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `attributedata` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `attributes` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `biologicalstatus` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `climatedata` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `climateoverlays` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `climates` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `collaborators` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `collectingsources` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `comments` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `commenttypes` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `compounddata` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `compounds` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `countries` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `dataset_export_jobs` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `datasetaccesslogs` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `datasetcollaborators` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `datasetlocations` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `datasetmembers` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `datasetmembertypes` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `datasetmeta` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `datasetpermissions` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `datasets` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `datasetstates` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `entitytypes` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `experiments` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `experimenttypes` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `germinatebase` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `groupmembers` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `groups` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `grouptypes` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `image_to_tags` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `images` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `imagetags` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `imagetypes` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `institutions` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `licensedata` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `licenselogs` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `licenses` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `links` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `linktypes` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `locales` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `locations` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `locationtypes` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `mapdefinitions` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `mapfeaturetypes` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `maps` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `markers` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `markertypes` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `mlsstatus` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `news` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `newstypes` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `pedigreedefinitions` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `pedigreedescriptions` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `pedigreenotations` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `pedigrees` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `phenotypedata` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `phenotypes` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `schema_version` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `storage` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `storagedata` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `synonyms` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `synonymtypes` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `taxonomies` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `treatments` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `trialseries` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `units` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `usergroupmembers` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `usergroups` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS=1;