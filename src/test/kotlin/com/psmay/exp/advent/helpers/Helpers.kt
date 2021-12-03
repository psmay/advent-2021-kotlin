package com.psmay.exp.advent.helpers

import java.io.File

fun getTextFile(name: String) = File("src/test/resources", "$name.txt")