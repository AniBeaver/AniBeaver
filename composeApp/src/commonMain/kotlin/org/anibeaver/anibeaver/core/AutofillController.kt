package org.anibeaver.anibeaver.core

import org.anibeaver.anibeaver.api.RequestType
import org.anibeaver.anibeaver.api.ValueSetter
import org.anibeaver.anibeaver.api.jsonStructures.AutofillMediaQuery
import org.anibeaver.anibeaver.DataWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.anibeaver.anibeaver.api.jsonStructures.AutofillData

object ParsedAutofillData {}

object AutofillController {
    fun idIsValid(mediaId: String): Boolean {
        return mediaId.isNotBlank() && mediaId.toIntOrNull() != null && mediaId.length == 6 && mediaId.all { it.isDigit() }
    }

    fun pullParsedAutofill(
        mediaIds: List<String>,
        onResult: (ParsedAutofillData) -> Unit,
        dataWrapper: DataWrapper,
        scope: CoroutineScope
    ) {
        fun parseAutofillDataList(autofillDataList: List<AutofillData>): ParsedAutofillData {
            // TODO: implement
            return ParsedAutofillData
        }
        scope.launch {
            val results = mutableListOf<AutofillData>()
            val validIds = mediaIds.filter { idIsValid(it) }
            var completed = 0
            if (validIds.isEmpty()) {
                onResult(parseAutofillDataList(results))
                return@launch
            }
            for (mediaId in validIds) {
                dataWrapper.apiHandler.makeRequest(
                    variables = mapOf("id" to mediaId),
                    valueSetter = ValueSetter { mediaQuery: AutofillMediaQuery ->
                        results.add(mediaQuery.data.media)
                        completed++
                        if (completed == validIds.size) {
                            onResult(parseAutofillDataList(results))
                        }
                    },
                    requestType = RequestType.AUTOFILL_MEDIA
                )
            }
        }
    }
}
