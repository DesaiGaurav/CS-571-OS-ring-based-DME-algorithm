/*
Gaurav Desai
G00851337
*/
package edu.gmu.os;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Department extends UnicastRemoteObject implements RemoteInterface, Runnable{ 
             
		private static final long serialVersionUID = 1L;
		RemoteInterface remoteInterface;       
        Registry registry;  
        
        public static int myPort;
        public static int nextNodePort;
        public static int prevNodePort;
        
        public static String topologyFile;
        public static String scheduleFile;
        public static String status = "Auditorium is Available";
        public static String deptName;
        
        public static String token="NA";
        public static int d1,d2;
        private String date;
        private String operation;
        private boolean requestArrived = false;
        private boolean flag = false;
        
        public Department() throws RemoteException {
        }
        
        public void Initialize() throws RemoteException {
            try {
                registry = LocateRegistry.createRegistry(myPort);
                registry.rebind("rmi", this);
                System.out.println("\n\tServer Ready, Port Number " + myPort);
            } catch(RemoteException e){
                throw e;
            }
        }
        
        public static void main(String args[]) throws RemoteException {
        	Department d = new Department();
            try{
            	topologyFile = args[0];
            	d1= Integer.parseInt(args[1]);
            	d2= Integer.parseInt(args[2]);
            	boolean processToken = false;
            	
            	BufferedReader br = null;
            	try {
    				br = new BufferedReader(new FileReader(topologyFile));
    				String line = br.readLine();
    	            do{
    	            	String[] temp = line.split("=");
    	            	if(temp[0].equals("DepartmentName")){
    	            		deptName = temp[1];
    	            	}else if(temp[0].equals("ScheduleFileLocation")){
    	            		scheduleFile = temp[1];
    	            	}else if(temp[0].equals("CurrentNode")){
    	            		myPort = Integer.parseInt(temp[1]);
    	            	}else if(temp[0].equals("NextNode")){
    	            		nextNodePort = Integer.parseInt(temp[1]);
    	            	}else if(temp[0].equals("PreviousNode")){
    	            		prevNodePort = Integer.parseInt(temp[1]);
    	            	}else if(temp[0].equals("FirstTokenGeneration")){
    	            		if(temp[1].equals("true")){
    	            			processToken = true;
    	            		}
    	            		else
    	            			token = "NA";
    	            	}
    	            	line = br.readLine();
    	            }while (line != null);
    				
            	}catch(Exception e){
            		e.printStackTrace();
            	}
            	
            	Department s = new Department(); 
            	s.Initialize();
            	
            	if(processToken){
            		Thread.sleep(2000);
        			d.processToken("MyTokenDME");
            	}
            } catch (Exception e) {
            	e.printStackTrace();
            }   
       }

		public void run() {
			System.out.println("\tEntering Critical Section");
			//synchronized(this){
				try {
					if(operation.equals("Look-up")){
						status = lookUpAuditorium(date);
					}else if(operation.equals("Reserve")){
						status = reserveAuditorium(date);
					}
					flag = true;
					//notify();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			//}
		}

		public String executeOperation(String operation, String date) throws RemoteException, InterruptedException, NotBoundException {
			this.date = date;
			this.operation = operation;
			Thread t = new Thread(this);
			
			if(canEnterCS()){
				t = new Thread(this);
				t.start();
				Thread.sleep(d1*1000);
				t.join();
				System.out.println("\n\tExiting Critical Section\n");
				Thread.sleep(1000);
				sendTokenToNextNode();
        	}else{
        		requestArrived = true;
        		/*synchronized(t){
        			t.wait();	
        		}*/
        		while(true){
        			if(flag)
        				break;
        		}
        		flag=false;
        		requestArrived = false;
        	}
			return status;
		}
		
        public String lookUpAuditorium(String date) throws RemoteException {
	        	BufferedReader br = null;
	        	try {
					br = new BufferedReader(new FileReader(scheduleFile));
					String line = br.readLine();
		            do{
		            	String[] temp = line.split(",");
		            	if(temp[0].equals(date) && temp.length == 2){
		            		status = "Auditorium is not Available, It is reserved by " + temp[1];
		            		System.out.println("\n\t" + status);
			            	return status;
		            	}
		            	line = br.readLine();
		            }while (line != null);
	        	} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}finally{
					try {
						br.close();
					} catch (IOException e) {
					}
				}
	        System.out.println("\n\t" + status);
        	return status;
        }
        
		public String reserveAuditorium(String date) throws RemoteException {
			RandomAccessFile raf = null;
			String status = null;
			long offset;
			
				try {
		    	    raf = new RandomAccessFile(new File(scheduleFile), "rw");
		    	    offset = raf.getFilePointer();
		    	    String line = raf.readLine();
		    	    
		            do{
		            	String[] temp = line.split(",");
		            	if(temp[0].equals(date)){
	
			            	if(temp.length == 2){
			            		status = "Auditorium is not Available, It is already reserved by " + temp[1];
			            		System.out.println("\n\t" + status);
				            	return status;
			            	}else{
			            		int i=0;
			            		byte temp2 = 0;
			            		byte restOfFile[] = new byte[(int)(raf.length()-offset-3)];
			            		
			            		do{
			            			try{
			            				temp2 = raf.readByte();
			            			}catch(EOFException e){
			            				temp2=0;
			            				continue;
			            			}
			            			if(temp2!=0){
			            				restOfFile[i] = temp2;
			            				i++;
			            			}
			            		}while(temp2!=0);
			            		
			            		String temp1 = temp[0]+","+deptName+"\n";
			            		raf.seek(offset);
			            		raf.write(temp1.getBytes());
			            		raf.write(restOfFile);
			            		status = "Auditorium has been reserved for "+deptName+" on December " + temp[0];
			            		System.out.println("\n\t" + status);
			            		return status;
			            	} 
		            	}
		            	offset = raf.getFilePointer();
		            	line = raf.readLine();
		            }while (line != null);
					
					
		    	} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}finally{
					try {
						raf.close();
					} catch (IOException e) {
					}
				}
			System.out.println("\n\t" + status);	
        	return status;
		}

		public boolean canEnterCS() throws RemoteException {
			if(token.equals("MyTokenDME"))
				return true;
			else 			
				return false;
		}

		public void processToken(String newToken) throws RemoteException, NotBoundException, InterruptedException {
			System.out.println("-------------------------------------------------");
			System.out.println("\tToken received by " + myPort );
			token = newToken;
			System.out.println("\t\t-----");
			System.out.println("\t\tTOKEN");
			System.out.println("\t\t-----");
			if(requestArrived){
				executeOperation(operation, date);
			}else{
				sendTokenToNextNode();	
			}
		}

		private void sendTokenToNextNode() throws RemoteException, NotBoundException, InterruptedException{
			Thread.sleep(d2*1000);
			System.out.println("\tToken sent to "+nextNodePort);
			System.out.println("-------------------------------------------------\n\n");
			Thread.sleep(1000);
			String tokenToSend = token;
			token = "NA";
			registry = LocateRegistry.getRegistry("localhost",nextNodePort); 
			remoteInterface = (RemoteInterface)(registry.lookup("rmi"));
          	remoteInterface.processToken(tokenToSend);
		}
  }