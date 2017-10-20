/************************************************************************************
 *	file: Ex3Client.java
 *	author: Daniel Spencer
 *	class: CS 380 - computer networks
 *
 *	assignment: Exercise 3
 *	date last modified: 10/19/2017
 *
 *	purpose: This project is to accept an amount of bytes and used checksum algorithm.
 *              When sum is calculated, perform ones complement and return the rightmost
 *              16 bits.
 *
 ************************************************************************************/
import java.io.*;
import java.net.*;

public final class Ex3Client {
    public static void main(String[] args) throws IOException{
        try(Socket socket = new Socket("18.221.102.182", 38103)){
            System.out.println("Connected to server.");
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            int test = in.read();
            System.out.println("Reading "+ test + " bytes.");

            byte[] bArray = new byte[test];
            in.read(bArray);
            System.out.println(Integer.toHexString(bArray[0]));

            char[] hexValue = byteHex(bArray);

            System.out.println("Data received: ");
            for(int i =0; i < test*2; ++i){
                if((i)% 20== 0){
                    System.out.print("\t");
                }
                System.out.print(hexValue[i]);
                if((i+1)% 20== 0){
                    System.out.println();
                }
            }
            short checksum = checksum(bArray);
            System.out.printf("\nChecksum calculated: 0x%04X.\n", checksum);
            byte[] bArray2 = new byte[2];
            bArray2[0] = (byte)((checksum & 0xFF00)>>8);
            bArray2[1] = (byte) (checksum & 0xff);

            out.write(bArray2);
            if(in.read() == 1){
                System.out.println("Response good.");
            }
            System.out.println("Disconnected from server.");

        }

    }
    public static short checksum(byte[] bytes) {
         int length = bytes.length;
         int index = 0;
         long sum = 0;
        while (length > 1) {
             sum += (((bytes[index]<<8) & 0xFF00) | ((bytes[index + 1]) & 0xFF));
             if ((sum & 0xFFFF0000) > 0){
                 sum = sum & 0xFFFF;
                 sum += 1;
             }
             index += 2;
             length -= 2;
         }
         if (length > 0) {
             sum += (bytes[index]<<8 & 0xFF00);
             if ((sum & 0xFFFF0000) > 0){
                 sum = sum & 0xFFFF;
                 sum += 1;
             }
         }
         sum = sum & 0xFFFF;
         return (short)~sum;
     }
     public static char[] byteHex(byte[] bytes){
        char[] hexArray="0123456789ABCDEF".toCharArray();
        char[] hex = new char[bytes.length * 2];
        for(int i=0;i<bytes.length;i++) {
             int current = bytes[i] & 0xff;
             hex[i*2+1] = hexArray[current & 0x0f];
             hex[i*2] = hexArray[current >> 4];
         }
         return hex;
     }
}
