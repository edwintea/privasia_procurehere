package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author parveen
 */
@Entity
@Table(name = "PROC_GR_TEMPLATE_APPROVAL")
public class GrTemplateApproval extends EventApproval implements Serializable {

    private static final long serialVersionUID = 808636093331279629L;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "PR_TEMPLATE_ID", foreignKey = @ForeignKey(name = "FK_GR_APRVAL_TMPL_ID"))
    private PrTemplate prTemplate;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "grApproval", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<GrTemplateApprovalUser> approvalUsers;

    /**
     * @return the grTemplate
     */
    public PrTemplate getPrTemplate() {
        return prTemplate;
    }

    /**
     * @param prTemplate the grTemplate to set
     */
    public void setPrTemplate(PrTemplate prTemplate) {
        this.prTemplate = prTemplate;
    }

    /**
     * @return the approvalUsers
     */
    public List<GrTemplateApprovalUser> getApprovalUsers() {
        return approvalUsers;
    }

    /**
     * @param approvalUsers the approvalUsers to set
     */
    public void setApprovalUsers(List<GrTemplateApprovalUser> approvalUsers) {
        this.approvalUsers = approvalUsers;
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
        return "TemplateGrApproval [ " + super.toLogString() + "]";
    }

}

