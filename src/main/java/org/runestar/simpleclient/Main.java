package org.runestar.simpleclient;

import javax.swing.*;
import java.applet.Applet;
import java.awt.*;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public final class Main {

    public static void main(String[] args) throws Exception {
        System.setProperty("sun.awt.noerasebackground", "true"); // fixes resize flickering

        JavConfig javConfig = JavConfig.load();

        HttpURLConnection conn = (HttpURLConnection) javConfig.gamepackUrl().openConnection();
        long contentLength = conn.getContentLengthLong();

        Path jar = Paths.get(System.getProperty("java.io.tmpdir"), "runescape-gamepack.jar");

        if (Files.notExists(jar) || Files.size(jar) != contentLength) {
            try (InputStream in = conn.getInputStream()) {
                Files.copy(in, jar, StandardCopyOption.REPLACE_EXISTING);
            }
        }

        conn.disconnect();

        launch(jar, javConfig);
    }

    private static void launch(
            Path gamepack,
            JavConfig javConfig
    ) throws Exception {
        URLClassLoader classLoader = new URLClassLoader(new URL[] { gamepack.toUri().toURL() });
        Class<?> clientClass = classLoader.loadClass(javConfig.initialClass());
        Constructor<?> clientConstructor = clientClass.getDeclaredConstructor();
        @SuppressWarnings("deprecation") Applet client = (Applet) clientConstructor.newInstance();

        client.setLayout(null); // fixes resize bouncing
        client.setStub(javConfig.new Stub());
        client.setMinimumSize(new Dimension(200, 350));
        client.setMaximumSize(javConfig.appletMaxSize());
        client.setPreferredSize(javConfig.appletMinSize());

        JFrame frame = new JFrame(javConfig.title());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(client);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setPreferredSize(frame.getSize());
        frame.setMinimumSize(client.getMinimumSize());

        client.init();
        client.start();
    }
}
