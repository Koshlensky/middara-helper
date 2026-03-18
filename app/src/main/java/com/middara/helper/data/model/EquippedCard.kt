package com.middara.helper.data.model

import java.util.UUID

data class EquippedCard(
    val instanceId: String = UUID.randomUUID().toString(),
    val card: Card,
    val rotationDegrees: Float = 0f,
    val isFlipped: Boolean = false
)

fun EquippedCard.nextRotation(): EquippedCard =
    copy(rotationDegrees = (rotationDegrees + 90f) % 360f)

fun EquippedCard.toggleFlip(): EquippedCard =
    copy(isFlipped = !isFlipped)
