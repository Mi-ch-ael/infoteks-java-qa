package com.example.qa;

import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.testng.AssertJUnit.assertEquals;

@Test
public class TestSystem {
    private FakeFtpServer fakeFtpServer;
    private PrintStream originalSystemOut, originalSystemErr;
    private ByteArrayOutputStream outputStream, errStream;

    @BeforeClass
    public void setup() {
        fakeFtpServer = new FakeFtpServer();
        FileSystem fileSystem = new UnixFakeFileSystem();
        DirectoryEntry filesystemRoot = new DirectoryEntry("/");
        DirectoryEntry filesDir = new DirectoryEntry("/files/");
        FileEntry students = new FileEntry("/files/students.json",
                "{\"students\":[{\"id\": 1, \"name\": \"Student1\"}," +
                        "{\"id\": 2, \"name\": \"Student2\"}, {\"id\": 3, \"name\": \"Student3\"}]}");
        students.setPermissions(Permissions.ALL);
        fileSystem.add(filesystemRoot);
        fileSystem.add(filesDir);
        fileSystem.add(students);
        fakeFtpServer.addUserAccount(new UserAccount("username",
                "password", "/"));
        fakeFtpServer.setFileSystem(fileSystem);
        fakeFtpServer.setServerControlPort(0);
        fakeFtpServer.start();
        originalSystemErr = System.err;
        originalSystemOut = System.out;
        outputStream = new ByteArrayOutputStream();
        errStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        System.setErr(new PrintStream(errStream));
    }

    @AfterClass
    public void cleanup() {
        fakeFtpServer.stop();
        System.setOut(originalSystemOut);
        System.setErr(originalSystemErr);
    }

    public void testSystem() throws IOException {
        String hostname = "localhost:" + fakeFtpServer.getServerControlPort();
        ConnectionManager connectionManager = new ConnectionManager("username", "password",
                String.format("localhost:%d/files/students.json", fakeFtpServer.getServerControlPort()));
        ByteArrayInputStream input = new ByteArrayInputStream(
                ("list\n" +
                        "delete 4\n" +
                        "list\n" +
                        "delete 2\n" +
                        "list\n" +
                        "add James\n" +
                        "list\n" +
                        "find 2\n" +
                        "find 4\n" +
                        "find 1\n")
                        .getBytes());
        String expectedOutput = "Hello, username!\n" +
                "Available commands:\n" +
                "list - list all students in alphabetical order\n" +
                "find <id> - print student with numerical id <id>\n" +
                "add <name> - add student with name <name>\n" +
                "delete <id> - delete student with numerical id <id>\n" +
                "quit - end the program. You can also quit by pressing Ctrl-D\n" +
                "username@" + hostname + "> Student1 (id=1)\n" +
                "Student2 (id=2)\n" +
                "Student3 (id=3)\n" +
                "username@" + hostname + "> Student with id=4 not found.\n" +
                "username@" + hostname + "> Student1 (id=1)\n" +
                "Student2 (id=2)\n" +
                "Student3 (id=3)\n" +
                "username@" + hostname + "> Ok\n" +
                "username@" + hostname + "> Student1 (id=1)\n" +
                "Student3 (id=3)\n" +
                "username@" + hostname + "> Ok\n" +
                "username@" + hostname + "> James (id=4)\n" +
                "Student1 (id=1)\n" +
                "Student3 (id=3)\n" +
                "username@" + hostname + "> Student with id=2 not found.\n" +
                "username@" + hostname + "> James (id=4)\n" +
                "username@" + hostname + "> Student1 (id=1)\n" +
                "username@" + hostname + "> Bye\n";
        new IOProcessor(input, new ConnCommandRunner(connectionManager), "username", hostname).process();
        assertEquals(expectedOutput, outputStream.toString());
        assertEquals(0, errStream.toString().length());
    }
}
