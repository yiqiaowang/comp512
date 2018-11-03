package Benchmark;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

public class BenchmarkLogger {     
    private PrintWriter p_writer;
    private static String format_string = "%f, %f\n";

    public BenchmarkLogger(String file_name) throws IOException {
        this.p_writer = new PrintWriter(new FileWriter(file_name));
    }

    public BenchmarkLogger(String file_name, String format_string) throws IOException {
        format_string = format_string;
        this.p_writer = new PrintWriter(new FileWriter(file_name));
    }

    public void writeRow(float x_value, float y_value) throws IOException {
        this.p_writer.printf(BenchmarkLogger.format_string, x_value, y_value);
    }

    public void close() {
        this.p_writer.close();
    }
}
