ALTER TABLE `climates`
MODIFY COLUMN `datatype` enum('float','int','char','categorical','numeric','text','date') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'int' COMMENT 'Defines the data type of the climate. This can be of float, int or char types.' AFTER `description`;

UPDATE `climates` SET `datatype` = 'numeric' WHERE `datatype` = 'float' OR `datatype` = 'int';
UPDATE `climates` SET `datatype` = 'text' WHERE `datatype` = 'char';

ALTER TABLE `climates`
MODIFY COLUMN `datatype` enum('categorical','numeric','text','date') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'text' COMMENT 'Defines the data type of the climate. This can be of numeric, text, date or categorical types.' AFTER `description`;