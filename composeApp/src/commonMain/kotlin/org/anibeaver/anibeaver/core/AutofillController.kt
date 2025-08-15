package org.anibeaver.anibeaver.core

import org.anibeaver.anibeaver.api.ApiHandler
import org.anibeaver.anibeaver.api.ValueSetter
import org.anibeaver.anibeaver.api.RequestType
import org.anibeaver.anibeaver.api.jsonStructures.AutofillMediaQuery

object AutofillController {
    suspend fun pullDataFromAniList(mediaId: String, apiHandler: ApiHandler, onResult: (AutofillMediaQuery) -> Unit) {
        apiHandler.makeRequest<AutofillMediaQuery>(
            variables = mapOf("id" to mediaId),
            valueSetter = ValueSetter { result: AutofillMediaQuery ->
                println(result)
                onResult(result)
            },
            requestType = RequestType.AUTOFILL_MEDIA
        )
    }
}