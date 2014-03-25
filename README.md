FNLP (formerly FudanNLP)
====
之前的FudanNLP项目地址为：http://code.google.com/p/fudannlp


介绍(Introduction)
-----------------------------------  
FNLP(formerly FudanNLP)主要是为中文自然语言处理而开发的工具包，也包含为实现这些任务的机器学习算法和数据集。
本工具包及其包含数据集使用LGPL3.0许可证。


FNLP is developed for Chinese natural language processing (NLP), which also includes some machine learning algorithms and [DataSet data sets] to achieve the NLP tasks. FudanNLP is distributed under LGPL3.0.

If you're new to FNLP, check out the [QuickStart Quick Start (使用说明)] page, [http://vdisk.weibo.com/s/iyiB0 FNLP Book] or [https://fudannlp.googlecode.com/svn/FudanNLP-1.5-API/java-docs/index.html  Java-docs].

You can also use the [http://jkx.fudan.edu.cn/nlp Demo Website(演示网站)] so that you may check the functionality prior to downloading.

有遇到FNLP不能处理的例子，请到这里提交: [http://code.google.com/p/fudannlp/wiki/CollaborativeCollection 协同数据收集]，有问题请查看[FAQ]或到[http://q.weibo.com/960122 微群]、QQ群（253541693）讨论。*


功能(Functions)
----
		信息检索： 文本分类 新闻聚类
		中文处理： 中文分词 词性标注 实体名识别 关键词抽取 依存句法分析 时间短语识别
		结构化学习： 在线学习 层次分类 聚类 精确推理


<font color="#FF0000"> 2014.3.11 项目更名为FNLP,迁移至GitHub </font> [ChangeLog 更新日志(ChangeLog)] 


 
[性能测试(Benchmark)] (Benchmark)
[开发计划(Development Plan)] (DevPlan)
[开发人员列表(Developers)](People)

使用(Usages)
----
欢迎大家提供非Java语言的接口。

引用(Citation)
----
If you would like to acknowledge our efforts, please cite the following paper.
如果我们的工作对您有帮助，请引用下面论文。

		Xipeng Qiu, Qi Zhang and Xuanjing Huang, FudanNLP: A Toolkit for Chinese Natural Language Processing, In Proceedings of Annual Meeting of the Association for Computational Linguistics (ACL), 2013.*


		@INPROCEEDINGS{Qiu:2013,
		author = {Xipeng Qiu and Qi Zhang and Xuanjing Huang},
		title = {FudanNLP: A Toolkit for Chinese Natural Language Processing},
		booktitle = {Proceedings of Annual Meeting of the Association for Computational Linguistics},
		year = {2013},
		}

在[这里](http://jkx.fudan.edu.cn/~xpqiu/) 或  [DBLP](http://scholar.google.com/citations?sortby=pubdate&hl=en&user=Pq4Yp_kAAAAJ&view_op=list_works Google Scholar] 或 [http://www.informatik.uni-trier.de/~ley/pers/hd/q/Qiu:Xipeng.html) 可以找到更多的相关论文。

We used [JProfiler](http://www.ej-technologies.com/products/jprofiler/overview.html ) to help optimize the code.