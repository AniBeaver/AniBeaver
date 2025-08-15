package org.anibeaver.anibeaver.ui.modals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.anibeaver.anibeaver.core.datastructures.Reference
import org.anibeaver.anibeaver.ui.components.references.ReferenceRow


@Composable
fun ManageAutofillPopup(
    show: Boolean,
    references: List<Reference>,
    onAddReference: (Reference) -> Unit,
    onDeleteReference: (Reference) -> Unit,
    onUpdateReference: (Reference, Reference) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onConfirmReorder: (List<Reference>) -> Unit
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                Button(onClick = onConfirm) {
                    Text("Autofill")
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text("Close for now")
                }
            },
            title = { Text("Manage AL Autofill") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text("Add any number of references here (e.g a single series or all seasons of a series) to automatically extract (common) data and fill in the selected entry inputs.")
                    references.forEachIndexed { idx, ref ->
                        ReferenceRow(
                            alId = ref.alId, // Use alId as String directly
                            refNote = ref.note,
                            onAlIdChange = { newAlIdStr ->
                                onUpdateReference(ref, Reference(ref.note, newAlIdStr))
                            },
                            onRefNoteChange = { newNote ->
                                onUpdateReference(ref, Reference(newNote, ref.alId))
                            },
                            onDelete = { onDeleteReference(ref) },
                            onMoveUp = if (idx > 0) {
                                {
                                    val newList = references.toMutableList()
                                    newList.removeAt(idx)
                                    newList.add(idx - 1, ref)
                                    onConfirmReorder(newList)
                                }
                            } else null,
                            onMoveDown = if (idx < references.lastIndex) {
                                {
                                    val newList = references.toMutableList()
                                    newList.removeAt(idx)
                                    newList.add(idx + 1, ref)
                                    onConfirmReorder(newList)
                                }
                            } else null
                        )
                    }
                    Button(onClick = { onAddReference(Reference("", "")) }, modifier = Modifier) {
                        Text("Add Reference")
                    }
                }
            }
        )
    }
}