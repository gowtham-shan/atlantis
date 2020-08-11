INSERT INTO role(role_name, description)
VALUES ('ROLE_ADMIN', 'Role for the admin of an organization');

INSERT INTO role(role_name, description)
VALUES ('ROLE_STAFF', 'Role for the employees/staffs inside the organisation');

INSERT INTO role(role_name, description)
VALUES ('ROLE_CUSTOMER', 'Role for customers');

INSERT INTO privilege(name)
values ('READ_PRIVILEGE');
INSERT INTO privilege(name)
values ('WRITE_PRIVILEGE');

INSERT INTO role_privileges(role_id, privilege_id)
values (1, 1);
INSERT INTO role_privileges(role_id, privilege_id)
values (1, 2);
INSERT INTO role_privileges(role_id, privilege_id)
values (2, 1);
INSERT INTO role_privileges(role_id, privilege_id)
values (2, 2);
INSERT INTO role_privileges(role_id, privilege_id)
values (3, 1);
INSERT INTO role_privileges(role_id, privilege_id)
values (3, 2);
