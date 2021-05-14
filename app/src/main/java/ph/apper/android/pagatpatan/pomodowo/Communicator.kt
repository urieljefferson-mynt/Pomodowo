package ph.apper.android.pagatpatan.pomodowo

interface Communicator {
    fun passWorkData(focus: String, shortBreak: String, longBreak: String, checkedTasks: String)
    fun passLongBreakCredits(longBreakCredit: Int)
}