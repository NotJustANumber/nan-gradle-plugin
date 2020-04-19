package org.evera.nan.reporter;

import java.util.Set;

public class ReportData {

	private String name;
	private long duration;
	private boolean isContainer;
	private Set<ReportData> children;
	private String status;
	private String throwable;
	private long passed = 0;
	private long failed = 0;
	private long skipped = 0;

	public ReportData() {
	}

	public ReportData(String name, boolean isContainer) {
		this.name = name;
		this.isContainer = isContainer;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public boolean isContainer() {
		return isContainer;
	}

	public void setContainer(boolean container) {
		isContainer = container;
	}

	public Set<ReportData> getChildren() {
		return children;
	}

	public void setChildren(Set<ReportData> children) {
		this.children = children;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getThrowable() {
		return throwable;
	}

	public void setThrowable(String throwable) {
		this.throwable = throwable;
	}

	public long getPassed() {
		return passed;
	}

	public void setPassed(long passed) {
		this.passed = passed;
	}

	public long getFailed() {
		return failed;
	}

	public void setFailed(long failed) {
		this.failed = failed;
	}

	public long getSkipped() {
		return skipped;
	}

	public void setSkipped(long skipped) {
		this.skipped = skipped;
	}
}
