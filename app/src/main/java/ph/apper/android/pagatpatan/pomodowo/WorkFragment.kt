package ph.apper.android.pagatpatan.pomodowo

import android.graphics.Color
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
import kotlinx.android.synthetic.main.appbar.*
import kotlinx.android.synthetic.main.appbar.view.*
import kotlinx.android.synthetic.main.fragment_work.*
import kotlinx.android.synthetic.main.fragment_work.view.*
import ph.apper.android.pagatpatan.pomodowo.adapters.RVTodoAdapter
import java.util.concurrent.TimeUnit


class WorkFragment : Fragment(){
    var LONG_BREAK_ELIGIBLE = false
    var SESSION_STARTED = false
    var SESSION_TYPE: SessionType =  SessionType.FOCUS

    private lateinit var communicator: Communicator

    companion object {
        private lateinit var timer: CountDownTimer
        private var currentTime : Long = 0
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_work, container, false)
        FinishedTasks.REQUIRED_TASKS = arguments?.getString("checkedTasks")?.toInt() ?: 0

        // Action Bar Buttons
        view.ic_arrow_back.visibility = View.INVISIBLE
        view.ic_menu.setOnClickListener{
            settingsFragment()
        }

        // Communicator
        communicator = activity as Communicator

        view.btn_stop.visibility = View.INVISIBLE
        view.btn_pause.visibility = View.INVISIBLE
        view.tv_countdown.visibility = View.INVISIBLE
        view.tv_workText.visibility = View.INVISIBLE
        view.btn_startBreakNow.visibility = View.INVISIBLE
        view.btn_startLongBreakNow.visibility = View.INVISIBLE
        view.btn_startPause.visibility = View.INVISIBLE

        // Start Button Init
        view.btn_start.setOnClickListener {

            var timeInput = arguments?.getString("focus")

            if (timeInput.isNullOrBlank()) {
                Toast.makeText(context, "Set a focus time first", Toast.LENGTH_SHORT).show()
            } else {

                var timeStart = timeInput.toString()

                // CountDownTimer
                timer = object: CountDownTimer(timeStart.toLong() * 1000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        currentTime = millisUntilFinished
                        tv_countdown.setText("" + String.format("%d:%d:%d",
                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))))
                    }
                    override fun onFinish() {
                        SESSION_TYPE = SessionType.BREAK
                        cancel()
                        view.tv_countdown.setText("HH:MM:SS")
                        longBreakEligibilityToggle()

                        if(LONG_BREAK_ELIGIBLE) {
                            view.btn_startLongBreakNow.setOnClickListener {
                                SESSION_TYPE = SessionType.LONG_BREAK
                                longBreakTimer()
                            }

                            view.btn_startLongBreakNow.visibility = View.VISIBLE
                            view.tv_countdown.visibility = View.INVISIBLE
                            view.tv_workText.visibility = View.INVISIBLE
                            view.btn_stop.visibility = View.INVISIBLE
                            view.btn_pause.visibility = View.INVISIBLE
//                            view.btn_break.visibility = View.INVISIBLE

                            // Change color
                            longBreakColor()
                        }else{
                            //SHORT BREAK
                            view.btn_startBreakNow.setOnClickListener {
                                SESSION_TYPE = SessionType.BREAK
                                view.btn_startBreakNow.visibility = View.VISIBLE
                                breakTimer()

                            }
                            view.btn_startBreakNow.visibility = View.VISIBLE
                            view.tv_countdown.visibility = View.INVISIBLE
                            view.tv_workText.visibility = View.INVISIBLE
                            view.btn_stop.visibility = View.INVISIBLE
                            view.btn_pause.visibility = View.INVISIBLE
//                            view.btn_break.visibility = View.INVISIBLE

                            // Change color
                            breakColor()
                        }

                    }
                }.start()
                view.btn_start.visibility = View.INVISIBLE // Work Button
                view.tv_countdown.visibility = View.VISIBLE
                view.tv_workText.visibility = View.VISIBLE
                view.btn_stop.visibility = View.VISIBLE
                view.btn_pause.visibility = View.VISIBLE
            }
        }

        // Stop Button
        view.btn_stop.setOnClickListener{
            timer.cancel()
            view.btn_start.visibility = View.VISIBLE
            view.btn_stop.visibility = View.INVISIBLE
            view.tv_countdown.visibility = View.INVISIBLE
            view.tv_workText.visibility = View.INVISIBLE
            view.btn_pause.visibility = View.INVISIBLE
        }

        // Pause Button
        view.btn_pause.setOnClickListener{
            timer.cancel()
            view.btn_pause.visibility = View.INVISIBLE
            view.btn_startPause.visibility = View.VISIBLE

        }

        // Focus Start-Pause Button
        view.btn_startPause.setOnClickListener {

            view.btn_pause.visibility = View.VISIBLE
            view.btn_startPause.visibility = View.INVISIBLE

            // CountDownTimer
            timer = object : CountDownTimer(currentTime*1, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    currentTime = millisUntilFinished
                    tv_countdown.setText("" + String.format("%d:%d:%d",
                            TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))))
                }

                override fun onFinish() {
                    longBreakEligibilityToggle()
                    cancel()
                    tv_countdown.setText("HH:MM:SS")

                    if(LONG_BREAK_ELIGIBLE) {
                        view.btn_startLongBreakNow.setOnClickListener {
                            SESSION_TYPE = SessionType.LONG_BREAK
                            longBreakTimer()
                        }

                        view.btn_startLongBreakNow.visibility = View.VISIBLE
                        view.tv_countdown.visibility = View.INVISIBLE
                        view.tv_workText.visibility = View.INVISIBLE
                        view.btn_stop.visibility = View.INVISIBLE
                        view.btn_pause.visibility = View.INVISIBLE
//                            view.btn_break.visibility = View.INVISIBLE

                        // Change color
                        longBreakColor()
                    }else{
                        //SHORT BREAK
                        view.btn_startBreakNow.setOnClickListener {
                            SESSION_TYPE = SessionType.BREAK
                            view.btn_startBreakNow.visibility = View.VISIBLE
                            breakTimer()

                        }
                        view.btn_startBreakNow.visibility = View.VISIBLE
                        view.tv_countdown.visibility = View.INVISIBLE
                        view.tv_workText.visibility = View.INVISIBLE
                        view.btn_stop.visibility = View.INVISIBLE
                        view.btn_pause.visibility = View.INVISIBLE
//                            view.btn_break.visibility = View.INVISIBLE

                        // Change color
                        breakColor()
                    }
                }
            }.start()
        }

        // Take a Break Button
//        view.btn_break.setOnClickListener{
//            view.btn_start.visibility = View.INVISIBLE
//            btn_break.visibility = View.INVISIBLE
//            breakColor()
//
//            view.btn_startBreakNow.visibility = View.VISIBLE
//            view.btn_startBreakNow.setOnClickListener{
//                breakTimer()
//            }
//        }

        return view
    }

    // Add To do List
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)  {
        super.onViewCreated(view, savedInstanceState)
        //Display to do list upon start up
        viewRecord()
        //Add Todo Button
        btn_add.setOnClickListener{ view ->
            Log.d("btn_add", "Selected")
            saveRecord()
        }
        // toolbar title
        activity!!.title = ""
    }

    // Navigate to Settings Fragment
    fun settingsFragment() {
        val fragment = SettingsFragment()
        val fragmentManager = activity!!.supportFragmentManager

        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    fun workTimer(){
        longBreakEligibilityToggle()
        btn_stop.visibility = View.INVISIBLE
        btn_pause.visibility = View.INVISIBLE
        tv_countdown.visibility = View.INVISIBLE
        tv_workText.visibility = View.INVISIBLE

        btn_start.visibility = View.VISIBLE
        btn_startBreakNow.visibility = View.INVISIBLE
        btn_startLongBreakNow.visibility = View.INVISIBLE

        // Start Button
        btn_start.setOnClickListener {
            SESSION_STARTED = true
            var timeInput = arguments?.getString("focus")
//            Log.d("TASK REQUIREMENT", checkedTaskRequirement.toString())

            if (timeInput.isNullOrBlank()) {
                Toast.makeText(context, "Set a focus time first", Toast.LENGTH_SHORT).show()
            }
            else if (FinishedTasks.REQUIRED_TASKS == 0) {
                Toast.makeText(context, "Set completed tasks requirements first", Toast.LENGTH_SHORT).show()
            }
            else {

                var timeStart = timeInput.toString()

                // CountDownTimer
                timer = object: CountDownTimer(timeStart.toLong() * 1000, 1000) {

                    override fun onTick(millisUntilFinished: Long) {
                        currentTime = millisUntilFinished
                        tv_countdown.setText("" + String.format("%d:%d:%d",
                            TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))))
                    }
                    override fun onFinish() {
                        SESSION_TYPE = SessionType.BREAK
                        cancel()
                        tv_countdown.setText("HH:MM:SS")
                        longBreakEligibilityToggle()
                        // Break Button
                        //LONG BREAK
                        if(LONG_BREAK_ELIGIBLE) {
                            Log.d("CheckedTasks", FinishedTasks.CHECKED_OFF_TASKS.toString())
                            Log.d("RequiredTasks", FinishedTasks.REQUIRED_TASKS.toString())
                            Log.d("WORK TIMER", "LONG BREAK ELIGIBLE!")
                            longBreakColor()
                            btn_startLongBreakNow.setOnClickListener {
                                SESSION_TYPE = SessionType.LONG_BREAK
                                longBreakTimer()
                            }

                            btn_startLongBreakNow.visibility = View.VISIBLE
                            tv_countdown.visibility = View.INVISIBLE
                            tv_workText.visibility = View.INVISIBLE
                            btn_stop.visibility = View.INVISIBLE
                            btn_pause.visibility = View.INVISIBLE
//                            btn_break.visibility = View.INVISIBLE

                            // Change color

                        }else {
                            //SHORT BREAK
                            Log.d("CheckedTasks", FinishedTasks.CHECKED_OFF_TASKS.toString())
                            Log.d("RequiredTasks", FinishedTasks.REQUIRED_TASKS.toString())
                            Log.d("WORK TIMER", "NOT LONG BREAK ELIGIBLE!")
                            breakColor()
                            btn_startBreakNow.setOnClickListener {
                                SESSION_TYPE = SessionType.BREAK
                                btn_startBreakNow.visibility = View.VISIBLE
                                breakTimer()

                            }
                            btn_startBreakNow.visibility = View.VISIBLE
                            tv_countdown.visibility = View.INVISIBLE
                            tv_workText.visibility = View.INVISIBLE
                            btn_stop.visibility = View.INVISIBLE
                            btn_pause.visibility = View.INVISIBLE
//                            btn_break.visibility = View.INVISIBLE

                            // Change color

                        }

                    }
                }.start()
                btn_start.visibility = View.INVISIBLE // Work Button
                tv_countdown.visibility = View.VISIBLE
                tv_workText.visibility = View.VISIBLE
                btn_stop.visibility = View.VISIBLE
                btn_pause.visibility = View.VISIBLE
            }
        }

        // Stop Button
        btn_stop.setOnClickListener{
            timer.cancel()
            btn_start.visibility = View.VISIBLE
            btn_stop.visibility = View.INVISIBLE
            tv_countdown.visibility = View.INVISIBLE
            tv_workText.visibility = View.INVISIBLE
            btn_pause.visibility = View.INVISIBLE
        }

        view?.btn_pause?.setOnClickListener{
            timer.cancel()
            view?.btn_pause?.visibility = View.INVISIBLE
            view?.btn_startPause?.visibility = View.VISIBLE

        }

        // Focus Start-Pause Button
        view?.btn_startPause?.setOnClickListener {
            longBreakEligibilityToggle()
            view?.btn_pause?.visibility = View.VISIBLE
            view?.btn_startPause?.visibility = View.INVISIBLE

            // CountDownTimer
            timer = object : CountDownTimer(currentTime*1, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    currentTime = millisUntilFinished
                    tv_countdown.setText("" + String.format("%d:%d:%d",
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))))
                }

                override fun onFinish() {
                    cancel()
                    tv_countdown.setText("HH:MM:SS")

                    if(LONG_BREAK_ELIGIBLE) {
                        view?.btn_startLongBreakNow?.setOnClickListener {
                            SESSION_TYPE = SessionType.LONG_BREAK
                            longBreakTimer()
                        }

                        view?.btn_startLongBreakNow?.visibility = View.VISIBLE
                        view?.tv_countdown?.visibility = View.INVISIBLE
                        view?.tv_workText?.visibility = View.INVISIBLE
                        view?.btn_stop?.visibility = View.INVISIBLE
                        view?.btn_pause?.visibility = View.INVISIBLE
//                            view.btn_break.visibility = View.INVISIBLE

                        // Change color
                        longBreakColor()
                    }else{
                        //SHORT BREAK
                        view?.btn_startBreakNow?.setOnClickListener {
                            SESSION_TYPE = SessionType.BREAK
                            view?.btn_startBreakNow?.visibility = View.VISIBLE
                            breakTimer()

                        }
                        view?.btn_startBreakNow?.visibility = View.VISIBLE
                        view?.tv_countdown?.visibility = View.INVISIBLE
                        view?.tv_workText?.visibility = View.INVISIBLE
                        view?.btn_stop?.visibility = View.INVISIBLE
                        view?.btn_pause?.visibility = View.INVISIBLE
//                            view.btn_break.visibility = View.INVISIBLE

                        // Change color
                        breakColor()
                    }
                }
            }.start()
        }


    }

    // Short Break Timer
    fun breakTimer() {

        var timeInput = arguments?.getString("break")
        var timeStart = timeInput.toString()
        tv_workText.setText("Break")

        if (timeInput.isNullOrBlank()) {
            Toast.makeText(context, "Set a break time first", Toast.LENGTH_SHORT).show()
        } else {

            // CountDownTimer
            timer = object: CountDownTimer(timeStart.toLong() * 1000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    currentTime = millisUntilFinished
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
                    tv_workText.visibility = View.INVISIBLE
                    btn_stop.visibility = View.INVISIBLE
                    btn_pause.visibility = View.INVISIBLE
                    tv_workText.setText("Focus")
//                    btn_breakfrag.visibility = View.VISIBLE

                    // Change color
                    focusColor()
                    workTimer()
                }
            }.start()
            btn_startBreakNow.visibility = View.INVISIBLE //shortBreak button
            tv_countdown.visibility = View.VISIBLE
            tv_workText.visibility = View.VISIBLE
            btn_stop.visibility = View.VISIBLE
            btn_pause.visibility = View.VISIBLE
            btn_startPause.setColorFilter(Color.parseColor("#99d5ca"))
        }

        // Break Start-Pause Button
        btn_startPause.setOnClickListener {

            btn_pause.visibility = View.VISIBLE
            btn_startPause.visibility = View.INVISIBLE

            // CountDownTimer
            timer = object : CountDownTimer(currentTime*1, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    currentTime = millisUntilFinished
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
                    tv_workText.visibility = View.INVISIBLE
                    btn_stop.visibility = View.INVISIBLE
                    btn_pause.visibility = View.INVISIBLE
                    tv_workText.setText("Focus")
//                    btn_breakfrag.visibility = View.VISIBLE

                    // Change color
                    focusColor()
                    workTimer()

                }
            }.start()
        }

    }

    // Long Break Timer
    fun longBreakTimer() {
        var checkedTaskRequirement = arguments?.getString("checkedTasks")
        var timeInput = arguments?.getString("longBreak")
        var timeStart = timeInput.toString()
        tv_workText.setText("Long Break")

        if (timeInput.isNullOrBlank()) {
            Toast.makeText(context, "Set a long break time first", Toast.LENGTH_SHORT).show()
        } else {

            // CountDownTimer
            timer = object: CountDownTimer(timeStart.toLong() * 1000, 1000) {

                override fun onTick(millisUntilFinished: Long) {
                    currentTime = millisUntilFinished
                    tv_countdown.setText("" + String.format("%d:%d:%d",
                            TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))))
                }
                override fun onFinish() {
                    cancel()
                    tv_countdown.setText("HH:MM:SS")

                    Log.d("TASK REQ: ", checkedTaskRequirement.toString())
//                   Reset number of finished tasks back to zero
                    FinishedTasks.CHECKED_OFF_TASKS = FinishedTasks.CHECKED_OFF_TASKS - (checkedTaskRequirement?.toInt()?: 0)
                    longBreakEligibilityToggle()
                    Log.d("LONGBREAKDONETASKS", FinishedTasks.CHECKED_OFF_TASKS.toString())
                    Log.d("LB -ELIGIBILITY?", LONG_BREAK_ELIGIBLE.toString())
                    workTimer()


                    // Change color
                    focusColor()

                    //TODO: Create function that will clear the checked off tasks in the recyclerView

                    workTimer()
                }
            }.start()
            btn_startLongBreakNow.visibility = View.INVISIBLE //longBreak button
            tv_countdown.visibility = View.VISIBLE
            tv_workText.visibility = View.VISIBLE
            btn_stop.visibility = View.VISIBLE
            btn_pause.visibility = View.VISIBLE
            btn_startPause.setColorFilter(Color.parseColor("#b391b5"))
        }

        // Long Break Start-Pause Button
        btn_startPause.setOnClickListener {

            btn_pause.visibility = View.VISIBLE
            btn_startPause.visibility = View.INVISIBLE

            // CountDownTimer
            timer = object : CountDownTimer(currentTime*1, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    currentTime = millisUntilFinished
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
                    tv_workText.visibility = View.INVISIBLE
                    btn_stop.visibility = View.INVISIBLE
                    btn_pause.visibility = View.INVISIBLE
                    tv_workText.setText("Focus")

                    FinishedTasks.CHECKED_OFF_TASKS = FinishedTasks.CHECKED_OFF_TASKS - (checkedTaskRequirement?.toInt()?: 0)
                    longBreakEligibilityToggle()
                    Log.d("LONGBREAKDONETASKS", FinishedTasks.CHECKED_OFF_TASKS.toString())
                    Log.d("LB -ELIGIBILITY?", LONG_BREAK_ELIGIBLE.toString())

                    focusColor()
                    workTimer()
                }
            }.start()
        }
    }

    // Change color of break view
    fun breakColor() {
        myToolBar.setBackgroundColor(Color.parseColor("#99d5ca"))
        btn_startBreakNow.setColorFilter(Color.parseColor("#99d5ca"))
        btn_stop.setColorFilter(Color.parseColor("#99d5ca"))
        btn_pause.setColorFilter(Color.parseColor("#99d5ca"))
        btn_add.setColorFilter(Color.parseColor("#99d5ca"))
    }

    // Change color of long break view
    fun longBreakColor() {
        myToolBar.setBackgroundColor(Color.parseColor("#b391b5"))
        btn_startLongBreakNow.setColorFilter(Color.parseColor("#b391b5"))
        btn_stop.setColorFilter(Color.parseColor("#b391b5"))
        btn_pause.setColorFilter(Color.parseColor("#b391b5"))
        btn_add.setColorFilter(Color.parseColor("#b391b5"))
    }

    // Change color of long break view
    fun focusColor() {
        myToolBar.setBackgroundColor(Color.parseColor("#ffbbb1"))
        btn_startBreakNow.setColorFilter(Color.parseColor("#ffbbb1"))
        btn_stop.setColorFilter(Color.parseColor("#ffbbb1"))
        btn_pause.setColorFilter(Color.parseColor("#ffbbb1"))
        btn_add.setColorFilter(Color.parseColor("#ffbbb1"))
        btn_startPause.setColorFilter(Color.parseColor("#ffbbb1"))
    }

    fun saveRecord(){
        val title = et_task.text.toString()
        val isChecked = false
        val databaseHandler: DatabaseHandler = DatabaseHandler(context!!)
        if(title.trim()!=""){
            val status = databaseHandler.addTodo(TodoModelClass(id = 0, title, isChecked))
            if(status > -1){
                Toast.makeText(activity?.applicationContext, "New task added", Toast.LENGTH_LONG).show()
                et_task.text.clear()
            }
        }else{
            Toast.makeText(activity?.applicationContext, "You cannot enter a blank task", Toast.LENGTH_LONG).show()
        }

        viewRecord()

    }
    //method for read records from database in ListView
    fun viewRecord(){
        //creating the instance of DatabaseHandler class
        val databaseHandler: DatabaseHandler= DatabaseHandler(context!!)
        //calling the viewEmployee method of DatabaseHandler class to read the records
        val emp: MutableList<TodoModelClass> = databaseHandler.viewTasks() as MutableList<TodoModelClass>
        //creating custom ArrayAdapter
        val myListAdapter = RVTodoAdapter(activity!!, emp)
        rv_todo_list.adapter = myListAdapter
        rv_todo_list.layoutManager = LinearLayoutManager(context)
    }

    //
    //Changes Long Break Eligibility State based on number of checked off task on todo list
    fun longBreakEligibilityToggle(){
        if(FinishedTasks.CHECKED_OFF_TASKS >= FinishedTasks.REQUIRED_TASKS){
            Log.d("Toggle", FinishedTasks.CHECKED_OFF_TASKS.toString())
            Log.d("Toggle", FinishedTasks.REQUIRED_TASKS.toString())
            LONG_BREAK_ELIGIBLE = true
        }else{
            LONG_BREAK_ELIGIBLE = false
        }
        Log.d("Toggle", LONG_BREAK_ELIGIBLE.toString())

    }

    // Buttons Visibility

}
