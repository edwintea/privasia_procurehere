package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import org.hibernate.annotations.Type;

/**
 * @author shubham
 */

@Entity
@Table(name = "PROC_BUDGET_DOCUMENT")
public class BudgetDocument implements Serializable {

	private static final long serialVersionUID = 2595246950126976213L;

		@Id
		@GenericGenerator(name = "idGen", strategy = "uuid.hex")
		@GeneratedValue(generator = "idGen")
		@Column(name = "ID", length = 64)
		private String id;

		@Lob
	@Type(type = "org.hibernate.type.BinaryType")
		@Column(name = "FILE_DATA")
		private byte[] fileData;

		@Column(name = "FILE_NAME", length = 500)
		private String fileName;

		@Column(name = "DESCRIPTION", length = 300)
		@Size(min = 0, max = 300, message = "{event.doc.file.maxlimit}")
		private String description;

		@JsonSerialize(using = CustomDateTimeSerializer.class)
		@Temporal(TemporalType.TIMESTAMP)
		@Column(name = "UPLOAD_DATE")
		private Date uploadDate;

		@Column(name = "CONTENT_TYPE", length = 160)
		private String contentType;

		@Column(name = "FILE_SIZE", length = 10)
		private Integer fileSizeInKb;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public byte[] getFileData() {
			return fileData;
		}

		public void setFileData(byte[] fileData) {
			this.fileData = fileData;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public Date getUploadDate() {
			return uploadDate;
		}

		public void setUploadDate(Date uploadDate) {
			this.uploadDate = uploadDate;
		}

		public String getContentType() {
			return contentType;
		}

		public void setContentType(String contentType) {
			this.contentType = contentType;
		}

		public Integer getFileSizeInKb() {
			return fileSizeInKb;
		}

		public void setFileSizeInKb(Integer fileSizeInKb) {
			this.fileSizeInKb = fileSizeInKb;
		}

		@Override
		public String toString() {
			return "BudgetDocument [id=" + id + ", fileName=" + fileName + ", description=" + description + ", uploadDate=" + uploadDate + ", contentType=" + contentType + ", fileSizeInKb=" + fileSizeInKb + "]";
		}
		
		
}
