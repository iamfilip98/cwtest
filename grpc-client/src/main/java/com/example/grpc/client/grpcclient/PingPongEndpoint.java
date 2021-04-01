package com.example.grpc.client.grpcclient;

import io.grpc.Server;
import io.grpc.ServerBuilder;
//import org.graalvm.compiler.core.common.type.ArithmeticOpTable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@RestController
public class PingPongEndpoint {    

	GRPCClientService grpcClientService;

	int[][] A;
	int[][] B;

	@Autowired
    	public PingPongEndpoint(GRPCClientService grpcClientService) {
        	this.grpcClientService = grpcClientService;
    	}    
	@GetMapping("/ping")
    	public String ping() {
        	return grpcClientService.ping();
    	}

	@GetMapping("/multiply")
		public String multiply() {
		//TODO size checks and all
			return grpcClientService.multiply(this.A,this.B);

	}

	@PostMapping("/uploadA")
	public String getMatrixA(@RequestParam("matrixA") MultipartFile file) throws IOException{
		BufferedReader isReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
		String line = isReader.lines().collect(Collectors.joining("\n"));
		//System.out.println(line);

		String[] rows = line.split("\n");
		int size = rows.length;
		int[][] newA = new int[size][size];

		int columnCount = 0;

		try {
			for(int i=0; i < size; i++){
				for(int j=0; j<size; j++){
					newA[i][j]= Integer.parseInt((rows[i].split(","))[j]);
					columnCount +=1;
				}
			}
		}
		catch(Exception E){
			return "ERROR, MATRIX NOT SQUARE";
		}




		if(!((newA.length & newA.length-1)==0)){
			return "ERROR, NOT POWER OF TWO";
		}


		this.A= newA;

		return "Status 200";
	}

	@PostMapping("/uploadB")
	public String getMatrixB(@RequestParam("matrixB") MultipartFile file) throws IOException{
		BufferedReader isReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
		String line = isReader.lines().collect(Collectors.joining("\n"));
		//System.out.println(line);

		String[] rows = line.split("\n");
		int size = rows.length;
		int[][] newB = new int[size][size];

		int columnCount = 0;


		try {
			for(int i=0; i < size; i++){
				for(int j=0; j<size; j++){
					newB[i][j]= Integer.parseInt((rows[i].split(","))[j]);
					columnCount +=1;
				}
			}
		}
		catch(Exception E){
			return "ERROR, MATRIX NOT SQUARE";
		}



		if(!((newB.length & newB.length-1)==0)){
			return "ERROR, NOT POWER OF TWO";
		}


		this.B= newB;

		return "Status 200";


	}




}













