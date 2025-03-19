CREATE TABLE proc_test_db.proc_po_events (
    id character varying(64) NOT NULL,
    action_date timestamp(6) without time zone,
    is_billofquantity_req integer,
    is_bq_completed integer,
    budget_amount numeric(22,6),
    cancel_reason character varying(550),
    conclude_remarks character varying(550),
    is_cq_completed integer,
    created_date timestamp(6) without time zone NOT NULL,
    buyer_set_decimal character varying(8),
    is_document_completed integer,
    is_document_req integer,
    is_envelop_completed integer,
    event_description character varying(550),
    is_event_detail_completed integer,
    event_end timestamp(6) without time zone,
    event_id character varying(64),
    event_name character varying(250),
    event_publish_date timestamp(6) without time zone,
    event_start timestamp(6) without time zone,
    event_visibility character varying(255),
    historical_amount numeric(22,6),
    is_meetings_completed integer,
    is_meetings_req integer,
    modified_date timestamp(6) without time zone,
    next_event character varying(64),
    next_event_type character varying(255),
    participation_fees numeric(12,6),
    payment_term character varying(550),
    previous_event character varying(64),
    previous_event_type character varying(255),
    is_questionnaires_req integer,
    referance_number character varying(64),
    start_message_sent integer,
    status character varying(255),
    submission_validity_days bigint,
    is_summary_completed integer,
    is_supplier_completed integer,
    suspend_remarks character varying(550),
    suspension_type character varying(255),
    tenant_id character varying(64) NOT NULL,
    awarded_price numeric(22,6),
    action_by character varying(64),
    currency_id character varying(64),
    cost_center character varying(64),
    created_by character varying(64),
    delivery_address character varying(64),
    event_owner character varying(64) NOT NULL,
    event_category character varying(64),
    modified_by character varying(64),
    participation_fee_currency character varying(64),
    template_id character varying(64),
    business_unit_id character varying(64),
    urgent_event integer,
    delivery_date timestamp(6) without time zone,
    is_awarded integer,
    erp_doc_no character varying(64),
    conclude_date timestamp(6) without time zone,
    conclude_by character varying(64),
    is_erp_enable integer,
    erp_award_ref_id character varying(500),
    erp_award_response character varying(2000),
    view_supplier_name numeric DEFAULT 0,
    allow_close_envelope integer DEFAULT 0,
    allow_add_supplier integer DEFAULT 0,
    allow_to_suspend_event integer DEFAULT 1,
    previous_request character varying(64),
    disable_masking numeric DEFAULT 0,
    unmasked_by character varying(64),
    deposit numeric(12,6),
    deposit_currency character varying(64),
    minimum_supplier_rating double precision,
    maximum_supplier_rating double precision,
    internal_remarks character varying(2000),
    winning_supplier character varying(64),
    winning_price numeric(20,4),
    award_date timestamp(6) without time zone,
    event_push_date timestamp(6) without time zone,
    pr_push_date timestamp(6) without time zone,
    add_bill_of_quantity integer DEFAULT 1,
    rfx_envelope_read_only integer DEFAULT 0,
    rfx_envelope_opening integer DEFAULT 0,
    rfx_env_opening_after character varying(128),
    enable_eval_con_prem integer DEFAULT 0,
    eval_con_prem_eval_count bigint,
    eval_con_prem_non_eval_count bigint,
    evaluation_process_declaration character varying(64),
    supplier_accept_declaration character varying(64),
    is_enable_eval_declaration integer DEFAULT 0,
    is_enable_supplier_declaration integer DEFAULT 0,
    eval_con_prem_disq_sup_count bigint,
    eval_con_prem_pend_sup_count bigint,
    is_enable_approval_reminder integer DEFAULT 0 NOT NULL,
    reminder_hours integer,
    reminder_count integer,
    is_notify_event_owner integer DEFAULT 0 NOT NULL,
    disable_total_amount integer DEFAULT 0 NOT NULL,
    is_enable_suspension_approval integer DEFAULT 0,
    estimated_budget numeric(20,4),
    procurement_method character varying(64),
    procurement_categories character varying(64),
    rfx_envelope_allow_evaluation integer,
    group_code character varying(64),
    allow_disq_sup_download integer,
    is_enable_award_approval integer DEFAULT 0 NOT NULL,
    award_status character varying(255),
    trnsfr_awrd_resp_flag integer DEFAULT 0 NOT NULL,
	po_id character varying(64) NOT NULL
);

CREATE INDEX IDX_PO_EVN_SUP_MSG on PROC_PO_EVNT_MESG_SUPP (SUPPLIER_ID )
CREATE INDEX IDX_PO_EVN_MSG_SUP_EVN_ID on PROC_PO_EVENT_MESSAGE (EVENT_ID);


ALTER TABLE proc_test_db.proc_po_events
ADD COLUMN schedule_of_rate INTEGER DEFAULT 0;

ALTER TABLE proc_test_db.proc_po_events
ADD COLUMN schedule_of_rate_completed INTEGER DEFAULT 0;

ALTER TABLE proc_test_db.proc_po_events
ADD COLUMN add_schedule_of_rate INTEGER DEFAULT 0;

