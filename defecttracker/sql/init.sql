
CREATE SEQUENCE s_group_id START 1000;
CREATE TABLE IF NOT EXISTS t_group
(
    id          INTEGER      NOT NULL,
    change_date TIMESTAMP    NOT NULL DEFAULT now(),
    name        VARCHAR(100) NOT NULL,
    notes       VARCHAR(500) NOT NULL DEFAULT '',
    CONSTRAINT t_group_pk PRIMARY KEY (id)
);

CREATE SEQUENCE s_user_id START 1000;
CREATE TABLE IF NOT EXISTS t_user
(
    id                 INTEGER      NOT NULL,
    change_date        TIMESTAMP    NOT NULL DEFAULT now(),
    title              VARCHAR(30)  NOT NULL DEFAULT '',
    first_name         VARCHAR(100) NOT NULL DEFAULT '',
    last_name          VARCHAR(100) NOT NULL,
    street             VARCHAR(100) NOT NULL DEFAULT '',
    zipCode            VARCHAR(16)  NOT NULL DEFAULT '',
    city               VARCHAR(50)  NOT NULL DEFAULT '',
    country            VARCHAR(50)  NOT NULL DEFAULT '',
    email              VARCHAR(100) NOT NULL DEFAULT '',
    phone              VARCHAR(50)  NOT NULL DEFAULT '',
    fax                VARCHAR(50)  NOT NULL DEFAULT '',
    mobile             VARCHAR(50)  NOT NULL DEFAULT '',
    notes              VARCHAR(500) NOT NULL DEFAULT '',
    portrait_name      VARCHAR(255) NOT NULL DEFAULT '',
    portrait           BYTEA        NULL,
    login              VARCHAR(30)  NOT NULL,
    pwd                VARCHAR(100) NOT NULL,
    token              VARCHAR(100) NOT NULL DEFAULT '',
    token_expiration   TIMESTAMP    NOT NULL DEFAULT now(),
    failed_login_count INTEGER      NOT NULL DEFAULT 0,
    locked             BOOLEAN      NOT NULL DEFAULT FALSE,
    deleted            BOOLEAN      NOT NULL DEFAULT FALSE,
    CONSTRAINT t_user_pk PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS t_user2group
(
    user_id  INTEGER NOT NULL,
    group_id INTEGER NOT NULL,
    CONSTRAINT t_user2group_pk PRIMARY KEY (user_id,
                                            group_id),
    CONSTRAINT t_user2group_fk1 FOREIGN KEY (user_id) REFERENCES t_user (id) ON DELETE CASCADE,
    CONSTRAINT t_user2group_fk2 FOREIGN KEY (group_id) REFERENCES t_group (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS t_system_right
(
    name     VARCHAR(30) NOT NULL,
    group_id INTEGER     NOT NULL,
    CONSTRAINT t_system_right_pk PRIMARY KEY (name,
                                              group_id),
    CONSTRAINT t_system_fk1 FOREIGN KEY (group_id) REFERENCES t_group (id) ON DELETE CASCADE
);

CREATE TABLE t_timer_task
(
    name               VARCHAR(60)  NOT NULL,
    display_name       VARCHAR(255) NOT NULL,
    execution_interval VARCHAR(30)  NOT NULL,
    day                INTEGER      NOT NULL DEFAULT 0,
    hour               INTEGER      NOT NULL DEFAULT 0,
    minute             INTEGER      NOT NULL DEFAULT 0,
    last_execution     TIMESTAMP    NULL,
    note_execution     BOOLEAN      NOT NULL DEFAULT FALSE,
    active             BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT t_timer_task_pk PRIMARY KEY (name)
);

CREATE SEQUENCE IF NOT EXISTS s_content_id START 1000;

CREATE TABLE IF NOT EXISTS t_content
(
    id            INTEGER       NOT NULL,
    type          VARCHAR(30)   NOT NULL,
    creation_date TIMESTAMP     NOT NULL DEFAULT now(),
    change_date   TIMESTAMP     NOT NULL DEFAULT now(),
    parent_id     INTEGER       NULL,
    ranking       INTEGER       NOT NULL DEFAULT 0,
    name          VARCHAR(60)   NOT NULL,
    display_name  VARCHAR(100)  NOT NULL,
    description   VARCHAR(2000) NOT NULL DEFAULT '',
    creator_id    INTEGER       NOT NULL DEFAULT 1,
    changer_id    INTEGER       NOT NULL DEFAULT 1,
    language      VARCHAR(10)   NOT NULL DEFAULT 'de',
    access_type   VARCHAR(10)   NOT NULL DEFAULT 'OPEN',
    nav_type      VARCHAR(10)   NOT NULL DEFAULT 'NONE',
    active        BOOLEAN       NOT NULL DEFAULT TRUE,
    CONSTRAINT t_content_pk PRIMARY KEY (id),
    CONSTRAINT t_content_fk1 FOREIGN KEY (parent_id) REFERENCES t_content (id) ON DELETE CASCADE,
    CONSTRAINT t_content_fk2 FOREIGN KEY (creator_id) REFERENCES t_user (id) ON DELETE SET DEFAULT,
    CONSTRAINT t_content_fk3 FOREIGN KEY (changer_id) REFERENCES t_user (id) ON DELETE SET DEFAULT,
    CONSTRAINT t_content_un1 UNIQUE (id, parent_id, name)
);

CREATE SEQUENCE IF NOT EXISTS s_file_id START 1000;

CREATE TABLE IF NOT EXISTS t_file
(
    id            INTEGER       NOT NULL,
    type          VARCHAR(30)   NOT NULL,
    creation_date TIMESTAMP     NOT NULL DEFAULT now(),
    change_date   TIMESTAMP     NOT NULL DEFAULT now(),
    parent_id     INTEGER       NULL,
    file_name     VARCHAR(60)   NOT NULL,
    display_name  VARCHAR(100)  NOT NULL,
    description   VARCHAR(2000) NOT NULL DEFAULT '',
    creator_id    INTEGER       NOT NULL DEFAULT 1,
    changer_id    INTEGER       NOT NULL DEFAULT 1,
    content_type  VARCHAR(255)  NOT NULL DEFAULT '',
    file_size     INTEGER       NOT NULL DEFAULT 0,
    bytes         BYTEA         NOT NULL,
    CONSTRAINT t_file_pk PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS t_image
(
    id            INTEGER       NOT NULL,
    width         INTEGER       NOT NULL DEFAULT 0,
    height        INTEGER       NOT NULL DEFAULT 0,
    preview_bytes BYTEA         NULL,
    CONSTRAINT t_image_pk PRIMARY KEY (id),
    CONSTRAINT t_image_fk1 FOREIGN KEY (id) REFERENCES t_file (id) ON DELETE CASCADE
);

CREATE OR REPLACE VIEW v_preview_file as (
                                         select t_file.id,file_name,content_type,preview_bytes
                                         from t_file, t_image
                                         where t_file.id=t_image.id
                                             );

CREATE TABLE IF NOT EXISTS t_content_right
(
    content_id INTEGER     NOT NULL,
    group_id   INTEGER     NOT NULL,
    value      VARCHAR(20) NOT NULL,
    CONSTRAINT t_content_right_pk PRIMARY KEY (content_id, group_id),
    CONSTRAINT t_content_right_fk1 FOREIGN KEY (content_id) REFERENCES t_content (id) ON DELETE CASCADE,
    CONSTRAINT t_content_right_fk2 FOREIGN KEY (group_id) REFERENCES t_group (id) ON DELETE CASCADE
);

-- root user
INSERT INTO t_user (id,first_name,last_name,email,login,pwd)
VALUES (1,'System','Administrator','root@localhost','root','');

-- timer
INSERT INTO t_timer_task (name,display_name,execution_interval,minute,active)
VALUES ('heartbeat','Heartbeat Task','CONTINOUS',5,FALSE);
INSERT INTO t_timer_task (name,display_name,execution_interval,minute,active)
VALUES ('cleanup','Cleanup Task','CONTINOUS',5,FALSE);
INSERT INTO t_timer_task (name,display_name,execution_interval,minute,active)
VALUES ('searchindex','Search Index Task','CONTINOUS',5,FALSE);


CREATE TABLE IF NOT EXISTS t_project
(
    id INTEGER NOT NULL,
    group_id   INTEGER NOT NULL,
    phase      VARCHAR(20) NOT NULL,
    CONSTRAINT t_project_pk PRIMARY KEY (id),
    CONSTRAINT t_project_fk1 FOREIGN KEY (id) REFERENCES t_content (id) ON DELETE CASCADE,
    CONSTRAINT t_project_fk2 FOREIGN KEY (group_id) REFERENCES t_group (id)
);

CREATE TABLE IF NOT EXISTS t_location
(
    id                 INTEGER      NOT NULL,
    project_id         INTEGER      NOT NULL,
    CONSTRAINT t_location_pk PRIMARY KEY (id),
    CONSTRAINT t_location_fk1 FOREIGN KEY (id) REFERENCES t_content (id) ON DELETE CASCADE,
    CONSTRAINT t_location_fk2 FOREIGN KEY (project_id) REFERENCES t_project (id) ON DELETE CASCADE
);

CREATE SEQUENCE IF NOT EXISTS s_defect_id START 1000;

CREATE TABLE IF NOT EXISTS t_defect
(
    id               INTEGER       NOT NULL,
    display_id       INTEGER       NOT NULL,
    location_id      INTEGER       NOT NULL,
    project_id       INTEGER       NOT NULL,
    assigned_id      INTEGER       NOT NULL,
    lot              VARCHAR(255)  NOT NULL DEFAULT '',
    phase            VARCHAR(20)   NOT NULL,
    state            VARCHAR(20)   NOT NULL,
    costs            INTEGER       NOT NULL DEFAULT 0,
    plan_id          INTEGER       NOT NULL,
    position_x       INTEGER       NOT NULL DEFAULT 0,
    position_y       INTEGER       NOT NULL DEFAULT 0,
    position_comment VARCHAR(255)  NOT NULL DEFAULT '',
    due_date1        TIMESTAMP     NULL,
    due_date2        TIMESTAMP     NULL,
    close_date       TIMESTAMP     NULL,
    CONSTRAINT t_defect_pk PRIMARY KEY (id),
    CONSTRAINT t_defect_fk1 FOREIGN KEY (id) REFERENCES t_content (id) ON DELETE CASCADE,
    CONSTRAINT t_defect_fk2 FOREIGN KEY (location_id) REFERENCES t_location (id) ON DELETE CASCADE,
    CONSTRAINT t_defect_fk3 FOREIGN KEY (project_id) REFERENCES t_project (id) ON DELETE CASCADE,
    CONSTRAINT t_defect_fk4 FOREIGN KEY (plan_id) REFERENCES t_image (id) ON DELETE CASCADE,
    CONSTRAINT t_defect_fk5 FOREIGN KEY (assigned_id) REFERENCES t_user (id)
);

CREATE SEQUENCE IF NOT EXISTS s_defect_comment_id START 1000;
CREATE TABLE IF NOT EXISTS t_defect_comment
(
    id            INTEGER       NOT NULL,
    defect_id     INTEGER       NOT NULL,
    creation_date TIMESTAMP     NOT NULL DEFAULT now(),
    creator_id    INTEGER       NOT NULL,
    comment       VARCHAR(2000) NOT NULL DEFAULT '',
    state         VARCHAR(20)   NOT NULL DEFAULT 'OPEN',
    CONSTRAINT t_defect_comment_pk PRIMARY KEY (id),
    CONSTRAINT t_defect_comment_fk1 FOREIGN KEY (defect_id) REFERENCES t_defect (id) ON DELETE CASCADE,
    CONSTRAINT t_defect_comment_fk2 FOREIGN KEY (creator_id) REFERENCES t_user (id)
);

CREATE TABLE IF NOT EXISTS t_defect_comment_document
(
    id            INTEGER       NOT NULL,
    comment_id    INTEGER       NOT NULL,
    CONSTRAINT t_defect_comment_document_pk PRIMARY KEY (id),
    CONSTRAINT t_defect_comment_document_fk1 FOREIGN KEY (id) REFERENCES t_file (id) ON DELETE CASCADE,
    CONSTRAINT t_defect_comment_document_fk2 FOREIGN KEY (comment_id) REFERENCES t_defect_comment (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS t_defect_comment_image
(
    id            INTEGER       NOT NULL,
    comment_id    INTEGER       NOT NULL,
    CONSTRAINT t_defect_comment_image_pk PRIMARY KEY (id),
    CONSTRAINT t_defect_comment_image_fk1 FOREIGN KEY (id) REFERENCES t_image (id) ON DELETE CASCADE,
    CONSTRAINT t_defect_comment_image_fk2 FOREIGN KEY (comment_id) REFERENCES t_defect_comment (id) ON DELETE CASCADE
);

-- global PROJECT owners
INSERT INTO t_group (id,name)
VALUES (1,'Project Owners');
INSERT INTO t_system_right (name,group_id)
VALUES ('USER',1);
INSERT INTO t_system_right (name,group_id)
VALUES ('CONTENTEDIT',1);
INSERT INTO t_system_right (name,group_id)
VALUES ('CONTENTREAD',1);


--- set pwd 'pass' dependent on salt qczm7vjTmdY=
-- root user
update t_user set pwd='lj31EhPC1JvEvw0/+DghmE/1xzc=' where id=1;


INSERT INTO t_content (id,type,parent_id,ranking,name,display_name,description,creator_id,changer_id,access_type,nav_type)
VALUES (1,'RootPageData',null,0,'home','Projekte','Projekte',1,1,'OPEN','NONE');





