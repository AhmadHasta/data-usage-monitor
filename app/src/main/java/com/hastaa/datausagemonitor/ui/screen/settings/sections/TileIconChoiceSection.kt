package com.hastaa.datausagemonitor.ui.screen.settings.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hastaa.datausagemonitor.domain.model.TileIconChoice
import com.hastaa.datausagemonitor.ui.screen.settings.components.IconChoiceGridItem
import com.hastaa.datausagemonitor.ui.screen.settings.components.SectionHeader

@Composable
fun TileIconChoiceSection(
    selectedChoice: TileIconChoice,
    onSelectChoice: (TileIconChoice) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SectionHeader(
            title = "Network Icon Choice",
            subtitle = "Icon symbol used on the tile",
            icon = Icons.Rounded.Wifi
        )
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IconChoiceGridItem(
                        choice = TileIconChoice.AUTO,
                        isSelected = selectedChoice == TileIconChoice.AUTO,
                        onSelect = { onSelectChoice(TileIconChoice.AUTO) },
                        modifier = Modifier.weight(1f)
                    )
                    IconChoiceGridItem(
                        choice = TileIconChoice.WIFI,
                        isSelected = selectedChoice == TileIconChoice.WIFI,
                        onSelect = { onSelectChoice(TileIconChoice.WIFI) },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IconChoiceGridItem(
                        choice = TileIconChoice.CELLULAR,
                        isSelected = selectedChoice == TileIconChoice.CELLULAR,
                        onSelect = { onSelectChoice(TileIconChoice.CELLULAR) },
                        modifier = Modifier.weight(1f)
                    )
                    IconChoiceGridItem(
                        choice = TileIconChoice.DATA_USAGE,
                        isSelected = selectedChoice == TileIconChoice.DATA_USAGE,
                        onSelect = { onSelectChoice(TileIconChoice.DATA_USAGE) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
