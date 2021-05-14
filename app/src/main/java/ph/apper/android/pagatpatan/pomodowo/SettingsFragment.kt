package ph.apper.android.pagatpatan.pomodowo

import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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


        val sharedPreferences: SharedPreferences? = this.activity?.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)

        //Restores the position of the seekbar from the latest user settings.
        val focus: String? = sharedPreferences?.getString("focus", null)
        val shortBreak: String? = sharedPreferences?.getString("break", null)
        val longBreak: String? = sharedPreferences?.getString("longBreak", null)
        val checkedTasks: String? = sharedPreferences?.getString("checkedTasks", null)
        Log.d("SHAREDPREFS", "$focus, $shortBreak, $longBreak, $checkedTasks")


        //Sets the seekbar position to default in case there is no existing previous user setting
        Log.d("SEEK", "$focus, $shortBreak, $longBreak, $checkedTasks")
        view.sb_break.progress = shortBreak?.toInt() ?: 50
        view.sb_focus.progress = focus?.toInt() ?: 50
        view.sb_longBreak.progress = longBreak?.toInt() ?: 50
        view.sb_completeTask.progress = checkedTasks?.toInt() ?: 5

        //Set text depending on seekbar position
        view.tv_focusDuration.text = view.sb_focus.progress.toString()
        view.tv_breakDuration.text = view.sb_break.progress.toString()
        view.tv_longBreakDuration.text = view.sb_longBreak.progress.toString()
        view.tv_completeTask.text = view.sb_completeTask.progress.toString()

        Log.d("SEEK", "NOT CALLED")

        // Action Bar Buttons
        view.ic_menu.visibility = View.INVISIBLE
        view.ic_arrow_back.setOnClickListener{
//            workFragment(savedInstanceState)
            this.fragmentManager?.popBackStackImmediate()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingsColor()
    }

    fun workFragment(savedInstanceState: Bundle?) {
            val fragment = WorkFragment()
            val fragmentManager = activity!!.supportFragmentManager

            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container, fragment)
//            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()

    }

    //Setting fragment theme
    fun settingsColor() {
        var focusFrag = frag_settings
        focusFrag.setBackgroundResource(R.drawable.gradient_settings_list)
        var animationDrawable: AnimationDrawable = focusFrag.background as AnimationDrawable
        animationDrawable.setEnterFadeDuration(10)
        animationDrawable.setExitFadeDuration(500)
        animationDrawable.start()
    }


}