package org.evera.nan.reporter;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.UnknownTaskException;
import org.gradle.api.tasks.testing.Test;

import java.util.Optional;

public class ReporterPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        try {
            Task testTask = project.getTasks().getByName("test");
            Optional.of(testTask).ifPresent(t -> addListener(t, project));
        } catch (UnknownTaskException e) {
            //Ignored
        }
        NanTestContext.INSTANCE.uploadTask(project.getRootProject());
    }


    private void addListener(Task testTask, Project project) {
        Test test = (Test) testTask;
        NanTestContext.INSTANCE.addListener(project, test);
    }

}