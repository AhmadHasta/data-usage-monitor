package com.hastaa.datausagemonitor.ui.components.appitem

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hastaa.datausagemonitor.domain.model.AppDataUsage
import com.hastaa.datausagemonitor.ui.theme.CyanNeon
import com.hastaa.datausagemonitor.ui.theme.SurfaceBorderDark
import com.hastaa.datausagemonitor.ui.theme.TextSecondary
import com.hastaa.datausagemonitor.ui.theme.TextTertiary
import com.hastaa.datausagemonitor.util.ByteFormatter

@Composable
fun CyberNeonAppUsageItem(
    app: AppDataUsage,
    iconImageBitmap: ImageBitmap?,
    animatedRatio: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, SurfaceBorderDark, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (iconImageBitmap != null) {
                    Image(
                        bitmap = iconImageBitmap,
                        contentDescription = app.appName,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CyanNeon.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Android,
                            contentDescription = null,
                            tint = CyanNeon,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = app.appName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = app.packageName,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                        color = TextTertiary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                val (valStr, unitStr) = remember(app.totalBytes) {
                    ByteFormatter.formatBytesParts(app.totalBytes)
                }
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = valStr,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = unitStr,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = CyanNeon
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                if (animatedRatio > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedRatio)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(CyanNeon)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            val mobileStr = remember(app.mobileBytes) {
                "Mobile: ${ByteFormatter.formatBytes(app.mobileBytes)}"
            }
            val wifiStr = remember(app.wifiBytes) {
                "Wi-Fi: ${ByteFormatter.formatBytes(app.wifiBytes)}"
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = mobileStr,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                    color = TextSecondary
                )
                Text(
                    text = wifiStr,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                    color = TextSecondary
                )
            }
        }
    }
}
