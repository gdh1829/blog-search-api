-- create table BlogSearchPriority(
--    `serverId` bigint(20) NOT NULL AUTO_INCREMENT,
--    `version` bigint(20) NOT NULL,
--    `createdTime` datetime NOT NULL,
--    `updatedTime` datetime NOT NULL,
--    `source` varchar(255) NOT NULL UNIQUE,
--    `use` bit(1) NOT NULL,
--    `priority` int(2) NOT NULL,
--    PRIMARY KEY(`serverId`)
-- );
CREATE INDEX `BlogSearchPriority_source_idx` ON `BlogSearchPriority` (`source`);

-- create table KeywordStatistics(
--    `keyword` varchar(255) NOT NULL,
--    `createdTime` datetime NOT NULL,
--    `updatedTime` datetime NOT NULL,
--    `deletedTime` datetime NULL,
--    `searchCount` bigint(20) NOT NULL,
--    PRIMARY KEY(`keyword`)
-- );

CREATE INDEX `KeywordStatistics_searchCount_idx` ON `keywordStatistics` (`searchCount`, `updatedTime`);

-- create TABLE shedlock(
--  name VARCHAR(64) NOT NULL,
--  lock_until TIMESTAMP(3) NOT NULL,
--  locked_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
--  locked_by VARCHAR(255) NOT NULL,
--  PRIMARY KEY (name)
-- );
