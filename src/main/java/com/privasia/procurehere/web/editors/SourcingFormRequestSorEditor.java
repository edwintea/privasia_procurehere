package com.privasia.procurehere.web.editors;

import com.privasia.procurehere.core.dao.SourcingFormRequestSorDao;
import com.privasia.procurehere.core.entity.SourcingFormRequestSor;
import com.privasia.procurehere.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.beans.PropertyEditorSupport;

@Component
public class SourcingFormRequestSorEditor extends PropertyEditorSupport {

    @Autowired
    SourcingFormRequestSorDao sourcingFormRequestSorDao;

    @Override
    public String getAsText() {
        return getValue() == null ? "" : getValue().toString();
    }

    @Override
    public void setAsText(String value) {
        if (StringUtils.checkString(value).length() > 0) {
            SourcingFormRequestSor sourcingFormRequestBq = sourcingFormRequestSorDao.findById(value);
            this.setValue(sourcingFormRequestBq);
        }
    }

}
