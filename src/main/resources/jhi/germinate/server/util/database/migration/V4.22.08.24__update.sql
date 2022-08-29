/* Create a new dedicated MCPD table */
CREATE TABLE `mcpd` (
  `germinatebase_id` int NOT NULL,
  `puid` text NULL COMMENT 'Any persistent, unique identifier assigned to the accession so it can be unambiguously referenced at the global level and the information associated with it harvested through automated means. Report one PUID for each accession.',
  `instcode` varchar(255) NULL COMMENT 'FAO WIEWS code of the institute where the accession is maintained. The codes consist of the 3-letter ISO 3166 country code of the country where the institute is located plus a number (e.g. COL001). The current set of institute codes is available from http://www.fao.org/wiews. For those institutes not yet having an FAO Code, or for those with \'obsolete\' codes, see \'Common formatting rules (v)\'.',
  `accenumb` varchar(255) NOT NULL COMMENT 'This is the unique identifier for accessions within a genebank, and is assigned when a sample is entered into the genebank collection (e.g. \'PI 113869\').',
  `collnumb` varchar(255) NULL COMMENT 'Original identifier assigned by the collector(s) of the sample, normally composed of the name or initials of the collector(s) followed by a number (e.g. \'FM9909\'). This identifier is essential for identifying duplicates held in different collections.',
  `collcode` varchar(255) NULL COMMENT 'FAO WIEWS code of the institute collecting the sample. If the holding institute has collected the material, the collecting institute code (COLLCODE) should be the same as the holding institute code (INSTCODE). Follows INSTCODE standard. Multiple values are separated by a semicolon without space.',
  `collname` varchar(255) NULL COMMENT 'Name of the institute collecting the sample. This descriptor should be used only if COLLCODE cannot be filled because the FAO WIEWS code for this institute is not available. Multiple values are separated by a semicolon without space.',
  `collinstaddress` text NULL COMMENT 'Address of the institute collecting the sample. This descriptor should be used only if COLLCODE cannot be filled since the FAO WIEWS code for this institute is not available. Multiple values are separated by a semicolon without space.',
  `collmissid` varchar(255) NULL COMMENT 'Identifier of the collecting mission used by the Collecting Institute (4 or 4.1) (e.g. \'CIATFOR052\', \'CN426\').',
  `genus` varchar(255) NULL COMMENT 'Genus name for taxon. Initial uppercase letter required.',
  `species` varchar(255) NULL COMMENT 'Specific epithet portion of the scientific name in lowercase letters. Only the following abbreviation is allowed: \'sp.\'',
  `spauthor` varchar(255) NULL COMMENT 'Provide the authority for the species name.',
  `subtaxa` varchar(255) NULL COMMENT 'Subtaxon can be used to store any additional taxonomic identifier. The following abbreviations are allowed: \'subsp.\' (for subspecies); \'convar.\' (for convariety); \'var.\' (for variety); \'f.\' (for form); \'Group\' (for \'cultivar group\').',
  `subtauthor` varchar(255) NULL COMMENT 'Provide the subtaxon authority at the most detailed taxonomic level.',
  `cropname` varchar(255) NULL COMMENT 'Common name of the crop. Example: \'malting barley\', \'macadamia\', \'maïs\'.',
  `accename` varchar(255) NULL COMMENT 'Either a registered or other designation given to the material received, other than the donor\'s accession number (23) or collecting number (3). First letter uppercase. Multiple names are separated by a semicolon without space. Example: Accession name: Bogatyr;Symphony;Emma.',
  `acqdate` varchar(255) NULL COMMENT 'Date on which the accession entered the collection where YYYY is the year, MM is the month and DD is the day. Missing data (MM or DD) should be indicated with hyphens or \'00\' [double zero].',
  `origcty` varchar(255) NULL COMMENT '3-letter ISO 3166-1 code of the country in which the sample was originally collected (e.g. landrace, crop wild relative, farmers\' variety), bred or selected (breeding lines, GMOs, segregating populations, hybrids, modern cultivars, etc.).',
  `collsite` varchar(255) NULL COMMENT 'Location information below the country level that describes where the accession was collected, preferable in English. This might include the distance in kilometres and direction from the nearest town, village or map grid reference point, (e.g. 7 km south of Curitiba in the state of Parana).',
  `declatitude` decimal(64, 10) NULL COMMENT 'Latitude expressed in decimal degrees. Positive values are North of the Equator; negative values are South of the Equator (e.g. -44.6975).',
  `latitude` varchar(255) NULL COMMENT 'Degrees (2 digits) minutes (2 digits), and seconds (2 digits) followed by N (North) or S (South) (e.g. 103020S). Every missing digit (minutes or seconds) should be indicated with a hyphen. Leading zeros are required (e.g. 10----S; 011530N; 4531--S).',
  `declongitude` decimal(64, 10) NULL COMMENT 'Longitude expressed in decimal degrees. Positive values are East of the Greenwich Meridian; negative values are West of the Greenwich Meridian (e.g. +120.9123).',
  `longitude` varchar(255) NULL COMMENT 'Degrees (3 digits), minutes (2 digits), and seconds (2 digits) followed by E (East) or W (West) (e.g. 0762510W). Every missing digit (minutes or seconds) should be indicated with a hyphen. Leading zeros are required (e.g. 076----W).',
  `coorduncert` int(11) NULL COMMENT 'Uncertainty associated with the coordinates in metres. Leave the value empty if the uncertainty is unknown.',
  `coorddatum` varchar(255) NULL COMMENT 'The geodetic datum or spatial reference system upon which the coordinates given in decimal latitude and decimal longitude are based (e.g. WGS84, ETRS89, NAD83). The GPS uses the WGS84 datum.',
  `georefmeth` varchar(255) NULL COMMENT 'The georeferencing method used (GPS, determined from map, gazetteer, or estimated using software). Leave the value empty if georeferencing method is not known.',
  `elevation` decimal(64, 10) NULL COMMENT 'Elevation of collecting site expressed in metres above sea level. Negative values are allowed.',
  `colldate` varchar(255) NULL COMMENT 'Collecting date of the sample, where YYYY is the year, MM is the month and DD is the day. Missing data (MM or DD) should be indicated with hyphens or \'00\' [double zero].',
  `bredcode` varchar(255) NULL COMMENT 'FAO WIEWS code of the institute that has bred the material. If the holding institute has bred the material, the breeding institute code (BREDCODE) should be the same as the holding institute code (INSTCODE). Follows INSTCODE standard. Multiple values are separated by a semicolon without space.',
  `bredname` varchar(255) NULL COMMENT 'Name of the institute (or person) that bred the material. This descriptor should be used only if BREDCODE cannot be filled because the FAO WIEWS code for this institute is not available. Multiple names are separated by a semicolon without space.',
  `sampstat` int(11) NULL COMMENT 'The coding scheme proposed can be used at 3 different levels of detail: either by using the general codes (in boldface) such as 100, 200, 300, 400, or by using the more specific codes such as 110, 120, etc.',
  `ancest` text NULL COMMENT 'Information about either pedigree or other description of ancestral information (e.g. parent variety in case of mutant or selection). For example a pedigree \'Hanna/7*Atlas//Turk/8*Atlas\' or a description \'mutation found in Hanna\', \'selection from Irene\' or \'cross involving amongst others Hanna and Irene\'.',
  `collsrc` int(11) NULL COMMENT 'The coding scheme proposed can be used at 2 different levels of detail: either by using the general codes (in boldface) such as 10, 20, 30, 40, etc., or by using the more specific codes, such as 11, 12, etc.',
  `donorcode` varchar(255) NULL COMMENT 'FAO WIEWS code of the donor institute. Follows INSTCODE standard.',
  `donorname` varchar(255) NULL COMMENT 'Name of the donor institute (or person). This descriptor should be used only if DONORCODE cannot be filled because the FAO WIEWS code for this institute is not available.',
  `donornumb` varchar(255) NULL COMMENT 'Identifier assigned to an accession by the donor. Follows ACCENUMB standard.',
  `othernumb` text NULL COMMENT 'Any other identifiers known to exist in other collections for this accession. Use the following format: INSTCODE:ACCENUMB;INSTCODE:identifier;… INSTCODE and identifier are separated by a colon without space. Pairs of INSTCODE and identifier are separated by a semicolon without space. When the institute is not known, the identifier should be preceded by a colon.',
  `duplsite` varchar(255) NULL COMMENT 'FAO WIEWS code of the institute(s) where a safety duplicate of the accession is maintained. Multiple values are separated by a semicolon without space. Follows INSTCODE standard.',
  `duplinstname` varchar(255) NULL COMMENT 'Name of the institute where a safety duplicate of the accession is maintained. Multiple values are separated by a semicolon without space.',
  `storage` varchar(255) NULL COMMENT 'If germplasm is maintained under different types of storage, multiple choices are allowed, separated by a semicolon (e.g. 20;30). (Refer to FAO/IPGRI Genebank Standards 1994 for details on storage type.)',
  `mlsstat` int(11) NULL COMMENT 'The status of an accession with regards to the Multilateral System (MLS) of the International Treaty on Plant Genetic Resources for Food and Agriculture. Leave the value empty if the status is not known',
  `remarks` text NULL COMMENT 'The remarks field is used to add notes or to elaborate on descriptors with value 99 or 999 (= Other). Prefix remarks with the field name they refer to and a colon (:) without space (e.g. COLLSRC:riverside). Distinct remarks referring to different fields are separated by semicolons without space.',
  `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Date and time when this record was created.',
  `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Timestamp of the last update to this record.',
  PRIMARY KEY (`germinatebase_id`),
  FOREIGN KEY (`germinatebase_id`) REFERENCES `germinatebase` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`sampstat`) REFERENCES `biologicalstatus` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`collsrc`) REFERENCES `collectingsources` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`mlsstat`) REFERENCES `mlsstatus` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

/* Fill the MCPD table with information from all over the place */
INSERT INTO `mcpd`
    SELECT `g`.`id`,
    `g`.`puid`,
    `i`.`code`,
    `g`.`name`,
    `g`.`collnumb`,
    `g`.`collcode`,
    `g`.`collname`,
    `i`.`address`,
    `g`.`collmissid`,
    `t`.`genus`,
    `t`.`species`,
    `t`.`species_author`,
    `t`.`subtaxa`,
    `t`.`subtaxa_author`,
    `t`.`cropname`,
    `g`.`number`,
    `g`.`acqdate`,
    `c`.`country_code3`,
    `l`.`site_name`,
    null,
    `l`.`latitude`,
    null,
    `l`.`longitude`,
    `l`.`coordinate_uncertainty`,
    `l`.`coordinate_datum`,
    `l`.`georeferencing_method`,
    `l`.`elevation`,
    `g`.`colldate`,
    `g`.`breeders_code`,
    `g`.`breeders_name`,
    `g`.`biologicalstatus_id`,
    (SELECT `definition` from `pedigreedefinitions` as `p` left join `datasets` as `d` on `d`.`id` = `p`.`dataset_id` and `d`.`name` = 'MCPD' WHERE `p`.`germinatebase_id` = `g`.`id` limit 1),
    `g`.`collsrc_id`,
    `g`.`donor_code`,
    `g`.`donor_name`,
    `g`.`donor_number`,
    `g`.`othernumb`,
    `g`.`duplsite`,
    `g`.`duplinstname`,
    GROUP_CONCAT(`s`.`id` SEPARATOR ';'),
    `g`.`mlsstatus_id`,
    (SELECT `value` from `attributes` as `a` left join `attributedata` as `ad` on `a`.`id` = `ad`.`attribute_id` and `a`.`target_table` = 'germinatebase' and `a`.`name` = 'Remarks' where `ad`.`foreign_id` = `g`.`id` limit 1),
    `g`.`created_on`,
    `g`.`updated_on`
    FROM `germinatebase` as `g`
    left join `institutions` as `i` on `i`.`id` = `g`.`institution_id`
    left join `taxonomies` as `t` on `t`.`id` = `g`.`taxonomy_id`
    left join `locations` as `l` on `l`.`id` = `g`.`location_id`
    left join `countries` as `c` on `c`.`id` = `l`.`country_id`
    left join `storagedata` as `sd` on `sd`.`germinatebase_id` = `g`.`id`
    left join `storage` as `s` on `s`.`id` = `sd`.`storage_id`
    group by `g`.`id`;

/* Remove foreign keys so we can drop the columns */
ALTER TABLE `germinatebase` DROP FOREIGN KEY `germinatebase_ibfk_biologicalstatus`;
ALTER TABLE `germinatebase` DROP FOREIGN KEY `germinatebase_ibfk_collsrc`;
ALTER TABLE `germinatebase` DROP FOREIGN KEY `germinatebase_ibfk_mlsstatus`;

/* Remove no longer required columns */
ALTER TABLE `germinatebase`
    DROP COLUMN `breeders_code`,
    DROP COLUMN `breeders_name`,
    DROP COLUMN `donor_code`,
    DROP COLUMN `donor_name`,
    DROP COLUMN `donor_number`,
    DROP COLUMN `acqdate`,
    DROP COLUMN `collnumb`,
    DROP COLUMN `colldate`,
    DROP COLUMN `collcode`,
    DROP COLUMN `collname`,
    DROP COLUMN `collmissid`,
    DROP COLUMN `othernumb`,
    DROP COLUMN `duplsite`,
    DROP COLUMN `duplinstname`,
    DROP COLUMN `mlsstatus_id`,
    DROP COLUMN `puid`,
    DROP COLUMN `biologicalstatus_id`,
    DROP COLUMN `collsrc_id`;

/* Create a new linking table between germplasm and institutions of different type */
CREATE TABLE `germplasminstitutions`  (
  `germinatebase_id` int(11) NOT NULL,
  `institution_id` int(11) NOT NULL,
  `type` enum('collection','maintenance','breeding','duplicate','donor') NOT NULL DEFAULT 'maintenance',
  `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`germinatebase_id`, `institution_id`, `type`),
  FOREIGN KEY (`germinatebase_id`) REFERENCES `germinatebase` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`institution_id`) REFERENCES `institutions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

/* Create a new temp table with all the different institution types from the MCPD */
DROP TABLE IF EXISTS `temp_institutions`;
CREATE TABLE `temp_institutions` ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci AS SELECT DISTINCT CAST(null AS UNSIGNED) as `institution_id`, `code`, `name`, `address`, `type` FROM (
SELECT DISTINCT `instcode` as `code`, 'N/A' as `name`, null as `address`, 'maintenance' as `type` FROM `mcpd` WHERE NOT ISNULL(`instcode`)
UNION
SELECT DISTINCT `collcode` as `code`, COALESCE(`collname`, 'N/A') as `name`, `collinstaddress` as `address`, 'collection' as `type` FROM `mcpd` WHERE NOT ISNULL(`collcode`) OR NOT ISNULL(`collname`) OR NOT ISNULL(`collinstaddress`)
UNION
SELECT DISTINCT `bredcode` as `code`, COALESCE(`bredname`, 'N/A') as `name`, null as `address`, 'breeding' as `type` FROM `mcpd` WHERE NOT ISNULL(`bredcode`) OR NOT ISNULL(`bredname`)
UNION
SELECT DISTINCT `donorcode` as `code`, COALESCE(`donorname`, 'N/A') as `name`, null as `address`, 'donor' as `type` FROM `mcpd` WHERE NOT ISNULL(`donorcode`) OR NOT ISNULL(`donorname`)
UNION
SELECT DISTINCT `duplsite` as `code`, COALESCE(`duplinstname`, 'N/A') as `name`, null as `address`, 'duplicate' as `type` FROM `mcpd` WHERE NOT ISNULL(`duplsite`) OR NOT ISNULL(`duplinstname`)
) inst_temp;

/* Delete anything that already exists */
DELETE FROM `temp_institutions` WHERE EXISTS (SELECT 1 FROM `institutions` WHERE `institutions`.`code` COLLATE utf8mb4_unicode_ci <=> `temp_institutions`.`code` AND `institutions`.`name` COLLATE utf8mb4_unicode_ci <=> `temp_institutions`.`name` AND `institutions`.`address` COLLATE utf8mb4_unicode_ci <=> `temp_institutions`.`address`);

/* Now insert the "new" institutions into the table */
INSERT INTO `institutions` (`code`, `name`, `address`) SELECT `code`, `name`, `address` FROM `temp_institutions`;

/* Update the temp table with the newly inserted ids */
UPDATE `temp_institutions` SET `institution_id` = (SELECT `id` FROM `institutions` WHERE `institutions`.`code` COLLATE utf8mb4_unicode_ci <=> `temp_institutions`.`code` AND `institutions`.`name` COLLATE utf8mb4_unicode_ci <=> `temp_institutions`.`name` AND `institutions`.`address` COLLATE utf8mb4_unicode_ci <=> `temp_institutions`.`address`);

/* Now insert the mapping between germplasm and institution with the corresponding type */
INSERT INTO `germplasminstitutions` ( `germinatebase_id`, `institution_id`, `type` ) SELECT `germinatebase_id`, `institution_id`, `type` FROM ( SELECT `mcpd`.`germinatebase_id` AS `germinatebase_id`, `ti`.`institution_id` AS `institution_id`, 'maintenance' AS `type` FROM `mcpd` LEFT JOIN `temp_institutions` AS `ti` ON `ti`.`type` = 'maintenance' AND `ti`.`code` <=> `mcpd`.`instcode` ) AS maintenance_table WHERE `germinatebase_id` IS NOT NULL AND `institution_id` IS NOT NULL;
INSERT INTO `germplasminstitutions` ( `germinatebase_id`, `institution_id`, `type` ) SELECT `germinatebase_id`, `institution_id`, `type` FROM ( SELECT `mcpd`.`germinatebase_id` AS `germinatebase_id`, `ti`.`institution_id` AS `institution_id`, 'collection' AS `type` FROM `mcpd` LEFT JOIN `temp_institutions` AS `ti` ON `ti`.`type` = 'collection' AND `ti`.`code` <=> `mcpd`.`collcode` AND `ti`.`address` <=> `mcpd`.`collinstaddress`) AS collection_table WHERE `germinatebase_id` IS NOT NULL AND `institution_id` IS NOT NULL;
INSERT INTO `germplasminstitutions` ( `germinatebase_id`, `institution_id`, `type` ) SELECT `germinatebase_id`, `institution_id`, `type` FROM ( SELECT `mcpd`.`germinatebase_id` AS `germinatebase_id`, `ti`.`institution_id` AS `institution_id`, 'breeding' AS `type` FROM `mcpd` LEFT JOIN `temp_institutions` AS `ti` ON `ti`.`type` = 'breeding' AND `ti`.`code` <=> `mcpd`.`bredcode` AND COALESCE(`mcpd`.`bredname`, 'N/A') <=> `ti`.`name` AND `ti`.`address` <=> `mcpd`.`collinstaddress`) AS breeding_table WHERE `germinatebase_id` IS NOT NULL AND `institution_id` IS NOT NULL;
INSERT INTO `germplasminstitutions` ( `germinatebase_id`, `institution_id`, `type` ) SELECT `germinatebase_id`, `institution_id`, `type` FROM ( SELECT `mcpd`.`germinatebase_id` AS `germinatebase_id`, `ti`.`institution_id` AS `institution_id`, 'donor' AS `type` FROM `mcpd` LEFT JOIN `temp_institutions` AS `ti` ON `ti`.`type` = 'donor' AND `ti`.`code` <=> `mcpd`.`donorcode` AND COALESCE(`mcpd`.`donorname`, 'N/A') <=> `ti`.`name` AND `ti`.`address` <=> `mcpd`.`collinstaddress`) AS donor_table WHERE `germinatebase_id` IS NOT NULL AND `institution_id` IS NOT NULL;
INSERT INTO `germplasminstitutions` ( `germinatebase_id`, `institution_id`, `type` ) SELECT `germinatebase_id`, `institution_id`, `type` FROM ( SELECT `mcpd`.`germinatebase_id` AS `germinatebase_id`, `ti`.`institution_id` AS `institution_id`, 'duplicate' AS `type` FROM `mcpd` LEFT JOIN `temp_institutions` AS `ti` ON `ti`.`type` = 'duplicate' AND `ti`.`code` <=> `mcpd`.`duplsite` AND COALESCE(`mcpd`.`duplinstname`, 'N/A') <=> `ti`.`name` AND `ti`.`address` <=> `mcpd`.`collinstaddress`) AS duplicate_table WHERE `germinatebase_id` IS NOT NULL AND `institution_id` IS NOT NULL;

/* Delete the temp table */
DROP TABLE IF EXISTS `temp_institutions`;

INSERT INTO `germplasminstitutions` (`germinatebase_id`, `institution_id`, `type`) SELECT `germinatebase`.`id`, `germinatebase`.`institution_id`, 'maintenance' FROM `germinatebase` WHERE NOT ISNULL(`germinatebase`.`institution_id`) AND NOT EXISTS (SELECT 1 FROM `germplasminstitutions` `gi` WHERE `gi`.`germinatebase_id` = `germinatebase`.`id` AND `gi`.`institution_id` = `germinatebase`.`institution_id` AND `gi`.`type` = 'maintenance');

ALTER TABLE `germinatebase` DROP FOREIGN KEY `germinatebase_ibfk_institution`;

ALTER TABLE `germinatebase`
    DROP COLUMN `institution_id`;

DROP TABLE `storagedata`;
DROP TABLE `storage`;

DROP TABLE IF EXISTS `experimenttypes`;