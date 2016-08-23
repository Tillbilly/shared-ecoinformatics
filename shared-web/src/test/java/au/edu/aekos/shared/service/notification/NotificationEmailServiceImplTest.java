package au.edu.aekos.shared.service.notification;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import javax.mail.internet.MimeMessage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:NotificationEmailServiceImplTest-context.xml"})
public class NotificationEmailServiceImplTest {

	@Autowired
	private StubJavaMailSender sender;
	
	@Autowired
	private SimpleMailMessage message;
	
	@Value("${shared.system.email.address.reply-to}")
	private String replyToAdress;
	
	@Value("${shared.system.email.subject.prefix}")
	private String subjectPrefix;
	
	/**
	 * Is the message object constructed correctly and passed to the sender?
	 */
	@Test
	public void testSendEmail01() {
		NotificationEmailServiceImpl objectUnderTest = new NotificationEmailServiceImpl();
		objectUnderTest.setSender(sender);
		objectUnderTest.setMessage(message);
		objectUnderTest.setEnvironmentSubjectPrefix(subjectPrefix);
		String recipient = "blah@test.com";
		String subject = "some subject text";
		String messageContext = "awesome content";
		objectUnderTest.sendEmail(recipient, subject, messageContext);
		assertTrue("Method should have been called", sender.isTargetMethodCalled);
		SimpleMailMessage sideEffect = sender.sentMessage;
		assertThat(sideEffect.getTo()[0], is(recipient));
		assertThat(sideEffect.getTo().length, is(1));
		assertThat(sideEffect.getSubject(), is(subjectPrefix + subject));
		assertThat(sideEffect.getText(), is(messageContext));
		assertThat(sideEffect.getReplyTo(), is(replyToAdress));
		// Not testing the 'from' address because that's set by the real sender impl
	}
	
	public static class StubJavaMailSender implements JavaMailSender {

		private SimpleMailMessage sentMessage;
		private boolean isTargetMethodCalled = false;
		
		@Override
		public void send(SimpleMailMessage simpleMessage) throws MailException {
			isTargetMethodCalled = true;
			sentMessage = simpleMessage;
		}

		@Override public void send(SimpleMailMessage[] simpleMessages) throws MailException {}
		@Override public MimeMessage createMimeMessage() { return null; }
		@Override public MimeMessage createMimeMessage(InputStream contentStream) throws MailException { return null; }
		@Override public void send(MimeMessage mimeMessage) throws MailException {}
		@Override public void send(MimeMessage[] mimeMessages) throws MailException {}
		@Override public void send(MimeMessagePreparator mimeMessagePreparator) throws MailException {}
		@Override public void send(MimeMessagePreparator[] mimeMessagePreparators) throws MailException {}
	}
}
