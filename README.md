下面的一些示例程序展示了 Flink 从简单的 WordCount 到图计算的不同应用。代码示例演示了 Flink 的 DataSet API 的使用。
可以在 Flink 源码库的 flink-examples-batch 或 flink-examples-streaming 模块中找到以下和更多示例的完整源代码。

如何运行示例#
为了运行 Flink 示例，假设你有一个正在运行的 Flink 实例。你可以在导航中的“快速起步”和“建立工程”选项卡了解启动 Flink 的各种方法。

最简单的方法是运行 ./bin/start-cluster.sh 脚本，默认情况下会启动一个带有一个 JobManager 和一个 TaskManager 的本地集群。
Flink 的 binary 版本资源包下有一个 examples 目录，里面有这个页面上每个示例的 jar 文件。
要运行 WordCount 示例，请先执行以下命令：