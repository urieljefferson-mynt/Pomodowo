package ph.apper.android.pagatpatan.pomodowo.adapters

import android.app.Activity
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistjeff.model.TodoModelClass
import kotlinx.android.synthetic.main.todo_view.view.*
import ph.apper.android.pagatpatan.pomodowo.Communicator
import ph.apper.android.pagatpatan.pomodowo.FinishedTasks
import ph.apper.android.pagatpatan.pomodowo.R
import ph.apper.android.pagatpatan.pomodowo.WorkFragment
import ph.apper.android.pagatpatan.pomodowo.dao.DatabaseHandler


class RVTodoAdapter(private val context: Activity, private var todos: MutableList<TodoModelClass>)
: RecyclerView.Adapter<RVTodoAdapter.TodoViewHolder>()  {
    var communicator: Communicator? = null
    private lateinit var updatedTodo : TodoModelClass




    val databaseHandler: DatabaseHandler = DatabaseHandler(context)
    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        override fun onClick(v: View?) {
            TODO("Not yet implemented")
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {

        return TodoViewHolder(
            //to get a reference to an existing view
            LayoutInflater.from(parent.context).inflate(
                R.layout.todo_view,
                parent,
                false
            )
        )
    }

    private fun toggleColorChange(tvTodoTitle: TextView, isChecked: Boolean, updatedTodo: TodoModelClass) {

        if(isChecked){
            Log.d("PAINT FLAGS?", tvTodoTitle.paintFlags.toString())
            Log.d("PAINT FLAGS?", STRIKE_THRU_TEXT_FLAG.toString())
            tvTodoTitle.paintFlags = tvTodoTitle.paintFlags or STRIKE_THRU_TEXT_FLAG
            Log.d("PAINT FLAGS?", tvTodoTitle.paintFlags.toString())

        } else {
            tvTodoTitle.paintFlags = tvTodoTitle.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
        }
    }


    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = todos[position]

        holder.itemView.apply {

            cb_done.setTag(todo)


            tv_todo_title.text = todo.title
            Log.d("TODO TITLE", todo.title)
            Log.d("LOGIC TEST", (1 or 0).toString())
            cb_done.isChecked = todo.isChecked
            Log.d("TODO ISCHECKED", todo.isChecked.toString())



            var updatedTodotitle = todo.title
            var updatedTodoisChecked = todo.isChecked
            var updatedTodoid = todo.id
            updatedTodo = TodoModelClass(updatedTodoid, updatedTodotitle, updatedTodoisChecked)



            toggleColorChange(tv_todo_title, todo.isChecked, updatedTodo)
            cb_done.setTag(todo)


            cb_done.setOnClickListener { v ->

                cb_done.setTag(todo)
                todo.isChecked = !todo.isChecked


                if (cb_done.isChecked) {
                    WorkFragment.breakPoints.value = WorkFragment.breakPoints.value?.plus(1)
//                    communicator?.passLongBreakCredits(FinishedTasks.LONG_BREAK_CREDITS)
                } else {
                    WorkFragment.breakPoints.value = WorkFragment.breakPoints.value?.minus(1)
//                    communicator?.passLongBreakCredits(FinishedTasks.LONG_BREAK_CREDITS)
                }

                val cb = v as CheckBox

                toggleColorChange(tv_todo_title, todo.isChecked, updatedTodo)
                Log.d("TODO ISCHECKED", todo.isChecked.toString())
                val updatedTodo: TodoModelClass = cb.tag as TodoModelClass
                databaseHandler.updateTodo(updatedTodo)
            }
        }
    }

    fun getTodoList(): MutableList<TodoModelClass>{
        return todos
    }


    override fun getItemCount(): Int {
        return todos.size
    }

}


