-- Procure to pay
	UPDATE proc_test_db.proc_access_rights
	SET for_supplier=1
	where acl_value='ROLE_PROC_TO_PAY';

	UPDATE proc_test_db.proc_access_rights
	SET for_owner=0
	where acl_value='ROLE_PROC_TO_PAY';	

	UPDATE proc_test_db.proc_access_rights
	SET for_finance_company=0
	where acl_value='ROLE_PROC_TO_PAY';

-- PO
	UPDATE proc_test_db.proc_access_rights
	SET for_supplier=1
	WHERE acl_value='ROLE_PR_PO';

	UPDATE proc_test_db.proc_access_rights
	SET for_supplier=1
	WHERE acl_value='ROLE_VIEW_PO_LIST';	

INSERT INTO proc_test_db.proc_access_rights(
	acl_value, acl_name, acl_order, for_buyer,for_supplier, parent_id, for_finance_company, for_backoffice_admin, for_funder)
	VALUES ('ROLE_ACCEPT_PO','Accept PO', 3,0,1, 'ROLE_PR_PO', 0, 0, 0);

--DO
UPDATE proc_test_db.proc_access_rights
	SET for_supplier=1
	WHERE acl_value='ROLE_DO_INVOICE';

UPDATE proc_test_db.proc_access_rights
	SET for_supplier=1
	where acl_value='ROLE_VIEW_DO_LIST';

INSERT INTO proc_test_db.proc_access_rights(
	acl_value, acl_name, acl_order,for_buyer, for_supplier, parent_id, for_finance_company, for_backoffice_admin, for_funder)
	VALUES ('ROLE_CREATE_DO','Create DO', 3,0,1, 'ROLE_DO_INVOICE', 0, 0, 0);

-- GR	
	UPDATE proc_test_db.proc_access_rights
	SET for_supplier=1
	WHERE acl_value='ROLE_GRN_LIST';

	UPDATE proc_test_db.proc_access_rights
	SET for_supplier=1
	WHERE acl_value='ROLE_GRN_VIEW_ONLY';

-- CREDIT DEBIT
	UPDATE proc_test_db.proc_access_rights
	SET for_supplier=1
	WHERE acl_value='ROLE_Invoice_CN_DN';

	UPDATE proc_test_db.proc_access_rights
	SET for_supplier=1
	WHERE acl_value='ROLE_VIEW_CN_DN';

	INSERT INTO proc_test_db.proc_access_rights(
	acl_value, acl_name, acl_order,for_buyer, for_supplier, parent_id, for_finance_company, for_backoffice_admin, for_funder)
	VALUES ('ROLE_CREATE_CN_DN','Create Invoice, CN & DN', 3,0,1, 'ROLE_Invoice_CN_DN', 0, 0, 0);
-- Payment Record
	UPDATE proc_test_db.proc_access_rights
	SET for_supplier=1
	WHERE acl_value='ROLE_PAYMENT_RECORD';

	UPDATE proc_test_db.proc_access_rights
	SET for_supplier=1
	WHERE acl_value='ROLE_VIEW_PAYMENT_RECORD';

-- to fix null pointer when loaded for supplier page
	UPDATE proc_test_db.proc_access_rights
	SET for_owner=0
	where acl_value='ROLE_ACCEPT_PO';

	UPDATE proc_test_db.proc_access_rights
	SET for_owner=0
	where acl_value='ROLE_CREATE_CN_DN';

	UPDATE proc_test_db.proc_access_rights
	SET for_owner=0
	where acl_value='ROLE_CREATE_DO';

	UPDATE proc_test_db.proc_access_rights
	SET for_buyer=0
	where acl_value='ROLE_SUPP_BUYER_LIST';

	UPDATE proc_test_db.proc_access_rights SET
	for_owner = '0', for_supplier = '0' WHERE
	acl_value = 'ROLE_ACCEPT_DO';

