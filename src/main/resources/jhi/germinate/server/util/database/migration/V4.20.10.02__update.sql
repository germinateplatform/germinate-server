ALTER TABLE `phenotypes`
MODIFY COLUMN `datatype` enum('float','int','char','categorical','numeric','text','date') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'int' COMMENT 'Defines the data type of the phenotype. This can be of float, int or char types.' AFTER `description`,
ADD COLUMN `restrictions` json NULL COMMENT 'A json object describing the restrictions placed on this trait. It is an object containing a field called \"categories\" which is an array of arrays, each describing a categorical scale. Each scale must have the same length as they describe the same categories just using different terms or numbers. The other fields are \"min\" and \"max\" to specify upper and lower limits for numeric traits.' AFTER `datatype`;

UPDATE `phenotypes` SET `datatype` = 'numeric' WHERE `datatype` = 'float' OR `datatype` = 'int';
UPDATE `phenotypes` SET `datatype` = 'text' WHERE `datatype` = 'char';

ALTER TABLE `phenotypes`
MODIFY COLUMN `datatype` enum('categorical','numeric','text','date') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'text' COMMENT 'Defines the data type of the phenotype. This can be of numeric, text, date or categorical types.' AFTER `description`;


ALTER TABLE `attributes`
MODIFY COLUMN `datatype` enum('float','int','char','categorical','numeric','text','date') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'int' COMMENT 'Defines the data type of the attribute. This can be of float, int or char types.' AFTER `description`;

UPDATE `attributes` SET `datatype` = 'numeric' WHERE `datatype` = 'float' OR `datatype` = 'int';
UPDATE `attributes` SET `datatype` = 'text' WHERE `datatype` = 'char';

ALTER TABLE `attributes`
MODIFY COLUMN `datatype` enum('categorical','numeric','text','date') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'text' COMMENT 'Defines the data type of the attribute. This can be of numeric, text, date or categorical types.' AFTER `description`;