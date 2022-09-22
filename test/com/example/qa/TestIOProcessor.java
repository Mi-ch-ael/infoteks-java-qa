package com.example.qa;

import org.testng.annotations.*;

import java.io.*;

import static org.testng.AssertJUnit.*;

public class TestIOProcessor {
    private PrintStream originalSystemOut, originalSystemErr;

    @BeforeTest
    public void setup() {
        originalSystemOut = System.out;
        originalSystemErr = System.err;
    }

    @AfterTest
    public void cleanup() {
        System.setOut(originalSystemOut);
        System.setErr(originalSystemErr);
    }

    @DataProvider(name = "inputStrings")
    public Object[][] serveInputStrings() {
        String greeting = "Hello, %s!\nAvailable commands:\n" +
                "list - list all students in alphabetical order\n" +
                "find <id> - print student with numerical id <id>\n" +
                "add <name> - add student with name <name>\n" +
                "delete <id> - delete student with numerical id <id>\n" +
                "quit - end the program. You can also quit by pressing Ctrl-D\n";
        TestCommandRunner testCommandRunner = new TestCommandRunner();
        return new Object[][]{
                {
                        "list all\n", "user", "host",
                        String.format(greeting, "user") +
                                "user@host> Command 'list' does not require arguments.\n" +
                                "Usage: list\n" +
                                "user@host> Bye\n"
                },
                {
                        "list\n", "user", "host",
                        String.format(greeting, "user") +
                                "user@host> " + testCommandRunner.list() + "\n" +
                                "user@host> Bye\n"
                },
                {
                        "find 1 2\n", "user", "host",
                        String.format(greeting, "user") +
                                "user@host> Argument 'id' for 'find' command must be an integer.\nUsage: find <id>\n" +
                                "user@host> Bye\n"
                },
                {
                        "find\n", "user", "host",
                        String.format(greeting, "user") +
                                "user@host> Command 'find' requires one argument 'id'.\nUsage: find <id>\n" +
                                "user@host> Bye\n"
                },
                {
                        "find Jack\n", "user", "host",
                        String.format(greeting, "user") +
                                "user@host> Argument 'id' for 'find' command must be an integer.\nUsage: find <id>\n" +
                                "user@host> Bye\n"
                },
                {
                        "find 1\n", "user", "host",
                        String.format(greeting, "user") +
                                "user@host> " + testCommandRunner.find(1) + "\n" +
                                "user@host> Bye\n"
                },
                {
                        "add\n", "user", "host",
                        String.format(greeting, "user") +
                                "user@host> Command 'add' requires one argument 'name'.\nUsage: add <name>\n" +
                                "user@host> Bye\n"
                },
                {
                        "add Jack\n", "user", "host",
                        String.format(greeting, "user") +
                                "user@host> " + testCommandRunner.add("Jack") + "\n" +
                                "user@host> Bye\n"
                },
                {
                        "add Jack Johnson\n", "user", "host",
                        String.format(greeting, "user") +
                                "user@host> " + testCommandRunner.add("Jack Johnson") + "\n" +
                                "user@host> Bye\n"
                },
                {
                        "delete\n", "user", "host",
                        String.format(greeting, "user") +
                                "user@host> Command 'delete' requires one argument 'id'.\n" +
                                "Usage: delete <id>\n" +
                                "user@host> Bye\n"
                },
                {
                        "delete Jack\n", "user", "host",
                        String.format(greeting, "user") +
                                "user@host> Argument 'id' for 'delete' command must be an integer.\n" +
                                "Usage: delete <id>\n" +
                                "user@host> Bye\n"
                },
                {
                        "delete 1\n", "user", "host",
                        String.format(greeting, "user") +
                                "user@host> " + testCommandRunner.delete(1) + "\n" +
                                "user@host> Bye\n"
                },
                {
                        "quit now\n", "user", "host",
                        String.format(greeting, "user") +
                                "user@host> Bye\n"
                },
                {
                        "quit\n", "user", "host",
                        String.format(greeting, "user") +
                                "user@host> Bye\n"
                },
                {
                        "aaa\n", "user", "host",
                        String.format(greeting, "user") +
                                "user@host> " +
                                "Unknown command 'aaa'.\nAvailable commands: list, find <id>, " +
                                "add <name>, delete <id>, quit\n" +
                                "user@host> Bye\n"
                },
                {
                        "\n", "user", "host",
                        String.format(greeting, "user") +
                                "user@host> user@host> Bye\n"
                },
                {
                        "", "user", "host",
                        String.format(greeting, "user") +
                                "user@host> Bye\n"
                },
                {
                        "", "username", "hostname",
                        String.format(greeting, "username") +
                                "username@hostname> Bye\n"
                }
        };
    }

    @Test(dataProvider = "inputStrings")
    public void testListCommand(String userInput, String username, String address, String expected) {
        byte[] input = userInput.getBytes();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errStream = new ByteArrayOutputStream();
        IOProcessor ioProcessor = new IOProcessor(new ByteArrayInputStream(input), new TestCommandRunner(),
                username, address);
        System.setOut(new PrintStream(outputStream));
        System.setErr(new PrintStream(errStream));
        ioProcessor.process();
        String actual = outputStream.toString();
        assertEquals(expected, actual);
        assertEquals(0, errStream.toString().length());
    }
}
