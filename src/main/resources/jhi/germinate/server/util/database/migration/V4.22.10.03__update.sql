INSERT INTO `attributes` (`name`, `description`, `datatype`, `target_table`) VALUES ('Molecular formula', 'Chemical formula is a way of presenting information about the chemical proportions of atoms that constitute a particular chemical compound or molecule, using chemical element symbols, numbers, and sometimes also other symbols.', 'text', 'phenotypes');
INSERT INTO `attributes` (`name`, `description`, `datatype`, `target_table`) VALUES ('Monoisotopic mass', 'Monoisotopic mass is one of several types of molecular masses used in mass spectrometry.', 'numeric', 'phenotypes');
INSERT INTO `attributes` (`name`, `description`, `datatype`, `target_table`) VALUES ('Average mass', 'The average mass of a molecule is obtained by summing the average atomic masses of the constituent elements.', 'numeric', 'phenotypes');
INSERT INTO `attributes` (`name`, `description`, `datatype`, `target_table`) VALUES ('Compound class', 'Description of the class of chemical compound.', 'text', 'phenotypes');

INSERT INTO `phenotypes` (`name`, `short_name`, `description`, `datatype`, `unit_id`, `created_on`, `updated_on`) SELECT `name`, NULL, `description`, 'numeric', `unit_id`, `created_on`, `updated_on` FROM `compounds`;

INSERT INTO `attributedata` (`foreign_id`, `attribute_id`, `value`) SELECT `phenotypes`.`id`, (SELECT `id` FROM `attributes` WHERE `attributes`.`name` = 'Molecular formula'), `compounds`.`molecular_formula` FROM `phenotypes` LEFT JOIN `compounds` ON `compounds`.`name` = `phenotypes`.`name` AND `compounds`.`description` <=> `phenotypes`.`description` WHERE NOT ISNULL(`compounds`.`molecular_formula`);
INSERT INTO `attributedata` (`foreign_id`, `attribute_id`, `value`) SELECT `phenotypes`.`id`, (SELECT `id` FROM `attributes` WHERE `attributes`.`name` = 'Monoisotopic mass'), `compounds`.`monoisotopic_mass` FROM `phenotypes` LEFT JOIN `compounds` ON `compounds`.`name` = `phenotypes`.`name` AND `compounds`.`description` <=> `phenotypes`.`description` WHERE NOT ISNULL(`compounds`.`monoisotopic_mass`);
INSERT INTO `attributedata` (`foreign_id`, `attribute_id`, `value`) SELECT `phenotypes`.`id`, (SELECT `id` FROM `attributes` WHERE `attributes`.`name` = 'Average mass'), `compounds`.`average_mass` FROM `phenotypes` LEFT JOIN `compounds` ON `compounds`.`name` = `phenotypes`.`name` AND `compounds`.`description` <=> `phenotypes`.`description` WHERE NOT ISNULL(`compounds`.`average_mass`);
INSERT INTO `attributedata` (`foreign_id`, `attribute_id`, `value`) SELECT `phenotypes`.`id`, (SELECT `id` FROM `attributes` WHERE `attributes`.`name` = 'Compound class'), `compounds`.`compound_class` FROM `phenotypes` LEFT JOIN `compounds` ON `compounds`.`name` = `phenotypes`.`name` AND `compounds`.`description` <=> `phenotypes`.`description` WHERE NOT ISNULL(`compounds`.`compound_class`);

INSERT INTO `phenotypedata` (`germinatebase_id`, `phenotype_id`, `phenotype_value`, `dataset_id`, `recording_date`, `created_on`, `updated_on`) SELECT `compounddata`.`germinatebase_id`, (SELECT `phenotypes`.`id` FROM `phenotypes` WHERE `phenotypes`.`name` = `compounds`.`name` AND `phenotypes`.`description` <=> `compounds`.`description`), `compounddata`.`compound_value`, `compounddata`.`dataset_id`, `compounddata`.`recording_date`, `compounddata`.`created_on`, `compounddata`.`updated_on` FROM `compounddata` LEFT JOIN `compounds` ON `compounds`.`id` = `compounddata`.`compound_id`;

UPDATE `datasets` SET `datasettype_id` = 3 WHERE `datasettype_id` = 6;

DROP TABLE `compounddata`;
DROP TABLE `compounds`;
DROP TABLE `analysismethods`;