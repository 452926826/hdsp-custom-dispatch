package com.hand.along.dispatch.master.infra.handler;

import com.google.common.graph.EndpointPair;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.hand.along.dispatch.common.constants.CommonConstant;
import com.hand.along.dispatch.common.domain.JobExecution;
import com.hand.along.dispatch.common.domain.JobNode;
import com.hand.along.dispatch.common.domain.WorkflowExecution;
import com.hand.along.dispatch.common.exceptions.CommonException;
import com.hand.along.dispatch.common.infra.mapper.JobMapper;
import com.hand.along.dispatch.common.utils.JSON;
import com.hand.along.dispatch.master.app.service.BaseStatusService;
import com.hand.along.dispatch.master.domain.*;
import com.hand.along.dispatch.common.infra.mapper.WorkflowExecutionMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class GraphUtil {
    private final JobMapper jobMapper;
    private final WorkflowExecutionMapper workflowExecutionMapper;
    private final BaseStatusService baseStatusService;

    public GraphUtil(JobMapper jobMapper,
                     WorkflowExecutionMapper workflowExecutionMapper,
                     BaseStatusService baseStatusService) {
        this.jobMapper = jobMapper;
        this.workflowExecutionMapper = workflowExecutionMapper;
        this.baseStatusService = baseStatusService;
    }

    /**
     * 解析图形
     *
     * @param graph             图形
     * @param workflow          任务流
     * @param workflowExecution 任务流执行记录
     * @param sourceList        源节点列表
     * @param targetList        目标节点列表
     * @param jobExecutionList  任务执行记录(理论上只有master重新选举时，重载所有的未完成的数据才会有值)
     * @return 节点临时map
     */
    public Map<String, JobNode> parseGraph(String graph, Workflow workflow,
                                           WorkflowExecution workflowExecution,
                                           List<String> sourceList,
                                           List<String> targetList,
                                           List<JobExecution> jobExecutionList) {
        try {
            log.info("开始解析任务流图形");
            workflowExecution.info("开始解析任务流图形");
            WorkflowGraph workflowGraph = JSON.toObj(graph, WorkflowGraph.class);
            Map<String, JobNode> tmpNodeMap = initGraph(workflowGraph, workflow, sourceList, targetList, jobExecutionList);
            log.info("解析任务流图形完毕");
            workflowExecution.info("解析任务流图形完毕");
            // 得到开始的节点
            sourceList.removeAll(targetList);
            // 设置需要跳过的节点状态为skip
            log.info("设置每个节点状态");
            workflowExecution.info("设置每个节点状态");
            skipNode(workflow.getGraphObj(), tmpNodeMap);
            return tmpNodeMap;
        } catch (Exception e) {
            log.error("提交任务流失败");
            workflowExecution.error(e, "提交任务流");
            baseStatusService.updateWorkflowExecutionStatus(workflowExecution, CommonConstant.ExecutionStatus.FAILED.name());
            throw new CommonException("提交任务流失败");
        }
    }

    private void skipNode(Graph graph, Map<String, JobNode> tmpNodeMap) {
        Set<JobNode> startSkipNodes = new HashSet<>();
        Set<JobNode> startReadyNodes = new HashSet<>();
        Set<JobNode> endReadyNodes = new HashSet<>();
        Set<JobNode> readyNodes = new HashSet<>();
        Set<JobNode> nodes = graph.nodes();
        // 拿到需要跳过的节点
        for (JobNode node : nodes) {
            // 存在开始节点
            if (CommonConstant.NodeMark.START.name().equals(node.getStatus())) {
                startSkipNodes.addAll(GraphUtil.getDepNode(graph, node, null));
                startReadyNodes.add(node);
                startReadyNodes.addAll(GraphUtil.getDepedNode(graph, node, null));
            }
            // 存在结束节点
            if (CommonConstant.NodeMark.END.name().equals(node.getStatus())) {
                endReadyNodes.add(node);
                endReadyNodes.addAll(GraphUtil.getDepNode(graph, node, null));
            }
            // 存在唯一节点
            if (CommonConstant.NodeMark.UNIQUE.name().equals(node.getStatus())) {
                readyNodes.add(node);
            }
        }
        // 只有开始节点
        if (!startReadyNodes.isEmpty() && endReadyNodes.isEmpty()) {
            readyNodes.addAll(startReadyNodes);
        }
        // 只有结束节点
        if (startReadyNodes.isEmpty() && !endReadyNodes.isEmpty()) {
            readyNodes.addAll(endReadyNodes);
        }

        // 开始节点和结束节点都有
        if (!startReadyNodes.isEmpty() && !endReadyNodes.isEmpty()) {
            if (CollectionUtils.containsAny(startReadyNodes, endReadyNodes)) {
                endReadyNodes.removeAll(startSkipNodes);
                readyNodes.addAll(endReadyNodes);
            } else {
                readyNodes.addAll(endReadyNodes);
                readyNodes.addAll(startReadyNodes);
            }
        }
        // 修改需要跳过节点的状态
        for (JobNode node : nodes) {
            if (StringUtils.isEmpty(node.getStatus())) {
                // skipNodes只有在unique的情况出现 所以要区别对待
                if (CollectionUtils.isNotEmpty(readyNodes)) {
                    if (!readyNodes.contains(node)) {
                        node.setStatus(CommonConstant.ExecutionStatus.SKIP.name());
                    } else {
                        node.setStatus(CommonConstant.ExecutionStatus.READY.name());
                    }
                } else {
                    node.setStatus(CommonConstant.ExecutionStatus.READY.name());
                }
            }
            tmpNodeMap.put(node.getId(), node);
        }
    }

    /**
     * 初始化图形
     *
     * @param workflowGraph    图形
     * @param workflow         任务流
     * @param sourceList       源节点
     * @param targetList       目标节点
     * @param jobExecutionList 已经运行任务的记录
     * @return id节点映射
     */
    private Map<String, JobNode> initGraph(WorkflowGraph workflowGraph,
                                           Workflow workflow,
                                           List<String> sourceList,
                                           List<String> targetList,
                                           List<JobExecution> jobExecutionList) {
        // 所有的nodes
        List<WorkflowNode> nodes = workflowGraph.getNodes();
        // 所有的连线
        List<WorkflowEdge> edges = workflowGraph.getEdges();
        // 连线
        List<EndpointPair<JobNode>> endpointList = new ArrayList<>();
        Map<String, JobNode> tmpNodeMap = new HashMap<>(16);
        log.info("解析节点");
        processJob(nodes, tmpNodeMap, workflow, jobExecutionList);
        // 存放连线上的条件
        Map<String,String> conditionMap = new HashMap<>();
        // 遍历连线梳理关系
        for (WorkflowEdge edge : edges) {
            String target = edge.getTarget();
            String source = edge.getSource();
            EndpointPair<JobNode> pair =
                    EndpointPair.ordered(tmpNodeMap.get(source), tmpNodeMap.get(target));
            String condition = edge.getCondition();
            if(StringUtils.isNotEmpty(condition)){
                conditionMap.put(String.format(CommonConstant.FROM_AND_TO, source,target), condition);
            }
            endpointList.add(pair);
            sourceList.add(source);
            targetList.add(target);
        }
        workflow.setConditionMap(conditionMap);
        // 拿到所有节点ID
        List<String> allNodeId = new ArrayList<>(tmpNodeMap.keySet());
        allNodeId.removeAll(sourceList);
        allNodeId.removeAll(targetList);
        // 没有前后节点的节点都是开始节点
        sourceList.addAll(allNodeId);
        // 初始化图形
        this.initGraph(endpointList, workflow);
        MutableGraph<JobNode> etlTaskGraph = (MutableGraph<JobNode>) workflow.getGraphObj();
        // 把没有前后节点的节点加到图形中
        allNodeId.forEach(a -> etlTaskGraph.addNode(tmpNodeMap.get(a)));
        return tmpNodeMap;
    }


    /**
     * 解析节点
     *
     * @param nodes            所有节点
     * @param tmpNodeMap       节点ID映射
     * @param workflow         任务流
     * @param jobExecutionList 任务执行记录
     */
    private void processJob(List<WorkflowNode> nodes, Map<String, JobNode> tmpNodeMap, Workflow workflow, List<JobExecution> jobExecutionList) {
        log.info("解析node");
        // 已经执行过的任务
        Map<String, JobExecution> executionMap = jobExecutionList.stream().collect(Collectors.toMap(JobExecution::getGraphId, i -> i));
        nodes.forEach(n -> {
            final String id = n.getId();
            String nodeType = n.getNodeType();
            JobNode jobNode = JobNode.builder()
                    .id(id)
                    .workflowId(workflow.getWorkflowId())
                    .nodeType(nodeType)
                    .objectId(n.getObjectId())
                    .uniqueId(String.format("%s_%s", workflow.getWorkflowId(), id))
                    .jobType(n.getJobType())
                    // 如果没有执行记录则设置为空 因为在后面skipNode方法里面 会再次设置状态
                    .status(executionMap.containsKey(id) ? executionMap.get(id).getExecutionStatus() : StringUtils.EMPTY)
                    .priorityLevel(StringUtils.isEmpty(n.getPriorityLevel()) ? "NORMAL" : n.getPriorityLevel())
                    .build();
            if (CommonConstant.NodeType.WORKFLOW.name().equals(nodeType)) {
                jobNode.setJobType("sub-workflow");
            }
            jobNode.setMessageType(CommonConstant.JOB);
            // 把正在运行中的节点加入到active map中
            if(executionMap.containsKey(id) && CommonConstant.ExecutionStatus.isRunning(executionMap.get(id).getExecutionStatus())){
                WorkflowTask.activeNodeMap.put(jobNode.getUniqueId(), jobNode);
            }
            tmpNodeMap.put(id, jobNode);
        });
    }

    /**
     * 初始化图形
     *
     * @param pairs    节点连线关系
     * @param workflow 任务流
     */
    public static void initGraph(List<EndpointPair<JobNode>> pairs, Workflow workflow) {
        log.info("初始化guava有向有环图");
        MutableGraph<JobNode> graph = GraphBuilder.directed().allowsSelfLoops(true).build();
        pairs.forEach(graph::putEdge);
        workflow.setGraphObj(graph);
    }


    /**
     * 获取dag图当前node前面的node
     *
     * @param graph    dag图
     * @param node     当前node
     * @param listNode 依赖的node的list
     * @return 依赖的node的list
     */
    @SuppressWarnings("UnstableApiUsage")
    public static LinkedList<JobNode> getDepNode(Graph<JobNode> graph, JobNode node, LinkedList<JobNode> listNode) {
        if (listNode == null) {
            listNode = new LinkedList<>();
        }
        Set<JobNode> preSet = graph.predecessors(node);
        if (preSet != null && !preSet.isEmpty()) {
            if (!listNode.containsAll(preSet)) {
                listNode.addAll(preSet);
                for (JobNode n : preSet) {
                    getDepNode(graph, n, listNode);
                }
            }
        }
        return listNode;
    }

    /**
     * 获取dag图当前node后面的node
     *
     * @param graph    dag图
     * @param node     当前node
     * @param listNode 依赖node的list
     * @return 依赖的node的list
     */
    @SuppressWarnings("UnstableApiUsage")
    public static List<JobNode> getDepedNode(Graph<JobNode> graph, JobNode node, List<JobNode> listNode) {
        if (listNode == null) {
            listNode = new ArrayList<>();
        }
        Set<JobNode> sucSet = graph.successors(node);
        if (sucSet != null && !sucSet.isEmpty()) {
            if (!listNode.containsAll(sucSet)) {
                listNode.addAll(sucSet);
                for (JobNode n : sucSet) {
                    getDepedNode(graph, n, listNode);
                }
            }
        }
        return listNode;
    }
}
