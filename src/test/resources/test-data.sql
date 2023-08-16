INSERT IGNORE INTO `user` VALUES 
    (1, 'super-admin', 'super-admin', 'surname', 'super-admin', '$2a$08$c2GKYEYoUVXH1Yb8GXVXVu66ltPvbZgLMcVSXRH.LgZNF/YeaYB8m', 'super-admin@nomail.nomail', 'en', 'UTC', 'ADMIN', 0, 0, null)
;

INSERT IGNORE INTO `client_access` VALUES
    (1, 'test', 'test', 'test-description','test', '$2a$04$9JXeMn4nIHtHUSujpgTiqe11Y./qM/LdiMeaSDbTTWBI/zkHWAfEi', 'super-admin', 0, 0, null)
;

INSERT IGNORE INTO `seb_group` VALUES
    (1, 'test_group', 'test_group', 'test-description', 'super-admin', 0, 0, null)
;

INSERT IGNORE INTO `session` VALUES
    (1, 1, 'session_uuid', 0, 'student1', '111.111.111.111', 'mst1', 'Win', '3.5.0', 0, 0, null)
;

