package weather.pusher.util

/* Created by evolandlupiz on 02/13/19
 * This file is subject to the terms and conditions defined in
 * file 'WEATHERPUSHER_LICENSE.txt', which is part of this source code package.*/

import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.auth.oauth2.CredentialStore
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.java6.auth.oauth2.FileCredentialStore
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import com.google.api.services.calendar.model.Events
import com.google.gson.Gson
import com.google.gson.JsonArray
import weather.pusher.BuildConfig
import weather.pusher.models.WeatherData
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.time.LocalDate
import java.util.*

/* @desc calendar loader that will perform various operations based on google calendar api */
class GoogleCalendarLoader {
    private val LOCATION_SERVICE_PORT: Int = 8888
    private val APPLICATION_NAME: String = "Weather Pusher"
    private val TOKENS_DIRECTORY_PATH: String = "tokens"
    private val CREDENTIALS_FILE_PATH: String = "credentials.json"
    private val ACCESS_TYPE: String = "offline"
    private val USER_ID: String = "user"
    private val JSON_FACTORY: JsonFactory = JacksonFactory.getDefaultInstance()
    private val SCOPES: List<String> = Collections.singletonList(CalendarScopes.CALENDAR)
    private val mNow: DateTime? = DateTime(System.currentTimeMillis())
    private var mService: Calendar? = null
    private var mCredentials: Credential? = null
    private var mAuthentication: Events? = null
    private var mItems: List<Event>? = null
    private var mFlow: GoogleAuthorizationCodeFlow? = null
    private var mReceiver: LocalServerReceiver? = null
    private var mBrowser: AuthorizationCodeInstalledApp.Browser? = null

    /**
     * routine to perform authentication on a clients google calendar.
     */
    fun fireAuthentication(activity: AppCompatActivity?) {
        val HTTP_TRANSPORT: HttpTransport? = AndroidHttp.newCompatibleTransport()
        this.mService = Calendar
            .Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT, activity))
            .setApplicationName(APPLICATION_NAME)
            .build()
        this.mAuthentication = PerformAuthenticationTask(
            activity,
            this.mService,
            this.mNow,
            this.mItems
        ).execute().get()
    }

    /**
     * routine used to determine clients credentials necessary for authenticating and generating an access token.
     */
    private fun getCredentials(HTTP_TRANSPORT: HttpTransport?, activity: AppCompatActivity?): Credential? {
        val inputStream: InputStream = activity!!.resources!!.assets!!.open(CREDENTIALS_FILE_PATH)
        val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(inputStream))
        val mDirectory: File? = File(activity.filesDir.absolutePath + File.separator + TOKENS_DIRECTORY_PATH)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) { /* (26)oreo */
            val mFile = File(activity.getExternalFilesDir(null)!!.absolutePath + File.separator + TOKENS_DIRECTORY_PATH)
            val fileFactory: FileDataStoreFactory? = FileDataStoreFactory(mFile)
            this.mFlow = GoogleAuthorizationCodeFlow
                .Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(fileFactory)
                .setAccessType(ACCESS_TYPE)
                .build()
        }
        else if(Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1
                || Build.VERSION.SDK_INT == Build.VERSION_CODES.N) { /* (24)nougat , (25)nougat++ */
            if (!mDirectory!!.exists()) {
                mDirectory.mkdir()
                mDirectory.setReadable(true, true)
                mDirectory.setWritable(true, true)
                mDirectory.setExecutable(true, false)
            }
            val mFile = File(mDirectory, CREDENTIALS_FILE_PATH)
            mFile.setReadable(true, true)
            mDirectory.setWritable(true, true)
            mDirectory.setExecutable(true, false)
            val mFileContents: String =
                activity.resources.assets
                    .open(CREDENTIALS_FILE_PATH).bufferedReader().use { it.readText() }
            mFile.outputStream().bufferedWriter().use { out ->
                out.write(mFileContents)
            }
            val mCredentialStore: CredentialStore? = FileCredentialStore(mFile, JSON_FACTORY)
            this.mFlow = GoogleAuthorizationCodeFlow
                .Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setCredentialStore(mCredentialStore)
                .setAccessType(ACCESS_TYPE)
                .build()
        } else {
            TODO("VERSION.SDK_INT <= 23, NOT SUPPORTED")
        }
        this.mReceiver = LocalServerReceiver.Builder().setPort(LOCATION_SERVICE_PORT).build()
        this.mBrowser = AuthorizationCodeInstalledApp.Browser {
            if(it != null){
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(it)
                activity.startActivity(intent)
            }
        }
        this.mCredentials = PerformCredentialsTask(
            this.mFlow,
            this.mReceiver,
            this.mBrowser
        ).execute().get()

        return this.mCredentials
    }

    /**
     * routine to create events.
     */
    fun fireCreateEvent(data: JsonArray, activity: AppCompatActivity) {
        val HTTP_TRANSPORT: HttpTransport? = AndroidHttp.newCompatibleTransport()
        this.mService = Calendar
            .Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT, activity))
            .setApplicationName(APPLICATION_NAME)
            .build()
        PerformCreateEventTask(
            activity,
            this.mService,
            data
        ).execute().get()
    }

    /**
     * asynchronous task to perform credentials task.
     */
    class PerformCredentialsTask(
        private val mFlow: GoogleAuthorizationCodeFlow?,
        private val mReceiver: LocalServerReceiver?,
        private val mBrowser: AuthorizationCodeInstalledApp.Browser?
    ) : AsyncTask<Void, Void, Credential>(){
        override fun doInBackground(vararg params: Void?): Credential {
            return AuthorizationCodeInstalledApp(mFlow, mReceiver, mBrowser).authorize("user")
        }
        override fun onPreExecute() {
            super.onPreExecute()
        }
        override fun onPostExecute(result: Credential?) {
            super.onPostExecute(result)
        }
    }

    /**
     * asynchronous task to perform authentication task.
     */
    class PerformAuthenticationTask(
        private val activity: AppCompatActivity?,
        private val mService: Calendar?,
        private val mNow: DateTime?,
        private var mItems: List<Event>?
    ) : AsyncTask<Void, Void, Events>(){
        private var mEvents: Events? = null
        private val CALENDAR_ID: String = "primary"
        private val OAUTH_API_KEY_TOKEN: String = BuildConfig.OWM_OAUTH_API_TOKEN
        override fun doInBackground(vararg params: Void?): Events? {
            this.mEvents = mService?.events()?.list(CALENDAR_ID)
                ?.setKey(OAUTH_API_KEY_TOKEN)
                ?.setMaxResults(1)
                ?.setTimeMin(mNow)
                ?.setOrderBy("startTime")
                ?.setSingleEvents(true)
                ?.execute()
            return this.mEvents
        }
        override fun onPreExecute() {
            super.onPreExecute()
        }
        override fun onPostExecute(result: Events?) {
            super.onPostExecute(result)
            Toast.makeText(activity, "You're now signed in to your calendar.", Toast.LENGTH_SHORT).show()
            mItems = mEvents?.items
            if (mItems!!.isEmpty()) {
            } else {
                mItems!!.forEach {
                    val start: DateTime = it.start.dateTime
                }
            }
        }
    }

    /**
     * asynchronous task to perform creating a calendar event task.
     */
    class PerformCreateEventTask(
        activity: AppCompatActivity
        , private val mService: Calendar?
        , private val mData: JsonArray
    ) : AsyncTask<Void, Void, Boolean>() {
        private val activity: AppCompatActivity? = activity
        private val CALENDAR_ID: String = "primary"
        override fun doInBackground(vararg params: Void?): Boolean {
            val data = Gson().fromJson(this.mData.get(0).asString,
                WeatherData::class.java).client_date

            val initialdate: Any = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> { /* (26)oreo */
                    LocalDate.parse(data, java.time.format.DateTimeFormatter.ISO_DATE)
                }
                Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1
                        || Build.VERSION.SDK_INT == Build.VERSION_CODES.N -> { /* (24)nougat , (25)nougat++ */
                    val formatter = org.threeten.bp.format.DateTimeFormatter.ofPattern("[yyyy-MM-dd]",Locale.ENGLISH)
                    org.threeten.bp.LocalDate.parse(data, formatter)
                }
                else -> { TODO("VERSION.SDK_INT <= 23, NOT SUPPORTED") }
            }

            val eventsmapAPI26: MutableMap<LocalDate, ArrayList<String>> = mutableMapOf()
            val eventsmapAPI25: MutableMap<org.threeten.bp.LocalDate, ArrayList<String>> = mutableMapOf()

            val eventlist: ArrayList<ArrayList<String>> = ArrayList()
            for(i in 0 until 5){
                eventlist.add(ArrayList())
            }
            for(i in 0 until this.mData.size()){
                val event: WeatherData = Gson().fromJson(this.mData.get(i).asString,
                    WeatherData::class.java)
                var details: String = "T: ${event.client_time} , Min: ${event.client_min} , Max: ${event.client_max}"
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                        val date = initialdate as LocalDate
                        when (LocalDate.parse(event.client_date, java.time.format.DateTimeFormatter.ISO_DATE)) {
                            date.plusDays(0) -> eventlist.get(0).add(details)
                            date.plusDays(1) -> eventlist.get(1).add(details)
                            date.plusDays(2) -> eventlist.get(2).add(details)
                            date.plusDays(3) -> eventlist.get(3).add(details)
                            date.plusDays(4) -> eventlist.get(4).add(details)
                        }
                    }
                    Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1
                            || Build.VERSION.SDK_INT == Build.VERSION_CODES.N-> { /* ((24)nougat, (25)nougat++ */
                        val date = initialdate as org.threeten.bp.LocalDate
                        when (org.threeten.bp.LocalDate.parse(event.client_date, org.threeten.bp.format.DateTimeFormatter.ISO_DATE)) {
                            date.plusDays(0) -> eventlist.get(0).add(details)
                            date.plusDays(1) -> eventlist.get(1).add(details)
                            date.plusDays(2) -> eventlist.get(2).add(details)
                            date.plusDays(3) -> eventlist.get(3).add(details)
                            date.plusDays(4) -> eventlist.get(4).add(details)
                        }
                    }
                    else -> { TODO("VERSION.SDK_INT < O") }
                }
            }
            /* O(n^2) but this is acceptable in this scenario */
            for(k in 0 until eventlist.size){
                for(l in 0 until eventlist.get(k).size){
                    when {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                            val date = initialdate as LocalDate
                            eventsmapAPI26.put(date.plusDays(k.toLong()), eventlist.get(k))}
                        Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1
                                || Build.VERSION.SDK_INT == Build.VERSION_CODES.N -> { /* (24)nougat , (25)nougat++ */
                            val date = initialdate as org.threeten.bp.LocalDate
                            eventsmapAPI25.put(date.plusDays(k.toLong()), eventlist.get(k))
                        }
                        else -> { TODO("VERSION.SDK_INT < O") }
                    }
                }
            }

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                eventsmapAPI26.forEach{(t, u) ->
                    var event: Event = Event().setSummary("Weather Notification, from WeatherPusher")
                    val starttime = DateTime("$t"+"T00:00:00")
                    val endtime = DateTime("$t"+"T24:00:00")
                    val start = EventDateTime().setDateTime(starttime).setTimeZone("America/Los_Angeles")
                    val end = EventDateTime().setDateTime(endtime).setTimeZone("America/Los_Angeles")
                    event.start = start
                    event.end = end
                    event.description = ""
                    u.iterator().forEach {
                        event.description += (it + System.lineSeparator())
                    }
                    event = this.mService!!.events().insert(CALENDAR_ID, event).execute()
                }
            } else if( Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1
                || Build.VERSION.SDK_INT == Build.VERSION_CODES.N) {
                eventsmapAPI25.forEach{(t, u) ->
                    var event: Event = Event().setSummary("Weather Notification, from WeatherPusher")
                    val starttime = DateTime("$t"+"T00:00:00")
                    val endtime = DateTime("$t"+"T24:00:00")
                    val start = EventDateTime().setDateTime(starttime).setTimeZone("America/Los_Angeles")
                    val end = EventDateTime().setDateTime(endtime).setTimeZone("America/Los_Angeles")
                    event.start = start
                    event.end = end
                    event.description = ""
                    u.iterator().forEach {
                        event.description += (it + System.lineSeparator())
                    }
                    event = this.mService!!.events().insert(CALENDAR_ID, event).execute()
                }
            } else {
                TODO("VERSION.SDK_INT < O")
            }

            return true
        }
        override fun onPreExecute() {
            super.onPreExecute()
        }
        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
            when(result){
                true -> Toast.makeText(this.activity, "Your events were created: $result", Toast.LENGTH_SHORT).show()
                false -> Toast.makeText(this.activity, "Your events were not created: $result", Toast.LENGTH_SHORT).show()
            }
        }
    }
}