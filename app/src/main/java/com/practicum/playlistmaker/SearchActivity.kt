package com.practicum.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity(), TrackAdapter.OnTrackClickListener {

    private var stringValue: String = AMOUNT_DEF
    private var lastSearchQuery: String = ""

    private val urlMusic: String = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(urlMusic)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val trackApiService = retrofit.create(TrackApiService::class.java)
    val trackList: MutableList<Track> = mutableListOf()
    val trackAdapter = TrackAdapter(trackList)
    var historyTrackList: MutableList<Track> = mutableListOf()
    val historyAdapter = TrackAdapter(historyTrackList)
    val historyManager = HistoryManager()


    private lateinit var queryInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val rvTrack = findViewById<RecyclerView>(R.id.recyclerView)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        val clearIcon = findViewById<ImageView>(R.id.clearIcon)
        val errorNet = findViewById<LinearLayout>(R.id.errorNet)
        val errorView = findViewById<LinearLayout>(R.id.errorView)
        val histView = findViewById<LinearLayout>(R.id.historyView)
        val refreshButton = findViewById<MaterialButton>(R.id.refreshButton)
        val clearHistoryButton = findViewById<MaterialButton>(R.id.clearSearchButton)
        val youSearch = findViewById<MaterialTextView>(R.id.youSearch)

        rvTrack.layoutManager = LinearLayoutManager(this)
        rvTrack.adapter = trackAdapter

        historyManager.init(getSharedPreferences("SEARCH_HISTORY", MODE_PRIVATE))
        historyTrackList.addAll(historyManager.getHistory())

        trackList.clear()
        searchEditText.text.clear()
        clearIcon.visibility = View.GONE
        errorNet.visibility = View.GONE
        errorView.visibility = View.GONE
        histView.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE
        youSearch.visibility = View.GONE
        trackAdapter.notifyDataSetChanged()

        trackAdapter.setOnTrackClickListener(this)
        historyAdapter.setOnTrackClickListener(this)

        if (savedInstanceState != null) {
            stringValue = savedInstanceState.getString(TEXT_AMOUNT, AMOUNT_DEF)
        }

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        findViewById<Toolbar>(R.id.toolbar).setNavigationOnClickListener {
            finish()
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearIcon.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE

                if (s.isNullOrEmpty() && searchEditText.hasFocus()) {
                    historyTrackList.clear()
                    historyTrackList.addAll(historyManager.getHistory())
                    rvTrack.adapter = historyAdapter
                    historyAdapter.updateTracks(historyTrackList)

                    if (historyTrackList.isNotEmpty()) {
                        histView.visibility = View.VISIBLE
                        clearHistoryButton.visibility = View.VISIBLE
                        youSearch.visibility = View.GONE
                    } else {
                        histView.visibility = View.GONE
                        clearHistoryButton.visibility = View.GONE
                        youSearch.visibility = View.GONE
                    }
                } else {
                    histView.visibility = View.GONE
                    clearHistoryButton.visibility = View.GONE
                    youSearch.visibility = View.GONE
                }

            }

            override fun afterTextChanged(s: Editable?) {
                stringValue = s.toString()
            }
        })

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchEditText.text.isEmpty()) {
                historyTrackList.clear()
                historyTrackList.addAll(historyManager.getHistory())
                rvTrack.adapter = historyAdapter
                historyAdapter.updateTracks(historyTrackList)

                if (historyTrackList.isNotEmpty()) {
                    histView.visibility = View.VISIBLE
                    clearHistoryButton.visibility = View.VISIBLE
                    youSearch.visibility = View.GONE
                } else {
                    histView.visibility = View.GONE
                    clearHistoryButton.visibility = View.GONE
                    youSearch.visibility = View.GONE
                }
            } else {
                histView.visibility = View.GONE
                clearHistoryButton.visibility = View.GONE
                youSearch.visibility = View.GONE
            }
        }

        clearIcon.setOnClickListener {
            searchEditText.text.clear()
            clearIcon.visibility = View.GONE
            errorNet.visibility = View.GONE
            errorView.visibility = View.GONE
            trackList.clear()
            trackAdapter.notifyDataSetChanged()

            historyTrackList.clear()
            historyTrackList.addAll(historyManager.getHistory())

            if (historyTrackList.isNotEmpty()) {
                histView.visibility = View.VISIBLE
                clearHistoryButton.visibility = View.VISIBLE
                youSearch.visibility = View.VISIBLE
                rvTrack.adapter = historyAdapter
                historyAdapter.updateTracks(historyTrackList)
            } else {
                histView.visibility = View.GONE
                clearHistoryButton.visibility = View.GONE
                youSearch.visibility = View.GONE
            }

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
            searchEditText.clearFocus()
        }

        queryInput = searchEditText
        fun performSearch(query: String) {
            if (query.isBlank()) return

            lastSearchQuery = query
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(queryInput.windowToken, 0)

            trackList.clear()
            trackAdapter.notifyDataSetChanged()

            trackApiService.searchTracks(query).enqueue(object : Callback<TrackResponse> {
                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null && responseBody.resultCount > 0) {
                            trackList.addAll(responseBody.results)
                            errorNet.visibility = View.GONE
                            errorView.visibility = View.GONE
                            rvTrack.adapter = trackAdapter
                        } else {
                            errorView.visibility = View.VISIBLE
                            errorNet.visibility = View.GONE
                        }
                    } else {
                        errorNet.visibility = View.VISIBLE
                        errorView.visibility = View.GONE
                    }
                    trackAdapter.notifyDataSetChanged()
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    errorNet.visibility = View.VISIBLE
                    errorView.visibility = View.GONE
                }
            })
        }

        queryInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch(queryInput.text.toString())
                queryInput.clearFocus()
                true
            } else {
                false
            }
        }

        refreshButton.setOnClickListener {
            queryInput.setText(lastSearchQuery)
            performSearch(lastSearchQuery)
        }

        clearHistoryButton.setOnClickListener {
            historyManager.clearHistory()
            historyTrackList.clear()
            historyAdapter.updateTracks(historyTrackList)
            histView.visibility = View.GONE
            clearHistoryButton.visibility = View.GONE
            youSearch.visibility = View.GONE
        }
    }

    override fun onTrackClick(track: Track) {
        historyManager.add(track)
        historyTrackList.clear()
        historyTrackList.addAll(historyManager.getHistory())
        historyAdapter.updateTracks(historyTrackList)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TEXT_AMOUNT, stringValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        stringValue = savedInstanceState.getString(TEXT_AMOUNT, AMOUNT_DEF)
    }

    companion object {
        const val TEXT_AMOUNT = "TEXT_AMOUNT"
        const val AMOUNT_DEF = ""
    }
}
