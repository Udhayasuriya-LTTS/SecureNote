package com.example.securenotes.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import com.example.securenotes.MainActivity
import com.example.securenotes.R
import com.example.securenotes.databinding.FragmentAddNoteBinding
import com.example.securenotes.databinding.FragmentHomeBinding
import com.example.securenotes.model.Note
import com.example.securenotes.security.CryptoManager
import com.example.securenotes.viewmodel.NoteViewModel

class AddNoteFragment : Fragment(R.layout.fragment_add_note), MenuProvider {
    private lateinit var addNoteBinding: FragmentAddNoteBinding
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var addNoteView: View
    private val cryptoManager: CryptoManager = CryptoManager()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        addNoteBinding = FragmentAddNoteBinding.inflate(inflater, container, false)
        return addNoteBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        noteViewModel = (activity as MainActivity).noteViewModel
        addNoteView = view

    }

    private fun saveNote(view: View) {
        val noteTitle = addNoteBinding.addNoteTitle.text.toString().trim()
        val noteDesc = addNoteBinding.addNoteDesc.text.toString().trim()

        if (noteTitle.isNotEmpty() && noteDesc.isNotEmpty()) {
            val note = Note(0, cryptoManager.encryptStr(noteTitle), cryptoManager.encryptStr(noteDesc))
            /*val note = Note(0, noteTitle, noteDesc)*/
            noteViewModel.addNote(note)
            Toast.makeText(addNoteView.context, "Note Saved", Toast.LENGTH_SHORT).show()
            view.findNavController().popBackStack(R.id.homeFragment, false)
        } else {
            Toast.makeText(addNoteView.context, "Please enter note title and description", Toast.LENGTH_SHORT)
                .show()
        }

    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.menu_add_note, menu)


    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.saveMenu -> {
                saveNote(addNoteView)
                true
            }

            else -> false
        }
    }

}