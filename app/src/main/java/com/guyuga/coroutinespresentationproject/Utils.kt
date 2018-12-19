package com.guyuga.coroutinespresentationproject

/**
 * Created by Guy Lidan on 17/12/2018.
 */

suspend fun measureTime(block: suspend () -> Unit) : String {
    val startTime = System.nanoTime()
    block()
    val endTime = System.nanoTime()
    return "measured time: ${(endTime - startTime) / 1.0e9} seconds"
}

suspend fun loadWords(network: Network, id: Int) : String{
    val response = network.callService(Network.getUrl(id))
    val jason = network.parseJason(response)
    return jason.getString("title").substringBefore(" ")
}

fun Int.plusPlus(i :Int) : Int {
    return i+1
}
