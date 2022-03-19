package com.codepath.apps.restclienttemplate.models

import android.os.Parcelable
import android.text.format.DateUtils
import kotlinx.parcelize.Parcelize
import org.json.JSONArray
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@Parcelize
class Tweet(var body: String = "", var createdAt: String = "", var user: User? = null):
    Parcelable {
    companion object {
        fun fromJson(jsonObject: JSONObject): Tweet {
            val tweet = Tweet()
            tweet.body = jsonObject.getString("text")
            tweet.createdAt = getRelativeTimeAgo(jsonObject.getString("created_at"))
            tweet.user = User.fromJson(jsonObject.getJSONObject("user"))

            return tweet
        }

        fun fromJsonArray(jsonArray: JSONArray): List<Tweet> {
            val tweets = ArrayList<Tweet>()

            for (i in 0 until jsonArray.length()) {
                tweets.add(fromJson(jsonArray.getJSONObject(i)))
            }

            return tweets
        }

        fun getRelativeTimeAgo(rawJsonData:String): String {
            val twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy"
            val sf = SimpleDateFormat(twitterFormat, Locale.ENGLISH)
            sf.isLenient = true

            var relativeDate = ""

            try {
                val dateMillis = sf.parse(rawJsonData).time
                relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS)
                    .toString()
            } catch (e : ParseException) {
                e.printStackTrace()
            }

            return relativeDate
        }
    }
}