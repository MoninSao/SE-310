package AlarmClockRadio;

import java.time.LocalTime;

public class AlarmClockRadio {

    private AlarmClock myAlarmClock;
    private Radio myRadio;

    public AlarmClockRadio(LocalTime alarmTime, boolean isAlarmOn, LocalTime currentTime, String currentRadioStation,
            int currentVolume, boolean isRadioOn) {
        myAlarmClock = new AlarmClock(alarmTime, isAlarmOn, currentTime);
        myRadio = new Radio(currentRadioStation, currentVolume, isRadioOn);
    }

    public String getRadioStation() {
        return myRadio.getRadioStation();
    }

    public void setRadioStation(String station) {
        myRadio.setRadioStation(station);
    }

    public int getVolume() {
        return myRadio.getVolume();
    }

    public void setVolume(int volume) {
        myRadio.setVolume(volume);
    }

    public boolean getIsRadioOn() {
        return myRadio.getIsRadioOn();
    }

    public void turnRadioOn() {
        myRadio.turnRadioOn();
    }

    public void turnRadioOff() {
        myRadio.turnRadioOff();
    }

    public LocalTime getCurrentTime() {
        return myAlarmClock.getCurrentTime();
    }

    public void setCurrentTime(LocalTime time) {
        myAlarmClock.setCurrentTime(time);
    }

    public LocalTime getAlarm() {
        return myAlarmClock.getAlarm();
    }

    public void setAlarm(LocalTime time) {
        myAlarmClock.setAlarm(time);
    }

    public void turnAlarmOn() {
        myAlarmClock.turnAlarmOn();
    }

    public void turnAlarmOff() {
        myAlarmClock.turnAlarmOff();
    }

    public boolean getIsAlarmOn() {
        return myAlarmClock.getIsAlarmOn();
    }

    public void snooze() {
        myAlarmClock.snooze();
    }

    public void tick() {
        myAlarmClock.tick();
    }
}
