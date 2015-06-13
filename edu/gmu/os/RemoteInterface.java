/*
Gaurav Desai
G00851337
*/
package edu.gmu.os;

import java.rmi.*; 

public interface RemoteInterface extends Remote {
    String lookUpAuditorium (String date) throws RemoteException;
    String reserveAuditorium(String date) throws RemoteException;
    boolean canEnterCS() throws RemoteException;
    String executeOperation(String operation, String date) throws RemoteException,InterruptedException, NotBoundException; 
    void processToken(String token) throws RemoteException,NotBoundException,InterruptedException;
}
