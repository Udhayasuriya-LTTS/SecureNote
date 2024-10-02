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
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.securenotes.MainActivity
import com.example.securenotes.R
import com.example.securenotes.databinding.FragmentAddNoteBinding
import com.example.securenotes.databinding.FragmentEditNoteBinding
import com.example.securenotes.model.Note
import com.example.securenotes.security.CryptoManager
import com.example.securenotes.viewmodel.NoteViewModel

class EditNoteFragment : Fragment(R.layout.fragment_edit_note), MenuProvider {
    private lateinit var editNoteBinding: FragmentEditNoteBinding
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var currentNote:Note
    private lateinit var editNoteView: View
    private val cryptoManager: CryptoManager = CryptoManager()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        editNoteBinding = FragmentEditNoteBinding.inflate(inflater, container, false)
        return editNoteBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        noteViewModel = (activity as MainActivity).noteViewModel
        currentNote= arguments?.getSerializable("note") as Note
       /* editNoteBinding.editNoteTitle.setText(cryptoManager.decryptStr(currentNote.noteTitle))
        editNoteBinding.editNoteDesc.setText(cryptoManager.decryptStr(currentNote.noteDesc))*/
         editNoteBinding.editNoteTitle.setText((currentNote.noteTitle))
        editNoteBinding.editNoteDesc.setText((currentNote.noteDesc))
        editNoteView = view
        editNoteBinding.addNoteFab.setOnClickListener {
            val noteTitle = editNoteBinding.editNoteTitle.text.toString().trim()
            val noteDesc = editNoteBinding.editNoteDesc.text.toString().trim()

            if (noteTitle.isNotEmpty() && noteDesc.isNotEmpty()) {
                val note = Note(
                    currentNote.id,
                    cryptoManager.encryptStr(noteTitle),
                    cryptoManager.encryptStr(noteDesc),
             )
                /*val note = Note(
                    currentNote.id,
                    noteTitle,
                    noteDesc,
                )*/
                noteViewModel.updateNote(note)
                view.findNavController().popBackStack(R.id.homeFragment, false)
            } else {
                Toast.makeText(context, "Please enter note title", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }
    private fun deleteNote(){
        AlertDialog.Builder(requireContext()).apply { setTitle("Delete Note")
        setMessage("Do you want to delete this note?")
            setPositiveButton("Delete"){_,_->
                noteViewModel.deleteNote(currentNote)
                Toast.makeText(context,"Note Deleted",Toast.LENGTH_SHORT).show()
                view?.findNavController()?.popBackStack(R.id.homeFragment,false)
            }
            setNegativeButton("Cancel",null)
        }.create().show()
    }
    private fun saveNote(view: View) {
        val noteTitle = editNoteBinding.editNoteTitle.text.toString().trim()
        val noteDesc = editNoteBinding.editNoteDesc.text.toString().trim()

        if (noteTitle.isNotEmpty() && noteDesc.isNotEmpty()) {
            val note = Note(0, noteTitle, noteDesc)
            noteViewModel.updateNote(note)
            Toast.makeText(context, "Note Saved", Toast.LENGTH_SHORT).show()
            view.findNavController().popBackStack(R.id.homeFragment, false)
        } else {
            Toast.makeText(context, "Please enter note title and description", Toast.LENGTH_SHORT)
                .show()
        }

    }
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.menu_edit_note, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.deleteMenu -> {
                deleteNote()
                true
            }
            R.id.homeFragment->{
                saveNote(editNoteView)
                true
            }

            else -> false
        }
    }
}