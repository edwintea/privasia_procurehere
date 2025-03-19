/**
 * 
 */
package com.privasia.procurehere.test;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.privasia.procurehere.service.NotificationService;

/**
 * @author arc
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class EmailTest {

	@Autowired
	@Qualifier("notificationService")
	NotificationService notificationService;

	@Test
	@Ignore
	public void sendEmailTest() {
		try {
			System.out.println("Sending email..."); 
			notificationService.sendPlainTextEmail("arc@recstech.com", "Test Mail", "Test Mail droooooooooooooooooooo");
			System.out.println("Email gayo...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
