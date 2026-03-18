package com.middara.helper.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.middara.helper.ui.theme.DarkCardBorder
import com.middara.helper.ui.theme.GoldAccent
import com.middara.helper.ui.theme.TextSecondary

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    isExpanded: Boolean? = null,
    onToggle: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val collapsible = onToggle != null && isExpanded != null
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (collapsible) Modifier.clickable(role = Role.Button, onClick = onToggle!!)
                    else Modifier
                )
                .padding(vertical = 2.dp)
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = GoldAccent,
                letterSpacing = 1.sp
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = subtitle, fontSize = 11.sp, color = TextSecondary)
            }
            Spacer(modifier = Modifier.weight(1f))
            if (collapsible && isExpanded != null) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Свернуть" else "Развернуть",
                    tint = GoldAccent,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider(color = DarkCardBorder, thickness = 0.5.dp)
    }
}
