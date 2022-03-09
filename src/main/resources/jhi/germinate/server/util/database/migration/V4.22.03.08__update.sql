SET FOREIGN_KEY_CHECKS=0;

/* Add a new dataset type */
INSERT INTO `datasettypes` (`description`) VALUES ('pedigree');

/* Add a new dataset column to pedigrees */
ALTER TABLE `pedigrees`
ADD COLUMN `dataset_id` int(11) NOT NULL DEFAULT -1 AFTER `id`;

/* Add a new dataset column to pedigreedefinitions */
ALTER TABLE `pedigreedefinitions`
ADD COLUMN `dataset_id` int(11) NOT NULL DEFAULT -1 AFTER `id`;

/* Add a new default pedigree experiment (if pedigree data exists) */
INSERT INTO `experiments` (`experiment_name`, `description`) SELECT 'Default pedigree experiment', 'Default pedigree experiment created by database update'
WHERE EXISTS (SELECT 1 FROM `pedigrees` LIMIT 1) OR EXISTS (SELECT 1 FROM `pedigreedefinitions`);

/* Add a new default pedigree dataset (if pedigree data exists) */
INSERT INTO `datasets` (`experiment_id`, `datasettype_id`, `name`, `description`) SELECT (SELECT `id` from `experiments` WHERE `experiment_name` = 'Default pedigree experiment'), (SELECT `id` FROM `datasettypes` WHERE `description` = 'pedigree'), 'Default pedigree dataset', 'Default pedigree dataset created by database update'
WHERE EXISTS (SELECT 1 FROM `pedigrees` LIMIT 1) OR EXISTS (SELECT 1 FROM `pedigreedefinitions`);

/* Set the id of the newly created dataset */
UPDATE `pedigrees` SET `dataset_id` = (SELECT `id` FROM `datasets` WHERE `name` = 'Default pedigree dataset');
UPDATE `pedigreedefinitions` SET `dataset_id` = (SELECT `id` FROM `datasets` WHERE `name` = 'Default pedigree dataset');

/* Define the foreign keys for the pedigrees and pedigreedefinitions table now that the referencing columns have been populated */
ALTER TABLE `pedigrees` ADD FOREIGN KEY (`dataset_id`) REFERENCES `datasets` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `pedigreedefinitions` ADD FOREIGN KEY (`dataset_id`) REFERENCES `datasets` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

SET FOREIGN_KEY_CHECKS=1;