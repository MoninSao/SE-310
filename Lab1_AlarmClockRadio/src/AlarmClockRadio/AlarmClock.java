package AlarmClockRadio;

import java.time.LocalTime;

public class AlarmClock extends Clock {
    protected LocalTime alarmTime;
    protected LocalTime snoozeAlarmTime;
    protected boolean isAlarmOn;

    public AlarmClock(LocalTime alarmTime, boolean isAlarmOn, LocalTime currentTime) {
        super(currentTime);
        this.alarmTime = alarmTime;
        this.snoozeAlarmTime = null;
        this.isAlarmOn = isAlarmOn;
    }

    public LocalTime getAlarm() {
        return (snoozeAlarmTime != null) ? snoozeAlarmTime : alarmTime;
    }

    public void setAlarm(LocalTime time) {
        alarmTime = time;
    }

    public void turnAlarmOn() {
        isAlarmOn = true;
    }

    public void turnAlarmOff() {
        isAlarmOn = false;
        snoozeAlarmTime = null;
    }

    public boolean getIsAlarmOn() {
        return isAlarmOn;
    }

    public void snooze() {
        snoozeAlarmTime = alarmTime.plusMinutes(9);
        isAlarmOn = true;
    }

    // should increase the second the minute
    public void tick() {
        currentTime = currentTime.plusSeconds(1);
    }
}
