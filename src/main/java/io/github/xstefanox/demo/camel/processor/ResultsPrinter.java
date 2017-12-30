package io.github.xstefanox.demo.camel.processor;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.apache.camel.Body;
import org.apache.camel.Handler;
import org.apache.camel.Message;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.util.stream.Collectors.joining;

public class ResultsPrinter implements Closeable {

    private static final Path PATH = Paths.get(System.getProperty("user.dir"), "results.txt");
    private static final Character SEPARATOR = '\t';

    private final BufferedWriter out;

    public ResultsPrinter() throws IOException {
        out = Files.newBufferedWriter(PATH, CREATE, APPEND);
    }

    @Handler
    public void print(final @Body List<Message> body) throws IOException {

        final String persons = body.stream()
                .map(b -> new String(b.getBody(byte[].class)))
                .collect(joining(","));

        out.write(String.valueOf(persons.length()));
        out.write(SEPARATOR);
        out.write(body.size());
        out.write(SEPARATOR);
        out.write(persons);
        out.newLine();
        out.flush();
    }

    @Override
    public void close() throws IOException {
        out.close();
    }
}
