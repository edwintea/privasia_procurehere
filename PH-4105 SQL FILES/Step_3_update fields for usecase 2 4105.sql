-- PR
UPDATE proc_test_db.proc_access_rights
SET parent_id='ROLE_PURCHASE_REQUISITION' WHERE acl_value='ROLE_PR_CREATE';

UPDATE proc_test_db.proc_access_rights
SET parent_id='ROLE_PURCHASE_REQUISITION' WHERE acl_value='ROLE_VIEW_PR_DRAFT';

UPDATE proc_test_db.proc_access_rights
SET acl_name='View PR List' WHERE acl_value='ROLE_VIEW_PR_DRAFT';

-- test case PR
UPDATE proc_test_db.proc_access_rights
SET acl_order=2 WHERE acl_value='ROLE_PR_CREATE';

UPDATE proc_test_db.proc_access_rights
SET acl_order=1 WHERE acl_value='ROLE_VIEW_PR_DRAFT';
-- PO
UPDATE proc_test_db.proc_access_rights
SET acl_name='View PO List' WHERE acl_value='ROLE_VIEW_PO_LIST';

UPDATE proc_test_db.proc_access_rights
SET acl_name='Purchase Order (PO)' WHERE acl_value='ROLE_PR_PO';

UPDATE proc_test_db.proc_access_rights
SET parent_id='ROLE_PROC_TO_PAY' WHERE acl_value='ROLE_PR_PO';

UPDATE proc_test_db.proc_access_rights
SET acl_order=2 WHERE acl_value='ROLE_PR_PO';
-- DO
UPDATE proc_test_db.proc_access_rights
SET acl_name='Delivery Order (DO)' WHERE acl_value='ROLE_DO_INVOICE';

UPDATE proc_test_db.proc_access_rights
SET parent_id='ROLE_PROC_TO_PAY' WHERE acl_value='ROLE_DO_INVOICE';

UPDATE proc_test_db.proc_access_rights
SET for_buyer=0 WHERE acl_value='ROLE_VIEW_INVOICE_LIST';

INSERT INTO proc_test_db.proc_access_rights(
acl_value, acl_name, acl_order, for_buyer, parent_id, for_finance_company, for_backoffice_admin, for_funder)
VALUES ('ROLE_ACCEPT_DO','Accept DO', 3,1, 'ROLE_DO_INVOICE', 0, 0, 0);

UPDATE proc_test_db.proc_access_rights
SET acl_order=3 WHERE acl_value='ROLE_DO_INVOICE';
-- GR
UPDATE proc_test_db.proc_access_rights
SET acl_name='Goods Receipt (GR)' WHERE acl_value='ROLE_GRN_LIST';

UPDATE proc_test_db.proc_access_rights
SET parent_id='ROLE_PROC_TO_PAY' WHERE acl_value='ROLE_GRN_LIST';

UPDATE proc_test_db.proc_access_rights
SET acl_name='Create GR' WHERE acl_value='ROLE_GRN_EDIT';

UPDATE proc_test_db.proc_access_rights
SET acl_name='View GR List' WHERE acl_value='ROLE_GRN_VIEW_ONLY';

UPDATE proc_test_db.proc_access_rights
SET acl_order=4 WHERE acl_value='ROLE_GRN_LIST';

-- test case GR
UPDATE proc_test_db.proc_access_rights
SET acl_order=2 WHERE acl_value='ROLE_GRN_EDIT';

UPDATE proc_test_db.proc_access_rights
SET acl_order=1 WHERE acl_value='ROLE_GRN_VIEW_ONLY';
-- test server db fixes only

UPDATE proc_test_db.proc_access_rights
SET acl_name='Account Payable (AP)' WHERE acl_value='ROLE_ACCOUNT_PAYABLE';

UPDATE proc_test_db.proc_access_rights
SET acl_name='Invoice, Credit Note(CN) & Debit Note(DN)' WHERE acl_value='ROLE_Invoice_CN_DN';
