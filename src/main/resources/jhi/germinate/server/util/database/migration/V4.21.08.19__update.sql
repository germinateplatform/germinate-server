ALTER TABLE `phenotypedata`
ADD COLUMN `rep` varchar(10) NOT NULL DEFAULT '1' AFTER `germinatebase_id`;