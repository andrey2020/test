/**
 *
 * @author andrey
 */

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

public final class WiserMatcher implements Matcher<Wiser> {
    
    /**
    * For any timeout ensures one run through the list (wiser.getMessages()).
    *
    * @param item the Wiser Object
    * @return true if message with Recipient and Body is in wiser.getMessages()
    *
    */    
    @Override
    public boolean matches(Object item) {
        long startTime = System.currentTimeMillis();
        Wiser wiser = (Wiser) item;
        while (!testResult && ! isTimeOut) {            
            for (WiserMessage m : wiser.getMessages()) {
                try {
                    if(m.getEnvelopeReceiver().equals(recipient) && m.getMimeMessage().getContent().equals(bodyText)){
                        testResult = true;
                    }
                } catch (IOException ex) {
                    Logger.getLogger(WiserMatcher.class.getName()).log(Level.SEVERE, null, ex);
                } catch (MessagingException ex) {
                    Logger.getLogger(WiserMatcher.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            isTimeOut = (System.currentTimeMillis() - startTime) > timeout *1000;
        }
        return testResult;
    }

    @Override
    public void describeTo(Description description) {
      log.info("Input Message: " + expectedString);
      description.appendText(expectedString);
    }
    
    @Override
    public void describeMismatch(Object item, Description description) {
        Wiser wiser = (Wiser)item;
        StringBuilder wiserMessages = new StringBuilder();
        description.appendText("Wiser have: ");
        
        wiserMessages.append(wiser.getMessages().size()).append(" messages. \n");
        
        for (WiserMessage m : wiser.getMessages()) {
            try {
                
                wiserMessages.append("Recipient: ")
                        .append("\"")
                        .append(m.getEnvelopeReceiver())
                        .append("\"")
                        .append(" Subject: ")
                        .append("\"")
                        .append(m.getMimeMessage().getSubject())
                        .append("\"")
                        .append(" Body: ")
                        .append("\"")
                        .append(m.getMimeMessage().getContent())
                        .append("\"")
                        .append("\n");
            } catch (MessagingException ex) {} catch (IOException ex) {
                Logger.getLogger(WiserMatcher.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
        if(isTimeOut) wiserMessages.append("Exit on timeout.");
        
        description.appendText(wiserMessages.toString());
        log.info("Wiser have: " + wiserMessages.toString());
        
    }    

    @Factory
    public static <T> Matcher<Wiser> wiserMatcher(String recipient, String bodyText, int timeout) {
      return new WiserMatcher(recipient, bodyText, timeout);
    }
    
    @Factory
    public static <T> Matcher<Wiser> wiserMatcher(String recipient, String bodyText) {
      return new WiserMatcher(recipient, bodyText, 3);
    }
    
    private final String recipient;
    private final String bodyText;
    private final int timeout;
    
    private static final Log log = LogFactory.getLog(WiserMatcher.class);
    
    private final String expectedString;
    
    private boolean testResult;
    private boolean isTimeOut;
    
    private WiserMatcher(String recipient, String bodyText, int timeout){
        this.bodyText = bodyText;
        this.recipient = recipient;
        this.timeout = timeout;
        
        expectedString = "Recipient: "
                        + recipient
                        + " Body: "
                        + bodyText;
    }
    
    @Deprecated
    @Override
    public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {}
    

}