SELECT DISTINCT a.id,
                a.event_name AS eventname,
                a.created_date AS createddate,
                a.login_id AS createdby,
                a.modified_date AS modifieddate,
                a.event_type AS type,
                a.event_start AS eventstart,
                a.event_end AS eventend,
                a.referance_number AS referencenumber,
                a.business_unit_name AS unitname,
                a.event_id AS syseventid,
                a.user_name AS eventuser,
                a.tenant_id AS tenantid,
                a.envelopopener,
                a.leadevaluator,
                a.tmuserid,
                a.evlid,
                a.usrid,
                a.cuuserid,
                a.event_owner AS eventowner,
                a.bizunitid,
                a.raauid,
                a.sauserid
FROM ( SELECT DISTINCT e.id,
                       e.is_billofquantity_req,
                       e.budget_amount,
                       e.created_date,
                       e.buyer_set_decimal,
                       e.is_document_req,
                       e.event_description,
                       e.event_end,
                       e.event_id,
                       e.event_name,
                       e.event_publish_date,
                       e.event_start,
                       e.event_visibility,
                       e.historical_amount,
                       e.is_meetings_req,
                       e.modified_date,
                       e.participation_fees,
                       e.payment_term,
                       e.is_questionnaires_req,
                       e.referance_number,
                       e.status,
                       e.submission_validity_days,
                       e.tenant_id,
                       e.currency_id,
                       e.cost_center,
                       e.created_by,
                       e.delivery_address,
                       e.event_owner,
                       e.event_category,
                       e.modified_by,
                       e.participation_fee_currency,
                       e.template_id,
                       e.is_bq_completed,
                       e.is_cq_completed,
                       e.is_document_completed,
                       e.is_envelop_completed,
                       e.is_event_detail_completed,
                       e.is_meetings_completed,
                       e.is_summary_completed,
                       e.is_supplier_completed,
                       e.action_date,
                       e.action_by,
                       bu.business_unit_name,
                       bu.id AS bizunitid,
                       e.suspension_type,
                       'Request for Tender'::text AS event_type,
               en.envelop_opener AS envelopopener,
                       en.lead_evaluater AS leadevaluator,
                       tm.user_id AS tmuserid,
                       evl.id AS evlid,
                       usr.user_id AS usrid,
                       cu.user_id AS cuuserid,
                       puser.login_id,
                       puser.user_name,
                       raau.user_id AS raauid,
                       sauser.user_id AS sauserid
       FROM proc_test_db.proc_rft_events e
                LEFT JOIN proc_test_db.proc_business_unit bu ON e.business_unit_id::text = bu.id::text
             LEFT JOIN proc_test_db.proc_rft_envelop en ON en.event_id::text = e.id::text
           LEFT JOIN proc_test_db.proc_rft_team tm ON tm.event_id::text = e.id::text
           LEFT JOIN proc_test_db.proc_rft_evaluator_user ev ON ev.rft_envelope_id::text = en.id::text
           LEFT JOIN proc_test_db.proc_user evl ON ev.user_id::text = evl.id::text
           LEFT JOIN proc_test_db.proc_rft_event_approval app ON app.event_id::text = e.id::text
           LEFT JOIN proc_test_db.proc_rft_approval_user usr ON usr.rft_event_approval_id::text = app.id::text
           LEFT JOIN proc_test_db.proc_rft_eval_con_users cu ON cu.event_id::text = e.id::text
           LEFT JOIN proc_test_db.proc_user puser ON e.created_by::text = puser.id::text
           LEFT JOIN proc_test_db.proc_rft_award_approval raa ON raa.event_id::text = e.id::text
           LEFT JOIN proc_test_db.proc_rft_award_appr_user raau ON raau.rft_event_award_appr_id::text = raa.id::text
           LEFT JOIN proc_test_db.proc_rft_suspension_approval sa ON sa.event_id::text=e.id::text
           LEFT JOIN proc_test_db.proc_rft_suspension_appr_user sauser on sauser.rft_event_susp_appr_id::text=sa.id::text
       WHERE e.status::text = 'FINISHED'::text
       UNION
       SELECT DISTINCT e.id,
           e.is_billofquantity_req,
           e.budget_amount,
           e.created_date,
           e.buyer_set_decimal,
           e.is_document_req,
           e.event_description,
           e.event_end,
           e.event_id,
           e.event_name,
           e.event_publish_date,
           e.event_start,
           e.event_visibility,
           e.historical_amount,
           e.is_meetings_req,
           e.modified_date,
           e.participation_fees,
           e.payment_term,
           e.is_questionnaires_req,
           e.referance_number,
           e.status,
           e.submission_validity_days,
           e.tenant_id,
           e.currency_id,
           e.cost_center,
           e.created_by,
           e.delivery_address,
           e.event_owner,
           e.event_category,
           e.modified_by,
           e.participation_fee_currency,
           e.template_id,
           e.is_bq_completed,
           e.is_cq_completed,
           e.is_document_completed,
           e.is_envelop_completed,
           e.is_event_detail_completed,
           e.is_meetings_completed,
           e.is_summary_completed,
           e.is_supplier_completed,
           e.action_date,
           e.action_by,
           bu.business_unit_name,
           bu.id AS bizunitid,
           e.suspension_type,
           'Request for Proposal'::text AS event_type,
           en.envelop_opener AS envelopopener,
           en.lead_evaluater AS leadevaluator,
           tm.user_id AS tmuserid,
           evl.id AS evlid,
           usr.user_id AS usrid,
           cu.user_id AS cuuserid,
           puser.login_id,
           puser.user_name,
           raau.user_id AS raauid,
           sauser.user_id AS sauserid
       FROM proc_test_db.proc_rfp_events e
           LEFT JOIN proc_test_db.proc_business_unit bu ON e.business_unit_id::text = bu.id::text
           LEFT JOIN proc_test_db.proc_rfp_envelop en ON en.event_id::text = e.id::text
           LEFT JOIN proc_test_db.proc_rfp_team tm ON tm.event_id::text = e.id::text
           LEFT JOIN proc_test_db.proc_rfp_evaluator_user ev ON ev.rfp_envelope_id::text = en.id::text
           LEFT JOIN proc_test_db.proc_user evl ON ev.user_id::text = evl.id::text
           LEFT JOIN proc_test_db.proc_rfp_event_approval app ON app.event_id::text = e.id::text
           LEFT JOIN proc_test_db.proc_rfp_approval_user usr ON usr.rfp_event_approval_id::text = app.id::text
           LEFT JOIN proc_test_db.proc_rfp_eval_con_users cu ON cu.event_id::text = e.id::text
           LEFT JOIN proc_test_db.proc_user puser ON e.created_by::text = puser.id::text
           LEFT JOIN proc_test_db.proc_rfp_award_approval raa ON raa.event_id::text = e.id::text
           LEFT JOIN proc_test_db.proc_rfp_award_appr_user raau ON raau.rfp_event_award_appr_id::text = raa.id::text
           LEFT JOIN proc_test_db.proc_rfp_suspension_approval sa on sa.event_id::text=e.id::text
           LEFT JOIN proc_test_db.proc_rfp_suspension_appr_user sauser on sauser.rfp_event_susp_appr_id::text=sa.id::text
       WHERE e.status::text = 'FINISHED'::text
       UNION
       SELECT DISTINCT e.id,
           e.is_billofquantity_req,
           e.budget_amount,
           e.created_date,
           e.buyer_set_decimal,
           e.is_document_req,
           e.event_description,
           e.event_end,
           e.event_id,
           e.event_name,
           e.event_publish_date,
           e.event_start,
           e.event_visibility,
           e.historical_amount,
           e.is_meetings_req,
           e.modified_date,
           e.participation_fees,
           e.payment_term,
           e.is_questionnaires_req,
           e.referance_number,
           e.status,
           e.submission_validity_days,
           e.tenant_id,
           e.currency_id,
           e.cost_center,
           e.created_by,
           e.delivery_address,
           e.event_owner,
           e.event_category,
           e.modified_by,
           e.participation_fee_currency,
           e.template_id,
           e.is_bq_completed,
           e.is_cq_completed,
           e.is_document_completed,
           e.is_envelop_completed,
           e.is_event_detail_completed,
           e.is_meetings_completed,
           e.is_summary_completed,
           e.is_supplier_completed,
           e.action_date,
           e.action_by,
           bu.business_unit_name,
           bu.id AS bizunitid,
           e.suspension_type,
           'Request for Information'::text AS event_type,
           en.envelop_opener AS envelopopener,
           en.lead_evaluater AS leadevaluator,
           tm.user_id AS tmuserid,
           evl.id AS evlid,
           usr.user_id AS usrid,
           cu.user_id AS cuuserid,
           puser.login_id,
           puser.user_name,
           NULL::text AS raauid,
           sauser.user_id AS sauserid
       FROM proc_test_db.proc_rfi_events e
           LEFT JOIN proc_test_db.proc_business_unit bu ON e.business_unit_id::text = bu.id::text
           LEFT JOIN proc_test_db.proc_rfi_envelop en ON en.event_id::text = e.id::text
           LEFT JOIN proc_test_db.proc_rfi_team tm ON tm.event_id::text = e.id::text
           LEFT JOIN proc_test_db.proc_rfi_evaluator_user ev ON ev.rfi_envelope_id::text = en.id::text
           LEFT JOIN proc_test_db.proc_user evl ON ev.user_id::text = evl.id::text
           LEFT JOIN proc_test_db.proc_rfi_event_approval app ON app.event_id::text = e.id::text
           LEFT JOIN proc_test_db.proc_rfi_approval_user usr ON usr.rfi_event_approval_id::text = app.id::text
           LEFT JOIN proc_test_db.proc_rfi_eval_con_users cu ON cu.event_id::text = e.id::text
           LEFT JOIN proc_test_db.proc_user puser ON e.created_by::text = puser.id::text
           LEFT JOIN proc_test_db.proc_rfi_suspension_approval sa on sa.event_id::text=e.id::text
           LEFT JOIN proc_test_db.proc_rfi_suspension_appr_user sauser on sauser.rfi_event_susp_appr_id::text=sa.id::text
       WHERE e.status::text = 'FINISHED'::text
       UNION
       SELECT DISTINCT e.id,
           e.is_billofquantity_req,
           e.budget_amount,
           e.created_date,
           e.buyer_set_decimal,
           e.is_document_req,
           e.event_description,
           e.event_end,
           e.event_id,
           e.event_name,
           e.event_publish_date,
           e.event_start,
           e.event_visibility,
           e.historical_amount,
           e.is_meetings_req,
           e.modified_date,
           e.participation_fees,
           e.payment_term,
           e.is_questionnaires_req,
           e.referance_number,
           e.status,
           e.submission_validity_days,
           e.tenant_id,
           e.currency_id,
           e.cost_center,
           e.created_by,
           e.delivery_address,
           e.event_owner,
           e.event_category,
           e.modified_by,
           e.participation_fee_currency,
           e.template_id,
           e.is_bq_completed,
           e.is_cq_completed,
           e.is_document_completed,
           e.is_envelop_completed,
           e.is_event_detail_completed,
           e.is_meetings_completed,
           e.is_summary_completed,
           e.is_supplier_completed,
           e.action_date,
           e.action_by,
           bu.business_unit_name,
           bu.id AS bizunitid,
           e.suspension_type,
           'Request for Quotation'::text AS event_type,
           en.envelop_opener AS envelopopener,
           en.lead_evaluater AS leadevaluator,
           tm.user_id AS tmuserid,
           evl.id AS evlid,
           usr.user_id AS usrid,
           cu.user_id AS cuuserid,
           puser.login_id,
           puser.user_name,
           raau.user_id AS raauid,
           sauser.user_id AS sauserid
       FROM proc_test_db.proc_rfq_events e
           LEFT JOIN proc_test_db.proc_business_unit bu ON e.business_unit_id::text = bu.id::text
           LEFT JOIN proc_test_db.proc_rfq_envelop en ON en.event_id::text = e.id::text
           LEFT JOIN proc_test_db.proc_rfq_team tm ON tm.event_id::text = e.id::text
           LEFT JOIN proc_test_db.proc_rfq_evaluator_user ev ON ev.rfq_envelope_id::text = en.id::text
           LEFT JOIN proc_test_db.proc_user evl ON ev.user_id::text = evl.id::text
           LEFT JOIN proc_test_db.proc_rfq_event_approval app ON app.event_id::text = e.id::text
           LEFT JOIN proc_test_db.proc_rfq_approval_user usr ON usr.rfq_event_approval_id::text = app.id::text
           LEFT JOIN proc_test_db.proc_rfq_eval_con_users cu ON cu.event_id::text = e.id::text
           LEFT JOIN proc_test_db.proc_user puser ON e.created_by::text = puser.id::text
           LEFT JOIN proc_test_db.proc_rfq_award_approval raa ON raa.event_id::text = e.id::text
           LEFT JOIN proc_test_db.proc_rfq_award_appr_user raau ON raau.rfq_event_award_appr_id::text = raa.id::text
           LEFT JOIN proc_test_db.proc_rfq_suspension_approval sa on sa.event_id::text=e.id::text
           LEFT JOIN proc_test_db.proc_rfq_suspension_appr_user sauser on sauser.rfq_event_susp_appr_id::text=sa.id::text
       WHERE e.status::text = 'FINISHED'::text
       UNION
       SELECT DISTINCT e.id,
           e.is_billofquantity_req,
           e.budget_amount,
           e.created_date,
           e.buyer_set_decimal,
           e.is_document_req,
           e.event_description,
           e.event_end,
           e.event_id,
           e.event_name,
           e.event_publish_date,
           e.event_start,
           e.event_visibility,
           e.historical_amount,
           e.is_meetings_req,
           e.modified_date,
           e.participation_fees,
           e.payment_term,
           e.is_questionnaires_req,
           e.referance_number,
           e.status,
           e.submission_validity_days,
           e.tenant_id,
           e.currency_id,
           e.cost_center,
           e.created_by,
           e.delivery_address,
           e.event_owner,
           e.event_category,
           e.modified_by,
           e.participation_fee_currency,
           e.template_id,
           e.is_bq_completed,
           e.is_cq_completed,
           e.is_document_completed,
           e.is_envelop_completed,
           e.is_event_detail_completed,
           e.is_meetings_completed,
           e.is_summary_completed,
           e.is_supplier_completed,
           e.action_date,
           e.action_by,
           bu.business_unit_name,
           bu.id AS bizunitid,
           e.suspension_type,
           'Request for Auction'::text AS event_type,
           en.envelop_opener AS envelopopener,
           en.lead_evaluater AS leadevaluator,
           tm.user_id AS tmuserid,
           evl.id AS evlid,
           usr.user_id AS usrid,
           cu.user_id AS cuuserid,
           puser.login_id,
           puser.user_name,
           raau.user_id AS raauid,
           sauser.user_id AS sauserid
       FROM proc_test_db.proc_rfa_events e
           LEFT JOIN proc_test_db.proc_business_unit bu ON e.business_unit_id::text = bu.id::text
           LEFT JOIN proc_test_db.proc_rfa_envelop en ON en.event_id::text = e.id::text
           LEFT JOIN proc_test_db.proc_rfa_team tm ON tm.event_id::text = e.id::text
           LEFT JOIN proc_test_db.proc_rfa_evaluator_user ev ON ev.rfa_envelope_id::text = en.id::text
           LEFT JOIN proc_test_db.proc_user evl ON ev.user_id::text = evl.id::text
           LEFT JOIN proc_test_db.proc_rfa_event_approval app ON app.event_id::text = e.id::text
           LEFT JOIN proc_test_db.proc_rfa_approval_user usr ON usr.rfa_event_approval_id::text = app.id::text
           LEFT JOIN proc_test_db.proc_rfa_eval_con_users cu ON cu.event_id::text = e.id::text
           LEFT JOIN proc_test_db.proc_user puser ON e.created_by::text = puser.id::text
           LEFT JOIN proc_test_db.proc_rfa_award_approval raa ON raa.event_id::text = e.id::text
           LEFT JOIN proc_test_db.proc_rfa_award_appr_user raau ON raau.rfa_event_award_appr_id::text = raa.id::text
           LEFT JOIN proc_test_db.proc_rfa_suspension_approval sa on sa.event_id::text=e.id::text
           LEFT JOIN proc_test_db.proc_rfa_suspension_appr_user sauser on sauser.rfa_event_susp_appr_id::text=sa.id::text
       WHERE e.status::text = 'FINISHED'::text) a;