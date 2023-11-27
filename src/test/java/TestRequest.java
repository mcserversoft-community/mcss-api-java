import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import com.mcserversoft.api.MCSS;
import com.mcserversoft.commons.structures.UserBuilder;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestRequest {

    String apiKey = "AuICAbD3gMX7A3waWOZwuMsGhPPwWhGUss8euBHi6OCPi1P8qzIblphcZPdy9GWVdjSFsJrz8w33WYr5n02hindVUlke22ucpUAkEG6j04LfxyBXrCW3gxTh";

    MCSS mcss = new MCSS("dev.bosstop.tech", 80, apiKey);
    
    @Test
    @Order(1)
    @DisplayName("Test GET request")
    public void testGET() {
        try {

           // ArrayList<UserResponse> users = mcss.users.get();

            UserBuilder newUser = new UserBuilder()
            .setEnabled(true)
            .setAdmin(true)
            .setHasAccessToAllServers(true)
            .setPassword("password")
            .setUsername("testuser");

            System.out.println(mcss.users.create(newUser));
            
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
