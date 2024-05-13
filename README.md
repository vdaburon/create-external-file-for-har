<p align="center">
<img src="https://github.com/vdaburon/create-external-file-for-har/blob/main/doc/create-external-file-for-har-logo.png" alt="create an external csv file for har"/>

  <p align="center">Create a csv file with transaction information to complete a HAR (Http ARchive) file.</p>
  <p align="center"><a href="https://github.com/vdaburon/create-external-file-for-har">Link to github project create-external-file-for-har</a></p>
</p>

# create-external-file-for-har
Console text tool to create transaction info corresponding to a har file.

Create a csv file with transaction information to complete a HAR (Http ARchive) file.

The csv format is : TIME_STAMP ISO GMT;ELEMENT;NAME;TYPE<br/>
- Timestamp ISO GMT format
- ELEMENT values are : <code>TRANSACTION</code> or <code>COMMENT</code>
- TYPE for transaction, values are : <code>start</code> or <code>stop</code>
- CSV separtor : ";"
- Charset : "UTF-8"

E.g :
<pre>
2024-05-07T07:56:40.513Z;TRANSACTION;authent;start
2024-05-07T07:56:56.261Z;TRANSACTION;authent;stop
2024-05-07T07:57:08.679Z;TRANSACTION;home;start
2024-05-07T07:57:10.123Z;COMMENT;user toto;
2024-05-07T07:57:14.310Z;TRANSACTION;home;stop
2024-05-07T07:57:30.280Z;TRANSACTION;logout;start
2024-05-07T07:58:15.377Z;TRANSACTION;logout;stop
</pre>


## Launch the tool 
This tool could be use with script shell Windows or Linux.
<pre>
C:\mydir>java -jar -Dfile.encoding=UTF-8 -jar ../target/create-external-file-for-har-1.0-jar-with-dependencies.jar
</pre>

With set file out parameter :
<pre>
C:\mydir>java -jar -Dfile.encoding=UTF-8 -jar ../target/create-external-file-for-har-1.0-jar-with-dependencies.jar c:/temp/demo1.csv
</pre>

## Help
Command help or h display :

<pre>
Help
Commands : 
sf &lt;file name&gt; : Set the File out name to save information, need to be first command or the file name is set at program start with a launch program parameter
ch &lt;charset&gt; : set the CHarset to write in the file out  (e.g : UTF-8 (Default) or ISO-8859-1 or Cp1252 (windows))
ts &lt;transaction name&gt; : for Transaction Start with transaction name not empty
te [&lt;transaction name&gt;] : for Transaction End if no transaction name then use the last transaction name
de &lt;transaction name&gt; : DElete start and end transaction in csv file or a comment
co &lt;comment&gt; : add a COmment
sh : SHow content file
exit : save file and EXIT
help : this HELP
h : this Help short command
</pre>

## How use this tool while recording a HAR file when navigate to web site

Record a HAR file and create external csv file with create-external-file-to-har tool.

![Record a HAR file and create external csv file](doc/images/browser_and_create_external_csv_save_har.png)

For Chrome Browser (**B** for Browser) : <br/>
B1) Open navigator (Chrome) <br/>
B2) Open dev tools with &lt;F12&gt; <br/>
B3) Tab "Network"  <br/>
B4) Record button is ON.  <br/>
B5) "Preserve log" is checked. (Optional) If needed, delete the exchanges before browsing  <br/>
   
Start create-external-file-for-har tool (**C** for Create external file tool)  <br/>
C1) Set a file name , command : sf c:/Temp/jpetstore_transaction.csv <br/>
<br/>
C2) Create the first transaction start, command : ts welcome  <br/>
B6) Navigate to the home url in the Browser  <br/>
C3) When the page is display, end the first transaction (welcome), command : te  <br/>
<br/>
C4) Create a new transaction for future page, command : ts enter store  <br/>
B7) Click on link or click on button to navigate to store page  <br/>
C5) When the page is display, end the current transaction (store page), command : te  <br/>
<br/>
C4) Create a new transaction for future page, command : ts page3 <br/>
B8) Click on link or click on button to navigate to the page3 <br/>
C5) When the page is display, end the current transaction (page3), command : te <br/>
<br/>
C6) Create a new transaction for futur page ou form, command : ts &lt;page name&gt; <br/>
B9) Navigate to the page <br/>
C7) When the page is display, end the current transaction (page name), command : te <br/>
<br/>
... continue to create a transaction before navigate, navigate to the page, end the transaction ... <br/>
<br/>
Bx) When navigation is finished, Save exchanges in HAR format "Export HAR ..." <br/>

## Companion tool
The csv file created will be used by this other tool : "har-to-jmeter-convertor"
https://github.com/vdaburon/har-to-jmeter-convertor

or this JMeter plugin "har-convertor-jmeter-plugin"
https://github.com/vdaburon/har-convertor-jmeter-plugin

![Step to create JMeter script and record from HAR file and external csv file](doc/images/browers_har_external_csv_convertor_script_record.png)

## JPetstore web application
This example use the JPetstore from Octoperf : https://petstore.octoperf.com/

## License
Licensed under the Apache License, Version 2.0

## Versions
Version 1.0 date 2024-05-10, First version
