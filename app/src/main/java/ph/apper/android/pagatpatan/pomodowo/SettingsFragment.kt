package ph.apper.android.pagatpatan.pomodowo

import android.os.Bundle
import android.view.*
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.appbar.view.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.view.*

class SettingsFragment : Fragment() {

    private lateinit var communicator: Communicator
    private var startPoint = 0
    private var endPoint = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // Action Bar Buttons
        view.ic_menu.visibility = View.INVISIBLE
        view.ic_arrow_back.setOnClickListener{
            workFragment()
        }

        // Communicator
        communicator = activity as Communicator

        // Submit Button
        view.btn_submit.setOnClickListener{
            communicator.passWorkData(view.tv_focusDuration.text.toString(),
                                      view.tv_breakDuration.text.toString(),
                                      view.tv_longBreakDuration.text.toString(),
                                      view.tv_completeTask.text.toString())
        }

        // Seekbar - Focus
        view.sb_focus.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tv_focusDuration.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                if (seekBar != null) {
                    startPoint = seekBar.progress
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (seekBar != null) {
                    endPoint = seekBar.progress
                }
                Toast.makeText(context, "Changed by ${endPoint - startPoint}%", Toast.LENGTH_SHORT).show()
            }
        })

        // Seekbar - Break
        view.sb_break.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tv_breakDuration.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                if (seekBar != null) {
                    startPoint = seekBar.progress
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (seekBar != null) {
                    endPoint = seekBar.progress
                }
                Toast.makeText(context, "Changed by ${endPoint - startPoint}%", Toast.LENGTH_SHORT).show()
            }
        })

        // Seekbar - Long Break
        view.sb_longBreak.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tv_longBreakDuration.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                if (seekBar != null) {
                    startPoint = seekBar.progress
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (seekBar != null) {
                    endPoint = seekBar.progress
                }
                Toast.makeText(context, "Changed by ${endPoint - startPoint}%", Toast.LENGTH_SHORT).show()
            }
        })

        view.sb_completeTask.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tv_completeTask.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                if (seekBar != null) {
                    startPoint = seekBar.progress
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (seekBar != null) {
                    endPoint = seekBar.progress
                }
                Toast.makeText(context, "Changed by ${endPoint - startPoint}%", Toast.LENGTH_SHORT).show()
            }
        })


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