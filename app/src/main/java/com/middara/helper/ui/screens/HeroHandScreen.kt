package com.middara.helper.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.middara.helper.data.model.CardType
import com.middara.helper.data.model.EquippedCard
import com.middara.helper.ui.components.*
import com.middara.helper.ui.theme.*
import com.middara.helper.ui.viewmodel.HandViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HeroHandScreen(
    onBack: () -> Unit,
    viewModel: HandViewModel = viewModel()
) {
    val hand by viewModel.hand.collectAsState()
    var editingName by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf(hand.heroName) }
    var addingCardType by remember { mutableStateOf<CardType?>(null) }
    var selectedCard by remember { mutableStateOf<EquippedCard?>(null) }

    LaunchedEffect(hand.heroName) {
        if (!editingName) nameInput = hand.heroName
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (editingName) {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldAccent,
                                cursorColor = GoldAccent,
                                unfocusedTextColor = TextPrimary,
                                focusedTextColor = TextPrimary
                            )
                        )
                    } else {
                        Text(hand.heroName, fontWeight = FontWeight.Bold, color = GoldAccent)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Назад", tint = TextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (editingName) {
                            viewModel.updateHeroName(nameInput)
                            editingName = false
                        } else {
                            editingName = true
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = if (editingName) "Сохранить" else "Редактировать",
                            tint = if (editingName) GoldAccent else TextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Token counters
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TokenCounter(
                        label = "Урон",
                        value = hand.damageTokens,
                        accentColor = Color(0xFFE57373),
                        onIncrement = { viewModel.adjustDamage(1) },
                        onDecrement = { viewModel.adjustDamage(-1) },
                        modifier = Modifier.weight(1f)
                    )
                    TokenCounter(
                        label = "ПЗ",
                        value = hand.hpTokens,
                        accentColor = Color(0xFF81C784),
                        onIncrement = { viewModel.adjustHp(1) },
                        onDecrement = { viewModel.adjustHp(-1) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Hero Sheet
            item {
                SectionHeader(title = "ПЛАНШЕТ ГЕРОЯ")
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    CardSlot(
                        cardType = CardType.HERO_SHEET,
                        equippedCard = hand.heroSheet,
                        width = 130.dp,
                        height = 182.dp,
                        onAddClick = { addingCardType = CardType.HERO_SHEET },
                        onCardClick = { selectedCard = it },
                        onCardLongClick = { selectedCard = it }
                    )
                    hand.heroSheet?.let { equipped ->
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(equipped.card.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(equipped.card.description, fontSize = 12.sp, color = TextSecondary, lineHeight = 16.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            CardStatsSummary(card = equipped.card)
                        }
                    }
                }
            }

            // Equipment: Weapons + Armor + Trinket
            item {
                SectionHeader(title = "СНАРЯЖЕНИЕ")
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CardSlot(
                        cardType = CardType.WEAPON,
                        equippedCard = hand.weapons.getOrNull(0),
                        onAddClick = { addingCardType = CardType.WEAPON },
                        onCardClick = { selectedCard = it },
                        onCardLongClick = { selectedCard = it }
                    )
                    CardSlot(
                        cardType = CardType.WEAPON,
                        equippedCard = hand.weapons.getOrNull(1),
                        onAddClick = { addingCardType = CardType.WEAPON },
                        onCardClick = { selectedCard = it },
                        onCardLongClick = { selectedCard = it }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    CardSlot(
                        cardType = CardType.ARMOR,
                        equippedCard = hand.armor,
                        onAddClick = { addingCardType = CardType.ARMOR },
                        onCardClick = { selectedCard = it },
                        onCardLongClick = { selectedCard = it }
                    )
                    CardSlot(
                        cardType = CardType.TRINKET,
                        equippedCard = hand.trinket,
                        onAddClick = { addingCardType = CardType.TRINKET },
                        onCardClick = { selectedCard = it },
                        onCardLongClick = { selectedCard = it }
                    )
                }
            }

            // Relics
            item {
                SectionHeader(title = "РЕЛИКВИИ / АКСЕССУАРЫ", subtitle = "${hand.relics.size} шт.")
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    hand.relics.forEach { relic ->
                        CardSlot(
                            cardType = CardType.RELIC,
                            equippedCard = relic,
                            onCardClick = { selectedCard = it },
                            onCardLongClick = { selectedCard = it }
                        )
                    }
                    AddButton(onClick = { addingCardType = CardType.RELIC })
                }
            }

            // Backpack
            item {
                SectionHeader(
                    title = "РЮКЗАК",
                    subtitle = "${hand.backpack.size}/${CardType.BACKPACK.maxInHand}"
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    hand.backpack.forEach { item ->
                        CardSlot(
                            cardType = CardType.BACKPACK,
                            equippedCard = item,
                            onCardClick = { selectedCard = it },
                            onCardLongClick = { selectedCard = it }
                        )
                    }
                    if (hand.backpack.size < CardType.BACKPACK.maxInHand) {
                        AddButton(onClick = { addingCardType = CardType.BACKPACK })
                    }
                }
            }

            // Discipline cards
            item {
                SectionHeader(title = "КАРТЫ ДИСЦИПЛИН", subtitle = "${hand.disciplineCards.size} шт.")
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    hand.disciplineCards.forEach { disc ->
                        CardSlot(
                            cardType = CardType.DISCIPLINE,
                            equippedCard = disc,
                            onCardClick = { selectedCard = it },
                            onCardLongClick = { selectedCard = it }
                        )
                    }
                    AddButton(onClick = { addingCardType = CardType.DISCIPLINE })
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    // Card browser bottom sheet
    addingCardType?.let { cardType ->
        CardBrowserSheet(
            cardType = cardType,
            cardRepository = viewModel.cardRepository,
            onCardSelected = { card ->
                viewModel.addCardByType(card)
                addingCardType = null
            },
            onDismiss = { addingCardType = null }
        )
    }

    // Card options bottom sheet (tap or long-press on equipped card)
    selectedCard?.let { equipped ->
        CardOptionsSheet(
            equippedCard = equipped,
            onRotate = {
                viewModel.rotateCard(equipped.instanceId)
                selectedCard = null
            },
            onFlip = {
                viewModel.flipCard(equipped.instanceId)
                selectedCard = null
            },
            onRemove = {
                viewModel.removeCard(equipped.instanceId)
                selectedCard = null
            },
            onDismiss = { selectedCard = null }
        )
    }
}

@Composable
private fun AddButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(90.dp)
            .height(126.dp)
            .background(DarkCard.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = onClick) {
            Icon(Icons.Default.Add, "Добавить", tint = GoldAccent)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CardOptionsSheet(
    equippedCard: EquippedCard,
    onRotate: () -> Unit,
    onFlip: () -> Unit,
    onRemove: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                CardImageContent(
                    card = equippedCard.card,
                    modifier = Modifier
                        .width(90.dp)
                        .height(126.dp)
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        equippedCard.card.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = GoldAccent
                    )
                    Text(
                        equippedCard.card.type.displayName,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        equippedCard.card.description,
                        fontSize = 13.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                    CardStatsSummary(card = equippedCard.card)
                    if (equippedCard.rotationDegrees != 0f || equippedCard.isFlipped) {
                        Text(
                            buildString {
                                if (equippedCard.rotationDegrees != 0f)
                                    append("Поворот: ${equippedCard.rotationDegrees.toInt()}°")
                                if (equippedCard.isFlipped) {
                                    if (isNotEmpty()) append("  ")
                                    append("Перевёрнута")
                                }
                            },
                            fontSize = 11.sp,
                            color = GoldAccent
                        )
                    }
                }
            }

            HorizontalDivider(color = DarkCardBorder)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onRotate,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldAccent),
                    border = BorderStroke(1.dp, SolidColor(GoldAccent.copy(alpha = 0.5f)))
                ) {
                    Text("Повернуть\n+90°", fontSize = 12.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
                OutlinedButton(
                    onClick = onFlip,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldAccent),
                    border = BorderStroke(1.dp, SolidColor(GoldAccent.copy(alpha = 0.5f)))
                ) {
                    Text(
                        if (equippedCard.isFlipped) "Вернуть\nлицом" else "Перевернуть",
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
                Button(
                    onClick = onRemove,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B1A1A))
                ) {
                    Text("Убрать\nкарту", fontSize = 12.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }
        }
    }
}
