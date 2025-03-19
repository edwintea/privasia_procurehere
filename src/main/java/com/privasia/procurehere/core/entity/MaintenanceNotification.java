package com.privasia.procurehere.core.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.FavouriteSupplierStatus;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "PROC_MAINTENANCE_NOTIFICATION")
public class MaintenanceNotification implements Serializable {

    @Id
    @GenericGenerator(name = "idGen", strategy = "uuid.hex")
    @GeneratedValue(generator = "idGen")
    @Column(name = "ID", nullable = false, length = 80)
    private String id;

    @Column(name = "NOTICE_DESCRIPTION", length = 550)
    private String noticeDesc;

    @Column(name = "NOTICE_WEEK", length = 550)
    private String noticeWeek;

    @Column(name = "NOTICE_DAY_OF_WEEK", length = 550)
    private String noticeDayOfWeek;

    @Column(name = "NOTICE_START_TIME", length = 550)
    private String noticeStartTime;

    @Column(name = "NOTICE_END_TIME", length = 550)
    private String noticeEndTime;

    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @Column(name = "CREATED_TIME", nullable = true, length = 20)
    private Date createdDate;

    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @Column(name = "MODIFIED_DATE", nullable = true)
    private Date modifiedDate;

    @Column(name = "IS_ENABLE", nullable = true)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean isEnable = Boolean.FALSE;

    public MaintenanceNotification() {

    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }
    /**
     * @return the createdDate
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * @param createdDate the createdDate to set
     */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * @return the modifiedDate
     */

    public Date getModifiedDate() {
        return modifiedDate;
    }
    /**
     * @param modifiedDate the modifiedDate to set
     */

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
    /**
     * @return the noticeDesc
     */
    public String getNoticeDesc() {
        return noticeDesc;
    }

    /**
     * @param noticeDesc the noticeDesc to set
     */
    public void setNoticeDesc(String noticeDesc) {
        this.noticeDesc = noticeDesc;
    }

    public Boolean getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(Boolean isEnable) {
        this.isEnable = isEnable;
    }

    public String getNoticeWeek() {
        return noticeWeek;
    }

    public void setNoticeWeek(String noticeWeek) {
        this.noticeWeek = noticeWeek;
    }

    public String getNoticeDayOfWeek() {
        return noticeDayOfWeek;
    }

    public void setNoticeDayOfWeek(String noticeDayOfWeek) {
        this.noticeDayOfWeek = noticeDayOfWeek;
    }

    public String getNoticeStartTime() {
        return noticeStartTime;
    }

    public void setNoticeStartTime(String noticeStartTime) {
        this.noticeStartTime = noticeStartTime;
    }

    public String getNoticeEndTime() {
        return noticeEndTime;
    }

    public void setNoticeEndTime(String noticeEndTime) {
        this.noticeEndTime = noticeEndTime;
    }

    public MaintenanceNotification(String id, String noticeDesc, String noticeWeek, String noticeDayOfWeek, String noticeStartTime, String noticeEndTime, Date createdDate, Date modifiedDate, boolean isEnable) {
        super();
        this.id = id;
        this.noticeDesc = noticeDesc;
        this.noticeWeek = noticeWeek;
        this.noticeDayOfWeek = noticeDayOfWeek;
        this.noticeStartTime = noticeStartTime;
        this.noticeEndTime = noticeEndTime;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.isEnable = isEnable;
    }

}
