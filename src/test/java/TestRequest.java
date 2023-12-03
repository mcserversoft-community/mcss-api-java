import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import dev.le_app.mcss_api_java.api.MCSS;
import dev.le_app.mcss_api_java.commons.responses.server.ServerResponse;
import dev.le_app.mcss_api_java.commons.responses.client.StatsResponse;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestRequest {

    String apiKey = "API";

    MCSS mcss = new MCSS("URI", 80, apiKey);
    
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
