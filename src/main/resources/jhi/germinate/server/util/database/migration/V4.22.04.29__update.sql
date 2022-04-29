ALTER TABLE `phenotypedata`
ADD COLUMN `block` varchar(10) NOT NULL DEFAULT '1' AFTER `rep`,
ADD COLUMN `latitude` decimal(64, 10) NULL AFTER `block`,
ADD COLUMN `longitude` decimal(64, 10) NULL AFTER `latitude`,
ADD COLUMN `elevation` decimal(64, 10) NULL AFTER `longitude`;