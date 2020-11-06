CREATE TABLE new_newsletter
(
    new_id     uuid primary key,
    new_domain varchar(255) not null,
    new_type   varchar(255) not null,
    constraint new_newsletter_domain_uk unique (new_domain)
);

CREATE TABLE usr_user
(
    usr_id       uuid primary key,
    mnew_id      uuid,
    usr_name     varchar(255) not null,
    usr_email    varchar(255) not null,
    usr_password varchar(32)  not null,
    usr_birthday date,
    usr_gender   char(1) default 'M',
    constraint usr_user_email_uk unique (usr_email),
    constraint usr_new_fk foreign key (mnew_id) references new_newsletter (new_id)
);

CREATE TABLE rol_role
(
    rol_id   uuid primary key,
    rol_name varchar(255) not null,
    constraint rol_rol_name_uk unique (rol_name)
);

CREATE TABLE rpu_role_per_user
(
    rol_id uuid not null,
    usr_id uuid not null,
    primary key (rol_id, usr_id),
    constraint rpu_rol_fk foreign key (rol_id) references rol_role (rol_id),
    constraint rpu_usr_fk foreign key (usr_id) references usr_user (usr_id)
);

CREATE TABLE adm_admin
(
    adm_id uuid primary key,
    constraint adm_usr_fk foreign key (adm_id) references usr_user (usr_id)
);

CREATE TABLE con_contractor
(
    con_id                uuid primary key,
    con_cnpj              varchar(14)  not null,
    con_company_name      varchar(255) not null,
    con_company_logo_link text,
    constraint con_contractor_cnpj_uk unique (con_cnpj),
    constraint con_usr_fk foreign key (con_id) references usr_user (usr_id)
);

CREATE TABLE fre_freelancer
(
    fre_id             uuid primary key,
    fre_bio            text,
    fre_avatar_link    text,
    fre_price_per_hour decimal(5, 2) not null,
    jnew_id            uuid,
    constraint fre_new_fk foreign key (jnew_id) references new_newsletter (new_id)
);

CREATE TABLE res_resume
(
    res_id                      uuid primary key,
    fre_id                      uuid not null,
    res_portfolio_link          text,
    res_linkedin_link           text,
    res_professional_experience text,
    res_academic_experience     text,
    res_skills                  text,
    constraint res_resume_fre_id_uk unique (fre_id),
    constraint res_fre_fk foreign key (fre_id) references fre_freelancer (fre_id)
);

CREATE TABLE job_job
(
    job_id              uuid primary key,
    con_id              uuid       not null,
    jnew_id             uuid,
    job_publishing_date date    default now(),
    job_description     text,
    job_city            text       not null,
    job_state           varchar(2) not null,
    job_is_remote       boolean default false,
    job_is_open         boolean default true,
    constraint job_job_uk unique (con_id, job_publishing_date),
    constraint job_con_fk foreign key (con_id) references con_contractor (con_id),
    constraint job_new_fk foreign key (jnew_id) references new_newsletter (new_id)
);

CREATE TABLE tag_tag
(
    tag_id    uuid primary key,
    job_id    uuid,
    tag_title varchar(255) not null,
    tag_type  varchar(255) not null,
    constraint tag_job_fk foreign key (job_id) references job_job (job_id),
    constraint tag_tag_tag_title_tag_type unique (tag_title, tag_type)
);

CREATE TABLE tpj_tag_per_job
(
    job_job_id uuid not null,
    tag_tag_id uuid not null,
    primary key (job_job_id, tag_tag_id),
    constraint tpj_job_fk foreign key (job_job_id) references job_job (job_id),
    constraint tpj_tag_fk foreign key (tag_tag_id) references tag_tag (tag_id)
);