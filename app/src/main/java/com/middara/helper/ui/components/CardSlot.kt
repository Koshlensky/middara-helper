package com.middara.helper.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.middara.helper.data.model.CardType
import com.middara.helper.data.model.EquippedCard
import com.middara.helper.ui.theme.DarkCard
import com.middara.helper.ui.theme.DarkCardBorder
import com.middara.helper.ui.theme.GoldAccent
import com.middara.helper.ui.theme.TextSecondary

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardSlot(
    cardType: CardType,
    equippedCard: EquippedCard?,
    width: Dp = 90.dp,
    height: Dp = 126.dp,
    onAddClick: () -> Unit = {},
    onCardClick: (EquippedCard) -> Unit = {},
    onCardLongClick: (EquippedCard) -> Unit = {}
) {
    val shape = RoundedCornerShape(8.dp)

    if (equippedCard != null) {
        Box(
            modifier = Modifier
                .width(width)
                .height(height)
                .clip(shape)
                .combinedClickable(
                    onClick = { onCardClick(equippedCard) },
                    onLongClick = { onCardLongClick(equippedCard) }
                )
        ) {
            CardView(
                equippedCard = equippedCard,
                modifier = Modifier.fillMaxSize()
            )
        }
    } else {
        Box(
            modifier = Modifier
                .width(width)
                .height(height)
                .background(DarkCard.copy(alpha = 0.5f), shape)
                .border(1.dp, DarkCardBorder, shape)
                .combinedClickable(onClick = onAddClick),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить",
                    tint = GoldAccent.copy(alpha = 0.6f),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = cardType.displayName,
                    fontSize = 9.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 12.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}
