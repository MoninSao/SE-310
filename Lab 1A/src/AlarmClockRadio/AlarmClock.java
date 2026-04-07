package AlarmClockRadio;

import java.time.LocalTime;

public class AlarmClock extends Clock {
    protected LocalTime alarmTime;
    protected boolean isAlarmOn;

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

    public boolean getIsAlarmOn() {
        return isAlarmOn;
    }

    public void snooze() {
        LocalTime snoozeTime = alarmTime.plusMinutes(9); // according to the instruction it should be 9
        setAlarm(snoozeTime);
    }

    // should increase the second the minute
    public void tick() {
        currentTime = currentTime.plusSeconds(1);
    }
}
