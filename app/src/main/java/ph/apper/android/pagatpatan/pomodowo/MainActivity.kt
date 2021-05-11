package ph.apper.android.pagatpatan.pomodowo

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), Communicator{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val workFragment = WorkFragment()
        supportFragmentManager.beginTransaction().replace(R.id.container, workFragment, "Work").commit()
        //TODO button

    }


    // Communicator - Work Fragment
    override fun passWorkData(focus: String, shortBreak: String, longBreak: String, checkedTasks: String) {
        val bundle = Bundle()
        //This is to make the user setting persistent
        val sharedPreferences: SharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)

            bundle.putString("focus", focus)
            bundle.putString("break", shortBreak)
            bundle.putString("longBreak", longBreak)
            bundle.putString("checkedTasks", checkedTasks)


        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.apply {
            putString("focus", focus)
            putString("break", shortBreak)
            putString("longBreak", longBreak)
            putString("checkedTasks", checkedTasks)
        }.apply()


        val transaction = this.supportFragmentManager.beginTransaction()
        val workFragment = WorkFragment()
        workFragment.arguments = bundle


        transaction.replace(R.id.container, workFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}