package org.runestar.simpleclient;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.applet.AudioClip;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class JavConfig {

    final Map<String, String> properties;

    final Map<String, String> parameters;

    private JavConfig(
            Map<String, String> properties,
            Map<String, String> parameters
    ) {
        this.properties = properties;
        this.parameters = parameters;
    }

    public String title() {
        return properties.get("title");
    }

    public URL codeBase() {
        try {
            return new URL(properties.get("codebase"));
        } catch (MalformedURLException e) {
            throw new InvalidParameterException();
        }
    }

    public URL gamepackUrl() throws MalformedURLException {
        return new URL(properties.get("codebase") + properties.get("initial_jar"));

    }

    public Dimension appletMinSize() {
        return new Dimension(
                Integer.parseInt(properties.get("applet_minwidth")),
                Integer.parseInt(properties.get("applet_minheight"))
        );
    }

    public Dimension appletMaxSize() {
        return new Dimension(
                Integer.parseInt(properties.get("applet_maxwidth")),
                Integer.parseInt(properties.get("applet_maxheight"))
        );
    }

    public String initialClass() {
        String fileName = properties.get("initial_class");
        return fileName.substring(0, fileName.length() - 6);
    }

    public static JavConfig load() throws IOException {
        Map<String, String> properties = new HashMap<>();
        Map<String, String> parameters = new HashMap<>();
        URL url = new URL("http://oldschool.runescape.com/jav_config.ws");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.ISO_8859_1))) {
            String line;
            while ((line = reader.readLine()) != null) {
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
                        properties.put(split1[0], split1[1]);
                }
            }
        }
        return new JavConfig(properties, parameters);
    }

    @SuppressWarnings("deprecation")
    public final class Stub implements AppletStub, AppletContext {

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
        public void appletResize(int width, int height) {}

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
                e.printStackTrace();
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