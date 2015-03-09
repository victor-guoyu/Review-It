package ir.crawler;

import ir.server.ServerConstants;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.util.Strings;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

public enum TrainingData {
    INSTANCE;

    private List<String> traingQuery;

    private TrainingData() {
        try {
            loadTrainingData();
        } catch (IOException e) {
            throw new RuntimeException("Unable to find the traing data file", e);
        }
    }

    private void loadTrainingData() throws IOException {
        File traingFile = new File(ServerConstants.TRAINING_DATA);
        traingQuery = Files.readLines(traingFile, Charsets.UTF_8,
                new LineProcessor<List<String>>() {
                    List<String> tdl = Lists.newLinkedList();

                    @Override
                    public List<String> getResult() {
                        return tdl;
                    }

                    @Override
                    public boolean processLine(String currentLine) throws IOException {
                        if (Strings.isNotBlank(currentLine)) {
                            tdl.add(currentLine);
                        }
                        return true;
                    }
                });
    }

    public List<String> getTraingQueries() {
        return traingQuery;
    }
}
