package com.tuwaiq.to_dolistapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.SparseBooleanArray
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView

class MainActivity : AppCompatActivity() {
    private lateinit var addBtn: Button
    private lateinit var deleteBtn: Button
    private lateinit var clearBtn: Button
    private lateinit var editText: EditText
    private lateinit var listView: ListView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        addBtn = findViewById(R.id.add)
        deleteBtn = findViewById(R.id.delete)
        clearBtn = findViewById(R.id.clear)
        editText = findViewById(R.id.editText)
        listView = findViewById(R.id.listView)

        // Initializing the array lists and the adapter
        var itemList = arrayListOf<String>()
        var adapter =ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_multiple_choice
            , itemList)

        // Adding the items to the list when the add button is pressed
        addBtn.setOnClickListener {

            itemList.add(editText.text.toString())
            listView.adapter =  adapter
            adapter.notifyDataSetChanged()
            // This is because every time when you add the item the input space or the eidt text space will be cleared
            editText.text.clear()
        }
        // Clearing all the items in the list when the clear button is pressed
        clearBtn.setOnClickListener {

            itemList.clear()
            adapter.notifyDataSetChanged()
        }
        // Adding the toast message to the list when an item on the list is pressed
        listView.setOnItemClickListener { adapterView, view, i, l ->
            android.widget.Toast.makeText(this, "You Selected the item --> "+itemList.get(i), android.widget.Toast.LENGTH_SHORT).show()
        }
        // Selecting and Deleting the items from the list when the delete button is pressed
        deleteBtn.setOnClickListener {
            val position: SparseBooleanArray = listView.checkedItemPositions
            val count = listView.count
            var item = count - 1
            while (item >= 0) {
                if (position.get(item))
                {
                    adapter.remove(itemList.get(item))
                }
                item--
            }
            position.clear()
            adapter.notifyDataSetChanged()
        }
    }
}