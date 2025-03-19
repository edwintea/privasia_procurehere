package com.privasia.procurehere.core.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.enums.ApprovalStatus;

/**
 * @author parveen
 */
@Entity
@Table(name = "PROC_INVOICE_TEMPLATE_APPROVAL_USER")
public class InvoiceTemplateApprovalUser extends ApprovalUser implements Serializable {

    private static final long serialVersionUID = -7210748298650055593L;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "TEMPLATE_APPROVAL_ID", foreignKey = @ForeignKey(name = "FK_INVOICE_APPROVAL_USER_TMPL_ID"))
    private InvoiceTemplateApproval invoiceApproval;

    public InvoiceTemplateApprovalUser() {
        setApprovalStatus(ApprovalStatus.PENDING);
    }

    public InvoiceTemplateApprovalUser(User user) {
        setApprovalStatus(ApprovalStatus.PENDING);
        if (user != null) {
            this.setUser(user);
            this.setId(user.getId());
        }
    }

    /**
     * @return the approval
     */
    public InvoiceTemplateApproval getInvoiceApproval() {
        return invoiceApproval;
    }

    /**
     * @param invoiceApproval the approval to set
     */
    public void setInvoiceApproval(InvoiceTemplateApproval invoiceApproval) {
        this.invoiceApproval = invoiceApproval;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public String toLogString() {
        return "TemplateInvoiceApprovalUser [ " + super.toLogString() + "]";
    }

}
