
#include <iostream>     // Only using cout and endl
#include <stdio.h>      // Only for printf
#include <pthread.h>      // POSIX thread library
#include <semaphore.h>  // POSIX semaphore library

using std::cout;
using std::endl;

/*
Receptionist – one thread
Doctor – one thread each, maximum of 3 doctors
Nurse – one per doctor thread, identifier of doctor and corresponding nurse should match
Patient – one thread each, up to 15 patients

"project2.cpp (number of doctors) (number of patients)

*/


void *testing(void *);

void *clinic( void *);


int main(int argc, char* argv[]) { 


    // Check command line input
    // If we inccorect input, exit program
    try{
        if(argc != 3){
            printf("Incorrect Command-Line Input...\n");    
            throw argc;
        }
    }
    catch (int argc){
        printf("Terminal Received: %d arguments, needs to be 2", argc - 1);  
        return 0;
    }




    /*
    pthread_t testing1;
    int variable_change = 10;

    void *testing_again;

    pthread_create(&testing1, NULL, testing, NULL);
    pthread_join(testing1, &testing_again);

    cout << *(int *)testing_again << endl;
    cout << variable_change << endl;
    */

    return 0;
}





void *testing(void *){

    int *testing = new int;
    *testing = 199;

    //cout << *(int *) testing << endl;
    return (void*) testing;
}
