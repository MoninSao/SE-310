package AlarmClockRadio;

import java.time.LocalTime;

public class AlarmClock {
    private LocalTime alarmTime;
    private boolean isAlarmOn;

    public AlarmClock(LocalTime alarmTime, boolean isAlarmOn) {
        this.alarmTime = alarmTime;
        this.isAlarmOn = isAlarmOn;

    }

    public LocalTime getAlarm() {
        return alarmTime;
    }

    public void setAlarm(LocalTime time) {
        alarmTime = time;
    }

    public boolean turnAlarmOn() {
        return isAlarmOn = true;
    }

    public boolean turnAlarmOff() {
        return isAlarmOn = false;
    }

    public boolean getIsAlarmOn(){
        return isAlarmOn;
    }

    public void snooze(LocalTime snoozeTime) {
        snoozeTime = alarmTime;
        snoozeTime = snoozeTime.plusMinutes(5);
    }
}
