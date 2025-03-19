------------------------PH-4106 USECASE 1.1---------------


ALTER TABLE proc_test_db.PROC_PR_TEMPLATE
ADD COLUMN MINIMUM_APPROVAL_COUNT INTEGER DEFAULT 0;

ALTER TABLE proc_test_db.PROC_PR_TEMPLATE
ADD COLUMN IS_APPROVAL_PO_VISIBLE  INTEGER DEFAULT 1;

ALTER TABLE proc_test_db.PROC_PR_TEMPLATE
ADD COLUMN IS_APPROVAL_PO_READ_ONLY  INTEGER DEFAULT 0;

ALTER TABLE proc_test_db.PROC_PR_TEMPLATE
ADD COLUMN IS_APPROVAL_PO_OPTIONAL  INTEGER DEFAULT 0;

ALTER TABLE proc_test_db.PROC_PR_TEMPLATE
ADD COLUMN MINIMUM_PO_APPROVAL_COUNT INTEGER DEFAULT 0;

ALTER TABLE proc_test_db.PROC_PR_TEMPLATE
ADD COLUMN IS_APPROVAL_GR_VISIBLE  INTEGER DEFAULT 1;

ALTER TABLE proc_test_db.PROC_PR_TEMPLATE
ADD COLUMN IS_APPROVAL_GR_READ_ONLY  INTEGER DEFAULT 0;

ALTER TABLE proc_test_db.PROC_PR_TEMPLATE
ADD COLUMN IS_APPROVAL_GR_OPTIONAL  INTEGER DEFAULT 0;

ALTER TABLE proc_test_db.PROC_PR_TEMPLATE
ADD COLUMN MINIMUM_GR_APPROVAL_COUNT INTEGER DEFAULT 0;

ALTER TABLE proc_test_db.PROC_PR_TEMPLATE
ADD COLUMN IS_APPROVAL_INVOICE_VISIBLE  INTEGER DEFAULT 1;

ALTER TABLE proc_test_db.PROC_PR_TEMPLATE
ADD COLUMN IS_APPROVAL_INVOICE_READ_ONLY  INTEGER DEFAULT 0;

ALTER TABLE proc_test_db.PROC_PR_TEMPLATE
ADD COLUMN IS_APPROVAL_INVOICE_OPTIONAL  INTEGER DEFAULT 0;

ALTER TABLE proc_test_db.PROC_PR_TEMPLATE
ADD COLUMN MINIMUM_INVOICE_APPROVAL_COUNT INTEGER DEFAULT 0;

CREATE TABLE IF NOT EXISTS proc_test_db.proc_invoice_template
(
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    template_name character varying(128) COLLATE pg_catalog."default" NOT NULL,
    template_description character varying(300) COLLATE pg_catalog."default",
    tenant_id character varying(64) COLLATE pg_catalog."default",
    created_by character varying(64) COLLATE pg_catalog."default" NOT NULL,
    created_date timestamp without time zone NOT NULL,
    modified_by character varying(64) COLLATE pg_catalog."default",
    modified_date timestamp without time zone,
    template_status character varying(32) COLLATE pg_catalog."default",
    is_approval_visible boolean DEFAULT true,
    is_approval_read_only boolean DEFAULT false,
    is_approval_optional boolean DEFAULT false,
    minimum_approval_count integer,
    contract_items_only boolean DEFAULT false,
    lock_budget boolean DEFAULT false,
    payment_termes_id character varying(64) COLLATE pg_catalog."default",
    CONSTRAINT proc_invoice_template_pkey PRIMARY KEY (id),
    CONSTRAINT fk_invoice_tmplate_buyer_id FOREIGN KEY (tenant_id)
        REFERENCES proc_test_db.proc_buyer (buyer_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_invoice_tmplate_created_by FOREIGN KEY (created_by)
        REFERENCES proc_test_db.proc_user (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_invoice_tmplate_modified_by FOREIGN KEY (modified_by)
        REFERENCES proc_test_db.proc_user (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_invoice_tmplate_pay_terms FOREIGN KEY (payment_termes_id)
        REFERENCES proc_test_db.proc_payment_terms (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);
CREATE TABLE IF NOT EXISTS proc_test_db.proc_po_template
(
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    template_name character varying(128) COLLATE pg_catalog."default" NOT NULL,
    template_description character varying(300) COLLATE pg_catalog."default",
    tenant_id character varying(64) COLLATE pg_catalog."default",
    created_by character varying(64) COLLATE pg_catalog."default" NOT NULL,
    created_date timestamp without time zone NOT NULL,
    modified_by character varying(64) COLLATE pg_catalog."default",
    modified_date timestamp without time zone,
    template_status character varying(32) COLLATE pg_catalog."default",
    is_approval_visible boolean DEFAULT true,
    is_approval_read_only boolean DEFAULT false,
    is_approval_optional boolean DEFAULT false,
    minimum_approval_count integer,
    contract_items_only boolean DEFAULT false,
    lock_budget boolean DEFAULT false,
    payment_termes_id character varying(64) COLLATE pg_catalog."default",
    CONSTRAINT proc_po_template_pkey PRIMARY KEY (id),
    CONSTRAINT fk_po_tmplate_buyer_id FOREIGN KEY (tenant_id)
        REFERENCES proc_test_db.proc_buyer (buyer_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_po_tmplate_created_by FOREIGN KEY (created_by)
        REFERENCES proc_test_db.proc_user (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_po_tmplate_modified_by FOREIGN KEY (modified_by)
        REFERENCES proc_test_db.proc_user (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_po_tmplate_pay_terms FOREIGN KEY (payment_termes_id)
        REFERENCES proc_test_db.proc_payment_terms (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS proc_test_db.proc_gr_template
(
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    template_name character varying(128) COLLATE pg_catalog."default" NOT NULL,
    template_description character varying(300) COLLATE pg_catalog."default",
    tenant_id character varying(64) COLLATE pg_catalog."default",
    created_by character varying(64) COLLATE pg_catalog."default" NOT NULL,
    created_date timestamp without time zone NOT NULL,
    modified_by character varying(64) COLLATE pg_catalog."default",
    modified_date timestamp without time zone,
    template_status character varying(32) COLLATE pg_catalog."default",
    is_approval_visible boolean DEFAULT true,
    is_approval_read_only boolean DEFAULT false,
    is_approval_optional boolean DEFAULT false,
    minimum_approval_count integer,
    contract_items_only boolean DEFAULT false,
    lock_budget boolean DEFAULT false,
    payment_termes_id character varying(64) COLLATE pg_catalog."default",
    CONSTRAINT proc_gr_template_pkey PRIMARY KEY (id),
    CONSTRAINT fk_gr_tmplate_buyer_id FOREIGN KEY (tenant_id)
        REFERENCES proc_test_db.proc_buyer (buyer_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_gr_tmplate_created_by FOREIGN KEY (created_by)
        REFERENCES proc_test_db.proc_user (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_gr_tmplate_modified_by FOREIGN KEY (modified_by)
        REFERENCES proc_test_db.proc_user (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_gr_tmplate_pay_terms FOREIGN KEY (payment_termes_id)
        REFERENCES proc_test_db.proc_payment_terms (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);
CREATE TABLE IF NOT EXISTS proc_test_db.proc_po_template_approval
(
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    approval_type character varying(255) COLLATE pg_catalog."default",
    po_template_id character varying(64) COLLATE pg_catalog."default",
    pr_template_id character varying(64) COLLATE pg_catalog."default",
    active integer,
    is_done integer NOT NULL,
    approval_level bigint NOT NULL,
    is_escalated integer,
    CONSTRAINT proc_po_template_approval_pkey PRIMARY KEY (id),
    CONSTRAINT proc_po_template_approval_po_template_id_fkey FOREIGN KEY (po_template_id)
        REFERENCES proc_test_db.proc_po_template (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS proc_test_db.proc_po_template_approval_user
(
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    approval_remarks character varying(500) COLLATE pg_catalog."default",
    user_id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    approval_status character varying(255) COLLATE pg_catalog."default",
    action_date timestamp without time zone,
    next_reminder_time timestamp without time zone,
    reminder_count integer,
    template_approval_id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT proc_po_template_approval_user_pkey PRIMARY KEY (id),
    CONSTRAINT proc_po_template_approval_user_template_approval_id_fkey FOREIGN KEY (template_approval_id)
        REFERENCES proc_test_db.proc_po_template_approval (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE,
    CONSTRAINT proc_po_template_approval_user_user_id_fkey FOREIGN KEY (user_id)
        REFERENCES proc_test_db.proc_user (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS proc_test_db.proc_gr_template_approval
(
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    approval_type character varying(255) COLLATE pg_catalog."default",
    gr_template_id character varying(64) COLLATE pg_catalog."default",
    pr_template_id character varying(64) COLLATE pg_catalog."default",
    approval_level bigint NOT NULL,
    is_done integer NOT NULL,
    active integer,
    is_escalated integer,
    CONSTRAINT proc_gr_template_approval_pkey PRIMARY KEY (id),
    CONSTRAINT proc_gr_template_approval_gr_template_id_fkey FOREIGN KEY (gr_template_id)
        REFERENCES proc_test_db.proc_gr_template (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS proc_test_db.proc_gr_template_approval_user
(
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    approval_remarks character varying(500) COLLATE pg_catalog."default",
    user_id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    approval_status character varying(255) COLLATE pg_catalog."default",
    action_date timestamp without time zone,
    next_reminder_time timestamp without time zone,
    reminder_count integer,
    template_approval_id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT proc_gr_template_approval_user_pkey PRIMARY KEY (id),
    CONSTRAINT proc_gr_template_approval_user_template_approval_id_fkey FOREIGN KEY (template_approval_id)
        REFERENCES proc_test_db.proc_gr_template_approval (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE,
    CONSTRAINT proc_gr_template_approval_user_user_id_fkey FOREIGN KEY (user_id)
        REFERENCES proc_test_db.proc_user (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS proc_test_db.proc_invoice_template_approval
(
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    approval_type character varying(255) COLLATE pg_catalog."default",
    invoice_template_id character varying(64) COLLATE pg_catalog."default",
    pr_template_id character varying(64) COLLATE pg_catalog."default",
    approval_level bigint NOT NULL,
    is_done integer NOT NULL,
    active integer,
    is_escalated integer,
    CONSTRAINT proc_invoice_template_approval_pkey PRIMARY KEY (id),
    CONSTRAINT proc_invoice_template_approval_invoice_template_id_fkey FOREIGN KEY (invoice_template_id)
        REFERENCES proc_test_db.proc_invoice_template (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS proc_test_db.proc_invoice_template_approval_user
(
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    approval_remarks character varying(500) COLLATE pg_catalog."default",
    user_id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    approval_status character varying(255) COLLATE pg_catalog."default",
    action_date timestamp without time zone,
    next_reminder_time timestamp without time zone,
    reminder_count integer,
    template_approval_id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT proc_invoice_template_approval_user_pkey PRIMARY KEY (id),
    CONSTRAINT proc_invoice_template_approval_user_template_approval_id_fkey FOREIGN KEY (template_approval_id)
        REFERENCES proc_test_db.proc_invoice_template_approval (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE,
    CONSTRAINT proc_invoice_template_approval_user_user_id_fkey FOREIGN KEY (user_id)
        REFERENCES proc_test_db.proc_user (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
);

---this is for existing pr template with only visible ticked
update proc_test_db.proc_pr_template set minimum_approval_count=1 where is_approval_visible=1 and is_approval_read_only=0 and is_approval_optional=0;

------------------------PH-4106 USECASE 1.1---------------



------------------------PH-4106 USECASE 1.2---------------

ALTER TABLE proc_test_db.PROC_PR_ITEM
ADD COLUMN price_per_unit numeric(22,6)

UPDATE proc_test_db.proc_pr_item
SET price_per_unit = 1.00
WHERE price_per_unit IS NULL OR price_per_unit = 0;

UPDATE proc_test_db.proc_pr
SET status = 'COMPLETE'
WHERE status = 'APPROVED' AND is_po = '1';

------------------------PH-4106 USECASE 1.2---------------
