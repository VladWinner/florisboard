/*
 * Copyright (C) 2021 Patrick Goldinger
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

package dev.patrickgold.florisboard.app.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.app.LocalNavController
import dev.patrickgold.florisboard.app.res.stringRes
import dev.patrickgold.florisboard.app.ui.Routes
import dev.patrickgold.florisboard.app.ui.components.FlorisErrorCard
import dev.patrickgold.florisboard.app.ui.components.FlorisScreen
import dev.patrickgold.florisboard.app.ui.components.FlorisWarningCard
import dev.patrickgold.florisboard.common.InputMethodUtils
import dev.patrickgold.jetpref.datastore.model.observeAsState
import dev.patrickgold.jetpref.datastore.ui.Preference

@Composable
fun HomeScreen() = FlorisScreen {
    title = stringRes(R.string.settings__home__title)
    navigationIconVisible = false
    previewFieldVisible = true

    val navController = LocalNavController.current
    val context = LocalContext.current

    content {
        val isCollapsed by prefs.internal.homeIsBetaToolboxCollapsed.observeAsState()

        val isFlorisBoardEnabled by InputMethodUtils.observeIsFlorisboardEnabled(foregroundOnly = true)
        val isFlorisBoardSelected by InputMethodUtils.observeIsFlorisboardSelected(foregroundOnly = true)
        if (!isFlorisBoardEnabled) {
            FlorisErrorCard(
                modifier = Modifier.padding(8.dp),
                showIcon = false,
                text = stringRes(R.string.settings__home__ime_not_enabled),
                onClick = { InputMethodUtils.showImeEnablerActivity(context) },
            )
        } else if (!isFlorisBoardSelected) {
            FlorisWarningCard(
                modifier = Modifier.padding(8.dp),
                showIcon = false,
                text = stringRes(R.string.settings__home__ime_not_selected),
                onClick = { InputMethodUtils.showImePicker(context) },
            )
        }

        Card(modifier = Modifier.padding(8.dp)) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Note on the new Settings UI",
                        style = MaterialTheme.typography.subtitle1,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.weight(1.0f))
                    IconButton(onClick = { this@content.prefs.internal.homeIsBetaToolboxCollapsed.set(!isCollapsed) }) {
                        Icon(
                            painter = painterResource(if (isCollapsed) {
                                R.drawable.ic_keyboard_arrow_down
                            } else {
                                R.drawable.ic_keyboard_arrow_up
                            }),
                            contentDescription = null,
                        )
                    }
                }
                if (!isCollapsed) {
                    Text("Welcome to the new Settings of FlorisBoard!\n")
                    Text("It has been quite a long time since 0.3.13, but since then a lot has changed. FlorisBoard has undergone a major overhaul and now uses a completely new UI library, backend logic and also the Settings have been completely revamped. A big thanks goes to all my beta testers who continuously provided feedback and tested things out, this made the development much more interactive and better!\n")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Note that this release does not contain support for word suggestions (will show the current word plus numbers as a placeholder).", color = Color.Red)
                    Text("Please DO NOT file an issue for this. It is already more than known and a major goal for implementation in 0.4.0. Thank you!\n")
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
        Preference(
            iconId = R.drawable.ic_language,
            title = stringRes(R.string.settings__localization__title),
            onClick = { navController.navigate(Routes.Settings.Localization) },
        )
        Preference(
            iconId = R.drawable.ic_palette,
            title = stringRes(R.string.settings__theme__title),
            onClick = { navController.navigate(Routes.Settings.Theme) },
        )
        Preference(
            iconId = R.drawable.ic_keyboard,
            title = stringRes(R.string.settings__keyboard__title),
            onClick = { navController.navigate(Routes.Settings.Keyboard) },
        )
        Preference(
            iconId = R.drawable.ic_smartbar,
            title = stringRes(R.string.settings__smartbar__title),
            onClick = { navController.navigate(Routes.Settings.Smartbar) },
        )
        Preference(
            iconId = R.drawable.ic_settings_suggest,
            title = stringRes(R.string.settings__typing__title),
            onClick = { navController.navigate(Routes.Settings.Typing) },
        )
        Preference(
            iconId = R.drawable.ic_spellcheck,
            title = stringRes(R.string.settings__spelling__title),
            onClick = { navController.navigate(Routes.Settings.Spelling) },
        )
        Preference(
            iconId = R.drawable.ic_library_books,
            title = stringRes(R.string.settings__dictionary__title),
            onClick = { navController.navigate(Routes.Settings.Dictionary) },
        )
        Preference(
            iconId = R.drawable.ic_gesture,
            title = stringRes(R.string.settings__gestures__title),
            onClick = { navController.navigate(Routes.Settings.Gestures) },
        )
        Preference(
            iconId = R.drawable.ic_assignment,
            title = stringRes(R.string.settings__clipboard__title),
            onClick = { navController.navigate(Routes.Settings.Clipboard) },
        )
        Preference(
            iconId = R.drawable.ic_sentiment_satisfied,
            title = stringRes(R.string.settings__media__title),
            onClick = { navController.navigate(Routes.Settings.Media) },
        )
        Preference(
            iconId = R.drawable.ic_adb,
            title = stringRes(R.string.devtools__title),
            onClick = { navController.navigate(Routes.Devtools.Home) },
        )
        Preference(
            iconId = R.drawable.ic_build,
            title = stringRes(R.string.settings__advanced__title),
            onClick = { navController.navigate(Routes.Settings.Advanced) },
        )
        Preference(
            iconId = R.drawable.ic_info,
            title = stringRes(R.string.about__title),
            onClick = { navController.navigate(Routes.Settings.About) },
        )
    }
}
