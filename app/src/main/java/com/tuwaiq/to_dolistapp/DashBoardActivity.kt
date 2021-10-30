package com.tuwaiq.to_dolistapp

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.PaintFlagsDrawFilter
import android.os.Bundle
import android.service.controls.actions.FloatAction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tuwaiq.to_dolistapp.DTO.ToDo

class DashboardActivity : AppCompatActivity() {
    private lateinit var fab : FloatingActionButton
    private lateinit var dbHandler: DBHandler
    private lateinit var rvDashboard: RecyclerView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

    // This is my main class that will create the ToDo list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        rvDashboard = findViewById(R.id.rv_dashboard)
        fab = findViewById(R.id.fab_dashboard)
        toolbar = findViewById(R.id.dashboard_toolbar)

        setSupportActionBar(toolbar)
        title = "Dashboard"
        dbHandler = DBHandler(this)
        rvDashboard.layoutManager = LinearLayoutManager(this)

        // When the user clicks on add it will generate a dialog that will permit the user to add and name the task

        fab.setOnClickListener{
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Add ToDo")
            val view = layoutInflater.inflate(R.layout.dialog_dashboard, null)
            val toDoName = view.findViewById<EditText>(R.id.tv_todo)
            dialog.setView(view)
            dialog.setPositiveButton("Add") { _: DialogInterface, _: Int ->
                if (toDoName.text.isNotEmpty()) {
                    val toDo = ToDo()
                    toDo.name = toDoName.text.toString()
                    dbHandler.addToDo(toDo)
                    refreshList()
                }
            }
            dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->

            }
            dialog.show()
        }


    }

    // This function will enable the user to edit their own task
    fun updateToDo(toDo: ToDo){
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Update ToDo")
        val view = layoutInflater.inflate(R.layout.dialog_dashboard, null)
        val toDoName = view.findViewById<EditText>(R.id.tv_todo)
        toDoName.setText(toDo.name)
        dialog.setView(view)
        dialog.setPositiveButton("Update") { _: DialogInterface, _: Int ->
            if (toDoName.text.isNotEmpty()) {
                toDo.name = toDoName.text.toString()
                dbHandler.updateToDo(toDo)
                refreshList()
            }
        }
        dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->

        }
        dialog.show()
    }

    // This function will call refreshList function
    override fun onResume() {
        refreshList()
        super.onResume()
    }

    // This function will refresh the list after any operations immediately
    private fun refreshList(){
        rvDashboard.adapter = DashboardAdapter(this,dbHandler.getToDos())
    }
    // This class is my adapter that will hold my RecyclerView and menu operations
    class DashboardAdapter(private val activity: DashboardActivity,private val list: MutableList<ToDo>) :
        RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(activity).inflate(R.layout.rv_child_dashboard, p0, false))
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, p1: Int) {
            holder.toDoName.text = list[p1].name
            holder.toDoName.setOnClickListener {
                val intent = Intent(activity,ItemActivity::class.java)
                intent.putExtra(INTENT_TODO_ID, list[p1].id)
                intent.putExtra(INTENT_TODO_NAME, list[p1].name)
                activity.startActivity(intent)
            }
            holder.menu.setOnClickListener {
                val popup = PopupMenu(activity,holder.menu)
                popup.inflate(R.menu.dashboard_child)
                popup.setOnMenuItemClickListener {

                    // Used When loop to choose which option the user wants directly
                    when(it.itemId){
                        R.id.menu_edit->{
                            activity.updateToDo(list[p1])
                        }
                        R.id.menu_delete->{
                            val dialog = AlertDialog.Builder(activity)
                            dialog.setTitle("Are you sure?")
                            dialog.setMessage("Do you want to delete this task?")
                            dialog.setPositiveButton("Continue") {_: DialogInterface, _: Int ->
                                activity.dbHandler.deleteToDo(list[p1].id)
                                activity.refreshList()
                            }
                            dialog.setNegativeButton("Cancel") {_: DialogInterface, _: Int ->

                            }
                            dialog.show()

                        }
                        R.id.menu_mark_as_completed->{
                            activity.dbHandler.updateToDoItemCompletedStatus(list[p1].id,true)
                        }
                        R.id.menu_reset->{
                            activity.dbHandler.updateToDoItemCompletedStatus(list[p1].id,false)
                        }
                    }

                    true
                }
                popup.show()
            }
        }
        // To view the task name and menu
        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val toDoName: TextView = v.findViewById(R.id.tv_todo_name)
            val menu: ImageView = v.findViewById(R.id.iv_menu)
        }
    }

}