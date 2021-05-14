package ph.apper.android.pagatpatan.pomodowo

import kotlin.properties.Delegates

object FinishedTasks {
    var LONG_BREAK_CREDITS: Int by Delegates.observable(0) { _, _, new ->

    }
    var REQUIRED_TASKS: Int = 0

}