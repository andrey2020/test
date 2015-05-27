import de.mackevision.sendmailtester.MailSender;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.subethamail.wiser.Wiser;

/**
 *
 * @author andrey
 */
public class MailSenderTest {
    private Wiser wiser;
    TypeSafeMatcher f;
    @Before
    public void setUp() {
        wiser = new Wiser();
        wiser.setPort(2500);
        wiser.setHostname("localhost");
        wiser.start();
        
    }
    
    @After
    public void tearDown() {        
        wiser.stop();
    }

    @Test
    public void mailSenderTest() {
        MailSender.send("aa", "aa");
        assertThat(wiser, WiserMatcher.hasSent("aa", "aaa", 10));    
    }
}
