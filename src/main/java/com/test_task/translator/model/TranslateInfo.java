package com.test_task.translator.model;

import java.util.Date;

public class TranslateInfo {
    private Date callTime;
    private String inputParameters;
    private String ipClient;

    public TranslateInfo(Date callTime, String inputParameters, String ipClient) {
        this.callTime = callTime;
        this.inputParameters = inputParameters;
        this.ipClient = ipClient;
    }

    @Override
    public String toString() {
        return "TranslateInfo{" +
                "callTime=" + callTime +
                ", inputParameters='" + inputParameters + '\'' +
                ", ipClient='" + ipClient + '\'' +
                '}';
    }
}
