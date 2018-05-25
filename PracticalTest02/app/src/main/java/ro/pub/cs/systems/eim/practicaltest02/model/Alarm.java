package ro.pub.cs.systems.eim.practicaltest02.model;

/**
 * Created by Ana-Maria on 5/21/2018.
 */

public class Alarm {

    private String command;
    private Integer hour;
    private Integer minute;

    public Alarm() {
    }

    public Alarm(String command, Integer hour, Integer minute) {
        this.command = command;
        this.hour = hour;
        this.minute = minute;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Alarm(Integer hour, Integer minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Integer getMinute() {
        return minute;
    }

    public void setMinute(Integer minute) {
        this.minute = minute;
    }

    @Override
    public String toString() {
        return "Alarm{" +
                "hour=" + hour +
                ", minute=" + minute +
                '}';
    }
}
