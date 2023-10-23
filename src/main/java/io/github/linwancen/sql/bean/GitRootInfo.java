package io.github.linwancen.sql.bean;

import java.io.File;
import java.util.Date;

public class GitRootInfo {
    private File gitRoot;
    private Date startDate;
    private Date endDate;

    public File getGitRoot() {
        return gitRoot;
    }

    public void setGitRoot(File gitRoot) {
        this.gitRoot = gitRoot;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
