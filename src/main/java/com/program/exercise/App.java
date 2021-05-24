package com.program.exercise;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.program.excercise.process.Processor;

/**
 * Jayashree
 *
 */
public class App {
	private static int CORE_THREAD_COUNT = 10;
	private static int MAX_THREAD_COUNT = 10;

	private static final Logger log = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) {
		ThreadPoolExecutor executor = new ThreadPoolExecutor(CORE_THREAD_COUNT, MAX_THREAD_COUNT, 500L,
				TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(10));
		Set<String> inputUris = new HashSet<>();
		crawl(executor, inputUris);
	}

	private static void crawl(ThreadPoolExecutor executor, Set<String> inputUris) {
		Scanner scanner = new Scanner(System.in);
		String input = null;
		String searchText = null;
		System.out.println("Please enter the url's to crawl or enter `n` to end the program.");
		input = scanner.nextLine();
		while (!"n".equalsIgnoreCase(input)) {
			if (input == null || "".equals(input.trim())) {
				System.out.println("Please enter the url's to crawl or enter `n` to end the program.");
			} else if (inputUris.contains(input.trim())) {
				System.out.println("Duplicate Uri input.");
			} else {
				System.out.println("Please enter searchText to crawl");
				searchText = scanner.nextLine();
				log.info("Active count" +executor.getActiveCount());
				inputUris.add(input.trim());
				Processor processor = new Processor();
				processor.setUri(input);
				processor.setText(searchText);
				processor.setFileName(searchText);
				try {
					executor.execute(processor);
				} catch (RejectedExecutionException ex) {
					System.out.println("All threads occupied. Please input again and try.");
					System.out.println("Urls rejected \n" + input);
					inputUris.remove(input.trim());
				}
			}
			System.out.println("Please enter the url's to crawl or enter `n` to end the program.");
			input = scanner.nextLine();
		}
		System.out.println("Active Processes" + executor.getActiveCount());
		executor.shutdown();
	}

}
