package ph.apper.android.pagatpatan.pomodowo.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.todo_view.view.*
import ph.apper.android.pagatpatan.pomodowo.R

class RVTodoAdapter(private val context: Activity, private val id: Array<String>, private val title: Array<String>, private val isChecked: Array<String>)
    : RecyclerView.Adapter<RVTodoAdapter.TodoViewHolder>()  {
    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


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

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {

        holder.itemView.apply {
            tv_todo_title.text = "${title[position]}"
        }
    }

    override fun getItemCount(): Int {
        return id.size
    }
}