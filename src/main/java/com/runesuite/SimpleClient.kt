package com.runesuite

import com.runesuite.general.JavConfig
import com.runesuite.general.downloadGamepack
import com.runesuite.general.updateRevision
import java.applet.Applet
import java.awt.Dimension
import java.io.IOException
import java.net.URLClassLoader
import java.nio.file.Path
import java.nio.file.Paths
import java.util.jar.JarFile
import javax.swing.JFrame
import javax.swing.WindowConstants

fun main(args: Array<String>) {
    val revision = updateRevision()
    System.setProperty("sun.awt.noerasebackground", true.toString()) // fixes resize flickering
    val jar = Paths.get(System.getProperty("java.io.tmpdir"), "runescape-gamepack.$revision.jar")
    try {
        JarFile(jar.toFile(), true)
    } catch (e: IOException) {
        // jar does not exist or was partially downloaded
        downloadGamepack(jar)
    }
    launch(jar)
}

fun launch(gamepack: Path, javConfig: JavConfig = JavConfig()) {
    val classLoader = URLClassLoader(arrayOf(gamepack.toUri().toURL()))
    val client = classLoader.loadClass(javConfig.initialClass).newInstance() as Applet
    client.apply {
        layout = null // fixes resize bouncing
        setStub(JavConfig.AppletStub(javConfig))
        minimumSize = Dimension(200, 350)
        maximumSize = javConfig.appletMaxSize
        preferredSize = javConfig.appletMinSize
        size = preferredSize
    }
    JFrame(javConfig[JavConfig.Key.TITLE]).apply {
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        add(client)
        pack()
        setLocationRelativeTo(null)
        isVisible = true
        preferredSize = size
        minimumSize = client.minimumSize
    }
    client.apply {
        init()
        start()
    }
}