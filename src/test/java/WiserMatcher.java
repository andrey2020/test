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
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

public final class WiserMatcher extends BaseMatcher<Wiser> {
    
    @Override
    public boolean matches(Object item) {
        long startTime = System.currentTimeMillis();
        Wiser wiser = (Wiser) item;
        while (!testResult && ! isTimeOut) {            
            testResult = wiser.getMessages().stream().parallel()
                    .filter(m -> m.getEnvelopeReceiver().equals(recipient))
                    .findAny()
                    .isPresent();
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
        StringBuilder wiserMessage = new StringBuilder();
        description.appendText("Wiser have: ");
        
        
        for (WiserMessage m : wiser.getMessages()) {
            try {
                
                wiserMessage.append("Recipient: ")
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
        
        description.appendText(wiserMessage.toString());
        log.info("Wiser Message: " + wiserMessage.toString());

                   
        
        
    }
    

    @Factory
    public static <T> Matcher<Wiser> hasSent(String recipient, String bodyText, int timeout) {
      return new WiserMatcher(recipient, bodyText, timeout);
    }
    
    @Factory
    public static <T> Matcher<Wiser> hasSent(String recipient, String bodyText) {
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
    

}