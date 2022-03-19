package com.codepath.apps.restclienttemplate

import android.content.Intent
import android.graphics.Color
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class ComposeActivity : AppCompatActivity() {

    lateinit var etCompose: EditText
    lateinit var btnTweet: Button
    lateinit var client: TwitterClient
    lateinit var tvCharacterCount: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        etCompose = findViewById(R.id.etTweetCompose)
        btnTweet = findViewById(R.id.btnTweet)
        tvCharacterCount = findViewById(R.id.tvCharacterCount)
        val originalTweetColor = tvCharacterCount.currentTextColor

        client = TwitterApplication.getRestClient(this)

        etCompose.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                return
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                return
            }

            override fun afterTextChanged(s: Editable?) {
                Log.e(TAG, "updated text")
                val lengthOfTweet = etCompose.text.toString().length
                val remainingTweetLength = 280 - lengthOfTweet
                val charactersRemaining = "Characters Remaining: $remainingTweetLength/280"
                tvCharacterCount.text = charactersRemaining

                // change color depending on the number of remaining characters
                if (remainingTweetLength < 0)
                    tvCharacterCount.setTextColor(Color.parseColor("#FF0000"))
                else
                    tvCharacterCount.setTextColor(originalTweetColor)
            }

        })

        // Handling user's click on the tweet button
        btnTweet.setOnClickListener{

            // Get content from etCompose
            val tweetContent = etCompose.text.toString()

            // Requirements
            // 1. Make sure the tweet is not empty
            if (tweetContent.isEmpty()) {
                Toast.makeText(this, "Empty Tweets not allowed!", Toast.LENGTH_SHORT).show()
            }

            // 2. Make sure the tweet is under character limit
            else if (tweetContent.length > 280) {
                Toast.makeText(this, "Tweet is too long. 280 character limit", Toast.LENGTH_SHORT).show()
            }
            else {
                client.publishTweet(tweetContent, object: JsonHttpResponseHandler(){
                    override fun onFailure(
                        statusCode: Int,
                        headers: Headers?,
                        response: String?,
                        throwable: Throwable?
                    ) {
                        Log.e(TAG, "Failed to publish the tweet", throwable)
                    }

                    override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                        Log.i(TAG, "Successfully published the tweet!")

                        val tweet = Tweet.fromJson(json.jsonObject)
                        val intent = Intent()
                        intent.putExtra("tweet", tweet)
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                })
            }
        }
    }

    companion object {
        const val TAG = "ComposeActivity"
    }
}