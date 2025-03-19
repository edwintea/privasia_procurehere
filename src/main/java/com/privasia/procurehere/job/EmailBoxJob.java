/**
 * 
 */
package com.privasia.procurehere.job;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;
import javax.mail.search.FlagTerm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.ExcelParseException;
import com.privasia.procurehere.core.exceptions.NoRollBackException;
import com.privasia.procurehere.core.exceptions.WarningException;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.FavoriteSupplierService;

/**
 * @author yogesh
 */
@Component
public class EmailBoxJob implements BaseSchedulerJob {

	private static final Logger LOG = LogManager.getLogger(EmailBoxJob.class);

	@Autowired
	FavoriteSupplierService favoriteSupplierService;

	@Autowired
	BuyerService buyerService;

	@Override
	@Transactional(readOnly = false)
	public void execute(JobExecutionContext ctx) {
		LOG.info("Running EmailBoxJob---");
		List<Buyer> list = buyerService.findAllActiveMailBoxBuyers();
		if (CollectionUtil.isNotEmpty(list)) {
			LOG.info("found buyer" + list.size());
			for (Buyer buyer : list) {
				try {
					getSupplierByAttchment(buyer.getId(), buyer.getMailBoxEmail());
				} catch (WarningException e) {
					LOG.error("Error while upload supplier via email:" + e.getMessage(), e);
				} catch (MessagingException e) {
					LOG.error("Error while upload supplier via email:" + e.getMessage(), e);
				} catch (IOException e) {
					LOG.error("Error while upload supplier via email:" + e.getMessage(), e);
				}
			}
		}
	}

	private void getSupplierByAttchment(String tenantId, String mailBoxEmail) throws MessagingException, IOException, WarningException {

		Folder folder = null;
		Store store = null;
		try {
			LOG.info("========");
			Properties props = System.getProperties();
			props.setProperty("mail.store.protocol", "imaps");

			Session session = Session.getDefaultInstance(props, null);
			// session.setDebug(true);
			store = session.getStore("imaps");
			store.connect("mail.simbiotiktech.com", "yogesh.djadhav@simbiotiktech.com", "h3wjpnI#gG3A");
			folder = store.getFolder("Inbox");
			/*
			 * Others GMail folders : [Gmail]/All Mail This folder contains all of your Gmail messages. [Gmail]/Drafts
			 * Your drafts. [Gmail]/Sent Mail Messages you sent to other people. [Gmail]/Spam Messages marked as spam.
			 * [Gmail]/Starred Starred messages. [Gmail]/Trash Messages deleted from Gmail.
			 */
			folder.open(Folder.READ_WRITE);
			// Message messages[] = folder.getMessages();

			Message messages[] = folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
			LOG.info("No of Messages : " + folder.getMessageCount());

			LOG.info("No of Unread Messages : " + folder.getUnreadMessageCount());
			LOG.info("No of Unread Messageslist : " + messages.length);
			for (int i = 0; i < messages.length; ++i) {
				Message msg = messages[i];
				if (!msg.getFlags().contains(Flags.Flag.SEEN)) {
					uploadFile(tenantId, msg.getContent(), msg.getSubject());
				}
				msg.setFlag(Flags.Flag.SEEN, false);
				/*
				 * if we don''t want to fetch messages already processed if (!msg.isSet(Flags.Flag.SEEN)) { String from
				 * = "unknown"; ... }
				 */
				// String from = "unknown";
				// if (msg.getReplyTo().length >= 1) {
				// from = msg.getReplyTo()[0].toString();
				// } else if (msg.getFrom().length >= 1) {
				// from = msg.getFrom()[0].toString();
				// }
				// String subject = msg.getSubject();
				// LOG.info("Saving ... " + subject + " " + from + "===" + msg.getReceivedDate());
				// // you may want to replace the spaces with "_"
				// // the TEMP directory is used to store the files
				// String filename = "" + subject;
				// saveParts(msg.getContent(), filename);
				//
				//
				// // to delete the message
				// // msg.setFlag(Flags.Flag.DELETED, true);
				//
				// if (i == 10)
				// break;
			}
		} finally {
			if (folder != null) {
				folder.close(true);
			}
			if (store != null) {
				store.close();
			}
		}
	}

	public void uploadFile(String tenantId, Object content, String filename) throws IOException, MessagingException, WarningException {
		OutputStream out = null;
		InputStream in = null;
		try {
			filename = "";
			if (content instanceof Multipart) {
				Multipart multi = ((Multipart) content);
				int parts = multi.getCount();
				for (int j = 0; j < parts; ++j) {
					MimeBodyPart part = (MimeBodyPart) multi.getBodyPart(j);
					if (part.getContent() instanceof Multipart) {
						uploadFile(tenantId, part.getContent(), filename);
					} else {
						String extension = "";
						if (!part.isMimeType("text/html") && !part.isMimeType("text/plain")) {
							extension = part.getDataHandler().getName();

							if (extension.equals("SUPPLIER.XLSX")) {
								try {
									favoriteSupplierService.supplierListUpload(tenantId, new File(extension), false);
								} catch (ExcelParseException | ApplicationException | NoRollBackException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.flush();
				out.close();
			}
		}
	}

}
