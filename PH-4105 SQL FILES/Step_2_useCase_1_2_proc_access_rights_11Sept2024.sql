INSERT INTO proc_test_db.proc_access_rights(
acl_value, acl_name, acl_order, for_buyer, for_owner, for_supplier,  for_finance_company, for_backoffice_admin, for_funder)
VALUES ('ROLE_PROC_TO_PAY', 'Procure-to-Pay', 13, 1, 0, 0,  0, 0, 0);
--PR
INSERT INTO proc_test_db.proc_access_rights(
acl_value, acl_name, acl_order, for_buyer, for_owner, for_supplier, parent_id, for_finance_company, for_backoffice_admin, for_funder)
VALUES ('ROLE_PURCHASE_REQUISITION', 'Purchase Requisition (PR)', 1, 1, 0, 0,'ROLE_PROC_TO_PAY',  0, 0, 0);
-- PO
INSERT INTO proc_test_db.proc_access_rights(
acl_value, acl_name, acl_order, for_buyer, for_owner, for_supplier, parent_id, for_finance_company, for_backoffice_admin, for_funder)
VALUES ('ROLE_PO_CREATE', 'Create PO', 2, 1, 0, 0,'ROLE_PR_PO',  0, 0, 0);
-- CREDIT DEBIT
INSERT INTO proc_test_db.proc_access_rights(
acl_value, acl_name, acl_order, for_buyer, for_owner, for_supplier, parent_id, for_finance_company, for_backoffice_admin, for_funder)
VALUES ('ROLE_Invoice_CN_DN', 'Invoice, Credit Note(CN) & Debit Note(DN)', 5, 1, 0, 0,'ROLE_PROC_TO_PAY',  0, 0, 0);
-- supposedly below replace ROLE_VIEW_INVOICE_LIST instead of making a new one
INSERT INTO proc_test_db.proc_access_rights(
acl_value, acl_name, acl_order, for_buyer, for_owner, for_supplier, parent_id, for_finance_company, for_backoffice_admin, for_funder)
VALUES ('ROLE_VIEW_CN_DN', 'View Invoice, CN & DN List', 1, 1, 0, 0,'ROLE_Invoice_CN_DN',  0, 0, 0);

INSERT INTO proc_test_db.proc_access_rights(
acl_value, acl_name, acl_order, for_buyer, for_owner, for_supplier, parent_id, for_finance_company, for_backoffice_admin, for_funder)
VALUES ('ROLE_ACCEPT_CN_DN', 'Accept Invoice, CN & DN', 2, 1, 0, 0,'ROLE_Invoice_CN_DN',  0, 0, 0);
-- ACCOUNT PAYABLE
INSERT INTO proc_test_db.proc_access_rights(
acl_value, acl_name, acl_order, for_buyer, for_owner, for_supplier, parent_id, for_finance_company, for_backoffice_admin, for_funder)
VALUES ('ROLE_ACCOUNT_PAYABLE', 'Account Payable(AP)', 6, 1, 0, 0,'ROLE_PROC_TO_PAY',  0, 0, 0);

INSERT INTO proc_test_db.proc_access_rights(
acl_value, acl_name, acl_order, for_buyer, for_owner, for_supplier, parent_id, for_finance_company, for_backoffice_admin, for_funder)
VALUES ('ROLE_VIEW_AP', 'View AP List',1, 1, 0, 0,'ROLE_ACCOUNT_PAYABLE',  0, 0, 0);


--PAYMENT RECORD
INSERT INTO proc_test_db.proc_access_rights(
acl_value, acl_name, acl_order, for_buyer, for_owner, for_supplier, parent_id, for_finance_company, for_backoffice_admin, for_funder)
VALUES ('ROLE_PAYMENT_RECORD', 'Payment Record', 7, 1, 0, 0,'ROLE_PROC_TO_PAY',  0, 0, 0);

INSERT INTO proc_test_db.proc_access_rights(
acl_value, acl_name, acl_order, for_buyer, for_owner, for_supplier, parent_id, for_finance_company, for_backoffice_admin, for_funder)
VALUES ('ROLE_VIEW_PAYMENT_RECORD', 'View Payment Record List', 1, 1, 0, 0,'ROLE_PAYMENT_RECORD',  0, 0, 0);

INSERT INTO proc_test_db.proc_access_rights(
acl_value, acl_name, acl_order, for_buyer, for_owner, for_supplier, parent_id, for_finance_company, for_backoffice_admin, for_funder)
VALUES ('ROLE_CREATE_PAYMENT_RECORD', 'Create Payment Record', 2, 1, 0, 0,'ROLE_PAYMENT_RECORD',  0, 0, 0);