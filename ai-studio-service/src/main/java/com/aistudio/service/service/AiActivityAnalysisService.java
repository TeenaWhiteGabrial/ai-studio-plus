package com.aistudio.service.service;

import com.aistudio.service.entity.GitlabCommitFact;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AiActivityAnalysisService {
    Optional<AiActivityAnalysisResult> analyze(Long userId, Long gitlabProjectId, LocalDate analysisDate, List<GitlabCommitFact> commits);

    class AiActivityAnalysisResult {
        private final List<AiWorkItemResult> workItems;
        private final String workSummary;
        private final String progressSummary;
        private final String riskSummary;
        private final String blockingSummary;
        private final String reportTitle;
        private final String reportContent;
        private final String analysisEngine;

        public AiActivityAnalysisResult(List<AiWorkItemResult> workItems, String workSummary, String progressSummary,
                                        String riskSummary, String blockingSummary, String reportTitle,
                                        String reportContent, String analysisEngine) {
            this.workItems = workItems;
            this.workSummary = workSummary;
            this.progressSummary = progressSummary;
            this.riskSummary = riskSummary;
            this.blockingSummary = blockingSummary;
            this.reportTitle = reportTitle;
            this.reportContent = reportContent;
            this.analysisEngine = analysisEngine;
        }

        public List<AiWorkItemResult> getWorkItems() {
            return workItems;
        }

        public String getWorkSummary() {
            return workSummary;
        }

        public String getProgressSummary() {
            return progressSummary;
        }

        public String getRiskSummary() {
            return riskSummary;
        }

        public String getBlockingSummary() {
            return blockingSummary;
        }

        public String getReportTitle() {
            return reportTitle;
        }

        public String getReportContent() {
            return reportContent;
        }

        public String getAnalysisEngine() {
            return analysisEngine;
        }
    }

    class AiWorkItemResult {
        private final String title;
        private final String summary;
        private final String workType;
        private final String progressStatus;
        private final String riskLevel;
        private final String riskSummary;
        private final String blockingStatus;
        private final String blockingSummary;
        private final String moduleNames;
        private final Integer relatedCommitCount;
        private final Integer relatedMrCount;
        private final String evidenceSummary;
        private final java.math.BigDecimal confidence;

        public AiWorkItemResult(String title, String summary, String workType, String progressStatus, String riskLevel,
                                String riskSummary, String blockingStatus, String blockingSummary, String moduleNames,
                                Integer relatedCommitCount, Integer relatedMrCount, String evidenceSummary,
                                java.math.BigDecimal confidence) {
            this.title = title;
            this.summary = summary;
            this.workType = workType;
            this.progressStatus = progressStatus;
            this.riskLevel = riskLevel;
            this.riskSummary = riskSummary;
            this.blockingStatus = blockingStatus;
            this.blockingSummary = blockingSummary;
            this.moduleNames = moduleNames;
            this.relatedCommitCount = relatedCommitCount;
            this.relatedMrCount = relatedMrCount;
            this.evidenceSummary = evidenceSummary;
            this.confidence = confidence;
        }

        public String getTitle() { return title; }
        public String getSummary() { return summary; }
        public String getWorkType() { return workType; }
        public String getProgressStatus() { return progressStatus; }
        public String getRiskLevel() { return riskLevel; }
        public String getRiskSummary() { return riskSummary; }
        public String getBlockingStatus() { return blockingStatus; }
        public String getBlockingSummary() { return blockingSummary; }
        public String getModuleNames() { return moduleNames; }
        public Integer getRelatedCommitCount() { return relatedCommitCount; }
        public Integer getRelatedMrCount() { return relatedMrCount; }
        public String getEvidenceSummary() { return evidenceSummary; }
        public java.math.BigDecimal getConfidence() { return confidence; }
    }
}
