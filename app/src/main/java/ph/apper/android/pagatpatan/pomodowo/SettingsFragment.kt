package ph.apper.android.pagatpatan.pomodowo

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_settings.view.*


class SettingsFragment : Fragment() {

    private lateinit var communicator: Communicator

    companion object {
        fun newInstance() = SettingsFragment() // for menu button
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val settingsView = inflater.inflate(R.layout.fragment_settings, container, false)

        // Communicator
        communicator = activity as Communicator

        settingsView.btn_submit.setOnClickListener{
            communicator.passWorkData(settingsView.et_focustime.text.toString(),
                                      settingsView.et_shortbreaktime.text.toString(),
                                      settingsView.et_longbreaktime.text.toString())
        }

        return settingsView
    }

    // Menu
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.nav_settings).setVisible(false)
        menu.findItem(R.id.nav_back).setVisible(true)
    }

    // Hide Action Bar
//    override fun onResume() {
//        super.onResume()
//        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
//    }
//
//    override fun onStop() {
//        super.onStop()
//        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
//    }

//    fun workFragment() {
//        val fragment = WorkFragment()
//        val fragmentManager = activity!!.supportFragmentManager
//
//        val fragmentTransaction = fragmentManager.beginTransaction()
//        fragmentTransaction.replace(R.id.container, fragment)
//        fragmentTransaction.addToBackStack(null)
//        fragmentTransaction.commit()
//    }

}