/*
 * Copyright (C) 2022 Patrick Goldinger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.patrickgold.florisboard.ime.media

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.consumeDownChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.app.prefs.florisPreferenceModel
import dev.patrickgold.florisboard.ime.core.InputEventDispatcher
import dev.patrickgold.florisboard.ime.core.InputKeyEvent
import dev.patrickgold.florisboard.ime.keyboard.FlorisImeSizing
import dev.patrickgold.florisboard.ime.keyboard.KeyData
import dev.patrickgold.florisboard.ime.media.emoji.EmojiPaletteView
import dev.patrickgold.florisboard.ime.media.emoji.parseRawEmojiSpecsFile
import dev.patrickgold.florisboard.ime.text.keyboard.TextKeyData
import dev.patrickgold.florisboard.ime.text.smartbar.SecondaryRowPlacement
import dev.patrickgold.florisboard.ime.theme.FlorisImeTheme
import dev.patrickgold.florisboard.ime.theme.FlorisImeUi
import dev.patrickgold.florisboard.keyboardManager
import dev.patrickgold.florisboard.snygg.ui.SnyggSurface
import dev.patrickgold.jetpref.datastore.model.observeAsState
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MediaInputLayout(
    modifier: Modifier = Modifier,
) {
    val prefs by florisPreferenceModel()
    val context = LocalContext.current
    val keyboardManager by context.keyboardManager()

    val smartbarEnabled by prefs.smartbar.enabled.observeAsState()
    val secondaryRowEnabled by prefs.smartbar.secondaryActionsEnabled.observeAsState()
    val secondaryRowExpanded by prefs.smartbar.secondaryActionsExpanded.observeAsState()
    val secondaryRowPlacement by prefs.smartbar.secondaryActionsPlacement.observeAsState()
    val height =
        if (smartbarEnabled) {
            if (secondaryRowEnabled && secondaryRowExpanded &&
                secondaryRowPlacement != SecondaryRowPlacement.OVERLAY_APP_UI) {
                FlorisImeSizing.smartbarHeight * 2
            } else {
                FlorisImeSizing.smartbarHeight
            }
        } else {
            0.dp
        } + (FlorisImeSizing.keyboardRowBaseHeight * 4)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
    ) {
        EmojiPaletteView(
            modifier = Modifier.weight(1f),
            fullEmojiMappings = parseRawEmojiSpecsFile(context, "ime/media/emoji/root.txt"),
        )
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(FlorisImeSizing.keyboardRowBaseHeight * 0.8f),
            ) {
                KeyboardLikeButton(
                    inputEventDispatcher = keyboardManager.inputEventDispatcher,
                    keyData = TextKeyData.IME_UI_MODE_TEXT,
                ) {
                    Text(
                        text = "ABC",
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                KeyboardLikeButton(
                    inputEventDispatcher = keyboardManager.inputEventDispatcher,
                    keyData = TextKeyData.DELETE,
                ) {
                    Icon(painter = painterResource(R.drawable.ic_backspace), contentDescription = null)
                }
            }
        }
    }
}

@Composable
internal fun KeyboardLikeButton(
    modifier: Modifier = Modifier,
    inputEventDispatcher: InputEventDispatcher,
    keyData: KeyData,
    content: @Composable RowScope.() -> Unit,
) {
    var isPressed by remember { mutableStateOf(false) }
    val keyStyle = FlorisImeTheme.style.get(
        element = FlorisImeUi.EmojiKey,
        code = keyData.code,
        isPressed = isPressed,
    )
    SnyggSurface(
        modifier = modifier.pointerInput(Unit) {
            forEachGesture {
                coroutineScope {
                    awaitPointerEventScope {
                        awaitFirstDown(requireUnconsumed = false).also { it.consumeDownChange() }
                        isPressed = true
                        inputEventDispatcher.send(InputKeyEvent.down(keyData))
                        val repeatedAction = launch {
                            delay(300)
                            inputEventDispatcher.send(InputKeyEvent.repeat(keyData))
                        }
                        val up = waitForUpOrCancellation()
                        repeatedAction.cancel()
                        isPressed = false
                        if (up != null) {
                            inputEventDispatcher.send(InputKeyEvent.up(keyData))
                        } else {
                            inputEventDispatcher.send(InputKeyEvent.cancel(keyData))
                        }
                    }
                }
            }
        },
        style = keyStyle,
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
    }
}
