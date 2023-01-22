package Measurements;

public class Log {
    public double error;
    public long messages1;
    public long messages2;

    public String infos;

    public Log(double error, long messages1, long messages2, String infos) {
        this.error = error;
        this.messages1 = messages1;
        this.messages2 = messages2;
        this.infos = infos;
    }

    @Override
    public String toString() {
        return "Error: " + error + "\tmsg1: " + messages1 + "\tmsg2: " + messages2 + "\tInfos: " + infos;
    }
}
