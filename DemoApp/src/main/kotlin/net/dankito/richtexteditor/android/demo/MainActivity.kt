package net.dankito.richtexteditor.android.demo

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import net.dankito.richtexteditor.android.RichTextEditor
import net.dankito.richtexteditor.android.toolbar.AllCommandsEditorToolbar


class MainActivity : AppCompatActivity() {

    private lateinit var editor: RichTextEditor

    private lateinit var editorToolbar: AllCommandsEditorToolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editor = findViewById(R.id.editor) as RichTextEditor

        editor.setEditorFontSize(20)
        editor.setPadding((4 * resources.displayMetrics.density).toInt())

        // a little bit experimental, uncomment these if you don't like them (and you most certainly won't)
        editor.setEditorBackgroundColor(Color.YELLOW)
        editor.setEditorFontColor(Color.MAGENTA)
        editor.setEditorFontFamily("cursive")

        editorToolbar = findViewById(R.id.editorToolbar) as AllCommandsEditorToolbar
        editorToolbar.editor = editor

        editor.postDelayed({
            editor.focusEditorAndShowKeyboard()
        }, 250)
    }

}
