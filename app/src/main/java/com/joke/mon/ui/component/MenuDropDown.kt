package com.joke.mon.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.joke.mon.R
import com.joke.mon.core.util.Config
import com.joke.mon.ui.theme.Grayscale200
import com.joke.mon.ui.theme.Grayscale500

@Composable
fun SortMenuDropdown(
    expanded: Boolean,
    sortOption: Config.SortOption,
    onDismiss: () -> Unit,
    onSelected: (Config.SortOption) -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = Modifier.width(180.dp),
        containerColor = Grayscale200,
        shape = RoundedCornerShape(12.dp)
    ) {
        DropdownMenuItem(
            modifier = Modifier.padding(start = 20.dp).background(Color.Transparent),
            text = {
                Text(stringResource(R.string.sort_order), style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 13.sp , color = Grayscale500
                ))
            },
            onClick = {},
            enabled = false
        )

        Divider()

        DropdownMenuItem(
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (sortOption == Config.SortOption.LATEST)
                        Icon(Icons.Default.Check, contentDescription = null)
                    else
                        Spacer(modifier = Modifier.size(24.dp))

                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.sort_by_latest) , style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp ))
                }
            },
            onClick = { onSelected(Config.SortOption.LATEST) }
        )

        Divider()

        DropdownMenuItem(
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (sortOption == Config.SortOption.OLDEST)
                        Icon(Icons.Default.Check, contentDescription = null)
                    else
                        Spacer(modifier = Modifier.size(24.dp))

                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.sort_by_oldest) , style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp))
                }
            },
            onClick = { onSelected(Config.SortOption.OLDEST) }
        )
    }
}
