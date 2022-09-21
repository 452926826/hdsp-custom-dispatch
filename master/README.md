### 任务流调度中心
功能：
多中心分布式
嵌套任务流
内置参数
邮件告警
文件存储
任务重试
任务失败处理

### 示例json
![img.png](images/img.png)
普通任务流json
```
{
    "workflowId":1,
    "graph":"{\"nodes\":[{\"id\":\"123\",\"nodeType\":\"job\",\"jobType\":\"datax\",\"objctId\":1,\"priorityLevel\":3},{\"id\":\"456\",\"nodeType\":\"job\",\"jobType\":\"sql\",\"objctId\":3,\"priorityLevel\":3}],\"edges\":[{\"source\":\"123\",\"sourceAncheor\":1,\"target\":\"456\",\"targetAnchor\":1,\"index\":1}]}"
}
```
子任务流json
```
{
    "workflowId":2,
    "graph":"{\"nodes\": [{\"id\": \"123\", \"nodeType\": \"job\", \"jobType\": \"datax\", \"objectId\": 1, \"priorityLevel\": 3 }, {\"id\": \"456\", \"nodeType\": \"job\", \"jobType\": \"datax\", \"objectId\": 3, \"priorityLevel\": 3 }, {\"id\": \"777\", \"nodeType\": \"workflow\", \"jobType\": \"sub-workflow\", \"objectId\": 1, \"priorityLevel\": 3 } ], \"edges\": [{\"source\": \"123\", \"sourceAncheor\": 1, \"target\": \"777\", \"targetAnchor\": 1, \"index\": 1 }, {\"source\": \"777\", \"sourceAncheor\": 1, \"target\": \"456\", \"targetAnchor\": 1, \"index\": 1 } ] }"
}
```
循环json
```
{
    "workflowId":2,
    "graph":"{\"nodes\": [{\"id\": \"111\", \"nodeType\": \"job\", \"jobType\": \"set_variable\", \"objectId\": 1, \"priorityLevel\": 3 },{\"id\": \"123\", \"nodeType\": \"job\", \"jobType\": \"datax\", \"objectId\": 1, \"priorityLevel\": 3 }, {\"id\": \"456\", \"nodeType\": \"job\", \"jobType\": \"datax\", \"objectId\": 3, \"priorityLevel\": 3 }, {\"id\": \"777\", \"nodeType\": \"job\", \"jobType\": \"set_variable\", \"objectId\": 2, \"priorityLevel\": 3 }, {\"id\": \"888\", \"nodeType\": \"job\", \"jobType\": \"datax\", \"objectId\": 5, \"priorityLevel\": 3 }, {\"id\": \"999\", \"nodeType\": \"job\", \"jobType\": \"datax\", \"objectId\": 6, \"priorityLevel\": 3 } ], \"edges\": [{\"source\": \"111\", \"sourceAncheor\": 1, \"target\": \"123\", \"targetAnchor\": 1, \"index\": 1 },{\"source\": \"123\", \"sourceAncheor\": 1, \"target\": \"456\", \"targetAnchor\": 1, \"index\": 1 }, {\"source\": \"456\", \"sourceAncheor\": 1, \"target\": \"777\", \"targetAnchor\": 1, \"index\": 1 } , {\"source\": \"777\", \"sourceAncheor\": 1, \"target\": \"123\", \"condition\":\"${tt}>0\",\"targetAnchor\": 1, \"index\": 1 } , {\"source\": \"777\", \"sourceAncheor\": 1, \"target\": \"888\",\"condition\":\"${tt}==0\", \"targetAnchor\": 1, \"index\": 1 } , {\"source\": \"888\", \"sourceAncheor\": 1, \"target\": \"999\", \"targetAnchor\": 1, \"index\": 1 } ] }"
}
```


