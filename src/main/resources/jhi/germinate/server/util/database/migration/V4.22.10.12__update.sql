ALTER TABLE `phenotypedata`
    ADD COLUMN `trial_row` smallint NULL COMMENT 'The row number in the trial layout.' AFTER `block`,
    ADD COLUMN `trial_column` smallint NULL COMMENT 'The column number in the trial layout.' AFTER `trial_row`;

ALTER TABLE `fileresourcetypes`
    ADD COLUMN `public_visibility` tinyint NOT NULL DEFAULT 1 COMMENT 'Determines whether this type is visible to non-admins.' AFTER `description`;

INSERT INTO `fileresourcetypes` (`name`, `description`, `public_visibility`) VALUES ('Trials Shapefile', 'Shape file associated with a phenotypic trial. Fields within the shape file have to match the database entries.', 0);
INSERT INTO `fileresourcetypes` (`name`, `description`, `public_visibility`) VALUES ('Trials GeoTIFF', 'GeoTIFF file associated with a phenotypic trial. The "created_on" date of this fileresource determines the time point at which it was recorded.', 0);