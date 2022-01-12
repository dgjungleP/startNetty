package pojo;

import java.net.InetSocketAddress;

public class LogEvent {
    public static final byte SEPARATOR = ':';
    private final InetSocketAddress source;
    private final String logfile;
    private final String msg;
    private final Long received;

    public LogEvent(InetSocketAddress source, String logfile, String msg, Long received) {
        this.source = source;
        this.logfile = logfile;
        this.msg = msg;
        this.received = received;
    }

    public LogEvent(String logfile, String msg) {
        this(null, logfile, msg, -1L);
    }

    public InetSocketAddress getSource() {
        return source;
    }

    public String getLogfile() {
        return logfile;
    }

    public String getMsg() {
        return msg;
    }

    public Long getReceived() {
        return received;
    }
}
