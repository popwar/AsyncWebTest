package com.pwc.analyticapps.platform.cloud.topic.service;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Service;

import com.pwc.analyticapps.platform.cloud.topic.controller.TopicController;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RxTestService implements Callable{
	
	private final ExecutorService customObservableExecutor = Executors.newFixedThreadPool(10);

	public Observable<String> getThings() {

		return Observable.<String>create(e -> {
			log.info("start processing");
			log.info(Thread.currentThread().getName());
			RxTestService.delay(5000);
			e.onNext("hello");
			e.onComplete();
			log.info("finish processing");
		}).subscribeOn(Schedulers.from(customObservableExecutor));
	}
	
	public String getThings1(){
		RxTestService.delay(5000);
		return "hello1";
	}

	public static void delay(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// Ignore
		}
	}

	@Override
	public String call(){
		log.info("start processing");
		log.info(Thread.currentThread().getName());
		RxTestService.delay(5000);
		log.info("finish processing");
		return "hello";
	}
}
