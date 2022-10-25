ALTER TABLE `phenotypedata`
    ADD COLUMN `trial_row` smallint NULL COMMENT 'The row number in the trial layout.' AFTER `block`,
    ADD COLUMN `trial_column` smallint NULL COMMENT 'The column number in the trial layout.' AFTER `trial_row`;

ALTER TABLE `fileresourcetypes`
    ADD COLUMN `public_visibility` tinyint NOT NULL DEFAULT 1 COMMENT 'Determines whether this type is visible to non-admins.' AFTER `description`;

INSERT INTO `fileresourcetypes` (`name`, `description`, `public_visibility`) VALUES ('Trials Shapefile', 'Shape file associated with a phenotypic trial. Fields within the shape file have to match the database entries.', 0);
INSERT INTO `fileresourcetypes` (`name`, `description`, `public_visibility`) VALUES ('Trials GeoTIFF', 'GeoTIFF file associated with a phenotypic trial. The "created_on" date of this fileresource determines the time point at which it was recorded.', 0);

ALTER TABLE `data_import_jobs`
    MODIFY COLUMN `datatype` enum('mcpd','trial','compound','genotype','pedigree','groups','climate','images','shapefile','geotiff') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'mcpd' AFTER `datasetstate_id`;

DROP TABLE IF EXISTS `mapoverlays`;
CREATE TABLE `mapoverlays`  (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'The primary key of this table.',
    `name` varchar(255) NOT NULL COMMENT 'A name for the map overlay.',
    `description` text NULL COMMENT 'An optional description of what is shown on the overlay.',
    `bottom_left_lat` DECIMAL(64, 10) NULL COMMENT 'The bottom left latitude coordinates in decimal degrees for anchoring on the map.',
    `bottom_left_lng` DECIMAL(64, 10) NULL COMMENT 'The bottom left longitude coordinates in decimal degrees for anchoring on the map.',
    `top_right_lat` DECIMAL(64, 10) NULL COMMENT 'The top right latitude coordinates in decimal degrees for anchoring on the map.',
    `top_right_lng` DECIMAL(64, 10) NULL COMMENT 'The top right longitude coordinates in decimal degrees for anchoring on the map.',
    `is_legend` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'Flag to indicate whether this is a legend or an actual overlay.',
    `reference_table` enum('phenotypes','climates') NULL COMMENT 'Optionally, other database items can be linked to this. As an example, an overlay can be linked to a climate variable.',
    `foreign_id` int(11) NULL COMMENT 'The foreign id within the reference_table of the linked database object.',
    `dataset_id` int(11) NULL COMMENT 'A dataset id this map overlay is linked to. Useful for providing map overlays for trials data that is not specific to a trait within the dataset.',
    `recording_date` datetime NULL COMMENT 'A date that is associated with the timepoint when this has been recorded.',
    `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'The datetime when this database record has been created.',
    `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'The timestamp when this database record has last been updated.',
    PRIMARY KEY (`id`),
    INDEX(`foreign_id`) USING BTREE,
    FOREIGN KEY (`dataset_id`) REFERENCES `datasets` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);