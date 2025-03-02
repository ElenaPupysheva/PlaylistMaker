package com.practicum.playlistmaker.ui.tracks

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.AMOUNT_DEF
import com.practicum.playlistmaker.domain.models.CLICK_DEBOUNCE_DELAY
import com.practicum.playlistmaker.domain.models.EXTRA_TRACK
import com.practicum.playlistmaker.domain.models.SEARCH_DEBOUNCE_DELAY
import com.practicum.playlistmaker.domain.models.TEXT_AMOUNT
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.player.PlayerActivity


class SearchActivity : AppCompatActivity(), TrackAdapter.OnTrackClickListener {

    private val tracksInteractor by lazy {
        Creator.tracksInteractor
    }

    private val historyInteractor by lazy {
        Creator.historyInteractor
    }

    private var stringValue: String = AMOUNT_DEF
    private var lastSearchQuery: String = ""

    val trackList: MutableList<Track> = mutableListOf()
    val trackAdapter = TrackAdapter(trackList)
    val historyList: MutableList<Track> = mutableListOf()
    val historyAdapter = TrackAdapter(historyList)
    private lateinit var queryInput: EditText
    private  val handler = Handler(Looper.getMainLooper())
    private lateinit var errorNet: LinearLayout
    private lateinit var errorView: LinearLayout
    private lateinit var rvTrack: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var clearHistoryButton: MaterialButton
    private lateinit var searchTitle: MaterialTextView
    private var currentQuery: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        rvTrack = findViewById(R.id.recyclerView)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        val clearIcon = findViewById<ImageView>(R.id.clearIcon)
        errorNet = findViewById(R.id.errorNet)
        errorView = findViewById(R.id.errorView)
        progressBar = findViewById(R.id.progressBar)
        val refreshButton = findViewById<MaterialButton>(R.id.refreshButton)
        val historyView = findViewById<LinearLayout>(R.id.historyView)
        clearHistoryButton = findViewById(R.id.clearSearch)
        searchTitle = findViewById(R.id.you_Search)

        rvTrack.layoutManager = LinearLayoutManager(this)
        rvTrack.adapter = trackAdapter

        historyList.clear()
        historyList.addAll(historyInteractor.getHistory())

        trackList.clear()
        searchEditText.text.clear()
        clearIcon.visibility = View.GONE
        errorNet.visibility = View.GONE
        errorView.visibility = View.GONE
        historyView.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE
        trackAdapter.notifyDataSetChanged()

        trackAdapter.setOnTrackClickListener(this)
        historyAdapter.setOnTrackClickListener(this)

        if (savedInstanceState != null) {
            stringValue = savedInstanceState.getString(TEXT_AMOUNT, AMOUNT_DEF)
        }
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }


        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearIcon.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                searchDebounce(s.toString())

            }

            override fun afterTextChanged(s: Editable?) {
                stringValue = s.toString()
            }
        })

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)
            }
        }
        searchEditText.setOnFocusChangeListener { _, hasFocus ->

            if (hasFocus && searchEditText.text.isEmpty()) {
                historyList.clear()
                historyList.addAll(historyInteractor.getHistory())
                rvTrack.adapter = historyAdapter
                historyAdapter.updateTracks(historyList)

                if (historyList.isNotEmpty()) {
                    historyView.visibility = View.GONE
                    clearHistoryButton.visibility = View.VISIBLE

                } else {
                    historyView.visibility = View.GONE
                    clearHistoryButton.visibility = View.GONE

                }
            } else {
                historyView.visibility = View.GONE
                clearHistoryButton.visibility = View.GONE

            }
        }

        clearIcon.setOnClickListener {
            queryInput.text.clear()
            clearIcon.visibility = View.GONE
            trackList.clear()
            trackAdapter.updateTracks(trackList)

            historyList.clear()
            historyList.addAll(historyInteractor.getHistory())
            if (historyList.isNotEmpty()) {
                historyView.visibility = View.VISIBLE
                clearHistoryButton.visibility = View.VISIBLE
                searchTitle.visibility = View.VISIBLE
                rvTrack.adapter = historyAdapter
                historyAdapter.updateTracks(historyList)
            } else {
                historyView.visibility = View.GONE
                clearHistoryButton.visibility = View.GONE
            }

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)

        }

        queryInput = searchEditText
        queryInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch(queryInput.text.toString())
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
            historyInteractor.clearHistory()
            historyList.clear()
            historyAdapter.updateTracks(historyList)
            historyView.visibility = View.GONE
            clearHistoryButton.visibility = View.GONE

        }

    }
    private fun performSearch(query: String) {
        if (query.isBlank()) return

        lastSearchQuery = query
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(queryInput.windowToken, 0)

        progressBar.visibility = View.VISIBLE
        errorNet.visibility = View.GONE
        errorView.visibility = View.GONE
        rvTrack.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE
        searchTitle.visibility = View.GONE

        trackList.clear()
        trackAdapter.notifyDataSetChanged()

        Thread {
            val result = tracksInteractor.searchTracks(query)

            runOnUiThread {
                progressBar.visibility = View.GONE
                rvTrack.visibility = View.VISIBLE

                if (result.isNotEmpty()) {
                    trackList.addAll(result)
                    errorNet.visibility = View.GONE
                    errorView.visibility = View.GONE
                    rvTrack.adapter = trackAdapter
                } else {
                    errorView.visibility = View.VISIBLE
                    errorNet.visibility = View.GONE
                }
            }
        }.start()
    }

    override fun onTrackClick(track: Track) {
        historyInteractor.addTrack(track)
        historyList.clear()
        historyList.addAll(historyInteractor.getHistory())
        historyAdapter.updateTracks(historyList)

        if (clickDebounce()){
            val intent = Intent(this, PlayerActivity::class.java)
            val jsonTrack = Gson().toJson(track)
            intent.putExtra(EXTRA_TRACK, jsonTrack)

            startActivity(intent)}
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TEXT_AMOUNT, stringValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        stringValue = savedInstanceState.getString(TEXT_AMOUNT, AMOUNT_DEF)
    }


    private var isClickAllowed = true

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private val searchRunnable = Runnable {performSearch(currentQuery) }
    private fun searchDebounce(query: String) {
        currentQuery = query
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

}