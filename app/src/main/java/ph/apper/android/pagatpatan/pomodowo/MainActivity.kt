package ph.apper.android.pagatpatan.pomodowo

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView




class MainActivity : AppCompatActivity(), Communicator{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val workFragment = WorkFragment()
        supportFragmentManager.beginTransaction().replace(R.id.container, workFragment, "Work").commit()
    }


    // Communicator - Work Fragment
    override fun passWorkData(focus: String, shortBreak: String, longBreak: String, checkedTasks: String) {

        //Persist User Settings
        val sharedPreferences: SharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)



        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.apply {
            putString("focus", focus)
            putString("break", shortBreak)
            putString("longBreak", longBreak)
            putString("checkedTasks", checkedTasks)
        }.apply()


        val bundle = Bundle()
        bundle.putString("focus", focus)
        bundle.putString("break", shortBreak)
        bundle.putString("longBreak", longBreak)
        bundle.putString("checkedTasks", checkedTasks)

        val transaction = this.supportFragmentManager.beginTransaction()
        val workFragment = WorkFragment()
        workFragment.arguments = bundle


        transaction.replace(R.id.container, workFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun passLongBreakCredits(longBreakCredit: Int) {
        persistLongBreakCredits(longBreakCredit)
    }

    fun persistLongBreakCredits(longBreakCredits: Int){
        val sharedPreferences: SharedPreferences = getSharedPreferences("persistentCredits", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.apply {
            putInt("longBreakCredits", longBreakCredits)
        }.apply()
    }
}