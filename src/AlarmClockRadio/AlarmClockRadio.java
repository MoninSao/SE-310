package AlarmClockRadio;

import java.time.LocalTime;

public class AlarmClockRadio {

    private AlarmClock myAlarmClock;
    private Radio myRadio;

    public AlarmClockRadio(LocalTime alarmTime, boolean isAlarmOn, LocalTime currentTime, String currentRadioStation) {
        myAlarmClock = new AlarmClock(alarmTime, isAlarmOn, currentTime);
        myRadio = new Radio(currentRadioStation);
    }

    public String getRadioStation() {
        return myRadio.getRadioStation();
    }

    public void setRadioStation(String station) {
        myRadio.setRadioStation(station);
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

    public void setAlarm(LocalTime time){
        myAlarmClock.setAlarm(time);
    }

    public void turnAlarmOn(){
        myAlarmClock.turnAlarmOn();
    }

    public void turnAlarmOff(){
        myAlarmClock.turnAlarmOff();
    }

    public boolean isAlarmOn() {
       return myAlarmClock.getIsAlarmOn();
    }

    public void snooze() {
        myAlarmClock.snooze();
    }
}
