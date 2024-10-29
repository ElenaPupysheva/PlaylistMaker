package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {

    private var stringValue: String = AMOUNT_DEF


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_search)

            val rvTrack = findViewById<RecyclerView>(R.id.recyclerView)
            rvTrack.layoutManager = LinearLayoutManager(this)

            val trackList = TrackList.getTracks()

            val trackAdapter = TrackAdapter(trackList)
            rvTrack.adapter = trackAdapter

            if (savedInstanceState != null) {
                stringValue = savedInstanceState.getString(TEXT_AMOUNT, AMOUNT_DEF)
            }

            val toolbar = findViewById<Toolbar>(R.id.toolbar)
            setSupportActionBar(toolbar)

            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            toolbar.setNavigationOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                finish()
            }
            val searchEditText = findViewById<EditText>(R.id.searchEditText)
            val clearIcon = findViewById<ImageView>(R.id.clearIcon)

            searchEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    //что-то в будущем
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    clearIcon.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                }

                override fun afterTextChanged(s: Editable?) {
                    stringValue = s.toString()
                }
            })

            searchEditText.setOnClickListener {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)
            }

            clearIcon.setOnClickListener {
                searchEditText.text.clear()
                clearIcon.visibility = View.GONE
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
            }

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