package com.middara.helper.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.middara.helper.data.model.HeroHand
import com.middara.helper.ui.components.CardImageContent
import com.middara.helper.ui.theme.*
import com.middara.helper.ui.viewmodel.HeroListViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HeroListScreen(
    onHeroClick: (String) -> Unit,
    viewModel: HeroListViewModel = viewModel()
) {
    val heroes by viewModel.heroes.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var heroToDelete by remember { mutableStateOf<HeroHand?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Middara Helper",
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = GoldAccent,
                contentColor = Color(0xFF1A1A00)
            ) {
                Icon(Icons.Default.Add, "Создать героя")
            }
        },
        containerColor = DarkBackground
    ) { padding ->
        if (heroes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = "Нет персонажей",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary
                    )
                    Text(
                        text = "Нажмите + чтобы создать персонажа",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(heroes, key = { it.id }) { hero ->
                    HeroListItem(
                        hero = hero,
                        onClick = { onHeroClick(hero.id) },
                        onDeleteClick = { heroToDelete = hero }
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateHeroDialog(
            onConfirm = { name ->
                viewModel.createHero(name)
                showCreateDialog = false
            },
            onDismiss = { showCreateDialog = false }
        )
    }

    heroToDelete?.let { hero ->
        AlertDialog(
            onDismissRequest = { heroToDelete = null },
            title = { Text("Удалить персонажа?") },
            text = { Text("Удалить «${hero.heroName}»? Это действие нельзя отменить.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteHero(hero.id)
                    heroToDelete = null
                }) {
                    Text("Удалить", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { heroToDelete = null }) {
                    Text("Отмена")
                }
            },
            containerColor = DarkSurface
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HeroListItem(
    hero: HeroHand,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkCard, shape)
            .combinedClickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(CardColorHeroSheet, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (hero.heroSheet != null) {
                    CardImageContent(
                        card = hero.heroSheet.card,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Column {
                Text(
                    text = hero.heroName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                val details = buildList {
                    val weaponCount = hero.weapons.size
                    if (weaponCount > 0) add("Оружие: $weaponCount")
                    if (hero.armor != null) add("Броня")
                    val discCount = hero.disciplineCards.size
                    if (discCount > 0) add("Дисц.: $discCount")
                }
                if (details.isNotEmpty()) {
                    Text(
                        text = details.joinToString(" · "),
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                } else {
                    Text(
                        text = "Пустая рука",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }
        IconButton(onClick = onDeleteClick) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Удалить",
                tint = TextSecondary
            )
        }
    }
}

@Composable
private fun CreateHeroDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новый персонаж", color = GoldAccent) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Имя персонажа") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldAccent,
                    focusedLabelColor = GoldAccent,
                    cursorColor = GoldAccent
                )
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name) },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
            ) {
                Text("Создать", color = Color(0xFF1A1A00))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        },
        containerColor = DarkSurface
    )
}
