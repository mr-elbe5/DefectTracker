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