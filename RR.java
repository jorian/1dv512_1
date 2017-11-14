/*
 * File:	RR.java
 * Course: 	Operating Systems
 * Code: 	1DV512
 * Author: 	Suejb Memeti
 * Date: 	November, 2017
 */

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;

public class RR{

	// The list of processes to be scheduled
	public ArrayList<Process> processes;

	// the quantum time - which indicates the maximum allowable time a process can run once it is scheduled
	int tq;

	// keeps track of which process should be executed next
	public Queue<Process> schedulingQueue;

	ArrayList<GanttInfo> ganttInfoArrayList = new ArrayList<>();

	// Class constructor
	public RR(ArrayList<Process> processes, int tq) {
		schedulingQueue = new LinkedList<Process>();
		this.processes = processes;
		this.tq = tq;

	}

	public void run() {
		int currentTime = 0;
		int pos = 1;

		sortProcesses();

		int loopDuration = 0;

		// Calculate roughly how long the while loop needs to run:
		for (Process p: processes) {
			loopDuration += (p.getBurstTime());

			if (p.equals(processes.get(processes.size()-1))) {
				loopDuration += p.getArrivalTime();
			}
		}

		// Initiate queue by inserting first process:
		schedulingQueue.offer(processes.get(0));

		while (currentTime <= loopDuration) {
			// Compare the next one in the processes list to the current time. If a new process arrives, add it to
			// scheduling list.

			Process p = schedulingQueue.poll();

			if (p != null) {
				for (int i = 0; i < tq; i++) {
					if (p.getRemainingBurstTime() > 0) {
						p.setRemainingBurstTime(p.getRemainingBurstTime() - 1);
						currentTime++;
					} else  // if no remainingbursttime is left, better to exit the loop.
						break;
				}

				// Add to arraylist for printing Gantt Chart later on
				GanttInfo ganttInfo = new GanttInfo(p.getProcessId(), currentTime);
				ganttInfoArrayList.add(ganttInfo);

				if (p. getRemainingBurstTime() == 0 && p.getCompletedTime() == 0) {
					p.setCompletedTime(currentTime);
				}

			} else {
				// CPU is IDLE but still needs to track time for next P in queue to process at some point
				currentTime++;
			}

			while (pos < (processes.size()) && processes.get(pos).getArrivalTime() <= currentTime) {
				schedulingQueue.offer(processes.get(pos));
				pos++;
			}

			if (p != null) {
				if (p.getRemainingBurstTime() > 0)
					schedulingQueue.offer(p);
			}
		}

		processes.forEach(process -> {
			process.setTurnaroundTime(process.getCompletedTime() - process.getArrivalTime());
			process.setWaitingTime(process.getTurnaroundTime() - process.getBurstTime());
		});

		printProcesses();
		printGanttChart();
	}

	public void printProcesses() {
		// TODO Print the list of processes in form of a table here
		System.out.printf("%11s| %15s| %15s| %20s| %20s| %17s\n", "Process ID", "Arrival Time", "Burst Time", "Completed Time",
				"Turnaround Time", "Waiting Time");
		System.out.printf("%s", "-------------------------------------------------------------------------------------------------------------\n");
		for (Process p : processes) {
			System.out.printf("%11s| %15s| %15s| %20s| %20s| %17s\n",
					p.getProcessId(),
					p.getArrivalTime(),
					p.getBurstTime(),
					p.getCompletedTime(),
					p.getTurnaroundTime(),
					p.getWaitingTime());
		}
		System.out.println("\n");
	}

	public void printGanttChart(){
		// TODO Print the demonstration of the scheduling algorithm using Gantt Chart
		System.out.print("");
		for (GanttInfo g : ganttInfoArrayList) {
			if (!g.equals(ganttInfoArrayList.get(ganttInfoArrayList.size() - 1)))
				System.out.printf("  P%-2d |", g.processID);
			else {
				System.out.printf("  P%-2d \n", g.processID);
			}
		}

		System.out.print("0   ");
		for (GanttInfo g : ganttInfoArrayList) {
			if (!g.equals(ganttInfoArrayList.get(ganttInfoArrayList.size() - 1)))
				System.out.printf("  %-2d   ", g.cT);
			else
				System.out.printf("  %-2d \n", g.cT);
		}

		System.out.println("\n");

	}

	private void sortProcesses() {
		processes.sort(Comparator.comparing(Process::getArrivalTime));
	}

	private class GanttInfo {
		private int processID;
		private int cT;

		GanttInfo(int processID, int cT) {
			this.processID = processID;
			this.cT = cT;
		}
	}
}
