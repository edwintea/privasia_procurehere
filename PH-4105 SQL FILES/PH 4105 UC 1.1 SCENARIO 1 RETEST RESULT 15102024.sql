UPDATE proc_test_db.proc_access_rights
SET for_buyer=0 WHERE acl_value='ROLE_REQUEST_ALL';

UPDATE proc_test_db.proc_access_rights
SET for_buyer=0 WHERE acl_value='ROLE_REQUEST_SELF';
-- this data only existed in test db not in demo db
-- when run this will have impact on uc 1.2 that already passed testing effecting the javascript triggers in userRole.js