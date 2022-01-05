package com.hand.along.dispatch.master.infra.handler;

import com.google.common.graph.Graph;
import com.hand.along.dispatch.common.constants.CommonConstant;
import com.hand.along.dispatch.common.utils.CommonUtil;
import com.hand.along.dispatch.common.utils.JSON;
import com.hand.along.dispatch.master.app.service.BaseStatusService;
import com.hand.along.dispatch.common.domain.JobNode;
import com.hand.along.dispatch.master.domain.Workflow;
import com.hand.along.dispatch.master.domain.WorkflowExecution;
import com.hand.along.dispatch.master.infra.netty.NettyServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class WorkflowJob implements Runnable {
    public static final ConcurrentHashMap<String, JobNode> activeNodeMap = new ConcurrentHashMap<>();
    private final Workflow workflow;
    private final List<String> sourceList;
    private final Map<String, JobNode> tmpNodeMap;
    private final WorkflowExecution workflowExecution;
    private final BaseStatusService baseStatusService;
    private final Graph graph;
    private Boolean workflowFinished = false;
    private Boolean workflowFailed = false;
    private static final Long WAIT_TIME = 5L;

    public WorkflowJob(Workflow workflow, List<String> sourceList, Map<String, JobNode> tmpNodeMap, BaseStatusService baseStatusService, WorkflowExecution workflowExecution) {
        this.workflow = workflow;
        this.sourceList = sourceList;
        this.tmpNodeMap = tmpNodeMap;
        this.baseStatusService = baseStatusService;
        this.workflowExecution = workflowExecution;
        this.graph = workflow.getGraphObj();
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        log.info("运行任务流");
        workflowExecution.info("运行任务流");
        try {
            baseStatusService.updateWorkflowExecutionStatus(workflowExecution, CommonConstant.ExecutionStatus.RUNNING.name());
            Graph graphObj = workflow.getGraphObj();
            Set<String> readyNodeSet = new HashSet<>(sourceList);
            Set<JobNode> runningSet = new HashSet<>();
            Set<JobNode> finishedSet = new HashSet<>();
            while (!workflowFinished && !workflowFailed) {
                if (CollectionUtils.isNotEmpty(graphObj.nodes())) {
                    processReadyNodeSet(readyNodeSet, runningSet, finishedSet);
                    runningSet.stream().map(JobNode::getId).collect(Collectors.toList()).forEach(readyNodeSet::remove);
                } else {
                    if (MapUtils.isNotEmpty(tmpNodeMap)) {
                        tmpNodeMap.values().forEach(n -> {
                            if (!runningSet.contains(n) && !finishedSet.contains(n)) {
                                n.setStatus(CommonConstant.ExecutionStatus.READY.name());
                                submitJob(readyNodeSet, runningSet, finishedSet, n);
                            }
                        });
                    }
                }
                processRunningNodeSet(readyNodeSet, runningSet, finishedSet);
                runningSet.removeAll(finishedSet);
                // 判断任务流状态
                if (!workflowFinished) {
                    workflowFinished = CollectionUtils.isEmpty(readyNodeSet) && CollectionUtils.isEmpty(runningSet);
                }
                try {
                    TimeUnit.SECONDS.sleep(WAIT_TIME);
                } catch (InterruptedException e) {
                    log.error("睡眠异常", e);
                    workflowExecution.error(e, "睡眠异常");
                }
                log.info("循环一次后，写入日志");
                baseStatusService.updateWorkflowExecutionStatus(workflowExecution, CommonConstant.ExecutionStatus.RUNNING.name());
            }
            if (workflowFinished) {
                log.info("workflow执行完毕");
                workflowExecution.info("workflow执行完毕");
                baseStatusService.updateWorkflowExecutionStatus(workflowExecution, workflowFailed ? CommonConstant.ExecutionStatus.FAILED.name() : CommonConstant.ExecutionStatus.SUCCEEDED.name());
            }
        } catch (Exception e) {
            log.error("workflow执行失败", e);
            workflowExecution.error(e, "workflow执行失败");
            baseStatusService.updateWorkflowExecutionStatus(workflowExecution, CommonConstant.ExecutionStatus.FAILED.name());
        }
    }


    /**
     * 处理正在运行的node
     *
     * @param readyNodeSet 准备运行的set
     * @param runningSet   正在运行的set
     * @param finishedSet  运行完毕的set
     */
    private void processRunningNodeSet(Set<String> readyNodeSet, Set<JobNode> runningSet, Set<JobNode> finishedSet) {
        Set<JobNode> tmpSet = new HashSet<>();
        if (CollectionUtils.isNotEmpty(runningSet)) {
            log.info("开始判断运行节点的状态");
            workflowExecution.info("开始判断运行节点的状态");
            //taskExecution.addLog("判断运行节点的状态");
            runningSet.forEach(r -> {
                String id = r.getId();
                if (CommonConstant.ExecutionStatus.isFailed(r.getStatus())) {
                    log.warn("{}节点运行失败", id);
                    workflowExecution.warn("{}节点运行失败", id);
                    workflowFinished = true;
                    workflowFailed = true;
                    log.warn("任务流失败");
                    workflowExecution.warn("ETL任务失败");
                }
                if (CommonConstant.ExecutionStatus.isSuccess(r.getStatus())) {
                    //  节点成功了加入到后置节点到待运行中
                    log.info("节点运行成功或者跳过：{}", id);
                    workflowExecution.info("节点运行成功或者跳过：{}", id);
                    //taskExecution.addLog(String.format("节点运行成功或者跳过：%s", r.getNodeName()));
                    if (CollectionUtils.isNotEmpty(graph.nodes())) {
                        // 获取当前节点的所有子节点
                        Set<JobNode> successors = graph.successors(r);
                        if (CollectionUtils.isNotEmpty(successors)) {
                            final List<String> readyNodeList = successors
                                    .stream()
                                    .filter(n -> !CommonConstant.ExecutionStatus.isSkipped(n.getStatus()))
                                    .map(JobNode::getId)
                                    .collect(Collectors.toList());
                            readyNodeSet.addAll(readyNodeList);
                            // 跳过的节点全部加入到运行中
                            successors
                                    .stream()
                                    .filter(n -> CommonConstant.ExecutionStatus.isSkipped(n.getStatus()))
                                    .forEach(tmpSet::add);
                        }
                    }
                }
                if (CommonConstant.ExecutionStatus.isFinished(r.getStatus())) {
                    finishedSet.add(r);
                }
            });
        }
        runningSet.addAll(tmpSet);
    }

    /**
     * 处理准备节点
     *
     * @param readyNodeSet 准备节点set
     * @param runningSet   运行中的set
     * @param finishedSet  完成的set
     */
    private void processReadyNodeSet(Set<String> readyNodeSet, Set<JobNode> runningSet, Set<JobNode> finishedSet) {
        if (CollectionUtils.isNotEmpty(readyNodeSet)) {
            for (String readyId : readyNodeSet) {
                final JobNode readyNode = tmpNodeMap.get(readyId);
                log.info("准备运行当前节点：{}", readyNode.getId());
                workflowExecution.info("准备运行当前节点：{}", readyNode.getId());
                // 前置节点
                final Set<JobNode> depNodeList = graph.predecessors(readyNode);
                if (CollectionUtils.isNotEmpty(depNodeList)) {
                    boolean depStatusFlag = true;
                    for (JobNode node : depNodeList) {
                        // 获取此依赖节点的前置节点
                        final LinkedList<JobNode> currentNodeDep = GraphUtil.getDepNode(graph, node, null);
                        // 如果此依赖节点的前置节点包含了readyNode 说明是一个循环，则跳过这个依赖节点的状态判断
                        if (currentNodeDep.contains(readyNode)) {
                            continue;
                        }
                        if (!CommonConstant.ExecutionStatus.isSuccess(node.getStatus())) {
                            log.info("当前节点的依赖：{}状态为{}，未完成，等待前置节点成功。。", node.getId(), node.getStatus());
                            workflowExecution.info("当前节点的依赖：{}状态为{}，未完成，等待前置节点成功。。", node.getId(), node.getStatus());
                            depStatusFlag = false;
                        }
                    }
                    if (!depStatusFlag) {
                        continue;
                    }
                }
                submitJob(readyNodeSet, runningSet, finishedSet, readyNode);
            }
        }
    }


    /**
     * 提交任务
     *
     * @param readyNodeSet 准备中的
     * @param runningSet   运行中
     * @param finishedSet  完成的
     * @param readyNode    准备提交
     */
    private void submitJob(Set<String> readyNodeSet, Set<JobNode> runningSet, Set<JobNode> finishedSet, JobNode readyNode) {
        log.info("准备提交任务:{}", readyNode.getId());
        workflowExecution.info("准备提交任务:{}", readyNode.getId());
        String status = readyNode.getStatus();
        String uniqueId = readyNode.getUniqueId();
        // 需要跳过的节点直接完成
        if (CommonConstant.ExecutionStatus.isSkipped(status)) {
            log.info("任务状态为跳过");
            workflowExecution.info("任务状态为跳过");
            readyNodeSet.remove(readyNode.getId());
            runningSet.add(readyNode);
            return;
        }
        // 如果节点正在运行
        if (CommonConstant.ExecutionStatus.isRunning(status)) {
            log.info("任务状态为正在运行");
            workflowExecution.info("任务状态为正在运行");
            readyNodeSet.remove(readyNode.getId());
            runningSet.add(readyNode);
            finishedSet.remove(readyNode);
            // 加入到正在运行的节点中
            if (!activeNodeMap.containsKey(uniqueId)) {
                activeNodeMap.put(uniqueId, readyNode);
            }
            return;
        }

        // 不是正在运行的 且 不是跳过的节点 提交到slave运行
        if (CommonConstant.ExecutionStatus.isReady(status)) {
            log.info("提交当前节点：{}", readyNode.getId());
            workflowExecution.info("提交当前节点：{}", readyNode.getId());
            // 提交任务执行
            // 运行的set里面加上运行的节点
            readyNode.setSubmitDate(CommonUtil.now());
            readyNodeSet.remove(readyNode.getId());
            runningSet.add(readyNode);
            // 已经完成的set去掉运行的节点(主要是循环会出现这种情况)
            finishedSet.remove(readyNode);
            // 修改状态
            readyNode.setStatus(CommonConstant.ExecutionStatus.RUNNING.name());
            activeNodeMap.put(uniqueId, readyNode);
            // 获取slave节点 并发送给slave
            NettyServer.sendRandom(JSON.toJson(readyNode));
            log.info("提交任务成功：{}", readyNode.getId());
            workflowExecution.info("提交任务成功：{}", readyNode.getId());
        }
    }

}
