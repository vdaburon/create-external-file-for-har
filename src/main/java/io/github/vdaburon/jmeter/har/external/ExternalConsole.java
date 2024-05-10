/*
 * Copyright 2024 Vincent DABURON
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.github.vdaburon.jmeter.har.external;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExternalConsole {

    public static final String K_APPLI_VERSION = "v1.0";

    public static final String K_CMD_HELP = "help";
    public static final String K_CMD_HELP_SHORT = "h";
    public static final String K_CMD_SET_FILE_NAME = "sf";
    public static final String K_CMD_SET_FILE_CHARSET = "ch";
    public static final String K_CMD_EXIT = "exit";
    public static final String K_CMD_TRANSACTION_START = "ts";
    public static final String K_CMD_TRANSACTION_END = "te";
    public static final String K_CMD_COMMENT = "co";
    public static final String K_CMD_SHOW_CONTENT_FILE = "sh";
    public static final String K_CMD_DELETE_TRANSACTION = "de";

    public static final String K_FILE_OUT_SEP = ";";
    public static final String K_FILE_OUT_DEFAULT_CHARSET = "UTF-8";

    public static final String K_ELEMENT_TRANSACTION = "TRANSACTION";
    public static final String K_ELEMENT_COMMENT = "COMMENT";
    public static final String K_TYPE_START = "start";
    public static final String K_TYPE_STOP = "stop";

    BufferedReader readerConsole;
    static String fileOut = "";

    String currentTransaction;

    public static void main(String[] args) {

        if (args.length == 1) {
            fileOut = args[0];
            System.out.println("From program parameter, set fileOut: " + fileOut);
        }

        ExternalConsole externalConsole = new ExternalConsole();
        try {
            externalConsole.interactionLoop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ExternalConsole() {
        readerConsole = new BufferedReader(new InputStreamReader(System.in));
    }

    public void interactionLoop() throws IOException {
        boolean isContinue = true;
        String charsetName = K_FILE_OUT_DEFAULT_CHARSET;

        // show help when program start
        help();

        while (isContinue) {
            String cmd = K_CMD_HELP;
            String parameter = "";

            String line = ask("Enter command: ");
            // read command and parameter
            int index_first_space_char = line.indexOf(' ');
            if (index_first_space_char > 0) {
                cmd = line.substring(0, index_first_space_char);
                parameter = line.substring(index_first_space_char, line.length());
                if (parameter == null) {
                    parameter = "";
                } else {
                    parameter = parameter.trim();
                }
            } else {
                cmd = line;
            }


            boolean isCmdProcessed = false;

            if (K_CMD_EXIT.equals(cmd)) {
                isContinue = false;
                println("Exit command");
                println("The csv file is : " + fileOut);
                isCmdProcessed = true;
            }

            if (K_CMD_HELP.equals(cmd) || K_CMD_HELP_SHORT.equals(cmd)) {
                help();
                isCmdProcessed = true;
            }

            if (K_CMD_SET_FILE_NAME.equals(cmd) && !parameter.isEmpty()) {
                fileOut = parameter;
                println("Set File name to : " + parameter);
                isCmdProcessed = true;

            }

            if (K_CMD_SET_FILE_CHARSET.equals(cmd) && !parameter.isEmpty()) {
                charsetName = parameter;
                println("set CHarset name to : " + charsetName);
                isCmdProcessed = true;
            }

            if (K_CMD_TRANSACTION_START.equals(cmd) && !parameter.isEmpty()) {
                if (fileOut.isEmpty()) {
                    println("ERROR, you need to set a file before with command " + K_CMD_SET_FILE_NAME + " fileOut.csv");
                } else {
                    currentTransaction = parameter;
                    String lineToAppend = formatLine(dateToIsoFormat(new Date()), K_ELEMENT_TRANSACTION, parameter, K_TYPE_START);
                    fileAppend(fileOut, lineToAppend, charsetName);
                    println("Line add : " + lineToAppend);
                }
                isCmdProcessed = true;
            }

            if (K_CMD_TRANSACTION_END.equals(cmd)) {
                if (fileOut.isEmpty()) {
                    println("ERROR, you need to set a file before with command " + K_CMD_SET_FILE_NAME + " fileOut.csv");
                } else {
                    if (parameter.isEmpty()) {
                        String lineToAppend = formatLine(dateToIsoFormat(new Date()), K_ELEMENT_TRANSACTION, currentTransaction, K_TYPE_STOP);
                        fileAppend(fileOut, lineToAppend, charsetName);
                        println("Line add : " + lineToAppend);
                        currentTransaction = "";
                    } else {
                        String lineToAppend = formatLine(dateToIsoFormat(new Date()), K_ELEMENT_TRANSACTION, parameter, K_TYPE_STOP);
                        fileAppend(fileOut, lineToAppend, charsetName);
                        println("Line add : " + lineToAppend);
                        currentTransaction = "";
                    }
                }
                isCmdProcessed = true;
            }

            if (K_CMD_COMMENT.equals(cmd) && !parameter.isEmpty()) {
                if (fileOut.isEmpty()) {
                    println("ERROR, you need to set a file before with command " + K_CMD_SET_FILE_NAME + " fileOut.csv");
                } else {
                    String lineToAppend = formatLine(dateToIsoFormat(new Date()), K_ELEMENT_COMMENT, parameter, "");
                    fileAppend(fileOut, lineToAppend, charsetName);
                    println("Line add : " + lineToAppend);
                }
                isCmdProcessed = true;
            }

            if (K_CMD_SHOW_CONTENT_FILE.equals(cmd)) {
                if (fileOut.isEmpty()) {
                    println("ERROR, you need to set a file before with command " + K_CMD_SET_FILE_NAME + " fileOut.csv");
                } else {
                    String content = readAllFile(fileOut, charsetName);
                    print(content);
                }
                isCmdProcessed = true;
            }

            if (K_CMD_DELETE_TRANSACTION.equals(cmd) && !parameter.isEmpty()) {
                if (fileOut.isEmpty()) {
                    println("ERROR, you need to set a file before with command " + K_CMD_SET_FILE_NAME + " fileOut.csv");
                } else {
                    ArrayList<String> lNewContent = new ArrayList<String>();

                    // read all file content
                    String contentOld = readAllFile(fileOut, charsetName);
                    String[] lines = contentOld.split("\n");
                    for (int i = 0; i < lines.length; i++) {
                        String lineInter = lines[i];
                        if (lineInter != null) {
                            if (!lineInter.contains(K_FILE_OUT_SEP + parameter + K_FILE_OUT_SEP)) {
                                // add olnly line that NOT contains the transaction name or comment
                                lNewContent.add(lineInter);
                            }
                        }
                    }

                    File fileToDelete = new File(fileOut);
                    print("Delete file and recreate the file, ");
                    fileToDelete.delete();
                    for (int i = 0; i < lNewContent.size(); i++) {
                        fileAppend(fileOut, lNewContent.get(i), charsetName);
                    }
                    println(", new content :");
                    String contentNew = readAllFile(fileOut, charsetName);
                    print(contentNew);
                }
                isCmdProcessed = true;
            }


            if (!isCmdProcessed) {
                println("Command incorrect or need parameter, command : " + cmd + ", parameter : " + parameter);
            }

        }
    }

    public String ask(String textToWrite) throws IOException {
        if (textToWrite == null) {
            textToWrite = "";
        }
        print(textToWrite);

        String line = readerConsole.readLine();
        return line;
    }

    public void help() throws IOException {
        println("Version appli : " + K_APPLI_VERSION);
        println("Help");
        println("Commands : ");
        println(K_CMD_SET_FILE_NAME + " <file name> : Set the File out name to save information, need to be first command or the file name is set at program start with a launch program parameter");
        println(K_CMD_SET_FILE_CHARSET + " <charset> : set the CHarset to write in the file out (e.g : UTF-8 (Default) or ISO-8859-1 or Cp1252 (windows))");
        println(K_CMD_TRANSACTION_START + " <transaction name> : for Transaction Start with transaction name not empty");
        println(K_CMD_TRANSACTION_END + " [<transaction name>] : for Transaction End if no transaction name then use the last transaction name");
        println(K_CMD_DELETE_TRANSACTION + " <transaction name> : DElete start and end transaction in csv file or a comment");
        println(K_CMD_COMMENT + " <comment> : add a COmment");
        println(K_CMD_SHOW_CONTENT_FILE + " : SHow content file");
        println(K_CMD_EXIT + " : save file and EXIT");
        println(K_CMD_HELP + " : this HELP");
        println(K_CMD_HELP_SHORT + " : this Help short command");
    }

    public void print(String textToWrite) {
        if (textToWrite != null) {
            System.out.print(textToWrite);
        }
    }

    public void println(String textToWrite) {
        if (textToWrite != null) {
            System.out.println(textToWrite);
        }
    }

    public static String dateToIsoFormat(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // 2024-05-03T14:30:42.271Z
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String sDateIso = sdf.format(calendar.getTime());
        return sDateIso;
    }

    public static void fileAppend(String fileOutName, String lineToAppend, String charsetName) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOutName, true), charsetName));
            out.write(lineToAppend);
            out.write("\n");
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                // silent closing
            }
        }
    }

    public static String formatLine(String timestamp, String element, String name, String type) {
        /*
        TIME_STAMP;ELEMENT;NAME;TYPE
        2024-05-07T07:56:40.513Z;TRANSACTION;home_page;start
        2024-05-07T07:56:56.261Z;TRANSACTION;home_page;stop
        2024-05-07T07:57:08.679Z;TRANSACTION;bt_authent;start
        2024-05-07T07:57:10.123;COMMENT;user toto;
        */

        if (type.isEmpty()) {
            type = "";
        }

        name = name.replace(K_FILE_OUT_SEP, ",");
        String line = timestamp + K_FILE_OUT_SEP + element + K_FILE_OUT_SEP + name + K_FILE_OUT_SEP + type;
        return line;
    }

    public String readAllFile(String fileName, String charsetName) throws IOException {
        File fileToRead = new File(fileName);
        String res = "Empty file\n";
        if (fileToRead.canRead()) {
            RandomAccessFile raf = new RandomAccessFile(fileName, "r");
            int taille = (int) raf.length();

            byte[] buff = new byte[taille];

            raf.readFully(buff);

            raf.close();
            res = new String(buff, charsetName);
        }
        return res;

    }
}
