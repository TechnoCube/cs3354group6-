package com.alamkanak.weekview.sample;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Alternative to SQL Database or Content Manager.
 * Utilizes a linked list to store events when the app runs,
 * and writes to a file when the app closes.
 * DO NOT INSTANTIATE, ONLY USE STATIC METHODS.
 * IMPORT: android.content.Context (if using file operations)
 */
abstract class EventManager {
    static private LinkedList<Event> eventData; // Linked list of events
    final static public String EVENT_FILENAME = new String ("BADCAL.DAT"); // Filename

    // Method to initialize Event Manager for use without reading from
    // or writing to a file.
    // MUST BE CALLED BEFORE ANY OPERATIONS IF NOT USING FILES
    static public boolean noFileStart() {
        boolean operationSuccess = true; // LinkedList creation flag

        // Check if list has already been created
        if (eventData == null)
            eventData = new LinkedList<Event>();
        else
            operationSuccess = false;

        return operationSuccess;
    }

    static public boolean noFileEnd() {
        boolean operationSuccess = true; // LinkedList destruction flag

        // Check if LinkedList already exists
        if (eventData != null) {
            eventData.clear();
            eventData = null;
        }
        else
            operationSuccess = false;

        return operationSuccess;
    }

    // Load File Method; returns true if read succeeded or generated empty list
    // MUST BE CALLED BEFORE ANY EVENT MANAGER OPERATIONS
    // USE: EventManager.loadFile(new File(Context.getFilesDir(), EventManager.EVENT_FILENAME))
    static public boolean loadFile(File localFile) {
        eventData = new LinkedList<Event>(); // Empty event list
        String eventLine; // File read string
        Event readEvent; // Read event object
        boolean operationSuccess = true; // File operation flag
        BufferedReader eventBuffer = null; // Buffer for file reader

        // Attempt file read operations
        try {
            // Read from file and append to linked list
            eventBuffer = new BufferedReader(new FileReader(localFile));
            while((eventLine = eventBuffer.readLine()) != null) {
                // Get Start Time
                readEvent = new Event();
                readEvent.setStartTime(new Date(Long.parseLong(eventLine)));

                // Get End Time
                eventLine = eventBuffer.readLine();
                readEvent.setEndTime(new Date(Long.parseLong(eventLine)));

                // Get Title
                eventLine = eventBuffer.readLine();
                readEvent.setTitle(eventLine);

                // Get Description
                eventLine = eventBuffer.readLine();
                readEvent.setTitle(eventLine);

                // Add completed event to list
                eventData.add(readEvent);
            }
        }
        catch (FileNotFoundException excFNF) {
            // File does not exist, empty event list
            eventData.clear();
            try {
                if (eventBuffer != null)
                    eventBuffer.close();
            }
            catch (IOException excClose){
                // Unable to handle close
            }
        }
        catch (IOException excIO) {
            // Unable to read file, set event list to null
            eventData = null;
            operationSuccess = false;

            try {
                if (eventBuffer != null)
                    eventBuffer.close();
            }
            catch (IOException excClose){
                // Unable to handle close
            }
        }

        return operationSuccess;
    }

    // Save File Method; returns true if write succeeded
    // MUST BE CALLED BEFORE CLOSING APPLICATION
    // USE: EventManager.saveFile(new File(Context.getFilesDir(), EventManager.EVENT_FILENAME))
    static public boolean saveFile(File localFile) {
        Event writeEvent; // Write event object
        boolean operationSuccess = true; // File operation flag
        BufferedWriter eventBuffer = null; // Buffer to contain file writer

        // Attempt file write operations
        try {
            eventBuffer = new BufferedWriter(new FileWriter(localFile));
            // Obtain iterator
            ListIterator<Event> eventIterator = eventData.listIterator();

            // Write to file
            while (eventIterator.hasNext()) {
                // Obtain element using iterator
                writeEvent = eventIterator.next();

                // Set Start Time
                eventBuffer.write(Long.toString(writeEvent.getStartTime().getTime()));
                eventBuffer.newLine();

                // Set End Time
                eventBuffer.write(Long.toString(writeEvent.getEndTime().getTime()));
                eventBuffer.newLine();

                // Set Title
                eventBuffer.write(writeEvent.getTitle());
                eventBuffer.newLine();

                // set Description
                eventBuffer.write(writeEvent.getDescription());
                eventBuffer.newLine();
            }
        }
        catch (IOException excIO) {
            // Unable to write to file
            operationSuccess = false;

            try {
                if (eventBuffer != null)
                    eventBuffer.close();
            }
            catch (IOException excClose) {
                // Unable to handle close
            }
        }

        return operationSuccess;
    }

    // Check if Empty Method; returns true if manager contains no events
    static public boolean isEmpty() {
        boolean emptyFlag = true; // Flag to signal empty list
        // Check if list exists
        if (eventData != null)
            emptyFlag = eventData.isEmpty();
        return emptyFlag;
    }

    // Add Event Method; returns true if event was added
    static public boolean addEvent(Event newEvent) {
        ListIterator<Event> eventIterator = eventData.listIterator(); // List iterator of events
        Event nextEvent = null, prevEvent = null, tempEvent = null; // Events for comparisons
        long newStartMillis = newEvent.getStartTime().getTime(); // New Event's start time
        long newEndMillis = newEvent.getEndTime().getTime(); // New Event's end time
        boolean operationSuccess = true; // Add operation success flag

        // Check if list is empty
        if (eventData.isEmpty())
            eventData.add(newEvent); // Empty list, just add event
        else {
            // Find where to insert element
            while (eventIterator.hasNext()) {
                // Obtain event from iterator
                tempEvent = eventIterator.next();

                // Check for where existing events begin in relation to new event
                if (newStartMillis < tempEvent.getStartTime().getTime()) {
                    // Found next event, check for previous event and break
                    nextEvent = tempEvent;
                    tempEvent = eventIterator.previous();
                    if (eventIterator.hasPrevious())
                        prevEvent = eventIterator.previous();
                    break;
                }
            }
            if (nextEvent == null) {
                // New event should be last, check for overlap
                if (newStartMillis < eventData.getLast().getEndTime().getTime())
                    operationSuccess = false;
                else
                    eventData.addLast(newEvent);
            }
            else if (prevEvent == null) {
                // New event should be first, check for overlap
                if (newEndMillis > eventData.getFirst().getStartTime().getTime())
                    operationSuccess = false;
                else
                    eventData.addFirst(newEvent);
            }
            else {
                // Event is between next and previous, check for overlaps
                if (newStartMillis < prevEvent.getEndTime().getTime() || newEndMillis > nextEvent.getStartTime().getTime())
                    operationSuccess = false;
                else
                    eventData.add(eventData.indexOf(nextEvent), newEvent);
            }
        }

        return operationSuccess;
    }

    // Remove Event Method; returns true if event was removed
    static public boolean removeEvent(Event deleteEvent) {
        // Remove event with list method
        return eventData.remove(deleteEvent);
    }

    // Get Event After Time Method; returns first event at or after given date, or null for no date after
    static public Event getAfterTime(Date time) {
        ListIterator<Event> eventIterator = eventData.listIterator(); // List iterator of events
        Event afterEvent; // Event after given time
        long afterTimeMillis = time.getTime(); // Time of given date object

        // Find event after time
        while (eventIterator.hasNext()) {
            // Get next and check if at right date
            afterEvent = eventIterator.next();
            if (afterTimeMillis <= afterEvent.getStartTime().getTime())
                return afterEvent;
        }

        // Unable to find event
        return null;
    }

    // Get Event Before Time Method; returns first event at or before given date
    static public Event getBeforeTime(Date time) {
        ListIterator<Event> eventIterator = eventData.listIterator(eventData.size()); // List iterator of events
        Event beforeEvent; // Event before given time
        long beforeTimeMillis = time.getTime(); // Time of given date object

        // Find event before time
        while (eventIterator.hasPrevious()) {
            // Get next and check if at right date
            beforeEvent = eventIterator.previous();
            if (beforeTimeMillis <= beforeEvent.getStartTime().getTime())
                return beforeEvent;
        }

        // Unable to find event
        return null;
    }

    // Get Events Between Times Method; returns all events between given dates
    static public LinkedList<Event> getBetweenTimes (Date start, Date end) {
        LinkedList<Event> eventList = new LinkedList<Event>(); // List to populate and return
        ListIterator<Event> eventIterator = eventData.listIterator();
        Event tempEvent; // Temporary event to compare against
        long startTimeMillis = start.getTime(); // Time of given start date
        long endTimeMillis = end.getTime(); // time of given end date

        // Find events from list
        while (eventIterator.hasNext()) {
            // Get event and check if it is between boundaries
            tempEvent = eventIterator.next();
            if (startTimeMillis < tempEvent.getStartTime().getTime()) {
                // Found first element, populate and break
                eventList.add(tempEvent);
                break;
            }
        }
        // Find rest of elements to populate
        while (eventIterator.hasNext()) {
            tempEvent = eventIterator.next();

            // Check if beyond end date yet
            if (endTimeMillis >= tempEvent.getStartTime().getTime())
                eventList.add(tempEvent);
            else
                break;
        }

        return eventList;
    }
}

/**
 * Event object to be stored in EventManager's linked list.
 * May instantiate elsewhere if needed.
 */
class Event {
    private Date eventStart; // Event start time
    private Date eventEnd; // event end time
    private String eventTitle; // Event title
    private String eventDescription; // Event description
    private int eventColor; // Event category

    // Default Constructor
    public Event() {
        // Set Date and String objects to default values
        eventStart = new Date();
        eventEnd = new Date();
        eventTitle = new String();
        eventDescription = new String();
        eventColor = 0;
    }

    // Copy Constructor
    public Event(Event original) {
        // Copy values from given Event object
        this.eventStart = original.eventStart;
        this.eventEnd = original.eventEnd;
        this.eventTitle = original.eventTitle;
        this.eventDescription = original.eventDescription;
        this.eventColor = original.eventColor;
    }

    // Initial Data Complete Constructor
    public Event(Date initialStart, Date initialEnd, String initialTitle, String initialDescription) {
        // Assign all values to fields
        eventStart = initialStart;
        eventEnd = initialEnd;
        eventTitle = initialTitle;
        eventDescription = initialDescription;
        eventColor = 0;
    }

    // Initial Times Object Partial Constructor
    public Event(Date initialStart, Date initialEnd) {
        // Call complete constructor using defaults for missing values
        this(initialStart, initialEnd, new String(), new String());
    }

    // Initial Title and Description Partial Constructor
    public Event(String initialTitle, String initialDescription) {
        // Call complete constructor using defaults for missing values
        this(new Date(), new Date(), initialTitle, initialDescription);
    }

    // Initial Start Time as Strings Constructor
    public Event(String year, String month, String day, String hour, String minute) {
        // Call set method and use defaults for title and description
        setStartTime(year, month, day, hour, minute);
        eventEnd = new Date();
        eventTitle = new String();
        eventDescription = new String();
        eventColor = 0;
    }

    // Get Start Time Method
    public Date getStartTime() {
        // Return start date object
        return eventStart;
    }

    // Get End Time Method
    public Date getEndTime() {
        // Return end date object
        return eventEnd;
    }

    // Get Start Year String Method
    @SuppressWarnings("deprecation")
    public String getStartYear() {
        // Obtain year
        Integer numericYear;
        numericYear = eventStart.getYear() + 1900;
        return numericYear.toString();
    }

    // Get End Year String Method
    @SuppressWarnings("deprecation")
    public String getEndYear() {
        // Obtain year
        Integer numericYear;
        numericYear = eventEnd.getYear() + 1900;
        return numericYear.toString();
    }

    // Get Start Month String Method
    @SuppressWarnings("deprecation")
    public String getStartMonth() {
        // Obtain month
        int numericMonth;
        String stringMonth;
        numericMonth = eventStart.getMonth();

        // Switch to set month value
        switch(numericMonth) {
            case 0:
                stringMonth = "January";
                break;
            case 1:
                stringMonth = "February";
                break;
            case 2:
                stringMonth = "March";
                break;
            case 3:
                stringMonth = "April";
                break;
            case 4:
                stringMonth = "May";
                break;
            case 5:
                stringMonth = "June";
                break;
            case 6:
                stringMonth = "July";
                break;
            case 7:
                stringMonth = "August";
                break;
            case 8:
                stringMonth = "September";
                break;
            case 9:
                stringMonth = "October";
                break;
            case 10:
                stringMonth = "November";
                break;
            case 11:
                stringMonth = "December";
                break;
            default:
                stringMonth = new String();
                break;
        }

        return stringMonth;
    }

    // Get End Month String Method
    @SuppressWarnings("deprecation")
    public String getEndMonth() {
        // Obtain month
        int numericMonth;
        String stringMonth;
        numericMonth = eventEnd.getMonth();

        // Switch to set month value
        switch(numericMonth) {
            case 0:
                stringMonth = "January";
                break;
            case 1:
                stringMonth = "February";
                break;
            case 2:
                stringMonth = "March";
                break;
            case 3:
                stringMonth = "April";
                break;
            case 4:
                stringMonth = "May";
                break;
            case 5:
                stringMonth = "June";
                break;
            case 6:
                stringMonth = "July";
                break;
            case 7:
                stringMonth = "August";
                break;
            case 8:
                stringMonth = "September";
                break;
            case 9:
                stringMonth = "October";
                break;
            case 10:
                stringMonth = "November";
                break;
            case 11:
                stringMonth = "December";
                break;
            default:
                stringMonth = new String();
                break;
        }

        return stringMonth;
    }

    // Get Start Day String Method
    @SuppressWarnings("deprecation")
    public String getStartDay() {
        // Obtain day
        Integer numericDay;
        numericDay = eventStart.getDate();
        return numericDay.toString();
    }

    // Get Start Day String Method
    @SuppressWarnings("deprecation")
    public String getEndDay() {
        // Obtain day
        Integer numericDay;
        numericDay = eventEnd.getDate();
        return numericDay.toString();
    }

    // Get Start Hour String Method
    @SuppressWarnings("deprecation")
    public String getStartHour() {
        // Obtain hour
        Integer numericHour;
        numericHour = eventStart.getHours();
        return numericHour.toString();
    }

    // Get End Hour String Method
    @SuppressWarnings("deprecation")
    public String getEndHour() {
        // Obtain hour
        Integer numericHour;
        numericHour = eventEnd.getHours();
        return numericHour.toString();
    }

    // Get Minute String Method
    @SuppressWarnings("deprecation")
    public String getStartMinute() {
        // Obtain minute
        Integer numericMinute;
        numericMinute = eventStart.getMinutes();
        return numericMinute.toString();
    }

    // Get Minute String Method
    @SuppressWarnings("deprecation")
    public String getEndMinute() {
        // Obtain minute
        Integer numericMinute;
        numericMinute = eventEnd.getMinutes();
        return numericMinute.toString();
    }

    // Get Event Title Method
    public String getTitle() {
        // Return event title
        return eventTitle;
    }

    // Get Event Description Method
    public String getDescription() {
        // Return event description
        return eventDescription;
    }

    // Get Event Category Method
    public int getColor() {
        // Return event category
        return eventColor;
    }

    // Set Start and End Times Method
    public void setTimes(Date newStart, Date newEnd) {
        // Set start and end dates
        eventStart = newStart;
        eventEnd = newEnd;
    }

    // Set Start Time with Object Method
    public void setStartTime(Date newStart) {
        // Set event date
        eventStart = newStart;
    }

    // Set Start Time with Strings Method
    @SuppressWarnings("deprecation")
    public void setStartTime(String newYear, String newMonth, String newDay, String newHour, String newMinute) {
        // Construct new date object
        Date newStart = new Date();

        // Set Year
        newStart.setYear(Integer.parseInt(newYear) + 1900);

        // Set Month
        switch (newMonth) {
            case "January":
                newStart.setMonth(0);
                break;
            case "February":
                newStart.setMonth(1);
                break;
            case "March":
                newStart.setMonth(2);
                break;
            case "April":
                newStart.setMonth(3);
                break;
            case "May":
                newStart.setMonth(4);
                break;
            case "June":
                newStart.setMonth(5);
                break;
            case "July":
                newStart.setMonth(6);
                break;
            case "August":
                newStart.setMonth(7);
                break;
            case "September":
                newStart.setMonth(8);
                break;
            case "October":
                newStart.setMonth(9);
                break;
            case "November":
                newStart.setMonth(10);
                break;
            case "December":
                newStart.setMonth(11);
                break;
            default:
                newStart.setMonth(-1);
                break;
        }

        // Set Day
        newStart.setDate(Integer.parseInt(newDay));

        // Set Hour
        newStart.setHours(Integer.parseInt(newHour));

        // Set Minute
        newStart.setMinutes(Integer.parseInt(newMinute));

        eventStart = newStart;
    }

    // Set Start Time with Object Method
    public void setEndTime(Date newEnd) {
        // Set event date
        eventStart = newEnd;
    }

    // Set Start Time with Strings Method
    @SuppressWarnings("deprecation")
    public void setEndTime(String newYear, String newMonth, String newDay, String newHour, String newMinute) {
        // Construct new date object
        Date newEnd = new Date();

        // Set Year
        newEnd.setYear(Integer.parseInt(newYear) + 1900);

        // Set Month
        switch (newMonth) {
            case "January":
                newEnd.setMonth(0);
                break;
            case "February":
                newEnd.setMonth(1);
                break;
            case "March":
                newEnd.setMonth(2);
                break;
            case "April":
                newEnd.setMonth(3);
                break;
            case "May":
                newEnd.setMonth(4);
                break;
            case "June":
                newEnd.setMonth(5);
                break;
            case "July":
                newEnd.setMonth(6);
                break;
            case "August":
                newEnd.setMonth(7);
                break;
            case "September":
                newEnd.setMonth(8);
                break;
            case "October":
                newEnd.setMonth(9);
                break;
            case "November":
                newEnd.setMonth(10);
                break;
            case "December":
                newEnd.setMonth(11);
                break;
            default:
                newEnd.setMonth(-1);
                break;
        }

        // Set Day
        newEnd.setDate(Integer.parseInt(newDay));

        // Set Hour
        newEnd.setHours(Integer.parseInt(newHour));

        // Set Minute
        newEnd.setMinutes(Integer.parseInt(newMinute));

        eventStart = newEnd;
    }

    // Set Event Title Method
    public void setTitle(String newTitle) {
        // Set event title
        eventTitle = newTitle;
    }

    // Set Event Description Method
    public void setDescription(String newDescription) {
        // Set event description
        eventDescription = newDescription;
    }

    // Set Event Category Method
    public void setColor(int newColor) {
        // Set event category
        eventColor = newColor;
    }
}