package zzpj.justjoinplugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.jcef.JBCefBrowser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zzpj.util.MappingLoader;

import javax.swing.*;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class JustJoinITAction extends AnAction {

    private static final Map<String, String> extensionMapping;

    static {
        try {
            extensionMapping = MappingLoader.loadMapping();
        } catch (IOException e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        Project project = e.getProject();
        if (project == null) {
            return;
        }

        FileEditorManager editorManager = FileEditorManager.getInstance(project);
        FileDocumentManager documentManager = FileDocumentManager.getInstance();
        Editor editor = editorManager.getSelectedTextEditor();
        if (editor == null || editor.getProject() == null) {
            return;
        }

        String currentFileName = Objects.requireNonNull(documentManager.getFile(editor.getDocument())).getName();
        String[] fileSplit = currentFileName.split("\\.");
        if (fileSplit.length != 2) {
            return;
        }
        String fileExtension = fileSplit[1];

        String fileURLPart = extensionMapping.get(fileExtension);

        JBCefBrowser browser = new JBCefBrowser();
        browser.loadURL(String.format("https://justjoin.it/all-locations/%s", fileURLPart));

        DialogWrapper dialogWrapper = new DialogWrapper(true) {
            {
                setTitle("Find a Job: ".concat(fileURLPart));
                setOKButtonText("Done");
                init();
            }
            @Override
            protected @Nullable JComponent createCenterPanel() {
                return browser.getComponent();
            }
        };

        dialogWrapper.show();
    }
}
