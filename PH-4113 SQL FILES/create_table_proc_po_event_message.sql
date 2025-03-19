CREATE TABLE proc_test_db.proc_po_event_message (
    id character varying(64) NOT NULL,
    content_type character varying(160),
    created_date timestamp(6) without time zone NOT NULL,
    file_data bytea,
    file_name character varying(500),
    message_text character varying(2000) NOT NULL,
    message_subject character varying(250) NOT NULL,
    tenant_id character varying(64),
    send_by_buyer integer,
    send_by_supplier integer,
    buyer_id character varying(64),
    created_by character varying(64),
    event_id character varying(64),
    parent_id character varying(64)
);