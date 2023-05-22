# CPU Scheduler Simulation
## Team Fire
- Jared Scarr 
- Derrek Do
- Mike Murphy

## Table of Contents
1. [Introduction](#introduction)

2. [Algorithms](#algorithms)
   3. [First Come First Served](#first-come-first-served)
   4. [Shortest Job First](#shortest-job-first)
   4. [Priority](#priority)
   5. [Round-Robin](#round-robin)
   6. [Multi-level Queue](#multi-level-queue)
   7. [Multi-level Feedback Queue](#multi-level-feedback-queue)
8. [Process](#process)
8. [UML Diagram](#uml-diagram)
9. [Data](#results)
   10. [First Come First Served Results](#first-come-first-served-results)
   11. [Shortest Job First Results](#shortest-job-first-results)
   12. [Priority Results](#priority-results)
   13. [Round-Robin Results](#round-robin-results)
   14. [Multi-level Queue Results](#multi-level-queue-results)
   15. [Multi-level Feedback Queue Results](#multi-level-feedback-queue-results)
16. [Conclusion](#conclusion)

## Introduction

The goals of this project were to implement several scheduling algorithms to simulate CPU scheduling.
Then compare and contrast the results based on an agreed upon criteria. In comparing these algorithms
the criteria used is: CPU utilization, throughput, turnaround time, waiting time, and response time.

## Algorithms

### First Come First Served

How this queue works is pretty well outlined by its name. The processes are run in the order
received with no preemption.

TODO: Derrek add your implementation details here

### Priority

This algorithm processes the next burst with the highest priority first. The priority can
be a set labeled number, burst duration, or something else. Whatever is selected that
priority runs first.

One major issue that arises with a strait Priority algorithm is starvation due to
new processes being added that always have a higher priority than others in the queue.
One way of solving this issue is by providing some sort of aging criteria which
would bump the priority of a process that has been in the queue for a certain amount of time.
Eventually, or probably, it would be processed. Another way to solve this is
by using a round-robin algorithm for processes that come in with the same priority level.
The algorithm that is implemented here uses this solution.

### Shortest Job First

This algorithm is a variation of the above described Priority algorithm. The priority in this
case is the length of the process job duration. As the title states the highest priority goes to
the shortest duration. There is a preemptive and non-preemptive version. The algorithm in this
report is the non-preemptive implementation.

TODO: Mike add impementation details here

### Round-Robin

In a round-robin tournament every competitor will play each other to find a winner.
In a similar fashion each process will execute against a period of time. This time
period is called a _quantum_. When a process executes a CPU burst it can only run
up to the limit of the quantum. If the CPU burst duration is longer than the quantum
allows it stops, is preempted, and then moved to the back of the ready queue to be processed
when next its turn arrives. In this fashion all the bursts get processed either in full
or in a piece at a time until completion.

### Multi-level Queue

This design has multiple queues each that hold a different priority per level. A process
comes in with a priority assigned then it is transferred to the queue that processes
that priority. There are different ways that these queues can be designed to run the processes.
One way could be that the queue that handles all the highest priority processes will
always run first before any of the other queues. Another design choice is to provide a time
slice to split up how different queues are run.

TODO: Mike add details of your implementation here

### Multi-level Feedback Queue

This queue also has levels of queues that handle different processes. In this design the processes
are split up by a characteristic of their bursts. Usually duration. So if a process does not
complete their burst within a certain quantum (duration period) it will be moved to a lower
queue. This algorithm's underlying queues can be round-robin, priority, FCFS, etc.

TODO: Derrek add your implementation details here

## Design Process

We decided to approach this from the bottom up writing the FCFS and Round Robin algorithms
so that we could compose the Multi-level queues with them. One flaw with this logic, or at
least in our planning, was that we did not develop those algorithms to function as a part
of another algorithm. Due to the difficulty in modifying the previously implemented algorithms
the Multi-level Queue and Multi-level Feedback Queue were designed with their own logic that did
not involve the composition of previous algorithms. A design from the top down may have provided
a better perspective on how we needed those algorithms to function from the perspective of
use with the ML queues.

## UML Diagram

## Results

TODO: Insert Diagrams, tables, plots, and discussions here

TODO: Discussion should be spent comparing algorithm performance and deciding on the best solution
to implement. Why its the best solution and why not should also be discussed.

### First Come First Served Results

### Shortest Job First Results

### Priority Results

### Round-Robin Results

### Multi-level Queue Results

### Multi-level Feedback Queue Results

## Conclusion

