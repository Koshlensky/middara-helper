package com.middara.helper.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.middara.helper.data.model.Card
import com.middara.helper.data.model.CardType
import com.middara.helper.data.model.EquippedCard
import com.middara.helper.ui.theme.*

@Composable
fun CardView(
    equippedCard: EquippedCard,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .graphicsLayer {
                rotationZ = equippedCard.rotationDegrees
                scaleX = if (equippedCard.isFlipped) -1f else 1f
            }
    ) {
        CardImageContent(
            card = equippedCard.card,
            modifier = Modifier.fillMaxSize()
        )

        if (equippedCard.rotationDegrees != 0f) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(2.dp)
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 3.dp, vertical = 1.dp)
            ) {
                Text(
                    text = "${equippedCard.rotationDegrees.toInt()}°",
                    fontSize = 9.sp,
                    color = GoldAccent
                )
            }
        }

        if (equippedCard.isFlipped) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(2.dp)
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 3.dp, vertical = 1.dp)
            ) {
                Text(text = "↔", fontSize = 9.sp, color = GoldAccent)
            }
        }
    }
}

@Composable
fun CardImageContent(
    card: Card,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val shape = RoundedCornerShape(8.dp)

    if (card.imageAsset.isNotBlank()) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data("file:///android_asset/${card.imageAsset}")
                .crossfade(true)
                .build(),
            contentDescription = card.name,
            modifier = modifier.clip(shape),
            contentScale = ContentScale.Fit
        ) {
            val state = painter.state
            if (state is AsyncImagePainter.State.Error ||
                state is AsyncImagePainter.State.Empty
            ) {
                CardPlaceholder(card = card, modifier = Modifier.fillMaxSize())
            } else {
                SubcomposeAsyncImageContent()
            }
        }
    } else {
        CardPlaceholder(card = card, modifier = modifier)
    }
}

@Composable
fun CardPlaceholder(
    card: Card,
    modifier: Modifier = Modifier
) {
    val bgColor = cardTypeColor(card.type)
    val shape = RoundedCornerShape(8.dp)

    Box(
        modifier = modifier
            .background(bgColor, shape)
            .border(1.dp, DarkCardBorder, shape),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(6.dp)
        ) {
            Text(
                text = card.type.displayName,
                fontSize = 9.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 11.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = card.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 15.sp
            )
            if (card.stats.attack != 0 || card.stats.defense != 0 || card.stats.hp != 0) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    if (card.stats.hp != 0)
                        StatChip("ПЗ", card.stats.hp)
                    if (card.stats.attack != 0)
                        StatChip("АТК", card.stats.attack)
                    if (card.stats.defense != 0)
                        StatChip("ЗЩ", card.stats.defense)
                }
            }
        }
    }
}

@Composable
private fun StatChip(label: String, value: Int) {
    val color = if (value > 0) Color(0xFF4CAF50) else Color(0xFFE57373)
    Text(
        text = "$label:${if (value > 0) "+$value" else "$value"}",
        fontSize = 8.sp,
        color = color,
        fontWeight = FontWeight.Bold
    )
}

fun cardTypeColor(cardType: CardType): Color = when (cardType) {
    CardType.HERO_SHEET -> CardColorHeroSheet
    CardType.WEAPON -> CardColorWeapon
    CardType.WEAPON_UPGRADE -> CardColorWeaponUpgrade
    CardType.ARMOR -> CardColorArmor
    CardType.ARMOR_UPGRADE -> CardColorArmorUpgrade
    CardType.TRINKET -> CardColorTrinket
    CardType.RELIC -> CardColorRelic
    CardType.ACCESSORY -> CardColorAccessory
    CardType.ITEM_UPGRADE -> CardColorUpgrade
    CardType.BACKPACK -> CardColorBackpack
    CardType.DISCIPLINE -> CardColorDiscipline
    CardType.CONSUMABLE -> CardColorConsumable
    CardType.FAMILIAR -> CardColorFamiliar
    CardType.COMPANION -> CardColorCompanion
}

@Composable
fun CardStatsSummary(card: Card, modifier: Modifier = Modifier) {
    val stats = card.stats
    val items = buildList {
        if (stats.hp != 0) add("ПЗ ${if (stats.hp > 0) "+${stats.hp}" else "${stats.hp}"}")
        if (stats.attack != 0) add("АТК ${if (stats.attack > 0) "+${stats.attack}" else "${stats.attack}"}")
        if (stats.defense != 0) add("ЗЩ ${if (stats.defense > 0) "+${stats.defense}" else "${stats.defense}"}")
        if (stats.speed != 0) add("СКР ${if (stats.speed > 0) "+${stats.speed}" else "${stats.speed}"}")
        if (stats.slots != 0) add("Слоты: ${stats.slots}")
    }
    if (items.isNotEmpty()) {
        Text(
            text = items.joinToString("  "),
            fontSize = 11.sp,
            color = TextSecondary,
            modifier = modifier
        )
    }
}

@Composable
fun EquipmentBonusSummary(
    bonusStats: com.middara.helper.data.model.CardStats,
    modifier: Modifier = Modifier
) {
    val entries = buildList {
        if (bonusStats.hp != 0) add("ПЗ" to bonusStats.hp)
        if (bonusStats.attack != 0) add("АТК" to bonusStats.attack)
        if (bonusStats.defense != 0) add("ЗЩ" to bonusStats.defense)
        if (bonusStats.speed != 0) add("СКР" to bonusStats.speed)
        if (bonusStats.slots != 0) add("Слоты" to bonusStats.slots)
    }

    if (entries.isEmpty()) {
        Text(
            text = "Нет бонусов",
            fontSize = 11.sp,
            color = TextSecondary,
            modifier = modifier
        )
        return
    }

    androidx.compose.foundation.layout.Column(
        modifier = modifier,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp)
    ) {
        entries.chunked(3).forEach { row ->
            androidx.compose.foundation.layout.Row(
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { (label, value) ->
                    val color = when {
                        value > 0 -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
                        value < 0 -> androidx.compose.ui.graphics.Color(0xFFE57373)
                        else -> TextSecondary
                    }
                    val sign = if (value > 0) "+" else ""
                    androidx.compose.foundation.layout.Row(
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(2.dp)
                    ) {
                        Text(label, fontSize = 10.sp, color = TextSecondary)
                        Text("$sign$value", fontSize = 12.sp, color = color, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    }
                }
            }
        }
    }
}
