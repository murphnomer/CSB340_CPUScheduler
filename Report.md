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

In comparing these algorithms the criteria used is CPU utilization, throughput, turnaround time,
waiting time, and response time.

## Algorithms

### First Come First Served

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

This algorithm is a variation of the above described Priority algorithm. The priority in this case is
the length of the process job duration. As the title states the highest priority goes to
the shortest duration. There is a preemptive and non-preemptive version. The algorithm in this
report is the non-preemptive implementation.


### Round-Robin

In a round-robin tournament every competitor will play each other to find a winner.
In a similar fashion each process will execute against a period of time. This time
period is called a _quantum_. When a process executes a CPU burst it can only run
up to the limit of the quantum. If the CPU burst duration is longer than the quantum
allows it stops, is preempted, and then moved to the back of the ready queue to be processed
when next its turn arrives. In this fashion all the bursts get processed either in full
or in a piece at a time until completion.

### Multi-level Queue

### Multi-level Feedback Queue

## UML Diagram

## Results

Insert Diagrams, tables, plots, and discussions here

Discussion should be spent comparing algorithm performance and deciding on the best solution to implement.
Why its the best solution and why not should also be discussed.

### First Come First Served Results

### Shortest Job First Results

### Priority Results

### Round-Robin Results

### Multi-level Queue Results

### Multi-level Feedback Queue Results

## Conclusion

