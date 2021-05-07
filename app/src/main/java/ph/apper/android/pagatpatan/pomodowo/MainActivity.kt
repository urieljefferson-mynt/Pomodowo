package ph.apper.android.pagatpatan.pomodowo

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

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

    // Communicator - Short Break Fragment
    override fun passBreakData(shortBreak: String, longBreak: String) {
        val bundle = Bundle()
        bundle.putString("break", shortBreak)
        bundle.putString("longBreak", longBreak)

        val transaction = this.supportFragmentManager.beginTransaction()
        val breakFragment = BreakFragment()
        breakFragment.arguments = bundle

        transaction.replace(R.id.container, breakFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    // Communicator - Long Break Fragment
    override fun passLongBreakData(longBreak: String) {
        val bundle = Bundle()
        bundle.putString("longBreak", longBreak)

        val transaction = this.supportFragmentManager.beginTransaction()
        val longBreakFragment = LongBreakFragment()
        longBreakFragment.arguments = bundle

        transaction.replace(R.id.container, longBreakFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    // Menu -> Settings Fragment
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item?.itemId){
            R.id.nav_settings -> {
                val fragment = SettingsFragment.newInstance()
                replaceFragment(fragment)
                true
            } R.id.nav_back -> {
                val fragment = WorkFragment.newInstance()
                replaceFragment(fragment)
                true
            } else -> super.onOptionsItemSelected(item)
        }
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragment)
        fragmentTransaction.commit()
    }



    override fun onPrepareOptionsMenu(menu: Menu) :Boolean {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.nav_back).setVisible(false)
        return true
    }


}