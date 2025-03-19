-- Table: proc_test_db.proc_user_business_unit_mapping

-- DROP TABLE IF EXISTS proc_test_db.proc_user_business_unit_mapping;

CREATE TABLE IF NOT EXISTS proc_test_db.proc_user_business_unit_mapping
(
    user_id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    business_unit_id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT pk_proc_user_business_unit_mapping PRIMARY KEY (user_id, business_unit_id),
    CONSTRAINT fk_user_id FOREIGN KEY (user_id)
        REFERENCES proc_test_db.proc_user (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_business_unit_id FOREIGN KEY (business_unit_id)
        REFERENCES proc_test_db.proc_business_unit (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

COMMENT ON TABLE proc_test_db.proc_user_business_unit_mapping IS 'Maps users to business units';

ALTER TABLE IF EXISTS proc_test_db.proc_user_business_unit_mapping
    OWNER to postgres;