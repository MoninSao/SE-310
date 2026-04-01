package AlarmClockRadio;

import java.time.LocalTime;

public class AlarmClock extends Clock{
    private LocalTime alarmTime;
    private boolean isAlarmOn;

    public AlarmClock(LocalTime alarmTime, boolean isAlarmOn, LocalTime currentTime) {
        super(currentTime);
        this.alarmTime = alarmTime;
        this.isAlarmOn = isAlarmOn;

    }

    public LocalTime getAlarm() {
        return alarmTime;
    }

    public void setAlarm(LocalTime time) {
        alarmTime = time;
    }

    public void turnAlarmOn() {
        isAlarmOn = true;
    }

    public void turnAlarmOff() {
        isAlarmOn = false;
    }

    public boolean getIsAlarmOn(){
        return isAlarmOn;
    }

    public void snooze(LocalTime snoozeTime) {
        snoozeTime = alarmTime;
        snoozeTime = snoozeTime.plusMinutes(5);
    }
}
