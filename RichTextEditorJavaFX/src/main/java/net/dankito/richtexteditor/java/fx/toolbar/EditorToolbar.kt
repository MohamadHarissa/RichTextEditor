package net.dankito.richtexteditor.java.fx.toolbar

import javafx.geometry.Pos
import javafx.scene.layout.HBox
import javafx.scene.layout.Region
import net.dankito.richtexteditor.Color
import net.dankito.richtexteditor.Icon
import net.dankito.richtexteditor.command.SetColorCommand
import net.dankito.richtexteditor.command.ToolbarCommand
import net.dankito.richtexteditor.command.ToolbarCommandStyle
import net.dankito.richtexteditor.java.fx.JavaFXCommandView
import net.dankito.richtexteditor.java.fx.RichTextEditor
import net.dankito.richtexteditor.java.fx.command.SelectValueCommand
import net.dankito.richtexteditor.java.fx.localization.Localization
import net.dankito.richtexteditor.java.fx.util.StyleApplier
import tornadofx.*


open class EditorToolbar : View() {

    var editor: RichTextEditor? = null
        set(value) {
            field = value

            setRichTextEditorOnCommands(value)
        }

    protected val localization = Localization()

    private val commandInvokedListeners = ArrayList<(ToolbarCommand) -> Unit>()


    private lateinit var contentLayout: HBox

    private val commands = HashMap<ToolbarCommand, Region>()

    private val styleApplier = StyleApplier()

    val commandStyle = ToolbarCommandStyle()


    init {
        commandStyle.heightDp = 22
        commandStyle.widthDp = 22
        commandStyle.isActivatedColor = Color.LightGray
        commandStyle.enabledTintColor = Color.Black
    }


    override val root = scrollpane(fitToHeight = true) {
        prefHeight = commandStyle.heightDp.toDouble()
        maxHeight = commandStyle.heightDp.toDouble()

        contentLayout = hbox {
            alignment = Pos.CENTER_LEFT
        }
    }


    fun addCommand(command: ToolbarCommand) {
        val commandView: Region =
            when(command) {
                is SelectValueCommand -> SelectValueView(command) { commandInvoked(it) }
                is SetColorCommand -> SelectColorCommandView(command) { commandInvoked(it) }
                else -> ActiveStateCommandView(command) { commandInvoked(it) }
            }

        contentLayout.add(commandView)

        commands.put(command, commandView)

        command.executor = editor?.javaScriptExecutor
        command.commandView = JavaFXCommandView(commandView)

        command.commandView?.setBackgroundColor(command.style.backgroundColor) // set default background, but don't do it in StyleApplier but JavaFXCommandView to avoid duplicate code
        applyCommandStyle(command, commandView)
    }

    private fun applyCommandStyle(command: ToolbarCommand, commandView: Region) {
        applyCommandStyle(command.icon, command.style, commandView)
    }

    internal fun applyCommandStyle(icon: Icon, style: ToolbarCommandStyle, commandView: Region) {
        mergeStyles(commandStyle, style)

        styleApplier.applyCommandStyle(icon, style, commandView)
    }

    private fun mergeStyles(toolbarCommandStyle: ToolbarCommandStyle, commandStyle: ToolbarCommandStyle) {
        if(commandStyle.backgroundColor == ToolbarCommandStyle.DefaultBackgroundColor) {
            commandStyle.backgroundColor = toolbarCommandStyle.backgroundColor
        }

        if(commandStyle.widthDp == ToolbarCommandStyle.DefaultWidthDp) {
            commandStyle.widthDp = toolbarCommandStyle.widthDp
        }

        if(commandStyle.heightDp == ToolbarCommandStyle.DefaultHeightDp) {
            commandStyle.heightDp = toolbarCommandStyle.heightDp
        }

        if(commandStyle.marginRightDp == ToolbarCommandStyle.DefaultMarginRightDp) {
            commandStyle.marginRightDp = toolbarCommandStyle.marginRightDp
        }

        if(commandStyle.paddingDp == ToolbarCommandStyle.DefaultPaddingDp) {
            commandStyle.paddingDp = toolbarCommandStyle.paddingDp
        }

        if(commandStyle.enabledTintColor == ToolbarCommandStyle.DefaultEnabledTintColor) {
            commandStyle.enabledTintColor = toolbarCommandStyle.enabledTintColor
        }

        if(commandStyle.disabledTintColor == ToolbarCommandStyle.DefaultDisabledTintColor) {
            commandStyle.disabledTintColor = toolbarCommandStyle.disabledTintColor
        }

        if(commandStyle.isActivatedColor == ToolbarCommandStyle.DefaultIsActivatedColor) {
            commandStyle.isActivatedColor = toolbarCommandStyle.isActivatedColor
        }
    }


//    fun addSearchView(style: SearchViewStyle = SearchViewStyle()) {
//        val searchView = SearchView(context)
//
//        contentLayout.addView(searchView)
//        searchViews.add(searchView)
//
//        searchView.applyStyle(style)
//
//        searchView.editor = editor
//
//        searchView.searchViewExpandedListener = { isExpanded ->
//            if(isExpanded) { // scroll to right by lytSearchControls' width
//                searchView.lytSearchControls.executeActionAfterMeasuringSize(true) {
//                    smoothScrollBy(searchView.lytSearchControls.width, 0)
//                }
//            }
//            else {
//                smoothScrollBy(-1 * searchView.lytSearchControls.width, 0)
//            }
//        }
//    }


    private fun setRichTextEditorOnCommands(editor: RichTextEditor?) {
        commands.keys.forEach {
            it.executor = editor?.javaScriptExecutor
        }
    }


    private fun commandInvoked(command: ToolbarCommand) {
        editor?.focusEditor(false)

        commandInvokedListeners.forEach {
            it.invoke(command)
        }
    }

    fun addCommandInvokedListener(listener: (ToolbarCommand) -> Unit) {
        commandInvokedListeners.add(listener)
    }

    fun removeCommandInvokedListener(listener: (ToolbarCommand) -> Unit) {
        commandInvokedListeners.remove(listener)
    }


}