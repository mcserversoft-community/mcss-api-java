package dev.le_app;

import dev.le_app.exceptions.APINotFoundException;
import dev.le_app.exceptions.APIUnauthorizedException;
import dev.le_app.exceptions.APIVersionMismatchException;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class MCSSApiTest {

    MCSSApi api;
    ArrayList<Server> servers;
    Scheduler scheduler;
    ArrayList<Task> tasks;
    Job job;
    Info info;

    static String ip;
    static String token;

    @BeforeAll
    static void setUp() {
        //Get the ip from the gradle build file
        ip = System.getProperty("ip");
        token = System.getProperty("token");
    }

    @Test
    @DisplayName("Test the API connection")
    @Order(1)
    void testConnection() {
        try {
            api = new MCSSApi(ip, token);
        } catch (IOException e) {
            e.printStackTrace();
            fail("Thorn IOException while testing connection");
        } catch (APIUnauthorizedException e) {
            e.printStackTrace();
            fail("Thorn APIUnauthorizedException while testing connection");
        } catch (APIVersionMismatchException e) {
            e.printStackTrace();
            fail("Thorn APIVersionMismatchException while testing connection");
        }
        assertNotNull(api, "API is null");
    }

    @Test
    @DisplayName("Testing returned information about API")
    @Order(2)
    void testInformation() {
        try {
            info = api.getInfo();
        } catch (IOException e) {
            e.printStackTrace();
            fail("Thorn IOException while testing information");
        } catch (APIUnauthorizedException e) {
            e.printStackTrace();
            fail("Thorn APIUnauthorizedException while testing information");
        }
        assertNotNull(info, "Info is null");
        assertEquals("1.0.0", info.getMCSSApiVersion(), "Version is not 1.0.0");
        assertTrue(info.getYouAreAwesome(), "Expected \"areYouAwesome\" to be true");
    }

    @Test
    @DisplayName("Get Servers")
    @Order(3)
    void testGetServers() {
        try {
            servers = api.getServers();
        } catch (APIUnauthorizedException e) {
            e.printStackTrace();
            fail("Thrown ApiUnauthorizedException while getting servers");
        } catch (IOException e) {
            e.printStackTrace();
            fail("Thrown IOException while getting servers");
        } catch (APINotFoundException e) {
            e.printStackTrace();
            fail("Thrown APINotFoundException while getting servers");
        }
        assertNotNull(servers, "Servers is null");
        assertTrue(servers.size() > 0, "Servers is empty");
        assertNotNull(servers.get(0).getGUID(), "Server GUID is null");
    }

    @Test


    @AfterEach
    void tearDown() {
    }
}