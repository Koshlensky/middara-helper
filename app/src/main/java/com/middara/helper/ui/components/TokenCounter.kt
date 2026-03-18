package com.middara.helper.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.middara.helper.ui.theme.DarkCard
import com.middara.helper.ui.theme.DarkCardBorder
import com.middara.helper.ui.theme.GoldAccent
import com.middara.helper.ui.theme.TextSecondary

@Composable
fun TokenCounter(
    label: String,
    value: Int,
    accentColor: Color = GoldAccent,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = modifier
            .background(DarkCard, shape)
            .border(1.dp, DarkCardBorder, shape)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = accentColor,
            fontWeight = FontWeight.SemiBold
        )
        IconButton(
            onClick = onDecrement,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Уменьшить",
                tint = accentColor,
                modifier = Modifier.size(16.dp)
            )
        }
        Text(
            text = value.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.widthIn(min = 32.dp),
            textAlign = TextAlign.Center
        )
        IconButton(
            onClick = onIncrement,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Увеличить",
                tint = accentColor,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun EnduranceDots(
    value: Int,
    maxValue: Int = 5,
    onSetValue: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    val activeColor = Color(0xFFE53935)
    val inactiveColor = Color(0xFF3A1A1A)

    Row(
        modifier = modifier
            .background(DarkCard, shape)
            .border(1.dp, DarkCardBorder, shape)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "ПВ",
            fontSize = 12.sp,
            color = activeColor,
            fontWeight = FontWeight.SemiBold
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 1..maxValue) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(if (i <= value) activeColor else inactiveColor)
                        .border(
                            1.5.dp,
                            if (i <= value) activeColor else activeColor.copy(alpha = 0.35f),
                            CircleShape
                        )
                        .clickable { onSetValue(if (i == value) 0 else i) }
                )
            }
        }
    }
}
