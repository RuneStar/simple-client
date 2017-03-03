package com.runesuite

import com.runesuite.general.JavConfig
import com.runesuite.general.RuneScape
import java.applet.Applet
import java.applet.AppletContext
import java.applet.AppletStub
import java.awt.Dimension
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Paths
import java.util.jar.JarFile
import javax.swing.JFrame
import javax.swing.WindowConstants

fun main(args: Array<String>) {
    val revision = RuneScape.revision
    val jar = Paths.get(System.getProperty("java.io.tmpdir"), "runescape-gamepack-$revision.jar").toFile()
    try {
        JarFile(jar, true)
    } catch (e: Exception) {
        RuneScape.downloadGamepack(jar.toPath())
    }
    val classLoader = URLClassLoader(arrayOf(jar.toURI().toURL()))
    val client = classLoader.loadClass("client").newInstance() as Applet
    client.apply {
        val jc = JavConfig()
        layout = null
        setStub(JavConfigStub(jc))
        minimumSize = Dimension(200, 350)
        maximumSize = Dimension(jc[JavConfig.Key.APPLET_MAXWIDTH].toInt(), jc[JavConfig.Key.APPLET_MAXHEIGHT].toInt())
        preferredSize = Dimension(jc[JavConfig.Key.APPLET_MINWIDTH].toInt(), jc[JavConfig.Key.APPLET_MINHEIGHT].toInt())
        init()
        start()
    }
    JFrame("Runescape").apply {
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        add(client)
        pack()
        preferredSize = size
        setLocationRelativeTo(null)
        isVisible = true
    }
}

class JavConfigStub(private val javConfig: JavConfig) : AppletStub {

    override fun getDocumentBase(): URL = codeBase

    override fun appletResize(width: Int, height: Int) {}

    override fun getParameter(name: String): String? = javConfig[name]

    override fun getCodeBase(): URL = javConfig.url

    override fun getAppletContext(): AppletContext? = null

    override fun isActive(): Boolean = true
}