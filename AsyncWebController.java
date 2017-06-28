import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.pwc.analyticapps.platform.bootstrapframework.bean.util.RestResponse;
import com.pwc.analyticapps.platform.bootstrapframework.web.controller.BaseController;
import com.pwc.analyticapps.platform.cloud.topic.bean.vo.SubscribeVo;
import com.pwc.analyticapps.platform.cloud.topic.bean.vo.TopicVo;
import com.pwc.analyticapps.platform.cloud.topic.service.EmailService;
import com.pwc.analyticapps.platform.cloud.topic.service.TopicService;

import io.netty.util.internal.StringUtil;
import io.reactivex.functions.Action;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

import com.pwc.analyticapps.platform.cloud.topic.service.RxTestService;

@Slf4j
@RestController
public class TopicController extends BaseController {

	@Autowired
	private RxTestService rxTestService;
	
	@Autowired
	private AsyncTaskExecutor asyncTaskExecutor;
	
	@CrossOrigin
	@RequestMapping(value = "/hello1", method = RequestMethod.GET)
	public RestResponse<List<String>> getAMessage(){
		log.info("fast request received: " + Thread.currentThread().getName());
		List<String> list = new ArrayList<>();
		list.add(rxTestService.getThings1());
		return this.standardResponse(list);
	}
	
	@CrossOrigin
	@RequestMapping(value = "/hello2", method = RequestMethod.GET)
	public Callable<String> executeSlowTask() {
		log.info("Request received: " + Thread.currentThread().getName());
        Callable<String> callable = rxTestService::call;
        log.info("Servlet thread released");
        
        return callable;
	}
	
	 @RequestMapping(value = "/hello3", method = RequestMethod.GET, produces = "text/html")
	    public DeferredResult<String> executeSlowTask1() {
		    log.info("Request received: " + Thread.currentThread().getName());
	        DeferredResult<String> deferredResult = new DeferredResult<>(3000L);
	        CompletableFuture.supplyAsync(() -> rxTestService.call(), asyncTaskExecutor)
	            .whenCompleteAsync((result, throwable) -> deferredResult.setResult(result), asyncTaskExecutor);
	        log.info("Servlet thread released");
	        
	        return deferredResult;
	    }

	@CrossOrigin
	@RequestMapping(value = "/hello", method = RequestMethod.GET)
	public DeferredResult<List<String>> getAMessageAsync() {
		log.info("rx Request received: " + Thread.currentThread().getName());
		DeferredResult<List<String>> deffered = new DeferredResult<>();
		deffered.onTimeout(() -> {
			log.info("timeout.");
			deffered.setResult(Collections.emptyList());
		});
		List<String> list = new LinkedList<>();
		log.info(Thread.currentThread().getName());
		rxTestService.getThings().subscribe(e -> list.add(e), e -> deffered.setErrorResult(e),
				() -> deffered.setResult(list));
		log.info("Servlet thread released");
		return deffered;
	}
}
