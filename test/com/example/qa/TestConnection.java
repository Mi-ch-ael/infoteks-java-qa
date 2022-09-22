package com.example.qa;

import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import sun.net.ftp.FtpLoginException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import static org.testng.AssertJUnit.assertEquals;

public class TestConnection {
    private FakeFtpServer fakeFtpServer;

    @Test(expectedExceptions = {MalformedURLException.class})
    public void testMalformedUrl() throws MalformedURLException {
        ConnectionManager connectionManager = new ConnectionManager("ftp", "-" ,
                "hostname:80:21/files/students.json");
    }

    @Test(expectedExceptions = {UnknownHostException.class})
    public void testUnknownServer() throws IOException {
        ConnectionManager connectionManager = new ConnectionManager("ftp", "-" ,
                "notexistingserver.com/students.json");
        connectionManager.getStudents();
    }

    @BeforeClass
    public void setupBasicServer() {
        fakeFtpServer = new FakeFtpServer();
        FileSystem fileSystem = new UnixFakeFileSystem();
        DirectoryEntry filesystemRoot = new DirectoryEntry("/");
        FileEntry students = new FileEntry("/students.json", "{\"students\":[]}");
        students.setPermissions(Permissions.NONE);
        fileSystem.add(filesystemRoot);
        fileSystem.add(students);
        fakeFtpServer.addUserAccount(new UserAccount("username",
                "password", "/"));
        fakeFtpServer.setFileSystem(fileSystem);
        fakeFtpServer.setServerControlPort(0);
        fakeFtpServer.start();
    }

    @AfterClass
    public void stopBasicServer() {
        fakeFtpServer.stop();
    }

    @Test(expectedExceptions = {FtpLoginException.class})
    public void testWrongPassword() throws IOException {
        int port = fakeFtpServer.getServerControlPort();
        ConnectionManager connectionManager = new ConnectionManager("username", "wrong",
                String.format("localhost:%d/students.json", port));
        connectionManager.getStudents();
    }

    @Test(expectedExceptions = {IOException.class},
            expectedExceptionsMessageRegExp =
                    "sun\\.net\\.ftp\\.FtpProtocolException:.*\\[/files] does not exist\\.\n")
    public void testNoFile() throws IOException {
        ConnectionManager connectionManager = new ConnectionManager("username", "password",
                String.format("localhost:%d/files/students.json", fakeFtpServer.getServerControlPort()));
        connectionManager.getStudents();
    }

    @Test(expectedExceptions = {FileNotFoundException.class})
    public void testNoReadPermissions() throws IOException {
        ConnectionManager connectionManager = new ConnectionManager("username", "password",
                String.format("localhost:%d/students.json", fakeFtpServer.getServerControlPort()));
        connectionManager.getStudents();
    }

    @Test(expectedExceptions = {IOException.class},
    expectedExceptionsMessageRegExp =
            "sun\\.net\\.ftp\\.FtpProtocolException:.*The current user does not have write permission for \\[/students\\.json]\\.\n")
    public void testNoWritePermissions() throws IOException {
        ConnectionManager connectionManager = new ConnectionManager("username", "password",
                String.format("localhost:%d/students.json", fakeFtpServer.getServerControlPort()));
        connectionManager.putStudents("{\"students\":[]}");
    }

    @Test
    public void testOkUnixServer() throws IOException {
        FakeFtpServer unixFtpServer = new FakeFtpServer();
        FileSystem fileSystem = new UnixFakeFileSystem();
        DirectoryEntry filesystemRoot = new DirectoryEntry("/");
        String oldStudents = "{\"students\":[]}";
        FileEntry students = new FileEntry("/students.json", oldStudents);
        students.setPermissions(Permissions.ALL);
        fileSystem.add(filesystemRoot);
        fileSystem.add(students);
        unixFtpServer.addUserAccount(new UserAccount("username",
                "password", "/"));
        unixFtpServer.setFileSystem(fileSystem);
        unixFtpServer.setServerControlPort(0);
        unixFtpServer.start();
        ConnectionManager connectionManager = new ConnectionManager("username", "password",
                String.format("localhost:%d/students.json", unixFtpServer.getServerControlPort()));
        assertEquals(oldStudents, connectionManager.getStudents());
        String newStudents = "{\"students\": [{\"name\": \"James\", \"id\": 2}]}";
        connectionManager.putStudents(newStudents);
        assertEquals(newStudents, connectionManager.getStudents());
        unixFtpServer.stop();
    }

    @Test
    public void testOkWindowsServer() throws IOException{
        FakeFtpServer windowsFtpServer = new FakeFtpServer();
        FileSystem fileSystem = new WindowsFakeFileSystem();
        DirectoryEntry systemRoot = new DirectoryEntry("C:\\");
        String oldStudents = "{\"students\":[]}";
        FileEntry students = new FileEntry("C:\\students.json", oldStudents);
        students.setPermissions(Permissions.ALL);
        fileSystem.add(systemRoot);
        fileSystem.add(students);
        windowsFtpServer.addUserAccount(new UserAccount("username",
                "password", "C:\\"));
        windowsFtpServer.setFileSystem(fileSystem);
        windowsFtpServer.setServerControlPort(0);
        windowsFtpServer.start();
        ConnectionManager connectionManager = new ConnectionManager("username", "password",
                String.format("localhost:%d/students.json", windowsFtpServer.getServerControlPort()));
        assertEquals(oldStudents, connectionManager.getStudents());
        String newStudents = "{\"students\": [{\"name\": \"James\", \"id\": 2}]}";
        connectionManager.putStudents(newStudents);
        assertEquals(newStudents, connectionManager.getStudents());
        windowsFtpServer.stop();
    }
}
