package ph.apper.android.pagatpatan.pomodowo

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_work.*
import kotlinx.android.synthetic.main.fragment_work.view.*
import java.util.concurrent.TimeUnit


class WorkFragment : Fragment() {

    private lateinit var communicator: Communicator

    companion object {
        private lateinit var timer: CountDownTimer
        fun newInstance() = WorkFragment() // for menu
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val workView = inflater.inflate(R.layout.fragment_work, container, false)

        communicator = activity as Communicator

        // Start Button
        workView.btn_stop.visibility = View.INVISIBLE
        workView.tv_countdown.visibility = View.INVISIBLE
        workView.tv_focustext.visibility = View.INVISIBLE
        workView.btn_start.setOnClickListener {

            // Communicator
            val timeInput = arguments?.getString("focus")
            val timeStart = timeInput.toString()

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
                    breakFragment() // nav to break fragment

                }
            }.start()
            workView.btn_start.visibility = View.INVISIBLE
            workView.tv_countdown.visibility = View.VISIBLE
            workView.tv_focustext.visibility = View.VISIBLE
            workView.btn_stop.visibility = View.VISIBLE
        }

        // Stop Button
        workView.btn_stop.setOnClickListener{
            timer.cancel()
            workView.btn_start.visibility = View.VISIBLE
            workView.btn_stop.visibility = View.INVISIBLE
            workView.tv_countdown.visibility = View.INVISIBLE
            workView.tv_focustext.visibility = View.INVISIBLE
        }

        workView.btn_breakfrag.setOnClickListener{
            breakFragment()
        }

        return workView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // toolbar title
        activity!!.title = ""
    }


    // Navigate to Settings Fragment
//    fun settingsFragment() {
//        val fragment = SettingsFragment()
//        val fragmentManager = activity!!.supportFragmentManager
//
//        val fragmentTransaction = fragmentManager.beginTransaction()
//        fragmentTransaction.replace(R.id.container, fragment)
//        fragmentTransaction.addToBackStack(null)
//        fragmentTransaction.commit()
//    }

    // Navigate to Break Fragment
    fun breakFragment() {

        val testInput1 = arguments?.getString("break")
        val testDemo1 = testInput1.toString()
        val testInput2 = arguments?.getString("longBreak")
        val testDemo2 = testInput2.toString()

        communicator.passBreakData(testDemo1, testDemo2)

    }


}
