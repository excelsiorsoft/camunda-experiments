/**
 * 
 */
package org.camunda.bpm.spring.boot.example.twitter;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author adcgwfk
 *
 */
@RestController
@RequestMapping("twitter-demo")
public class ProcessFlowController {
	
	private final Logger logger = getLogger(this.getClass());
	
	@Autowired
	private RuntimeService runtimeService;
	
	@RequestMapping(value= "/invoke", method = RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE,consumes=MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public String tweetWritten( @RequestBody CreateTweet data) {
		Map<String, Object> context = new HashMap<>();
		context.put("content", data.content);
		context.put("email", data.email);
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("TwitterDemoProcessControlled",context);
		String processInstanceId = processInstance.getProcessInstanceId();
	    logger.info("started instance: {}", processInstanceId);
	    return processInstanceId;
	}
	
	@RequestMapping(value= "/review", method = RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE,consumes=MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public String reviewTweet( @RequestBody ReviewTweet data) {
		
		boolean approved = data.approved;
		String comments = data.comments;
		
		Map<String, Object> context = new HashMap<>();
		context.put("approved", approved);
		context.put("comments", comments);
		
		logger.info("Application approved: {} with comments: {}", approved, comments);
		
		List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery().list();
		StringBuilder builder = new StringBuilder();
		
		for(ProcessInstance processInstance: processInstances) {
			String processInstanceId = processInstance.getId();
			logger.info("Instance from the query: {}", processInstanceId);
			
			runtimeService.createProcessInstanceModification(processInstanceId)
		    .startAfterActivity("user_task_review_tweet")
		    .setVariable("approved", approved)
		    .setVariable("comments", comments)
		    .execute();
			builder.append(processInstanceId).append(",");
			
		}
	    
	    return builder.toString();
	
	}
	
	public static class CreateTweet {
		public String email;
		public String content;
	}
	
	public static class ReviewTweet {
		public boolean approved;
		public String comments;
	}

}
