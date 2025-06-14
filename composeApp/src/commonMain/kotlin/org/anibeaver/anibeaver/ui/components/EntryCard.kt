package org.anibeaver.anibeaver.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EntryCard(
    name: String,
    labels: String,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    Card(shape = RoundedCornerShape(6.dp)) {
        Row(
            Modifier
                .height(120.dp)
                .width(420.dp)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(44.dp)
                    .background(Color.LightGray, RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("IMG", fontSize = 10.sp, color = Color.DarkGray)
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                Text(name, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(8.dp))
                Text(labels, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 12.sp, color = Color.Gray)
            }
            Spacer(Modifier.width(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.End) {
                Button(onClick = onEdit, contentPadding = PaddingValues(0.dp), modifier = Modifier.height(28.dp)) {
                    Text("Edit", fontSize = 12.sp)
                }
                Button(onClick = onDelete, contentPadding = PaddingValues(0.dp), modifier = Modifier.height(28.dp)) {
                    Text("Delete", fontSize = 12.sp)
                }
            }
        }
    }
}
