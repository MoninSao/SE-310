package AlarmClockRadio;

import java.time.LocalTime;

public class main
{
    public static void main(String[] args)
    {
        int i;
        int seconds;
        LocalTime alarmTime = LocalTime.of(10,0);
        AlarmClock myAlarmClock = new AlarmClock(alarmTime, true);

        for (i = 0; i < 5; i++)
        {
            System.out.println("Time: " + myAlarmClock.getAlarm());

            for (int seconds = 0; seconds < 60; seconds++)
            {
                myAlarmClock.getIsAlarmOn();
                myAlarmClock.;
            }
        }
        myAlarmClock.snooze();
        for (i = 0; i < 9; i++)
        {
            System.out.println("Time: " + myAlarmClock.getAlarm());
            for (int seconds = 0; seconds < 60; seconds++)
            {
                myAlarmClock.getIsAlarmOn();
                myAlarmClock.tick();
            }
        }
        myAlarmClock.turnAlarmOff();
    }
}