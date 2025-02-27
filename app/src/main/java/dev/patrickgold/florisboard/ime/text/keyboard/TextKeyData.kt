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

package dev.patrickgold.florisboard.ime.text.keyboard

import dev.patrickgold.florisboard.common.FlorisLocale
import dev.patrickgold.florisboard.common.kotlin.lowercase
import dev.patrickgold.florisboard.common.kotlin.uppercase
import dev.patrickgold.florisboard.ime.keyboard.AbstractKeyData
import dev.patrickgold.florisboard.ime.keyboard.ComputingEvaluator
import dev.patrickgold.florisboard.ime.keyboard.KeyData
import dev.patrickgold.florisboard.ime.popup.PopupSet
import dev.patrickgold.florisboard.ime.text.key.KeyCode
import dev.patrickgold.florisboard.ime.text.key.KeyType
import kotlinx.serialization.*

/**
 * Data class which describes a single key and its attributes.
 *
 * @property type The type of the key. Some actions require both [code] and [type] to match in order
 *  to be successfully executed. Defaults to [KeyType.CHARACTER].
 * @property code The UTF-8 encoded code of the character. The code defined here is used as the
 *  data passed to the system. Defaults to 0.
 * @property label The string used to display the key in the UI. Is not used for the actual data
 *  passed to the system. Should normally be the exact same as the [code]. Defaults to an empty
 *  string.
 */
@Serializable
@SerialName("text_key")
class TextKeyData(
    override val type: KeyType = KeyType.CHARACTER,
    override val code: Int = KeyCode.UNSPECIFIED,
    override val label: String = "",
    override val groupId: Int = KeyData.GROUP_DEFAULT,
    override val popup: PopupSet<AbstractKeyData>? = null
) : KeyData {
    override fun compute(evaluator: ComputingEvaluator): KeyData? {
        return if (evaluator.isSlot(this)) {
            evaluator.slotData(this)?.let { data ->
                TextKeyData(data.type, data.code, data.label, groupId, popup)
            }
        } else {
            this
        }
    }

    override fun asString(isForDisplay: Boolean): String {
        return buildString {
            if (isForDisplay || code == KeyCode.URI_COMPONENT_TLD || code < KeyCode.SPACE) {
                // Combining Diacritical Marks
                // See: https://en.wikipedia.org/wiki/Combining_Diacritical_Marks
                if (code in 0x0300..0x036F && !label.startsWith("◌")) {
                    append("◌")
                }
                append(label)
            } else {
                try { appendCodePoint(code) } catch (_: Throwable) { }
            }
        }
    }

    override fun toString(): String {
        return "${TextKeyData::class.simpleName} { type=$type code=$code label=\"$label\" groupId=$groupId }"
    }

    @Suppress("UNUSED")
    companion object {
        /** Predefined key data for [KeyCode.UNSPECIFIED] */
        val UNSPECIFIED = TextKeyData(
            type = KeyType.UNSPECIFIED,
            code = KeyCode.UNSPECIFIED,
            label = "unspecified",
        )

        /** Predefined key data for [KeyCode.SPACE] */
        val SPACE = TextKeyData(
            type = KeyType.CHARACTER,
            code = KeyCode.SPACE,
            label = "space",
        )

        /** Predefined key data for [KeyCode.CTRL] */
        val CTRL = TextKeyData(
            type = KeyType.MODIFIER,
            code = KeyCode.CTRL,
            label = "ctrl",
        )
        /** Predefined key data for [KeyCode.CTRL_LOCK] */
        val CTRL_LOCK = TextKeyData(
            type = KeyType.MODIFIER,
            code = KeyCode.CTRL_LOCK,
            label = "ctrl_lock",
        )
        /** Predefined key data for [KeyCode.ALT] */
        val ALT = TextKeyData(
            type = KeyType.MODIFIER,
            code = KeyCode.ALT,
            label = "alt",
        )
        /** Predefined key data for [KeyCode.ALT_LOCK] */
        val ALT_LOCK = TextKeyData(
            type = KeyType.MODIFIER,
            code = KeyCode.ALT_LOCK,
            label = "alt_lock",
        )
        /** Predefined key data for [KeyCode.FN] */
        val FN = TextKeyData(
            type = KeyType.MODIFIER,
            code = KeyCode.FN,
            label = "fn",
        )
        /** Predefined key data for [KeyCode.FN_LOCK] */
        val FN_LOCK = TextKeyData(
            type = KeyType.MODIFIER,
            code = KeyCode.FN_LOCK,
            label = "fn_lock",
        )
        /** Predefined key data for [KeyCode.DELETE] */
        val DELETE = TextKeyData(
            type = KeyType.ENTER_EDITING,
            code = KeyCode.DELETE,
            label = "delete",
        )
        /** Predefined key data for [KeyCode.DELETE_WORD] */
        val DELETE_WORD = TextKeyData(
            type = KeyType.ENTER_EDITING,
            code = KeyCode.DELETE_WORD,
            label = "delete_word",
        )
        /** Predefined key data for [KeyCode.FORWARD_DELETE] */
        val FORWARD_DELETE = TextKeyData(
            type = KeyType.ENTER_EDITING,
            code = KeyCode.FORWARD_DELETE,
            label = "forward_delete",
        )
        /** Predefined key data for [KeyCode.FORWARD_DELETE_WORD] */
        val FORWARD_DELETE_WORD = TextKeyData(
            type = KeyType.ENTER_EDITING,
            code = KeyCode.FORWARD_DELETE_WORD,
            label = "forward_delete_word",
        )
        /** Predefined key data for [KeyCode.SHIFT] */
        val SHIFT = TextKeyData(
            type = KeyType.MODIFIER,
            code = KeyCode.SHIFT,
            label = "shift",
        )
        /** Predefined key data for [KeyCode.SHIFT_LOCK] */
        val SHIFT_LOCK = TextKeyData(
            type = KeyType.MODIFIER,
            code = KeyCode.SHIFT_LOCK,
            label = "shift_lock",
        )
        /** Predefined key data for [KeyCode.CAPS_LOCK] */
        val CAPS_LOCK = TextKeyData(
            type = KeyType.MODIFIER,
            code = KeyCode.CAPS_LOCK,
            label = "caps_lock",
        )

        /** Predefined key data for [KeyCode.ARROW_LEFT] */
        val ARROW_LEFT = TextKeyData(
            type = KeyType.NAVIGATION,
            code = KeyCode.ARROW_LEFT,
            label = "arrow_left",
        )
        /** Predefined key data for [KeyCode.ARROW_RIGHT] */
        val ARROW_RIGHT = TextKeyData(
            type = KeyType.NAVIGATION,
            code = KeyCode.ARROW_RIGHT,
            label = "arrow_right",
        )
        /** Predefined key data for [KeyCode.ARROW_UP] */
        val ARROW_UP = TextKeyData(
            type = KeyType.NAVIGATION,
            code = KeyCode.ARROW_UP,
            label = "arrow_up",
        )
        /** Predefined key data for [KeyCode.ARROW_DOWN] */
        val ARROW_DOWN = TextKeyData(
            type = KeyType.NAVIGATION,
            code = KeyCode.ARROW_DOWN,
            label = "arrow_down",
        )
        /** Predefined key data for [KeyCode.MOVE_START_OF_PAGE] */
        val MOVE_START_OF_PAGE = TextKeyData(
            type = KeyType.NAVIGATION,
            code = KeyCode.MOVE_START_OF_PAGE,
            label = "move_start_of_page",
        )
        /** Predefined key data for [KeyCode.MOVE_END_OF_PAGE] */
        val MOVE_END_OF_PAGE = TextKeyData(
            type = KeyType.NAVIGATION,
            code = KeyCode.MOVE_END_OF_PAGE,
            label = "move_end_of_page",
        )
        /** Predefined key data for [KeyCode.MOVE_START_OF_LINE] */
        val MOVE_START_OF_LINE = TextKeyData(
            type = KeyType.NAVIGATION,
            code = KeyCode.MOVE_START_OF_LINE,
            label = "move_start_of_line",
        )
        /** Predefined key data for [KeyCode.MOVE_END_OF_LINE] */
        val MOVE_END_OF_LINE = TextKeyData(
            type = KeyType.NAVIGATION,
            code = KeyCode.MOVE_END_OF_LINE,
            label = "move_end_of_line",
        )

        /** Predefined key data for [KeyCode.CLIPBOARD_COPY] */
        val CLIPBOARD_COPY = TextKeyData(
            type = KeyType.SYSTEM_GUI,
            code = KeyCode.CLIPBOARD_COPY,
            label = "clipboard_copy",
        )
        /** Predefined key data for [KeyCode.CLIPBOARD_CUT] */
        val CLIPBOARD_CUT = TextKeyData(
            type = KeyType.SYSTEM_GUI,
            code = KeyCode.CLIPBOARD_CUT,
            label = "clipboard_cut",
        )
        /** Predefined key data for [KeyCode.CLIPBOARD_PASTE] */
        val CLIPBOARD_PASTE = TextKeyData(
            type = KeyType.SYSTEM_GUI,
            code = KeyCode.CLIPBOARD_PASTE,
            label = "clipboard_paste",
        )
        /** Predefined key data for [KeyCode.CLIPBOARD_SELECT] */
        val CLIPBOARD_SELECT = TextKeyData(
            type = KeyType.SYSTEM_GUI,
            code = KeyCode.CLIPBOARD_SELECT,
            label = "clipboard_select",
        )
        /** Predefined key data for [KeyCode.CLIPBOARD_SELECT_ALL] */
        val CLIPBOARD_SELECT_ALL = TextKeyData(
            type = KeyType.SYSTEM_GUI,
            code = KeyCode.CLIPBOARD_SELECT_ALL,
            label = "clipboard_select_all",
        )
        /** Predefined key data for [KeyCode.CLIPBOARD_CLEAR_HISTORY] */
        val CLIPBOARD_CLEAR_HISTORY = TextKeyData(
            type = KeyType.SYSTEM_GUI,
            code = KeyCode.CLIPBOARD_CLEAR_HISTORY,
            label = "clipboard_clear_history",
        )
        /** Predefined key data for [KeyCode.CLIPBOARD_CLEAR_FULL_HISTORY] */
        val CLIPBOARD_CLEAR_FULL_HISTORY = TextKeyData(
            type = KeyType.SYSTEM_GUI,
            code = KeyCode.CLIPBOARD_CLEAR_FULL_HISTORY,
            label = "clipboard_clear_full_history",
        )
        /** Predefined key data for [KeyCode.CLIPBOARD_CLEAR_PRIMARY_CLIP] */
        val CLIPBOARD_CLEAR_PRIMARY_CLIP = TextKeyData(
            type = KeyType.SYSTEM_GUI,
            code = KeyCode.CLIPBOARD_CLEAR_PRIMARY_CLIP,
            label = "clipboard_clear_primary_clip",
        )

        /** Predefined key data for [KeyCode.COMPACT_LAYOUT_TO_LEFT] */
        val COMPACT_LAYOUT_TO_LEFT = TextKeyData(
            type = KeyType.SYSTEM_GUI,
            code = KeyCode.COMPACT_LAYOUT_TO_LEFT,
            label = "compact_layout_to_left",
        )
        /** Predefined key data for [KeyCode.COMPACT_LAYOUT_TO_RIGHT] */
        val COMPACT_LAYOUT_TO_RIGHT = TextKeyData(
            type = KeyType.SYSTEM_GUI,
            code = KeyCode.COMPACT_LAYOUT_TO_RIGHT,
            label = "compact_layout_to_right",
        )

        /** Predefined key data for [KeyCode.UNDO] */
        val UNDO = TextKeyData(
            type = KeyType.SYSTEM_GUI,
            code = KeyCode.UNDO,
            label = "undo",
        )
        /** Predefined key data for [KeyCode.REDO] */
        val REDO = TextKeyData(
            type = KeyType.SYSTEM_GUI,
            code = KeyCode.REDO,
            label = "redo",
        )

        /** Predefined key data for [KeyCode.VIEW_CHARACTERS] */
        val VIEW_CHARACTERS = TextKeyData(
            type = KeyType.SYSTEM_GUI,
            code = KeyCode.VIEW_CHARACTERS,
            label = "view_characters",
        )
        /** Predefined key data for [KeyCode.VIEW_SYMBOLS] */
        val VIEW_SYMBOLS = TextKeyData(
            type = KeyType.SYSTEM_GUI,
            code = KeyCode.VIEW_SYMBOLS,
            label = "view_symbols",
        )
        /** Predefined key data for [KeyCode.VIEW_SYMBOLS2] */
        val VIEW_SYMBOLS2 = TextKeyData(
            type = KeyType.SYSTEM_GUI,
            code = KeyCode.VIEW_SYMBOLS2,
            label = "view_symbols2",
        )
        /** Predefined key data for [KeyCode.VIEW_NUMERIC_ADVANCED] */
        val VIEW_NUMERIC_ADVANCED = TextKeyData(
            type = KeyType.SYSTEM_GUI,
            code = KeyCode.VIEW_NUMERIC_ADVANCED,
            label = "view_numeric_advanced",
        )

        /** Predefined key data for [KeyCode.IME_UI_MODE_TEXT] */
        val IME_UI_MODE_TEXT = TextKeyData(
            type = KeyType.SYSTEM_GUI,
            code = KeyCode.IME_UI_MODE_TEXT,
            label = "ime_ui_mode_text",
        )
        /** Predefined key data for [KeyCode.IME_UI_MODE_MEDIA] */
        val IME_UI_MODE_MEDIA = TextKeyData(
            type = KeyType.SYSTEM_GUI,
            code = KeyCode.IME_UI_MODE_MEDIA,
            label = "ime_ui_mode_media",
        )
        /** Predefined key data for [KeyCode.IME_UI_MODE_CLIPBOARD] */
        val IME_UI_MODE_CLIPBOARD = TextKeyData(
            type = KeyType.SYSTEM_GUI,
            code = KeyCode.IME_UI_MODE_CLIPBOARD,
            label = "ime_ui_mode_clipboard",
        )

        /** Predefined key data for [KeyCode.SYSTEM_INPUT_METHOD_PICKER] */
        val SYSTEM_INPUT_METHOD_PICKER = TextKeyData(
            type = KeyType.FUNCTION,
            code = KeyCode.SYSTEM_INPUT_METHOD_PICKER,
            label = "system_input_method_picker",
        )
        /** Predefined key data for [KeyCode.SYSTEM_PREV_INPUT_METHOD] */
        val SYSTEM_PREV_INPUT_METHOD = TextKeyData(
            type = KeyType.FUNCTION,
            code = KeyCode.SYSTEM_PREV_INPUT_METHOD,
            label = "system_prev_input_method",
        )
        /** Predefined key data for [KeyCode.SYSTEM_NEXT_INPUT_METHOD] */
        val SYSTEM_NEXT_INPUT_METHOD = TextKeyData(
            type = KeyType.FUNCTION,
            code = KeyCode.SYSTEM_NEXT_INPUT_METHOD,
            label = "system_next_input_method",
        )
        /** Predefined key data for [KeyCode.IME_SUBTYPE_PICKER] */
        val IME_SUBTYPE_PICKER = TextKeyData(
            type = KeyType.FUNCTION,
            code = KeyCode.IME_SUBTYPE_PICKER,
            label = "ime_subtype_picker",
        )
        /** Predefined key data for [KeyCode.IME_PREV_SUBTYPE] */
        val IME_PREV_SUBTYPE = TextKeyData(
            type = KeyType.FUNCTION,
            code = KeyCode.IME_PREV_SUBTYPE,
            label = "ime_prev_subtype",
        )
        /** Predefined key data for [KeyCode.IME_NEXT_SUBTYPE] */
        val IME_NEXT_SUBTYPE = TextKeyData(
            type = KeyType.FUNCTION,
            code = KeyCode.IME_NEXT_SUBTYPE,
            label = "ime_next_subtype",
        )

        /** Predefined key data for [KeyCode.IME_SHOW_UI] */
        val IME_SHOW_UI = TextKeyData(
            type = KeyType.FUNCTION,
            code = KeyCode.IME_SHOW_UI,
            label = "ime_show_ui",
        )
        /** Predefined key data for [KeyCode.IME_HIDE_UI] */
        val IME_HIDE_UI = TextKeyData(
            type = KeyType.FUNCTION,
            code = KeyCode.IME_HIDE_UI,
            label = "ime_hide_ui",
        )

        /** Predefined key data for [KeyCode.SETTINGS] */
        val SETTINGS = TextKeyData(
            type = KeyType.CHARACTER,
            code = KeyCode.SETTINGS,
            label = "settings",
        )

        /** Predefined key data for [KeyCode.INTERNAL_BATCH_EDIT] */
        val INTERNAL_BATCH_EDIT = TextKeyData(
            type = KeyType.FUNCTION,
            code = KeyCode.INTERNAL_BATCH_EDIT,
            label = "internal_batch_edit",
        )
    }
}

@Serializable
@SerialName("auto_text_key")
class AutoTextKeyData(
    override val type: KeyType = KeyType.CHARACTER,
    override val code: Int = KeyCode.UNSPECIFIED,
    override val label: String = "",
    override val groupId: Int = KeyData.GROUP_DEFAULT,
    override val popup: PopupSet<AbstractKeyData>? = null
) : KeyData {
    @Transient private val lower: TextKeyData =
        TextKeyData(type, Character.toLowerCase(code), label.lowercase(FlorisLocale.default()), groupId, popup)
    @Transient private val upper: TextKeyData =
        TextKeyData(type, Character.toUpperCase(code), label.uppercase(FlorisLocale.default()), groupId, popup)

    override fun compute(evaluator: ComputingEvaluator): KeyData? {
        return if (evaluator.isSlot(this)) {
            evaluator.slotData(this)?.let { data ->
                TextKeyData(data.type, data.code, data.label, groupId, popup)
            }
        } else {
            if (evaluator.activeState().isUppercase) { upper } else { lower }
        }
    }

    override fun asString(isForDisplay: Boolean): String {
        return buildString {
            if (isForDisplay || code == KeyCode.URI_COMPONENT_TLD || code < KeyCode.SPACE) {
                // Combining Diacritical Marks
                // See: https://en.wikipedia.org/wiki/Combining_Diacritical_Marks
                if (code in 0x0300..0x036F && !label.startsWith("◌")) {
                    append("◌")
                }
                append(label)
            } else {
                try { appendCodePoint(code) } catch (_: Throwable) { }
            }
        }
    }

    override fun toString(): String {
        return "${AutoTextKeyData::class.simpleName} { type=$type code=$code label=\"$label\" groupId=$groupId }"
    }
}

@Serializable
@SerialName("multi_text_key")
class MultiTextKeyData(
    override val type: KeyType = KeyType.CHARACTER,
    val codePoints: IntArray = intArrayOf(),
    override val label: String = "",
    override val groupId: Int = KeyData.GROUP_DEFAULT,
    override val popup: PopupSet<AbstractKeyData>? = null
) : KeyData {
    @Transient override val code: Int = KeyCode.MULTIPLE_CODE_POINTS

    override fun compute(evaluator: ComputingEvaluator): KeyData {
        return this
    }

    override fun asString(isForDisplay: Boolean): String {
        return buildString {
            if (isForDisplay) {
                append(label)
            } else {
                for (codePoint in codePoints) {
                    try { appendCodePoint(codePoint) } catch (_: Throwable) { }
                }
            }
        }
    }

    override fun toString(): String {
        return "${MultiTextKeyData::class.simpleName} { type=$type code=$code label=\"$label\" groupId=$groupId }"
    }
}
