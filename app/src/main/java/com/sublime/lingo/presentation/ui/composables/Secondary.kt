package com.sublime.lingo.presentation.ui.composables

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sublime.lingo.presentation.ui.theme.Pink80

@Suppress("ktlint:standard:function-naming")
@Composable
fun TypingIndicatorItem(
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp, top = 4.dp),
    ) {
        ChatBubble(
            backgroundColor = Pink80.copy(alpha = 0.3f),
            contentColor = Color.Black,
            cornerRadius = 16.dp,
            isDarkTheme,
        ) {
            TypingIndicator()
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun ScrollToBottomButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = Pink80,
        modifier =
            modifier
                .padding(18.dp)
                .size(36.dp)
                .clip(RoundedCornerShape(28.dp)),
    ) {
        Icon(
            imageVector = Icons.Filled.KeyboardArrowDown,
            contentDescription = "Scroll to bottom",
            tint = Color.White,
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun TypingIndicator(modifier: Modifier = Modifier) {
    Row(
        modifier =
            modifier
                .widthIn(max = 70.dp)
                .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(3) { index ->
            AnimatedDot()
            if (index < 2) Spacer(modifier = Modifier.width(4.dp))
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun AnimatedDot(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(500, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "Animated Dots",
    )

    Box(
        modifier =
            modifier
                .size(8.dp)
                .scale(scale)
                .background(Color.Gray, CircleShape),
    )
}
