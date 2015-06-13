
/*
Gaurav Desai
G00851337
*/

package edu.gmu.os;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.StringTokenizer;

public class RMIClient {
      
	static public void main(String args[]) throws IOException, InterruptedException { 
          RemoteInterface rmiServer;   
          Registry registry;   
          int serverPort; 
          String input;   
          String date;
          
          String response = null;
          String operation = null;
          
          BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
          
          System.out.print("\nEnter the server port to connect to: ");
	  	  serverPort = new Integer(in.readLine()).intValue();
	  	  
          while(true) { 
              try {
            	  	  System.out.print("\n=========================================================================");
	            	  System.out.println("\nSelect the operation from options below:");
	                  System.out.println("\nLook-up (press 1)");
	                  System.out.println("\nReserve (press 2)");
	                  System.out.println("\nExit    (press 3)");
	                  System.out.print("\nYour Option: ");
	                  
	                  if((input = in.readLine()) != null){
	        	        	  if(input.equals("1"))
	        	        		  operation = "Look-up";
	        	        	  else if(input.equals("2"))
	        	        		  operation = "Reserve";
	        	        	  else if(input.equals("3"))
	        	        		  System.exit(0);
	        	        	  else
	        	        		  System.out.println("\nEnter correct option please...");
	                  }
	                  
	                  System.out.print("\nEnter the date for the month of december: ");
                  	  date = in.readLine();
                  	  
                      registry = LocateRegistry.getRegistry("localhost",serverPort); 
                      rmiServer = (RemoteInterface)(registry.lookup("rmi"));
                      response = rmiServer.executeOperation(operation,date);
                      
                      System.out.println(response);
                      System.out.println("=========================================================================\n");
                  
              } catch(RemoteException e) {
            	  
              } catch(NotBoundException e) {
            	  
              } catch (IOException ex) {
            	  
              }
          }
      }
}