INSERT IGNORE INTO `user` VALUES
    (1, 'super-admin', 'super-admin', 'surname', 'super-admin', '$2a$08$c2GKYEYoUVXH1Yb8GXVXVu66ltPvbZgLMcVSXRH.LgZNF/YeaYB8m', 'super-admin@nomail.nomail', 'en', 'UTC', 'ADMIN', 0, 0, null)
;

INSERT IGNORE INTO `client_access` VALUES
    (1, 'test', 'test', 'test-description','test', '$2a$04$9JXeMn4nIHtHUSujpgTiqe11Y./qM/LdiMeaSDbTTWBI/zkHWAfEi', 'super-admin', 0, 0, null)
;

INSERT IGNORE INTO `exam` VALUES
    (1, 'a33d1f74-d5f2-47a3-8993-dc4d813bd4e4', 'test exam 01', '', '', '', 'super-admin', 1709890124393, 1709890124393, NULL, 1709890124393, 1709890124397)
;

INSERT IGNORE INTO `seb_group` VALUES
    (1, '3cfb99c0-34a5-4ffd-a11c-6d9790b3f24c', 'test_group', 'test-description', 'super-admin', 0, 0, null, 1),
    (2, '1cfb88c0-34a5-4ffd-a11c-6d9790b3f24c', 'exam_group', 'exam_group description', 'super-admin', 1721743475058, 1721743475060, null, 1)
;

INSERT IGNORE INTO `session` VALUES
    (1, 1, '9cfb99c0-34a5-4ffd-a11c-4d9790b3f24c', 0, 'Scottie Degroot', '111.111.111.111', 'localhost', 'windows', '0.1-httpBot', 1721743476058, 1721743656171, null);

INSERT IGNORE INTO `session` VALUES
    (2, 1, '1cfb99c0-34a5-4ffd-a11c-4d9790b3f24c', 0, 'Scottie Degroot', '111.111.111.111', 'localhost', 'windows', '0.1-httpBot', 1721743476058, 1721743656171, null);

INSERT IGNORE INTO `session` VALUES
    (2, 1, 'a9ecf98e-4fe9-45d8-b513-ec30d82ae22f', 0, 'Meela Greenleaf', '30.254.51.215', 'localhost', 'windows', '0.1-httpBot', 1721743476058, 1721743656171, null);

INSERT IGNORE INTO `session` VALUES
    (3, 1, '4461dec0-5579-4fef-a86f-0ec7b252c779', 0, 'Owen Torres', '11.66.101.212', 'localhost', 'windows', '0.1-httpBot', 1721743474041, 1721743654070, null);

INSERT IGNORE INTO `session` VALUES
    (4, 1, 'a21f4778-2d8a-49be-8ee2-7750682ca424', 0, 'Bethel Jenks', '83.85.253.32', 'localhost', 'windows', '0.1-httpBot', 1721743473036, 1721743653055, 1721743653055);

INSERT IGNORE INTO `session` VALUES
    (5, 1, 'c8ebdedc-1105-4ecb-bd04-c20ba2e221a5', 0, 'Makenley Zell', '245.144.246.95', 'localhost', 'windows', '0.1-httpBot', 1721743472020, 1721743652045, 1721743652045);

INSERT IGNORE INTO `session` VALUES
    (6, 2, 'cbb6ace2-4840-41c1-8b45-0752e1fb1ecd', 0, 'Arnulfo Durfee', '99.120.90.178', 'localhost', 'windows', '0.1-httpBot', 1721743467980, 1721743647997, 1721743647997);

INSERT IGNORE INTO `session` VALUES
    (7, 2, 'e0fd98d7-7617-4fa9-8ef3-e46c00f8b6d3', 0, 'Omega Bixby', '84.231.12.75', 'localhost', 'windows', '0.1-httpBot', 1720707637322, 1720707637322, NULL);

INSERT IGNORE INTO `session` VALUES
    (8, 2, '7022e48e-955f-4ff5-8db0-c9db166af31a', 0, 'Bristol Estes', '253.160.4.128', 'localhost', 'windows', '0.1-httpBot', 1720707636306, 1720707636306, NULL);

INSERT IGNORE INTO `session` VALUES
    (9, 2, '620b8ad2-da0d-48bf-be0d-55962f9bc8f4', 0, 'Janely Mcmichael', '94.100.140.181', 'localhost', 'windows', '0.1-httpBot', 1720707635298, 1720707635298, NULL);

INSERT IGNORE INTO `session` VALUES
    (10, 2, '830c4164-8167-4952-ab6c-2a72b585465b', 0, 'Charissa Dice', '58.80.14.86', 'localhost', 'windows', '0.1-httpBot', 1720707634348, 1720707634348, NULL);



--screenshot data for 9cfb99c0-34a5-4ffd-a11c-4d9790b3f24c
INSERT IGNORE INTO `screenshot_data` VALUES
    (1, '9cfb99c0-34a5-4ffd-a11c-4d9790b3f24c', 1721743477134, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Maximum interval of 5000ms has been reached.","screenProctoringMetadataBrowser":"Moodle Page 3","screenProctoringMetadataURL":"https://safeexambrowser.org"}'),
    (2, '9cfb99c0-34a5-4ffd-a11c-4d9790b3f24c', 1721743478153, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Right Click","screenProctoringMetadataBrowser":"Moodle Page 2","screenProctoringMetadataURL":"https://moodle-page-1.com"}'),
    (3, '9cfb99c0-34a5-4ffd-a11c-4d9790b3f24c', 1721743479147, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Left Click","screenProctoringMetadataBrowser":"Moodle Page 1","screenProctoringMetadataURL":"https://chat-application.com"}'),
    (4, '9cfb99c0-34a5-4ffd-a11c-4d9790b3f24c', 1721743480132, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Ctrl c + Ctrl v","screenProctoringMetadataBrowser":"Google Homepage","screenProctoringMetadataURL":"https://google.com"}'),
    (5, '9cfb99c0-34a5-4ffd-a11c-4d9790b3f24c', 1721743481170, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Ctrl c + Ctrl v","screenProctoringMetadataBrowser":"Google Homepage","screenProctoringMetadataURL":"https://google.com"}'),
    (6, '9cfb99c0-34a5-4ffd-a11c-4d9790b3f24c', 1721743482182, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Ctrl c + Ctrl v","screenProctoringMetadataBrowser":"Google Homepage","screenProctoringMetadataURL":"https://google.com"}'),
    (7, '9cfb99c0-34a5-4ffd-a11c-4d9790b3f24c', 1721743483215, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Maximum interval of 5000ms has been reached.","screenProctoringMetadataBrowser":"Moodle Page 3","screenProctoringMetadataURL":"https://safeexambrowser.org"}'),
    (8, '9cfb99c0-34a5-4ffd-a11c-4d9790b3f24c', 1721743484222, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Right Click","screenProctoringMetadataBrowser":"Moodle Page 2","screenProctoringMetadataURL":"https://moodle-page-1.com"}'),
    (9, '9cfb99c0-34a5-4ffd-a11c-4d9790b3f24c', 1721743485226, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Maximum interval of 5000ms has been reached.","screenProctoringMetadataBrowser":"Moodle Page 3","screenProctoringMetadataURL":"https://safeexambrowser.org"}'),
    (10, '9cfb99c0-34a5-4ffd-a11c-4d9790b3f24c', 1721743486247, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Ctrl c + Ctrl v","screenProctoringMetadataBrowser":"Google Homepage","screenProctoringMetadataURL":"https://google.com"}')
;


--screenshot data for a9ecf98e-4fe9-45d8-b513-ec30d82ae22f
INSERT INTO `screenshot_data` VALUES
    (11, 'a9ecf98e-4fe9-45d8-b513-ec30d82ae22f', 1721743476116, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Ctrl c + Ctrl v","screenProctoringMetadataBrowser":"Google Homepage","screenProctoringMetadataURL":"https://google.com"}'),
    (12, 'a9ecf98e-4fe9-45d8-b513-ec30d82ae22f', 1721743477142, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Right Click","screenProctoringMetadataBrowser":"Moodle Page 2","screenProctoringMetadataURL":"https://moodle-page-1.com"}'),
    (13, 'a9ecf98e-4fe9-45d8-b513-ec30d82ae22f', 1721743478156, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Maximum interval of 5000ms has been reached.","screenProctoringMetadataBrowser":"Moodle Page 3","screenProctoringMetadataURL":"https://safeexambrowser.org"}'),
    (14, 'a9ecf98e-4fe9-45d8-b513-ec30d82ae22f', 1721743479155, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Ctrl c + Ctrl v","screenProctoringMetadataBrowser":"Google Homepage","screenProctoringMetadataURL":"https://google.com"}'),
    (15, 'a9ecf98e-4fe9-45d8-b513-ec30d82ae22f', 1721743480153, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Ctrl c + Ctrl v","screenProctoringMetadataBrowser":"Google Homepage","screenProctoringMetadataURL":"https://google.com"}'),
    (16, 'a9ecf98e-4fe9-45d8-b513-ec30d82ae22f', 1721743481176, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Right Click","screenProctoringMetadataBrowser":"Moodle Page 2","screenProctoringMetadataURL":"https://moodle-page-1.com"}'),
    (17, 'a9ecf98e-4fe9-45d8-b513-ec30d82ae22f', 1721743482183, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Maximum interval of 5000ms has been reached.","screenProctoringMetadataBrowser":"Moodle Page 3","screenProctoringMetadataURL":"https://safeexambrowser.org"}'),
    (18, 'a9ecf98e-4fe9-45d8-b513-ec30d82ae22f', 1721743483213, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Left Click","screenProctoringMetadataBrowser":"Moodle Page 1","screenProctoringMetadataURL":"https://chat-application.com"}'),
    (19, 'a9ecf98e-4fe9-45d8-b513-ec30d82ae22f', 1721743484220, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Right Click","screenProctoringMetadataBrowser":"Moodle Page 2","screenProctoringMetadataURL":"https://moodle-page-1.com"}'),
    (20, 'a9ecf98e-4fe9-45d8-b513-ec30d82ae22f', 1721743485220, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Left Click","screenProctoringMetadataBrowser":"Moodle Page 1","screenProctoringMetadataURL":"https://chat-application.com"}')
;


--screenshot data for 4461dec0-5579-4fef-a86f-0ec7b252c779
INSERT INTO `screenshot_data` VALUES
    (21, '4461dec0-5579-4fef-a86f-0ec7b252c779', 1721743475099, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Maximum interval of 5000ms has been reached.","screenProctoringMetadataBrowser":"Moodle Page 3","screenProctoringMetadataURL":"https://safeexambrowser.org"}'),
    (22, '4461dec0-5579-4fef-a86f-0ec7b252c779', 1721743476106, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Left Click","screenProctoringMetadataBrowser":"Moodle Page 1","screenProctoringMetadataURL":"https://chat-application.com"}'),
    (23, '4461dec0-5579-4fef-a86f-0ec7b252c779', 1721743477128, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Right Click","screenProctoringMetadataBrowser":"Moodle Page 2","screenProctoringMetadataURL":"https://moodle-page-1.com"}'),
    (24, '4461dec0-5579-4fef-a86f-0ec7b252c779', 1721743478148, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Ctrl c + Ctrl v","screenProctoringMetadataBrowser":"Google Homepage","screenProctoringMetadataURL":"https://google.com"}'),
    (25, '4461dec0-5579-4fef-a86f-0ec7b252c779', 1721743479145, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Left Click","screenProctoringMetadataBrowser":"Moodle Page 1","screenProctoringMetadataURL":"https://chat-application.com"}'),
    (26, '4461dec0-5579-4fef-a86f-0ec7b252c779', 1721743480141, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Left Click","screenProctoringMetadataBrowser":"Moodle Page 1","screenProctoringMetadataURL":"https://chat-application.com"}'),
    (27, '4461dec0-5579-4fef-a86f-0ec7b252c779', 1721743481175, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Left Click","screenProctoringMetadataBrowser":"Moodle Page 1","screenProctoringMetadataURL":"https://chat-application.com"}'),
    (28, '4461dec0-5579-4fef-a86f-0ec7b252c779', 1721743482172, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Right Click","screenProctoringMetadataBrowser":"Moodle Page 2","screenProctoringMetadataURL":"https://moodle-page-1.com"}'),
    (29, '4461dec0-5579-4fef-a86f-0ec7b252c779', 1721743483218, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Ctrl c + Ctrl v","screenProctoringMetadataBrowser":"Google Homepage","screenProctoringMetadataURL":"https://google.com"}'),
    (30, '4461dec0-5579-4fef-a86f-0ec7b252c779', 1721743484220, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Left Click","screenProctoringMetadataBrowser":"Moodle Page 1","screenProctoringMetadataURL":"https://chat-application.com"}')
;


--screenshot data for a21f4778-2d8a-49be-8ee2-7750682ca424
INSERT INTO `screenshot_data` VALUES
    (31, 'a21f4778-2d8a-49be-8ee2-7750682ca424', 1721743474096, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Maximum interval of 5000ms has been reached.","screenProctoringMetadataBrowser":"Moodle Page 3","screenProctoringMetadataURL":"https://safeexambrowser.org"}'),
    (32, 'a21f4778-2d8a-49be-8ee2-7750682ca424', 1721743475106, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Maximum interval of 5000ms has been reached.","screenProctoringMetadataBrowser":"Moodle Page 3","screenProctoringMetadataURL":"https://safeexambrowser.org"}'),
    (33, 'a21f4778-2d8a-49be-8ee2-7750682ca424', 1721743476119, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Right Click","screenProctoringMetadataBrowser":"Moodle Page 2","screenProctoringMetadataURL":"https://moodle-page-1.com"}'),
    (34, 'a21f4778-2d8a-49be-8ee2-7750682ca424', 1721743477149, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Left Click","screenProctoringMetadataBrowser":"Moodle Page 1","screenProctoringMetadataURL":"https://chat-application.com"}'),
    (35, 'a21f4778-2d8a-49be-8ee2-7750682ca424', 1721743478162, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Left Click","screenProctoringMetadataBrowser":"Moodle Page 1","screenProctoringMetadataURL":"https://chat-application.com"}'),
    (36, 'a21f4778-2d8a-49be-8ee2-7750682ca424', 1721743479157, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Left Click","screenProctoringMetadataBrowser":"Moodle Page 1","screenProctoringMetadataURL":"https://chat-application.com"}'),
    (37, 'a21f4778-2d8a-49be-8ee2-7750682ca424', 1721743480154, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Right Click","screenProctoringMetadataBrowser":"Moodle Page 2","screenProctoringMetadataURL":"https://moodle-page-1.com"}'),
    (38, 'a21f4778-2d8a-49be-8ee2-7750682ca424', 1721743481182, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Left Click","screenProctoringMetadataBrowser":"Moodle Page 1","screenProctoringMetadataURL":"https://chat-application.com"}'),
    (39, 'a21f4778-2d8a-49be-8ee2-7750682ca424', 1721743482188, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Maximum interval of 5000ms has been reached.","screenProctoringMetadataBrowser":"Moodle Page 3","screenProctoringMetadataURL":"https://safeexambrowser.org"}'),
    (40, 'a21f4778-2d8a-49be-8ee2-7750682ca424', 1721743483222, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Maximum interval of 5000ms has been reached.","screenProctoringMetadataBrowser":"Moodle Page 3","screenProctoringMetadataURL":"https://safeexambrowser.org"}')
;


--screenshot data for c8ebdedc-1105-4ecb-bd04-c20ba2e221a5
INSERT INTO `screenshot_data` VALUES
    (41, 'c8ebdedc-1105-4ecb-bd04-c20ba2e221a5', 1721743473084, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Ctrl c + Ctrl v","screenProctoringMetadataBrowser":"Google Homepage","screenProctoringMetadataURL":"https://google.com"}'),
    (42, 'c8ebdedc-1105-4ecb-bd04-c20ba2e221a5', 1721743474086, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Left Click","screenProctoringMetadataBrowser":"Moodle Page 1","screenProctoringMetadataURL":"https://chat-application.com"}'),
    (43, 'c8ebdedc-1105-4ecb-bd04-c20ba2e221a5', 1721743475100, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Maximum interval of 5000ms has been reached.","screenProctoringMetadataBrowser":"Moodle Page 3","screenProctoringMetadataURL":"https://safeexambrowser.org"}'),
    (44, 'c8ebdedc-1105-4ecb-bd04-c20ba2e221a5', 1721743476112, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Right Click","screenProctoringMetadataBrowser":"Moodle Page 2","screenProctoringMetadataURL":"https://moodle-page-1.com"}'),
    (45, 'c8ebdedc-1105-4ecb-bd04-c20ba2e221a5', 1721743477134, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Ctrl c + Ctrl v","screenProctoringMetadataBrowser":"Google Homepage","screenProctoringMetadataURL":"https://google.com"}'),
    (46, 'c8ebdedc-1105-4ecb-bd04-c20ba2e221a5', 1721743478160, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Left Click","screenProctoringMetadataBrowser":"Moodle Page 1","screenProctoringMetadataURL":"https://chat-application.com"}'),
    (47, 'c8ebdedc-1105-4ecb-bd04-c20ba2e221a5', 1721743479160, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Maximum interval of 5000ms has been reached.","screenProctoringMetadataBrowser":"Moodle Page 3","screenProctoringMetadataURL":"https://safeexambrowser.org"}'),
    (48, 'c8ebdedc-1105-4ecb-bd04-c20ba2e221a5', 1721743480170, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Left Click","screenProctoringMetadataBrowser":"Moodle Page 1","screenProctoringMetadataURL":"https://chat-application.com"}'),
    (49, 'c8ebdedc-1105-4ecb-bd04-c20ba2e221a5', 1721743481195, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Right Click","screenProctoringMetadataBrowser":"Moodle Page 2","screenProctoringMetadataURL":"https://moodle-page-1.com"}'),
    (50, 'c8ebdedc-1105-4ecb-bd04-c20ba2e221a5', 1721743482212, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Left Click","screenProctoringMetadataBrowser":"Moodle Page 1","screenProctoringMetadataURL":"https://chat-application.com"}')
;


--screenshot data for cbb6ace2-4840-41c1-8b45-0752e1fb1ecd
INSERT INTO `screenshot_data` VALUES
    (51, 'cbb6ace2-4840-41c1-8b45-0752e1fb1ecd', 1721743469029, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Left Click","screenProctoringMetadataBrowser":"Moodle Page 1","screenProctoringMetadataURL":"https://chat-application.com"}'),
    (52, 'cbb6ace2-4840-41c1-8b45-0752e1fb1ecd', 1721743470041, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Ctrl c + Ctrl v","screenProctoringMetadataBrowser":"Google Homepage","screenProctoringMetadataURL":"https://google.com"}'),
    (53, 'cbb6ace2-4840-41c1-8b45-0752e1fb1ecd', 1721743471054, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Maximum interval of 5000ms has been reached.","screenProctoringMetadataBrowser":"Moodle Page 3","screenProctoringMetadataURL":"https://safeexambrowser.org"}'),
    (54, 'cbb6ace2-4840-41c1-8b45-0752e1fb1ecd', 1721743472068, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Ctrl c + Ctrl v","screenProctoringMetadataBrowser":"Google Homepage","screenProctoringMetadataURL":"https://google.com"}'),
    (55, 'cbb6ace2-4840-41c1-8b45-0752e1fb1ecd', 1721743473086, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Maximum interval of 5000ms has been reached.","screenProctoringMetadataBrowser":"Moodle Page 3","screenProctoringMetadataURL":"https://safeexambrowser.org"}'),
    (56, 'cbb6ace2-4840-41c1-8b45-0752e1fb1ecd', 1721743474090, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Maximum interval of 5000ms has been reached.","screenProctoringMetadataBrowser":"Moodle Page 3","screenProctoringMetadataURL":"https://safeexambrowser.org"}'),
    (57, 'cbb6ace2-4840-41c1-8b45-0752e1fb1ecd', 1721743475102, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Ctrl c + Ctrl v","screenProctoringMetadataBrowser":"Google Homepage","screenProctoringMetadataURL":"https://google.com"}'),
    (58, 'cbb6ace2-4840-41c1-8b45-0752e1fb1ecd', 1721743476108, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Right Click","screenProctoringMetadataBrowser":"Moodle Page 2","screenProctoringMetadataURL":"https://moodle-page-1.com"}'),
    (59, 'cbb6ace2-4840-41c1-8b45-0752e1fb1ecd', 1721743477135, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Ctrl c + Ctrl v","screenProctoringMetadataBrowser":"Google Homepage","screenProctoringMetadataURL":"https://google.com"}'),
    (60, 'cbb6ace2-4840-41c1-8b45-0752e1fb1ecd', 1721743478161, 0, '{"screenProctoringMetadataApplication":"Google Homepage","screenProctoringMetadataUserAction":"Ctrl c + Ctrl v","screenProctoringMetadataBrowser":"Google Homepage","screenProctoringMetadataURL":"https://google.com"}')
;


--screenshot data for e0fd98d7-7617-4fa9-8ef3-e46c00f8b6d3
INSERT INTO `screenshot_data` VALUES
    (61, 'e0fd98d7-7617-4fa9-8ef3-e46c00f8b6d3', 1720707638401, 0, '{"screenProctoringMetadataUserAction":"Moodle Page 1","screenProctoringMetadataURL":"bla:14öéééè_––","screenProctoringMetadataWindowTitle":"Safe Exam Browser.Client"}'),
    (62, 'e0fd98d7-7617-4fa9-8ef3-e46c00f8b6d3', 1720707639415, 0, '{"screenProctoringMetadataUserAction":"Moodle Page 1","screenProctoringMetadataURL":"bla:14öéééè_––","screenProctoringMetadataWindowTitle":"Safe Exam Browser.Client"}'),
    (63, 'e0fd98d7-7617-4fa9-8ef3-e46c00f8b6d3', 1720707640439, 0, '{"screenProctoringMetadataUserAction":"Moodle Page 2","screenProctoringMetadataURL":"bla:14öéééè_––","screenProctoringMetadataWindowTitle":"Safe Exam Browser.Client"}'),
    (64, 'e0fd98d7-7617-4fa9-8ef3-e46c00f8b6d3', 1720707641451, 0, '{"screenProctoringMetadataUserAction":"Moodle Page 3","screenProctoringMetadataURL":"bla:14öéééè_––","screenProctoringMetadataWindowTitle":"Safe Exam Browser.Client"}'),
    (65, 'e0fd98d7-7617-4fa9-8ef3-e46c00f8b6d3', 1720707642483, 0, '{"screenProctoringMetadataUserAction":"Moodle Page 2","screenProctoringMetadataURL":"bla:14öéééè_––","screenProctoringMetadataWindowTitle":"Safe Exam Browser.Client"}'),
    (66, 'e0fd98d7-7617-4fa9-8ef3-e46c00f8b6d3', 1720707643487, 0, '{"screenProctoringMetadataUserAction":"Moodle Page 3","screenProctoringMetadataURL":"bla:14öéééè_––","screenProctoringMetadataWindowTitle":"Safe Exam Browser.Client"}'),
    (67, 'e0fd98d7-7617-4fa9-8ef3-e46c00f8b6d3', 1720707644504, 0, '{"screenProctoringMetadataUserAction":"Moodle Page 1","screenProctoringMetadataURL":"bla:14öéééè_––","screenProctoringMetadataWindowTitle":"Safe Exam Browser.Client"}'),
    (68, 'e0fd98d7-7617-4fa9-8ef3-e46c00f8b6d3', 1720707645506, 0, '{"screenProctoringMetadataUserAction":"Moodle Page 1","screenProctoringMetadataURL":"bla:14öéééè_––","screenProctoringMetadataWindowTitle":"Safe Exam Browser.Client"}'),
    (69, 'e0fd98d7-7617-4fa9-8ef3-e46c00f8b6d3', 1720707646529, 0, '{"screenProctoringMetadataUserAction":"Moodle Page 1","screenProctoringMetadataURL":"bla:14öéééè_––","screenProctoringMetadataWindowTitle":"Safe Exam Browser.Client"}'),
    (70, 'e0fd98d7-7617-4fa9-8ef3-e46c00f8b6d3', 1720707647547, 0, '{"screenProctoringMetadataUserAction":"Moodle Page 2","screenProctoringMetadataURL":"bla:14öéééè_––","screenProctoringMetadataWindowTitle":"Safe Exam Browser.Client"}')
;


--screenshot data for 7022e48e-955f-4ff5-8db0-c9db166af31a
INSERT INTO `screenshot_data` VALUES
    (71, '7022e48e-955f-4ff5-8db0-c9db166af31a', 1720707637374, 0, '{"screenProctoringMetadataUserAction":"Moodle Page 1","screenProctoringMetadataURL":"bla:14öéééè_––","screenProctoringMetadataWindowTitle":"Safe Exam Browser.Client"}'),
    (72, '7022e48e-955f-4ff5-8db0-c9db166af31a', 1720707638402, 0, '{"screenProctoringMetadataUserAction":"Moodle Page 1","screenProctoringMetadataURL":"bla:14öéééè_––","screenProctoringMetadataWindowTitle":"Safe Exam Browser.Client"}'),
    (73, '7022e48e-955f-4ff5-8db0-c9db166af31a', 1720707639417, 0, '{"screenProctoringMetadataUserAction":"Moodle Page 2","screenProctoringMetadataURL":"bla:14öéééè_––","screenProctoringMetadataWindowTitle":"Safe Exam Browser.Client"}'),
    (74, '7022e48e-955f-4ff5-8db0-c9db166af31a', 1720707640438, 0, '{"screenProctoringMetadataUserAction":"Moodle Page 2","screenProctoringMetadataURL":"bla:14öéééè_––","screenProctoringMetadataWindowTitle":"Safe Exam Browser.Client"}'),
    (75, '7022e48e-955f-4ff5-8db0-c9db166af31a', 1720707641445, 0, '{"screenProctoringMetadataUserAction":"Moodle Page 1","screenProctoringMetadataURL":"bla:14öéééè_––","screenProctoringMetadataWindowTitle":"Safe Exam Browser.Client"}'),
    (76, '7022e48e-955f-4ff5-8db0-c9db166af31a', 1720707642482, 0, '{"screenProctoringMetadataUserAction":"Moodle Page 1","screenProctoringMetadataURL":"bla:14öéééè_––","screenProctoringMetadataWindowTitle":"Safe Exam Browser.Client"}'),
    (77, '7022e48e-955f-4ff5-8db0-c9db166af31a', 1720707643485, 0, '{"screenProctoringMetadataUserAction":"Moodle Page 2","screenProctoringMetadataURL":"bla:14öéééè_––","screenProctoringMetadataWindowTitle":"Safe Exam Browser.Client"}'),
    (78, '7022e48e-955f-4ff5-8db0-c9db166af31a', 1720707644494, 0, '{"screenProctoringMetadataUserAction":"Moodle Page 1","screenProctoringMetadataURL":"bla:14öéééè_––","screenProctoringMetadataWindowTitle":"Safe Exam Browser.Client"}'),
    (79, '7022e48e-955f-4ff5-8db0-c9db166af31a', 1720707645471, 0, '{"screenProctoringMetadataUserAction":"Moodle Page 3","screenProctoringMetadataURL":"bla:14öéééè_––","screenProctoringMetadataWindowTitle":"Safe Exam Browser.Client"}'),
    (80, '7022e48e-955f-4ff5-8db0-c9db166af31a', 1720707646494, 0, '{"screenProctoringMetadataUserAction":"Moodle Page 1","screenProctoringMetadataURL":"bla:14öéééè_––","screenProctoringMetadataWindowTitle":"Safe Exam Browser.Client"}')
;


--screenshot data for 620b8ad2-da0d-48bf-be0d-55962f9bc8f4
INSERT INTO `screenshot_data` VALUES
    (81, '620b8ad2-da0d-48bf-be0d-55962f9bc8f4', 1720707636356, 0, '{"screenProctoringMetadataUserAction":"Moodle Page 3","screenProctoringMetadataURL":"bla:14öéééè_––","screenProctoringMetadataWindowTitle":"Safe Exam Browser.Client"}'),
    (82, '620b8ad2-da0d-48bf-be0d-55962f9bc8f4', 1720707637374, 0, '{"screenProctoringMetadataUserAction":"Moodle Page 3","screenProctoringMetadataURL":"bla:14öéééè_––","screenProctoringMetadataWindowTitle":"Safe Exam Browser.Client"}'),
    (83, '620b8ad2-da0d-48bf-be0d-55962f9bc8f4', 1720707638402, 0, '{"screenProctoringMetadataUserAction":"Moodle Page 3","screenProctoringMetadataURL":"bla:14öéééè_––","screenProctoringMetadataWindowTitle":"Safe Exam Browser.Client"}'),
    (84, '620b8ad2-da0d-48bf-be0d-55962f9bc8f4', 1720707639420, 0, '{"screenProctoringMetadataUserAction":"Moodle Page 1","screenProctoringMetadataURL":"bla:14öéééè_––","screenProctoringMetadataWindowTitle":"Safe Exam Browser.Client"}'),
    (85, '620b8ad2-da0d-48bf-be0d-55962f9bc8f4', 1720707640440, 0, '{"screenProctoringMetadataUserAction":"Moodle Page 3","screenProctoringMetadataURL":"bla:14öéééè_––","screenProctoringMetadataWindowTitle":"Safe Exam Browser.Client"}'),
    (86, '620b8ad2-da0d-48bf-be0d-55962f9bc8f4', 1720707641452, 0, '{"screenProctoringMetadataUserAction":"Moodle Page 2","screenProctoringMetadataURL":"bla:14öéééè_––","screenProctoringMetadataWindowTitle":"Safe Exam Browser.Client"}'),
    (87, '620b8ad2-da0d-48bf-be0d-55962f9bc8f4', 1720707642488, 0, '{"screenProctoringMetadataUserAction":"Moodle Page 2","screenProctoringMetadataURL":"bla:14öéééè_––","screenProctoringMetadataWindowTitle":"Safe Exam Browser.Client"}'),
    (88, '620b8ad2-da0d-48bf-be0d-55962f9bc8f4', 1720707643495, 0, '{"screenProctoringMetadataUserAction":"Moodle Page 2","screenProctoringMetadataURL":"bla:14öéééè_––","screenProctoringMetadataWindowTitle":"Safe Exam Browser.Client"}'),
    (89, '620b8ad2-da0d-48bf-be0d-55962f9bc8f4', 1720707644501, 0, '{"screenProctoringMetadataUserAction":"Moodle Page 1","screenProctoringMetadataURL":"bla:14öéééè_––","screenProctoringMetadataWindowTitle":"Safe Exam Browser.Client"}'),
    (90, '620b8ad2-da0d-48bf-be0d-55962f9bc8f4', 1720707645485, 0, '{"screenProctoringMetadataUserAction":"Moodle Page 3","screenProctoringMetadataURL":"bla:14öéééè_––","screenProctoringMetadataWindowTitle":"Safe Exam Browser.Client"}')
;


--screenshot data for 830c4164-8167-4952-ab6c-2a72b585465b
INSERT INTO `screenshot_data` VALUES
    (91, '830c4164-8167-4952-ab6c-2a72b585465b', 1720707635612, 0, '{"screenProctoringMetadataUserAction":"Moodle Page 3","screenProctoringMetadataURL":"bla:14öéééè_––","screenProctoringMetadataWindowTitle":"Safe Exam Browser.Client"}'),
    (92, '830c4164-8167-4952-ab6c-2a72b585465b', 1720707636409, 0, '{"screenProctoringMetadataUserAction":"Moodle Page 2","screenProctoringMetadataURL":"bla:14öéééè_––","screenProctoringMetadataWindowTitle":"Safe Exam Browser.Client"}'),
    (93, '830c4164-8167-4952-ab6c-2a72b585465b', 1720707637413, 0, '{"screenProctoringMetadataUserAction":"Moodle Page 1","screenProctoringMetadataURL":"bla:14öéééè_––","screenProctoringMetadataWindowTitle":"Safe Exam Browser.Client"}'),
    (94, '830c4164-8167-4952-ab6c-2a72b585465b', 1720707638434, 0, '{"screenProctoringMetadataUserAction":"Moodle Page 2","screenProctoringMetadataURL":"bla:14öéééè_––","screenProctoringMetadataWindowTitle":"Safe Exam Browser.Client"}'),
    (95, '830c4164-8167-4952-ab6c-2a72b585465b', 1720707639455, 0, '{"screenProctoringMetadataUserAction":"Moodle Page 3","screenProctoringMetadataURL":"bla:14öéééè_––","screenProctoringMetadataWindowTitle":"Safe Exam Browser.Client"}'),
    (96, '830c4164-8167-4952-ab6c-2a72b585465b', 1720707640470, 0, '{"screenProctoringMetadataUserAction":"Moodle Page 2","screenProctoringMetadataURL":"bla:14öéééè_––","screenProctoringMetadataWindowTitle":"Safe Exam Browser.Client"}'),
    (97, '830c4164-8167-4952-ab6c-2a72b585465b', 1720707641474, 0, '{"screenProctoringMetadataUserAction":"Moodle Page 3","screenProctoringMetadataURL":"bla:14öéééè_––","screenProctoringMetadataWindowTitle":"Safe Exam Browser.Client"}'),
    (98, '830c4164-8167-4952-ab6c-2a72b585465b', 1720707642500, 0, '{"screenProctoringMetadataUserAction":"Moodle Page 3","screenProctoringMetadataURL":"bla:14öéééè_––","screenProctoringMetadataWindowTitle":"Safe Exam Browser.Client"}'),
    (99, '830c4164-8167-4952-ab6c-2a72b585465b', 1720707643510, 0, '{"screenProctoringMetadataUserAction":"Moodle Page 2","screenProctoringMetadataURL":"bla:14öéééè_––","screenProctoringMetadataWindowTitle":"Safe Exam Browser.Client"}'),
    (100, '830c4164-8167-4952-ab6c-2a72b585465b', 1720707644517, 0, '{"screenProctoringMetadataUserAction":"Moodle Page 1","screenProctoringMetadataURL":"bla:14öéééè_––","screenProctoringMetadataWindowTitle":"Safe Exam Browser.Client"}')
;