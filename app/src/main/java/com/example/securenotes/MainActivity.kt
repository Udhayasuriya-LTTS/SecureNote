package com.example.securenotes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.securenotes.database.NoteDataBase
import com.example.securenotes.repository.NoteRepository
import com.example.securenotes.viewmodel.NoteViewModel
import com.example.securenotes.viewmodel.NoteViewModelFactory


class MainActivity : AppCompatActivity() {
    lateinit var noteViewModel: NoteViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
        setContentView(R.layout.activity_main)

    }

    private fun setupViewModel() {
        val noteRepository = NoteRepository(NoteDataBase.getDatabase(this))
        val viewModelProviderFactory = NoteViewModelFactory(application, noteRepository)
        noteViewModel = ViewModelProvider(this, viewModelProviderFactory)[NoteViewModel::class.java]
    }
}