package de.christcoding.smartstudy.models;

public class Report {
    public String senderMail, reason, timestamp;
    public Member reportedMember;
    public boolean isResolved, messages, content, files, events, spam;

    public Report(String senderMail, String reason, Member reportedMember, String timestamp,
                  boolean isResolved, boolean messages, boolean content, boolean files, boolean events,
                  boolean spam) {
        this.senderMail = senderMail;
        this.reason = reason;
        this.reportedMember = reportedMember;
        this.timestamp = timestamp;
        this.isResolved = isResolved;
        this.messages = messages;
        this.content = content;
        this.files = files;
        this.events = events;
        this.spam = spam;
    }
}
