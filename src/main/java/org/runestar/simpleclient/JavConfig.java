package org.runestar.simpleclient;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AudioClip;
import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public final class JavConfig {

    Map<String, String> defaults;

    Map<String, String> parameters;

    private JavConfig(
            Map<String, String> defaults,
            Map<String, String> parameters
    ) {
        this.defaults = defaults;
        this.parameters = parameters;
    }

    public String title() {
        return defaults.get("title");
    }

    public URL codeBase() {
        try {
            return new URL(defaults.get("codebase"));
        } catch (MalformedURLException e) {
            throw new InvalidParameterException();
        }
    }

    public URL gamepackUrl() throws MalformedURLException {
        return new URL(defaults.get("codebase") + defaults.get("initial_jar"));

    }

    public Dimension appletMinSize() {
        return new Dimension(
                Integer.parseInt(defaults.get("applet_minwidth")),
                Integer.parseInt(defaults.get("applet_minheight"))
        );
    }

    public Dimension appletMaxSize() {
        return new Dimension(
                Integer.parseInt(defaults.get("applet_maxwidth")),
                Integer.parseInt(defaults.get("applet_maxheight"))
        );
    }

    public String initialClass() {
        String fileName = defaults.get("initial_class");
        return fileName.substring(0, fileName.length() - 6);
    }

    public static JavConfig load() throws IOException {
        Map<String, String> defaults = new LinkedHashMap<>();
        Map<String, String> parameters = new LinkedHashMap<>();
        URL url = new URL("http://oldschool.runescape.com/jav_config.ws");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.ISO_8859_1))) {
            reader.lines().forEach(line -> {
                String[] split1 = line.split("=", 2);
                switch (split1[0]) {
                    case "param":
                        String[] split2 = split1[1].split("=", 2);
                        parameters.put(split2[0], split2[1]);
                        break;
                    case "msg":
                        // ignore
                        break;
                    default:
                        defaults.put(split1[0], split1[1]);
                }
            });
        }
        return new JavConfig(defaults, parameters);
    }

    @SuppressWarnings("deprecation")
    public class AppletStub implements java.applet.AppletStub, AppletContext {

        @Override
        public boolean isActive() {
            return true;
        }

        @Override
        public URL getDocumentBase() {
            return getCodeBase();
        }

        @Override
        public URL getCodeBase() {
            return JavConfig.this.codeBase();
        }

        @Override
        public String getParameter(String name) {
            return parameters.get(name);
        }

        @Override
        public AppletContext getAppletContext() {
            return null;
        }

        @Override
        public void appletResize(int width, int height) { }

        @Override
        public AudioClip getAudioClip(URL url) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Image getImage(URL url) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Applet getApplet(String name) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Enumeration<Applet> getApplets() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void showDocument(URL url) {
            try {
                Desktop.getDesktop().browse(url.toURI());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void showDocument(URL url, String target) {
            showDocument(url);
        }

        @Override
        public void showStatus(String status) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setStream(String key, InputStream stream) {
            throw new UnsupportedOperationException();
        }

        @Override
        public InputStream getStream(String key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Iterator<String> getStreamKeys() {
            throw new UnsupportedOperationException();
        }
    }
}
