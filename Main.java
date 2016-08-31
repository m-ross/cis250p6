/*	Program name:	Lab 06 Restaurant Queue
	Programmer:		Marcus Ross
	Date Due:		24 Mar, 2014
	Description:	This program models a queue of customers in a restaurant. There are buttons to signal whether each register is open or closed, the end of the business day, when a customer enters the line, when their service begins, and when their service ends. The first attempt at closing the program will be met with a report showing the average times of the customers' wait and service.	*/

package lab06;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class Main {
	public static void main(String[] args) {
		Queue queue = new Queue(100); // maximum capacity of restaurant via fire department order
		Frame frame = new Frame("Restaurant Queue");
		Register[] reg = new Register[3];

		LoadFrame(frame, queue, reg);
	}

	public static void LoadFrame(Frame frame, Queue queue, Register[] reg) {
		// function: loads the appearance of the main menu
		// pre:	queue must have been created
		// post: frame will contain buttons that consist of main menu

		// initialize components
		SpringLayout layout = new SpringLayout();
		Panel panel = new Panel(layout);
		Button arrivalB = new Button("Customer Arrival");
		Button endDayB = new Button("End of Day");
		Label[] regL = {new Label("Register A", Label.CENTER), new Label("Register B", Label.CENTER), new Label("Register C", Label.CENTER)};
		Button[] regB = {new Button(), new Button(), new Button()};
		Button[] regInB = {new Button("Cust In"), new Button("Cust In"), new Button("Cust In")};
		Button[] regOutB = {new Button("Cust Out"), new Button("Cust Out"), new Button("Cust Out")};

		for(int i = 0; i < reg.length; i++)
			reg[i] = new Register(regL[i], regB[i], regInB[i], regOutB[i]);

		// component methods
		arrivalB.addActionListener(new ArrivalListen(queue));
		endDayB.addActionListener(new EndDayListen(arrivalB, reg[0], reg[1], reg[2]));
		for(int i = 0; i < reg.length; i++) { // for each register
			reg[i].close(); // closed by default
			regB[i].addActionListener(new RegListen(reg[i]));
			regInB[i].addActionListener(new InListen(queue, reg[i]));
			regOutB[i].addActionListener(new OutListen(reg[i]));
		}

		// constraints
		layout.putConstraint("South", arrivalB, -4, "South", panel);
		layout.putConstraint("West", arrivalB, 4, "West", panel);
		layout.putConstraint("East", arrivalB, -2, "HorizontalCenter", panel);
		layout.putConstraint("South", endDayB, -4, "South", panel);
		layout.putConstraint("West", endDayB, 2, "HorizontalCenter", panel);
		layout.putConstraint("East", endDayB, -4, "East", panel);
		layout.putConstraint("West", regL[0], 4, "West", panel);
		layout.putConstraint("West", regL[1], -50, "HorizontalCenter", panel);
		layout.putConstraint("West", regL[2], 4, "East", regL[1]);
		layout.putConstraint("East", regL[0], -4, "West", regL[1]);
		layout.putConstraint("East", regL[1], 50, "HorizontalCenter", panel);
		layout.putConstraint("East", regL[2], -4, "East", panel);
		for(int i = 0; i < regL.length; i++) { // cutting down on the number of lines this takes
			layout.putConstraint("North", regL[i], -4, "North", panel);
			layout.putConstraint("North", regB[i], 4, "South", regL[0]);
			layout.putConstraint("North", regInB[i], 4, "South", regB[0]);
			layout.putConstraint("North", regOutB[i], 4, "South", regInB[0]);
			layout.putConstraint("West", regB[i], 0, "West", regL[i]);
			layout.putConstraint("West", regInB[i], 0, "West", regL[i]);
			layout.putConstraint("West", regOutB[i], 0, "West", regL[i]);
			layout.putConstraint("East", regB[i], 0, "East", regL[i]);
			layout.putConstraint("East", regInB[i], 0, "East", regL[i]);
			layout.putConstraint("East", regOutB[i], 0, "East", regL[i]);
		}

		// add components
		panel.add(arrivalB);	panel.add(endDayB);
		for(Label label:regL)
			panel.add(label);
		for(Button button:regB)
			panel.add(button);
		for(Button button:regInB)
			panel.add(button);
		for(Button button:regOutB)
			panel.add(button);
		frame.add(panel);

		// frame methods
		frame.setResizable(false);
		frame.addWindowListener(new WinListen(frame, reg));
		frame.setSize(324, 156);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public static void ToggleReg(Register reg) {
		// function: toggles the open/closed status of a register
		// pre: register's buttons must have been instantiated
		// post: if register is open and unoccupied, it will be closed and its "Cust In" button will be hidden
			// if register is open and occupied, it will be closed
			// if register is closed and unoccupied, it will be opened and its "Cust In" button will be shown
			// if register is closed and occupied, it will be opened

		reg.toggle();
	}

	public static void EndDay(Register[] reg, Button arrivalB) {
		// function: signifies the end of the business day; closes all registers and prevents them from opening again
		// pre: register's buttons must have been instantiated
		// post: each unoccupied register will be closed and each of its buttons will be hidden
			// each occupied register will be closed and each of its buttons except "Cust Out" will be hidden

		for(Register register:reg)
			register.shutdown();

		arrivalB.setVisible(false);
	}

	public static void Arrival(Queue queue) {
		// function: signals a customer's entrance to the building
		// pre: queue must have been created
		// post: a customer with arrival time equal to the time the function is called will be added to the back of the queue

		Element customer = new Element();

		if(!queue.isFull())
			queue.enqueue(customer);
	}

	public static void CustIn(Queue queue, Register reg) {
		// function: transfers a customer from the queue to a register and logs the time
		// pre: register's buttons must have been instantiated and queue must have been created
		// post: if the queue is empty, no work will be done
			// if the queue is not empty, the customer at the front of the queue will be removed,
			// the customer will be added to the register with "in" time equal to the time the function is called,
			// the register will be set to occupied
			// the "Cust In" button will be hidden
			// the "Cust Out" button will be made visible

		Element customer;

		if(!queue.isEmpty()) {
			customer = queue.dequeue();
			customer.setTimeIn(new Date());
			reg.custIn(customer);
		}
	}

	public static void CustOut(Register reg) {
		// function: signals the end of a customer's service time and makes the register available
		// pre: register's buttons must have been instantiated
		// post: if the register is not occupied, no work will be done
			// if the register is occupied, that register's customer's "out" time will be set to the time the function is called,
			// the quantity of customers handled by the register will be incremented,
			// the customer's wait time will be added to the register's running total
			// the customer's service time will be added to the register's running total
			// the customer will be removed from the register
			// the register will be set to not occupied
			// the "Cust Out" button will be hidden
			// if the register is open, the "Cust In" button will be made visible

		Element customer;
		long waitTime, serviceTime;

		if(reg.isOccupied())
			reg.custOut();
	}

	public static void DispMean(Frame frame, Register[] reg) {
		// function: rearranges the appearance of the frame to show mean times
		// pre: reg[] must consist of three registers
		// post: the frame will display:
			// for each register, its quantity of customers served, and the means of its wait times and its service times, 
			// row headers displaying "Register", "Wait", "Service", "Customers",
			// column headers displaying "A", "B", "C", "Collective",
			// below the "Collective" heading will be the mean wait time across all registers,
			// the mean service time across all registers, and the total quantity of customers

		if(frame.getTitle().equals("Average Times"))
			CloseFrame(frame); // close the program if means are already displayed

		frame.removeAll();

		String[] totals = CalcTotals(reg);

		// init components
		SpringLayout layout = new SpringLayout();
		Panel panel = new Panel(layout);
		Button exitB = new Button("Exit");
		Label[][] cell = { // this is a table, basically
			{new Label("Register"), new Label("A", Label.RIGHT), new Label("B", Label.RIGHT), new Label("C", Label.RIGHT), new Label("Collective", Label.RIGHT)}, 
			{new Label("Wait"), new Label(reg[0].getWaitMean(true), Label.RIGHT), new Label(reg[1].getWaitMean(true), Label.RIGHT), new Label(reg[2].getWaitMean(true), Label.RIGHT), new Label(totals[0])},
			{new Label("Service"), new Label(reg[0].getServiceMean(true), Label.RIGHT), new Label(reg[1].getServiceMean(true), Label.RIGHT), new Label(reg[2].getServiceMean(true), Label.RIGHT), new Label(totals[1])},
			{new Label("Customers"), new Label(reg[0].getCustomers(true), Label.RIGHT), new Label(reg[1].getCustomers(true), Label.RIGHT), new Label(reg[2].getCustomers(true), Label.RIGHT), new Label(totals[2])}
		};

		// component methods
		exitB.addActionListener(new ExitListen(frame));

		// constraints
		layout.putConstraint("South", exitB, -10, "South", panel);
		layout.putConstraint("HorizontalCenter", exitB, 0, "HorizontalCenter", panel);
		for(int i = 0; i < cell.length; i++)
			layout.putConstraint("East", cell[i][cell[0].length - 1], -4, "East", panel);
		for(int i = 3; i > 0; i--)
			layout.putConstraint("East", cell[0][i], -25, "West", cell[0][i + 1]);
		for(Label label:cell[0])
			layout.putConstraint("North", label, 0, "North", panel);
		for(int i = 1; i < cell.length; i++)
			for(int j = 0; j < cell[1].length; j++)
				layout.putConstraint("North", cell[i][j], 0, "South", cell[i-1][j]);
		for(int i = 1; i < cell.length; i++)
			for(int j = 3; j > 0; j--)
				layout.putConstraint("East", cell[i][j], 0, "East", cell[0][j]);

		// add components
		for(int i = 0; i < cell.length; i++)
			for(int j = 0; j < cell[i].length; j++)
				panel.add(cell[i][j]); // add each element of cell
		panel.add(exitB);
		frame.add(panel);

		// frame methods
		frame.setTitle("Average Times");
		frame.setVisible(true);
	}

	public static String[] CalcTotals(Register[] reg) {
		// function: returns an array of formatted Strings consisting of, across all registers, the mean wait time, the mean service time, and the quantity of customers served
		// pre: none
		// post: an array of three Strings will be returned

		long waitTotalMean, serviceTotalMean, waitTotal, serviceTotal, customerTotal, waitSeconds, waitMinutes, serviceSeconds, serviceMinutes;
		String[] times = new String[3]; // this is returned

		waitTotal = serviceTotal = customerTotal = 0;

		for(Register register:reg) { // sum running totals
			waitTotal += register.getWaitTime();
			serviceTotal += register.getServiceTime();
			customerTotal += register.getCustomers();
		}

		if(customerTotal == 0) { // check this to avoid div by 0
			waitTotalMean = 0;
			serviceTotalMean = 0;
		} else {
			waitTotalMean = waitTotal / customerTotal;
			serviceTotalMean = serviceTotal / customerTotal;
		}

		waitMinutes = waitTotalMean / 60000; // convert milliseconds to minutes
		waitSeconds = waitTotalMean % 60000 / 1000; // to seconds
		serviceMinutes = serviceTotalMean / 60000;
		serviceSeconds = serviceTotalMean % 60000 / 1000;

		times[0] = String.format("%2d:%02d", waitMinutes, waitSeconds); // formatted to 0:00
		times[1] = String.format("%2d:%02d", serviceMinutes, serviceSeconds); // formatted to 0:00
		times[2] = Long.toString(customerTotal);

		return times;
	}

	public static void CloseFrame(Frame frame) {
		// function: close program
		// pre: none
		// post: program will end

		frame.setVisible(false);
		frame.dispose();
		System.exit(0);
	}
}

class ExitListen implements ActionListener {
	private Frame frame;

	public ExitListen(Frame frame) {
		this.frame= frame;
	}

	public void actionPerformed(ActionEvent e) {
		Main.CloseFrame(frame);
	}
}

class OutListen implements ActionListener {
	private Register reg;

	public OutListen(Register reg) {
		this.reg = reg;
	}

	public void actionPerformed(ActionEvent e) {
		Main.CustOut(reg);
	}
}

class InListen implements ActionListener {
	private Queue queue;
	private Register reg;

	public InListen(Queue queue, Register reg) {
		this.queue = queue;
		this.reg = reg;
	}

	public void actionPerformed(ActionEvent e) {
		Main.CustIn(queue, reg);
	}
}

class ArrivalListen implements ActionListener {
	private Queue queue;

	public ArrivalListen(Queue queue) {
		this.queue = queue;
	}

	public void actionPerformed(ActionEvent e) {
		Main.Arrival(queue);
	}
}

class EndDayListen implements ActionListener {
	private Button arrivalB;
	private Register[] reg;

	public EndDayListen(Button arrivalB, Register... reg) {
		this.arrivalB = arrivalB;
		this.reg = reg;
	}

	public void actionPerformed(ActionEvent e) {
		Main.EndDay(reg, arrivalB);
	}
}

class RegListen implements ActionListener {
	private Register reg;

	public RegListen(Register reg) {
		this.reg = reg;
	}

	public void actionPerformed(ActionEvent e) {
		Main.ToggleReg(reg);
	}
}

class WinListen implements WindowListener {
	private Frame frame;
	private Register[] reg;

	public WinListen(Frame frame, Register[] reg) {
		this.frame = frame;
		this.reg = reg;
	}

	public void windowClosing(WindowEvent e) {
		Main.DispMean(frame, reg);
	}

	public void windowOpened(WindowEvent e) { }
	public void windowClosed(WindowEvent e) { }
	public void windowIconified(WindowEvent e) { }
	public void windowDeiconified(WindowEvent e) { }
	public void windowActivated(WindowEvent e) { }
	public void windowDeactivated(WindowEvent e) { }
}

class Register {
	private boolean occupied, open;
	private Element customer;
	private Label label;
	private Button toggle, in, out; // the register is part of the application, not a utility!
	private long waitTimeSum, serviceTimeSum;
	private int custQty;

	public Register() {
		// function: instantiate a register
		// pre: none
		// post: register will exist in unusable state

		waitTimeSum = serviceTimeSum = custQty = 0;
		setOpen(false);
		setOccupied(false);
	}

	public Register(Label label, Button toggle, Button in, Button out) {
		// function: instantiate a register
		// pre: none
		// post: register will exist with the parameters as attributes

		waitTimeSum = serviceTimeSum = custQty = 0;
		setOpen(false);
		setOccupied(false);
		this.toggle = toggle;
		this.in = in;
		this.out = out;
		toggle.setLabel("Open");
	}

	public void setOccupied(boolean occupied) {
		// function: sets the occupation status of the register
		// pre: none
		// post: register will be set to occupied

		this.occupied = occupied;
	}

	public boolean isOccupied() {
		// function: checks whether the register is occupied
		// pre: none
		// post: if the register is set to occupied, a boolean of true will be returned
			// if the register is not set to occupied, a boolean of false will be returned

		return occupied;
	}

	public void setComponents(Label label, Button... buttons) {
		// function: sets the register's label and button attributes
		// pre: none
		// post: register's label and buttons will be instantiated

		this.label = label;
		this.toggle = buttons[0];
		this.in = buttons[1];
		this.out = buttons[2];
	}

	public void custIn(Element customer) {
		// function: signals to register when a customer's service begins
		// pre: register's buttons must be instantiated
		// post: customer attribute will be set to clone of customer parameter
			// button in will be hidden
			// button out will be made visible
		this.customer = customer.clone();
		setOccupied(true);
		in.setVisible(false);
		out.setVisible(true);
	}

	public void custOut() {
		// function: signals to register when a customer's service ends
		// pre: register's buttons must be instantiated
			// register must be occupied
			// getTimeIn() and getTimeArrive() by customer attribute must not return null
		// post: register's customer count will be incremented
			// time of end of service of customer attribute will be set to time of method call
			// wait time and service time of customer attribute will be added to register's running total
			// customer attribute will be set to null
			// register will be set to unoccupied
			// "Cust Out" button will be hidden
			// if register is open, "Cust In" button will be made visible

		custQty++;
		customer.setTimeOut(new Date());
		long waitTime = customer.getTimeIn().getTime() - customer.getTimeArrive().getTime();
		long serviceTime = customer.getTimeOut().getTime() - customer.getTimeIn().getTime();
		addWaitTime(waitTime);
		addServiceTime(serviceTime);

		customer = null;
		setOccupied(false);
		if(isOpen())
			in.setVisible(true);
		out.setVisible(false);
	}

	public Label getLabel() {
		// function: returns register's label
		// pre: none
		// post: returns register's label

		return label;
	}

	public Button getToggleButton() {
		// function: returns register's toggle button
		// pre: none
		// post: returns register's toggle button

		return toggle;
	}

	public Button getInButton() {
		// function: returns register's "Cust In" button
		// pre: none
		// post: returns register's "Cust In" button

		return in;
	}

	public Button getOutButton() {
		// function: returns register's "Cust Out" button
		// pre: none
		// post: returns register's "Cust Out" button

		return out;
	}

	public Button[] getButtons() {
		// function: returns all of register's buttons
		// pre: none
		// post: array of 3 elements consisting of toggle, "Cust In", and "Cust Out" buttons will be returned

		Button[] buttons = {toggle, in, out};
		return buttons;
	}

	public void toggle() {
		// function: toggles open/closed status of register
		// pre: register's buttons must be instantiated
		// post: if register is closed, it will be set to open
			// if register is open, it will be set to closed

		if(isOpen()) {
			close();
		} else {
			open();
		}
	}

	public void open() {
		// function: opens the register
		// pre: register's buttons must be instantiated
		// post: label of toggle button will be set to "Close"
			// register will be set to open
			// if register is unoccupied, "Cust In" button will be made visible

		toggle.setLabel("Close");
		setOpen(true);
		if(!isOccupied())		
			in.setVisible(true);
	}

	public void close() {
		// function: closes the register
		// pre: register's buttons must be instantiated
		// post: label of toggle button will be set to "Open"
			// register will be set to closed
			// if register is unoccupied, "Cust Out" button will be hidden

		toggle.setLabel("Open");
		setOpen(false);
		in.setVisible(false);
		if(!isOccupied())
			out.setVisible(false);
	}

	public boolean isOpen() {
		// function: checks open/closed status of register
		// pre: none
		// post: if register is open, a boolean of true will be returned
			// if the register is not open, a boolean of false will be returned

		return open;
	}

	public void setOpen(boolean open) {
		// function: sets open/closed status of register
		// pre: none
		// post: if parameter is true, register will be set to open
			// if parameter is false, register will be set to closed

		this.open = open;
	}

	public void shutdown() {
		// function: shutdown register at end of day
		// pre: buttons must be instantiated
		// post: register will be closed
			// toggle button will be hidden
			// "Cust In" button will be hidden
			// if register is unoccupied, "Cust Out" button will be hidden

		setOpen(false);
		toggle.setVisible(false);
		in.setVisible(false);
		if(!isOccupied())
			out.setVisible(false);		
	}

	private void addWaitTime(long waitTime) {
		// function: adds a customer's wait time to the register's running total
		// pre: none
		// post: parameter will be added to waitTimeSum attribute

		waitTimeSum += waitTime;
	}

	private void addServiceTime(long serviceTime) {
		// function: adds a customer's service time to the register's running total
		// pre: none
		// post: parameter will be added to serviceTimeSum attribute

		serviceTimeSum += serviceTime;
	}

	public long getWaitTime() {
		// function: returns the register's running total of customer wait times
		// pre: none
		// post: returns the register's running total of customer wait times

		return waitTimeSum;
	}

	public long getServiceTime() {
		// function: returns the register's running total of customer service times
		// pre: none
		// post: returns the register's running total of customer service times

		return serviceTimeSum;
	}

	public long getWaitMean() {
		// function: gets the mean of the register's customers' wait times
		// pre: none
		// post: mean is calculated then returned by dividing the register's running total of wait times by the quantity of customers served by the register
			// if quantity of customers is zero, the mean returned will be zero

		long mean;
		try {
			mean = waitTimeSum / custQty;
			return mean;
		} catch(ArithmeticException e) {
			mean = 0;
			return mean;
		}
	}

	public String getWaitMean(boolean string) {
		// function: gets the mean of the register's customers' wait times
		// pre: none
		// post: returns the mean of the register's customers' wait times as a String in the form of m:ss

		long seconds, minutes;
		String time;

		minutes = getWaitMean() / 60000;
		seconds = getWaitMean() % 60000 / 1000;

		time = String.format("%2d:%02d", minutes, seconds);
		return time;
	}

	public long getServiceMean() {
		// function: gets the mean of the register's customers' service times
		// pre: none
		// post: mean is calculated then returned by dividing the register's running total of service times by the quantity of customers served by the register
			// if quantity of customers is zero, the mean returned will be zero

		long mean;
		try {
			mean = serviceTimeSum / custQty;
			return mean;
		} catch(ArithmeticException e) {
			mean = 0;
			return mean;
		}
	}

	public String getServiceMean(boolean string) {
		// function: gets the mean of the register's customers' service times
		// pre: none
		// post: returns the mean of the register's customers' service times as a String in the form of m:ss

		long seconds, minutes;
		String time;

		minutes = getServiceMean() / 60000;
		seconds = getServiceMean() % 60000 / 1000;

		time = String.format("%2d:%02d", minutes, seconds);
		return time;
	}

	public int getCustomers() {
		// function: gets the quantity of customers served by the register
		// pre: none
		// post: returns the quantity of customers served by the register

		return custQty;
	}

	public String getCustomers(boolean string) {
		// function: gets the quantity of customers served by the register
		// pre: none
		// post: returns the quantity of customers served by the register as a String

		return Integer.toString(custQty);
	}
}

class Element {
	private Date arrive, in, out;

	public Element() {
		// function: instantiates element
		// pre: none
		// post: creates new element with arrive attribute set to time of method invocation

		arrive = new Date();
	}

	public Element(Date arrive) {
		// function: instantiates element
		// pre: none
		// post: creates new element with arrive attribute set to parameter

		this.arrive = new Date(arrive.getTime()); // encapsulation!
	}

	public Element(Long arrive) {
		// function: instantiates element
		// pre: none
		// post: creates new element with arrive attribute set to Date instantiated with parameter

		this.arrive = new Date(arrive);
	}


	public Element(Date arrive, Date in) {
		// function: instantiates element
		// pre: none
		// post: creates new element with arrive attribute set to first parameter and in attribute set to second parameter

		this.arrive = new Date(arrive.getTime());
		this.in = new Date(in.getTime());
	}

	public Element(Date arrive, Date in, Date out) {
		// function: instantiates element
		// pre: none
		// post: creates new element with arrive attribute set to first parameter, in attribute set to second parameter, and out attribute set to third parameter

		this.arrive = new Date(arrive.getTime());
		this.in = new Date(in.getTime());
		this.out = new Date(out.getTime());
	}

	public Element(Long arrive, Long in, Long out) {
		// function: instantiates element
		// pre: none
		// post: creates new element with arrive attribute set to Date instantiated with first parameter, in attribute set to Date instantiated with second parameter, and out attribute set to Date instantiated with third parameter

		this.arrive = new Date(arrive);
		this.in = new Date(in);
		this.out = new Date(out);
	}

	public void set(Date arrive, Date in) {
		// function: sets the arrive and in attributes
		// pre: none
		// post: the arrive attribute will be set to first parameter and the in attribute will be set to the second parameter

		this.arrive = new Date(arrive.getTime());
		this.in = new Date(in.getTime());
	}

	public void set(Date arrive, Date in, Date out) {
		// function: sets the arrive, in, and out attributes
		// pre: none
		// post: the arrive attribute will be set to first parameter, the in attribute will be set to the second parameter, and the out attribute will be set to third parameter

		this.arrive = new Date(arrive.getTime());
		this.in = new Date(in.getTime());
		this.out = new Date(out.getTime());
	}

	public void setTimeArrive(Date arrive) {
		// function: sets arrive attribute
		// pre: none
		// post: sets arrive attribute to date instantiated with time of Date parameter

		this.arrive = new Date(arrive.getTime());
	}

	public void setTimeIn(Date in) {
		// function: sets in attribute
		// pre: none
		// post: sets in attribute to date instantiated with time of Date parameter

		this.in = new Date(in.getTime());
	}

	public void setTimeOut(Date out) {
		// function: sets out attribute
		// pre: none
		// post: sets out attribute to date instantiated with time of Date parameter

		this.out = new Date(out.getTime());
	}

	public Date getTimeArrive() {
		// function: returns arrive attribute
		// pre: arrive attribute must not be null
		// post: date instantiated with time of arrive attribute will be returned

		Date arrive = new Date(this.arrive.getTime());
		return arrive;
	}

	public Date getTimeIn() {
		// function: returns in attribute
		// pre: in attribute must not be null
		// post: date instantiated with time of in attribute will be returned

		Date in = new Date(this.in.getTime());
		return in;
	}

	public Date getTimeOut() {
		// function: returns out attribute
		// pre: out attribute must not be null
		// post: date instantiated with time of out attribute will be returned

		Date out = new Date(this.out.getTime());
		return out;
	}
	
	public Element clone() {
		// function: returns a clone of the element
		// pre: arrive attribute must not be null
		// post: if in and out attributes are null, element instantiated with arrive attribute will be returned
			// if out attribute is null, element instantiated with arrive and in attributes will be returned
			// if arrive, in, and out attributes are not null, element instantiated with arrive, in, and out attributes will be returned

		Element element;
		if(in != null && out != null)
			element = new Element(arrive, in, out);
		else
			if(in != null)
				element = new Element(arrive, in);
			else
				element = new Element(arrive);
		return element;
	}
}

class Queue {
	private Element[] data;
	private int front, back;

	public Queue() { }

	public Queue(int size) {
		// function: instantiate queue
		// pre: none
		// post: queue will be created with usable slots equal to parameter

		create(size);
	}

	public void create(int size) {
		// function: create queue
		// pre: none
		// post: data attribute will be instantiated with elements equal to one more than parameter
			// queue will be usable

		data = new Element[size + 1];
		front = back = 0;
	}

	public void destroy() {
		// function: destroy queue
		// pre: none
		// post: each element of data attribute will be set to null
			// queue will not be usable

		for(Element element:data)
			element = null;
		front = back = -1;
	}

	public boolean isFull() {
		// function: checks whether queue is full
		// pre: queue must have been created
		// post: if queue has no space for additional elements, boolean of true will be returned
			// otherwise, false is returned

		boolean result;
		result = (back + 1) % data.length == front;
		return result;
	}

	public boolean enqueue(Element element) {
		// function: adds element to back of queue
		// pre: queue must have been created
		// post: if queue is full, boolean of false will be returned
			// if queue is not full, boolean of true will be returned, element of data attribute in position indicated by back attribute will be set to clone of parameter, and position of back attribute will be advanced

		boolean result;

		if(isFull())
			result = false;
		else {
			result = true;
			data[back] = element.clone();
			back = (back + 1) % data.length;
		}

		return result;
	}

	public Element dequeue() {
		// function: returns element from front of queue
		// pre: queue must have been created
		// post: if queue is empty, null will be returned
			// if queue is not empty, element of data attribute in position indicated by front attribute will be returned, said element will be set to null, and position of front attribute will be advanced

		Element result;

		if(isEmpty())
			result = null;
		else {
			result = data[front];
			data[front] = null;
			front = (front + 1) % data.length;
		}

		return result;
	}

	public boolean isEmpty() {
		// function: check if queue is empty
		// pre: queue must have been created
		// post: if queue contains no elements, boolean of true is returned
			// otherwise, boolean of false is returned

		boolean result;
		result = front == back;
		return result;
	}	
}