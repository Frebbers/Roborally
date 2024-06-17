package dk.dtu.compute.se.pisd.roborally.service;
import org.junit.jupiter.api.Test;

public class ApiServicesTest {
@Test
    public void testIsReachable() {
        // Test if the server is reachable
        assert new ApiServices().isReachable();
    }
}
