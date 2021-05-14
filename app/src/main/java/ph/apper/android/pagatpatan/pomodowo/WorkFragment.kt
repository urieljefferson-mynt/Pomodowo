package ph.apper.android.pagatpatan.pomodowo

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistjeff.model.TodoModelClass
import kotlinx.android.synthetic.main.appbar.*
import kotlinx.android.synthetic.main.appbar.view.*
import kotlinx.android.synthetic.main.fragment_work.*
import kotlinx.android.synthetic.main.fragment_work.view.*
import ph.apper.android.pagatpatan.pomodowo.adapters.RVTodoAdapter
import ph.apper.android.pagatpatan.pomodowo.dao.DatabaseHandler
import java.util.concurrent.TimeUnit


class WorkFragment : Fragment(){


    private lateinit var communicator: Communicator

    companion object {
        private lateinit var timer: CountDownTimer
        private lateinit var focus: String
        private lateinit var shortBreak: String
        private lateinit var longBreak: String
        private lateinit var checkedTasks: String
        private lateinit var timeInput: String
        private lateinit var myListAdapter: RVTodoAdapter
        private lateinit var todoList: MutableList<TodoModelClass>
        private lateinit var databaseHandler: DatabaseHandler
        private var currentTime : Long = 0
        var LONG_BREAK_ELIGIBLE = false
//        var SESSION_STARTED = false
        var SESSION_TYPE: SessionType =  SessionType.FOCUS
        var breakPoints: MutableLiveData<Int> = MutableLiveData(0)
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment


        val view = inflater.inflate(R.layout.fragment_work, container, false)

        //Load persisted user settings data
        val sharedPreferences: SharedPreferences = this.activity!!.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val persistentCredits: SharedPreferences? = this.activity?.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
//        val longBreakCredits: Int? = sharedPreferences.getInt("longBreakCredits", 0)

        view.myToolBar.visibility = View.VISIBLE


        //Assigning saved user settings from shared preferences
        focus = sharedPreferences.getString("focus", "25").toString()
        shortBreak= sharedPreferences.getString("break", "5").toString()
        longBreak = sharedPreferences?.getString("longBreak", "15").toString()
        checkedTasks = sharedPreferences?.getString("checkedTasks", "4").toString()


        //Checking if there is a stored break (Doro Credits) from last user session
        val breakCredits: Int? = persistentCredits?.getInt("breakPoints", 0)
        if(breakCredits != 0){
            breakPoints.value = breakCredits
        }


        //Listen for data changes of the value of breakPoints, then display new value in tv_credits
        breakPoints.observe(this, Observer {
                newValue ->
                tv_credits.text = "Pomo Points: $newValue"
                val editor: SharedPreferences.Editor? = persistentCredits?.edit()
                editor?.apply {
                    putInt("breakPoints", newValue)
                }?.apply()
        })


        // Action Bar Buttons
        view.ic_arrow_back.visibility = View.INVISIBLE
        view.ic_menu.setOnClickListener{
            settingsFragment(savedInstanceState)
        }



        // Stop Button
        view.btn_stop.setOnClickListener{
            myToolBar.visibility = View.VISIBLE
            progress_countdown.progress = 0

            timer.cancel()
            view.btn_stop.visibility = View.INVISIBLE
            view.tv_countdown.visibility = View.INVISIBLE
            view.tv_workText.visibility = View.INVISIBLE
            view.btn_pause.visibility = View.INVISIBLE
            view.btn_startPause.visibility = View.INVISIBLE
            view.btn_start.visibility = View.VISIBLE

            if(SESSION_TYPE == SessionType.LONG_BREAK){
                breakPoints.value = breakPoints.value?.minus((checkedTasks?.toInt()?: 0))
            }
            workTimer()

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
                if(focus == "null") {
                    timeInput = arguments?.getString("focus").toString()
                }else{
                    timeInput = focus
                }
            var timeStart = timeInput.toString()
            // CountDownTimer
            timer = object : CountDownTimer(currentTime*1, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    currentTime = millisUntilFinished
                    tv_countdown.text = "" + String.format("%d:%d:%d",
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)))
                        progress_countdown.progress = (timeStart.toLong()*1000*60 - millisUntilFinished).toInt()

                }

                override fun onFinish() {
                    progress_countdown.progress = 0
                    longBreakEligibilityToggle()
                    cancel()
                    tv_countdown.setText("HH:MM:SS")
                    myToolBar.visibility = View.VISIBLE

                    if(LONG_BREAK_ELIGIBLE) {
                        SESSION_TYPE = SessionType.LONG_BREAK
                        view.btn_startLongBreakNow.setOnClickListener {
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
                        SESSION_TYPE = SessionType.BREAK
                        view.btn_startBreakNow.setOnClickListener {
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
        return view
    }



    // Add To do List
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)  {
        super.onViewCreated(view, savedInstanceState)

        //Add Todo Button
        btn_add.setOnClickListener{ view ->
            Log.d("btn_add", "Selected")
            saveRecord()
        }
        // toolbar title
        activity!!.title = ""



        //Initialize todo list and focus timer
        viewRecord()
        focusColor()
        workTimer()
    }


    // Navigate to Settings Fragment
    fun settingsFragment(savedInstanceState: Bundle?) {

        if (savedInstanceState == null) {
            val settingsfragment = SettingsFragment()
            val workFragment = activity!!.supportFragmentManager.findFragmentByTag("Work")
            val fragmentManager = activity!!.supportFragmentManager

            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.add(R.id.container, settingsfragment, "Settings").addToBackStack(null)
            fragmentTransaction.commit()

        }
    }


    fun workTimer(){
        myToolBar.visibility = View.VISIBLE
        longBreakEligibilityToggle()
        focusColor()

        view?.btn_stop?.visibility = View.INVISIBLE
        view?.btn_pause?.visibility = View.INVISIBLE
        view?.tv_countdown?.visibility = View.INVISIBLE
        view?.tv_workText?.visibility = View.INVISIBLE
        view?.btn_startBreakNow?.visibility = View.INVISIBLE
        view?.btn_startLongBreakNow?.visibility = View.INVISIBLE
        view?.btn_startPause?.visibility = View.INVISIBLE
        btn_start.visibility = View.VISIBLE


        tv_workText.setText("Focus")
        // Start Button
        view?.btn_start?.setOnClickListener {
            if(focus == "null") {
                timeInput = arguments?.getString("focus").toString()
            }else{
                timeInput = focus
            }



            if (timeInput == "null") {
                Toast.makeText(context, "Set a focus time first", Toast.LENGTH_SHORT).show()
            } else {
                myToolBar.visibility = View.INVISIBLE
                var timeStart = timeInput.toString()
                progress_countdown.max = timeStart.toInt() * 1000 * 60


                // CountDownTimer
                timer = object: CountDownTimer(timeStart.toLong() * 1000 * 60, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        currentTime = millisUntilFinished
                        tv_countdown.text = "" + String.format("%d:%d:%d",
                            TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)))

                        progress_countdown.progress = (timeStart.toLong()*1000*60 - millisUntilFinished).toInt()
                    }

                    override fun onFinish() {
                        myToolBar.visibility = View.VISIBLE
                        cancel()
                        progress_countdown.progress = 0
                        view?.tv_countdown?.setText("HH:MM:SS")
                        longBreakEligibilityToggle()

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
                view?.btn_start?.visibility = View.INVISIBLE // Work Button
                view?.tv_countdown?.visibility = View.VISIBLE
                view?.tv_workText?.visibility = View.VISIBLE
                view?.btn_stop?.visibility = View.VISIBLE
                view?.btn_pause?.visibility = View.VISIBLE
            }
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

            if(focus == "null") {
                timeInput = arguments?.getString("focus").toString()
            }else{
                timeInput = focus
            }
            var timeStart = timeInput.toString()

            // CountDownTimer
            timer = object : CountDownTimer(currentTime*1, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    currentTime = millisUntilFinished
                    tv_countdown.text = "" + String.format("%d:%d:%d",
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)))
                        progress_countdown.progress = (timeStart.toLong()*1000*60 - currentTime).toInt()

                }

                override fun onFinish() {
                    progress_countdown.progress = 0
                    cancel()
                    tv_countdown.setText("HH:MM:SS")

                    if(LONG_BREAK_ELIGIBLE) {
                        view?.btn_startLongBreakNow?.setOnClickListener {
                            activity?.actionBar?.hide()
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
                            activity?.actionBar?.hide()
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
        myToolBar.visibility = View.VISIBLE
        SESSION_TYPE = SessionType.BREAK
        breakColor()
        if(shortBreak == "null") {
            timeInput = arguments?.getString("focus").toString()
        }else{
            timeInput = shortBreak
        }
        var timeStart = timeInput.toString()
        tv_workText.setText("Break")

        if (timeInput == "null") {
            Toast.makeText(context, "Set a break time first", Toast.LENGTH_SHORT).show()
        } else {
            progress_countdown.max = timeStart.toInt() * 1000 * 60
            myToolBar.visibility = View.INVISIBLE
            // CountDownTimer
            timer = object: CountDownTimer(timeStart.toLong() * 1000 * 60, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    currentTime = millisUntilFinished
                    tv_countdown.text = "" + String.format("%d:%d:%d",
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)))
                        progress_countdown.progress = (timeStart.toLong()*1000*60 - millisUntilFinished).toInt()

                }
                override fun onFinish() {
                    progress_countdown.progress = 0
                    cancel()
                    tv_countdown.setText("HH:MM:SS")
                    myToolBar.visibility = View.VISIBLE

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
            if(shortBreak == "null") {
                timeInput = arguments?.getString("break").toString()
            }else{
                timeInput = shortBreak
            }
            var timeStart = timeInput.toString()
            // CountDownTimer
            timer = object : CountDownTimer(currentTime*1, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    currentTime = millisUntilFinished
                    tv_countdown.text = "" + String.format("%d:%d:%d",
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)))
                        progress_countdown.progress = (timeStart.toLong()*1000*60 - millisUntilFinished).toInt()

                }

                override fun onFinish() {
                    progress_countdown.progress = 0
                    cancel()
                    tv_countdown.setText("HH:MM:SS")

                    btn_start.visibility = View.VISIBLE

                    tv_countdown.visibility = View.INVISIBLE
                    tv_workText.visibility = View.INVISIBLE
                    btn_stop.visibility = View.INVISIBLE
                    btn_pause.visibility = View.INVISIBLE
                    tv_workText.setText("Focus")

                    // Change color
                    focusColor()
                    workTimer()

                }
            }.start()
        }




    }

    // Long Break Timer
    fun longBreakTimer() {
        myToolBar.visibility = View.VISIBLE
        SESSION_TYPE = SessionType.LONG_BREAK
        longBreakColor()

        if(longBreak == "null") {
            timeInput = arguments?.getString("longBreak").toString()
        }else{
            timeInput = longBreak
        }
        var timeStart = timeInput.toString()
        tv_workText.setText("Long Break")

        if (timeInput == "null") {
            Toast.makeText(context, "Set a long break time first", Toast.LENGTH_SHORT).show()
        } else {
            progress_countdown.max = timeStart.toInt() * 1000 * 60
            myToolBar.visibility = View.INVISIBLE
            // CountDownTimer
            timer = object: CountDownTimer(timeStart.toLong() * 1000 * 60, 1000) {

                override fun onTick(millisUntilFinished: Long) {
                    currentTime = millisUntilFinished
                    tv_countdown.text = "" + String.format("%d:%d:%d",
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)))
                        progress_countdown.progress = (timeStart.toLong()*1000*60 - millisUntilFinished).toInt()

                }
                override fun onFinish() {
                    progress_countdown.progress = 0
                    myToolBar.visibility = View.VISIBLE
                    cancel()
                    tv_countdown.setText("HH:MM:SS")

                    Log.d("TASK REQ: ", checkedTasks.toString())

//                  Decrease the number of long break elibility credits
                    breakPoints.value = breakPoints.value?.minus((checkedTasks?.toInt()?:0))
                    longBreakEligibilityToggle()
                    Log.d("LONGBREAKDONETASKS", FinishedTasks.LONG_BREAK_CREDITS.toString())
                    Log.d("LB -ELIGIBILITY?", LONG_BREAK_ELIGIBLE.toString())

                    workTimer()


                    //TODO: Create function that will clear the checked off tasks in the recyclerView
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
                    tv_countdown.text = "" + String.format("%d:%d:%d",
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)))
                        progress_countdown.progress = (timeStart.toLong()*1000*60 - millisUntilFinished).toInt()


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

                    FinishedTasks.LONG_BREAK_CREDITS = FinishedTasks.LONG_BREAK_CREDITS - (checkedTasks?.toInt()?: 0)
                    breakPoints.value = breakPoints.value?.minus((checkedTasks?.toInt()?:0))

                    longBreakEligibilityToggle()
                    Log.d("LONGBREAKDONETASKS", FinishedTasks.LONG_BREAK_CREDITS.toString())
                    Log.d("LB -ELIGIBILITY?", LONG_BREAK_ELIGIBLE.toString())

                    focusColor()
                    workTimer()
                }
            }.start()
        }
    }

    // Change color of break view
    fun breakColor() {
        var focusFrag = frag_work
        focusFrag.setBackgroundResource(R.drawable.gradient_break_list)
        var animationDrawable: AnimationDrawable = focusFrag.background as AnimationDrawable
        animationDrawable.setEnterFadeDuration(10)
        animationDrawable.setExitFadeDuration(500)
        animationDrawable.start()
    }

    // Change color of long break view
    fun longBreakColor() {
        var focusFrag = frag_work
        focusFrag.setBackgroundResource(R.drawable.gradient_long_list)
        var animationDrawable: AnimationDrawable = focusFrag.background as AnimationDrawable
        animationDrawable.setEnterFadeDuration(10)
        animationDrawable.setExitFadeDuration(500)
        animationDrawable.start()
    }

    // Change color of focus view
    fun focusColor() {
        var focusFrag = frag_work
        focusFrag.setBackgroundResource(R.drawable.gradient_focus_list)
        var animationDrawable: AnimationDrawable = focusFrag.background as AnimationDrawable
        animationDrawable.setEnterFadeDuration(10)
        animationDrawable.setExitFadeDuration(500)
        animationDrawable.start()
    }

    //Changes Long Break Eligibility State based on number of Doro Points and the set required task
    //Toggles the state LONG_BREAK_ELIGIBLE, if true, timer proceeds to Long Break after Focus Session
    fun longBreakEligibilityToggle(){
        if(breakPoints.value!! >= checkedTasks.toInt()){
            Log.d("Toggle", FinishedTasks.LONG_BREAK_CREDITS.toString())
            Log.d("Toggle", FinishedTasks.REQUIRED_TASKS.toString())
            LONG_BREAK_ELIGIBLE = true
        }else{
            LONG_BREAK_ELIGIBLE = false
        }
        Log.d("Toggle", LONG_BREAK_ELIGIBLE.toString())
    }


    //DATABASE FUNCTIONS
    fun saveRecord(){
        val title = et_task.text.toString()
        val isChecked = false
        databaseHandler = DatabaseHandler(context!!)
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
        databaseHandler = DatabaseHandler(context!!)
        //calling the viewEmployee method of DatabaseHandler class to read the records
        todoList = databaseHandler.viewTasks() as MutableList<TodoModelClass>
        //creating custom ArrayAdapter
        myListAdapter = RVTodoAdapter(activity!!, todoList)
        rv_todo_list.adapter = myListAdapter
        rv_todo_list.layoutManager = LinearLayoutManager(context)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rv_todo_list)
    }

    fun deleteTodo(todo: TodoModelClass){
        todoList.remove(todo)
        databaseHandler = DatabaseHandler(context!!)
        databaseHandler.deleteTodo(todo)
    }




    //Swipe right to delete feature
    var itemTouchHelperCallback: ItemTouchHelper.SimpleCallback =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteTodo(myListAdapter.getTodoList()[viewHolder.adapterPosition])
                myListAdapter.notifyDataSetChanged()
            }
        }

}
