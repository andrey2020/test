package de.mackevision.sendmailtester;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.*;
import javax.mail.internet.*;

/**
 *
 * @author andrey
 */
public class MailSender {


    public static void send(String recipient, String bodyText){    

        // Sender's email ID needs to be mentioned
        String from = "test@te.st";

        // Assuming you are sending email from localhost
        String host = "localhost";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.port","2500");

        // Get the default Session object.
        Session session = Session.getDefaultInstance(properties);


        //Sending mail in new thread 
        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.currentThread().sleep(1 *1000);
                    // Create a default MimeMessage object.
                    MimeMessage message = new MimeMessage(session);

                    // Set From: header field of the header.
                    message.setFrom(new InternetAddress(from));

                    // Set To: header field of the header.
                    message.addRecipient(Message.RecipientType.TO,
                                 new InternetAddress(recipient));

                    // Set Subject: header field
                    message.setSubject("This is the Subject Line!");

                    // Now set the actual message
                    message.setText(bodyText);
                    // Send message
                    Transport.send(message);
               } catch (InterruptedException ex) {
                    Logger.getLogger(MailSender.class.getName()).log(Level.SEVERE, null, ex);

                } catch (MessagingException ex) {
                    Logger.getLogger(MailSender.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        
        //Start mailThread
        new Thread(r).start();
    }
}

