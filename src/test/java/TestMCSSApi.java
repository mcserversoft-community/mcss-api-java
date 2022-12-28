import dev.le_app.mcss_api_java.*;
import dev.le_app.mcss_api_java.exceptions.*;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestMCSSApi {

    static MCSSApi api;
    static ArrayList<Server> servers;
    static Scheduler scheduler;
    static ArrayList<Task> tasks;
    static Job job;
    static Info info;

    static String ip;
    static String token;

    @BeforeAll
    static void setUp() {
        //Get the ip from environment variable
        ip = System.getenv("MCSS_API_IP");
        //Get the token from environment variable
        token = System.getenv("MCSS_API_TOKEN");
    }

    @BeforeEach
    public void init() {
        try {
            System.out.println("IP: " + ip);
            System.out.println("Is token null/empty: " + (token == null || token.isEmpty()));
            api = new MCSSApi(Objects.requireNonNullElse(ip, "127.0.0.1:25560"), token, true);
        } catch (IOException e) {
            e.printStackTrace();
            fail("Thorn IOException while testing connection");
        } catch (APIUnauthorizedException e) {
            e.printStackTrace();
            fail("Thorn APIUnauthorizedException while testing connection");
        } catch (APIVersionMismatchException e) {
            e.printStackTrace();
            fail("Thorn APIVersionMismatchException while testing connection");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            fail("Thorn NoSuchAlgorithmException while testing connection");
        } catch (KeyManagementException e) {
            e.printStackTrace();
            fail("Thorn KeyManagementException while testing connection");
        }
        assertNotNull(api, "API is null");
    }

    /*
    @Test
    @DisplayName("Test the API connection")
    @Order(1)
    void testConnection() {
        try {
            System.out.println("IP: " + ip);
            System.out.println("Is token null/empty: " + (token == null || token.isEmpty()));
            api = new MCSSApi(Objects.requireNonNullElse(ip, "127.0.0.1:25560"), token, true);
        } catch (IOException e) {
            e.printStackTrace();
            fail("Thorn IOException while testing connection");
        } catch (APIUnauthorizedException e) {
            e.printStackTrace();
            fail("Thorn APIUnauthorizedException while testing connection");
        } catch (APIVersionMismatchException e) {
            e.printStackTrace();
            fail("Thorn APIVersionMismatchException while testing connection");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            fail("Thorn NoSuchAlgorithmException while testing connection");
        } catch (KeyManagementException e) {
            e.printStackTrace();
            fail("Thorn KeyManagementException while testing connection");
        }
        assertNotNull(api, "API is null");
    }
    */

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
        assertEquals("1.2.1", info.getMCSSApiVersion(), "Version is not 1.2.1");
        assertTrue(info.getYouAreAwesome(), "Expected \"areYouAwesome\" to be true");
    }

    @Test
    @DisplayName("Test the get servers method")
    @Order(3)
    void testGetServers() {
        try {
            String name;
            servers = api.getServers();
            for (Server server : servers) {
                System.out.println(server);
                server.executeServerAction(ServerAction.START);
                server.executeServerAction(ServerAction.KILL);
                name = server.getName();
                server.setName("API Test");
                assert(server.getName().equals("API Test"));
                server.setName(name);
            }
        } catch (APIUnauthorizedException e) {
            e.printStackTrace();
            fail("Thrown ApiUnauthorizedException while getting servers");
        } catch (IOException e) {
            e.printStackTrace();
            fail("Thrown IOException while getting servers");
        } catch (APINotFoundException e) {
            e.printStackTrace();
            fail("Thrown APINotFoundException while getting servers");
        } catch (APINoServerAccessException e) {
            e.printStackTrace();
            fail("Thrown APINoServerAccessException while getting servers");
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
            for (Task task : tasks) {
                System.out.println(task.toString());
                task.runTask();
            }
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
        } catch (APINoServerAccessException e) {
            e.printStackTrace();
            fail("Thrown APINoServerAccessException while getting tasks");
        }
        assertNotNull(tasks, "Tasks is null");
        assertTrue(tasks.size() > 0, "Tasks is empty");
    }


    @Test
    @DisplayName("Test the task information")
    @Order(6)
    void testGetTaskInfo() {
        try {
            if (tasks.get(0).getTaskType() == TaskType.INTERVAL) System.out.println(tasks.get(0).getInterval());
            else if (tasks.get(0).getTaskType() == TaskType.FIXED_TIME) System.out.println(tasks.get(0).getTime().toString());
            else if (tasks.get(0).getTaskType() == TaskType.TIMELESS) System.out.println("Timeless task");
            else fail("Unknown task type");
            System.out.println(tasks.get(0).getTaskName());
        } catch (APIUnauthorizedException e) {
            e.printStackTrace();
            fail("Thrown ApiUnauthorizedException while getting task info");
        } catch (IOException e) {
            e.printStackTrace();
            fail("Thrown IOException while getting task info");
        } catch (APINotFoundException e) {
            e.printStackTrace();
            fail("Thrown APINotFoundException while getting task info");
        } catch (APIInvalidTaskDetailsException e) {
            e.printStackTrace();
            fail("Thrown APIInvalidTaskDetailsException while getting task info");
        } catch (APINoServerAccessException e) {
            e.printStackTrace();
            fail("Thrown APINoServerAccessException while getting task info");
        }
    }

    @Test
    @DisplayName("Test the get Job method")
    @Order(7)
    void testGetJob() {
        try {
            job = tasks.get(0).getJob();
        } catch (APINotFoundException e) {
            e.printStackTrace();
            fail("Thrown APINotFoundException while getting job");
        }
        assertNotNull(job, "Job is null");
        try {
            if (job instanceof RunCommandsJob) {
                assertNotNull(job.getCommands(), "Commands is null");
                assertTrue(job.getCommands().size() > 0, "Commands is empty");
            } else if (job instanceof BackupJob ) {
                assertNotNull(job.getBackupGUID(), "Backup ID is null");
            } else if (job instanceof ServerActionJob) {
                assertNotNull(job.getAction(), "Action is null");
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


    @AfterAll
    void tearDown() {
        token = null;
        ip = null;
        api = null;
        info = null;
        servers = null;
        scheduler = null;
        tasks = null;
        job = null;
    }
}

