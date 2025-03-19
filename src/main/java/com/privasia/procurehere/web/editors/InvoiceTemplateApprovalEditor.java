package com.privasia.procurehere.web.editors;

import java.beans.PropertyEditorSupport;

import com.privasia.procurehere.core.entity.InvoiceTemplateApprovalUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.PrTemplateApprovalUser;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author parveen
 */
@Component
public class InvoiceTemplateApprovalEditor extends PropertyEditorSupport {

    @SuppressWarnings("unused")
    private static final Logger LOG = LogManager.getLogger(InvoiceTemplateApprovalEditor.class);

    @Autowired
    UserDao userDao;

    @Override
    public void setAsText(String value) {
        if (StringUtils.checkString(value).length() > 0) {
            User user = userDao.findById(value);
            this.setValue(new InvoiceTemplateApprovalUser(user));
        }
    }

    @Override
    public String getAsText() {
        return getValue() == null ? "" : ((InvoiceTemplateApprovalUser) getValue()).getUser().getId();
    }
}
