package com.example.securenotes.adapter

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.securenotes.R
import com.example.securenotes.databinding.NoteLayoutBinding
import com.example.securenotes.model.Note
import com.example.securenotes.security.CryptoManager

class NoteAdapter: RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {
    private val cryptoManager: CryptoManager = CryptoManager()
    class NoteViewHolder(val itemBinding: NoteLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id &&
                    oldItem.noteDesc == newItem.noteDesc &&
                    oldItem.noteTitle == newItem.noteTitle
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
     }
     val differ = AsyncListDiffer(this, differCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(NoteLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
       return differ.currentList.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
      val currentNote=differ.currentList[position]

        holder.itemBinding.noteTitle.text=cryptoManager.decryptStr(currentNote.noteTitle)
        holder.itemBinding.noteDesc.text=cryptoManager.decryptStr(currentNote.noteDesc)
       /* holder.itemBinding.noteTitle.text=currentNote.noteTitle
        holder.itemBinding.noteDesc.text=currentNote.noteDesc*/

        holder.itemView.setOnClickListener{
            val bundle=Bundle()
            bundle.putSerializable("note",currentNote)
            it.findNavController().navigate(R.id.action_homeFragment_to_editNoteFragment2,bundle)
        }

    }
}