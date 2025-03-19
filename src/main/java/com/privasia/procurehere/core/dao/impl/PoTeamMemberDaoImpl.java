package com.privasia.procurehere.core.dao.impl;

import com.privasia.procurehere.core.dao.PoTeamMemberDao;
import com.privasia.procurehere.core.entity.PoTeamMember;
import com.privasia.procurehere.core.pojo.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.Collections;
import java.util.List;

/**
 * @author parveen
 */
@Repository
public class PoTeamMemberDaoImpl extends GenericDaoImpl<PoTeamMember, String> implements PoTeamMemberDao {

	private static final Logger LOG = LogManager.getLogger(PoTeamMemberDaoImpl.class);

}

