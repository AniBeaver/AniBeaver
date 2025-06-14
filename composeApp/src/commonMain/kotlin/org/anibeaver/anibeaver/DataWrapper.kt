package org.anibeaver.anibeaver

import org.anibeaver.anibeaver.api.ApiHandler

class DataWrapper(
    val activityKiller: () -> Unit = {},
    val apiHandler : ApiHandler)