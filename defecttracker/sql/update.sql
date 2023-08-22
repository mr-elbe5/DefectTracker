--version 1.0

alter table t_content add language      VARCHAR(10)   NOT NULL DEFAULT 'de';

alter table t_project drop CONSTRAINT t_project_fk1;
alter table t_project add CONSTRAINT t_project_fk1 FOREIGN KEY (id) REFERENCES t_content (id) ON DELETE CASCADE;

alter table t_location drop CONSTRAINT t_location_fk1;
alter table t_location drop CONSTRAINT t_location_fk2;
alter table t_location add CONSTRAINT t_location_fk1 FOREIGN KEY (id) REFERENCES t_content (id) ON DELETE CASCADE;
alter table t_location add CONSTRAINT t_location_fk2 FOREIGN KEY (project_id) REFERENCES t_project (id) ON DELETE CASCADE;

alter table t_defect drop CONSTRAINT t_defect_fk1;
alter table t_defect drop CONSTRAINT t_defect_fk2;
alter table t_defect drop CONSTRAINT t_defect_fk3;
alter table t_defect drop CONSTRAINT t_defect_fk4;
alter table t_defect add CONSTRAINT t_defect_fk1 FOREIGN KEY (id) REFERENCES t_content (id) ON DELETE CASCADE;
alter table t_defect add CONSTRAINT t_defect_fk2 FOREIGN KEY (location_id) REFERENCES t_location (id) ON DELETE CASCADE;
alter table t_defect add CONSTRAINT t_defect_fk3 FOREIGN KEY (project_id) REFERENCES t_project (id) ON DELETE CASCADE;
alter table t_defect add CONSTRAINT t_defect_fk4 FOREIGN KEY (plan_id) REFERENCES t_image (id) ON DELETE CASCADE;

alter table t_defect_comment drop CONSTRAINT t_defect_comment_fk1;
alter table t_defect_comment add CONSTRAINT t_defect_comment_fk1 FOREIGN KEY (defect_id) REFERENCES t_defect (id) ON DELETE CASCADE;

alter table t_defect_comment_document drop CONSTRAINT t_defect_comment_document_fk1;
alter table t_defect_comment_document drop CONSTRAINT t_defect_comment_document_fk2;
alter table t_defect_comment_document add CONSTRAINT t_defect_comment_document_fk1 FOREIGN KEY (id) REFERENCES t_file (id) ON DELETE CASCADE;
alter table t_defect_comment_document add CONSTRAINT t_defect_comment_document_fk2 FOREIGN KEY (comment_id) REFERENCES t_defect_comment (id) ON DELETE CASCADE;

alter table t_defect_comment_image drop CONSTRAINT t_defect_comment_image_fk1;
alter table t_defect_comment_image drop CONSTRAINT t_defect_comment_image_fk2;
alter table t_defect_comment_image add CONSTRAINT t_defect_comment_image_fk1 FOREIGN KEY (id) REFERENCES t_image (id) ON DELETE CASCADE;
alter table t_defect_comment_image add CONSTRAINT t_defect_comment_image_fk2 FOREIGN KEY (comment_id) REFERENCES t_defect_comment (id) ON DELETE CASCADE;

--

alter table t_user drop column company_id;
alter table t_user drop column approval_code;
alter table t_user drop column approved;
alter table t_user drop column email_verified;

drop table t_company;
drop sequence s_company_id;

--

alter table t_defect add notified BOOLEAN NOT NULL DEFAULT FALSE;

update t_system_right set name = 'CONTENTADMINISTRATION' where name = 'CONTENTEDIT';
update t_system_right set name = 'CONTENTEDIT' where name = 'SPECIFICCONTENTEDIT';

INSERT INTO t_group (id,name)
VALUES (2,'Project Editors');
INSERT INTO t_system_right (name,group_id)
VALUES ('CONTENTEDIT',2);
INSERT INTO t_system_right (name,group_id)
VALUES ('CONTENTREAD',2);

INSERT INTO t_user (id,first_name,last_name,email,login,pwd)
VALUES (2,'System','Editor','editor@localhost','sysedit','');
INSERT INTO t_user2group (user_id, group_id)
VALUES(2,1);


--- set pwd 'pass' dependent on salt jB8FPa3E6h4=
-- root user
update t_user set pwd='UniSdEEc7IcDa38/ph/LND4yBSk=' where id=1;
update t_user set pwd='UniSdEEc7IcDa38/ph/LND4yBSk=' where id=2;

alter table t_location add approve_date DATE NULL;

alter table t_project drop column phase;
alter table t_defect drop column phase;

alter table t_defect add import_id INTEGER NULL;
alter table t_image add import_id INTEGER NULL;

--bauhaus live ok
--defecttracker elbe5 ok
--defecttracker viertmann ok