package com.example.wincc_alarme;

public class AlarmModel {
    public AlarmModel(String ConditionName, String Message) {
        this.ConditionName = ConditionName;
        this.Message = Message;
    }
    public String getMessage() {
        return Message;
    }
    public String getConditionName() { return ConditionName; }

    public void setConditionName(String conditionName) { ConditionName = conditionName; }
    public void setMessage(String message) { Message = message; }

    private String ConditionName;
    private String Message;
}