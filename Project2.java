
import java.util.concurrent.Semaphore;

public class Project2{

    ///////////////////////////////////
        // Declaring single Semaphores
    private static Semaphore WAIT_FOR_RECEPTIONIST;
    private static Semaphore REGISTER_WITH_RECEPTIONIST;
    private static Semaphore ASSING_DOCTORS_ROOM;
    private static Semaphore LEAVE_RECPTIONIST;

        // Declaring array of Sempahores // 
    // Patient and Nurse
    private static Semaphore PATIENTS_WAITING_FOR_NURSE [];

    // Receptionist and Nurse
    private static Semaphore TELL_NURSE_GET_PATIENT [];
    private static Semaphore WAIT_TILL_NURSE_GETS_INFO [];

    // Patient and Nurse
    private static Semaphore GET_AND_SEND_PATIENT[];

    // Doctor and Nurse
    private static Semaphore DOCTOR_READY[];
    private static Semaphore PATIENT_IN_ROOM[];

    // Doctor and Patient
    private static Semaphore DOCTOR_ARRIVES_ROOM[];
    private static Semaphore PATIENT_TALKS_DOCOTR[];
    private static Semaphore GIVE_PATIENT_ADIVE[];
    private static Semaphore GOODBYE_DOCTOR[];

    // Other variables for resources
	private static int current_patient_id;
    private static int [] patient_in_office;
    private static int [] patient_room_num;
    ///////////////////////////////////

    // Creating Variables for num of threasd
    private static int num_of_patients;
    private static int num_of_doctors;
    private static int num_of_nurses;

    // "checkInputErrors" checks in for incorrect user-input
    static void checkInputErrors(String[] args){

        // If arguments is not 2, exit program
        if(args.length != 2){
            System.out.println("Error, inccorect number of arguments: " + args.length);
            System.exit(0);
        }
        else{

            // If docotr or patient are more than what is required, close program
            if((Integer.parseInt(args[0]) > 3) || Integer.parseInt(args[1]) > 15){
                System.out.println("Incorrect number of patients or doctors.");
                System.out.println("Doctor: " + args[0]);
                System.out.println("Patients: " + args[1]);
                System.exit(0);
            }
        }
    }

    
    // Function "intializeSemaphores" initializes every global semaphore and other data types  
    static void intializeSemaphores(){

        // Patient and Receptionist
        WAIT_FOR_RECEPTIONIST = new Semaphore(0, true);
        REGISTER_WITH_RECEPTIONIST = new Semaphore(0, true);
        ASSING_DOCTORS_ROOM = new Semaphore(0, true);
        LEAVE_RECPTIONIST = new Semaphore(0, true);

        // Receptionist and Nurse
        TELL_NURSE_GET_PATIENT =  new Semaphore[num_of_nurses];
        WAIT_TILL_NURSE_GETS_INFO = new Semaphore[num_of_nurses];

        // Patient and Nurse
        GET_AND_SEND_PATIENT =  new Semaphore[num_of_patients];
        PATIENTS_WAITING_FOR_NURSE = new Semaphore[num_of_patients];

        // Doctor and Nurse
        DOCTOR_READY = new Semaphore[num_of_doctors];
        PATIENT_IN_ROOM = new Semaphore[num_of_doctors];

        // Patient and Doctor
        DOCTOR_ARRIVES_ROOM = new Semaphore[num_of_doctors]; 
        PATIENT_TALKS_DOCOTR = new Semaphore[num_of_doctors];
        GIVE_PATIENT_ADIVE = new Semaphore[num_of_doctors];
        GOODBYE_DOCTOR = new Semaphore[num_of_doctors];

        // Create an array of exactly n number of elements from user 1st input
        for(int i = 0; i < num_of_doctors; i++){  
            TELL_NURSE_GET_PATIENT[i] = new Semaphore(0, true);
            WAIT_TILL_NURSE_GETS_INFO[i] = new Semaphore(0, true);
            DOCTOR_READY[i] = new Semaphore(0, true);
            PATIENT_IN_ROOM[i] = new Semaphore(0, true);
            DOCTOR_ARRIVES_ROOM[i] = new Semaphore(0, true);
            PATIENT_TALKS_DOCOTR[i] = new Semaphore(0, true);
            GIVE_PATIENT_ADIVE[i] = new Semaphore(0, true);
            GOODBYE_DOCTOR[i] = new Semaphore(0, true);
        }

        // Create an array of exactly n number of elements from user 2nd input
        for(int i = 0; i < num_of_patients; i++){   
            PATIENTS_WAITING_FOR_NURSE[i] = new Semaphore(0, true);
            GET_AND_SEND_PATIENT[i] = new Semaphore(0, true);
        }

        // Integer variables, will be needed for keeping track of patients ID and which Doctor their assign
        patient_room_num = new int[num_of_patients];
        patient_in_office = new int[num_of_doctors];

    }


    //////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////
    public static void main(String[] args) {
         
        // Call function to check for user input errors
        checkInputErrors(args);

        // Turn string input into integers
        num_of_patients = Integer.parseInt(args[1]);
        num_of_nurses = num_of_doctors = Integer.parseInt(args[0]);

        // Print num of doctors, nurses, and patients
        System.out.printf("Run with " + num_of_doctors + " doctors, " + num_of_nurses + " nurses, " + num_of_patients + " patients\n\n");

        // Function intializes threads
        intializeSemaphores();
        
        // Initialize the Threads
        Thread patient_id[] = new Thread[num_of_patients];
        Thread nurses_id[] = new Thread[num_of_nurses];
        Thread doctor_id[] = new Thread[num_of_doctors];
        Thread receptionis = new Thread(new Receptionist_Desk());

        // Start receptionist
        receptionis.start();

        // Start patients
		for(int i = 0; i < num_of_patients; i++){

			patient_id[i] = new Thread(new Clinic(i));
			patient_id[i].start();
        }

        // Start Doctors and Nurses
		for(int i = 0; i < num_of_nurses; i++){
            // Nurses
            nurses_id[i] = new Thread(new Nurses(i));
			nurses_id[i].start();

            // Doctors
            doctor_id[i] = new Thread(new Doctors(i));
            doctor_id[i].start();
        }

        // Collected patient threads only, rest of the threads will be deleted when program closes
        for(int i = 0; i < num_of_patients; i++){
			try{
				patient_id[i].join();
			} catch (InterruptedException e) {}
		}

        System.out.println("\nSimulation  complete");
    
        // Leave program
        System.exit(0);
    }
    //////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////////////////////////////               
//////////////////////////////////////////////////////
    // Clinc class for patient threads
    static class Clinic implements Runnable{
        
        private int patient_id;
        private int doctors_room_num;
        
        // Assign ID of thread when thread is created
        public Clinic(int thread_id){
            this.patient_id = thread_id; 
        }

        public void run(){
            try {

                ///////////////////////////////
                // Communicatoin with the receptionist //
                ////////////////////////////////////////////////////
				//Patient enters and waits for receptionist
                WAIT_FOR_RECEPTIONIST.acquire();

                // Onces signaled, get id ready and pass it receptionist
                System.out.printf("Patient %d enters waiting room, waits for Receptionist. %n", this.patient_id);
                current_patient_id = this.patient_id;

                // Singal receptionist that patient is handing out ID
                REGISTER_WITH_RECEPTIONIST.release();

                // Wait until receptionist assigns us a doctors room number
                ASSING_DOCTORS_ROOM.acquire();
                this.doctors_room_num = patient_room_num[patient_id];
                System.out.printf("Patient %d leaves receptionist and sits in waiting room. %n", this.patient_id);
               
               // Leave receptionist desk
                LEAVE_RECPTIONIST.release();
                ////////////////////////////////////////////////////


                ///////////////////////////////
                // Communication with the Nurse // 
                ////////////////////////////////////////////////////
                // Wait for nurse to arrive and be guided to doctors room
                GET_AND_SEND_PATIENT[this.patient_id].release();
                ////////////////////////////////////////////////////


                ///////////////////////////////
                // Communication with Doctor // 
                ////////////////////////////////////////////////////
                // Wait for doctor to arrive
                DOCTOR_ARRIVES_ROOM[this.doctors_room_num].acquire();
                System.out.printf("Patient %d enters doctor %d's office. %n", this.patient_id, this.doctors_room_num);


                // Let doctor know about problem
                PATIENT_TALKS_DOCOTR[this.doctors_room_num].release();

                // Wait to hear doctors advice
                GIVE_PATIENT_ADIVE[this.doctors_room_num].acquire();
                System.out.printf("Patient %d receives advice from doctor %d. %n", this.patient_id, this.doctors_room_num);

                // Leave office
                GOODBYE_DOCTOR[this.doctors_room_num].release();
                System.out.printf("Patient %d leaves. %n", this.patient_id);
                ////////////////////////////////////////////////////
            }
            catch (InterruptedException e) {
                System.out.println("ERROR IN THREAD: " + this.patient_id + "...");
            }
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////               
//////////////////////////////////////////////////////
    static class Receptionist_Desk implements Runnable{
        private int room_number;

        public void run(){
            try {
                while(true){

                    ///////////////////////////////
                    // Communicatoin with the Patient //
                    ////////////////////////////////////////////////////                    
					//Receptionist waits for patient to enter
					WAIT_FOR_RECEPTIONIST.release();

                    // Wait until patient hands out ID t
                    REGISTER_WITH_RECEPTIONIST.acquire();

                    System.out.printf("Receptionist registers patient %d.%n", current_patient_id);
                
                    // Assign a random Doctor room number to patient
                    this.room_number = new java.util.Random().nextInt(num_of_doctors);
                    patient_room_num[current_patient_id] = this.room_number;

                    // Signal patient of which room to give them
                    ASSING_DOCTORS_ROOM.release();
                    ////////////////////////////////////////////////////                    
            

                    ///////////////////////////////
                    // Communicatoin with the Nurse //
                    ////////////////////////////////////////////////////      
                    // Singal nurse to get patients info and signal them to get patient
                    TELL_NURSE_GET_PATIENT[this.room_number].release();
                    
                    // Wait until nurse gets patients info
                    WAIT_TILL_NURSE_GETS_INFO[this.room_number].acquire();

                    // Wait until patient is ready to leave front desk
                    LEAVE_RECPTIONIST.acquire();
                    ////////////////////////////////////////////////////
                }
            }
            catch (InterruptedException e) {
                System.out.println("ERROR IN RECEPTIONIST THREAD...");
            }
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////               
//////////////////////////////////////////////////////
    static class Nurses implements Runnable{

        private int nurse_id;
        private int collect_patient_id;

        // Assign nurse ID when thread is created
        public Nurses(int nurse_id){
            this.nurse_id = nurse_id;
        }
        
        public void run(){
            try {
                while(true){

                    ///////////////////////////////
                    // Communicatoin with the Receptionist //
                    ////////////////////////////////////////////////////      
                    // Notify Nurse from receptionist that patient is waiting
                    TELL_NURSE_GET_PATIENT[this.nurse_id].acquire();

                    // Collect Patient ID
                    this.collect_patient_id = current_patient_id;
                    patient_in_office[this.nurse_id] = this.collect_patient_id;
                    
                    // Signal receptionist that info is collected
                    WAIT_TILL_NURSE_GETS_INFO[this.nurse_id].release();
                    ////////////////////////////////////////////////////


                    ///////////////////////////////
                    // Communicatoin with the Doctor //
                    ////////////////////////////////////////////////////   
                    // Now wait till Doctors office is clear
                    DOCTOR_READY[this.nurse_id].acquire();
                    ////////////////////////////////////////////////////   


                    ///////////////////////////////
                    // Communicatoin with the Patient //
                    ////////////////////////////////////////////////////   
                    // Go get patient and wait for them
                    GET_AND_SEND_PATIENT[this.collect_patient_id].acquire();

					System.out.printf("Nurse %d takes patient %d to doctor's office. %n", this.nurse_id, this.collect_patient_id);
					//System.out.printf("Patient %d enters doctor %d's office. %n", this.collect_patient_id, this.nurse_id);

                    ////////////////////////////////////////////////////


                    ///////////////////////////////
                    // Communicatoin with the Doctor //
                    ////////////////////////////////////////////////////   
                    // Notify Doctor that patient is in waiting
                    PATIENT_IN_ROOM[this.nurse_id].release();
                    ////////////////////////////////////////////////////
                }
            }
            catch (InterruptedException e) {
                System.out.println("ERROR IN NURSE" + this.nurse_id + "THREAD...");
            }
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////               
//////////////////////////////////////////////////////
    static class Doctors implements Runnable{
        private int doctor_id;

        // Assign nurse ID when thread is created
        public Doctors(int doctor_id){
            this.doctor_id = doctor_id;
        }
        
        public void run(){
            try {
                while(true){

                    ///////////////////////////////
                    // Communicatoin with the Nurse //
                    ////////////////////////////////////////////////////   
                    // Doctor is ready to take pations
                    DOCTOR_READY[this.doctor_id].release();

                    // Nurse notifes Doctor that patient is in room
                    PATIENT_IN_ROOM[this.doctor_id].acquire();
                    ////////////////////////////////////////////////////   

    
                    ///////////////////////////////
                    // Communicatoin with the Patient //
                    ////////////////////////////////////////////////////   
                    // Doctor arrives in Room
                    DOCTOR_ARRIVES_ROOM[this.doctor_id].release();

                    // Let doctor know about problem
                    PATIENT_TALKS_DOCOTR[this.doctor_id].acquire();
                    System.out.printf("Doctor %d listens to symptoms from patient %d. %n", this.doctor_id, patient_in_office[this.doctor_id]);

                    // Give patient advice
                    GIVE_PATIENT_ADIVE[this.doctor_id].release();

                    // Wait till patient is ready to leave office
                    GOODBYE_DOCTOR[this.doctor_id].acquire();
                    ////////////////////////////////////////////////////                   
                }
            }
            catch (InterruptedException e) {
                System.out.println("ERROR IN DOCTOR" + this.doctor_id + "THREAD...");
            }
        }
    }


}
