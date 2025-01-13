CREATE TABLE `phenotypecategories`  (
      `id` int NOT NULL AUTO_INCREMENT,
      `name` varchar(255) NOT NULL,
      `description` text NULL,
      `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP,
      `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
      PRIMARY KEY (`id`),
      INDEX(`name`) USING BTREE
);

ALTER TABLE `phenotypes`
    ADD COLUMN `category_id` int NULL COMMENT 'Foreign key to phenotypecategories (phenotypecategories.id)' AFTER `unit_id`,
    ADD FOREIGN KEY (`category_id`) REFERENCES `phenotypecategories` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;