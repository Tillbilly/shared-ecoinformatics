package au.edu.aekos.shared.notification;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-mail-context.xml"})
public class NotificationEmailTestIT {

	@Autowired
	JavaMailSender sender;
	
	@Autowired
	SimpleMailMessage message;
	
	@Test
	public void testSendEmailToMyself(){
		
		SimpleMailMessage msg = new SimpleMailMessage(message);
        msg.setTo("mosheh.eliyahu@adelaide.edu.au");
        msg.setText("Sent by Shared Autobot v0.0" );
        
        try{
            this.sender.send(msg);
        }
        catch(MailException ex) {
            // simply log it and go on...
            System.err.println(ex.getMessage());            
        }
	}
}
