--ADD NEW COLUM PH-4113===

ALTER TABLE proc_test_db.proc_po
ADD COLUMN IS_PO_DETAIL_COMPLETED INTEGER DEFAULT 1;

ALTER TABLE proc_test_db.proc_po
ADD COLUMN IS_DOCUMENT_COMPLETED INTEGER DEFAULT 0;

ALTER TABLE proc_test_db.proc_po
ADD COLUMN IS_SUPPLIER_COMPLETED INTEGER DEFAULT 0;

ALTER TABLE proc_test_db.proc_po
ADD COLUMN IS_DELIVERY_COMPLETED INTEGER DEFAULT 0;

ALTER TABLE proc_test_db.proc_po
ADD COLUMN IS_PO_ITEM_COMPLETED INTEGER DEFAULT 0;

ALTER TABLE proc_test_db.proc_po
ADD COLUMN IS_REMARK_COMPLETED INTEGER DEFAULT 0;

ALTER TABLE proc_test_db.proc_po
ADD COLUMN IS_SUMMARY_COMPLETED INTEGER DEFAULT 0;

ALTER TABLE proc_test_db.proc_po_item
ADD COLUMN LOCKED_QUANTITY INTEGER DEFAULT 0;

ALTER TABLE proc_test_db.proc_po_item
ADD COLUMN BALANCE_QUANTITY INTEGER DEFAULT 0;

ALTER TABLE proc_test_db.proc_po_item
ADD COLUMN PRICE_PER_UNIT INTEGER DEFAULT 0;

--set default value for existing data
UPDATE proc_po_item ppi SET locked_quantity = 0; ---- for old will try the down one ----
--UPDATE proc_po_item ppi
--SET locked_quantity = 0
--FROM proc_po pp
--WHERE ppi.po_id = pp.id
--AND pp.po_created_date <= '2023-12-31';

UPDATE proc_po_item
SET balance_quantity = (proc_po_item.item_quantity - pgi.item_quantity)
FROM proc_po pp
LEFT JOIN proc_grn grn ON grn.po_id = pp.id
LEFT JOIN proc_grn_item pgi ON pgi.grn_id = grn.id
WHERE proc_po_item.po_id = pp.id
  AND pp.po_created_date <= '2023-12-31'
  AND grn.status = 'RECEIVED';



UPDATE proc_po_item
SET balance_quantity = CASE
    WHEN grn.id IS NULL THEN proc_po_item.item_quantity
    ELSE (proc_po_item.balance_quantity + COALESCE(pgi.item_quantity, 0))
END
FROM proc_po pp
LEFT JOIN proc_grn grn ON grn.po_id = pp.id
LEFT JOIN proc_grn_item pgi ON pgi.grn_id = grn.id
WHERE proc_po_item.po_id = pp.id
  AND pp.po_created_date <= '2023-12-31'
  AND (grn.status = 'CANCELLED' OR grn.id IS NULL);

UPDATE proc_test_db.proc_po_item set PRICE_PER_UNIT =1;

--UPDATE proc_test_db.proc_po_item
--SET PRICE_PER_UNIT = pri.price_per_unit
--FROM proc_test_db.proc_pr_item pri
--WHERE proc_po_item.id = pri.id;

-- END PH-4113

ALTER TABLE proc_test_db.proc_po
ADD COLUMN IS_CANCELLED INTEGER DEFAULT 0;

ALTER TABLE proc_test_db.proc_po
ADD COLUMN is_from_integration INTEGER DEFAULT 0;

ALTER TABLE proc_test_db.proc_po
ADD COLUMN is_sent_to_sap INTEGER DEFAULT 0;

ALTER TABLE proc_test_db.proc_po
ADD COLUMN is_sent_to_sap_failed INTEGER DEFAULT 0;

ALTER TABLE proc_test_db.proc_erp_setup
ADD COLUMN ENABLE_PO_SEND_TO_SAP SMALLINT DEFAULT 0;

ALTER TABLE ONLY proc_test_db.proc_po_team ADD CONSTRAINT proc_po_team_pkey PRIMARY KEY (id);
ALTER TABLE ONLY proc_test_db.proc_po_team ADD CONSTRAINT fk_po_team FOREIGN KEY (po_id) REFERENCES proc_test_db.proc_po(id);
ALTER TABLE ONLY proc_test_db.proc_po_team ADD CONSTRAINT fk6em2l3rtfmt4vhjmv1a5ptj4t FOREIGN KEY (user_id) REFERENCES proc_test_db.proc_user(id);