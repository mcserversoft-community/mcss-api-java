import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import com.mcserversoft.api.MCSS;
import com.mcserversoft.commons.responses.server.ServerResponse;
import com.mcserversoft.commons.responses.client.StatsResponse;
import com.mcserversoft.commons.responses.user.UserResponse;
import com.mcserversoft.commons.structures.UserBuilder;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestRequest {

    String apiKey = "AuICAbD3gMX7A3waWOZwuMsGhPPwWhGUss8euBHi6OCPi1P8qzIblphcZPdy9GWVdjSFsJrz8w33WYr5n02hindVUlke22ucpUAkEG6j04LfxyBXrCW3gxTh";

    MCSS mcss = new MCSS("dev.bosstop.tech", 80, apiKey);
    
    @Test
    @Order(1)
    @DisplayName("Client Test")
    public void clientGet() {
        try {
            StatsResponse clientStats = mcss.getStats();
            int serverCount = mcss.getServerCount();

            assertTrue(clientStats.getStatus()==200);
            assertTrue(serverCount > 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(2)
    @DisplayName("Servers Test")
    public void serverGet() {
        try {
            ArrayList<ServerResponse> servers = mcss.getServers();

            assertTrue(servers.size() > 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
