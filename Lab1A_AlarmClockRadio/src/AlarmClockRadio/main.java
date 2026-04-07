package AlarmClockRadio;

import java.time.LocalTime;

public class main {
    public static void main(String[] args) {
        int i;
        LocalTime alarmTime = LocalTime.of(8, 6);
        LocalTime currentTime = LocalTime.of(8, 0);
        AlarmClock myAlarmClock = new AlarmClock(alarmTime, true, currentTime);

        for (i = 0; i < 6; i++) {
            System.out.println("Time: " + myAlarmClock.getCurrentTime() + " AM");

            for (int seconds = 0; seconds < 60; seconds++) {
                myAlarmClock.tick(); // tick is called 60 times to increase the time to 1 minute
            }
        }
        if ((myAlarmClock.getCurrentTime().equals(alarmTime)) && (myAlarmClock.getIsAlarmOn() == true)) {
            System.out.println("Buzz, Buzz, Buzz");
        }

        myAlarmClock.snooze();
        System.out.println("Snooze was pressed");

        for (i = 0; i < 9; i++) {
            System.out.println("Time: " + myAlarmClock.getCurrentTime() + " AM");
            for (int seconds = 0; seconds < 60; seconds++) {
                myAlarmClock.tick();
            }
        }
        if ((myAlarmClock.getCurrentTime().equals(myAlarmClock.getAlarm())) && (myAlarmClock.getIsAlarmOn() == true)) {
            System.out.println("Buzz, Buzz, Buzz");
        }
        myAlarmClock.turnAlarmOff();
        if (myAlarmClock.getIsAlarmOn() == false) {
            System.out.println("The alarm was shut off");
        }
    }
}