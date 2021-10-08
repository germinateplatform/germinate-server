DROP TABLE IF EXISTS `publicationdata`;
DROP TABLE IF EXISTS `publications`;

CREATE TABLE `publications`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `doi` text NOT NULL,
  `fallback_cache` mediumtext NULL,
  `created_on` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
);

CREATE TABLE `publicationdata`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `foreign_id` int(11) NULL,
  `publication_id` int(11) NOT NULL,
  `reference_type` enum('database','dataset','germplasm','group','experiment') NOT NULL DEFAULT 'database',
  `created_on` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `publication_id`(`publication_id`) USING BTREE,
  FOREIGN KEY (`publication_id`) REFERENCES `publications` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);