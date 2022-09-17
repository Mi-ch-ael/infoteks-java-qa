package com.example.qa;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.stream.Collectors;

public class ConnectionManager {
    private final URL url;

    public ConnectionManager(String username, String password, String fullPath) throws MalformedURLException {
        String urlString = String.format("ftp://%s:%s@%s", username, password, fullPath);
        System.out.println(urlString);
        url = new URL(urlString);
    }

    public String getStudents() throws IOException {
        URLConnection conn = url.openConnection();
        InputStream inputStream = conn.getInputStream();
        return new BufferedReader(
                new InputStreamReader(inputStream))
                .lines()
                .collect(Collectors.joining(System.lineSeparator()));
    }

    public void putStudents(String string) throws IOException {
        URLConnection conn = url.openConnection();
        OutputStream outputStream = conn.getOutputStream();
        outputStream.write(string.getBytes());
        outputStream.close();
    }
}
