package com.middara.helper.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.middara.helper.data.model.CardType
import com.middara.helper.data.model.EquippedCard
import com.middara.helper.data.model.WeaponMode
import com.middara.helper.data.model.totalBonusStats
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

    var weaponSheetSlot by remember { mutableStateOf<Int?>(null) }
    var weaponSheetInitialTab by remember { mutableStateOf(0) }
    var armorSheetOpen by remember { mutableStateOf(false) }
    var armorSheetInitialTab by remember { mutableStateOf(0) }

    // Collapsible state
    var equipmentExpanded by rememberSaveable { mutableStateOf(true) }
    var relicsExpanded by rememberSaveable { mutableStateOf(true) }
    var accessoryExpanded by rememberSaveable { mutableStateOf(true) }
    var backpackExpanded by rememberSaveable { mutableStateOf(true) }
    var disciplinesExpanded by rememberSaveable { mutableStateOf(true) }
    var consumablesExpanded by rememberSaveable { mutableStateOf(true) }
    var companionsExpanded by rememberSaveable { mutableStateOf(true) }

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
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                capitalization = KeyboardCapitalization.Words
                            ),
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
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TokenCounter(
                        label = "Здоровье",
                        value = hand.healthPoints,
                        accentColor = Color(0xFF66BB6A),
                        onIncrement = { viewModel.adjustHealth(1) },
                        onDecrement = { viewModel.adjustHealth(-1) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    TokenCounter(
                        label = "Повреждение",
                        value = hand.damageTokens,
                        accentColor = Color(0xFFE57373),
                        onIncrement = { viewModel.adjustDamage(1) },
                        onDecrement = { viewModel.adjustDamage(-1) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    EnduranceDots(
                        value = hand.endurancePoints,
                        onSetValue = { viewModel.setEndurance(it) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Hero Sheet + Bonus Stats + End Turn button
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
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        hand.heroSheet?.let { equipped ->
                            Text(equipped.card.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(equipped.card.description, fontSize = 12.sp, color = TextSecondary, lineHeight = 16.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            CardStatsSummary(card = equipped.card)
                            HorizontalDivider(color = DarkCardBorder, modifier = Modifier.padding(vertical = 4.dp))
                        }
                        Text(
                            text = "Бонусы снаряжения",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GoldAccent
                        )
                        EquipmentBonusSummary(bonusStats = hand.totalBonusStats())
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedButton(
                            onClick = { viewModel.endTurn() },
                            modifier = Modifier.wrapContentWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldAccent),
                            border = BorderStroke(1.dp, SolidColor(GoldAccent.copy(alpha = 0.5f))),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text("Конец хода", fontSize = 12.sp)
                        }
                    }
                }
            }

            // Equipment: Weapons + Armor + Trinket
            item {
                SectionHeader(
                    title = "СНАРЯЖЕНИЕ",
                    isExpanded = equipmentExpanded,
                    onToggle = { equipmentExpanded = !equipmentExpanded }
                )
                if (equipmentExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.setWeaponMode(WeaponMode.ONE_HANDED) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (hand.weaponMode == WeaponMode.ONE_HANDED) GoldAccent.copy(alpha = 0.15f) else Color.Transparent,
                            contentColor = if (hand.weaponMode == WeaponMode.ONE_HANDED) GoldAccent else TextSecondary
                        ),
                        border = BorderStroke(1.dp, SolidColor(if (hand.weaponMode == WeaponMode.ONE_HANDED) GoldAccent else DarkCardBorder)),
                        contentPadding = PaddingValues(vertical = 6.dp)
                    ) { Text("Одноручное ×2", fontSize = 12.sp) }
                    OutlinedButton(
                        onClick = { viewModel.setWeaponMode(WeaponMode.TWO_HANDED) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (hand.weaponMode == WeaponMode.TWO_HANDED) GoldAccent.copy(alpha = 0.15f) else Color.Transparent,
                            contentColor = if (hand.weaponMode == WeaponMode.TWO_HANDED) GoldAccent else TextSecondary
                        ),
                        border = BorderStroke(1.dp, SolidColor(if (hand.weaponMode == WeaponMode.TWO_HANDED) GoldAccent else DarkCardBorder)),
                        contentPadding = PaddingValues(vertical = 6.dp)
                    ) { Text("Двуручное", fontSize = 12.sp) }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            if (hand.weaponMode == WeaponMode.TWO_HANDED) "Двуручное" else "Оружие 1",
                            fontSize = 10.sp, color = TextSecondary, modifier = Modifier.padding(start = 2.dp)
                        )
                        CardSlot(
                            cardType = CardType.WEAPON,
                            equippedCard = hand.weapons.getOrNull(0),
                            onAddClick = { weaponSheetSlot = 0; weaponSheetInitialTab = 0 },
                            onCardClick = { selectedCard = it },
                            onCardLongClick = { selectedCard = it }
                        )
                        if (hand.weapons.getOrNull(0) != null) {
                            Text("Улучшение", fontSize = 9.sp, color = TextSecondary, modifier = Modifier.padding(start = 2.dp))
                            CardSlot(
                                cardType = CardType.WEAPON_UPGRADE,
                                equippedCard = hand.weaponUpgrade1,
                                onAddClick = { weaponSheetSlot = 0; weaponSheetInitialTab = 1 },
                                onCardClick = { selectedCard = it },
                                onCardLongClick = { selectedCard = it }
                            )
                        }
                    }
                    if (hand.weaponMode == WeaponMode.ONE_HANDED) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Оружие 2", fontSize = 10.sp, color = TextSecondary, modifier = Modifier.padding(start = 2.dp))
                            CardSlot(
                                cardType = CardType.WEAPON,
                                equippedCard = hand.weapons.getOrNull(1),
                                onAddClick = { weaponSheetSlot = 1; weaponSheetInitialTab = 0 },
                                onCardClick = { selectedCard = it },
                                onCardLongClick = { selectedCard = it }
                            )
                            if (hand.weapons.getOrNull(1) != null) {
                                Text("Улучшение", fontSize = 9.sp, color = TextSecondary, modifier = Modifier.padding(start = 2.dp))
                                CardSlot(
                                    cardType = CardType.WEAPON_UPGRADE,
                                    equippedCard = hand.weaponUpgrade2,
                                    onAddClick = { weaponSheetSlot = 1; weaponSheetInitialTab = 1 },
                                    onCardClick = { selectedCard = it },
                                    onCardLongClick = { selectedCard = it }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Броня", fontSize = 10.sp, color = TextSecondary, modifier = Modifier.padding(start = 2.dp))
                        CardSlot(
                            cardType = CardType.ARMOR,
                            equippedCard = hand.armor,
                            onAddClick = { armorSheetOpen = true; armorSheetInitialTab = 0 },
                            onCardClick = { selectedCard = it },
                            onCardLongClick = { selectedCard = it }
                        )
                        if (hand.armor != null) {
                            Text("Улучшение", fontSize = 9.sp, color = TextSecondary, modifier = Modifier.padding(start = 2.dp))
                            CardSlot(
                                cardType = CardType.ARMOR_UPGRADE,
                                equippedCard = hand.armorUpgrade,
                                onAddClick = { armorSheetOpen = true; armorSheetInitialTab = 1 },
                                onCardClick = { selectedCard = it },
                                onCardLongClick = { selectedCard = it }
                            )
                        }
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Диковинка", fontSize = 10.sp, color = TextSecondary, modifier = Modifier.padding(start = 2.dp))
                        CardSlot(
                            cardType = CardType.TRINKET,
                            equippedCard = hand.trinket,
                            onAddClick = { addingCardType = CardType.TRINKET },
                            onCardClick = { selectedCard = it },
                            onCardLongClick = { selectedCard = it }
                        )
                    }
                }
                } // equipmentExpanded
            }

            // Relics (collapsible, max 3)
            item {
                SectionHeader(
                    title = "РЕЛИКВИИ",
                    subtitle = "${hand.relics.size}/${CardType.RELIC.maxInHand}",
                    isExpanded = relicsExpanded,
                    onToggle = { relicsExpanded = !relicsExpanded }
                )
                if (relicsExpanded) {
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
                        if (hand.relics.size < CardType.RELIC.maxInHand) {
                            AddButton(onClick = { addingCardType = CardType.RELIC })
                        }
                    }
                }
            }

            // Accessory (collapsible, single slot)
            item {
                SectionHeader(
                    title = "АКСЕССУАР",
                    subtitle = if (hand.accessory != null) "1/1" else "0/1",
                    isExpanded = accessoryExpanded,
                    onToggle = { accessoryExpanded = !accessoryExpanded }
                )
                if (accessoryExpanded) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CardSlot(
                            cardType = CardType.ACCESSORY,
                            equippedCard = hand.accessory,
                            onAddClick = { addingCardType = CardType.ACCESSORY },
                            onCardClick = { selectedCard = it },
                            onCardLongClick = { selectedCard = it }
                        )
                    }
                }
            }

            // Backpack (collapsible)
            item {
                SectionHeader(
                    title = "РЮКЗАК",
                    subtitle = "${hand.backpack.size}/${CardType.BACKPACK.maxInHand}",
                    isExpanded = backpackExpanded,
                    onToggle = { backpackExpanded = !backpackExpanded }
                )
                if (backpackExpanded) {
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
            }

            // Discipline cards (collapsible)
            item {
                SectionHeader(
                    title = "КАРТЫ ДИСЦИПЛИН",
                    subtitle = "${hand.disciplineCards.size}/${CardType.DISCIPLINE.maxInHand}",
                    isExpanded = disciplinesExpanded,
                    onToggle = { disciplinesExpanded = !disciplinesExpanded }
                )
                if (disciplinesExpanded) {
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
                        if (hand.disciplineCards.size < CardType.DISCIPLINE.maxInHand) {
                            AddButton(onClick = { addingCardType = CardType.DISCIPLINE })
                        }
                    }
                }
            }

            // Consumables (collapsible)
            item {
                SectionHeader(
                    title = "ОДНОРАЗОВЫЕ ПРЕДМЕТЫ",
                    subtitle = "${hand.consumables.size} шт.",
                    isExpanded = consumablesExpanded,
                    onToggle = { consumablesExpanded = !consumablesExpanded }
                )
                if (consumablesExpanded) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        hand.consumables.forEach { item ->
                            CardSlot(
                                cardType = CardType.CONSUMABLE,
                                equippedCard = item,
                                onCardClick = { selectedCard = it },
                                onCardLongClick = { selectedCard = it }
                            )
                        }
                        AddButton(onClick = { addingCardType = CardType.CONSUMABLE })
                    }
                }
            }

            // Companions (collapsible)
            item {
                SectionHeader(
                    title = "СПУТНИКИ",
                    subtitle = "${hand.companions.size} шт.",
                    isExpanded = companionsExpanded,
                    onToggle = { companionsExpanded = !companionsExpanded }
                )
                if (companionsExpanded) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text("Фамильяры", fontSize = 11.sp, color = TextSecondary)
                        Text("·", fontSize = 11.sp, color = TextSecondary)
                        Text("Компаньоны", fontSize = 11.sp, color = TextSecondary)
                    }
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        hand.companions.forEach { companion ->
                            CardSlot(
                                cardType = companion.card.type,
                                equippedCard = companion,
                                onCardClick = { selectedCard = it },
                                onCardLongClick = { selectedCard = it }
                            )
                        }
                        AddButton(onClick = { addingCardType = CardType.FAMILIAR })
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    // Weapon tabbed browser
    weaponSheetSlot?.let { slotIdx ->
        TabbedCardBrowserSheet(
            tabs = listOf(
                BrowserTab(if (slotIdx == 0) "Оружие 1" else "Оружие 2", CardType.WEAPON),
                BrowserTab("Улучшение оружия", CardType.WEAPON_UPGRADE)
            ),
            selectedCards = listOf(
                hand.weapons.getOrNull(slotIdx)?.card,
                if (slotIdx == 0) hand.weaponUpgrade1?.card else hand.weaponUpgrade2?.card
            ),
            initialTabIndex = weaponSheetInitialTab,
            cardRepository = viewModel.cardRepository,
            onCardSelected = { tabIdx, card ->
                when (tabIdx) {
                    0 -> viewModel.setWeapon(slotIdx, card)
                    1 -> viewModel.setWeaponUpgrade(slotIdx, card)
                }
            },
            onDismiss = { weaponSheetSlot = null }
        )
    }

    if (armorSheetOpen) {
        TabbedCardBrowserSheet(
            tabs = listOf(
                BrowserTab("Броня", CardType.ARMOR),
                BrowserTab("Улучшение брони", CardType.ARMOR_UPGRADE)
            ),
            selectedCards = listOf(hand.armor?.card, hand.armorUpgrade?.card),
            initialTabIndex = armorSheetInitialTab,
            cardRepository = viewModel.cardRepository,
            onCardSelected = { tabIdx, card ->
                when (tabIdx) {
                    0 -> viewModel.setArmor(card)
                    1 -> viewModel.setArmorUpgrade(card)
                }
            },
            onDismiss = { armorSheetOpen = false }
        )
    }

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

    selectedCard?.let { equipped ->
        CardOptionsSheet(
            equippedCard = equipped,
            onDischarge = {
                viewModel.dischargeCard(equipped.instanceId)
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
    onDischarge: () -> Unit,
    onFlip: () -> Unit,
    onRemove: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isCharged = equippedCard.rotationDegrees == 0f

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
                    Text(equippedCard.card.type.displayName, fontSize = 12.sp, color = TextSecondary)
                    Text(equippedCard.card.description, fontSize = 13.sp, color = TextSecondary, lineHeight = 18.sp)
                    CardStatsSummary(card = equippedCard.card)
                    if (equippedCard.rotationDegrees != 0f || equippedCard.isFlipped) {
                        Text(
                            buildString {
                                if (equippedCard.rotationDegrees != 0f) append("Разряжена")
                                if (equippedCard.isFlipped) {
                                    if (isNotEmpty()) append("  ")
                                    append("Перевёрнута")
                                }
                            },
                            fontSize = 11.sp, color = Color(0xFFE57373)
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
                    onClick = onDischarge,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (isCharged) GoldAccent else Color(0xFF66BB6A)
                    ),
                    border = BorderStroke(
                        1.dp,
                        SolidColor(if (isCharged) GoldAccent.copy(alpha = 0.5f) else Color(0xFF66BB6A).copy(alpha = 0.5f))
                    )
                ) {
                    Text(
                        if (isCharged) "Разрядить" else "Зарядить",
                        fontSize = 12.sp, textAlign = TextAlign.Center
                    )
                }
                OutlinedButton(
                    onClick = onFlip,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldAccent),
                    border = BorderStroke(1.dp, SolidColor(GoldAccent.copy(alpha = 0.5f)))
                ) {
                    Text(
                        if (equippedCard.isFlipped) "Вернуть\nлицом" else "Перевернуть",
                        fontSize = 12.sp, textAlign = TextAlign.Center
                    )
                }
                Button(
                    onClick = onRemove,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B1A1A))
                ) {
                    Text("Убрать\nкарту", fontSize = 12.sp, textAlign = TextAlign.Center)
                }
            }
        }
    }
}
