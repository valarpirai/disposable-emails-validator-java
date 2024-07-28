package org.disposable

import java.text.NumberFormat


fun main() {

    println(info())
    println(DisposableEmail.isDisposable("yopmail.com"))
    println(info())
    println(DisposableEmail.isDisposable("mailsac.com"))
    println(DisposableEmail.isDisposable("mailsac.co"))

    DisposableEmail.refreshDisposableDomains()
    println(info())
    println(DisposableEmail.isDisposable("yopmail.com"))
    println(DisposableEmail.isDisposable("mailsac.com"))
    println(DisposableEmail.isDisposable("mailsac.co"))
}

fun info(): String {
    val runtime = Runtime.getRuntime()

    val format = NumberFormat.getInstance()

    val sb = StringBuilder()
    val maxMemory = runtime.maxMemory()
    val allocatedMemory = runtime.totalMemory()
    val freeMemory = runtime.freeMemory()

    sb.append("free memory: " + format.format(freeMemory / 1024) + "<br/>")
    sb.append("allocated memory: " + format.format(allocatedMemory / 1024) + "<br/>")
    sb.append("max memory: " + format.format(maxMemory / 1024) + "<br/>")
    sb.append("total free memory: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024) + "<br/>")
    return sb.toString()
}