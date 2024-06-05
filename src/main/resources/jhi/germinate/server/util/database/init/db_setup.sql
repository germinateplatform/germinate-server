SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for attributedata
-- ----------------------------
DROP TABLE IF EXISTS `attributedata`;
CREATE TABLE `attributedata`  (
                                  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                                  `attribute_id` int(11) NOT NULL COMMENT 'Foreign key to attributes (attributes.id).',
                                  `foreign_id` int(11) NOT NULL COMMENT 'Foreign key to germinatebase (germinatebase.id).',
                                  `value` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'The value of the attribute.',
                                  `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                                  `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                                  PRIMARY KEY (`id`) USING BTREE,
                                  INDEX `germinatebase_id`(`foreign_id`) USING BTREE,
                                  INDEX `attribute_id`(`attribute_id`) USING BTREE,
                                  CONSTRAINT `attributedata_ibfk_1` FOREIGN KEY (`attribute_id`) REFERENCES `attributes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Defines attributes data. Attributes which are defined in attributes can have values associated with them. Data which does not warrant new column in the germinatebase table can be added here. Examples include small amounts of data defining germplasm which only exists for a small sub-group of the total database.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of attributedata
-- ----------------------------

-- ----------------------------
-- Table structure for attributes
-- ----------------------------
DROP TABLE IF EXISTS `attributes`;
CREATE TABLE `attributes`  (
                               `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                               `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Defines the name of the attribute.',
                               `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Describes the attribute. This should expand on the name to make it clear what the attribute actually is.',
                               `datatype` enum('categorical','numeric','text','date') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'text' COMMENT 'Defines the data type of the attribute. This can be of numeric, text, date or categorical types.',
                               `target_table` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'germinatebase',
                               `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                               `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Describes attributes. Attributes are bits of information that can be joined to, for example, a germinatebase entry. These are bits of data that while important do not warrant adding additional columns in the other tables. Examples would be using this to define ecotypes for germinatebase entries.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of attributes
-- ----------------------------
INSERT INTO `attributes` VALUES (1, 'Molecular formula', 'Chemical formula is a way of presenting information about the chemical proportions of atoms that constitute a particular chemical compound or molecule, using chemical element symbols, numbers, and sometimes also other symbols.', 'text', 'phenotypes', '2022-10-06 10:39:31', '2022-10-06 10:39:31');
INSERT INTO `attributes` VALUES (2, 'Monoisotopic mass', 'Monoisotopic mass is one of several types of molecular masses used in mass spectrometry.', 'numeric', 'phenotypes', '2022-10-06 10:39:31', '2022-10-06 10:39:31');
INSERT INTO `attributes` VALUES (3, 'Average mass', 'The average mass of a molecule is obtained by summing the average atomic masses of the constituent elements.', 'numeric', 'phenotypes', '2022-10-06 10:39:31', '2022-10-06 10:39:31');
INSERT INTO `attributes` VALUES (4, 'Compound class', 'Description of the class of chemical compound.', 'text', 'phenotypes', '2022-10-06 10:39:31', '2022-10-06 10:39:31');

-- ----------------------------
-- Table structure for biologicalstatus
-- ----------------------------
DROP TABLE IF EXISTS `biologicalstatus`;
CREATE TABLE `biologicalstatus`  (
                                     `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                                     `sampstat` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Previoulsy known as sampstat.',
                                     `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                                     `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1000 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Based on Multi Crop Passport Descriptors (MCPD V2 2012) - The coding scheme proposed can be used at 3 different levels of detail: either by using the\ngeneral codes (in boldface) such as 100, 200, 300, 400, or by using the more specific codes\nsuch as 110, 120, etc.\n100) Wild\n110) Natural\n120) Semi-natural/wild\n130) Semi-natural/sown\n200) Weedy\n300) Traditional cultivar/landrace\n400) Breeding/research material\n 410) Breeder\'s line\n 411) Synthetic population\n 412) Hybrid\n 413) Founder stock/base population\n 414) Inbred line (parent of hybrid cultivar)\n 415) Segregating population\n 416) Clonal selection\n 420) Genetic stock\n 421) Mutant (e.g. induced/insertion mutants, tilling populations)\n 422) Cytogenetic stocks (e.g. chromosome addition/substitution, aneuploids,\namphiploids)\n 423) Other genetic stocks (e.g. mapping populations)\n500) Advanced or improved cultivar (conventional breeding methods)\n600) GMO (by genetic engineering)\n 999) Other ' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of biologicalstatus
-- ----------------------------
INSERT INTO `biologicalstatus` VALUES (100, 'Wild', NULL, '2015-08-20 09:38:37');
INSERT INTO `biologicalstatus` VALUES (110, 'Natural', NULL, '2015-08-20 09:38:47');
INSERT INTO `biologicalstatus` VALUES (120, 'Semi-natural/wild', NULL, '2015-08-20 09:38:57');
INSERT INTO `biologicalstatus` VALUES (130, 'Semi-natural/sown', NULL, '2015-08-20 09:39:07');
INSERT INTO `biologicalstatus` VALUES (200, 'Weedy', NULL, '2015-08-20 09:39:14');
INSERT INTO `biologicalstatus` VALUES (300, 'Traditional cultivar/landrace', NULL, '2015-08-20 09:39:26');
INSERT INTO `biologicalstatus` VALUES (400, 'Breeding/research material', NULL, '2015-08-20 09:39:38');
INSERT INTO `biologicalstatus` VALUES (410, 'Breeder\'s line', NULL, '2015-08-20 09:39:49');
INSERT INTO `biologicalstatus` VALUES (411, 'Synthetic population', NULL, '2015-08-20 09:39:59');
INSERT INTO `biologicalstatus` VALUES (412, 'Hybrid', NULL, '2015-08-20 09:40:05');
INSERT INTO `biologicalstatus` VALUES (413, 'Founder stock/base population', NULL, '2015-08-20 09:40:17');
INSERT INTO `biologicalstatus` VALUES (414, 'Inbred line (parent of hybrid cultivar)', NULL, '2015-08-20 09:40:29');
INSERT INTO `biologicalstatus` VALUES (415, 'Segregating population', NULL, '2015-08-20 09:40:41');
INSERT INTO `biologicalstatus` VALUES (416, 'Clonal selection', NULL, '2015-08-20 09:40:50');
INSERT INTO `biologicalstatus` VALUES (420, 'Genetic stock', NULL, '2015-08-20 09:40:58');
INSERT INTO `biologicalstatus` VALUES (421, 'Mutant (e.g. induced/inserion mutants, tilling populations)', NULL, '2015-08-20 09:41:21');
INSERT INTO `biologicalstatus` VALUES (422, 'Cytogenic stocks (e.g. chromosome addition/substitution, aneuploids, amphiploids)', NULL, '2015-08-20 09:41:52');
INSERT INTO `biologicalstatus` VALUES (423, 'Other genetic stocks (e.g. mapping populations)', NULL, '2015-08-20 09:42:08');
INSERT INTO `biologicalstatus` VALUES (500, 'Advanced or improved cultivar (conventional breeding methods)', NULL, '2015-08-20 09:42:34');
INSERT INTO `biologicalstatus` VALUES (600, 'GMO (by genetic engineering)', NULL, '2015-08-20 09:42:45');
INSERT INTO `biologicalstatus` VALUES (999, 'Other', NULL, '2015-08-20 09:42:52');

-- ----------------------------
-- Table structure for climatedata
-- ----------------------------
DROP TABLE IF EXISTS `climatedata`;
CREATE TABLE `climatedata`  (
                                `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                                `climate_id` int(11) NOT NULL DEFAULT 0 COMMENT 'Foreign key to climates (climates.id).',
                                `location_id` int(11) NOT NULL DEFAULT 0 COMMENT 'Foreign key to locations (locations.id).',
                                `climate_value` double(64, 10) NULL DEFAULT NULL COMMENT 'Value for the specific climate attribute. These are monthly averages and not daily. Monthly data is required for the current Germinate climate viisualizations and interface.',
                                `dataset_id` int(11) NOT NULL COMMENT 'Foreign key to datasets (datasets.id).',
                                `recording_date` datetime NULL DEFAULT NULL COMMENT 'The date at which this data point was recorded.',
                                `old_recording_date` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'The month that the data was recorded. This uses an integer to represent the month (1-12).',
                                `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                                `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                                PRIMARY KEY (`id`) USING BTREE,
                                INDEX `dataset_id`(`dataset_id`) USING BTREE,
                                INDEX `climate_id`(`climate_id`) USING BTREE,
                                INDEX `location_id`(`location_id`) USING BTREE,
                                INDEX `climate_location_id`(`climate_id`, `location_id`) USING BTREE,
                                INDEX `recording_date_climate_calue`(`old_recording_date`, `climate_value`) USING BTREE,
                                INDEX `climate_query_index`(`climate_id`, `location_id`, `recording_date`, `dataset_id`, `climate_value`) USING BTREE,
                                CONSTRAINT `climatedata_ibfk_1` FOREIGN KEY (`dataset_id`) REFERENCES `datasets` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                                CONSTRAINT `climatedata_ibfk_2` FOREIGN KEY (`climate_id`) REFERENCES `climates` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                                CONSTRAINT `climatedata_ibfk_3` FOREIGN KEY (`location_id`) REFERENCES `locations` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Holds montly average climate data such as rainfall, temperature or cloud cover. This is based on locations rather than accessions like most of the other tables in Germinate.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of climatedata
-- ----------------------------

-- ----------------------------
-- Table structure for climates
-- ----------------------------
DROP TABLE IF EXISTS `climates`;
CREATE TABLE `climates`  (
                             `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                             `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'Describes the climate.',
                             `short_name` char(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Shortened version of the climate name which is used in some table headers.',
                             `description` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'A longer description of the climate.',
                             `datatype` enum('categorical','numeric','text','date') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'text' COMMENT 'Defines the data type of the climate. This can be of numeric, text, date or categorical types.',
                             `unit_id` int(11) NULL DEFAULT NULL COMMENT 'Foreign key to units (units.id).\n',
                             `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                             `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                             PRIMARY KEY (`id`) USING BTREE,
                             INDEX `unit_id`(`unit_id`) USING BTREE,
                             CONSTRAINT `climates_ibfk_1` FOREIGN KEY (`unit_id`) REFERENCES `units` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Defines climates. Climates are measureable weather type characteristics such as temperature or cloud cover.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of climates
-- ----------------------------

-- ----------------------------
-- Table structure for collaborators
-- ----------------------------
DROP TABLE IF EXISTS `collaborators`;
CREATE TABLE `collaborators`  (
                                  `id` int(11) NOT NULL AUTO_INCREMENT,
                                  `first_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Last name (surname) of the author(s), researcher(s), scientist(s), student(s) responsible for producing the information product.',
                                  `last_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'First name (and middle name if available) of the author(s), researcher(s), scientist(s), student(s) responsible for producing the information product.',
                                  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'E-mail address of the author(s), researcher(s), scientist(s), student(s) responsible for producing the information product.',
                                  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Phone number of the author(s), researcher(s), scientist(s), student(s) responsible for producing the information product.',
                                  `external_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'An identifier for the data submitter. If that submitter is an individual, ORCID identifiers are recommended.',
                                  `institution_id` int(11) NULL DEFAULT NULL COMMENT 'Author\'s affiliation when the resource was created. Foreign key to \'institutions\'',
                                  `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                                  `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                                  PRIMARY KEY (`id`) USING BTREE,
                                  INDEX `institution_id`(`institution_id`) USING BTREE,
                                  CONSTRAINT `collaborators_ibfk_1` FOREIGN KEY (`institution_id`) REFERENCES `institutions` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of collaborators
-- ----------------------------

-- ----------------------------
-- Table structure for collectingsources
-- ----------------------------
DROP TABLE IF EXISTS `collectingsources`;
CREATE TABLE `collectingsources`  (
                                      `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                                      `collsrc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'collsrc in the Multi Crop Passport Descriptors (MCPD V2 2012)\n',
                                      `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                                      `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'The coding scheme proposed can be used at 2 different levels of detail: either by using the\r\ngeneral codes such as 10, 20, 30, 40, etc., or by using the more specific codes,\r\nsuch as 11, 12, etc. See Multi Crop Passport Descriptors (MCPD V2 2012) for further definitions.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of collectingsources
-- ----------------------------
INSERT INTO `collectingsources` VALUES (10, 'Wild habitat', NULL, '2015-08-20 09:49:03');
INSERT INTO `collectingsources` VALUES (11, 'Forest or woodland', NULL, '2015-08-20 09:49:13');
INSERT INTO `collectingsources` VALUES (12, 'Shrubland', NULL, '2015-08-20 09:49:18');
INSERT INTO `collectingsources` VALUES (13, 'Grassland', NULL, '2015-08-20 09:49:24');
INSERT INTO `collectingsources` VALUES (14, 'Desert or tundra', NULL, '2015-08-20 09:49:33');
INSERT INTO `collectingsources` VALUES (15, 'Aquatic habitat', NULL, '2015-08-20 09:49:40');
INSERT INTO `collectingsources` VALUES (20, 'Farm or cultivated habitat', NULL, '2015-08-20 09:49:48');
INSERT INTO `collectingsources` VALUES (21, 'Field', NULL, '2015-08-20 09:49:53');
INSERT INTO `collectingsources` VALUES (22, 'Orchard', NULL, '2015-08-20 09:49:59');
INSERT INTO `collectingsources` VALUES (23, 'Backyard, kitchen or home garden (urban, peri-urban or rural)', NULL, '2015-08-20 09:50:17');
INSERT INTO `collectingsources` VALUES (24, 'Fallow land', NULL, '2015-08-20 09:50:24');
INSERT INTO `collectingsources` VALUES (25, 'Pasture', NULL, '2015-08-20 09:50:32');
INSERT INTO `collectingsources` VALUES (26, 'Farm store', NULL, '2015-08-20 09:50:38');
INSERT INTO `collectingsources` VALUES (27, 'Threshing floor', NULL, '2015-08-20 09:50:45');
INSERT INTO `collectingsources` VALUES (28, 'Park', NULL, '2015-08-20 09:50:50');
INSERT INTO `collectingsources` VALUES (30, 'Market or shop', NULL, '2015-08-20 09:50:57');
INSERT INTO `collectingsources` VALUES (40, 'Institute, Experimental station, Research organization, Genebank', NULL, '2015-08-20 09:51:15');
INSERT INTO `collectingsources` VALUES (50, 'Seed company', NULL, '2015-08-20 09:51:21');
INSERT INTO `collectingsources` VALUES (60, 'Weedy, disturbed or ruderal habitat', NULL, '2015-08-20 09:51:36');
INSERT INTO `collectingsources` VALUES (61, 'Roadside', NULL, '2015-08-20 09:51:42');
INSERT INTO `collectingsources` VALUES (62, 'Field margin', NULL, '2015-08-20 09:51:51');
INSERT INTO `collectingsources` VALUES (99, 'Other (Elaborate in REMARKS field)', NULL, '2015-08-20 09:52:04');

-- ----------------------------
-- Table structure for comments
-- ----------------------------
DROP TABLE IF EXISTS `comments`;
CREATE TABLE `comments`  (
                             `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                             `commenttype_id` int(11) NOT NULL DEFAULT 0 COMMENT 'Foreign key to commentypes (commenttypes.id).',
                             `user_id` int(11) NULL DEFAULT NULL COMMENT 'Foreign key to Gatekeeper users (Gatekeeper users.id).',
                             `visibility` tinyint(1) NULL DEFAULT NULL COMMENT 'Defines if the comment is available or masked (hidden) from the interface.',
                             `description` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'The comment content.',
                             `reference_id` int(11) NOT NULL DEFAULT 0 COMMENT 'Relates to the UID of the table to which the comment relates',
                             `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                             `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                             PRIMARY KEY (`id`) USING BTREE,
                             INDEX `user_id`(`user_id`) USING BTREE,
                             INDEX `commenttype_id`(`commenttype_id`) USING BTREE,
                             CONSTRAINT `comments_ibfk_1` FOREIGN KEY (`commenttype_id`) REFERENCES `commenttypes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Comments can be added to different entries in Germinate such as entries from germinatebase or markers from the markers table.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comments
-- ----------------------------

-- ----------------------------
-- Table structure for commenttypes
-- ----------------------------
DROP TABLE IF EXISTS `commenttypes`;
CREATE TABLE `commenttypes`  (
                                 `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                                 `description` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Describes the comment type.',
                                 `reference_table` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'This could include \'germinatebase\' or \'markers\' to define the table that the comment relates to.',
                                 `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                                 `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Defines the comment type.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of commenttypes
-- ----------------------------
INSERT INTO `commenttypes` VALUES (1, 'line annotation', 'germinatebase', '2009-03-04 13:43:42', NULL);
INSERT INTO `commenttypes` VALUES (2, 'pedigree annotation', 'germinatebase', '2010-04-29 11:34:59', NULL);
INSERT INTO `commenttypes` VALUES (3, 'location annotations', 'locations', '2013-07-24 11:50:59', NULL);

-- ----------------------------
-- Table structure for countries
-- ----------------------------
DROP TABLE IF EXISTS `countries`;
CREATE TABLE `countries`  (
                              `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                              `country_code2` char(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'ISO 2 Code for country.',
                              `country_code3` char(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'ISO 3 Code for country.',
                              `country_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'Country name.',
                              `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                              `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 250 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Countries that are used in the locations type tables in Germinate. These are the ISO codes for countries.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of countries
-- ----------------------------
INSERT INTO `countries` VALUES (-1, 'UN', 'UNK', 'UNKNOWN COUNTRY ORIGIN', '2007-01-05 14:58:53', NULL);
INSERT INTO `countries` VALUES (1, 'AF', 'AFG', 'Afghanistan', NULL, NULL);
INSERT INTO `countries` VALUES (2, 'AX', 'ALA', 'Åland Islands', NULL, NULL);
INSERT INTO `countries` VALUES (3, 'AL', 'ALB', 'Albania', NULL, NULL);
INSERT INTO `countries` VALUES (4, 'DZ', 'DZA', 'Algeria', NULL, NULL);
INSERT INTO `countries` VALUES (5, 'AS', 'ASM', 'American Samoa', NULL, NULL);
INSERT INTO `countries` VALUES (6, 'AD', 'AND', 'Andorra', NULL, NULL);
INSERT INTO `countries` VALUES (7, 'AO', 'AGO', 'Angola', NULL, NULL);
INSERT INTO `countries` VALUES (8, 'AI', 'AIA', 'Anguilla', NULL, NULL);
INSERT INTO `countries` VALUES (9, 'AQ', 'ATA', 'Antarctica', NULL, NULL);
INSERT INTO `countries` VALUES (10, 'AG', 'ATG', 'Antigua and Barbuda', NULL, NULL);
INSERT INTO `countries` VALUES (11, 'AR', 'ARG', 'Argentina', NULL, NULL);
INSERT INTO `countries` VALUES (12, 'AM', 'ARM', 'Armenia', NULL, NULL);
INSERT INTO `countries` VALUES (13, 'AW', 'ABW', 'Aruba', NULL, NULL);
INSERT INTO `countries` VALUES (14, 'AU', 'AUS', 'Australia', NULL, NULL);
INSERT INTO `countries` VALUES (15, 'AT', 'AUT', 'Austria', NULL, NULL);
INSERT INTO `countries` VALUES (16, 'AZ', 'AZE', 'Azerbaijan', NULL, NULL);
INSERT INTO `countries` VALUES (17, 'BS', 'BHS', 'Bahamas', NULL, NULL);
INSERT INTO `countries` VALUES (18, 'BH', 'BHR', 'Bahrain', NULL, NULL);
INSERT INTO `countries` VALUES (19, 'BD', 'BGD', 'Bangladesh', NULL, NULL);
INSERT INTO `countries` VALUES (20, 'BB', 'BRB', 'Barbados', NULL, NULL);
INSERT INTO `countries` VALUES (21, 'BY', 'BLR', 'Belarus', NULL, NULL);
INSERT INTO `countries` VALUES (22, 'BE', 'BEL', 'Belgium', NULL, NULL);
INSERT INTO `countries` VALUES (23, 'BZ', 'BLZ', 'Belize', NULL, NULL);
INSERT INTO `countries` VALUES (24, 'BJ', 'BEN', 'Benin', NULL, NULL);
INSERT INTO `countries` VALUES (25, 'BM', 'BMU', 'Bermuda', NULL, NULL);
INSERT INTO `countries` VALUES (26, 'BT', 'BTN', 'Bhutan', NULL, NULL);
INSERT INTO `countries` VALUES (27, 'BO', 'BOL', 'Bolivia (Plurinational State of)', NULL, NULL);
INSERT INTO `countries` VALUES (28, 'BQ', 'BES', 'Bonaire, Sint Eustatius and Saba', NULL, NULL);
INSERT INTO `countries` VALUES (29, 'BA', 'BIH', 'Bosnia and Herzegovina', NULL, NULL);
INSERT INTO `countries` VALUES (30, 'BW', 'BWA', 'Botswana', NULL, NULL);
INSERT INTO `countries` VALUES (31, 'BV', 'BVT', 'Bouvet Island', NULL, NULL);
INSERT INTO `countries` VALUES (32, 'BR', 'BRA', 'Brazil', NULL, NULL);
INSERT INTO `countries` VALUES (33, 'IO', 'IOT', 'British Indian Ocean Territory', NULL, NULL);
INSERT INTO `countries` VALUES (34, 'BN', 'BRN', 'Brunei Darussalam', NULL, NULL);
INSERT INTO `countries` VALUES (35, 'BG', 'BGR', 'Bulgaria', NULL, NULL);
INSERT INTO `countries` VALUES (36, 'BF', 'BFA', 'Burkina Faso', NULL, NULL);
INSERT INTO `countries` VALUES (37, 'BI', 'BDI', 'Burundi', NULL, NULL);
INSERT INTO `countries` VALUES (38, 'KH', 'KHM', 'Cambodia', NULL, NULL);
INSERT INTO `countries` VALUES (39, 'CM', 'CMR', 'Cameroon', NULL, NULL);
INSERT INTO `countries` VALUES (40, 'CA', 'CAN', 'Canada', NULL, NULL);
INSERT INTO `countries` VALUES (41, 'CV', 'CPV', 'Cabo Verde', NULL, NULL);
INSERT INTO `countries` VALUES (42, 'KY', 'CYM', 'Cayman Islands', NULL, NULL);
INSERT INTO `countries` VALUES (43, 'CF', 'CAF', 'Central African Republic', NULL, NULL);
INSERT INTO `countries` VALUES (44, 'TD', 'TCD', 'Chad', NULL, NULL);
INSERT INTO `countries` VALUES (45, 'CL', 'CHL', 'Chile', NULL, NULL);
INSERT INTO `countries` VALUES (46, 'CN', 'CHN', 'China', NULL, NULL);
INSERT INTO `countries` VALUES (47, 'CX', 'CXR', 'Christmas Island', NULL, NULL);
INSERT INTO `countries` VALUES (48, 'CC', 'CCK', 'Cocos (Keeling) Islands', NULL, NULL);
INSERT INTO `countries` VALUES (49, 'CO', 'COL', 'Colombia', NULL, NULL);
INSERT INTO `countries` VALUES (50, 'KM', 'COM', 'Comoros', NULL, NULL);
INSERT INTO `countries` VALUES (51, 'CG', 'COG', 'Congo', NULL, NULL);
INSERT INTO `countries` VALUES (52, 'CD', 'COD', 'Congo (Democratic Republic of the)', NULL, NULL);
INSERT INTO `countries` VALUES (53, 'CK', 'COK', 'Cook Islands', NULL, NULL);
INSERT INTO `countries` VALUES (54, 'CR', 'CRI', 'Costa Rica', NULL, NULL);
INSERT INTO `countries` VALUES (55, 'CI', 'CIV', 'Côte d\'Ivoire', NULL, NULL);
INSERT INTO `countries` VALUES (56, 'HR', 'HRV', 'Croatia', NULL, NULL);
INSERT INTO `countries` VALUES (57, 'CU', 'CUB', 'Cuba', NULL, NULL);
INSERT INTO `countries` VALUES (58, 'CW', 'CUW', 'Curaçao', NULL, NULL);
INSERT INTO `countries` VALUES (59, 'CY', 'CYP', 'Cyprus', NULL, NULL);
INSERT INTO `countries` VALUES (60, 'CZ', 'CZE', 'Czech Republic', NULL, NULL);
INSERT INTO `countries` VALUES (61, 'DK', 'DNK', 'Denmark', NULL, NULL);
INSERT INTO `countries` VALUES (62, 'DJ', 'DJI', 'Djibouti', NULL, NULL);
INSERT INTO `countries` VALUES (63, 'DM', 'DMA', 'Dominica', NULL, NULL);
INSERT INTO `countries` VALUES (64, 'DO', 'DOM', 'Dominican Republic', NULL, NULL);
INSERT INTO `countries` VALUES (65, 'EC', 'ECU', 'Ecuador', NULL, NULL);
INSERT INTO `countries` VALUES (66, 'EG', 'EGY', 'Egypt', NULL, NULL);
INSERT INTO `countries` VALUES (67, 'SV', 'SLV', 'El Salvador', NULL, NULL);
INSERT INTO `countries` VALUES (68, 'GQ', 'GNQ', 'Equatorial Guinea', NULL, NULL);
INSERT INTO `countries` VALUES (69, 'ER', 'ERI', 'Eritrea', NULL, NULL);
INSERT INTO `countries` VALUES (70, 'EE', 'EST', 'Estonia', NULL, NULL);
INSERT INTO `countries` VALUES (71, 'ET', 'ETH', 'Ethiopia', NULL, NULL);
INSERT INTO `countries` VALUES (72, 'FK', 'FLK', 'Falkland Islands (Malvinas)', NULL, NULL);
INSERT INTO `countries` VALUES (73, 'FO', 'FRO', 'Faroe Islands', NULL, NULL);
INSERT INTO `countries` VALUES (74, 'FJ', 'FJI', 'Fiji', NULL, NULL);
INSERT INTO `countries` VALUES (75, 'FI', 'FIN', 'Finland', NULL, NULL);
INSERT INTO `countries` VALUES (76, 'FR', 'FRA', 'France', NULL, NULL);
INSERT INTO `countries` VALUES (77, 'GF', 'GUF', 'French Guiana', NULL, NULL);
INSERT INTO `countries` VALUES (78, 'PF', 'PYF', 'French Polynesia', NULL, NULL);
INSERT INTO `countries` VALUES (79, 'TF', 'ATF', 'French Southern Territories', NULL, NULL);
INSERT INTO `countries` VALUES (80, 'GA', 'GAB', 'Gabon', NULL, NULL);
INSERT INTO `countries` VALUES (81, 'GM', 'GMB', 'Gambia', NULL, NULL);
INSERT INTO `countries` VALUES (82, 'GE', 'GEO', 'Georgia', NULL, NULL);
INSERT INTO `countries` VALUES (83, 'DE', 'DEU', 'Germany', NULL, NULL);
INSERT INTO `countries` VALUES (84, 'GH', 'GHA', 'Ghana', NULL, NULL);
INSERT INTO `countries` VALUES (85, 'GI', 'GIB', 'Gibraltar', NULL, NULL);
INSERT INTO `countries` VALUES (86, 'GR', 'GRC', 'Greece', NULL, NULL);
INSERT INTO `countries` VALUES (87, 'GL', 'GRL', 'Greenland', NULL, NULL);
INSERT INTO `countries` VALUES (88, 'GD', 'GRD', 'Grenada', NULL, NULL);
INSERT INTO `countries` VALUES (89, 'GP', 'GLP', 'Guadeloupe', NULL, NULL);
INSERT INTO `countries` VALUES (90, 'GU', 'GUM', 'Guam', NULL, NULL);
INSERT INTO `countries` VALUES (91, 'GT', 'GTM', 'Guatemala', NULL, NULL);
INSERT INTO `countries` VALUES (92, 'GG', 'GGY', 'Guernsey', NULL, NULL);
INSERT INTO `countries` VALUES (93, 'GN', 'GIN', 'Guinea', NULL, NULL);
INSERT INTO `countries` VALUES (94, 'GW', 'GNB', 'Guinea-Bissau', NULL, NULL);
INSERT INTO `countries` VALUES (95, 'GY', 'GUY', 'Guyana', NULL, NULL);
INSERT INTO `countries` VALUES (96, 'HT', 'HTI', 'Haiti', NULL, NULL);
INSERT INTO `countries` VALUES (97, 'HM', 'HMD', 'Heard Island and McDonald Islands', NULL, NULL);
INSERT INTO `countries` VALUES (98, 'VA', 'VAT', 'Holy See', NULL, NULL);
INSERT INTO `countries` VALUES (99, 'HN', 'HND', 'Honduras', NULL, NULL);
INSERT INTO `countries` VALUES (100, 'HK', 'HKG', 'Hong Kong', NULL, NULL);
INSERT INTO `countries` VALUES (101, 'HU', 'HUN', 'Hungary', NULL, NULL);
INSERT INTO `countries` VALUES (102, 'IS', 'ISL', 'Iceland', NULL, NULL);
INSERT INTO `countries` VALUES (103, 'IN', 'IND', 'India', NULL, NULL);
INSERT INTO `countries` VALUES (104, 'ID', 'IDN', 'Indonesia', NULL, NULL);
INSERT INTO `countries` VALUES (105, 'IR', 'IRN', 'Iran (Islamic Republic of)', NULL, NULL);
INSERT INTO `countries` VALUES (106, 'IQ', 'IRQ', 'Iraq', NULL, NULL);
INSERT INTO `countries` VALUES (107, 'IE', 'IRL', 'Ireland', NULL, NULL);
INSERT INTO `countries` VALUES (108, 'IM', 'IMN', 'Isle of Man', NULL, NULL);
INSERT INTO `countries` VALUES (109, 'IL', 'ISR', 'Israel', NULL, NULL);
INSERT INTO `countries` VALUES (110, 'IT', 'ITA', 'Italy', NULL, NULL);
INSERT INTO `countries` VALUES (111, 'JM', 'JAM', 'Jamaica', NULL, NULL);
INSERT INTO `countries` VALUES (112, 'JP', 'JPN', 'Japan', NULL, NULL);
INSERT INTO `countries` VALUES (113, 'JE', 'JEY', 'Jersey', NULL, NULL);
INSERT INTO `countries` VALUES (114, 'JO', 'JOR', 'Jordan', NULL, NULL);
INSERT INTO `countries` VALUES (115, 'KZ', 'KAZ', 'Kazakhstan', NULL, NULL);
INSERT INTO `countries` VALUES (116, 'KE', 'KEN', 'Kenya', NULL, NULL);
INSERT INTO `countries` VALUES (117, 'KI', 'KIR', 'Kiribati', NULL, NULL);
INSERT INTO `countries` VALUES (118, 'KP', 'PRK', 'Korea (Democratic People\'s Republic of)', NULL, NULL);
INSERT INTO `countries` VALUES (119, 'KR', 'KOR', 'Korea (Republic of)', NULL, NULL);
INSERT INTO `countries` VALUES (120, 'KW', 'KWT', 'Kuwait', NULL, NULL);
INSERT INTO `countries` VALUES (121, 'KG', 'KGZ', 'Kyrgyzstan', NULL, NULL);
INSERT INTO `countries` VALUES (122, 'LA', 'LAO', 'Lao People\'s Democratic Republic', NULL, NULL);
INSERT INTO `countries` VALUES (123, 'LV', 'LVA', 'Latvia', NULL, NULL);
INSERT INTO `countries` VALUES (124, 'LB', 'LBN', 'Lebanon', NULL, NULL);
INSERT INTO `countries` VALUES (125, 'LS', 'LSO', 'Lesotho', NULL, NULL);
INSERT INTO `countries` VALUES (126, 'LR', 'LBR', 'Liberia', NULL, NULL);
INSERT INTO `countries` VALUES (127, 'LY', 'LBY', 'Libya', NULL, NULL);
INSERT INTO `countries` VALUES (128, 'LI', 'LIE', 'Liechtenstein', NULL, NULL);
INSERT INTO `countries` VALUES (129, 'LT', 'LTU', 'Lithuania', NULL, NULL);
INSERT INTO `countries` VALUES (130, 'LU', 'LUX', 'Luxembourg', NULL, NULL);
INSERT INTO `countries` VALUES (131, 'MO', 'MAC', 'Macao', NULL, NULL);
INSERT INTO `countries` VALUES (132, 'MK', 'MKD', 'Macedonia (the former Yugoslav Republic of)', NULL, NULL);
INSERT INTO `countries` VALUES (133, 'MG', 'MDG', 'Madagascar', NULL, NULL);
INSERT INTO `countries` VALUES (134, 'MW', 'MWI', 'Malawi', NULL, NULL);
INSERT INTO `countries` VALUES (135, 'MY', 'MYS', 'Malaysia', NULL, NULL);
INSERT INTO `countries` VALUES (136, 'MV', 'MDV', 'Maldives', NULL, NULL);
INSERT INTO `countries` VALUES (137, 'ML', 'MLI', 'Mali', NULL, NULL);
INSERT INTO `countries` VALUES (138, 'MT', 'MLT', 'Malta', NULL, NULL);
INSERT INTO `countries` VALUES (139, 'MH', 'MHL', 'Marshall Islands', NULL, NULL);
INSERT INTO `countries` VALUES (140, 'MQ', 'MTQ', 'Martinique', NULL, NULL);
INSERT INTO `countries` VALUES (141, 'MR', 'MRT', 'Mauritania', NULL, NULL);
INSERT INTO `countries` VALUES (142, 'MU', 'MUS', 'Mauritius', NULL, NULL);
INSERT INTO `countries` VALUES (143, 'YT', 'MYT', 'Mayotte', NULL, NULL);
INSERT INTO `countries` VALUES (144, 'MX', 'MEX', 'Mexico', NULL, NULL);
INSERT INTO `countries` VALUES (145, 'FM', 'FSM', 'Micronesia (Federated States of)', NULL, NULL);
INSERT INTO `countries` VALUES (146, 'MD', 'MDA', 'Moldova (Republic of)', NULL, NULL);
INSERT INTO `countries` VALUES (147, 'MC', 'MCO', 'Monaco', NULL, NULL);
INSERT INTO `countries` VALUES (148, 'MN', 'MNG', 'Mongolia', NULL, NULL);
INSERT INTO `countries` VALUES (149, 'ME', 'MNE', 'Montenegro', NULL, NULL);
INSERT INTO `countries` VALUES (150, 'MS', 'MSR', 'Montserrat', NULL, NULL);
INSERT INTO `countries` VALUES (151, 'MA', 'MAR', 'Morocco', NULL, NULL);
INSERT INTO `countries` VALUES (152, 'MZ', 'MOZ', 'Mozambique', NULL, NULL);
INSERT INTO `countries` VALUES (153, 'MM', 'MMR', 'Myanmar', NULL, NULL);
INSERT INTO `countries` VALUES (154, 'NA', 'NAM', 'Namibia', NULL, NULL);
INSERT INTO `countries` VALUES (155, 'NR', 'NRU', 'Nauru', NULL, NULL);
INSERT INTO `countries` VALUES (156, 'NP', 'NPL', 'Nepal', NULL, NULL);
INSERT INTO `countries` VALUES (157, 'NL', 'NLD', 'Netherlands', NULL, NULL);
INSERT INTO `countries` VALUES (158, 'NC', 'NCL', 'New Caledonia', NULL, NULL);
INSERT INTO `countries` VALUES (159, 'NZ', 'NZL', 'New Zealand', NULL, NULL);
INSERT INTO `countries` VALUES (160, 'NI', 'NIC', 'Nicaragua', NULL, NULL);
INSERT INTO `countries` VALUES (161, 'NE', 'NER', 'Niger', NULL, NULL);
INSERT INTO `countries` VALUES (162, 'NG', 'NGA', 'Nigeria', NULL, NULL);
INSERT INTO `countries` VALUES (163, 'NU', 'NIU', 'Niue', NULL, NULL);
INSERT INTO `countries` VALUES (164, 'NF', 'NFK', 'Norfolk Island', NULL, NULL);
INSERT INTO `countries` VALUES (165, 'MP', 'MNP', 'Northern Mariana Islands', NULL, NULL);
INSERT INTO `countries` VALUES (166, 'NO', 'NOR', 'Norway', NULL, NULL);
INSERT INTO `countries` VALUES (167, 'OM', 'OMN', 'Oman', NULL, NULL);
INSERT INTO `countries` VALUES (168, 'PK', 'PAK', 'Pakistan', NULL, NULL);
INSERT INTO `countries` VALUES (169, 'PW', 'PLW', 'Palau', NULL, NULL);
INSERT INTO `countries` VALUES (170, 'PS', 'PSE', 'Palestine, State of', NULL, NULL);
INSERT INTO `countries` VALUES (171, 'PA', 'PAN', 'Panama', NULL, NULL);
INSERT INTO `countries` VALUES (172, 'PG', 'PNG', 'Papua New Guinea', NULL, NULL);
INSERT INTO `countries` VALUES (173, 'PY', 'PRY', 'Paraguay', NULL, NULL);
INSERT INTO `countries` VALUES (174, 'PE', 'PER', 'Peru', NULL, NULL);
INSERT INTO `countries` VALUES (175, 'PH', 'PHL', 'Philippines', NULL, NULL);
INSERT INTO `countries` VALUES (176, 'PN', 'PCN', 'Pitcairn', NULL, NULL);
INSERT INTO `countries` VALUES (177, 'PL', 'POL', 'Poland', NULL, NULL);
INSERT INTO `countries` VALUES (178, 'PT', 'PRT', 'Portugal', NULL, NULL);
INSERT INTO `countries` VALUES (179, 'PR', 'PRI', 'Puerto Rico', NULL, NULL);
INSERT INTO `countries` VALUES (180, 'QA', 'QAT', 'Qatar', NULL, NULL);
INSERT INTO `countries` VALUES (181, 'RE', 'REU', 'Réunion', NULL, NULL);
INSERT INTO `countries` VALUES (182, 'RO', 'ROU', 'Romania', NULL, NULL);
INSERT INTO `countries` VALUES (183, 'RU', 'RUS', 'Russian Federation', NULL, NULL);
INSERT INTO `countries` VALUES (184, 'RW', 'RWA', 'Rwanda', NULL, NULL);
INSERT INTO `countries` VALUES (185, 'BL', 'BLM', 'Saint Barthélemy', NULL, NULL);
INSERT INTO `countries` VALUES (186, 'SH', 'SHN', 'Saint Helena, Ascension and Tristan da Cunha', NULL, NULL);
INSERT INTO `countries` VALUES (187, 'KN', 'KNA', 'Saint Kitts and Nevis', NULL, NULL);
INSERT INTO `countries` VALUES (188, 'LC', 'LCA', 'Saint Lucia', NULL, NULL);
INSERT INTO `countries` VALUES (189, 'MF', 'MAF', 'Saint Martin (French part)', NULL, NULL);
INSERT INTO `countries` VALUES (190, 'PM', 'SPM', 'Saint Pierre and Miquelon', NULL, NULL);
INSERT INTO `countries` VALUES (191, 'VC', 'VCT', 'Saint Vincent and the Grenadines', NULL, NULL);
INSERT INTO `countries` VALUES (192, 'WS', 'WSM', 'Samoa', NULL, NULL);
INSERT INTO `countries` VALUES (193, 'SM', 'SMR', 'San Marino', NULL, NULL);
INSERT INTO `countries` VALUES (194, 'ST', 'STP', 'Sao Tome and Principe', NULL, NULL);
INSERT INTO `countries` VALUES (195, 'SA', 'SAU', 'Saudi Arabia', NULL, NULL);
INSERT INTO `countries` VALUES (196, 'SN', 'SEN', 'Senegal', NULL, NULL);
INSERT INTO `countries` VALUES (197, 'RS', 'SRB', 'Serbia', NULL, NULL);
INSERT INTO `countries` VALUES (198, 'SC', 'SYC', 'Seychelles', NULL, NULL);
INSERT INTO `countries` VALUES (199, 'SL', 'SLE', 'Sierra Leone', NULL, NULL);
INSERT INTO `countries` VALUES (200, 'SG', 'SGP', 'Singapore', NULL, NULL);
INSERT INTO `countries` VALUES (201, 'SX', 'SXM', 'Sint Maarten (Dutch part)', NULL, NULL);
INSERT INTO `countries` VALUES (202, 'SK', 'SVK', 'Slovakia', NULL, NULL);
INSERT INTO `countries` VALUES (203, 'SI', 'SVN', 'Slovenia', NULL, NULL);
INSERT INTO `countries` VALUES (204, 'SB', 'SLB', 'Solomon Islands', NULL, NULL);
INSERT INTO `countries` VALUES (205, 'SO', 'SOM', 'Somalia', NULL, NULL);
INSERT INTO `countries` VALUES (206, 'ZA', 'ZAF', 'South Africa', NULL, NULL);
INSERT INTO `countries` VALUES (207, 'GS', 'SGS', 'South Georgia and the South Sandwich Islands', NULL, NULL);
INSERT INTO `countries` VALUES (208, 'SS', 'SSD', 'South Sudan', NULL, NULL);
INSERT INTO `countries` VALUES (209, 'ES', 'ESP', 'Spain', NULL, NULL);
INSERT INTO `countries` VALUES (210, 'LK', 'LKA', 'Sri Lanka', NULL, NULL);
INSERT INTO `countries` VALUES (211, 'SD', 'SDN', 'Sudan', NULL, NULL);
INSERT INTO `countries` VALUES (212, 'SR', 'SUR', 'Suriname', NULL, NULL);
INSERT INTO `countries` VALUES (213, 'SJ', 'SJM', 'Svalbard and Jan Mayen', NULL, NULL);
INSERT INTO `countries` VALUES (214, 'SZ', 'SWZ', 'Swaziland', NULL, NULL);
INSERT INTO `countries` VALUES (215, 'SE', 'SWE', 'Sweden', NULL, NULL);
INSERT INTO `countries` VALUES (216, 'CH', 'CHE', 'Switzerland', NULL, NULL);
INSERT INTO `countries` VALUES (217, 'SY', 'SYR', 'Syrian Arab Republic', NULL, NULL);
INSERT INTO `countries` VALUES (218, 'TW', 'TWN', 'Taiwan, Province of China', NULL, NULL);
INSERT INTO `countries` VALUES (219, 'TJ', 'TJK', 'Tajikistan', NULL, NULL);
INSERT INTO `countries` VALUES (220, 'TZ', 'TZA', 'Tanzania, United Republic of', NULL, NULL);
INSERT INTO `countries` VALUES (221, 'TH', 'THA', 'Thailand', NULL, NULL);
INSERT INTO `countries` VALUES (222, 'TL', 'TLS', 'Timor-Leste', NULL, NULL);
INSERT INTO `countries` VALUES (223, 'TG', 'TGO', 'Togo', NULL, NULL);
INSERT INTO `countries` VALUES (224, 'TK', 'TKL', 'Tokelau', NULL, NULL);
INSERT INTO `countries` VALUES (225, 'TO', 'TON', 'Tonga', NULL, NULL);
INSERT INTO `countries` VALUES (226, 'TT', 'TTO', 'Trinidad and Tobago', NULL, NULL);
INSERT INTO `countries` VALUES (227, 'TN', 'TUN', 'Tunisia', NULL, NULL);
INSERT INTO `countries` VALUES (228, 'TR', 'TUR', 'Turkey', NULL, NULL);
INSERT INTO `countries` VALUES (229, 'TM', 'TKM', 'Turkmenistan', NULL, NULL);
INSERT INTO `countries` VALUES (230, 'TC', 'TCA', 'Turks and Caicos Islands', NULL, NULL);
INSERT INTO `countries` VALUES (231, 'TV', 'TUV', 'Tuvalu', NULL, NULL);
INSERT INTO `countries` VALUES (232, 'UG', 'UGA', 'Uganda', NULL, NULL);
INSERT INTO `countries` VALUES (233, 'UA', 'UKR', 'Ukraine', NULL, NULL);
INSERT INTO `countries` VALUES (234, 'AE', 'ARE', 'United Arab Emirates', NULL, NULL);
INSERT INTO `countries` VALUES (235, 'GB', 'GBR', 'United Kingdom of Great Britain and Northern Ireland', NULL, NULL);
INSERT INTO `countries` VALUES (236, 'US', 'USA', 'United States of America', NULL, NULL);
INSERT INTO `countries` VALUES (237, 'UM', 'UMI', 'United States Minor Outlying Islands', NULL, NULL);
INSERT INTO `countries` VALUES (238, 'UY', 'URY', 'Uruguay', NULL, NULL);
INSERT INTO `countries` VALUES (239, 'UZ', 'UZB', 'Uzbekistan', NULL, NULL);
INSERT INTO `countries` VALUES (240, 'VU', 'VUT', 'Vanuatu', NULL, NULL);
INSERT INTO `countries` VALUES (241, 'VE', 'VEN', 'Venezuela (Bolivarian Republic of)', NULL, NULL);
INSERT INTO `countries` VALUES (242, 'VN', 'VNM', 'Viet Nam', NULL, NULL);
INSERT INTO `countries` VALUES (243, 'VG', 'VGB', 'Virgin Islands (British)', NULL, NULL);
INSERT INTO `countries` VALUES (244, 'VI', 'VIR', 'Virgin Islands (U.S.)', NULL, NULL);
INSERT INTO `countries` VALUES (245, 'WF', 'WLF', 'Wallis and Futuna', NULL, NULL);
INSERT INTO `countries` VALUES (246, 'EH', 'ESH', 'Western Sahara', NULL, NULL);
INSERT INTO `countries` VALUES (247, 'YE', 'YEM', 'Yemen', NULL, NULL);
INSERT INTO `countries` VALUES (248, 'ZM', 'ZMB', 'Zambia', NULL, NULL);
INSERT INTO `countries` VALUES (249, 'ZW', 'ZWE', 'Zimbabwe', NULL, NULL);

-- ----------------------------
-- Table structure for data_export_jobs
-- ----------------------------
DROP TABLE IF EXISTS `data_export_jobs`;
CREATE TABLE `data_export_jobs`  (
                                     `id` int(11) NOT NULL AUTO_INCREMENT,
                                     `uuid` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                     `job_id` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                     `job_config` json NULL,
                                     `user_id` int(11) NULL DEFAULT NULL,
                                     `status` enum('waiting','running','failed','completed','cancelled') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'waiting',
                                     `visibility` tinyint(1) NOT NULL DEFAULT 1,
                                     `datatype` enum('genotype','trials','allelefreq','climate','compound','pedigree','unknown','images') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'unknown',
                                     `dataset_ids` json NULL,
                                     `result_size` bigint(20) NULL DEFAULT NULL,
                                     `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP,
                                     `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of data_export_jobs
-- ----------------------------

-- ----------------------------
-- Table structure for data_import_jobs
-- ----------------------------
DROP TABLE IF EXISTS `data_import_jobs`;
CREATE TABLE `data_import_jobs`  (
                                     `id` int(11) NOT NULL AUTO_INCREMENT,
                                     `uuid` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                     `job_id` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                     `job_config` json NULL,
                                     `user_id` int(11) NULL DEFAULT NULL,
                                     `original_filename` varchar(266) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                     `is_update` tinyint(1) NOT NULL DEFAULT 0,
                                     `datasetstate_id` int(11) NOT NULL DEFAULT 1,
                                     `datatype` enum('mcpd','trial','compound','genotype','pedigree','groups','climate','images','shapefile','geotiff') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'mcpd',
                                     `status` enum('waiting','running','failed','completed','cancelled') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'waiting',
                                     `imported` tinyint(1) NOT NULL DEFAULT 0,
                                     `visibility` tinyint(1) NOT NULL DEFAULT 1,
                                     `feedback` json NULL,
                                     `stats` json NULL,
                                     `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP,
                                     `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                     PRIMARY KEY (`id`) USING BTREE,
                                     INDEX `data_import_jobs_uuid`(`uuid`) USING BTREE,
                                     INDEX `data_import_jobs_status`(`status`) USING BTREE,
                                     INDEX `data_import_jobs_visibility`(`visibility`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of data_import_jobs
-- ----------------------------

-- ----------------------------
-- Table structure for datasetaccesslogs
-- ----------------------------
DROP TABLE IF EXISTS `datasetaccesslogs`;
CREATE TABLE `datasetaccesslogs`  (
                                      `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                                      `user_id` int(11) NULL DEFAULT NULL,
                                      `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                      `user_email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                      `user_institution` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                      `dataset_id` int(11) NOT NULL,
                                      `reason` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
                                      `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                                      `updated_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                                      PRIMARY KEY (`id`) USING BTREE,
                                      INDEX `dataset_id`(`dataset_id`) USING BTREE,
                                      CONSTRAINT `datasetaccesslogs_ibfk_1` FOREIGN KEY (`dataset_id`) REFERENCES `datasets` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'If enabled, tracks which user accessed which datasets.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of datasetaccesslogs
-- ----------------------------

-- ----------------------------
-- Table structure for datasetcollaborators
-- ----------------------------
DROP TABLE IF EXISTS `datasetcollaborators`;
CREATE TABLE `datasetcollaborators`  (
                                         `id` int(11) NOT NULL AUTO_INCREMENT,
                                         `dataset_id` int(11) NOT NULL,
                                         `collaborator_id` int(11) NOT NULL,
                                         `collaborator_roles` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Type of contribution of the person to the investigation (e.g. data submitter; author; corresponding author)',
                                         `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                                         `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                                         PRIMARY KEY (`id`) USING BTREE,
                                         INDEX `dataset_id`(`dataset_id`) USING BTREE,
                                         INDEX `collaborator_id`(`collaborator_id`) USING BTREE,
                                         CONSTRAINT `datasetcollaborators_ibfk_1` FOREIGN KEY (`dataset_id`) REFERENCES `datasets` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                                         CONSTRAINT `datasetcollaborators_ibfk_2` FOREIGN KEY (`collaborator_id`) REFERENCES `collaborators` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of datasetcollaborators
-- ----------------------------

-- ----------------------------
-- Table structure for datasetfileresources
-- ----------------------------
DROP TABLE IF EXISTS `datasetfileresources`;
CREATE TABLE `datasetfileresources`  (
                                         `dataset_id` int(11) NOT NULL,
                                         `fileresource_id` int(11) NOT NULL,
                                         `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP,
                                         `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                         PRIMARY KEY (`dataset_id`, `fileresource_id`) USING BTREE,
                                         INDEX `fileresource_id`(`fileresource_id`) USING BTREE,
                                         CONSTRAINT `datasetfileresources_ibfk_1` FOREIGN KEY (`dataset_id`) REFERENCES `datasets` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                                         CONSTRAINT `datasetfileresources_ibfk_2` FOREIGN KEY (`fileresource_id`) REFERENCES `fileresources` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of datasetfileresources
-- ----------------------------

-- ----------------------------
-- Table structure for datasetlocations
-- ----------------------------
DROP TABLE IF EXISTS `datasetlocations`;
CREATE TABLE `datasetlocations`  (
                                     `dataset_id` int(11) NOT NULL,
                                     `location_id` int(11) NOT NULL,
                                     `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP,
                                     `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                     PRIMARY KEY (`dataset_id`, `location_id`) USING BTREE,
                                     INDEX `location_id`(`location_id`) USING BTREE,
                                     CONSTRAINT `datasetlocations_ibfk_1` FOREIGN KEY (`dataset_id`) REFERENCES `datasets` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                                     CONSTRAINT `datasetlocations_ibfk_2` FOREIGN KEY (`location_id`) REFERENCES `locations` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of datasetlocations
-- ----------------------------

-- ----------------------------
-- Table structure for datasetmembers
-- ----------------------------
DROP TABLE IF EXISTS `datasetmembers`;
CREATE TABLE `datasetmembers`  (
                                   `id` int(11) NOT NULL AUTO_INCREMENT,
                                   `dataset_id` int(11) NOT NULL,
                                   `foreign_id` int(11) NOT NULL,
                                   `datasetmembertype_id` int(11) NOT NULL,
                                   `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                                   `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                                   PRIMARY KEY (`id`) USING BTREE,
                                   INDEX `dataset_id`(`dataset_id`) USING BTREE,
                                   INDEX `datasetmembertype_id`(`datasetmembertype_id`) USING BTREE,
                                   INDEX `dataset_id_2`(`dataset_id`, `datasetmembertype_id`) USING BTREE,
                                   INDEX `foreign_id`(`foreign_id`) USING BTREE,
                                   CONSTRAINT `datasetmembers_ibfk_1` FOREIGN KEY (`dataset_id`) REFERENCES `datasets` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                                   CONSTRAINT `datasetmembers_ibfk_2` FOREIGN KEY (`datasetmembertype_id`) REFERENCES `datasetmembertypes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of datasetmembers
-- ----------------------------

-- ----------------------------
-- Table structure for datasetmembertypes
-- ----------------------------
DROP TABLE IF EXISTS `datasetmembertypes`;
CREATE TABLE `datasetmembertypes`  (
                                       `id` int(11) NOT NULL AUTO_INCREMENT,
                                       `target_table` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                       `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                                       `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of datasetmembertypes
-- ----------------------------
INSERT INTO `datasetmembertypes` VALUES (1, 'markers', '2018-03-27 14:26:16', '2018-03-27 14:26:16');
INSERT INTO `datasetmembertypes` VALUES (2, 'germinatebase', '2018-03-27 14:26:16', '2018-03-27 14:26:16');

-- ----------------------------
-- Table structure for datasetmeta
-- ----------------------------
DROP TABLE IF EXISTS `datasetmeta`;
CREATE TABLE `datasetmeta`  (
                                `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                                `dataset_id` int(11) NOT NULL COMMENT 'Foreign key to [datasets] ([datasets].id).',
                                `nr_of_data_objects` bigint(20) UNSIGNED NOT NULL COMMENT 'The number of data objects contained in this dataset.',
                                `nr_of_data_points` bigint(20) UNSIGNED NOT NULL COMMENT 'The number of individual data points contained in this dataset.',
                                `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                                `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                                PRIMARY KEY (`id`) USING BTREE,
                                INDEX `datasetmeta_ibfk_datasets`(`dataset_id`) USING BTREE,
                                CONSTRAINT `datasetmeta_ibfk_1` FOREIGN KEY (`dataset_id`) REFERENCES `datasets` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Defines dataset sizes for the items in the datasets table. This table is automatically updated every hour.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of datasetmeta
-- ----------------------------

-- ----------------------------
-- Table structure for datasetpermissions
-- ----------------------------
DROP TABLE IF EXISTS `datasetpermissions`;
CREATE TABLE `datasetpermissions`  (
                                       `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                                       `dataset_id` int(11) NOT NULL COMMENT 'Foreign key to datasets (datasets.id).',
                                       `user_id` int(11) NULL DEFAULT NULL COMMENT 'Foreign key to Gatekeeper users (Gatekeeper usersid).',
                                       `group_id` int(11) NULL DEFAULT NULL COMMENT 'Foreign key to usergroups table.',
                                       `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                                       `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                                       PRIMARY KEY (`id`) USING BTREE,
                                       INDEX `datasetpermissions_ibfk1`(`dataset_id`) USING BTREE,
                                       INDEX `group_id`(`group_id`) USING BTREE,
                                       CONSTRAINT `datasetpermissions_ibfk_1` FOREIGN KEY (`dataset_id`) REFERENCES `datasets` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                                       CONSTRAINT `datasetpermissions_ibfk_2` FOREIGN KEY (`group_id`) REFERENCES `usergroups` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'This defines which users can view which datasets. Requires Germinate Gatekeeper. This overrides the datasets state.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of datasetpermissions
-- ----------------------------

-- ----------------------------
-- Table structure for datasets
-- ----------------------------
DROP TABLE IF EXISTS `datasets`;
CREATE TABLE `datasets`  (
                             `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                             `experiment_id` int(11) NOT NULL DEFAULT 0 COMMENT 'Foreign key to experiments (experiments.id).',
                             `datasettype_id` int(11) NOT NULL DEFAULT -1 COMMENT 'Foreign key to datasettypes (datasettypes.id).',
                             `name` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Describes the dataset.',
                             `description` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'The name of this dataset.',
                             `date_start` date NULL DEFAULT NULL COMMENT 'Date that the dataset was generated.',
                             `date_end` date NULL DEFAULT NULL COMMENT 'Date at which the dataset recording ended.',
                             `source_file` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                             `datatype` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'A description of the data type of the contained data. Examples might be: \"raw data\", \"BLUPs\", etc.',
                             `dublin_core` json NULL,
                             `version` char(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Dataset version if this exists.',
                             `created_by` int(11) NULL DEFAULT NULL COMMENT 'Defines who created the dataset. This is a FK in Gatekeeper users table. Foreign key to Gatekeeper users (users.id).',
                             `dataset_state_id` int(11) NOT NULL DEFAULT 1 COMMENT 'Foreign key to datasetstates (datasetstates.id).',
                             `license_id` int(11) NULL DEFAULT NULL,
                             `is_external` tinyint(1) NULL DEFAULT 0 COMMENT 'Defines if the dataset is contained within Germinate or from an external source and not stored in the database.',
                             `hyperlink` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Link to access the external dasets.',
                             `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.\n',
                             `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                             `contact` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'The contact to get more information about this dataset.',
                             PRIMARY KEY (`id`) USING BTREE,
                             INDEX `experiment`(`experiment_id`) USING BTREE,
                             INDEX `id`(`id`) USING BTREE,
                             INDEX `datasets_ibfk_2`(`dataset_state_id`) USING BTREE,
                             INDEX `license_id`(`license_id`) USING BTREE,
                             INDEX `datasets_ibfk_datasettypes`(`datasettype_id`) USING BTREE,
                             CONSTRAINT `datasets_ibfk_dataset_state` FOREIGN KEY (`dataset_state_id`) REFERENCES `datasetstates` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                             CONSTRAINT `datasets_ibfk_datasettypes` FOREIGN KEY (`datasettype_id`) REFERENCES `datasettypes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                             CONSTRAINT `datasets_ibfk_experiment` FOREIGN KEY (`experiment_id`) REFERENCES `experiments` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                             CONSTRAINT `datasets_ibfk_license` FOREIGN KEY (`license_id`) REFERENCES `licenses` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Datasets which are defined within Germinate although there can be external datasets which are links out to external data sources most will be held within Germinate.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of datasets
-- ----------------------------

-- ----------------------------
-- Table structure for datasetstates
-- ----------------------------
DROP TABLE IF EXISTS `datasetstates`;
CREATE TABLE `datasetstates`  (
                                  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                                  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Defines the datasetstate.',
                                  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Describes the datasetstate.',
                                  `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                                  `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of datasetstates
-- ----------------------------
INSERT INTO `datasetstates` VALUES (1, 'public', 'Public datasets are visible to all registered users on private web interfaces and everybody on public web interfaces.', '2014-08-07 11:40:08', '2014-08-07 11:45:38');
INSERT INTO `datasetstates` VALUES (2, 'private', 'Private datasets are visible to all registered admin users and the creator of the dataset. They are not visible on the public web interface.', '2014-08-07 11:40:48', '2014-08-07 11:45:40');
INSERT INTO `datasetstates` VALUES (3, 'hidden', 'Hidden datasets are only visible to admins.', '2014-08-07 11:54:33', '2014-08-07 14:09:50');

-- ----------------------------
-- Table structure for datasettypes
-- ----------------------------
DROP TABLE IF EXISTS `datasettypes`;
CREATE TABLE `datasettypes`  (
                                 `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                                 `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Describes the experiment type.',
                                 `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                                 `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                                 PRIMARY KEY (`id`) USING BTREE,
                                 INDEX `id`(`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of datasettypes
-- ----------------------------
INSERT INTO `datasettypes` VALUES (-1, 'unknown', '2015-09-24 10:30:42', NULL);
INSERT INTO `datasettypes` VALUES (1, 'genotype', '2013-08-22 14:32:06', NULL);
INSERT INTO `datasettypes` VALUES (3, 'trials', '2013-09-02 13:16:44', NULL);
INSERT INTO `datasettypes` VALUES (4, 'allelefreq', '2013-10-11 09:23:15', NULL);
INSERT INTO `datasettypes` VALUES (5, 'climate', '2015-09-02 10:35:58', NULL);
INSERT INTO `datasettypes` VALUES (6, 'compound', '2018-11-07 11:49:53', '2018-11-07 11:49:53');
INSERT INTO `datasettypes` VALUES (7, 'pedigree', '2022-03-08 08:43:32', '2022-03-08 08:43:32');

-- ----------------------------
-- Table structure for entitytypes
-- ----------------------------
DROP TABLE IF EXISTS `entitytypes`;
CREATE TABLE `entitytypes`  (
                                `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                                `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'The name of the entity type.',
                                `description` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'Describes the entity type.',
                                `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                                `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of entitytypes
-- ----------------------------
INSERT INTO `entitytypes` VALUES (1, 'Accession', 'The basic working unit of conservation in the genebanks.', '2018-03-27 14:26:06', '2018-03-27 14:26:06');
INSERT INTO `entitytypes` VALUES (2, 'Plant/Plot', 'An individual grown from an accession OR a plot of individuals from the same accession.', '2018-03-27 14:26:06', '2018-03-27 14:26:06');
INSERT INTO `entitytypes` VALUES (3, 'Sample', 'A sample from a plant. An example would be taking multiple readings for the same phenotype from a plant.', '2018-03-27 14:26:06', '2018-03-27 14:26:06');

-- ----------------------------
-- Table structure for experiments
-- ----------------------------
DROP TABLE IF EXISTS `experiments`;
CREATE TABLE `experiments`  (
                                `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                                `experiment_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'The name of the experiment.',
                                `user_id` int(11) NULL DEFAULT NULL COMMENT 'Foreign key to Gatekeeper users (Gatekeeper users.id).\n',
                                `description` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'Describes the experiment.',
                                `experiment_date` date NULL DEFAULT NULL COMMENT 'The date that the experiment was carried out.',
                                `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                                `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Defines ecperiments that are held in Germinate.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of experiments
-- ----------------------------

-- ----------------------------
-- Table structure for fileresources
-- ----------------------------
DROP TABLE IF EXISTS `fileresources`;
CREATE TABLE `fileresources`  (
                                  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'The primary id.',
                                  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'The name of the file resource.',
                                  `path` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'The file name of the actual data file.',
                                  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'A description of the file contents.',
                                  `filesize` bigint(20) NULL DEFAULT NULL COMMENT 'The file size in bytes.',
                                  `fileresourcetype_id` int(11) NOT NULL COMMENT 'Foreign key to fileresourcetypes.',
                                  `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When this record was created.',
                                  `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When this record was last updated.',
                                  PRIMARY KEY (`id`) USING BTREE,
                                  INDEX `fileresource_name`(`name`) USING BTREE,
                                  INDEX `fileresourcetype_id`(`fileresourcetype_id`) USING BTREE,
                                  CONSTRAINT `fileresources_ibfk_1` FOREIGN KEY (`fileresourcetype_id`) REFERENCES `fileresourcetypes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fileresources
-- ----------------------------

-- ----------------------------
-- Table structure for fileresourcetypes
-- ----------------------------
DROP TABLE IF EXISTS `fileresourcetypes`;
CREATE TABLE `fileresourcetypes`  (
                                      `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'The primary id.',
                                      `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'The name of the file type.',
                                      `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'The description of the file type.',
                                      `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When this record was created.',
                                      `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When this record was last updated.',
                                      PRIMARY KEY (`id`) USING BTREE,
                                      INDEX `fileresourcetype_name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fileresourcetypes
-- ----------------------------
INSERT INTO `fileresourcetypes` VALUES (1, 'Trials Shapefile', 'Shape file associated with a phenotypic trial. Fields within the shape file have to match the database entries.', '2022-10-24 10:55:05', '2022-10-24 10:55:05');
INSERT INTO `fileresourcetypes` VALUES (2, 'Trials GeoTIFF', 'GeoTIFF file associated with a phenotypic trial. The \"created_on\" date of this fileresource determines the time point at which it was recorded.', '2022-10-24 10:55:05', '2022-10-24 10:55:05');

-- ----------------------------
-- Table structure for germinatebase
-- ----------------------------
DROP TABLE IF EXISTS `germinatebase`;
CREATE TABLE `germinatebase`  (
                                  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                                  `general_identifier` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'A unique identifier.',
                                  `number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'This is the unique identifier for accessions within a genebank, and is assigned when a sample is\nentered into the genebank collection (e.g. ‘PI 113869’).',
                                  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'A unique name which defines an entry in the germinatbase table.',
                                  `bank_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Alternative genebank number.',
                                  `taxonomy_id` int(11) NULL DEFAULT NULL COMMENT 'Foreign key to taxonomies (taxonomies.id).',
                                  `plant_passport` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Record if the entry has a plant passport.',
                                  `location_id` int(11) NULL DEFAULT NULL COMMENT 'Foreign key to locations (locations.id).',
                                  `entitytype_id` int(11) NULL DEFAULT 1 COMMENT 'Foreign key to entitytypes (entitytypes.id).',
                                  `entityparent_id` int(11) NULL DEFAULT NULL COMMENT 'Foreign key to germinatebase (germinatebase.id).',
                                  `pdci` float(64, 10) NULL DEFAULT NULL COMMENT 'Passport Data Completeness Index. This is calculated by Germinate. Manual editing of this field will be overwritten.',
                                  `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                                  `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                                  PRIMARY KEY (`id`) USING BTREE,
                                  INDEX `taxonomy_id`(`taxonomy_id`) USING BTREE,
                                  INDEX `collsite_id`(`location_id`) USING BTREE,
                                  INDEX `general_identifier`(`general_identifier`) USING BTREE,
                                  INDEX `germinatebase_ibfk_entitytype`(`entitytype_id`) USING BTREE,
                                  INDEX `germinatebase_ibfk_entityparent`(`entityparent_id`) USING BTREE,
                                  INDEX `germinatebase_name`(`name`) USING BTREE,
                                  INDEX `germinatebase_number`(`number`) USING BTREE,
                                  CONSTRAINT `germinatebase_ibfk_entityparent` FOREIGN KEY (`entityparent_id`) REFERENCES `germinatebase` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
                                  CONSTRAINT `germinatebase_ibfk_entitytype` FOREIGN KEY (`entitytype_id`) REFERENCES `entitytypes` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
                                  CONSTRAINT `germinatebase_ibfk_location` FOREIGN KEY (`location_id`) REFERENCES `locations` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
                                  CONSTRAINT `germinatebase_ibfk_taxonomy` FOREIGN KEY (`taxonomy_id`) REFERENCES `taxonomies` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Germinatebase is the Germinate base table which contains passport and other germplasm definition data.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of germinatebase
-- ----------------------------

-- ----------------------------
-- Table structure for germplasminstitutions
-- ----------------------------
DROP TABLE IF EXISTS `germplasminstitutions`;
CREATE TABLE `germplasminstitutions`  (
                                          `germinatebase_id` int(11) NOT NULL,
                                          `institution_id` int(11) NOT NULL,
                                          `type` enum('collection','maintenance','breeding','duplicate','donor') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'maintenance',
                                          `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP,
                                          `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                          PRIMARY KEY (`germinatebase_id`, `institution_id`, `type`) USING BTREE,
                                          INDEX `institution_id`(`institution_id`) USING BTREE,
                                          CONSTRAINT `germplasminstitutions_ibfk_1` FOREIGN KEY (`germinatebase_id`) REFERENCES `germinatebase` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                                          CONSTRAINT `germplasminstitutions_ibfk_2` FOREIGN KEY (`institution_id`) REFERENCES `institutions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of germplasminstitutions
-- ----------------------------

-- ----------------------------
-- Table structure for groupmembers
-- ----------------------------
DROP TABLE IF EXISTS `groupmembers`;
CREATE TABLE `groupmembers`  (
                                 `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                                 `foreign_id` int(2) NOT NULL COMMENT 'Foreign key to [table] ([table].id).',
                                 `group_id` int(2) NOT NULL COMMENT 'Foreign key to groups (groups.id).',
                                 `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                                 `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                                 PRIMARY KEY (`id`) USING BTREE,
                                 INDEX `group_id`(`group_id`) USING BTREE,
                                 INDEX `groupmembers_foreign`(`foreign_id`) USING BTREE,
                                 CONSTRAINT `groupmembers_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Defines which entities are contained within a group. These can be the primary key from any table.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of groupmembers
-- ----------------------------

-- ----------------------------
-- Table structure for groups
-- ----------------------------
DROP TABLE IF EXISTS `groups`;
CREATE TABLE `groups`  (
                           `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                           `grouptype_id` int(11) NOT NULL COMMENT 'Foreign key to grouptypes (grouptypes.id).',
                           `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'The name of the group which can be used to identify it.',
                           `description` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'A free text description of the group. This has no length limitations.',
                           `visibility` tinyint(1) NULL DEFAULT NULL COMMENT 'Defines if the group is visuble or hidden from the Germinate user interface.',
                           `created_by` int(11) NULL DEFAULT NULL COMMENT 'Defines who created the group. Foreign key to Gatekeeper users (Gatekeeper users.id).',
                           `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Foreign key to locations (locations.id).',
                           `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                           PRIMARY KEY (`id`) USING BTREE,
                           INDEX `grouptype_id`(`grouptype_id`) USING BTREE,
                           CONSTRAINT `groups_ibfk_1` FOREIGN KEY (`grouptype_id`) REFERENCES `grouptypes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Allows the definition of groups within Germinate. Germinate supports a number of different group types such as germinatebase accesion groups and marker groups.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of groups
-- ----------------------------

-- ----------------------------
-- Table structure for grouptypes
-- ----------------------------
DROP TABLE IF EXISTS `grouptypes`;
CREATE TABLE `grouptypes`  (
                               `id` int(11) NOT NULL AUTO_INCREMENT,
                               `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
                               `target_table` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
                               `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                               `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of grouptypes
-- ----------------------------
INSERT INTO `grouptypes` VALUES (1, 'Collectingsites', 'locations', '2013-07-15 16:19:39', NULL);
INSERT INTO `grouptypes` VALUES (2, 'Markers', 'markers', '2013-07-15 16:19:50', NULL);
INSERT INTO `grouptypes` VALUES (3, 'Accessions', 'germinatebase', '2013-07-29 12:04:37', NULL);

-- ----------------------------
-- Table structure for image_to_tags
-- ----------------------------
DROP TABLE IF EXISTS `image_to_tags`;
CREATE TABLE `image_to_tags`  (
                                  `image_id` int(11) NOT NULL,
                                  `imagetag_id` int(11) NOT NULL,
                                  `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP,
                                  `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                  PRIMARY KEY (`image_id`, `imagetag_id`) USING BTREE,
                                  INDEX `image_to_tag_ibfk_imagetag`(`imagetag_id`) USING BTREE,
                                  CONSTRAINT `image_to_tag_ibfk_image` FOREIGN KEY (`image_id`) REFERENCES `images` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                                  CONSTRAINT `image_to_tag_ibfk_imagetag` FOREIGN KEY (`imagetag_id`) REFERENCES `imagetags` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of image_to_tags
-- ----------------------------

-- ----------------------------
-- Table structure for images
-- ----------------------------
DROP TABLE IF EXISTS `images`;
CREATE TABLE `images`  (
                           `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                           `imagetype_id` int(11) NOT NULL DEFAULT 0 COMMENT 'Foreign key to imagetypes (imagetypes.id).',
                           `description` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'A description of what the image shows if required.',
                           `foreign_id` int(11) NOT NULL DEFAULT 0 COMMENT 'Relates to the UID of the table to which the comment relates.',
                           `path` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'The file system path to the image.',
                           `exif` json NULL,
                           `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                           `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                           PRIMARY KEY (`id`) USING BTREE,
                           INDEX `imagetype_id`(`imagetype_id`) USING BTREE,
                           INDEX `imagetype_foreign_id`(`foreign_id`) USING BTREE,
                           CONSTRAINT `images_ibfk_1` FOREIGN KEY (`imagetype_id`) REFERENCES `imagetypes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of images
-- ----------------------------

-- ----------------------------
-- Table structure for imagetags
-- ----------------------------
DROP TABLE IF EXISTS `imagetags`;
CREATE TABLE `imagetags`  (
                              `id` int(11) NOT NULL AUTO_INCREMENT,
                              `tag_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                              `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP,
                              `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              PRIMARY KEY (`id`) USING BTREE,
                              INDEX `imagetags_tag_name`(`tag_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of imagetags
-- ----------------------------

-- ----------------------------
-- Table structure for imagetypes
-- ----------------------------
DROP TABLE IF EXISTS `imagetypes`;
CREATE TABLE `imagetypes`  (
                               `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                               `description` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'A description of the image type. This would usually be a description of what the image was showing in general terms such as \'field image\' or \'insitu hybridisation images\'.',
                               `reference_table` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'The table which the image type relates to.',
                               `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                               `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of imagetypes
-- ----------------------------
INSERT INTO `imagetypes` VALUES (1, 'accession images', 'germinatebase', '2009-03-04 14:13:22', NULL);
INSERT INTO `imagetypes` VALUES (3, 'phenotype images', 'phenotypes', '2018-11-06 13:46:11', '2018-11-06 13:46:11');
INSERT INTO `imagetypes` VALUES (4, 'Data story images', 'storysteps', '2023-08-03 08:12:31', '2023-08-03 08:12:31');

-- ----------------------------
-- Table structure for institutions
-- ----------------------------
DROP TABLE IF EXISTS `institutions`;
CREATE TABLE `institutions`  (
                                 `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                                 `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'If there is a defined ISO code for the institute this should be used here.',
                                 `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'The institute name.',
                                 `acronym` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'If there is an acronym for the institute.',
                                 `country_id` int(11) NULL DEFAULT NULL COMMENT 'Foreign key to countries.id.',
                                 `contact` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'The contact at the institute which should be used for correspondence.',
                                 `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'The telephone number for the institute.',
                                 `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'The email address to contact the institute.',
                                 `address` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'The postal address of the institute.',
                                 `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                                 `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                                 PRIMARY KEY (`id`) USING BTREE,
                                 INDEX `country_id`(`country_id`) USING BTREE,
                                 CONSTRAINT `institutions_ibfk_1` FOREIGN KEY (`country_id`) REFERENCES `countries` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Defines institutions within Germinate. Accessions may be associated with an institute and this can be defined here.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of institutions
-- ----------------------------

-- ----------------------------
-- Table structure for licensedata
-- ----------------------------
DROP TABLE IF EXISTS `licensedata`;
CREATE TABLE `licensedata`  (
                                `id` int(11) NOT NULL AUTO_INCREMENT,
                                `license_id` int(11) NOT NULL,
                                `locale_id` int(11) NOT NULL,
                                `content` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
                                `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                                `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                                PRIMARY KEY (`id`) USING BTREE,
                                INDEX `license_id`(`license_id`) USING BTREE,
                                INDEX `locale_id`(`locale_id`) USING BTREE,
                                CONSTRAINT `licensedata_ibfk_1` FOREIGN KEY (`license_id`) REFERENCES `licenses` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                                CONSTRAINT `licensedata_ibfk_2` FOREIGN KEY (`locale_id`) REFERENCES `locales` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of licensedata
-- ----------------------------
INSERT INTO `licensedata` VALUES (1, 1, 1, '<h3>Creative Commons Attribution 4.0 International Public License</h3> <p>By exercising the Licensed Rights (defined below), You accept and agree to be bound by the terms and conditions of this Creative Commons Attribution 4.0 International Public License (\"Public License\"). To the extent this Public License may be interpreted as a contract, You are granted the Licensed Rights in consideration of Your acceptance of these terms and conditions, and the Licensor grants You such rights in consideration of benefits the Licensor receives from making the Licensed Material available under these terms and conditions.</p> <p id=\"s1\"><strong>Section 1 – Definitions.</strong></p> <ol type=\"a\"> <li id=\"s1a\"><strong>Adapted Material</strong> means material subject to Copyright and Similar Rights that is derived from or based upon the Licensed Material and in which the Licensed Material is translated, altered, arranged, transformed, or otherwise modified in a manner requiring permission under the Copyright and Similar Rights held by the Licensor. For purposes of this Public License, where the Licensed Material is a musical work, performance, or sound recording, Adapted Material is always produced where the Licensed Material is synched in timed relation with a moving image.</li> <li id=\"s1b\"><strong>Adapter\'s License</strong> means the license You apply to Your Copyright and Similar Rights in Your contributions to Adapted Material in accordance with the terms and conditions of this Public License.</li> <li id=\"s1c\"><strong>Copyright and Similar Rights</strong> means copyright and/or similar rights closely related to copyright including, without limitation, performance, broadcast, sound recording, and Sui Generis Database Rights, without regard to how the rights are labeled or categorized. For purposes of this Public License, the rights specified in Section <a href=\"#s2b\">2(b)(1)-(2)</a> are not Copyright and Similar Rights.</li> <li id=\"s1d\"><strong>Effective Technological Measures</strong> means those measures that, in the absence of proper authority, may not be circumvented under laws fulfilling obligations under Article 11 of the WIPO Copyright Treaty adopted on December 20, 1996, and/or similar international agreements.</li> <li id=\"s1e\"><strong>Exceptions and Limitations</strong> means fair use, fair dealing, and/or any other exception or limitation to Copyright and Similar Rights that applies to Your use of the Licensed Material.</li> <li id=\"s1f\"><strong>Licensed Material</strong> means the artistic or literary work, database, or other material to which the Licensor applied this Public License.</li> <li id=\"s1g\"><strong>Licensed Rights</strong> means the rights granted to You subject to the terms and conditions of this Public License, which are limited to all Copyright and Similar Rights that apply to Your use of the Licensed Material and that the Licensor has authority to license.</li> <li id=\"s1h\"><strong>Licensor</strong> means the individual(s) or entity(ies) granting rights under this Public License.</li> <li id=\"s1i\"><strong>Share</strong> means to provide material to the public by any means or process that requires permission under the Licensed Rights, such as reproduction, public display, public performance, distribution, dissemination, communication, or importation, and to make material available to the public including in ways that members of the public may access the material from a place and at a time individually chosen by them.</li> <li id=\"s1j\"><strong>Sui Generis Database Rights</strong> means rights other than copyright resulting from Directive 96/9/EC of the European Parliament and of the Council of 11 March 1996 on the legal protection of databases, as amended and/or succeeded, as well as other essentially equivalent rights anywhere in the world.</li> <li id=\"s1k\"><strong>You</strong> means the individual or entity exercising the Licensed Rights under this Public License. <strong>Your</strong> has a corresponding meaning.</li> </ol> <p id=\"s2\"><strong>Section 2 – Scope.</strong></p> <ol type=\"a\"> <li id=\"s2a\"><strong>License grant</strong>. <ol> <li id=\"s2a1\">Subject to the terms and conditions of this Public License, the Licensor hereby grants You a worldwide, royalty-free, non-sublicensable, non-exclusive, irrevocable license to exercise the Licensed Rights in the Licensed Material to: <ol type=\"A\"> <li id=\"s2a1A\">reproduce and Share the Licensed Material, in whole or in part; and</li> <li id=\"s2a1B\">produce, reproduce, and Share Adapted Material.</li> </ol> </li><li id=\"s2a2\"><span style=\"text-decoration: underline;\">Exceptions and Limitations</span>. For the avoidance of doubt, where Exceptions and Limitations apply to Your use, this Public License does not apply, and You do not need to comply with its terms and conditions.</li> <li id=\"s2a3\"><span style=\"text-decoration: underline;\">Term</span>. The term of this Public License is specified in Section <a href=\"#s6a\">6(a)</a>.</li> <li id=\"s2a4\"><span style=\"text-decoration: underline;\">Media and formats; technical modifications allowed</span>. The Licensor authorizes You to exercise the Licensed Rights in all media and formats whether now known or hereafter created, and to make technical modifications necessary to do so. The Licensor waives and/or agrees not to assert any right or authority to forbid You from making technical modifications necessary to exercise the Licensed Rights, including technical modifications necessary to circumvent Effective Technological Measures. For purposes of this Public License, simply making modifications authorized by this Section <a href=\"#s2a4\">2(a)(4)</a> never produces Adapted Material.</li> <li id=\"s2a5\"><span style=\"text-decoration: underline;\">Downstream recipients</span>. <div class=\"para\"> <ol type=\"A\"> <li id=\"s2a5A\"><span style=\"text-decoration: underline;\">Offer from the Licensor – Licensed Material</span>. Every recipient of the Licensed Material automatically receives an offer from the Licensor to exercise the Licensed Rights under the terms and conditions of this Public License.</li> <li id=\"s2a5B\"><span style=\"text-decoration: underline;\">No downstream restrictions</span>. You may not offer or impose any additional or different terms or conditions on, or apply any Effective Technological Measures to, the Licensed Material if doing so restricts exercise of the Licensed Rights by any recipient of the Licensed Material.</li> </ol> </div> </li><li id=\"s2a6\"><span style=\"text-decoration: underline;\">No endorsement</span>. Nothing in this Public License constitutes or may be construed as permission to assert or imply that You are, or that Your use of the Licensed Material is, connected with, or sponsored, endorsed, or granted official status by, the Licensor or others designated to receive attribution as provided in Section <a href=\"#s3a1Ai\">3(a)(1)(A)(i)</a>.</li> </ol> </li><li id=\"s2b\"><p><strong>Other rights</strong>.</p> <ol> <li id=\"s2b1\">Moral rights, such as the right of integrity, are not licensed under this Public License, nor are publicity, privacy, and/or other similar personality rights; however, to the extent possible, the Licensor waives and/or agrees not to assert any such rights held by the Licensor to the limited extent necessary to allow You to exercise the Licensed Rights, but not otherwise.</li> <li id=\"s2b2\">Patent and trademark rights are not licensed under this Public License.</li> <li id=\"s2b3\">To the extent possible, the Licensor waives any right to collect royalties from You for the exercise of the Licensed Rights, whether directly or through a collecting society under any voluntary or waivable statutory or compulsory licensing scheme. In all other cases the Licensor expressly reserves any right to collect such royalties.</li> </ol> </li> </ol> <p id=\"s3\"><strong>Section 3 – License Conditions.</strong></p> <p>Your exercise of the Licensed Rights is expressly made subject to the following conditions.</p> <ol type=\"a\"> <li id=\"s3a\"><p><strong>Attribution</strong>.</p> <ol> <li id=\"s3a1\"><p>If You Share the Licensed Material (including in modified form), You must:</p> <ol type=\"A\"> <li id=\"s3a1A\">retain the following if it is supplied by the Licensor with the Licensed Material: <ol type=\"i\"> <li id=\"s3a1Ai\">identification of the creator(s) of the Licensed Material and any others designated to receive attribution, in any reasonable manner requested by the Licensor (including by pseudonym if designated);</li> <li id=\"s3a1Aii\">a copyright notice;</li> <li id=\"s3a1Aiii\">a notice that refers to this Public License; </li> <li id=\"s3a1Aiv\">a notice that refers to the disclaimer of warranties;</li> <li id=\"s3a1Av\">a URI or hyperlink to the Licensed Material to the extent reasonably practicable;</li> </ol> </li><li id=\"s3a1B\">indicate if You modified the Licensed Material and retain an indication of any previous modifications; and</li> <li id=\"s3a1C\">indicate the Licensed Material is licensed under this Public License, and include the text of, or the URI or hyperlink to, this Public License.</li> </ol> </li> <li id=\"s3a2\">You may satisfy the conditions in Section <a href=\"#s3a1\">3(a)(1)</a> in any reasonable manner based on the medium, means, and context in which You Share the Licensed Material. For example, it may be reasonable to satisfy the conditions by providing a URI or hyperlink to a resource that includes the required information.</li> <li id=\"s3a3\">If requested by the Licensor, You must remove any of the information required by Section <a href=\"#s3a1A\">3(a)(1)(A)</a> to the extent reasonably practicable.</li> <li id=\"s3a4\">If You Share Adapted Material You produce, the Adapter\'s License You apply must not prevent recipients of the Adapted Material from complying with this Public License.</li> </ol> </li> </ol> <p id=\"s4\"><strong>Section 4 – Sui Generis Database Rights.</strong></p> <p>Where the Licensed Rights include Sui Generis Database Rights that apply to Your use of the Licensed Material:</p> <ol type=\"a\"> <li id=\"s4a\">for the avoidance of doubt, Section <a href=\"#s2a1\">2(a)(1)</a> grants You the right to extract, reuse, reproduce, and Share all or a substantial portion of the contents of the database;</li> <li id=\"s4b\">if You include all or a substantial portion of the database contents in a database in which You have Sui Generis Database Rights, then the database in which You have Sui Generis Database Rights (but not its individual contents) is Adapted Material; and</li> <li id=\"s4c\">You must comply with the conditions in Section <a href=\"#s3a\">3(a)</a> if You Share all or a substantial portion of the contents of the database.</li> </ol> For the avoidance of doubt, this Section <a href=\"#s4\">4</a> supplements and does not replace Your obligations under this Public License where the Licensed Rights include other Copyright and Similar Rights. <p id=\"s5\"><strong>Section 5 – Disclaimer of Warranties and Limitation of Liability.</strong></p> <ol style=\"font-weight: bold;\" type=\"a\"> <li id=\"s5a\"><strong>Unless otherwise separately undertaken by the Licensor, to the extent possible, the Licensor offers the Licensed Material as-is and as-available, and makes no representations or warranties of any kind concerning the Licensed Material, whether express, implied, statutory, or other. This includes, without limitation, warranties of title, merchantability, fitness for a particular purpose, non-infringement, absence of latent or other defects, accuracy, or the presence or absence of errors, whether or not known or discoverable. Where disclaimers of warranties are not allowed in full or in part, this disclaimer may not apply to You.</strong></li> <li id=\"s5b\"><strong>To the extent possible, in no event will the Licensor be liable to You on any legal theory (including, without limitation, negligence) or otherwise for any direct, special, indirect, incidental, consequential, punitive, exemplary, or other losses, costs, expenses, or damages arising out of this Public License or use of the Licensed Material, even if the Licensor has been advised of the possibility of such losses, costs, expenses, or damages. Where a limitation of liability is not allowed in full or in part, this limitation may not apply to You.</strong></li> </ol> <ol start=\"3\" type=\"a\"> <li id=\"s5c\">The disclaimer of warranties and limitation of liability provided above shall be interpreted in a manner that, to the extent possible, most closely approximates an absolute disclaimer and waiver of all liability.</li> </ol> <p id=\"s6\"><strong>Section 6 – Term and Termination.</strong></p> <ol type=\"a\"> <li id=\"s6a\">This Public License applies for the term of the Copyright and Similar Rights licensed here. However, if You fail to comply with this Public License, then Your rights under this Public License terminate automatically.</li> <li id=\"s6b\"> <p>Where Your right to use the Licensed Material has terminated under Section <a href=\"#s6a\">6(a)</a>, it reinstates:</p> <ol> <li id=\"s6b1\">automatically as of the date the violation is cured, provided it is cured within 30 days of Your discovery of the violation; or</li> <li id=\"s6b2\">upon express reinstatement by the Licensor.</li> </ol> For the avoidance of doubt, this Section <a href=\"#s6b\">6(b)</a> does not affect any right the Licensor may have to seek remedies for Your violations of this Public License.</li> <li id=\"s6c\">For the avoidance of doubt, the Licensor may also offer the Licensed Material under separate terms or conditions or stop distributing the Licensed Material at any time; however, doing so will not terminate this Public License.</li> <li id=\"s6d\">Sections <a href=\"#s1\">1</a>, <a href=\"#s5\">5</a>, <a href=\"#s6\">6</a>, <a href=\"#s7\">7</a>, and <a href=\"#s8\">8</a> survive termination of this Public License.</li> </ol> <p id=\"s7\"><strong>Section 7 – Other Terms and Conditions.</strong></p> <ol type=\"a\"> <li id=\"s7a\">The Licensor shall not be bound by any additional or different terms or conditions communicated by You unless expressly agreed.</li> <li id=\"s7b\">Any arrangements, understandings, or agreements regarding the Licensed Material not stated herein are separate from and independent of the terms and conditions of this Public License.</li> </ol> <p id=\"s8\"><strong>Section 8 – Interpretation.</strong></p> <ol type=\"a\"> <li id=\"s8a\">For the avoidance of doubt, this Public License does not, and shall not be interpreted to, reduce, limit, restrict, or impose conditions on any use of the Licensed Material that could lawfully be made without permission under this Public License.</li> <li id=\"s8b\">To the extent possible, if any provision of this Public License is deemed unenforceable, it shall be automatically reformed to the minimum extent necessary to make it enforceable. If the provision cannot be reformed, it shall be severed from this Public License without affecting the enforceability of the remaining terms and conditions.</li> <li id=\"s8c\">No term or condition of this Public License will be waived and no failure to comply consented to unless expressly agreed to by the Licensor.</li> <li id=\"s8d\">Nothing in this Public License constitutes or may be interpreted as a limitation upon, or waiver of, any privileges and immunities that apply to the Licensor or You, including from the legal processes of any jurisdiction or authority.</li> </ol>', '2022-09-26 09:53:37', '2022-09-26 09:53:37');
INSERT INTO `licensedata` VALUES (2, 2, 1, '<h3>Creative Commons Attribution-ShareAlike 4.0 International Public License</h3> <p>By exercising the Licensed Rights (defined below), You accept and agree to be bound by the terms and conditions of this Creative Commons Attribution-ShareAlike 4.0 International Public License (\"Public License\"). To the extent this Public License may be interpreted as a contract, You are granted the Licensed Rights in consideration of Your acceptance of these terms and conditions, and the Licensor grants You such rights in consideration of benefits the Licensor receives from making the Licensed Material available under these terms and conditions.</p> <p id=\"s1\"><strong>Section 1 – Definitions.</strong></p> <ol type=\"a\"> <li id=\"s1a\"><strong>Adapted Material</strong> means material subject to Copyright and Similar Rights that is derived from or based upon the Licensed Material and in which the Licensed Material is translated, altered, arranged, transformed, or otherwise modified in a manner requiring permission under the Copyright and Similar Rights held by the Licensor. For purposes of this Public License, where the Licensed Material is a musical work, performance, or sound recording, Adapted Material is always produced where the Licensed Material is synched in timed relation with a moving image.</li> <li id=\"s1b\"><strong>Adapter\'s License</strong> means the license You apply to Your Copyright and Similar Rights in Your contributions to Adapted Material in accordance with the terms and conditions of this Public License.</li> <li id=\"s1c\"><strong>BY-SA Compatible License</strong> means a license listed at <a href=\"//creativecommons.org/compatiblelicenses\"> creativecommons.org/compatiblelicenses</a>, approved by Creative Commons as essentially the equivalent of this Public License.</li> <li id=\"s1d\"><strong>Copyright and Similar Rights</strong> means copyright and/or similar rights closely related to copyright including, without limitation, performance, broadcast, sound recording, and Sui Generis Database Rights, without regard to how the rights are labeled or categorized. For purposes of this Public License, the rights specified in Section <a href=\"#s2b\">2(b)(1)-(2)</a> are not Copyright and Similar Rights.</li> <li id=\"s1e\"><strong>Effective Technological Measures</strong> means those measures that, in the absence of proper authority, may not be circumvented under laws fulfilling obligations under Article 11 of the WIPO Copyright Treaty adopted on December 20, 1996, and/or similar international agreements.</li> <li id=\"s1f\"><strong>Exceptions and Limitations</strong> means fair use, fair dealing, and/or any other exception or limitation to Copyright and Similar Rights that applies to Your use of the Licensed Material.</li> <li id=\"s1g\"><strong>License Elements</strong> means the license attributes listed in the name of a Creative Commons Public License. The License Elements of this Public License are Attribution and ShareAlike.</li> <li id=\"s1h\"><strong>Licensed Material</strong> means the artistic or literary work, database, or other material to which the Licensor applied this Public License.</li> <li id=\"s1i\"><strong>Licensed Rights</strong> means the rights granted to You subject to the terms and conditions of this Public License, which are limited to all Copyright and Similar Rights that apply to Your use of the Licensed Material and that the Licensor has authority to license.</li> <li id=\"s1j\"><strong>Licensor</strong> means the individual(s) or entity(ies) granting rights under this Public License.</li> <li id=\"s1k\"><strong>Share</strong> means to provide material to the public by any means or process that requires permission under the Licensed Rights, such as reproduction, public display, public performance, distribution, dissemination, communication, or importation, and to make material available to the public including in ways that members of the public may access the material from a place and at a time individually chosen by them.</li> <li id=\"s1l\"><strong>Sui Generis Database Rights</strong> means rights other than copyright resulting from Directive 96/9/EC of the European Parliament and of the Council of 11 March 1996 on the legal protection of databases, as amended and/or succeeded, as well as other essentially equivalent rights anywhere in the world.</li> <li id=\"s1m\"><strong>You</strong> means the individual or entity exercising the Licensed Rights under this Public License. <strong>Your</strong> has a corresponding meaning.</li> </ol> <p id=\"s2\"><strong>Section 2 – Scope.</strong></p> <ol type=\"a\"> <li id=\"s2a\"><strong>License grant</strong>. <ol> <li id=\"s2a1\">Subject to the terms and conditions of this Public License, the Licensor hereby grants You a worldwide, royalty-free, non-sublicensable, non-exclusive, irrevocable license to exercise the Licensed Rights in the Licensed Material to: <ol type=\"A\"> <li id=\"s2a1A\">reproduce and Share the Licensed Material, in whole or in part; and</li> <li id=\"s2a1B\">produce, reproduce, and Share Adapted Material.</li> </ol> </li><li id=\"s2a2\"><span style=\"text-decoration: underline;\">Exceptions and Limitations</span>. For the avoidance of doubt, where Exceptions and Limitations apply to Your use, this Public License does not apply, and You do not need to comply with its terms and conditions.</li> <li id=\"s2a3\"><span style=\"text-decoration: underline;\">Term</span>. The term of this Public License is specified in Section <a href=\"#s6a\">6(a)</a>.</li> <li id=\"s2a4\"><span style=\"text-decoration: underline;\">Media and formats; technical modifications allowed</span>. The Licensor authorizes You to exercise the Licensed Rights in all media and formats whether now known or hereafter created, and to make technical modifications necessary to do so. The Licensor waives and/or agrees not to assert any right or authority to forbid You from making technical modifications necessary to exercise the Licensed Rights, including technical modifications necessary to circumvent Effective Technological Measures. For purposes of this Public License, simply making modifications authorized by this Section <a href=\"#s2a4\">2(a)(4)</a> never produces Adapted Material.</li> <li id=\"s2a5\"><span style=\"text-decoration: underline;\">Downstream recipients</span>. <div class=\"para\"> <ol type=\"A\"> <li id=\"s2a5A\"><span style=\"text-decoration: underline;\">Offer from the Licensor – Licensed Material</span>. Every recipient of the Licensed Material automatically receives an offer from the Licensor to exercise the Licensed Rights under the terms and conditions of this Public License.</li> <li id=\"s2a5B\"><span style=\"text-decoration: underline;\">Additional offer from the Licensor – Adapted Material</span>. Every recipient of Adapted Material from You automatically receives an offer from the Licensor to exercise the Licensed Rights in the Adapted Material under the conditions of the Adapter’s License You apply.</li> <li id=\"s2a5C\"><span style=\"text-decoration: underline;\">No downstream restrictions</span>. You may not offer or impose any additional or different terms or conditions on, or apply any Effective Technological Measures to, the Licensed Material if doing so restricts exercise of the Licensed Rights by any recipient of the Licensed Material.</li> </ol> </div> </li><li id=\"s2a6\"><span style=\"text-decoration: underline;\">No endorsement</span>. Nothing in this Public License constitutes or may be construed as permission to assert or imply that You are, or that Your use of the Licensed Material is, connected with, or sponsored, endorsed, or granted official status by, the Licensor or others designated to receive attribution as provided in Section <a href=\"#s3a1Ai\">3(a)(1)(A)(i)</a>.</li> </ol> </li><li id=\"s2b\"><p><strong>Other rights</strong>.</p> <ol> <li id=\"s2b1\">Moral rights, such as the right of integrity, are not licensed under this Public License, nor are publicity, privacy, and/or other similar personality rights; however, to the extent possible, the Licensor waives and/or agrees not to assert any such rights held by the Licensor to the limited extent necessary to allow You to exercise the Licensed Rights, but not otherwise.</li> <li id=\"s2b2\">Patent and trademark rights are not licensed under this Public License.</li> <li id=\"s2b3\">To the extent possible, the Licensor waives any right to collect royalties from You for the exercise of the Licensed Rights, whether directly or through a collecting society under any voluntary or waivable statutory or compulsory licensing scheme. In all other cases the Licensor expressly reserves any right to collect such royalties.</li> </ol> </li> </ol> <p id=\"s3\"><strong>Section 3 – License Conditions.</strong></p> <p>Your exercise of the Licensed Rights is expressly made subject to the following conditions.</p> <ol type=\"a\"> <li id=\"s3a\"><p><strong>Attribution</strong>.</p> <ol> <li id=\"s3a1\"><p>If You Share the Licensed Material (including in modified form), You must:</p> <ol type=\"A\"> <li id=\"s3a1A\">retain the following if it is supplied by the Licensor with the Licensed Material: <ol type=\"i\"> <li id=\"s3a1Ai\">identification of the creator(s) of the Licensed Material and any others designated to receive attribution, in any reasonable manner requested by the Licensor (including by pseudonym if designated);</li> <li id=\"s3a1Aii\">a copyright notice;</li> <li id=\"s3a1Aiii\">a notice that refers to this Public License; </li> <li id=\"s3a1Aiv\">a notice that refers to the disclaimer of warranties;</li> <li id=\"s3a1Av\">a URI or hyperlink to the Licensed Material to the extent reasonably practicable;</li> </ol> </li><li id=\"s3a1B\">indicate if You modified the Licensed Material and retain an indication of any previous modifications; and</li> <li id=\"s3a1C\">indicate the Licensed Material is licensed under this Public License, and include the text of, or the URI or hyperlink to, this Public License.</li> </ol> </li> <li id=\"s3a2\">You may satisfy the conditions in Section <a href=\"#s3a1\">3(a)(1)</a> in any reasonable manner based on the medium, means, and context in which You Share the Licensed Material. For example, it may be reasonable to satisfy the conditions by providing a URI or hyperlink to a resource that includes the required information.</li> <li id=\"s3a3\">If requested by the Licensor, You must remove any of the information required by Section <a href=\"#s3a1A\">3(a)(1)(A)</a> to the extent reasonably practicable.</li> </ol> </li> <li id=\"s3b\"><strong>ShareAlike</strong>. <p>In addition to the conditions in Section <a href=\"#s3a\">3(a)</a>, if You Share Adapted Material You produce, the following conditions also apply.</p> <ol> <li id=\"s3b1\">The Adapter’s License You apply must be a Creative Commons license with the same License Elements, this version or later, or a BY-SA Compatible License.</li> <li id=\"s3b2\">You must include the text of, or the URI or hyperlink to, the Adapter\'s License You apply. You may satisfy this condition in any reasonable manner based on the medium, means, and context in which You Share Adapted Material.</li> <li id=\"s3b3\">You may not offer or impose any additional or different terms or conditions on, or apply any Effective Technological Measures to, Adapted Material that restrict exercise of the rights granted under the Adapter\'s License You apply.</li> </ol> </li> </ol> <p id=\"s4\"><strong>Section 4 – Sui Generis Database Rights.</strong></p> <p>Where the Licensed Rights include Sui Generis Database Rights that apply to Your use of the Licensed Material:</p> <ol type=\"a\"> <li id=\"s4a\">for the avoidance of doubt, Section <a href=\"#s2a1\">2(a)(1)</a> grants You the right to extract, reuse, reproduce, and Share all or a substantial portion of the contents of the database;</li> <li id=\"s4b\">if You include all or a substantial portion of the database contents in a database in which You have Sui Generis Database Rights, then the database in which You have Sui Generis Database Rights (but not its individual contents) is Adapted Material, including for purposes of Section <a href=\"#s3b\">3(b)</a>; and</li> <li id=\"s4c\">You must comply with the conditions in Section <a href=\"#s3a\">3(a)</a> if You Share all or a substantial portion of the contents of the database.</li> </ol> For the avoidance of doubt, this Section <a href=\"#s4\">4</a> supplements and does not replace Your obligations under this Public License where the Licensed Rights include other Copyright and Similar Rights. <p id=\"s5\"><strong>Section 5 – Disclaimer of Warranties and Limitation of Liability.</strong></p> <ol style=\"font-weight: bold;\" type=\"a\"> <li id=\"s5a\"><strong>Unless otherwise separately undertaken by the Licensor, to the extent possible, the Licensor offers the Licensed Material as-is and as-available, and makes no representations or warranties of any kind concerning the Licensed Material, whether express, implied, statutory, or other. This includes, without limitation, warranties of title, merchantability, fitness for a particular purpose, non-infringement, absence of latent or other defects, accuracy, or the presence or absence of errors, whether or not known or discoverable. Where disclaimers of warranties are not allowed in full or in part, this disclaimer may not apply to You.</strong></li> <li id=\"s5b\"><strong>To the extent possible, in no event will the Licensor be liable to You on any legal theory (including, without limitation, negligence) or otherwise for any direct, special, indirect, incidental, consequential, punitive, exemplary, or other losses, costs, expenses, or damages arising out of this Public License or use of the Licensed Material, even if the Licensor has been advised of the possibility of such losses, costs, expenses, or damages. Where a limitation of liability is not allowed in full or in part, this limitation may not apply to You.</strong></li> </ol> <ol start=\"3\" type=\"a\"> <li id=\"s5c\">The disclaimer of warranties and limitation of liability provided above shall be interpreted in a manner that, to the extent possible, most closely approximates an absolute disclaimer and waiver of all liability.</li> </ol> <p id=\"s6\"><strong>Section 6 – Term and Termination.</strong></p> <ol type=\"a\"> <li id=\"s6a\">This Public License applies for the term of the Copyright and Similar Rights licensed here. However, if You fail to comply with this Public License, then Your rights under this Public License terminate automatically.</li> <li id=\"s6b\"> <p>Where Your right to use the Licensed Material has terminated under Section <a href=\"#s6a\">6(a)</a>, it reinstates:</p> <ol> <li id=\"s6b1\">automatically as of the date the violation is cured, provided it is cured within 30 days of Your discovery of the violation; or</li> <li id=\"s6b2\">upon express reinstatement by the Licensor.</li> </ol> For the avoidance of doubt, this Section <a href=\"#s6b\">6(b)</a> does not affect any right the Licensor may have to seek remedies for Your violations of this Public License.</li> <li id=\"s6c\">For the avoidance of doubt, the Licensor may also offer the Licensed Material under separate terms or conditions or stop distributing the Licensed Material at any time; however, doing so will not terminate this Public License.</li> <li id=\"s6d\">Sections <a href=\"#s1\">1</a>, <a href=\"#s5\">5</a>, <a href=\"#s6\">6</a>, <a href=\"#s7\">7</a>, and <a href=\"#s8\">8</a> survive termination of this Public License.</li> </ol> <p id=\"s7\"><strong>Section 7 – Other Terms and Conditions.</strong></p> <ol type=\"a\"> <li id=\"s7a\">The Licensor shall not be bound by any additional or different terms or conditions communicated by You unless expressly agreed.</li> <li id=\"s7b\">Any arrangements, understandings, or agreements regarding the Licensed Material not stated herein are separate from and independent of the terms and conditions of this Public License.</li> </ol> <p id=\"s8\"><strong>Section 8 – Interpretation.</strong></p> <ol type=\"a\"> <li id=\"s8a\">For the avoidance of doubt, this Public License does not, and shall not be interpreted to, reduce, limit, restrict, or impose conditions on any use of the Licensed Material that could lawfully be made without permission under this Public License.</li> <li id=\"s8b\">To the extent possible, if any provision of this Public License is deemed unenforceable, it shall be automatically reformed to the minimum extent necessary to make it enforceable. If the provision cannot be reformed, it shall be severed from this Public License without affecting the enforceability of the remaining terms and conditions.</li> <li id=\"s8c\">No term or condition of this Public License will be waived and no failure to comply consented to unless expressly agreed to by the Licensor.</li> <li id=\"s8d\">Nothing in this Public License constitutes or may be interpreted as a limitation upon, or waiver of, any privileges and immunities that apply to the Licensor or You, including from the legal processes of any jurisdiction or authority.</li> </ol>', '2022-09-26 09:53:37', '2022-09-26 09:53:37');
INSERT INTO `licensedata` VALUES (3, 3, 1, '<h3>Creative Commons Attribution-NoDerivatives 4.0 International Public License</h3> <p>By exercising the Licensed Rights (defined below), You accept and agree to be bound by the terms and conditions of this Creative Commons Attribution-NoDerivatives 4.0 International Public License (\"Public License\"). To the extent this Public License may be interpreted as a contract, You are granted the Licensed Rights in consideration of Your acceptance of these terms and conditions, and the Licensor grants You such rights in consideration of benefits the Licensor receives from making the Licensed Material available under these terms and conditions.</p> <p id=\"s1\"><strong>Section 1 – Definitions.</strong></p> <ol type=\"a\"> <li id=\"s1a\"><strong>Adapted Material</strong> means material subject to Copyright and Similar Rights that is derived from or based upon the Licensed Material and in which the Licensed Material is translated, altered, arranged, transformed, or otherwise modified in a manner requiring permission under the Copyright and Similar Rights held by the Licensor. For purposes of this Public License, where the Licensed Material is a musical work, performance, or sound recording, Adapted Material is always produced where the Licensed Material is synched in timed relation with a moving image.</li> <li id=\"s1b\"><strong>Copyright and Similar Rights</strong> means copyright and/or similar rights closely related to copyright including, without limitation, performance, broadcast, sound recording, and Sui Generis Database Rights, without regard to how the rights are labeled or categorized. For purposes of this Public License, the rights specified in Section <a href=\"#s2b\">2(b)(1)-(2)</a> are not Copyright and Similar Rights.</li> <li id=\"s1c\"><strong>Effective Technological Measures</strong> means those measures that, in the absence of proper authority, may not be circumvented under laws fulfilling obligations under Article 11 of the WIPO Copyright Treaty adopted on December 20, 1996, and/or similar international agreements.</li> <li id=\"s1d\"><strong>Exceptions and Limitations</strong> means fair use, fair dealing, and/or any other exception or limitation to Copyright and Similar Rights that applies to Your use of the Licensed Material.</li> <li id=\"s1e\"><strong>Licensed Material</strong> means the artistic or literary work, database, or other material to which the Licensor applied this Public License.</li> <li id=\"s1f\"><strong>Licensed Rights</strong> means the rights granted to You subject to the terms and conditions of this Public License, which are limited to all Copyright and Similar Rights that apply to Your use of the Licensed Material and that the Licensor has authority to license.</li> <li id=\"s1g\"><strong>Licensor</strong> means the individual(s) or entity(ies) granting rights under this Public License.</li> <li id=\"s1h\"><strong>Share</strong> means to provide material to the public by any means or process that requires permission under the Licensed Rights, such as reproduction, public display, public performance, distribution, dissemination, communication, or importation, and to make material available to the public including in ways that members of the public may access the material from a place and at a time individually chosen by them.</li> <li id=\"s1i\"><strong>Sui Generis Database Rights</strong> means rights other than copyright resulting from Directive 96/9/EC of the European Parliament and of the Council of 11 March 1996 on the legal protection of databases, as amended and/or succeeded, as well as other essentially equivalent rights anywhere in the world.</li> <li id=\"s1j\"><strong>You</strong> means the individual or entity exercising the Licensed Rights under this Public License. <strong>Your</strong> has a corresponding meaning.</li> </ol> <p id=\"s2\"><strong>Section 2 – Scope.</strong></p> <ol type=\"a\"> <li id=\"s2a\"><strong>License grant</strong>. <ol> <li id=\"s2a1\">Subject to the terms and conditions of this Public License, the Licensor hereby grants You a worldwide, royalty-free, non-sublicensable, non-exclusive, irrevocable license to exercise the Licensed Rights in the Licensed Material to: <ol type=\"A\"> <li id=\"s2a1A\">reproduce and Share the Licensed Material, in whole or in part; and</li> <li id=\"s2a1B\">produce and reproduce, but not Share, Adapted Material.</li> </ol> </li><li id=\"s2a2\"><span style=\"text-decoration: underline;\">Exceptions and Limitations</span>. For the avoidance of doubt, where Exceptions and Limitations apply to Your use, this Public License does not apply, and You do not need to comply with its terms and conditions.</li> <li id=\"s2a3\"><span style=\"text-decoration: underline;\">Term</span>. The term of this Public License is specified in Section <a href=\"#s6a\">6(a)</a>.</li> <li id=\"s2a4\"><span style=\"text-decoration: underline;\">Media and formats; technical modifications allowed</span>. The Licensor authorizes You to exercise the Licensed Rights in all media and formats whether now known or hereafter created, and to make technical modifications necessary to do so. The Licensor waives and/or agrees not to assert any right or authority to forbid You from making technical modifications necessary to exercise the Licensed Rights, including technical modifications necessary to circumvent Effective Technological Measures. For purposes of this Public License, simply making modifications authorized by this Section <a href=\"#s2a4\">2(a)(4)</a> never produces Adapted Material.</li> <li id=\"s2a5\"><span style=\"text-decoration: underline;\">Downstream recipients</span>. <div class=\"para\"> <ol type=\"A\"> <li id=\"s2a5A\"><span style=\"text-decoration: underline;\">Offer from the Licensor – Licensed Material</span>. Every recipient of the Licensed Material automatically receives an offer from the Licensor to exercise the Licensed Rights under the terms and conditions of this Public License.</li> <li id=\"s2a5B\"><span style=\"text-decoration: underline;\">No downstream restrictions</span>. You may not offer or impose any additional or different terms or conditions on, or apply any Effective Technological Measures to, the Licensed Material if doing so restricts exercise of the Licensed Rights by any recipient of the Licensed Material.</li> </ol> </div> </li><li id=\"s2a6\"><span style=\"text-decoration: underline;\">No endorsement</span>. Nothing in this Public License constitutes or may be construed as permission to assert or imply that You are, or that Your use of the Licensed Material is, connected with, or sponsored, endorsed, or granted official status by, the Licensor or others designated to receive attribution as provided in Section <a href=\"#s3a1Ai\">3(a)(1)(A)(i)</a>.</li> </ol> </li><li id=\"s2b\"><p><strong>Other rights</strong>.</p> <ol> <li id=\"s2b1\">Moral rights, such as the right of integrity, are not licensed under this Public License, nor are publicity, privacy, and/or other similar personality rights; however, to the extent possible, the Licensor waives and/or agrees not to assert any such rights held by the Licensor to the limited extent necessary to allow You to exercise the Licensed Rights, but not otherwise.</li> <li id=\"s2b2\">Patent and trademark rights are not licensed under this Public License.</li> <li id=\"s2b3\">To the extent possible, the Licensor waives any right to collect royalties from You for the exercise of the Licensed Rights, whether directly or through a collecting society under any voluntary or waivable statutory or compulsory licensing scheme. In all other cases the Licensor expressly reserves any right to collect such royalties.</li> </ol> </li> </ol> <p id=\"s3\"><strong>Section 3 – License Conditions.</strong></p> <p>Your exercise of the Licensed Rights is expressly made subject to the following conditions.</p> <ol type=\"a\"> <li id=\"s3a\"><p><strong>Attribution</strong>.</p> <ol> <li id=\"s3a1\"><p>If You Share the Licensed Material, You must:</p> <ol type=\"A\"> <li id=\"s3a1A\">retain the following if it is supplied by the Licensor with the Licensed Material: <ol type=\"i\"> <li id=\"s3a1Ai\">identification of the creator(s) of the Licensed Material and any others designated to receive attribution, in any reasonable manner requested by the Licensor (including by pseudonym if designated);</li> <li id=\"s3a1Aii\">a copyright notice;</li> <li id=\"s3a1Aiii\">a notice that refers to this Public License; </li> <li id=\"s3a1Aiv\">a notice that refers to the disclaimer of warranties;</li> <li id=\"s3a1Av\">a URI or hyperlink to the Licensed Material to the extent reasonably practicable;</li> </ol> </li><li id=\"s3a1B\">indicate if You modified the Licensed Material and retain an indication of any previous modifications; and</li> <li id=\"s3a1C\">indicate the Licensed Material is licensed under this Public License, and include the text of, or the URI or hyperlink to, this Public License.</li> </ol> For the avoidance of doubt, You do not have permission under this Public License to Share Adapted Material. </li> <li id=\"s3a2\">You may satisfy the conditions in Section <a href=\"#s3a1\">3(a)(1)</a> in any reasonable manner based on the medium, means, and context in which You Share the Licensed Material. For example, it may be reasonable to satisfy the conditions by providing a URI or hyperlink to a resource that includes the required information.</li> <li id=\"s3a3\">If requested by the Licensor, You must remove any of the information required by Section <a href=\"#s3a1A\">3(a)(1)(A)</a> to the extent reasonably practicable.</li> </ol> </li> </ol> <p id=\"s4\"><strong>Section 4 – Sui Generis Database Rights.</strong></p> <p>Where the Licensed Rights include Sui Generis Database Rights that apply to Your use of the Licensed Material:</p> <ol type=\"a\"> <li id=\"s4a\">for the avoidance of doubt, Section <a href=\"#s2a1\">2(a)(1)</a> grants You the right to extract, reuse, reproduce, and Share all or a substantial portion of the contents of the database, provided You do not Share Adapted Material;</li> <li id=\"s4b\">if You include all or a substantial portion of the database contents in a database in which You have Sui Generis Database Rights, then the database in which You have Sui Generis Database Rights (but not its individual contents) is Adapted Material; and</li> <li id=\"s4c\">You must comply with the conditions in Section <a href=\"#s3a\">3(a)</a> if You Share all or a substantial portion of the contents of the database.</li> </ol> For the avoidance of doubt, this Section <a href=\"#s4\">4</a> supplements and does not replace Your obligations under this Public License where the Licensed Rights include other Copyright and Similar Rights. <p id=\"s5\"><strong>Section 5 – Disclaimer of Warranties and Limitation of Liability.</strong></p> <ol style=\"font-weight: bold;\" type=\"a\"> <li id=\"s5a\"><strong>Unless otherwise separately undertaken by the Licensor, to the extent possible, the Licensor offers the Licensed Material as-is and as-available, and makes no representations or warranties of any kind concerning the Licensed Material, whether express, implied, statutory, or other. This includes, without limitation, warranties of title, merchantability, fitness for a particular purpose, non-infringement, absence of latent or other defects, accuracy, or the presence or absence of errors, whether or not known or discoverable. Where disclaimers of warranties are not allowed in full or in part, this disclaimer may not apply to You.</strong></li> <li id=\"s5b\"><strong>To the extent possible, in no event will the Licensor be liable to You on any legal theory (including, without limitation, negligence) or otherwise for any direct, special, indirect, incidental, consequential, punitive, exemplary, or other losses, costs, expenses, or damages arising out of this Public License or use of the Licensed Material, even if the Licensor has been advised of the possibility of such losses, costs, expenses, or damages. Where a limitation of liability is not allowed in full or in part, this limitation may not apply to You.</strong></li> </ol> <ol start=\"3\" type=\"a\"> <li id=\"s5c\">The disclaimer of warranties and limitation of liability provided above shall be interpreted in a manner that, to the extent possible, most closely approximates an absolute disclaimer and waiver of all liability.</li> </ol> <p id=\"s6\"><strong>Section 6 – Term and Termination.</strong></p> <ol type=\"a\"> <li id=\"s6a\">This Public License applies for the term of the Copyright and Similar Rights licensed here. However, if You fail to comply with this Public License, then Your rights under this Public License terminate automatically.</li> <li id=\"s6b\"> <p>Where Your right to use the Licensed Material has terminated under Section <a href=\"#s6a\">6(a)</a>, it reinstates:</p> <ol> <li id=\"s6b1\">automatically as of the date the violation is cured, provided it is cured within 30 days of Your discovery of the violation; or</li> <li id=\"s6b2\">upon express reinstatement by the Licensor.</li> </ol> For the avoidance of doubt, this Section <a href=\"#s6b\">6(b)</a> does not affect any right the Licensor may have to seek remedies for Your violations of this Public License.</li> <li id=\"s6c\">For the avoidance of doubt, the Licensor may also offer the Licensed Material under separate terms or conditions or stop distributing the Licensed Material at any time; however, doing so will not terminate this Public License.</li> <li id=\"s6d\">Sections <a href=\"#s1\">1</a>, <a href=\"#s5\">5</a>, <a href=\"#s6\">6</a>, <a href=\"#s7\">7</a>, and <a href=\"#s8\">8</a> survive termination of this Public License.</li> </ol> <p id=\"s7\"><strong>Section 7 – Other Terms and Conditions.</strong></p> <ol type=\"a\"> <li id=\"s7a\">The Licensor shall not be bound by any additional or different terms or conditions communicated by You unless expressly agreed.</li> <li id=\"s7b\">Any arrangements, understandings, or agreements regarding the Licensed Material not stated herein are separate from and independent of the terms and conditions of this Public License.</li> </ol> <p id=\"s8\"><strong>Section 8 – Interpretation.</strong></p> <ol type=\"a\"> <li id=\"s8a\">For the avoidance of doubt, this Public License does not, and shall not be interpreted to, reduce, limit, restrict, or impose conditions on any use of the Licensed Material that could lawfully be made without permission under this Public License.</li> <li id=\"s8b\">To the extent possible, if any provision of this Public License is deemed unenforceable, it shall be automatically reformed to the minimum extent necessary to make it enforceable. If the provision cannot be reformed, it shall be severed from this Public License without affecting the enforceability of the remaining terms and conditions.</li> <li id=\"s8c\">No term or condition of this Public License will be waived and no failure to comply consented to unless expressly agreed to by the Licensor.</li> <li id=\"s8d\">Nothing in this Public License constitutes or may be interpreted as a limitation upon, or waiver of, any privileges and immunities that apply to the Licensor or You, including from the legal processes of any jurisdiction or authority.</li> </ol>', '2022-09-26 09:53:37', '2022-09-26 09:53:37');
INSERT INTO `licensedata` VALUES (4, 4, 1, '<h3>Creative Commons Attribution-NonCommercial 4.0 International Public License</h3> <p>By exercising the Licensed Rights (defined below), You accept and agree to be bound by the terms and conditions of this Creative Commons Attribution-NonCommercial 4.0 International Public License (\"Public License\"). To the extent this Public License may be interpreted as a contract, You are granted the Licensed Rights in consideration of Your acceptance of these terms and conditions, and the Licensor grants You such rights in consideration of benefits the Licensor receives from making the Licensed Material available under these terms and conditions.</p> <p id=\"s1\"><strong>Section 1 – Definitions.</strong></p> <ol type=\"a\"> <li id=\"s1a\"><strong>Adapted Material</strong> means material subject to Copyright and Similar Rights that is derived from or based upon the Licensed Material and in which the Licensed Material is translated, altered, arranged, transformed, or otherwise modified in a manner requiring permission under the Copyright and Similar Rights held by the Licensor. For purposes of this Public License, where the Licensed Material is a musical work, performance, or sound recording, Adapted Material is always produced where the Licensed Material is synched in timed relation with a moving image.</li> <li id=\"s1b\"><strong>Adapter\'s License</strong> means the license You apply to Your Copyright and Similar Rights in Your contributions to Adapted Material in accordance with the terms and conditions of this Public License.</li> <li id=\"s1c\"><strong>Copyright and Similar Rights</strong> means copyright and/or similar rights closely related to copyright including, without limitation, performance, broadcast, sound recording, and Sui Generis Database Rights, without regard to how the rights are labeled or categorized. For purposes of this Public License, the rights specified in Section <a href=\"#s2b\">2(b)(1)-(2)</a> are not Copyright and Similar Rights.</li> <li id=\"s1d\"><strong>Effective Technological Measures</strong> means those measures that, in the absence of proper authority, may not be circumvented under laws fulfilling obligations under Article 11 of the WIPO Copyright Treaty adopted on December 20, 1996, and/or similar international agreements.</li> <li id=\"s1e\"><strong>Exceptions and Limitations</strong> means fair use, fair dealing, and/or any other exception or limitation to Copyright and Similar Rights that applies to Your use of the Licensed Material.</li> <li id=\"s1f\"><strong>Licensed Material</strong> means the artistic or literary work, database, or other material to which the Licensor applied this Public License.</li> <li id=\"s1g\"><strong>Licensed Rights</strong> means the rights granted to You subject to the terms and conditions of this Public License, which are limited to all Copyright and Similar Rights that apply to Your use of the Licensed Material and that the Licensor has authority to license.</li> <li id=\"s1h\"><strong>Licensor</strong> means the individual(s) or entity(ies) granting rights under this Public License.</li> <li id=\"s1i\"><strong>NonCommercial</strong> means not primarily intended for or directed towards commercial advantage or monetary compensation. For purposes of this Public License, the exchange of the Licensed Material for other material subject to Copyright and Similar Rights by digital file-sharing or similar means is NonCommercial provided there is no payment of monetary compensation in connection with the exchange.</li> <li id=\"s1j\"><strong>Share</strong> means to provide material to the public by any means or process that requires permission under the Licensed Rights, such as reproduction, public display, public performance, distribution, dissemination, communication, or importation, and to make material available to the public including in ways that members of the public may access the material from a place and at a time individually chosen by them.</li> <li id=\"s1k\"><strong>Sui Generis Database Rights</strong> means rights other than copyright resulting from Directive 96/9/EC of the European Parliament and of the Council of 11 March 1996 on the legal protection of databases, as amended and/or succeeded, as well as other essentially equivalent rights anywhere in the world.</li> <li id=\"s1l\"><strong>You</strong> means the individual or entity exercising the Licensed Rights under this Public License. <strong>Your</strong> has a corresponding meaning.</li> </ol> <p id=\"s2\"><strong>Section 2 – Scope.</strong></p> <ol type=\"a\"> <li id=\"s2a\"><strong>License grant</strong>. <ol> <li id=\"s2a1\">Subject to the terms and conditions of this Public License, the Licensor hereby grants You a worldwide, royalty-free, non-sublicensable, non-exclusive, irrevocable license to exercise the Licensed Rights in the Licensed Material to: <ol type=\"A\"> <li id=\"s2a1A\">reproduce and Share the Licensed Material, in whole or in part, for NonCommercial purposes only; and</li> <li id=\"s2a1B\">produce, reproduce, and Share Adapted Material for NonCommercial purposes only.</li> </ol> </li><li id=\"s2a2\"><span style=\"text-decoration: underline;\">Exceptions and Limitations</span>. For the avoidance of doubt, where Exceptions and Limitations apply to Your use, this Public License does not apply, and You do not need to comply with its terms and conditions.</li> <li id=\"s2a3\"><span style=\"text-decoration: underline;\">Term</span>. The term of this Public License is specified in Section <a href=\"#s6a\">6(a)</a>.</li> <li id=\"s2a4\"><span style=\"text-decoration: underline;\">Media and formats; technical modifications allowed</span>. The Licensor authorizes You to exercise the Licensed Rights in all media and formats whether now known or hereafter created, and to make technical modifications necessary to do so. The Licensor waives and/or agrees not to assert any right or authority to forbid You from making technical modifications necessary to exercise the Licensed Rights, including technical modifications necessary to circumvent Effective Technological Measures. For purposes of this Public License, simply making modifications authorized by this Section <a href=\"#s2a4\">2(a)(4)</a> never produces Adapted Material.</li> <li id=\"s2a5\"><span style=\"text-decoration: underline;\">Downstream recipients</span>. <div class=\"para\"> <ol type=\"A\"> <li id=\"s2a5A\"><span style=\"text-decoration: underline;\">Offer from the Licensor – Licensed Material</span>. Every recipient of the Licensed Material automatically receives an offer from the Licensor to exercise the Licensed Rights under the terms and conditions of this Public License.</li> <li id=\"s2a5B\"><span style=\"text-decoration: underline;\">No downstream restrictions</span>. You may not offer or impose any additional or different terms or conditions on, or apply any Effective Technological Measures to, the Licensed Material if doing so restricts exercise of the Licensed Rights by any recipient of the Licensed Material.</li> </ol> </div> </li><li id=\"s2a6\"><span style=\"text-decoration: underline;\">No endorsement</span>. Nothing in this Public License constitutes or may be construed as permission to assert or imply that You are, or that Your use of the Licensed Material is, connected with, or sponsored, endorsed, or granted official status by, the Licensor or others designated to receive attribution as provided in Section <a href=\"#s3a1Ai\">3(a)(1)(A)(i)</a>.</li> </ol> </li><li id=\"s2b\"><p><strong>Other rights</strong>.</p> <ol> <li id=\"s2b1\">Moral rights, such as the right of integrity, are not licensed under this Public License, nor are publicity, privacy, and/or other similar personality rights; however, to the extent possible, the Licensor waives and/or agrees not to assert any such rights held by the Licensor to the limited extent necessary to allow You to exercise the Licensed Rights, but not otherwise.</li> <li id=\"s2b2\">Patent and trademark rights are not licensed under this Public License.</li> <li id=\"s2b3\">To the extent possible, the Licensor waives any right to collect royalties from You for the exercise of the Licensed Rights, whether directly or through a collecting society under any voluntary or waivable statutory or compulsory licensing scheme. In all other cases the Licensor expressly reserves any right to collect such royalties, including when the Licensed Material is used other than for NonCommercial purposes.</li> </ol> </li> </ol> <p id=\"s3\"><strong>Section 3 – License Conditions.</strong></p> <p>Your exercise of the Licensed Rights is expressly made subject to the following conditions.</p> <ol type=\"a\"> <li id=\"s3a\"><p><strong>Attribution</strong>.</p> <ol> <li id=\"s3a1\"><p>If You Share the Licensed Material (including in modified form), You must:</p> <ol type=\"A\"> <li id=\"s3a1A\">retain the following if it is supplied by the Licensor with the Licensed Material: <ol type=\"i\"> <li id=\"s3a1Ai\">identification of the creator(s) of the Licensed Material and any others designated to receive attribution, in any reasonable manner requested by the Licensor (including by pseudonym if designated);</li> <li id=\"s3a1Aii\">a copyright notice;</li> <li id=\"s3a1Aiii\">a notice that refers to this Public License; </li> <li id=\"s3a1Aiv\">a notice that refers to the disclaimer of warranties;</li> <li id=\"s3a1Av\">a URI or hyperlink to the Licensed Material to the extent reasonably practicable;</li> </ol> </li><li id=\"s3a1B\">indicate if You modified the Licensed Material and retain an indication of any previous modifications; and</li> <li id=\"s3a1C\">indicate the Licensed Material is licensed under this Public License, and include the text of, or the URI or hyperlink to, this Public License.</li> </ol> </li> <li id=\"s3a2\">You may satisfy the conditions in Section <a href=\"#s3a1\">3(a)(1)</a> in any reasonable manner based on the medium, means, and context in which You Share the Licensed Material. For example, it may be reasonable to satisfy the conditions by providing a URI or hyperlink to a resource that includes the required information.</li> <li id=\"s3a3\">If requested by the Licensor, You must remove any of the information required by Section <a href=\"#s3a1A\">3(a)(1)(A)</a> to the extent reasonably practicable.</li> <li id=\"s3a4\">If You Share Adapted Material You produce, the Adapter\'s License You apply must not prevent recipients of the Adapted Material from complying with this Public License.</li> </ol> </li> </ol> <p id=\"s4\"><strong>Section 4 – Sui Generis Database Rights.</strong></p> <p>Where the Licensed Rights include Sui Generis Database Rights that apply to Your use of the Licensed Material:</p> <ol type=\"a\"> <li id=\"s4a\">for the avoidance of doubt, Section <a href=\"#s2a1\">2(a)(1)</a> grants You the right to extract, reuse, reproduce, and Share all or a substantial portion of the contents of the database for NonCommercial purposes only;</li> <li id=\"s4b\">if You include all or a substantial portion of the database contents in a database in which You have Sui Generis Database Rights, then the database in which You have Sui Generis Database Rights (but not its individual contents) is Adapted Material; and</li> <li id=\"s4c\">You must comply with the conditions in Section <a href=\"#s3a\">3(a)</a> if You Share all or a substantial portion of the contents of the database.</li> </ol> For the avoidance of doubt, this Section <a href=\"#s4\">4</a> supplements and does not replace Your obligations under this Public License where the Licensed Rights include other Copyright and Similar Rights. <p id=\"s5\"><strong>Section 5 – Disclaimer of Warranties and Limitation of Liability.</strong></p> <ol style=\"font-weight: bold;\" type=\"a\"> <li id=\"s5a\"><strong>Unless otherwise separately undertaken by the Licensor, to the extent possible, the Licensor offers the Licensed Material as-is and as-available, and makes no representations or warranties of any kind concerning the Licensed Material, whether express, implied, statutory, or other. This includes, without limitation, warranties of title, merchantability, fitness for a particular purpose, non-infringement, absence of latent or other defects, accuracy, or the presence or absence of errors, whether or not known or discoverable. Where disclaimers of warranties are not allowed in full or in part, this disclaimer may not apply to You.</strong></li> <li id=\"s5b\"><strong>To the extent possible, in no event will the Licensor be liable to You on any legal theory (including, without limitation, negligence) or otherwise for any direct, special, indirect, incidental, consequential, punitive, exemplary, or other losses, costs, expenses, or damages arising out of this Public License or use of the Licensed Material, even if the Licensor has been advised of the possibility of such losses, costs, expenses, or damages. Where a limitation of liability is not allowed in full or in part, this limitation may not apply to You.</strong></li> </ol> <ol start=\"3\" type=\"a\"> <li id=\"s5c\">The disclaimer of warranties and limitation of liability provided above shall be interpreted in a manner that, to the extent possible, most closely approximates an absolute disclaimer and waiver of all liability.</li> </ol> <p id=\"s6\"><strong>Section 6 – Term and Termination.</strong></p> <ol type=\"a\"> <li id=\"s6a\">This Public License applies for the term of the Copyright and Similar Rights licensed here. However, if You fail to comply with this Public License, then Your rights under this Public License terminate automatically.</li> <li id=\"s6b\"> <p>Where Your right to use the Licensed Material has terminated under Section <a href=\"#s6a\">6(a)</a>, it reinstates:</p> <ol> <li id=\"s6b1\">automatically as of the date the violation is cured, provided it is cured within 30 days of Your discovery of the violation; or</li> <li id=\"s6b2\">upon express reinstatement by the Licensor.</li> </ol> For the avoidance of doubt, this Section <a href=\"#s6b\">6(b)</a> does not affect any right the Licensor may have to seek remedies for Your violations of this Public License.</li> <li id=\"s6c\">For the avoidance of doubt, the Licensor may also offer the Licensed Material under separate terms or conditions or stop distributing the Licensed Material at any time; however, doing so will not terminate this Public License.</li> <li id=\"s6d\">Sections <a href=\"#s1\">1</a>, <a href=\"#s5\">5</a>, <a href=\"#s6\">6</a>, <a href=\"#s7\">7</a>, and <a href=\"#s8\">8</a> survive termination of this Public License.</li> </ol> <p id=\"s7\"><strong>Section 7 – Other Terms and Conditions.</strong></p> <ol type=\"a\"> <li id=\"s7a\">The Licensor shall not be bound by any additional or different terms or conditions communicated by You unless expressly agreed.</li> <li id=\"s7b\">Any arrangements, understandings, or agreements regarding the Licensed Material not stated herein are separate from and independent of the terms and conditions of this Public License.</li> </ol> <p id=\"s8\"><strong>Section 8 – Interpretation.</strong></p> <ol type=\"a\"> <li id=\"s8a\">For the avoidance of doubt, this Public License does not, and shall not be interpreted to, reduce, limit, restrict, or impose conditions on any use of the Licensed Material that could lawfully be made without permission under this Public License.</li> <li id=\"s8b\">To the extent possible, if any provision of this Public License is deemed unenforceable, it shall be automatically reformed to the minimum extent necessary to make it enforceable. If the provision cannot be reformed, it shall be severed from this Public License without affecting the enforceability of the remaining terms and conditions.</li> <li id=\"s8c\">No term or condition of this Public License will be waived and no failure to comply consented to unless expressly agreed to by the Licensor.</li> <li id=\"s8d\">Nothing in this Public License constitutes or may be interpreted as a limitation upon, or waiver of, any privileges and immunities that apply to the Licensor or You, including from the legal processes of any jurisdiction or authority.</li> </ol>', '2022-09-26 09:53:37', '2022-09-26 09:53:37');
INSERT INTO `licensedata` VALUES (5, 5, 1, '<h3>Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International Public License</h3> <p>By exercising the Licensed Rights (defined below), You accept and agree to be bound by the terms and conditions of this Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International Public License (\"Public License\"). To the extent this Public License may be interpreted as a contract, You are granted the Licensed Rights in consideration of Your acceptance of these terms and conditions, and the Licensor grants You such rights in consideration of benefits the Licensor receives from making the Licensed Material available under these terms and conditions.</p> <p id=\"s1\"><strong>Section 1 – Definitions.</strong></p> <ol type=\"a\"> <li id=\"s1a\"><strong>Adapted Material</strong> means material subject to Copyright and Similar Rights that is derived from or based upon the Licensed Material and in which the Licensed Material is translated, altered, arranged, transformed, or otherwise modified in a manner requiring permission under the Copyright and Similar Rights held by the Licensor. For purposes of this Public License, where the Licensed Material is a musical work, performance, or sound recording, Adapted Material is always produced where the Licensed Material is synched in timed relation with a moving image.</li> <li id=\"s1b\"><strong>Adapter\'s License</strong> means the license You apply to Your Copyright and Similar Rights in Your contributions to Adapted Material in accordance with the terms and conditions of this Public License.</li> <li id=\"s1c\"><strong>BY-NC-SA Compatible License</strong> means a license listed at <a href=\"//creativecommons.org/compatiblelicenses\"> creativecommons.org/compatiblelicenses</a>, approved by Creative Commons as essentially the equivalent of this Public License.</li> <li id=\"s1d\"><strong>Copyright and Similar Rights</strong> means copyright and/or similar rights closely related to copyright including, without limitation, performance, broadcast, sound recording, and Sui Generis Database Rights, without regard to how the rights are labeled or categorized. For purposes of this Public License, the rights specified in Section <a href=\"#s2b\">2(b)(1)-(2)</a> are not Copyright and Similar Rights.</li> <li id=\"s1e\"><strong>Effective Technological Measures</strong> means those measures that, in the absence of proper authority, may not be circumvented under laws fulfilling obligations under Article 11 of the WIPO Copyright Treaty adopted on December 20, 1996, and/or similar international agreements.</li> <li id=\"s1f\"><strong>Exceptions and Limitations</strong> means fair use, fair dealing, and/or any other exception or limitation to Copyright and Similar Rights that applies to Your use of the Licensed Material.</li> <li id=\"s1g\"><strong>License Elements</strong> means the license attributes listed in the name of a Creative Commons Public License. The License Elements of this Public License are Attribution, NonCommercial, and ShareAlike.</li> <li id=\"s1h\"><strong>Licensed Material</strong> means the artistic or literary work, database, or other material to which the Licensor applied this Public License.</li> <li id=\"s1i\"><strong>Licensed Rights</strong> means the rights granted to You subject to the terms and conditions of this Public License, which are limited to all Copyright and Similar Rights that apply to Your use of the Licensed Material and that the Licensor has authority to license.</li> <li id=\"s1j\"><strong>Licensor</strong> means the individual(s) or entity(ies) granting rights under this Public License.</li> <li id=\"s1k\"><strong>NonCommercial</strong> means not primarily intended for or directed towards commercial advantage or monetary compensation. For purposes of this Public License, the exchange of the Licensed Material for other material subject to Copyright and Similar Rights by digital file-sharing or similar means is NonCommercial provided there is no payment of monetary compensation in connection with the exchange.</li> <li id=\"s1l\"><strong>Share</strong> means to provide material to the public by any means or process that requires permission under the Licensed Rights, such as reproduction, public display, public performance, distribution, dissemination, communication, or importation, and to make material available to the public including in ways that members of the public may access the material from a place and at a time individually chosen by them.</li> <li id=\"s1m\"><strong>Sui Generis Database Rights</strong> means rights other than copyright resulting from Directive 96/9/EC of the European Parliament and of the Council of 11 March 1996 on the legal protection of databases, as amended and/or succeeded, as well as other essentially equivalent rights anywhere in the world.</li> <li id=\"s1n\"><strong>You</strong> means the individual or entity exercising the Licensed Rights under this Public License. <strong>Your</strong> has a corresponding meaning.</li> </ol> <p id=\"s2\"><strong>Section 2 – Scope.</strong></p> <ol type=\"a\"> <li id=\"s2a\"><strong>License grant</strong>. <ol> <li id=\"s2a1\">Subject to the terms and conditions of this Public License, the Licensor hereby grants You a worldwide, royalty-free, non-sublicensable, non-exclusive, irrevocable license to exercise the Licensed Rights in the Licensed Material to: <ol type=\"A\"> <li id=\"s2a1A\">reproduce and Share the Licensed Material, in whole or in part, for NonCommercial purposes only; and</li> <li id=\"s2a1B\">produce, reproduce, and Share Adapted Material for NonCommercial purposes only.</li> </ol> </li><li id=\"s2a2\"><span style=\"text-decoration: underline;\">Exceptions and Limitations</span>. For the avoidance of doubt, where Exceptions and Limitations apply to Your use, this Public License does not apply, and You do not need to comply with its terms and conditions.</li> <li id=\"s2a3\"><span style=\"text-decoration: underline;\">Term</span>. The term of this Public License is specified in Section <a href=\"#s6a\">6(a)</a>.</li> <li id=\"s2a4\"><span style=\"text-decoration: underline;\">Media and formats; technical modifications allowed</span>. The Licensor authorizes You to exercise the Licensed Rights in all media and formats whether now known or hereafter created, and to make technical modifications necessary to do so. The Licensor waives and/or agrees not to assert any right or authority to forbid You from making technical modifications necessary to exercise the Licensed Rights, including technical modifications necessary to circumvent Effective Technological Measures. For purposes of this Public License, simply making modifications authorized by this Section <a href=\"#s2a4\">2(a)(4)</a> never produces Adapted Material.</li> <li id=\"s2a5\"><span style=\"text-decoration: underline;\">Downstream recipients</span>. <div class=\"para\"> <ol type=\"A\"> <li id=\"s2a5A\"><span style=\"text-decoration: underline;\">Offer from the Licensor – Licensed Material</span>. Every recipient of the Licensed Material automatically receives an offer from the Licensor to exercise the Licensed Rights under the terms and conditions of this Public License.</li> <li id=\"s2a5B\"><span style=\"text-decoration: underline;\">Additional offer from the Licensor – Adapted Material</span>. Every recipient of Adapted Material from You automatically receives an offer from the Licensor to exercise the Licensed Rights in the Adapted Material under the conditions of the Adapter’s License You apply.</li> <li id=\"s2a5C\"><span style=\"text-decoration: underline;\">No downstream restrictions</span>. You may not offer or impose any additional or different terms or conditions on, or apply any Effective Technological Measures to, the Licensed Material if doing so restricts exercise of the Licensed Rights by any recipient of the Licensed Material.</li> </ol> </div> </li><li id=\"s2a6\"><span style=\"text-decoration: underline;\">No endorsement</span>. Nothing in this Public License constitutes or may be construed as permission to assert or imply that You are, or that Your use of the Licensed Material is, connected with, or sponsored, endorsed, or granted official status by, the Licensor or others designated to receive attribution as provided in Section <a href=\"#s3a1Ai\">3(a)(1)(A)(i)</a>.</li> </ol> </li><li id=\"s2b\"><p><strong>Other rights</strong>.</p> <ol> <li id=\"s2b1\">Moral rights, such as the right of integrity, are not licensed under this Public License, nor are publicity, privacy, and/or other similar personality rights; however, to the extent possible, the Licensor waives and/or agrees not to assert any such rights held by the Licensor to the limited extent necessary to allow You to exercise the Licensed Rights, but not otherwise.</li> <li id=\"s2b2\">Patent and trademark rights are not licensed under this Public License.</li> <li id=\"s2b3\">To the extent possible, the Licensor waives any right to collect royalties from You for the exercise of the Licensed Rights, whether directly or through a collecting society under any voluntary or waivable statutory or compulsory licensing scheme. In all other cases the Licensor expressly reserves any right to collect such royalties, including when the Licensed Material is used other than for NonCommercial purposes.</li> </ol> </li> </ol> <p id=\"s3\"><strong>Section 3 – License Conditions.</strong></p> <p>Your exercise of the Licensed Rights is expressly made subject to the following conditions.</p> <ol type=\"a\"> <li id=\"s3a\"><p><strong>Attribution</strong>.</p> <ol> <li id=\"s3a1\"><p>If You Share the Licensed Material (including in modified form), You must:</p> <ol type=\"A\"> <li id=\"s3a1A\">retain the following if it is supplied by the Licensor with the Licensed Material: <ol type=\"i\"> <li id=\"s3a1Ai\">identification of the creator(s) of the Licensed Material and any others designated to receive attribution, in any reasonable manner requested by the Licensor (including by pseudonym if designated);</li> <li id=\"s3a1Aii\">a copyright notice;</li> <li id=\"s3a1Aiii\">a notice that refers to this Public License; </li> <li id=\"s3a1Aiv\">a notice that refers to the disclaimer of warranties;</li> <li id=\"s3a1Av\">a URI or hyperlink to the Licensed Material to the extent reasonably practicable;</li> </ol> </li><li id=\"s3a1B\">indicate if You modified the Licensed Material and retain an indication of any previous modifications; and</li> <li id=\"s3a1C\">indicate the Licensed Material is licensed under this Public License, and include the text of, or the URI or hyperlink to, this Public License.</li> </ol> </li> <li id=\"s3a2\">You may satisfy the conditions in Section <a href=\"#s3a1\">3(a)(1)</a> in any reasonable manner based on the medium, means, and context in which You Share the Licensed Material. For example, it may be reasonable to satisfy the conditions by providing a URI or hyperlink to a resource that includes the required information.</li> <li id=\"s3a3\">If requested by the Licensor, You must remove any of the information required by Section <a href=\"#s3a1A\">3(a)(1)(A)</a> to the extent reasonably practicable.</li> </ol> </li> <li id=\"s3b\"><strong>ShareAlike</strong>. <p>In addition to the conditions in Section <a href=\"#s3a\">3(a)</a>, if You Share Adapted Material You produce, the following conditions also apply.</p> <ol> <li id=\"s3b1\">The Adapter’s License You apply must be a Creative Commons license with the same License Elements, this version or later, or a BY-NC-SA Compatible License.</li> <li id=\"s3b2\">You must include the text of, or the URI or hyperlink to, the Adapter\'s License You apply. You may satisfy this condition in any reasonable manner based on the medium, means, and context in which You Share Adapted Material.</li> <li id=\"s3b3\">You may not offer or impose any additional or different terms or conditions on, or apply any Effective Technological Measures to, Adapted Material that restrict exercise of the rights granted under the Adapter\'s License You apply.</li> </ol> </li> </ol> <p id=\"s4\"><strong>Section 4 – Sui Generis Database Rights.</strong></p> <p>Where the Licensed Rights include Sui Generis Database Rights that apply to Your use of the Licensed Material:</p> <ol type=\"a\"> <li id=\"s4a\">for the avoidance of doubt, Section <a href=\"#s2a1\">2(a)(1)</a> grants You the right to extract, reuse, reproduce, and Share all or a substantial portion of the contents of the database for NonCommercial purposes only;</li> <li id=\"s4b\">if You include all or a substantial portion of the database contents in a database in which You have Sui Generis Database Rights, then the database in which You have Sui Generis Database Rights (but not its individual contents) is Adapted Material, including for purposes of Section <a href=\"#s3b\">3(b)</a>; and</li> <li id=\"s4c\">You must comply with the conditions in Section <a href=\"#s3a\">3(a)</a> if You Share all or a substantial portion of the contents of the database.</li> </ol> For the avoidance of doubt, this Section <a href=\"#s4\">4</a> supplements and does not replace Your obligations under this Public License where the Licensed Rights include other Copyright and Similar Rights. <p id=\"s5\"><strong>Section 5 – Disclaimer of Warranties and Limitation of Liability.</strong></p> <ol style=\"font-weight: bold;\" type=\"a\"> <li id=\"s5a\"><strong>Unless otherwise separately undertaken by the Licensor, to the extent possible, the Licensor offers the Licensed Material as-is and as-available, and makes no representations or warranties of any kind concerning the Licensed Material, whether express, implied, statutory, or other. This includes, without limitation, warranties of title, merchantability, fitness for a particular purpose, non-infringement, absence of latent or other defects, accuracy, or the presence or absence of errors, whether or not known or discoverable. Where disclaimers of warranties are not allowed in full or in part, this disclaimer may not apply to You.</strong></li> <li id=\"s5b\"><strong>To the extent possible, in no event will the Licensor be liable to You on any legal theory (including, without limitation, negligence) or otherwise for any direct, special, indirect, incidental, consequential, punitive, exemplary, or other losses, costs, expenses, or damages arising out of this Public License or use of the Licensed Material, even if the Licensor has been advised of the possibility of such losses, costs, expenses, or damages. Where a limitation of liability is not allowed in full or in part, this limitation may not apply to You.</strong></li> </ol> <ol start=\"3\" type=\"a\"> <li id=\"s5c\">The disclaimer of warranties and limitation of liability provided above shall be interpreted in a manner that, to the extent possible, most closely approximates an absolute disclaimer and waiver of all liability.</li> </ol> <p id=\"s6\"><strong>Section 6 – Term and Termination.</strong></p> <ol type=\"a\"> <li id=\"s6a\">This Public License applies for the term of the Copyright and Similar Rights licensed here. However, if You fail to comply with this Public License, then Your rights under this Public License terminate automatically.</li> <li id=\"s6b\"> <p>Where Your right to use the Licensed Material has terminated under Section <a href=\"#s6a\">6(a)</a>, it reinstates:</p> <ol> <li id=\"s6b1\">automatically as of the date the violation is cured, provided it is cured within 30 days of Your discovery of the violation; or</li> <li id=\"s6b2\">upon express reinstatement by the Licensor.</li> </ol> For the avoidance of doubt, this Section <a href=\"#s6b\">6(b)</a> does not affect any right the Licensor may have to seek remedies for Your violations of this Public License.</li> <li id=\"s6c\">For the avoidance of doubt, the Licensor may also offer the Licensed Material under separate terms or conditions or stop distributing the Licensed Material at any time; however, doing so will not terminate this Public License.</li> <li id=\"s6d\">Sections <a href=\"#s1\">1</a>, <a href=\"#s5\">5</a>, <a href=\"#s6\">6</a>, <a href=\"#s7\">7</a>, and <a href=\"#s8\">8</a> survive termination of this Public License.</li> </ol> <p id=\"s7\"><strong>Section 7 – Other Terms and Conditions.</strong></p> <ol type=\"a\"> <li id=\"s7a\">The Licensor shall not be bound by any additional or different terms or conditions communicated by You unless expressly agreed.</li> <li id=\"s7b\">Any arrangements, understandings, or agreements regarding the Licensed Material not stated herein are separate from and independent of the terms and conditions of this Public License.</li> </ol> <p id=\"s8\"><strong>Section 8 – Interpretation.</strong></p> <ol type=\"a\"> <li id=\"s8a\">For the avoidance of doubt, this Public License does not, and shall not be interpreted to, reduce, limit, restrict, or impose conditions on any use of the Licensed Material that could lawfully be made without permission under this Public License.</li> <li id=\"s8b\">To the extent possible, if any provision of this Public License is deemed unenforceable, it shall be automatically reformed to the minimum extent necessary to make it enforceable. If the provision cannot be reformed, it shall be severed from this Public License without affecting the enforceability of the remaining terms and conditions.</li> <li id=\"s8c\">No term or condition of this Public License will be waived and no failure to comply consented to unless expressly agreed to by the Licensor.</li> <li id=\"s8d\">Nothing in this Public License constitutes or may be interpreted as a limitation upon, or waiver of, any privileges and immunities that apply to the Licensor or You, including from the legal processes of any jurisdiction or authority.</li> </ol>', '2022-09-26 09:53:37', '2022-09-26 09:53:37');
INSERT INTO `licensedata` VALUES (6, 6, 1, '<h3>Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License</h3> <p>By exercising the Licensed Rights (defined below), You accept and agree to be bound by the terms and conditions of this Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License (\"Public License\"). To the extent this Public License may be interpreted as a contract, You are granted the Licensed Rights in consideration of Your acceptance of these terms and conditions, and the Licensor grants You such rights in consideration of benefits the Licensor receives from making the Licensed Material available under these terms and conditions.</p> <p id=\"s1\"><strong>Section 1 – Definitions.</strong></p> <ol type=\"a\"> <li id=\"s1a\"><strong>Adapted Material</strong> means material subject to Copyright and Similar Rights that is derived from or based upon the Licensed Material and in which the Licensed Material is translated, altered, arranged, transformed, or otherwise modified in a manner requiring permission under the Copyright and Similar Rights held by the Licensor. For purposes of this Public License, where the Licensed Material is a musical work, performance, or sound recording, Adapted Material is always produced where the Licensed Material is synched in timed relation with a moving image.</li> <li id=\"s1b\"><strong>Copyright and Similar Rights</strong> means copyright and/or similar rights closely related to copyright including, without limitation, performance, broadcast, sound recording, and Sui Generis Database Rights, without regard to how the rights are labeled or categorized. For purposes of this Public License, the rights specified in Section <a href=\"#s2b\">2(b)(1)-(2)</a> are not Copyright and Similar Rights.</li> <li id=\"s1c\"><strong>Effective Technological Measures</strong> means those measures that, in the absence of proper authority, may not be circumvented under laws fulfilling obligations under Article 11 of the WIPO Copyright Treaty adopted on December 20, 1996, and/or similar international agreements.</li> <li id=\"s1d\"><strong>Exceptions and Limitations</strong> means fair use, fair dealing, and/or any other exception or limitation to Copyright and Similar Rights that applies to Your use of the Licensed Material.</li> <li id=\"s1e\"><strong>Licensed Material</strong> means the artistic or literary work, database, or other material to which the Licensor applied this Public License.</li> <li id=\"s1f\"><strong>Licensed Rights</strong> means the rights granted to You subject to the terms and conditions of this Public License, which are limited to all Copyright and Similar Rights that apply to Your use of the Licensed Material and that the Licensor has authority to license.</li> <li id=\"s1g\"><strong>Licensor</strong> means the individual(s) or entity(ies) granting rights under this Public License.</li> <li id=\"s1h\"><strong>NonCommercial</strong> means not primarily intended for or directed towards commercial advantage or monetary compensation. For purposes of this Public License, the exchange of the Licensed Material for other material subject to Copyright and Similar Rights by digital file-sharing or similar means is NonCommercial provided there is no payment of monetary compensation in connection with the exchange.</li> <li id=\"s1i\"><strong>Share</strong> means to provide material to the public by any means or process that requires permission under the Licensed Rights, such as reproduction, public display, public performance, distribution, dissemination, communication, or importation, and to make material available to the public including in ways that members of the public may access the material from a place and at a time individually chosen by them.</li> <li id=\"s1j\"><strong>Sui Generis Database Rights</strong> means rights other than copyright resulting from Directive 96/9/EC of the European Parliament and of the Council of 11 March 1996 on the legal protection of databases, as amended and/or succeeded, as well as other essentially equivalent rights anywhere in the world.</li> <li id=\"s1k\"><strong>You</strong> means the individual or entity exercising the Licensed Rights under this Public License. <strong>Your</strong> has a corresponding meaning.</li> </ol> <p id=\"s2\"><strong>Section 2 – Scope.</strong></p> <ol type=\"a\"> <li id=\"s2a\"><strong>License grant</strong>. <ol> <li id=\"s2a1\">Subject to the terms and conditions of this Public License, the Licensor hereby grants You a worldwide, royalty-free, non-sublicensable, non-exclusive, irrevocable license to exercise the Licensed Rights in the Licensed Material to: <ol type=\"A\"> <li id=\"s2a1A\">reproduce and Share the Licensed Material, in whole or in part, for NonCommercial purposes only; and</li> <li id=\"s2a1B\">produce and reproduce, but not Share, Adapted Material for NonCommercial purposes only.</li> </ol> </li><li id=\"s2a2\"><span style=\"text-decoration: underline;\">Exceptions and Limitations</span>. For the avoidance of doubt, where Exceptions and Limitations apply to Your use, this Public License does not apply, and You do not need to comply with its terms and conditions.</li> <li id=\"s2a3\"><span style=\"text-decoration: underline;\">Term</span>. The term of this Public License is specified in Section <a href=\"#s6a\">6(a)</a>.</li> <li id=\"s2a4\"><span style=\"text-decoration: underline;\">Media and formats; technical modifications allowed</span>. The Licensor authorizes You to exercise the Licensed Rights in all media and formats whether now known or hereafter created, and to make technical modifications necessary to do so. The Licensor waives and/or agrees not to assert any right or authority to forbid You from making technical modifications necessary to exercise the Licensed Rights, including technical modifications necessary to circumvent Effective Technological Measures. For purposes of this Public License, simply making modifications authorized by this Section <a href=\"#s2a4\">2(a)(4)</a> never produces Adapted Material.</li> <li id=\"s2a5\"><span style=\"text-decoration: underline;\">Downstream recipients</span>. <div class=\"para\"> <ol type=\"A\"> <li id=\"s2a5A\"><span style=\"text-decoration: underline;\">Offer from the Licensor – Licensed Material</span>. Every recipient of the Licensed Material automatically receives an offer from the Licensor to exercise the Licensed Rights under the terms and conditions of this Public License.</li> <li id=\"s2a5B\"><span style=\"text-decoration: underline;\">No downstream restrictions</span>. You may not offer or impose any additional or different terms or conditions on, or apply any Effective Technological Measures to, the Licensed Material if doing so restricts exercise of the Licensed Rights by any recipient of the Licensed Material.</li> </ol> </div> </li><li id=\"s2a6\"><span style=\"text-decoration: underline;\">No endorsement</span>. Nothing in this Public License constitutes or may be construed as permission to assert or imply that You are, or that Your use of the Licensed Material is, connected with, or sponsored, endorsed, or granted official status by, the Licensor or others designated to receive attribution as provided in Section <a href=\"#s3a1Ai\">3(a)(1)(A)(i)</a>.</li> </ol> </li><li id=\"s2b\"><p><strong>Other rights</strong>.</p> <ol> <li id=\"s2b1\">Moral rights, such as the right of integrity, are not licensed under this Public License, nor are publicity, privacy, and/or other similar personality rights; however, to the extent possible, the Licensor waives and/or agrees not to assert any such rights held by the Licensor to the limited extent necessary to allow You to exercise the Licensed Rights, but not otherwise.</li> <li id=\"s2b2\">Patent and trademark rights are not licensed under this Public License.</li> <li id=\"s2b3\">To the extent possible, the Licensor waives any right to collect royalties from You for the exercise of the Licensed Rights, whether directly or through a collecting society under any voluntary or waivable statutory or compulsory licensing scheme. In all other cases the Licensor expressly reserves any right to collect such royalties, including when the Licensed Material is used other than for NonCommercial purposes.</li> </ol> </li> </ol> <p id=\"s3\"><strong>Section 3 – License Conditions.</strong></p> <p>Your exercise of the Licensed Rights is expressly made subject to the following conditions.</p> <ol type=\"a\"> <li id=\"s3a\"><p><strong>Attribution</strong>.</p> <ol> <li id=\"s3a1\"><p>If You Share the Licensed Material, You must:</p> <ol type=\"A\"> <li id=\"s3a1A\">retain the following if it is supplied by the Licensor with the Licensed Material: <ol type=\"i\"> <li id=\"s3a1Ai\">identification of the creator(s) of the Licensed Material and any others designated to receive attribution, in any reasonable manner requested by the Licensor (including by pseudonym if designated);</li> <li id=\"s3a1Aii\">a copyright notice;</li> <li id=\"s3a1Aiii\">a notice that refers to this Public License; </li> <li id=\"s3a1Aiv\">a notice that refers to the disclaimer of warranties;</li> <li id=\"s3a1Av\">a URI or hyperlink to the Licensed Material to the extent reasonably practicable;</li> </ol> </li><li id=\"s3a1B\">indicate if You modified the Licensed Material and retain an indication of any previous modifications; and</li> <li id=\"s3a1C\">indicate the Licensed Material is licensed under this Public License, and include the text of, or the URI or hyperlink to, this Public License.</li> </ol> For the avoidance of doubt, You do not have permission under this Public License to Share Adapted Material. </li> <li id=\"s3a2\">You may satisfy the conditions in Section <a href=\"#s3a1\">3(a)(1)</a> in any reasonable manner based on the medium, means, and context in which You Share the Licensed Material. For example, it may be reasonable to satisfy the conditions by providing a URI or hyperlink to a resource that includes the required information.</li> <li id=\"s3a3\">If requested by the Licensor, You must remove any of the information required by Section <a href=\"#s3a1A\">3(a)(1)(A)</a> to the extent reasonably practicable.</li> </ol> </li> </ol> <p id=\"s4\"><strong>Section 4 – Sui Generis Database Rights.</strong></p> <p>Where the Licensed Rights include Sui Generis Database Rights that apply to Your use of the Licensed Material:</p> <ol type=\"a\"> <li id=\"s4a\">for the avoidance of doubt, Section <a href=\"#s2a1\">2(a)(1)</a> grants You the right to extract, reuse, reproduce, and Share all or a substantial portion of the contents of the database for NonCommercial purposes only and provided You do not Share Adapted Material;</li> <li id=\"s4b\">if You include all or a substantial portion of the database contents in a database in which You have Sui Generis Database Rights, then the database in which You have Sui Generis Database Rights (but not its individual contents) is Adapted Material; and</li> <li id=\"s4c\">You must comply with the conditions in Section <a href=\"#s3a\">3(a)</a> if You Share all or a substantial portion of the contents of the database.</li> </ol> For the avoidance of doubt, this Section <a href=\"#s4\">4</a> supplements and does not replace Your obligations under this Public License where the Licensed Rights include other Copyright and Similar Rights. <p id=\"s5\"><strong>Section 5 – Disclaimer of Warranties and Limitation of Liability.</strong></p> <ol style=\"font-weight: bold;\" type=\"a\"> <li id=\"s5a\"><strong>Unless otherwise separately undertaken by the Licensor, to the extent possible, the Licensor offers the Licensed Material as-is and as-available, and makes no representations or warranties of any kind concerning the Licensed Material, whether express, implied, statutory, or other. This includes, without limitation, warranties of title, merchantability, fitness for a particular purpose, non-infringement, absence of latent or other defects, accuracy, or the presence or absence of errors, whether or not known or discoverable. Where disclaimers of warranties are not allowed in full or in part, this disclaimer may not apply to You.</strong></li> <li id=\"s5b\"><strong>To the extent possible, in no event will the Licensor be liable to You on any legal theory (including, without limitation, negligence) or otherwise for any direct, special, indirect, incidental, consequential, punitive, exemplary, or other losses, costs, expenses, or damages arising out of this Public License or use of the Licensed Material, even if the Licensor has been advised of the possibility of such losses, costs, expenses, or damages. Where a limitation of liability is not allowed in full or in part, this limitation may not apply to You.</strong></li> </ol> <ol start=\"3\" type=\"a\"> <li id=\"s5c\">The disclaimer of warranties and limitation of liability provided above shall be interpreted in a manner that, to the extent possible, most closely approximates an absolute disclaimer and waiver of all liability.</li> </ol> <p id=\"s6\"><strong>Section 6 – Term and Termination.</strong></p> <ol type=\"a\"> <li id=\"s6a\">This Public License applies for the term of the Copyright and Similar Rights licensed here. However, if You fail to comply with this Public License, then Your rights under this Public License terminate automatically.</li> <li id=\"s6b\"> <p>Where Your right to use the Licensed Material has terminated under Section <a href=\"#s6a\">6(a)</a>, it reinstates:</p> <ol> <li id=\"s6b1\">automatically as of the date the violation is cured, provided it is cured within 30 days of Your discovery of the violation; or</li> <li id=\"s6b2\">upon express reinstatement by the Licensor.</li> </ol> For the avoidance of doubt, this Section <a href=\"#s6b\">6(b)</a> does not affect any right the Licensor may have to seek remedies for Your violations of this Public License.</li> <li id=\"s6c\">For the avoidance of doubt, the Licensor may also offer the Licensed Material under separate terms or conditions or stop distributing the Licensed Material at any time; however, doing so will not terminate this Public License.</li> <li id=\"s6d\">Sections <a href=\"#s1\">1</a>, <a href=\"#s5\">5</a>, <a href=\"#s6\">6</a>, <a href=\"#s7\">7</a>, and <a href=\"#s8\">8</a> survive termination of this Public License.</li> </ol> <p id=\"s7\"><strong>Section 7 – Other Terms and Conditions.</strong></p> <ol type=\"a\"> <li id=\"s7a\">The Licensor shall not be bound by any additional or different terms or conditions communicated by You unless expressly agreed.</li> <li id=\"s7b\">Any arrangements, understandings, or agreements regarding the Licensed Material not stated herein are separate from and independent of the terms and conditions of this Public License.</li> </ol> <p id=\"s8\"><strong>Section 8 – Interpretation.</strong></p> <ol type=\"a\"> <li id=\"s8a\">For the avoidance of doubt, this Public License does not, and shall not be interpreted to, reduce, limit, restrict, or impose conditions on any use of the Licensed Material that could lawfully be made without permission under this Public License.</li> <li id=\"s8b\">To the extent possible, if any provision of this Public License is deemed unenforceable, it shall be automatically reformed to the minimum extent necessary to make it enforceable. If the provision cannot be reformed, it shall be severed from this Public License without affecting the enforceability of the remaining terms and conditions.</li> <li id=\"s8c\">No term or condition of this Public License will be waived and no failure to comply consented to unless expressly agreed to by the Licensor.</li> <li id=\"s8d\">Nothing in this Public License constitutes or may be interpreted as a limitation upon, or waiver of, any privileges and immunities that apply to the Licensor or You, including from the legal processes of any jurisdiction or authority.</li> </ol>', '2022-09-26 09:53:37', '2022-09-26 09:53:37');
INSERT INTO `licensedata` VALUES (7, 7, 1, '<h2>Attribution License (ODC-By)</h2><h4>Preamble</h4><p>The Open Data Commons Attribution License is a license agreement intended to allow users to freely share, modify, and use this Database subject only to the attribution requirements set out in Section 4.</p> <p>Databases can contain a wide variety of types of content (images, audiovisual material, and sounds all in the same database, for example), and so this license only governs the rights over the Database, and not the contents of the Database individually. Licensors may therefore wish to use this license together with another license for the contents.</p> <p>Sometimes the contents of a database, or the database itself, can be covered by other rights not addressed here (such as private contracts, trademark over the name, or privacy rights / data protection rights over information in the contents), and so you are advised that you may have to consult other documents or clear other rights before doing activities not covered by this License.</p> <p>The Licensor (as defined below)</p> <p>and</p> <p>You (as defined below)</p> <p>agree as follows:</p> <h4>1.0 Definitions of Capitalised Words</h4><p>\"Collective Database\" – Means this Database in unmodified form as part of a collection of independent databases in themselves that together are assembled into a collective whole. A work that constitutes a Collective Database will not be considered a Derivative Database.</p> <p>\"Convey\" – As a verb, means Using the Database, a Derivative Database, or the Database as part of a Collective Database in any way that enables a Person to make or receive copies of the Database or a Derivative Database. Conveying does not include interaction with a user through a computer network, or creating and Using a Produced Work, where no transfer of a copy of the Database or a Derivative Database occurs.</p> <p>\"Contents\" – The contents of this Database, which includes the information, independent works, or other material collected into the Database. For example, the contents of the Database could be factual data or works such as images, audiovisual material, text, or sounds.</p> <p>\"Database\" – A collection of material (the Contents) arranged in a systematic or methodical way and individually accessible by electronic or other means offered under the terms of this License.</p> <p>\"Database Directive\" – Means Directive 96/9/EC of the European Parliament and of the Council of 11 March 1996 on the legal protection of databases, as amended or succeeded.</p> <p>\"Database Right\" – Means rights resulting from the Chapter III (\"sui generis\") rights in the Database Directive (as amended and as transposed by member states), which includes the Extraction and Re-utilisation of the whole or a Substantial part of the Contents, as well as any similar rights available in the relevant jurisdiction under Section 10.4.</p> <p>\"Derivative Database\" – Means a database based upon the Database, and includes any translation, adaptation, arrangement, modification, or any other alteration of the Database or of a Substantial part of the Contents. This includes, but is not limited to, Extracting or Re-utilising the whole or a Substantial part of the Contents in a new Database.</p> <p>\"Extraction\" – Means the permanent or temporary transfer of all or a Substantial part of the Contents to another medium by any means or in any form.</p> <p>\"License\" – Means this license agreement and is both a license of rights such as copyright and Database Rights and an agreement in contract.</p> <p>\"Licensor\" – Means the Person that offers the Database under the terms of this License.</p> <p>\"Person\" – Means a natural or legal person or a body of persons corporate or incorporate.</p> <p>\"Produced Work\" – a work (such as an image, audiovisual material, text, or sounds) resulting from using the whole or a Substantial part of the Contents (via a search or other query) from this Database, a Derivative Database, or this Database as part of a Collective Database.</p> <p>\"Publicly\" – means to Persons other than You or under Your control by either more than 50% ownership or by the power to direct their activities (such as contracting with an independent consultant).</p> <p>\"Re-utilisation\" – means any form of making available to the public all or a Substantial part of the Contents by the distribution of copies, by renting, by online or other forms of transmission.</p> <p>\"Substantial\" – Means substantial in terms of quantity or quality or a combination of both. The repeated and systematic Extraction or Re-utilisation of insubstantial parts of the Contents may amount to the Extraction or Re-utilisation of a Substantial part of the Contents.</p> <p>\"Use\" – As a verb, means doing any act that is restricted by copyright or Database Rights whether in the original medium or any other; and includes without limitation distributing, copying, publicly performing, publicly displaying, and preparing derivative works of the Database, as well as modifying the Database as may be technically necessary to use it in a different mode or format.</p> <p>\"You\" – Means a Person exercising rights under this License who has not previously violated the terms of this License with respect to the Database, or who has received express permission from the Licensor to exercise rights under this License despite a previous violation.</p> <p>Words in the singular include the plural and vice versa.</p> <h4>2.0 What this License covers</h4><p>2.1. Legal effect of this document. This License is:</p> <p>a. A license of applicable copyright and neighbouring rights;</p> <p>b. A license of the Database Right; and</p> <p>c. An agreement in contract between You and the Licensor.</p> <p>2.2 Legal rights covered. This License covers the legal rights in the Database, including:</p> <p>a. Copyright. Any copyright or neighbouring rights in the Database. The copyright licensed includes any individual elements of the Database, but does not cover the copyright over the Contents independent of this Database. See Section 2.4 for details. Copyright law varies between jurisdictions, but is likely to cover: the Database model or schema, which is the structure, arrangement, and organisation of the Database, and can also include the Database tables and table indexes; the data entry and output sheets; and the Field names of Contents stored in the Database;</p> <p>b. Database Rights. Database Rights only extend to the Extraction and Re-utilisation of the whole or a Substantial part of the Contents. Database Rights can apply even when there is no copyright over the Database. Database Rights can also apply when the Contents are removed from the Database and are selected and arranged in a way that would not infringe any applicable copyright; and</p> <p>c. Contract. This is an agreement between You and the Licensor for access to the Database. In return you agree to certain conditions of use on this access as outlined in this License.</p> <p>2.3 Rights not covered.</p> <p>a. This License does not apply to computer programs used in the making or operation of the Database;</p> <p>b. This License does not cover any patents over the Contents or the Database; and</p> <p>c. This License does not cover any trademarks associated with the Database.</p> <p>2.4 Relationship to Contents in the Database. The individual items of the Contents contained in this Database may be covered by other rights, including copyright, patent, data protection, privacy, or personality rights, and this License does not cover any rights (other than Database Rights or in contract) in individual Contents contained in the Database.</p> <p>For example, if used on a Database of images (the Contents), this License would not apply to copyright over individual images, which could have their own separate licenses, or one single license covering all of the rights over the images.</p> <h4>3.0 Rights granted</h4><p>3.1 Subject to the terms and conditions of this License, the Licensor grants to You a worldwide, royalty-free, non-exclusive, terminable (but only under Section 9) license to Use the Database for the duration of any applicable copyright and Database Rights. These rights explicitly include commercial use, and do not exclude any field of endeavour. To the extent possible in the relevant jurisdiction, these rights may be exercised in all media and formats whether now known or created in the future.</p> <p>The rights granted cover, for example:</p> <p>a. Extraction and Re-utilisation of the whole or a Substantial part of the Contents;</p> <p>b. Creation of Derivative Databases;</p> <p>c. Creation of Collective Databases;</p> <p>d. Creation of temporary or permanent reproductions by any means and in any form, in whole or in part, including of any Derivative Databases or as a part of Collective Databases; and</p> <p>e. Distribution, communication, display, lending, making available, or performance to the public by any means and in any form, in whole or in part, including of any Derivative Database or as a part of Collective Databases.</p> <p>3.2 Compulsory license schemes. For the avoidance of doubt:</p> <p>a. Non-waivable compulsory license schemes. In those jurisdictions in which the right to collect royalties through any statutory or compulsory licensing scheme cannot be waived, the Licensor reserves the exclusive right to collect such royalties for any exercise by You of the rights granted under this License;</p> <p>b. Waivable compulsory license schemes. In those jurisdictions in which the right to collect royalties through any statutory or compulsory licensing scheme can be waived, the Licensor waives the exclusive right to collect such royalties for any exercise by You of the rights granted under this License; and,</p> <p>c. Voluntary license schemes. The Licensor waives the right to collect royalties, whether individually or, in the event that the Licensor is a member of a collecting society that administers voluntary licensing schemes, via that society, from any exercise by You of the rights granted under this License.</p> <p>3.3 The right to release the Database under different terms, or to stop distributing or making available the Database, is reserved. Note that this Database may be multiple-licensed, and so You may have the choice of using alternative licenses for this Database. Subject to Section 10.4, all other rights not expressly granted by Licensor are reserved.</p> <h4>4.0 Conditions of Use</h4><p>4.1 The rights granted in Section 3 above are expressly made subject to Your complying with the following conditions of use. These are important conditions of this License, and if You fail to follow them, You will be in material breach of its terms.</p> <p>4.2 Notices. If You Publicly Convey this Database, any Derivative Database, or the Database as part of a Collective Database, then You must:</p> <p>a. Do so only under the terms of this License;</p> <p>b. Include a copy of this License or its Uniform Resource Identifier (URI) with the Database or Derivative Database, including both in the Database or Derivative Database and in any relevant documentation;</p> <p>c. Keep intact any copyright or Database Right notices and notices that refer to this License; and</p> <p>d. If it is not possible to put the required notices in a particular file due to its structure, then You must include the notices in a location (such as a relevant directory) where users would be likely to look for it.</p> <p>4.3 Notice for using output (Contents). Creating and Using a Produced Work does not require the notice in Section 4.2. However, if you Publicly Use a Produced Work, You must include a notice associated with the Produced Work reasonably calculated to make any Person that uses, views, accesses, interacts with, or is otherwise exposed to the Produced Work aware that Content was obtained from the Database, Derivative Database, or the Database as part of a Collective Database, and that it is available under this License.</p> <p>a. Example notice. The following text will satisfy notice under Section 4.3:</p> <pre><code>Contains information from DATABASE NAME which is made available under the ODC Attribution License. </code></pre> <p>DATABASE NAME should be replaced with the name of the Database and a hyperlink to the location of the Database. \"ODC Attribution License\" should contain a hyperlink to the URI of the text of this License. If hyperlinks are not possible, You should include the plain text of the required URI’s with the above notice.</p> <p>4.4 Licensing of others. You may not sublicense the Database. Each time You communicate the Database, the whole or Substantial part of the Contents, or any Derivative Database to anyone else in any way, the Licensor offers to the recipient a license to the Database on the same terms and conditions as this License. You are not responsible for enforcing compliance by third parties with this License, but You may enforce any rights that You have over a Derivative Database. You are solely responsible for any modifications of a Derivative Database made by You or another Person at Your direction. You may not impose any further restrictions on the exercise of the rights granted or affirmed under this License.</p> <h4>5.0 Moral rights</h4><p>5.1 Moral rights. This section covers moral rights, including any rights to be identified as the author of the Database or to object to treatment that would otherwise prejudice the author’s honour and reputation, or any other derogatory treatment:</p> <p>a. For jurisdictions allowing waiver of moral rights, Licensor waives all moral rights that Licensor may have in the Database to the fullest extent possible by the law of the relevant jurisdiction under Section 10.4;</p> <p>b. If waiver of moral rights under Section 5.1 a in the relevant jurisdiction is not possible, Licensor agrees not to assert any moral rights over the Database and waives all claims in moral rights to the fullest extent possible by the law of the relevant jurisdiction under Section 10.4; and</p> <p>c. For jurisdictions not allowing waiver or an agreement not to assert moral rights under Section 5.1 a and b, the author may retain their moral rights over certain aspects of the Database.</p> <p>Please note that some jurisdictions do not allow for the waiver of moral rights, and so moral rights may still subsist over the Database in some jurisdictions.</p> <h4>6.0 Fair dealing, Database exceptions, and other rights not affected</h4><p>6.1 This License does not affect any rights that You or anyone else may independently have under any applicable law to make any use of this Database, including without limitation:</p> <p>a. Exceptions to the Database Right including: Extraction of Contents from non-electronic Databases for private purposes, Extraction for purposes of illustration for teaching or scientific research, and Extraction or Re-utilisation for public security or an administrative or judicial procedure.</p> <p>b. Fair dealing, fair use, or any other legally recognised limitation or exception to infringement of copyright or other applicable laws.</p> <p>6.2 This License does not affect any rights of lawful users to Extract and Re-utilise insubstantial parts of the Contents, evaluated quantitatively or qualitatively, for any purposes whatsoever, including creating a Derivative Database (subject to other rights over the Contents, see Section 2.4). The repeated and systematic Extraction or Re-utilisation of insubstantial parts of the Contents may however amount to the Extraction or Re-utilisation of a Substantial part of the Contents.</p> <h4>7.0 Warranties and Disclaimer</h4><p>7.1 The Database is licensed by the Licensor \"as is\" and without any warranty of any kind, either express, implied, or arising by statute, custom, course of dealing, or trade usage. Licensor specifically disclaims any and all implied warranties or conditions of title, non-infringement, accuracy or completeness, the presence or absence of errors, fitness for a particular purpose, merchantability, or otherwise. Some jurisdictions do not allow the exclusion of implied warranties, so this exclusion may not apply to You.</p> <h4>8.0 Limitation of liability</h4><p>8.1 Subject to any liability that may not be excluded or limited by law, the Licensor is not liable for, and expressly excludes, all liability for loss or damage however and whenever caused to anyone by any use under this License, whether by You or by anyone else, and whether caused by any fault on the part of the Licensor or not. This exclusion of liability includes, but is not limited to, any special, incidental, consequential, punitive, or exemplary damages such as loss of revenue, data, anticipated profits, and lost business. This exclusion applies even if the Licensor has been advised of the possibility of such damages.</p> <p>8.2 If liability may not be excluded by law, it is limited to actual and direct financial loss to the extent it is caused by proved negligence on the part of the Licensor.</p> <h4>9.0 Termination of Your rights under this License</h4><p>9.1 Any breach by You of the terms and conditions of this License automatically terminates this License with immediate effect and without notice to You. For the avoidance of doubt, Persons who have received the Database, the whole or a Substantial part of the Contents, Derivative Databases, or the Database as part of a Collective Database from You under this License will not have their licenses terminated provided their use is in full compliance with this License or a license granted under Section 4.8 of this License. Sections 1, 2, 7, 8, 9 and 10 will survive any termination of this License.</p> <p>9.2 If You are not in breach of the terms of this License, the Licensor will not terminate Your rights under it.</p> <p>9.3 Unless terminated under Section 9.1, this License is granted to You for the duration of applicable rights in the Database.</p> <p>9.4 Reinstatement of rights. If you cease any breach of the terms and conditions of this License, then your full rights under this License will be reinstated:</p> <p>a. Provisionally and subject to permanent termination until the 60th day after cessation of breach;</p> <p>b. Permanently on the 60th day after cessation of breach unless otherwise reasonably notified by the Licensor; or</p> <p>c. Permanently if reasonably notified by the Licensor of the violation, this is the first time You have received notice of violation of this License from the Licensor, and You cure the violation prior to 30 days after your receipt of the notice.</p> <p>9.5 Notwithstanding the above, Licensor reserves the right to release the Database under different license terms or to stop distributing or making available the Database. Releasing the Database under different license terms or stopping the distribution of the Database will not withdraw this License (or any other license that has been, or is required to be, granted under the terms of this License), and this License will continue in full force and effect unless terminated as stated above.</p> <h4>10.0 General</h4><p>10.1 If any provision of this License is held to be invalid or unenforceable, that must not affect the validity or enforceability of the remainder of the terms and conditions of this License and each remaining provision of this License shall be valid and enforced to the fullest extent permitted by law.</p> <p>10.2 This License is the entire agreement between the parties with respect to the rights granted here over the Database. It replaces any earlier understandings, agreements or representations with respect to the Database.</p> <p>10.3 If You are in breach of the terms of this License, You will not be entitled to rely on the terms of this License or to complain of any breach by the Licensor.</p> <p>10.4 Choice of law. This License takes effect in and will be governed by the laws of the relevant jurisdiction in which the License terms are sought to be enforced. If the standard suite of rights granted under applicable copyright law and Database Rights in the relevant jurisdiction includes additional rights not granted under this License, these additional rights are granted in this License in order to meet the terms of this License.</p>', '2022-09-26 09:53:37', '2022-09-26 09:53:37');

-- ----------------------------
-- Table structure for licenselogs
-- ----------------------------
DROP TABLE IF EXISTS `licenselogs`;
CREATE TABLE `licenselogs`  (
                                `id` int(11) NOT NULL AUTO_INCREMENT,
                                `license_id` int(11) NOT NULL,
                                `user_id` int(11) NOT NULL,
                                `accepted_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                PRIMARY KEY (`id`) USING BTREE,
                                INDEX `license_id`(`license_id`) USING BTREE,
                                CONSTRAINT `licenselogs_ibfk_1` FOREIGN KEY (`license_id`) REFERENCES `licenses` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of licenselogs
-- ----------------------------

-- ----------------------------
-- Table structure for licenses
-- ----------------------------
DROP TABLE IF EXISTS `licenses`;
CREATE TABLE `licenses`  (
                             `id` int(11) NOT NULL AUTO_INCREMENT,
                             `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                             `description` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
                             `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                             `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of licenses
-- ----------------------------
INSERT INTO `licenses` VALUES (1, 'CC BY', 'Creative Commons Attribution License', '2022-09-26 09:53:37', '2022-09-26 09:53:37');
INSERT INTO `licenses` VALUES (2, 'CC BY-SA', 'Creative Commons Attribution-ShareAlike License', '2022-09-26 09:53:37', '2022-09-26 09:53:37');
INSERT INTO `licenses` VALUES (3, 'CC BY-ND', 'Creative Commons Attribution-NoDerivs License', '2022-09-26 09:53:37', '2022-09-26 09:53:37');
INSERT INTO `licenses` VALUES (4, 'CC BY-NC', 'Creative Commons Attribution-NonCommercial License', '2022-09-26 09:53:37', '2022-09-26 09:53:37');
INSERT INTO `licenses` VALUES (5, 'CC BY-NC-SA', 'Creative Commons Attribution-NonCommercial-ShareAlike License', '2022-09-26 09:53:37', '2022-09-26 09:53:37');
INSERT INTO `licenses` VALUES (6, 'CC BY-NC-ND', 'Creative Commons Attribution-NonCommercial-NoDerivs License', '2022-09-26 09:53:37', '2022-09-26 09:53:37');
INSERT INTO `licenses` VALUES (7, 'ODC-By', 'Open Data Commons Attribution License', '2022-09-26 09:53:37', '2022-09-26 09:53:37');

-- ----------------------------
-- Table structure for links
-- ----------------------------
DROP TABLE IF EXISTS `links`;
CREATE TABLE `links`  (
                          `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                          `linktype_id` int(11) NULL DEFAULT NULL COMMENT 'Foreign key to linktypes (linktypes.id).',
                          `foreign_id` int(11) NULL DEFAULT NULL,
                          `hyperlink` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'The actual hyperlink.',
                          `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'A description of the link.',
                          `visibility` tinyint(1) NULL DEFAULT 1 COMMENT 'Determines if the link is visible or not: {0, 1}',
                          `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                          `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                          PRIMARY KEY (`id`) USING BTREE,
                          INDEX `links_linktype_id`(`linktype_id`) USING BTREE,
                          INDEX `links_id`(`id`) USING BTREE,
                          CONSTRAINT `links_ibfk_1` FOREIGN KEY (`linktype_id`) REFERENCES `linktypes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Germinate allows to define external links for different types of data. With this feature you can\r\ndefine links to external resources.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of links
-- ----------------------------

-- ----------------------------
-- Table structure for linktypes
-- ----------------------------
DROP TABLE IF EXISTS `linktypes`;
CREATE TABLE `linktypes`  (
                              `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                              `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'A description of the link\r.',
                              `target_table` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'This is the table that the link links to.',
                              `target_column` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'This is the column that is used to generate the link.',
                              `placeholder` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'The part of the link that will be replaced by the value of the target column.',
                              `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                              `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                              PRIMARY KEY (`id`) USING BTREE,
                              INDEX `linktypes_id`(`id`) USING BTREE,
                              INDEX `linktypes_target_table`(`target_table`, `target_column`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'The link type determines which database table and column are used to construct the final\r\nlink. The ”placeholder” in the link (from the links table) will be replaced by the value of the\r\n”target column” in the ”target table”' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of linktypes
-- ----------------------------

-- ----------------------------
-- Table structure for locales
-- ----------------------------
DROP TABLE IF EXISTS `locales`;
CREATE TABLE `locales`  (
                            `id` int(11) NOT NULL AUTO_INCREMENT,
                            `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                            `description` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
                            `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                            `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of locales
-- ----------------------------
INSERT INTO `locales` VALUES (1, 'en_GB', 'British English', '2022-09-26 09:53:37', '2022-09-26 09:53:37');

-- ----------------------------
-- Table structure for locations
-- ----------------------------
DROP TABLE IF EXISTS `locations`;
CREATE TABLE `locations`  (
                              `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                              `locationtype_id` int(11) NOT NULL COMMENT 'Foreign key to locations (locations.id).',
                              `country_id` int(11) NOT NULL DEFAULT 0 COMMENT 'Foreign key to countries (countries.id).',
                              `state` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'The state where the location is if this exists.',
                              `region` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'The region where the location is if this exists.',
                              `site_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'The site name where the location is.',
                              `site_name_short` varchar(22) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Shortened site name which can be used in tables within Germinate.',
                              `elevation` decimal(64, 10) NULL DEFAULT NULL COMMENT 'The elevation of the site in metres.',
                              `latitude` decimal(64, 10) NULL DEFAULT NULL COMMENT 'Latitude of the location.',
                              `longitude` decimal(64, 10) NULL DEFAULT NULL COMMENT 'Longitude of the location.',
                              `coordinate_uncertainty` int(11) NULL DEFAULT NULL COMMENT 'Uncertainty associated with the coordinates in metres. Leave the value empty if the uncertainty is unknown. ',
                              `coordinate_datum` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'The geodetic datum or spatial reference system upon which the coordinates given in decimal latitude and decimal longitude are based (e.g. WGS84, ETRS89, NAD83). The GPS uses the WGS84 datum.',
                              `georeferencing_method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'The georeferencing method used (GPS, determined from map, gazetteer, or estimated using software). Leave the value empty if georeferencing method is not known.',
                              `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                              `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                              PRIMARY KEY (`id`) USING BTREE,
                              INDEX `country_id`(`country_id`) USING BTREE,
                              INDEX `locations_ibfk_2`(`locationtype_id`) USING BTREE,
                              CONSTRAINT `locations_ibfk_1` FOREIGN KEY (`country_id`) REFERENCES `countries` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE,
                              CONSTRAINT `locations_ibfk_2` FOREIGN KEY (`locationtype_id`) REFERENCES `locationtypes` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Describes locations. Locations can be collecting sites or the location of any geographical feature such as research institutes or lab locations.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of locations
-- ----------------------------

-- ----------------------------
-- Table structure for locationtypes
-- ----------------------------
DROP TABLE IF EXISTS `locationtypes`;
CREATE TABLE `locationtypes`  (
                                  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                                  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'The name of the location type. ',
                                  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'A description of the location type.',
                                  `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                                  `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Describes a location.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of locationtypes
-- ----------------------------
INSERT INTO `locationtypes` VALUES (1, 'collectingsites', 'Locations where accessions have been collected', '2014-11-27 14:57:36', '2014-11-27 14:57:26');
INSERT INTO `locationtypes` VALUES (2, 'datasets', 'Locations associated with datasets', '2015-01-28 12:49:03', '2015-01-28 12:49:05');
INSERT INTO `locationtypes` VALUES (3, 'trialsite', 'Locations associated with a trial', '2015-01-28 12:49:01', '2015-01-28 12:49:02');

-- ----------------------------
-- Table structure for mapdefinitions
-- ----------------------------
DROP TABLE IF EXISTS `mapdefinitions`;
CREATE TABLE `mapdefinitions`  (
                                   `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                                   `mapfeaturetype_id` int(11) NOT NULL COMMENT 'Foreign key to mapfeaturetypes (mapfeaturetypes.id).',
                                   `marker_id` int(11) NOT NULL COMMENT 'Foreign key to markers (markers.id).',
                                   `map_id` int(11) NOT NULL COMMENT 'Foreign key to maps (maps.id).',
                                   `definition_start` double(64, 10) NOT NULL COMMENT 'Used if the markers location spans over an area more than a single point on the maps. Determines the marker start location.',
                                   `definition_end` double(64, 10) NULL DEFAULT NULL COMMENT 'Used if the markers location spans over an area more than a single point on the maps. Determines the marker end location.',
                                   `chromosome` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'The chromosome/linkage group that this marker is found on.',
                                   `arm_impute` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'If a chromosome arm is available then this can be entered here.',
                                   `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                                   `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                                   PRIMARY KEY (`id`) USING BTREE,
                                   INDEX `mapfeaturetype_id`(`mapfeaturetype_id`) USING BTREE,
                                   INDEX `marker_id`(`marker_id`) USING BTREE,
                                   INDEX `map_id`(`map_id`) USING BTREE,
                                   INDEX `marker_id_2`(`marker_id`, `map_id`) USING BTREE,
                                   CONSTRAINT `mapdefinitions_ibfk_1` FOREIGN KEY (`mapfeaturetype_id`) REFERENCES `mapfeaturetypes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                                   CONSTRAINT `mapdefinitions_ibfk_2` FOREIGN KEY (`marker_id`) REFERENCES `markers` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                                   CONSTRAINT `mapdefinitions_ibfk_3` FOREIGN KEY (`map_id`) REFERENCES `maps` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Relates genetic markers to a map and assigns a position (if known). Maps are made up of lists of markers and positions (genetic or physiscal and chromosome/linkage group assignation). In the case of QTL the definition_start and definition_end columns can be used to specify a range across a linkage group.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mapdefinitions
-- ----------------------------

-- ----------------------------
-- Table structure for mapfeaturetypes
-- ----------------------------
DROP TABLE IF EXISTS `mapfeaturetypes`;
CREATE TABLE `mapfeaturetypes`  (
                                    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                                    `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Description of the feature type. This could include a definition of the marker type such as \'SNP\', \'KASP\' or \'AFLP\'.',
                                    `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                                    `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Defines features which can exist on maps. In general this will be the marker type but it can also be used to identify QTL regions.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mapfeaturetypes
-- ----------------------------

-- ----------------------------
-- Table structure for mapoverlays
-- ----------------------------
DROP TABLE IF EXISTS `mapoverlays`;
CREATE TABLE `mapoverlays`  (
                                `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'The primary key of this table.',
                                `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'A name for the map overlay.',
                                `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'An optional description of what is shown on the overlay.',
                                `bottom_left_lat` decimal(64, 10) NULL DEFAULT NULL COMMENT 'The bottom left latitude coordinates in decimal degrees for anchoring on the map.',
                                `bottom_left_lng` decimal(64, 10) NULL DEFAULT NULL COMMENT 'The bottom left longitude coordinates in decimal degrees for anchoring on the map.',
                                `top_right_lat` decimal(64, 10) NULL DEFAULT NULL COMMENT 'The top right latitude coordinates in decimal degrees for anchoring on the map.',
                                `top_right_lng` decimal(64, 10) NULL DEFAULT NULL COMMENT 'The top right longitude coordinates in decimal degrees for anchoring on the map.',
                                `is_legend` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'Flag to indicate whether this is a legend or an actual overlay.',
                                `reference_table` enum('phenotypes','climates') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Optionally, other database items can be linked to this. As an example, an overlay can be linked to a climate variable.',
                                `foreign_id` int(11) NULL DEFAULT NULL COMMENT 'The foreign id within the reference_table of the linked database object.',
                                `dataset_id` int(11) NULL DEFAULT NULL COMMENT 'A dataset id this map overlay is linked to. Useful for providing map overlays for trials data that is not specific to a trait within the dataset.',
                                `recording_date` datetime NULL DEFAULT NULL COMMENT 'A date that is associated with the timepoint when this has been recorded.',
                                `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'The datetime when this database record has been created.',
                                `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'The timestamp when this database record has last been updated.',
                                PRIMARY KEY (`id`) USING BTREE,
                                INDEX `foreign_id`(`foreign_id`) USING BTREE,
                                INDEX `dataset_id`(`dataset_id`) USING BTREE,
                                CONSTRAINT `mapoverlays_ibfk_1` FOREIGN KEY (`dataset_id`) REFERENCES `datasets` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mapoverlays
-- ----------------------------

-- ----------------------------
-- Table structure for maps
-- ----------------------------
DROP TABLE IF EXISTS `maps`;
CREATE TABLE `maps`  (
                         `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                         `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Describes the map.',
                         `description` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'The name of this map.',
                         `visibility` tinyint(1) NOT NULL DEFAULT 1 COMMENT 'Determines if the map is visible to the Germinate interface or hidden.',
                         `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                         `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                         `user_id` int(11) NULL DEFAULT NULL COMMENT 'Foreign key to Gatekeeper users (Gatekeeper users.id).',
                         PRIMARY KEY (`id`) USING BTREE,
                         INDEX `user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Describes genetic maps that have been defined within Germinate.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of maps
-- ----------------------------

-- ----------------------------
-- Table structure for markers
-- ----------------------------
DROP TABLE IF EXISTS `markers`;
CREATE TABLE `markers`  (
                            `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                            `markertype_id` int(11) NOT NULL COMMENT 'Foreign key to locations (locations.id).',
                            `marker_name` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'The name of the marker. This should be a unique name which identifies the marker.',
                            `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.\n',
                            `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                            PRIMARY KEY (`id`) USING BTREE,
                            INDEX `markertype_id`(`markertype_id`) USING BTREE,
                            INDEX `marker_name`(`marker_name`) USING BTREE,
                            CONSTRAINT `markers_ibfk_1` FOREIGN KEY (`markertype_id`) REFERENCES `markertypes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Defines genetic markers within the database and assigns a type (markertypes).' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of markers
-- ----------------------------

-- ----------------------------
-- Table structure for markertypes
-- ----------------------------
DROP TABLE IF EXISTS `markertypes`;
CREATE TABLE `markertypes`  (
                                `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                                `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'Describes the marker type. Markers (markers) have a defined type. This could be AFLP, MicroSat, SNP and so on.',
                                `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                                `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Describes the marker type. Markers (markers) have a defined type. This could be AFLP, MicroSat, SNP and so on. Used to differentiate markers within the markers table and alllows for mixing of marker types on genetic and physical maps.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of markertypes
-- ----------------------------

-- ----------------------------
-- Table structure for mcpd
-- ----------------------------
DROP TABLE IF EXISTS `mcpd`;
CREATE TABLE `mcpd`  (
                         `germinatebase_id` int(11) NOT NULL,
                         `puid` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'Any persistent, unique identifier assigned to the accession so it can be unambiguously referenced at the global level and the information associated with it harvested through automated means. Report one PUID for each accession.',
                         `instcode` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'FAO WIEWS code of the institute where the accession is maintained. The codes consist of the 3-letter ISO 3166 country code of the country where the institute is located plus a number (e.g. COL001). The current set of institute codes is available from http://www.fao.org/wiews. For those institutes not yet having an FAO Code, or for those with \'obsolete\' codes, see \'Common formatting rules (v)\'.',
                         `accenumb` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'This is the unique identifier for accessions within a genebank, and is assigned when a sample is entered into the genebank collection (e.g. \'PI 113869\').',
                         `collnumb` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Original identifier assigned by the collector(s) of the sample, normally composed of the name or initials of the collector(s) followed by a number (e.g. \'FM9909\'). This identifier is essential for identifying duplicates held in different collections.',
                         `collcode` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'FAO WIEWS code of the institute collecting the sample. If the holding institute has collected the material, the collecting institute code (COLLCODE) should be the same as the holding institute code (INSTCODE). Follows INSTCODE standard. Multiple values are separated by a semicolon without space.',
                         `collname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Name of the institute collecting the sample. This descriptor should be used only if COLLCODE cannot be filled because the FAO WIEWS code for this institute is not available. Multiple values are separated by a semicolon without space.',
                         `collinstaddress` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'Address of the institute collecting the sample. This descriptor should be used only if COLLCODE cannot be filled since the FAO WIEWS code for this institute is not available. Multiple values are separated by a semicolon without space.',
                         `collmissid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Identifier of the collecting mission used by the Collecting Institute (4 or 4.1) (e.g. \'CIATFOR052\', \'CN426\').',
                         `genus` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Genus name for taxon. Initial uppercase letter required.',
                         `species` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Specific epithet portion of the scientific name in lowercase letters. Only the following abbreviation is allowed: \'sp.\'',
                         `spauthor` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Provide the authority for the species name.',
                         `subtaxa` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Subtaxon can be used to store any additional taxonomic identifier. The following abbreviations are allowed: \'subsp.\' (for subspecies); \'convar.\' (for convariety); \'var.\' (for variety); \'f.\' (for form); \'Group\' (for \'cultivar group\').',
                         `subtauthor` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Provide the subtaxon authority at the most detailed taxonomic level.',
                         `cropname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Common name of the crop. Example: \'malting barley\', \'macadamia\', \'maïs\'.',
                         `accename` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Either a registered or other designation given to the material received, other than the donor\'s accession number (23) or collecting number (3). First letter uppercase. Multiple names are separated by a semicolon without space. Example: Accession name: Bogatyr;Symphony;Emma.',
                         `acqdate` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Date on which the accession entered the collection where YYYY is the year, MM is the month and DD is the day. Missing data (MM or DD) should be indicated with hyphens or \'00\' [double zero].',
                         `origcty` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '3-letter ISO 3166-1 code of the country in which the sample was originally collected (e.g. landrace, crop wild relative, farmers\' variety), bred or selected (breeding lines, GMOs, segregating populations, hybrids, modern cultivars, etc.).',
                         `collsite` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Location information below the country level that describes where the accession was collected, preferable in English. This might include the distance in kilometres and direction from the nearest town, village or map grid reference point, (e.g. 7 km south of Curitiba in the state of Parana).',
                         `declatitude` decimal(64, 10) NULL DEFAULT NULL COMMENT 'Latitude expressed in decimal degrees. Positive values are North of the Equator; negative values are South of the Equator (e.g. -44.6975).',
                         `latitude` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Degrees (2 digits) minutes (2 digits), and seconds (2 digits) followed by N (North) or S (South) (e.g. 103020S). Every missing digit (minutes or seconds) should be indicated with a hyphen. Leading zeros are required (e.g. 10----S; 011530N; 4531--S).',
                         `declongitude` decimal(64, 10) NULL DEFAULT NULL COMMENT 'Longitude expressed in decimal degrees. Positive values are East of the Greenwich Meridian; negative values are West of the Greenwich Meridian (e.g. +120.9123).',
                         `longitude` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Degrees (3 digits), minutes (2 digits), and seconds (2 digits) followed by E (East) or W (West) (e.g. 0762510W). Every missing digit (minutes or seconds) should be indicated with a hyphen. Leading zeros are required (e.g. 076----W).',
                         `coorduncert` int(11) NULL DEFAULT NULL COMMENT 'Uncertainty associated with the coordinates in metres. Leave the value empty if the uncertainty is unknown.',
                         `coorddatum` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'The geodetic datum or spatial reference system upon which the coordinates given in decimal latitude and decimal longitude are based (e.g. WGS84, ETRS89, NAD83). The GPS uses the WGS84 datum.',
                         `georefmeth` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'The georeferencing method used (GPS, determined from map, gazetteer, or estimated using software). Leave the value empty if georeferencing method is not known.',
                         `elevation` decimal(64, 10) NULL DEFAULT NULL COMMENT 'Elevation of collecting site expressed in metres above sea level. Negative values are allowed.',
                         `colldate` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Collecting date of the sample, where YYYY is the year, MM is the month and DD is the day. Missing data (MM or DD) should be indicated with hyphens or \'00\' [double zero].',
                         `bredcode` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'FAO WIEWS code of the institute that has bred the material. If the holding institute has bred the material, the breeding institute code (BREDCODE) should be the same as the holding institute code (INSTCODE). Follows INSTCODE standard. Multiple values are separated by a semicolon without space.',
                         `bredname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Name of the institute (or person) that bred the material. This descriptor should be used only if BREDCODE cannot be filled because the FAO WIEWS code for this institute is not available. Multiple names are separated by a semicolon without space.',
                         `sampstat` int(11) NULL DEFAULT NULL COMMENT 'The coding scheme proposed can be used at 3 different levels of detail: either by using the general codes (in boldface) such as 100, 200, 300, 400, or by using the more specific codes such as 110, 120, etc.',
                         `ancest` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'Information about either pedigree or other description of ancestral information (e.g. parent variety in case of mutant or selection). For example a pedigree \'Hanna/7*Atlas//Turk/8*Atlas\' or a description \'mutation found in Hanna\', \'selection from Irene\' or \'cross involving amongst others Hanna and Irene\'.',
                         `collsrc` int(11) NULL DEFAULT NULL COMMENT 'The coding scheme proposed can be used at 2 different levels of detail: either by using the general codes (in boldface) such as 10, 20, 30, 40, etc., or by using the more specific codes, such as 11, 12, etc.',
                         `donorcode` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'FAO WIEWS code of the donor institute. Follows INSTCODE standard.',
                         `donorname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Name of the donor institute (or person). This descriptor should be used only if DONORCODE cannot be filled because the FAO WIEWS code for this institute is not available.',
                         `donornumb` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Identifier assigned to an accession by the donor. Follows ACCENUMB standard.',
                         `othernumb` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'Any other identifiers known to exist in other collections for this accession. Use the following format: INSTCODE:ACCENUMB;INSTCODE:identifier;… INSTCODE and identifier are separated by a colon without space. Pairs of INSTCODE and identifier are separated by a semicolon without space. When the institute is not known, the identifier should be preceded by a colon.',
                         `duplsite` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'FAO WIEWS code of the institute(s) where a safety duplicate of the accession is maintained. Multiple values are separated by a semicolon without space. Follows INSTCODE standard.',
                         `duplinstname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Name of the institute where a safety duplicate of the accession is maintained. Multiple values are separated by a semicolon without space.',
                         `storage` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'If germplasm is maintained under different types of storage, multiple choices are allowed, separated by a semicolon (e.g. 20;30). (Refer to FAO/IPGRI Genebank Standards 1994 for details on storage type.)',
                         `mlsstat` int(11) NULL DEFAULT NULL COMMENT 'The status of an accession with regards to the Multilateral System (MLS) of the International Treaty on Plant Genetic Resources for Food and Agriculture. Leave the value empty if the status is not known',
                         `remarks` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'The remarks field is used to add notes or to elaborate on descriptors with value 99 or 999 (= Other). Prefix remarks with the field name they refer to and a colon (:) without space (e.g. COLLSRC:riverside). Distinct remarks referring to different fields are separated by semicolons without space.',
                         `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Date and time when this record was created.',
                         `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Timestamp of the last update to this record.',
                         PRIMARY KEY (`germinatebase_id`) USING BTREE,
                         INDEX `sampstat`(`sampstat`) USING BTREE,
                         INDEX `collsrc`(`collsrc`) USING BTREE,
                         INDEX `mlsstat`(`mlsstat`) USING BTREE,
                         CONSTRAINT `mcpd_ibfk_1` FOREIGN KEY (`germinatebase_id`) REFERENCES `germinatebase` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                         CONSTRAINT `mcpd_ibfk_2` FOREIGN KEY (`sampstat`) REFERENCES `biologicalstatus` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                         CONSTRAINT `mcpd_ibfk_3` FOREIGN KEY (`collsrc`) REFERENCES `collectingsources` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                         CONSTRAINT `mcpd_ibfk_4` FOREIGN KEY (`mlsstat`) REFERENCES `mlsstatus` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mcpd
-- ----------------------------

-- ----------------------------
-- Table structure for mlsstatus
-- ----------------------------
DROP TABLE IF EXISTS `mlsstatus`;
CREATE TABLE `mlsstatus`  (
                              `id` int(11) NOT NULL,
                              `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                              `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                              `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mlsstatus
-- ----------------------------
INSERT INTO `mlsstatus` VALUES (0, 'No (not included)', '2017-10-12 13:19:34', '2017-10-12 13:19:34');
INSERT INTO `mlsstatus` VALUES (1, 'Yes (included)', '2017-10-12 13:19:34', '2017-10-12 13:19:34');
INSERT INTO `mlsstatus` VALUES (99, 'Other (elaborate in REMARKS field, e.g. \'under development\'', '2017-10-12 13:19:34', '2017-10-12 13:19:34');

-- ----------------------------
-- Table structure for news
-- ----------------------------
DROP TABLE IF EXISTS `news`;
CREATE TABLE `news`  (
                         `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                         `newstype_id` int(11) NOT NULL COMMENT 'Foreign key newstypes (newstypes.id).',
                         `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'A title which is used to name this news item. This appears in the Germinate user interface if used.',
                         `content` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'The textual content of this news item.',
                         `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Image to use with this news item.',
                         `image_fit` enum('contain','cover') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'cover' COMMENT 'Determines the css property of the news item image.',
                         `hyperlink` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'HTML hyperlink to use for this news item. This can be a link to another source which contains more information or a link to the original source.',
                         `user_id` int(11) NULL DEFAULT NULL COMMENT 'Foreign key users (users.id).',
                         `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                         `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                         PRIMARY KEY (`id`) USING BTREE,
                         INDEX `news_user_id`(`user_id`) USING BTREE,
                         INDEX `news_updated_on`(`updated_on`) USING BTREE,
                         INDEX `news_type_id`(`newstype_id`) USING BTREE,
                         CONSTRAINT `news_ibfk_1` FOREIGN KEY (`newstype_id`) REFERENCES `newstypes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Holds news items that are displayed within Germinate.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of news
-- ----------------------------

-- ----------------------------
-- Table structure for newstypes
-- ----------------------------
DROP TABLE IF EXISTS `newstypes`;
CREATE TABLE `newstypes`  (
                              `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                              `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Name of the news type.',
                              `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'A longer description of the news type.',
                              `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                              `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Defines the news types which are contained the database. The news types are displayed on the Germinate user interface and are not required if the user interface is not used.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of newstypes
-- ----------------------------
INSERT INTO `newstypes` VALUES (1, 'General', 'General news', NULL, NULL);
INSERT INTO `newstypes` VALUES (2, 'Updates', 'News about updates to the page', NULL, NULL);
INSERT INTO `newstypes` VALUES (3, 'Data', 'News about new data', NULL, NULL);
INSERT INTO `newstypes` VALUES (4, 'Projects', 'News about new projects', NULL, NULL);

-- ----------------------------
-- Table structure for pedigreedefinitions
-- ----------------------------
DROP TABLE IF EXISTS `pedigreedefinitions`;
CREATE TABLE `pedigreedefinitions`  (
                                        `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                                        `dataset_id` int(11) NOT NULL,
                                        `germinatebase_id` int(11) NOT NULL COMMENT 'Foreign key to germinatebase (germinatebase.id).',
                                        `pedigreenotation_id` int(11) NOT NULL COMMENT 'Foreign key to pedigreenotations (pedigreenotations.id).',
                                        `pedigreedescription_id` int(11) NULL DEFAULT NULL,
                                        `definition` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'The pedigree string which is used to represent the germinatebase entry.',
                                        `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                                        `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                                        PRIMARY KEY (`id`) USING BTREE,
                                        INDEX `pedigreedefinitions_ibfk_pedigreenotations`(`pedigreenotation_id`) USING BTREE,
                                        INDEX `pedigreedefinitions_ibfk_germinatebase`(`germinatebase_id`) USING BTREE,
                                        INDEX `pedigreedefinitions_ibfk_3`(`pedigreedescription_id`) USING BTREE,
                                        INDEX `dataset_id`(`dataset_id`) USING BTREE,
                                        CONSTRAINT `pedigreedefinitions_ibfk_1` FOREIGN KEY (`germinatebase_id`) REFERENCES `germinatebase` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                                        CONSTRAINT `pedigreedefinitions_ibfk_2` FOREIGN KEY (`pedigreenotation_id`) REFERENCES `pedigreenotations` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                                        CONSTRAINT `pedigreedefinitions_ibfk_3` FOREIGN KEY (`pedigreedescription_id`) REFERENCES `pedigreedescriptions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                                        CONSTRAINT `pedigreedefinitions_ibfk_4` FOREIGN KEY (`dataset_id`) REFERENCES `datasets` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'This table holds the actual pedigree definition data.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pedigreedefinitions
-- ----------------------------

-- ----------------------------
-- Table structure for pedigreedescriptions
-- ----------------------------
DROP TABLE IF EXISTS `pedigreedescriptions`;
CREATE TABLE `pedigreedescriptions`  (
                                         `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                                         `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'The name of the pedigree.',
                                         `description` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'Describes the pedigree in more detail.',
                                         `author` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Who is responsible for the creation of the pedigree. Attribution should be included in here for pedigree sources.',
                                         `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                                         `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Description of pedigrees. Pedigrees can have a description which details additional information about the pedigree, how it was constructed and who the contact is for the pedigree.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pedigreedescriptions
-- ----------------------------

-- ----------------------------
-- Table structure for pedigreenotations
-- ----------------------------
DROP TABLE IF EXISTS `pedigreenotations`;
CREATE TABLE `pedigreenotations`  (
                                      `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                                      `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Name of the reference notation source.',
                                      `description` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'A longer description about the reference notation source.',
                                      `reference_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Hyperlink to the notation source.',
                                      `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                                      `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Allows additional supporting data to be associated with a pedigree definition such as the contributing data source.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pedigreenotations
-- ----------------------------

-- ----------------------------
-- Table structure for pedigrees
-- ----------------------------
DROP TABLE IF EXISTS `pedigrees`;
CREATE TABLE `pedigrees`  (
                              `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                              `dataset_id` int(11) NOT NULL,
                              `germinatebase_id` int(11) NOT NULL COMMENT 'Foreign key germinatebase (germinatebase.id).',
                              `parent_id` int(11) NOT NULL COMMENT 'Foreign key germinatebase (germinatebase.id). This is the parrent of the individual identified in the germinatebase_id column.',
                              `relationship_type` enum('M','F','OTHER') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'OTHER' COMMENT 'Male or Female parent. Should be recorded as \'M\' (male) or \'F\' (female).',
                              `pedigreedescription_id` int(11) NOT NULL COMMENT 'Foreign key pedigreedescriptions (pedigreedescriptions.id).',
                              `relationship_description` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'Can be used as a meta-data field to describe the relationships if a complex rellationship is required. Examples may include, \'is a complex cross containing\', \'F4 generation\' and so on. This is used by the Helium pedigree visualiztion tool.',
                              `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                              `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                              PRIMARY KEY (`id`) USING BTREE,
                              INDEX `pedigrees_ibfk_germinatebase`(`germinatebase_id`) USING BTREE,
                              INDEX `pedigrees_ibfk_germinatebase_parent`(`parent_id`) USING BTREE,
                              INDEX `pedigrees_ibfk_pedigreedescriptions`(`pedigreedescription_id`) USING BTREE,
                              INDEX `dataset_id`(`dataset_id`) USING BTREE,
                              CONSTRAINT `pedigrees_ibfk_1` FOREIGN KEY (`germinatebase_id`) REFERENCES `germinatebase` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                              CONSTRAINT `pedigrees_ibfk_2` FOREIGN KEY (`parent_id`) REFERENCES `germinatebase` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                              CONSTRAINT `pedigrees_ibfk_3` FOREIGN KEY (`pedigreedescription_id`) REFERENCES `pedigreedescriptions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                              CONSTRAINT `pedigrees_ibfk_4` FOREIGN KEY (`dataset_id`) REFERENCES `datasets` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Holds pedigree definitions. A pedigree is constructed from a series of individial->parent records. This gives a great deal of flexibility in how pedigree networks can be constructed. This table is required for operation with the Helium pedigree viewer.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pedigrees
-- ----------------------------

-- ----------------------------
-- Table structure for phenotypedata
-- ----------------------------
DROP TABLE IF EXISTS `phenotypedata`;
CREATE TABLE `phenotypedata`  (
                                  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                                  `trialsetup_id` int(11) NOT NULL,
                                  `phenotype_id` int(11) NOT NULL DEFAULT 0 COMMENT 'Foreign key phenotypes (phenotype.id).',
                                  `phenotype_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'The phenotype value for this phenotype_id and germinatebase_id combination.',
                                  `recording_date` datetime NULL DEFAULT NULL COMMENT 'Date when the phenotypic result was recorded. Should be formatted \'YYYY-MM-DD HH:MM:SS\' or just \'YYYY-MM-DD\' where a timestamp is not available.',
                                  `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                                  `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                                  PRIMARY KEY (`id`) USING BTREE,
                                  INDEX `phenotype_id`(`phenotype_id`) USING BTREE,
                                  INDEX `trials_query_index`(`phenotype_id`, `recording_date`, `phenotype_value`) USING BTREE,
                                  INDEX `phenotypedata_recording_date`(`recording_date`) USING BTREE,
                                  INDEX `trialsetup_id`(`trialsetup_id`) USING BTREE,
                                  CONSTRAINT `phenotypedata_ibfk_2` FOREIGN KEY (`phenotype_id`) REFERENCES `phenotypes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                                  CONSTRAINT `phenotypedata_ibfk_7` FOREIGN KEY (`trialsetup_id`) REFERENCES `trialsetup` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Contains phenotypic data which has been collected.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of phenotypedata
-- ----------------------------

-- ----------------------------
-- Table structure for phenotypes
-- ----------------------------
DROP TABLE IF EXISTS `phenotypes`;
CREATE TABLE `phenotypes`  (
                               `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                               `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'Phenotype full name.',
                               `short_name` char(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Shortened name for the phenotype. This is used in table columns where space is an issue.',
                               `description` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'Full description of the phenotype. This should contain enough infomation to accurately identify the phenoytpe and how it was recorded.',
                               `datatype` enum('categorical','numeric','text','date') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'text' COMMENT 'Defines the data type of the phenotype. This can be of numeric, text, date or categorical types.',
                               `restrictions` json NULL COMMENT 'A json object describing the restrictions placed on this trait. It is an object containing a field called \"categories\" which is an array of arrays, each describing a categorical scale. Each scale must have the same length as they describe the same categories just using different terms or numbers. The other fields are \"min\" and \"max\" to specify upper and lower limits for numeric traits.',
                               `unit_id` int(11) NULL DEFAULT NULL COMMENT 'Foreign Key to units (units.id).',
                               `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                               `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if changes have been made subsequently to the underlying record.',
                               PRIMARY KEY (`id`) USING BTREE,
                               INDEX `unit_id`(`unit_id`) USING BTREE,
                               CONSTRAINT `phenotypes_ibfk_1` FOREIGN KEY (`unit_id`) REFERENCES `units` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Defines phenoytpes which are held in Germinate.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of phenotypes
-- ----------------------------

-- ----------------------------
-- Table structure for publicationdata
-- ----------------------------
DROP TABLE IF EXISTS `publicationdata`;
CREATE TABLE `publicationdata`  (
                                    `id` int(11) NOT NULL AUTO_INCREMENT,
                                    `foreign_id` int(11) NULL DEFAULT NULL,
                                    `publication_id` int(11) NOT NULL,
                                    `reference_type` enum('database','dataset','germplasm','group','experiment') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'database',
                                    `created_on` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    `updated_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                    PRIMARY KEY (`id`) USING BTREE,
                                    INDEX `publication_id`(`publication_id`) USING BTREE,
                                    CONSTRAINT `publicationdata_ibfk_1` FOREIGN KEY (`publication_id`) REFERENCES `publications` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of publicationdata
-- ----------------------------

-- ----------------------------
-- Table structure for publications
-- ----------------------------
DROP TABLE IF EXISTS `publications`;
CREATE TABLE `publications`  (
                                 `id` int(11) NOT NULL AUTO_INCREMENT,
                                 `doi` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                 `fallback_cache` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
                                 `created_on` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 `updated_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of publications
-- ----------------------------

-- ----------------------------
-- Table structure for schema_version
-- ----------------------------
DROP TABLE IF EXISTS `schema_version`;
CREATE TABLE `schema_version`  (
                                   `installed_rank` int(11) NOT NULL,
                                   `version` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                   `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                   `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                   `script` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                   `checksum` int(11) NULL DEFAULT NULL,
                                   `installed_by` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                   `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   `execution_time` int(11) NOT NULL,
                                   `success` tinyint(1) NOT NULL,
                                   PRIMARY KEY (`installed_rank`) USING BTREE,
                                   INDEX `schema_version_s_idx`(`success`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of schema_version
-- ----------------------------
INSERT INTO `schema_version` VALUES (1, '1', '<< Flyway Baseline >>', 'BASELINE', '<< Flyway Baseline >>', NULL, 'germinate3', '2016-08-22 16:24:04', 0, 1);
INSERT INTO `schema_version` VALUES (2, '3.3.2', 'update', 'SQL', 'V3.3.2__update.sql', 905709629, 'germinate3', '2016-08-22 16:24:04', 111, 1);
INSERT INTO `schema_version` VALUES (3, '3.3.2.1', 'update', 'SQL', 'V3.3.2.1__update.sql', -256506759, 'germinate3', '2016-11-03 15:46:40', 123, 1);
INSERT INTO `schema_version` VALUES (4, '3.3.2.2', 'update', 'SQL', 'V3.3.2.2__update.sql', 508407614, 'germinate3', '2016-11-04 10:31:18', 9, 1);
INSERT INTO `schema_version` VALUES (5, '3.4.0', 'update', 'SQL', 'V3.4.0__update.sql', 771931752, 'germinate3', '2017-01-10 14:23:11', 198, 1);
INSERT INTO `schema_version` VALUES (6, '3.4.0.1', 'update', 'SQL', 'V3.4.0.1__update.sql', -1497522993, 'germinate3', '2017-09-28 15:58:00', 161, 1);
INSERT INTO `schema_version` VALUES (7, '3.5.0', 'update', 'SQL', 'V3.5.0__update.sql', -1130493621, 'germinate3', '2018-03-27 14:29:38', 132, 1);
INSERT INTO `schema_version` VALUES (8, '3.6.0', 'update', 'SQL', 'V3.6.0__update.sql', -576211582, 'germinate3', '2020-01-23 09:46:58', 125, 1);
INSERT INTO `schema_version` VALUES (9, '4.0.0', 'update', 'SQL', 'V4.0.0__update.sql', -1062238208, 'germinate', '2020-04-10 14:14:55', 193, 1);
INSERT INTO `schema_version` VALUES (10, '4.20.06.15', 'update', 'SQL', 'V4.20.06.15__update.sql', -924460307, 'germinate', '2020-06-15 10:41:49', 123, 1);
INSERT INTO `schema_version` VALUES (11, '4.20.10.02', 'update', 'SQL', 'V4.20.10.02__update.sql', -1150563781, 'germinate', '2020-10-05 14:59:18', 43, 1);
INSERT INTO `schema_version` VALUES (12, '4.20.10.30', 'update', 'SQL', 'V4.20.10.30__update.sql', -14100518, 'germinate', '2020-10-30 13:55:49', 43, 1);
INSERT INTO `schema_version` VALUES (13, '4.21.04.09', 'update', 'SQL', 'V4.21.04.09__update.sql', 1453956800, 'germinate', '2021-08-11 16:15:35', 111, 1);
INSERT INTO `schema_version` VALUES (14, '4.21.08.11', 'update', 'SQL', 'V4.21.08.11__update.sql', -587448746, 'germinate', '2021-08-11 16:14:44', 111, 1);
INSERT INTO `schema_version` VALUES (15, '4.21.08.19', 'update', 'SQL', 'V4.21.08.19__update.sql', -71098644, 'germinate', '2021-08-19 15:51:36', 111, 1);
INSERT INTO `schema_version` VALUES (16, '4.21.10.05', 'update', 'SQL', 'V4.21.10.05__update.sql', -1542652149, 'germinate', '2021-10-07 13:07:19', 111, 1);
INSERT INTO `schema_version` VALUES (17, '4.21.11.08', 'update', 'SQL', 'V4.21.11.08__update.sql', -1396782859, 'germinate', '2021-11-09 14:06:47', 111, 1);
INSERT INTO `schema_version` VALUES (18, '4.22.03.08', 'update', 'SQL', 'V4.22.03.08__update.sql', 1266180548, 'germinate', '2022-03-10 11:31:21', 111, 1);
INSERT INTO `schema_version` VALUES (19, '4.22.04.29', 'update', 'SQL', 'V4.22.04.29__update.sql', -1600868952, 'root', '2022-04-29 11:04:44', 86, 1);
INSERT INTO `schema_version` VALUES (20, '4.22.05.04', 'update', 'SQL', 'V4.22.05.04__update.sql', -442883119, 'root', '2022-07-11 18:17:26', 222, 1);
INSERT INTO `schema_version` VALUES (21, '4.22.08.10', 'update', 'SQL', 'V4.22.08.10__update.sql', 1992346541, 'root', '2022-08-10 08:25:59', 100, 1);
INSERT INTO `schema_version` VALUES (22, '4.22.08.18', 'update', 'SQL', 'V4.22.08.18__update.sql', -1881471068, 'root', '2022-08-25 15:18:39', 44, 1);
INSERT INTO `schema_version` VALUES (23, '4.22.08.23', 'update', 'SQL', 'V4.22.08.23__update.sql', -1016121391, 'root', '2022-08-25 15:19:12', 177, 1);
INSERT INTO `schema_version` VALUES (24, '4.22.08.24', 'update', 'SQL', 'V4.22.08.24__update.sql', -943687077, 'root', '2022-08-26 09:39:32', 1058, 1);
INSERT INTO `schema_version` VALUES (25, '4.22.09.02', 'update', 'SQL', 'V4.22.09.02__update.sql', -21851311, 'root', '2022-09-02 14:14:14', 154, 1);
INSERT INTO `schema_version` VALUES (26, '4.22.09.26', 'update', 'SQL', 'V4.22.09.26__update.sql', 2124455446, 'root', '2022-09-26 09:53:37', 45, 1);
INSERT INTO `schema_version` VALUES (27, '4.22.10.03', 'update', 'SQL', 'V4.22.10.03__update.sql', 2025067794, 'root', '2022-10-06 10:39:31', 85, 1);
INSERT INTO `schema_version` VALUES (28, '4.22.10.12', 'update', 'SQL', 'V4.22.10.12__update.sql', 826583756, 'root', '2022-10-24 10:55:05', 212, 1);
INSERT INTO `schema_version` VALUES (29, '4.22.10.31', 'update', 'SQL', 'V4.22.10.31__update.sql', 1643716658, 'root', '2022-11-01 09:33:47', 22, 1);
INSERT INTO `schema_version` VALUES (30, '4.22.11.18', 'update', 'SQL', 'V4.22.11.18__update.sql', -606967356, 'root', '2022-11-21 08:18:43', 45, 1);
INSERT INTO `schema_version` VALUES (31, '4.23.01.09', 'update', 'SQL', 'V4.23.01.09__update.sql', -1410254264, 'root', '2023-01-09 14:11:44', 104, 1);
INSERT INTO `schema_version` VALUES (32, '4.23.02.08', 'update', 'SQL', 'V4.23.02.08__update.sql', -892456639, 'root', '2023-02-08 15:32:07', 121, 1);
INSERT INTO `schema_version` VALUES (33, '4.23.02.16', 'update', 'SQL', 'V4.23.02.16__update.sql', 1048526513, 'root', '2023-02-16 09:07:11', 93, 1);
INSERT INTO `schema_version` VALUES (34, '4.23.08.03', 'update', 'SQL', 'V4.23.08.03__update.sql', -1322793455, 'root', '2024-06-05 15:53:25', 1231, 1);
INSERT INTO `schema_version` VALUES (35, '4.24.02.09', 'update', 'SQL', 'V4.24.02.09__update.sql', -176246237, 'root', '2024-02-09 14:49:04', 1412, 1);

-- ----------------------------
-- Table structure for stories
-- ----------------------------
DROP TABLE IF EXISTS `stories`;
CREATE TABLE `stories`  (
                            `id` int(11) NOT NULL AUTO_INCREMENT,
                            `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                            `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
                            `image_id` int(11) NULL DEFAULT NULL,
                            `requirements` json NULL,
                            `publication_id` int(11) NULL DEFAULT NULL,
                            `featured` tinyint(1) NOT NULL DEFAULT 0,
                            `visibility` tinyint(1) NOT NULL DEFAULT 1,
                            `user_id` int(11) NOT NULL,
                            `created_on` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            `updated_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            PRIMARY KEY (`id`) USING BTREE,
                            INDEX `publication_id`(`publication_id`) USING BTREE,
                            CONSTRAINT `stories_ibfk_1` FOREIGN KEY (`publication_id`) REFERENCES `publications` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of stories
-- ----------------------------

-- ----------------------------
-- Table structure for storysteps
-- ----------------------------
DROP TABLE IF EXISTS `storysteps`;
CREATE TABLE `storysteps`  (
                               `id` int(11) NOT NULL AUTO_INCREMENT,
                               `story_id` int(11) NOT NULL,
                               `story_index` int(11) NOT NULL,
                               `page_config` json NOT NULL,
                               `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                               `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
                               `image_id` int(11) NULL DEFAULT NULL,
                               `created_on` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               `updated_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                               PRIMARY KEY (`id`) USING BTREE,
                               INDEX `story_id`(`story_id`) USING BTREE,
                               INDEX `image_id`(`image_id`) USING BTREE,
                               CONSTRAINT `storysteps_ibfk_1` FOREIGN KEY (`story_id`) REFERENCES `stories` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                               CONSTRAINT `storysteps_ibfk_2` FOREIGN KEY (`image_id`) REFERENCES `images` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of storysteps
-- ----------------------------

-- ----------------------------
-- Table structure for synonyms
-- ----------------------------
DROP TABLE IF EXISTS `synonyms`;
CREATE TABLE `synonyms`  (
                             `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.\n',
                             `foreign_id` int(11) NOT NULL COMMENT 'Foreign key to target table (l[targettable].id).',
                             `synonymtype_id` int(11) NOT NULL COMMENT 'Foreign key to synonymtypes (synonymnstypes.id).',
                             `synonyms` json NULL COMMENT 'The synonyms as a json array.',
                             `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                             `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                             PRIMARY KEY (`id`) USING BTREE,
                             INDEX `synonyms_ibfk_synonymtypes`(`synonymtype_id`) USING BTREE,
                             INDEX `foreign_id`(`foreign_id`) USING BTREE,
                             CONSTRAINT `synonyms_ibfk_1` FOREIGN KEY (`synonymtype_id`) REFERENCES `synonymtypes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Allows the definition of synonyms for entries such as germinatebase entries or marker names.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of synonyms
-- ----------------------------

-- ----------------------------
-- Table structure for synonymtypes
-- ----------------------------
DROP TABLE IF EXISTS `synonymtypes`;
CREATE TABLE `synonymtypes`  (
                                 `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                                 `target_table` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'The target table.',
                                 `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Name of the synonym type.',
                                 `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Description of the type.',
                                 `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                                 `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Synonym type definitions.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of synonymtypes
-- ----------------------------
INSERT INTO `synonymtypes` VALUES (1, 'germinatebase', 'Accessions', 'Accession synonyms', NULL, NULL);
INSERT INTO `synonymtypes` VALUES (2, 'markers', 'Markers', 'Marker synonyms', NULL, NULL);
INSERT INTO `synonymtypes` VALUES (4, 'phenotypes', 'Phenotypes', 'Phenotype synonyms', '2018-11-06 13:46:11', '2018-11-06 13:46:11');

-- ----------------------------
-- Table structure for taxonomies
-- ----------------------------
DROP TABLE IF EXISTS `taxonomies`;
CREATE TABLE `taxonomies`  (
                               `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                               `genus` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'Genus name for the species.',
                               `species` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'Species name in lowercase.',
                               `subtaxa` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Subtaxa name.',
                               `species_author` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'also known as spauthor in the Multi Crop Passport Descriptors (MCPD V2 2012). Describes the authority for the species name.',
                               `subtaxa_author` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'also known as subtauthor in the Multi Crop Passport Descriptors (MCPD V2 2012).',
                               `cropname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'The name of the crop. This should be the common name. Examples would include barley, maize, wheat, rice and so on.',
                               `ploidy` int(11) NULL DEFAULT NULL COMMENT 'Defines the ploidy level for the species. Use numbers to reference ploidy for example diploid = 2, tetraploid = 4.',
                               `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                               `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'The species table holds information relating to the species that are deinfed within a particular Germinate instance including common names and ploidy levels.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of taxonomies
-- ----------------------------

-- ----------------------------
-- Table structure for treatments
-- ----------------------------
DROP TABLE IF EXISTS `treatments`;
CREATE TABLE `treatments`  (
                               `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                               `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'The name which defines the treatment.',
                               `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'A longer descripiton of the treatment. This should include enough information to be able to identify what the treatment was and why it was applied.',
                               `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                               `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if changes have been made subsequently to the underlying record.',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'For trials data the treatment is used to distinguish between factors. Examples would include whether the trial was treated with fungicides or not.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of treatments
-- ----------------------------

-- ----------------------------
-- Table structure for trialseries
-- ----------------------------
DROP TABLE IF EXISTS `trialseries`;
CREATE TABLE `trialseries`  (
                                `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                                `seriesname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'The description of the trial series name.',
                                `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                                `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if changes have been made subsequently to the underlying record.',
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Holds the names of trial series. Trial series define the name of the trial to which trials data is associated. Examples would include the overarching project.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of trialseries
-- ----------------------------

-- ----------------------------
-- Table structure for trialsetup
-- ----------------------------
DROP TABLE IF EXISTS `trialsetup`;
CREATE TABLE `trialsetup`  (
                               `id` int(11) NOT NULL AUTO_INCREMENT,
                               `germinatebase_id` int(11) NOT NULL,
                               `dataset_id` int(11) NOT NULL,
                               `location_id` int(11) NULL DEFAULT NULL,
                               `treatment_id` int(11) NULL DEFAULT NULL,
                               `trialseries_id` int(11) NULL DEFAULT NULL,
                               `block` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1',
                               `rep` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1',
                               `trial_row` smallint(6) NULL DEFAULT NULL,
                               `trial_column` smallint(6) NULL DEFAULT NULL,
                               `latitude` decimal(64, 10) NULL DEFAULT NULL,
                               `longitude` decimal(64, 10) NULL DEFAULT NULL,
                               `elevation` decimal(64, 10) NULL DEFAULT NULL,
                               `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP,
                               `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                               PRIMARY KEY (`id`) USING BTREE,
                               INDEX `germinatebase_id`(`germinatebase_id`) USING BTREE,
                               INDEX `dataset_id`(`dataset_id`) USING BTREE,
                               INDEX `location_id`(`location_id`) USING BTREE,
                               INDEX `treatment_id`(`treatment_id`) USING BTREE,
                               INDEX `trialseries_id`(`trialseries_id`) USING BTREE,
                               CONSTRAINT `trialsetup_ibfk_1` FOREIGN KEY (`germinatebase_id`) REFERENCES `germinatebase` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                               CONSTRAINT `trialsetup_ibfk_2` FOREIGN KEY (`dataset_id`) REFERENCES `datasets` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                               CONSTRAINT `trialsetup_ibfk_3` FOREIGN KEY (`location_id`) REFERENCES `locations` (`id`) ON DELETE SET NULL ON UPDATE SET NULL,
                               CONSTRAINT `trialsetup_ibfk_4` FOREIGN KEY (`treatment_id`) REFERENCES `treatments` (`id`) ON DELETE SET NULL ON UPDATE SET NULL,
                               CONSTRAINT `trialsetup_ibfk_5` FOREIGN KEY (`trialseries_id`) REFERENCES `trialseries` (`id`) ON DELETE SET NULL ON UPDATE SET NULL
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of trialsetup
-- ----------------------------

-- ----------------------------
-- Table structure for units
-- ----------------------------
DROP TABLE IF EXISTS `units`;
CREATE TABLE `units`  (
                          `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
                          `unit_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'The name of the unit. This should be the name of the unit in full.',
                          `unit_abbreviation` char(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'This should be the unit abbreviation.',
                          `unit_description` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'A description of the unit. If the unit is not a standard SI unit then it is beneficial to have a description which explains what the unit it, how it is derived and any other information which would help identifiy it.',
                          `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                          `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if changes have been made subsequently to the underlying record.',
                          PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'The \'units\' table holds descriptions of the various units that are used in the Germinate database. Examples of these would include International System of Units (SI) base units: kilogram, meter, second, ampere, kelvin, candela and mole but can include any units that are required.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of units
-- ----------------------------

-- ----------------------------
-- Table structure for userfeedback
-- ----------------------------
DROP TABLE IF EXISTS `userfeedback`;
CREATE TABLE `userfeedback`  (
                                 `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Auto-incremented primary key.',
                                 `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Feedback content.',
                                 `image` mediumblob NULL COMMENT 'Optional interface screenshot.',
                                 `page_url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'The URL of the page the user was looking at.',
                                 `user_id` int(11) NULL DEFAULT NULL COMMENT 'Optional user id if user was logged in.',
                                 `contact_email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Contact email address.',
                                 `feedback_type` enum('question','data_error','general','bug','feature_request') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'general' COMMENT 'The type of feedback.',
                                 `severity` enum('low','medium','high') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'medium' COMMENT 'The estimated severity of the issue.',
                                 `is_new` tinyint(1) NOT NULL DEFAULT 1 COMMENT 'Indicates whether this is new feedback or has been seen before.',
                                 `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When this database record has been created.',
                                 `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When this database record has last been updated.',
                                 PRIMARY KEY (`id`) USING BTREE,
                                 INDEX `is_new`(`is_new`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of userfeedback
-- ----------------------------

-- ----------------------------
-- Table structure for usergroupmembers
-- ----------------------------
DROP TABLE IF EXISTS `usergroupmembers`;
CREATE TABLE `usergroupmembers`  (
                                     `id` int(11) NOT NULL AUTO_INCREMENT,
                                     `user_id` int(11) NOT NULL,
                                     `usergroup_id` int(11) NOT NULL,
                                     `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                                     `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if changes have been made subsequently to the underlying record.',
                                     PRIMARY KEY (`id`) USING BTREE,
                                     INDEX `usergroup_id`(`usergroup_id`) USING BTREE,
                                     CONSTRAINT `usergroupmembers_ibfk_1` FOREIGN KEY (`usergroup_id`) REFERENCES `usergroups` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of usergroupmembers
-- ----------------------------

-- ----------------------------
-- Table structure for usergroups
-- ----------------------------
DROP TABLE IF EXISTS `usergroups`;
CREATE TABLE `usergroups`  (
                               `id` int(11) NOT NULL AUTO_INCREMENT,
                               `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'The name of the user group.',
                               `description` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'A description of the user group.',
                               `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
                               `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if changes have been made subsequently to the underlying record.',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of usergroups
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
