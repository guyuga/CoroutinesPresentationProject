package com.guyuga.coroutinespresentationproject

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

import kotlinx.coroutines.*

class MainActivity : AppCompatActivity(), CoroutineScope {

    /**
     * setting up the scope of all the coroutines
     * activity implements [CoroutineScope]
     * adding the default thread + all jobs
     */
    lateinit var job: Job
    val sting : String by lazy { requestNetworkButton.text.toString() }
    override val coroutineContext
        get() = Dispatchers.Main + job


    // get Network reference
    private val network by lazy { Network() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // init coroutines [Job]
        job = Job()


        /**
         * example of order of operations in Java
         */
        // Order of execution.
        for (i in 1..10) {
            //do something time consuming
        }

        // example of sequential programing and non blocking work
        sequentialNonBlockingExample()

        // Click listeners
        // will continue immediately after sequentialNonBlockingExample starts
        requestNetworkButton.setOnClickListener {
            if(checkBox.isChecked) startAsynchronousNetworkCall()
            else startSynchroniseNetworkCall()}
        ClearButton.setOnClickListener { body.text = "" }
    }






    /**
     * This demonstrate coroutines approach to sequential code.
     * As well as Non-Blocking coroutine
     *
     * generating counter from 10 to 1 and then hide view
     *
     * coroutines have two builder blocks:
     * launch (sequential),
     * async (parallel)
     */
    @SuppressLint("SetTextI18n")
    private fun sequentialNonBlockingExample() {
        launch {
            for (i in 10 downTo 0) {
                counter.text = "starting in: $i" //set text to TextView
                delay(1000) // non blocking!!!!! delay. NOTE: the suspend indication icon
            }
            counter.visibility = View.GONE //set TextView visibility to GONE
        }
    }












    /**
     * CALLBACK HELL !!!!!! }}}}}}
     *
     * example of delegation (listeners) to get value back from long operations.
     * multiple operations create what is known as callback hell
     *
     *   איך הייתם רוצים ליראות את הקוד הזה?
     */
    private fun callServiceWithDelegation(id: Int) {
        // call network service without coroutines
        // known as callback hell
        network.callService(Network.getUrl(id)) { response: String ->
            network.parseJason(response) { jsonObject ->
                network.getStringFromJson(jsonObject, "title") {
                    val word = it.substringBefore(" ")
                }
            }
        }
    }






    /**
     * no need for callback hell
     * coroutines helps us by enabling synchronous operations
     */
    private suspend fun callServiceWitheCoroutines(id: Int) : String {
        val response = network.callService(Network.getUrl(id))
        val jsonObject = network.parseJason(response)
        val word = network.getStringFromJson(jsonObject, "title")
        return word.substringBefore(" ")
    }













    /**
     * Sequentially call service
     */
    private fun startAsynchronousNetworkCall() {
        addToTextField("\nLoading Asynchronous data: ")

        launch {
            val responseList = mutableListOf<Deferred<String>>()
            val time = measureTime {
                for (i in 1..50) {
                    responseList += async { callServiceWitheCoroutines(i) }
                }
                responseList.forEach { addToTextField(it.await()) }
            }
            addToTextField("\n $time")
        }
    }







    /**
     * Sequentially call service
     */
    private fun startSynchroniseNetworkCall() {
        addToTextField("\nLoading synchronise data: ")

        launch {
            requestNetworkButton.isEnabled = false
            val responseList = mutableListOf<String>()
            val time = measureTime {
                for (i in 1..50) {
                    responseList += callServiceWitheCoroutines(i)
                }
                responseList.forEach { addToTextField(it) }
            }
            addToTextField("\n $time")
            requestNetworkButton.isEnabled = true
        }

    }

    /**
     * prints String to app Body TextView
     */
    private fun addToTextField(string: String?) {
        body.text = body.text.toString().plus(string).plus(", ")
    }



}
