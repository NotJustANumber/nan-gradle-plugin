package org.evera.nan.reporter;

import org.gradle.api.tasks.testing.TestDescriptor;
import org.gradle.api.tasks.testing.TestListener;
import org.gradle.api.tasks.testing.TestResult;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.evera.nan.reporter.NanTestContext.FAILED;
import static org.evera.nan.reporter.NanTestContext.SUCCESSFUL;

public class NanTestListener implements TestListener {


    Map<TestDescriptor, List<TestDescriptor>> testSuite = new HashMap<>();
    Map<TestDescriptor, TestResult> testSuitResults = new HashMap<>();

    Map<TestDescriptor, Instant> startedSuite = new HashMap<>();
    Map<TestDescriptor, Instant> endedSuite = new HashMap<>();


    @Override

    public void beforeSuite(TestDescriptor testDescriptor) {
        testSuite.put(testDescriptor, new ArrayList<>());
    }

    @Override
    public void afterSuite(TestDescriptor testDescriptor, TestResult testResult) {
        testSuitResults.put(testDescriptor, testResult);
    }

    @Override
    public void beforeTest(TestDescriptor testDescriptor) {
        testSuite.get(testDescriptor.getParent()).add(testDescriptor);
        startedSuite.put(testDescriptor, Instant.now());
    }

    @Override
    public void afterTest(TestDescriptor testDescriptor, TestResult testResult) {
        endedSuite.put(testDescriptor, Instant.now());
        testSuitResults.put(testDescriptor, testResult);
    }

    public Set<ReportData> getReportData() {
        return testSuite.entrySet().stream().map(e -> convertAsReport(e.getKey(), e.getValue())).collect(Collectors.toSet());
    }

    private ReportData convertAsReport(TestDescriptor parent, List<TestDescriptor> children) {
        ReportData data = new ReportData(parent.getName(), parent.isComposite());
        TestResult result = testSuitResults.get(parent);
        data.setThrowable(Optional.ofNullable(testSuitResults.get(parent).getException()).map(this::getThrowableAsString).orElse(""));
        data.setPassed(result.getSuccessfulTestCount());
        data.setFailed(result.getFailedTestCount());
        data.setStatus(result.getFailedTestCount() > 0 ? FAILED : SUCCESSFUL);
        data.setSkipped(result.getSkippedTestCount());
        data.setDuration(result.getEndTime() - result.getStartTime());
        data.setChildren(children.stream().map(s -> convertAsReport(s, Collections.emptyList())).collect(Collectors.toSet()));
        return data;
    }

    String getThrowableAsString(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }
}
