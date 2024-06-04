package com.example.academy_exam_weather_app

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class MainActivity : AppCompatActivity() {
    private var userCity: EditText? = null
    private var mainButton: Button? = null
    private var resultText: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userCity = findViewById(R.id.user_city)
        mainButton = findViewById(R.id.main_button)
        resultText = findViewById(R.id.result_text)

        mainButton?.setOnClickListener(View.OnClickListener {
            if (TextUtils.isEmpty(userCity?.text?.toString())) {
                Toast.makeText(this@MainActivity, R.string.emptyInput, Toast.LENGTH_LONG).show()
            } else {
                val city = userCity?.text.toString()
                val apiKey = ""
                val url = "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKey&units=metric&lang=ru"

                GetURLData().execute(url)
            }
        })
    }

    private inner class GetURLData : AsyncTask<String?, String?, String?>() {
        override fun onPreExecute() {
            super.onPreExecute()
            resultText?.setText("wait")
        }

        override fun doInBackground(vararg urls: String?): String? {
            var connection: HttpURLConnection? = null
            var reader: BufferedReader? = null

            try {
                val url = URL(urls[0]?: "")
                connection = url.openConnection() as HttpURLConnection
                connection.connect()

                val stream = connection.inputStream
                reader = BufferedReader(InputStreamReader(stream))

                val buffer = StringBuilder()
                var line: String?

                while (reader.readLine().also { line = it }!= null) buffer.append(line).append("\n")

                return buffer.toString()
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                connection?.disconnect()
                reader?.close()
            }

            return null
        }

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            try {
                val jsonObject = JSONObject(result)
                resultText?.text = "temperature: ${jsonObject.getJSONObject("main").getDouble("temp")}"
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }
}
