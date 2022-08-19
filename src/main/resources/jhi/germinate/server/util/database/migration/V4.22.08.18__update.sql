ALTER TABLE `data_import_jobs`
MODIFY COLUMN `datatype` enum('mcpd','trial','compound','genotype','pedigree','groups','climate','images') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'mcpd' AFTER `datasetstate_id`;

RENAME TABLE `dataset_export_jobs` TO `data_export_jobs`;

ALTER TABLE `data_export_jobs`
ADD COLUMN `datatype` enum('genotype','trials','allelefreq','climate','compound','pedigree','unknown','images') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'unknown' AFTER `visibility`;

UPDATE `data_export_jobs`
SET `datatype` = COALESCE((SELECT `description` FROM `datasettypes` WHERE `datasettypes`.`id` = `data_export_jobs`.`datasettype_id` LIMIT 1), 'unknown');

ALTER TABLE `data_export_jobs`
DROP FOREIGN KEY `data_export_jobs_ibfk_1`;

ALTER TABLE `data_export_jobs`
DROP COLUMN `datasettype_id`;
