import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.io.File;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import java.lang.String;

class Course implements Comparable<Course> {
    
    public String course_name;
    public String prof_name;
    public List<Integer> preference;
    public int cap;
    public List<String> clashes_course;
    public Course(String coursename, String professor_name, String teaching_preference, String capacity, String clash) { //course constructor
        
        course_name = coursename.toUpperCase();
        prof_name = professor_name;
        cap = Integer.parseInt(capacity);
        preference = new ArrayList();
        clashes_course = new ArrayList();
        
        for (String s : teaching_preference.split(", ")) {
            
            preference.add(Integer.parseInt(s));
        }
        
        //Collections.shuffle(preference); //shuffling the preference list
        
        for (String str : clash.split(",")) {
            
            clashes_course.add(str.toUpperCase());
        }
    }

    //compareTo method sorts the Course objects based on the size of preference list
     @Override
    public int compareTo(Course o) {
        
        return preference.size() - o.preference.size();
    }
}

class Room {
    
    public int ser;
    public String roomname;
    public int cap;
    public Room(String serial, String room_name, String capacity) {
        
        ser = Integer.parseInt(serial);
        roomname = room_name;
        cap = Integer.parseInt(capacity);
    }
}

public class Allocator {
    
    public static final String[] FILE_HEADER_MAPPING = {"Course", "Professor Name", "Teaching Preference", "Capacity", "Clashes"};
    public static final String[] ROOM_MAPPING = {"Serial", "Classroom", "Capacity"};
    public static final String[] SLOT_MAPPING = {"Slot Number", "Day", "Duration"}; //change this now ***********
    public static List courseobj = new ArrayList(); // List containing all the course objects
    public static final ArrayList<Room> ROOMS = new ArrayList<>(); // ArrayList containing all room objects
    public static final ArrayList<String> SLOTS = new ArrayList<>();
    public static String[][] coursealloc; //matrix containing allocated courses


    /**
     * @param coursesfile refers to the file containing course name, professor name, teaching preference, capacity, clashes
     * @throws IOException
     * @throws NullPointerException
     * @throws IndexOutOfBoundsException
     *
     * parseFileAndPrintOutput method takes in 'coursefile' of type file as input and returns nothing. It prints out the output (coursealloc matrix)
     */

    public void parseFileAndPrintOutput(File coursesfile) throws IOException, NullPointerException, IndexOutOfBoundsException {


        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(FILE_HEADER_MAPPING); // excel file first row
        FileReader fileReader = new FileReader(coursesfile); //starting a new FileReader
        CSVParser csvFileParser = new CSVParser(fileReader, csvFileFormat);
        List csvRecords = csvFileParser.getRecords();
        Course course; // making an object course of type Course

        //start reading coursesfile
        for (int i = 1; i < csvRecords.size(); i++) { //looping through each row of coursesfile
            
            CSVRecord record = (CSVRecord) csvRecords.get(i);
            // making a course object with object fields Course, Professor Name, Teaching Preference, Capacity, and Clashses
            course = new Course(record.get("Course"), record.get("Professor Name"), record.get("Teaching Preference"),record.get("Capacity"),record.get("Clashes"));
            courseobj.add(course);
        }

        //sorting the courseobj list in ascending order in order to increase the performance of the algorithm
        Collections.sort(courseobj);

        //calling allocate method
        if (allocate(0)) {  // if allocate returns true (meaning that all the courses have been allocated), the below code gets executed
            
            for (int i = 0; i < SLOTS.size(); i++) {
                
                for (int j = 0; j < ROOMS.size(); j++) {
                    
                    System.out.println(coursealloc[i][j] + " : " + SLOTS.get(i) + " : " + ROOMS.get(j).roomname); //prints out output in order <coursename : slot : roomname>
                }
            }
        }
        
        else {
            
            System.out.println("There is no possible allocation.");
        }

    } //end of parseFileAndPrintOutput method


    public void roomToCapacity(File roomsfile) throws IOException, FileNotFoundException { //Reading File2 (ROOMS.csv)
        
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(ROOM_MAPPING);
        FileReader fileReader = new FileReader(roomsfile);
        CSVParser csvFileParser1 = new CSVParser(fileReader, csvFileFormat);
        List csvRecords = csvFileParser1.getRecords();
        
        for (int i = 1; i < csvRecords.size(); i++) { //goes through each row
            
            CSVRecord record = (CSVRecord) csvRecords.get(i); //of type CSVRecord
            //creating a room object with fields Serial, Classroom, and Capacity
            Room room = new Room(record.get("Serial"), record.get("Classroom"), record.get("Capacity"));
            //adding room object to ROOMS list
            ROOMS.add(room);
        }
    }

    public void slotsToTimings(File timingsfile) throws IOException, FileNotFoundException {
        
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(SLOT_MAPPING);
        FileReader fileReader = new FileReader(timingsfile);
        CSVParser csvFileParser2 = new CSVParser(fileReader, csvFileFormat);
        List csvRecords = csvFileParser2.getRecords();
        
        for (int i = 1; i < csvRecords.size(); i++) { //goes through each row
            
            CSVRecord record = (CSVRecord) csvRecords.get(i); //of type CSVRecord
            SLOTS.add(record.get("Day") + " " + record.get("Duration"));
        }
    }

    /**
     * @param courseNum refers to course number
     * @return
     * input: courselist <Course>, state of the matrix (few cells maybe assigned and few may not be unallocated)
     * returns true when coursealloc matrix has been correctly assigned with input number of courses
     * returns false: couldn't find an assignment for all input courses. If so, it leaves the state when the function was called
     */

    public boolean allocate(int courseNum) {
        // base case: no courses, so can return true
        if (courseNum == courseobj.size())
            
            return true;
        
        Course firstcourse = (Course) courseobj.get(courseNum); //setting lastcourse to last course in the courseobj list
        
        for (int currentslot : firstcourse.preference) { //loops through faculty preference

            for (int room = 0; room < ROOMS.size(); room++) { //looping through matrix

                // 1) if the room is occupied at the current time slot or course capacity > room capacity: go to next room

                if (coursealloc[currentslot - 1][room] != null || firstcourse.cap > ROOMS.get(room).cap) {
                    
                    continue;
                }

                // 2) check if any clashing course exist in the same slot: chuck the room; go to next room //clashing courses is wrong

                boolean clashes = false;
                
                for (int roomcheck = 0; roomcheck < ROOMS.size(); roomcheck++) {
                    
                    if (firstcourse.clashes_course.contains(coursealloc[currentslot - 1][roomcheck])) {
                        
                        clashes = true;
                    }
                }

                if (clashes) {
                    
                    break;
                }

                //3) place the course in the room at that slot

                coursealloc[currentslot - 1][room] = firstcourse.course_name + " : " + firstcourse.prof_name;

                int updatedcourseobj = courseNum + 1;

            /*4) allocate the remaining courses
                 a) TRUE: return true
                 b) FALSE: remove the course allocated in 3. */

                if (allocate(updatedcourseobj)) {
                    
                    return true;
                }
                
                else {
                    
                    coursealloc[currentslot - 1][room] = null; //reset allocated course
                }
            }
        }
        return false;
    }


    public static void main(String[] args) throws IOException, NullPointerException, IndexOutOfBoundsException, FileNotFoundException {
        
        File coursesfile = new File(args[0]);
        File roomsfile = new File(args[1]);
        File timingsfile = new File(args[2]);
        Allocator start = new Allocator();
        
        start.roomToCapacity(roomsfile); //setting up ROOMS ArrayList
        start.slotsToTimings(timingsfile); //setting up SLOTS ArrayList
        coursealloc = new String[SLOTS.size()][ROOMS.size()]; //setting the size of the matrix

        // setting each cell of the coursealloc matrix to null
        
        for (int i = 0; i < SLOTS.size();i++) {
            
            for (int j = 0; j < ROOMS.size();j++) {
                
                coursealloc[i][j] = null;
            }
        }
        
        start.parseFileAndPrintOutput(coursesfile);
    }
}

