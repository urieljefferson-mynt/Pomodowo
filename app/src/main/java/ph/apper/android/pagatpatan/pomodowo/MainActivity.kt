package ph.apper.android.pagatpatan.pomodowo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), Communicator{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val workFragment = WorkFragment()
        supportFragmentManager.beginTransaction().replace(R.id.container, workFragment).commit()
        //TODO button

    }

    // Communicator - Work Fragment
    override fun passWorkData(focus: String, shortBreak: String, longBreak: String, checkedTasks: String) {
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


}