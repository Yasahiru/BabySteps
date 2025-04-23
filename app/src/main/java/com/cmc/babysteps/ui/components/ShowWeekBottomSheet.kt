package com.cmc.babysteps.ui.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ShowWeekBottomSheet(onWeekSelected: (Int) -> Unit) {
    // Pregnancy theme colors
    val babyPink = Color(0xFFF8BBD0)
    val lightPurple = Color(0xFFD1C4E9)
    val softBlue = Color(0xFFBBDEFB)
    val babyGreen = Color(0xFFDCEDC8)
    val accentPink = Color(0xFFAD1457)

    Surface(
        color = Color(0xFFFCE4EC)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Icon(
                    Icons.Filled.CalendarMonth,
                    contentDescription = "Calendar",
                    tint = accentPink,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    "Select Your Pregnancy Week",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = accentPink
                )
            }

            Divider(
                color = babyPink,
                thickness = 2.dp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                "First Trimester",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF880E4F),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // First trimester (Weeks 1-13)
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(120.dp)
            ) {
                items((1..13).toList()) { week ->
                    WeekButton(
                        week = week,
                        color = babyPink,
                        onWeekSelected = onWeekSelected
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Second Trimester",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4A148C),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Second trimester (Weeks 14-26)
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(160.dp)
            ) {
                items((14..26).toList()) { week ->
                    WeekButton(
                        week = week,
                        color = lightPurple,
                        onWeekSelected = onWeekSelected
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Third Trimester",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0D47A1),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Third trimester (Weeks 27-40)
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(200.dp)
            ) {
                items((27..40).toList()) { week ->
                    WeekButton(
                        week = week,
                        color = softBlue,
                        onWeekSelected = onWeekSelected
                    )
                }
            }
        }
    }
}

@Composable
private fun WeekButton(
    week: Int,
    color: Color,
    onWeekSelected: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .size(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onWeekSelected(week) },
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(4.dp)
            ) {
                Text(
                    text = "$week",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Text(
                    text = "week",
                    fontSize = 10.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}