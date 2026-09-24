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
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hastaa.datausagemonitor.domain.model.UsagePeriod
import com.hastaa.datausagemonitor.ui.screen.settings.components.PeriodChoiceItem
import com.hastaa.datausagemonitor.ui.screen.settings.components.SectionHeader

@Composable
fun TilePeriodSection(
    selectedPeriod: UsagePeriod,
    onSelectPeriod: (UsagePeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SectionHeader(
            title = "Data Usage Period",
            subtitle = "Time range calculated in the Quick Settings tile",
            icon = Icons.Rounded.DateRange
        )
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    UsagePeriod.TODAY,
                    UsagePeriod.THIS_WEEK,
                    UsagePeriod.THIS_MONTH
                ).forEach { period ->
                    PeriodChoiceItem(
                        label = period.label,
                        isSelected = selectedPeriod == period,
                        onSelect = { onSelectPeriod(period) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
