# Web-Crawler

### Command Line Argument Options
arg[0] url
arg[1] search type; "B" for breadth first, "D" for depth first or "X" for random
arg[2] search limit
arg[3-10] (optional)
- STDOUT-true/false: send incremental json to STDOUT                        (Default true)
- FILE-"name"/false: send incremental json to file name                     (Default false)
- SEARCH-"term"/false: search for a term during the crawl                   (Default false)
- DELAY-100/false: set a delay in milliseconds between page indexing        (Default false)
- RANDOM-true/false: randomly select next web page link                     (Default true)
- RANDOMUA-true/false: randomly selects user agent for each page search     (Default false)
- CUSTOMUA-"user agent"/false: set a custom user agent for the search       (Default false)
- CYCLIC-true/false: Sets the graph results to cyclic or acyclic            (Default true)
- LINK-ALL/REL/ABS: Sets the link types to search for                       (Default ALL)

Example:  
java -jar Web-Crawler.jar https://www.msn.com B 500 STDOUT-true FILE-test.out SEARCH-"test term" DELAY-55 RANDOM-true

### Error Code List
101 - Invalid URL
102 - Invalid Search Type
103 - Invalid Limit, Set To Default

### JSON Output
{  
"id" : 1,  
"url" : "http://localhost:3000/B",  
"hostname" : "localhost",  
"ipAddress" : "127.0.0.1",  
"groupId" : 1,  
"title" : "B",  
"userAgent" : "Mozilla/5.0",  
"height" : 1,  
"searchTermFound" : false,  
"keywords" : null,  
"wordCount" : 5,  
"charCount" : 17,  
"byteCount" : 374,  
"numberOfImages" : 0,  
"isDeadEnd" : true,
"targetLinkCount" : 2,  
"predecessorLinks" : [ {  
    "bidirectional" : true,  
    "cyclic" : false,  
    "sourceId" : 0,  
    "targetId" : 1  
    } ]  
} 

### Compile Jar File on the Server
java -jar Web-Crawler.jar + command line arguments

### Spawn Process in Node.js
var child_process = require('child_process');

var java = child_process.spawn('java', ['-jar', 'Web-Crawler.jar', 'args0', 'arg1', 'arg2'...]);

&nbsp;&nbsp;  java.stdout.on('data', function(data){  
&nbsp;&nbsp;&nbsp;&nbsp;      console.log('Java process stdout =  ' + data);  
&nbsp;&nbsp;  });  

&nbsp;&nbsp;  java.on('exit', function(exitCode){  
&nbsp;&nbsp;&nbsp;&nbsp;      console.log('Java process exit code =  ' + exitCode);  
&nbsp;&nbsp;  });  

&nbsp;&nbsp;  java.stderr.on('data', function(data){  
&nbsp;&nbsp;&nbsp;&nbsp;        console.log('Java process stderr =  ' + data);  
&nbsp;&nbsp;  });  

});  
