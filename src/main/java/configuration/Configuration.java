package configuration;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.storage.StorageLevel;
import scala.Tuple2;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Stack;

/**
 * Configuration contains the full gamut of settings required for various packages in this library.
 * In addition, this class contains a runtime logger for timing code sections. Default parameters
 * are provided upon instantiation and can be changed using the provided setter methods.
 */
public class Configuration implements Serializable {

    /* Used for tracking of elapsed time since initialization */
    private final long startTime;
    /* Random Number Generator */
    protected long seed;
    protected byte[] secureSeed;
    /* Context */
    protected String context;
    /* Verbose Flag */
    private boolean verboseFlag;
    private HashMap<String, Long> timerLogs;
    private int indentation;
    private boolean previousEnd;
    /* Runtime Logging */
    private boolean runtimeFlag;
    private String rootDirectory;
    /* HashMap<context, Tuple2<filename, isHeaderWritten>> */
    private HashMap<String, Tuple2<String, Boolean>> runtimeFiles;
    /* HashMap<context, LinkedHashMap<header, Stack<Tuple2<nanotime, isEndTime>>>> */
    private HashMap<String, LinkedHashMap<String, Stack<Tuple2<Long, Boolean>>>> runtimeLogs;

    /* System Configurations */
    private int numExecutors;
    private int numCores;
    private int numMemory;
    private int numPartitions;
    private StorageLevel storageLevel;
    private JavaSparkContext sc;

    /* Debug Flag runs assertion checks for debugging */
    private boolean debugFlag;

    public Configuration() {
        seed = 5;
        secureSeed = null;

        verboseFlag = true;
        timerLogs = new HashMap<>();
        indentation = 0;
        previousEnd = false;
        startTime = System.nanoTime();

        runtimeFlag = true;
        context = "config-default";
        rootDirectory = "/tmp/spark-events/";
        runtimeFiles = new HashMap<>();
        runtimeLogs = new HashMap<>();

        numExecutors = 1;
        numCores = 1;
        numMemory = 16;
        numPartitions = 2;
        storageLevel = StorageLevel.MEMORY_AND_DISK_SER();

        debugFlag = false;
    }

    public Configuration(
            final int numExecutors,
            final int numCores,
            final int numMemory,
            final int numPartitions,
            final JavaSparkContext sc,
            final StorageLevel storageLevel) {
        this();
        this.numExecutors = numExecutors;
        this.numCores = numCores;
        this.numMemory = numMemory;
        this.numPartitions = numPartitions;
        this.sc = sc;
        this.storageLevel = storageLevel;
        sc.setLogLevel("ERROR");
    }

    /**
     * Prints the message and stores the current time for tracking runtime.
     */
    public void beginLog(final String message, final boolean indent) {
        if (verboseFlag) {
            System.out.println("");
            printIndentation();
            System.out.println("Starting : " + message + " " + elapsedTimeInSeconds());

            if (indent) {
                indentation++;
            }

            if (previousEnd) {
                previousEnd = false;
            }

            timerLogs.put(message, System.nanoTime());
        }
    }

    /**
     * Prints the message as defined by the above beginLog() with default parameter indent=true.
     */
    public void beginLog(final String message) {
        beginLog(message, true);
    }

    /**
     * Prints the message and prints the total runtime of the code section.
     * If beginLog() was not invoked for this message, the resulting runtime is not returned.
     */
    public void endLog(final String message, final boolean indent) {
        if (verboseFlag) {
            final long endTime = System.nanoTime();
            double totalTime = (endTime - timerLogs.getOrDefault(message, endTime)) / 1000000.0;

            if (timerLogs.containsKey(message)) {
                timerLogs.remove(message);
            }

            if (indent) {
                indentation--;
            }

            // Prints new line if there is two endLog() invocations in a row.
            if (previousEnd) {
                // Print newline
                System.out.println("");
            }

            // Print message indentation.
            printIndentation();

            if (totalTime == 0) {
                // Print message.
                System.out.println("Ending   : " + message);
            } else {
                String timeOutput;
                if (totalTime >= 1000) {
                    totalTime = Math.round(totalTime * 100) / 100;
                    totalTime = totalTime / 1000.0;
                    timeOutput = totalTime + " seconds";
                } else {
                    timeOutput = totalTime + " milliseconds";
                }

                // Print message.
                System.out
                        .println("Ending   : " + message + " " + elapsedTimeInSeconds() + " - " + timeOutput);
            }

            previousEnd = true;
        }
    }

    /**
     * Prints the message as defined by the above endLog() with default parameter indent=true.
     */
    public void endLog(final String message) {
        endLog(message, true);
    }

    /**
     * Initializes a new runtime metadata record for the specified context and header in runtimeLogs.
     * Note, if the specified context and header are not present in the records, then the
     * necessary records are created. If the last header record is not completed, then
     * nothing is done. Observe that 'null' is used to denote metadata status.
     */
    public void beginRuntimeMetadata(final String header, final Long data) {
        if (!runtimeFlag) {
            return;
        }

        if (!runtimeFiles.containsKey(context)
                || !runtimeLogs.containsKey(context)
                || !runtimeLogs.get(context).containsKey(header)) {
            newRuntimeLog(context, header);
        }

        final Stack<Tuple2<Long, Boolean>> headerStack = runtimeLogs.get(context).get(header);
        headerStack.push(new Tuple2<>(data, null));
    }

    /**
     * Initalizes a new runtime record for the specified context and header in runtimeLogs.
     * Note, if the specified context and header are not present in the records, then the
     * necessary records are created. If the last header record is not completed, then
     * nothing is done.
     */
    public void beginRuntime(final String header) {
        if (!runtimeFlag) {
            return;
        }

        if (!runtimeFiles.containsKey(context)
                || !runtimeLogs.containsKey(context)
                || !runtimeLogs.get(context).containsKey(header)) {
            newRuntimeLog(context, header);
        }

        final Stack<Tuple2<Long, Boolean>> headerStack = runtimeLogs.get(context).get(header);

        // If the last record on the stack was not finished, remove it
        // and overwrite it with this new record.
        if (!headerStack.isEmpty() && !headerStack.peek()._2) {
            headerStack.pop();
        }

        headerStack.push(new Tuple2<>(System.nanoTime(), false));
    }

    /**
     * Completes an existing runtime record for the specified context and header, setting
     * boolean to true, representing a completion of the time calculation for this record.
     * Note, if the specified context and header are not present in the records, then
     * nothing is done. If the last header record is already completed, then nothing is done.
     */
    public void endRuntime(final String header) {
        if (!runtimeFlag) {
            return;
        }

        if (runtimeFiles.containsKey(context)
                && runtimeLogs.containsKey(context)
                && runtimeLogs.get(context).containsKey(header)) {
            final Stack<Tuple2<Long, Boolean>> headerStack = runtimeLogs.get(context).get(header);

            if (!headerStack.peek()._2) {
                headerStack.push(new Tuple2<>(System.nanoTime() - headerStack.pop()._1, true));
            }
        }
    }

    /**
     * Writes all runtime data from runtimeLogs into the specified context file. Note, if the
     * specified context and header are not present in the records, then nothing is done. If the
     * length of the keyList and valueList do not match, then nothing is done. If any record
     * for a respective header has boolean value false, then that record is logged as -1.
     */
    public void writeRuntimeLog(final String context) {
        if (!runtimeFlag) {
            return;
        }

        try {
            if (runtimeFiles.containsKey(context) && runtimeLogs.containsKey(context)) {
                final String runtimeFileName = runtimeFiles.get(context)._1;
                final LinkedHashMap<String, Stack<Tuple2<Long, Boolean>>> headers = runtimeLogs
                        .get(context);

                final ArrayList<String> keyList = new ArrayList<>(headers.keySet());
                final ArrayList<Stack<Tuple2<Long, Boolean>>> valueList = new ArrayList<>(headers.values());

                storeSparkConfiguration(runtimeFileName);

                final StringBuilder sb = new StringBuilder();
                if (keyList.size() == valueList.size()) {
                    // Write the CSV headers if they haven't been written yet.
//          if (!runtimeFiles.get(context)._2) {
//
//            for (int i = 0; i < keyList.size(); i++) {
//              sb.append(keyList.get(i));
//
//              // Append time denomination, if not metadata.
//              if (valueList.get(i).isEmpty() || valueList.get(i).peek()._2 != null) {
//                sb.append(" (sec)");
//              }
//
//              if (i < keyList.size() - 1) {
//                sb.append(",");
//              } else {
//                sb.append("\n");
//              }
//            }
//
//            // Record that the header has now been written.
//            runtimeFiles.put(context, new Tuple2<>(runtimeFileName, true));
//          }

                    // Write the CSV values row-wise.
                    for (int i = 0; i < valueList.size(); i++) {
                        if (!valueList.get(i).isEmpty() && valueList.get(i).peek()._2 == null) {
                            sb.append(valueList.get(i).pop()._1);
                        } else if (valueList.get(i).isEmpty() || !valueList.get(i).peek()._2) {
                            sb.append(-1);
                        } else {
                            sb.append(valueList.get(i).pop()._1 / 1000000000.0);
                        }

                        if (i < valueList.size() - 1) {
                            sb.append(",");
                        } else {
                            sb.append("\n");
                        }
                    }
                }

                PrintWriter pw;
                final File file = new File(runtimeFileName);
                file.getParentFile().mkdirs();
                pw = new PrintWriter(new FileWriter(file, true));

                // Append to existing file, if it exists.
                if (file.exists() && !file.isDirectory()) {
                    pw.append(sb.toString());
                } else {
                    pw.write(sb.toString());
                }

                pw.close();
            }
        } catch (FileNotFoundException e1) {
            System.out.println("Error with log file creation");
        } catch (IOException e1) {
            System.out.println("Error with path creation");
        }
    }

    public void setSeed(final long _seed) {
        seed = _seed;
    }

    public void setSecureSeed(final byte[] _secureSeed) {
        secureSeed = _secureSeed;
    }

    public void setVerboseFlag(final boolean _verboseFlag) {
        verboseFlag = _verboseFlag;
    }

    public void setDebugFlag(final boolean _debugFlag) {
        debugFlag = _debugFlag;
    }

    public void setRuntimeFlag(final boolean _runtimeFlag) {
        runtimeFlag = _runtimeFlag;
    }

    public void setContext(final String _context) {
        final String metadata =
                "-" + numExecutors + "exec-" + numCores + "cpu-"
                        + numMemory + "mem-" + numPartitions + "partitions";
        context = _context + metadata;
    }

    public int numExecutors() {
        return numExecutors;
    }

    public StorageLevel storageLevel() {
        return storageLevel;
    }

    public int numPartitions() {
        return numPartitions;
    }

    public long seed() {
        return seed;
    }

    public byte[] secureSeed() {
        return secureSeed;
    }

    public boolean verboseFlag() {
        return verboseFlag;
    }

    public boolean debugFlag() {
        return debugFlag;
    }

    public String context() {
        return context;
    }

    public JavaSparkContext sparkContext() {
        return sc;
    }

    private void printIndentation() {
        for (int i = 0; i < indentation; i++) {
            System.out.print("\t");
        }
    }

    private String elapsedTimeInSeconds() {
        return "[" + ((System.nanoTime() - startTime) / 1000000000.0) + " seconds elapsed]";
    }

    /**
     * Initializes a new CSV runtime log file with filename in format
     * "logs/{time}_{date}/{context}.csv" and CSV headers as provided in variable headers.
     */
    private void newRuntimeLog(final String context, final String... headers) {
        // If runtimeFiles does not contain this context, then record a context and file name.
        if (!runtimeFiles.containsKey(context)) {
            final String runtimeFileName = rootDirectory + "src/main/resources/logs/" + context + ".csv";

            runtimeFiles.put(context, new Tuple2<>(runtimeFileName, false));
        }

        // If runtimeLogs does not contain this context, then create this context
        // and link the associated headers. Else If this context exists, but this
        // header record does not exist, then create the header record.
        if (!runtimeLogs.containsKey(context)) {
            final LinkedHashMap<String, Stack<Tuple2<Long, Boolean>>> headersMap = new LinkedHashMap<>();

            for (String header : headers) {
                headersMap.put(header, new Stack<>());
            }

            runtimeLogs.put(context, headersMap);
        } else {
            final LinkedHashMap<String, Stack<Tuple2<Long, Boolean>>> headersMap = runtimeLogs
                    .get(context);

            for (String header : headers) {
                if (!headersMap.containsKey(header)) {
                    headersMap.put(header, new Stack<>());
                }
            }
        }
    }

    private void storeSparkConfiguration(final String contextFileName) {
        if (sc != null) {
            final String system =
                    "\n----------------------------------------------------------------------------\n"
                            + "Instantiated as: \n"
                            + numExecutors + " executors\n"
                            + numCores + " cores per executor\n"
                            + numMemory + " GB RAM memory per executor\n"
                            + numPartitions + " partitions per RDD\n\nSpark Configuration:\n";
            final String debugString = sc.getConf().toDebugString();

            final String systemFileName =
                    contextFileName.substring(0, contextFileName.length() - 4) + "-system.txt";

            try {
                PrintWriter pw;
                final File file = new File(systemFileName);
                file.getParentFile().mkdirs();
                pw = new PrintWriter(new FileWriter(file, true));
                // Append to existing file, if it exists.
                if (file.exists() && !file.isDirectory()) {
                    pw.append(system + debugString);
                } else {
                    pw.write(system + debugString);
                }

                pw.close();
            } catch (FileNotFoundException e1) {
                System.out.println("Error with log file creation");
            } catch (IOException e1) {
                System.out.println("Error with path creation");
            }
        }
    }

}
