package ph.apper.android.pagatpatan.pomodowo

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_break.*
import kotlinx.android.synthetic.main.fragment_break.view.*
import kotlinx.android.synthetic.main.fragment_long_break.*
import kotlinx.android.synthetic.main.fragment_long_break.view.*
import kotlinx.android.synthetic.main.fragment_work.view.*
import java.util.concurrent.TimeUnit

class LongBreakFragment : Fragment() {

    private lateinit var communicator: Communicator

    companion object {
        private lateinit var longBreakTimer: CountDownTimer
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_long_break, container, false)

        communicator = activity as Communicator

        // Start Button
        view.btn_stopLongBreak.visibility = View.INVISIBLE
        view.tv_longBreakCountdown.visibility = View.INVISIBLE
        view.tv_longBreakText.visibility = View.INVISIBLE

        view.btn_startLongBreak.setOnClickListener{

            val timeLongBreak = arguments?.getString("longBreak")
            val timeLongBreakStart = timeLongBreak.toString()

            // CountDownTimer
            longBreakTimer = object: CountDownTimer(timeLongBreakStart.toLong() * 1000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    tv_longBreakCountdown.setText("" + String.format("%d:%d:%d",
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))))
                }
                override fun onFinish() {
                    cancel()
                    tv_longBreakCountdown.setText("HH:MM:SS")
                    workFragment() // nav to work fragment

                }
            }.start()
            view.btn_startLongBreak.visibility = View.INVISIBLE
            view.tv_longBreakCountdown.visibility = View.VISIBLE
            view.tv_longBreakText.visibility = View.VISIBLE
            view.btn_stopLongBreak.visibility = View.VISIBLE
        }

        // Stop Button
        view.btn_stopLongBreak.setOnClickListener{
            longBreakTimer.cancel()
            view.btn_startLongBreak.visibility = View.VISIBLE
            view.btn_stopLongBreak.visibility = View.INVISIBLE
            view.tv_longBreakCountdown.visibility = View.INVISIBLE
            view.tv_longBreakText.visibility = View.INVISIBLE
        }

        return view
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