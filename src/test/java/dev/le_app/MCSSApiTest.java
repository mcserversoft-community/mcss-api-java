package dev.le_app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class MCSSApiTest {

    MCSSApi api;
    ArrayList<Server> servers;
    Scheduler scheduler;
    ArrayList<Task> tasks;
    Job job;

    String ip;
    String token;

    @BeforeEach
    void setUp() {
        //Get the ip from the gradle build file
        ip =  

    }

    @AfterEach
    void tearDown() {
    }
}