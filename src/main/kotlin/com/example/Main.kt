package com.example

fun main() {

    val dispose = DisposableEmail.getInstance()

    println(dispose.isDisposable("yopmail.com"))
    println(dispose.isDisposable("mailsac.com"))
}