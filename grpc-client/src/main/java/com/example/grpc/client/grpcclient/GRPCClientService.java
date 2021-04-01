package com.example.grpc.client.grpcclient;

import com.example.grpc.server.grpcserver.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.*;

@Service
public class GRPCClientService {
    public String ping() {
        	ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();

		PingPongServiceGrpc.PingPongServiceBlockingStub stub
                = PingPongServiceGrpc.newBlockingStub(channel);
		PongResponse helloResponse = stub.ping(PingRequest.newBuilder()
                .setPing("")
                .build());
		channel.shutdown();
		return helloResponse.getPong();


    }

    public String multiply(int[][] A, int[][] B){
		ManagedChannel channel1 = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();
		ManagedChannel channel2 = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();
		ManagedChannel channel3 = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();
		ManagedChannel channel4 = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();
		ManagedChannel channel5 = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();
		ManagedChannel channel6 = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();
		ManagedChannel channel7 = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();
		ManagedChannel channel8 = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();

		MatrixServiceGrpc.MatrixServiceBlockingStub stub1 = MatrixServiceGrpc.newBlockingStub(channel1);
		MatrixServiceGrpc.MatrixServiceBlockingStub stub2 = MatrixServiceGrpc.newBlockingStub(channel2);
		MatrixServiceGrpc.MatrixServiceBlockingStub stub3 = MatrixServiceGrpc.newBlockingStub(channel3);
		MatrixServiceGrpc.MatrixServiceBlockingStub stub4 = MatrixServiceGrpc.newBlockingStub(channel4);
		MatrixServiceGrpc.MatrixServiceBlockingStub stub5 = MatrixServiceGrpc.newBlockingStub(channel5);
		MatrixServiceGrpc.MatrixServiceBlockingStub stub6 = MatrixServiceGrpc.newBlockingStub(channel6);
		MatrixServiceGrpc.MatrixServiceBlockingStub stub7 = MatrixServiceGrpc.newBlockingStub(channel7);
		MatrixServiceGrpc.MatrixServiceBlockingStub stub8 = MatrixServiceGrpc.newBlockingStub(channel8);




		int MAX = A.length;



		int[][] A11 = new int[MAX / 2][MAX / 2];
		int[][] A12 = new int[MAX / 2][MAX / 2];
		int[][] A21 = new int[MAX / 2][MAX / 2];
		int[][] A22 = new int[MAX / 2][MAX / 2];
		int[][] B11 = new int[MAX / 2][MAX / 2];
		int[][] B12 = new int[MAX / 2][MAX / 2];
		int[][] B21 = new int[MAX / 2][MAX / 2];
		int[][] B22 = new int[MAX / 2][MAX / 2];

		for (int i = 0; i < MAX / 2; i++) {
			for (int j = 0; j < MAX / 2; j++) {

				A11[i][j] = A[i][j]; // top left
				A12[i][j] = A[i][j + MAX / 2]; // top right
				A21[i][j] = A[i + MAX / 2][j]; // bottom left
				A22[i][j] = A[i + MAX / 2][j + MAX / 2]; // bottom right

				B11[i][j] = B[i][j]; // top left
				B12[i][j] = B[i][j + MAX / 2]; // top right
				B21[i][j] = B[i + MAX / 2][j]; // bottom left
				B22[i][j] = B[i + MAX / 2][j + MAX / 2]; // bottom right
			}
		}

		Matrix A11m = convertArrayToProto(A11);
		Matrix A12m = convertArrayToProto(A12);
		Matrix A21m = convertArrayToProto(A21);
		Matrix A22m = convertArrayToProto(A22);
		Matrix B11m = convertArrayToProto(B11);
		Matrix B12m = convertArrayToProto(B12);
		Matrix B21m = convertArrayToProto(B21);
		Matrix B22m = convertArrayToProto(B22);

		// Result from mult
		long startTime = System.nanoTime();
		MatrixReply M1C1 = stub1.multiplyBlock(MatrixRequest.newBuilder().setMatrixA(A11m).setMatrixB(B11m).build());
		long endTime = System.nanoTime();
		long footprint = endTime-startTime;

		MatrixReply M2C1 = stub2.multiplyBlock(MatrixRequest.newBuilder().setMatrixA(A12m).setMatrixB(B21m).build());

		MatrixReply M1C2 = stub3.multiplyBlock(MatrixRequest.newBuilder().setMatrixA(A11m).setMatrixB(B12m).build());
		MatrixReply M2C2 = stub4.multiplyBlock(MatrixRequest.newBuilder().setMatrixA(A12m).setMatrixB(B22m).build());

		MatrixReply M1C3 = stub5.multiplyBlock(MatrixRequest.newBuilder().setMatrixA(A21m).setMatrixB(B11m).build());
		MatrixReply M2C3 = stub6.multiplyBlock(MatrixRequest.newBuilder().setMatrixA(A22m).setMatrixB(B21m).build());

		MatrixReply M1C4 = stub7.multiplyBlock(MatrixRequest.newBuilder().setMatrixA(A21m).setMatrixB(B12m).build());
		MatrixReply M2C4 = stub8.multiplyBlock(MatrixRequest.newBuilder().setMatrixA(A22m).setMatrixB(B22m).build());


		int numBlockCalls = 7;
		int deadline = 200000000;

		int numberServer = (int) ((footprint*numBlockCalls)/deadline);

		System.out.println("Number of servers needed: " + numberServer);


		MatrixReply C1=stub1.addBlock(MatrixRequest.newBuilder().setMatrixA(M1C1.getMatrixC()).setMatrixB(M2C1.getMatrixC()).build());
		MatrixReply C2=stub2.addBlock(MatrixRequest.newBuilder().setMatrixA(M1C2.getMatrixC()).setMatrixB(M2C2.getMatrixC()).build());
		MatrixReply C3=stub3.addBlock(MatrixRequest.newBuilder().setMatrixA(M1C3.getMatrixC()).setMatrixB(M2C3.getMatrixC()).build());
		MatrixReply C4=stub4.addBlock(MatrixRequest.newBuilder().setMatrixA(M1C4.getMatrixC()).setMatrixB(M2C4.getMatrixC()).build());

		int[][] C1array = convertProtoToArray(C1);
		int[][] C2array = convertProtoToArray(C2);
		int[][] C3array = convertProtoToArray(C3);
		int[][] C4array = convertProtoToArray(C4);



		int[][] finalArray = new int[MAX][MAX];


		for (int i = 0; i < MAX / 2; i++) {
			for (int j = 0; j < MAX / 2; j++) {

				finalArray[i][j] = C1array[i][j]; // top left
				finalArray[i][j + MAX / 2] = C2array[i][j]; // top right
				finalArray[i + MAX / 2][j] = C3array[i][j]; // bottom left
				finalArray[i + MAX / 2][j + MAX / 2] = C4array[i][j]; // bottom right

			}
		}




		System.out.println("Final Answer");
		System.out.println(Arrays.deepToString(finalArray));
		channel1.shutdown();



		return Arrays.deepToString(finalArray);






	}


	public static Matrix convertArrayToProto(int[][] A){

		Matrix.Builder response = Matrix.newBuilder();

		for (int i = 0; i < A.length; i++) {
			Row.Builder row = Row.newBuilder();
			for (int j = 0; j < A[i].length; j++) {
				row.addColumn(A[i][j]);
			}
			response.addA(i, row.build());
		}

		return response.build();

	}


	public static int[][] convertProtoToArray(MatrixReply m){
		int size = m.getMatrixC().getACount();
		int resp[][]= new int[size][size];
		Matrix matrix = m.getMatrixC();
		for (int i=0; i<size; i++)
		{
			for (int j=0;j < size;j++)
			{
				resp[i][j] = matrix.getA(i).getColumn(j);
			}
		}
		return resp;
	}




}


