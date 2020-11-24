BEGIN;

INSERT INTO usr_user(usr_id, usr_name, usr_email, usr_password, usr_birthday)
VALUES ('875d374c-a3e4-43e5-8f81-904da3f36db1', 'Arthur Benozati', 'arthur@email.com', 'admin123', '1976-10-18')
RETURNING *;

INSERT INTO adm_admin(adm_id)
VALUES ('875d374c-a3e4-43e5-8f81-904da3f36db1')
RETURNING *;

INSERT INTO rpu_role_per_user (rol_id, usr_id)
VALUES ((SELECT rol_role.rol_id FROM rol_role WHERE rol_name = 'ADMIN'), '875d374c-a3e4-43e5-8f81-904da3f36db1')
RETURNING *;

COMMIT;

BEGIN;

INSERT INTO usr_user(usr_id, usr_name, usr_email, usr_password, usr_birthday)
VALUES ('4b509f3a-78d3-4d3c-bdbc-1569a0deabd7', 'Matheus Rocha', 'admin@email.com', 'admin123', '1998-11-09')
RETURNING *;

INSERT INTO adm_admin(adm_id)
VALUES ('4b509f3a-78d3-4d3c-bdbc-1569a0deabd7')
RETURNING *;

INSERT INTO rpu_role_per_user (rol_id, usr_id)
VALUES ((SELECT rol_role.rol_id FROM rol_role WHERE rol_name = 'ADMIN'), '4b509f3a-78d3-4d3c-bdbc-1569a0deabd7')
RETURNING *;

COMMIT;