package jpe.jb;

import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;

public class QtAssistantPlugin extends AbstractProjectComponent {

    private Project project;
    private static int numInits = 0;

    public QtAssistantPlugin(Project project) {
        super(project);
        this.project = project;
    }

    public void initComponent() {
        numInits += 1;
    }

    public void disposeComponent() {
        numInits -= 1;
        if (numInits == 0) {
            QtAssistantAction.releaseAssistant();
        }
    }

    @Override
    public String getComponentName() {
        return "QtAssistantProjectComponent";
    }

    @Override
    public void projectOpened() {
    }

    @Override
    public void projectClosed() {
    }
}