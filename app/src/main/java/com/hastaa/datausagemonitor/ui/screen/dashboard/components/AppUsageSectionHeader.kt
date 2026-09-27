package com.hastaa.datausagemonitor.ui.screen.dashboard.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hastaa.datausagemonitor.data.local.AppTheme
import com.hastaa.datausagemonitor.ui.screen.dashboard.NetworkFilter
import com.hastaa.datausagemonitor.ui.theme.CyanNeon
import com.hastaa.datausagemonitor.ui.theme.LocalAppTheme
import com.hastaa.datausagemonitor.ui.theme.SurfaceBorderDark
import com.hastaa.datausagemonitor.ui.theme.TextSecondary
import com.hastaa.datausagemonitor.ui.theme.TextTertiary

@Composable
fun AppUsageSectionHeader(
    appCount: Int,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    isSearchExpanded: Boolean,
    onToggleSearch: () -> Unit,
    selectedFilter: NetworkFilter,
    onNetworkFilterChanged: (NetworkFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val appTheme = LocalAppTheme.current
    val isCyberNeon = appTheme == AppTheme.CYBER_NEON

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Application Usage",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isCyberNeon) MaterialTheme.colorScheme.surfaceVariant
                            else MaterialTheme.colorScheme.surfaceContainerHighest
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = appCount.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isCyberNeon) CyanNeon else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            IconButton(onClick = onToggleSearch) {
                Icon(
                    imageVector = if (isSearchExpanded) Icons.Rounded.Close else Icons.Rounded.Search,
                    contentDescription = "Toggle Search",
                    tint = if (isCyberNeon) {
                        if (isSearchExpanded) CyanNeon else TextSecondary
                    } else {
                        if (isSearchExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }

        AnimatedVisibility(visible = isSearchExpanded) {
            TextField(
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                placeholder = {
                    Text(
                        text = "Search applications...",
                        color = if (isCyberNeon) TextTertiary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = if (isCyberNeon) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surfaceContainerHigh,
                    unfocusedContainerColor = if (isCyberNeon) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surfaceContainerHigh,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = if (isCyberNeon) CyanNeon else MaterialTheme.colorScheme.primary,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(14.dp),
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { onSearchQueryChanged("") }) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Clear Search",
                                tint = if (isCyberNeon) TextSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .then(
                        if (isCyberNeon) Modifier.border(1.dp, SurfaceBorderDark, RoundedCornerShape(14.dp))
                        else Modifier
                    )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NetworkFilter.values().forEach { filter ->
                val isSelected = filter == selectedFilter
                if (isCyberNeon) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) CyanNeon.copy(alpha = 0.2f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .border(
                                1.dp,
                                if (isSelected) CyanNeon else SurfaceBorderDark,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { onNetworkFilterChanged(filter) }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = filter.label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) CyanNeon else TextSecondary
                        )
                    }
                } else {
                    FilterChip(
                        selected = isSelected,
                        onClick = { onNetworkFilterChanged(filter) },
                        label = {
                            Text(
                                text = filter.label,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        leadingIcon = if (isSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors()
                    )
                }
            }
        }
    }
}
