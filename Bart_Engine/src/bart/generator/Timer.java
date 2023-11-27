package bart.generator;

import java.util.Date;

public class Timer {

    private Date start;

    public void startTimer() {
        if (start != null) {
            throw new IllegalArgumentException("Timer is already started");
        }
        start = new Date();
    }

    public long stopTimer() {
        if (start == null) {
            throw new IllegalArgumentException("Timer is not started yet");
        }
        Date fine = new Date();
        long msLast = (fine.getTime() - this.start.getTime());
        this.start = null;
        return msLast;
    }

}
