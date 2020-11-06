BEGIN;

INSERT INTO rol_role (rol_id, rol_name)
VALUES ('26053fed-5251-48c1-a0cb-98bb48c74db4', 'ADMIN'),
       ('4bf709d7-3307-4768-9afd-f77747ec7527', 'FREELANCER'),
       ('f8023627-fe17-415b-b0e1-3fcd8b34cad0', 'CONTRACTOR')
RETURNING *;

COMMIT;