package ph.apper.android.pagatpatan.pomodowo

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_break.*
import kotlinx.android.synthetic.main.fragment_break.view.*
import kotlinx.android.synthetic.main.fragment_work.view.*
import java.util.concurrent.TimeUnit

class BreakFragment : Fragment() {

    private lateinit var communicator: Communicator

    companion object {
        private lateinit var breakTimer: CountDownTimer
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val breakView = inflater.inflate(R.layout.fragment_break, container, false)

        communicator = activity as Communicator

        // Start Button
        breakView.btn_stopBreak.visibility = View.INVISIBLE
        breakView.tv_breakCountdown.visibility = View.INVISIBLE
        breakView.tv_breaktext.visibility = View.INVISIBLE

        breakView.btn_startBreak.setOnClickListener{

            val timeBreak = arguments?.getString("break")
            val timeBreakStart = timeBreak.toString()

            // CountDownTimer
            breakTimer = object: CountDownTimer(timeBreakStart.toLong() * 1000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    tv_breakCountdown.setText("" + String.format("%d:%d:%d",
                            TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))))
                }
                override fun onFinish() {
                    cancel()
                    tv_breakCountdown.setText("HH:MM:SS")
                    longBreakFragment() // nav to long break fragment

//                    breakView.btn_stop.visibility = View.INVISIBLE
//                    breakView.btn_start.visibility = View.VISIBLE
//                    breakView.tv_countdown.visibility = View.INVISIBLE
//                    breakView.tv_focustext.visibility = View.INVISIBLE
                }
            }.start()
            breakView.btn_startBreak.visibility = View.INVISIBLE
            breakView.tv_breakCountdown.visibility = View.VISIBLE
            breakView.tv_breaktext.visibility = View.VISIBLE
            breakView.btn_stopBreak.visibility = View.VISIBLE
        }

        // Stop Button
        breakView.btn_stopBreak.setOnClickListener{
            breakTimer.cancel()
            breakView.btn_startBreak.visibility = View.VISIBLE
            breakView.btn_stopBreak.visibility = View.INVISIBLE
            breakView.tv_breakCountdown.visibility = View.INVISIBLE
            breakView.tv_breaktext.visibility = View.INVISIBLE
        }


        return breakView
    }

    fun longBreakFragment() {

        val testInput = arguments?.getString("longBreak")
        val testDemo = testInput.toString()

        communicator.passLongBreakData(testDemo)
    }

    fun workFragment() {
        val fragment = WorkFragment()
        val fragmentManager = activity!!.supportFragmentManager

        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

}