package notification;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailThread implements Runnable{

	public static volatile boolean ok= true;
	
//	@Override
	public void run() {

		while(ok){
		 // Recipient email, for multiple recipient's separate by comma
        String to = "crawlerprojekt@gmail.com";
        // Sender email
        String from = "crawlerprojekt@gmail.com";
        // Sender host
        String host = "smtp.gmail.com";
        // Get system properties
        Properties properties = System.getProperties();
        // Mail server
        properties.setProperty("mail.smtp.host", host);
        // Get the default Session object.
        properties.put("mail.smtp.socketFacry.port", "465");
        properties.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", "465");
        Session session = Session.getDefaultInstance(properties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("crawlerprojekt",
                                "crawler1991");
                    }
                });
        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);
            // From
            message.setFrom(new InternetAddress(from));
            // To
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(
                    to));
            // Subject
            message.setSubject("Subject");
            // Message
            message.setText("Message on http://www.raghuwansh.com");
            // Send message
            Transport.send(message);
            System.out.println("Sent successfully.");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
	}

}
