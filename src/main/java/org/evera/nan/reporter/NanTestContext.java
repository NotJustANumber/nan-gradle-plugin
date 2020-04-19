package org.evera.nan.reporter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gradle.BuildResult;
import org.gradle.api.Project;
import org.gradle.api.tasks.testing.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class NanTestContext {

    public static final String SUCCESSFUL = "SUCCESSFUL";
    public static final String FAILED = "FAILED";
    public static NanTestContext INSTANCE = new NanTestContext();

    private boolean isUploadTaskAttached = false;

    private Map<Project, NanTestListener> projectTests = new HashMap<>();

    private NanTestContext() {
    }

    public void addListener(Project project, Test testTask) {
        NanTestListener listener = new NanTestListener();
        projectTests.put(project, listener);
        testTask.addTestListener(listener);
    }

    public void uploadTask(Project rootProject) {
        if (!isUploadTaskAttached) {
            NanExtension extension = rootProject.getExtensions()
                    .create("nan", NanExtension.class);
            rootProject.getGradle().buildFinished((buildResult) -> uploadTest(extension, rootProject));
            isUploadTaskAttached = true;
        }
    }

    private void uploadTest(NanExtension extension, Project rootProject) {
        ReportData data = buildReport(rootProject);
        uploadData(Collections.singletonList(data), extension);
    }

    private void uploadData(Collection<ReportData> data, NanExtension extension) {

        try {
            String json = new ObjectMapper().writeValueAsString(data);
            URL obj = new URL(extension.getServer() + "/v1/test/report/upload");
            HttpURLConnection postConnection = (HttpURLConnection) obj.openConnection();
            postConnection.setRequestMethod("POST");
            postConnection.setRequestProperty("Content-Type", "application/json");
            postConnection.setDoOutput(true);
            OutputStream os = postConnection.getOutputStream();
            os.write(json.getBytes());
            os.flush();
            os.close();
            postConnection.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private ReportData buildReport(Project rootProject) {
        ReportData data = new ReportData(rootProject.getName() + ":" + rootProject.getVersion(), true);
        data.setStatus(SUCCESSFUL);
        long passed = 0;
        long failed = 0;
        long skipped = 0;

        long duration = 0;

        Set<ReportData> children = new HashSet<>();

        for (Map.Entry<Project, NanTestListener> entry : projectTests.entrySet()) {
            ReportData subProject = new ReportData(entry.getKey().getName(), true);
            subProject.setStatus(SUCCESSFUL);
            Set<ReportData> childrenSub = entry.getValue().getReportData();

            long passedSub = childrenSub.stream().map(ReportData::getPassed).reduce(0L, Long::sum);
            subProject.setPassed(passedSub);
            passed += passedSub;

            long failedSub = childrenSub.stream().map(ReportData::getFailed).reduce(0L, Long::sum);
            subProject.setFailed(failedSub);
            failed += failedSub;

            long skippedSub = childrenSub.stream().map(ReportData::getSkipped).reduce(0L, Long::sum);
            subProject.setSkipped(skippedSub);
            skipped += skippedSub;

            long durationSub = childrenSub.stream().map(ReportData::getDuration).reduce(0l, Long::sum);
            subProject.setDuration(durationSub);
            duration += durationSub;

            subProject.setChildren(childrenSub);

            if (passedSub + failedSub + skippedSub > 0) {
                children.add(subProject);
            }
        }

        data.setPassed(passed);
        data.setFailed(failed);
        data.setSkipped(skipped);
        data.setDuration(duration);
        data.setChildren(children);
        return data;
    }

}
