package dev.le_app;

import dev.le_app.exceptions.APIInvalidTaskDetailsException;
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
    @DisplayName("Test the get servers method")
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
    @DisplayName("Test the get scheduler method")
    @Order(4)
    void testGetScheduler() {

        scheduler = servers.get(0).getScheduler();

        assertNotNull(scheduler, "Scheduler is null");
    }

    @Test
    @DisplayName("Test the get tasks method")
    @Order(5)
    void testGetTasks() {
        try {
            tasks = scheduler.getTasks();
        } catch (APIUnauthorizedException e) {
            e.printStackTrace();
            fail("Thrown ApiUnauthorizedException while getting tasks");
        } catch (IOException e) {
            e.printStackTrace();
            fail("Thrown IOException while getting tasks");
        } catch (APINotFoundException e) {
            e.printStackTrace();
            fail("Thrown APINotFoundException while getting tasks");
        } catch (APIInvalidTaskDetailsException e) {
            e.printStackTrace();
            fail("Thrown APIInvalidTaskDetailsException while getting tasks");
        }
        assertNotNull(tasks, "Tasks is null");
        assertTrue(tasks.size() > 0, "Tasks is empty");
        assertNotNull(tasks.get(0).getTaskID(), "Task ID is null");
    }

    @Test
    @DisplayName("Test the get tasks method")
    @Order(6)
    void testGetJob() {
        try {
            job = tasks.get(0).getJob();
        } catch (APIUnauthorizedException e) {
            e.printStackTrace();
            fail("Thrown ApiUnauthorizedException while getting job");
        } catch (IOException e) {
            e.printStackTrace();
            fail("Thrown IOException while getting job");
        } catch (APINotFoundException e) {
            e.printStackTrace();
            fail("Thrown APINotFoundException while getting job");
        } catch (APIInvalidTaskDetailsException e) {
            e.printStackTrace();
            fail("Thrown APIInvalidTaskDetailsException while getting job");
        }
        assertNotNull(job, "Job is null");
        try {
            if (job instanceof RunCommandsJob) {
                assertNotNull(job.getCommands(), "Commands is null");
                assertTrue(job.getCommands().size() > 0, "Commands is empty");
            } else if (job instanceof BackupJob) {
                assertNotNull(job.getBackupGUID(), "Backup GUID is null");
                assertFalse(job.getBackupGUID().isEmpty(), "Backup GUID is empty");
            } else if (job instanceof ServerActionJob) {
                assertNotNull(job.getAction(), "Action is null");
                ServerAction action = job.getAction();
                for (ServerAction serverAction : ServerAction.values()) {
                    if (serverAction == action) {
                        return;
                    }
                }
                fail("Action is not a valid ServerAction");
            }
        } catch (APIUnauthorizedException e) {
            e.printStackTrace();
            fail("Thrown ApiUnauthorizedException while getting job details");
        } catch (IOException e) {
            e.printStackTrace();
            fail("Thrown IOException while getting job details");
        } catch (APINotFoundException e) {
            e.printStackTrace();
            fail("Thrown APINotFoundException while getting job details");
        } catch (APIInvalidTaskDetailsException e) {
            e.printStackTrace();
            fail("Thrown APIInvalidTaskDetailsException while getting job details");
        }
    }


    @AfterEach
    void tearDown() {
    }
}