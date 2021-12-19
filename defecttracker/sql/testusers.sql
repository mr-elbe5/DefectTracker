
--Project Owners
INSERT INTO t_user (id, first_name, last_name, email, login, pwd, approval_code, approved) VALUES (1000, 'Theo', 'Tester', 'tester@localhost', 'tester', 'A0y3+ZmqpMhWA21VFQMkyY6v74Y=', '', TRUE);
INSERT INTO t_user (id, first_name, last_name, email, login, pwd, approval_code, approved) VALUES (1001, 'Thea', 'Testerin', 'testerin@localhost', 'testerin', 'A0y3+ZmqpMhWA21VFQMkyY6v74Y=', '', TRUE);

INSERT INTO t_user2group (user_id, group_id) VALUES (1000,1);
INSERT INTO t_user2group (user_id, group_id) VALUES (1001,1);

--Firmen
INSERT INTO t_user (id, first_name, last_name, email, login, pwd, approval_code, approved) VALUES (1010, 'Fa', 'Strom + Gas', 'sg@localhost', 'sg', 'A0y3+ZmqpMhWA21VFQMkyY6v74Y=', '', TRUE);
INSERT INTO t_user (id, first_name, last_name, email, login, pwd, approval_code, approved) VALUES (1011, 'Fa', 'Trockenbau', 'tb@localhost', 'tb', 'A0y3+ZmqpMhWA21VFQMkyY6v74Y=', '', TRUE);
INSERT INTO t_user (id, first_name, last_name, email, login, pwd, approval_code, approved) VALUES (1012, 'Fa', 'Elektrik', 'el@localhost', 'el', 'A0y3+ZmqpMhWA21VFQMkyY6v74Y=', '', TRUE);
INSERT INTO t_user (id, first_name, last_name, email, login, pwd, approval_code, approved) VALUES (1013, 'Fa', 'Malen und Lack', 'ml@localhost', 'ml', 'A0y3+ZmqpMhWA21VFQMkyY6v74Y=', '', TRUE);
INSERT INTO t_user (id, first_name, last_name, email, login, pwd, approval_code, approved) VALUES (1014, 'Fa', 'Holz', 'hz@localhost', 'hz', 'A0y3+ZmqpMhWA21VFQMkyY6v74Y=', '', TRUE);

ALTER SEQUENCE s_user_id RESTART WITH 1020;

INSERT INTO t_group (id, name, notes) VALUES (1000, 'Siedlung1', 'Beteiligte an Siedlung 1');
INSERT INTO t_group (id, name, notes) VALUES (1001, 'Holzhaus1', 'Beteiligte an Holzhaus 1');

ALTER SEQUENCE s_group_id RESTART WITH 1002;
