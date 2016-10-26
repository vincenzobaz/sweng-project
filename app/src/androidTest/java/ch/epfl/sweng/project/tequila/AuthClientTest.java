package ch.epfl.sweng.project.tequila;


import junit.framework.Assert;
import org.junit.Test;

/**
 * Tests for AuthClient methods
 *
 * @author Alexis Montavon
 */

public class
AuthClientTest {

    @Test
    public void createRightRequestUrl() {
        String rightSample = "https://tequila.epfl.ch/cgi-bin/OAuth2IdP/auth" +
                "?response_type=code" +
                "&client_id=TOTO" +
                "&redirect_uri=TITI" +
                "&scope=Tequila.profile";
        String[] scopes = {"Tequila.profile"};
        OAuth2Config testConfig = new OAuth2Config(scopes, "TOTO", "TUTU", "TITI");
        String url = AuthClient.createCodeRequestUrl(testConfig);

        Assert.assertEquals(rightSample, url);
    }

    @Test
    public void extractCodeTest() {
        String rightString = "cestbienlecoderequis112234";
        String extractMe = "voila%undebut2string&inutilecode=cestbienlecoderequis112234";
        String extractResult = AuthClient.extractCode(extractMe);

        Assert.assertEquals(rightString, extractResult);
    }
}
