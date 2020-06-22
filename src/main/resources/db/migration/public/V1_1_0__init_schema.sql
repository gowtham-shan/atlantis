CREATE TABLE IF NOT EXISTS organization
(
    org_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS auth_user
(
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_name VARCHAR(50) UNIQUE NOT NULL,
    password  VARCHAR(60)        NOT NULL,
    org_id BIGINT NOT NULL,
    CONSTRAINT fk_organization foreign key(org_id) references organization(org_id)
);