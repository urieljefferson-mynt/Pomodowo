package ph.apper.android.pagatpatan.pomodowo

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolistjeff.dao.DatabaseHandler
import com.example.todolistjeff.model.TodoModelClass
import kotlinx.android.synthetic.main.fragment_work.*
import kotlinx.android.synthetic.main.fragment_work.view.*
import ph.apper.android.pagatpatan.pomodowo.adapters.RVTodoAdapter
import java.util.concurrent.TimeUnit


class WorkFragment : Fragment(){

    private lateinit var communicator: Communicator

    companion object {
        private lateinit var timer: CountDownTimer
        fun newInstance() = WorkFragment() // for menu
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val workView = inflater.inflate(R.layout.fragment_work, container, false)

        // Communicator
        communicator = activity as Communicator

        workView.btn_stop.visibility = View.INVISIBLE
        workView.btn_pause.visibility = View.INVISIBLE
        workView.tv_countdown.visibility = View.INVISIBLE
        workView.tv_focustext.visibility = View.INVISIBLE

        workView.btn_startBreakNow.visibility = View.INVISIBLE
        workView.btn_startLongBreakNow.visibility = View.INVISIBLE

        // Start Button
        workView.btn_start.setOnClickListener {

            var timeInput = arguments?.getString("focus")

            if (timeInput.isNullOrBlank()) {
                Toast.makeText(context, "Set a focus time first", Toast.LENGTH_SHORT).show()
            } else {

                var timeStart = timeInput.toString()

                // CountDownTimer
                timer = object: CountDownTimer(timeStart.toLong() * 1000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        tv_countdown.setText("" + String.format("%d:%d:%d",
                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))))
                    }
                    override fun onFinish() {
                        cancel()
                        tv_countdown.setText("HH:MM:SS")

                        // Break Button
                        workView.btn_startBreakNow.visibility = View.VISIBLE
                        workView.btn_startBreakNow.setOnClickListener{
                            breakTimer()
                        }
                        workView.tv_countdown.visibility = View.INVISIBLE
                        workView.tv_focustext.visibility = View.INVISIBLE
                        workView.btn_stop.visibility = View.INVISIBLE
                        workView.btn_pause.visibility = View.INVISIBLE
                        workView.btn_breakfrag.visibility = View.INVISIBLE
                    }
                }.start()
                workView.btn_start.visibility = View.INVISIBLE // Work Button
                workView.tv_countdown.visibility = View.VISIBLE
                workView.tv_focustext.visibility = View.VISIBLE
                workView.btn_stop.visibility = View.VISIBLE
                workView.btn_pause.visibility = View.VISIBLE
            }
        }

        // Stop Button
        workView.btn_stop.setOnClickListener{
            timer.cancel()
            workView.btn_start.visibility = View.VISIBLE
            workView.btn_stop.visibility = View.INVISIBLE
            workView.tv_countdown.visibility = View.INVISIBLE
            workView.tv_focustext.visibility = View.INVISIBLE
            workView.btn_pause.visibility = View.INVISIBLE
        }

        // Take a Break Button
        workView.btn_breakfrag.setOnClickListener{
            workView.btn_start.visibility = View.INVISIBLE
            btn_breakfrag.visibility = View.INVISIBLE

            workView.btn_startBreakNow.visibility = View.VISIBLE
            workView.btn_startBreakNow.setOnClickListener{
                breakTimer()
            }
        }

        return workView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)  {
        super.onViewCreated(view, savedInstanceState)

        //Add Todo Button
        btn_add.setOnClickListener{
                view ->
            Log.d("btn_add", "Selected")
            saveRecord()
        }
        // toolbar title
        activity!!.title = ""
    }

    // Navigate to Break Fragment
    fun breakFragment() {

        val testInput1 = arguments?.getString("break")
        val testDemo1 = testInput1.toString()
        val testInput2 = arguments?.getString("longBreak")
        val testDemo2 = testInput2.toString()

        communicator.passBreakData(testDemo1, testDemo2)
    }

    // Short Break Timer
    fun breakTimer() {

        var timeInput = arguments?.getString("break")
        var timeStart = timeInput.toString()
        tv_focustext.setText("Break")

        if (timeInput.isNullOrBlank()) {
            Toast.makeText(context, "Set a break time first", Toast.LENGTH_SHORT).show()
        } else {

            // CountDownTimer
            timer = object: CountDownTimer(timeStart.toLong() * 1000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    tv_countdown.setText("" + String.format("%d:%d:%d",
                            TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))))
                }
                override fun onFinish() {
                    cancel()
                    tv_countdown.setText("HH:MM:SS")

                    btn_startLongBreakNow.visibility = View.VISIBLE
                    btn_startLongBreakNow.setOnClickListener{
                        longBreakTimer()
                    }
                    tv_countdown.visibility = View.INVISIBLE
                    tv_focustext.visibility = View.INVISIBLE
                    btn_stop.visibility = View.INVISIBLE
                    btn_pause.visibility = View.INVISIBLE
                }
            }.start()
            btn_startBreakNow.visibility = View.INVISIBLE //shortBreak button
            tv_countdown.visibility = View.VISIBLE
            tv_focustext.visibility = View.VISIBLE
            btn_stop.visibility = View.VISIBLE
            btn_pause.visibility = View.VISIBLE
        }
    }

    // Long Break Timer
    fun longBreakTimer() {

        var timeInput = arguments?.getString("longBreak")
        var timeStart = timeInput.toString()
        tv_focustext.setText("Long Break")

        if (timeInput.isNullOrBlank()) {
            Toast.makeText(context, "Set a long break time first", Toast.LENGTH_SHORT).show()
        } else {

            // CountDownTimer
            timer = object: CountDownTimer(timeStart.toLong() * 1000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    tv_countdown.setText("" + String.format("%d:%d:%d",
                            TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))))
                }
                override fun onFinish() {
                    cancel()
                    tv_countdown.setText("HH:MM:SS")

                    btn_start.visibility = View.VISIBLE

                    tv_countdown.visibility = View.INVISIBLE
                    tv_focustext.visibility = View.INVISIBLE
                    btn_stop.visibility = View.INVISIBLE
                    btn_pause.visibility = View.INVISIBLE
                    tv_focustext.setText("Focus")
                    btn_breakfrag.visibility = View.VISIBLE
                }
            }.start()
            btn_startLongBreakNow.visibility = View.INVISIBLE //longBreak button
            tv_countdown.visibility = View.VISIBLE
            tv_focustext.visibility = View.VISIBLE
            btn_stop.visibility = View.VISIBLE
            btn_pause.visibility = View.VISIBLE
        }
    }

    fun saveRecord(){
        val title = et_task.text.toString()
        val isChecked = "false"
        val databaseHandler: DatabaseHandler = DatabaseHandler(context!!)
        if(title.trim()!=""){
            val status = databaseHandler.addTodo(TodoModelClass(title, isChecked))
            if(status > -1){
                Toast.makeText(activity?.applicationContext,"New task added", Toast.LENGTH_LONG).show()
                et_task.text.clear()
            }
        }else{
            Toast.makeText(activity?.applicationContext,"You cannot enter a blank task", Toast.LENGTH_LONG).show()
        }

        viewRecord()

    }
    //method for read records from database in ListView
    fun viewRecord(){
        //creating the instance of DatabaseHandler class
        val databaseHandler: DatabaseHandler= DatabaseHandler(context!!)
        //calling the viewEmployee method of DatabaseHandler class to read the records
        val emp: List<TodoModelClass> = databaseHandler.viewTasks()
        val taskArrayId = Array<String>(emp.size){"0"}
        val taskArrayTitle = Array<String>(emp.size){"null"}
        val taskArrayisChecked = Array<String>(emp.size){"null"}
        var index = 0
        for(e in emp){
            taskArrayTitle[index] = e.title
            taskArrayisChecked[index] = e.isChecked
            index++
        }
        //creating custom ArrayAdapter
        val myListAdapter = RVTodoAdapter(activity!!,taskArrayId,taskArrayTitle,taskArrayisChecked)
        rv_todo_list.adapter = myListAdapter
        rv_todo_list.layoutManager = LinearLayoutManager(context!!)
    }



}
