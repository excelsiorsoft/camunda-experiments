/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.getstarted.loanapproval;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.camunda.bpm.spring.boot.starter.event.PostDeployEvent;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.EventListener;

import static org.slf4j.LoggerFactory.getLogger;

@SpringBootApplication
@EnableProcessApplication
public class WebappExampleProcessApplication {

 private final Logger logger = getLogger(this.getClass());
	
  @Autowired
  private RuntimeService runtimeService;
  
  @Autowired
  private TaskService taskService;

  public static void main(String... args) {
    SpringApplication.run(WebappExampleProcessApplication.class, args);
  }

  @EventListener
  private void processPostDeploy(PostDeployEvent event) {
    //ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("loanApproval");
    
    String processInstanceId = runtimeService.startProcessInstanceByKey("loanApproval").getProcessInstanceId();
    logger.info("started instance: {}", processInstanceId);

    Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
    logger.info("taskId: {}", task.getId());
  }

}