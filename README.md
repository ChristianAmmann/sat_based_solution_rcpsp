# SAT-based solution for the Resource-Constrained Project Scheduling Problems

We present an efficient encoding of the Resource-Constrained Project Scheduling Problem (RCPSP) to SAT such that makespan optimization can be accomplished with incremental bound refinement on a
single SAT instance. While previous SAT based approaches use external
algorithms to solve resource constraints, our experiments with project
scheduling problems from industry indicate that it can be more efficient
to encode the cardinality constraint for constant resource constraints
directly to SAT.

[Click here for paper](https://drive.google.com/open?id=1L8KJMA1Wu-jNyBmYyrJmbcgckZhY9G-y) 


###Project example with activities, relations and consumptions:

```sh
project;0;2147483647;test
task;0;308;Task 0
task;1;10;Task 1
task;2;1;Task 2
task;3;20;Task 3
task;4;2;Task 4
task;5;5;Task 5
task;6;1;Task 6
aob;1;2;ea
aob;2;3;ea
aob;3;4;ea
aob;4;5;ea
aob;5;6;ea
consumption;1;0;-3
consumption;3;0;-3
consumption;3;1;-3
consumption;3;2;-3
consumption;4;0;-3
consumption;4;3;-3
consumption;4;4;-3
consumption;5;0;-3
consumption;5;3;-3
consumption;5;4;-3
resource;0;8;Resource 0
resource;5;8;Resource 5
resource;6;8;Resource 6
resource;7;8;Resource 7
resource;1;8;Resource 1
resource;2;8;Resource 2
resource;8;8;Resource 8
resource;3;8;Resource 3
resource;9;8;Resource 9
resource;10;8;Resource 10
resource;4;8;Resource 4
```

###Syntax:

```sh
project;wat;wet;name
task;id;duration;name
aob;task_id_1;task_id_2;relation_type
consumption;resource_id;capacity;consumtion
resource;id;capacity;name 
```

###Execution Example
```sh
for m in "-bcc" "-pow"; 
do 
	for f in `find $DIR -name *.project`; 
		do 
			java -Xms12000M -Xmx20000M -jar scheduler.jar scheduler.Scheduler -algo rcpsp $m -logPath ".\evaluation_$m.log" -project $f
		;done 
;done
```

###Measurements
<object data="https://drive.google.com/open?id=1Ao6MlmgN8UFhgQwMr1RTc67LRrrLUnr3" type="application/pdf" width="700px" height="700px">
    <embed src="https://drive.google.com/open?id=1Ao6MlmgN8UFhgQwMr1RTc67LRrrLUnr3">
        <p>This browser does not support PDFs. Please download the PDF to view it: <a href="https://drive.google.com/open?id=1Ao6MlmgN8UFhgQwMr1RTc67LRrrLUnr3">Download PDF</a>.</p>
    </embed>
</object>

<object data="https://drive.google.com/open?id=1IlF0Yv4xcVLRfluBQfeg_c4lgYURsye2" type="application/pdf" width="700px" height="700px">
    <embed src="https://drive.google.com/open?id=1IlF0Yv4xcVLRfluBQfeg_c4lgYURsye2">
        <p>This browser does not support PDFs. Please download the PDF to view it: <a href="https://drive.google.com/open?id=1IlF0Yv4xcVLRfluBQfeg_c4lgYURsye2">Download PDF</a>.</p>
    </embed>
</object>

