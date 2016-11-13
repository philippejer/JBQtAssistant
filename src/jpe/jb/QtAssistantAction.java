package jpe.jb;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.awt.RelativePoint;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.io.IOException;
import java.io.PrintStream;

public class QtAssistantAction extends AnAction {

    private static Process assistantProcess = null;
    private static PrintStream assistantOutput = null;

    private static void initAssistant() throws IOException {
        if ((assistantProcess != null) && assistantProcess.isAlive())
            return;
        ProcessBuilder builder = new ProcessBuilder("assistant", "-enableRemoteControl");
        builder.redirectErrorStream(true);
        assistantProcess = builder.start();
        assistantOutput = new PrintStream(assistantProcess.getOutputStream());
    }

    private static void queryAssistant(String keyword) throws IOException {
        initAssistant();
        if ((assistantOutput != null) && assistantProcess.isAlive()) {
            assistantOutput.print("activateKeyword " + keyword);
            assistantOutput.flush();
        }
    }

    static void releaseAssistant() {
        if (assistantOutput != null) {
            assistantOutput.close();
            assistantOutput = null;
        }
        if (assistantProcess != null) {
            assistantProcess.destroy();
            assistantProcess = null;
        }
    }

    private void showError(JTextComponent component, String htmlText) {
        try {
            Rectangle startRect = component.modelToView(component.getSelectionStart());
            Rectangle endRect = component.modelToView(component.getSelectionEnd());
            Point position = new Point((int) ((startRect.getX() + endRect.getX()) / 2), (int) (startRect.getY() + startRect.getHeight()));
            JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(htmlText, MessageType.ERROR, null).setFadeoutTime(10000)
                    .createBalloon().show(new RelativePoint(component, position), Balloon.Position.below);
        } catch (BadLocationException ee) {
        }
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        Editor editor = event.getData(CommonDataKeys.EDITOR_EVEN_IF_INACTIVE);
        if (editor == null)
            return;
        JTextComponent component = (JTextComponent) editor.getContentComponent();
        String selected = component.getSelectedText();
        if (selected != null && !selected.isEmpty()) {
            try {
                queryAssistant(selected);
            } catch (IOException e) {
                showError(component, "Cannot start QtAssistant: " + e.getMessage());
            }
        }
    }
}
