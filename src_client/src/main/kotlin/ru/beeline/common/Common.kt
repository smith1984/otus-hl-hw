package ru.beeline.common

import java.io.File

fun readFile(fileName: String): List<String> = File(fileName).useLines { it.toList() }